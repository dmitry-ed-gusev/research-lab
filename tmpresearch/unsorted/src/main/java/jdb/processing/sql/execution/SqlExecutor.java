package jdb.processing.sql.execution;

import jdb.config.connection.BaseDBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ����� �������� ����������� ������ ��� ���������� ��������� sql-�������� (�����). 
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 21.06.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class SqlExecutor
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(SqlExecutor.class.getName());

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ����� ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql String ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, String sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    logger.debug("executeSelectQuery SQL [" + sql + "].");
    // ���� ��� �������� ������ ���������� - ������!
    if (connection == null) {throw new DBConnectionException("Connection to DBMS is empty!");}
    // ���� ��� ������� ������ ������ - ������!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty sql-query!");}
    // ���� ������ ��������� ���� - �� SELECT-������ - ������!
    if (!DBUtils.isSelectQuery(sql)) {throw new SQLException("No SELECT-query for this method!");}
    // ���� �� ��� � ������� - ��������� ������ � ���������� ���������.
    Statement statement = connection.createStatement();
    // ��� ������� ������� �������� ������
    String realSql;
    if (useSqlFilter) {realSql = SqlFilter.removeDeprecated(sql);}
    else              {realSql = sql;}
    logger.debug("Real sql-query for execution: [" + realSql + "].");
    // ��������� ������
    return statement.executeQuery(realSql);
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql String ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, String sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ����� ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuffer ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException 
   {
    // �������� �� null-�������� ����������, �.�. ���� sql==null, �� ����������� sql.toString() ����� �������� ��
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeSelectQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuffer ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuffer sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ����� ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuilder ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeSelectQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ ResultSet. ���� ��������� ����������
   * ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� ����� ���������� ��� ������������� ����������,
   * ������� �� ����������� ����� ���������� �������. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuilder ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return ResultSet ������, ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuilder sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��� ������������� ����������, ������� �� ����������� ����� ���������� �������. ��� ������� � ����������
   * (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. ����� ����� ������������ ����������
   * sql-�������� (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql String ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static String executeStrSelectQuery(Connection connection, String sql, boolean useSqlFilter)
   throws DBConnectionException, SQLException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    // �������� ������ (��� �� ���� ������ �� ����������)
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
    // ����������� ������ � ������ � ����������
    return DBUtils.getStringResultSet(rs);
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��� ������������� ����������, ������� �� ����������� ����� ���������� �������. ��� ������� � ����������
   * (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. ����� ������ ���������� ����������
   * sql-��������.
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql String ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static String executeStrSelectQuery(Connection connection, String sql)
   throws DBConnectionException, SQLException {return SqlExecutor.executeStrSelectQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��� ������������� ����������, ������� �� ����������� ����� ���������� �������. ��� ������� � ����������
   * (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. ����� ����� ������������ ����������
   * sql-�������� (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuffer ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static String executeStrSelectQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws DBConnectionException, SQLException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    // �������� ������ (��� �� ���� ������ �� ����������)
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
    // ����������� ������ � ������ � ����������
    return DBUtils.getStringResultSet(rs);
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��� ������������� ����������, ������� �� ����������� ����� ���������� �������. ��� ������� � ����������
   * (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. ����� ������ ���������� ����������
   * sql-��������.
   * @param connection Connection ���������� � ����, ����� ������� ��������� ������. ���� ���������� �����, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql StringBuffer ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
  */
  public static String executeStrSelectQuery(Connection connection, StringBuffer sql)
   throws DBConnectionException, SQLException {return SqlExecutor.executeStrSelectQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��������� � ���������� ������������ ���������� - ����� ����������� ������� ����������� � ��.
   * ��� ������� � ���������� (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. �����
   * ����� ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param config BaseDBConfig ������������ ���������� � ����. ���� ������������ ����� ��� �������, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
   * @throws jdb.exceptions.DBModuleConfigException �������� ��� ������ ������ ��� ���������� � ����.
  */
  public static String executeStrSelectQuery(BaseDBConfig config, String sql, boolean useSqlFilter)
   throws SQLException, DBModuleConfigException, DBConnectionException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    String result = null;

    // ���� ��� ������� ������ � ��������, ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ���� �� ������ � ������� - ������� ���������� ���������
    else {logger.debug("DBMS connection config is OK. Processing.");}

    // ���� ��� ������� ������ ������ - ������!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty query!");}
    // ���� ������ ��������� ���� - �� SELECT-������ - ������!
    if (!DBUtils.isSelectQuery(sql)) {throw new SQLException("No SELECT-query for this method!");}
    // ���� �� ��� � ������� - ��������� ������ � ���������� ���������.
    Connection connection = null;
    try
     {
      // �������� ����������
      connection = DBUtils.getDBConn(config);
      // �������� ������ (��� �� ���� ������ �� ����������)
      ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
      // ����������� ������ � ������
      result = DBUtils.getStringResultSet(rs);
     }
    // ���� ���� ������ ���������� �������� - ���������� ���������� ���������� �������!
    finally {if (connection != null) {connection.close();}}
    return result;
   }

  /**
   * ����� ��������� ���� "SELECT..." ������ � ���������� �������������� ������ (ResultSet) � ��������� ���� (String).
   * ���� ��������� ���������� ������� ����� ����, �� ����� ������ �������� null. ��� ���������� ������� �����
   * ���������� ��������� � ���������� ������������ ���������� - ����� ����������� ������� ����������� � ��.
   * ��� ������� � ���������� (������������ ���������� � ���� ��� �������� sql-�������) ����� ������������� ��. �����
   * ������ ���������� ���������� sql-��������.
   * @param config BaseDBConfig ������������ ���������� � ����. ���� ������������ ����� ��� �������, �� ������
   * �������� �� �����, � ����� ������������� ��.
   * @param sql ����������� SELECT-������. ���� ������ �� SELECT-����, �� ����� ������ ������ (��)!
   * @return String ������ (� ��������� ����), ���������� � ���������� ������� �� �� ��� �������� null.
   * @throws SQLException ������ ��� �������� ������� Statement ��� ��������� ���������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ���� (���������� �����).
   * @throws jdb.exceptions.DBModuleConfigException �������� ��� ������ ������ ��� ���������� � ����.
  */
  public static String executeStrSelectQuery(BaseDBConfig config, String sql)
   throws SQLException, DBModuleConfigException, DBConnectionException {return SqlExecutor.executeStrSelectQuery(config, sql, true);}
  
  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� � ������� config ���������� � ����. ����� �����
   * ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param config BaseDBConfig ������ ��� ���������� � ����.
   * @param sql ����������� ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(BaseDBConfig config, String sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("executeUpdate: executing query [" + sql + "].");
    int result = 0;

    // ���� ��� ������� ������ � ��������, ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ���� �� ������ � ������� - ������� ���������� ���������
    else {logger.debug("DBMS connection config is OK. Processing.");}
    
    // ���� ��� ������� ������ ������ - ������!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty query!");}
    // ���� ������ ��������� ���� - SELECT-������ - ������!
    if (DBUtils.isSelectQuery(sql)) {throw new SQLException("SELECT-query for this method is not allowed!");}
    // ���� ��� � ������� - ��������� ������
    Connection connection = null;
    try
     {
      // �������� ����������
      connection = DBUtils.getDBConn(config);
      Statement statement = connection.createStatement();
      // ��������������� ��������� ������
      if (useSqlFilter) {result = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
      else              {result = statement.executeUpdate(sql);}
     }
    // ���� ���� ������ ���������� �������� - ���������� ���������� ���������� �������!
    finally {if (connection != null) {connection.close();}}
    // ���������� ��������� ���������� �������
    return result;
   }

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� � ������� config ���������� � ����. ����� ������
   * ���������� ���������� sql-��������.
   * @param config BaseDBConfig ������ ��� ���������� � ����.
   * @param sql ����������� ������.
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(BaseDBConfig config, String sql)
   throws SQLException, DBConnectionException, DBModuleConfigException {return SqlExecutor.executeUpdateQuery(config, sql, true);}

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� � ������� config ���������� � ����. ����� �����
   * ������������ ���������� sql-�������� (��. �������� useSqlFilter).
   * @param config BaseDBConfig ������ ��� ���������� � ����.
   * @param sql StrinBuilder ����������� ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(BaseDBConfig config, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(config, sql.toString(), useSqlFilter);}
   }

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� � ������� config ���������� � ����. ����� ������
   * ���������� ���������� sql-�������� (��. �������� useSqlFilter).
   * @param config BaseDBConfig ������ ��� ���������� � ����.
   * @param sql ����������� ������.
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(BaseDBConfig config, StringBuilder sql)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return SqlExecutor.executeUpdateQuery(config, sql, true);}

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ����� ������������ ���������� sql-��������
   * (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����.
   * @param sql String ����������� ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, String sql, boolean useSqlFilter) 
   throws SQLException, DBConnectionException
   {
    logger.debug("executeUpdate: executing query [" + sql + "].");
    // ���� ��� �������� ������ ���������� - ������!
    if (connection == null) {throw new DBConnectionException("Connection to DBMS is empty!");}
    // ���� ��� ������� ������ ������ - ������!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty sql-query!");}
    // ���� ������ ��������� ���� - SELECT-������ - ������!
    if (DBUtils.isSelectQuery(sql)) {throw new SQLException("SELECT-query for this method is not allowed!");}
    // ���� ��� � ������� - ��������� ������
    Statement statement = connection.createStatement();
    int result;
    if (useSqlFilter) {result = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
    else              {result = statement.executeUpdate(sql);}
    return result;
   }

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����.
   * @param sql String ����������� ������.
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, String sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ����� ������������ ���������� sql-��������
   * (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����.
   * @param sql StringBuffer ����������� ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    // �������� �� null-�������� ����������, �.�. ���� sql==null, �� ����������� sql.toString() ����� �������� ��
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����.
   * @param sql StringBuffer ����������� ������.
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, StringBuffer sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ����� ������������ ���������� sql-��������
   * (��. �������� useSqlFilter).
   * @param connection Connection ���������� � ����.
   * @param sql StringBuilder ����������� ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-������� ����� �����������
   * (true=������������/false=�� ������������).
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    // �������� �� null-�������� ����������, �.�. ���� sql==null, �� ����������� sql.toString() ����� �������� ��
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * ����� ��������� ���� ��-"SELECT..." ������ ��� ���������� ���������� � ����. ���� ���������� ����� - ���������
   * �� DBConnectionException - ������ ���������� � ����. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ���������� � ����.
   * @param sql StringBuilder ����������� ������.
   * @return int ��������� ���������� �������.
   * @throws java.sql.SQLException ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
  */
  public static int executeUpdateQuery(Connection connection, StringBuilder sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}
  
 }