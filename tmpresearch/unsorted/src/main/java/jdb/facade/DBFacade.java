package jdb.facade;

import jdb.DBConsts;
import jlib.common.Consts;
import org.apache.log4j.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 ������ ����� ��������� ������ ��� facade - �����, ������� ���������
 ��������� �������������� �� ������� �������� � ��������� �� ������������
 �������� ���������� ���������������� ��������������. ����� DBFacade
 ��������� ����� ��� ������ � ���������� ������ ���� � ������� SQL-��������.
 �� ������ ������ �������������� ����������� � ���������� ����� SQL-��������
 ��� ��������� ����:      <br>
  - Informix              <br>
  - mySQL                 <br>
  - ODBC-�������� ������  <br>
  - DBF-���� ������       <br>
 ����� � ������ ������ ����������� ���������������� ���������� � ���� � �������
 ������ ������������ ���������� (������������ ����� ����������� � ����, ������������
 � ���������) ��� ��������� ��� ����������, �.�. ����������� �������������
 ������������� ��������� ���������� ���������� � ���� (�������� ������������� �����������
 � ���������� �������� � ���������� ����). <br>
 ���������������� ���������� ������������� ����������� � ��������� ��� � ����� ����
 ����������� ��������� �������: <br>
  ������ connect() � close() ���������� ������, ��� ������ ������ connect() ���������
  ����� ����������, ��� ���������� � ��� � ������������ �������. ���� ��� ��� �����, ��
  ����� ���������� �� ���������, � ������� ������������ ��������� ����������� � ���
  ����������. ����� close() ��������� ��������� ����������� � ��� ���������� �
  ����������� ����� � ����. ������������ ������ ���� ���������� ����� . �������
  ���������� ���������� (�������� Connection) � ���� ���������� ����� . ���� ��� ��
  ������������ ��� ��� �� ���������������, �� ��� ������ ����������� 0.

 ������ ������: �� ��������� ������� ������� ������� ���������� � Level.ERROR, � ���
 ������ ��������� ������� ������������ ��������� �������.

 @author Gusev Dmitry, dept. 019. 2005(�).
 @version 1.0
 * @deprecated �� ������������� ������������� ������ ����� ��� �����������!
*/
public class DBFacade
 {
  // --- �������� ���������� ����������� � ���� ---
  /** ���� ��� �������� ���� ����. */
  private String db_type  = null;
  /** ���� ��� �������� ����� ����. */
  private String host     = null;
  /** ���� ��� �������� ����� ������� ���� (�� �����, � �������� ����). */
  private String server   = null;
  /** ���� ��� �������� ����� �� �� �������. */
  private String db       = null;
  /** ���� ��� �������� ����� ������������ ��� ������� � ����. */
  private String user     = null;
  /** ���� ��� �������� ������ ������������ ��� ������� � ����. */
  private String pwd      = null;
  /** ���� ��� �������� �������������� ���������� ��� ����������� � ����. */
  private String params   = null;
  /** ���� ��� �������� ���. ���������� ��� ���������� � ����. */
  private String connInfo = null;
  /**  */
  private Properties connProperties = null;

  /** ������ ��� ������� ��������� ������� ������. */
  private static String          logger_format          = Consts.LOGGER_LOG_FORMAT_DEFAULT;
  /** ���������� ������ ������� ������� (�� ������ �������). */
  private static PatternLayout   logger_patternLayout   = null;
  /** ����� ��� ������ ��������� ������� �� ������� (ConsoleAppender). */
  private static ConsoleAppender logger_consoleAppender = null;
  /** ����� ��� ������ ��������� ������� � ���� (FileAppender). */
  private static FileAppender    logger_fileAppender    = null;
  /** �������� ����� ��� ������� ������� ������. */
  private static String          logger_file            = null;
  /***/
  private static String          logger_parent_name     = null;
  /** �������� ���������� ������� (������� �������). */
  private static String          logger_name            = "DBFacade";
  /** ��������������� ����� ������� �������. */
  private static Logger          logger                 = null;
  /**
   * ���������� ��� �������� ������ ������� �������.
   * �������� ������� ��. � ������ org.apache.log4j.
   * ��������� ��������:
   * - Level.DEBUG
   * - Level.INFO
   * - Level.WARN
   * - Level.ERROR
   * - Level.FATAL
   * - Level.ALL - ����� ��������� ���� �������
   * - Level.OFF - ���������� ������ ���� ���������
   * ��������� ��������: DEBUG < INFO < WARN < ERROR < FATAL.
   * �� ��������� ���������������� ��������� Level.ALL
   * ��������� � ��������� �������� �������������� � �������
   * ���� ������� set_log_level(Level level) + get_log_level().
  */
  private static Level logger_level = Level.ERROR;

  /** ���������� singleton-������ ������� DBFacade. */
  private static DBFacade dbf = null;
  /** ���� ��� �������� ���������� ������ PreparedStatement. */
  private PSqlStatement p_ss   = null;
  /** ���� ��� �������� ���������� ������ Statement. */
  private SqlStatement  ss     = null;
  /** ���� ��� �������� ���������� ������ ConnectionManager. */
  private ConnectionManager cm     = null;
  /**
   ���������� ����, ����������� ������������ �� �������� DBFacade ���
   ���������� SQL-�������� ����� Statement (usePrepared = false) ���
   ����� PreparedStatement (usePrepared = true).
  */
  private static boolean usePrepared = false;
  /***/
  private static boolean test_mode   = false;

  /**  */
  public void setDb_type(String db_type)   {this.db_type = db_type;}
  /**  */
  public void setHost(String host)         {this.host = host;}
  /**  */
  public void setServer(String server)     {this.server = server;}
  /**  */
  public void setDb(String db)             {this.db = db;}
  /**  */
  public void setUser(String user)         {this.user = user;}
  /**  */
  public void setPwd(String pwd)           {this.pwd = pwd;}
  /**  */
  public void setParams(String params)     {this.params = params;}
  /**  */
  public void setConnInfo(String connInfo) {this.connInfo = connInfo;}

  /**
   * ������������� ������� ��� ������� ������ (���������� ����������
   * ��� ������ ��������� � �������). ���� ������ ��� ����� ���������
   * ��� ������ ��������� - ������ �� ������.
  */
  private static void initLogger(Properties props) //throws Exception
   {
    // ���� ������ ��� �� ��������������� - �������������
    if (DBFacade.logger == null)
     {
      // ���� �������� ��������� ������������� ������� - ���������
      if ((props != null) && (props.size() > 0))
       {
        // ������ ��������� ��� �������
        DBFacade.logger_format = props.getProperty(Consts.LOGGER_LOG_FORMAT, DBFacade.logger_format);
        // �������� ����� ��� ������� �������
        DBFacade.logger_file   = props.getProperty(Consts.LOGGER_FILE_NAME, null);
        // ������� ������� �������
        DBFacade.logger_level  = Level.toLevel(props.getProperty(Consts.LOGGER_LOG_LEVEL, ""),
                                              DBFacade.logger_level);
       }
      // ������ ������� �������
      DBFacade.logger_patternLayout   = new PatternLayout(DBFacade.logger_format);
      // ��������� ��� ������ ��������� ������� �� �������
      DBFacade.logger_consoleAppender = new ConsoleAppender(DBFacade.logger_patternLayout);
      // ��������������� ��� ������
      DBFacade.logger = Logger.getLogger(DBFacade.logger_name);
      // ��������� ������������ ����������� ������ ��������� (Appenders)
      DBFacade.logger.setAdditivity(false);
      // ������������� ������� ��� ��������� ����
      DBFacade.logger.setLevel(DBFacade.logger_level);
      // ��������� ��������� ��� ������ ��������� �� �������
      DBFacade.logger.addAppender(logger_consoleAppender);
      // ���� ��� �������� ��� ����� ��� ������� - ������� ��������� ��� ������ � ����
      if ((DBFacade.logger_file != null) && (!DBFacade.logger_file.trim().equals("")))
       {
        try
         {
          DBFacade.logger_fileAppender = new FileAppender (DBFacade.logger_patternLayout,
                                                             DBFacade.logger_file, false);
          DBFacade.logger.addAppender(DBFacade.logger_fileAppender);
          logger.debug("FileAppender was added for [" + Logger.class + "].");
         }
        catch (Exception e)
         {
          logger.error("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
          //throw new Exception("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
         }
       }
      logger.debug("ConsoleAppender was added for [" + Logger.class + "].");
      // ����� ������������� ������� - �������� �� ����
      logger.debug("Logger [" + Logger.class + "] initialized OK.");
     }
    else
     logger.debug("Logger already initialized!");
   }

  /**
   * ������������� ������������ ����� ������� � ���������� ������ DBFacade. ����
   * ��� ��� �� ������ ���������� ������ DBFacade �� ��������� � ����� ����������
   * ������ �� ����, ���� �� ��� ���� ��������� - ������ ������������ ������. ���
   * ������������� ������� ������ ��������� ��������� ����������� ���������� � ����
   * (��� ������� ������). ��� ���������� sql-�������� ������������ �����
   * Statement.
   * @return ������ �� ��������� ������ DBFacade (���� ��� �� ���� - �������).
   * @throws Exception
  */
  public static synchronized DBFacade getInstance() throws Exception {return DBFacade.getInstance(null);}

  /**
   * ������������� ������������ ����� ������� � ���������� ������ DBFacade. ����
   * ��� ��� �� ������ ���������� ������ DBFacade �� ��������� � ����� ����������
   * ������ �� ����, ���� �� ��� ���� ��������� - ������ ������������ ������.
   * ������ ������� �������� ��������� �� ������������/�� ������������� ���� ����������.
   * ������ ������� �������� ��������� �� ��� ������� ��� ���������� sql-�������� -
   * Statement ��� Prepared Statement (����������������� ������).
   * @return ��������� ������ DBFacade (���� ��� �� ���� - �������).
   * @throws Exception
  */
  public static synchronized DBFacade getInstance(Properties props) throws Exception
   {
    // ��� ��� �������� ��������� - ��� ����� ���� � ���-�� ��� �������
    DBFacade.initLogger(props);
    logger.debug("ENTERING getInstance(Properties props).");
    if ((props != null) && (props.size() > 0))
     {
      logger.debug("Input parameters (Properties) are not empty.");
      // ������� �������� ��� ���� <usePrepared>
      //DBFacade.usePrepared = Boolean.getBoolean(props.getProperty(DBConsts.DBCONN_USE_PREPARED, "false"));
      logger.debug("Received usePrepared value [" + DBFacade.usePrepared + "].");
     }
    else
     logger.debug("Input parameters are empty.");
    // ������ ���������� ���������������� ��������� ������ DBFacade
    logger.debug("Trying to initialize DBFacade instance.");
    if(DBFacade.dbf == null)
     {
      logger.debug("DBFacade is not initialized. Initializing...");
      DBFacade.dbf = new DBFacade();
     }
    else
     logger.debug("DBFacade already initialized.");
    logger.debug("LEAVING getInstance(Properties props).");
    return DBFacade.dbf;
   }

  /**
   * ����������� �������� ��� private ��� ����, ����� ������ ���� ��������
   * �������������� ���������� ������� ������ - �������� � ���������
   * ����������� �������� ������ � ������� ������� getInstance() - ���
   * �������� ����������� ������� ��� singleton (��������).
   * @throws Exception
  */
  private DBFacade() throws Exception {cm = ConnectionManager.getInstance();}

  /**
   * ����� ��������� �������� ���������� - ������������ �� ��� ������� ����������
   * ������ DBFacade ����� PreparedStatement (�������� true) ��� �� �����
   * Statement (�������� false).
   * @return boolean �������� ���������, ������������� ������������� ������ ���
   * ������� ������ ��� ���������� sql-��������.
  */
  public boolean get_use_prepared_status()
   {
    logger.debug("ENTERING get_use_prepared_status().");
    logger.debug("Returning usePrepared value [" + DBFacade.usePrepared + "].");
    logger.debug("LEAVING get_use_prepared_status().");
    return DBFacade.usePrepared;
   }

  /**
   * ����� ��������� ���������� �������� ���������, ������� ���������� �������������
   * ������ �� ������� ��� ���������� sql-�������� (Statement ��� Prepared Statement).
   * ������������ ������ ����� ����� ����� ������ ����� ������� DBFacade.connect(), �.�.
   * ������ ����� ���������������� ������������� ���������� � ���� ����� ������� ��� ������,
   * ������������� ��� ���������� sql-��������. �.�. ������������ ��� ����������
   * sql-�������� ������ PreparedStatement ����� ����� ���������:
   * <br> <b>1.</b> ��������� ����� DBFacade.getInstance(<boolean>, true)
   * <br> <b>2.</b> ��������� ����� DBFacade.getInstance(), � ����� ��������� �����
   * DBFacade.set_use_prepared_status(true).
   * <br>������, ���� ����� ������ ������ �� �.1 ������� �����
   * DBFacade.set_use_prepared_status(false), �� ��� ���������� sql-�������� �����
   * ����������� ����� Statement.
   * @param usePrepared boolean 123
  */
  public void set_use_prepared_status(boolean usePrepared)
   {
    logger.debug("ENTERING set_use_prepared_status(boolean usePrepared).");
    logger.debug("Received usePrepared value [" + usePrepared + "]. Setting value.");
    DBFacade.usePrepared = usePrepared;
    logger.debug("LEAVING set_use_prepared_status(boolean usePrepared).");
   }

  //Initialization to open a connection and pass it to the SqlStatement object.
  //private void init(Properties props) throws Exception
  // {
  //  Connection conn = cm.connect(props);
  //  p_ss = new PSqlStatement();
  //  p_ss.setConnection(conn);
  //  ss = new SqlStatement();
  //  ss.setConnection(conn);
  // }

  /**
   * ������ ����� ��������� ���������� � ����. ������� ��������� ����������
   * ��������� ����������.
   *
   * @param props Properties �������� ��������� ���������� � ���� (���, ����
   *   ...).
   * @throws Exception
   * @return int
  */
  public int connect(Properties props) throws Exception
   {
    logger.debug("ENTERING connect(Properties props).");
    // � ���� ���������� ������ ����� ���������� ���������� � ����
    int conn_number = -1;
    // � ���� ���������� ������ ��������� ���������� (������ Connection)
    Connection conn = null;
      logger.debug("Using connection pool. Trying to add connection to the pool.");
      // ���������� ���������� � ���, �������� ��� ����� � ����
      //conn_number = cm.connect(props);
      logger.debug("[returned number of connection in pool: " + conn_number + "]");
      // ���� ��� ��������� -1 - ��� �������� � ����� ���������� �� ���������
      if (conn_number == -1) // <- ��� ��������, ���������� �� ���������
       {
        logger.error("Connection pool size = MAX. New connection not added!");
        // �� - ��� ��������!
        throw new Exception("Connection pool size = MAX. New connection not added!");
       }
      else // <- ���������� ���������
       {
        logger.debug("Connection pool size < MAX. New connection added!");
        // �������� ���� ������ Connection �� ����
        //conn = cm.getConn(conn_number);
       }

    logger.debug("Calling setStmtConn(Connection conn) method.");
    // �������� �����, ��� ������������� ������ Statement � ��������� ���������� �� ���������
    if (conn != null)
     {
      logger.debug("Connection is not null - STATEMENT object initialized!");
      this.setStmtConn(conn);
     }
    else
     logger.debug("Connection is null! STATEMENT object is not initialized!");
    logger.debug("LEAVING connect(Properties props).");
    return conn_number;
   }

 /**
  *
  * @param conn Connection
  */
 private void setStmtConn(Connection conn)
   {
    logger.debug("ENTERING setStmtConn(Connection conn).");
    // ��������� ���������� ������������ ������ ���� ���������� ����������������
    if (conn != null)
     {
      logger.debug("Connection received successfully.");
      if (DBFacade.usePrepared) // <- USING PREPARED SQL STATEMENT
       {
        logger.debug("Using prepared SQL-statements.");
        // ���� ����� PreparedStatement �� ��������������� - �������������
        if(this.p_ss == null)
         {
          logger.debug("Prepared SQL-statement not initialized. Initializing...");
          this.p_ss = new PSqlStatement();
         }
        else
         logger.debug("Prepared SQL-statement already initialized.");
        // ��������������� ��������� ���������� ��� ������� Prepared Statement
        logger.debug("Setting connection for prepared SQL-statement.");
        p_ss.setConnection(conn);
       }
      else // <- NOT USING PREPARED SQL STATEMENT
       {
        logger.debug("Not using prepared SQL-statements.");
        // ���� ����� Statement �� ��������������� - �������������
        if(this.ss == null)
         {
          logger.debug("SQL-statement not initialized. Initializing...");
          this.ss = new SqlStatement(DBFacade.logger_name);
         }
        else
         logger.debug("SQL-statement already initialized.");
        // ��������������� ��������� ���������� ��� ������� Statement
        logger.debug("Setting connection for SQL-statement.");
        this.ss.setConnection(conn);
       }
     }
    else // <- ���������� �� ���� ����������������
     logger.debug("Connection not initialized (input Connection parameter is NULL)! ");
    logger.debug("LEAVING setStmtConn(Connection conn).");
   }

  /**
   * ����� ��� ���������� sql-�������, ������������� ������ (select).
   * ���������������� ������� �� ��������� usePrepared
   * @param sql String ������, ������������ ������ �� ����������. ������
   * ����� ���� ������ ���� SELECT ...
   * @return ResultSet ��������� ���������� ������� �� ������� ������ - ������.
   * @throws SQLException
  */
  public ResultSet executeQuery(String sql) throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(String sql).");
    ResultSet rs = this.executeQuery(-1, sql, -1, -1);
    logger.debug("LEAVING executeSelectQuery(String sql).");
    return rs;
   }

  /***/
  public ResultSet executeQuery(int conn_number, String sql) throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(int conn_number, String sql).");
    ResultSet rs = this.executeQuery(conn_number, sql, -1, -1);
    logger.debug("LEAVING executeSelectQuery(int conn_number, String sql).");
    return rs;
   }

  /***/
  public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    return this.executeQuery(-1, sql, resultSetType, resultSetConcurrency);
   }

  /**
   * ������ ����� �������� ������� ��� ���������� sql-�������, ����������� � ��������
   * ���������. ����� ��������� ��������� ������ SELECT-�������, �.�. ������
   * �������, ������������ ������ - ������ (ResultSet).
   * @param conn_number int
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
   * @throws SQLException
   * @return ResultSet
  */
  public ResultSet executeQuery(int conn_number, String sql,
                                int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(int conn_number, String sql, " +
                 "int resultSetType, int resultSetConcurrency)");
    logger.debug("[SQL:" + sql + "]");
    // ������ ��������� ������� - ������
    ResultSet rs;
    // ��������, ��� ���������� ��� ������ �� ���� � ��� select-������.
    // ���� �������� ����������� ��������, �� ��������� ���� � ��������
    // ���� � ���������� �������� ��������� �� ����� - ����� ������
    // ������ ������ (null).
    if ((sql != null) && (!sql.trim().equals("")) &&
        (sql.trim().toUpperCase().startsWith("SELECT")))
     {
      logger.debug("SQL query is OK (not empty and 'select...' like).");
     }
    else
     {
      logger.error("SQL is invalid (empty or not 'select...' like)!");
      logger.error("[SQL:" + sql + "]");
      // ���� ������ ��������� ���� - �������� �� (�������������� ��������)
      throw new SQLException("SQL is invalid (empty or not 'select...' like)!" +
                             "\n[SQL:" + sql + "]");
     }
    // �������� �������� ������������� ���������� �/��� ����!

      logger.debug("Using connection pool. Continue.");
    /*
      if (this.cm.isConnExist(conn_number))
       {
        logger.debug("Connection #" + conn_number + " exists in the pool.");
        // ��������� ��� ������� Statement ���������� �� ���������
        this.setStmtConn(this.cm.getConn(conn_number));
       }
      else
       {
        logger.debug("Connection #" + conn_number + " not exists in the pool.");
        logger.debug("Using last connection in the pool.");
       }
     */
    
    // ���������������� ���������� SQL-�������
    if (DBFacade.usePrepared) // <- USED PREPARED STATEMENT
     {
      logger.debug("Using prepared statements. Continue - running query.");
      // ���� ������ ��� ���������, ������������ ��� ������������� �������,
      // �� �������� ����� executeSelectQuery() � ����� �����������.
      if ((resultSetType > 0) && (resultSetConcurrency > 0))
       {
        logger.debug("For ResultSet class types used - running query.");
        rs = this.p_ss.executeQuery(sql, resultSetType, resultSetConcurrency);
       }
      else
       {
        logger.debug("For ResultSet class types not used - running query.");
        rs = this.p_ss.executeQuery(sql);
       }
     }
    else // <- NOT USED PREPARED STATEMENT
     {
      logger.debug("Not using prepared statement. Continue - running query.");
      // ���� ������ ��� ���������, ������������ ��� ������������� �������,
      // �� �������� ����� executeSelectQuery() � ����� �����������.
      if ((resultSetType > 0) && (resultSetConcurrency > 0))
       {
        logger.debug("For ResultSet class types used - running query.");
        rs = this.ss.executeQuery(sql, resultSetType, resultSetConcurrency);
       }
      else
       {
        logger.debug("For ResultSet class types not used - running query.");
        rs = this.ss.executeQuery(sql);
       }
     }

    logger.debug("LEAVING executeSelectQuery(int conn_number, String sql, " +
                 "int resultSetType, int resultSetConcurrency)");
    return rs;
   }

  //Method to execute SELECT SQL statements
  public ResultSet executeQuery() throws SQLException
   {
    if (DBFacade.usePrepared) return this.p_ss.executeQuery();
    else return this.ss.executeQuery();
   }

  //Method to execute all SQL statements except SELECT
  public int execute(String sql) throws SQLException
   {
    logger.debug("ENTERING execute(String sql).");
    logger.debug("[SQL:" + sql + "]");
    // �������� �� ������������ ���������� ��� sql-������
    if ((sql != null) && (!sql.trim().equals("")) &&
        (!sql.trim().toUpperCase().startsWith("SELECT")))
     {
      logger.debug("SQL query is OK (not empty and not 'select...' like).");
     }
    else
     {
      logger.error("SQL query is invalid (empty or 'select...' like)!");
      logger.error("[SQL:" + sql + "]");
      throw new SQLException("SQL query is invalid (empty or 'select...' like)!" +
                             "\n[SQL:" + sql + "]");
     }

    int res = 0;
    if (usePrepared) res = p_ss.execute(sql);
    else res = ss.execute(sql);
    logger.debug("LEAVING execute(String sql).");
    return res;
   }

  //Method to execute all SQL statements except SELECT
  public int execute() throws SQLException
   {
    if (usePrepared) return p_ss.execute();
    else return ss.execute();
   }

  //Sets the SQL string in the PSqlStatement object
  public void setSql(String sql) throws SQLException
   {if (usePrepared) p_ss.setSql(sql); else ss.setSql(sql);}

  public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
    {
     if (DBFacade.usePrepared) p_ss.setSql(sql);
     else ss.setSql(sql, resultSetType,resultSetConcurrency);
    }

  //Clears the SqlStatement object
  //public void reset(Properties props) throws Exception
  // {
    //Set the reference to the ss to null;
    //p_ss = null;
  //  ss = null;
    //Reinitialize object
  //  init(props);
  // }

  /**
   ��������� ���������� � ����.
   @throws SQLException
  */
  public void close() throws SQLException
   {
    if (usePrepared) {if(p_ss != null) p_ss.close();}
    else {if(ss != null) ss.close();}
    if(cm != null) cm.close();
   }

  /**
   * Set a String value in a PreparedStatement
   *
   * @param index int
   * @param value String
   * @throws SQLException
  */
  public void setString(int index, String value) throws SQLException
   {
    logger.debug("ENTERING setString(int index, String value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setString(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setString(int index, String value).");
   }

  /**
   * Set an int value in a PreparedStatement
   *
   * @param index int
   * @param value int
   * @throws SQLException
  */
  public void setInt(int index, int value) throws SQLException
   {
    logger.debug("ENTERING setInt(int index, int value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setInt(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setInt(int index, int value).");
   }

  /**
   * Set a double value in a PreparedStatement
   *
   * @param index int
   * @param value double
   * @throws SQLException
  */
  public void setDouble(int index, double value) throws SQLException
   {
    logger.debug("ENTERING setDouble(int index, double value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setDouble(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setDouble(int index, double value).");
   }

  /**
   * Set a Date value in a PreparedStatement
   *
   * @param index int
   * @param value Date
   * @throws SQLException
  */
  public void setDate(int index, Date value) throws SQLException
   {
    logger.debug("ENTERING setDate(int index, Date value).");
    logger.debug("[index:" + index + "; Date:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setDate(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setDate(int index, Date value).");
   }

  /**
   * ����� ���������� ���������������� ���� ������� ������, � ����� ������������
   * ���������� � ���� (�������� ����� print_config() ������ ConnectionManager).
  */
  public void print_config()
   {
    System.out.println("----- DEBUG INFO (CONFIG) [" + this.getClass().getName() + "] -----");
    System.out.println("usePrepared = " + DBFacade.usePrepared);
    System.out.println("*** DBMS connection properties ***");
    //this.cm.print_config();
    System.out.println("----- DEBUG INFO (CONFIG) [" + this.getClass().getName() + "] -----");
   }

 /**
  * ����� main ������������ ��� ������������ ������� ������.
  * @param args String[] ������ ���������� ��������� ������ ��� ��������
  * ������ main.
  */
 public static void main(String[] args)
    {
     try
      {
       Properties props = new Properties();
       DBFacade db = DBFacade.getInstance();
       //props.put(DBConsts.DB_TYPE, DBConsts.DBTYPE_INFORMIX);
       props.put(DBConsts.DB_HOST,    "10.1.19.30:1526");
       props.put(DBConsts.DB_SERVER,  "edu");
       //props.put(DBConsts.DB_NAME,      "flt");
       props.put(DBConsts.DB_USER,    "informix");
       props.put(DBConsts.DB_PWD,     "123456");

       props = null;
       db.connect(props);

       //db.print_config();
       //ResultSet rs = db.executeSelectQuery("SELECT namer FROM flt");

       //props = new Properties();
       //props.setProperty(DBConsts.DB_TYPE, DBConsts.DBTYPE_DBF);
       //props.put(DBC.DB_CONN_INFO, DBC.DBCONN_INFO_DBF_DBASE4);
       //props.setProperty(DBConsts.DB_NAME, "e:/temp");
       //int dbf_conn = db.connect(props);
       //db.print_config();
       //ResultSet rs2 = db.executeSelectQuery("SELECT name FROM insp");

       //ResultSet rs  = db.executeSelectQuery(ifx_conn, "SELECT namer FROM flt",
       //                                ResultSet.TYPE_SCROLL_INSENSITIVE,
       //                                ResultSet.CONCUR_READ_ONLY);
       //ResultSet rs2 = db.executeSelectQuery(dbf_conn, "SELECT name FROM insp");

       //int Counter = 0;
       //while (rs.next()) Counter++;
       //System.out.println("count = " + Counter);

       //rs.beforeFirst();

       //while (rs.next() && rs2.next())
       // {System.out.println(rs.getString("namer") + ":" + rs2.getString("name"));}

       //  while (rs2.next())
       //  {System.out.println(rs2.getString("name"));}
       //db.close();
     /*
     Properties props = new Properties();
     props.put(DBC.DB_TYPE, DBC.DBTYPE_ODBC);
     props.put(DBC.DB_NAME, "c:\\temp\\documents.mdb");
     props.put(LOGC.LOGGER_LOG_LEVEL, "DEBUG");
     //props.put(LOGC.LOGGER_FILE_NAME, "c:\\temp\\1");
     DBFacade db = DBFacade.getInstance(props);
     db.connect(props);
     String sql = "SELECT Departments.DepartmentNumber, Departments.DepartmentName, DocumentType.DocumentTypeID, " +
            "Mailing.MailingDate, DocumentType.DocumentType_Ru, Documents.Number_New, VolumeType.VolumeType_Ru, " +
            "Edoc.VolumeNumber FROM VolumeType INNER JOIN ((Departments INNER JOIN Keepers ON Departments.DepartmentID = " +
            "Keepers.DepartmentID) INNER JOIN (EdocType INNER JOIN ((DocumentType INNER JOIN (Documents INNER JOIN Edoc ON " +
            "Documents.DocumentID = Edoc.DocumentID) ON DocumentType.DocumentTypeID = Documents.DocumentTypeID) INNER JOIN " +
            "Mailing ON Edoc.EdocID = Mailing.EdocID) ON EdocType.EdocTypeID = Edoc.EdocTypeID) ON (Documents.DocumentID = " +
            "Keepers.DocumentID) AND (Keepers.KeepersID = Mailing.KeepersID)) ON VolumeType.VolumeTypeID = Edoc.VolumeTypeID " +
            "WHERE (((Departments.DepartmentNumber)=19) AND ((DocumentType.DocumentTypeID)=1 Or (DocumentType.DocumentTypeID)=2 " +
            "Or (DocumentType.DocumentTypeID)=3) AND ((Edoc.EdocTypeID)=1) AND ((Mailing.ConfirmationDate) Is Null))" +
            " UNION SELECT Departments.DepartmentNumber, Departments.DepartmentName, DocumentType.DocumentTypeID, " +
            "Mailing.MailingDate, DocumentType.DocumentType_Ru, AdditionalDocument.AdditionalDocumentNumber_New, " +
            "VolumeType.VolumeType_Ru, Edoc.VolumeNumber FROM VolumeType INNER JOIN ((Departments INNER JOIN Keepers ON " +
            "Departments.DepartmentID = Keepers.DepartmentID) INNER JOIN (EdocType INNER JOIN ((DocumentType INNER JOIN " +
            "(Documents INNER JOIN (AdditionalDocument INNER JOIN Edoc ON AdditionalDocument.AdditionalDocumentID = Edoc.DocumentID) " +
            "ON Documents.DocumentID = AdditionalDocument.DocumentID) ON DocumentType.DocumentTypeID = Documents.DocumentTypeID) " +
            "INNER JOIN Mailing ON Edoc.EdocID = Mailing.EdocID) ON EdocType.EdocTypeID = Edoc.EdocTypeID) ON (Documents.DocumentID " +
            "= Keepers.DocumentID) AND (Keepers.KeepersID = Mailing.KeepersID)) ON VolumeType.VolumeTypeID = Edoc.VolumeTypeID WHERE " +
            "(((Departments.DepartmentNumber)=19) AND ((DocumentType.DocumentTypeID)=1 Or (DocumentType.DocumentTypeID)=2 Or " +
            "(DocumentType.DocumentTypeID)=3) AND ((Edoc.EdocTypeID)=2) AND ((Mailing.ConfirmationDate) Is Null)) ORDER BY " +
            "DocumentType.DocumentTypeID, Mailing.MailingDate, Documents.Number_New, Edoc.VolumeNumber";
     ResultSet rs = db.executeSelectQuery(sql);
      ResultSetMetaData rsmeta = rs.getMetaData();
      while (rs.next())
       {
        for (int i = 1; i <= rsmeta.getColumnCount(); i++)
         System.out.print(rs.getString(i) + " ");
        System.out.println();
       }
      db.close();
      */
      }
     catch (Exception e)
      {System.out.println("error:" + e.getMessage()); /*e.printStackTrace();*/}
    }
 }//--> ����� ������� DBFacade.
