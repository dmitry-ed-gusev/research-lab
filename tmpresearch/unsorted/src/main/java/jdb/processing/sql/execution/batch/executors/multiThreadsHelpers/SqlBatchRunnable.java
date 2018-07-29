package jdb.processing.sql.execution.batch.executors.multiThreadsHelpers;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ������ ����� ��������� ������ ��������� ������ ������ ������ ��� ��������������� ���������� sql-�����. ������ ��������
 * ������� ���������� ���������� � ������ �� ��� ����� ����� - �������, ����, ��������. ������� � �������� ����� ���� �������,
 * ���� ����� ������ ���� �� ����� - � ��������� ������ � ��� ����� �������� ������ � ������� �������� ��������� �� �����.
 * ������ ����� �� ������������� � ���������������� (����������) �������������, ��� ������� ������� ������ �������� ������
 * ��������������� ���������� sql-����� - ������
 * {@link jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor MultiThreadsSqlBatchExecutor}.
 *
 * @author Gusev Dmitry (�������)
 * @version 4.0 (DATE: 19.11.2010)
*/

// todo: ���� ���������� ������ ������ �� ������������ �������� stopOnError, � ������ �����������, ���� ��� �������. ???

public class SqlBatchRunnable implements Runnable
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(getClass().getName());

  // ����� ������, � ������� ����� ����������� ������ ����� (��� ����� run())
  private int                   threadNumber        = 0;
  // ������� - �������� �� ������ ����� ��������� ����������� � ������ (���� ������, �� ���� ����� ������������
  // ��������� ������� sql-�����)
  private boolean               isLastThread        = false;
  // ������ ����� ����� (���������� ��������), ������������ ����� �������
  private int                   partSize            = 0;
  // ��������� ������� ����� (���������� �������� � �������)
  private int                   remainder           = 0;
  // ������������ ��� ���������� � ����
  private DBConfig              dbConfig            = null;

  // ������ �� ����-�������, ������� ������ ����������� ����� �������� ������
  private ArrayList<String>     prefixBatch         = null;
  // ������ �� ��������������� ����������� sql-����. ��������������, ��� ������� ������ ���������� ��� ������������
  // ���� (�� ������� �������� �������������). ����� ����������� ����������� ������ ���� ���� ��� ���. ���� � ����� �����
  // select-�������, ��� ��������� �� �����.
  private ArrayList<String>     sqlBatch            = null;
  // ������ �� ����-��������, ������� ������ ����������� ����� ��������� �����
  private ArrayList<String>     postfixBatch        = null;

  // ������������ �� ���������� sql-�������� ����� �����������. �� ��������� - ��������.
  private boolean               useSqlFilter        = true;

  // ������ �� ��������� ������ - ������ ���� ������ ���� �������, ������ ������ ���� �� ������ (��-NULL).
  private final ArrayList<String>     allErrors;
  // ������ �� ��������� ������ - ��������� �������� ���� ������������ �������� �� ���� ������� (��������). �������
  // ������������ �������� ����������� �� ���� ���������� ��������. ������ ������ ���� �� ������ (��-NULL).
  private final TotalProcessedQueries totalQueries;


  /**
   * �����������. �������������� ��� ����������� ���� ������.
   * @param threadNumber int ����� ������. ������ ���� �������������.
   * @param isLastThread boolean �������� �� ������ ����� ��������� ����������� � ������ (������/����).
   * @param partSize int ������ ����� sql-����� ��� ����������. �������� ������ ���� �������������.
   * @param remainder int ������� sql-����� ��� ���������� (���� ���������� �������� � ����� �� ������� ������ ��
   * ���������� �������). ������� ������ ����������� � ��������� ����������� � ������ ������.
   * @param dbConfig DBConfig ������������ ��� ���������� � ����.
   * @param prefixBatch ArrayList[String] ������ �� ���������� sql-����.
   * @param sqlBatch ArrayList[String] ������ �� ��������������� ����������� sql-����.
   * @param postfixBatch ArrayList[String] ������ �� ����������� sql-����.
   * @param allErrors ArrayList[String] ������ �� ��������� ������, ������� ������ ��� ��������� �� ������� �� ����
   * �������.
   * @param totalQueries TotalProcessedQueries ������ �� ��������� ������, ������� ������ ���������� ����������� ��������
   * �� ���� ����������� �������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-��������.
  */
  public SqlBatchRunnable(int threadNumber, boolean isLastThread, int partSize, int remainder, DBConfig dbConfig,
   ArrayList<String> prefixBatch, ArrayList<String> sqlBatch, ArrayList<String> postfixBatch, ArrayList<String> allErrors,
   TotalProcessedQueries totalQueries, boolean useSqlFilter)
   {
    this.threadNumber = threadNumber;
    this.isLastThread = isLastThread;
    this.partSize     = partSize;
    this.remainder    = remainder;
    this.dbConfig     = dbConfig;
    // ����������� �����
    this.prefixBatch  = prefixBatch;
    this.sqlBatch     = sqlBatch;
    this.postfixBatch = postfixBatch;
    this.allErrors    = allErrors;
    this.totalQueries = totalQueries;
    this.useSqlFilter = useSqlFilter;
   }

  /**
   * ����� ���������� ������ � ��������� ������ ���������� ������� ���������� ������. ���� ������ ��� - �����
   * ���������� �������� NULL.
   * @return String �������� ������ � ���������� ��� �������� NULL. 
  */
  private String getErrors()
   {
    // ������������ ��������� ������ ������� ������
    String result = null;
    // ��� ������ ���������� ��������� ������� ��������� ������� ������, ���� ��������� �������� ������������� -
    // ������ ����������� �� �����. ����� ��� ������ ���������� �������� � ��� � �� ����/����������.
    String dbConfigErrors = DBUtils.getConfigErrors(dbConfig);
    if (!StringUtils.isBlank(dbConfigErrors))
     {result = "Can't process work! DB configuration had errors [" + dbConfigErrors + "]!";}
    // ��������� - �� ���� �� �������� ����. ������������� �� ������������! ����� ���������� �
    // ����������� ����� ���� �������.
    else if ((sqlBatch == null) || (sqlBatch.isEmpty()))
     {result = "Can't process work! SQL batch is empty or NULL!";}
    // ��������� - �� ����� �� ������ �� ��������� ������ - "������ ������ ���� �������"
    else if (allErrors == null)
     {result = "Can't process work! Link to [all errors list] is NULL!";}
    // ��������� - �� ����� �� ������ �� ��������� ������ - "��������� �������� ���� ������������ ��������"
    else if (totalQueries == null)
     {result = "Can't process work! Link to [total processed queries] is NULL";}
    // ����� ������ ������ ���� �������������
    else if (threadNumber <= 0)
     {result = "Can't process work! Thread number is negative or 0!";}
    // ������ ����� ����� ��� ���������� ������ ���� �������������
    else if (partSize <= 0)
     {result = "Can't process work! Batch part size is negative or 0!";}
    // ��������������� ����������� ����������
    return result;
   }

  @Override
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed", "ConstantConditions"})
  public void run()
   {
    // ������ ������������� ������, ��������� � ������� ������ ��� ����������. �������������� ���������� �����.
    ArrayList<String> currentThreadErrors = new ArrayList<String>();

    logger.debug("Thread #" + threadNumber + " starting. Checking parameters.");
    
    // �������� �������� ������ ���������������� ���������� ������� ������
    String classConfigErrors = this.getErrors();
    // ��������������� ���������� ������ ������, ���� �������� ������ ���������� ������� ������ �����
    if (StringUtils.isBlank(classConfigErrors))
     {
      logger.debug("All parameters OK. Processing work.");

      // ��������� � �������� ������� (������) � ������ sql-�������� ��� ������� ������. ���� ��� ����������
      // ������ sql-�������� �� ������ ������� ������� (������ �� ������ ���������� �������), �� ���� �������
      // ��������� ���������� ������ (������).
      int start  = partSize*(threadNumber - 1);
      int finish;
      // ���� ���� ������� � ������ ����� ��������� ��������� - ������������� �������� �������
      if ((remainder > 0) && (isLastThread)) {finish = (partSize*threadNumber) - 1 + remainder;}
      // ���� ������� ��� ��� ������ ����� ��������� �� ��������� - �������� ������� ����������
      else                                   {finish = (partSize*threadNumber) - 1;}

      // ����� ���������� ��������� � �������� ������� � ������ sql-�������� ��� ���������� � ������ ������ ����������
      // ���������, ��� ��� ��� �������� �� ������� �� ����� ������� sql-�����. ����� ���������, ��� �������� start �
      // finish ������������� ���� �����: start < finish
      int sqlBatchSize = sqlBatch.size();
      if ((start < sqlBatchSize) && (finish < sqlBatchSize) && (start < finish))
       {
        // ����� ������������� �������� �������� ��������� sql-����
        logger.debug("START [" + start + "] and FINISH [" + finish + "] values are correct. Processing.");
        Connection connection = null;
        Statement statement   = null;
        try
         {
          // ����������� � ����
          connection = DBUtils.getDBConn(dbConfig);
          statement  = connection.createStatement();

          // ������� ����������� sql-��������
          int sqlCounter = 0;
          // ��������� ����������� �������� �������� ����������� sql-��������
          int lastValueSqlCounter = 0;

          // ��������� ���������� ������� �������-����, ���� �� �� ����.
          if ((this.prefixBatch != null) && (!this.prefixBatch.isEmpty())) 
           {
            for (String sql : this.prefixBatch)
             {
              // ����������� try...catch ��������� ��� ����, ����� ��������� ���������� ������ sql-������� ��
              // ������� � ���������� ����� ������ ����������.
              try
               {
                // ���� ���������� �������� ��������� ���������� - ��������� ���������� ����� ����������� ������� �������
                if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
                else              {statement.executeUpdate(sql);}
                // ����������� ������� ������������ ��������
                sqlCounter++;
                // ���� ��������� ����������� ��� ������ ��������� (���������� ������ �������� ����������� ��������) ���
                // �������� - ����� ��������� � ����������  ���������� ������������ �������� � ������ ��������.
                if (sqlCounter % DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
                 {
                  int step = sqlCounter - lastValueSqlCounter;
                  //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                  // ����������� ����� ������� ����������� �� ���� ������� ��������. ����� ��������� ������, ��������
                  // �������, ����������� �� ����� ������� �� ������� ������ (�������� synchronized)
                  synchronized (totalQueries) {totalQueries.addTotal(step);}
                  lastValueSqlCounter = sqlCounter;
                 }
               }
              // ������������� ��������� �� ��� ���������� ������� (����� �� �������� ���� ����). ��������� � ���������
              // �� ��������� � ������ ������ ������� ������
              catch (SQLException e)
               {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sql + "]. Message: " + e.getMessage());}

              // ��� ����, ����� ������ ����� �� ������ ��� �������, ��������� ����. �����, ������� �������� �������
              // ��������� ������ �����. ��� �������� �� ������ �������� �����. ��� ���� ������ ����� (yield()) ��������
              // ������ ����� ���������� ����������, �� �� ��������� �����. ���������� (���������) ������ �����������
              // ������ ������� - sleep(ms).
              Thread.yield();
             }
           }

          // � ����� ��������� ����� sql-�������� � ������ ������
          for (int count = start; count <= finish; count++)
           {
            // ����������� try...catch ��������� ��� ����, ����� ��������� ���������� ������ sql-������� ��
            // ������� � ���������� ����� ������ ����������.
            try
             {
              // ���� ���������� �������� ��������� ���������� - ��������� ���������� ����� ����������� ������� �������
              if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sqlBatch.get(count)));}
              else              {statement.executeUpdate(sqlBatch.get(count));}
              // ����������� ������� ������������ ��������
              sqlCounter++;
              // ���� ��������� ����������� ��� ������ ��������� (���������� ������ �������� ����������� ��������) ���
              // �������� - ����� ��������� � ����������  ���������� ������������ �������� � ������ ��������.
              if (sqlCounter %DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
               {
                int step = sqlCounter - lastValueSqlCounter;
                //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                // ����������� ����� ������� ����������� �� ���� ������� ��������. ����� ��������� ������, ��������
                // �������, ����������� �� ����� ������� �� ������� ������ (�������� synchronized)
                synchronized (totalQueries) {totalQueries.addTotal(step);}
                lastValueSqlCounter = sqlCounter;
               }
             }
            // ������������� ��������� �� ��� ���������� ������� (����� �� �������� ���� ����). ��������� � ���������
            // �� ��������� � ������ ������ ������� ������
            catch (SQLException e)
             {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sqlBatch.get(count) + "]. Message: " + e.getMessage());}

            // ��� ����, ����� ������ ����� �� ������ ��� �������, ��������� ����. �����, ������� �������� �������
            // ��������� ������ �����. ��� �������� �� ������ �������� �����. ��� ���� ������ ����� (yield()) ��������
            // ������ ����� ���������� ����������, �� �� ��������� �����. ���������� (���������) ������ �����������
            // ������ ������� - sleep(ms).
            Thread.yield();
           }

          // ��������� ���������� ������� ��������-����, ���� �� �� ����.
          if ((this.postfixBatch != null) && (!this.postfixBatch.isEmpty()))
           {
            for (String sql : this.postfixBatch)
             {
              // ����������� try...catch ��������� ��� ����, ����� ��������� ���������� ������ sql-������� ��
              // ������� � ���������� ����� ������ ����������.
              try
               {
                // ���� ���������� �������� ��������� ���������� - ��������� ���������� ����� ����������� ������� �������
                if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
                else              {statement.executeUpdate(sql);}
                // ����������� ������� ������������ ��������
                sqlCounter++;
                // ���� ��������� ����������� ��� ������ ��������� (���������� ������ �������� ����������� ��������) ���
                // �������� - ����� ��������� � ����������  ���������� ������������ �������� � ������ ��������.
                if (sqlCounter % DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
                 {
                  int step = sqlCounter - lastValueSqlCounter;
                  //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                  // ����������� ����� ������� ����������� �� ���� ������� ��������. ����� ��������� ������, ��������
                  // �������, ����������� �� ����� ������� �� ������� ������ (�������� synchronized)
                  synchronized (totalQueries) {totalQueries.addTotal(step);}
                  lastValueSqlCounter = sqlCounter;
                 }
               }
              // ������������� ��������� �� ��� ���������� ������� (����� �� �������� ���� ����). ��������� � ���������
              // �� ��������� � ������ ������ ������� ������
              catch (SQLException e)
               {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sql + "]. Message: " + e.getMessage());}

              // ��� ����, ����� ������ ����� �� ������ ��� �������, ��������� ����. �����, ������� �������� �������
              // ��������� ������ �����. ��� �������� �� ������ �������� �����. ��� ���� ������ ����� (yield()) ��������
              // ������ ����� ���������� ����������, �� �� ��������� �����. ���������� (���������) ������ �����������
              // ������ ������� - sleep(ms).
              Thread.yield();
             }
           }

          // ����� ���������� ���������� ����� ������� � ������ �������� ���������� ��������
          int step = sqlCounter - lastValueSqlCounter;
          synchronized (totalQueries) {totalQueries.addTotal(step);}
         }

        // ������������� ��, ������� ����� ���������� ��� ���������� ����� ���� ������ run(). ��������� � ���������
        // �� ��������� � ������ ������ ������� ������
        catch (DBConnectionException e)   {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
        catch (DBModuleConfigException e) {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
        catch (SQLException e)            {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}

        // ����������� ��������� �� ����� ���������� � ���� (��������). ���� ��������� ������ - ������� �� � ������.
        finally
         {
          try                    {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}
          catch (SQLException e) {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
          
          // ���� � ������ �������� ������ � ��� ���� �������� � ������ ������ ������ - ������� �� � ����� ������ ������
          if ((currentThreadErrors != null) && (!currentThreadErrors.isEmpty()))
           {
            logger.debug("THREAD #" + threadNumber + " HAD ERRORS. SEE LOG.");
            // ��������� ��� �� �������� ������ � ����� ������ ��. ��� ���� ��������� ������ � ����� ������� ��.
            synchronized (allErrors) {allErrors.addAll(currentThreadErrors);}
           }
          // ���� �� � ������ �� ���� ������ - ������ ������� �� ���� � ���
          else {logger.debug("THREAD #" + threadNumber + " HAD NO ERRORS. ALL OK.");}

          // ���������� ��������� �� ��������� ������ ������
          logger.debug("THREAD " + threadNumber + " FINISHED!");
         }
       }
      // ���� � ��������� ���� ������ - ������ �� �����������, ��������� � ��� ��������� � �������������� ����������
      else {logger.error("START[" + start + "] and/or FINISH[" + finish + "] value(s) incorrect [BATCH SIZE = " + sqlBatchSize + "]!");}
     }
    // ������� � ��� �������� ������ ���������� ������� ������.
    else {logger.error("Thread config had error [" +  classConfigErrors + "]!");}
   }

 }