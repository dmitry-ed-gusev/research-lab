package jdb.processing.data;

import jdb.exceptions.DBConnectionException;
import jdb.processing.data.helpers.DataProcessingHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ������ �������� �������������� ������ �������� ��������� ����� ��������� �������. ������ ������ ������������
 * ��� ���������� ������ ���� ������� ������ (������� �������� ����������� ������ �������).
 * @author Gusev Dmitry
 * @version 2.0 (DATE: 30.11.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class DataReader
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DataReader.class.getName());

  /**
   * ������ ���������� �������� ���� dataFieldName �� �������� keyFieldValue ��������� ���� keyFieldName �� �������
   * tableName. ���� �� ���������� ����� ����� ������� ������ ����� ������, �� ����� ��������� �������� �� ������
   * ��������� ������. ������� ������� � ������� ����� ��������������� ������� ������� � ������� ��. ����� ����������
   * ��� ��������� � �������� ���������� � ����. ���������� ����� ��������� ������ ������ �� �����������. 
   * @param connection Connection ����������, � ������� �������� �����.
   * @param tableName String �������, �� ������� ������ ������.
   * @param keyFieldName String ������������ ��������� ����.
   * @param keyFieldValue int �������� ��������� ����.
   * @param dataFieldName String ������������ ���� ������ (����, �������� �������� ������).
   * @return String ����������� �������� ��� NULL (���� ���� �� ��������� :)).
   * @throws SQLException ������ ��� ���������� sql-�������.
   * @throws DBConnectionException ������ ��� ���������� � ����.
  */
  public static String getStringValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName) throws SQLException, DBConnectionException
   {
    String result = null;
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ������ ���� ������
    String sql = "select " + dataFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � �������� ������
    ResultSet rs = connection.createStatement().executeQuery(sql);
    if (rs.next()) {result = rs.getString(dataFieldName);}
    // ���������� ���������� ���������
    return result;
   }

  /**
   * ������ �������������� �������� ���� dataFieldName �� �������� keyFieldValue ��������� ���� keyFieldName �� �������
   * tableName. ���� �� ���������� ����� ����� ������� ������ ����� ������, �� ����� ��������� �������� �� ������
   * ��������� ������. ������� ������� � ������� ����� ��������������� ������� ������� � ������� ��. ����� ����������
   * ��� ��������� � �������� ���������� � ����. ���������� ����� ��������� ������ ������ �� �����������.
   * @param connection Connection ����������, � ������� �������� �����.
   * @param tableName String �������, �� ������� ������ ������.
   * @param keyFieldName String ������������ ��������� ����.
   * @param keyFieldValue int �������� ��������� ����.
   * @param dataFieldName String ������������ ���� ������ (����, �������� �������� ������).
   * @return int ����������� �������� ��� 0 (���� ���� �� ��������� :)).
   * @throws SQLException ������ ��� ���������� sql-�������.
   * @throws DBConnectionException ������ ��� ���������� � ����.
  */
  public static int getIntValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName) throws SQLException, DBConnectionException
   {
    int result = 0;
    // ��������� ����������
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // ��������� ���������
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // ���������� ������ ��� ������ ���� ������
    String sql = "select " + dataFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // ��������� ������ � �������� ������
    ResultSet rs = connection.createStatement().executeQuery(sql);
    if (rs.next()) {result = rs.getInt(dataFieldName);}
    // ���� ������ �� ������� - �� �� ������ �������� ��������, ���������� ��, �.�. ������� 0 - ��� ��������, ��� ��
    // ��� ������ (�� �������� ������ �������� �� ������� ������������ �������� 0)
    else {throw new SQLException("Value not found!");}
    // ���������� ���������� ���������
    return result;
   }

 }