package jdb.processing.data;

import jdb.DBConsts;
import jdb.exceptions.DBConnectionException;
import jdb.processing.data.helpers.DataProcessingHelper;
import jdb.processing.sql.execution.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ����� ��������� ������ ��� ��������� �������� ������ � ��. ������ ������� ������ �� �������� ������ � �������� ��.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.02.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class DataChecker
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DataChecker.class.getName());

  /**
   * ����� ��������� � ������� tableName � �������� ����� keyFieldName ������������� ������ � ��������������� (���������
   * ��������� ����) keyFieldValue. ����� ���������� ��� ������������� ���������� � ���� - connection. ��� ������� �
   * ������ ������ - ��������� �� SQLException, DBConnectionException, ��������� ������� ��������� ��������� ������.
   * ������������ ������� ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� �������� ������.
   * @param tableName String ��� �������, � ������� ����������� ������������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ���������� ���� �������.
   * @return boolean �������� ������/���� � ����������� �� ����, ���������� ������ ��� ���.
   * @throws SQLException ��, ����������� � �������� �������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static boolean isRecordExists(Connection connection, String tableName, String keyFieldName,
   int keyFieldValue) throws SQLException, DBConnectionException
   {
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� �������� ������������� ������
    boolean result = false;
    // �������� ����������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� �������� ������
    String sql = "select " + keyFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    // ��������� ������ � ���� ��������� �� ���� - ���������� �������� ������
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql);
    if (rs.next()) {result = true;}
    // ���������� ����� (��������� ���������� ������� � ��� ������)
    logger.debug("In table [" + tableName + "] record with [" + keyFieldName + " = " + keyFieldValue +
                 "] exists: [" + result + "]. SQL [" + sql + "].");
    return result;
   }

  /**
   * ����� ��������� � ������� tableName � �������� ����� DBFieldsConsts.KEY_FIELD_NAME (�������� �� ��������� = ID)
   * ������������� ������ � ��������������� (��������� ��������� ����) keyFieldValue. ����� ���������� ��� �������������
   * ���������� � ���� - connection. ��� ������� � ������ ������ - ��������� �� SQLException, DBConnectionException,
   * ��������� ������� ��������� ��������� ������. ������������ ������� ���������� ����� ������ ������ �� �����������.
   * @param connection Connection ������������ ���������� � �� ��� �������� ������.
   * @param tableName String ��� �������, � ������� ����������� ������������� ������.
   * @param keyFieldValue int ������������� �������� ���������� ���� �������.
   * @return boolean �������� ������/���� � ����������� �� ����, ���������� ������ ��� ���.
   * @throws SQLException ��, ����������� � �������� �������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static boolean isRecordExists(Connection connection, String tableName, int keyFieldValue)
   throws DBConnectionException, SQLException
    {return DataChecker.isRecordExists(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue);}

  /**
   * ����� ��������� � ������� tableName � �������� ����� keyFieldName ������������� ������ � ��������������� (���������
   * ��������� ����) keyFieldValue. ����� ���������� �������� ������ (DataSource) ��� ��������� ���������� � ����. ���
   * ������� � ������ ������ - ��������� �� SQLException, DBConnectionException, ��������� ������� ��������� ���������
   * ������.
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � ����.
   * @param tableName String ��� �������, � ������� ����������� ������������� ������.
   * @param keyFieldName String ������������ ��������� ���� �������. ���� ������ ���� �������������.
   * @param keyFieldValue int ������������� �������� ���������� ���� �������.
   * @return boolean �������� ������/���� � ����������� �� ����, ���������� ������ ��� ���.
   * @throws SQLException ��, ����������� � �������� �������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static boolean isRecordExists(DataSource dataSource, String tableName, String keyFieldName,
   int keyFieldValue) throws SQLException, DBConnectionException
   {
    boolean    result     = false;
    Connection connection = null;
    try
     {
      // ������������� ���������� (���� �������� ������ �� ����)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // ��� ��������� ���������� �������� ������ �����
      result = DataChecker.isRecordExists(connection, tableName, keyFieldName, keyFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    return result;
   }

  /**
   * ����� ��������� � ������� tableName � �������� ����� DBFieldsConsts.KEY_FIELD_NAME (�������� �� ��������� = ID)
   * ������������� ������ � ��������������� (��������� ��������� ����) keyFieldValue. ����� ���������� �������� ������
   * (DataSource) ��� ��������� ���������� � ����. ��� ������� � ������ ������ - ��������� �� SQLException,
   * DBConnectionException, ��������� ������� ��������� ��������� ������.
   * @param dataSource DataSource �������� ������ ��� ��������� ���������� � ����.
   * @param tableName String ��� �������, � ������� ����������� ������������� ������.
   * @param keyFieldValue int ������������� �������� ���������� ���� �������.
   * @return boolean �������� ������/���� � ����������� �� ����, ���������� ������ ��� ���.
   * @throws SQLException ��, ����������� � �������� �������� ������.
   * @throws jdb.exceptions.DBConnectionException ������ ��� ������ � ����������� (�������� ��� �����).
  */
  public static boolean isRecordExists(DataSource dataSource, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {
    boolean    result     = false;
    Connection connection = null;
    try
     {
      // ������������� ���������� (���� �������� ������ �� ����)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // ��� ��������� ���������� �������� ������ �����
      result = DataChecker.isRecordExists(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue);
     }
    // ��������� ���������� � ����� ������ (��������)
    finally {if (connection != null) {connection.close();}}
    return result;
   }

 }