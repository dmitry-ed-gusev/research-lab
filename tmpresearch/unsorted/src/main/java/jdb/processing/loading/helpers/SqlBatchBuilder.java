package jdb.processing.loading.helpers;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import dgusev.io.MyIOUtils;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import jdb.model.dto.TableDTOModel;
import jdb.processing.data.DataChecker;
import jdb.processing.sql.generation.DataChangeSQLBuilder;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс-помощник модуля десериализации данных. Метод данного класса позволяет получать готовый к исполнению sql-batch
 * из одного файла сериализованных табличных данных. Метод может использовать фильтрацию sql-запроса, в основном
 * фильтрация используется для удаления/замены "запрещенных" одиночных кавычек (они могут обвалить выполнение запроса).
 * Фильтрация полученного запроса для удаления остальных "запрещенных" символов используется фильтрация на этапе
 * выполнения sql-запроса.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 22.06.2010)
*/

public class SqlBatchBuilder 
 {
  /** Логгер данного класса. */
  private static Logger logger = Logger.getLogger(SqlBatchBuilder.class.getName());

  /**
   * Метод десериализует часть данных таблицы из указанного файла fullFilePath. Формат этого файла: zip-архив, в
   * котором должен находиться ОДИН файл с сериализованным объектом TableDTO (см. пакет jlib.db.model.dto), в котором,
   * в свою очередь, находятся строки таблицы - все или нет. Если в архиве более одного файла - будет обработан только
   * ПЕРВЫЙ из них. Соединение с СУБД данному методу необходимо для правильного формирования sql-батча - проверяется
   * существование записи, полученной из файла по ключевому полю - если такая запись в таблице уже есть, то будет
   * сформирован update-запрос, если же такой записи нет, то будет сформирован insert-запрос. Параметр deleteSource
   * указывает - удалять или нет исходный файл после обработки данных. Имя таблицы tableName указывает, данные какой
   * таблицы содержатся в указанном файле (соответствие имен проверяется после чтения(десериализации) объекта), если
   * параметр пуст, то возникает ИС SQLException, если же параметр не пуст, то проверяется соответствие имени
   * таблицы из файла указанному имени и, если эти имена не совпадают, то обработка файла не производится (метод возвращает
   * значение null). Метод может использовать фильтрацию sql-запросов.
   * @param config DBConfig конфигурация для соединения с СУБД.
   * @param fullFilePath String полный путь к архиву с сериализованным объектом.
   * @param deleteSource boolean удалять или нет исходный файл после удачной десериализации таблицы.
   * @param tableName String имя таблицы, данные которой лежат в указанном файле.
   * @param useSqlFilter boolean использовать или нет фильтрацию sql-запросов.
   * @throws java.io.IOException ошибка при распаковке файла или при чтении с диска.
   * @throws java.sql.SQLException ИС при выполнении анализа таблицы БД.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
   * @throws jdb.exceptions.DBModelException ошибки модели базы данных.
   * @return ArrayList[String] sql-batch, полученный в результате десериализации файла с частью табличных данных.
  */
  public static ArrayList<String> getBatchFromTableFile(DBConfig config, String fullFilePath, boolean deleteSource,
   String tableName, boolean useSqlFilter)
   throws IOException, SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {
    logger.debug("WORKING SqlBatchBuilder.getBatchFromTableFile().");
    // Результирующий sql-batch
    ArrayList<String> sqlBatch = null;

    // Если нам передан конфиг с ошибками, возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Если же конфиг в порядке - выведем отладочной сообщение
    else {logger.debug("DBMS connection config is OK. Processing.");}

    // Проверка соединения с СУБД с помощью указанного конфига (без соединения с СУБД не построить правильный sql-batch)
    if (!DBUtils.isConnectionValid(config)) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}

    // Проверка переданного пути к файлу и проверка существования файла, и проверка что это именно файл
    if (StringUtils.isBlank(fullFilePath)) {throw new IOException("Received path is empty!");}
    else if (!new File(fullFilePath).exists()) {throw new IOException("File [" + fullFilePath + "] doesn't exists!");}
    else if (!new File(fullFilePath).isFile()) {throw new IOException("Path [" + fullFilePath + "] not a file!");}
    else {logger.debug("File [" + fullFilePath + "] is OK. Deserializing (reading) object.");}
    // Проверка указанного имени таблицы (не пусто ли оно)
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Specifyied table name is empty!");}
    else {logger.debug("Table name is OK.");}

    // Получение из файла объекта TableDTOModel
    TableDTOModel tableDTOModel;
    try
     {
      tableDTOModel = (TableDTOModel) MyIOUtils.deserializeObject(fullFilePath, deleteSource, false);
      logger.debug("Object TableDTOModel was deserialized from file [" + fullFilePath + "]. Checking object.");
     }
    // Несоответсвие классов - преобразуем в IOException
    catch (ClassCastException e) {throw new IOException(e.getMessage());}
    // Класс не найден - также преобразуем в IOException
    catch (ClassNotFoundException e) {throw new IOException(e.getMessage());}

    // Если полученный объект пуст - ошибка
    if ((tableDTOModel == null) || (tableDTOModel.getRows() == null) || (tableDTOModel.getRows().isEmpty()))
     {throw new DBModelException("Deserialized TableDTOModel object is EMPTY!");}

    // Если имя полученной таблицы пусто - ошибка
    String currentTableName = tableDTOModel.getTableName();
    if (StringUtils.isBlank(currentTableName)) {throw new DBModelException("Table name in deserialized object TableDTOModel is EMPTY!");}
    // Проверяем соответствие указанного имени таблицы с именем таблицы из файла - если они не совпадут - ИС (обработка прекращается).
    else if (!tableName.equalsIgnoreCase(currentTableName))
     {throw new SQLException("Specifyied table name [" + tableName + "] doesn't match table name ["
       + currentTableName + "] from object!");}

    // Отладочный вывод - десериализованный объект в порядке - начинаем его обработку
    logger.debug("Object TableDTOModel is OK [" + tableDTOModel.getRows().size() + " record(s)]. Processing.");

    // После успешной десериализации и проверки объекта из файла необходимо построить sql-batch. Алгоритм следующий -
    // ищем в БД запись с нужным идентификатором, если она найдена, то формируем UPDATE sql-запрос, если же не найдена -
    // INSERT sql-запрос. Перед обработкой данных из объекта надо проверить - не подпадает ли таблица (чьи данные
    // содержатся в объекте) под какие-нить ограничения данного модуля - списки "разрешенных" и "запрещенных" таблиц,
    // эти списки содержатся в конфиге.

    // Если текущая таблица подпадает под ограничения - наффик!
    if (config.isTableAllowed(currentTableName))
     {
      logger.debug("Table [" + currentTableName + "] is allowed. Processing.");
      
      Connection connection = DBUtils.getDBConn(config);
      // Счетчик обработанных записей
      // Таблица не подпадает под ограничения - обрабатываем в цикле каждую запись, полученную из объекта TableDTOModel
      for (RowDTOModel rowModel : tableDTOModel.getRows())
       {
        // Получаем значение ключевого поля для данной таблицы
        int id;
        FieldDTOModel keyFieldModel = rowModel.getFieldByName(DBConsts.FIELD_NAME_KEY);
        // Если ключевое поле в записи не найдено - запись не обрабатывается
        if (keyFieldModel != null)
         {
          // Получаем значение ключевого поля. Если удалось получить значение - ищем запись с таким идентификатором
          // в таблице, если нашли - выполняем обновления данной строки (запрос UPDATE), если же не нашли -
          // выполняем добавление данной строки (запрос INSERT)
          try {id = Integer.parseInt(keyFieldModel.getFieldValue());}
          catch (NumberFormatException e)
           {logger.error("Can't parse KEY value! [" + e.getMessage() + "]. Record was skipped!"); id = 0;}

          // Если удалось получить значение ключевого поля - ищем запись с таким же идентификатором в таблице
          if (id > 0)
           {
            String dataUpdateSql;
            // Проверка существования записи с указанным значением ключевого поля. Если запись найдена - генерируем
            // UPDATE-запрос, если же нет - генерируем INSERT-запрос
            if (DataChecker.isRecordExists(connection, currentTableName, DBConsts.FIELD_NAME_KEY, id))
             {dataUpdateSql = DataChangeSQLBuilder.getDataUpdateSql(currentTableName, rowModel, id, useSqlFilter);}
            // Если запись не найдена - генерация запроса INSERT
            else
             {dataUpdateSql = DataChangeSQLBuilder.getDataInsertSql(currentTableName, rowModel, useSqlFilter);}

            // Если сгенерированный запрос не пуст - добавляем его к полученному списку (батчу). Если результирующий
            // батч еще не инициализирован - инициализация.
            if (!StringUtils.isBlank(dataUpdateSql))
             {
              if (sqlBatch == null) {sqlBatch = new ArrayList<String>();}
              sqlBatch.add(dataUpdateSql);
             }
            // Если же запрос пуст - сообщим в лог
            else {logger.warn("Generated data update/insert query is empty!");}
           }
          // Ключевое поле в данной строке не имеет значения (значение получить не удалось)
          else {logger.error("Can't receive KEY field [" + DBConsts.FIELD_NAME_KEY + "] value.");}

        }
       // Ключевое поле не найдено в обрабатываемой строке
       else {logger.warn("KEY field in rowModel was not found!");}
      } // END OF FOR CYCLE

      // Закроем ненужное подключение
      if (connection != null) {connection.close();}
     }
    // Если же обработка таблицы не разрешена - сообщим об ошибке
    else {logger.warn("Current table [" + currentTableName + "] is deprecated or not allowed! Skipping.");}
    // Возвращаем сформированный sql-batch
    return sqlBatch;
   }

 }