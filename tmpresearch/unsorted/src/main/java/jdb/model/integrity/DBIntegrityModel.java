package jdb.model.integrity;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.DBModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ����� ��������� ������ �� ��� �������� �� �����������. ������ �������� ������ ����������� ������� ������ -
 * ������� ������ ��� �������� �� �����������. ��� ����� ������ �������� � ������� �������� ��������.
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.03.2011)
*/

public class DBIntegrityModel extends DBModel implements Serializable
 {
  // ���� ������������ ��� ������������� ����������� ������ ������ � ����������� (��� ��������� ������������)
  static final long serialVersionUID = -8598456500297919139L;

  /** ������������ ���������� �� ��� ��������� ���� ��. */
  private static final String DIFFERENCES_DB_NAME = "DIFFERENCES_DB";
  /** ���������-������ ������� ������. ��������� ���������� - �� �������������. */
  private transient Logger logger = Logger.getLogger(getClass().getName());
  /** ������ ������ ��. */
  private ArrayList<TableIntegrityModel> tables = null;

  public DBIntegrityModel(String dbName) throws DBModelException {super(dbName);}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  public ArrayList<TableIntegrityModel> getTables() {return tables;}
  
  public void setTables(ArrayList<TableIntegrityModel> tables) {this.tables = tables;}

  /**
   * ����� ��������� � ������ ������ ������ ������ �� ��� ����. ���� ��������� ������� ����� - ��� ��������� �� �����.
   * @param table TableIntegrityModel ����������� � ������ ������ �������.
  */
  public void addTable(TableIntegrityModel table)
   {
    if (table != null)
     {
      if (this.tables == null) {this.tables = new ArrayList<TableIntegrityModel>();}
      this.tables.add(table);
     }
   }

  /**
   * ��������� ������ ������� �� ����� �� ������ ������ ��. ��� ������� ����� ����������� � ����� �������� ��������. ����
   * ��������� ��� ����� ��� ������� �� ������� - ����� ���������� �������� null.
   * ����� �������� null-safe - ��������� ������������ null-��������.
   * @param tableName String ��� ������� �������.
   * @return TableIntegrityModel ��������� ������� ��� �������� null.
  */
  public TableIntegrityModel getTable(String tableName)
   {
    TableIntegrityModel table = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && (!StringUtils.isBlank(tableName)))
     {
      // �������� �� ������ ������ ������� �� � ���� �� ���������� �����
      for (TableIntegrityModel localTable : this.tables)
       {
        // ���� ���������� ������� �� null, �� ��� �� ����� � ��������� � ���������, �� �������� ���������.
        if ((localTable != null) && (!StringUtils.isBlank(localTable.getTableName())) && 
            (tableName.toUpperCase().equals(localTable.getTableName())))
         {table = localTable;}
       }
     }
    return table;
   }

  /**
   * ����� ���������� ������� ���� ������ � ����� ������, ���������� � �������� ���������. ��������� ������ ������ -
   * ������ ����������� �� (DBIntegrityModel), ������� �������� ������� �� �������� ������, ������� ���� � ��������
   * ������� ��, �� ������� ��� � ��������������� �� ������ �������� ��-��������� ������. ���� ��-��������
   * ����� �������� null - ����� ������ �������� null. ���� ������ ������ ��-��������� ����, �� ����� ������ �� ��
   * ������� ������ ������� ��, ���� �� �� ����. ���� �� � ����� �������� ������ ������ ������� �� ����, �� �����
   * ������ �������� null. �������������� �������� lightCheck ��������� ������� �������� - ������� ����� (lightCheck=true)
   * � ����������� (lightCheck=false).
   * @param db DBIntegrityModel ������ �� ��� ���������.
   * @param lightCheck boolean �������/����������� �������� - ������/����.
   * @param monitor ProcessMonitor �����-�������, ������� ��������� ���������������� �� ����������� �������� ���������.
   * @param processedCount int ��� ������ ��������� ��� �������� �������� (���������� ������������ ������/�����, �����
   * ������� ����� �������� ��������� ��������).
   * @return DBIntegrityModel ��-�������� ��������� ������� �� � ��-���������.
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db, boolean lightCheck, DBProcessingMonitor monitor, int processedCount)
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // ���� ������ ������ ������� �� ���� - ������ � ����������, ����� ���������� �������� null. ���� ��
    // ������ ������ ������� �� �� ���� - �������� �����
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // ���� ��-������� ����� �������� null, �� ����� ������ ���������� - ���������� null.
      if (db != null)
       {

        // ���� ������ ������ ��-��������� ����, �� ���������� ������ ������ ������� ��
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // ������ ��-����������
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // ������ ������ ���������� - ������ ������ ������� ��
            result.tables = this.tables;
           }
          // ��� ������ ����� � ��� � ���������� null. ���� ����� ������ ������������. 
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // ���� �� ������ ������ ��-��������� �� ����, �� ���������� ���������� ������ ������� ��� ������
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");
          // � ����� �������� �� ������ ������ ������� �� 
          for (TableIntegrityModel currentTable : this.tables)
           {
            // ���� ������� ������� ������� �� �� null - ��������
            if (currentTable != null)
             {
              TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
              logger.debug("Processing table [" + currentTable.getTableName() + "].");
              // ���� ���� ������� - ������� ��� �� �������������� �������
              if (monitor != null) {monitor.processMessage(currentTable.getTableName());}

              // ���������� ������� ������� � ����������� �� ����� �������� ��_���������
              ArrayList<Integer> keysList = currentTable.compareTo(foreignTable, lightCheck, monitor, processedCount);
              // ���� � ���������� ��������� ������� �������� ������ ������ - ��������� ������� ��������
              if ((keysList != null) && (!keysList.isEmpty()))
               {
                // ������� ������� ��������� � ��������� �� � ��-���������. ����������� try-catch - ��� ����, �����
                // ��� ������ ��������� ����� ������� �� ��������� ���� ����.
                try
                 {
                  // ���� ��-��������� ��� �� ���������������� - �������������
                  if (result == null) {result = new DBIntegrityModel(DIFFERENCES_DB_NAME);}
                  // ������ �������� �������-��������� � ������, ����������� ����� ������� �������
                  TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                  // ��������� ������ ������ �������� � �������-����������
                  table.setKeysList(keysList);
                  // ���������� ������� � �������������� ��
                  result.addTable(table);
                 }
                // �������� ���������� ��� ������ � ������� ��������
                catch (DBModelException e)
                 {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                               "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
               }
              // ���� �� ���������� ������ ���� - ������� � ���
              else {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
             }
            // ���� ������� ������� ������� �� ����� �������� null - ������� � ���
            else {logger.warn("Current processing table is NULL!");}
           }

         }

       }
     }
    
    return result;
   }

  /**
   * ����� ���������� ������� ���� ������ � ����� ������, ���������� � �������� ���������. ��������� ������ ������ -
   * ������ ����������� �� (DBIntegrityModel), ������� �������� ������� �� �������� ������, ������� ���� � ��������
   * ������� ��, �� ������� ��� � ��������������� �� ������ �������� ��-��������� ������. ���� ��-��������
   * ����� �������� null - ����� ������ �������� null. ���� ������ ������ ��-��������� ����, �� ����� ������ �� ��
   * ������� ������ ������� ��, ���� �� �� ����. ���� �� � ����� �������� ������ ������ ������� �� ����, �� �����
   * ������ �������� null.
   * @param db DBIntegrityModel
   * @return DBIntegrityModel
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db, boolean lightCheck)
   {return this.compareTo(db, lightCheck, null, -1);}

  /**
   * ����� ���������� ������� ���� ������ � ����� ������, ���������� � �������� ���������. ��������� ������ ������ -
   * ������ ����������� �� (DBIntegrityModel), ������� �������� ������� �� �������� ������, ������� ���� � ��������
   * ������� ��, �� ������� ��� � ��������������� �� ������ �������� ��-��������� ������. ���� ��-��������
   * ����� �������� null - ����� ������ �������� null. ���� ������ ������ ��-��������� ����, �� ����� ������ �� ��
   * ������� ������ ������� ��, ���� �� �� ����. ���� �� � ����� �������� ������ ������ ������� �� ����, �� �����
   * ������ �������� null.
   * @param db DBIntegrityModel
   * @return DBIntegrityModel
  */
  public DBIntegrityModel compareTo(DBIntegrityModel db)
   {return this.compareTo(db, false, null, -1);}

  /**
   * ����� ���������� ������� ���� ������ � ����� ������, ���������� � �������� ���������. ��������� ������ ������ -
   * ������ ����������� �� (DBIntegrityModel), ������� �������� ������� �� �������� ������, ������� ���� � ��������
   * ������� ��, �� ������� ��� � ��������������� �� ������ �������� ��-��������� ������. ���� ��-��������
   * ����� �������� null - ����� ������ �������� null. ���� ������ ������ ��-��������� ����, �� ����� ������ �� ��
   * ������� ������ ������� ��, ���� �� �� ����. ���� �� � ����� �������� ������ ������ ������� �� ����, �� �����
   * ������ �������� null. �������������� �������� lightCheck ��������� ������� �������� - ������� ����� (lightCheck=true)
   * � ����������� (lightCheck=false).<br>
   * �� ��������� � ������������ ������� ������ ����� ���� �������������� ������� �������������� (~5-7%) �� ������
   * ������� (��� ����) � ������� ������� ������ (>512MB). <b>����� ������������� � ���������� ������ �� ������������ ���
   * ����������������� ������� � ������� ������� ����������� ������.</b><br>
   * ������ ����� ��������� ������� ������ ��������������� - ��������� ������ ���� ������ �� ���� ���������������
   * (� ���� �����), � ��� ��������� ������ ������� ������������.
   * @param db DBIntegrityModel ������ �� ��� ���������.
   * @param lightCheck boolean �������/����������� �������� - ������/����.
   * @param threadsNumber int ������������ ���������� ������� ��� ������� ������ � ���� �� ����������.
   * @param monitor ProcessMonitor �����-�������, ������� ��������� ���������������� �� ����������� �������� ���������.
   * @param processedCount int ��� ������ ��������� ��� �������� �������� (���������� ������������ ������/�����, �����
   * ������� ����� �������� ��������� ��������).
   * @return DBIntegrityModel ��-�������� ��������� ������� �� � ��-���������.
  */
  public DBIntegrityModel simpleMultiThreadsCompareTo(DBIntegrityModel db, boolean lightCheck, int threadsNumber,
   DBProcessingMonitor monitor, int processedCount)
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // ���� ������ ������ ������� �� ���� - ������ � ����������, ����� ���������� �������� null. ���� ��
    // ������ ������ ������� �� �� ���� - �������� �����
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // ���� ��-������� ����� �������� null, �� ����� ������ ���������� - ���������� null.
      if (db != null)
       {
        // ���� ������ ������ ��-��������� ����, �� ���������� ������ ������ ������� ��
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // ������ ��-����������
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // ������ ������ ���������� - ������ ������ ������� ��
            result.tables = this.tables;
           }
          // ��� ������ ����� � ��� � ���������� null. ���� ����� ������ ������������.
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // ���� �� ������ ������ ��-��������� �� ����, �� ���������� ���������� ������ ������� ��� ������
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");
          // � ����� �������� �� ������ ������ ������� ��
          for (TableIntegrityModel currentTable : this.tables)
           {
            // ���� ������� ������� ������� �� �� null - ��������
            if (currentTable != null)
             {
              TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
              logger.debug("Processing table [" + currentTable.getTableName() + "].");
              // ���� ���� ������� - ������� ��� �� �������������� �������
              if (monitor != null) {monitor.processMessage(currentTable.getTableName());}

              // ���������� ������� ������� � ����������� �� ����� �������� ��_���������
              ArrayList<Integer> keysList = currentTable.multiThreadsCompareTo(foreignTable, lightCheck, threadsNumber, monitor, processedCount);
              // ���� � ���������� ��������� ������� �������� ������ ������ - ��������� ������� ��������
              if ((keysList != null) && (!keysList.isEmpty()))
               {
                // ������� ������� ��������� � ��������� �� � ��-���������. ����������� try-catch - ��� ����, �����
                // ��� ������ ��������� ����� ������� �� ��������� ���� ����.
                try
                 {
                  // ���� ��-��������� ��� �� ���������������� - �������������
                  if (result == null) {result = new DBIntegrityModel(DIFFERENCES_DB_NAME);}
                  // ������ �������� �������-��������� � ������, ����������� ����� ������� �������
                  TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                  // ��������� ������ ������ �������� � �������-����������
                  table.setKeysList(keysList);
                  // ���������� ������� � �������������� ��
                  result.addTable(table);
                 }
                // �������� ���������� ��� ������ � ������� ��������
                catch (DBModelException e)
                 {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                               "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
               }
              // ���� �� ���������� ������ ���� - ������� � ���
              else {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
             }
            // ���� ������� ������� ������� �� ����� �������� null - ������� � ���
            else {logger.warn("Current processing table is NULL!");}
           }

         }

       }
     }

    return result;
   }

  /**
   * ����� ���������� ������� ���� ������ � ����� ������, ���������� � �������� ���������. ��������� ������ ������ -
   * ������ ����������� �� (DBIntegrityModel), ������� �������� ������� �� �������� ������, ������� ���� � ��������
   * ������� ��, �� ������� ��� � ��������������� �� ������ �������� ��-��������� ������. ���� ��-��������
   * ����� �������� null - ����� ������ �������� null. ���� ������ ������ ��-��������� ����, �� ����� ������ �� ��
   * ������� ������ ������� ��, ���� �� �� ����. ���� �� � ����� �������� ������ ������ ������� �� ����, �� �����
   * ������ �������� null. �������������� �������� lightCheck ��������� ������� �������� - ������� ����� (lightCheck=true)
   * � ����������� (lightCheck=false).<br>
   * �� ��������� � ������������ ������� ������ ����� ���� �������������� ������� �������������� (~5-7%) �� ������
   * ������� (��� ����) � ������� ������� ������ (>512MB). <b>����� ������������� � ���������� ������ �� ������������ ���
   * ����������������� ������� � ������� ������� ����������� ������.</b><br>
   * ������ ����� ��������� ����������� ������ ��������������� - ��������� ������ ���� ������ �� ���� ����������� �
   * ��������� �������, ������ �� �������, � ���� �������, �������� ������������� ����� ��������� ����� �������. ������
   * ��������� ���������� �������, ����������� ������ ������� �� ��������� �������� min(threadsNumber, DBConsts.MAX_THREADS).
   * @param db DBIntegrityModel ������ �� ��� ���������.
   * @param lightCheck boolean �������/����������� �������� - ������/����.
   * @param threadsNumber int ������������ ���������� ������� ��� ������� ������ � ���� �� ����������.
   * @param monitor ProcessMonitor �����-�������, ������� ��������� ���������������� �� ����������� �������� ���������.
   * @param processedCount int ��� ������ ��������� ��� �������� �������� (���������� ������������ ������/�����, �����
   * ������� ����� �������� ��������� ��������).
   * @return DBIntegrityModel ��-�������� ��������� ������� �� � ��-���������.
   * @deprecated ����� �� ������������� � �������������, �.�. ������������������ ��� ������ ��������� �������� ��������� ���
   * ������ �� ������ ������ - ����, ��� ��������� ���������������� �����. ������ ������� ������ ������� ������������ �����
   * simpleMultiThreadsCompareTo() - � ������ ������ �������������� ������� - ������ ��������� ������, ������� �� ������
   * �������������� ���������������.
  */
  public DBIntegrityModel advancedMultiThreadsCompareTo(final DBIntegrityModel db, final boolean lightCheck,
   int threadsNumber, final DBProcessingMonitor monitor, final int processedCount)
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}

    logger.debug("DBIntegrityModel.compareTo() starting.");
    DBIntegrityModel result = null;
    // ���� ������ ������ ������� �� ���� - ������ � ����������, ����� ���������� �������� null. ���� ��
    // ������ ������ ������� �� �� ���� - �������� �����
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      // ���� ��-������� ����� �������� null, �� ����� ������ ���������� - ���������� null.
      if (db != null)
       {
        // ���� ������ ������ ��-��������� ����, �� ���������� ������ ������ ������� ��
        if ((db.tables == null) || (db.tables.isEmpty()))
         {
          try
           {
            // ������ ��-����������
            result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
            // ������ ������ ���������� - ������ ������ ������� ��
            result.tables = this.tables;
           }
          // ��� ������ ����� � ��� � ���������� null. ���� ����� ������ ������������.
          catch (DBModelException e) {logger.error(e.getMessage());}
         }
        // ���� �� ������ ������ ��-��������� �� ����, �� ���������� ���������� ������ ������� ��� ������
        else
         {
          logger.debug("Parameter DB tables list is not empty. Processing.");

          // ����������� ����������� ���������� ������� � ������ ������ � � ������� �� ���������� - ���������������
          // � ������� TableIntegrityModel.advancedMultiThreadsCompareTo() - �������������� � ���� ���������� ������� ����������
          // ������� ������ ���� ������. ����� ��������� ���������� ������� ��� ������� ������ �� ����� ���� ������ �����
          // ������������.
          int threadsRelation = 2;

          // ��� ������ ������� ������������ ��������� ���������� ������� ��� ������� ������ � ������ ��������������
          // �������, ���������� ������ (TableIntegrityModel.advancedMultiThreadsCompareTo()). ���� ��������� ���������� �������
          // �� �������� - ������� ����� ������������ ���������� (��. �����. ��������� � DBConsts).
          int threadsCount;
          if (threadsNumber >= threadsRelation) {threadsCount = Math.min(threadsNumber, DBConsts.MAX_THREADS);}
          else                                  {threadsCount = DBConsts.MAX_THREADS;}

          // ������ ������������ ���������� ������� ��� ������� ������ � ��� ������� ��������� ������. ���������, ���
          // ���������� ������� � ������� ��������� ������ (TableIntegrityModel.advancedMultiThreadsCompareTo()) ������ ����
          // � threadsRelation ���(�) ������, ��� ���������� ������� � ������ ������.
          int currentMethodThreads = ((Double)(Math.sqrt(threadsCount/threadsRelation))).intValue();

          // ����� �� ����� ������ �������������� ������ ������ ������
          final int partSize;
          final int remainder;
          if (this.tables.size() >= currentMethodThreads)
           {
            partSize  = this.tables.size()/currentMethodThreads;
            remainder = this.tables.size()%currentMethodThreads;
           }
          else
           {
            currentMethodThreads = this.tables.size();
            partSize = 1;
            remainder = 0;
           }
          
          // ��� ���������� ������ ������ ���������� ������� ��������� ���-�� ������� �� ���������� ������� ��� (������,
          // ��� ����� ������� ���������� �������� ����������� ����� ������ �������� threadsRelation, ���� �� ���� �����
          // �������� ����������� ��������� � ���������, ��������� ���� ���: threadsRelation*currentMethodThreads, �� ���
          // ���� ����� ������� ������ ��������������� (����������) ���������� �������):
          final int internalMethodThreads; // <- (final) - ��� ������� �� ����������� ������-������
          if (currentMethodThreads > 1) {internalMethodThreads = threadsCount/threadsRelation;}
          else                          {internalMethodThreads = threadsCount;}

          // ������ ��� ���� ������� ������� ������
          ThreadGroup group = new ThreadGroup("DBIntegrityModelThreads");
          // ��������� ���������� ��� ������� � ������ ���������� ������� �� ������ ������ (������)
          final int allThreadsCount = currentMethodThreads;
          // ����� ������ ������ ������� ������ �� - ��� ������� � ����� ������ �� �������
          final ArrayList<TableIntegrityModel> currentTablesList = new ArrayList<TableIntegrityModel>();
          currentTablesList.addAll(this.tables);
          try
           {
            // �������������� ������ �� ��� ���������� ����������� ������ ���� �������
            final DBIntegrityModel differencesDB = new DBIntegrityModel(DIFFERENCES_DB_NAME);

            // � ����� ������� ����������� ���������� ������� ��� ������� ������
            for (int i = 1; i <= currentMethodThreads; i++)
             {
              // ��������� ���������� ��� ������� � ������ ������ �� ������ ������
              final int currentThreadNumber    = i;
              // ��������������� ���������� ����� � ����������� ���� ������ ������
              new Thread
               (group,
                new Runnable()
                 {
                  public void run()
                   {
                    logger.debug("Thread # " + currentThreadNumber + " started!");
                    // ��������� � �������� ������� (������) � ������ ������ ������� ������ �� ��� ������� ������. ���� ���
                    // ���������� ������ ������ �� ������ ������� ������� (������ �� ������ ���������� �������), �� ���� �������
                    // ��������� ���������� ������ (������).
                    int start  = partSize*(currentThreadNumber - 1);
                    int finish;
                    // ���� ���� ������� � ������ ����� ��������� ��������� - ������������� �������� �������
                    if ((remainder > 0) && (currentThreadNumber == allThreadsCount))
                     {finish = (partSize*currentThreadNumber) - 1 + remainder;}
                    // ���� ������� ��� ��� ������ ����� ��������� �� ��������� - �������� ������� ����������
                    else
                     {finish = (partSize*currentThreadNumber) - 1;}
                    // ������� ���������� �����
                    int counter = 0;
                    int lastCounter = 0;
                    for (int count = start; count <= finish; count++)
                     {
                      TableIntegrityModel currentTable = currentTablesList.get(count);
                      // ���� ������� ������� �� ����� - ������������ ��
                      if (currentTable != null)
                       {
                        TableIntegrityModel foreignTable = db.getTable(currentTable.getTableName());
                        logger.debug("Processing table [" + currentTable.getTableName() + "].");
                        // ���������� ������� ������� � ����������� �� ����� �������� ��_���������
                        ArrayList<Integer> keysList = currentTable.multiThreadsCompareTo(foreignTable,
                         lightCheck, internalMethodThreads, monitor, processedCount);
                        // ���� � ���������� ��������� ������� �������� ������ ������ - ��������� ������� ��������
                        if ((keysList != null) && (!keysList.isEmpty()))
                         {
                          // ������� ������� ��������� � ��������� �� � ��-���������. ����������� try-catch - ��� ����,
                          // ����� ��� ������ ��������� ����� ������� �� ��������� ���� ����.
                          try
                           {
                            // �������� �������-��������� � ������, ����������� ����� ������� �������
                            TableIntegrityModel table = new TableIntegrityModel(currentTable.getTableName());
                            // ��������� ������ ������ �������� � �������-����������
                            table.setKeysList(keysList);
                            // ���������� ������� � �������������� ��
                            synchronized (differencesDB) {differencesDB.addTable(table);}
                           }
                          // �������� ���������� ��� ������ � ������� ��������
                          catch (DBModelException e)
                           {logger.error("Error processing table! [current: " + currentTable.getTableName() + "]" +
                             "[foreign: " + foreignTable.getTableName() + "] Message: " + e.getMessage());}
                         }
                        // ���� �� ���������� ������ ���� - ������� � ���
                        else
                         {logger.debug("Tables [" + currentTable.getTableName() + "] are equal (differences keys list is empty)!");}
                       }
                      // ���� ������� ������� ����� - ������� �� ���� - ��������� ��������
                      else {logger.warn("Empty table! Check current model!");}
                      // ����� ��� �������, ��� �������� ������������ ������
                      Thread.yield();
                     }
                   } // END OF RUN METHOD
                 }
               ).start();
             } // END OF FOR STATEMENT

            // ���� ��������� ���� ������� � ������
            logger.info("WAITING FOR ALL THREADS STOP...");
            // ������� �������� ����� �������� ��������� ���� �������
            int counter = 0;
            // ���� �������� ���������� ���� ������� � ������ ���������� � ��������� ����������
            do
             {
              //if (counter%10000 == 0) {logger.debug("Processing...");}
              counter++;
             }
            while (group.activeCount() > 0);
            // ��� � ���� ����� ��� ������ ���������
            logger.info("ALL THREADS CLOSED SUCCESSFULLY.");

            logger.debug("\n\n -> \n\n" + differencesDB);

            // ���� � ��-��������� ���� �������� �����-���� �������, �� ��������� �� � ��������� ������
            if ((differencesDB.tables != null) && !differencesDB.tables.isEmpty())
             {
              result = new DBIntegrityModel(DIFFERENCES_DB_NAME);
              result.tables = new ArrayList<TableIntegrityModel>();
              result.tables.addAll(differencesDB.tables);
             }

           } // END OF TRY
          // �������� ��
          catch (DBModelException e) {logger.error(e.getMessage());}
         }

       }
     }

    // ���������� ���������
    return result;
   }

  /** ��������� ������������� ������ ������ ��. */
  @Override
  public String toString()
   {
    StringBuilder dbString = new StringBuilder();
    dbString.append("\nDATABASE: ").append(this.getDbName());
    dbString.append("\nTABLES COUNT: ");
    // ���� ������ ������ �� ���� � ����� ��� ���������
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      dbString.append(this.tables.size()).append("\n TABLES LIST: \n----------\n\n");
      for (TableIntegrityModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // ���� ������ ������ ���� - ������� �� ����
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  @Override
  public int getTablesCount()
   {
    int result = 0;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {result = this.tables.size();}
    return result;
   }

  /**
   * ����� ������ ��� ������������ ������!
   * @param args String[] ��������� ������.
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
    //mysqlConfig1.addAllowedTable("ruleset");

    DBConfig ifxConfig1 = new DBConfig();
    ifxConfig1.setDbType(DBConsts.DBType.INFORMIX);
    ifxConfig1.setServerName("hercules");
    ifxConfig1.setHost("appserver:1526");
    ifxConfig1.setDbName("storm");
    ifxConfig1.setUser("informix");
    ifxConfig1.setPassword("ifx_dba_019");
    //ifxConfig1.addAllowedTable("items");
    //ifxConfig1.addAllowedTable("ruleset");

    try
     {
      DBEngineer serverEngineer = new DBEngineer(ifxConfig1);
      DBIntegrityModel serverModel = serverEngineer.getDBIntegrityModel();

      DBEngineer clientEngineer = new DBEngineer(mysqlConfig1);
      DBIntegrityModel clientModel = clientEngineer.getDBIntegrityModel();

      if ((serverModel != null) && (clientModel != null))
       {
        logger.debug("\n\n-----------------------------------------------------------------------\n\n");
        logger.debug("multi multiThreadsHelpers -> " + serverModel.simpleMultiThreadsCompareTo(clientModel, false, 130, null, 2000));
        logger.debug("\n\n-----------------------------------------------------------------------\n\n");
        logger.debug("standart -> " + serverModel.compareTo(clientModel, false, null, 2000));
       }

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}

   }

 }