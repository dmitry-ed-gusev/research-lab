package jdb.nextGen;

import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.nextGen.exceptions.JdbException;
import jdb.nextGen.models.SimpleDBIntegrityModel;
import jdb.nextGen.models.SimpleDBTimedModel;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 6.0 (DATE: 19.05.11)
*/

public final class DBasesLoader
 {
  /***/
  private static Logger logger = Logger.getLogger(DBasesLoader.class.getName());

  // Предотвращаем инстанцирование и наследование
  public DBasesLoader() {}

  /**
   *
   * @param conn
   * @param path String путь к каталогу для выгрузки БД. Ограничения на значение: если каталог сущесвует, он должен быть
   * именно каталогом (не файлом) и должен быть пуст, в противном случае возникнет ИС; если каталог не существует, будет
   * предпринята попытка его создать и, в случае неудачи, возникнет ИС.
  */
  public static void unloadDB(Connection conn, String path, String dbName, ArrayList<String> tablesList,
   SimpleDBTimedModel timedModel, SimpleDBIntegrityModel integrityModel) throws JdbException
   {
    logger.debug("DBasesLoader.unloadDB().");
    // Проверка соединения
    if (conn != null)
     {
      // Проверка указанного пути для выгрузки (должен быть не пуст!)
      if (!StringUtils.isBlank(path))
       {
        // Проверка указанного имени БД (должно быть не пустым)
        if (!StringUtils.isBlank(dbName))
         {
          // Проверяем список таблиц - он должен быть не пуст
          if ((tablesList != null) && (!tablesList.isEmpty()))
           {
            boolean isOutputCatalogExists = false;
            // Обработка каталога назначения - если он существует, надо проверить его пустоту
            File output = new File(path);
            if (output.exists())
             {
              if (!output.isDirectory()) {throw new JdbException("Path [" + path + "] is not a directory!");}
              else if (!FSUtils.isEmptyDir(path)) {throw new JdbException("Catalog [" + path + "] is not empty!");}
              isOutputCatalogExists = true;
             }
            // Каталог назначения не существует - пробуем создать
            else
             {if (!output.mkdirs()) {throw new JdbException("Can't create catalog [" + path + "]!");}}
            // Если во время обработки каталога не возникла ИС - выгружаем данные
            try
             {
              boolean unloadDBResult = DBasesLoaderCore.unloadDBToDisk(conn, FSUtils.fixFPath(path, true), dbName,
                                        tablesList, timedModel, integrityModel);
              // Если ничего выгружено не было - просто удалим созданный каталог для выгрузки
              if (!unloadDBResult)
               {
                // Если каталог существовал на момент выгрузки - его не удаляем, а очищаем,
                // если же каталог не существовал (мы его создали), то удаляем его.
                if (isOutputCatalogExists) {FSUtils.clearDir(FSUtils.fixFPath(path, true) + dbName);}
                else                       {FSUtils.delTree(FSUtils.fixFPath(path, true) + dbName);}
               }
             }
            // В данном блоке CATCH не происходит обработка возникшей ИС, блок необходим только для
            catch (JdbException e)
             {
              // При возникновении фатальной ИС во время выгрузки БД необходимо удалить созданный для выгрузки
              // БД каталог - провести очистку ресурсов. Если каталог существовал на момент выгрузки - его не
              // удаляем, а очищаем, если же каталог не существовал (мы его создали), то удаляем его.
              if (isOutputCatalogExists) {FSUtils.clearDir(FSUtils.fixFPath(path, true) + dbName);}
              else                       {FSUtils.delTree(FSUtils.fixFPath(path, true) + dbName);}
              // ИС в данном блоке не обрабатывается - обработка передается выше
              throw new JdbException(e);
             }
           }
          // Пустой список таблиц для выгрузки
          else {throw new JdbException("Empty tables list!");}
         }
        // Пустое имя БД
        else {throw new JdbException("Empty DB name!");}
       }
      // Пустой путь для выгрузки
      else {throw new JdbException("Path for unloading DB is empty!");}
     }
    // Пустое соединение с СУБД
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /***/
  public static void unloadTable(Connection conn, String path, String dbName, String tableName,
   Timestamp timestamp, ArrayList<Integer> keysList) throws JdbException
   {
    // Готовим данные для выгрузки (используем метод для выгрузки всей БД)
    ArrayList<String>  tablesList = new ArrayList<String>(Arrays.asList(tableName));
    // Если есть не пустой таймштамп - добавим его в выгрузку
    SimpleDBTimedModel timedModel = null;
    if (timestamp != null)
     {
      timedModel = new SimpleDBTimedModel(dbName);
      timedModel.addTable(tableName, timestamp);
     }
    // Если список ключей не пуст - добавим его в выгрузку
    SimpleDBIntegrityModel integrityModel = null;
    if ((keysList != null) && (!keysList.isEmpty()))
     {
      integrityModel = new SimpleDBIntegrityModel(dbName);
      integrityModel.addTable(tableName, keysList);
    }
    DBasesLoader.unloadDB(conn, path, dbName, tablesList, timedModel, integrityModel);
   }

  /***/
  public static void loadDB(Connection conn, String path, ArrayList<String> tablesList, DBProcessingMonitor monitor,
   boolean useIdentityInsert)
   throws JdbException
   {
    logger.debug("DBasesLoader.loadDB().");
    // Проверка соединения с СУБД
    if (conn != null)
     {
      // Проверка каталога, из которого загружаем БД (должен существовать, быть каталогом и быть непустым)
      if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory() && !FSUtils.isEmptyDir(path))
       {
        // Проверяем список таблиц (должен быть не пуст)
        if ((tablesList != null) && (!tablesList.isEmpty()))
         {
          // Непосредственно загрузка данных с диска в БД. Все ошибки загрузки (возникшие ИС) оборачиваются в нашу
          // собственную ИС (JdbException). Это дает нам возможность менять внутреннюю структуру модулей как угодно -
          // для внешней части этой системы (загрузки/выгрузки БД) останется только одна ИС - JdbException
          try {DBasesLoaderCore.loadDBFromDisk(conn, path, tablesList, monitor, useIdentityInsert);}
          // Все перехватываемые ИС оборачивабтся в нашу ИС - JdbException
          catch (ClassNotFoundException e) {throw new JdbException(e);}
          catch (SQLException e)           {throw new JdbException(e);}
          catch (IOException e)            {throw new JdbException(e);}
         }
        // Пустой список таблиц для выгрузки данных
        else {throw new JdbException("Empty tables list [" + tablesList + "]!");}
       }
      // Неверный путь к файлам таблицы
      else {throw new JdbException("Path [" + path + "] is empty, or not exists, or not a directory, or is empty directory!");}
     }
    // Соединение с СУБД пусто
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /***/
  public static void loadDB(Connection conn, String path, ArrayList<String> tablesList, DBProcessingMonitor monitor)
   throws JdbException {DBasesLoader.loadDB(conn, path, tablesList, monitor, false);}

  /***/
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");

    try
     {
      //DBConfig config               = new DBConfig("jdb_java_module/dbConfigs/ifxNormDocsConfig.xml");
      //DBConfig mssqlConfig          = new DBConfig("jdb_java_module/dbConfigs/mssqlAppSupidConfig.xml");
      DBConfig mssqlStormTestConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlAppStormConfig.xml");
      DBConfig mssqlFleetTestConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlAppFleetConfig.xml");
      DBConfig ifxStormConfig       = new DBConfig("jdb_java_module/dbConfigs/ifxStormConfig.xml");
      DBConfig ifxFleetConfig       = new DBConfig("jdb_java_module/dbConfigs/ifxFleetConfig.xml");
      //DBConfig mysqlStormConfig     = new DBConfig("jdb_java_module/dbConfigs/mysqlStormConfig.xml");
      //DBConfig mysqlStormTestConfig = new DBConfig("jdb_java_module/dbConfigs/mysqlStormTestConfig.xml");

      //Connection conn      = DBUtils.getDBConn(config);
      //Connection mssqlConn = DBUtils.getDBConn(mssqlConfig);
      //Connection mysqlStormConn = DBUtils.getDBConn(mysqlStormConfig);
      //Connection mysqlStormTestConn = DBUtils.getDBConn(mysqlStormTestConfig);

      Connection mssqlStormTestConn = DBUtils.getDBConn(mssqlStormTestConfig);
      Connection mssqlFleetTestConn = DBUtils.getDBConn(mssqlFleetTestConfig);
      Connection ifxStormConn       = DBUtils.getDBConn(ifxStormConfig);
      Connection ifxFleetConn       = DBUtils.getDBConn(ifxFleetConfig);

      // Таблицы системы Шторм - для обновления
      ArrayList<String> STORM_SYNC_TABLES_LIST = new ArrayList<String>(Arrays.asList
       (
        "SURVEY_ASPECT", "SURVEY_OCCASION", "KEEL_LAYING", "SHIP_LENGTH", "SHIP_DELIVERY", "SHIP_BUILD", "SHIP_DWT",
        "SHIP_LENGTH_MK", "SHIP_AGE", "SURVEY_ACTION", "SHIP_SIZE", "SHIP_TYPE", "SURVEY_TYPE", "SHIP_MISC", "RULESET",
        "STRAN", "SHIP_TYPE_2_RULESET", "SHIP_TYPE_2_FLEET", "SPECIALIZATION", "ITEMS", "ITEM_2_RULESET", "MISC_2_RULESET",
        "FLEET_2_MISC", "TOPIC", "TOPIC_PARENTS", "VERSION_CLIENT", "VERSION_MODULES", "VERSION_CLIENT_CONTENT"
       ));
      // Системные таблицы Шторма
      ArrayList<String> STORM_SPEC_TABLES = new ArrayList<String>(Arrays.asList
      (
       "survey", "survey_2_type", "survey_2_ship_misc", "survey_2_ship_type", "survey_2_ship_data",
       "survey_upload", "sys_sql_table"));
      // Все таблицы Шторма вместе
      ArrayList<String> ALL_TABLES = new ArrayList<String>();
      ALL_TABLES.addAll(STORM_SYNC_TABLES_LIST);
      ALL_TABLES.addAll(STORM_SPEC_TABLES);

      // Таблицы БД FLEET
      ArrayList<String> FLEET_TABLES = new ArrayList<String>(Arrays.asList
       ("Dvig", "Dvizh", "Gorod", "Insp", "Klcold", "Sost", "Statgr", "Stip", "Stran", "tip"));

      // Выгрузка БД Шторм из Информикса и загрузка в MSSQL
      FSUtils.delTree("c:\\temp\\storm");
      //FSUtils.delTree("c:\\temp\\fleet");
      // new ArrayList<String>(Arrays.asList("survey"))
      // Выгрузка/загрузка БД Шторм
      DBasesLoader.unloadDB(ifxStormConn, "c:\\temp\\storm", "storm", new ArrayList<String>(Arrays.asList("check_list1")), null, null);
      DBasesLoader.loadDB(mssqlStormTestConn, "c:\\temp\\storm", new ArrayList<String>(Arrays.asList("check_list1")), null);
      // Выгрузка/загрузка БД Флот
      //DBasesLoader.unloadDB(ifxFleetConn, "c:\\temp\\fleet", "fleet", FLEET_TABLES, null, null);
      //DBasesLoader.loadDB(mssqlFleetTestConn, "c:\\temp\\fleet", FLEET_TABLES);
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    catch (JdbException e)            {logger.error(e.getMessage());}
   }

 }