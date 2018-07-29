package jdb.model.time;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * ������ �� � ��������� ������� ���������� ���������� ������ ������� - ����. �������� ���� timestamp.
 * ������ ������ ������ ������ �� - ������ �������� ���� TableTimeModel. ��� �� �������� � ������� ��������.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 29.07.2010)
 *
 * @deprecated ������ ������� ������ ������������� ������������ �����
 * {@link jdb.nextGen.models.SimpleDBIntegrityModel SimpleDBIntegrityModel}
*/

public class DBTimedModel extends DBModel implements Serializable
 {
  static final long serialVersionUID = -6414707109042975340L;

  /** ���������-������ ������� ������. */
  private transient Logger           logger = Logger.getLogger(getClass().getName());
  /** ������ ������ ������ �� � ��������� ���� ����������. */
  private ArrayList<TableTimedModel> tables = null;

  /**
   * ����������� �� ���������. ����������� �������������� ��� ��.
   * @param dbName String ��� ����������� ������ ��.
   * @throws DBModelException �� ��������� ��� �������� ������ �� � ������ ������.
  */
  public DBTimedModel(String dbName) throws DBModelException {super(dbName);}

  public ArrayList<TableTimedModel> getTables() {
   return tables;
  }

  public void setTables(ArrayList<TableTimedModel> tables) {
   this.tables = tables;
  }

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  /**
   * ����� ��������� ������ ������� �� �������� � ������.
   * @param table TableTimeModel ����������� � ������ �������.
  */
  public void addTable(TableTimedModel table)
   {if (table != null) {if (this.tables == null) {this.tables = new ArrayList<TableTimedModel>();} this.tables.add(table);}}

  /**
   * ����� ���������� ������ ������� � ��������� ������� �� ���������� �����. ���� ������ ������ ������ ������ �� ����,
   * ��� ��� ������� ������� ����� ��� ������� �� ������� - ����� ���������� �������� null.
   * @param tableName String ������������ ������� �������.
   * @return TableTimeModel ��������� ������ ������� ��� null.
  */
  public TableTimedModel getTable(String tableName)
   {
    TableTimedModel result = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      for (TableTimedModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) && (tableName.equals(table.getTableName())))
         {result = table;}
       }
     }
    return result;
   }

  /** ��������� ������������� ������� �������. */
  @Override
  public String toString()
   {
    StringBuilder dbString = new StringBuilder();
    dbString.append("\nDATABASE: ").append(this.getDbName());
    // ������� ���� � ���������� ������ � �� � ��� ������
    dbString.append("\nTABLES COUNT: ");
    // ���� ������ ������ �� ���� � ����� ��� ���������
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      dbString.append(this.tables.size()).append("\nTABLES LIST: \n").append("----------\n");
      for (TableTimedModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // ���� ������ ������ ���� - ������ ������ �� �����!
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  /**
   * ����� ���������� ��� ������ �� � ��������� ������� � ���������� ������ ������, ������� �������� ������� �����
   * ����� �������.
   * @param dbTimedModel DatabaseTimeModel ������ ��, � ������� ���������� ������� ������.
   * @return DatabaseTimeModel ������, ���������� ������� ����� ���������� �����.
   * @throws DBModelException �� ����� ��������� ������ ������ ��� ������� ������������� ����������
   * ��������-������� ��� ������ � ������� �������.
   */
  public DBTimedModel compareTo(DBTimedModel dbTimedModel) throws DBModelException
   {
    // ���� ������ �� ��������������� - ��������������. ������ ����� ���� null, ���� ������ ��� ������������, �
    // ����� �������������� (������ �������� ��� transient ���� - ��� �� �������������).
    if (logger == null) {logger = Logger.getLogger(getClass().getName());}
    
    logger.debug("WORKING DatabaseTimeModel.compareTo().");
    DBTimedModel resultModel = null;
    // ���� ������ ������ ������� ������ ���� - ���������� null
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      logger.debug("Tables list of current model is not empty.");
      // ���� ��������� � �������� ��������� ������ ����� ��� ������ �� ������ ���� - ���������� ������� ������
      if (dbTimedModel == null || dbTimedModel.getTables() == null || dbTimedModel.getTables().isEmpty())
       {
        logger.debug("Tables list of parameter-model is empty");
        resultModel = this;
       }
      else
       {
        logger.debug("Tables list of parameter-model is not empty.");

        // �������� �� ������ ������ ������� ������ �� � ���������� �� � �������-����������
        for (TableTimedModel table : this.tables)
         {
          TableTimedModel paramTable = dbTimedModel.getTable(table.getTableName());
          // ���� ����� ������� � ��-��������� ������� - ������� �������� timestamp
          if(paramTable != null)
           {
            // ������� �������� - �� ������������ �� ��� ������ �������. ���� ��� �� ������������, �� ���������
            // �������� �� ������� �� "�����������"
            if (!table.equals(paramTable))
             // ���� � �������-��������� ��� ���� (timestamp=null), �� ������ ������� ������ ����
             // ��������� ��������� (�� ��������� timestamp=null)
             if (paramTable.getTimeStamp() == null)
              {
               // ���� ��������� ��� �� ��������������� - �������������
               if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
               resultModel.addTable(paramTable);
              }
             // ���� ���� ���� - ���������� �� � ����� ������� ������� ������
             else
              {
               // ���� ���� ������� ������� ����� (timestamp=null), �� ����� ������� ����������� � ����������
               // ������� - �� ��������� timestamp=null
               if (table.getTimeStamp() == null)
                {
                 // ���� ��������� ��� �� ��������������� - �������������
                 if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
                 resultModel.addTable(table);
                }
               //
               else
                {
                 int compareResult = table.getTimeStamp().compareTo(paramTable.getTimeStamp());
                 if (compareResult > 0)
                  {
                   // ���� ��������� ��� �� ��������������� - �������������
                   if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
                   resultModel.addTable(paramTable);
                  }
                }
              }

           }
          // ���� �� ����� ������� � ��-��������� ��� - �� ���� �������� � ��������� � ����������
          // timestamp=null - �.�. ������ ������� ������ ���
          else
           {
            // ���� ��������� ��� �� ��������������� - �������������
            if (resultModel == null) {resultModel = new DBTimedModel(this.getDbName());}
            resultModel.addTable(table);
           }
         } // END OF FOR CYCLE

       }
     }
    else logger.debug("Tables list of current model is empty.");

    return resultModel;
   }

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DBTimedModel.class.getName());
    Logger logger = Logger.getLogger(DBTimedModel.class.getName());
    try
     {
      DBTimedModel model1 = new DBTimedModel("model1");
      DBTimedModel model2 = new DBTimedModel("model2");
      model1.addTable(new TableTimedModel("table1", Timestamp.valueOf("2001-08-01 00:00:00")));
      model1.addTable(new TableTimedModel("table2", Timestamp.valueOf("2001-03-02 00:00:00")));
      model1.addTable(new TableTimedModel("table3", Timestamp.valueOf("2001-09-01 00:00:00")));
      model1.addTable(new TableTimedModel("table4", Timestamp.valueOf("2001-05-04 00:00:00")));
      model1.addTable(new TableTimedModel("table5", Timestamp.valueOf("2001-01-01 00:00:00")));
      model1.addTable(new TableTimedModel("table6", null));
      model1.addTable(new TableTimedModel("table8", null));
      model2.addTable(new TableTimedModel("table1", Timestamp.valueOf("2001-07-01 00:00:00")));
      model2.addTable(new TableTimedModel("table2", Timestamp.valueOf("2001-03-01 00:00:00")));
      model2.addTable(new TableTimedModel("table3", Timestamp.valueOf("2001-09-01 00:00:00")));
      model2.addTable(new TableTimedModel("table4", Timestamp.valueOf("2001-05-03 00:00:00")));
      model2.addTable(new TableTimedModel("table5", null));
      model2.addTable(new TableTimedModel("table7", null));
      model2.addTable(new TableTimedModel("table8", Timestamp.valueOf("2001-01-01 00:00:00")));
      logger.info("RESULT: " + model2.compareTo(model1));
     } 
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }