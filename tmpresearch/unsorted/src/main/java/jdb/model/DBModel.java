package jdb.model;

import jdb.DBConsts.DBType;
import jdb.exceptions.DBModelException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * ������ ����������� ����� ��������� ����� ������ ���� ������ - ��� ����� ������ ������� ���� ��� (� ������ �� �����������
 * ������ ���� �������� ���) � ������������� � ���� ������. ��� �������� � ������� �������� ��������. ����� ������ �����
 * ��������� ����������� ������, ������� ������ ���� �� ���� ��� ��������.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 21.03.2011)
*/

abstract public class DBModel implements Serializable
 {
  // ���� ������������ ��� ������������� ����������� ������ ������ � ����������� (��� ��������� ������������)
  static final long serialVersionUID = -310649003724243853L;

  /** ������������ ��. */
  private String dbName = null;
  /** ��� ����, � ������� �������� ��, ������ ������� ������������ ������ ���������. */
  private DBType dbType = null;

  /**
   * ����������� �� ���������. ����������� �������������� ��� ��.
   * @param dbName String ��� ����������� ������ ��.
   * @throws DBModelException �� ��������� ��� �������� ������ �� � ������ ������.
  */
  public DBModel(String dbName) throws DBModelException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else {throw new DBModelException("Name of the database is empty!");}
   }

  /**
   * ����� ������� � ����� ��.
   * @return String ��� ������� ��.
  */
  public String getDbName() {return this.dbName;}

  /**
   * ����� ��������� ����� ��.
   * @param dbName String ��� ����������� ������ ��.
   * @throws DBModelException �� ��������� ��� �������� ������ �� � ������ ������.
  */
  public void setDbName(String dbName) throws DBModelException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else {throw new DBModelException("Name of the database is empty! Can't set empty name!");}
   }

  public DBType getDbType()            {return dbType;}
  public void setDbType(DBType dbType) {this.dbType = dbType;}
  
  /**
   * ����� ���������� ���������� ������ � ������ ������ ��. ������ ������� ������� ������ ������ ����� ����
   * ���������� ������� ������.
   * @return int ���������� ������ � ������ ������ ��.
  */
  abstract public int getTablesCount();

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, ���� ��� ��� ��������� ������� ������. ������
   * �����-������� ������� ������ ������ ����� ���� ���������� ������� ������.
   * @return boolean ������/���� � ����������� �� ���� ���� ��� ��� ��������� ������� ������.
  */
  abstract public boolean isEmpty();

  /**
   * ����� ������ ������� �� ����� � ������ ������ ������ ������ ��.
   * @param tableName String ��� �������, �� �������� ���� ������� � ������.
   * @return TableModel ������������ ������ ������� �� ��� �������� NULL.
  */
  abstract public TableModel getTable(String tableName);

  /** ��������� ������������� ���������� ������-������ ��. */
  abstract public String toString();

 }