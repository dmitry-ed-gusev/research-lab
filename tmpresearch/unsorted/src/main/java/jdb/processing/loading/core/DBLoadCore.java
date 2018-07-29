package jdb.processing.loading.core;

import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.modeling.DBModeler;
import jdb.utils.DBUtils;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ������ �������������� ��. �������������� ����������� ��� � ������������, ��� � � ������������� �������. ���
 * ���������������� ������� ������ ������������ ����� ������������ - SerializationConfig. ��������������� ������
 * ������� ������� ������ ����������� � �������������� ��������� ������� sql-��������, ������� ������������ ���
 * ������� ������ � ������� ��.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 20.05.2010)
*/

public class DBLoadCore
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(DBLoadCore.class.getName());

  /**
   * ����� ��������� � ����� ��������������� �� � ������� �� (�� ������� ��������� ����� ������������ ���������� � ����).
   * ��� ��������������� �� (�� �����) ������ ��������� � ������ ������� �� (�� ������� ��������� ����� ������������
   * ����������� � ����).
   * @param config SerializationConfig ������������ ��� ���������� ��������������. �������� ��� ����������� ������.
   * @throws java.sql.SQLException �� ��� ���������� ������� ������� ��.
   * @throws java.io.IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws jdb.exceptions.DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
   * @throws jdb.exceptions.DBModelException ������ ������ ���� ������.
   * @return ArrayList<Exception> ������ ��������� ��� ������ ������ ��. � ������ ������ ������������ �� �����������
   * �� - ��, ������� �� �������� � �������� ������.
  */
  public static ArrayList<String> load(DBLoaderConfig config)
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException, IOException
   {
    logger.debug("WORKING DBLoadCore.load().");

    // ������������ ������ ��������� �� (��� �������������� ��)
    ArrayList<String> errorsList = null;

    // ��������� ���������� ������ ��� ������������. ���� �� �������� ������ - ������ �� �������� (���������� ��)
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ���� �� ������ � ������� - ������� ���������� ���������
    else {logger.debug("Serialization config is OK. Processing.");}
    // �������� ���������� � ���� � ������� ������� (��� ���������� � ���� ���������� �������������� ������)
    if (!DBUtils.isConnectionValid(config.getDbConfig())) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}
    
    // ���� � �������� � ������� ��������������� ��
    String dbPath = config.getPath();
    // �������� ����������� ���� � �������� - �������� ������������� ��������, �������� ��� ��� ������ �������
    if (!new File(dbPath).exists())           {throw new IOException("Catalog [" + dbPath + "] doesn't exists!");}
    else if (!new File(dbPath).isDirectory()) {throw new IOException("Path [" + dbPath + "] not a catalog!");}
    // ������� � ���, ��� ������� ��� �������� ������
    else {logger.debug("Catalog [" + dbPath + "] is OK. Processing.");}
    // ��������� ����� ��������� "���� � ��������������� ��" (����� ������������ ���������� ������� / � ����� ����)
    String localDBPath = FSUtils.fixFPath(config.getPath(), true);

    // ������ �� ������-������� ������� ��������
    DBProcessingMonitor monitor = config.getMonitor();

    // ��������� ������ ��������� ������� �� (� ������ ����������� - ������ "�����������" � "�����������")
    DBStructureModel dbModel = new DBModeler(config.getDbConfig()).getDBStructureModel();
    // ���� ���������� ������ ����� (=null), ������������ ��
    if (dbModel == null) {throw new DBModelException("Database model is empty!");}

    // ���� � ������ �� ���� ������� - ���������
    if ((dbModel.getTables() != null) && (!dbModel.getTables().isEmpty()))
     {
      logger.debug("Database structure model is not empty. Processing deserialization.");
      // �������� �� ���� �������� ������ ��������������� �� � ����� �� ��� ������
      int processedTablesCounter = 0;                        // <- ������� ������������ ������
      int tablesCount            = dbModel.getTablesCount(); // <- ����� ���������� �������������� ������

      // ���������� ��� �������� ������ ��� �������� ����� �������. ��� �������� ������ ������� �������� ����������������.
      ArrayList<String>tableLoadErrors;

      // ������������ ��� �������� ����� �������. ������ ������������ ���������� �� �������� ����� � ������ (path).
      DBLoaderConfig tableLoaderConfig = new DBLoaderConfig(config);

      // �������� �� ������ ���� ������ ������� �� � �������� ��������� � ��� ������ �� ��������������� �����
      for (TableStructureModel table : dbModel.getTables())
       {
        // ��������� �������� �� �������������� ������� (� ������ ���������)
        if (monitor != null) {monitor.processMessage("[" + table.getTableName().toUpperCase() + "] [LOADING STARTED]");}
        logger.debug("Processing table [" + table.getTableName() + "]");

        // ������� �� ��������� ��� ����������� - ������������ ��. ����� ����� ����������� ��� ������� -
        // ���� ��� �����, �� ������� ������������� "���������".
        if (config.getDbConfig().isTableAllowed(table.getTableName()))
         {
          // ���� � �������� � ������� ��� ���������� �������
          String tablePath = localDBPath + table.getTableName() + "/";
          // ���������� ���� File - �������� ������ �� ������� � ������� ��� ������� �������������� �������
          File tableDir  = new File(tablePath);
          // ���� ���������� ������� � ������, ����������� � ������ ������� - ��������� �� ���� �������
          if (tableDir.exists() && tableDir.isDirectory())
           {
            logger.info("Loading table [" + table.getTableName() + "]");
            // ����� ������ ���������������� �������� ������ � ��. ��� ������������� ������ ����� �������� � ��������������
            // ������ ������ ������. ��� ������������� ����������� ������, ��������� ������������ ��� ��������� ������� -
            // ��� �������������� ������������ try...catch.
            tableLoadErrors = null;
            // ��� ������������ �������� ����� ������� ��������� ���� � ������ ������ ���� �������
            tableLoaderConfig.setPath(tablePath);
            tableLoaderConfig.setMonitorMsgPrefix("[" + table.getTableName().toUpperCase() + "]");
            try {tableLoadErrors = TableLoadCore.load(tableLoaderConfig, table.getTableName());} // <- �������� ����� �������
            // ������������� ��������� ����������� �� ��� �������� � �� ����� �������
            catch (DBModuleConfigException e) {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (DBConnectionException e)   {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (SQLException e)            {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            catch (IOException e)             {logger.error(e.getMessage() + " TABLE [" + table.getTableName() + "].");}
            // ���� ��� ��������� ����� ������� �������� ������������� ������, �� ������ ������� �
            // �������������� ������ ������������� ������ (��� ������)
            if ((tableLoadErrors != null) && (!tableLoadErrors.isEmpty()))
             {
              logger.error("There are non-critical errors during processing table [" + table.getTableName() + "]!");
              if (errorsList == null) {errorsList = new ArrayList<String>();}
              errorsList.addAll(tableLoadErrors);
             }
            // ������� �� ��������� ��������� ������� �������
            logger.info("Table [" + table.getTableName() + "] loaded successfully.");
           }
         }
        // ���� ������� ������� ��������� ��� ����������� - ������!
        else {logger.warn("Current table [" + table.getTableName() + "] is deprecated or not allowed! Skipping.");}

        // ����� ��������� ������ �������(�������, ��������� ��� ��� ���) ������� �������������� ��������� (���� ����������),
        // ������� �������� ���������� � ��������� ��������� ������ (����� ������ processProgress() ��������)
        if (monitor != null)
         {
          // ����������� ������� ������������ ������
          processedTablesCounter++;
          // ������� ������� ������� ����������
          int currentProgress = (processedTablesCounter*100/tablesCount);
          // ����� � �������-������� �������� ��������� ���������� ()
          monitor.processProgress(currentProgress);
          // ����� � �������-������� ��������� �� ��������� ��������� ������� �������
          monitor.processMessage("[" + table.getTableName().toUpperCase() + "] [LOADING FINISHED]");
          // ���� �������� ������� (������� ��������� ���� DEBUG), �� ������� ��������� �� �������������� ������ �������.
          //if (logger.getEffectiveLevel().equals(Level.DEBUG)) {monitor.processDebugInfo("[DEBUG] " + table.getTableName());}
         }
        
       } // ����� ����� ��������� ���� ������ [FOR]
     }
    // ���� �� � ���������� ������ �� ��� ������ - ������� �� ���� � ��� (������ �� ������)
    else {logger.error("Current database model is empty! Nothing to process.");}

    return errorsList;
   }

 }