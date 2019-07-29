package jdb.processing.loading.core;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.config.DBTableType;
import dgusev.dbpilot.utils.DBUtilities;
import dgusev.io.MyIOUtils;
import dgusev.utils.MyCommonUtils;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import jdb.model.dto.TableDTOModel;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.time.DBTimedModel;
import jdb.model.time.TableTimedModel;
import jdb.processing.loading.helpers.DataExportSQLBuilder;
import jdb.processing.modeling.DBModeler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Данный класс реализует сериализацию таблиц любой БД. Сериализация производится с помощью DTO-модели
 * БД - см. классы в пакете model.
 *
 * <br> Алгоритм сериализации следующий:
 * <ul>
 * <li> проверяется конфигурация модуля - коняигурация для соединения с СУБД, путь для сохранения данных. Если проверка
 * завершается неудачно - метод не выполняет никаких действий. Если каталог назначения (куда будут сохраняться
 * данные) не существует - он создается, если создать не удалось - возникает критическая ошибка (при возникновении
 * критических ошибок работа модуля завершается). Если каталог назначения существует - он очищается от всех файлов.
 * Ошибка очистки каталога - критическая.
 * <li> генерируется модель структуры сериализуемой БД, в модель попадают только "разрешенные" (явно и неявно) таблицы.
 * Если полученная модель пуста - возникает критическая ошибка.
 * <li> В цикле проходим по списку таблиц сериализуемой БД, обрабатывая каждую "разрешенную" таблицу: получаем модель для
 * таблицы, генерируем sql-запрос для выборки ВСЕХ данных сериализуемой таблицы, получаем непосредственно сами данные
 * (если данных нет - таблица пуста, экспорт продолжается для следующей таблицы), в цикле проходим по курсору данных и
 * формируем модель таблицы с данными (для формирования модели используются классы пакета model), при достижении
 * определенного количества обработанных строк (фракция сериализации, см. константу в модуле DBConsts) модель
 * выгружается в файл на диске. Обработка продолжается. Ошибки записи файла на диск - критические (работа останавливается).
 * <li> Файлы с выгружаемыми данными именуются специальным образом - [лидирующие 0] + [номер файла].zip Максимальное
 * количество выгружаемых (формируемых) файлов - девятизначное число (999999999), по достижении которого происходит
 * останов работы модуля. Лидирующие 0 применяются для однозначной сортировки файлов. Объекты с данными имеют большой
 * размер (в сериализованном виде - в виде файлов), поэтому для них применяется сжатие с простым шифрованием, для сжатия
 * применяется открытый алгоритм ZIP.
 * </ul>
 * <br> При больших размерах сериализуемых данных (много записей в таблицах, большая длина записи, много таблиц в одной БД
 * и т.п.) метод очень требователен к памяти, поэтому во избежание ошибок типа OutOfMemory при экспорте больших таблиц
 * рекомендуется задавать большие значения для используемой JAVA-машиной памяти (рекомендуются значения более 256Mb).
 * Максимальный размер памяти задается опцией командной строки -Xmx[число магабайт]M (подробнее - см. документацию).
 *
 * @author Gusev Dmitry (019gus)
 * @version 9.2 (DATE: 21.03.2011)
 */

public class DBUnloadCore {
    /**
     * Компонент-логгер данного класса.
     */
    private static Logger logger = Logger.getLogger(DBUnloadCore.class.getName());
    /**
     * Стандартная длина имени файла с выгруженными из БД данными. К этой длине приводятся все имена выгружаемых файлов.
     * Не рекомендую ставить слишком большие значения - это скажется на быстродействии системы.
     */
    private static final int FILE_NAME_LENGTH = 10;
    /**
     * Указанным ниже символом дополняется имя файла до необходимой длины (дополняется в начало файла). Этот символ - ноль.
     * Другие символы (особенно цифры) ставить не надо - это нарушит порядок сортировки файлов в файловой системе и нарушит
     * порядок их (файлов) обработки методами загрузки данных из файлов в БД.
     */
    private static final char FILE_NAME_FILL_SYMBOL = '0';

