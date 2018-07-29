package jdb.processing.sql.execution.batch.executors;

import jdb.DBConsts;
import jdb.DBResources;
import jdb.config.batch.BatchConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.SqlBatchRunnable;
import jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.TotalProcessedQueries;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ����� �������� ����������� ����� ��� ��������������� ���������� sql-������. ������� ����������� �� ������ �������
 * (����� ������� ������ �������������). � ������� �������� ����������� ������ ��-SELECT ������� (������� ���� "SELECT..."
 * �����������������).������ ����� ����� ������������ ��� ��������� ������ (�����������), ��� � ����� ������ �����
 * �������� ������ - {@link jdb.processing.sql.execution.batch.SqlBatcher SqlBatcher} (������-����� ��� �������
 * ��������������/��������������� ���������� sql-������). 
 * @author Gusev Dmitry (019gus)
 * @version 6.0 (DATE: 19.11.2010)
*/

// todo: �������������� ���������� �� ������������ �������� stopOnError, � ������ �����������, ���� ��� �������. ???

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class MultiThreadsSqlBatchExecutor
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(MultiThreadsSqlBatchExecutor.class.getName());

  /** ������������ ������ �������, � ������� ��������� ��� ������ ���������� sql-����� */
  private static final String THREADS_GROUP_NAME     = "sqlBatchThreads";

  /**
   * ������ ����� ��������� ��� ���������� � ����, ���������� � ������� config, ����� SQL-�������� - ���� (batch). �������
   * � ������ �� ������ ���� "SELECT..."-���������.
   * @param config BatchConfig
   * @return ArrayList[String] �������������� (String) ������ ������, ��������� ��� ���������� ������ ��������.
   * @throws java.sql.SQLException ����������� ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ��������������� ���������� � ����.
  */
  @SuppressWarnings({"MethodWithMultipleReturnPoints"})
  public static ArrayList<String> execute(BatchConfig config) throws SQLException, DBModuleConfigException, DBConnectionException
   {
    logger.debug("execute: executing!");
    // ��������� ���������� ����� - ������ ��������� ������. ���������� ��������� ��� final - �.�. �
    // ������ ���������� �������������� ������ �� ���� ����������� �������.
    final ArrayList<String> result;
    // ��������� ���������� ��� �������� � ������� �� ������� � �������� ������������ ��������. ��������������
    // ���������� �����. ���������� final - � ������ ���������� �������������� ������ �� ���� ����������� �������.
    final TotalProcessedQueries total  = new TotalProcessedQueries();
    // ��������� ������������ �� ������, ���� ��� ���� - ������ �� ������ (���������� ��)!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else                                    {logger.debug("Batch configuration is OK. Testing connection to DBMS.");}
    // ��������� ���������� � ���� ����� ����������� ���������� � �������� ����� �������� �������
    Connection connection = null;
    Statement  statement  = null;
    try
     {
      // ����������� � ��������� ����. ���� ���������� � ���� ���������� �� ������� - ������ ������ ��
      // ����������� (��� ��� ������������ ��).
      connection = DBUtils.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. All OK.");
     }
    // ����������� ����������� ������� (��������)
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // � ����������� �� ������� ���� ����� ���������� ����� ����� ����������� ��-�������. ���� ���� ����� ����� - �������
    // ��������������� �� ����� - ��� ��������-��������� ����� ��������� � ����� ������. ������ �� ��� � ��������.
    if ((config.getBatchBody() == null) || (config.getBatchBody().isEmpty()))
     {
      logger.debug("No batch body. Processing prefixes and postfixes.");
      // �.�. ��� ���� �����, ������� ����������� ������������, �� ���������� ����� ����� ����� ���������
      // � ����� ������. ��� ����� ����� ����������� ����� SingleThreadSqlBatchExecutor.
      result = SingleThreadSqlBatchExecutor.execute(config);
     }
    // ���� �� � ���� ����� ���� ���� ���� ������ (���� �� �����), �� �������������� � ��������
    else
     {
      logger.debug("Batch body is not empty. Processing multi-threads.");
      // ������� �������������� ���������� ����������
      result = new ArrayList<String>();

      // ����� ���� �������� (������������� �������) ��������� �� ������ ����� ����������� ��� ��� ���������� �����.
      // ��� ������ ��������� ���������� ����������� ���������� ����.
      ArrayList<String> batchPrefix = null;
      if ((config.getBatchRunOncePrefix() != null) & (!config.getBatchRunOncePrefix().isEmpty()))
       {
        batchPrefix = new ArrayList<String>();
        // ��������� ������� �������-����
        if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
         {batchPrefix.addAll(config.getBatchPrefix().getBatch());}
        // ��������� ���������� ����������� �������-����
        batchPrefix.addAll(config.getBatchRunOncePrefix().getBatch());
        // ��������� ������� ��������-����
        if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
         {batchPrefix.addAll(config.getBatchPostfix().getBatch());}
       }
      // ���� ���� ���������� ������������ �����-�������� - ������� � ���
      else {logger.debug("Run-once prefix batch is empty!");}
      // ������ ��������� ���������� ����������� ����������� ����
      ArrayList<String> batchPostfix = null;
      if ((config.getBatchRunOncePostfix() != null) & (!config.getBatchRunOncePostfix().isEmpty()))
       {
        batchPostfix = new ArrayList<String>();
        // ��������� ������� �������-����
        if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
         {batchPostfix.addAll(config.getBatchPrefix().getBatch());}
        // ��������� ���������� ����������� ��������-����
        batchPostfix.addAll(config.getBatchRunOncePostfix().getBatch());
        // ��������� ������� ��������-����
        if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
         {batchPostfix.addAll(config.getBatchPostfix().getBatch());}
       }
      // ���� ���� ���������� ������������ �����-��������� - ������� � ���
      else {logger.debug("Run-once postfix batch is empty!");}

      // --------------------------------------------------------------------------------------------------------------
      // � ������ ������ �� ������������ ���������� �������, ������ ����� ���� ����� ��� ������ ������, ������� �����
      // - ���������, ������� ���������� ��� ����������� ������������ ���� �������.
      //
      // ���������� ������� (����� ���������� ����������� :), �� � �������� ��������). ���� �������� �������� "���������� ����������"
      // ����������� - ����� ��� ��������, ���� �� �� �����������, �� ����� �������� �� ���������. ���������� ������� � ����������
      // ����� ���� (� �����) ��������������� � ����������� �� ����������� ������� ����� � ������ ���������� �������. ����� ����������
      // ������� (� �����. ���������� � ����) �� ������ ��������� �������� DBConsts.MAX_DBMS_CONNECTIONS.
      int threadsCount;
      int connNumber = config.getDbmsConnNumber();
      if ((connNumber  >= DBConsts.MIN_DBMS_CONNECTIONS) && (connNumber <= DBConsts.MAX_DBMS_CONNECTIONS))
       {threadsCount = connNumber;}
      else if (connNumber > DBConsts.MAX_DBMS_CONNECTIONS)
       {threadsCount = DBConsts.MAX_DBMS_CONNECTIONS;}
      else
       {threadsCount = DBConsts.MIN_DBMS_CONNECTIONS;}
      // ������������ ������ ����� ���� �����, ����������� � ����� ������
      final int partSize;
      // ������� �����, ����������� � ��������� ������
      final int remainder;
      // ������ �� ��������� ������ �������� �� �����, � ����������� �� ���������� �������. ���� ������ ������ ��������
      // ������ ���������� �������, �� ��������� ����� ����������� ��-������� (����� ����� �������� ���������� �������).
      if (config.getBatchSize() >= threadsCount*DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL)
       {
        logger.debug("Batch size > threadsCount*RATIO [" + threadsCount + "*" + DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL + "]. Processing.");
        partSize  = config.getBatchSize()/threadsCount;
        remainder = config.getBatchSize()%threadsCount; // <- �������� ������� �� �������������� �������
       }
      // �������� ���������� ��� ������, ����� ������ ������ �������� ������ ���������� �������. ��� ������� ������
      // ���������� � ���������� ������� - ����� ����� ����� ����!
      else
       {
        logger.debug("Batch size < threadsCount. Processing.");
        partSize     = config.getBatchSize();
        remainder    = 0;
        threadsCount = 1;
       }
      // ����� ���������� ���� �������� - ������� ���������� ��������� � ���������� ������
      logger.debug("PARAMETERS: [partSize=" + partSize + "], [remainder=" + remainder + "], [threadsCount=" + threadsCount + "]");
      // --------------------------------------------------------------------------------------------------------------

      // ������������ ����� ���������� ��������, ������� ���������� ����� ��������� ������� ������ (������� ��, ���
      // ��� ������� ������� � �������� ����� ��������� �� ��������� � ����� ���).
      int sqlCount = 0;
      // ������ ��������
      if ((batchPrefix != null)  && (!batchPrefix.isEmpty()))  {sqlCount += batchPrefix.size();}
      // ������ ���������
      if ((batchPostfix != null) && (!batchPostfix.isEmpty())) {sqlCount += batchPostfix.size();}
      // ������ ����
      sqlCount += config.getBatchBodySize();
      // ������ ������������ ��� ������� ������ ��������
      if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
       {sqlCount += config.getBatchPrefixSize()*threadsCount;}
      // ������ ������������ ��� ������� ������ ���������
      if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
       {sqlCount += config.getBatchPostfixSize()*threadsCount;}

      logger.debug("Starting batch. Size [" + sqlCount + "]. SQL filtering mode [" + config.isUseSqlFilter() + "]");
      // ���������� ��������� ��� ��������� ��������. ��������� ���� ���.
      String monitorPrefix = "";
      // �������� ������ �� �����-�������
      DBProcessingMonitor monitor = config.getMonitor();
      // ���� ���� ������� - ������������ �������. ����� ������ ��������� �������� �� ����� ���� ���������� ����� (������)
      if (monitor != null)
       {
        if (!StringUtils.isBlank(config.getMonitorMsgPrefix())) {monitorPrefix = config.getMonitorMsgPrefix();}
        monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, 0, sqlCount));
       }

      // ������ �������� ��������������� ��������.
      // ������ ������� ��� ������������� ���� �������, ��������� ��� ���������� ��������
      ThreadGroup threadGroup = new ThreadGroup(THREADS_GROUP_NAME);
      // � ����� ������� ����������� ���������� �������, ������� � ��������� sql-����
      logger.debug("Starting threads generation cycle.");
      for (int i = 1; i <= threadsCount; i++)
       {
        // ������� - �������� �� ������ ����� ���������, ����������� � ������. ���������� ���������� �������
        // ������������� �����������/��������������� ����
        boolean isLastThread = (i == threadsCount);
        // ������� ��������� ������, ����������� ����� run() ������ ������
        SqlBatchRunnable sqlBatchRunnable = new SqlBatchRunnable(i, isLastThread, partSize, remainder, config.getDbConfig(),
         /*�������*/config.getBatchPrefix().getBatch(), /*����*/config.getBatchBody().getBatch(),
         /*��������*/config.getBatchPostfix().getBatch(), result, total, config.isUseSqlFilter());
        // � ��������� ������ ������� ������� ��� ���� �����
        Thread sqlBatchThread = new Thread(threadGroup, sqlBatchRunnable);
        // ��������� ��������� �����
        sqlBatchThread.start();
       }

      // ���� ��������� ���� ������� � ������ (������� �� ���� ���������� ���������)
      logger.debug("WAITING FOR ALL THREADS STOP IN GROUP [" + THREADS_GROUP_NAME + "]...");
      // ������� �������� ����� �������� ��������� ���� �������
      int counter = 0;
      // ���������� ��� ����������� ���������� �������� �������� ������������ ��������
      int lastCounter = -1;
      // ��������� ��� (���������� ������������ ��������), ����� ������� ����� ���������� ��������� ��������.
      int monitorMessageStep;
      if ((config.getOperationsCount() >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
          (config.getOperationsCount() <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
       {monitorMessageStep = config.getOperationsCount();}
      // ���� ������������� �������� ���������� �������� ������� �� ��������� ������� - ��������� max ��������
      else {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}
      // ���� �������� ���������� ���� ������� � ������ ���������� � ��������� ����������
      do
       {
        if (counter%DBConsts.WAIT_CYCLE_STEPS_COUNT == 0)
         {
          int totalProcessed = total.getTotal();
          if ((lastCounter != totalProcessed) && ((totalProcessed - lastCounter) > monitorMessageStep))
           {
            logger.debug("TOTAL processed : [" + totalProcessed + "/" + config.getBatchSize() + "]");
            // ���� ���� ������� ������� �������� - ������� ��� ���������
            if (monitor != null)
             {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, totalProcessed, sqlCount));}
            lastCounter = totalProcessed;
           }
         }
        counter++;
       }
      // ���� �����������, ���� �� ���������� ��� ������.
      // todo: ��� "���������" ������ �� ������� �������� ������������� "�������" �����
      while(threadGroup.activeCount() > 0);

      // ��� � ���� ����� ��� ������ ���������. ������� ���������� ���������.
      logger.debug("ALL THREADS IN GROUP [" + THREADS_GROUP_NAME + "] FINISHED SUCCESSFULLY.");
      // ����� ��������� ���������� ���� ������� ��� ��� ������� ���� � ��������. ���� ���������� ��� ������ -
      // ������� ��������� �������� (���������).
      int totalProcessed = total.getTotal();
      if (lastCounter != totalProcessed)
       {
        logger.debug("TOTAL processed : [" + totalProcessed + "/" + config.getBatchSize() + "]");
        // ���� ���� ������� ������� �������� - ������� ��� ���������
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, totalProcessed, sqlCount));}
       }
     }
    
    // ���� � �������������� ������ ������ ���� ��������� ������ - ��, ���� �� ������ �� ���� ��������� - �����
    // ������ ������� �������� null.
    if (!result.isEmpty()) {logger.warn("There are errors in threads. See log."); return result;}
    else                   {logger.debug("There are no errors in threads. All OK."); return null;}
    
   }

 }