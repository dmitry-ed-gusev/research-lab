package spammer.config;

import org.apache.commons.lang.StringUtils;
import spammer.Defaults;

/**
 * ������� ���� ������������ ������� ��������. �� ������������ ��������! ��� ������������� �
 * ������� ������������� �����-�������.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.12.2010)
*/

public class BaseMailerConfig
 {
  /** ���� ��������� �������. */
  private String              mailHost            = Defaults.MAIL_HOST;
  /**
   * ���� ��������� �������. �� ��������� ������� �������� ��������� = 0, �.�. ���� �� ������.
   * ������ �������� ����� ���������� � ����� ������ ���� �� ��������� = ���� 25. 
  */
  private int                 mailPort            = 0;
  /** �������� ����� ��� ������� ��������. */
  private String              mailFrom            = Defaults.MAIL_FROM;
  /** �����(�) ��� �������� �������� ��������. ��������� ������ ������� ������������ (���� �������� �� ����). */
  private String              testMailTo          = null;
  /** ���� � �� �����. ���� � ������� DBF. */
  private String              fleetDBPath         = Defaults.DB_FLEET_PATH;
  /** ���� � �� ����. ���� � ������� DBF. */
  private String              firmDBPath          = Defaults.DB_FIRM_PATH;
  /** ������������� �������������� �������� (�� �� ��������). */
  private int                 deliveryId          = 0;
  /** ���� � ������, ������������� � ��������� (���� � ���������). */
  private String              deliveriesFilesPath = null;
  /** ������� ��� �������� ����-�����. � ����-������ ������ �� ������������! */
  private boolean             isDemoMode          = false;
  /** ��������� ���� � ������ ������������ ������. */
  private String              mailEncoding        = null;

  public String getMailHost() {
   return mailHost;
  }

  public void setMailHost(String mailHost) {
   this.mailHost = mailHost;
  }

  public int getMailPort() {
   return mailPort;
  }

  public void setMailPort(int mailPort) {
   this.mailPort = mailPort;
  }

  public String getMailFrom() {
   return mailFrom;
  }

  public void setMailFrom(String mailFrom) {
   this.mailFrom = mailFrom;
  }

  public String getFleetDBPath() {
   return fleetDBPath;
  }

  public void setFleetDBPath(String fleetDBPath) {
   this.fleetDBPath = fleetDBPath;
  }

  public String getFirmDBPath() {
   return firmDBPath;
  }

  public void setFirmDBPath(String firmDBPath) {
   this.firmDBPath = firmDBPath;
  }

  public int getDeliveryId() {
   return deliveryId;
  }

  public void setDeliveryId(int deliveryId) {
   this.deliveryId = deliveryId;
  }

  public String getDeliveriesFilesPath() {
   return deliveriesFilesPath;
  }

  public void setDeliveriesFilesPath(String deliveriesFilesPath) {
   this.deliveriesFilesPath = deliveriesFilesPath;
  }

  public boolean isDemoMode() {
   return isDemoMode;
  }

  public void setDemoMode(boolean demoMode) {
   isDemoMode = demoMode;
  }

  public String getTestMailTo() {
   return testMailTo;
  }

  public void setTestMailTo(String testMailTo) {
   this.testMailTo = testMailTo;
  }

  public String getMailEncoding() {
   return mailEncoding;
  }

  public void setMailEncoding(String mailEncoding) {
   this.mailEncoding = mailEncoding;
  }

  public String getConfigErrors()
   {
    String result = null;
    // ���������� ���� ��� �������� �����
    if (StringUtils.isBlank(this.mailHost))      {result = "Empty mail host parameter!";}
    // ��������� �������� �����
    else if (StringUtils.isBlank(this.mailFrom)) {result = "Empty mail from parameter!";}
    // ��������� ������������� �������� - �� ������ ���� ������ ����
    else if (this.deliveryId <= 0)               {result = "Invalid delivery ID [" + deliveryId + "]!";}
    // ���������� ���������
    return result;
   }

 }