    /**
     * Метод на основании анализа структуры текущей БД создает ее (БД) модель на диске со всеми данными (сериализует БД).
     * Сериализация производится в каталог, указанный в переменной pathToDB. В этом каталоге создается подкаталог с именем
     * сериализуемой БД, в котором располагаются каталоги с именами, совпадающими с именами таблиц БД. Сериализованные данные
     * находятся в каталогах с именами таблиц - данные каждой таблицы в соответствующем каталоге. Данные сериализуются
     * частями - по DBSerializerConsts.TABLE_SERIALIZE_FRACTION записей (на данный момент = 200), если же сериализуемых
     * записей меньше - они все попадают в один файл. Затем полученный файл архивируется (в формат zip) и удаляется. Архивному
     * файлу присваивается имя, совпадающее с именем исходного файла. Имя исходного файла - номер порции данных из
     * DBSerializerConsts.TABLE_SERIALIZE_FRACTION записей. Расширение исходного файла -
     * DBSerializerConsts.SERIALIZED_DATA_FILE_EXTENSION (на данный момент - .bin). Расширение архивного файла -
     * DBSerializerConsts.ARCHIVED_DATA_FILE_EXTENSION (на данный момент  - .zip). Данные из сериализуемой таблицы
     * помещаются в объекты TableDTO (таблица). Эти объекты в свою очередь состоят из объектов TableRowDTO (запись), а эти
     * объекты состоят из объектов TableFieldDTO (поле).<br>
     * Если имя текущей сериализуемой таблицы присутствует в списке "запрещенных" таблиц - такая таблица выгружена не
     * будет.<br>
     * Также в механизме сериализации данных участвуют (как параметры) две модели БД: <br>
     * - DBTimeModel - модель БД с указанием времени обновления каждой таблицы (время обновления
     * таблицы - максимальное значение поля timestamp для всех записей данной таблицы). Если эта модель пуста (=null), то
     * текущая БД будет сериализована полностью. Если для какой-либо таблицы из данной модели не указано значение
     * timestamp - такая таблица будет сериализована полностью.<br>
     * - DBIntegrityModel - модель целостности БД. Если эта модель пуста (=null), то текущая БД будет сериализована полностью.
     * Если же какие-то таблицы будут упомянуты в этой модели, то для них будут выгружены данные по списку из модели.<br>
     * Максимальное количество файлов, в которое может быть выгружена одна таблица БД - максимальное девятизначное
     * число (999999999).
     *
     * @param config SerializationConfig конфигурация для работы метода выгрузки (сериализации) данных из БД.
     * @return boolean результат сериализации БД - если ИСТИНА, то часть данных сериализована, если ЛОЖЬ -
     * ничего не сериализовано. Часть данных (не полностью вся БД) может быть сериализована если для сериализации
     * используется параметр DatabaseTimeModel - модель БД с указанием времени.
     * @throws SQLException            ИС при выполнении анализа таблицы БД.
     * @throws IOException             ошибки ввода/вывода при работе с файловой системой.
     * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
     * @throws DBConnectionException   ошибки соединения с СУБД.
     * @throws DBModelException        ошибки модели базы данных.
     */
    @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
    public static boolean unload(DBLoaderConfig config) throws SQLException, DBConnectionException,
            DBModuleConfigException, IOException, DBModelException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Результат сериализации БД - если ИСТИНА, то часть данных сериализована, если ЛОЖЬ - ничего не сериализовано.
        boolean isDataSerialized = false;
        logger.debug("WORKING DBSerializer.unload().");

        // Если конфигурация модуля ошибочна - возбуждаем ИС!
        String configErrors = null; // DBUtils.getConfigErrors(config);
        if (!StringUtils.isBlank(configErrors)) {
            throw new DBModuleConfigException(configErrors);
        }

        // Корректируем путь к каталогу (он должен оканчиваться символом /).
        String localPathToDB = MyIOUtils.fixFPath(config.getPath(), true);
        // Если такого каталога нет - создаем его. Если создание каталога не удалось - возбуждаем ИС IOException
        if (!new File(localPathToDB).exists()) {
            if (!new File(localPathToDB).mkdirs()) {
                throw new IOException("Can't create loading catalog [" + localPathToDB + "]!");
            }
        }
        // Если каталог существует - очищаем его перед экспортом (записью) в него данных
        else {
            MyIOUtils.clearDir(localPathToDB);
        }

        // Получение модели структуры текущей БД (структура будет в соответствии с ограничениями)
        DBModeler modeler = new DBModeler(config.getDbConfig());
        DBStructureModel currentDbModel = modeler.getDBStructureModel();
        // Если полученная модель пуста (=null), генерируется ИС
        if (currentDbModel == null) {
            throw new DBModelException("Database model is empty!");
        }

        // Выбираем значение фракции сериализации (количество строк/записей одной таблицы для записи в один
        // сериализационный файл), если размер не указан (он <= 0), то используется значение по умолчанию - см.
        // константу SERIALIZE_TABLE_FRACTION в модуле DBConsts.
        int serializeFraction;
        if (config.getSerializeFraction() > 0) {
            serializeFraction = config.getSerializeFraction();
        } else {
            serializeFraction = DBConsts.SERIALIZATION_TABLE_FRACTION;
        }
        logger.debug("Calculated value for table serialize fraction:" + serializeFraction);

