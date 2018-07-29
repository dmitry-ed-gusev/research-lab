package jdb.processing.tester;

import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.processing.spider.DBSpider;
import jlib.logging.InitLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ����� ��� �������� ��������� ���������� ��� ������� ���� ��� � ����� ��.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 01.09.2008)
*/

public class DBTester
 {
  /** ���������-������ ������� ������. */
  private Logger           logger = Logger.getLogger(this.getClass().getName());
  /** ������� ������������ ������� ������. */
  private DBConfig config = null;

  /**
   * ����������� �� ���������. �������������� ������� ������������.
   * @param config ConnectionConfig ������������ ������.
  */
  /**
  public DBTester(DBConfig config) throws EmptyConnectionConfigException
   {
    logger.debug("WORKING DBTester constructor().");
    // ������������� ������������ ������
    if (config == null) throw new EmptyConnectionConfigException("Empty connection config!");
    else this.config = config;
   }
  */
  
  /**
   * �������� ���������� � ��. ���������� �������� ������ - ���� �� ������� ����������� � ��, ���� �� ������� -
   * ����� ���������� �������� null.
   * @return String �������� ������ ��� �������� null (���� ��� � �������).
   * @deprecated �� ������������� ������������ ������ �����.
  */
  public String testDBMSConnection()
   {
    String     result = null;
    Connection conn   = null;

    /**
    try
     {
      // ��������� ������������ ��� ����������� � ����
      ConnectionConfig localConfig = new ConnectionConfig(this.config);
      // ��������� ����� �� ���������� ��� ����������� � ������� � �����, � �� � ���������� ��
      localConfig.setDbName(null);
      // ������������
      JdbcConnector connector = JdbcConnector.getInstance(localConfig);
      conn = connector.getConnection();
     }
    // �������� ���� ��������� �� � ������������ ������ � ��������� ������ ����������
    catch (SQLException e)
     {
      result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "code: " + e.getErrorCode() +
                             " sqlState: " + e.getSQLState());
     }
    catch (EmptyConnectionTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (ClassNotFoundException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (EmptyDBMSTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (EmptyConnectionConfigException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (InvalidDBMSTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (InstantiationException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (IllegalAccessException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}

    // ���� ������� ����������� ����� ������� �� ����� ����������
    if (conn != null)
     try {conn.close();} catch (SQLException e) {logger.error("Can't close connection! [" + e.getMessage() + "]");}
    */

    return result;
   }

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, ���������� �� �� ������� ������� ��� ������
   * ���� � ������ dbName.
   * @param dbName String ��� ������� ��.
   * @return boolean ������/���� - ���������� �� ��������� ��.
  */
  public boolean isDBExists(String dbName)
   {
    boolean result = false;
    logger.debug("Checking existence DB [" + dbName + "] on current DBMS server.");
    try
     {
      // ���� ���������� ��� �� ����� - ������ ������ �� ������
      if ((dbName != null) && (!dbName.trim().equals("")))
       {
        // ��������� ������������� �� ������ ���� ���� ������� � ����
        String connectResult = this.testDBMSConnection();
        if (connectResult == null)
         {
          logger.debug("Connect to DBMS is OK! Searching DB.");
          DBSpider spider = new DBSpider(this.config);
          ArrayList<String> databases = spider.getDBSList();
          // ���� ������� �������� ������ ��� ������ ������� ������� - ���� �� ���� ������� ��
          if ((databases != null) && (!databases.isEmpty()))
           if (databases.contains(dbName.toUpperCase()))
            result = true;
         } // ����� ��������� �������� �������� � ����
        else logger.error(String.format(DBTesterConsts.MSG_ERROR_CONNECT, "", connectResult, ""));
       } // ����� ��������� �������� ����� ��
      else logger.error("DB name is empty! Nothing to search!");
     }
    catch (SQLException e)
     {logger.error(String.format(DBTesterConsts.MSG_ERROR, e.getClass().getName(), e.getMessage()));}
     catch (DBConnectionException e) {
     logger.error(e.getMessage());
    } catch (DBModuleConfigException e) {
     logger.error(e.getMessage());
    }
    return result;
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jlib");
    //Logger logger = Logger.getLogger(DBTester.class.getName());

    // ������� ���������� ����� universalConnector
    InitLogger.initLogger("jlib.db.universalConnector", Level.INFO);
    // ������� ���������� ����� universalConfig
    InitLogger.initLogger("jlib.db.universalConfig", Level.INFO);

    /**
    ConnectionConfig mysqlServerConfig = new ConnectionConfig();
    mysqlServerConfig.setDbConnectionType("direct");
    mysqlServerConfig.setDbType("mysql");
    mysqlServerConfig.setDbHost("localhost:3306");
    mysqlServerConfig.setDbName("storm");
    mysqlServerConfig.setDbUser("root");
    mysqlServerConfig.setDbPassword("mysql");

    ConnectionConfig mysqlClientConfig = new ConnectionConfig();
    mysqlClientConfig.setDbConnectionType("direct");
    mysqlClientConfig.setDbType("mysql");
    mysqlClientConfig.setDbHost("localhost:3306");
    mysqlClientConfig.setDbName("storm_client");
    mysqlClientConfig.setDbUser("root");
    mysqlClientConfig.setDbPassword("mysql");

    ConnectionConfig ifxConfig = new ConnectionConfig();
    ifxConfig.setDbConnectionType("direct");
    ifxConfig.setDbType("informix");
    ifxConfig.setDbHost("appserver:1526");
    ifxConfig.setDbServerName("hercules");
    ifxConfig.setDbName("storm_test");
    ifxConfig.setDbUser("informix");
    ifxConfig.setDbPassword("ifx_dba_019");

    ConnectionConfig dbfConfig = new ConnectionConfig();
    dbfConfig.setDbConnectionType("direct");
    dbfConfig.setDbType("dbf");
    dbfConfig.setDbName("q:/new/fleet");
    */
   
   }

 }