package jdb.model.structure;

import jdb.exceptions.DBModelException;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * ����� ��������� ������ ������ ������������ ���� �� ����������� ������� ��. �������� ���� �� ��������, ��������
 * ��������� ��������� ����: ���, ��� ���� (java) - �������� ���� int, ��� ���� (����) - ��������� ������������ (String),
 * ����������� ����. ��� ���� �������� � ������� �������� �������� - ��� ����������� ���������� ������. ��� ���� ��
 * ����� ���� ������. ��� ������� ������� ����� ���� (������ ������ ��� �������� null) ������������ ��.<br>
 *
 * 26.12.2008 ������ ����� ��������� ��������� Comparable - ��� ����, ����� ����� ���� ����������� ���������� �������
 * ������ � ������ ArrayList �� ����� ����.<br>
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 23.07.2010)
*/

// todo: ������ equals() � hashCode() �� ������������ � ������� ����������������. ������������� �� �������������. ���������!

public class FieldStructureModel implements Serializable, Comparable
 {
  static final long serialVersionUID = 3603083484070873288L;

  /** ������������ ���� �������. */
  private String  name;
  /** ��� ������ ���� ������� - ��� JAVA. */
  private int     javaDataType;
  /**
   * ��������� ������������ ���� ������ ���� ������� - ��� ����. ��� ������ ���� ���� � ���� ���� ������
   * (����������� �� ���� JAVA) ����� ����� ��������� ��������� ������������.
  */
  private String  dbmsDataType;
  /** ����������� ���� �������. */
  private int     size;
  /** ������� - ����� �� ������ ���� ��������� �������� NULL. */
  private boolean isNullable;
  /** �������� ������� ���� �� ���������. ����� ���� �����. */
  private String  defaultValue;

  /**
   * ����������� �� ���������.
   * @param name String ���, ����������� ������ ���������� ���������� ������� ������.
   * @param javaDataType int ��� ���� ������.
   * @param dbmsDataType String ������������ ���� ���� ������ (��������� ������������).
   * @param size int ����������� ����.
   * @throws DBModelException �� ���������, ���� ��������� ���� � ������ ������.
  */
  public FieldStructureModel(String name, int javaDataType, String dbmsDataType, int size) throws DBModelException
   {
    if (!StringUtils.isBlank(name)) {this.name = name.toUpperCase();}
    else {throw new DBModelException("Name of the field is empty!");}
    this.javaDataType = javaDataType;
    this.dbmsDataType = dbmsDataType;
    this.size         = size;
    this.isNullable   = true;
    this.defaultValue = null;
   }

  public String getName() {return name;}

  public void setName(String name) throws DBModelException
   {
    if (!StringUtils.isBlank(name)) {this.name = name.toUpperCase();}
    else {throw new DBModelException("Name of the field is empty!");}
   }

  public int getJavaDataType() {return javaDataType;}
  public void setJavaDataType(int javaDataType) {this.javaDataType = javaDataType;}
  public String getDbmsDataType() {return dbmsDataType;}
  public void setDbmsDataType(String dbmsDataType) {this.dbmsDataType = dbmsDataType;}
  public int getSize() {return size;}
  public void setSize(int size) {this.size = size;}
  public boolean isNullable() {return isNullable;}
  public void setNullable(boolean nullable) {isNullable = nullable;}
  public String getDefaultValue() {return defaultValue;}
  public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}

  /** ��������� ������������� ���������� ������� ������ (������ ���� �������). */
  @Override
  public String toString()
   {
    return "FieldStructureModel{name='" + name + '\'' + ", javaDataType=" + javaDataType + ", dbmsDataType='"
            + dbmsDataType + '\'' + ", size=" + size + ", isNullable=" + isNullable + ", defaultValue='"
            + defaultValue + '\'' + '}';
   }

  /** ����� ��������� ���� ��������(�����������) ������� ������. ���������� ������������ ������ �� ����� ����. */
  /**
  @Override
  public boolean equals(Object obj)
   {
    // ��������� ��������� ����������� ������� ������
    boolean result = false;
    // ������� �������� ������������ �����������
    if (this == obj) result = true;
    // ���� ������� �������� �� ������ - ��������� ����� - ���� ����� �������� null ��� ������ �� ���������
    // (������ ���������� �� ������ �������) - ������������ �������� false � �������� ������������. ���� �� ���
    // ���������� ������ ������ - �������� ������� ������ � ������� ������ � ��������� ������������ ���� �����.
    else if (obj != null && this.getClass() == obj.getClass())
     {
      // �������� ��� (������ �� �����, ��� ������ ����� ��� FieldStructureModel � �� �������� �������)
      FieldStructureModel foreign = (FieldStructureModel) obj;
      // ���������� ����� ����� - ���� �������, �� �������� ��� ������ java - ���� ��� ���������� ���, �������
      // ����������� - ���� �������, �� ���� ������������. ���� �� ���� �� ����������� ���� - �� ��� ������������
      // ������ �� ������.
      if (name.equals(foreign.name))
       {
        // ���� ��� ���� ����� ���������� ��� ������ (���� ������ JAVA) - ������� �� �����������
        if (((this.javaDataType == Types.CHAR) || (this.javaDataType == Types.LONGNVARCHAR) ||
             (this.javaDataType == Types.LONGVARCHAR) || (this.javaDataType == Types.NCHAR) ||
             (this.javaDataType == Types.NVARCHAR) || (this.javaDataType == Types.VARCHAR)) &&
            ((foreign.javaDataType == Types.CHAR) || (foreign.javaDataType == Types.LONGNVARCHAR) ||
             (foreign.javaDataType == Types.LONGVARCHAR) || (foreign.javaDataType == Types.NCHAR) ||
             (foreign.javaDataType == Types.NVARCHAR) || (foreign.javaDataType == Types.VARCHAR)))
         {if (this.size == foreign.size) result = true;}
        // ���� ���� �� ����������� ���� - ��� ��������� ������������ �� ������.
        else {result = true;}
       }
     }
    return result;
   }
  */

  /**
   * ����� ���������� ���-��� �������. ���-��� - ������������� ��� ������������� ����� �����. �������������
   * �������� ������ ��������������� ���������� ���-����. � ��������� ���-���� ������ ����������� �� ����,
   * ������� ��������� � �������� ��������� - ����� equals() ������� ������.
  */
  /**
  public int hashCode()
   {
    int result;
    result = name.hashCode();
    // ���� ���� ���������� - � ��������� ���� ������ ����������� ������ ����
    if ((this.javaDataType == Types.CHAR) || (this.javaDataType == Types.LONGNVARCHAR) ||
        (this.javaDataType == Types.LONGVARCHAR) || (this.javaDataType == Types.NCHAR) ||
        (this.javaDataType == Types.NVARCHAR) || (this.javaDataType == Types.VARCHAR))
     {result = 31 * result + size;}
    //result = 31 * result + javaDataType;
    //result = 31 * result + dbmsDataType.hashCode();
    return result;
   }
  */
  
  @Override
  /** ��������� ���� ����������� ������� ������ �� ������� ������-������. */
  public int compareTo(Object o)
   {
    FieldStructureModel field = (FieldStructureModel)o;
    return this.name.compareTo(field.getName()); // <- ���������� � ������ ���������� �������
    // return field.getName().compareTo(this.name); // <- ���������� ����� � �������� ���������� �������
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(FieldStructureModel.class.getName());
    Logger logger = Logger.getLogger(FieldStructureModel.class.getName());
    try
     {
      FieldStructureModel field1 = new FieldStructureModel("aaa", 0, "", 10);
      FieldStructureModel field2 = new FieldStructureModel("aaa", 0, "", 10);
      FieldStructureModel field3 = new FieldStructureModel("bbb", 0, "", 10);

      logger.info(field1.equals(field2));
      logger.info(field2.equals(field1));
      logger.info(field1.equals(field3));

      logger.info(field1.compareTo(field2));
      logger.info(field2.compareTo(field1));
      logger.info(field3.compareTo(field3));

      logger.info(field1.hashCode() + " " + field2.hashCode() + " " + field3.hashCode());
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }