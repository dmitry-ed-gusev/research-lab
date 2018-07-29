package jdb.utils;

import jdb.DBConsts;
import jdb.DBConsts.DBType;
import jdb.config.DBConfig;
import jdb.config.common.ConfigInterface;
import jdb.config.connection.BaseDBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.DBModel;
import jdb.utils.helpers.JdbcUrlHelper;
import jlib.exceptions.utils.ExceptionUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * ������ ����� �������� ��������� (������ � ��������) �������� ��� ������ � ����.
 * @author Gusev Dmitry (019gus)
 * @version 7.1 (DATE: 26.04.2011)
*/

@SuppressWarnings({"CallToDriverManagerGetConnection", "JDBCResourceOpenedButNotSafelyClosed", "JNDIResourceOpenedButNotSafelyClosed"})
public final class DBUtils
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DBUtils.class.getName());

  // ������������� ��������������� � ������������
  private DBUtils() {}

  /**
   * ����� ��������� � ���������� ��������� ������������� ����������� � �������� ��������� ������� ResultSet.
   * ���� ������ null ��� �� �������� �� ����� ������ - ����� ���������� �������� null.
   * @param rs ResultSet ������ ��� ������������ (��� ��������� ���������� �������������).
   * @return String ��������� ������������� ����������� �������.
  */
  public static String getStringResultSet(ResultSet rs)
   {
    logger.debug("WORKING DBUtils.getStringResultSet().");
    StringBuilder result = null;
    // ���� ������ ���� - ���������� null
    if (rs != null)
     {
      logger.debug("ResultSet is not empty. Processing.");
      try
       {
        // ���� � ������� ���� ���� ���� ������ - ��������
        if (rs.next())
         {
          result = new StringBuilder();
          int columnCount = rs.getMetaData().getColumnCount();
          // ������� ����� �������
          int counter = 0;
          // �������� �� ���� ������� ������� � ��������� �� � ���������
          do
           {
            // ��������� ������ ���������� ������� �� ���� ����� ������ �������
            for (int i = 1; i <= columnCount; i++) {result.append(rs.getString(i)).append("|");}
            result.append("\n");
            counter++;
           }
          while(rs.next());
          result.append("-------\n");
          result.append("RECORDS: ").append(counter).append("\n");
         }
        else
         {logger.warn("ResultSet is not NULL, but is EMPTY!");}
       } // end of TRY
      catch (SQLException e) {logger.error("SQL error occured: " + e.getMessage());}
     }
    else logger.warn("ResultSet is NULL!");

    String str;
    if (result != null) {str = result.toString();} else {str = null;}
    return str;
   }

  /**
   * ����� � ����������� �� ���������� ���� ���� ���������� ����������� ����� �������� ��� ���������� � ����
   * (������������ ��������). ���� ��� ���������� �� ������ (������ ��� �� �������������� ��������) - �����
   * ���������� �������� null.
   * @param dbType DBType ��� ���� ��� ������ ��������.
   * @return String ������������ ������ �������� ��� ����������� � ����.
  */
  public static String getDBDriver(DBType dbType)
   {
    String driverClass = null;
    // ���� ��� ���� �� ���� - ��������
    if (dbType != null)
     {
      logger.debug("DBType is not empty. Processing.");
      // ����� �������� � ����������� �� ���� ����
      switch (dbType)
       {
        case INFORMIX:     driverClass = DBConsts.DBDRIVER_INFORMIX;     break;
        case MYSQL:        driverClass = DBConsts.DBDRIVER_MYSQL;        break;
        case DBF:          driverClass = DBConsts.DBDRIVER_DBF;          break;
        case ODBC:         driverClass = DBConsts.DBDRIVER_ODBC;         break;
        case MSSQL_JTDS:   driverClass = DBConsts.DBDRIVER_MSSQL_JTDS;   break;
        case MSSQL_NATIVE: driverClass = DBConsts.DBDRIVER_MSSQL_NATIVE; break;
        default:           logger.error("Unsupported DB type: [" + dbType + "]!"); driverClass = null; break;
       }
     }
    else {logger.error("DBType is empty! Can't select DB driver class.");}
    // ���������� �����
    logger.debug("getDBDriverClass: DB type [" + dbType + "]. DB driver class [" + driverClass + "].");
    return driverClass;
   }

  /**
   * �����, � ����������� �� ���������� ���������� � ����, ��������� ���������������� URL ��� ���������� � ���� - jdbcUrl.
   * ���� ����� ������ ������ ������ - ����� ���������� �������� null. ���� �� �������������� ��������� ��� ���� -
   * ����� ����� ������ �������� null.
   * @param config ������ ��� ������������ JDBC URL.
   * @return String ������������� ������-������������ ��� ���������� � ���� (URL).
  */
  public static String getDBUrl(BaseDBConfig config)
   {
    // ��������� ���������� ������
    String result = null;
    // ���� ��������� ������ ��� ���������� �� �������� ������ � �� ���� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      logger.debug("Received connection config is OK. Processing.");
      // ����� ���� ����
      switch (config.getDbType())
       {
        case MYSQL:        result = JdbcUrlHelper.getMysqlJdbcUrl(config);       break;
        case ODBC:         result = JdbcUrlHelper.getOdbcJdbcUrl(config);        break;
        case DBF:          result = JdbcUrlHelper.getDbfJdbcUrl(config);         break;
        case INFORMIX:     result = JdbcUrlHelper.getInformixJdbcUrl(config);    break;
        case MSSQL_JTDS:   result = JdbcUrlHelper.getMssqlJtdsJdbcUrl(config);   break;
        case MSSQL_NATIVE: result = JdbcUrlHelper.getMssqlNativeJdbcUrl(config); break;
        default:           logger.error("Unsupported DB type: [" + config.getDbType() + "]!"); result = null; break;
       }
     }
    // ���� �� ��������� ������ ���� - ������� ������ � ���
    else {logger.error("Received connection config had errors [" + configErrors + "]!");}
    // ���������� �����
    logger.debug("getDBUrl: generated URL -> [" + result + "].");
    return result;
   }

  /**
   * ����� ������� � ���������� ���������� (������ Connection) � ��������� � ������� ����. ������ ����� ����� ��������
   * ��� � ������ ����������� (����� JDBC �������), ��� � � ���������� ������ JNDI, ����� ������� ���������� �������
   * �� ����������� ������ ������� - ���� � ������� ��������� ���� dataSource, �� ����� ��������� ���������� �����
   * JNDI �������� ������, ���� �� �� ��������� - ����� ���������� ���������� ���������� ����������� JDBC ��������
   * ��������� ����.
   * @param config BaseDBConfig ������ ��� ���������� � ����.
   * @return Connection ��������� � ���� ����������.
   * @throws DBModuleConfigException ������ � ������������ ���������� � ���� (� ������ �������).
   * @throws DBConnectionException ������ ��� ���������� � ����.
  */
  public static Connection getDBConn(BaseDBConfig config) throws DBModuleConfigException, DBConnectionException
   {
    logger.debug("DBUtils.getDBConn(): connecting to DBMS.");
    Connection connection;
    // ���� ��� ������� ������ � �������� - ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ������ ��� ���� ������������ ��� �� ����������� � �� - ����� JDBC ��� ����� JNDI. ���� � ���������� ���
    // ������� ������� ��� ��������� ������ JNDI - ���������� ���� ��� ����������, ���� �� �� ������� - ���������� ���
    // ���������� - ����� JDBC-�������.
    if (!StringUtils.isBlank(config.getDataSource()))
     {
      logger.debug("Connecting to DBMS over JNDI data source.");
      try
       {
        // �������� �������� ������
        DataSource dataSource = (DataSource) new InitialContext().lookup(config.getDataSource());
        connection = dataSource.getConnection();
       }
      catch (NamingException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (SQLException e)    {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
     }
    // ��� ��������� ������ JNDI �� ������� - ����������� ����� JDBC
    else
     {
      logger.debug("Connecting to DBMS over JDBC driver.");
      try
       {
        // �������� �������
        String dbDriver = DBUtils.getDBDriver(config.getDbType());
        // ���� ������� �� ���� - �������� ������
        if (!StringUtils.isBlank(dbDriver))
         {
          logger.debug("Database driver OK. Processing. Driver: [" + dbDriver + "].");
          // �������� ������ �������� (��� ��������� ���� JDBC 4 �� ����� - ???)
          // todo: ���������� �� ������ �������� ��������?
          Class.forName(dbDriver).newInstance();
          logger.debug("Driver [" + dbDriver + "] loaded!");
          // �������������� ��������� ��� ���������� � ����
          Properties connectionInfo = config.getConnInfo();
          // ��������������� ����������� � ����
          if ((connectionInfo != null) && (!connectionInfo.isEmpty()))
           {
            logger.debug("Using getConnection() with [CONNECTION INFO].");
            connection = DriverManager.getConnection(DBUtils.getDBUrl(config), config.getConnInfo());
            //logger.debug("Connection ok.");
           }
          else
           {
            logger.debug("Using getConnection() without [CONNECTION INFO].");
            connection = DriverManager.getConnection(DBUtils.getDBUrl(config));
            //logger.debug("Connection ok.");
           }
         }
        // ���� ������� (����� ��������) �� ������ - ���������� �� � �������� �� ����
        else {throw new DBConnectionException("Database driver class is empty (NULL)!");}
       }
      catch (ClassNotFoundException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (IllegalAccessException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (InstantiationException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (SQLException e)           {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
     }
    // ���������� ������ "���������� � ����".
    return connection;
   }

  /**
   * ����� ������� � ���������� ����������, ������������������ � ������� JNDI ��������� ������ dataSourceName. ����
   * ��� ��������� ������ ����� - ��������� ��.
   * @param dataSourceName String ��� JNDI ��������� ������.
   * @return Connection ��������� � ���� ����������.
   * @throws DBModuleConfigException ������ � ������������ ���������� � ���� (������ ��� ��������� ������).
   * @throws DBConnectionException ������ ��� ���������� � ����.
  */
  public static Connection getDBConn(String dataSourceName) throws DBConnectionException, DBModuleConfigException
   {
    if (!StringUtils.isBlank(dataSourceName))
     {
      BaseDBConfig config = new BaseDBConfig();
      config.setDataSource(dataSourceName);
      return DBUtils.getDBConn(config);
     }
    // ��� ��������� ������ �����
    else {throw new DBModuleConfigException("Empty data source name!");}
   }

  /**
   * �������� ���������� (������������, �������������, �����������) ���������� � ���� � ������� ���������� �������.
   * ��������� ������ ����������� - �� ���� �� ��, ����� ������������ ���������� � ����, ���� ���������� �����������
   * ������ - �� �������� ���������� - ����� ���������� �������� ������, ���������� ���������� �����������. ���
   * ������������� �����-���� ������, ����� ���������� �������� ����, ��� ���� ��� ��������� �� ���������������,
   * ��������� � ��������� ������� ������������ � ���.
   * @param config BaseDBConfig ����������� ������������ ��� ���������� � ����.
   * @return boolean ������/���� � ����������� �� ����, �������� �� ���������� � ���� � ������� ���������� �������.
  */
  public static boolean isConnectionValid(BaseDBConfig config)
   {
    boolean result = false;
    Connection connection = null;
    try
     {
      logger.debug("Trying get connection to DBMS.");
      // �������� �������� ����������
      connection = DBUtils.getDBConn(config);
      // ���� ������� �������� ���������� - ������ ��� ��
      if (connection != null) {result = true;}
     }
    // ������������� � ������������ ��� ��������� ��
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    // ���� ���������� ������� ������� - ����������� ��������� ��� (��������)
    finally
     {
      try {if (connection != null) {connection.close();}}
      catch (SQLException e) {logger.error("Can't close connection (reason: " + e.getMessage() + ")!");}
     }
    // ���������� �����
    logger.debug("DBConfig validation result: " + result);
    return result;
   }

  /**
   * �������� ���������� (������������, �������������, �����������) ���������� � ���� � ������� ���������� ���������
   * ������. ��������� �������� ������ ����������� - �� ���� �� ��, ����� ������������ ��������� ���������� � ���� �
   * ������� ������� ��������� ������. ���� ���������� ������ ��������, �� ����� ���������� �������� ������, ����������
   * ���������� �����������. ��� ������������� �����-���� ������, ����� ���������� �������� ����, ��� ���� ��� ���������
   * �� ���������������, ��������� � ��������� ������� ������������ � ���.
   * @param dataSource DataSource ����������� �� ���������� �������� ������ ��� ���������� � ����.
   * @return boolean ������/���� � ����������� �� ����, �������� �� ���������� � ���� � ������� ���������� ���������
   * ������.
  */
  public static boolean isConnectionValid(DataSource dataSource)
   {
    boolean result = false;
    Connection connection = null;
    try
     {
      logger.debug("Trying get connection to DBMS.");
      // �������� �������� ����������
      if (dataSource != null)
       {
        connection = dataSource.getConnection();
        // ���� ������� �������� ���������� - ������ ��� ��
        if (connection != null) {result = true;}
       }
      // ���� �������� ������ ���� - ������� � ���
      else {logger.warn("Data source is NULL!");}
     }
    // �������� ���� ��
    catch (SQLException e) {logger.error(e.getMessage());}
    // ���� ���������� ������� ������� - ����������� ��������� ��� (��������)
    finally
     {
      try {if (connection != null) {connection.close();}}
      catch (SQLException e) {logger.error("Can't close connection (reason: " + e.getMessage() + ")!");}
     }
    // ���������� �����
    logger.debug("DataSource validation result: " + result);
    return result;
   }

  /**
   * ����� ���������� ���������, ��������������� ������ ������ ������������, ������������ ��������� ConfigInterface.
   * ���� ������ ��� (������������ � �������), �� ����� ���������� �������� NULL.
   * @param config ConfigInterface ������������ ��� ��������.
   * @return String �������� ������ ������� ��� NULL.
  */
  public static String getConfigErrors(ConfigInterface config)
   {
    // ���� ������ � �������, �� ����� ������ ������� NULL
    String result;
    if (config == null) {result = "Configuration is NULL!";}
    else                {result = config.getConfigErrors();}
    return result;
   }

  /**
   * ������ ����� ���������� �������� ������/���� � ����������� �� ����, �������� �� ��������� SQL-������ ��������
   * ���� "SELECT...".
   * @param sql String ������ ��� �������.
   * @return boolean ������ - ��� "SELECT..."-������, ���� - � ��������� ������
   * @throws java.sql.SQLException �� ���������, ���� ��������� ������ ����.
  */
  public static boolean isSelectQuery(String sql) throws SQLException
   {
    // ���� ������������� ������ ���� - �� �� ����� ������ �������! ������!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Query for analize is empty!");}
    // ������ ����������� �������������� �� ������ ������ (� ���������� ���������)
    return (StringUtils.strip(sql).toUpperCase().startsWith("SELECT"));
   }

  /**
   * ����� ����������� ���������� � �������� ��������� ������ �� � ���������� ��������� - ����� ��� ��� ���������
   * ������ (������ - ������ �����, ���� - ������ �� �����).
   * @param model DBModel ����������� ������ ��.
   * @return boolean ������/���� � ����������� �� ����, ���� ��� ��� ������ ���������� ��������� ������-������ ��.
  */
  public static boolean isDBModelEmpty(DBModel model)
   {
    boolean result = true;
    if ((model != null) && (!model.isEmpty())) {result = false;}
    return result;
   }

  /**
   * ����� ������ ��� ������� � ������������ ������� ������!
   * @param args String[] ��������� ������ main.
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
      ifxConfig.setDbName("memorandum");
      ifxConfig.setUser("informix");
      ifxConfig.setPassword("ifx_dba_019");
      
      DBConfig mysqlConfig = new DBConfig();
      mysqlConfig.setDbType(DBType.MYSQL);
      //mysqlConfig.setDbName("akme");
      mysqlConfig.setUser("root");
      mysqlConfig.setPassword("mysql");

      DBConfig dbfConfig = new DBConfig();
      dbfConfig.setDbType(DBType.DBF);
      dbfConfig.setDbName("//rshead/db002/new/fleet");

      DBConfig config = new DBConfig();
      config.setDbType(DBType.MSSQL_JTDS);
      config.setHost("APP");
      config.setDbName("MassEmailsSender");
      config.setUser("sa");
      config.setPassword("adminsql245#I");

      //DBSpider spider   = new DBSpider(ifxConfig);
      //DBModeler modeler = new DBModeler(config);

      //logger.info("DBS list: " + spider.getDBSList());
      //logger.info(spider.isDBExists("mkub"));
      //logger.info(spider.getUserTablesPlainList("c:/temp/dbf/fLEEt"));
      //logger.info(modeler.getDBStructureModel());
      //logger.info(modeler.getDBIntegrityModel("c:/temp/dbf/firm"));
      //logger.info(modeler.getDBTimedModel("memorandum"));

      Connection connection = DBUtils.getDBConn(config);

      // ����� �������� ��������� �� MS SQL 2005
      CallableStatement cstmt = connection.prepareCall("{call dbo.addDelivery(?, ?, ?, ?, ?)}");
      cstmt.setString(1, "subject from java");
      cstmt.setString(2, "text from java");
      cstmt.setInt(3, 0);
      cstmt.setString(4, "019bru");
      cstmt.registerOutParameter(5, java.sql.Types.INTEGER);
      cstmt.execute();
      logger.info("ID: " + cstmt.getInt(5));

      // ������� ����� � ����� (������� �����)
      //Scanner scanner = new Scanner(System.in);
      //scanner.nextLine();
      connection.close();
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
    //catch (DBModelException e)        {logger.error(e.getMessage());}
   }

 }