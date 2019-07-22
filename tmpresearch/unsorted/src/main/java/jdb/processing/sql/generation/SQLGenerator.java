package jdb.processing.sql.generation;

import jdb.DBConsts.DBType;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Данный класс реализует движок генерации sql-запросов для различных типов СУБД.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 26.01.2009)
*/

public class SQLGenerator
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(SQLGenerator.class.getName());

  /**
   * Метод создает SQL-запрос для создания таблицы по указанному экземпляру класса TableStructureModel. Если
   * указанный экземпляр пуст, то метод возвращает значение null.
   * @param table TableStructureModel модель таблицы, по которой будет сгенерирован запрос.
   * @param targetDBType DBType тип СУБД, для которой готовится данный запрос. 
   * @param usePrimaryKey boolean данный параметр указывает - генерировать (true) или нет (false) опции для
   * создания первичного ключа. Если указана опция true - опция "первичный ключ" будет указана в sql-операторе
   * [CREATE TABLE...], если же указана опция false, то вместо "первичного ключа" будет создан уникальный индекс по
   * тем же полям, которые входят в первичный ключ.
   * @param addSemi boolean добавлять или нет разделитель в конец каждого sql-запроса (разделитель -> ;)
   * @return ArrayList[String] список созданных запросов.
  */
  public static ArrayList<String> getCreateTableSQL(TableStructureModel table, DBType targetDBType,
                                                    boolean usePrimaryKey, boolean addSemi)
   {
    ArrayList<String> sql = null;
    if (table != null)
     {
      // Генерация sql-запроса для создания ТОЛЬКО таблицы
      String tableFieldsQuery = SQLUtils.getCreateTableFieldsSQL(table, targetDBType, usePrimaryKey, addSemi);
      // Если запрос для создания таблицы не пуст - работаем - добавляем его в список и
      // генерируем запросы для индексов (если пуст запрос для таблицы - нет смысла создавать индексы).
      if ((tableFieldsQuery != null) && (!tableFieldsQuery.trim().equals("")))
       {
        sql = new ArrayList<String>();
        sql.add(tableFieldsQuery);
        // Генерация sql-запроса для создания ТОЛЬКО индексов данной таблицы
        ArrayList<String> indexesQueries = SQLUtils.getCreateTableIndexesSQL(table, targetDBType, usePrimaryKey, addSemi);
        // Индексы также пытаемся добавить в результат если их список не пуст!
        if ((indexesQueries != null) && (!indexesQueries.isEmpty()))
         {for (String query : indexesQueries) {sql.add(query);}}
        else {logger.warn("Indexes query for table [" + table.getTableName() + "] is empty!");}
       }
      // Если запрос для создания таблицы пуст - сообщение в лог
      else {logger.warn("Query [CREATE TABLE...] is empty for table [" + table.getTableName() + "]!");} 
     }
    return sql;
   }

  /**
   * Метод создает sql-запрос для создания БД по модели DBStructureModel. Если переданная в качестве параметра
   * модель пуста - метод возвращает заначение null.
   * @param dbName String имя БД, для которой генерируем запрос на создание. Данный параметр используется, только
   * если происходит генерация запроса для непосредственного создания БД (CREATE DATABASE...). Этот параметр
   * дает возможность указать другое имя для создаваемой БД - отличное от имени в полученной модели БД - db. Если
   * параметр пуст - используется имя БД из указанной модели.
   * @param db DBStructureModel объект-образец для генерации sql-запроса создания БД.
   * @param targetDBType DBType тип СУБД, для которой готовится данный запрос. Если данный параметр не указан
   * (равен null), то тип СУБД, для которой готовится запрос берется из указанной модели БД (DBStructureModel). 
   * @param usePrimaryKey boolean данный параметр указывает - генерировать (true) или нет (false) опции для
   * создания первичного ключа. Если указана опция true - опция "первичный ключ" будет указана в sql-операторе
   * [CREATE TABLE...], если же указана опция false, то вместо "первичного ключа" будет создан уникальный индекс по
   * тем же полям, которые входят в первичный ключ.
   * @param createDB boolean создавать (true) или нет (false) запрос для создания самой БД (CREATE DATABASE...).
   * @param addSemi boolean добавлять или нет разделитель в конец каждого sql-запроса (разделитель -> ;)
   * @return String сгенерированный sql-запрос для создания БД. 
  */
  public static ArrayList<String> getCreateDBSQL(String dbName, DBStructureModel db, DBType targetDBType,
                                                 boolean usePrimaryKey, boolean createDB, boolean addSemi)
   {
    ArrayList<String> sql = null;
    // Если БД не пуста - работаем
    if (db != null)
     {
      // Выбираем используемое для создания БД имя
      String localDBName;
      // Если указано другое имя БД - используем его для создания, если же нет - используем имя из модели БД
      if ((dbName != null) && (!dbName.trim().equals(""))) {localDBName = dbName;} else {localDBName = db.getDbName();}

      // Инициализируем результат
      sql = new ArrayList<String>();
      
      // Если указан соотв. ключик - добавляем запрос на создание именно БД
      if (createDB) {sql.add("CREATE DATABASE " + localDBName + ";\n");}

      // Для выполнения скрипта необходимо переключиться на БД назначения (добавляем команду использования нужной БД).
      if (targetDBType != null)
       {
        switch (targetDBType)
         {
          case MYSQL:        sql.add("USE " + localDBName + ";\n"); break;
          case INFORMIX:     sql.add("DATABASE " + localDBName + ";\n"); break;
          case ODBC:         logger.warn("CAN'T ADD [USE DATABASE] COMMAND! UNKNOWN SYNTAX!"); break;
          case DBF:          logger.warn("CAN'T ADD [USE DATABASE] COMMAND! UNKNOWN SYNTAX!"); break;
          default:           logger.warn("UNKNOWN DBTYPE!"); break;
         }
       }
      // Если тип СУБД не указан - значение null, то команда не добавляется.
      else {logger.warn("Target DBMS type is NULL!");}

      // Если список таблиц не пуст - проходим по нему и генерируем запросы для каждой таблицы
      TreeSet<TableStructureModel> tables = db.getTables();
      if ((tables != null) && (!tables.isEmpty()))
       {
        // Выбираем тип СУБД назначения
        DBType targetDB;
        if (targetDBType != null) {targetDB = targetDBType;} else {targetDB = db.getDbType();}
        for (TableStructureModel table : tables)
         {
          // Получаем список сгенерированных запросов для создания текущей таблицы
          ArrayList<String> tableQueries = SQLGenerator.getCreateTableSQL(table, targetDB, usePrimaryKey, addSemi);
          // Если список не пуст - добавляем его к результату
          if ((tableQueries != null) && (!tableQueries.isEmpty()))
           {for (String query : tableQueries) {sql.add(query);}}
         }
       }
     }
    return sql;
   }

  /**
   * Метод генерирует полный sql-запрос для модификации таблицы current и приведению ее к образцу - к
   * таблице foreign.
   * @param current TableStructureModel текущая таблица, которую нужно привести к требуемому виду.
   * @param currentDBType DBType тип СУБД, в которой находится текущая таблица current.
   * @param foreign TableStructureModel таблица образец, для приведения таблицы current в нужный вид.
   * @return String сгенерированный sql-запрос.
  */
  public static String getAlterTableSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    // Результирующий набор запросов
    StringBuilder sql = null;
    // Получаем SQL-запрос для сброса полей из таблицы current
    String dropFieldsSQL   = SQLUtils.getDropFieldsSQL(current, foreign);
    // Получаем SQL-запрос для добавления полей в таблицу current
    String addFieldsSQL    = SQLUtils.getAddFieldsSQL(current, currentDBType, foreign);
    // Получаем SQL-запрос для изменения полей в таблице current
    String changeFieldsSQL = SQLUtils.getChangeFieldsSQL(current, currentDBType, foreign);
    // Если запросы не пусты - добавляем их в результирующий набор запросов
    if (dropFieldsSQL   != null) {sql = new StringBuilder(); sql.append(dropFieldsSQL);}
    if (addFieldsSQL    != null) {if (sql == null) sql = new StringBuilder(); sql.append(addFieldsSQL);}
    if (changeFieldsSQL != null) {if (sql == null) sql = new StringBuilder(); sql.append(changeFieldsSQL);}
    // Формируем результат
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * Метод только для тестирования данного класса.
   * @param args String[] аргументы метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    
    try
     {
      DBConfig ifxConfig = new DBConfig();
      ifxConfig.setDbType(DBType.INFORMIX);
      ifxConfig.setHost("appserver:1526");
      ifxConfig.setServerName("hercules");
      ifxConfig.setDbName("storm");
      ifxConfig.setUser("informix");
      ifxConfig.setPassword("ifx_dba_019");

      // Берем модель данных из Информикса
      DBEngineer ifxEngineer = new DBEngineer(ifxConfig);
      DBStructureModel ifxModel = ifxEngineer.getDBStructureModel();
      //ArrayList<String> createSQL = SQLGenerator.getCreateDBSQL(null, ifxModel, DBType.DERBY_EMBEDD, false, false, false);
      //logger.info("->\n" + createSQL);

      // Пытаемся создать аналогичную БД в Дерби
      DBConfig derbyConfig = new DBConfig();
      derbyConfig.loadFromFile("derbyConfig.xml");
      //DBEngineer derbyEngineer = new DBEngineer(derbyConfig);
      //SingleThreadSqlBatchExecutor.execute(derbyConfig, createSQL, true);
     }

    catch (SQLException e) {logger.error(e.getMessage());}
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }