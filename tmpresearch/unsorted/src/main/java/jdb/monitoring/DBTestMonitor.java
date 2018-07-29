package jdb.monitoring;

import org.apache.log4j.Logger;

/**
 * ��������� ���������� ���������� ��������-�������� (DBProcessingMonitor) ��� ������� ��������� ������. ������
 * ���������� ��������� ����� � ������ � ���������� ������ ��������� �������� ���������. ��� �������������� ������������
 * ������ log4j. ��� ������ ��������� � ������ ������������ ������� ������� ������� INFO.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 09.03.2010)
*/

public class DBTestMonitor implements DBProcessingMonitor
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public void processProgress(int progress)
   {logger.info("DBTESTMONITOR -> processProgress -> " + progress);}

  @Override
  public void processMessage(String message)
   {logger.info("DBTESTMONITOR -> processMessage -> " + message);}

  @Override
  public void processDebugInfo(String debugInfo)
  {logger.info("DBTESTMONITOR -> processDebugInfo -> " + debugInfo);}
  
 }