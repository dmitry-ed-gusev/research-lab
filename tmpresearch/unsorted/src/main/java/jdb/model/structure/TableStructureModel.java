package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;
import jdb.model.structure.key.IndexedField;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * ����� ��������� ������ ����������� ������� ��. �������� ������ ����� ������� - ������ �������� ����
 * FieldStructureModel. �������� ����� �� ��������. ��� ������� �����������(������ �����������) ������ �
 * ������� �������� (��. ����������� � ������ DBStructureModel). � ������� ������ ������ ���� ���. ������
 * ��� ������� (������ ������ ��� null) �� ����������� (������������ ��).<br>
 *
 * 26.12.2008 ������ ����� ��������� ��������� Comparable - ��� ����, ����� ����� ���� ����������� ���������� �������
 * ������ � ������ ArrayList �� ����� �������.<br>
 *
 * 29.12.2008 � ������� � ������ ����� �� ������ ���� ���� ����� � ����������� ������� (���� ���������� �����������
 * ������ FieldStructureModel). ����� �� ������ ���� ���� ���������� ������������� ����� - ���� ����������� ������
 * IndexedField � ������������ ������� �������� � ������� �����. ����� ��� ��������� ������ ������ ����� ����������
 * ����� ������� � ������ (�������� FieldStructureModel) � ������������� ����� � ������ (�������� IndexedField).
 * ��� ����������� �������������� ���������� ���� ������� � ������������� ���� ������� �������� � ������� �
 * ����������� - TreeSet. ����� ��������� ������ (FieldStructureModel � IndexedField) ��������� ��������� Comparable,
 * ������� ��������������� ����� ��������� compareTo().
 *
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 23.07.2010)
*/

// todo: ������ equals() � hashCode() �� ������������ � ������� ����������������. ������������� �� �������������. ���������!
 
