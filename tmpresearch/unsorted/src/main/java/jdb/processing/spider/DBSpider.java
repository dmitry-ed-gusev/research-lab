package jdb.processing.spider;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.config.DBTableType;
import dgusev.dbpilot.config.DBType;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.model.TypeMapping;
import jdb.processing.DBCommonProcessor;
import jdb.processing.sql.execution.SqlExecutor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Данный модуль реализует движок анализа структуры любой СУБД. После анализа структуры БД возможно создание различных
 * моделей данной БД - см. классы в пакете jlib.db.model. Также метод учитывает наличие в СУБД Informix системного
 * каталога, таблицы которого не должны быть изменены пользователем (соответственно и не должны быть включены в
 * модели данной БД).
 * @author Gusev Dmitry (019gus)
 * @version 10.0 (DATE: 27.07.2010)
*/

public class DBSpider extends DBCommonProcessor
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * Конструктор по умолчанию. Инициализирует текущую конфигурацию. Также конструктор инициализирует необходимые
   * поля класса (в частности, списки системных таблиц и системного каталога СУБД Informix).
   * @param config DBConfig конфигурация модуля.
   * @throws DBModuleConfigException ИС возникает, если конструктору передана пустая конфигурация.
  */
  public DBSpider(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * Метод получает из СУБД информацию о маппингах типов данных DBMS -> JAVA и формирует список этих маппингов,
   * который возвращает. Если маппингов не найдено (нет инфы) или возникли какие-либо проблемы - метод
   * возвращает значение null.
   * @return ArrayList<TypeMapping> список найденных маппингов типов данных DBMS -> JAVA или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<TypeMapping> getTypesMappings() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getTypesMapping(): getting types mapping [DBMS->JAVA] for current DBMS.");

    // Если конфигурация модуля ошибочна - возбуждаем ИС!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // Результирующий список маппингов
    ArrayList<TypeMapping> mappings = null;
    Connection       connection = null;
    ResultSet        typesRS    = null;
    DatabaseMetaData metaData;
    try
     {
      // Соединение с СУБД и получение метаданных о БД
      connection = DBUtils.getDBConn(getConfig());
      metaData   = connection.getMetaData();
      typesRS    = metaData.getTypeInfo();
      // Перебор все маппингов типов данных СУБД к типам данных JAVA
      if (typesRS.next())
       {
        mappings = new ArrayList<TypeMapping>();
        do
         {
          TypeMapping typeMapping = new TypeMapping(typesRS.getString(DBConsts.META_DATA_COLUMN_TYPE_NAME),
                                                    typesRS.getInt(DBConsts.META_DATA_COLUMN_DATA_TYPE),
                                                    typesRS.getInt(DBConsts.META_DATA_COLUMN_PRECISION));
          mappings.add(typeMapping);
         }
        while (typesRS.next());
       }
     }
    // Закроем соединение с СУБД и окрытый курсор
    finally {if (typesRS != null) typesRS.close(); if (connection != null) connection.close();}
    return mappings;
   }

  /**
   * Метод возвращает список(ArrayList[TableModel]) таблиц данной БД (к которой подключились) или указанной в качестве
   * параметра БД. Если не удалось - метод возвращает null. Для типов СУБД DBF и ODBC смена БД подключения на ходу
   * невозможна - будет возникать ИС SQLException().
   * @param dbName String БД, для которой необходимо вернуть список таблиц. Если указана - используется это имя, если не
   * указана, то используется имя БД из конфига соединения. Если же и в конфиге соединения не указано имя - возникнет
   * ошибка - непонятно для какой БД показать список таблиц.
   * @param tableTypes TableType[] список типов таблиц, которые необходимо показать. Значения для списка - из
   * класса-перечисления DBConsts.TableType.
   * @return ArrayList[TableModel] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<TableModel> getUserTablesList(String dbName, DBTableType[] tableTypes)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // Если конфигурация модуля ошибочна - возбуждаем ИС!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // Проверяем наличие указанной БД. Если указана БД как параметр, то используем ее, если же нет -
    // используем БД, указанную в конфигурации соединения. Если ни там, ни там имя БД не указано - ИС!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // Для типов СУБД DBF и ODBC нельзя переключиться на другую БД при подключении
      if ((DBType.DBF.equals(this.getConfig().getDbType()) || DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // Для других СУБД - все ОК! Работаем.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // Если с именем БД все в порядке, то выводим отладочное сообщение
    logger.debug("getUserTablesList(): creating list of tables for DB [" + localDbName + "].");

    // Проверяем существование БД, перед тем, как что-то делать
    if (!this.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS! Processing.");}

    // Определяемся с типом получаемых таблиц
    String[] localTableTypes;
    if ((tableTypes != null) && (tableTypes.length > 0))
     {
      localTableTypes = new String[tableTypes.length];
      for (int i = 0; i < tableTypes.length; i++) {localTableTypes[i] = tableTypes[i].getStrValue();}
     }
    else {localTableTypes = null;}

    // Результирующий список таблиц
    ArrayList<TableModel> list       = null;
    Connection            connection = null;
    ResultSet             tablesRS   = null;
    DatabaseMetaData      metaData;
    
    try
     {
      // Соединение с СУБД и получение метаданных о БД
      connection = DBUtils.getDBConn(getConfig());

      // Для СУБД Информикс необходимо выбрать БД, если мы хотим получить список таблиц БД отличной, от БД подключения
      // (указанной в конфиге соединения) или если БД в конфиге подключения не указана вовсе.
      if (this.getConfig().getDbType().equals(DBType.INFORMIX))
       {
        if (StringUtils.isBlank(this.getConfig().getDbName()) ||
            (!StringUtils.isBlank(this.getConfig().getDbName()) && 
             !this.getConfig().getDbName().equalsIgnoreCase(localDbName)))
         {SqlExecutor.executeUpdateQuery(connection, "database " + localDbName);}
       }
      
      // Непосредственно получение метаданных о таблицах указанной БД
      metaData   = connection.getMetaData();
      tablesRS = metaData.getTables(localDbName, null, null, localTableTypes);

      // todo: убрать!!! только для отладки!!!
      //logger.info("\n\n" + DBUtils.getStringResultSet(tablesRS));
      //System.exit(0);

      // Если список таблиц получен - пройдемся по нему
      if ((tablesRS != null) && tablesRS.next())
       {
        logger.debug("Tables list received and not empty. Processing.");
        // Проход цикла по списку таблиц
        do
         {
          String tableName   = tablesRS.getString(DBConsts.META_DATA_TABLE_NAME);
          String tableSchema = tablesRS.getString(DBConsts.META_DATA_TABLE_SCHEM);
          String tableType   = tablesRS.getString(DBConsts.META_DATA_TABLE_TYPE);

          // Обработку выполняем если полученное имя таблицы не пусто
          if (!StringUtils.isBlank(tableName))
           {
            // Флажок - входит ли таблица в системный каталог своей СУБД
            boolean isTableSystem = false;

            // Проверяем таблицу на принадлежность к системному каталогу СУБД Информикс
            if (DBType.INFORMIX.equals(this.getConfig().getDbType()))
             {if (DBConsts.SYSCATALOG_INFORMIX.contains(tableName.toUpperCase())) {isTableSystem = true;}}
            // Проверяем таблицу на принадлежность к системному каталогу СУБД MS SQL
            else if (DBType.MSSQL_JTDS.equals(this.getConfig().getDbType()) || DBType.MSSQL_NATIVE.equals(this.getConfig().getDbType()))
             {
              // Для Сиквел-сервера проверяем принадлежность к системному каталогу только если указана схема для таблицы
              if (!StringUtils.isBlank(tableSchema))
               {
                String fullTableName = tableSchema.toUpperCase() + "." + tableName.toUpperCase();
                // Проверяем принадлежность таблицы к списку системных и проверяем схему таблицы - системная схема или нет.
                if (DBConsts.SYSCATALOG_MSSQL.contains(fullTableName) || "SYS".equals(tableSchema.toUpperCase()) ||
                    "INFORMATION_SCHEMA".equals(tableSchema.toUpperCase()))
                 {isTableSystem = true;}
               }
             }

            // Если таблица не системная - все ок, добавляем ее в список
            if (!isTableSystem)
             {
              logger.debug("Table [" + tableName + "] not system. Trying to add to tables list.");
              // Если произойдет ИС мы просто перейдем к следующей таблице
              try
               {
                TableModel tableModel = new TableModel(tableName);
                tableModel.setTableSchema(tableSchema);
                tableModel.setTableType(tableType);
                // Список инициализируется только в том случае, если найдена хоть одна таблица
                if (list == null) {list = new ArrayList<TableModel>();}
                list.add(tableModel);
               }
              catch (DBModelException e) {logger.error(e.getMessage());}
             }
            // Если же таблица оказалась системной - просто сообщим об этом
            else {logger.debug("Table [" + tableName + "] is system table in DBMS [" + this.getConfig().getDbType().getStrValue() + "].");}
           }
          // Если полученное имя таблицы пусто - обломс! :)
          else {logger.warn("Empty table name!");}
         }
        while (tablesRS.next());
       }
      // Если же список таблиц пуст - сообщим об этом в лог
      else {logger.warn("Received tables list is empty or NULL!");}

      // Также для СУБД Информикс необходимо вернуть БД в исходное состояние (если мы применяли оператор "DATABASE ...")
      if (this.getConfig().getDbType().equals(DBType.INFORMIX))
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

     }
    // Закроем соединение с СУБД и окрытые курсоры
    finally {if (tablesRS != null) {tablesRS.close();} if (connection != null) {connection.close();}}
    // Возвращаем результат
    return list;
   }

  /**
   * Метод возвращает список(ArrayList[TableModel]) таблиц данной БД (к которой подключились) или указанной в качестве
   * параметра БД. Если не удалось - метод возвращает null. Для типов СУБД DBF и ODBC смена БД подключения на ходу
   * невозможна - будет возникать ИС SQLException(). Метод показывает таблицы всех типов.
   * @param dbName String БД, для которой необходимо вернуть список таблиц. Если указана - используется это имя, если не
   * указана, то используется имя БД из конфига соединения. Если же и в конфиге соединения не указано имя - возникнет
   * ошибка - непонятно для какой БД показать список таблиц.
   * @return ArrayList[TableModel] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<TableModel> getUserTablesList(String dbName)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.getUserTablesList(dbName, null);}

  /**
   * Метод возвращает список(ArrayList[TableModel]) таблиц данной БД. Если не удалось - метод возвращает null. Метод
   * показывает таблицы всех типов. Имя БД для показа таблиц берется из конфига соединения с СУБД, если его там нет -
   * возникает ошибка.
   * @return ArrayList[TableModel] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<TableModel> getUserTablesList() 
   throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.getUserTablesList(null);}

  /**
   * Метод возвращает простой список (ArrayList[String]) таблиц указанной БД (к которой подключились) или указанной в качестве
   * параметра БД. Если не удалось - метод возвращает null. Для типов СУБД DBF и ODBC смена БД подключения на ходу
   * невозможна - будет возникать ИС SQLException().
   * @param dbName String БД, для которой необходимо вернуть список таблиц. Если указана - используется это имя, если не
   * указана, то используется имя БД из конфига соединения. Если же и в конфиге соединения не указано имя - возникнет
   * ошибка - непонятно для какой БД показать список таблиц.
   * @param tableTypes TableType[] список типов таблиц, которые необходимо показать. Значения для списка - из
   * класса-перечисления DBConsts.TableType.
   * @param useSchemaPrefix boolean использовать (ИСТИНА) или нет (ЛОЖЬ) имя схемы как префикс для имени таблицы в списке.
   * @return ArrayList[String] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName, DBTableType[] tableTypes, boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {
    // Результат работы метода
    ArrayList<String> list = null;
    // "Расширенный" список таблиц
    ArrayList<TableModel> extendedList = this.getUserTablesList(dbName, tableTypes);
    // Если полученный "расширенный" список таблиц не пуст - работаем
    if ((extendedList != null) && (!extendedList.isEmpty()))
     {
      list = new ArrayList<String>();
      // Проходим по расширенному списку и формируем простой список
      for (TableModel table : extendedList)
       {
        // Если используем имя схемы как префикс к имени таблицы имя имя схемы не пусто - добавляем
        if (useSchemaPrefix && !StringUtils.isBlank(table.getTableSchema()))
         {list.add(table.getTableSchema() + "." + table.getTableName());}
        // Если же имя схемы не используем или оно пусто - просто добавляем имя таблицы в список
        else
         {list.add(table.getTableName());}
       }
     }
    // Если же расширенный список пуст - просто сообщим в лог
    else {}
    return list;
   }

  /**
   * Метод возвращает простой список (ArrayList[String]) таблиц указанной БД (к которой подключились) или указанной в
   * качестве параметра БД. Если не удалось - метод возвращает null. Для типов СУБД DBF и ODBC смена БД подключения на ходу
   * невозможна - будет возникать ИС SQLException(). Метод показывает таблицы всех типов.
   * @param dbName String БД, для которой необходимо вернуть список таблиц. Если указана - используется это имя, если не
   * указана, то используется имя БД из конфига соединения. Если же и в конфиге соединения не указано имя - возникнет
   * ошибка - непонятно для какой БД показать список таблиц.
   * @param useSchemaPrefix boolean использовать (ИСТИНА) или нет (ЛОЖЬ) имя схемы как префикс для имени таблицы в списке.
   * @return ArrayList[String] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName, boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(dbName, null, useSchemaPrefix);}

  /**
   * Метод возвращает простой список (ArrayList[String]) таблиц указанной БД (к которой подключились) или указанной в качестве
   * параметра БД. Если не удалось - метод возвращает null. Для типов СУБД DBF и ODBC смена БД подключения на ходу
   * невозможна - будет возникать ИС SQLException().Метод показывает таблицы всех типов.
   * По умолчанию имя схемы таблицы как префикс имени таблицы в списке не используется.
   * @param dbName String БД, для которой необходимо вернуть список таблиц. Если указана - используется это имя, если не
   * указана, то используется имя БД из конфига соединения. Если же и в конфиге соединения не указано имя - возникнет
   * ошибка - непонятно для какой БД показать список таблиц.
   * @return ArrayList[String] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName)
     throws DBConnectionException, DBModuleConfigException, SQLException
     {return this.getUserTablesPlainList(dbName, null, false);}

  /**
   * Метод возвращает простой список (ArrayList[String]) таблиц указанной БД (к которой подключились). Если не удалось -
   * метод возвращает null. Метод показывает таблицы всех типов. Имя БД для показа таблиц берется из конфига соединения с
   * СУБД, если его там нет - возникает ошибка.
   * @param useSchemaPrefix boolean использовать (ИСТИНА) или нет (ЛОЖЬ) имя схемы как префикс для имени таблицы в списке.
   * @return ArrayList[String] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getUserTablesPlainList(boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(null, null, useSchemaPrefix);}

  /**
   * Метод возвращает простой список (ArrayList[String]) таблиц указанной БД. Если не удалось - метод возвращает null.
   * Метод показывает таблицы всех типов. Имя БД для показа таблиц берется из конфига соединения с СУБД, если его там нет -
   * возникает ошибка. По умолчанию имя схемы таблицы как префикс имени таблицы в списке не используется.
   * @return ArrayList[String] список таблиц данной БД или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getUserTablesPlainList()
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(null, null, false);}

  /**
   * Возвращает список баз данных сервера СУБД, к которому мы подключены. Наименования баз данных заносятся в
   * список в верхнем регистре - для обеспечения единообразия хранения и избежания проблем из-за разницы в
   * написании - строчные/прописные буквы. Если список баз данных получить не удалось, то метод возвращает значение null.
   * @return ArrayList[String] список баз данных текущего сервера или значение null.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getDBSList() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getDBSList(): creating list of DBs for current server.");

    // Если конфигурация модуля ошибочна - возбуждаем ИС!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // Результирующий список баз данных текущего сервера
    ArrayList<String> list = null;

    // todo: проверить получение списка БД при отсутствии прав
    
    // Пытаемся получить список баз данных текущего сервера только если это Informix, MySQL или MSSQL. Для Dbf и ODBC
    // список баз получать нет смысла - т.к. БД этого типа это каталоги.
    if (
        this.getConfig().getDbType().equals(DBType.INFORMIX)     ||
        this.getConfig().getDbType().equals(DBType.MYSQL)        ||
        this.getConfig().getDbType().equals(DBType.MSSQL_JTDS)   ||
        this.getConfig().getDbType().equals(DBType.MSSQL_NATIVE)
       )
     {
      logger.debug("DBType is INFORMIX or MYSQL. Processing DBs list.");
      Connection        connection = null;
      ResultSet         basesRS    = null;
      DatabaseMetaData  metaData;
      try
       {
        // Соединение с СУБД и получение метаданных о БД
        connection = DBUtils.getDBConn(getConfig());
        metaData   = connection.getMetaData();
        // Перебор всех баз данных указанного сервера
        basesRS = metaData.getCatalogs();
        if (basesRS.next())
         {
          logger.debug("Databases list of current server is not empty! Processing.");
          // В цикле проходим по всему курсору и формируем список баз данных
          do
           {
            String baseName = basesRS.getString(DBConsts.META_DATA_TABLE_CAT);
            if (!StringUtils.isBlank(baseName))
             {
              // Если список еще не инициализирован - инициализация
              if (list == null) {list = new ArrayList<String>();}
              // Добавляем непустое имя БД в список
              list.add(baseName.toUpperCase());
             }
           }
          while (basesRS.next());
         }
        else {logger.warn("Cannot create list of bases for current DBMS server [meta data is empty]! ");}
       }
      // Закроем соединение с СУБД и окрытые курсоры
      finally {if (basesRS != null) {basesRS.close();} if (connection != null) {connection.close();}}
     }
    // Если тип СУБД Derby или Dbf - возвращаем пустой список 
    else {logger.warn("DBMS type is Derby or Dbf - list of databases is empty!");}

    return list;
   }

  /**
   * Метод проверяет существование указанной БД для соединения, указанного с помощью конфига данного модуля.
   * @param dbName String имя проверяемой на существование БД.
   * @return boolean ИСТИНА/ЛОЖЬ - существует или нет указанная БД в текущей СУБД.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД. 
  */
  public boolean isDBExists(String dbName) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("isDBExists: checking existence [" + dbName + "] database.");
    boolean result = false;
    // Работаем, только если переданный параметр не пуст
    if (!StringUtils.isBlank(dbName))
     {
      logger.debug("DB name is not empty. Processing.");
      // Если СУБД - Informix или MySQL - получаем список баз данных указанного сервера (если он доступен и у
      // нас есть права), а если тип СУБД - Derby или Dbf - проверяем существование соответствующего каталога на диске.
      if (
          this.getConfig().getDbType().equals(DBType.INFORMIX)     ||
          this.getConfig().getDbType().equals(DBType.MYSQL)        ||
          this.getConfig().getDbType().equals(DBType.MSSQL_JTDS)   ||
          this.getConfig().getDbType().equals(DBType.MSSQL_NATIVE)
         )
       {
        logger.debug("DBType is INFORMIX or MYSQL.");
        ArrayList<String> dbsList = this.getDBSList();
        // Если список не пуст - проверяем, существует ли такая БД
        if ((dbsList != null) && (!dbsList.isEmpty()) && (dbsList.contains(dbName.toUpperCase()))) {result = true;}
        else {logger.warn("Databases list is empty or not contains DB [" + dbName + "].");}
       }
      // Проверка существования БД для Derby и Dbf - проверка существования каталога на диске. Каталог на диске
      // может быть и пустым - это значит, что в БД просто нет таблиц.
      else
       {
        logger.debug("DBMS type is Derby or Dbf.");
        if (new File(dbName).exists() && new File(dbName).isDirectory()) {result = true;}
        else {logger.warn("Catalog [" + dbName + "] doesn't exists.");}
       }
     }
    else {logger.warn("DB name is EMPTY! Can't check existence.");}
    return result;
   }

  /**
   * Метод возвращает простой список(ArrayList<String>) таблиц данной БД. Если не удалось - метод возвращает null.
   * В зависимости от параметра ignoreConstraints в результирующий список таблиц попадут/не попадут таблицы, из
   * списков-ограничений: системный каталог СУБД Информикс (только при подключении к нему), "разрешенные",
   * "запрещенные". при значение параметра ИСТИНА - все ограничения игнорируются, при значении ЛОЖЬ - все ограничения
   * действуют.
   * @param ignoreConstraints игнорировать ли ограничения(списки "разрешенных"/"запрещенных" таблиц, сис. каталог
   * Информикса) текущего модуля Spider для построения списка таблиц текущей БД. Если игнорируем ограничения (параметр
   * ignoreConstraints = true), то в список таблиц попадают ВСЕ таблицы текущей БД.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @return ArrayList<String> список таблиц данной БД или значение null.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public ArrayList<String> getTablesList(boolean ignoreConstraints) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getTablesList(): creating list of tables for DB[" + this.getConfig().getDbName() + "].");
    // Если конфигурация модуля пуста - ошибка!
    if (!StringUtils.isBlank(DBUtils.getConfigErrors(this.getConfig())))
     {throw new DBModuleConfigException("DBMS connect config is empty!");}
    // Если в конфигурации не указано имя БД - ошибка!
    if (StringUtils.isBlank(this.getConfig().getDbName())) {throw new DBModuleConfigException("Name of DB is empty!");}
    // Результирующий список таблиц
    ArrayList<String> list       = null;
    Connection        connection = null;
    ResultSet         tablesRS   = null;
    DatabaseMetaData  metaData;
    try
     {
      // Соединение с СУБД и получение метаданных о БД
      connection = DBUtils.getDBConn(getConfig());
      metaData   = connection.getMetaData();
      // Перебор всех таблиц указанной БД и добавление их в список. Для СУБД Дерби список таблиц получается
      // немного по-другому...
      tablesRS = metaData.getTables(null, null, "", null);
      // Если список таблиц получен - пройдемся по нему
      if (tablesRS.next())
       {
        logger.debug("Tables list received. Processing.");
        // Проход цикла по списку таблиц
        do
         {
          String tableName = tablesRS.getString(DBConsts.META_DATA_TABLE_NAME);
          // Обработку выполняем если полученное имя таблицы не пусто
          if (!StringUtils.isBlank(tableName))
           {
            // Если не игнорируем ограничения (системный каталог СУБД, списки "разрешенных" и "запрещенных" таблиц),
            // то мы должны их обработать (проверить, что текущая таблица нам подходит)
            if (!ignoreConstraints && !getConfig().isTableAllowed(tableName))
             {logger.debug("This table [" + tableName + "] is in DBMS system catalog, deprecated or not allowed - skipping.");}
            // Если игнорируем ограничения или таблица нам подошла при включенных ограничениях - работаем
            else
             {
              logger.debug("Table name OK. Processing table [" + tableName + "].");
              // Список инициализируется только в том случае, если найдена хоть одна таблица
              if (list == null) {list = new ArrayList<String>();}
              // Имя таблицы добавляется в список всегда в верхнем регистре символов
              list.add(tableName.toUpperCase());
             }
           }
          else {logger.warn("Empty table name!");}
         }
        while (tablesRS.next());
       }
      // Если же список таблиц пуст - сообщим об этом в лог
      else {logger.warn("Received tables list is empty!");}
     }
    // Закроем соединение с СУБД и окрытые курсоры
    finally {if (tablesRS != null) {tablesRS.close();} if (connection != null) {connection.close();}}
    // Возвращаем результат
    return list;
   }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ, в зависимости от того, существует ли в текущей БД таблица tableName.
   * Параметр ignoreConstraints указывает, учитываются ли при этом списки ограничений для данного модуля - списки
   * "запрещенных" и "разрешенных" таблиц (true -> ограничения не учитываются, false -> ограничения учитываются).
   * Если ограничения учитываются, то при существовании таблицы в БД и при ее присутствии в списке "запрещенных"
   * метод вернет значение ЛОЖЬ, также как и при существовании таблицы в БД и при ее отсутствии в списке "разрешенных".
   * Если переданное имя таблицы пусто ("" или null), то метод возвращает значение ЛОЖЬ.
   * @param tableName String имя искомой таблицы.
   * @param ignoreConstraints boolean игнорировать(true) или учитывать(false) ограничения данного модуля.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от существования таблицы в текущей БД.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public boolean isTableExists(String tableName, boolean ignoreConstraints)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("isTablesExists(): checking existence of table [" + tableName + "].");

    // Если конфигурация модуля ошибочна - возбуждаем ИС!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // Возвращаемый результат
    boolean result = false;
    // Работаем только если имя таблицы не пусто
    if (!StringUtils.isBlank(tableName))
     {
      // Получаем список таблиц текущей БД
      ArrayList<String> tablesList = this.getTablesList(ignoreConstraints);
      // Если полученный список таблиц не пуст - проходим по нему
      if ((tablesList != null) && (!tablesList.isEmpty()))
       {
        // Проходим по списку таблиц и ищем нашу
        for (String table : tablesList) {if (table.equals(tableName.toUpperCase())) {result = true;}}
       }
      // Если же полученный список таблиц пуст - сообщим в лог
      else {logger.warn("Tables list is empty!");}
     }
    else {logger.warn("Empty table name!");}
    return result;
   }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ, в зависимости от того, существует ли в текущей БД таблица tableName.
   * Данный метод НЕ учитывает ограничения текущего модуля DBSpider - списки "запрещенных" и "разрешенных" таблиц,
   * системный каталог Информикса. Если переданное имя таблицы пусто ("" или null), то метод возвращает значение ЛОЖЬ.
   * @param tableName String имя искомой таблицы.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от существования таблицы в текущей БД.
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  public boolean isAbsTableExists(String tableName) throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.isTableExists(tableName, true);}

  /***/
  public void test()
   {
    try
     {
      Connection connection = DBUtils.getDBConn(getConfig());
      DatabaseMetaData metaData   = connection.getMetaData();
      // Инфа об индексах
      ResultSet rs = metaData.getIndexInfo(null, null, "check_2_action", false, false);
      System.out.println("indexes: \n" + DBUtils.getStringResultSet(rs));
      ResultSet rs2 = metaData.getBestRowIdentifier(null, null, "check_2_action", 0, true);
      System.out.println("row identifiers: \n" + DBUtils.getStringResultSet(rs2));
      ResultSet rs3 = metaData.getExportedKeys(null, null, "check_2_action");
      System.out.println("exported key: \n" + DBUtils.getStringResultSet(rs3));
      ResultSet rs4 = metaData.getImportedKeys(null, null, "check_2_action");
      System.out.println("imported key: \n" + DBUtils.getStringResultSet(rs4));
      ResultSet rs5 = metaData.getPrimaryKeys(null, null, "check_2_action");
      System.out.println("primary key: \n" + DBUtils.getStringResultSet(rs5));
      ResultSet rs6 = metaData.getVersionColumns(null, null, "check_2_action");
      System.out.println("version columns: \n" + DBUtils.getStringResultSet(rs6));
     }
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}

   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args) throws org.apache.commons.configuration2.ex.ConfigurationException {
    Logger logger = Logger.getLogger(DBSpider.class.getName());

//    ConnectionConfig mysqlConfig = new ConnectionConfig();
//    mysqlConfig.setDbConnectionType("direct");
//    mysqlConfig.setDbType("mysql");
//    mysqlConfig.setDbHost("appserver:3306");
//    mysqlConfig.setDbName("storm_test");
//    mysqlConfig.setDbUser("root");
//    mysqlConfig.setDbPassword("mysql");

//    ConnectionConfig mysqlClientConfig = new ConnectionConfig();
//    mysqlClientConfig.setDbConnectionType("direct");
//    mysqlClientConfig.setDbType("mysql");
//    mysqlClientConfig.setDbHost("appserver:3306");
//    mysqlClientConfig.setDbName("storm_client");
//    mysqlClientConfig.setDbUser("root");
//    mysqlClientConfig.setDbPassword("mysql");

//    ConnectionConfig ifxConfig = new ConnectionConfig();
//    ifxConfig.setDbConnectionType("direct");
//    ifxConfig.setDbType("informix");
//    ifxConfig.setDbHost("appserver:1526");
//    ifxConfig.setDbServerName("hercules");
//    ifxConfig.setDbName("storm_test");
//    ifxConfig.setDbUser("informix");
//    ifxConfig.setDbPassword("ifx_dba_019");
    
//    ConnectionConfig dbfConfig = new ConnectionConfig();
//    dbfConfig.setDbConnectionType("direct");
//    dbfConfig.setDbType("dbf");
//    dbfConfig.setDbName("q:/new/fleet");

    try
     {
      DBConfig derbyConfig = new DBConfig();
      derbyConfig.loadFromFile("derbyConfig.xml", null, true);

      DBConfig ifxConfig = new DBConfig();
      ifxConfig.loadFromFile("ifxNormDocsConfig.xml", null, true);
      
      DBSpider spider = new DBSpider(ifxConfig);
      //logger.debug("DBs list -> " + spider.getDBSList());
      //logger.debug("Tables list -> " + spider.getTablesList());

     }
    catch (IOException e) {logger.error(e.getMessage());}
    //catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    //catch (DBConnectionException e) {logger.error(e.getMessage());}

   }

 }