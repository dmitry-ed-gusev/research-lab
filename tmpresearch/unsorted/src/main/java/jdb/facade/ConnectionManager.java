package jdb.facade;

import jdb.DBConsts;
import jlib.common.Consts;
import jlib.logging.InitLogger;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * ��������:
 * ������ ����� ��������� ���������� � ����. ������ ���������� (Connection)
 * �������� �����������. ����� ���� ����� ������������� ��� �������������
 * ��� ����������� ���������� � ����, ���-�� ������� �������; ��� ��������
 * �����������. ����� ConnectionManager ��������� �������� ������ ���� ���������
 * (� ������ ������ ���������� ������ ��� singleton - ��������)! �������
 * ����������� �� ��������� �������� ��� private, ������ ������������� ���.
 * �������� ���������� � ������ � ��� ���������� ���������� (��������� ������
 * �� ����) �������������� � ������� ������ getInstance(). ���� ����� ����������
 * ������ �� ��������� ��������� ������, � ���� ��� �� ������ ����������, ��
 * ������� ���. ��� ������������� ���� ���������� ���������� ��������
 * ������ connect(Properties, Properties) � ������ ��������� ���� ���=��������
 * ���������� ����: LIB.USE_CONN_POOL=LIB.TRUE_STR.
 * ��������! ������ ����� �������� ������ �������� ������ DBFacade
 * (������������ ���� �������)! ��������� ������������� ������� ������ ��
 * �������������!
 * @author Gusev Dmitry, dept. 019, RMRS. (c) 2006.
 * @version 1.0
 * @deprecated �� ������������� ������������� ������ ����� ��� �����������!
*/

