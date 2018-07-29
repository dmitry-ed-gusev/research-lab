package jlib.system.exec.mysql;

/**
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 25.02.2009)
*/

public class MysqlEngineConsts
 {
  /***/
  public static final String MYSQL_SERVICE_NAME  = "mysql";
  /***/
  public static final String MYSQL_START_SERVICE = "net start " + MYSQL_SERVICE_NAME;
  /***/
  public static final String MYSQL_STOP_SERVICE  = "net stop " + MYSQL_SERVICE_NAME;
  /***/
  public static final String MYSQL_QUERY_SERVICE = "sc query " + MYSQL_SERVICE_NAME;

  /***/
  public static final String SC_ERROR                  = "FAILED 1060";
  /***/
  public static final String SC_SERVICE_STATUS_RUNNING = "RUNNING";
  /***/
  public static final String SC_SERVICE_STATUS_STOPPED = "STOPPED";

  /**
   * ����� � �������������, ������� ������ ������ ����� ������� ������ Mysql � ������ ���������� � ��. �� ��� �����
   * ����� ���������� ������ ���� �������������. �� ������� ������ ������������������ ����� ����������� - �������
   * ��� ���� �������� - 5-10 ������ (5000-10000 �����������).  
  */
  public static final int    MYSQL_WAIT_BEFORE_START   = 5000; // <- 5 ������
  /**
   * ����� � �������������, ������� ������ ������ ����� �������� ������ Mysql ��� ����, ����� ���������, ���
   * ������ ������������� �����������. �� ��� ����� ����� ���������� ������ ���� �������������. �� �������
   * ������ ������������������ ����� ����������� - ������� ��� ���� �������� - 5-10 ������ (5000-10000 �����������).
  */
  public static final int    MYSQL_WAIT_BEFORE_STOP    = 5000; // <- 5 ������
 }