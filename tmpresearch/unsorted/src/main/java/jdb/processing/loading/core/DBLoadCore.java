package jdb.processing.loading.core;

import dgusev.io.MyIOUtils;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.modeling.DBModeler;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Модуль десериализации БД. Десериализация выполняется как в однопоточном, так и в многопоточном режимах. Для
 * конфигурирования методов модуля используется класс конфигурации - SerializationConfig. Многопоточность работы
 * методов данного класса заключается в многопотоковой обработки наборов sql-запросов, которые используются для
 * вставки данных в таблицы БД.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 20.05.2010)
*/

public class DBLoadCore
 {
  /** Логгер данного класса. */
  private static Logger logger = Logger.getLogger(DBLoadCore.class.getName());

  /**
   * Метод загружает с диска сериализованную БД в текущую БД (на которую указывает класс конфигурации соединения с СУБД).
   * Имя сериализованной БД (на диске) должно совпадать с именем текущей БД (на которую указывает класс конфигурации
   * подключения к СУБД).
   * @param config SerializationConfig конфигурация для выполнения десериализации. Содержит все необходимые данные.
   * @throws java.sql.SQLException ИС при выполнении анализа таблицы БД.
   * @throws java.io.IOException ошибки ввода/вывода при работе с файловой системой.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
   * @throws jdb.exceptions.DBModelException ошибки модели базы данных.
   * @return ArrayList<Exception> список возникших при работе модуля ИС. В данном списке возвращаются НЕ КРИТИЧЕСКИЕ
   * ИС - те, которые не приводят к останову модуля.
  */
  public static ArrayList<String> load(DBLoaderConfig config)
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException, IOException
   {
    logger.debug("WORKING DBLoadCore.load().");

    // Возвращаемый список возникших ИС (при десериализации БД)
    ArrayList<String> errorsList = null;

    // Проверяем полученный конфиг для сериализации. Если он содержит ошибки - дальше не работаем (возбуждаем ИС)
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Если же конфиг в порядке - выведем отладочное сообщение
    else {logger.debug("Serialization config is OK. Processing.");}
    // Проверка соединения с СУБД с помощью конфига (без соединения с СУБД невозможна десериализация данных)
    if (!DBUtils.isConnectionValid(config.getDbConfig())) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}
    
    // Путь к каталогу с данными сериализованной БД
    String dbPath = config.getPath();
    // Проверка переданного пути к каталогу - проверка существования каталога, проверка что это именно каталог
    if (!new File(dbPath).exists())           {throw new IOException("Catalog [" + dbPath + "] doesn't exists!");}
    else if (!new File(dbPath).isDirectory()) {throw new IOException("Path [" + dbPath + "] not a catalog!");}
    // Сообщим о том, что каталог все проверки прошел
    else {logger.debug("Catalog [" + dbPath + "] is OK. Processing.");}
    // Локальная копия параметра "путь к сериализованной БД" (сразу корректируем отсутствие символа / в конце пути)
    String localDBPath = MyIOUtils.fixFPath(config.getPath(), true);

    // Ссылка на модуль-монитор данного процесса
    DBProcessingMonitor monitor = config.getMonitor();

    // Получение модели структуры текущей БД (с учетом ограничений - списки "разрешенных" и "запрещенных")
    DBStructureModel dbModel = new DBModeler(config.getDbConfig()).getDBStructureModel();
    // Если полученная модель пуста (=null), генерируется ИС
    if (dbModel == null) {throw new DBModelException("Database model is empty!");}

    // Есди в модели БД есть таблицы - обработка
    if ((dbModel.getTables() != null) && (!dbModel.getTables().isEmpty()))
     {
      logger.debug("Database structure model is not empty. Processing deserialization.");
      // Проходим по всем таблицам данной сериализованной БД и берем из них данные
      int processedTablesCounter = 0;                        // <- счетчик обработанных таблиц
      int tablesCount            = dbModel.getTablesCount(); // <- общее количество обрабатываемых таблиц

      // Переменная для хранения ошибок при загрузке одной таблицы. При загрузке каждой таблицы значение перезаписывается.
      ArrayList<String>tableLoadErrors;

      // Конфигурация для загрузки одной таблицы. Данная конфигурация отличается от основной путем к данным (path).
      DBLoaderConfig tableLoaderConfig = new DBLoaderConfig(config);

      // Проходим по списку всех таблиц текущей БД и пытаемся загрузить в них данные из сериализованной копии
      for (TableStructureModel table : dbModel.getTables())
       {
        // Сообщение монитору об обрабатываемой таблице (о начале обработки)
        if (monitor != null) {monitor.processMessage("[" + table.getTableName().toUpperCase() + "] [LOADING STARTED]");}
        logger.debug("Processing table [" + table.getTableName() + "]");

        // Таблица не подпадает под ограничения - обрабатываем ее. Также здесь проверяется имя таблицы -
        // если имя пусто, то таблица автоматически "запрещена".
        if (config.getDbConfig().isTableAllowed(table.getTableName()))
         {
          // Путь к каталогу с данными для конкретной таблицы
          String tablePath = localDBPath + table.getTableName() + "/";
          // Переменная типа File - содержит ссылку на каталог с данными для текущей обрабатываемой таблицы
          File tableDir  = new File(tablePath);
          // Если существует каталог с именем, совпадающим с именем таблицы - загружаем из него таблицу
          if (tableDir.exists() && tableDir.isDirectory())
           {
            logger.info("Loading table [" + table.getTableName() + "]");
            // Вызов метода непосредственной загрузки данных в БД. Все некритические ошибки будут записаны в результирующий
            // список ошибок метода. При возникновении критической ошибки, обработка продолжается для следующей таблицы -
            // это обеспечивается конструкцией try...catch.
            tableLoadErrors = null;
            // Для конфигурации загрузки одной таблицы указываем путь к данным именно этой таблицы
            tableLoaderConfig.setPath(tablePath);
            tableLoaderConfig.setMonitorMsgPrefix("[" + table.getTableName().toUpperCase() + "]");
            try {tableLoadErrors = TableLoadCore.load(tableLoaderConfig, table.getTableName());} // <- Загрузка одной таблицы
            // Перехватываем возможные критические ИС при загрузке в БД одной таблицы
            catch (DBModuleConfigException e) {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (DBConnectionException e)   {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (SQLException e)            {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (IOException e)             {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            // Если при обработке одной таблицы возникли некритические ошибки, их список заносим в
            // результирующий список некритических ошибок (для метода)
            if ((tableLoadErrors != null) && (!tableLoadErrors.isEmpty()))
             {
              logger.error("There are non-critical errors during processing table [" + table.getTableName() + "]!");
              if (errorsList == null) {errorsList = new ArrayList<String>();}
              errorsList.addAll(tableLoadErrors);
             }
            // Сообщим об окончании обработки текущей таблицы
            logger.info("Table [" + table.getTableName() + "] loaded successfully.");
           }
         }
        // Если текущая таблица подпадает под ограничения - наффик!
        else {logger.warn("Current table [" + table.getTableName() + "] is deprecated or not allowed! Skipping.");}

        // После обработки каждой таблицы(неважно, разрешена она или нет) выводим мониторинговое сообщение (если необходимо),
        // которое содержит информацию о прогрессе обработки данных (вызов метода processProgress() монитора)
        if (monitor != null)
         {
          // Увеличиваем счетчик обработанных таблиц
          processedTablesCounter++;
          // Считаем текущий процент выполнения
          int currentProgress = (processedTablesCounter*100/tablesCount);
          // Вывод в процесс-монитор текущего прогресса выполнения ()
          monitor.processProgress(currentProgress);
          // Вывод в процесс-монитор сообщения об окончании обработки текущей таблицы
          monitor.processMessage("[" + table.getTableName().toUpperCase() + "] [LOADING FINISHED]");
          // Если включена отладка (уровень сообщений лога DEBUG), то выведем сообщение об обрабатываемой сейчас таблице.
          //if (logger.getEffectiveLevel().equals(Level.DEBUG)) {monitor.processDebugInfo("[DEBUG] " + table.getTableName());}
         }
        
       } // конец цикла обработки всех таблиц [FOR]
     }
    // Если же в полученной модели БД нет таблиц - сообщим об этом в лог (ничего не делаем)
    else {logger.error("Current database model is empty! Nothing to process.");}

    return errorsList;
   }

 }