        // Компоненты для соединения с сериализуемой СУБД
        Connection connection = DBUtilities.getDBConn(config.getDbConfig());
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(5000);

        // Ссылки на модель целостности и временнУю модель текущей БД
        DBIntegrityModel integrityDB = config.getDbIntegrityModel();
        DBTimedModel timedDB = config.getDbTimedModel();

        // Проходим по всем таблицам данной БД и берем из них данные. Если имя текущей таблицы подпадает под
        // ограничения - не обрабатываем ее. Также не обрабатываются пустые (null) таблицы.
        for (TableStructureModel currentTable : currentDbModel.getTables()) {
            // Сохраняем имя текущей обрабатываемой таблицы. Если у таблицы указана схема - используем ее.
            String currentTableName = currentTable.getTableName();

            // Если текущая полученная таблица не пуста - работаем
            if ((currentTable != null) && (!StringUtils.isBlank(currentTableName))) {
                // Полное имя текущей обрабатываемой таблицы, с указанием схемы (если она есть).
                String currentTableFullName;
                if (!StringUtils.isBlank(currentTable.getTableSchema())) {
                    currentTableFullName = currentTable.getTableSchema() + "." + currentTableName;
                } else {
                    currentTableFullName = currentTableName;
                }

                // Из БД выгружаются только таблицы (имеют тип TABLE), другие объекты не выгружаются (верменные таблицы,
                // вьюшки (представления) и т.п.)
                if (DBTableType.TABLE.getStrValue().equals(currentTable.getTableType())) {
                    logger.debug("Processing table [" + currentTableFullName + "]. Table type [" + currentTable.getTableType() + "].");
                    // Если таблица не подпадает под ограничения, то обрабатываем ее. Проверка таблицы на ограничения
                    // производится по имени таблицы БЕЗ указания имени схемы. Важно это помнить, чувак! :)
                    if (config.isTableAllowed(currentTableName)) {
                        logger.debug("Current table [" + currentTableFullName + "] is ALLOWED for processing!");
                        // Проверяем наличие ключевого поля для экспортируемой таблицы. Если ключевого поля нет - такую таблицу
                        // нельзя будет загрузить обратно и ее выгрузка не имеет смысла.
                        if (currentTable.getField(config.getKeyFieldName()) != null) {
                            logger.debug("Key field [" + config.getKeyFieldName() + "] found in table [" + currentTableFullName + "]. Processing.");
                            // Каталог для сохранения конкретной таблицы. Каталог (имя каталога) для хранения таблицы также (как и
                            // проверка таблицы на "разрешенность") берется по простому имени таблицы, без указания схемы данных.
                            String pathToTable = localPathToDB + currentTableName + "/";

                            // Модель целостности для данной таблицы (получаем по имени таблицы, БЕЗ указания схемы)
                            TableIntegrityModel integrityTable = null;
                            if (integrityDB != null && !integrityDB.isEmpty()) {
                                integrityTable = integrityDB.getTable(currentTableName);
                            }
                            // Модель с указанием времени для данной таблицы (получаем по имени таблицы, БЕЗ указания схемы)
                            TableTimedModel timedTable = null;
                            if (timedDB != null && !timedDB.isEmpty()) {
                                timedTable = timedDB.getTable(currentTableName);
                            }

                            // Получаем от класса-помощника сгенерированный sql-запрос для выгрузки текущей таблицы на диск
                            String sql = DataExportSQLBuilder.getExportTableSQL(currentTable, integrityTable, timedTable);
                            // Если от класса-помощника мы получили не пустой запрос - работаем
                            if (!StringUtils.isBlank(sql)) {
                                logger.debug("Export SQL is OK! Processing.");
                                try {
                                    // Непосредственно получение данных и метаданных из таблицы
                                    ResultSet rs = stmt.executeQuery(sql);
                                    ResultSetMetaData rsmeta = rs.getMetaData();
                                    logger.debug("DATA and METADATA was received. Processing.");

                                    // Проход по всему полученному курсору данных
                                    int counter = 1; // <- счетчик количества обработанных строк из данного курсора
                                    int packCounter = 1; // <- счетчик количества созданных файлов с сериализованными данными из данной таблицы
                                    // Если есть данные - обрабатываем их
                                    if (rs.next()) {
                                        // Создаем новую модель таблицы для сериализации данных. Указываем схему и тип таблицы.
                                        TableDTOModel tableDTOModel = new TableDTOModel(currentTableName);
                                        tableDTOModel.setTableSchema(currentTable.getTableSchema());
                                        tableDTOModel.setTableType(currentTable.getTableType());
                                        // Если найдены данные - создаем каталог для сериализуемой таблицы. Если каталог для текущей сериализуемой
                                        // таблицы создать не удалось - возбуждается ИС и обработка текущей таблицы прекращается
                                        if (!new File(pathToTable).exists()) {
                                            logger.debug("Creating catalog [" + pathToTable + "] for current table [" + currentTableName + "].");
                                            if (!new File(pathToTable).mkdirs()) {
                                                throw new IOException("Can't create catalog [" + pathToTable + "]!");
                                            } else {
                                                logger.debug("Catalog for current table created successfully.");
                                            }
                                        }

                                        // Есди найдены данные для сериализации - надо установить флаг, который будет возвращен данным методом -
                                        // некоторые данные были сериализованы (записаны на диск)
                                        if (!isDataSerialized) {
                                            isDataSerialized = true;
                                        }

                                        // Отладочный вывод - сообщаем в журнал о начале обработки (выгрузки) данных таблицы
                                        logger.debug("Starting export table [" + currentTableFullName + "]. Creating data files.");
                                        // Непосредственно обработка полученного курсора с данными текущей таблицы (обрабатываем все строки
                                        // курсора и формируем файлы, состоящие из объектов-строк)
                                        do {
                                            // Начинаем формировать новую строку таблицы
                                            RowDTOModel rowModel = new RowDTOModel();
                                            // Цикл прохода по полям одной строки из курсора данных
                                            for (int i = 1; i <= currentTable.getFields().size(); i++) {
                                                // Имя текущего поля
                                                String fName = rsmeta.getColumnName(i);
                                                // Если имя поля не пусто - получаем тип поля и заносим его (поле) в модель строки
                                                if (!StringUtils.isBlank(fName)) {
                                                    // Получаем тип текущего поля из модели данных
                                                    //int fType = currentDbModel.getTable(currentTableName).getField(fName).getJavaDataType();
                                                    int fType = currentTable.getField(fName).getJavaDataType();
                                                    // Добавляем поле в модель строки таблицы (с именем, значением, типом)
                                                    rowModel.addField(new FieldDTOModel(fName, rs.getString(i), fType));
                                                }
                                                // Если же имя поля пусто - в модель строки поле НЕ заносим!
                                                else {
                                                    logger.error("Empty field name! Check the source table!");
                                                }
                                            }
                                            // Добавление сформированной модели строки таблицы в модель таблицы
                                            tableDTOModel.addRow(rowModel);

                                            // Отладочный вывод (сообщаем в лог о ходе обработки данных)
                                            if ((config.getOperationsCount() > 0) && (counter % config.getOperationsCount() == 0)) {
                                                logger.debug("# " + counter + " rows of table [" + currentTable.getTableName() + "] processed.");
                                            }

                                            // Сериализация набора из N записей (запись этого набора записей в файл на диске). Сериализуются
                                            // только наборы из количества записей, кратного значению фракции сериализации (указывается в конфиге),
                                            // если же количество записей в таблице (сериализуемом курсоре) не кратно значению фракции сериализации,
                                            // то оставшиеся записи сериализуются после окончания цикла (это необходимо для того, чтобы избежать
                                            // использования ресурсоемкого оператора ResultSet.isLast()).
                                            if ((counter % serializeFraction == 0) /** || ((counter%serializeFraction != 0) && (rs.isLast()))*/) {
                                                // Получаем имя для очередного записываемого файла. Имя строится по определенным правилам (см.
                                                // документацию/комментарии к методу getNameForFile())
                                                String fileName = MyCommonUtils.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, String.valueOf(packCounter));
                                                // Вызов модуля MyIOUtils для сериализации объекта
                                                MyIOUtils.serializeObject(tableDTOModel, pathToTable, fileName, null, false);
                                                logger.debug("### created file [" + fileName + "]");

                                                // Создание нового объекта TableDTOModel (обнуление ссылки на текущий экземпляр класса). Обязательно
                                                // указываем схему и тип таблицы.
                                                tableDTOModel = new TableDTOModel(currentTableName);
                                                tableDTOModel.setTableSchema(currentTable.getTableSchema());
                                                tableDTOModel.setTableType(currentTable.getTableType());
                                                // Увеличение счетчика файлов с данными
                                                packCounter++;
                                            }
                                            // Увеличение счетчика количества обработанных строк
                                            counter++;
                                        }
                                        while (rs.next()); // Конец цикла обработки данных текущей обрабатываемой таблицы

                                        // Сериализуем остаток записей из курсора, количество которых (записей) не кратно
                                        // значению фракции сериализации.
                                        if ((counter - 1) % serializeFraction != 0) {
                                            logger.debug("Serializing records remainder [" + ((counter - 1) % serializeFraction) + " records].");
                                            // Получаем имя для очередного записываемого файла. Имя строится по определенным правилам (см.
                                            // документацию/комментарии к методу getNameForFile())
                                            String fileName = MyCommonUtils.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, String.valueOf(packCounter));
                                            // Вызов модуля MyIOUtils для сериализации объекта
                                            MyIOUtils.serializeObject(tableDTOModel, pathToTable, fileName, null, false);
                                            logger.debug("### created file [" + fileName + "]");
                                        }
                                        // Отладочный вывод - всего обработано строк в таблице
                                        logger.debug("Table [" + currentTableFullName + "]. Total processed records [" + (counter - 1) + "].");
                                        // Закрываем за собой курсор
                                        rs.close();
                                    }
                                    // Если же данных нет - сообщаем об этом в лог
                                    else {
                                        logger.error("Data for current table [" + currentTableFullName + "] is empty!");
                                    }
                                }
                                // Перехват некритических ИС. При возникновении некритической ИС работа модуля продолжается.
                                catch (IOException e) {
                                    logger.error("I/O error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");
                                } catch (SQLException e) {
                                    logger.error("SQL error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");
                                }
                            }
                            // Если же получен пустой запрос - ошибка! Ничего не делаем!
                            else {
                                logger.error("Export SQL for current table [" + currentTableFullName + "] is empty!");
                            }
                        }
                        // Если в таблице нет ключевого поля - сообщаем в лог и таблицу не выгружаем
                        else {
                            logger.warn("Key field [" + config.getKeyFieldName() + "] not found in table [" + currentTableFullName + "]! Can't process!");
                        }
                    }
                    // Если же таблица подпала под ограничения - выводим об этом сообщение и не обрабатываем ее
                    else {
                        logger.warn("Current table [" + currentTableFullName + "] is not allowed! Skipping.");
                    }
                }
                // Если же таблица имеет тип отличный от TABLE - она не выгружается на диск
                else {
                    logger.warn("Can't unload to disk table [" + currentTableFullName + "] with type [" + currentTable.getTableType() + "]!");
                }
            }
            // Если же таблица пуста - выведем ошибку в лог
            else {
                logger.error("Current table model is empty! Can't process table!");
            }

        } // [END OF FOR] Окончание цикла обработки всех таблиц из модели данных текущей (экспортируемой) БД

        // Если ничего не было сериализовано, то надо удалить созданный каталог для БД
        if (!isDataSerialized) {
            if (!new File(localPathToDB).delete()) {
                logger.warn("Can't delete created loading catalog [" + localPathToDB + "]!");
            } else {
                logger.debug("Catalog [" + localPathToDB + "] was deleted successfully.");
            }
        }
        // Возвращение результата работы метода
        return isDataSerialized;
    }

    public static boolean unload2(DBLoaderConfig config) throws SQLException, DBConnectionException,
            DBModuleConfigException, IOException, DBModelException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Результат сериализации БД - если ИСТИНА, то часть данных сериализована, если ЛОЖЬ - ничего не сериализовано.
        boolean isDataSerialized = false;
        logger.debug("WORKING DBSerializer.unload().");

        // Если конфигурация модуля ошибочна - возбуждаем ИС!
        String configErrors = null; // DBUtils.getConfigErrors(config);
        if (!StringUtils.isBlank(configErrors)) {
            throw new DBModuleConfigException(configErrors);
        }

        // Корректируем путь к каталогу (он должен оканчиваться символом /).
        String localPathToDB = MyIOUtils.fixFPath(config.getPath(), true);
        // Если такого каталога нет - создаем его. Если создание каталога не удалось - возбуждаем ИС IOException
        if (!new File(localPathToDB).exists()) {
            if (!new File(localPathToDB).mkdirs()) {
                throw new IOException("Can't create loading catalog [" + localPathToDB + "]!");
            }
        }
        // Если каталог существует - очищаем его перед экспортом (записью) в него данных
        else {
            MyIOUtils.clearDir(localPathToDB);
        }

        // Получение модели структуры текущей БД (структура будет в соответствии с ограничениями)
        DBModeler modeler = new DBModeler(config.getDbConfig());
        DBStructureModel currentDbModel = modeler.getDBStructureModel();
        // Если полученная модель пуста (=null), генерируется ИС
        if (currentDbModel == null) {
            throw new DBModelException("Database model is empty!");
        }

        // Выбираем значение фракции сериализации (количество строк/записей одной таблицы для записи в один
        // сериализационный файл), если размер не указан (он <= 0), то используется значение по умолчанию - см.
        // константу SERIALIZE_TABLE_FRACTION в модуле DBConsts.
        int serializeFraction;
        if (config.getSerializeFraction() > 0) {
            serializeFraction = config.getSerializeFraction();
        } else {
            serializeFraction = DBConsts.SERIALIZATION_TABLE_FRACTION;
        }
        logger.debug("Calculated value for table serialize fraction:" + serializeFraction);

        // Компоненты для соединения с сериализуемой СУБД
        Connection connection = DBUtilities.getDBConn(config.getDbConfig());
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(5000);

        // Ссылки на модель целостности и временнУю модель текущей БД
        DBIntegrityModel integrityDB = config.getDbIntegrityModel();
        DBTimedModel timedDB = config.getDbTimedModel();

        // Проходим по всем таблицам данной БД и берем из них данные. Если имя текущей таблицы подпадает под
        // ограничения - не обрабатываем ее. Также не обрабатываются пустые (null) таблицы.
        for (TableStructureModel currentTable : currentDbModel.getTables()) {
            // Сохраняем имя текущей обрабатываемой таблицы. Если у таблицы указана схема - используем ее.
            String currentTableName = currentTable.getTableName();

            // Если текущая полученная таблица не пуста - работаем
            if ((currentTable != null) && (!StringUtils.isBlank(currentTableName))) {
                // Полное имя текущей обрабатываемой таблицы, с указанием схемы (если она есть).
                String currentTableFullName;
                if (!StringUtils.isBlank(currentTable.getTableSchema())) {
                    currentTableFullName = currentTable.getTableSchema() + "." + currentTableName;
                } else {
                    currentTableFullName = currentTableName;
                }

                // Из БД выгружаются только таблицы (имеют тип TABLE), другие объекты не выгружаются (верменные таблицы,
                // вьюшки (представления) и т.п.)
                if (DBTableType.TABLE.getStrValue().equals(currentTable.getTableType())) {
                    logger.debug("Processing table [" + currentTableFullName + "]. Table type [" + currentTable.getTableType() + "].");
                    // Если таблица не подпадает под ограничения, то обрабатываем ее. Проверка таблицы на ограничения
                    // производится по имени таблицы БЕЗ указания имени схемы. Важно это помнить, чувак! :)
                    if (config.isTableAllowed(currentTableName)) {
                        logger.debug("Current table [" + currentTableFullName + "] is ALLOWED for processing!");
                        // Проверяем наличие ключевого поля для экспортируемой таблицы. Если ключевого поля нет - такую таблицу
                        // нельзя будет загрузить обратно и ее выгрузка не имеет смысла.
                        if (currentTable.getField(config.getKeyFieldName()) != null) {
                            logger.debug("Key field [" + config.getKeyFieldName() + "] found in table [" + currentTableFullName + "]. Processing.");
                            // Каталог для сохранения конкретной таблицы. Каталог (имя каталога) для хранения таблицы также (как и
                            // проверка таблицы на "разрешенность") берется по простому имени таблицы, без указания схемы данных.
                            String pathToTable = localPathToDB + currentTableName + "/";

                            // Модель целостности для данной таблицы (получаем по имени таблицы, БЕЗ указания схемы)
                            TableIntegrityModel integrityTable = null;
                            if (integrityDB != null && !integrityDB.isEmpty()) {
                                integrityTable = integrityDB.getTable(currentTableName);
                            }
                            // Модель с указанием времени для данной таблицы (получаем по имени таблицы, БЕЗ указания схемы)
                            TableTimedModel timedTable = null;
                            if (timedDB != null && !timedDB.isEmpty()) {
                                timedTable = timedDB.getTable(currentTableName);
                            }

                            // Получаем от класса-помощника сгенерированный sql-запрос для выгрузки текущей таблицы на диск
                            String sql = DataExportSQLBuilder.getExportTableSQL(currentTable, integrityTable, timedTable);
                            // Если от класса-помощника мы получили не пустой запрос - работаем
                            if (!StringUtils.isBlank(sql)) {
                                logger.debug("Export SQL is OK! Processing.");
                                try {
                                    // Непосредственно получение данных и метаданных из таблицы
                                    ResultSet rs = stmt.executeQuery(sql);
                                    ResultSetMetaData rsmeta = rs.getMetaData();
                                    logger.debug("DATA and METADATA was received. Processing.");

                                    // Проход по всему полученному курсору данных
                                    int counter = 1; // <- счетчик количества обработанных строк из данного курсора
                                    int packCounter = 1; // <- счетчик количества созданных файлов с сериализованными данными из данной таблицы
                                    // Если есть данные - обрабатываем их
                                    if (rs.next()) {
                                        // Создаем новую модель таблицы для сериализации данных. Указываем схему и тип таблицы.
                                        TableDTOModel tableDTOModel = new TableDTOModel(currentTableName);
                                        tableDTOModel.setTableSchema(currentTable.getTableSchema());
                                        tableDTOModel.setTableType(currentTable.getTableType());
                                        // Если найдены данные - создаем каталог для сериализуемой таблицы. Если каталог для текущей сериализуемой
                                        // таблицы создать не удалось - возбуждается ИС и обработка текущей таблицы прекращается
                                        if (!new File(pathToTable).exists()) {
                                            logger.debug("Creating catalog [" + pathToTable + "] for current table [" + currentTableName + "].");
                                            if (!new File(pathToTable).mkdirs()) {
                                                throw new IOException("Can't create catalog [" + pathToTable + "]!");
                                            } else {
                                                logger.debug("Catalog for current table created successfully.");
                                            }
                                        }

                                        // Есди найдены данные для сериализации - надо установить флаг, который будет возвращен данным методом -
                                        // некоторые данные были сериализованы (записаны на диск)
                                        if (!isDataSerialized) {
                                            isDataSerialized = true;
                                        }

                                        // Отладочный вывод - сообщаем в журнал о начале обработки (выгрузки) данных таблицы
                                        logger.debug("Starting export table [" + currentTableFullName + "]. Creating data files.");
                                        // Непосредственно обработка полученного курсора с данными текущей таблицы (обрабатываем все строки
                                        // курсора и формируем файлы, состоящие из объектов-строк)
                                        do {
                                            // Начинаем формировать новую строку таблицы
                                            RowDTOModel rowModel = new RowDTOModel();
                                            // Цикл прохода по полям одной строки из курсора данных
                                            for (int i = 1; i <= currentTable.getFields().size(); i++) {
                                                // Имя текущего поля
                                                String fName = rsmeta.getColumnName(i);
                                                // Если имя поля не пусто - получаем тип поля и заносим его (поле) в модель строки
                                                if (!StringUtils.isBlank(fName)) {
                                                    // Получаем тип текущего поля из модели данных
                                                    //int fType = currentDbModel.getTable(currentTableName).getField(fName).getJavaDataType();
                                                    int fType = currentTable.getField(fName).getJavaDataType();
                                                    // Добавляем поле в модель строки таблицы (с именем, значением, типом)
                                                    rowModel.addField(new FieldDTOModel(fName, rs.getString(i), fType));
                                                }
                                                // Если же имя поля пусто - в модель строки поле НЕ заносим!
                                                else {
                                                    logger.error("Empty field name! Check the source table!");
                                                }
                                            }
                                            // Добавление сформированной модели строки таблицы в модель таблицы
                                            tableDTOModel.addRow(rowModel);

                                            // Отладочный вывод (сообщаем в лог о ходе обработки данных)
                                            if ((config.getOperationsCount() > 0) && (counter % config.getOperationsCount() == 0)) {
                                                logger.debug("# " + counter + " rows of table [" + currentTable.getTableName() + "] processed.");
                                            }

                                            // Сериализация набора из N записей (запись этого набора записей в файл на диске). Сериализуются
                                            // только наборы из количества записей, кратного значению фракции сериализации (указывается в конфиге),
                                            // если же количество записей в таблице (сериализуемом курсоре) не кратно значению фракции сериализации,
                                            // то оставшиеся записи сериализуются после окончания цикла (это необходимо для того, чтобы избежать
                                            // использования ресурсоемкого оператора ResultSet.isLast()).
                                            if ((counter % serializeFraction == 0) /** || ((counter%serializeFraction != 0) && (rs.isLast()))*/) {
                                                // Получаем имя для очередного записываемого файла. Имя строится по определенным правилам (см.
                                                // документацию/комментарии к методу getNameForFile())
                                                String fileName = MyCommonUtils.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, String.valueOf(packCounter));

                                                // Вызов модуля MyIOUtils для сериализации объекта
                                                MyIOUtils.serializeObject(tableDTOModel, pathToTable, fileName, null, false);
                                                logger.debug("### created file [" + fileName + "]");

                                                // Создание нового объекта TableDTOModel (обнуление ссылки на текущий экземпляр класса). Обязательно
                                                // указываем схему и тип таблицы.
                                                tableDTOModel = new TableDTOModel(currentTableName);
                                                tableDTOModel.setTableSchema(currentTable.getTableSchema());
                                                tableDTOModel.setTableType(currentTable.getTableType());
                                                // Увеличение счетчика файлов с данными
                                                packCounter++;
                                            }
                                            // Увеличение счетчика количества обработанных строк
                                            counter++;
                                        }
                                        while (rs.next()); // Конец цикла обработки данных текущей обрабатываемой таблицы

                                        // Сериализуем остаток записей из курсора, количество которых (записей) не кратно
                                        // значению фракции сериализации.
                                        if ((counter - 1) % serializeFraction != 0) {
                                            logger.debug("Serializing records remainder [" + ((counter - 1) % serializeFraction) + " records].");
                                            // Получаем имя для очередного записываемого файла. Имя строится по определенным правилам (см.
                                            // документацию/комментарии к методу getNameForFile())
                                            String fileName = MyCommonUtils.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, String.valueOf(packCounter));
                                            // Вызов модуля MyIOUtils для сериализации объекта

                                            MyIOUtils.serializeObject(tableDTOModel, pathToTable, fileName, null, false);
                                            logger.debug("### created file [" + fileName + "]");

                                        }
                                        // Отладочный вывод - всего обработано строк в таблице
                                        logger.debug("Table [" + currentTableFullName + "]. Total processed records [" + (counter - 1) + "].");
                                        // Закрываем за собой курсор
                                        rs.close();
                                    }
                                    // Если же данных нет - сообщаем об этом в лог
                                    else {
                                        logger.error("Data for current table [" + currentTableFullName + "] is empty!");
                                    }
                                }
                                // Перехват некритических ИС. При возникновении некритической ИС работа модуля продолжается.
                                catch (IOException e) {
                                    logger.error("I/O error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");
                                } catch (SQLException e) {
                                    logger.error("SQL error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");
                                }
                            }
                            // Если же получен пустой запрос - ошибка! Ничего не делаем!
                            else {
                                logger.error("Export SQL for current table [" + currentTableFullName + "] is empty!");
                            }
                        }
                        // Если в таблице нет ключевого поля - сообщаем в лог и таблицу не выгружаем
                        else {
                            logger.warn("Key field [" + config.getKeyFieldName() + "] not found in table [" + currentTableFullName + "]! Can't process!");
                        }
                    }
                    // Если же таблица подпала под ограничения - выводим об этом сообщение и не обрабатываем ее
                    else {
                        logger.warn("Current table [" + currentTableFullName + "] is not allowed! Skipping.");
                    }
                }
                // Если же таблица имеет тип отличный от TABLE - она не выгружается на диск
                else {
                    logger.warn("Can't unload to disk table [" + currentTableFullName + "] with type [" + currentTable.getTableType() + "]!");
                }
            }
            // Если же таблица пуста - выведем ошибку в лог
            else {
                logger.error("Current table model is empty! Can't process table!");
            }

        } // [END OF FOR] Окончание цикла обработки всех таблиц из модели данных текущей (экспортируемой) БД

        // Если ничего не было сериализовано, то надо удалить созданный каталог для БД
        if (!isDataSerialized) {
            if (!new File(localPathToDB).delete()) {
                logger.warn("Can't delete created loading catalog [" + localPathToDB + "]!");
            } else {
                logger.debug("Catalog [" + localPathToDB + "] was deleted successfully.");
            }
        }
        // Возвращение результата работы метода
        return isDataSerialized;
    }

    /**
     * Метод предназначен только для тестирования данного класса.
     *
     * @param args String[] параметры метода main.
     */
    public static void main(String[] args) throws org.apache.commons.configuration2.ex.ConfigurationException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        // Логгер текущего класса
        Logger logger = Logger.getLogger(DBUnloadCore.class.getName());
        try {
            // Выгрузка БД СУПИД на диск
            DBConfig config = new DBConfig("jdb_java_module/dbConfigs/ifxNormDocsConfig.xml");
            DBLoaderConfig loader = new DBLoaderConfig();
            loader.setDbConfig(config);
            loader.setPath("c:\\temp\\norm_docs");
            DBUnloadCore.unload(loader);
        } catch (DBModuleConfigException e) {
            logger.error(e.getMessage());
        } catch (DBConnectionException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (DBModelException e) {
            logger.error(e.getMessage());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

    }

}