package jdb.model;

import jdb.exceptions.DBModelException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * ������ ����� ��������� ���������� ������ ������� ��. ������ ������ ����� ������ ������� ���� ��������
 * ��� � ������� �������� ��������. ��� ��������� ������ ������ ������������ �� ������ ������.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 23.07.2010)
*/

public class TableModel implements Serializable
 {
  static final long serialVersionUID = -982570254006000243L;

  /** ����� ������, ������� ����������� �������. */
  private String tableSchema = null;
  /** ������������ �������. */
  private String tableName   = null;
  /** ��� �������. */
  private String tableType   = null; 

  /**
   * ����������� �� ���������. ����������� �������������� ��� ��.
   * @param tableName String ��� ����������� ������ ��.
   * @throws DBModelException �� ��������� ��� �������� ������ �� � ������ ������.
  */
  public TableModel(String tableName) throws DBModelException
   {
    if (!StringUtils.isBlank(tableName)) {this.tableName = tableName.toUpperCase();}
    else {throw new DBModelException("Name of the table is empty!");}
   }

  /**
   * ����� ������� � ����� �������.
   * @return String ��� ������� �������.
  */
  public String getTableName() {return tableName;}

  /**
   * ����� ��������� ����� �������.
   * @param tableName String ��� ����������� ������ ��.
   * @throws DBModelException �� ��������� ��� �������� ������ �� � ������ ������.
  */
  public void setTableName(String tableName) throws DBModelException
   {
    if (!StringUtils.isBlank(tableName)) {this.tableName = tableName.toUpperCase();}
    else {throw new DBModelException("Name of the table is empty!");}
   }

  public String getTableSchema() {
   return tableSchema;
  }

  public void setTableSchema(String tableSchema)
   {
    if (!StringUtils.isBlank(tableSchema)) {this.tableSchema = tableSchema.toUpperCase();}
    else {this.tableSchema = tableSchema;}
  }

  public String getTableType() {
   return tableType;
  }

  public void setTableType(String tableType) {
   this.tableType = tableType;
  }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("tableSchema", tableSchema).
            append("tableName", tableName).
            append("tableType", tableType).
            toString();
   }
  
 }