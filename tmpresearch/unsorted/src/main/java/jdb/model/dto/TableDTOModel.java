package jdb.model.dto;

import jdb.exceptions.DBModelException;
import jdb.model.TableModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ������ ����� ��������� ������ ������� �� ����� �� � ������� (��������� ��� ��������) - ������ ������� ������
 * java (ResultSet). ������ ����� ����� ���� ������������. ��� ������� �������� � ������� �������� ��������.
 * ����� ������������ (� ��������) ��� ���������� ������ � ������� �� ��������� ���� - ��. ����� DBSerializer. 
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 20.03.2008)
*/

public class TableDTOModel extends TableModel implements Serializable
 {
  static final long serialVersionUID = 6590356882080866767L;

  /** ������ ������� ������ �������. */
  private ArrayList<RowDTOModel> rowModels = null;

  public TableDTOModel(String tableName) throws DBModelException {super(tableName);}

  public ArrayList<RowDTOModel> getRows() {return rowModels;}
  public void setRows(ArrayList<RowDTOModel> rowModels) {this.rowModels = rowModels;}

  /**
   * ����� ��������� ���� ������ (��������� ������ TableRowDTO) � ������ ������� ������ �������. ���� ������ ����� - ��� ��
   * ����� ���������. ���� ������ ������� ��� ���� - �� ����� ������������������.
   * @param rowModel TableRowDTO ����������� � ������ ����.
  */
  public void addRow(RowDTOModel rowModel)
   {if (rowModel != null) {if (this.rowModels == null) this.rowModels = new ArrayList<RowDTOModel>(); this.rowModels.add(rowModel);}}

  /** ����� ��������� � ���������� ��������� ������������� ���������� ������� ������. */
  public String toString() {return ("\nTABLE: " + this.getTableName() + "\nROWS: " + this.rowModels);}

 }