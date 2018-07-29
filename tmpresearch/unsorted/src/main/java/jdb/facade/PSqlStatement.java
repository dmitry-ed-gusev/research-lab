package jdb.facade;

import jdb.exceptions.SqlStatementException;
import jlib.common.Consts;
import org.apache.log4j.*;

import java.sql.*;

/**
 �����, ����������� ���������������� ������� PreparedStatement
 (������� �������������� ��� ���������� SQL-������).
 * @deprecated �� ������������� ������������� ������ ����� ��� �����������!
*/

class PSqlStatement
 {
  /** ���������� ������ ������� ������� (�� ������ �������). */
  private static PatternLayout   patternLayout   = new PatternLayout(Consts.LOGGER_LOG_FORMAT_DEFAULT);
  /** ����� ��� ������ ��������� ������� �� ������� (ConsoleAppender). */
  private static ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
  /** ����� ��� ������ ��������� ������� � ���� (FileAppender). */
  private static FileAppender    fileAppender    = null;
  /** �������� ����� ��� ������� ������� ������. */
  private static String          log_file        = PSqlStatement.class.getName();
  /** ��������������� ����� ������� �������. */
  private static Logger          logger          = Logger.getLogger(PSqlStatement.class);
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
  private static Level log_level = Level.ERROR;

  /** ���� ��� �������� ����������� ������� Connection - ���������� � ����. */
  private Connection conn          = null;
  /** ���� ��� �������� ����������� ������� PreparedStatement. */
  private PreparedStatement pstmt  = null;
  /** ���������� ���� ��� �������� SQL-�������. */
  private String sql               = null;
  /***/
  private int resultSetType        = -1;
  /***/
  private int resultSetConcurrency = -1;

  /**
   * ����������� �� ���������. ��������� ������ ������������� ������� ���
   * ������� ������ (���������� ���������� ��� ������ ��������� � �������).
   * ���� ������ ��� ����� ��������� ��� ������ ��������� - ������ �� ������.
  */
  public PSqlStatement()
   {
    // ������������� ������� ��� ��������� ����
    logger.setLevel(PSqlStatement.log_level);
    // ��������� ������������ ����������� ������ ��������� (Appenders)
    if (logger.getAdditivity()) logger.setAdditivity(false);
    // ���� � ������� ��� ��� �� ������ ���������� ��� ������ (Appender),
    // �� ��������� ���� ���������
    if (!logger.getAllAppenders().hasMoreElements())
     {
      // ��������� ��� ������ �� �������
      logger.addAppender(consoleAppender);
      try
       {
        // ��������� ��� ������ � ���� <- ���� �� ����������
        fileAppender = new FileAppender (patternLayout, log_file, false);
        logger.addAppender(fileAppender);
       }
      catch (Exception e)
       {
        logger.error("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
        //throw new Exception("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
       }
      // ����� ���������� ����������, �� ����� ������ ����� ������
      logger.debug("Logger [" + Logger.class + "] initialized OK.");
      logger.debug("ConsoleAppender was added for [" + Logger.class + "].");
      logger.debug("FileAppender was added for [" + Logger.class + "].");
     }
   }

 /**
  * ������������� ������� Statement.
  *
  * @throws SQLException
  */
 private void initStatement() throws SQLException
   {
    // Only initialize PrepareStatement if member sql is not null.
    if(sql == null) throw new SqlStatementException(/**DBConsts.DBCONN_ERR_EMPTY_SQL*/);
    pstmt = conn.prepareStatement(sql);
   }
  /**
   ��������� PreparedStatement ������.
   @throws SQLException
  */
  public void close() throws SQLException
   {if(pstmt != null) {pstmt.close(); pstmt = null;}}

 /**
  * ������������� �������� ���� sql ������� ������� � ������������������ ������
  * PreparedStatement.
  *
  * @param sql String
  * @throws SQLException
  */
 public void setSql(String sql) throws SQLException
   {this.sql = sql; pstmt = null; initStatement();}

 public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    this.resultSetType        = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
    this.setSql(sql);
   }

 /**
  ���������� ������� SQL-������.
  @return sql-������.
 */
 public String getSql() {return sql;}

 /**
  * ��������� ����������� SQL-������ �� ������� ������(SELECT), ���������� �
  * �������� ���������.
  *
  * @param sql SQL-������ ��� ����������
  * @return ������ ResultSet � ������� ��������� ������.
  * @throws SQLException
  */
 public ResultSet executeQuery(String sql) throws SQLException
  {setSql(sql); return executeQuery();}

 public ResultSet executeQuery(String sql,int resultSetType, int resultSetConcurrency)
  throws SQLException
   {this.setSql(sql,resultSetType,resultSetConcurrency); return executeQuery();}

 /**
  * ��������� ����������� SQL-������ (SELECT), ������������� � �������� ��������
  * ���� sql ������� ������.
  *
  * @return ������ ResultSet � ������� ��������� ������.
  * @throws SQLException
  */
 public ResultSet executeQuery() throws SQLException
  {
   //Check to see if pstmt statement is null.
   if(pstmt == null)
    throw new SqlStatementException(/**DBConsts.DBCONN_ERR_PSQLSTMT_INIT*/);
   return pstmt.executeQuery();
  }

 /**
  * ��������� ����������� SQL-������� UPDATE, INSERT, ��� DELETE, ���������� �
  * �������� ��������.
  *
  * @param sql SQL-������ ��� ����������.
  * @return �������� ���� int - ��� ������ ������������� �������.
  * @throws SQLException
  */
 public int execute(String sql) throws SQLException
  {setSql(sql); return execute();}

 /**
  * ��������� ����������� SQL-������� UPDATE, INSERT, ��� DELETE, �������������
  * � �������� �������� ���� sql ������� ������.
  *
  * @return �������� ���� int - ��� ������ ������������� �������.
  * @throws SQLException
  */
 public int execute() throws SQLException
 {
  if(pstmt == null) throw new SqlStatementException(/**DBConsts.DBCONN_ERR_PSQLSTMT_INIT*/);
  return pstmt.executeUpdate();
 }
 /**
  ������������� ������ Connection �� ����������� ���������� � ����.
  @param conn ��������� �� ���������� � ����.
 */
 public void setConnection(Connection conn) {this.conn = conn;}
 /**
  ������������� �������� ���� String � �������������� sql-�������(PreparedStatement).
  @throws SQLException
  @param index ��������� ��������� � �������������� �������
  @param value �������� ��������� ��� �����������
 */
 public void setString(int index, String value) throws SQLException
  {pstmt.setString(index,value);}
 /**
  ������������� �������� ���� int � �������������� sql-�������(PreparedStatement).
  @throws SQLException
  @param index ��������� ��������� � �������������� �������
  @param value �������� ��������� ��� �����������
 */
 public void setInt(int index, int value) throws SQLException
  {pstmt.setInt(index,value);}
 /**
  ������������� �������� ���� Double � �������������� sql-�������(PreparedStatement).
  @throws SQLException
  @param index ��������� ��������� � �������������� �������
  @param value �������� ��������� ��� �����������
 */
 public void setDouble(int index, double value) throws SQLException
  {pstmt.setDouble(index,value);}
 /**
  ������������� �������� ���� Date � �������������� sql-�������(PreparedStatement).
  @throws SQLException
  @param index ��������� ��������� � �������������� �������
  @param value �������� ��������� ��� �����������
 */
 public void setDate(int index, Date value) throws SQLException
  {pstmt.setDate(index,value);}
}//--> ����� ������� PSqlStatement
