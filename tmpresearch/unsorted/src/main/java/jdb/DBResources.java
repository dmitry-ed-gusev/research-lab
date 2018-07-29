package jdb;

/**
 * ������ ����� �������� ��������� ������� ��� ��������� ������� ����������, ������� �������� � ������� �����
 * ����������� ��������� (���������) ���������. ��� ������� ������������� ��� ���������� ��������� ������� ������� JDB. 
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 10.03.2011)
*/

public interface DBResources
 {
  /**
   * ������ ��������� � ���� ��������� (����������) sql-�������� � �����. � ������ ������ ������ �������������
   * ������������ �������� ��� �������� �� ������������ ����. ������������ ��� �������������� ���������� ������-��������,
   * ��� ���������� ���� ��������� ��������.
  */
  public static final String MSG_SQL_PROCESSING = "%1$S [SQL: %2$S / %3$S]";
  /***/
  public static final String ERR_MSG_DB_CONFIG_DATA = "DB TYPE is NULL and DATA SOURCE name is NULL! Can't connect!";
  /***/
  public static final String ERR_MSG_DB_TYPE        = "Invalid database type [%1$S]!";
  /***/
  public static final String ERR_MSG_DB_HOST        = "Database host is empty for db type [%1$S]!";
  /***/
  public static final String ERR_MSG_DB_NAME        = "Database name is empty for db type [%1$S]!";
  /***/
  public static final String ERR_MSG_DB_USERNAME    = "Username is empty for db type [%1$S]!";
  /***/
  public static final String ERR_MSG_DB_PASSWORD    = "Password is empty for db type [%1$S]!";
 }