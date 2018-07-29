package jlib.system.exec.mysql;

import jlib.logging.InitLogger;
import jlib.system.exec.WindowsExec;
import jlib.system.exec.WindowsExecResult;
import org.apache.log4j.Logger;

/**
 * ������ ����� ������������ ��� ������ � ���� MySQL, ������������� ��������. ������
 * ������������� ������/������� ������� "mysql", �����������/�������� ������� ������� �
 * �.�. ��������. �������������� ������ ������� ������ � �� ��������� Windows XP/Vista.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 25.02.2009)
*/

public class MysqlEngine
 {
  // todo: ������� ������� ����������� ������� mysql
  
  /** �������� �� � ������ �� ������ ������� ������. */
  private boolean isOSVersionCorrect = false;

  public MysqlEngine() {this.isOSVersionCorrect = new WindowsExec().isOSVersionCorrect();}

  /**
   * �������� - ������������� �� � ������ ������� ������ ���� Mysql - ������ ���������� 'mysql'.
   * @return boolean ������/���� - ��������� ���������� �������.
  */
  public boolean isMysqlServiceInstalled()
   {
    boolean result = false;
    // ���� ������ �� ��������, �� ��������� �������
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_QUERY_SERVICE);
      if ((execResult != null) && (execResult.getOutput().indexOf(MysqlEngineConsts.SC_ERROR) < 0)) result = true;
     }
    return result;
   }

  /**
   * �������� - ������� �� ������ mysql � ������ �������.
   * @return boolean ������/���� - ��������� ���������� �������.
  */
  public boolean isMysqlServiceRunning()
   {
    boolean result = false;
    // ���� ������ �� ��������, �� ��������� �������
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_QUERY_SERVICE);
      if ((execResult != null) && (execResult.getOutput().indexOf(MysqlEngineConsts.SC_SERVICE_STATUS_RUNNING) >= 0)) result = true;
     }
    return result;
   }

  /**
   * ������ ������� mysql.
   * @return boolean ������/���� - ��������� ���������� �������.
  */
  public boolean startMysqlService()
   {
    boolean result = false;
    // ���� ������ �� ��������, �� ��������� �������
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_START_SERVICE);
      if ((execResult != null) && (execResult.getExitCode() == 0) && (execResult.getError() == null)) result = true;
     }
    return result;
   }

  /**
   * ������� ������� mysql.
   * @return boolean ������/���� - ��������� ���������� �������.
  */
  public boolean stopMysqlService()
   {
    boolean result = false;
    // ���� ������ �� ��������, �� ��������� �������
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_STOP_SERVICE);
      if ((execResult != null) && (execResult.getExitCode() == 0) && (execResult.getError() == null)) result = true;
     }
    return result;
   }
  
  /**
   * ����� ��� ������������ ������.
   * @param args String[] ��������� ������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("updater");
    Logger logger = Logger.getLogger("updater");
    MysqlEngine mysql = new MysqlEngine();
    logger.info(mysql.isMysqlServiceInstalled());
    logger.info(mysql.isMysqlServiceRunning());
   }

 }