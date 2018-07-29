package jdb.nextGen.models;

import jdb.nextGen.exceptions.JdbException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * �����-������ ����������� �� � ��������� ������ ������ ��� ���� ������ ��. ����� �����-immutable, �.�. �� �� ���������
 * ������ ��� ��, � ������ ��������� ������� � ������ ��.
 * @author Gusev Dmitry (����� �������)
 * @version 4.0 (DATE: 30.05.2011)
*/

// todo: �����, ������� ����� immutable?

public final class SimpleDBIntegrityModel implements Serializable
 {
  /** ���� ��� ������������� � ������������ �������� ������. */
  static final long serialVersionUID = -1345660993253251L;

  //
  private final String                              dbName;
  //
  private final HashMap<String, ArrayList<Integer>> tables = new HashMap<String, ArrayList<Integer>>();

  /***/
  public SimpleDBIntegrityModel(String dbName) throws JdbException
   {
    if (!StringUtils.isBlank(dbName)) {this.dbName = dbName.toUpperCase();}
    else                              {throw new JdbException("Empty db name!");}
   }

  public String getDbName() {return dbName;}

  /**
   * ���������� ������� � ������ ������ ������ ��. ���� ����� ��� ����������� ������� - ������������ �� JdbException.
   * ���� ���� ��� NULL ������ ������ ��� �������, �� ������� �����������, ������ ������ ��� ��� ������������ ��� NULL.
   * ���� ������ ������ ������� �� ���� � �� NULL, �� �������� ���� �� ���� NULL-��������, �� ������������ �� JdbException -
   * ������ ������ �� ����� ��������� ������ ��������. ���� �� ������ �� ����, �� NULL � �� �������� �� ������ NULL-�����,
   * �� �� ����������� � ����� ������ ������ ������ ��.
  */
  public void addTable(String name, ArrayList<Integer> keys) throws JdbException
   {
    // ��������� ��������� ��� �������
    if (!StringUtils.isBlank(name))
     {
      // ���� ��������� ��� ������� ������ ������ �� ���� - ��������� ���.
      if ((keys != null) && (!keys.isEmpty()))
       {
        // ���� �������� ������ ������ ������� �������� ���� ���� �������� NULL - ������������ ��, �.�.
        // ������ ������ �� ����� ��������� ������ ��������.
        if (!keys.contains(null)) {tables.put(name.toUpperCase(), keys);}
        else {throw new JdbException("Keys list for table [" + name.toUpperCase() + "] contains NULL-key(s)!");}
       }
      // ���� ������ ������ ��� ������� ���� ��� NULL - ������� ����� �����������, �� ������ ������
      // �� ������ ��������������� �������� NULL.
      else {tables.put(name.toUpperCase(), null);}
     }
    // ��� ������� �����
    else {throw new JdbException("Empty table name!");}
   }

  /***/
  public ArrayList<Integer> getKeysListForTable(String tableName)
   {
    ArrayList<Integer> list = null;
    if (!StringUtils.isBlank(tableName) && (tables != null) && (!tables.isEmpty()))
     {list = new ArrayList<Integer>(tables.get(tableName));}
    return list;
   }

  /***/
  public Set<String> getTablesList()
   {
    Set<String> list = null;
    if ((this.tables != null) && (!this.tables.isEmpty())) {list = new HashSet<String>(this.tables.keySet());}
    return list;
   }

  /***/
  public boolean containsTable(String tableName)
   {
    boolean result = false;
    if (!StringUtils.isBlank(tableName) && (!this.tables.isEmpty()) && this.tables.containsKey(tableName)) {result = true;}
    return result;
   }

  /***/
  public boolean isEmpty()
   {
    boolean result = true;
    if (!this.tables.isEmpty()) {result = false;}
    return result;
   }

 }