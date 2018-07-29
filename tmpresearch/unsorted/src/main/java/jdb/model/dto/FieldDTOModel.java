package jdb.model.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Класс реализует модель одного поля (его наименование и значение) одной записи из абстрактной таблицы любой БД.
 * Имя поля хранится в верхнем регистре - т.к. имена полей в разном регистре считаются разными. Значение поля хранится
 * как есть - "as is" (так, как оно получено в виде строки).
 * Данный класс может быть сериализован.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 20.03.2008)
*/

public class FieldDTOModel implements Serializable
 {
  static final long serialVersionUID = -8378955442192328474L;
  
  /** Наименование поля. */
  private String fieldName;
  /** Значение поля. */
  private String fieldValue;
  /** Тип данных поля таблицы - тип java. */
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

  /** Метод формирует и возвращает строковое представление экземпляра данного класса. */
  public String toString() {return ("[FIELD: " + this.fieldName + "; VALUE: " + this.fieldValue + "]");}

 }