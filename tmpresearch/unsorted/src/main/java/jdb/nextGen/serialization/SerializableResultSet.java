package jdb.nextGen.serialization;

import jdb.DBConsts;
import jdb.nextGen.exceptions.JdbException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ����� ��������� ������������� ����� ������� �� (ResultSet). ������������ ������ ���������� ���������� ��������� ������
 * ResultSet � �� ��� ������ ����� SerializableResultSet ��������� ������� ���� ����.
 * <br>����������� ������� ������:
 * <ul>
 *  <li> ����� �������� immutable - �� ������ ���� ��������� ����� �������� ����������. �.� ������ "���������" ��� ������� �
 *       ����� ������ ���������� ��� ���������� "��������" (defensive) ����� ����� � �������.
 *  <li> ��� ������� �������� ��� �������� ���������� ������ - ����������� ������ ���� ��������!
 *  <li> ����� ����� (��������) ������� � ��� ������� �������� � ������� �������� �������� - ��� �������� ������
 *       (����� �� ������� �� �������� ��������).
 *  <li> ������������ ��������� ���� �������� ��������� �� ��������� - DBConsts.FIELD_NAME_KEY (="ID"). �� �������������
 *       ������� ��� ��������� ����� �������� - � ������ ������� ������ ���� ������� (�� ���������) �������� ���� ID
 *       (��� ������ � ������ �������).
 * </ul>
 *
 * @author Gusev Dmitry (����� �������)
 * @version 6.0 (DATE: 09.06.2011)
*/