class ConnectionManager
 {
  /** ����. ��������� ����� ���������� � ����. */
  private static final int MAX_CONNECTIONS = 20;

  /** ��������������� ����� ������� �������. */
  private static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
  /** ������������ ��������� ���������� � ����. */
  //private ConnectionConfig config = null;

  /** ������������ ��������� ������� ������(���������� ������� singleton). */
  private static ConnectionManager manager = null;

  /** ������������ (�����������) ��� ���������� � ����. */
  private static ArrayList<Connection> connectionsPool = null;

  /**
   * ����������� �� ��������� �������� ��� private ��� ����, ����� ���� ���������, ��� ����� �� ������ �������� ���
   * ���� ��������� ������� ������.
   * ����������� �������������� ���������-������ ��� ������� ������.
  */
  private ConnectionManager()
   {
    // ������������� �������
    InitLogger.initLogger(getClass().getName());
    logger.debug("WORKING ConnectionManager constructor.");
   }

  /**
   * ����� ������������ ��� ��������� ������ ������ ���������� ������ ConnectionManager, ������� ����������� � ���������
   * ���������� � ������������ �� �������������.�.�. � ������ ������ ���������� ������ ��� singleton (��������), ��
   * �������� ������ ��������� ������ ���� �� �� ��� ��� ������, � ��������� ������ ������������ ������ �� ��� ���������
   * ���������.
   * @return ConnectionManager ������ �� ������������ ��������� ������ ConnectionManager.
  */
  public static synchronized ConnectionManager getInstance()
   {
    // ���� �������� �� ��������������� - ��������������
    if(ConnectionManager.manager == null) ConnectionManager.manager = new ConnectionManager();
    logger.debug("WORKING ConnectionManager.getInstance(). Manager initialized.");
    return manager;
   }

  /**
   * ����� ���������� ������������ ���������� ���������� ��������� � ����.
   * @return int ����������� ���� ����������.
  */
  public int getPoolMaxCapacity()
   {
    logger.debug("WORKING ConnectionManager.getPoolCapacity(). RETURNED: " + ConnectionManager.MAX_CONNECTIONS);
    return ConnectionManager.MAX_CONNECTIONS;
   }

  /**
   * ���� ��� ���������� ���������������, �� ����� ���������� ���������� ����������, ������� ����������� � ����.
   * ���� ��� �� ���������������, ����� ���������� �������� -1.
   * @return ���������� ����������, ����������� � ����.
  */
  public int getCurrentPoolSize()
   {
    int res = -1;
    if (this.isPoolInitialized()) res = ConnectionManager.connectionsPool.size();
    logger.debug("WORKING ConnectionManager.getPoolSize(). Pool size: [" + res + "].");
    return res;
   }

  /**
   * ����� ���������� ������, ���� ��� ���������� ��������������� � ��� ������
   * ������ ���� (�.�. � ���� ���� ���� �� ���� ����������). ��� ���� ����������
   * useConnPool �� ����������� �� �������� (�.�. ������������ �������� ���������
   * ���� ����������, �������� �� ��, ������������ �� ��� ���������� ������
   * ����������� ������ ��� ���).
   * @return boolean ��������������� ��� ���������� ��� ���
  */
  private boolean isPoolInitialized()
   {
    boolean res = false;
    if ((ConnectionManager.connectionsPool != null) && (!ConnectionManager.connectionsPool.isEmpty())) res = true;
    logger.debug("WORKING  ConnectionManager.isPoolInitialized(). Result: " + res);
    return res;
   }

  /**
   * ��������� ����� ��� ����������������� ���������� �������� �� ��������
   * ���������� � ���� ��� ��� ������������� ���� ����������. ���� ������������
   * ��� ����������, �� ���������� ����� ���������� ���������� � ����, ���� ��
   * ��� - ���������� 0. ��� ������������� ���� ����� ���������� ����������� �
   * ��� �� ��� ���������� - �� ���������� MAX_CONNECTIONS ���������� � ����.
   * ����� ���������� ����� ���-�� ���������� ����� ���������� ID ����������
   * ���������� � ����.
  */
  public synchronized void openConnection(/** ConnectionConfig config */) throws Exception
   {
    /**
    logger.debug("ENTERING ConnectionManager.openConnection().");
    // ������� ��������� ������� ��� ���������� � ����
    Class.forName(config.getDbDriverClass()).newInstance();
    logger.debug("Database driver [" + config.getDbDriverClass() + "] loaded successfully.");
    // ���� ��� ���������� �� ��������������� - �������������
    if (!this.isPoolInitialized())
     {
      logger.debug("Connections pool not initialized yet. Initializing...");
      ConnectionManager.connectionsPool = new ArrayList<Connection>(MAX_CONNECTIONS);
     }
    else logger.debug("Connection pool already initialized.");
    // ���� � ���� ���-�� ���������� ������ ��������� - ������ �� ����� �������� ����� ���������� � ���
    if (connectionsPool.size() < MAX_CONNECTIONS)
     {
      logger.debug("Connection pool size OK [< " + ConnectionManager.MAX_CONNECTIONS + "]. Adding new connection...");
      if (config.getDbConnectionInfo() != null)
       ConnectionManager.connectionsPool.add(DriverManager.getConnection(config.getDbJdbcUrl(),
                                             Utils.getPropsFromString(config.getDbConnectionInfo())));
      else
       ConnectionManager.connectionsPool.add(DriverManager.getConnection(config.getDbJdbcUrl()));
     }
    else logger.debug("MAX pool size[] reached! Can't add connection!");
    logger.debug("LEAVING ConnectionManager.openConnection().");
    */
   }

  /**
   * 123
   * @throws Exception
   * @return int
  */
  /*
  public int connect(Properties props) throws Exception
   {
    logger.debug("ENTERING connect_pool(Properties props).");
    // �������� �� ��������� {-1} - ��� ��� �������� � ����� ���������� �� ���������
    int conn_number = -1;
      logger.debug("Using connection pool - continue.");
      if (this.isPoolInitialized()) // <- ��� ���������������
       {
        logger.debug("Connection pool already initialized. Trying to add connection.");
        // ����� ���������� � ��� ���������, ������ ���� ��������� ������ ����
        if (this.getPoolSize() < this.MAX_CONNECTIONS)
         {
          logger.debug("Connection pool size < MAX, adding new connection.");
          openConnection(props);
          conn_number = this.getPoolSize() - 1;
          logger.debug("Get number of last connection [#" + conn_number + "].");
         }
        else
         logger.debug("Connection pool size = MAX, connection not added.");
       }
      else // <- ��� �� ���������������
       {
        logger.debug("Connection pool is not initialized. Initializing.");
        openConnection(props);
        conn_number = this.getPoolSize() - 1;
        logger.debug("Get number of last connection [#" + conn_number + "].");
       }
    logger.debug("LEAVING connect_pool(Properties props).");
    return conn_number;
   }
  */
  /**
   * ����� ���������� ������, ���� ���������� ����� conn_number
   * ���������� � ���� ���������� � ��� ���������� ������������.
   * @param conn_number int ����� ����������, ������� ��������
   * �� ����� ��������� � ����.
   * @return boolean ��������� �������� ������� ���������� � ����.
  */
  /*
  public boolean isConnExist(int conn_number)
   {
    boolean res = false;
    logger.debug("ENETRING isConnExist(int conn_number).");
    if (this.isPoolInitialized() && (conn_number >= 0) && (conn_number <= this.getPoolSize()))
     res = true;
    logger.debug("[Result: " + res + "]");
    logger.debug("LEAVING isConnExist(int conn_number).");
    return res;
   }
  */

  /**
   * ����� ���������� ������ ���������� (Connection) ����� conn_number
   * �� ���� ����������. ���� �� ��� �� ������������ (useConnPool=false),
   * �� ��������������� (isPoolInitialized()=false) ��� ���������� ���
   * ���� ������� � ���� ��� (isConnExist()=false), �� ������ �����
   * ���������� �������� null.
   * //@param conn_number int ����� ����������, ������� �� �����
   * �������� �� ����.
   * @return Connection ������ ��� ������� conn_number �� ����
   * ���������� ��� null, � ������ �������.
  */
  /*
  public Connection getConn(int conn_number)
   {
    logger.debug("ENTERING getConn(int conn_number).");
    Connection conn = null;
    if (this.isConnExist(conn_number))
     {
      conn = (Connection) ConnectionManager.connectionsPool.get(conn_number);
      logger.debug("Connection pool initialized and used.");
      logger.debug("Connection #" + conn_number + " exists in the pool.");
     }
    else
     logger.debug("Conn. pool not used or not initialized or conn #" +
                  conn_number + " not exists in the pool!");
    logger.debug("LEAVING getConn(int conn_number).");
    return conn;
   }
  */
  /**
   * ��������� ���������� � ����. ���� ������������ ��� ����������, ��
   * ����������� � ��������� �� ���� ��������� ����������� ���������� (�.�.
   * ����������, ������� ���������� (���������) ���������� �����). �����
   * �������� ���������� (�������������, ����������� � ���� ����������), ����
   * ������������� �������� null. � ������, ���� ��� �� ��������������� ( =
   * null), ��� � ���� ��� ����������, ����� �� ��������� ������� ��������. ����
   * �� ��� ���������� �� ������������, �� ����� ���������� �����������
   * ����������, ������������ ������ �������� ��� ������ � ����.
   *
   * @throws SQLException
  */
  public void close() throws SQLException
   {
    logger.debug("ENTERING close().");
      logger.debug("Using connection pool - closing all connections in the pool.");
      // ���� ��� ���������� ��������������� - ��������
      // �� ���� �����������, ��������� �� � �������� ������
      if (this.isPoolInitialized())
       {
        Connection tmp_conn = null;
        logger.debug("Connection pool initialized [size=" + ConnectionManager.connectionsPool.size() + "].");
        for (int i = 0; i < ConnectionManager.connectionsPool.size(); i++)
         {
          logger.debug("Closing connection #" + i);
          // ��������� ���������� �� ����
          tmp_conn = (Connection) ConnectionManager.connectionsPool.get(i);
          // ��������� ����������
          tmp_conn.close();
          // �������� ������ �� ����������
          tmp_conn = null;
          logger.debug("Connection #" + i + " closed successfully.");
         }
        logger.debug("Clearing connection pool.");
        ConnectionManager.connectionsPool.clear();
        logger.debug("Clearing the pool completed successfully.");
       }
      else logger.debug("Connection pool not initialized - nothing to close!");
    logger.debug("LEAVING close().");
   }

  /***/
  public void close(int conn_number) throws SQLException
   {
    logger.debug("ENTERING close(int i).");
    logger.debug("Using connection pool. Closing connection #" + conn_number + " in pool.");

    logger.debug("LEAVING close(int i).");
   }

  /**
   ����� main ������������ ������ ��� ������������ ������ ConnectionManager.
   @param args ����� ��������� ����������.
  */
  public static void main(String[] args)
   {
    Logger logger                   = Logger.getLogger(ConnectionManager.class.getName());
    PatternLayout patternLayout     = new PatternLayout(Consts.LOGGER_LOG_FORMAT_DEFAULT);
    ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
    logger.addAppender(consoleAppender);

    ConnectionManager conn = ConnectionManager.getInstance();

    logger.info("********** STARTING main() **********");

    Properties props = new Properties();

    //props.put(DBConsts.DB_TYPE, DBConsts.DBTYPE_INFORMIX);
    props.put(DBConsts.DB_HOST,   "10.1.19.30:1526");
    props.put(DBConsts.DB_SERVER, "edu");
    props.put(DBConsts.DB_NAME,     "flt");
    props.put(DBConsts.DB_USER,   "informix");
    props.put(DBConsts.DB_PWD,    "123456");

    //props.put(DBC.DB_TYPE, DBC.DBTYPE_DBF);
    //props.put(DBC.DB_NAME, "c:/temp/");
    try
     {
      //conn.connect(props);
      //conn.print_config();
      conn.close();
     }
    catch (Exception e)
     {logger.fatal("REASON: " + e.getMessage());}


    logger.info("********** FINISHING main(). **********");
   }

 }//--> ����� ������ ConnectionManager.
