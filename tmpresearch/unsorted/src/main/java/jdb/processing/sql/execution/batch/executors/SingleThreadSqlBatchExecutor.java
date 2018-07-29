package jdb.processing.sql.execution.batch.executors;

import jdb.DBConsts;
import jdb.DBResources;
import jdb.config.batch.BatchConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.monitoring.DBProcessingMonitor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ����� �������� ����� ��� ���������� ������� sql-�������� (sql-batch). ������� ����������� � ����� ������ (�����
 * ������� ������ ������������). � ������� �������� ����������� ������ ��-SELECT ������� (������� ���� "SELECT..."
 * �����������������). ������ ����� ����� ������������ ��� ��������� ������ (�����������), ��� � ����� ������ �����
 * �������� ������ - SqlBatcher (������-����� ��� ������� ��������������/��������������� ���������� sql-������).
 *<br>
 * 16.11.2010 ��� ������� ����� ���������������� - {@link jdb.config.batch.BatchConfig BatchConfig}. ������
 * �������������� ����������� ���� ���������� �� ���������� ������. ��������� �������� ��������� ��. �
 * ������ {@link jdb.config.batch.BatchConfig BatchConfig}.
 * 
 * @author Gusev Dmitry (019gus)
 * @version 7.0 (DATE: 18.11.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class SingleThreadSqlBatchExecutor
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(SingleThreadSqlBatchExecutor.class.getName());

  /**
   * ������ ����� ��������� ��� ���������� � ����, ���������� � ������� config, ����� SQL-�������� - ���� (batch). �������
   * � ������ �� ������ ���� "SELECT..."-���������. �������� stopOnError ��������� - ������������� �� ���������� ������
   * �������� ��� ������������� ������ ��� ���.
   * @param config BatchConfig ������������ ��� ������ ���������� �����.
   * @return ArrayList [String] ������ ������, ��������� ��� ���������� ������ ��������.
   * @throws java.sql.SQLException ����������� ������ ��� �������� ������� Statement.
   * @throws jdb.exceptions.DBModuleConfigException ������ ������������ ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ��������������� ���������� � ����.
  */
  public static ArrayList<String> execute(BatchConfig config) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("executeSqlBatch: executing!");
    // ��������� ���������� ����� - ������ ��������� ������
    ArrayList<String> result = null;

    // ��������� ������������ �� ������, ���� ��� ���� - ������ �� ������!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else                                    {logger.debug("Batch configuration is OK. Processing.");}
    
    logger.debug("Processing batch. Size [" + config.getBatchSize() + "]");
    // ���������� ��������� ��� ��������� ��������. ��������� ���� ���.
    String monitorPrefix = StringUtils.trimToEmpty(config.getMonitorMsgPrefix());

    // ��������� ���������� � ���� ����� ����������� ���������� � �������� ����� �������� �������
    Connection connection = null;
    Statement statement  = null;
    try
     {
      // ����������� � ��������� ����. ���� ���������� � ���� ���������� �� ������� - ������ ������ ��
      // ����������� (��� ��� ������������ ��).
      connection = DBUtils.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. Statement object created.");
      // �������� ������ �� ��������� ������-��������
      DBProcessingMonitor monitor = config.getMonitor();
      
      // ���� ����������� ��������. ���� ������ �������� ������ stopOnError=true (������� ��� ������������� ������), ��
      // ��� ������������� �� ������ ���� ����� ������� � �������� false � ���������� ����� �����������.
      boolean continueFlag = true;

      // �� ������-������ ����������� ������� ���������� ������ ���� ��� ���������� ��� � ����� (�������) ������
      ArrayList<String> fullBatch = null;
      // ��������� ����-�������
      if ((config.getBatchPrefix() != null) && !config.getBatchPrefix().isEmpty())
       {
        fullBatch = new ArrayList<String>();
        fullBatch.addAll(config.getBatchPrefix().getBatch());
       }
      // ��������� ���������� ����������� ���������� ����
      if ((config.getBatchRunOncePrefix() != null) && !config.getBatchRunOncePrefix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchRunOncePrefix().getBatch());
       }
      // ��������� ���� �����
      if ((config.getBatchBody() != null) && !config.getBatchBody().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchBody().getBatch());
       }
      // ��������� ���������� ����������� ����������� ����
      if ((config.getBatchRunOncePostfix() != null) && !config.getBatchRunOncePostfix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchRunOncePostfix().getBatch());
       }
      // ��������� ����-��������
      if ((config.getBatchPostfix() != null) && !config.getBatchPostfix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchPostfix().getBatch());
       }

      // ***
      // �������������� �������� - �� ���� �� ���������� �������������� ����. � ��������, � ������ �������� ��� �������������,
      // �.�. ��� ����������� ������� .getConfigErrors() - ���� ���������� ������� � �������� ��������� ���� ����, �� �� �������
      // ��������. ��! ��� ��������� ��������� ���������� ������ (.getConfigErrors()) ������ ����� ����� ��� ����� �������, �.�.
      // ���������� �������� ������� ������ �� �������� ������������� ��. ������� - ������ �������� ��� ������������.
      if ((fullBatch != null) && (!fullBatch.isEmpty()))
       {
        logger.debug("Batch created OK. Processing.");
        // ��� ���� ��������� ����������. ������ ����������� ���� ����� � ��������, ������������ �����. ������� ������
        // ������������ (BatchConfig) ������ ���������. ���� ��� - ���-�� ������!
        if (fullBatch.size() != config.getBatchSize()) {logger.warn("Batches sizes mismatch! Maybe some errors? :)");}
        else                                           {logger.info("Batches sizes match. Processing.");}

        // ��������� �������� �� ����� ���� ���������� ����� (������ ���������� �����)
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, 0, fullBatch.size()));}

        // � ����� �������� �� ������� ����� (�� ���� �������� � ������(������)) � ��������� ��.
        // ����������� ������ ��-SELECT �������.
        Iterator<String> iterator = fullBatch.iterator();
        logger.debug("Starting batch processing. SQL filtering mode [" + config.isUseSqlFilter() + "]");
        // ������� ���������� ��������
        int counter = 0;
        // ��� (���������� ������������ ��������), ����� ������� ����� ������ ��������� ��������.
        int monitorMessageStep;
        if ((config.getOperationsCount() >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
            (config.getOperationsCount() <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
         {monitorMessageStep = config.getOperationsCount();}
        else
         {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

        // ���������� ��� �������� ������������ � ������ ������ �������
        String sql;
        while (iterator.hasNext() && continueFlag)
         {
          sql = iterator.next();
          // ������������� ������� ���� ������ ��� �������� �������
          //logger.debug("Trying to execute sql-query: [" + sql +"].");
          // ��������� ������ ������ � ����� try...catch, ����� ����� ���� ��������� ��������� ��� ������������� ��
          try
           {
            // ���� ������������ ���������� sql-�������� - ��������� ��!
            int executeResult;
            if (config.isUseSqlFilter()) {executeResult = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
            else                         {executeResult = statement.executeUpdate(sql);}
            // ��������� ���������� ������� ���������� ��� �������������� (���� ��������� ������� �� ����������� (1))
            if (executeResult != 1) {logger.warn("Sql query execute result <> 1 [" + executeResult + "]!");}
            // ���������� �������� ������������ ��������. ������� ������������� ������ ����� �������� ����������
            // �������, �.�. ���� �����-�� ������� ��������� �� �����, �� �������� ��������� ��� ��� �������.
            counter++;
            // ���� �� �������� ���������� ��� ����������� - ������� ���������� ���������. �����, ���� ���� �������,
            // ������� ��������� ��� �������� (���������� ������������/����� ����������)
            if (counter%monitorMessageStep == 0)
             {
              logger.debug("Processed: [" + counter + "/" + fullBatch.size() + "]");
              // ���� ���� ������� - �������� ��� � ���� ���������� �����
              if (monitor != null)
               {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, counter, fullBatch.size()));}
             }
            // ������������� ������� ���� ������ ��� �������� �������
            //logger.debug("Query executed. Result: [" + executeResult + "]");
           }
          // ��������� �� ��� ���������� �������
          catch (SQLException e)
           {
            // �������� � ��� �� ������
            logger.error(e.getMessage() + " SQL: [" + sql + "]");
            // ��������� �������� �� ������ � ���������
            if (result == null) {result = new ArrayList<String>();}
            result.add("Error message: " + e.getMessage() + ", ");
            // ���� �������� ������ stopOnError=true, �� ���������� ���� ����������� ��������
            if (config.isStopOnError()) {continueFlag = false;}
           }
         } // END OF WHILE CYCLE
        
        logger.debug("TOTAL processed: [" + counter + "/" + fullBatch.size() + "]");
        // ���� ���� ������� - �������� ���
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, counter, fullBatch.size()));}
        logger.debug("Batch processing complete.");
       }
      // ���� ����� ��������� ������-������ ����������� ������� ������� ��������� ������ ���� - ��� ������! ���������
      // ������ �� ����. ������ ������� � ���. ��������� � ����� �������� - ��. ������� ����, ���������� ***.
      else {logger.error("Batch was created with error: result batch is empty (or null)!");}
     }
    // ����������� ����������� �������. �� ������������� ������ � ����� ���������� �����, ����� ����� ���� ���������
    // ��������� ���������� �����. ��� ����� �� �� ��������������� - ���������� ����� ������ � ������������� ���������
    // � ��������� ������ ��.
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // ����������� ����������
    return result;
   }

 }