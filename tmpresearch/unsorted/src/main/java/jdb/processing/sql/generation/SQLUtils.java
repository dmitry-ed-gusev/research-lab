package jdb.processing.sql.generation;

import jdb.DBConsts.DBType;
import jdb.model.DBModelConsts;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.structure.key.IndexedField;
import org.apache.log4j.Logger;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * ������ ����� �������� ��������� ��������������� ������ ��� ������ ���������� sql-�������� (����� SQLGenerator).
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 04.02.2009)
*/

public class SQLUtils
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(SQLUtils.class.getName());

  /**
   * ����� ���������� � ���������� SQL-�������� ���� �������, ��������� �������� ������������ �����������
   * ������ FieldStructureModel. ���� ������� ���� - ����� ������ �������� NULL.
   * @param field FieldStructureModel ������� ��� �������� ��������.
   * @param targetDBType DBType ��� ���� ����������, ��� ������� ������������ ��������. ����� ���� ����.
   * @return String ��������������� ��������������� �������� ���� ��� null.
  */
  public static String getFieldSQL(FieldStructureModel field, DBType targetDBType)
   {
    StringBuilder sql = null;

    // ������������ ����, ������ ���� ��� �� �����
    if (field != null)
     {
      sql = new StringBuilder(field.getName()).append(" ");
      // ���� � ���� ���� �������� �� ���������, �� ��� �������� �������������� ��� � ����� ����������
      // �������� ���� (switch {}), ��� ����� ����� �����. ����� ��� ���� �� ������������ �������� �� ��������� �
      // ����� ������ ������.
      boolean isDefaultProcessed = false;
      
      switch (field.getJavaDataType())
       {
        // ������� ��� ������
        case Types.NUMERIC:
          // ���� �� ������ ��� ���� ��� ������� ��������� ������ - ���������� ��� �������� ����
          if (DBType.INFORMIX.equals(targetDBType))      {sql.append("DECIMAL(13,2)");}
          else if (DBType.MYSQL.equals(targetDBType))    {sql.append("DECIMAL(13,2)");}
          else sql.append(field.getDbmsDataType());
         break;

        // ������� ��� ������
        case Types.DECIMAL:
          // ���� �� ������ ��� ���� ��� ������� ��������� ������ - ���������� ��� �������� ����
          if (DBType.MYSQL.equals(targetDBType)) sql.append("DECIMAL(13,2)");
          else sql.append(field.getDbmsDataType());
         break;

        // ������������� ���
        case Types.INTEGER: sql.append(field.getDbmsDataType()); break;

        // ������� ��� ������
        case Types.DOUBLE: sql.append(field.getDbmsDataType()); break;

        // ��� ������ ����
        case Types.DATE: sql.append(field.getDbmsDataType()); break;

        case Types.BIT: sql.append(field.getDbmsDataType()); break;

        // ���������� ��� ������ ������������� �����
        case Types.CHAR:
          // ���� ������ ��� ������� ����, �� ������ ������������� ��� ��� ���������� ���
          if (DBType.INFORMIX.equals(targetDBType)) {sql.append("NCHAR");}
          else if (DBType.DBF.equals(targetDBType)) {sql.append("CHAR");}
          else if (DBType.MYSQL.equals(targetDBType)) {sql.append("NVARCHAR");}
          else {sql.append(field.getDbmsDataType());}

          // ��������� ����������� ����.
          sql.append("(").append(field.getSize()).append(")");

          // ��� ����������� ���� �������� �� ��������� (���� ��� ����) ������ ���� � ��������
          if (field.getDefaultValue() != null)
           {
            // ��������� ������ ��������� �������� �� ���������
            isDefaultProcessed = true;
            // �������� �� ��������� ������ ���������� � ������������ �� ��������� �������. ���� ��� �� ��� -
            // ������� ����� ��������.
            String defaultValue = field.getDefaultValue();
            if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
             {sql.append(" DEFAULT '").append(defaultValue).append("'");}
            else {sql.append(" DEFAULT ").append(defaultValue);}
           }
         
         break;

        // ���������� ��� ������ ���������� �����
        case Types.VARCHAR:
          // ���� ������ ��� ������� ����, �� ������ ������������� ��� ��� ���������� ���
          if (DBType.INFORMIX.equals(targetDBType)) {sql.append("NVARCHAR");}
          else if (DBType.DBF.equals(targetDBType)) {sql.append("VARCHAR");}
          else {sql.append(field.getDbmsDataType());}
          // ��������� ����������� ����.
          sql.append("(").append(field.getSize()).append(")");
          // ��� ����������� ���� �������� �� ��������� (���� ��� ����) ������ ���� � ��������
          if (field.getDefaultValue() != null)
           {
            // ��������� ������ ��������� �������� �� ���������
            isDefaultProcessed = true;
            // �������� �� ��������� ������ ���������� � ������������ �� ��������� �������. ���� ��� �� ��� -
            // ������� ����� ��������.
            String defaultValue = field.getDefaultValue();
            if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
             {sql.append(" DEFAULT '").append(defaultValue).append("'");}
            else {sql.append(" DEFAULT ").append(defaultValue);}
           }
         break;

        // ������� ���������� ��� ������ ������������� ����� (� DBF - memo - ����)
        case Types.LONGVARCHAR:
         // ���� ������ ��� ������� ��, �� ������ ������������� ��� ��� ���������� ���
         if (DBType.INFORMIX.equals(targetDBType))
          {sql.append("NCHAR(").append(DBModelConsts.LONGVARCHAR_SIZE).append(")");}
         else
          {sql.append(field.getDbmsDataType()).append("(").append(field.getSize()).append(")");}
         
         // ��� ����������� ���� �������� �� ��������� (���� ��� ����) ������ ���� � ��������
         if (field.getDefaultValue() != null)
          {
           // ��������� ������ ��������� �������� �� ���������
           isDefaultProcessed = true;
           // �������� �� ��������� ������ ���������� � ������������ �� ��������� �������. ���� ��� �� ��� -
           // ������� ����� ��������.
           String defaultValue = field.getDefaultValue();
           if ((!defaultValue.startsWith("'")) && (!defaultValue.endsWith("'")))
            {sql.append(" DEFAULT '").append(defaultValue).append("'");}
           else {sql.append(" DEFAULT ").append(defaultValue);}
          }
         break;

        // ��� ������ ��������� (�����+����)
        case Types.TIMESTAMP:
         // ��������������� ��� ������
         sql.append(field.getDbmsDataType());
         // ��������� �������� �� ���������
         if (field.getDefaultValue() != null)
          {
           switch (targetDBType)
            {
             // ���� �������� ���� - MySQL, �� �������� �� ��������� ���������� ��� ������� ����.
             case MYSQL:
              // ��������� ������ - �������� �� ��������� ����������
              isDefaultProcessed = true;
              // todo: ��������� �������� �� ��������� ��� MySQL!
              logger.warn("DEFAULT VALUE FOR FIELD [" + field.getName() + "] TYPE [TIMESTAMP] IGNORED!");
              break;
            }
          }
         break;

        // ���� �� ���� �� ����� ������ �� �������
        default:
          sql.append("[TYPE: ").append(field.getJavaDataType()).append("]");
          logger.fatal("UNKNOWN DATA TYPE: " + field.getJavaDataType());
         break;
       }
      
      // ���� ��� ���� ������� �������� �� ��������� - ���������� ��� (��������) - ���� ��� ��� �� ����������!
      if ((field.getDefaultValue() != null) && (!isDefaultProcessed))
       {sql.append(" DEFAULT ").append(field.getDefaultValue());}

      // ���� ������ ���� �� ����� ��������� �������� NULL - ��������� ���
      if (!field.isNullable()) {sql.append(" NOT NULL");}
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * ����� ��������� � ���������� sql-������ ������ ��� �������� ������� ��. ����� ��� ����������� �������������.
   * ������ ����� �� ���������� sql-������ ��� �������� � ����������� ������ �������. ���� ��������� � ��������
   * ��������� ������ ����� - ����� ������ �������� null.
   * @param table TableStructureModel ������ �������, �� ������� ����� ������������ ������.
   * @param targetDBType DBType ��� ����, ��� ������� ��������� ������ ������.
   * @param usePrimaryKey boolean ������ �������� ��������� - ������������ (true) ��� ��� (false) ����� ���
   * �������� ���������� �����. ���� ������� ����� true - ����� "��������� ����" ����� ������� � sql-���������
   * [CREATE TABLE...], ���� �� ������� ����� false, �� ������ "���������� �����" ����� ������ ���������� ������ ��
   * ��� �� �����, ������� ������ � ��������� ����.
   * @param addSemi boolean ��������� ��� ��� ����������� � ����� ������� sql-������� (����������� -> ;)
   * @return String sql-������ ��� �������� ������ �������.
  */
  public static String getCreateTableFieldsSQL(TableStructureModel table, DBType targetDBType,
                                               boolean usePrimaryKey, boolean addSemi)
   {
    StringBuilder sql = null;
    // ���� ���������� ������� �� ����� - ��������
    if (table != null)
     {
      sql = new StringBuilder("CREATE TABLE ").append(table.getTableName()).append(" \n(");
      // ������������ ������ ����� ������ ������� (���� �� �� ����)
      if ((table.getFields() != null) && (!table.getFields().isEmpty()))
       {
        Iterator iterator = table.getFields().iterator();
        while (iterator.hasNext())
         {
          // �������� ����
          FieldStructureModel field = (FieldStructureModel) iterator.next();
          // ������������� ��� (����) sql-��������
          sql.append("\n").append(SQLUtils.getFieldSQL(field, targetDBType));
          // ���� ���� - �������� �������
          if (iterator.hasNext()) {sql.append(",");}
         }
        // ���� ������� ����� usePrimaryKey=true � � ������� ���� ��������� ����� - ���������� ������ [PRIMARY KEY...]
        if (usePrimaryKey)
         {
          String keys = table.getCSVPKFieldsList();
          // ���� ���� ��������� ���� - ���������� ������ [PRIMARY KEY...]
          if ((keys != null) && (!keys.isEmpty())) {sql.append(", \n  PRIMARY KEY (").append(keys).append(")");}
         }
        sql.append("\n)");
        // ���� ����� ����-�-������� � ����� ������� - ������� ��
        if (addSemi) {sql.append(";");}
        // ������� ������ �������� ������ � ����� ������� (��� ��� ���������������)
        sql.append("\n");
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * ����� ���������� sql-������ ��� �������� �������� ������ �������. ����� ��� ����������� �������������.
   * ������ ����� �� ���������� sql-������ ��� ����� �������. ���� ��������� � �������� ��������� ������
   * ������� ����� - ����� ������ �������� null.
   * @param table TableStructureModel ������ �������, �� ������� ����� ������������ ������.
   * @param targetDBType DBType ��� ����, ��� ������� ��������� ������ ������.
   * @param usePrimaryKey boolean ������ �������� ��������� - ������������ (true) ��� ��� (false) ����� ���
   * �������� ���������� �����. ���� ������� ����� true - ����� "��������� ����" ����� ������� � sql-���������
   * [CREATE TABLE...], ���� �� ������� ����� false, �� ������ "���������� �����" ����� ������ ���������� ������ ��
   * ��� �� �����, ������� ������ � ��������� ����.
   * @param addSemi boolean ��������� ��� ��� ����������� � ����� ������� sql-������� (����������� -> ;)
   * @return ArrayList[String] ������ sql-�������� ��� �������� �������� �������.
  */
  public static ArrayList<String> getCreateTableIndexesSQL(TableStructureModel table, DBType targetDBType,
                                                           boolean usePrimaryKey, boolean addSemi)
   {
    ArrayList<String> sql = null;
    // ���� ������� �� ����� � � ������ ������� ������ ���� ������� - ����� ��������� sql-������
    if ((table != null) && (table.getIndexes() != null) && (!table.getIndexes().isEmpty()))
     {
      sql = new ArrayList<String>();
      // ������������ ������� ������ �������
      TreeSet<String> processed = new TreeSet<String>();
      for (IndexedField field : table.getIndexes())
       {
        // ���� ������� ����� usePrimaryKey=true � ������ ���� ������ � ������ ���������� ����� - ���������� ���
        // (��� ����� usePrimaryKey=true ������ ������� ����� ������������� ������ [PRIMARY KEY...])
        if (!(usePrimaryKey && field.isPrimaryKey()))
         {
          // ���� ������ � ������ ������ ��� �� ������������� - ������������
          if (!processed.contains(field.getIndexName()))
           {
            // ��������� ������ � ������ ������������
            processed.add(field.getIndexName());
            // ��������������� ��������� sql-������ �� �������� �������
            StringBuilder query = new StringBuilder("CREATE ");
            // ������� ������������ �������
            if (field.isUnique()) {query.append("UNIQUE ");}
            query.append("INDEX ").append(field.getIndexName());
            query.append(" ON ").append(table.getTableName()).append(" (");
            // �������� �� ����� ������ ������������� �����, ������� ���� ��� ������� �������
            boolean flag = false;
            for (IndexedField iField : table.getIndexes())
             {
              if (iField.getIndexName().equals(field.getIndexName()))
               {if (!flag) {flag = true;} else {query.append(",");} query.append(iField.getFieldName());}
             }
            query.append(")");
            // ���� � ����� ������� ����� �����-�-������� - ���������
            if (addSemi) {query.append(";");}
            // ��������� ������ �������� ������ � ����� ������� - ��� ���������������
            query.append("\n");
            
            // ��������� ������ ��������� � ��������������� ������ ��������
            sql.add(query.toString());
           }
         }
       }
     }
    return sql;
   }

  /**
   * ����� ���������� sql-������� �� ����� �����. ��� ������ ����� �� ������� ������������ �� ������ ���� ������� -
   * ���� ������ ���� ��������� � �����-���� ����������� ��� �������. �����, ������ �����, ���� ����� ����������
   * � ���� ������.
   * @param current TableStructureModel �������, ������� � ������� sql-������� ����� �������� � ������� �������.
   * @param foreign TableStructureModel �������-������� ��� ��������� ������� current.
   * @return String sql-������(�) ��� ������ ����� � ������� current.
  */
  // todo: ����� ������ ���������� ArrayList<String>
  public static String getDropFieldsSQL(TableStructureModel current, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // ���� ��� ������� �� ����� � �� ����������� - ����� ���������� sql-������ ��� ����������� �������
    // current � ������� foreign (����� ����� �� ������� current, ����� ��� ����� ������ �� foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      // �������� �� ������� current � ���������� � ��� �� ����, ������� ��� � ������� foreign
      for (FieldStructureModel field : current.getFields())
       {
        boolean found = false;
        Iterator iterator = foreign.getFields().iterator();
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel foreignField = (FieldStructureModel) iterator.next();
          // ���� ���� � ����� ��, ��� � ���� ������� ������� current ������ ������� - ��� ��.
          if (field.getName().equals(foreignField.getName())) {found = true;}
         }
        // ���� ������ �� ���� ����� ������� foreign �� �� ����� ������ ���� - ���� � ������ �� ��������
        if (!found)
         {
          if (sql == null) sql = new StringBuilder();
          sql.append("ALTER TABLE ").append(current.getTableName()).append(" DROP ").append(field.getName()).append(";\n");
         }
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * ����� ���������� sql-������� ��� ���������� �����. ��� ���������� ����� � ������� �� ������ ��������� �������.
   * @param current TableStructureModel �������, ������� � ������� sql-������� ����� �������� � ������� �������.
   * @param currentDBType DBType ��� ���� ������� current.
   * @param foreign TableStructureModel �������-������� ��� ��������� ������� current.
   * @return String sql-������(�) ��� ���������� ����� � ������� current.
  */
  // todo: ����� ������ ���������� ArrayList<String>
  public static String getAddFieldsSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // ���� ��� ������� �� ����� � �� ����������� - ����� ���������� sql-������ ��� ����������� �������
    // current � ������� foreign (����� ����� �� ������� current, ����� ��� ����� ������ �� foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      for (FieldStructureModel field : foreign.getFields())
       {
        boolean found = false;
        Iterator iterator = current.getFields().iterator();
        // �������� �� ����� ������� current - ���� ����, ����������� �������� ���� foreign
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel currentField = (FieldStructureModel) iterator.next();
          // ���� ����� ����������� ����(�� �����) - ����� ���� ��������� ��� �� ����. ���, ����� ����,
          // ���� ������ �������� - �� ��� ��� ������ ������� ������.
          if (field.getName().equals(currentField.getName())) {found = true;}
         }
        // ���� �� �� ����� ���� �� ������� foreign � ������� current - ��� ���� �������� � ������� current
        if (!found)
         {
          if (sql == null) sql = new StringBuilder();
          sql.append("ALTER TABLE ").append(current.getTableName()).append(" ADD ");
          sql.append(SQLUtils.getFieldSQL(field, currentDBType)).append(";\n");
         }
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }
  
  /**
   * ����� ���������� sql-������� ��� ����������� ����� ������� current. ��� ��������� ����(-��) �������� ���������
   * ������ ��������...
   * @param current TableStructureModel �������, ������� � ������� sql-������� ����� �������� � ������� �������.
   * @param currentDBType DBType ��� ���� ������� current.
   * @param foreign TableStructureModel �������-������� ��� ��������� ������� current.
   * @return String sql-������(�) ��� ��������� ����� � ������� current.
  */
  // todo: ����� ������ ���������� ArrayList<String>
  public static String getChangeFieldsSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    StringBuilder sql = null;
    // ���� ��� ������� �� ����� � �� ����������� - ����� ���������� sql-������ ��� ����������� �������
    // current � ������� foreign (����� ����� �� ������� current, ����� ��� ����� ������ �� foreign)
    if (current != null && foreign != null && !current.equals(foreign))
     {
      for (FieldStructureModel field : foreign.getFields())
       {
        boolean found = false;
        Iterator iterator = current.getFields().iterator();
        // �������� �� ����� ������� current - ���� ����, ����������� �������� ���� foreign
        while (iterator.hasNext() && !found)
         {
          FieldStructureModel currentField = (FieldStructureModel) iterator.next();
          // ���� ����� ����������� ����(�� �����) - ��������� ��� � ����� ������� current.
          if (field.getName().equals(currentField.getName()))
           {
            // ����� � ����� ������ ���������� - ��������� ���� ��� ���
            found = true;
            // ���� �� ���� �������� - ������� ���� �� ������� foreign ����� �������� ��� ���� � ����� �� ������
            // �� ������� current
            if (!field.equals(currentField))
             {
              if (sql == null) sql = new StringBuilder();
              sql.append("ALTER TABLE ").append(current.getTableName()).append(" MODIFY ");
              sql.append(SQLUtils.getFieldSQL(field, currentDBType)).append(";\n");
             }
           }
         }
        // ���� �� �� ����� ���� �� ������� foreign � ������� current - ��� ���� �������� �
        // ������� current - �� ��� ��� ������ �����
       }
     }
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

 }