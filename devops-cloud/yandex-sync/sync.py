import asyncio
import hashlib
import logging
import os
import sqlite3
import traceback
from typing import Any, Optional

# Сторонние библиотеки
import aiofiles
import anyio
from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict
from smbclient import open_file as smb_open, walk as smb_walk
from tqdm import tqdm
from tqdm.asyncio import tqdm as async_tqdm
import yadisk
from yadisk.aio import YaDisk as AsyncYaDisk

# ==========================================
# ВНЕШНЯЯ ПАРАМЕТРИЗАЦИЯ ЧЕРЕЗ PYDANTIC
# ==========================================
class AppConfig(BaseSettings):
    """Модель конфигурации приложения с автоматической валидацией."""
    model_config = SettingsConfigDict(
        env_prefix="HASH_APP_", 
        env_file=".env", 
        env_file_encoding="utf-8",
        extra="ignore"
    )

    async_mode: bool = Field(default=True, description="Использовать асинхронный режим обработки")
    algorithm: str = Field(default="sha256", description="Алгоритм хэширования (например, sha256, md5)")
    chunk_size: int = Field(default=65536, description="Размер чанка для потокового чтения (в байтах)")
    max_path_length: int = Field(default=240, description="Максимальная длина пути файла для сохранения в базу данных")
    
    # Лимитеры нагрузки (Семафоры)
    max_concurrent_disk: int = Field(default=2, description="Максимальное число параллельных потоков чтения диска")
    max_concurrent_net: int = Field(default=4, description="Максимальное число параллельных потоков для сети")
    
    # Пути инфраструктуры
    db_path: str = Field(default="hashes.db", description="Путь к файлу базы данных SQLite")
    log_path: str = Field(default="app_errors.log", description="Путь к файлу изолированного лога ошибок")


# Инициализация синглтона конфигурации
config = AppConfig()

# ==========================================
# ИЗОЛИРОВАННОЕ ЛОГИРОВАНИЕ ОШИБОК
# ==========================================
error_logger = logging.getLogger("ErrorLogger")
error_logger.setLevel(logging.ERROR)
error_logger.propagate = False

file_handler = logging.FileHandler(config.log_path, encoding="utf-8")
formatter = logging.Formatter("[%(asctime)s] %(levelname)s: %(message)s")
file_handler.setFormatter(formatter)
error_logger.addHandler(file_handler)


# ==========================================
# МЕХАНИЗМ АВТОМАТИЧЕСКОГО УСЕЧЕНИЯ ПУТЕЙ
# ==========================================
def truncate_path(path_str: str, max_len: int) -> str:
    """
    Усекает строку пути до указанной длины, сохраняя расширение файла.
    Возвращает актуальное усеченное имя для последующей записи в БД.
    """
    if len(path_str) <= max_len:
        return path_str
    
    root, ext = os.path.splitext(path_str)
    # Вычисляем доступный размер для левой части с учетом "..." и расширения
    allowed_root_len = max_len - len(ext) - 3  
    
    if allowed_root_len <= 0:
        truncated = path_str[:max_len]
    else:
        truncated = f"{root[:allowed_root_len]}...{ext}"
        
    error_logger.warning(f"Путь усечен со спецификацией длины ({len(path_str)} -> {len(truncated)} симв.): {truncated}")
    return truncated