public class TableStructureModel extends TableModel implements Serializable, Comparable
 {
  // ���� ������������ ��� ������������� ����������� ������ ������ � ����������� (��� ��������� ������������)
  static final long serialVersionUID = 2562343871344731251L;

  /** ���������-������ ������� ������. */
  //private transient Logger logger = Logger.getLogger(this.getClass().getName());

  /** ������ ����� ������� - ������ �������� ���� TableFieldModel. */
  private TreeSet<FieldStructureModel> fields  = null;
  /** ������ ������������� ����� ������� - ������ �������� ���� IndexedField. */
  private TreeSet<IndexedField>        indexes = null;

  /**
   * ����������� ����� �������������� ��� �������. ��� ������� ����������� � ������� �������!
   * @param tableName String ��� ����������� ������� (�� ������).
   * @throws DBModelException �� ��������� ���� ��� �������� ������� ������� ������ ��� �������.
  */
  public TableStructureModel(String tableName) throws DBModelException {super(tableName);}

  public TreeSet<FieldStructureModel> getFields()            {return fields;}
  public void setFields(TreeSet<FieldStructureModel> fields) {this.fields = fields;}
  public TreeSet<IndexedField> getIndexes()                  {return indexes;}
  public void setIndexes(TreeSet<IndexedField> indexes)      {this.indexes = indexes;}

  /**
   * ����� ��������� ���� ���� (������ FieldStructureModel) � ������ ����� ������� (���� �����������, ������ ���� ��� �� �����).
   * @param field FieldStructureModel ����������� � ������ ����.
  */
  public void addField(FieldStructureModel field)
   {
    if ((field != null) && (!StringUtils.isBlank(field.getName())))
     {
      if (this.fields == null) {this.fields = new TreeSet<FieldStructureModel>();}
      this.fields.add(field);
     }
   }

  /**
   * ����� ��������� ���� ������ (������ IndexedField) � ������ �������� ������� (����������� ������ �������� ������).
   * @param index IndexedField ����������� ������.
  */
  public void addIndex(IndexedField index)
   {
    if (index != null)
     {
      if (this.indexes == null) {this.indexes = new TreeSet<IndexedField>();}
      this.indexes.add(index);
     }
   }

  /**
   * ����� ���������� ������ ���� TableFieldModel �� ��� ����� fieldName. ���� ��������� ��� ���� ����� ��� ������
   * ���� �� ������� - ����� ���������� �������� null.
   * @param fieldName String ��� �������� ����.
   * @return FieldStructureModel ��������� �� ����� ������ ���� ��� �������� null.
  */
  public FieldStructureModel getField(String fieldName)
   {
    FieldStructureModel field = null;
    if (!StringUtils.isBlank(fieldName))
     {
      for (FieldStructureModel localField : this.fields)
       {if (fieldName.toUpperCase().equals(localField.getName())) {field = localField;}}
     }
    return field;
   }

  /** ��������� ������������� ������� ������� (������ �������). */
  @Override
  public String toString()
   {
    StringBuilder tableString = new StringBuilder();
    tableString.append(" TABLE: ");
    // ���� ���� ����� - ������ ��
    if (!StringUtils.isBlank(this.getTableSchema())) {tableString.append(this.getTableSchema()).append(".");}
    tableString.append(this.getTableName()).append(" (TYPE: ").append(this.getTableType()).append(")");
    // ���� ������ �������
    tableString.append("; FIELDS COUNT: ");
    // ��������� ���� (���� ��� ����, ���� ��� - ������� �� ����)
    if ((this.fields != null) && (!this.fields.isEmpty()))
     {
      tableString.append(this.fields.size()).append("\n");
      tableString.append("  FIELDS LIST:\n");
      for (FieldStructureModel field : this.fields) {tableString.append("   ").append(field).append("\n");}
     }
    // �������� �� ���������� ����� � ������� 
    else {tableString.append(0).append("\n  FIELDS LIST IS EMPTY!\n");}

    // ��������� ������� (���� ��� ����, ���� ��� - ������� �� ����)
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      tableString.append("  INDEXES LIST:\n");
      for (IndexedField index : this.indexes) {tableString.append("   ").append(index).append("\n");}
     }
    // �������� �� ���������� �������� � �������
    else {tableString.append("  INDEXES LIST IS EMPTY!\n");}
    // ���������� ���������
    return tableString.toString();
   }

  /**
   * ����� ���������� CSV-������ (CSV - comma separated values - ������, ����������� ��������) ����� ������ �������
   * ��� �������� null, ���� ������ ����. ��� ����, ������� �������� null ����� ���������������. ���� �� ������ �����
   * ������� ������ �� null-�����, �� ����� ����� ������ �������� null.
   * ������ ����� �������� null-safe - ��������� ������������ null-��������.
   * @return String CSV-������ ����� ������ ������� ��� �������� null.
  */
  public String getCSVFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.fields != null) && (!this.fields.isEmpty()))
     {
      for (FieldStructureModel field : this.fields)
       {
        // ���� ��������� ���������� �� ������ ���� �� ����� - ��������� ��� � ��������������� ������
        if ((field != null) && (!StringUtils.isBlank(field.getName())))
         {
          // ���� ��� �� ��������������� ��������� - �������������
          if (csvList == null) {csvList = new StringBuilder();}
          // ���� �� ��������� ��� ��������������� - � ��� ��� ���� ����, ������� � ��� �������
          else {csvList.append(", ");}
          csvList.append(field.getName());
         }
       }
     }
    // ����� ����������� ����� ��� ����, ����� ���� ���� ����� ������ �� ������ (���� �������� return).
    String result;
    if (csvList == null) {result = null;} else {result = csvList.toString();}
    return result;
   }

  /**
   * ����� ���������� CSV-������ (CSV - comma separated values - ������, ����������� ��������) ������������� �����
   * ������ ������� ��� �������� null, ���� ������ ����. ��� ������������� ����, ������� �������� null �����
   * ���������������. ���� �� ������ ������������� ����� ������� ������ �� null-�����, �� ����� ����� ������
   * �������� null.
   * ������ ����� �������� null-safe - ��������� ������������ null-��������.
   * @return String CSV-������ ������������� ����� ������ ������� ��� �������� null.
  */
  public String getCSVIndexedFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      for (IndexedField field : this.indexes)
       {
        // ���� ���������� ���� �� ����� - ��������� ��� � ����������
        if ((field != null) && (!StringUtils.isBlank(field.getFieldName())))
         {
          // ���� ��� �� ��������������� ��������� - �������������
          if (csvList == null) {csvList = new StringBuilder();}
          // ���� �� ��������� ��� ��������������� - � ��� ��� ���� ����, ������� � ��� �������
          else {csvList.append(", ");}
          csvList.append(field.getFieldName());
         }
       }
     }
    // ����� ����������� ����� ��� ����, ����� ���� ���� ����� ������ �� ������ (���� �������� return).
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  /**
   * ����� ���������� ����������� �������� ������ �����, ����������� � ��������� ����� ������ �������.
   * @return String ������ ����� ���������� �����.
  */
  public String getCSVPKFieldsList()
   {
    StringBuilder csvList = null;
    // ���� ���� ������� - ����� ��� ����� ���� � ��������� ����
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      for (IndexedField field : this.indexes)
       {
        // ���� ���������� ���� �� ����� � �������� ��������� ������ - ��������
        if ((field != null) && (!StringUtils.isBlank(field.getFieldName())) && (field.isPrimaryKey()))
         {
          // ���� ���� ��������� ���� - �������������� ������
          if (csvList == null) {csvList = new StringBuilder();}
          // ���� �� ��������� ��� ��������������� - � ��� ��� ���� ����, ������� �������
          else {csvList.append(", ");}
          csvList.append(field.getFieldName());
         }
       }
     }
    // ����� ����������� ����� ��� ����, ����� ���� ���� ����� ������ �� ������ (���� �������� return).
    String result;
    if (csvList == null) result = null; else result = csvList.toString();
    return result;
   }

  /**
   * ����� ��������� ���� ��������(�����������) ������� ������. � ������ ������ ������������ �����, ������� �����������
   * �������, ����� ������, ������ ����� ������ (������ �������� ������ FieldStrucutureModel), ������ �������������
   * ����� (������ �������� ������ IndexedField).
  */
  /**
  @Override
  public boolean equals(Object obj)
   {
    // ��������� ��������� ����������� ������� ������
    boolean result = false;
    // ������� �������� ������������ �����������
    if (this == obj) result = true;
    // ���� ������� �������� �� ������ - ��������� ����� - ���� ����� �������� null ��� ������ �� ���������
    // (������ ���������� �� ������ �������) - ������������ �������� false � �������� ������������. ���� �� ���
    // ���������� ������ ������ - �������� ������� ������ � ������� ������ � ��������� ������������ ���� �����.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // �������� ��� (������ �� �����, ��� ������ ����� ��� TableModel � �� �������� �������)
      TableStructureModel foreign = (TableStructureModel) obj;

      // ������� ������ ����� ������, ������ �������� ������ � ����� ������ - ���� ���������, ������ ������� ���������
      if (this.getTableName().equals(foreign.getTableName())) // <- ���������� ����� ������
       {
        // ������ ���������� ����� ���� ������. ���� ����� ����� (��� ���� ��� (��� �� ���� � �����)), �� ���������� ���������.
        String thisSchema    = this.getTableSchema();
        String foreignSchema = foreign.getTableSchema();
        if (
            (StringUtils.isBlank(thisSchema) && StringUtils.isBlank(foreignSchema)) ||
            (!StringUtils.isBlank(thisSchema) && !StringUtils.isBlank(foreignSchema) && thisSchema.equals(foreignSchema))
           )
         {
          // ���������� ������ �����. ������ ����� ���� ������� - ����� ��������� ���� ��������. ���� ������ ��
          // ������� - ���������� �������� �� �����. ����� ���� ��� ������ ����� ����� - ��� ������ ���������� �������.

          // ���� ��� ������ ����� �� ����� - ���������� �� ����������. ����� ���������� �������.
          if ((fields != null) && (!fields.isEmpty()) && (foreign.fields != null) && (!foreign.fields.isEmpty()))
           {
            // ���� ���������� �������� ������� ����� ��������� - ���������� �������
            if (fields.equals(foreign.fields))
             {
              // ���� ��� ������ �������� �� ����� - ���������� �� ����������.
              if ((indexes != null) && (!indexes.isEmpty()) && (foreign.indexes != null) && (!foreign.indexes.isEmpty()))
               {
                // ������ �������� ������� ���������� ������ - ���� �������� ������ �������� �� �������, ��� ����� ��������,
                // ��� ������������ ������ ������ ��� ������ - ����� ���������� � ���������� ������� ������ �������������
                // ����� - ��� ���� ������������� � ����� �������, ������ ���� �������������� � � ������
                if (indexes.equals(foreign.indexes)) {result = true;} // <- ������� ��������� ������� ��������
                // ���� �������� ������ �������� �� ������� - �������� ����������� �������� �� ������ ������������� �����
                else if (this.getUniqueIndexesList().equals(foreign.getUniqueIndexesList())) {result = true;}
               }
              // �������� - ���� ������ �������� ����� ������������ - ��� ���������.
              else if (((indexes == null) || (indexes.isEmpty())) && ((foreign.indexes == null) || (foreign.indexes.isEmpty())))
               {result = true;}
             }
           }
          // ���� ���� ���� �� ���� ������ - ��������, ����� ��� ������ ��� ���. ���� ������ ����� ������ -
          // ������ ��� ���������.
          else if (((fields == null) || (fields.isEmpty())) && ((foreign.fields == null) || (foreign.fields.isEmpty())))
           {result = true;}
         } // ����� ����� ��������� ���� ���� ������
       } // ����� ����� ��������� ���� ���� ������

     }
    return result;
   }
  */

  /**
   * ����� ���������� ���-��� �������. ���-��� - ������������� ��� ������������� ����� �����. �������������
   * �������� ������ ��������������� ���������� ���-����.
  */
  /**
  @Override
  public int hashCode()
   {
    int result;
    result = this.getTableName().hashCode();
    if (this.getTableSchema() != null) {result = 31*result + this.getTableSchema().hashCode();}
    result = 31*result + (fields != null ? fields.hashCode() : 0);
    result = 31*result + (indexes != null ? indexes.hashCode() : 0);
    return result;
   }
  */
  
  /** ����� �������� ����������� ������� � ������ �� ������. */
  @Override
  public int compareTo(Object o)
   {
    TableStructureModel table = (TableStructureModel) o;
    int result;
    if (!StringUtils.isBlank(this.getTableSchema()) && !StringUtils.isBlank(table.getTableSchema()))
     {result = (this.getTableSchema() + "." + this.getTableName()).compareTo(table.getTableSchema() + "." + table.getTableName());}
    else
     {result = this.getTableName().compareTo(table.getTableName());}
    return result;
   }

  /**
   * ����� ��������� � ���������� ������ ���� ������������� �����. ������ ��� ���������� (�� ������ ��������� TreeSet).
   * @return TreeSet<String> ������� ��������� ������ ������������� �����.
  */
  public TreeSet<String> getUniqueIndexesList()
   {
    TreeSet<String> indexesList = null;
    // ���� ������ �������� ���� - ��������� ���� ����� ����
    if ((this.indexes != null) && (!this.indexes.isEmpty()))
     {
      indexesList = new TreeSet<String>();
      for (IndexedField index : this.indexes) indexesList.add(index.getFieldName());
     }
    return indexesList;
   }

  /**
   * @param foreign TableStructureModel
   * @return String 
  */
  public String getDifferenceReport(TableStructureModel foreign)
   {
    // �������������� �����
    StringBuilder report;
    // ���� ���������� � �������� ��������� ������ �� ���� - ������������ ���
    if (foreign != null)
     {
      report = new StringBuilder("\n---> Tables [" + foreign.getTableName() + "] difference report. ---\n");
      report.append("----> SIMPLE EQUALS: ").append(this.equals(foreign)).append("\n");
      
      // ������ ����� ������� �������
      report.append("----> FIELDS [").append(this.getTableName()).append("] current: ");
      // ���� ������ ����� ����, �� ���� ������������� NullPointerException
      if ((this.fields == null) || (this.fields.isEmpty())) report.append("NO FIELDS.\n");
      else report.append("(").append(this.fields.size()).append(") ").append(this.getCSVFieldsList()).append("\n");
      // ������ ����� ������� �������
      report.append("----> FIELDS [").append(foreign.getTableName()).append("] foreign: ");
      // ���� ������ ����� ����, �� ���� ������������� NullPointerException
      if ((foreign.fields == null) || (foreign.fields.isEmpty())) report.append("NO FIELDS.\n");
      else report.append("(").append(foreign.fields.size()).append(") ").append(foreign.getCSVFieldsList()).append("\n");
      // ������� ��������� ������� ����� ������� � ������� ������.
      report.append("----> FIELDS simple current.equals(foreign) = ");
      // ���� ��� ������ �� ����� - ������� �� ����������.
      if (this.fields != null && !this.fields.isEmpty() && foreign.getFields() != null && !foreign.getFields().isEmpty())
       {report.append(this.fields.equals(foreign.getFields())).append("\n");}
      // ���� ��� ������ ����� ������������ - ��� ������������
      else if (((this.fields == null) || (this.fields.isEmpty())) && ((foreign.getFields() == null) || (foreign.getFields().isEmpty())))
       {report.append(true).append("\n");}
      // ���� �� �� ���� �� �������� �� ������ - ������ ������
      else {report.append(false).append("\n");} 

      // ������ ������������� ����� ������� �������
      report.append("----> INDEXES [").append(this.getTableName()).append("] current: ");
      // ���� ������ �������� ����, �� ���� ������������� NullPointerException
      if ((this.indexes == null) || (this.indexes.isEmpty())) report.append("NO INDEXES.\n");
      else report.append("(").append(this.indexes.size()).append(") ").append(this.getCSVIndexedFieldsList()).append("\n");
      // ������ ������������� ����� ������� �������
      report.append("----> INDEXES [").append(foreign.getTableName()).append("] foreign: ");
      // ���� ������ �������� ����, �� ���� ������������� NullPointerException
      if ((foreign.indexes == null) || (foreign.indexes.isEmpty())) report.append("NO INDEXES.\n");
      else report.append("(").append(foreign.indexes.size()).append(") ").append(foreign.getCSVIndexedFieldsList()).append("\n");
      // ������� ��������� ������� �������� ������� � ������� ������
      report.append("----> INDEXES simple current.equals(foreign) = ");
      // ���� ��� ������ �� ����� - ������� �� ����������.
      if (this.indexes != null && !this.indexes.isEmpty() && foreign.getIndexes() != null && !foreign.getIndexes().isEmpty())
       {
        // ������ �������� ������� ���������� ������ - ���� �������� ������ �������� �� �������, ��� ����� ��������,
        // ��� ������������ ������ ������ ��� ������ - ����� ���������� � ���������� ������� ������ �������������
        // ����� - ��� ���� ������������� � ����� �������, ������ ���� �������������� � � ������
        if (this.indexes.equals(foreign.getIndexes())) report.append(true).append("\n");
        // ���� �������� ������ �������� �� ������� - �������� ����������� �������� �� ������ ������������� �����
        else if (this.getUniqueIndexesList().equals(foreign.getUniqueIndexesList())) {report.append(true).append("\n");}
        // ���� �� � ��� ������� �� ������� - ������ ��� ���-���� ������ :)
        else report.append(false).append("\n");
       }
      // ���� ��� ������ ����� ������������ - ��� ������������
      else if (((this.indexes == null) || (this.indexes.isEmpty())) &&
               ((foreign.getIndexes() == null) || (foreign.getIndexes().isEmpty())))
       {report.append(true).append("\n");}
      // ���� �� �� ���� �� �������� �� ������ - ������ ������
      else {report.append(false).append("\n");}
     }
    // ���� ���������� �������� ���� - ������ �� ���� � ������
    else {report = new StringBuilder("Foreign table object is NULL!");}
    return report.toString();
   }

  /**
   * ������ ����� ������������ ������ ��� ������������ ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(TableStructureModel.class.getName());
    Logger logger = Logger.getLogger(TableStructureModel.class.getName());
    try
     {
      TableStructureModel table  = new TableStructureModel("ffff");
      FieldStructureModel field  = new FieldStructureModel("123", 0, "", 0);
      FieldStructureModel field2 = new FieldStructureModel("456", 0, "", 0);
      IndexedField        index1 = new IndexedField("aaa", "bbb");
      IndexedField        index2 = new IndexedField("xxx", "bbb");
      table.addField(field);
      table.addField(field2);
      table.addIndex(index1);
      table.addIndex(index2);
      logger.info(table + " \nFIELDS: " + table.getCSVFieldsList());
      logger.info(index1.compareTo(index2) + " " + index1.equals(index2));
     }
    //catch (EmptyFieldNameException e)      {logger.error(e.getMessage());}
    //catch (EmptyIndexNameException e)      {logger.error(e.getMessage());}
    //catch (EmptyIndexFieldNameException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }