package jdb.processing.loading.core;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.dto.FieldDTOModel;
import jdb.model.dto.RowDTOModel;
import jdb.model.dto.TableDTOModel;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.time.DBTimedModel;
import jdb.model.time.TableTimedModel;
import jdb.processing.loading.helpers.DataExportSQLBuilder;
import jdb.processing.modeling.DBModeler;
import jdb.utils.DBUtils;
import jlib.exceptions.EmptyObjectException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import jlib.utils.string.StrUtilities;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * ������ ����� ��������� ������������ ������ ����� ��. ������������ ������������ � ������� DTO-������
 * �� - ��. ������ � ������ model.
 *
 * <br> �������� ������������ ���������:
 * <ul>
 *  <li> ����������� ������������ ������ - ������������ ��� ���������� � ����, ���� ��� ���������� ������. ���� ��������
 *       ����������� �������� - ����� �� ��������� ������� ��������. ���� ������� ���������� (���� ����� �����������
 *       ������) �� ���������� - �� ���������, ���� ������� �� ������� - ��������� ����������� ������ (��� �������������
 *       ����������� ������ ������ ������ �����������). ���� ������� ���������� ���������� - �� ��������� �� ���� ������.
 *       ������ ������� �������� - �����������.
 *  <li> ������������ ������ ��������� ������������� ��, � ������ �������� ������ "�����������" (���� � ������) �������.
 *       ���� ���������� ������ ����� - ��������� ����������� ������.
 *  <li> � ����� �������� �� ������ ������ ������������� ��, ����������� ������ "�����������" �������: �������� ������ ���
 *       �������, ���������� sql-������ ��� ������� ���� ������ ������������� �������, �������� ��������������� ���� ������
 *       (���� ������ ��� - ������� �����, ������� ������������ ��� ��������� �������), � ����� �������� �� ������� ������ �
 *       ��������� ������ ������� � ������� (��� ������������ ������ ������������ ������ ������ model), ��� ����������
 *       ������������� ���������� ������������ ����� (������� ������������, ��. ��������� � ������ DBConsts) ������
 *       ����������� � ���� �� �����. ��������� ������������. ������ ������ ����� �� ���� - ����������� (������ ���������������).
 *  <li> ����� � ������������ ������� ��������� ����������� ������� - [���������� 0] + [����� �����].zip ������������
 *       ���������� ����������� (�����������) ������ - ������������� ����� (999999999), �� ���������� �������� ����������
 *       ������� ������ ������. ���������� 0 ����������� ��� ����������� ���������� ������. ������� � ������� ����� �������
 *       ������ (� ��������������� ���� - � ���� ������), ������� ��� ��� ����������� ������ � ������� �����������, ��� ������
 *       ����������� �������� �������� ZIP.
 * </ul>
 * <br> ��� ������� �������� ������������� ������ (����� ������� � ��������, ������� ����� ������, ����� ������ � ����� ��
 * � �.�.) ����� ����� ������������ � ������, ������� �� ��������� ������ ���� OutOfMemory ��� �������� ������� ������
 * ������������� �������� ������� �������� ��� ������������ JAVA-������� ������ (������������� �������� ����� 256Mb).
 * ������������ ������ ������ �������� ������ ��������� ������ -Xmx[����� ��������]M (��������� - ��. ������������).
 * 
 * @author Gusev Dmitry (019gus)
 * @version 9.2 (DATE: 21.03.2011)
*/

public class DBUnloadCore
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DBUnloadCore.class.getName());
  /**
   * ����������� ����� ����� ����� � ������������ �� �� �������. � ���� ����� ���������� ��� ����� ����������� ������.
   * �� ���������� ������� ������� ������� �������� - ��� �������� �� �������������� �������.
  */
  private static final int FILE_NAME_LENGTH       = 10;
  /**
   * ��������� ���� �������� ����������� ��� ����� �� ����������� ����� (����������� � ������ �����). ���� ������ - ����.
   * ������ ������� (�������� �����) ������� �� ���� - ��� ������� ������� ���������� ������ � �������� ������� � �������
   * ������� �� (������) ��������� �������� �������� ������ �� ������ � ��.
  */
  private static final char FILE_NAME_FILL_SYMBOL = '0';

  /**
   * ����� �� ��������� ������� ��������� ������� �� ������� �� (��) ������ �� ����� �� ����� ������� (����������� ��).
   * ������������ ������������ � �������, ��������� � ���������� pathToDB. � ���� �������� ��������� ���������� � ������
   * ������������� ��, � ������� ������������� �������� � �������, ������������ � ������� ������ ��. ��������������� ������
   * ��������� � ��������� � ������� ������ - ������ ������ ������� � ��������������� ��������. ������ �������������
   * ������� - �� DBSerializerConsts.TABLE_SERIALIZE_FRACTION ������� (�� ������ ������ = 200), ���� �� �������������
   * ������� ������ - ��� ��� �������� � ���� ����. ����� ���������� ���� ������������ (� ������ zip) � ���������. ���������
   * ����� ������������� ���, ����������� � ������ ��������� �����. ��� ��������� ����� - ����� ������ ������ ��
   * DBSerializerConsts.TABLE_SERIALIZE_FRACTION �������. ���������� ��������� ����� -
   * DBSerializerConsts.SERIALIZED_DATA_FILE_EXTENSION (�� ������ ������ - .bin). ���������� ��������� ����� -
   * DBSerializerConsts.ARCHIVED_DATA_FILE_EXTENSION (�� ������ ������  - .zip). ������ �� ������������� �������
   * ���������� � ������� TableDTO (�������). ��� ������� � ���� ������� ������� �� �������� TableRowDTO (������), � ���
   * ������� ������� �� �������� TableFieldDTO (����).<br>
   * ���� ��� ������� ������������� ������� ������������ � ������ "�����������" ������ - ����� ������� ��������� ��
   * �����.<br>
   * ����� � ��������� ������������ ������ ��������� (��� ���������) ��� ������ ��: <br>
   * - DBTimeModel - ������ �� � ��������� ������� ���������� ������ ������� (����� ����������
   * ������� - ������������ �������� ���� timestamp ��� ���� ������� ������ �������). ���� ��� ������ ����� (=null), ��
   * ������� �� ����� ������������� ���������. ���� ��� �����-���� ������� �� ������ ������ �� ������� ��������
   * timestamp - ����� ������� ����� ������������� ���������.<br>
   * - DBIntegrityModel - ������ ����������� ��. ���� ��� ������ ����� (=null), �� ������� �� ����� ������������� ���������.
   * ���� �� �����-�� ������� ����� ��������� � ���� ������, �� ��� ��� ����� ��������� ������ �� ������ �� ������.<br>
   * ������������ ���������� ������, � ������� ����� ���� ��������� ���� ������� �� - ������������ �������������
   * ����� (999999999).
   * @param config SerializationConfig ������������ ��� ������ ������ �������� (������������) ������ �� ��.
   * @return boolean ��������� ������������ �� - ���� ������, �� ����� ������ �������������, ���� ���� -
   * ������ �� �������������. ����� ������ (�� ��������� ��� ��) ����� ���� ������������� ���� ��� ������������
   * ������������ �������� DatabaseTimeModel - ������ �� � ��������� �������.
   * @throws SQLException �� ��� ���������� ������� ������� ��.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ������ ���� ������.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public static boolean unload(DBLoaderConfig config) throws SQLException, DBConnectionException,
   DBModuleConfigException, IOException, DBModelException
   {
    // ��������� ������������ �� - ���� ������, �� ����� ������ �������������, ���� ���� - ������ �� �������������.
    boolean isDataSerialized = false;
    logger.debug("WORKING DBSerializer.unload().");

    // ���� ������������ ������ �������� - ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // ������������ ���� � �������� (�� ������ ������������ �������� /).
    String localPathToDB = FSUtils.fixFPath(config.getPath(), true);
    // ���� ������ �������� ��� - ������� ���. ���� �������� �������� �� ������� - ���������� �� IOException
    if (!new File(localPathToDB).exists())
     {if (!new File(localPathToDB).mkdirs())
       {throw new IOException("Can't create loading catalog [" + localPathToDB + "]!");}}
    // ���� ������� ���������� - ������� ��� ����� ��������� (�������) � ���� ������
    else {FSUtils.clearDir(localPathToDB);}
    
    // ��������� ������ ��������� ������� �� (��������� ����� � ������������ � �������������)
    DBModeler modeler = new DBModeler(config.getDbConfig());
    DBStructureModel currentDbModel = modeler.getDBStructureModel();
    // ���� ���������� ������ ����� (=null), ������������ ��
    if (currentDbModel == null) {throw new DBModelException("Database model is empty!");}

    // �������� �������� ������� ������������ (���������� �����/������� ����� ������� ��� ������ � ����
    // ���������������� ����), ���� ������ �� ������ (�� <= 0), �� ������������ �������� �� ��������� - ��.
    // ��������� SERIALIZE_TABLE_FRACTION � ������ DBConsts.
    int serializeFraction;
    if (config.getSerializeFraction() > 0) {serializeFraction = config.getSerializeFraction();}
    else                                   {serializeFraction = DBConsts.SERIALIZATION_TABLE_FRACTION;}
    logger.debug("Calculated value for table serialize fraction:" + serializeFraction);

    // ���������� ��� ���������� � ������������� ����
    Connection    connection = DBUtils.getDBConn(config.getDbConfig());
    Statement     stmt       = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    stmt.setFetchSize(5000);
    
    // ������ �� ������ ����������� � ��������� ������ ������� ��
    DBIntegrityModel integrityDB = config.getDbIntegrityModel();
    DBTimedModel     timedDB     = config.getDbTimedModel();

    // �������� �� ���� �������� ������ �� � ����� �� ��� ������. ���� ��� ������� ������� ��������� ���
    // ����������� - �� ������������ ��. ����� �� �������������� ������ (null) �������.
    for (TableStructureModel currentTable : currentDbModel.getTables())
     {
      // ��������� ��� ������� �������������� �������. ���� � ������� ������� ����� - ���������� ��.
      String currentTableName = currentTable.getTableName();

      // ���� ������� ���������� ������� �� ����� - ��������
      if ((currentTable != null) && (!StringUtils.isBlank(currentTableName)))
       {
        // ������ ��� ������� �������������� �������, � ��������� ����� (���� ��� ����).
        String currentTableFullName;
        if (!StringUtils.isBlank(currentTable.getTableSchema()))
         {currentTableFullName = currentTable.getTableSchema() + "." + currentTableName;}
        else
         {currentTableFullName = currentTableName;}

        // �� �� ����������� ������ ������� (����� ��� TABLE), ������ ������� �� ����������� (��������� �������,
        // ������ (�������������) � �.�.)
        if (DBConsts.TableType.TABLE.strValue().equals(currentTable.getTableType()))
         {
          logger.debug("Processing table [" + currentTableFullName + "]. Table type [" + currentTable.getTableType() + "].");
          // ���� ������� �� ��������� ��� �����������, �� ������������ ��. �������� ������� �� �����������
          // ������������ �� ����� ������� ��� �������� ����� �����. ����� ��� �������, �����! :)
          if (config.isTableAllowed(currentTableName))
           {
            logger.debug("Current table [" + currentTableFullName + "] is ALLOWED for processing!");
            // ��������� ������� ��������� ���� ��� �������������� �������. ���� ��������� ���� ��� - ����� �������
            // ������ ����� ��������� ������� � �� �������� �� ����� ������.
            if (currentTable.getField(config.getKeyFieldName()) != null)
             {
              logger.debug("Key field [" + config.getKeyFieldName() + "] found in table [" + currentTableFullName + "]. Processing.");
              // ������� ��� ���������� ���������� �������. ������� (��� ��������) ��� �������� ������� ����� (��� �
              // �������� ������� �� "�������������") ������� �� �������� ����� �������, ��� �������� ����� ������.
              String pathToTable = localPathToDB + currentTableName + "/";

              // ������ ����������� ��� ������ ������� (�������� �� ����� �������, ��� �������� �����)
              TableIntegrityModel integrityTable = null;
              if (!DBUtils.isDBModelEmpty(integrityDB)) {integrityTable = integrityDB.getTable(currentTableName);}
              // ������ � ��������� ������� ��� ������ ������� (�������� �� ����� �������, ��� �������� �����)
              TableTimedModel timedTable = null;
              if (!DBUtils.isDBModelEmpty(timedDB)) {timedTable = timedDB.getTable(currentTableName);}
          
              // �������� �� ������-��������� ��������������� sql-������ ��� �������� ������� ������� �� ����
              String sql = DataExportSQLBuilder.getExportTableSQL(currentTable, integrityTable, timedTable);
              // ���� �� ������-��������� �� �������� �� ������ ������ - ��������
              if (!StringUtils.isBlank(sql))
               {
                logger.debug("Export SQL is OK! Processing.");
                try
                 {
                  // ��������������� ��������� ������ � ���������� �� �������
                  ResultSet         rs     = stmt.executeQuery(sql);
                  ResultSetMetaData rsmeta = rs.getMetaData();
                  logger.debug("DATA and METADATA was received. Processing.");

                  // ������ �� ����� ����������� ������� ������
                  int counter     = 1; // <- ������� ���������� ������������ ����� �� ������� �������
                  int packCounter = 1; // <- ������� ���������� ��������� ������ � ���������������� ������� �� ������ �������
                  // ���� ���� ������ - ������������ ��
                  if (rs.next())
                   {
                    // ������� ����� ������ ������� ��� ������������ ������. ��������� ����� � ��� �������.
                    TableDTOModel tableDTOModel = new TableDTOModel(currentTableName);
                    tableDTOModel.setTableSchema(currentTable.getTableSchema());
                    tableDTOModel.setTableType(currentTable.getTableType());
                    // ���� ������� ������ - ������� ������� ��� ������������� �������. ���� ������� ��� ������� �������������
                    // ������� ������� �� ������� - ������������ �� � ��������� ������� ������� ������������
                    if (!new File(pathToTable).exists())
                     {
                      logger.debug("Creating catalog [" + pathToTable + "] for current table [" + currentTableName + "].");
                      if (!new File(pathToTable).mkdirs()) {throw new IOException("Can't create catalog [" + pathToTable + "]!");}
                      else {logger.debug("Catalog for current table created successfully.");}
                     }

                    // ���� ������� ������ ��� ������������ - ���� ���������� ����, ������� ����� ��������� ������ ������� -
                    // ��������� ������ ���� ������������� (�������� �� ����)
                    if (!isDataSerialized) {isDataSerialized = true;}

                    // ���������� ����� - �������� � ������ � ������ ��������� (��������) ������ �������
                    logger.debug("Starting export table [" + currentTableFullName + "]. Creating data files.");
                    // ��������������� ��������� ����������� ������� � ������� ������� ������� (������������ ��� ������
                    // ������� � ��������� �����, ��������� �� ��������-�����)
                    do
                     {
                      // �������� ����������� ����� ������ �������
                      RowDTOModel rowModel = new RowDTOModel();
                      // ���� ������� �� ����� ����� ������ �� ������� ������
                      for (int i = 1; i <= currentTable.getFields().size(); i++)
                       {
                        // ��� �������� ����
                        String fName = rsmeta.getColumnName(i);
                        // ���� ��� ���� �� ����� - �������� ��� ���� � ������� ��� (����) � ������ ������
                        if (!StringUtils.isBlank(fName))
                         {
                          // �������� ��� �������� ���� �� ������ ������
                          //int fType = currentDbModel.getTable(currentTableName).getField(fName).getJavaDataType();
                          int fType = currentTable.getField(fName).getJavaDataType();
                          // ��������� ���� � ������ ������ ������� (� ������, ���������, �����)
                          rowModel.addField(new FieldDTOModel(fName, rs.getString(i), fType));
                         }
                        // ���� �� ��� ���� ����� - � ������ ������ ���� �� �������!
                        else {logger.error("Empty field name! Check the source table!");}
                       }
                      // ���������� �������������� ������ ������ ������� � ������ �������
                      tableDTOModel.addRow(rowModel);

                      // ���������� ����� (�������� � ��� � ���� ��������� ������)
                      if ((config.getOperationsCount() > 0) && (counter%config.getOperationsCount() == 0))
                       {logger.debug("# " + counter + " rows of table [" + currentTable.getTableName() + "] processed.");}

                      // ������������ ������ �� N ������� (������ ����� ������ ������� � ���� �� �����). �������������
                      // ������ ������ �� ���������� �������, �������� �������� ������� ������������ (����������� � �������),
                      // ���� �� ���������� ������� � ������� (������������� �������) �� ������ �������� ������� ������������,
                      // �� ���������� ������ ������������� ����� ��������� ����� (��� ���������� ��� ����, ����� ��������
                      // ������������� ������������� ��������� ResultSet.isLast()).
                      if ((counter%serializeFraction == 0) /** || ((counter%serializeFraction != 0) && (rs.isLast()))*/ )
                       {
                        // �������� ��� ��� ���������� ������������� �����. ��� �������� �� ������������ �������� (��.
                        // ������������/����������� � ������ getNameForFile())
                        String fileName = StrUtilities.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, packCounter);
                        // ����� ������ FSUtils ��� ������������ �������
                        try
                         {
                          FSUtils.serializeObject(tableDTOModel, pathToTable, fileName);
                          logger.debug("### created file [" + fileName + "]");
                         }
                        catch (EmptyObjectException e) {logger.error("Can't serialize object! Reason: [" + e.getMessage() + "]");}
                        // �������� ������ ������� TableDTOModel (��������� ������ �� ������� ��������� ������). �����������
                        // ��������� ����� � ��� �������.
                        tableDTOModel = new TableDTOModel(currentTableName);
                        tableDTOModel.setTableSchema(currentTable.getTableSchema());
                        tableDTOModel.setTableType(currentTable.getTableType());
                        // ���������� �������� ������ � �������
                        packCounter++;
                       }
                      // ���������� �������� ���������� ������������ �����
                      counter++;
                     }
                    while (rs.next()); // ����� ����� ��������� ������ ������� �������������� �������

                    // ����������� ������� ������� �� �������, ���������� ������� (�������) �� ������
                    // �������� ������� ������������.
                    if ((counter - 1)%serializeFraction != 0)
                     {
                      logger.debug("Serializing records remainder [" + ((counter - 1)%serializeFraction) + " records].");
                      // �������� ��� ��� ���������� ������������� �����. ��� �������� �� ������������ �������� (��.
                      // ������������/����������� � ������ getNameForFile())
                      String fileName = StrUtilities.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, packCounter);
                      // ����� ������ FSUtils ��� ������������ �������
                      try
                       {
                        FSUtils.serializeObject(tableDTOModel, pathToTable, fileName);
                        logger.debug("### created file [" + fileName + "]");
                       }
                      catch (EmptyObjectException e) {logger.error("Can't serialize object! Reason: [" + e.getMessage() + "]");}
                     }
                    // ���������� ����� - ����� ���������� ����� � �������
                    logger.debug("Table [" + currentTableFullName + "]. Total processed records [" + (counter - 1) + "].");
                    // ��������� �� ����� ������
                    rs.close();
                   }
                  // ���� �� ������ ��� - �������� �� ���� � ���
                  else {logger.error("Data for current table [" + currentTableFullName + "] is empty!");}
                 }
                // �������� ������������� ��. ��� ������������� ������������� �� ������ ������ ������������.
                catch (IOException e)
                 {logger.error("I/O error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");}
                catch (SQLException e)
                 {logger.error("SQL error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");}
               }
              // ���� �� ������� ������ ������ - ������! ������ �� ������!
              else {logger.error("Export SQL for current table [" + currentTableFullName + "] is empty!");}
             }
            // ���� � ������� ��� ��������� ���� - �������� � ��� � ������� �� ���������
            else {logger.warn("Key field [" + config.getKeyFieldName() + "] not found in table [" + currentTableFullName + "]! Can't process!");}
           }
          // ���� �� ������� ������� ��� ����������� - ������� �� ���� ��������� � �� ������������ ��
          else {logger.warn("Current table [" + currentTableFullName + "] is not allowed! Skipping.");}
         }
        // ���� �� ������� ����� ��� �������� �� TABLE - ��� �� ����������� �� ����
        else {logger.warn("Can't unload to disk table [" + currentTableFullName + "] with type [" + currentTable.getTableType() + "]!");}
       }
      // ���� �� ������� ����� - ������� ������ � ���
      else {logger.error("Current table model is empty! Can't process table!");}

     } // [END OF FOR] ��������� ����� ��������� ���� ������ �� ������ ������ ������� (��������������) ��

    // ���� ������ �� ���� �������������, �� ���� ������� ��������� ������� ��� ��
    if (!isDataSerialized)
     {
      if (!new File(localPathToDB).delete()) {logger.warn("Can't delete created loading catalog [" + localPathToDB + "]!");}
      else {logger.debug("Catalog [" + localPathToDB + "] was deleted successfully.");}
     }
    // ����������� ���������� ������ ������
    return isDataSerialized;
   }

  public static boolean unload2(DBLoaderConfig config) throws SQLException, DBConnectionException,
   DBModuleConfigException, IOException, DBModelException
   {
    // ��������� ������������ �� - ���� ������, �� ����� ������ �������������, ���� ���� - ������ �� �������������.
    boolean isDataSerialized = false;
    logger.debug("WORKING DBSerializer.unload().");

      // ���� ������������ ������ �������� - ���������� ��!
      String configErrors = DBUtils.getConfigErrors(config);
      if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

      // ������������ ���� � �������� (�� ������ ������������ �������� /).
      String localPathToDB = FSUtils.fixFPath(config.getPath(), true);
      // ���� ������ �������� ��� - ������� ���. ���� �������� �������� �� ������� - ���������� �� IOException
      if (!new File(localPathToDB).exists())
       {if (!new File(localPathToDB).mkdirs())
         {throw new IOException("Can't create loading catalog [" + localPathToDB + "]!");}}
      // ���� ������� ���������� - ������� ��� ����� ��������� (�������) � ���� ������
      else {FSUtils.clearDir(localPathToDB);}

      // ��������� ������ ��������� ������� �� (��������� ����� � ������������ � �������������)
      DBModeler modeler = new DBModeler(config.getDbConfig());
      DBStructureModel currentDbModel = modeler.getDBStructureModel();
      // ���� ���������� ������ ����� (=null), ������������ ��
      if (currentDbModel == null) {throw new DBModelException("Database model is empty!");}

      // �������� �������� ������� ������������ (���������� �����/������� ����� ������� ��� ������ � ����
      // ���������������� ����), ���� ������ �� ������ (�� <= 0), �� ������������ �������� �� ��������� - ��.
      // ��������� SERIALIZE_TABLE_FRACTION � ������ DBConsts.
      int serializeFraction;
      if (config.getSerializeFraction() > 0) {serializeFraction = config.getSerializeFraction();}
      else                                   {serializeFraction = DBConsts.SERIALIZATION_TABLE_FRACTION;}
      logger.debug("Calculated value for table serialize fraction:" + serializeFraction);

      // ���������� ��� ���������� � ������������� ����
      Connection    connection = DBUtils.getDBConn(config.getDbConfig());
      Statement     stmt       = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      stmt.setFetchSize(5000);

      // ������ �� ������ ����������� � ��������� ������ ������� ��
      DBIntegrityModel integrityDB = config.getDbIntegrityModel();
      DBTimedModel     timedDB     = config.getDbTimedModel();

      // �������� �� ���� �������� ������ �� � ����� �� ��� ������. ���� ��� ������� ������� ��������� ���
      // ����������� - �� ������������ ��. ����� �� �������������� ������ (null) �������.
      for (TableStructureModel currentTable : currentDbModel.getTables())
       {
        // ��������� ��� ������� �������������� �������. ���� � ������� ������� ����� - ���������� ��.
        String currentTableName = currentTable.getTableName();

        // ���� ������� ���������� ������� �� ����� - ��������
        if ((currentTable != null) && (!StringUtils.isBlank(currentTableName)))
         {
          // ������ ��� ������� �������������� �������, � ��������� ����� (���� ��� ����).
          String currentTableFullName;
          if (!StringUtils.isBlank(currentTable.getTableSchema()))
           {currentTableFullName = currentTable.getTableSchema() + "." + currentTableName;}
          else
           {currentTableFullName = currentTableName;}

          // �� �� ����������� ������ ������� (����� ��� TABLE), ������ ������� �� ����������� (��������� �������,
          // ������ (�������������) � �.�.)
          if (DBConsts.TableType.TABLE.strValue().equals(currentTable.getTableType()))
           {
            logger.debug("Processing table [" + currentTableFullName + "]. Table type [" + currentTable.getTableType() + "].");
            // ���� ������� �� ��������� ��� �����������, �� ������������ ��. �������� ������� �� �����������
            // ������������ �� ����� ������� ��� �������� ����� �����. ����� ��� �������, �����! :)
            if (config.isTableAllowed(currentTableName))
             {
              logger.debug("Current table [" + currentTableFullName + "] is ALLOWED for processing!");
              // ��������� ������� ��������� ���� ��� �������������� �������. ���� ��������� ���� ��� - ����� �������
              // ������ ����� ��������� ������� � �� �������� �� ����� ������.
              if (currentTable.getField(config.getKeyFieldName()) != null)
               {
                logger.debug("Key field [" + config.getKeyFieldName() + "] found in table [" + currentTableFullName + "]. Processing.");
                // ������� ��� ���������� ���������� �������. ������� (��� ��������) ��� �������� ������� ����� (��� �
                // �������� ������� �� "�������������") ������� �� �������� ����� �������, ��� �������� ����� ������.
                String pathToTable = localPathToDB + currentTableName + "/";

                // ������ ����������� ��� ������ ������� (�������� �� ����� �������, ��� �������� �����)
                TableIntegrityModel integrityTable = null;
                if (!DBUtils.isDBModelEmpty(integrityDB)) {integrityTable = integrityDB.getTable(currentTableName);}
                // ������ � ��������� ������� ��� ������ ������� (�������� �� ����� �������, ��� �������� �����)
                TableTimedModel timedTable = null;
                if (!DBUtils.isDBModelEmpty(timedDB)) {timedTable = timedDB.getTable(currentTableName);}

                // �������� �� ������-��������� ��������������� sql-������ ��� �������� ������� ������� �� ����
                String sql = DataExportSQLBuilder.getExportTableSQL(currentTable, integrityTable, timedTable);
                // ���� �� ������-��������� �� �������� �� ������ ������ - ��������
                if (!StringUtils.isBlank(sql))
                 {
                  logger.debug("Export SQL is OK! Processing.");
                  try
                   {
                    // ��������������� ��������� ������ � ���������� �� �������
                    ResultSet         rs     = stmt.executeQuery(sql);
                    ResultSetMetaData rsmeta = rs.getMetaData();
                    logger.debug("DATA and METADATA was received. Processing.");

                    // ������ �� ����� ����������� ������� ������
                    int counter     = 1; // <- ������� ���������� ������������ ����� �� ������� �������
                    int packCounter = 1; // <- ������� ���������� ��������� ������ � ���������������� ������� �� ������ �������
                    // ���� ���� ������ - ������������ ��
                    if (rs.next())
                     {
                      // ������� ����� ������ ������� ��� ������������ ������. ��������� ����� � ��� �������.
                      TableDTOModel tableDTOModel = new TableDTOModel(currentTableName);
                      tableDTOModel.setTableSchema(currentTable.getTableSchema());
                      tableDTOModel.setTableType(currentTable.getTableType());
                      // ���� ������� ������ - ������� ������� ��� ������������� �������. ���� ������� ��� ������� �������������
                      // ������� ������� �� ������� - ������������ �� � ��������� ������� ������� ������������
                      if (!new File(pathToTable).exists())
                       {
                        logger.debug("Creating catalog [" + pathToTable + "] for current table [" + currentTableName + "].");
                        if (!new File(pathToTable).mkdirs()) {throw new IOException("Can't create catalog [" + pathToTable + "]!");}
                        else {logger.debug("Catalog for current table created successfully.");}
                       }

                      // ���� ������� ������ ��� ������������ - ���� ���������� ����, ������� ����� ��������� ������ ������� -
                      // ��������� ������ ���� ������������� (�������� �� ����)
                      if (!isDataSerialized) {isDataSerialized = true;}

                      // ���������� ����� - �������� � ������ � ������ ��������� (��������) ������ �������
                      logger.debug("Starting export table [" + currentTableFullName + "]. Creating data files.");
                      // ��������������� ��������� ����������� ������� � ������� ������� ������� (������������ ��� ������
                      // ������� � ��������� �����, ��������� �� ��������-�����)
                      do
                       {
                        // �������� ����������� ����� ������ �������
                        RowDTOModel rowModel = new RowDTOModel();
                        // ���� ������� �� ����� ����� ������ �� ������� ������
                        for (int i = 1; i <= currentTable.getFields().size(); i++)
                         {
                          // ��� �������� ����
                          String fName = rsmeta.getColumnName(i);
                          // ���� ��� ���� �� ����� - �������� ��� ���� � ������� ��� (����) � ������ ������
                          if (!StringUtils.isBlank(fName))
                           {
                            // �������� ��� �������� ���� �� ������ ������
                            //int fType = currentDbModel.getTable(currentTableName).getField(fName).getJavaDataType();
                            int fType = currentTable.getField(fName).getJavaDataType();
                            // ��������� ���� � ������ ������ ������� (� ������, ���������, �����)
                            rowModel.addField(new FieldDTOModel(fName, rs.getString(i), fType));
                           }
                          // ���� �� ��� ���� ����� - � ������ ������ ���� �� �������!
                          else {logger.error("Empty field name! Check the source table!");}
                         }
                        // ���������� �������������� ������ ������ ������� � ������ �������
                        tableDTOModel.addRow(rowModel);

                        // ���������� ����� (�������� � ��� � ���� ��������� ������)
                        if ((config.getOperationsCount() > 0) && (counter%config.getOperationsCount() == 0))
                         {logger.debug("# " + counter + " rows of table [" + currentTable.getTableName() + "] processed.");}

                        // ������������ ������ �� N ������� (������ ����� ������ ������� � ���� �� �����). �������������
                        // ������ ������ �� ���������� �������, �������� �������� ������� ������������ (����������� � �������),
                        // ���� �� ���������� ������� � ������� (������������� �������) �� ������ �������� ������� ������������,
                        // �� ���������� ������ ������������� ����� ��������� ����� (��� ���������� ��� ����, ����� ��������
                        // ������������� ������������� ��������� ResultSet.isLast()).
                        if ((counter%serializeFraction == 0) /** || ((counter%serializeFraction != 0) && (rs.isLast()))*/ )
                         {
                          // �������� ��� ��� ���������� ������������� �����. ��� �������� �� ������������ �������� (��.
                          // ������������/����������� � ������ getNameForFile())
                          String fileName = StrUtilities.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, packCounter);
                          // ����� ������ FSUtils ��� ������������ �������
                          try
                           {
                            FSUtils.serializeObject(tableDTOModel, pathToTable, fileName);
                            logger.debug("### created file [" + fileName + "]");
                           }
                          catch (EmptyObjectException e) {logger.error("Can't serialize object! Reason: [" + e.getMessage() + "]");}
                          // �������� ������ ������� TableDTOModel (��������� ������ �� ������� ��������� ������). �����������
                          // ��������� ����� � ��� �������.
                          tableDTOModel = new TableDTOModel(currentTableName);
                          tableDTOModel.setTableSchema(currentTable.getTableSchema());
                          tableDTOModel.setTableType(currentTable.getTableType());
                          // ���������� �������� ������ � �������
                          packCounter++;
                         }
                        // ���������� �������� ���������� ������������ �����
                        counter++;
                       }
                      while (rs.next()); // ����� ����� ��������� ������ ������� �������������� �������

                      // ����������� ������� ������� �� �������, ���������� ������� (�������) �� ������
                      // �������� ������� ������������.
                      if ((counter - 1)%serializeFraction != 0)
                       {
                        logger.debug("Serializing records remainder [" + ((counter - 1)%serializeFraction) + " records].");
                        // �������� ��� ��� ���������� ������������� �����. ��� �������� �� ������������ �������� (��.
                        // ������������/����������� � ������ getNameForFile())
                        String fileName = StrUtilities.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, packCounter);
                        // ����� ������ FSUtils ��� ������������ �������
                        try
                         {
                          FSUtils.serializeObject(tableDTOModel, pathToTable, fileName);
                          logger.debug("### created file [" + fileName + "]");
                         }
                        catch (EmptyObjectException e) {logger.error("Can't serialize object! Reason: [" + e.getMessage() + "]");}
                       }
                      // ���������� ����� - ����� ���������� ����� � �������
                      logger.debug("Table [" + currentTableFullName + "]. Total processed records [" + (counter - 1) + "].");
                      // ��������� �� ����� ������
                      rs.close();
                     }
                    // ���� �� ������ ��� - �������� �� ���� � ���
                    else {logger.error("Data for current table [" + currentTableFullName + "] is empty!");}
                   }
                  // �������� ������������� ��. ��� ������������� ������������� �� ������ ������ ������������.
                  catch (IOException e)
                   {logger.error("I/O error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");}
                  catch (SQLException e)
                   {logger.error("SQL error! Message [" + e.getMessage() + "]. Table [" + currentTableFullName + "].");}
                 }
                // ���� �� ������� ������ ������ - ������! ������ �� ������!
                else {logger.error("Export SQL for current table [" + currentTableFullName + "] is empty!");}
               }
              // ���� � ������� ��� ��������� ���� - �������� � ��� � ������� �� ���������
              else {logger.warn("Key field [" + config.getKeyFieldName() + "] not found in table [" + currentTableFullName + "]! Can't process!");}
             }
            // ���� �� ������� ������� ��� ����������� - ������� �� ���� ��������� � �� ������������ ��
            else {logger.warn("Current table [" + currentTableFullName + "] is not allowed! Skipping.");}
           }
          // ���� �� ������� ����� ��� �������� �� TABLE - ��� �� ����������� �� ����
          else {logger.warn("Can't unload to disk table [" + currentTableFullName + "] with type [" + currentTable.getTableType() + "]!");}
         }
        // ���� �� ������� ����� - ������� ������ � ���
        else {logger.error("Current table model is empty! Can't process table!");}

       } // [END OF FOR] ��������� ����� ��������� ���� ������ �� ������ ������ ������� (��������������) ��

      // ���� ������ �� ���� �������������, �� ���� ������� ��������� ������� ��� ��
      if (!isDataSerialized)
       {
        if (!new File(localPathToDB).delete()) {logger.warn("Can't delete created loading catalog [" + localPathToDB + "]!");}
        else {logger.debug("Catalog [" + localPathToDB + "] was deleted successfully.");}
       }
      // ����������� ���������� ������ ������
      return isDataSerialized;
     }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    // ������������� ������� ������ �������� ������ ��� ���������� jlib
    InitLogger.initLoggers(new String[] {"jdb", "jlib", "org"});
    // ������ �������� ������
    Logger logger = Logger.getLogger(DBUnloadCore.class.getName());
    try
     {
      // �������� �� ����� �� ����
      DBConfig config = new DBConfig("jdb_java_module/dbConfigs/ifxNormDocsConfig.xml");
      DBLoaderConfig loader = new DBLoaderConfig();
      loader.setDbConfig(config);
      loader.setPath("c:\\temp\\norm_docs");
      DBUnloadCore.unload(loader);
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (IOException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}

   }

 }