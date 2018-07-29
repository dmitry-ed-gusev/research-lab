package jdb.model.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ����� ��������� ������ ����� ������ (�� ����� ������ � �� ����������) �� ����� ������� ����� ��.
 * ������ ����� ����� ���� ������������.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 24.03.2008)
*/

public class RowDTOModel implements Serializable
 {
  static final long serialVersionUID = 796418028120737040L;

  /** ������ ����� ����� ������ � �� ����������. */
  private ArrayList<FieldDTOModel> fieldsModels = null;

  public RowDTOModel() {}

  public ArrayList<FieldDTOModel> getFields() {return fieldsModels;}
  public void setFields(ArrayList<FieldDTOModel> fieldModels) {this.fieldsModels = fieldModels;}

  /**
   * ����� ��������� ���� ����(��������� ������ TableFieldDTO) � ������ ����� ������ ������. ���� ���� ����� - ��� ��
   * ����� ���������. ���� ������ ��� ���� - �� ����� ������������������.
   * @param fieldModel TableFieldDTO ����������� � ������ ����.
  */
  public void addField(FieldDTOModel fieldModel)
   {
    if (fieldModel != null)
     {if (this.fieldsModels == null) this.fieldsModels = new ArrayList<FieldDTOModel>(); this.fieldsModels.add(fieldModel);}
   }

  /**
   * ����� ������� � ������� ������ TableRowDTO ���� � ������ fieldName � ���������� ���. ���� �������� fieldName ����
   * ��� ����� ���� �� ������� - ����� ���������� �������� null.
   * @param fieldName String ��� �������� ����.
   * @return TableFieldDTO ��������� �� ����� ���� ��� �������� null.
  */
  public FieldDTOModel getFieldByName(String fieldName)
   {
    FieldDTOModel fieldModel = null;
    // �.�. ����� ����� �������� � ������� ��������, �� ��� ���� ��� ������ ����� ��������� � ������� �������
    if (!StringUtils.isBlank(fieldName) && (this.fieldsModels != null) && (!this.fieldsModels.isEmpty()))
     {
      for (FieldDTOModel localFieldModel : this.fieldsModels)
       {
        if ((localFieldModel != null) && (fieldName.toUpperCase().equals(localFieldModel.getFieldName())))
         {fieldModel = localFieldModel;}
       }
     }
    return fieldModel;
   }

  /**
   * ����� ���������� CSV-������ ����� ������ ������ ��� �������� null, ���� ������ ����.
   * @return String CSV-������ ����� ������ ������ ��� �������� null.
  */
  public String getCSVFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.fieldsModels != null) && (!this.fieldsModels.isEmpty()))
     {
      csvList = new StringBuilder();
      for (int i = 0; i < this.fieldsModels.size(); i++)
       {
        csvList.append(this.fieldsModels.get(i).getFieldName());
        if (i < this.fieldsModels.size() - 1) csvList.append(",");
       }
     }
    // ��������� ���������
    String result;
    if (csvList == null) {result = null;} else {result = csvList.toString();}
    return result;
   }

  /** ����� ��������� � ���������� ��������� ������������� ���������� ������� ������. */
  public String toString() {return ("\n ROW: " + this.fieldsModels);}

  /**
   * ����� ���������� �������� ������, ���� ������ ��������� ������ ���� (���� ������ �����).
   * @return boolean ����� ���������� ������/���� � ����������� �� ����, ���� �� ������ ������� ����� ��� ������ ������.
  */
  public boolean isEmpty() {return ((this.fieldsModels == null) || (this.fieldsModels.isEmpty()));}

 }