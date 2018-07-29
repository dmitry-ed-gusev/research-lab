package jdb.model.dto;

import jdb.exceptions.DBModelException;
import jdb.model.DBModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ����� ��������� ������ �� �� ����� �� ���������, ������ � �������.
 * ������ ����� ����� ���� ������������.
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.03.2011)
*/

public class DBDTOModel extends DBModel implements Serializable
 {
  static final long serialVersionUID = -714595024946774739L;

  /** ������ ������ ������ ��. */
  private ArrayList<TableDTOModel> tables = null;

  public DBDTOModel(String dbName) throws DBModelException {super(dbName);}

  public ArrayList<TableDTOModel> getTables() {return tables;}
  public void setTables(ArrayList<TableDTOModel> tableModels) {this.tables = tableModels;}

  @Override
  public boolean isEmpty()
   {
    boolean result = true;
    if ((tables != null) && (!tables.isEmpty())) {result = false;}
    return result;
   }

  /**
   * ����� ��������� ���� ������� (��������� ������ TableDTO) � ������ ������ ������ ������ ��. ���� ������� ����� - ���
   * �� ����� ���������. ���� ������ ������ ���� - �� ����� ���������������.
   * @param tableModel TableDTO ����������� � ������ �������.
  */
  public void addTable(TableDTOModel tableModel)
   {
    if (tableModel != null)
     {
      if (this.tables == null) {this.tables = new ArrayList<TableDTOModel>();}
      this.tables.add(tableModel);
     }
   }

  /**
   * ����� ���������� ������ ������� �� �� �����, ���� ������ ��������� � ����� ������� ����������.
   * ����� �������� null-safe - ��������� ������������ null-��������.
   * @param tableName String ��� �������, ������ ������� ���������� �����.
   * @return TableModel ������ ������� ��� null.
  */
  public TableDTOModel getTable(String tableName)
   {
    TableDTOModel tableModel = null;
    if ((this.tables != null) && (!this.tables.isEmpty()) && !StringUtils.isBlank(tableName))
     {
      // ����� ������� � ������ (�.�. ����� ������ �������� � ������� ��������, ��������� ���������� ��� � ������� �������)
      for (TableDTOModel table : this.tables)
       {
        if ((table != null) && (!StringUtils.isBlank(table.getTableName())) &&
            (table.getTableName().equals(tableName.toUpperCase())))
         {tableModel = table;}
       }
     }
    return tableModel;
   }

  /** ����� ��������� � ���������� ��������� ������������� ���������� ������� ������. */
  public String toString()
   {return ("\nDATABASE: " + this.getDbName() + "\nTABLES: " + this.tables);}

  @Override
  public int getTablesCount()
   {int result = 0; if ((this.tables != null) && (!this.tables.isEmpty())) {result = this.tables.size();} return result;}

 }