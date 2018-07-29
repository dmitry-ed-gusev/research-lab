package jdb.processing.processor;

import jdb.DBConsts.DBType;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.processing.sql.generation.SQLGenerator;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ������ ����� ������������ ��� ������ � �� � ����. ����� ������ ��������� ��������� ����������� ��� �� � ����.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 02.09.2008)
 * @deprecated ������ ������ �� �����������. ����� ��� ������ ��� �������� � ��������� ������� ����������.
*/

public class DBProcessor extends DBCommonProcessor
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * ����������� �� ���������. �������������� ������� ������������.
   * @param config ConnectionConfig ������������ ������.
   * @throws DBModuleConfigException �� ���������, ���� ������������ �������� ������ ������������.
  */
  public DBProcessor(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   *
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public boolean createDB(String dbName, DBStructureModel db, DBType targetDBType, boolean usePrimaryKey)
   throws SQLException, DBModuleConfigException, DBConnectionException
   {
    boolean result = false;
    // �������� ������ ���� ��������� ������ �� �� �����
    if (db != null)
     {
      // ��������� ������������� ��������� �� - ���� ���������� - ������ �� ������!
      DBSpider spider = new DBSpider(getConfig());
      // ��� ����������� �� - �������� ����� ��������� ������ � ������ �� ������ ��
      String localDBName;
      if ((dbName != null) && (!dbName.trim().equals(""))) {localDBName = dbName;}
      else {localDBName = db.getDbName();}
      // ���������������� �������� �� �������������� ������ ���� �������� �� �� ����������
      if (!spider.isDBExists(localDBName))
       {
        // ������� sql-������ ��� �������� ��������� ��
        ArrayList<String> sql    = SQLGenerator.getCreateDBSQL(localDBName, db, targetDBType, usePrimaryKey, true, false);
        // ���������� � ���������� sql-�������
        Connection    connection = null;
        Statement     statement  = null;
        // ��������������� ���������� �������
        try
         {
          // ������� ������� ����
          connection = DBUtils.getDBConn(getConfig());
          // ������� ������ statement
          statement  = connection.createStatement();
          // � ����� ��������� �������� �� (��������� ��������� ������ �� ������ �������)
          if ((sql != null) && (!sql.isEmpty()))
           {
            for (String currentQuery : sql)
             {
              try
               {statement.executeUpdate(currentQuery);}
              catch (SQLException e)
               {logger.error("Can't execute sql [" + currentQuery + "]! Reason [" + e.getMessage() + "].");}
             }
           }
          else {logger.fatal("Query [CREATE DATABASE...] is empty!");}
         }
        // ����������� ��������� �������
        finally {if (statement != null) statement.close(); if (connection != null) connection.close();}
       }
     }
    return result;
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger(DBProcessor.class.getName());

    // ������� ���������� ����� universalConnector
    InitLogger.initLogger("jdb.universalConnector", Level.INFO);
    // ������� ���������� ����� universalConfig
    InitLogger.initLogger("jdb.universalConfig", Level.INFO);

    /**
    ConnectionConfig mysqlConfig = new ConnectionConfig();
    mysqlConfig.setDbConnectionType("direct");
    mysqlConfig.setDbType("mysql");
    mysqlConfig.setDbHost("appserver:3306");
    mysqlConfig.setDbName("_storm_");
    mysqlConfig.setDbUser("root_");
    mysqlConfig.setDbPassword("mysql");

    ConnectionConfig mysqlClientConfig = new ConnectionConfig();
    mysqlClientConfig.setDbConnectionType("direct");
    mysqlClientConfig.setDbType("mysql");
    mysqlClientConfig.setDbHost("appserver:3306");
    mysqlClientConfig.setDbName("storm_test_test");
    mysqlClientConfig.setDbUser("root");
    mysqlClientConfig.setDbPassword("mysql");

    ConnectionConfig mysqlLocalConfig = new ConnectionConfig();
    mysqlLocalConfig.setDbConnectionType("direct");
    mysqlLocalConfig.setDbType("mysql");
    mysqlLocalConfig.setDbHost("localhost:3306");
    mysqlLocalConfig.setDbName("storm_client");
    mysqlLocalConfig.setDbUser("root");
    mysqlLocalConfig.setDbPassword("mysql");

    ConnectionConfig ifxConfig = new ConnectionConfig();
    ifxConfig.setDbConnectionType("direct");
    ifxConfig.setDbType("informix");
    ifxConfig.setDbHost("appserver:1526");
    ifxConfig.setDbServerName("hercules");
    ifxConfig.setDbName("storm");
    ifxConfig.setDbUser("informix");
    ifxConfig.setDbPassword("ifx_dba_019");

    ConnectionConfig dbfConfig = new ConnectionConfig();
    dbfConfig.setDbConnectionType("direct");
    dbfConfig.setDbType("dbf");
    dbfConfig.setDbName("q:/new/fleet");
    */
   }

 }