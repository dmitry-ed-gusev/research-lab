package jdb.model.structure.key;

import jdb.DBConsts;
import jdb.exceptions.DBModelException;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * ����� ��������� ������ ������ �������������� ����. �������� ��� �������, ��� ����, ��� �������, �������
 * ������������ �������. ������������� ���� ����������� ������ ��������� ��� ������� � ��� ����. ��� ���������
 * �������� � ������� �������� ��������.<br>
 *
 * 26.12.2008 ������ ����� ��������� ��������� Comparable - ��� ����, ����� ����� ���� ����������� ���������� �������
 * ������ � ������ ArrayList �� ����� ����.
 *
 * 26.01.09 ��� ����������� ��������� ������������� ����� ��� ������� � ��� ���� ������ ��������� � �������
 * �������� ��������.
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 23.07.2010)
*/

// todo: ������ equals() � hashCode() �� ������������ � ������� ����������������. ������������� �� �������������. ���������!
 
public class IndexedField implements Comparable, Serializable
 {
  static final long serialVersionUID = -4169826276815804319L;
  
  /** ������������ �������. */
  private String    indexName;
  /** ������������ �������������� ����. */
  private String    fieldName;
  /** ��� �������. */
  private DBConsts.IndexType type;
  /** ������� ������������ �������. */
  private boolean   isUnique;
  /** ������� - �������� �� ������ ���� ������ ���������� ����� �������. */
  private boolean   isPrimaryKey;

  public IndexedField(String indexName, String fieldName) throws DBModelException
   {
    if (!StringUtils.isBlank(indexName)) {this.indexName = indexName.toUpperCase();}
    else {throw new DBModelException("Index name is empty!");}
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {throw new DBModelException("Indexed field name is empty!");}
    this.isUnique     = false;
    this.isPrimaryKey = false;
    this.type         = null;
   }

  public String getIndexName() {return indexName;}

  public void setIndexName(String indexName) throws DBModelException
   {
    if (!StringUtils.isBlank(indexName)) {this.indexName = indexName.toUpperCase();}
    else {throw new DBModelException("Index name is empty!");}
   }

  public String getFieldName() {return fieldName;}

  public void setFieldName(String fieldName) throws DBModelException
   {
    if (!StringUtils.isBlank(fieldName)) {this.fieldName = fieldName.toUpperCase();}
    else {throw new DBModelException("Indexed field name is empty!");}
   }

  public DBConsts.IndexType getType() {
   return type;
  }

  public void setType(DBConsts.IndexType type) {
   this.type = type;
  }

  public boolean isUnique() {return isUnique;}
  public void setUnique(boolean unique) {isUnique = unique;}

  public boolean isPrimaryKey() {return isPrimaryKey;}
  public void setPrimaryKey(boolean primaryKey) {isPrimaryKey = primaryKey;}

  @Override
  public String toString()
   {
    return "[" + "indexName='" + indexName + '\'' + ", fieldName='" + fieldName + '\'' +
            ", type=" + type + ", isUnique=" + isUnique + ", isPrimaryKey=" + isPrimaryKey +']';
   }

  /**
   * ��������� ���� ����������� ������� ������. ��������� ������������ �� ����� �������������� ����, �����
   * ������� � �� �������� ������������ ������� (����� �������� �� ������ ���� ����� �� ���������).
  */
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
      IndexedField foreign = (IndexedField) obj;
      // ���������� ����� ������������� ����� (����� �������� � ������ ���� ����� �� ���������) � ��������
      // ������������ ��������
      //if (fieldName.equals(foreign.fieldName) && (indexName.equals(foreign.indexName)) &&
      //    ((isUnique && foreign.isUnique) || (!isUnique && !foreign.isUnique)))
      // result = true;
      if (fieldName.equals(foreign.fieldName)) result = true;
     }
    return result;
   }
  */

  /**
  @Override
  public int hashCode()
   {
    int result = fieldName.hashCode();
    //result     = 31*result + indexName.hashCode();
    //result     = 31*result + (isUnique ? 1 : 0);
    return result;
   }
  */

  @Override
  /***/
  public int compareTo(Object o)
   {
    IndexedField field = (IndexedField)o;
    // ������ ���������� - �� ����� ����
    int result = this.fieldName.compareTo(field.getFieldName());
    // ���� ����� ����� ��������� - ��������� �� ����� �������
    if (result == 0) {result = this.indexName.compareTo(field.getIndexName());}
    // ���������� ��������� ���������
    return result;
   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(IndexedField.class.getName());
    Logger logger = Logger.getLogger(IndexedField.class.getName());
    try
     {
      IndexedField field1 = new IndexedField("aaa", "bbb");
      IndexedField field2 = new IndexedField("aaa", "bbb");
      IndexedField field3 = new IndexedField("aaa", "zzz");

      logger.info(field1.equals(field2));
      logger.info(field2.equals(field1));
      logger.info(field1.equals(field3));
      logger.info(field3.equals(field2));

      logger.info(field1.compareTo(field2));
      logger.info(field2.compareTo(field1));
      logger.info(field1.compareTo(field3));
      logger.info(field3.compareTo(field2));

      logger.info(field1.hashCode() + " " + field2.hashCode() + " " + field3.hashCode());
     }
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }