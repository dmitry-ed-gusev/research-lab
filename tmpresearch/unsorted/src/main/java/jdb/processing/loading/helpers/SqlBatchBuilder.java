package jdb.processing.loading.helpers;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import jdb.model.dto.TableDTOModel;
import jdb.processing.data.DataChecker;
import jdb.processing.sql.generation.DataChangeSQLBuilder;
import jdb.utils.DBUtils;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * �����-�������� ������ �������������� ������. ����� ������� ������ ��������� �������� ������� � ���������� sql-batch
 * �� ������ ����� ��������������� ��������� ������. ����� ����� ������������ ���������� sql-�������, � ��������
 * ���������� ������������ ��� ��������/������ "�����������" ��������� ������� (��� ����� �������� ���������� �������).
 * ���������� ����������� ������� ��� �������� ��������� "�����������" �������� ������������ ���������� �� �����
 * ���������� sql-�������.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 22.06.2010)
*/

public class SqlBatchBuilder 
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(SqlBatchBuilder.class.getName());

  /**
   * ����� ������������� ����� ������ ������� �� ���������� ����� fullFilePath. ������ ����� �����: zip-�����, �
   * ������� ������ ���������� ���� ���� � ��������������� �������� TableDTO (��. ����� jlib.db.model.dto), � �������,
   * � ���� �������, ��������� ������ ������� - ��� ��� ���. ���� � ������ ����� ������ ����� - ����� ��������� ������
   * ������ �� ���. ���������� � ���� ������� ������ ���������� ��� ����������� ������������ sql-����� - �����������
   * ������������� ������, ���������� �� ����� �� ��������� ���� - ���� ����� ������ � ������� ��� ����, �� �����
   * ����������� update-������, ���� �� ����� ������ ���, �� ����� ����������� insert-������. �������� deleteSource
   * ��������� - ������� ��� ��� �������� ���� ����� ��������� ������. ��� ������� tableName ���������, ������ �����
   * ������� ���������� � ��������� ����� (������������ ���� ����������� ����� ������(��������������) �������), ����
   * �������� ����, �� ��������� �� SQLException, ���� �� �������� �� ����, �� ����������� ������������ �����
   * ������� �� ����� ���������� ����� �, ���� ��� ����� �� ���������, �� ��������� ����� �� ������������ (����� ����������
   * �������� null). ����� ����� ������������ ���������� sql-��������.
   * @param config DBConfig ������������ ��� ���������� � ����.
   * @param fullFilePath String ������ ���� � ������ � ��������������� ��������.
   * @param deleteSource boolean ������� ��� ��� �������� ���� ����� ������� �������������� �������.
   * @param tableName String ��� �������, ������ ������� ����� � ��������� �����.
   * @param useSqlFilter boolean ������������ ��� ��� ���������� sql-��������.
   * @throws java.io.IOException ������ ��� ���������� ����� ��� ��� ������ � �����.
   * @throws java.sql.SQLException �� ��� ���������� ������� ������� ��.
   * @throws jdb.exceptions.DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
   * @throws jdb.exceptions.DBModelException ������ ������ ���� ������.
   * @return ArrayList[String] sql-batch, ���������� � ���������� �������������� ����� � ������ ��������� ������.
  */
  public static ArrayList<String> getBatchFromTableFile(DBConfig config, String fullFilePath, boolean deleteSource,
   String tableName, boolean useSqlFilter)
   throws IOException, SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {
    logger.debug("WORKING SqlBatchBuilder.getBatchFromTableFile().");
    // �������������� sql-batch
    ArrayList<String> sqlBatch = null;

    // ���� ��� ������� ������ � ��������, ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ���� �� ������ � ������� - ������� ���������� ���������
    else {logger.debug("DBMS connection config is OK. Processing.");}

    // �������� ���������� � ���� � ������� ���������� ������� (��� ���������� � ���� �� ��������� ���������� sql-batch)
    if (!DBUtils.isConnectionValid(config)) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}

    // �������� ����������� ���� � ����� � �������� ������������� �����, � �������� ��� ��� ������ ����
    if (StringUtils.isBlank(fullFilePath)) {throw new IOException("Received path is empty!");}
    else if (!new File(fullFilePath).exists()) {throw new IOException("File [" + fullFilePath + "] doesn't exists!");}
    else if (!new File(fullFilePath).isFile()) {throw new IOException("Path [" + fullFilePath + "] not a file!");}
    else {logger.debug("File [" + fullFilePath + "] is OK. Deserializing (reading) object.");}
    // �������� ���������� ����� ������� (�� ����� �� ���)
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Specifyied table name is empty!");}
    else {logger.debug("Table name is OK.");}

    // ��������� �� ����� ������� TableDTOModel
    TableDTOModel tableDTOModel;
    try
     {
      tableDTOModel = (TableDTOModel) FSUtils.deserializeObject(fullFilePath, deleteSource);
      logger.debug("Object TableDTOModel was deserialized from file [" + fullFilePath + "]. Checking object.");
     }
    // ������������� ������� - ����������� � IOException
    catch (ClassCastException e) {throw new IOException(e.getMessage());}
    // ����� �� ������ - ����� ����������� � IOException
    catch (ClassNotFoundException e) {throw new IOException(e.getMessage());}

    // ���� ���������� ������ ���� - ������
    if ((tableDTOModel == null) || (tableDTOModel.getRows() == null) || (tableDTOModel.getRows().isEmpty()))
     {throw new DBModelException("Deserialized TableDTOModel object is EMPTY!");}

    // ���� ��� ���������� ������� ����� - ������
    String currentTableName = tableDTOModel.getTableName();
    if (StringUtils.isBlank(currentTableName)) {throw new DBModelException("Table name in deserialized object TableDTOModel is EMPTY!");}
    // ��������� ������������ ���������� ����� ������� � ������ ������� �� ����� - ���� ��� �� �������� - �� (��������� ������������).
    else if (!tableName.equalsIgnoreCase(currentTableName))
     {throw new SQLException("Specifyied table name [" + tableName + "] doesn't match table name ["
       + currentTableName + "] from object!");}

    // ���������� ����� - ����������������� ������ � ������� - �������� ��� ���������
    logger.debug("Object TableDTOModel is OK [" + tableDTOModel.getRows().size() + " record(s)]. Processing.");

    // ����� �������� �������������� � �������� ������� �� ����� ���������� ��������� sql-batch. �������� ��������� -
    // ���� � �� ������ � ������ ���������������, ���� ��� �������, �� ��������� UPDATE sql-������, ���� �� �� ������� -
    // INSERT sql-������. ����� ���������� ������ �� ������� ���� ��������� - �� ��������� �� ������� (��� ������
    // ���������� � �������) ��� �����-���� ����������� ������� ������ - ������ "�����������" � "�����������" ������,
    // ��� ������ ���������� � �������.

    // ���� ������� ������� ��������� ��� ����������� - ������!
    if (config.isTableAllowed(currentTableName))
     {
      logger.debug("Table [" + currentTableName + "] is allowed. Processing.");
      
      Connection connection = DBUtils.getDBConn(config);
      // ������� ������������ �������
      // ������� �� ��������� ��� ����������� - ������������ � ����� ������ ������, ���������� �� ������� TableDTOModel
      for (RowDTOModel rowModel : tableDTOModel.getRows())
       {
        // �������� �������� ��������� ���� ��� ������ �������
        int id;
        FieldDTOModel keyFieldModel = rowModel.getFieldByName(DBConsts.FIELD_NAME_KEY);
        // ���� �������� ���� � ������ �� ������� - ������ �� ��������������
        if (keyFieldModel != null)
         {
          // �������� �������� ��������� ����. ���� ������� �������� �������� - ���� ������ � ����� ���������������
          // � �������, ���� ����� - ��������� ���������� ������ ������ (������ UPDATE), ���� �� �� ����� -
          // ��������� ���������� ������ ������ (������ INSERT)
          try {id = Integer.parseInt(keyFieldModel.getFieldValue());}
          catch (NumberFormatException e)
           {logger.error("Can't parse KEY value! [" + e.getMessage() + "]. Record was skipped!"); id = 0;}

          // ���� ������� �������� �������� ��������� ���� - ���� ������ � ����� �� ��������������� � �������
          if (id > 0)
           {
            String dataUpdateSql;
            // �������� ������������� ������ � ��������� ��������� ��������� ����. ���� ������ ������� - ����������
            // UPDATE-������, ���� �� ��� - ���������� INSERT-������
            if (DataChecker.isRecordExists(connection, currentTableName, DBConsts.FIELD_NAME_KEY, id))
             {dataUpdateSql = DataChangeSQLBuilder.getDataUpdateSql(currentTableName, rowModel, id, useSqlFilter);}
            // ���� ������ �� ������� - ��������� ������� INSERT
            else
             {dataUpdateSql = DataChangeSQLBuilder.getDataInsertSql(currentTableName, rowModel, useSqlFilter);}

            // ���� ��������������� ������ �� ���� - ��������� ��� � ����������� ������ (�����). ���� ��������������
            // ���� ��� �� ��������������� - �������������.
            if (!StringUtils.isBlank(dataUpdateSql))
             {
              if (sqlBatch == null) {sqlBatch = new ArrayList<String>();}
              sqlBatch.add(dataUpdateSql);
             }
            // ���� �� ������ ���� - ������� � ���
            else {logger.warn("Generated data update/insert query is empty!");}
           }
          // �������� ���� � ������ ������ �� ����� �������� (�������� �������� �� �������)
          else {logger.error("Can't receive KEY field [" + DBConsts.FIELD_NAME_KEY + "] value.");}

        }
       // �������� ���� �� ������� � �������������� ������
       else {logger.warn("KEY field in rowModel was not found!");}
      } // END OF FOR CYCLE

      // ������� �������� �����������
      if (connection != null) {connection.close();}
     }
    // ���� �� ��������� ������� �� ��������� - ������� �� ������
    else {logger.warn("Current table [" + currentTableName + "] is deprecated or not allowed! Skipping.");}
    // ���������� �������������� sql-batch
    return sqlBatch;
   }

 }