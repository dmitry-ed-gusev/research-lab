package jdb.model;

/**
 * Класс реализует модель маппинга одного типа данных из СУБД к типу данных java. 
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 01.04.2008)
*/

public class TypeMapping
 {
  /** Наименование типа данных СУБД. */
  private String dbTypeName;
  /** Тип данных java. */
  private int    javaDataType;
  /** Размерность типа данных. */
  private int    maxPrecision;

  /**
   * Конструктор, инициализирующий все поля класса.
   * @param dbTypeName String тип данных СУБД.
   * @param javaDataType int тип данных java.
   * @param maxPrecision int размерность типа.
  */
  public TypeMapping(String dbTypeName, int javaDataType, int maxPrecision)
   {
    this.dbTypeName   = dbTypeName;
    this.javaDataType = javaDataType;
    this.maxPrecision = maxPrecision;
   }

  public String getDbTypeName() {
   return dbTypeName;
  }

  public void setDbTypeName(String dbTypeName) {
   this.dbTypeName = dbTypeName;
  }

  public int getJavaDataType() {
   return javaDataType;
  }

  public void setJavaDataType(int javaDataType) {
   this.javaDataType = javaDataType;
  }

  public int getMaxPrecision() {
   return maxPrecision;
  }

  public void setMaxPrecision(int maxPrecision) {
   this.maxPrecision = maxPrecision;
  }

  /** Строковое представление экземпляра данного класса. */
  public String toString()
   {return ("\n DBMS type: " + this.dbTypeName + "; JAVA SQL type: " + this.javaDataType);}

 }