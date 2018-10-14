package jdb.processing.modeling;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.structure.key.IndexedField;
import jdb.model.time.DBTimedModel;
import jdb.model.time.TableTimedModel;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.processing.sql.execution.SqlExecutor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Данный класс реализует несколько методов построения различных моделей баз данных - модели
 * структуры, модели с указанием времени, модели целостности, модели для переноса данных. Методы
 * построения моделей используют функциональность и вспомогательные методы модуля DBSpider.
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 23.07.2010)
*/

public class DBModeler extends DBCommonProcessor
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  public DBModeler(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * Метод превращает курсор (ResultSet), полученный при вызове DatabaseMetaData.getColumns(),
   * в список объектов типа FieldStructureModel - полей таблицы TableStructureModel. Список генерируется
   * для таблоицы с именем tableName. После использования курсор закрывается.
   * @param rs ResultSet обрабатываемый курсор данных.
   * @return TreeSet<FieldStructureModel> полученный список.
   * @throws SQLException ошибка при обработке курсора.
   * @throws DBModelException ошибка при работе с моделью БД
  */
  private TreeSet<FieldStructureModel> getStructureFieldsList(ResultSet rs) throws SQLException, DBModelException 
   {
    TreeSet<FieldStructureModel> result = null;
    // Если курсор не пуст и в нем есть записи - работаемс
    if ((rs != null) && (rs.next()))
     {
      result = new TreeSet<FieldStructureModel>();
      // Обрабатывем поля текущей таблицы (если они есть)
      do
       {
        // Создаем экземпляр класса FielsStructureModel
        FieldStructureModel field = new FieldStructureModel(rs.getString(DBConsts.META_DATA_COLUMN_NAME),
         rs.getInt(DBConsts.META_DATA_COLUMN_DATA_TYPE), rs.getString(DBConsts.META_DATA_COLUMN_TYPE_NAME),
          rs.getInt(DBConsts.META_DATA_COLUMN_SIZE));
        // Может ли данное поле принимать значение NULL
        if (rs.getInt(DBConsts.META_DATA_COLUMN_NULLABLE) == DatabaseMetaData.columnNoNulls) {field.setNullable(false);}
        else {field.setNullable(true);}
        // Значение по умолчанию для данного поля
        String defaultValue = rs.getString(DBConsts.META_DATA_COLUMN_DEFAULT);
        if (defaultValue != null) field.setDefaultValue(defaultValue);
        // Добавляем поле в результирующий список
        result.add(field);
       }
      while (rs.next());
      rs.close();
     }
    return result;
   }

  /**
   * Метод превращает курсор (ResultSet), полученный при вызове DatabaseMetaData.getIndexInfo(), в список индексов
   * таблицы - список объектов типа IndexedField. После использования курсор закрывается. Также для формирования
   * списка индексированных полей используется другой курсор данных, полученный при вызове
   * DatabaseMetaData.getPrimaryKeys(). Второй курсор нужен для того, чтобы пометить индексированные поля,
   * входящие в первичный ключ.
   * Метод является вспомогательным для public-метода данного модуля - getDBStructureModel.
   * @param primaryKeysRS ResultSet курсор со списком полей, которые входят в первичный ключ.
   * @param indexesRS ResultSet курсор со списком всех индексированных полей таблицы.
   * @return TreeSet<IndexedField> результирующий список индексированных полей.
   * @throws SQLException ошибка при обработке курсора.
   * @throws DBModelException ошибка при работе с моделью БД.
  */
  private TreeSet<IndexedField> getStructureIndexesList(ResultSet primaryKeysRS, ResultSet indexesRS) throws SQLException, DBModelException
   {
    TreeSet<IndexedField> result = null;
    // Если курсор со списком не пуст и в нем есть записи - работаемс. Курсор со списком полей первичного ключа
    // не проверяем - т.к. если нет индексов вообще - нет и первичного ключа в частности.
    if ((indexesRS != null) && (indexesRS.next()))
     {
      // Для начала обработаем список полей первичного ключа - если список не пуст
      TreeSet<String> primaryKeysList = null;
      if (primaryKeysRS.next())
       {
        primaryKeysList = new TreeSet<String>();
        do
         {
          String keyColumn = primaryKeysRS.getString(DBConsts.META_DATA_COLUMN_NAME);
          if (!StringUtils.isBlank(keyColumn)) {primaryKeysList.add(keyColumn.toUpperCase());}
         }
        while (primaryKeysRS.next());
        primaryKeysRS.close();
       }

      // Теперь обрабатываем курсор со списком индексов
      result = new TreeSet<IndexedField>();
      do
       {
        // Получаем имя индекса и имя индексируемого поля
        String indexName = indexesRS.getString(DBConsts.META_DATA_INDEX_NAME);
        String fieldName = indexesRS.getString(DBConsts.META_DATA_COLUMN_NAME);
        // Если полученные имена не пусты - заносим индекс в список
        if (!StringUtils.isBlank(indexName) && !StringUtils.isBlank(fieldName))
         {
          // Создаем новый индекс и заносим его в список
          IndexedField field = new IndexedField(indexName, fieldName);
          // Признак уникальности индекса и его тип
          //field.setType(tmpRS.getShort("TYPE"));
          field.setUnique(!indexesRS.getBoolean(DBConsts.META_DATA_NON_UNIQUE));
          // Признак - входит ли данное поле в первичный ключ
          if ((primaryKeysList != null) && (primaryKeysList.contains(fieldName.toUpperCase()))) {field.setPrimaryKey(true);}
          // Непосредственно добавление индексируемого поля в список
          result.add(field);
         }
       }
      while (indexesRS.next());
     }
    return result;
   }

  /**
   * Метод выполняет анализ БД соответствующей текущей конфигурации подключения данного модуля к СУБД и создает
   * программную модель БД - экземпляр класса DBStructureModel. Если исходная БД на сервере пуста (не содержит таблиц),
   * то и созданная модель также будет пуста. Параметр dbName позволяет указать для создания модели БД другую БД, отличную
   * от БД, к которой мы подключались. Если БД не сущесвует в СУБД, к которой мы подключаемся, возникает ИС.
   * @param dbName String имя БД в данной СУБД, для которой необходимо построить программную модель.
   * @return DatabaseModel созданная модель БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибка при работе с моделью БД.
  */
  public DBStructureModel getDBStructureModel(String dbName)
   throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {
    // Если конфигурация модуля содержит ошибки - возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // Проверяем наличие указанной БД. Если указана БД как параметр, то используем ее, если же нет -
    // используем БД, указанную в конфигурации соединения. Если ни там, ни там имя БД не указано - ИС!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // Для типов СУБД DBF и ODBC нельзя переключиться на другую БД при подключении
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // Для других СУБД - все ОК! Работаем.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // Если с именем БД все в порядке, то выводим отладочное сообщение
    logger.debug("getDBStructureModel(): creating structure model for DB [" + localDbName + "].");

    // Экземпляр класса DBSpider - для выполнения вспомогательных задач
    DBSpider spider = new DBSpider(this.getConfig());
    // Проверяем существование БД, перед тем, как что-то делать
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}

    // Возвращаемый объект - модель структуры БД
    DBStructureModel database = new DBStructureModel(localDbName);
    // Установим тип СУБД, в которой хранится данная БД
    database.setDbType(this.getConfig().getDbType());
    // Список таблиц текущей БД - он нужен для перебора всех таблиц
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);

    // Если список таблиц получить удалось - обрабатываем его
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      // Объект соединение с СУБД
      Connection connection = null;
      try
       {
        // Соединение с СУБД и получение метаданных о БД
        connection = DBUtils.getDBConn(this.getConfig());
        // Объект с метаинформацией о СУБД
        DatabaseMetaData  metaData   = connection.getMetaData();

        // Для СУБД Информикс необходимо выбрать БД, если мы хотим получить данные по БД отличной, от БД подключения
        // (указанной в конфиге соединения) или если БД в конфиге подключения не указана вовсе.
        if (this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX))
         {
          if (StringUtils.isBlank(this.getConfig().getDbName()) ||
              (!StringUtils.isBlank(this.getConfig().getDbName()) &&
               !this.getConfig().getDbName().equalsIgnoreCase(localDbName)))
           {SqlExecutor.executeUpdateQuery(connection, "database " + localDbName);}
         }

        // Перебор всех таблиц указанной БД и добавление их в модель БД
        for (TableModel table : tablesList)
         {
          // Если таблица не НУЛЛ и имя таблицы не пусто - обрабатываем ее
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            logger.debug("Processing model for table [" + table.getTableName() + "].");
            // Конструкция try->catch применена здесь для перехвата ИС при создании новой модели структуры таблицы или
            // при создании полей таблицы или при создании моделей индексов таблицы. Если на одном из этапов произошла ИС -
            // текущая модель таблицы не попадет в результирующую модель БД.
            try
             {
              // Выбираем схему для выборки данных о таблице
              String schemaName = null;
              if (!StringUtils.isBlank(table.getTableSchema())) {schemaName = table.getTableSchema();}
              // Собираем инфу о таблице
              ResultSet columnsRS     = metaData.getColumns(localDbName, schemaName, table.getTableName(), null);
              ResultSet indexesRS     = metaData.getIndexInfo(localDbName, schemaName, table.getTableName(), false, false);
              ResultSet primaryKeysRS = metaData.getPrimaryKeys(localDbName, schemaName, table.getTableName());
              // Создаем объект модель таблицы БД
              TableStructureModel tableModel = new TableStructureModel(table.getTableName());
              // Информация о схеме и типе текущей таблицы
              tableModel.setTableSchema(table.getTableSchema());
              tableModel.setTableType(table.getTableType());
              // Информация о полях текущей обрабатываемой таблицы
              tableModel.setFields(this.getStructureFieldsList(columnsRS));
              // Информация об индексах и первичных ключах текущей обрабатываемой таблицы
              tableModel.setIndexes(this.getStructureIndexesList(primaryKeysRS, indexesRS));
              // Непосредственно добавляем объект таблица БД в объект БД - если не возникло ни одной ИС
              database.addTable(tableModel);
             }
            // Перехват возможной ИС при обработке/построении модели таблицы
            catch (DBModelException e)
             {logger.error("Error while processing table [" + table.getTableName() + "]. Message: " + e.getMessage());}
           }
          // Если же таблица НУЛЛ или имя таблицы пусто - сообщаем в лог!
          else {logger.warn("NULL table in tables list or table name is empty!");}
         } // END OF FOR CYCLE

        // Также для СУБД Информикс необходимо вернуть БД в исходное состояние (если мы применяли оператор "DATABASE ...")
        if (this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX))
         {
          // Если в конфиге соединения не указана БД - закроем выбранную
          // todo: читай доку информикса про "CLOSE DATABASE" - может нет необходимости?
          if (StringUtils.isBlank(this.getConfig().getDbName()))
           {SqlExecutor.executeUpdateQuery(connection, "close database");}
          // Если же в конфиге соединения указана БД, но она не совпадает с запрашиваемой - вернем исходную БД
          else if (!StringUtils.isBlank(this.getConfig().getDbName()) &&
                   !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
           {SqlExecutor.executeUpdateQuery(connection, "database " + this.getConfig().getDbName());}
         }
        
       } // END OF TRY
      // Пробуем закрыть соединение с СУБД и окрытые курсоры
      finally {if (connection != null) connection.close();}
     }
    // Если же полученный список таблиц пуст - запишем в лог
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // Возвращаем результат
    return database;
   }

  /**
   * Метод выполняет анализ БД соответствующей текущей конфигурации подключения данного модуля к СУБД и создает
   * программную модель БД - экземпляр класса DBStructureModel. Если исходная БД на сервере пуста (не содержит таблиц),
   * то и созданная модель также будет пуста. Для создания модели используется БД, указанная в конфигурации подключения
   * к СУБД. Если БД не сущесвует в СУБД, к которой мы подключаемся, возникает ИС.
   * @return DatabaseModel созданная модель БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибка при работе с моделью БД.
  */
  public DBStructureModel getDBStructureModel() throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {return this.getDBStructureModel(null);}

  /**
   * Метод строит для текущей БД модель для проверки ее (БД) целостности. Если модель построить не удается -
   * метод возвращает значение null. Если исходная БД на сервере пуста - метод вренет такую же модель (не null, но с
   * пустым списком таблиц).
   * @param dbName String имя БД в данной СУБД, для которой необходимо построить программную модель.
   * @return DBIntegrityModel модель текущей БД или значение null.
   * @throws SQLException ошибка при выполнении sql-запроса или при обработке его результатов.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибка при работе с моделью БД.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public DBIntegrityModel getDBIntegrityModel(String dbName)
   throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {
    // Если конфигурация модуля содержит ошибки - возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // Проверяем наличие указанной БД. Если указана БД как параметр, то используем ее, если же нет -
    // используем БД, указанную в конфигурации соединения. Если ни там, ни там имя БД не указано - ИС!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // Для типов СУБД DBF и ODBC нельзя переключиться на другую БД при подключении
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // Для других СУБД - все ОК! Работаем.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // Если с именем БД все в порядке, то выводим отладочное сообщение
    logger.debug("getDBIntegrityModel(): creating integrity model for DB [" + localDbName + "].");

    // Экземпляр класса DBSpider - для выполнения вспомогательных задач
    DBSpider spider = new DBSpider(this.getConfig());
    // Проверяем существование БД, перед тем, как что-то делать
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}
    
    // Модель БД для проверки ее целостности - результат работы данного метода
    DBIntegrityModel integrity  = new DBIntegrityModel(localDbName);
    // Установим тип СУБД, в которой хранится данная БД
    integrity.setDbType(this.getConfig().getDbType());
    // Список таблиц текущей БД - он нужен для перебора всех таблиц
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);
    
    // Если полученный список таблиц БД не пуст - работаем
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      Connection connection = null;
      Statement  stmt       = null;
      try
       {
        connection     = DBUtils.getDBConn(getConfig());
        stmt           = connection.createStatement();

        // Выбор sql-запроса для переключения БД (для разных СУБД)
        String selectDBQuery = null;
        switch (this.getConfig().getDbType())
         {
          case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: selectDBQuery = "use "      + localDbName; break;
          case INFORMIX:                                  selectDBQuery = "database " + localDbName; break;
         }
        // Если полученный запрос не пуст - выполняем его
        if (!StringUtils.isBlank(selectDBQuery) &&
            (StringUtils.isBlank(this.getConfig().getDbName()) ||
             (!StringUtils.isBlank(this.getConfig().getDbName()) &&
              !this.getConfig().getDbName().equalsIgnoreCase(localDbName))))
         {SqlExecutor.executeUpdateQuery(connection, selectDBQuery);}

        // Проходим по всем таблицам БД и создаем для каждой таблицы модель
        for (TableModel table : tablesList)
         {
          // Если таблица не НУЛЛ и имя таблицы не пусто - обрабатываем ее
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            String fullTableName;
            if (!StringUtils.isBlank(table.getTableSchema()))
             {fullTableName = table.getTableSchema() + "." + table.getTableName();}
            else
             {fullTableName = table.getTableName();}

            logger.debug("Processing model for table [" + fullTableName + "].");
            ResultSet rs = null;
            // Конструкция try->catch применена здесь для перехвата ИС при создании новой модели таблицы. Если
            // произошла ИС - текущая модель таблицы не попадет в результирующую модель БД.
            try
             {
              StringBuilder sql = new StringBuilder("select ").append(DBConsts.FIELD_NAME_KEY).append(" from ");
              sql.append(fullTableName);
              rs = stmt.executeQuery(sql.toString());
              // Если в таблице нет ключей (ключевого поля или полей) - таблица не попадает в модель
              if (rs.next())
               {
                logger.debug("Keys list found for table [" + fullTableName + "].");
                // Создаем новую модель таблицы целостности
                TableIntegrityModel integrityTable = new TableIntegrityModel(table.getTableName());
                integrityTable.setTableSchema(table.getTableSchema());
                integrityTable.setTableType(table.getTableType());
                // В цикле добавляем все ключи из таблицы в модель
                do {integrityTable.addKey(rs.getInt(DBConsts.FIELD_NAME_KEY));} while (rs.next());
                // Непосредственно добавление таблицы в модель
                integrity.addTable(integrityTable);
               }
              // Сообщаем в лог, что ключи не нашли
              else {logger.debug("For table [" + fullTableName + "] keys list is not found!");}
             }
            // Перехват ИС, возникающих при обработке одной таблицы - чтобы неудача не обрушила весь цикл
            catch (SQLException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            catch (DBModelException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            // Обязательно освобождаем ресурсы
            finally                           {if (rs != null) rs.close();}
           } 
          // Если же таблица НУЛЛ или имя таблицы пусто - сообщаем в лог!
          else {logger.warn("NULL table in tables list or table name is empty!");}
         } // END OF FOR CYCLE

        // Выбор sql-запроса для переключения БД (для разных СУБД) - возврат БД в исходное состояние
        String returnDBQuery = null;
        // Выбираем запрос только если имя БД в конфигурации соединения не пусто
        if (!StringUtils.isBlank(this.getConfig().getDbName()))
         {
          switch (this.getConfig().getDbType())
           {
            case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: returnDBQuery = "use "      + this.getConfig().getDbName(); break;
            case INFORMIX:                                  returnDBQuery = "database " + this.getConfig().getDbName(); break;
           }
         }
        // Если полученный запрос не пуст - выполняем его (переключаем БД обратно)
        if (!StringUtils.isBlank(selectDBQuery) && !StringUtils.isBlank(this.getConfig().getDbName()) &&
            !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
         {SqlExecutor.executeUpdateQuery(connection, returnDBQuery);}

       }
      // Освобождаем ресурсы
      finally {if (stmt != null) {stmt.close();} if (connection != null) {connection.close();}}
     }
    // Если же полученный список пуст - сообщим в лог!
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // Возвращаем результат
    return integrity;
   }

  public DBIntegrityModel getDBIntegrityModel() throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {return this.getDBIntegrityModel(null);}
  
  /**
   * Метод создает и возвращает модель БД с указанием времени (класс DBTimedModel). Если исходная БД (на сервере)
   * пуста - т.е. не имеет таблиц, то и построенная модель будет пуста (без таблиц).
   * @return DatabaseTimeModel построенная модель Бд с указанием времени или значение null.
   * @throws SQLException ошибка при выполнении sql-запроса или при обработке его результатов.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
   * @throws DBModelException ошибка при работе с моделью БД.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public DBTimedModel getDBTimedModel(String dbName) 
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {
    // Если конфигурация модуля содержит ошибки - возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // Проверяем наличие указанной БД. Если указана БД как параметр, то используем ее, если же нет -
    // используем БД, указанную в конфигурации соединения. Если ни там, ни там имя БД не указано - ИС!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // Для типов СУБД DBF и ODBC нельзя переключиться на другую БД при подключении
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // Для других СУБД - все ОК! Работаем.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // Если с именем БД все в порядке, то выводим отладочное сообщение
    logger.debug("getDBTimedModel(): creating timed model for DB [" + localDbName + "].");

    // Экземпляр класса DBSpider - для выполнения вспомогательных задач
    DBSpider spider = new DBSpider(this.getConfig());
    // Проверяем существование БД, перед тем, как что-то делать
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}

    // Модель БД с указанием времени обновления таблиц - искомый результат
    DBTimedModel dbTimedModel = new DBTimedModel(this.getConfig().getDbName());
    // Установим тип СУБД, в которой хранится данная БД
    dbTimedModel.setDbType(this.getConfig().getDbType());
    // Список таблиц текущей БД - он нужен для перебора всех таблиц
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);

    // Если полученный список таблиц БД не пуст - работаем
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      Connection        connection   = null;
      Statement         stmt         = null;
      try
       {
        connection = DBUtils.getDBConn(getConfig());
        stmt       = connection.createStatement();

        // Выбор sql-запроса для переключения БД (для разных СУБД)
        String selectDBQuery = null;
        switch (this.getConfig().getDbType())
         {
          case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: selectDBQuery = "use "      + localDbName; break;
          case INFORMIX:                                  selectDBQuery = "database " + localDbName; break;
         }
        // Если полученный запрос не пуст - выполняем его
        if (!StringUtils.isBlank(selectDBQuery) &&
            (StringUtils.isBlank(this.getConfig().getDbName()) ||
             (!StringUtils.isBlank(this.getConfig().getDbName()) &&
              !this.getConfig().getDbName().equalsIgnoreCase(localDbName))))
         {SqlExecutor.executeUpdateQuery(connection, selectDBQuery);}

        // Перебор списка всех таблиц текущей БД
        for (TableModel table : tablesList)
         {
          // Если таблица не НУЛЛ и имя таблицы не пусто - обрабатываем ее
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            // Формируем полное имя таблицы (с указанием с указанием схемы для некоторых СУБД)
            String fullTableName;
            if (!StringUtils.isBlank(table.getTableSchema()))
             {fullTableName = table.getTableSchema() + "." + table.getTableName();}
            else
             {fullTableName = table.getTableName();}
            logger.debug("Processing model for table [" + fullTableName + "].");

            ResultSet rs = null;
            // Конструкция try->catch применена здесь для перехвата ИС при создании новой модели таблицы. Если
            // произошла ИС - текущая модель таблицы не попадет в результирующую модель БД.
            try
             {
              // Вытаскиваем из таблицы таймштамп (timestamp)
              StringBuilder sql = new StringBuilder("select max(").append(DBConsts.FIELD_NAME_TIMESTAMP);
              sql.append(") from ").append(fullTableName);
              rs = stmt.executeQuery(sql.toString());
              // Если есть результат - обработаем его
              if (rs.next())
               {
                logger.debug("MAX(TIMESTAMP) found for table [" + fullTableName + "].");
                TableTimedModel timedTable = new TableTimedModel(table.getTableName(), rs.getTimestamp(1));
                timedTable.setTableSchema(table.getTableSchema());
                timedTable.setTableType(table.getTableType());
                dbTimedModel.addTable(timedTable);
               }
              // Если нет данных - значит таблица просто пуста... Такая таблица не добавляется к результирующей модели.
              else {logger.warn("Table [" + fullTableName + "] doesn't have [" + DBConsts.FIELD_NAME_TIMESTAMP + "] field!");}
             } // END OF TRY
            // Перехват ИС нужен здесь для того, чтобы неудача при построении модели одной таблицы обрушила весь цикл
            catch (SQLException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            catch (DBModelException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            // Закрываем курсор - сберегаем ресурсы и природу!
            finally                {if (rs != null) rs.close();}
           }
          // Если же таблица НУЛЛ или имя таблицы пусто - сообщаем в лог!
          else {logger.warn("NULL table in tables list or table name is empty!");} 
         } // END FOR CYCLE

        // Выбор sql-запроса для переключения БД (для разных СУБД) - возврат БД в исходное состояние
        String returnDBQuery = null;
        // Выбираем запрос только если имя БД в конфигурации соединения не пусто
        if (!StringUtils.isBlank(this.getConfig().getDbName()))
         {
          switch (this.getConfig().getDbType())
           {
            case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: returnDBQuery = "use "      + this.getConfig().getDbName(); break;
            case INFORMIX:                                  returnDBQuery = "database " + this.getConfig().getDbName(); break;
           }
         }
        // Если полученный запрос не пуст - выполняем его (переключаем БД обратно)
        if (!StringUtils.isBlank(selectDBQuery) && !StringUtils.isBlank(this.getConfig().getDbName()) &&
            !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
         {SqlExecutor.executeUpdateQuery(connection, returnDBQuery);}

       }
      // В любом случае пытаемся закрыть открытое соединение и курсоры
      finally {if (stmt != null) stmt.close(); if (connection != null) connection.close();}
     }
    // Если же полученный список пуст - сообщим в лог!
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // Возвращаем результат
    return dbTimedModel;
   }

  public DBTimedModel getDBTimedModel()
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {return this.getDBTimedModel(null);}

  /**
   * Метод тестовый - нуждается в проверке. Используется для тестирования всей библиотеки.
   * @param current DBStructureModel
   * @param foreign DBStructureModel
   * @return String
  */
  public String getDifferenceReport(DBStructureModel current, DBStructureModel foreign)
   {
    // Результирующий отчет
    StringBuilder report;
    // Если полученные модели баз данных не пусты - обрабатываем их
    if ((current != null) && (foreign != null))
     {
      report = new StringBuilder("\nDIFFERENCE REPORT \n");
      // Таблицы текущей БД (список)
      report.append("DB [").append(current.getDbName()).append("] (").append(current.getDbType()).append(") current: ");
      if ((current.getTables() != null) && (!current.getTables().isEmpty()))
       {report.append("(").append(current.getTables().size()).append(") ").append(current.getCSVTablesList());}
      else {report.append("table list is empty.");}
      report.append("\n");
      // Таблицы внешней БД (список)
      report.append("DB [").append(foreign.getDbName()).append("] (").append(foreign.getDbType()).append(") foreign: ");
      if ((foreign.getTables() != null) && (!foreign.getTables().isEmpty()))
       {report.append("(").append(foreign.getTables().size()).append(") ").append(foreign.getCSVTablesList());}
      else {report.append("table list is empty.");}
      report.append("\n");

      // Если БД не совпадают при быстром сравнении - тогда генерируем отчет по разнице между ними
      if (!current.equals(foreign))
       {
        // Теперь надо сравнить списки таблиц - найти несовпадающие таблицы. Вначале проходим по
        // таблицам текущей БД и сравниваем их с таблицами внешней БД.
        report.append("\n***** COMPARE [").append(current.getDbName()).append("] (").append(current.getDbType());
        report.append(") (current) -> [").append(foreign.getDbName()).append("] (").append(foreign.getDbType());
        report.append(") (foreing)\n");
        int existsCounter = 0;
        int equalsCounter = 0;
        for (TableStructureModel table : current.getTables())
         {
          // Получаем таблицу с таким же именем из списка таблиц внешней БД
          TableStructureModel foreignTable = foreign.getTable(table.getTableName());
          // Если такая таблица отсутствует во внешней БД - в отчет
          if (foreignTable == null)
           {
            report.append("--> [exists] TABLE [").append(table.getTableName()).append("] DOESN'T EXISTS IN FOREIGN DB.\n");
            existsCounter++;
           }
          // Если же таблица существует - сравним таблицы (при несовпадении - в отчет).
          else if (!foreignTable.equals(table))
           {
            report.append("\n--> [equals] TABLE [").append(table.getTableName()).append("] DOESN'T EQUAL IN DBs.");
            report.append(table.getDifferenceReport(foreignTable));
            equalsCounter++;
           }
         }
        // Инфа о количестве несовпавших таблиц
        report.append("***** Not exists in foreign DB: ").append(existsCounter).append("; not equals: ");
        report.append(equalsCounter).append("\n");

        // Теперь проходим по таблицам внешней БД и сравниваем их с таблицами текущей БД.
        report.append("\n***** COMPARE [").append(foreign.getDbName()).append("] (").append(foreign.getDbType());
        report.append(") (foreign) -> [").append(current.getDbName()).append("] (").append(current.getDbType());
        report.append(") (current)\n");
        // Обнуляем счетчики
        int existsCounterBack = 0;
        int equalsCounterBack = 0;
        for (TableStructureModel table : foreign.getTables())
         {
          // Получаем таблицу с тем же именем из списка таблиц текущей БД
          TableStructureModel currentTable = current.getTable(table.getTableName());
          // Если такая таблица отсутствует в текущей БД - в отчет
          if (currentTable == null)
           {
            report.append("--> [exists] TABLE [").append(table.getTableName()).append("] DOESN'T EXISTS IN CURRENT DB.\n");
            existsCounterBack++;
           }
          // Если же таблица существует - сравним таблицы (при несовпадении - в отчет).
          else if (!currentTable.equals(table))
           {
            report.append("\n--> [equals] TABLE [").append(table.getTableName()).append("] DOESN'T EQUAL IN DBs.");
            report.append(table.getDifferenceReport(currentTable));
            equalsCounterBack++;
           }
         }
        // Инфа о количестве несовпавших таблиц
        report.append("***** Not exists in current DB: ").append(existsCounterBack).append("; not equals: ");
        report.append(equalsCounterBack).append("\n");

       }

      // Если при сравнении БД оказались равными - отчет не составляется
      else {report.append("DATABASES ARE EQUAL!");}
     }
    // Если переданный параметр пуст - скажем об этом в отчете
    else {report = new StringBuilder("Current and/or foreign database object is NULL!");}

    return report.toString();
   }

 }