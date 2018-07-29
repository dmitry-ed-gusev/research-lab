package jdb.processing.sql.generation;

import jdb.DBConsts;
import jdb.filter.sql.SqlFilter;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Types;

/**
 * ����� �������� ��������������� ������ ��� ���������� INSERT � UPDATE �������� �� ������ ������
 * ������ �������.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 22.06.2010)
*/

public class DataChangeSQLBuilder
 {
  /** ���������-������ ��� ������� ������. */
  private static Logger logger = Logger.getLogger(DataChangeSQLBuilder.class.getName());

  /**
   * ����� ������ sql-������ INSERT �� ������ ������ ������ �������. ����� ����� ������������ ���������� sql-�������� �
   * ��������� ��������.
   * @param tableName String ��� �������.
   * @param rowModel RowDTOModel ������ ������ ������ �������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� ������ (� �������� ��� ��������� ��������).
   * @return String ��������������� ������ ��� �������� null. 
  */
  public static String getDataInsertSql(String tableName, RowDTOModel rowModel, boolean useSqlFilter)
   {
    //logger.debug("Creating data insert sql."); // <- ����� ���������� ������� ����� ����������� ������

    // ���������� ��� ���������� � ����������� ����������
    String        result = null;
    StringBuilder sql    = null;

    // ���� ��� ������� ����� ��� ����� ������ ������ - ����� ������ �������� null
    if ((!StringUtils.isBlank(tableName)) && (rowModel != null) && (!rowModel.isEmpty()))
     {
      sql = new StringBuilder("insert into ");
      sql.append(tableName).append("(").append(rowModel.getCSVFieldsList()).append(") values (");
      // �������� �� ���� ����� ������ ������ � ��������� sql-������
       for (int i = 0; i < rowModel.getFields().size(); i++)
        {
         // �������� ������� �������������� ���� � �������
         FieldDTOModel fieldDTOModel = rowModel.getFields().get(i);
         // ���� ���������� ���� �� ����� - ������������ ���
         if (fieldDTOModel != null)
          {
           // ���� ��� ��������������� ���� ���������� ��� ����/����� - ��������� ��� � �������
           int fType = fieldDTOModel.getFieldType();
           if ((fType == Types.DATE) || (fType == Types.TIMESTAMP) || (fType == Types.CHAR) ||
               (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR))
            {
             // ���� �������� ���� = NULL ��� � ������ �������� �����, � �� ������������ � 'NULL' (��������� - ������!)
             String fieldValue = fieldDTOModel.getFieldValue();
             if (fieldValue == null) {sql.append("null");}
             else
              {
               // ���� �������� ���������� � ������������ ���������� - �������� ����������
               if (((fType == Types.CHAR) || (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR)) && (useSqlFilter)) 
                {sql.append("'").append(SqlFilter.changeQuotes(fieldValue)).append("'");}
               // ���� �� �������� �� ��������� ��� ��������� ���������� - ���������� ������� ��������
               else
                {sql.append("'").append(fieldValue).append("'");}
              }
            }
           // ���� ��� ���� �� ���������� - ������� �� ����������� � �������
           else {sql.append(fieldDTOModel.getFieldValue());}
           // ������ ������� � ������ �����
           if (i < rowModel.getFields().size() - 1) {sql.append(",");}
          }
         // ���� �� ���������� ���� ����� - ������� � ��� ������ ���������
         else {logger.error("Processing error - empty field in row!");}
        }
       sql.append(")");
     }
    // ���� �� ��������� �� � ������� - ������� �� ������
    else {logger.error("Table name or table row model is empty!");}

    // ����������� ��������� � ������ (���� ��������� �� ����) � ������� ��� � ���������� ������
    if (sql != null)
     {
      result = sql.toString();
      // logger.debug("Generated SQL: " + sql); // <- ����� ���������� ������� ����� ����������� ������
     }
    // ���� �������������� ������ ���� - ������� �� ����
    else             {logger.warn("Result sql is empty!");}

    // ��������������� ����������� ����������
    return result;
   }

  /**
   * ����� ������ sql-������ UPDATE �� ������ ������ ������ �������. ����� ����� ������������ ���������� sql-�������� �
   * ��������� ��������.
   * @param tableName String ��� �������.
   * @param rowModel RowDTOModel ������ ������ ������ �������.
   * @param id int �������� ��������� ���� ��� ���������� ������ ������.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� ������ (� �������� ��� ��������� ��������).
   * @return String ��������������� ������ ��� �������� null.
  */
  public static String getDataUpdateSql(String tableName, RowDTOModel rowModel, int id, boolean useSqlFilter)
   {
    //logger.debug("Creating data update sql."); // <- ����� ���������� ������� ����� ����������� ������

    // ���������� ��� ���������� � ����������� ����������
    String        result = null;
    StringBuilder sql    = null;

    // ���� ��� ������� ����� ��� ����� ������ ������ - ����� ������ null.
    if (!StringUtils.isBlank(tableName) && (rowModel != null) && (!rowModel.isEmpty()) && (id > 0))
     {
      sql = new StringBuilder("update ").append(tableName).append(" set");
      // �������� �� ���� ����� ������ ������ � ��������� sql-������
      for (int i = 0; i < rowModel.getFields().size(); i++)
       {
        // ������� �������������� ����
        FieldDTOModel fieldDTOModel = rowModel.getFields().get(i);
        // ���� ���������� ���� �� ����� - ������������ ���
        if (fieldDTOModel != null)
         {
          sql.append(" ").append(fieldDTOModel.getFieldName()).append(" = ");
          // ���� ��� ��������������� ���� ���������� ��� ����/����� - ��������� ��� � �������
          int fType = fieldDTOModel.getFieldType();
          if ((fType == Types.DATE) || (fType == Types.TIMESTAMP) || (fType == Types.CHAR) ||
              (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR))
           {
            // ���� �������� ���� = NULL ��� � ������ �������� �����, � �� ������������ � 'NULL'
            String fieldValue = fieldDTOModel.getFieldValue();
            if (fieldValue == null) {sql.append("null");}
            else
             {
              // ���� �������� ���������� � ������������ ���������� - �������� ����������
              if (((fType == Types.CHAR) || (fType == Types.VARCHAR) || (fType == Types.LONGVARCHAR)) && (useSqlFilter))
               {sql.append("'").append(SqlFilter.changeQuotes(fieldValue)).append("'");}
              // ���� �� �������� �� ��������� ��� ��������� ���������� - ���������� ������� ��������
              else
               {sql.append("'").append(fieldValue).append("'");}
             }
           }
          else {sql.append(fieldDTOModel.getFieldValue());}
          // ������ ������� � ������ �����
          if (i < rowModel.getFields().size() - 1) {sql.append(",");}
         }
        // ���� �� ���������� ���� ����� - ������� � ��� ������ ���������
        else {logger.error("Processing error - empty field in row!");}
       }
      sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ").append(id);
     }
    // ���� �� ��������� �� � ������� - ������� �� ������
    else {logger.error("Table name, table row model or id is empty!");}

    // ����������� ��������� � ������ (���� ��������� �� ����) � ������� ��� � ���������� ������
    if (sql != null)
     {
      result = sql.toString();
      //logger.debug("Generated SQL: " + sql); // <- ����� ���������� ������� ����� ����������� ������
     }
    // ���� �������������� ������ ���� - ������� �� ����
    else             {logger.warn("Result sql is empty!");}
    
    // ��������������� ����������� ����������
    return result;
   }

 }