@SuppressWarnings({"NullableProblems"})
public final class SerializableResultSet implements Serializable
 {
  /** ���� ��� ������������� � ������������ �������� ������. */
  static final            long               serialVersionUID = 8540809276925800602L;
  // ������ ������.
  transient private final Logger             logger           = Logger.getLogger(this.getClass().getName());

  // ��� �������. �������� ���������� ������������.
  private final String                       tableName;
  // ������ ��������� ���� � ������ ����� �������.
  private final int                          keyFieldIndex;
  // ������ ���� ����� �������.
  private final ArrayList<String>            fieldsNames;
  // ������ ����� ������ �������.
  private final ArrayList<ArrayList<String>> data;

  /**
   * �����������.
   * @param rs ResultSet
   * @param tableName String
   * @throws SQLException ������ ��� ������ � �������� ������ (ResultSet)
   * @throws JdbException ������ � ���������� ������.
  */
  public SerializableResultSet(ResultSet rs, String tableName) throws SQLException, JdbException
   {
    // ��������� ��������� ��� �������
    if (!StringUtils.isBlank(tableName))
     {
      // ��� �������
       this.tableName = tableName.toUpperCase();
      // ��������� ������ ������ � ��������� ��������� �� ������ (��� ���������) ������
      if ((rs != null) && rs.next())
       {
        // ��������������
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        // ������������� ����� (����� �����, ������)
        fieldsNames  = new ArrayList<String>(columnCount);
        data         = new ArrayList<ArrayList<String>>();
        // ��������� ���� ���������
        for (int i = 1; i <= columnCount; i++)
         {fieldsNames.add(i - 1, meta.getColumnName(i).toUpperCase());}
        // ������������ ��������� ���� - �� ��������� �������� ���������. ��������� �����������
        // ������ (����� � �������(������) �����) ��� ������� ��������� ����.
        this.keyFieldIndex = fieldsNames.indexOf(DBConsts.FIELD_NAME_KEY);
        // ���� � ������� ��� ��������� ���� - ������!
        if (keyFieldIndex < 0) {throw new SQLException("No such index field [" + DBConsts.FIELD_NAME_KEY + "]!");}
        // ��������� ���� � ������� (������������ ��������)
        do
         {
          ArrayList<String> row = new ArrayList<String>(columnCount);
          for (int i = 1; i <= columnCount; i++) {row.add(rs.getString(i));}
          data.add(row);
         }
        while (rs.next());
       }
      // ���� ������� ������ ResultSet - ������� �� ���� � ��� - ��� ������������� ������!
      else
       {
        logger.warn("Empty ResultSet object! Empty class instance was created!");
        this.keyFieldIndex = -1;   // ���� ������ ���������� ����������������!
        this.fieldsNames   = null; // ���� ������ ���������� ����������������!
        this.data          = null; // ���� ������ ���������� ����������������!
       }
     }
    // ���� ������� ������ ��� ������� - ���������� ��
    else {throw new JdbException("Empty table name!");}
   }

  /**
   * �����������.
   * @param rs ResultSet
   * @param tableName String
   * @param count int
   * @throws SQLException ������ ��� ������ � �������� ������ (ResultSet)
   * @throws JdbException ������ � ���������� ������.
  */
  public SerializableResultSet(ResultSet rs, String tableName, int count) throws SQLException, JdbException
   {
    // ��������� ��������� ��� �������
    if (!StringUtils.isBlank(tableName))
     {
      // ��� �������
      this.tableName = tableName.toUpperCase();
      if ((count > 0) && (rs != null))
       {
        //logger.debug("Table name [" + tableName + "] ok, count [" + count + "] ok, ResultSet not null. Processing."); // <- DEBUG!
        // ��������� (��� ������������) ������ ������
        ArrayList<ArrayList<String>> localData = new ArrayList<ArrayList<String>>();
        // ��������������
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        // ��������� ���� � �������
        int     counter       = 0;    // ���-�� ������������ ������� (�������)
        boolean iterationFlag = true; // ���� ��������, ���� = true -> �������� ������������
        // ��������������� ��������� ������ (������������ ��������)
        while ((counter < count) && iterationFlag)
         {
          if (rs.next())
           {
            ArrayList<String> row = new ArrayList<String>(columnCount);
            for (int i = 1; i <= columnCount; i++) {row.add(rs.getString(i));}
            localData.add(row);
            counter++;
           }
          else {iterationFlag = false;}
         }
        // ���� �� ������� ���� ��������� ������ - �������������� ���� � ��������������� (����� � ���� �����)
        if (!localData.isEmpty())
         {
          // ���� ������ "������" �������� ������ �� ��������� ������ ������
          this.data = localData;
          // ���� � ������� ���� ������ - �������������� ���� � ��������� ��������
          fieldsNames  = new ArrayList<String>(columnCount);
          // ��������� ���� ���������
          for (int i = 1; i <= columnCount; i++)
           {fieldsNames.add(i - 1, meta.getColumnName(i).toUpperCase());}
          // ������������ ��������� ���� - �� ��������� �������� ���������. ��������� �����������
          // ������ (����� � �������(������) �����) ��� ������� ��������� ����.
          this.keyFieldIndex = fieldsNames.indexOf(DBConsts.FIELD_NAME_KEY);
          // ���� � ������� ��� ��������� ���� - ������!
          if (keyFieldIndex < 0) {throw new SQLException("No such index field [" + DBConsts.FIELD_NAME_KEY + "]!");}
         }
        // ���� �� ������ ��� - ������� ��������������������� ���� � �������. �������� �� ������ �� �������.
        // ����� ����������������� ������� ���������� ��������� ���� �������
        else
         {
          logger.info("Empty SerializableResultSet class instance was created!");
          this.keyFieldIndex = -1;   // ���� ������ ���������� ����������������!
          this.fieldsNames   = null; // ���� ������ ���������� ����������������!
          this.data          = null; // ���� ������ ���������� ����������������!
         }
       }
      // ���� ������ == NULL ��� ������� �������� ���-�� ������� (<= 0), �� ��������� ������ ���-���� �����
      // ���������������, �� ���� ������ ��������� ������� (������ ������)
      else
       {
        logger.warn("Wrong count value [" + count + "] or ResultSet is NULL! Empty SerializableResultSet class instance was created!");
        this.keyFieldIndex = -1;   // ���� ������ ���������� ����������������!
        this.fieldsNames   = null; // ���� ������ ���������� ����������������!
        this.data          = null; // ���� ������ ���������� ����������������!
       }
     }
    // ���� ������� ������ ��� ������� - ���������� ��
    else {throw new JdbException("Empty table name!");}
   }


  /**
   * ����� ���������� ������ ������� (������ �� ���������� ������ �� ������������ - ������������ ���
   * "��������" (defensive) �����).
   * @return ArrayList[ArrayList[String]]
  */
  public ArrayList<ArrayList<String>> getData() {return new ArrayList<ArrayList<String>>(data);}

  /**
   * ������ (���������) ��������� ���� � ������ �����.
   * @return int ������ ��������� ����. ���� ��� ��������� ���� ��� ������ �� ������ - ����� ������ �������� -1.
  */
  public int getKeyFieldIndex() {return keyFieldIndex;}

  /**
   * ���������� ��� �������, ���������� �� ResultSet'a, �� ������ �������� �������� ������ ��������� ������. ����
   * ��������� ���� (�������� �� ������ ������� ResultSet'a), �� ����� ������ �������� NULL.
   * @return String ��� ������� ��� NULL.
  */
  public String getTableName() {return tableName;}

  /**
   * ����� ���������� ���������� ����� ������ ������. ���� ����� �������� �� ������ ResultSet'e - ����� ������
   * �������� 0.
   * @return int ���������� ����� ������.
  */
  public int getRowsCount()
   {
    int result = 0;
    if ((data != null) && (!data.isEmpty())) {result = data.size();}
    return result;
   }

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, �������� �� ������ ��������� ������ ������.
   * ��������� ������� ������ ��������� ������, ���� ����� ���� �� �����: ���� "������������ �������" (tableName)
   * ��� ���� "������" (data).
   * @return boolean ������/���� � ����������� �� ����, ���� ��� ��� ��������� ������� ������.
  */
  public boolean isEmpty()
   {
    boolean result = true;
    if (!StringUtils.isBlank(tableName) && (data != null) && (!data.isEmpty())) {result = false;}
    return result;
   }

  /**
   * ����� ���������� � ���������� ����������������� (prepared) sql-������ INSERT, ������� ����� �������������� ���
   * ������� ������, ������������ � ������ ������������� �������, � ������� ��. ���� � ������� ���� ������ � ��������
   * ������ ����� - ������ ����� ������������, � ��������� ������ ����� ������ �������� NULL. ���� ������ ��������
   * tableName, �� � ��������������� ������� ��� ������� (���� ����������� ������), �������� ��� �������� ����������
   * ������, ����� �������� �� ��������� (���� ��� �� �����).
   * @param tableName String ��� ������� ��� ��������� ������� INSERT.
   * @return String ��������������� INSERT-������ ��� NULL.
  */
  public String getPreparedInsertSql(String tableName)
   {
    String result = null;
    if (!this.isEmpty() && (fieldsNames != null) && (!fieldsNames.isEmpty()))
     {
      StringBuilder sql = new StringBuilder("insert into ");
      // ���� ������ �������� - ������ ��� �������, �� ���������� ��� ��������
      if (!StringUtils.isBlank(tableName)) {sql.append(tableName);}
      else                                 {sql.append(this.tableName);}
      sql.append("(");

      // ��������� ������ ����� ��� �������
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        sql.append(fieldsNames.get(i));
        if (i < (fieldsNames.size() - 1)) {sql.append(", ");}
       }
      sql.append(") values (");
      // ��������� ����������� ���������� ������ ? (������)
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        sql.append("?");
        if (i < (fieldsNames.size() - 1)) {sql.append(", ");}
       }
      // ��������� ������������ insert-�������
      sql.append(")");
      result = sql.toString();
     }
    return result;
   }

  /**
   * ����� ���������� � ���������� ����������������� (prepared) sql-������ INSERT, ������� ����� �������������� ���
   * ������� ������, ������������ � ������ ������������� �������, � ������� ��. ���� � ������� ���� ������ � ��������
   * ������ ����� - ������ ����� ������������, � ��������� ������ ����� ������ �������� NULL. ��� �������� �������
   * ������������ ��� �������, �������� � ������������ ������.
   * @return String ��������������� INSERT-������ ��� NULL.
  */
  public String getPreparedInsertSql() {return this.getPreparedInsertSql(null);}

  /**
   * ����� ���������� � ���������� ����������������� (prepared) sql-������ UPDATE, ������� ����� �������������� ���
   * ���������� ������ � ������� �� �������, ������������� � �������. ���� � ������� ���� ������, �������� ������ �����
   * � ������ ����� �������� �������� ���� - ������ ����� ������������, � ��������� ������ ����� ������ �������� NULL.
   * ���� ������ �������� tableName, �� � ��������������� ������� ��� �������, �������� ��� �������� ���������� ������,
   * ����� �������� �� ��������� (���� ��� �� �����).
   * @param tableName String ��� ������� ��� ��������� ������� UPDATE.
   * @return String ��������������� UPDATE-������ ��� NULL.
  */
  public String getPreparedUpdateSql(String tableName)
   {
    String result = null;
    // ��� ������� ������������ ������� ���������� ���������� ��������� �������: ������ ������ ��������� ������,
    // ������ ������������ ����� �� ������ ���� ����, ������ ���� ������� �������� ��� �������, ������ ����� �������
    // ������ ����������� ��������� �������� ����.
    if (!this.isEmpty() && (fieldsNames != null) && (!fieldsNames.isEmpty()) && fieldsNames.contains(DBConsts.FIELD_NAME_KEY))
     {
      StringBuilder sql = new StringBuilder("update ");
      // ���� ������ �������� - ������ ��� �������, �� ���������� ��� ��������
      if (!StringUtils.isBlank(tableName)) {sql.append(tableName);}
      else                                 {sql.append(this.tableName);}
      sql.append(" set ");
      // ��������� ������ ����� ��� �������
      for (int i = 0; i < fieldsNames.size(); i++)
       {
        // �������� ��������� ���� �� �� ��������� - �� ��������� ��� ������ ��
        // ��������� �������� ��������� ����.
        if (!fieldsNames.get(i).equals(DBConsts.FIELD_NAME_KEY))
         {
          sql.append(fieldsNames.get(i)).append(" = ?");
          // ������� ��������� ������ ���� ������������ ���� �� �������� � �� ��������� � ������ (��� ���������,
          // ��� �� ������ ����� ������ �������� - ������� �������������, �������� - ���������)
          if ((i < (fieldsNames.size() - 1)) && !((i == (fieldsNames.size() - 2)) && (keyFieldIndex == fieldsNames.size() - 1)))
           {sql.append(", ");}
         }
       }
      sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ?");
      result = sql.toString();
     }
    return result;
   }

  /**
   * ����� ���������� � ���������� ����������������� (prepared) sql-������ UPDATE, ������� ����� �������������� ���
   * ���������� ������ � ������� �� �������, ������������� � �������. ���� � ������� ���� ������, �������� ������ �����
   * � ������ ����� �������� �������� ���� - ������ ����� ������������, � ��������� ������ ����� ������ �������� NULL.
   * ��� �������� ������� ������������ ��� �������, �������� � ������������ ������.
   * @return String ��������������� UPDATE-������ ��� NULL.
  */
  public String getPreparedUpdateSql() {return this.getPreparedUpdateSql(null);}

 }