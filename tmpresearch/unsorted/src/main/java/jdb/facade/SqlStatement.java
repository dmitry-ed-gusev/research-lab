package jdb.facade;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * �����, ����������� ���������������� ������� Statement.
 * @deprecated �� ������������� ������������� ������ ����� ��� �����������!
*/

class SqlStatement
 {
  /** �������� ������� ��� ������� ������. */
  private static String          logger_name     = "SqlStatement";
  /** ��������������� ����� ������� �������. */
  private static Logger          logger          = null;

  /** ���� ��� �������� ����������� ������� Connection - ���������� � ����. */
  private Connection conn          = null;
  /** ���� ��� �������� ����������� ������� Statement. */
  private Statement stmt           = null;
  /** ���������� ���� ��� �������� SQL-�������. */
  private String sql               = null;
  /***/
  private int resultSetType        = -1;
  /***/
  private int resultSetConcurrency = -1;

  /***/
  //private SqlStatement() {}

  /**
   * ����������� �� ���������. ��������� ������ ������������� ������� ���
   * ������� ������ (���������� ���������� ��� ������ ��������� � �������). ����
   * ������ ��� ����� ��������� ��� ������ ��������� - ������ �� ������.
   *
   * @param parent_logger_name String
  */
  public SqlStatement(String parent_logger_name)
   {
    if ((parent_logger_name != null) && (!parent_logger_name.trim().equals("")))
     if (SqlStatement.logger == null)
      {
       SqlStatement.logger = Logger.getLogger(parent_logger_name + "." +
                               SqlStatement.logger_name);
       logger.debug("Logger initialization completed OK.");
      }
     else
      logger.debug("Logger already initialized!");
   }

  /**
   * ������������� ������� Statement.
   *
   * @throws SQLException
  */
  private void initStatement () throws SQLException
   {
    logger.debug("ENTERING initStatement().");
    // ���� ��� sql-�������(���� sql �����) - ����� �� �����
    if (this.sql == null)
     {
      //logger.error(DBConsts.DBCONN_ERR_EMPTY_SQL);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_EMPTY_SQL);
     }
    // ���� ���� �� ������� ���������� ���� - ������� ��������� ������
    // Statement � ����������� �� ���������
    if ((this.resultSetType == -1) || (this.resultSetConcurrency == -1))
     {
      logger.debug("ResultSetType or ResultSetConcurrency is empty.");
      logger.debug("Creating easy statement (without parameters).");
      this.stmt = conn.createStatement();
     }
    else
     {
      logger.debug("ResultSetType or ResultSetConcurrency is not empty.");
      logger.debug("Creating statement with parameters.");
      // ������� ��������� ������ Statement � ����������� �����������
      this.stmt = conn.createStatement(this.resultSetType, this.resultSetConcurrency);
     }
    logger.debug("LEAVING initStatement().");
   }

  /**
   * ����� ��������� ������ SqlStatement � ����������� ��� �������� null.
   * @throws SQLException
  */
  public void close() throws SQLException
   {
    logger.debug("ENTERING close().");
    if(this.stmt != null)
     {
      logger.debug("Statement object not null. Closing statement.");
      this.stmt.close(); this.stmt = null;
      logger.debug("OK. Statement object closed.");
     }
    else
     logger.debug("Statement object is null, nothing to close.");
    logger.debug("LEAVING close().");
   }

  /**
   * ����� ������������� �������� ���� sql ������� �������. �����
   * ����� ������ SqlStatement ������������������.
   * @throws SQLException
   * @param  sql ������ � SQL-�������� ��� ����������.
  */
  public void setSql(String sql) throws SQLException
   {
    logger.debug("ENTERING setSql(String sql).");
    logger.debug("[SQL:" + sql + "]");
    this.sql = sql;
    logger.debug("Initializing SqlStatement field. Calling initStatement().");
    stmt = null;
    this.initStatement();
    logger.debug("LEAVING setSql(String sql).");
   }

  /**
   *
   * @throws SQLException
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
  */
  public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING setSql(String sql, int resultSetType, int resultSetConcurrency).");
    logger.debug("[resultSetType:" + resultSetType + "]");
    logger.debug("[resultSetConcurrency:" + resultSetConcurrency + "]");
    this.resultSetType        = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("LEAVING setSql(String sql, int resultSetType, int resultSetConcurrency).");
   }

  /**
   * ����� ��� ������� � �������� ���� sql ������� �������.
   * @return �������� ���� sql.
  */
  public String getSql()
   {
    logger.debug("ENTERING getSql().");
    logger.debug("[returning sql:" + this.sql + "]");
    logger.debug("LEAVING getSql().");
    return this.sql;
   }

  /**
   * ��������� SQL-������, ���������� � �������� ��������� � ����������
   * ������ ResultSet � �������������� ������� �������. ������ �����
   * ��������� ��������� ������ SELECT �������, �.�. �������, ������������
   * �������������� ����� ������� (ResultSet).
   * @throws SQLException
   * @param  sql ������ � SQL-�������� ��� ����������.
   * @return ResultSet ����� �������, ���������� � ����������
   * ���������� sql-�������.
  */
  public ResultSet executeQuery(String sql) throws SQLException
   {
    ResultSet rs;
    logger.debug("ENTERING executeSelectQuery(String sql).");
    logger.debug("[SQL:" + sql + "].");
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("Calling executeSelectQuery().");
    rs = this.executeQuery();
    logger.debug("LEAVING executeSelectQuery(String sql).");
    return rs;
   }

  /**
   * 123
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
   * @throws SQLException
   * @return ResultSet
  */
  public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(String sql, int resultSetType, int resultSetConcurrency).");
    logger.debug("[SQL:" + sql + "]");
    logger.debug("[resultSetType:" + resultSetType + "]");
    logger.debug("[resultSetConcurrency:" + resultSetConcurrency + "]");
    logger.debug("Calling setSql(String sql, int resultSetType, int resultSetConcurrency).");
    this.setSql(sql,resultSetType,resultSetConcurrency);
    logger.debug("Calling executeSelectQuery().");
    ResultSet rs = this.executeQuery();
    logger.debug("LEAVING executeSelectQuery(String sql, int resultSetType, int resultSetConcurrency).");
    return rs;
   }

  /**
   * ��������� SQL-������, ������� �������� � ���� sql ������ ������� �
   * ���������� ������ ResultSet � ������� �������. ������ ����� ���������
   * ��������� ������ SELECT �������, �.�. �������, ������������ ������. ����
   * ������ �� ���������������, �� ������������ ��.
   *
   * @throws SQLException
   * @return ResultSet ����� �������, ���������� � ���������� ����������
   *   sql-�������.
  */
  public ResultSet executeQuery() throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery().");
    // �������� ������������� ����, ��������� ������ SqlStatement
    if(this.stmt == null)
     {
      //logger.debug(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
     }
    else
     logger.debug("stmt field is OK. Continue.");
    logger.debug("Calling executeSelectQuery(String sql).");
    ResultSet rs = this.stmt.executeQuery(this.sql);
    logger.debug("LEAVING executeSelectQuery().");
    return rs;
   }

  /**
   * ��������� SQL-������, ���������� � �������� ��������� � ����������
   * ���, ��������������� � ������� ���������� �������. ������ ����� ���������
   * ��������� ������ UPDATE, INSERT ��� DELETE �������.
   * @throws SQLException
   * @param  sql ������ � �������� ��� ����������.
   * @return ��� ���������, ���������� �� ���� ����� ���������� �������.
  */
  public int execute(String sql) throws SQLException
   {
    logger.debug("ENTERING execute(String sql).");
    logger.debug("[SQL:" + sql + "]");
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("Calling execute().");
    int res = this.execute();
    logger.debug("LEAVING execute(String sql).");
    return res;
   }

  /**
   * ��������� SQL-������, ������� �������� � ���� sql ������� ������� � ����������
   * ���, ��������������� � ������� ���������� �������. ������ ����� ���������
   * ��������� ������ UPDATE, INSERT ��� DELETE �������. ���� ������ ��
   * ���������������, �� ������������ ��.
   * @throws SQLException
   * @return ��� ���������, ���������� �� ���� ����� ���������� �������.
  */
  public int execute() throws SQLException
   {
    logger.debug("ENTERING execute().");
    if(this.stmt == null)
     {
      //logger.error(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
     }
    else
     logger.debug("stmt field is OK. Countinue.");
    logger.debug("Executing executeUpdate(String sql) method.");
    int count = this.stmt.executeUpdate(sql);
    logger.debug("LEAVING execute().");
    return count;
   }

  /**
   * ������������� �������� ���� Connection �� ���������� � ����.
   * @param conn ������ Connection, ���������� ������� ���������� � ����.
  */
  public void setConnection(Connection conn)
   {
    logger.debug("ENTERING setConnection(Connection conn).");
    this.conn = conn;
    logger.debug("LEAVING setConnection(Connection conn).");
   }

 }//--> ����� ������� SqlStatement.
