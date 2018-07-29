package jdb.model.integrity;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ����� ��������� ������ ������� �� ��� �������� �� ����������� - ������ �������� ������ �������� ���������
 * ���� ��� ���� ������� (primary key). ��� ������� �������� ������ � ������� �������� ��������. ��� ������� ��
 * ����� ���� ������ (������ ������ ��� null).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 27.07.2010)
*/

public class TableIntegrityModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 8194532538337172710L;

  /** ���������-������ ������� ������ */
  private transient Logger logger = Logger.getLogger(getClass().getName());

  /** ������ �������� �������� ����� ��� ���� ������� �������. */
  private           ArrayList<Integer> keysList = null;

  public TableIntegrityModel(String tableName) throws DBModelException {super(tableName);}

  public ArrayList<Integer> getKeysList() {return keysList;}
  public void setKeysList(ArrayList<Integer> keysList) {this.keysList = keysList;}

  /**
   * ����� ���������� csv-������ ���� ������, ������� �������� � ������ ������ ����������� �������. ���� ������
   * ������� ������ �� null-������, �� ����� ������ �������� null.
   * ������ ����� �������� null-safe - ��������� ������������ null-��������.
   * @return String CSV-������ ������ ������� ��� �������� null.
  */
  public String getCSVKeysList()
   {
    String        result  = null;
    StringBuilder csvList = null;
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      for (Integer key : this.keysList)
       {
        // ���� ��������� ���������� �������� �� ����� - ��������
        if (key != null)
         {
          // ���� ��� �� ��������������� ��������� - �������������
          if (csvList == null) {csvList = new StringBuilder();}
          // ���� �� ��������� ��� ��������������� - � ��� ��� ���� ����, ������� � ��� �������
          else {csvList.append(", ");}
          csvList.append(key);
         }
       }
     }
    if (csvList != null) {result = csvList.toString();}
    // ���������� ���������
    return result;
   }

  /**
   * ����� ��������� ��� ���� ������������� ���� � ������ ������ ������ �������.
   * @param key int ����������� � ������ ����.
  */
  public void addKey(int key)
   {
    if (this.keysList == null) {this.keysList = new ArrayList<Integer>();} 
    this.keysList.add(key);
   }

  /**
   * ����� ���������� ������� ������� � ��������, ���������� � �������� ���������. ��������� ������ ������ - ������
   * ������, ������� ���� � ������� �������, �� ������� ��� � �������-���������. �����, ������� ���� � �������-���������,
   * �� ������� ��� � ������� ������� - � ��������� �� ��������. ���� �������-��������=null, �� ����� ������ �������� null.
   * ���� ������ ������ �������-��������� ���� - ����� ���������� ������ ������ ������� �������, ���� �� �� ����, ����
   * �� ����, �� ������������ �������� null. �������������� �������� lightCheck ��������� ������� �������� - �������
   * ����� (lightCheck=true) � ����������� (lightCheck=false). � ������� ������ ������������ ���������� ������ �, ����
   * ���������� ����������, �� ������� ��������� ����������� � ����� ���������� �������� null, ���� �� ���������� ������,
   * �� ����� �������� ������ ������ �� ���������� � ������ ������� (������� ����). � ����������� ������ ������ ������
   * ������������ ������, ���������� �� ����, ��������� �� �� ����������.
   * ����� �������� null-safe - �� ��������� ������������ null ����� � ������ ������ - ��� ����� ������ ������������.
   * @param table TableIntegrityModel ������ ������� ��� ��������� � ������� �������.
   * @param lightCheck boolean ���������/���������� ������������/�������� ������ ��������� ������.
   * @param monitor ProcessMonitor �����-�������, ������� ��������� ���������������� �� ����������� �������� ���������.
   * @param processedCount int ��� ������ ��������� ��� �������� �������� (���������� ������������ �����, ����� �������
   * ����� �������� ��������� ��������). 
   * @return ArrayList[Integer] ������ ������, ������� ���� � ������� �������, �� ������� ��� � ������� ��������� ���
   * �������� null, ���� ����� ����� �� �������.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table, boolean lightCheck, DBProcessingMonitor monitor, int processedCount)
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}
    
    ArrayList<Integer> result = null;
    // ���� � ������� ������� ��� ������ - ������ � ����������, ����� ���������� �������� null. ���� �� �
    // ������� ������� ���� �����, �� �������� ������.
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      // ���� �������-�������� ����� �������� null - ���������� ������, ���������� null.
      if (table != null)
       {
        // ���� �������-�������� �� ����� (�� null), �� ���� ������ �� ������, �� ���������� ������ ������
        // ������� �������
        if ((table.keysList == null) || (table.keysList.isEmpty())) {result = this.keysList;}
        // ���� �� �������-�������� �� null � �� ���� ������ �� ������ - ����� ��������
        else
         {
          // ���� �������� ������� �������� (lightCheck=true), �� ���������� ����� ������� ������ ������. ���� ����� ���������,
          // �� ��� ������� �������� - ������� �� ��������� null (����������). ���� �� ������� �������� ���������
          // (lightCheck=false), �� ������ ���������� ����� ����� ������ �������� ������ ������ (�� �����, ��������� �� �� ����������).
          if (!(lightCheck && (table.keysList.size() == this.keysList.size())))
           {
            // ��� ������������ � ����������� ������� ��� ����������� �������� ��� ������
            int processedStep;
            if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) && (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
             {processedStep = processedCount;}
            else
             {processedStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}
            // ������� �������� �����
            int counter = 0;
            // � ����� �������� �� ������ ������ ������� ������� � ���������� ��� �� ������� ������ �������-���������.
            for (int currentKey : this.keysList)
             {
              if (/**(currentKey != null) && */(!table.keysList.contains(currentKey)))
               {
                if (result == null) {result = new ArrayList<Integer>();}
                result.add(currentKey);
               }
              // ���������� ����� � ����� � ������� (���� �� ����)
              if (counter%processedStep == 0)
               {
                logger.debug("Processed [" + counter + "/" + this.keysList.size() + "]");
                // ���� ���� ������� - ������� � ���� ������
                if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + counter + " / " + this.keysList.size());}
               }
              counter++;
             }
            // ����� ��������� ����� ������� �� ��������� ���� ������� ������
            logger.debug("Processed [" + counter + "/" + this.keysList.size() + "]");
            // ���� ���� ������� - ������� � ���� ������
            if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + counter + " / " + this.keysList.size());}
            
           } // ���� if � ������ for

         }
       }
     }
    return result;
   }

  /**
   * ����� ���������� ������� ������� � ��������, ���������� � �������� ���������. ��������� ������ ������ - ������
   * ������, ������� ���� � ������� �������, �� ������� ��� � �������-���������. �����, ������� ���� � �������-���������,
   * �� ������� ��� � ������� ������� - � ��������� �� ��������. ���� �������-��������=null, �� ����� ������ �������� null.
   * ���� ������ ������ �������-��������� ���� - ����� ���������� ������ ������ ������� �������, ���� �� �� ����, ����
   * �� ����, �� ������������ �������� null. �������������� �������� lightCheck ��������� ������� �������� - �������
   * ����� (lightCheck=true) � ����������� (lightCheck=false). � ������� ������ ������������ ���������� ������ �, ����
   * ���������� ����������, �� ������� ��������� ����������� � ����� ���������� �������� null, ���� �� ���������� ������,
   * �� ����� �������� ������ ������ �� ���������� � ������ ������� (������� ����). � ����������� ������ ������ ������
   * ������������ ������, ���������� �� ����, ��������� �� �� ����������.
   * ����� �������� null-safe - �� ��������� ������������ null ����� � ������ ������ - ��� ����� ������ ������������.
   * @param table TableIntegrityModel ������ ������� ��� ��������� � ������� �������.
   * @param lightCheck boolean ���������/���������� ������������/�������� ������ ��������� ������.
   * @return ArrayList[Integer] ������ ������, ������� ���� � ������� �������, �� ������� ��� � ������� ��������� ���
   * �������� null, ���� ����� ����� �� �������.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table, boolean lightCheck)
   {return this.compareTo(table, lightCheck, null, -1);}

  /**
   * ����� ���������� ������� ������� � ��������, ���������� � �������� ���������. ��������� ������ ������ - ������
   * ������, ������� ���� � ������� �������, �� ������� ��� � �������-���������. �����, ������� ���� � �������-���������,
   * �� ������� ��� � ������� ������� - � ��������� �� ��������. ���� �������-��������=null, �� ����� ������ �������� null.
   * ���� ������ ������ �������-��������� ���� - ����� ���������� ������ ������ ������� �������, ���� �� �� ����, ����
   * �� ����, �� ������������ �������� null. ��� ��������� ������ � ������� ������� ������ ������ ������������ ������ ������,
   * ��� ����������� �� ���������� �� (������) ����������.
   * @param table TableIntegrityModel ������ ������� ��� ��������� � ������� �������.
   * @return ArrayList[Integer] ������ ������, ������� ���� � ������� �������, �� ������� ��� � ������� ��������� ���
   * �������� null, ���� ����� ����� �� �������.
  */
  public ArrayList<Integer> compareTo(TableIntegrityModel table)
   {return this.compareTo(table, false);}

  /**
   * ������������� ����� ��������� ����������� ���� ������. �� ��������� � ������������ ������� ������ ����� ����
   * �������������� ������� �������������� (~5-7%) �� ������ ������� (��� ����) � ������� ������� ������ (>512MB).
   * @param table TableIntegrityModel ������ ������� ��� ��������� � ������� �������.
   * @param lightCheck boolean ���������/���������� ������������/�������� ������ ��������� ������.
   * @param threadsNumber int
   * @param monitor DBProcessingMonitor
   * @param processedCount int
   * @return ArrayList[Integer] ������ ������, ������� ���� � ������� �������, �� ������� ��� � ������� ��������� ���
   * �������� null, ���� ����� ����� �� �������.
  */
  public ArrayList<Integer> multiThreadsCompareTo(final TableIntegrityModel table, boolean lightCheck,
   int threadsNumber, DBProcessingMonitor monitor, int processedCount)
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    ArrayList<Integer> result = null;
    // ���� � ������� ������� ��� ������ - ������ � ����������, ����� ���������� �������� null. ���� �� �
    // ������� ������� ���� �����, �� �������� ������.
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      // ���� �������-�������� ����� �������� null - ���������� ������, ���������� null.
      if (table != null)
       {
        // ���� �������-�������� �� ����� (�� null), �� ���� ������ �� ������, �� ���������� ������ ������
        // ������� �������
        if ((table.keysList == null) || (table.keysList.isEmpty())) {result = this.keysList;}
        // ���� �� �������-�������� �� null � �� ���� ������ �� ������ - ����� ��������
        else
         {
          // ���� �������� ������� �������� (lightCheck=true), �� ���������� ����� ������� ������ ������. ���� ����� ���������,
          // �� ��� ������� �������� - ������� �� ��������� null (����������). ���� �� ������� �������� ���������
          // (lightCheck=false), �� ������ ���������� ����� ����� ������ �������� ������ ������ (�� �����, ��������� �� �� ����������).
          if (!(lightCheck && (table.keysList.size() == this.keysList.size())))
           {
            // ��� ������������ � ����������� ������� ��� ����������� �������� ��� ������
            //int processedStep;
            //if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) && (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
            // {processedStep = processedCount;}
            //else
            // {processedStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

            // ��������� ���������� ����������� �������. ����������� - DBConsts.MAX_THREADS. 
            int threadsCount = Math.min(DBConsts.MAX_THREADS, threadsNumber) ;
            int currentListSize = this.keysList.size();
            int foreignListSize = table.keysList.size();
            // ���� ���������� �������� (������������ ���� �������) ����� �������� ������ ������������� ���������� �������,
            // �� ��� ������ � ������� � ����� ����� ����. ���� �� ������ - ���������� ������� ����� ����� ����������
            // ����� ������ �� ���� �������.
            if (currentListSize*foreignListSize <= threadsCount) {threadsCount = 1;}
            else
             {
              threadsCount = Math.min(Math.min(currentListSize, foreignListSize), threadsCount);
              // ���� ������������ ���������� ������� ������ ��������� - ������������� ���
              //if (threadsCount > DBConsts.MAX_THREADS) {threadsCount = DBConsts.MAX_THREADS;}
             }

            // ������ ����� ������ ������ ������� ���������, �������(�����) ����� ������������ ���� �����
            final int partSize;
            partSize = currentListSize/threadsCount;
            // ������� ������ ������ �������-���������, ������� ����� �������������� ��������� �������
            final int remainder;
            remainder = currentListSize%threadsCount;

            // ����� ������ ������ ������� ������� ��� ������� �� ���� �������
            final ArrayList<Integer> currentKeysList = new ArrayList<Integer>();
            currentKeysList.addAll(this.keysList);

            // ���������� ����� ��� ���������� ���������� ������������ �������� �� ���� ������� ������
            class Total
             {
              // ����� ������ ����� �����
              private int total = 0;
              // ���������� ������ ����� ������������ ��������
              public void addTotal(int count) {total += count;}
              // ��������� ������ ����� ������������ ��������
              public int  getTotal() {return total;}
             }
            // ��������� ���������� ��� �������� � ������� �� ������� � �������� ������������ ��������
            final Total total = new Total();

            // ������ ��� ���� ����������� �������
            ThreadGroup group = new ThreadGroup("tableIntegrityThreads");

            // ����� �������������� ������ ������. � ��� ����� ���������� ���� �������.
            final ArrayList<Integer> allThreadsResultList = new ArrayList<Integer>();

            // � ����� ������� ����������� ���������� ������� ��� ���������
            for (int i = 1; i <= threadsCount; i++) 
             {
              // ��������� ���������� ��� ������� � ������ ���������� ������� �� ������ ������ (������)
              final int allThreadsCount = threadsCount;
              // ��������� ���������� ��� ������� � ������ ������ �� ������ ������
              final int threadNumber    = i;
              // ����� ������ ������ �������-��������� ��� ������� ������ (�� ������ ����� ���� �����)
              final ArrayList<Integer> foreignKeysList = new ArrayList<Integer>();
              foreignKeysList.addAll(table.keysList);
              // �������������� ������ ������, ������� ���� � ������� �������, �� ��� � ��� ����� ������ ������
              // �������-���������, ������� (�����) ������������ ������ �����
              final ArrayList<Integer> currentThreadResult = new ArrayList<Integer>();
              // �������� ������ ������
              new Thread
               (group,
                new Runnable()
                 {
                  // ����� ������� ������ �� ����������
                  public void run()
                   {
                    logger.debug("Thread # " + threadNumber + " started!");
                    // ��������� � �������� ������� (������) � ������ ������ �������-��������� ��� ������� ������. ���� ���
                    // ���������� ������ �� ������ ������� ������� (������ �� ������ ���������� �������), �� ���� �������
                    // ��������� ���������� ������ (������).
                    int start  = partSize*(threadNumber - 1);
                    int finish;
                    // ���� ���� ������� � ������ ����� ��������� ��������� - ������������� �������� �������
                    if ((remainder > 0) && (threadNumber == allThreadsCount)) {finish = (partSize*threadNumber) - 1 + remainder;}
                    // ���� ������� ��� ��� ������ ����� ��������� �� ��������� - �������� ������� ����������
                    else               {finish = (partSize*threadNumber) - 1;}
                    // ������� ���������� �����
                    int counter = 0;
                    int lastCounter = 0;
                    // � ����� ����� �������� �� ����� ����� ������ ������ ������� �������-��������� � ��������� ��
                    // ������� � ����� ����� ������ ������ ������� �������
                    for (int count = start; count <= finish; count++)
                     {
                      // �������� ���� �� ������ ������ ������� �������
                      Integer currentKey = currentKeysList.get(count);
                      // ���� ������ ����� ��� � ������ ������ �������-��������� - ��������� ���� � ����������
                      if ((currentKey != null) && (!foreignKeysList.contains(currentKey)))
                       {currentThreadResult.add(currentKey);}
                      // ����������� ������� ��������
                      counter++;
                      // ���� ��������� ����������� ��� ������ ��������� ��� �������� - ����� ��������� � ����������
                      // ���������� ������������ �������� � ������ ��������.
                      if (counter%100 == 0)
                       {
                        int step = counter - lastCounter;
                        //logger.debug("Thread #" + threadNumber + ". Processed: " + counter + "/" + (finish-start));
                        // ����������� ����� ������� ����������� �� ���� ������� ��������. ������ ����������� �� �����
                        // ������� �� ������� ������ (�������� synchronized)
                        synchronized (total) {total.addTotal(step);}
                        lastCounter = counter;
                       }
                      // ��� ����, ����� ������ ����� �� ������ ��� �������, ��������� ����. �����, ������� �������� �������
                      // ��������� ������ �����. ��� �������� �� ������ �������� �����. ��� ���� ������ ����� (yield()) ��������
                      // ������ ����� ���������� ����������, �� �� ��������� �����. ���������� (���������) ������ �����������
                      // ������ ������� - sleep(ms).
                      Thread.yield();
                      //try {Thread.sleep(5);}
                      //catch(InterruptedException e) {logger.error(e.getMessage());}

                     } // ����� ����� FOR
                    // ����� ���������� ���������� ����� ������� � ������ �������� ���������� ��������
                    int step = counter - lastCounter;
                    synchronized (total) {total.addTotal(step);}
                    // ���� ���-�� ���� ��������� � �������������� ���� �������� ������, �� ���� ��������
                    // ��� ������ � ����� �������������� ������
                    synchronized (allThreadsResultList)
                     {if (!currentThreadResult.isEmpty()) {allThreadsResultList.addAll(currentThreadResult);}}
                   }
                 }
               ).start();
             }

            // ���� ��������� ���� ������� � ������
            logger.info("WAITING FOR ALL THREADS STOP...");
            // ������� �������� ����� �������� ��������� ���� �������
            int counter = 0;
            // ���������� ��� ����������� ���������� �������� �������� ������������ ��������
            int lastCounter = -1;
            // ��� (���������� ������������ ��������), ����� ������� ����� ������ ��������� ��������.
            int monitorMessageStep;
            if ((processedCount >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
                (processedCount <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT)) {monitorMessageStep = processedCount;}
            else {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

            // ���� �������� ���������� ���� ������� � ������ ���������� � ��������� ����������
            do
             {
              if (counter%25000 == 0)
               {
                int totalProcessed = total.getTotal();
                if ((lastCounter != totalProcessed) && ((totalProcessed - lastCounter) > monitorMessageStep))
                 {
                  logger.debug("TOTAL processed : [" + totalProcessed + "/" + currentListSize + "]");
                  // ���� ���� ������� ������� �������� - ������� ��� ���������
                  if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + totalProcessed + " / " + currentListSize);}
                  lastCounter = totalProcessed;
                 }
               }
              counter++;
             }
            while(group.activeCount() > 0);
            // ��� � ���� ����� ��� ������ ���������
            logger.info("ALL THREADS CLOSED SUCCESSFULLY.");

            // ����� ��������� ���������� ���� ������� ��� ��� ������� ���� � ��������. ���� ���������� ��� ������ -
            // ������� ��������� �������� (���������).
            int totalProcessed = total.getTotal();
            if (lastCounter != totalProcessed)
             {
              logger.debug("TOTAL processed : [" + totalProcessed + "/" + currentListSize + "]");
              // ���� ���� ������� ������� �������� - ������� ��� ���������
              if (monitor != null) {monitor.processMessage(table.getTableName() + ": " + totalProcessed + " / " + currentListSize);}
             }

            // ���� � ���������� ������ ���� ������� ���� ��������� ������ � ����� �������������� ������ ���� �������,
            // �� ��������� ��� ������ � �������� ���������
            if (!allThreadsResultList.isEmpty()) {result = allThreadsResultList;}
            
          }
         }
       }
     }
    return result;
   }

  /** ��������� ������������� ������ �������. */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // ���� ���� ����� - ������ ��
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    // ��� �������
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append(")");
    // ������ ������ �������
    tableString.append("; KEYS COUNT: ");
    if ((this.keysList != null) && (!this.keysList.isEmpty()))
     {
      tableString.append(this.keysList.size()).append("\n KEYS LIST:\n  ");
      for (int i = 0; i < this.keysList.size(); i++)
       {
        tableString.append(this.keysList.get(i));
        // ������� ����� ��������
        if (i < this.keysList.size() - 1) {tableString.append(",");}
        // ������� ������ (��� ������������ ������������� "�������" ������)
        if ((i + 1)%30 == 0) {tableString.append("\n  ");}
       }
      // �������������� ������� ������
      tableString.append("\n");
     }
    // ���� ������ ������ ���� - ������ ������� �� ����
    else {tableString.append(0);}

    return tableString.toString();
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger(TableIntegrityModel.class.getName());
    
    DBConfig mysqlConfig1 = new DBConfig();
    mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig1.setHost("localhost:3306");
    mysqlConfig1.setDbName("storm");
    mysqlConfig1.setUser("root");
    mysqlConfig1.setPassword("mysql");
    //mysqlConfig1.addAllowedTable("items");

    /**
    DBConfig mysqlConfig2 = new DBConfig();
    mysqlConfig2.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig2.setHost("localhost:3306");
    mysqlConfig2.setDbName("storm");
    mysqlConfig2.setUser("root");
    mysqlConfig2.setPassword("mysql");
    mysqlConfig2.addAllowedTable("items");
    */

    DBConfig ifxConfig1 = new DBConfig();
    ifxConfig1.setDbType(DBConsts.DBType.INFORMIX);
    ifxConfig1.setServerName("hercules");
    ifxConfig1.setHost("appserver:1526");
    ifxConfig1.setDbName("storm");
    ifxConfig1.setUser("informix");
    ifxConfig1.setPassword("ifx_dba_019");
    //ifxConfig1.addAllowedTable("items");

    try
     {
      DBEngineer serverEngineer = new DBEngineer(ifxConfig1);
      DBIntegrityModel serverModel = serverEngineer.getDBIntegrityModel();

      DBEngineer clientEngineer = new DBEngineer(mysqlConfig1);
      DBIntegrityModel clientModel = clientEngineer.getDBIntegrityModel();

      // �������� �������
      TableIntegrityModel serverTable = serverModel.getTable("ruleset");
      if (serverTable != null) {logger.debug("server table ok!");}
      else {logger.debug("server table is null!");}
      
      TableIntegrityModel clientTable = clientModel.getTable("ruleset");
      if (clientTable != null) {logger.debug("client table ok!");}
      else {logger.debug("client table is null!");}

      if ((serverTable != null) && (clientTable != null))
       {
        logger.debug("standart -> " + serverTable.compareTo(clientTable, false, null, 2000));
        logger.debug("multi multiThreadsHelpers -> " + serverTable.multiThreadsCompareTo(clientTable, false, 20, null, 2000));
       }

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}

   }

 }