# ==========================================
# ИНИЦИАЛИЗАЦИЯ И ПОДДЕРЖКА БАЗЫ ДАННЫХ SQLite
# ==========================================
def init_db(db_path: str) -> None:
    """Создает инфраструктуру таблиц хэшей в SQLite."""
    with sqlite3.connect(db_path) as conn:
        conn.execute("""
            CREATE TABLE IF NOT EXISTS file_hashes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                source_type TEXT NOT NULL,
                file_path TEXT NOT NULL UNIQUE,
                checksum TEXT NOT NULL,
                algorithm TEXT NOT NULL,
                is_duplicate INTEGER DEFAULT 0,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)
        conn.commit()

def check_and_save_hash_sync(source_type: str, safe_file_path: str, checksum: str, algorithm: str, db_path: str) -> None:
    """Проверяет хэш на идентичность и сохраняет усеченное имя в SQLite."""
    try:
        with sqlite3.connect(db_path) as conn:
            # Сравниваем хэши на предмет идентичности
            cursor = conn.execute("SELECT 1 FROM file_hashes WHERE checksum = ? LIMIT 1", (checksum,))
            is_duplicate = 1 if cursor.fetchone() else 0
            
            # Записываем актуальное усеченное имя файла (safe_file_path)
            conn.execute(
                """INSERT OR REPLACE INTO file_hashes (source_type, file_path, checksum, algorithm, is_duplicate) 
                   VALUES (?, ?, ?, ?, ?)""",
                (source_type, safe_file_path, checksum, algorithm, is_duplicate)
            )
            conn.commit()
    except Exception:
        error_logger.error(f"Ошибка сохранения хэша для пути {safe_file_path}:\n{traceback.format_exc()}")


# Асинхронная очередь записи (Предотвращает OperationalError: database is locked)
db_queue: asyncio.Queue = asyncio.Queue()

async def db_writer_worker(db_path: str) -> None:
    """Фоновый поток-обработчик очереди, последовательно пишущий в SQLite."""
    while True:
        item = await db_queue.get()
        if item is None:
            db_queue.task_done()
            break
        source_type, safe_file_path, checksum, algorithm = item
        await anyio.to_thread.run_sync(check_and_save_hash_sync, source_type, safe_file_path, checksum, algorithm, db_path)
        db_queue.task_done()


# ==========================================
# УНИФИЦИРОВАННЫЙ ТРЕКЕР ПРОГРЕССА
# ==========================================
class UnifiedProgressTracker:
    def __init__(self, is_async: bool = False) -> None:
        self.is_async = is_async
        pbar_cls = async_tqdm if is_async else tqdm
        self.file_pbar = pbar_cls(
            desc="Всего файлов", unit="file", dynamic_ncols=True, position=0
        )

    def _get_byte_pbar(self, file_name: str, file_size: Optional[int]) -> tqdm:
        pbar_cls = async_tqdm if self.is_async else tqdm
        display_name = file_name if len(file_name) <= 20 else f"{file_name[:]}..."
        return pbar_cls(
            total=file_size,
            desc=f" -> {display_name}",
            unit="B",
            unit_scale=True,
            unit_divisor=1024,
            leave=False,
            position=1,
            dynamic_ncols=True,
        )

    def calculate_stream_checksum(
        self, file_stream: Any, file_name: str, file_size: Optional[int] = None, algorithm: str = "sha256"
    ) -> str:
        """Синхронный подсчет хэша из открытого файлового дескриптора."""
        hash_func = hashlib.new(algorithm)
        byte_pbar = self._get_byte_pbar(file_name, file_size)

        try:
            for chunk in iter(lambda: file_stream.read(config.chunk_size), b""):
                hash_func.update(chunk)
                byte_pbar.update(len(chunk))
        finally:
            byte_pbar.close()

        self.file_pbar.update(1)
        return hash_func.hexdigest()

    async def calculate_stream_checksum_async(
        self, file_stream: Any, file_name: str, file_size: Optional[int] = None, algorithm: str = "sha256", is_yadisk: bool = False
    ) -> str:
        """Асинхронный подсчет хэша из стрима (поддерживает aiofiles и yadisk.aio)."""
        hash_func = hashlib.new(algorithm)
        byte_pbar = self._get_byte_pbar(file_name, file_size)

        try:
            if is_yadisk:
                # В yadisk.aio загрузка возвращает асинхронный генератор чанков байт
                async for chunk in file_stream:
                    hash_func.update(chunk)
                    byte_pbar.update(len(chunk))
            else:
                while True:
                    chunk = await file_stream.read(config.chunk_size)
                    if not chunk:
                        break
                    hash_func.update(chunk)
                    byte_pbar.update(len(chunk))
        finally:
            byte_pbar.close()

        self.file_pbar.update(1)
        return hash_func.hexdigest()

    def close(self) -> None:
        self.file_pbar.close()


# ==========================================
# ЛОКАЛЬНЫЕ ФАЙЛЫ (LOCAL)
# ==========================================
def hash_local_tree(root_dir: str, tracker: UnifiedProgressTracker, algorithm: str = "sha256") -> None:
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            full_path = os.path.join(dirpath, filename)
            try:
                # Усекаем имя файла для синхронной записи в БД
                safe_path = truncate_path(full_path, config.max_path_length)
                file_size = os.path.getsize(full_path)
                with open(full_path, "rb") as f:
                    checksum = tracker.calculate_stream_checksum(f, filename, file_size, algorithm)
                    tqdm.write(f"{checksum} {safe_path}")
                    check_and_save_hash_sync("Local", safe_path, checksum, algorithm, config.db_path)
            except Exception:
                error_logger.error(f"Ошибка обработки локального файла {full_path}:\n{traceback.format_exc()}")

async def _process_local_file_async(full_path: str, filename: str, tracker: UnifiedProgressTracker, algorithm: str, semaphore: asyncio.Semaphore) -> None:
    async with semaphore:
        try:
            # Получаем актуальное усеченное имя до отправки в очередь БД
            safe_path = truncate_path(full_path, config.max_path_length)
            file_size = os.path.getsize(full_path)
            async with aiofiles.open(full_path, "rb") as f:
                checksum = await tracker.calculate_stream_checksum_async(f, filename, file_size, algorithm)
                async_tqdm.write(f"{checksum} {safe_path}")
                await db_queue.put(("Local", safe_path, checksum, algorithm))
        except Exception:
error_logger.error(f"Ошибка асинхронной обработки локального файла {full_path}:\n{traceback.format_exc()}")async def hash_local_tree_async(root_dir: str, tracker: UnifiedProgressTracker, semaphore: asyncio.Semaphore, algorithm: str = "sha256") -> None:tasks =for dirpath, _, filenames in os.walk(root_dir):for filename in filenames:full_path = os.path.join(dirpath, filename)tasks.append(_process_local_file_async(full_path, filename, tracker, algorithm, semaphore))if tasks:await asyncio.gather(*tasks)==========================================SMB СЕТЕВАЯ ПАПКА (SMB)==========================================def hash_smb_tree(share_path: str, username: str, password: str, tracker: UnifiedProgressTracker, algorithm: str = "sha256") -> None:for dirpath, _, filenames in smb_walk(share_path, username=username, password=password):for filename in filenames:full_path = os.path.join(dirpath, filename)try:safe_path = truncate_path(full_path, config.max_path_length)with smb_open(full_path, mode="rb", username=username, password=password) as f:checksum = tracker.calculate_stream_checksum(f, filename, None, algorithm)tqdm.write(f"{checksum} {safe_path}")check_and_save_hash_sync("SMB", safe_path, checksum, algorithm, config.db_path)except Exception:error_logger.error(f"Ошибка SMB файла {full_path}:\n{traceback.format_exc()}")async def hash_smb_tree_async(share_path: str, username: str, password: str, tracker: UnifiedProgressTracker, semaphore: asyncio.Semaphore, algorithm: str = "sha256") -> None:def _sync_smb():for dirpath, _, filenames in smb_walk(share_path, username=username, password=password):for filename in filenames:full_path = os.path.join(dirpath, filename)try:safe_path = truncate_path(full_path, config.max_path_length)with smb_open(full_path, mode="rb", username=username, password=password) as f:checksum = tracker.calculate_stream_checksum(f, filename, None, algorithm)tqdm.write(f"{checksum} {safe_path}")asyncio.run_coroutine_threadsafe(db_queue.put(("SMB", safe_path, checksum, algorithm)),asyncio.get_running_loop())except Exception:error_logger.error(f"Ошибка асинхронного SMB файла {full_path}:\n{traceback.format_exc()}")async with semaphore:await anyio.to_thread.run_sync(_sync_smb)==========================================ЯНДЕКС ДИСК (YADISK)==========================================def _walk_yadisk(y: yadisk.YaDisk, remote_dir: str, tracker: UnifiedProgressTracker, algorithm: str) -> None:try:for item in y.get_meta(remote_dir, fields=["type", "path", "name", "size"]).embedded.items:if item.type == "dir":_walk_yadisk(y, item.path, tracker, algorithm)elif item.type == "file":try:safe_path = truncate_path(item.path, config.max_path_length)with y.download(item.path) as response:checksum = tracker.calculate_stream_checksum(response, item.name, item.size, algorithm)tqdm.write(f"{checksum} {safe_path}")check_and_save_hash_sync("YaDisk", safe_path, checksum, algorithm, config.db_path)except Exception:error_logger.error(f"Ошибка скачивания YaDisk файла {item.path}:\n{traceback.format_exc()}")except Exception:error_logger.error(f"Ошибка чтения папки YaDisk {remote_dir}:\n{traceback.format_exc()}")def hash_yadisk_tree(token: str, remote_dir: str = "/", tracker: Optional[UnifiedProgressTracker] = None, algorithm: str = "sha256") -> None:y = yadisk.YaDisk(token=token)if not y.check_token():tqdm.write("Неверный токен Яндекс Диска")returnif tracker is None:tracker = UnifiedProgressTracker(is_async=False)_walk_yadisk(y, remote_dir, tracker, algorithm)async def _process_yadisk_file_async(y: AsyncYaDisk, item: Any, tracker: UnifiedProgressTracker, algorithm: str, semaphore: asyncio.Semaphore) -> None:async with semaphore:try:safe_path = truncate_path(item.path, config.max_path_length)async with y.download(item.path) as response:checksum = await tracker.calculate_stream_checksum_async(response, item.name, item.size, algorithm, is_yadisk=True)async_tqdm.write(f"{checksum} {safe_path}")await db_queue.put(("YaDisk", safe_path, checksum, algorithm))except Exception:error_logger.error(f"Ошибка асинхронного скачивания YaDisk файла {item.path}:\n{traceback.format_exc()}")async def _walk_yadisk_async(y: AsyncYaDisk, remote_dir: str, tracker: UnifiedProgressTracker, semaphore: asyncio.Semaphore, algorithm: str) -> None:try:meta = await y.get_meta(remote_dir, fields=["type", "path", "name", "size"])tasks =for item in meta.embedded.items:if item.type == "dir":await _walk_yadisk_async(y, item.path, tracker, semaphore, algorithm)elif item.type == "file":tasks.append(_process_yadisk_file_async(y, item, tracker, algorithm, semaphore))if tasks:await asyncio.gather(*tasks)except Exception:error_logger.error(f"Ошибка асинхронного чтения папки YaDisk {remote_dir}:\n{traceback.format_exc()}")async def hash_yadisk_tree_async(token: str, remote_dir: str = "/", tracker: Optional[UnifiedProgressTracker] = None, semaphore: Optional[asyncioSemaphore] = None, algorithm: str = "sha256") -> None:if semaphore is None:semaphore = asyncio.Semaphore(config.max_concurrent_net)async with AsyncYaDisk(token=token) as y:if not await y.check_token():async_tqdm.write("Неверный токен Яндекс Диска")returnif tracker is None:tracker = UnifiedProgressTracker(is_async=True)await _walk_yadisk_async(y, remote_dir, tracker, semaphore, algorithm)==========================================ЗАПУСК СЦЕНАРИЕВ==========================================async def main_async() -> None:# Инициализируем фоновую запись в БДwriter_task = asyncio.create_task(db_writer_worker(config.db_path))tracker = UnifiedProgressTracker(is_async=True)disk_semaphore = asyncio.Semaphore(config.max_concurrent_disk)net_semaphore = asyncio.Semaphore(config.max_concurrent_net)try:# Процессинг локальной директории (Замените на целевую директорию)await hash_local_tree_async("C:\MyFolder", tracker, disk_semaphore, config.algorithm)# Интеграция с удаленными источниками (раскомментировать при необходимости):# await hash_smb_tree_async(r'\192.168.1.50\Share', 'user', 'pass', tracker, net_semaphore, config.algorithm)# await hash_yadisk_tree_async('TOKEN', '/Data', tracker, net_semaphore, config.algorithm)finally:tracker.close()# Ожидаем завершения очереди и глушим фоновый воркерawait db_queue.join()await db_queue.put(None)await writer_taskdef main_sync() -> None:tracker = UnifiedProgressTracker(is_async=False)try:# Процессинг в классическом синхронном однопоточном режимеhash_local_tree("C:\MyFolder", tracker, config.algorithm)# hash_smb_tree(r'\192.168.1.50\Share', 'user', 'pass', tracker, config.algorithm)# hash_yadisk_tree('TOKEN', '/Data', tracker, config.algorithm)finally:tracker.close()if name == "main":# Шаг 1. Развертывание структуры SQLiteinit_db(config.db_path)# Шаг 2. Разделение исполнения на основе валидированного Pydantic-конфигаif config.async_mode:asyncio.run(main_async())else:main_sync()
### Памятка по управлению конфигурацией
Благодаря Pydantic, изменять поведение утилиты (включая лимит усечения пути файла) можно без редактирования кода скрипта. Создайте файл `.env` в корневой директории проекта:

```ini
# Содержимое файла .env
HASH_APP_ASYNC_MODE=true
HASH_APP_ALGORITHM=sha256
HASH_APP_MAX_PATH_LENGTH=120
HASH_APP_DB_PATH=my_secured_hashes.db
HASH_APP_MAX_CONCURRENT_DISK=4
При запуске Pydantic автоматически подтянет новые значения, проверит их типы, и все пути длиной более 120 символов гарантированно усекутся с сохранением расширения файла, после чего сохранятся именно в таком виде в базу my_secured_hashes.db.