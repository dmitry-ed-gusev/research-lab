package spammer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * �������� �� ��������� ��� ����� ����������. ����� ������ ������ �������� ����� �������-������������, ������������ �
 * ���������� � �������� ������������.
 * @author Gusev Dmitry (�������)
 * @version 4.1 (DATE: 20.12.2010)
*/

public interface Defaults
 {
  /** ������ ������� �������� ��������. */
  public static final String SYSTEM_VERSION = "MassEmailsSender system, v. 3.1.0 (22.12.2010), (C) RMRS";

  /** ���-������������ ��������� �������� ��������. */
  public static enum DeliveryStatus
   {
    DELIVERY_STATUS_UNKNOWN              ("������ �������� ����������!",                          -1),
    DELIVERY_STATUS_OK                   ("�������� ������� ���������.",                           0),
    DELIVERY_STATUS_IN_PROCESS           ("�������� � �������� ����������.",                       1),
    DELIVERY_STATUS_FAILED               ("�������� ����������� �������� (��������). ������ ���.", 2),
    DELIVERY_STATUS_FINISHED_WITH_ERRORS ("�������� ��������� � ��������. ������ ���.",            3);
    // ���� ������-������������
    private final String strValue;
    private final int intValue;
    // ����������� ������-������������
    DeliveryStatus(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // ������ ������� � ����� ������-������������
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // ����� ������� �������� �� �������������� ��������
    public static DeliveryStatus findByIntValue(int i)
     {
      // �� ��������� - ������ �� ��������
      DeliveryStatus status = DELIVERY_STATUS_UNKNOWN;
      // ���������� �������� �������� ��������
      if      (DELIVERY_STATUS_OK.intValue                   == i) {status = DELIVERY_STATUS_OK;}
      else if (DELIVERY_STATUS_IN_PROCESS.intValue           == i) {status = DELIVERY_STATUS_IN_PROCESS;}
      else if (DELIVERY_STATUS_FAILED.intValue               == i) {status = DELIVERY_STATUS_FAILED;}
      else if (DELIVERY_STATUS_FINISHED_WITH_ERRORS.intValue == i) {status = DELIVERY_STATUS_FINISHED_WITH_ERRORS;}
      // ���������� ���������
      return status;
     }
   }

  /** ���-������������ ��������� ����� ��������. */
  public static enum DeliveryType
   {
    DELIVERY_TYPE_UNKNOWN  ("��� �������� ����������!", -1),
    DELIVERY_TYPE_STANDARD ("����������� ��������.",     0),
    DELIVERY_TYPE_TEST     ("�������� ��������.",        1);
    // ���� ������-������������
    private final String strValue;
    private final int    intValue;
    // ����������� ������-������������
    DeliveryType(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // ������ ������� � ����� ������-������������
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // ����� ���� �������� �� �������������� ��������
    public static DeliveryType findByIntValue(int i)
     {
      // �� ��������� - ��� ����������
      DeliveryType type = DELIVERY_TYPE_UNKNOWN;
      // ���������� �������� ����� ��������
      if      (DELIVERY_TYPE_STANDARD.intValue == i) {type = DELIVERY_TYPE_STANDARD;}
      else if (DELIVERY_TYPE_TEST.intValue     == i) {type = DELIVERY_TYPE_TEST;}
      // ���������� ���������
      return type;
     }
   }

  /** ���-������������ ��������� ����� ����������� ��������. */
  public static enum RecipientType
   {
    RECIPIENT_TYPE_TEST          ("�������� ������ �������� (����� 019).",                  1),
    RECIPIENT_TYPE_SHIPOWNERS_RUS("�������������, ������������������ �� ���������� ��.",    2),
    RECIPIENT_TYPE_SHIPOWNERS_ENG("�������������, ������������������ �� �� ���������� ��.", 3);
    // ����
    private final String strValue;
    private final int    intValue;
    // �����������
    RecipientType(String strValue, int intValue) {this.strValue = strValue; this.intValue = intValue;}
    // ������ ������� � �����
    public String getStrValue() {return strValue;}
    public int    getIntValue() {return intValue;}
    // ����� ���� ����������� �� �������������� ��������
    public static RecipientType findByIntValue(int i)
     {
      RecipientType type = null;
      if      (RECIPIENT_TYPE_TEST.intValue           == i) {type = RECIPIENT_TYPE_TEST;}
      else if (RECIPIENT_TYPE_SHIPOWNERS_RUS.intValue == i) {type = RECIPIENT_TYPE_SHIPOWNERS_RUS;}
      else if (RECIPIENT_TYPE_SHIPOWNERS_ENG.intValue == i) {type = RECIPIENT_TYPE_SHIPOWNERS_ENG;}
      return type;
     }
    // ����������� ����� ������ ����� ����������� ��������
    public static ArrayList<RecipientType> findAllTypes()
     {
      ArrayList<RecipientType> list = new ArrayList<RecipientType>();
      list.addAll(Arrays.asList(RecipientType.values()));
      return list;
     }
   }

  /** ������ ������� ����������. �� ������������ �� ��������� ������. */
  public static final String LOGGER_NAME     = "spammer";
  /** ������ ��� ��������� ������� ����������. �� ������������ �� ��������� ������. */
  public static final String LOGGER_PATTERN  = "%d{dd/MM/yyyy HH:mm:ss} %p [%c{1}] %m%n";
  /** ���� ������� ������� ����������. �� ������������ �� ��������� ������. */
  public static final String LOGGER_FILE     = "spammer.log";
  /**
   * ���� ������������ ���������� � ���� ��� ���������� (������������ DAO-������������). �� ������������ ��
   * ��������� ������. ���� ���������� ����� ����������� �� ��������� ������ - ���������, ������������ � DAO!
  */
  public static final String DBCONFIG_FILE   = "spammer.xml";

  /** ���� � ��� ���� �����. ������������ �� ��������� ������. */
  public static final String DB_FLEET_PATH                 = "c:\\temp\\fleet";
  /** �������� ��������� ������ ��� ���� � �� ����. */
  public static final String CMDLINE_DB_FLEET_PATH         = "fleetPath";
  /** ���� � ��� ���� ����. ������������ �� ��������� ������. */
  public static final String DB_FIRM_PATH                  = "c:\\temp\\firm";
  /** �������� ��������� ������ ��� ���� � �� ����. */
  public static final String CMDLINE_DB_FIRM_PATH          = "firmPath";
  /** ���� ��������� �������. ������������ �� ��������� ������. */
  public static final String MAIL_HOST                     = "smtp.rs-head.spb.ru";
  /** �������� ��������� ������ ��� ����� ��������� �������. */
  public static final String CMDLINE_MAIL_HOST             = "mailHost";
  /** �������� ��������� ������ - ���� ��������� �������. */
  public static final String CMDLINE_MAIL_PORT             = "mailPort";
  /** �������� ����� ��� ����� ��������. ������������ �� ��������� ������. */
  public static final String MAIL_FROM                     = "info_na@rs-head.spb.ru";
  /** �������� ��������� ������ ��� ��������� ������ ��������. */
  public static final String CMDLINE_MAIL_FROM             = "mailFrom";
  /** �������� ��������� ������ - ������������� �������� �� �� ��������. */
  public static final String CMDLINE_DELIVERY_ID           = "deliveryId";
  /** �������� ��������� ������ - ���� � ������ �������� (� ��������� �����������). */
  public static final String CMDLINE_DELIVERIES_FILES_PATH = "deliveriesFiles";

  /** ����� ��������� ������ - �������� ���� (�������� ����� ����������). */
  public static final String CMDLINE_SHOW_HELP             = "help";
  /** ����� ��������� ������ - ��������� ����-������. */
  public static final String CMDLINE_DEMO_MODE             = "demo";
  /** ����� ��������� ������ - �������� ������ �������. */
  public static final String CMDLINE_SHOW_VERSION          = "version";

  /** �������� �������� �������� �� ��������� ����� (��� ������ �������, ����������� ��������). */
  public static final String CMDLINE_TEST_MAIL_TO          = "testMailTo";
  /***/
  public static final String CMDLINE_MAIL_ENCODING         = "encoding";

  /***/
  public static final String[] EMAILS_DELIMITERS_WRONG = {";"};
  /***/
  public static final String   EMAILS_DELIMITER_RIGHT  = ",";
 }