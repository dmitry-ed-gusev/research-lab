package jdb.model;

/**
 * ����� ��������� ������ �������� ������ ���� ������ �� ���� � ���� ������ java. 
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 01.04.2008)
*/

public class TypeMapping
 {
  /** ������������ ���� ������ ����. */
  private String dbTypeName;
  /** ��� ������ java. */
  private int    javaDataType;
  /** ����������� ���� ������. */
  private int    maxPrecision;

  /**
   * �����������, ���������������� ��� ���� ������.
   * @param dbTypeName String ��� ������ ����.
   * @param javaDataType int ��� ������ java.
   * @param maxPrecision int ����������� ����.
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

  /** ��������� ������������� ���������� ������� ������. */
  public String toString()
   {return ("\n DBMS type: " + this.dbTypeName + "; JAVA SQL type: " + this.javaDataType);}

 }