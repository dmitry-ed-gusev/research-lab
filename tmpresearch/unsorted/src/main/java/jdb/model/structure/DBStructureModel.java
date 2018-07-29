package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * ����� ��������� ������ ��������� ����������� ���� ������. ������ �������� ������ ������ (������ �������� ����
 * TableStructureModel), ������ ������� ������ - �������, ���������� ������ ����� (������ �������� ����
 * FieldStructureModel). ������� ������ �� �� �� �������� - �������� ������ ���������� � ��������� �� - �������,
 * ���� � �.�. ���������� � ��������� ����� ������ (DBMS->JAVA) �� ��������. ����� ���� ������ ����������� �
 * ������� �������� - �.�. �������� ��� dbf-������ ������� ����� ���������� � ������ � ������� ��� ���������
 * �������, ��� � ����������, � ��� ����� ������ �������. ��� ����� �� ����� �������� � ������� �������� ��������.
 * �� ����������� ������ ����� �� ������ (������ ������ ��� �������� null) ���. ��� ������� ������� ����� �� �����
 * �������������� ��.<br>
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 26.07.2010)
*/

// todo: ������ equals() � hashCode() �� ������������ � ������� ����������������. ������������� �� �������������. ���������!
 
public class DBStructureModel extends DBModel implements Serializable
 {
  // ���� ������������ ��� ������������� ����������� ������ ������ � ����������� (��� ��������� ������������)
  static final long serialVersionUID = 6101916045951918449L;

  /** ���������-������ ������� ������. */
  //private transient Logger logger = Logger.getLogger(getClass().getName());

  /** ������ ������ ��. */
  private TreeSet<TableStructureModel> tables = null;

  /**
   * ����������� �� ���������. ����������� ���������������� ��� ��.
   * @param dbName String ��� ����������� ������ ��. ����������� �� ������!
   * @throws DBModelException �� ���������, ���� �� �������� ������� �� � ������ ������.
  */
  public DBStructureModel(String dbName) throws DBModelException {super(dbName);}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  public TreeSet<TableStructureModel> getTables() {return tables;}
  public void setTables(TreeSet<TableStructureModel> tables) {this.tables = tables;}

  /**
   * ����� ���������� ������ ������� �� �� �����, ���� ������ ��������� � ����� ������� ����������.
   * ����� �������� null-safe - ��������� ������������ null-��������.
   * @param tableName String ��� �������, ������ ������� ���������� �����.
   * @return TableModel ������ ������� ��� null.
  */
  public TableStructureModel getTable(String tableName)
   {
    TableStructureModel tableModel = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      // ����� ������� � ������ (�.�. ����� ������ �������� � ������� ��������, ��������� ���������� ��� � ������� �������)
      for (TableStructureModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) &&
            (table.getTableName().equals(tableName.toUpperCase())))
         {tableModel = table;}
       }
     }
    return tableModel;
   }

  /**
   * ����� ��������� ���� ������� � ������ ������ ������ ������ ��. ������� ����������� � ������ ������ ����
   * ��� �� ������ - ������ TableModel �� ����� null.
   * @param table TableModel ����������� � ������ �������.
  */
  public void addTable(TableStructureModel table)
   {
    if (table != null)
     {if (this.tables == null) this.tables = new TreeSet<TableStructureModel>(); this.tables.add(table);}
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
      dbString.append(this.tables.size()).append("\nTABLES LIST: \n").append("----------\n\n");
      for (TableStructureModel table : this.tables) {dbString.append(table).append("\n");}
      dbString.append("----------\n");
     }
    // ���� ������ ������ ���� - ������ ������ �� �����!
    else {dbString.append(0).append("\nTABLES LIST IS EMPTY!\n");}
    return dbString.toString();
   }

  /**
   * ����� ��������� ���� ��������(�����������) ������� ������. ������������ ������ ������ ������, ����� ���
   * ������ �� ������������. 
  */
  /**
  @Override
  public boolean equals(Object obj)
   {
    //logger.debug("***[EQUALS] Working equals() method."); // <- ������ ����� ����� ������ ��� �������� �������
    
    // ��������� ��������� ����������� ������� ������
    boolean result = false;
    // ������� �������� ������������ �����������
    if (this == obj) result = true;
    // ���� ������� �������� �� ������ - ��������� ����� - ���� ����� �������� null ��� ������ �� ���������
    // (������ ���������� �� ������ �������) - ������������ �������� false � �������� ������������. ���� �� ���
    // ���������� ������ ������ - �������� ������� ������ � ������� ������ � ��������� ������������ ������� ������.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // �������� ��� (������ �� �����, ��� ������ ����� ��� DatabaseModel � �� �������� �������)
      DBStructureModel foreign = (DBStructureModel) obj;

      // ���� ������ ������ ��� ������ �� ����� - ����� �������� �� ����������
      if ((tables != null) && (!tables.isEmpty()) && (foreign.tables != null) && (!foreign.tables.isEmpty()))
       {
        //logger.debug("***[EQUALS] tables lists are not empty!"); // <- ������ ����� ����� ������ ��� �������� �������
        // �������� ���������� ����� - ���� ��� ���������, �� ������ ������������. �� ���������� ����� ���
        // ������ - �.�. ���� � ������� ������� ����� ����� ���������� ���������.
        // ���������� ������ ������ � ����� ������ ���������� �����������, �.�. ��� ������� ��������� ���
        // ���������� ������� ������� ����� ���������� true
        if (this.getTables().equals(foreign.getTables()))
         {
          //logger.debug("***[EQUALS] simple equals -> true. (result->" + result + ")"); // <- ������ ����� ����� ������ ��� �������� �������
          result = true;

          // ������������ ��������� ������� ������. ������� ���������� ������ ������ ������� �� ��
          // ������� ������ ������� ��.
          Iterator currentIterator = this.tables.iterator();
          //logger.debug("***[EQUALS] processing current-to-foreign"); // <- ������ ����� ����� ������ ��� �������� �������
          while (currentIterator.hasNext() && result)
           {
            boolean found = false;
            TableStructureModel currentTable = (TableStructureModel)currentIterator.next();
            Iterator foreignIterator = foreign.getTables().iterator();
            while (foreignIterator.hasNext() && !found)
             {
              TableStructureModel foreignTable = (TableStructureModel)foreignIterator.next();
              if (currentTable.equals(foreignTable)) found = true;

              // ������ ����� ����� ������ ��� �������� �������
              //logger.debug("***[EQUALS] current[" + currentTable.getTableName() + "]->foreign[" +
              //        foreignTable.getTableName() + "] (found -> " + found + ")" + currentTable.equals(foreignTable));
   
             }
            if (!found) result = false;
           }
          
          // ���� ���������� ���� ���������� ������� (��� ������� ������� �� ������� �� �������), �� ����������
          // ������ ������ ������� �� �� ������� ������ ������� ��
          Iterator foreignIterator = foreign.getTables().iterator();
          //logger.debug("***[EQUALS] processing foreign-to-current"); // <- ������ ����� ����� ������ ��� �������� �������
          while (foreignIterator.hasNext() && result)
           {
            boolean found = false;
            TableStructureModel foreignTable = (TableStructureModel)foreignIterator.next();
            currentIterator = this.tables.iterator();
            while (currentIterator.hasNext() && !found)
             {
              TableStructureModel currentTable = (TableStructureModel)currentIterator.next();
              if (foreignTable.equals(currentTable)) found = true;

              // ������ ����� ����� ������ ��� �������� �������
              //logger.debug("***[EQUALS] foreign[" + foreignTable.getTableName() + "]->current[" +
              //        currentTable.getTableName() + "] (found -> " + found + ")" + foreignTable.equals(currentTable));
              
             }
            if (!found) result = false;

           }
         }
        // ������� �������� �� ������
        else
         {
          //logger.debug("***[EQUALS] simple equals -> false. (result->" + result + ")"); // <- ������ ����� ����� ������ ��� �������� �������
         }
       }
      // ���� ������ ������ ��� ������ ����� ������������ - ���� ������ ����� ������������
      else if (((tables == null) || (tables.isEmpty())) && ((foreign.tables == null) || (foreign.tables.isEmpty())))
       {result = true;}
     }
    return result;
   }
  */

  /**
   * ����� ���������� ���-��� �������. ���-��� - ������������� ��� ������������� ����� �����. �������������
   * �������� ������ ��������������� ���������� ���-����.
  */
  //@Override
  //public int hashCode() {return tables.hashCode();}

  /**
   * ����� ���������� ������ ������ ������ ��. ������ �������� ������ ��������� ����� ������, � �� ���� �������
   * ���� TableModel. ���� � ������ ������ �� ��� ������, ����� ���������� �������� null.
   * @return ArrayList<String> ������ ���� ������ ������ �� ��� �������� null.
  */
  public ArrayList<String> getTablesList()
   {
    ArrayList<String> tablesList = null;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      for (TableStructureModel table : this.tables)
       {
        if (table != null)
         {if (tablesList == null) {tablesList = new ArrayList<String>();} tablesList.add(table.getTableName());}
       }
     }
    return tablesList;
   }

  /**
   * ����� ���������� ������ (����������� ��������) ������ ������� ��.
   * @return String ���������� ������ ������.
  */
  public String getCSVTablesList()
   {
    StringBuilder csvList = null;
    if ((this.tables != null) && (!this.tables.isEmpty()))
     {
      Iterator iterator = this.tables.iterator();
      csvList = new StringBuilder();
      while (iterator.hasNext())
       {
        TableStructureModel table = (TableStructureModel) iterator.next();
        if (table != null)
         {
          csvList.append(table.getTableName());
          if (iterator.hasNext()) csvList.append(", ");
         }
       }
     }
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

  /**
   * ������ ����� ������������ ������ ��� ������������ ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DBStructureModel.class.getName());
    Logger logger = Logger.getLogger(DBStructureModel.class.getName());

    try
     {
      TableStructureModel table = new TableStructureModel("ffff");
      FieldStructureModel field  = new FieldStructureModel("123", 0, "", 0);
      FieldStructureModel field2 = new FieldStructureModel("456", 0, "", 0);
      table.addField(field);
      table.addField(field2);
      TableStructureModel table2 = new TableStructureModel("ffff2");
      FieldStructureModel field3 = new FieldStructureModel("987", 0, "", 0);
      FieldStructureModel field4 = new FieldStructureModel("654", 0, "", 0);
      table2.addField(field3);
      table2.addField(field4);
      DBStructureModel database = new DBStructureModel("dddd");
      database.addTable(table);
      database.addTable(table2);
      logger.info(database);
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }