package jdb.nextGen;

import jdb.config.DBConfig;
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
 * @author Gusev Dmitry (����� �������)
 * @version 6.0 (DATE: 19.05.11)
*/

public final class DBasesLoader
 {
  /***/
  private static Logger logger = Logger.getLogger(DBasesLoader.class.getName());

  // ������������� ��������������� � ������������
  public DBasesLoader() {}

  /**
   *
   * @param conn
   * @param path String ���� � �������� ��� �������� ��. ����������� �� ��������: ���� ������� ���������, �� ������ ����
   * ������ ��������� (�� ������) � ������ ���� ����, � ��������� ������ ��������� ��; ���� ������� �� ����������, �����
   * ����������� ������� ��� ������� �, � ������ �������, ��������� ��.
  */
  public static void unloadDB(Connection conn, String path, String dbName, ArrayList<String> tablesList,
   SimpleDBTimedModel timedModel, SimpleDBIntegrityModel integrityModel) throws JdbException
   {
    logger.debug("DBasesLoader.unloadDB().");
    // �������� ����������
    if (conn != null)
     {
      // �������� ���������� ���� ��� �������� (������ ���� �� ����!)
      if (!StringUtils.isBlank(path))
       {
        // �������� ���������� ����� �� (������ ���� �� ������)
        if (!StringUtils.isBlank(dbName))
         {
          // ��������� ������ ������ - �� ������ ���� �� ����
          if ((tablesList != null) && (!tablesList.isEmpty()))
           {
            boolean isOutputCatalogExists = false;
            // ��������� �������� ���������� - ���� �� ����������, ���� ��������� ��� �������
            File output = new File(path);
            if (output.exists())
             {
              if (!output.isDirectory()) {throw new JdbException("Path [" + path + "] is not a directory!");}
              else if (!FSUtils.isEmptyDir(path)) {throw new JdbException("Catalog [" + path + "] is not empty!");}
              isOutputCatalogExists = true;
             }
            // ������� ���������� �� ���������� - ������� �������
            else
             {if (!output.mkdirs()) {throw new JdbException("Can't create catalog [" + path + "]!");}}
            // ���� �� ����� ��������� �������� �� �������� �� - ��������� ������
            try
             {
              boolean unloadDBResult = DBasesLoaderCore.unloadDBToDisk(conn, FSUtils.fixFPath(path, true), dbName,
                                        tablesList, timedModel, integrityModel);
              // ���� ������ ��������� �� ���� - ������ ������ ��������� ������� ��� ��������
              if (!unloadDBResult)
               {
                // ���� ������� ����������� �� ������ �������� - ��� �� �������, � �������,
                // ���� �� ������� �� ����������� (�� ��� �������), �� ������� ���.
                if (isOutputCatalogExists) {FSUtils.clearDir(FSUtils.fixFPath(path, true) + dbName);}
                else                       {FSUtils.delTree(FSUtils.fixFPath(path, true) + dbName);}
               }
             }
            // � ������ ����� CATCH �� ���������� ��������� ��������� ��, ���� ��������� ������ ���
            catch (JdbException e)
             {
              // ��� ������������� ��������� �� �� ����� �������� �� ���������� ������� ��������� ��� ��������
              // �� ������� - �������� ������� ��������. ���� ������� ����������� �� ������ �������� - ��� ��
              // �������, � �������, ���� �� ������� �� ����������� (�� ��� �������), �� ������� ���.
              if (isOutputCatalogExists) {FSUtils.clearDir(FSUtils.fixFPath(path, true) + dbName);}
              else                       {FSUtils.delTree(FSUtils.fixFPath(path, true) + dbName);}
              // �� � ������ ����� �� �������������� - ��������� ���������� ����
              throw new JdbException(e);
             }
           }
          // ������ ������ ������ ��� ��������
          else {throw new JdbException("Empty tables list!");}
         }
        // ������ ��� ��
        else {throw new JdbException("Empty DB name!");}
       }
      // ������ ���� ��� ��������
      else {throw new JdbException("Path for unloading DB is empty!");}
     }
    // ������ ���������� � ����
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /***/
  public static void unloadTable(Connection conn, String path, String dbName, String tableName,
   Timestamp timestamp, ArrayList<Integer> keysList) throws JdbException
   {
    // ������� ������ ��� �������� (���������� ����� ��� �������� ���� ��)
    ArrayList<String>  tablesList = new ArrayList<String>(Arrays.asList(tableName));
    // ���� ���� �� ������ ��������� - ������� ��� � ��������
    SimpleDBTimedModel timedModel = null;
    if (timestamp != null)
     {
      timedModel = new SimpleDBTimedModel(dbName);
      timedModel.addTable(tableName, timestamp);
     }
    // ���� ������ ������ �� ���� - ������� ��� � ��������
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
    // �������� ���������� � ����
    if (conn != null)
     {
      // �������� ��������, �� �������� ��������� �� (������ ������������, ���� ��������� � ���� ��������)
      if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory() && !FSUtils.isEmptyDir(path))
       {
        // ��������� ������ ������ (������ ���� �� ����)
        if ((tablesList != null) && (!tablesList.isEmpty()))
         {
          // ��������������� �������� ������ � ����� � ��. ��� ������ �������� (��������� ��) ������������� � ����
          // ����������� �� (JdbException). ��� ���� ��� ����������� ������ ���������� ��������� ������� ��� ������ -
          // ��� ������� ����� ���� ������� (��������/�������� ��) ��������� ������ ���� �� - JdbException
          try {DBasesLoaderCore.loadDBFromDisk(conn, path, tablesList, monitor, useIdentityInsert);}
          // ��� ��������������� �� ������������� � ���� �� - JdbException
          catch (ClassNotFoundException e) {throw new JdbException(e);}
          catch (SQLException e)           {throw new JdbException(e);}
          catch (IOException e)            {throw new JdbException(e);}
         }
        // ������ ������ ������ ��� �������� ������
        else {throw new JdbException("Empty tables list [" + tablesList + "]!");}
       }
      // �������� ���� � ������ �������
      else {throw new JdbException("Path [" + path + "] is empty, or not exists, or not a directory, or is empty directory!");}
     }
    // ���������� � ���� �����
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

      // ������� ������� ����� - ��� ����������
      ArrayList<String> STORM_SYNC_TABLES_LIST = new ArrayList<String>(Arrays.asList
       (
        "SURVEY_ASPECT", "SURVEY_OCCASION", "KEEL_LAYING", "SHIP_LENGTH", "SHIP_DELIVERY", "SHIP_BUILD", "SHIP_DWT",
        "SHIP_LENGTH_MK", "SHIP_AGE", "SURVEY_ACTION", "SHIP_SIZE", "SHIP_TYPE", "SURVEY_TYPE", "SHIP_MISC", "RULESET",
        "STRAN", "SHIP_TYPE_2_RULESET", "SHIP_TYPE_2_FLEET", "SPECIALIZATION", "ITEMS", "ITEM_2_RULESET", "MISC_2_RULESET",
        "FLEET_2_MISC", "TOPIC", "TOPIC_PARENTS", "VERSION_CLIENT", "VERSION_MODULES", "VERSION_CLIENT_CONTENT"
       ));
      // ��������� ������� ������
      ArrayList<String> STORM_SPEC_TABLES = new ArrayList<String>(Arrays.asList
      (
       "survey", "survey_2_type", "survey_2_ship_misc", "survey_2_ship_type", "survey_2_ship_data",
       "survey_upload", "sys_sql_table"));
      // ��� ������� ������ ������
      ArrayList<String> ALL_TABLES = new ArrayList<String>();
      ALL_TABLES.addAll(STORM_SYNC_TABLES_LIST);
      ALL_TABLES.addAll(STORM_SPEC_TABLES);

      // ������� �� FLEET
      ArrayList<String> FLEET_TABLES = new ArrayList<String>(Arrays.asList
       ("Dvig", "Dvizh", "Gorod", "Insp", "Klcold", "Sost", "Statgr", "Stip", "Stran", "tip"));

      // �������� �� ����� �� ���������� � �������� � MSSQL
      FSUtils.delTree("c:\\temp\\storm");
      //FSUtils.delTree("c:\\temp\\fleet");
      // new ArrayList<String>(Arrays.asList("survey"))
      // ��������/�������� �� �����
      DBasesLoader.unloadDB(ifxStormConn, "c:\\temp\\storm", "storm", new ArrayList<String>(Arrays.asList("check_list1")), null, null);
      DBasesLoader.loadDB(mssqlStormTestConn, "c:\\temp\\storm", new ArrayList<String>(Arrays.asList("check_list1")), null);
      // ��������/�������� �� ����
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