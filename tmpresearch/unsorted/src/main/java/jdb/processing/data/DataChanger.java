package jdb.processing.data;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.processing.data.helpers.DataProcessingHelper;
import jdb.processing.sql.execution.SqlExecutor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ����� ��������� ��������� ������� ��� ��������� �������� ��������� ����� � �������� ��� ������. ������ ������
 * ��������� ��������� �������� ����� ���� ��� ������� ������ - ���������� �������� ���/���� ����� � ��������������
 * ������� ����.
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 30.09.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class DataChanger
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DataChanger.class.getName());

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setIntValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ��������� ������
    String sql = "update " + tableName + " set " + dataFieldName + " = " + dataFieldValue + " where " +
                 keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������
    return SqlExecutor.executeUpdateQuery(connection, sql);
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setIntValue(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ��������� ������
    String sql = "update " + tableName + " set " + dataFieldName + " = " + dataFieldValue + " where " + keyFieldName;
    // ���� �������� ��������� ���� �� ����� - ����������� ��� � ������ ��� ����
    if (!StringUtils.isBlank(keyFieldValue)) {sql = sql + " = '" + keyFieldValue + "'";}
    // ���� �� �������� ��������� ���� ����� - ��� ���� ���������� � ������ ��-�������
    else                                     {sql += " is null";}
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������
    return SqlExecutor.executeUpdateQuery(connection, sql);
   }
  
  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, DBModuleConfigException, ��������� ������� ��������� ��������� ������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setIntValue(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ����������
      connection = DBUtils.getDBConn(config);
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setIntValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, DBModuleConfigException, ��������� ������� ��������� ��������� ������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setIntValue(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ����������
      connection = DBUtils.getDBConn(config);
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setIntValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��������� �������� ������ (DataSource) ��� ���������� � ����. ��� ������� � ������ ������ -
   * ��������� �� SQLException � DBConnectionException, ��������� ������� ��������� ��������� ������.
   * @param dataSource DataSource ������������ �������� ������ ��� ��������� ���������� � ��.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setIntValue(DataSource dataSource, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ���������� (���� �������� ������ �� ����)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setIntValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��������� �������� ������ (DataSource) ��� ���������� � ����. ��� ������� � ������ ������ -
   * ��������� �� SQLException � DBConnectionException, ��������� ������� ��������� ��������� ������.
   * @param dataSource DataSource ������������ �������� ������ ��� ��������� ���������� � ��.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue int ����� ������������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setIntValue(DataSource dataSource, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ���������� (���� �������� ������ �� ����)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setIntValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������. ����� ����� ������������ ���������� sql-��������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-�������.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setStringValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, String dataFieldValue, boolean useSqlFilter) throws SQLException, DBConnectionException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ��������� ������. 
    String sql = "update " + tableName + " set " + dataFieldName + " = ";
    // ���� �������� ���� ������ �� ����� - ����������� ��� � ������ ��� ����
    if (!StringUtils.isBlank(dataFieldValue))
     {
      // ���� ���������� ������ sql-��������, �� � ��������� �������� �������� ������� �� "�����������" (������� -> ").
      // "�����������" ������� ����� ������ �� ����� ���������� ����� sql-�������.
      if (useSqlFilter) {sql = sql + "'" + SqlFilter.changeQuotes(dataFieldValue) + "'";}
      else              {sql = sql + "'" + dataFieldValue + "'";}
     }
    // ���� �� �������� ��������� ���� ����� - ��� ���� ���������� � ������ ��-�������
    else                                     {sql += " null";}
    // ������������� ��������� sql-�������
    sql = sql + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������. ������ ����������� ��� ����������.
    return SqlExecutor.executeUpdateQuery(connection, sql, useSqlFilter);
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setStringValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException
   {return DataChanger.setStringValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);}

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������. ����� ����� ������������ ���������� sql-��������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-�������.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setStringValue(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, String dataFieldValue, boolean useSqlFilter) throws SQLException, DBConnectionException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ��������� ������.
    String sql = "update " + tableName + " set " + dataFieldName + " = ";
    // ���� �������� ���� ������ �� ����� - ����������� ��� � ������ ��� ����
    if (!StringUtils.isBlank(dataFieldValue))
     {
      // ���� ���������� ������ sql-��������, �� � ��������� �������� �������� ������� �� "�����������" (������� -> ").
      // "�����������" ������� ����� ������ �� ����� ���������� ����� sql-�������.
      if (useSqlFilter) {sql = sql + "'" + SqlFilter.changeQuotes(dataFieldValue) + "'";}
      else              {sql = sql + "'" + dataFieldValue + "'";}
     }
    // ���� �� �������� ��������� ���� ����� - ��� ���� ���������� � ������ ��-�������
    else                                     {sql += " null";}
    // ������������� ��������� sql-�������
    sql = sql + " where " + keyFieldName;
    // ���� �������� ��������� ���� �� ����� - ����������� ��� � ������ ��� ����
    if (!StringUtils.isBlank(keyFieldValue)) {sql = sql + " = '" + keyFieldValue + "'";}
    // ���� �� �������� ��������� ���� ����� - ��� ���� ���������� � ������ ��-�������
    else                                     {sql += " is null";}
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������
    return SqlExecutor.executeUpdateQuery(connection, sql, useSqlFilter);
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������. ����� ������ ���������� ���������� sql-��������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int setStringValue(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException
   {return DataChanger.setStringValue(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);}

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ����� ����� ������������
   * ���������� sql-��������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-��������.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setStringValue(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, String dataFieldValue, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ����������
      connection = DBUtils.getDBConn(config);
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setStringValue(connection, tableName, keyFieldName, keyFieldValue,
                                          dataFieldName, dataFieldValue, useSqlFilter);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ������������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ����� ������ ����������
   * ���������� sql-��������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setStringValue(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException
   {return DataChanger.setStringValue(config, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);}

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ����� ����� ������������
   * ���������� sql-��������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-��������.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setStringValue(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, String dataFieldValue, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ����������
      connection = DBUtils.getDBConn(config);
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.setStringValue(connection, tableName, keyFieldName, keyFieldValue,
                                          dataFieldName, dataFieldValue, useSqlFilter);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� ���������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� ���������� ���� ��� ��������� - dataFieldValue.
   * ���� �������� ���� ��� ��������� null, �� ��������������� ���� � ������ ������� ����� ����� ����������� � null.
   * ����� ���������� ��������� ������������ ���������� � ���� - config. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ����� ������ ����������
   * ���������� sql-��������.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @param dataFieldName String ������������ ����, ������ �������� ������ ���� ��������.
   * @param dataFieldValue String ����� ��������� �������� ��� ����������� ����.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int setStringValue(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
   String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException
   {return DataChanger.setStringValue(config, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);}

  /**
   * ����� ������������� ������ ������ � ������� �������/������� �� ���������� ��������� ����. ����� ���������� ��
   * ������ ������� ������� ������ (setIntValue()). ���� �� ������� ��� ��������� ���� ��� ��� ���� �� ��������
   * ��������, �� ��� ���� ����� ������� �������� �� ��������� (��. ����� DBConsts ������ ����������). ��������
   * ������� �������/������� - ��. ��������� DBConsts.DELETED_RECORD_STATUS � DBConsts.ACTIVE_RECORD_STATUS, ���������
   * �������� �������������� �� �������� ����� �� ���� �������� (���� �������� != 0, �� ��� ������ DBConsts.DELETED_RECORD_STATUS,
   * ���� �� = 0, �� ��� ������ DBConsts.ACTIVE_RECORD_STATUS). �� ��� ������ ������� ������ - ������ ���������� � ���� ���
   * ���������� ��������. ��� ���������� � ���� ����� ���������� ��� ������������� ����������, ������� ����� ����������
   * ��������� ������ �� �����������.
   * @param connection Connection ���������� � ���� ��� ������ ������.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldName String ������������ ��������� ���� �������.
   * @param keyFieldValue int �������� ����� ������.
   * @param deletedFieldName String ������������ ����, ��������� ������ ��������/���������� ������.
   * @param deletedFieldValue int ����� �������� ������� ��������/���������� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setDeleted(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String deletedFieldName, int deletedFieldValue) throws SQLException, DBConnectionException
   {
    logger.debug("DataChanger: setDeleted().");
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // �������� ��� ��������� ����. ���� ��� ���� �� ������� - �������� ��� �� ���������.
    String keyField;
    if (StringUtils.isBlank(keyFieldName)) {keyField = DBConsts.FIELD_NAME_KEY;}
    else                                   {keyField = keyFieldName;}
    // �������� ��� ���� �� �������� ��������. ���� ��� ���� �� ������� - �������� ��� �� ���������.
    String deletedField;
    if (StringUtils.isBlank(deletedFieldName)) {deletedField = DBConsts.FIELD_NAME_DELETED;}
    else                                       {deletedField = deletedFieldName;}
    // �������� ��������������� ������ ��������.
    int deletedValue;
    if (deletedFieldValue != 0) {deletedValue = DBConsts.RECORD_STATUS_DELETED;}
    else                        {deletedValue = DBConsts.RECORD_STATUS_ACTIVE;}
    return DataChanger.setIntValue(connection, tableName, keyField, keyFieldValue, deletedField, deletedValue);
   }

  /**
   * ����� ������������� ������ ������ � ������� �������/������� �� ���������� ��������� ����. ����� ���������� ��
   * ������ ������� ������� ������ (setIntValue()). ���� �� ������� ��� ��������� ���� ��� ��� ���� �� ��������
   * ��������, �� ��� ���� ����� ������� �������� �� ��������� (��. ����� DBConsts ������ ����������). ��������
   * ������� �������/������� - ��. ��������� DBConsts.DELETED_RECORD_STATUS � DBConsts.ACTIVE_RECORD_STATUS, ���������
   * �������� �������������� �� �������� ����� �� ���� �������� (���� �������� != 0, �� ��� ������ DBConsts.DELETED_RECORD_STATUS,
   * ���� �� = 0, �� ��� ������ DBConsts.ACTIVE_RECORD_STATUS). �� ��� ������ ������� ������ - ������ ���������� � ���� ���
   * ���������� ��������. ��� ���������� � ���� ����� ���������� �������� ������ (DataSource).
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � �������� ��.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldName String ������������ ��������� ���� �������.
   * @param keyFieldValue int �������� ����� ������.
   * @param deletedFieldName String ������������ ����, ��������� ������ ��������/���������� ������.
   * @param deletedFieldValue int ����� �������� ������� ��������/���������� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setDeleted(DataSource dataSource, String tableName, String keyFieldName, int keyFieldValue,
   String deletedFieldName, int deletedFieldValue) throws SQLException, DBConnectionException
   {
    logger.debug("DataChanger: setDeleted().");
    // ��������� �������� ������
    if (dataSource == null) {throw new DBConnectionException("DataSource is NULL!");}
    // �������� ��� ��������� ����. ���� ��� ���� �� ������� - �������� ��� �� ���������.
    String keyField;
    if (StringUtils.isBlank(keyFieldName)) {keyField = DBConsts.FIELD_NAME_KEY;}
    else                                   {keyField = keyFieldName;}
    // �������� ��� ���� �� �������� ��������. ���� ��� ���� �� ������� - �������� ��� �� ���������.
    String deletedField;
    if (StringUtils.isBlank(deletedFieldName)) {deletedField = DBConsts.FIELD_NAME_DELETED;}
    else                                       {deletedField = deletedFieldName;}
    // �������� ��������������� ������ ��������.
    int deletedValue;
    if (deletedFieldValue != 0) {deletedValue = DBConsts.RECORD_STATUS_DELETED;}
    else                        {deletedValue = DBConsts.RECORD_STATUS_ACTIVE;}
    return DataChanger.setIntValue(dataSource, tableName, keyField, keyFieldValue, deletedField, deletedValue);
   }

  /**
   * ����� ������������� ������ ������ � ������� �������/������� �� ���������� ��������� ����. ����� ���������� ��
   * ������ ������� ������� ������ (setIntValue(), setDeleted()). ��� ��������� ���� � ��� ���� �� ��������
   * �������� ������� �� ��������� (��. ����� DBConsts ������ ����������). �������� ������� �������/������� - ��.
   * ��������� DBConsts.DELETED_RECORD_STATUS � DBConsts.ACTIVE_RECORD_STATUS, ��������� �������� �������������� ��
   * �������� ����� �� ���� �������� (���� �������� != 0, �� ��� ������ DBConsts.DELETED_RECORD_STATUS, ���� �� = 0,
   * �� ��� ������ DBConsts.ACTIVE_RECORD_STATUS). �� ��� ������ ������� ������ - ������ ���������� � ���� ���
   * ���������� ��������. ��� ���������� � ���� ����� ���������� ��� ������������� ����������, ������� ����� ����������
   * ��������� ������ �� �����������.
   * @param connection Connection ���������� � ���� ��� ������ ������.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @param deletedFieldValue int ����� �������� ������� ��������/���������� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setDeleted(Connection connection, String tableName, int keyFieldValue, int deletedFieldValue)
   throws SQLException, DBConnectionException
   {
    return DataChanger.setDeleted(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue,
                                  DBConsts.FIELD_NAME_DELETED, deletedFieldValue);
   }

  /**
   * ����� ������������� ������ ������ � ������� �������/������� �� ���������� ��������� ����. ����� ���������� ��
   * ������ ������� ������� ������ (setIntValue(), setDeleted()). ��� ��������� ���� � ��� ���� �� ��������
   * �������� ������� �� ��������� (��. ����� DBConsts ������ ����������). �������� ������� �������/������� - ��.
   * ��������� DBConsts.DELETED_RECORD_STATUS � DBConsts.ACTIVE_RECORD_STATUS, ��������� �������� �������������� ��
   * �������� ����� �� ���� �������� (���� �������� != 0, �� ��� ������ DBConsts.DELETED_RECORD_STATUS, ���� �� = 0,
   * �� ��� ������ DBConsts.ACTIVE_RECORD_STATUS). �� ��� ������ ������� ������ - ������ ���������� � ���� ���
   * ���������� ��������. ��� ���������� � ���� ����� ���������� �������� ������ (DataSource).
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � �������� ��.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @param deletedFieldValue int ����� �������� ������� ��������/���������� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setDeleted(DataSource dataSource, String tableName, int keyFieldValue, int deletedFieldValue)
   throws SQLException, DBConnectionException
   {
    return DataChanger.setDeleted(dataSource, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue,
                                  DBConsts.FIELD_NAME_DELETED, deletedFieldValue);
   }

  /**
   * ����� ������������� ������ ������ "�������". �������� ��� ����� ��������� ����, ����� ���� �� �������� �������� ������,
   * �������� ���� ������� �������� ������ ������� �� ��������� (��. �������� ������ �������).
   * @param connection Connection ���������� � ���� ��� ������ ������.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setRecordDeleted(Connection connection, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {return DataChanger.setDeleted(connection, tableName, keyFieldValue, DBConsts.RECORD_STATUS_DELETED);}

  /**
   * ����� ������������� ������ ������ "������� (���������)". �������� ��� ����� ��������� ����, ����� ���� �� ��������
   * �������� ������, �������� ���� ������� �������� ������ ������� �� ��������� (��. �������� ������ �������).
   * @param connection Connection ���������� � ���� ��� ������ ������.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setRecordUndeleted(Connection connection, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {return DataChanger.setDeleted(connection, tableName, keyFieldValue, DBConsts.RECORD_STATUS_ACTIVE);}

  /**
   * ����� ������������� ������ ������ "�������". �������� ��� ����� ��������� ����, ����� ���� �� �������� �������� ������,
   * �������� ���� ������� �������� ������ ������� �� ��������� (��. �������� ������ �������).
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � �������� ��.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setRecordDeleted(DataSource dataSource, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {return DataChanger.setDeleted(dataSource, tableName, keyFieldValue, DBConsts.RECORD_STATUS_DELETED);}

  /**
   * ����� ������������� ������ ������ "������� (���������)". �������� ��� ����� ��������� ����, ����� ���� �� ��������
   * �������� ������, �������� ���� ������� �������� ������ ������� �� ��������� (��. �������� ������ �������).
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � �������� ��.
   * @param tableName String ��� �������, � ������� ������ ������ ������.
   * @param keyFieldValue int �������� ����� ������.
   * @return int ��� ���������� ������� �� ��������� ������ (� ����������� ������� = 0 ��� �������� ���������� �������).
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBConnectionException ������ ������ � ����������� � ����.
  */
  public static int setRecordUndeleted(DataSource dataSource, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {return DataChanger.setDeleted(dataSource, tableName, keyFieldValue, DBConsts.RECORD_STATUS_ACTIVE);}

  /**
   * ����� ��������� ������� ��������� ������� tableName ��� ��������� ������������ ���������� � ����. ���� ���
   * ������� ����� - ��������� ��.
   * @param config DBConfig ������������ ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� ��������� �������.
   * @return int ��������� ���������� ��������� (���������� ������� DELETE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� (�������� ������������ �����).
  */
  public static int cleanupTable(DBConfig config, String tableName)
   throws DBConnectionException, SQLException, DBModuleConfigException
   {
    // ���� ��� �������� ����������
    int result = 0;
    // ��������� ��� ������� - ���� ��� ����� - ������!
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Error occured during cleaning up table: table name is empty!");}
    logger.info("Cleaning table [" + tableName.toUpperCase() + "].");
    // ������ ��� ������ ������� �������
    String sql = "delete from " + tableName;
    SqlExecutor.executeUpdateQuery(config, sql);
    // ���������� ���������
    return result;
   }

  /**
   * ����� ������� ������ � ��������������� id �� ������� tableName, �������� ���� ������� ���������� keyFieldName.
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @return int ��������� ���������� ��������� (���������� ������� DELETE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int deleteRecord(Connection connection, String tableName, String keyFieldName, int keyFieldValue)
   throws DBConnectionException, SQLException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ��������� ������
    String sql = "delete from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������
    return SqlExecutor.executeUpdateQuery(connection, sql);
   }

  /**
   * ����� ������� ������ � ��������������� id �� ������� tableName, �������� ���� ������� ���������� ID (��. ��������
   * �� ��������� � ������ DBConsts).
   * ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� ��
   * SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������. ������������ �������
   * ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� ��������� ������.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldValue int ������������� �������� ��������� ���� �������.
   * @return int ��������� ���������� ��������� (���������� ������� DELETE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int deleteRecord(Connection connection, String tableName, int keyFieldValue)
   throws DBConnectionException, SQLException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ��� ������� - ��� ������ ���� �� ������
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Can't delete record: table name is empty.");}
    // ���������� ������ ��� ��������� ������
    String sql = "delete from " + tableName + " where " + DBConsts.FIELD_NAME_KEY + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � ���������� ��������� ����������
    return SqlExecutor.executeUpdateQuery(connection, sql);
   }

  /**
   * ����� ������������� � ������� tableName � �������� ����� keyFieldName �������� �������������� ���� dataFiledName.
   * �������� ����� - ��������� �������� keyFieldValue, �������� �������������� ���� ��� ��������� - dataFieldValue.
   * ����� ���������� ��������� �������� ������ (DataSource) ��� ���������� � ����. ��� ������� � ������ ������ -
   * ��������� �� SQLException � DBConnectionException, ��������� ������� ��������� ��������� ������.
   * @param dataSource DataSource ������������ �������� ������ ��� ��������� ���������� � ��.
   * @param tableName String ��� �������, � ������� ���������� ������.
   * @param keyFieldValue String ��������� �������� ��������� ���� �������.
   * @return int ��������� ���������� ��������� (���������� ������� UPDATE...).
   * @throws SQLException ��, ����������� � �������� ��������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static int deleteRecord(DataSource dataSource, String tableName, int keyFieldValue)
   throws DBConnectionException, SQLException
   {
    // ���� ��� �������� ����������
    int result = 0;
    Connection connection = null;
    try
     {
      // ������������� ���������� (���� �������� ������ �� ����)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // �������� ����� ��� ��������� �������� (������������ ������������� ����������)
      result = DataChanger.deleteRecord(connection, tableName, keyFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    // ���������� ���������
    return result;
   }
  
 }