package jdb.processing.loading.helpers;

import jdb.DBConsts;
import jdb.exceptions.DBModelException;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.time.TableTimedModel;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * ����� �������� ������ ��������� sql-�������� ��� �������� (������������) ������ �� ��. ������������ ����� ������. 
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 30.07.2010)
*/

public class DataExportSQLBuilder
 {
  /** ������ ������. */
  private static Logger logger = Logger.getLogger(DataExportSQLBuilder.class.getName());

  /**
   * ����� ���������� sql-������ ��� �������� (������������) ����� ������� ��. ������ ������������ �� ������ ����������
   * ������� ������ - TableStructureModel, TableIntegrityModel, TableTimedModel. � ����������� �� �� ������� ������ �����
   * ������������ ��-�������. �������� ������ - TableStructureModel, ���� �� ���, ��  ����� ������ �������� null.
   * @param structureTable TableStructureModel
   * @param integrityTable TableIntegrityModel
   * @param timedTable TableTimedModel
   * @return String ��������������� ������ ��� �������� null. 
  */
  public static String getExportTableSQL(TableStructureModel structureTable, TableIntegrityModel integrityTable,
                                         TableTimedModel timedTable)
   {
    String        result = null;
    StringBuilder sql    = null;

    // ���� ������ ������� �� ����� � ��� ������� �� ����� - ��������
    if ((structureTable != null) && (!StringUtils.isBlank(structureTable.getTableName())))
     {
      // �������� csv-������ ����� ����������� �������. ���� �� �� ���� - ��������!
      String csvList = structureTable.getCSVFieldsList();
      if (!StringUtils.isBlank(csvList))
       {
        logger.debug("Structure table is not empty and fields list is not empty! Processing.");
        // �������� �������������� sql-������ ��� �������� �������. ���� � ������ ��������� ������� ������� �����
        // ������ - ���������� ��.
        sql = new StringBuilder("select ").append(csvList).append(" from ");
        if (!StringUtils.isBlank(structureTable.getTableSchema())) {sql.append(structureTable.getTableSchema()).append(".");}
        sql.append(structureTable.getTableName());
        //logger.debug("Pregenerated SQL: " + sql.toString());

        // ���� ������� ������ ����������� �������. ���� ������ ����������� �������, �� sql ��� �������� ��
        // ���������� ����� ����������� ��-�������
        boolean isIntegrityPresent = false;
        // ���� ������� ������ ������� � ��������� �������. ���� ����� ������ ����, �� � sql-������� ����� ���������� ��
        // ����������, ���� �� ������ ���, �� ����� ���������� �� �������������� (��������� ����) - ���� ��� ���� � ������ �����.
        boolean isTimedPresent     = false;

        // ���� ������� ��������� ���� � ����������� �������
        boolean isKeyFieldPresent = false;
        if (structureTable.getField(DBConsts.FIELD_NAME_KEY) != null) {isKeyFieldPresent = true;}
        // ���� ������� ���� "���������" ("timestamp")
        boolean isTimestampPresent = false;
        if (structureTable.getField(DBConsts.FIELD_NAME_TIMESTAMP) != null) {isTimestampPresent = true;}

        // ���� ���� ������ ����������� ������� (�.�. ��� �� �����) � �� ���� ������ �� ������ - ���������
        // ������ �� ������, ����� ������� ���� � ������ ������ �����������. ����� ����������� ������� ��������� ���� -
        // ��� ���� �� ����� ������ ��������� � ������ ������ �����������.
        if (isKeyFieldPresent && (integrityTable != null) && (!StringUtils.isBlank(integrityTable.getTableName())) &&
            (!StringUtils.isBlank(integrityTable.getCSVKeysList())))
         {
          logger.debug("Integrity model exists! Adding to loading SQL.");
          sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" in (");
          sql.append(integrityTable.getCSVKeysList()).append(")");
          //logger.debug("Pregenerated SQL: " + sql.toString());
          isIntegrityPresent = true;
         }

        // ���� ���� ������ ������� � ��������� ������� - ��������� ������ �� ����������. ����� �� ����������
        // ��������� ����������� ������. ����� ����������� ������� ���� "���������" - ��� ���� �� ����� ������
        // ��������� � ������ ������ ������� � ��������� �����������.
        if (isTimestampPresent && (timedTable != null) && (!StringUtils.isBlank(timedTable.getTableName())) &&
            (timedTable.getTimeStamp() != null))
         {
          logger.debug("Timed model exists! Adding to loading SQL.");
          Timestamp timeStamp = timedTable.getTimeStamp();
          // ���� ���� timestamp � java ����� � ����� �������� ������� -> '2008-07-07 12:10:32.0', ��� �� ������������
          // ��������� (� ������ ������ ����) - ������� ���������� �������������� ������� (����� ��� ������� ������).
          // ���� �������������� �� ������� - �� ��������� � ��������� sql �������� ������ �� ����������.
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          try
           {
            String strTimeStamp  = sdf.format(sdf.parse(timeStamp.toString()));
            
            // ���� �������������� ������� - ���������� ������������� ���. ���� �� ������� - �� � sql ���������
            // �� ����� ��������� ������� � ���������� �� ���������.

            // ���� ������ ����������� ���� ��������� � �������, �� ��������� �������� ���������� ���
            if (isIntegrityPresent) {sql.append(" and ");}
            // ���� �� ������ ����������� �� ���� - ��������� �������� ����������� ���
            else                    {sql.append(" where ");}
            // ��������� ��������� �������� ������� �� ����������
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" > '").append(strTimeStamp).append("'");
            // ��������� ���������� �� ����������
            sql.append(" order by ").append(DBConsts.FIELD_NAME_TIMESTAMP);
            // ����� �������� ������ � ������� ������� � ��������� ������� - �������� ������ ������� ����� ������
            isTimedPresent = true;
           }
          catch (ParseException e) {logger.error("Can't parse timestamp [" + timeStamp + "]. Reason: " + e.getMessage());}
         }

        // ���� ��� ������ � �������� ������� - ��������� ���������� �� ��������� ���� (��������������). ����������
        // ��������� ������ ���� � ������� ���� ���� "���������"
        if (!isTimedPresent && isTimestampPresent)
         {
          logger.debug("No timed model for table! Adding ordering by key field.");
          sql.append(" order by ").append(DBConsts.FIELD_NAME_KEY);
         }

        // ���������� ����� ������������� sql-�������
        logger.debug("Generated loading table SQL: " + sql.toString());
       }
      // ���� ������ ����� ������ ������� ���� - ������! ����� ������ NULL.
      else {logger.error("CSV fields for table [" + structureTable.getTableName() + "] is empty!");}
     }
    // ���� ������ ������� ����� - ������� � ���
    else {logger.error("TableStructureModel is NULL or table name is empty!");}

    // �������������� ���������� � ������
    if (sql != null) {result = sql.toString();}
    return result;
   }

  /**
   * ������ ����� ������������ ������ ��� ������������ ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(DataExportSQLBuilder.class.getName());
    Logger logger = Logger.getLogger(DataExportSQLBuilder.class.getName());
    try
     {
      TableStructureModel structureTable = new TableStructureModel("structure");
      structureTable.addField(new FieldStructureModel("id", 0, "", 0));
      structureTable.addField(new FieldStructureModel("field1", 0, "", 0));

      TableIntegrityModel integrityTable = new TableIntegrityModel("integrity");
      integrityTable.addKey(10);
      integrityTable.addKey(345);
      integrityTable.addKey(8);

      TableTimedModel     timedTable     = new TableTimedModel("timed", Timestamp.valueOf("2008-07-07 12:10:32"));

      logger.debug("\n SQL: \n" + DataExportSQLBuilder.getExportTableSQL(structureTable, integrityTable, timedTable));
      
     }
    catch (DBModelException e) {logger.error(e.getMessage());}

   }

 }