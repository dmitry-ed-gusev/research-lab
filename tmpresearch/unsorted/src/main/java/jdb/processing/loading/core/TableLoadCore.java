package jdb.processing.loading.core;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.config.batch.BatchConfig;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.loading.helpers.SqlBatchBuilder;
import jdb.processing.sql.execution.batch.SqlBatcher;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ������ ����� ��������� �������������� ����� ������� �� �� ������ �����/�� ������ ������ �� �����. ��� ��������������
 * ������ ������������ ������� ����� ������� ������ ����������. ������� �������������� ������ ����� ������������ � ��������
 * ������� (������, ������������������ ����������).
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 15.11.2010)
*/

// todo: ������� �� ���������� ������ ������ � ����� � � ������� � �� (���-��, ���� � ������������ ����� � �.�.)
// todo: � ������ ������������ ������ ������ ����. ���������� ��������� ������!!!!!

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class TableLoadCore
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(TableLoadCore.class.getName());

  /**
   * ����� ������������� � ���������� � �� ���������� �� ��������������� �������, ����������� � ��������� � �������
   * ��������. ����� ��������������� ������������ ��� �����, ����������� � ��������� �������� �, �������� ��� ������� �����
   * sql-batch, ��� �� ��� ���������. ��� �������������� ����������� ��� ������� - ���� ���� � ����������� �������� ��������
   * �� ���������� ��� ������� - ���� ������������ (���� �������� �������� ������������ �������������� ������ ������ ���
   * ��������� ������� �, ���� ������� "���������" ��� ������ �� (��. ��������� ���������� � ����), �� �� ������ ����� �� �����
   * �������� �������).
   * @param config SerializationConfig ������������ ������ �������������� ����� �������.
   * @param tableName String ��� ��������������� �������.
   * @return ArrayList[String] ������ ������������� ������, ��������� ��� �������������� ������ ����� �������.
   * @throws DBModuleConfigException ������ ���������������� ������/������.
   * @throws IOException ������ ������ ������ � �����.
   * @throws SQLException ����������� ������ ������ � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public static ArrayList<String> load(DBLoaderConfig config, String tableName)
   throws DBModuleConfigException, DBConnectionException, IOException, SQLException
   {
    logger.debug("WORKING TableLoadCore.load().");
    // ������������ ������ ��������� ������������� �� (��� �������������� �������)
    ArrayList<String> errorsList   = null;
    // ������ sql-batch, ������� ����������� � �����������, ���� ��������� �������� useFullScript=true (�.�. ����
    // �� �� ��������� ��������� ������ - ��� ������� �����)
    ArrayList<String> fullSqlBatch = null;

    // ���� ��� ������� ������ � ��������, ���������� ��!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // ���� �� ������ � ������� - ������� ���������� ���������
    else {logger.debug("Serialization config is OK. Processing.");}
    // �������� ���������� � ���� � ������� ������� (��� ���������� � ���� ���������� �������������� ������)
    if (!DBUtils.isConnectionValid(config.getDbConfig())) {throw new DBConnectionException("Can't connect to DBMS!");}
    else {logger.debug("Connection to DBMS is OK! Processing.");}
    // ��������� ��������� ��� ������� - ���� ��� ����� - ������
    if (StringUtils.isBlank(tableName)) {throw new SQLException("Specifyied table name is empty!");}
    else {logger.debug("Table name [" + tableName + "] is OK.");}

    // ���� � �������� � ������� ��������������� �������
    String tablePath = config.getPath();
    // �������� ����������� ���� � �������� - �������� ������������� ��������, �������� ��� ��� ������ �������,
    // �������� ��� ������� �� ���� � �������� �����.
    if (!new File(tablePath).exists())           {throw new IOException("Catalog [" + tablePath + "] doesn't exists!");}
    else if (!new File(tablePath).isDirectory()) {throw new IOException("Path [" + tablePath + "] not a catalog!");}
    else if (!FSUtils.containFiles(tablePath))   {throw new IOException("Catalog [" + tablePath + "] doesn't contain files!");}
    // ������� � ���, ��� ������� ��� �������� ������
    else {logger.debug("Catalog [" + tablePath + "] is OK. Processing.");}
    // ��������� ����� ��������� "���� � ��������������� �������" (����� ������������ ���������� ������� / � ����� ����)
    String localTablePath = FSUtils.fixFPath(tablePath, true);
    
    // ������ �� �����-������� �������� ��������� ������
    DBProcessingMonitor monitor = config.getMonitor();
    // ������� ��� ��������� ��������. ���� ������� �� ������, �� � �������� �������� ����������
    // ��� �������������� �������.
    String monitorMessagePrefix;
    if (StringUtils.isBlank(config.getMonitorMsgPrefix())) {monitorMessagePrefix = "[" + tableName.toUpperCase() + "]";}
    else                                                   {monitorMessagePrefix= config.getMonitorMsgPrefix();}

    // �������� - ������������ ������ ������ (true) ��� ��� (false)
    boolean useFullScript = config.isUseFullScript();

    // ���� ������� �� ��������� ��� ����������� ���������� ���������� � ���� - ������������ ��. ����������� ������
    // ����������� ��, ���� ����� ������������� ������ �� 
    if (config.isTableAllowed(tableName))
     {
      logger.debug("Processing table import. Table [" + tableName + "]. Path [" + localTablePath + "]");
      // ���� ���� ������� - ������ �������������� �������
      //if (monitor != null) {monitor.processMessage(tableName);}

      // �������� ������ ������ � ���������� �� (�����) ������������� (� �����, ��. ����)
      File[] files = new File(localTablePath).listFiles();
      
      // ���� ������ ������ �� ����, �� �������� ���������
      if ((files != null) && (files.length > 0))
       {
        logger.debug("Catalog [" + localTablePath + "] contain [" + files.length + "] objects. Processing.");

        // ����� ������������ ��� ���������� sql-�����. ��� �������, ��� � ����������. ������������ �����
        // ��������� �� ������ ������������ ������������. ��������������� ��� ���� ����������� � ������� �����.
        BatchConfig batchConfig = new BatchConfig(config);
        
        // ������� �������������� ������
        int processedFilesCounter = 1;
        // ��������������� ���� ��������� ������ � ��������� ��������
        for (File file : files)
         {
          // ��������� �������� ����� ������ �������������� ������. ���� ����������� ���� ��������� ������������.
          if (monitor != null)
           {monitor.processMessage(monitorMessagePrefix + " [FILE: " +
             processedFilesCounter + " / " + files.length + "] [PROCESSING]");}

          // ���� ������� ���� �� ���� ( != NULL), ������������ ���
          if (file != null)
           {
            // ������������ ������� ���� ������ ���� ��� ������������� ���� � �� ����������
            if ((file.exists()) && (file.isFile()))
             {
              logger.debug("Processing [" + file.getAbsolutePath() + "]");
              // ��������������� ��������� �������� �����. ����� ������ ��������� ������ ����� �� ��������� � ���������
              // �������� ��������� ����������� try...catch
              try
               {
                // ������ sql-�������� ��� ���������� ��� ������ ����� ����� �������
                ArrayList<String> sqlBatch = SqlBatchBuilder.getBatchFromTableFile(config.getDbConfig(),
                 (localTablePath + file.getName()), config.isDeleteSource(), tableName, config.isUseSqlFilter());

                // ���� ���������� ����� ��������� sql-������ (�������� useFullScript=false), �� ���������� ���� �����
                // ���������, ���� �� ��������� ������ ���� - �� ��������� ���������� ���� (���� �� �� ����) � ������
                // ����� ��� ���������� ����� ��������� ���� ������ � �������� � ��������
                if ((sqlBatch != null) && (!sqlBatch.isEmpty()))
                 {
                  // ������� � ���, ��� ���������� ��� ������� ����� ���� �� ����
                  logger.debug("Batch for table [" + tableName + "] in file [" + (localTablePath + file.getName()) + "] is not" +
                               " empty. Processing.");
                  // ���� ����� ��������� ������ (useFullScript=false) - ��������� ����� �������� �����
                  if (!useFullScript)
                   {
                    logger.debug("USING PART SCRIPT METHOD. EXECUTING BATCH FOR FILE [" + file.getAbsolutePath() + "].");

                    // todo: ������ ���������� ����� ���������� ������� � ��������-������
                    //for (String sql : sqlBatch) {logger.debug("-> " + sql);} // <- ���������� ����� (�� ��� ����������)

                    // ������� ��������� � ���� ����� ������ ��������� ��
                    switch (config.getDbConfig().getDbType())
                     {
                      case INFORMIX:
                       //batchConfig.addSqlToBatch("DATABASE " + config.getDbConfig().getDbName());
                       break;
                      case MSSQL_NATIVE: case MSSQL_JTDS: case MYSQL:
                       //batchConfig.addSqlToBatch("USE " + config.getDbConfig().getDbName());
                       break;
                     }
                    // ���� �������� ����� ������� ������� ����� ��������� � ��� ������ (isClearTableBeforeLoad) -
                    // ��������� ������ ��� ������� �������
                    //if (config.isClearTableBeforeLoad()) {batchConfig.addSqlToBatch("DELETE FROM " + config.getDbConfig().getDbName());}
                    
                    // ���� �������� ����� "SET IDENTITY_INSERT..." (useSetIdentityInsert), �� ���������� ��������� sql-����������
                    // �� ���������� �������� ���������������. ������ ����� ������������ ������ ������� MS SQL (�������������
                    // � ������� 2005). ����� � ����� ����� ���������� �������� sql-����������, ���������� �������� ���������������.
                    DBConsts.DBType dbType = config.getDbConfig().getDbType();
                    logger.debug("-> " + dbType);
                    if (config.isUseSetIdentityInsert() &&
                     (DBConsts.DBType.MSSQL_JTDS.equals(dbType) || DBConsts.DBType.MSSQL_NATIVE.equals(dbType)))
                     {
                      logger.debug("DBMS type = MSSQL, useIdentityInsert = true. Processing.");
                      //batchConfig.addSqlToBatch("SET IDENTITY_INSERT " + tableName + " ON");
                     }
                    // ��������� ����������� ���� � ������������
                    //batchConfig.addBatch(sqlBatch);
                    // ���� ����� IDENTITY_INSERT ���� �������� - ���������� �� ���������
                    if (config.isUseSetIdentityInsert() &&
                     (DBConsts.DBType.MSSQL_JTDS.equals(dbType) || DBConsts.DBType.MSSQL_NATIVE.equals(dbType)))
                     {
                      logger.debug("DBMS type = MSSQL, useIdentityInsert = true. Processing.");
                      //batchConfig.addSqlToBatch("SET IDENTITY_INSERT " + tableName + " OFF");
                     }

                    //logger.debug("-> \n" + batchConfig.getBatch());
                    System.exit(0);
                    
                    // ������ ������� ��������� �������� ��� �����
                    batchConfig.setMonitorMsgPrefix(monitorMessagePrefix + " [FILE " + processedFilesCounter + " / " + files.length + "]");
                     // ��������� ���� � �������� ������ ������, ��������� � ���������� ���������� ������ sql-����� (���
                    // ������ �����). ���������������/�������������� ���������� ����� ������������ ������������� ��� ������������.
                    ArrayList<String> errors = SqlBatcher.execute(batchConfig);
                    // ���� ���� ������, �� ������� �� � ��������������� ������
                    if ((errors != null) && (!errors.isEmpty()))
                     {
                      logger.warn("There are errors during processing part batch for table [" + tableName + "]. See log!");
                      if (errorsList == null) {errorsList = new ArrayList<String>();}
                      errorsList.addAll(errors);
                     }
                    // ���� ��� ��������� �������� ����� ������ ������� �� ���� ������ - ����� ������� �� ����
                    else {logger.debug("Executing part batch for table [" + tableName + "] finished without errors.");}
                   }
                  
                  // ���� �� ���������� ����� ������� ����� - ��������� �������������� ����
                  else
                   {
                    logger.debug("USING FULL SCRIPT METHOD. ADDING BATCH FOR FILE [" + file.getAbsolutePath() + "] TO RESULT SQL-BATCH.");
                    // ���� ������ ���� ��� �� ��������������� - �������������
                    if (fullSqlBatch == null) {fullSqlBatch = new ArrayList<String>();}
                    // ��������� � ������ ���� ���� �� ����� �������� �����
                    fullSqlBatch.addAll(sqlBatch);
                   }
                 }
                // ���� �� ���������� ���� ���� - ������� � ���
                else {logger.warn("Batch for table [" + tableName + "] in file [" + (localTablePath + file.getName()) + "] is empty!");}
                
               }
              // ��� ����������� ������ ��������� � �������������� ������
              catch (IOException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (SQLException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (DBModuleConfigException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
              catch (DBModelException e)
               {if (errorsList == null) {errorsList = new ArrayList<String>();} errorsList.add(e.getMessage());}
             }
           }
          // ���� ������� ���� �������� �� ������ (��������� � �.�.), �� �� ������������ ���
          else {logger.warn("Object [" + file.getAbsolutePath() + "] doesn't exists or not a file! Skipped!");}

          // ����������� ������� ������������ ������. ������� ����������� ��� ����������� �� ���� ��� ��������� ���� ��� ��� -
          // �.�. ���� ���� ��� ��������, �� ������� ��� ����� �������� (���� ������� ����� ��������������� ��� ��� ���������).
          processedFilesCounter++;
          
         } // END OF FOR CYCLE (����� ��������� ���� ������ � ��������)

         // ���� ������������ ����� "�������" ������� (��������� ������� sql-batch ��� ���� ������), �� ������
         // ���������� ��������� ���������� ������
         if (useFullScript)
          {
           logger.debug("USING FULL SCRIPT METHOD. EXECUTING FULL SQL-BATCH.");
           // ���� ������ sql-batch �� ����, ��������� ���
           if ((fullSqlBatch != null) && (!fullSqlBatch.isEmpty()))
            {
             logger.debug("Full sql-batch not empty. Executing.");
             // ��������� ����������� ���� � ������������
             //batchConfig.setBatch(fullSqlBatch);
             // ��������� ���� � �������� ������ ������, ��������� � ���������� ���������� ������ sql-����� (��� ������
             // �����). ���������������/�������������� ���������� ����� � ����������� ������������� ��� ������������.
             ArrayList<String> errors = SqlBatcher.execute(batchConfig);
             // ���� ���� ������, �� ������� �� � ��������������� ������
             if ((errors != null) && (!errors.isEmpty()))
              {
               logger.warn("There are errors during executing full batch for table [" + tableName + "]. See log!");
               if (errorsList == null) {errorsList = new ArrayList<String>();}
               errorsList.addAll(errors);
              }
             // ���� ��� ��������� ������ ������� �� ���� ������ - ����� ������� �� ����
             else {logger.debug("Executing full batch for table [" + tableName + "] finished without errors.");}
            }
           // ���� �� ������ sql-batch �������� ������, ������ �� ������ (������ ������� � ���)
           else {logger.warn("Full sql-batch is empty! Nothing to execute!");}
          }

       }
      // ���� ���������� ������ ������ ���� 
      else {logger.warn("File list is empty (no files in [" + localTablePath + "])!");}
      // ������� �� ��������� ��������� ������� ������� (��� ����������� �� ���������� ���� ���������)
      logger.debug("Table [" + tableName + "] processed successfully. See log for results.");
     }
    // ���� ������� ������� ��������� ��� ����������� - ������!
    else {logger.warn("Current table [" + tableName + "] is deprecated or not allowed! Skipping.");}
    // ���������� ������ ��, ��������� ��� ��������� ������ ������� �������
    return errorsList;
   }

  /**
   * ����� ��� ������������ � ������� ������� ������.
   * @param args String[] ��������� ������ main. 
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    InitLogger.initLogger("jlib", Level.INFO);
    Logger logger = Logger.getLogger("jdb");

    DBConfig mysqlConfig1 = new DBConfig();
    mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig1.setHost("localhost:3306");
    mysqlConfig1.setDbName("storm");
    mysqlConfig1.setUser("root");
    mysqlConfig1.setPassword("mysql");
    mysqlConfig1.addAllowedTable("items");

    DBConfig mssqlConfig = new DBConfig();
    mssqlConfig.setDbType(DBConsts.DBType.MSSQL_JTDS);
    mssqlConfig.setHost("APP");
    mssqlConfig.setDbName("norm_docs");
    mssqlConfig.setUser("sa");
    mssqlConfig.setPassword("adminsql245#I");

    try
     {
      //DataChanger.cleanupTable(mssqlConfig1, "items");
      DBLoaderConfig config = new DBLoaderConfig();
      config.setDbConfig(mssqlConfig);
      config.setDeleteSource(false);
      //config.setClearTableBeforeLoad(true);
      //config.setMultiThreads(true);
      //config.setDbmsConnNumber(10);

      //config.setPath("c:\\temp\\norm_docs\\docTypes");
      //TableLoadCore.load(config, "doctypes");
      //config.setPath("c:\\temp\\norm_docs\\norm_docs");
      //TableLoadCore.load(config, "norm_docs");
      
      config.setPath("c:\\temp\\norm_docs\\norm_docs_parts");
      TableLoadCore.load(config, "norm_docs_parts");
      
      //config.setPath("c:\\temp\\norm_docs\\changes_journal");
      //TableLoadCore.load(config, "changes_journal");
      //config.setPath("c:\\temp\\norm_docs\\files");
      //TableLoadCore.load(config, "files");

      //SerializationConfig mysqlSerConfig = new SerializationConfig();
      //mysqlSerConfig.setDbConfig(mysqlConfig1);
      //mysqlSerConfig.setPath("c:\\temp\\storm");
      //mysqlSerConfig.setDeleteSource(false);

      // ��������� ������� � ������ ��
      //DBDeserializer.deserializeDB(mysqlSerConfig);
     }
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (IOException e) {logger.error(e.getMessage());}
    //catch (DBModelException e) {logger.error(e.getMessage());}
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}

   }

 }