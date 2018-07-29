package jdb.model.structure;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * ������ ����� �������� ������ �������� �� (DBStructureModel) � �����������, �������������� ���
 * �������� ������ ������ - ������ "�����������" � "�����������" ������. ������ ����� ����� ��� �������
 * ���������� � ������������� ������ ������� Storm.
 * @author Gusev Dmitry (gusev)
 * @version 1.0 (DATE: 03.10.2008)
*/

public class DBStructureModelConstrained implements Serializable
 {
  static final long serialVersionUID = 5843959363113209891L;

  /** ������ ��������� ���� ������. */
  private DBStructureModel  dbModel;
  /** ������ "�����������" ������, �������������� ������� DBSpider ��� �������� ������ ������ ��. */
  private ArrayList<String> allowedTables;
  /** ������ "�����������" ������, �������������� ������� DBSpider ��� �������� ������ ������ ��. */
  private ArrayList<String> deprecatedTables;

  public DBStructureModelConstrained()
   {
    this.dbModel          = null;
    this.allowedTables    = null;
    this.deprecatedTables = null;
   }

  public DBStructureModel getDbModel() {
   return dbModel;
  }

  public void setDbModel(DBStructureModel dbModel) {
   this.dbModel = dbModel;
  }

  public ArrayList<String> getAllowedTables() {
   return allowedTables;
  }

  public void setAllowedTables(ArrayList<String> allowedtables) {
   this.allowedTables = allowedtables;
  }

  public ArrayList<String> getDeprecatedTables() {
   return deprecatedTables;
  }

  public void setDeprecatedTables(ArrayList<String> deprecatedTables) {
   this.deprecatedTables = deprecatedTables;
  }

 }