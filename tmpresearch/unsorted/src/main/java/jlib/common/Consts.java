package jlib.common;

/**
 * ������ ����� ���������� ����� ��������� ��� ���� ������ ����������.
 * @author Gusev Dmitry
 * @version 2.0
*/

public interface Consts
 {
  /** ����������� ��� �������� ��������� � ������-������. ������������ � ������� IniFileReader, Utils, JMail. */
  public static final String VALUES_DELIM = ",";

  /** ����������� ��� ��� �������� � ������-������. */
  public static final String PAIRS_DELIM  = ";";

  /** ����������� ��� ����� � �������� � ���� ���/��������: name[KEY_VALUE_DELIM]value (��� �����). */
  public static final String KEY_VALUE_DELIM = "=";

  /**
   * ��������� ��� ����������� �������� "������� ��������� ��� ������� ������� ������".
   * ��������� �������� ��� ���������, ������������� ������ ����������
   * ��. � ������ org.apache.log4j.Level.
  */
  public final static String LOGGER_LOG_LEVEL   = "log_level";
  /**  */
  public final static String LOGGER_FILE_NAME   = "log_file_name";
  /**  */
  public final static String LOGGER_LOG_FORMAT  = "log_format";
  /***/
  public final static String LOGGER_PARENT_NAME = "logger_parent_name";

  /** ����������� �� ��������� ��� ��������� �������. ������� ��������: ":".*/
  public final static String TIME_DEF_DELIM = ":";
  /** ����������� �� ��������� ��� ��������� ����. ������� ��������: "/".*/
  public final static String DATE_DEF_DELIM = "/";
  
  /**
   * ������ �� ��������� ��� ������ ��������������� ��������� - ������� ����.
   * ����� ��������� �������� ��. � ������������ � ������ log4j.<br>
   * ������� ��������: "%d{dd/MM/yyyy HH:mm:ss}(%rms) [%t] %p %c %x - %m%n" <br>
   * %d{dd/MM/yyyy HH:mm:ss} - ���� � �����, ������ �������� <br>
   * (%rms) - ����� � �������������, ��������� �� ������ ���������� �� ���������
   * ��������� <br>
   * {%M} - �����, �� �������� ������ ��������� (��������� �����...) <br>
   * [%t] - ������������ ������, ��������������� ����� ��������� <br>
   * %p   - ��������� ��������� (INFO,DEBUG......) <br>
   * %c   - ��������� ��������� <br>
   * %x   - ����� ��������� NDC(nested diagnostic context), ���� ��� ���� <br>
   * %m   - ����� ����������������� ��������� <br>
   * %n   - ������������� ������ �������� �� ����� ������ (������������������) <br>
  */
  public final static String LOGGER_LOG_FORMAT_DEFAULT = "%d{dd/MM/yyyy HH:mm:ss}(%-5rms) {%17M} [%t] %p %c %x - %m%n";

 }
