package jdb.model.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * ����� ��������� ������ ������ ���� (��� ������������ � ��������) ����� ������ �� ����������� ������� ����� ��.
 * ��� ���� �������� � ������� �������� - �.�. ����� ����� � ������ �������� ��������� �������. �������� ���� ��������
 * ��� ���� - "as is" (���, ��� ��� �������� � ���� ������).
 * ������ ����� ����� ���� ������������.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 20.03.2008)
*/

public class FieldDTOModel implements Serializable
 {
  static final long serialVersionUID = -8378955442192328474L;
  
  /** ������������ ����. */
  private String fieldName;
  /** �������� ����. */
  private String fieldValue;
  /** ��� ������ ���� ������� - ��� java. */
  private int    fieldType;
  
  public FieldDTOModel(String fieldName, String fieldValue, int fieldType)
   {
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {this.fieldName = fieldName;}
    this.fieldValue = fieldValue;
    this.fieldType  = fieldType;
   }

  public String getFieldName() {
   return fieldName;
  }

  public void setFieldName(String fieldName)
   {
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {this.fieldName = fieldName;}
   }

  public String getFieldValue() {
   return fieldValue;
  }

  public void setFieldValue(String fieldValue) {
   this.fieldValue = fieldValue;
  }

  public int getFieldType() {
   return fieldType;
  }

  public void setFieldType(int fieldType) {
   this.fieldType = fieldType;
  }

  /** ����� ��������� � ���������� ��������� ������������� ���������� ������� ������. */
  public String toString() {return ("[FIELD: " + this.fieldName + "; VALUE: " + this.fieldValue + "]");}

 }