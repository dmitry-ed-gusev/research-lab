package jdb.nextGen;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.nextGen.exceptions.JdbException;
import jdb.nextGen.models.SimpleDBIntegrityModel;
import jdb.nextGen.models.SimpleDBTimedModel;
import jdb.nextGen.serialization.Descriptor;
import jdb.nextGen.serialization.SerializableResultSet;
import jdb.utils.DBUtils;
import jlib.exceptions.EmptyObjectException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import jlib.utils.JLibCommonUtils;
import jlib.utils.string.StrUtilities;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ������-���� ������� ���������/�������� �� �� ����/� �����. �������� �������� ������, ����������� ��������� ������.
 * ����� ��� ������ ��������� ��� private (���� ������� ��������/��������), ��������� ��������� ��� protected - ���
 * ������ ������ ����� ��������� ���� ��������/��������. ��������� ������������ ������ ������� ����������� ������� ������.
 * @author Gusev Dmitry (����� �������)
 * @version 6.0 (DATE: 18.05.11)
*/

// todo: ��� ���� ���������� ����� fixFPath() � ��� ����� ��������� ���������� � ������� ������� �������� (toUpperCase())

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public final class DBasesLoaderCore
 {
  /** ���-������������ �������� �������� �� �� ���� - ��� ����� ���� ������� ��� �� ������� (����� 2 ��������). */
  public static enum ObjectType {TABLE, DATABASE}

  /** ������ ������. */
  private static Logger logger = Logger.getLogger(DBasesLoaderCore.class.getName());

  // ������������� ��������������� � ������������, �.�. ����� �����������
  private DBasesLoaderCore() {}

  /**
   * ����������� ����� ����� ����� � ������������ �� �� �������. � ���� ����� ���������� ��� ����� ����������� ������ -
   * �������� ��� 12 ���������� � 0000000012 (��� �������� ������� ��������� = 10).
   * �� ���������� ������� ������� ������� �������� - ��� �������� �� �������������� �������.
  */
  private static final int    FILE_NAME_LENGTH       = 10;
  /**
   * ��������� ���� �������� ����������� ��� ����� �� ����������� ����� (����������� � ������ �����). ���� ������ - ����.
   * ������ ������� (�������� �����) ������� �� ���� - ��� ������� ������� ���������� ������ � �������� ������� � �������
   * ������� �� (������) ��������� �������� �������� ������ �� ������ � ��.
  */
  private static final char   FILE_NAME_FILL_SYMBOL = '0';
  /** ��� �����-����������� ��� ��������������� ������� ��� ��. */
  private static final String DESCRIPTOR_FILE_NAME  = "descriptor";
  /** ���������� �����-����������� ��� ��������������� �������. */
  private static final String EXTENSION_TABLE       = "tbl";
  /** ���������� �����-����������� ��� ��������������� ��. */
  private static final String EXTENSION_DB          = "db";

  /**
   * ����� ��������� �������� ��������� ������� �� ����.
   * @param conn Connection
   * @param path String
   * @param tableName String
   * @param lowerTimestamp Timestamp
   * @param upperTimestamp Timestamp
   * @param keysList ArrayList[Integer]
   * @return boolean ������������ �������� ������/���� � ����������� �� ���������� �������� ������� �� ����. ����
   * ���� �����-�� ������ ���� ��������� (���� ���� ������ �������), ����� ������ �������� ������, ���� �� ������
   * ��������� �� ���� - ����� ������ �������� ����.
   * @throws JdbException ��
  */
  private static boolean unloadTableToDisk(Connection conn, String path, String tableName, Timestamp lowerTimestamp,
   Timestamp upperTimestamp, ArrayList<Integer> keysList) throws JdbException
   {
    boolean result = false;
    logger.debug("DBasesLoaderCore.unloadTableToDisk().");
    // �������� ���������� � ����
    if (conn != null)
     {
      // �������� ����� �������
      if (!StringUtils.isBlank(tableName))
       {
        // �������� �������� ����������
        if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory())
         {
          // �������� �������� - ��������������� ���������� ��������
          StringBuilder sql = new StringBuilder("select * from ").append(tableName);
          // ��������� � ������ ������ ������, � ������� ������ ������� ���������� �� ������� �����
          if ((keysList != null) && (!keysList.isEmpty()))
           {
            sql.append(" where ").append(DBConsts.FIELD_NAME_KEY).append("in (");
            sql.append(JLibCommonUtils.getCSVFromArrayList(keysList)).append(")");
           }
          // ��������� � ������ ������ ������� �� ������� (�� ����������)
          if (lowerTimestamp != null)
           {
            logger.debug("Lower timestamp for table [" + tableName + "]: " + lowerTimestamp);
            if ((keysList != null) && (!keysList.isEmpty())) {sql.append(" and ");}
            else                                             {sql.append(" where ");}
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" >= ?");
           }
          else {logger.debug("There is no lower timestamp for table [" + tableName + "].");}
          // ��������� � ������ ������� ������� �� ������� (�� ����������)
          if (upperTimestamp != null)
           {
            logger.debug("Upper timestamp for table [" + tableName + "]: " + upperTimestamp);
            if (((keysList != null) && (!keysList.isEmpty())) || (lowerTimestamp != null)) {sql.append(" and ");}
            else                                                                           {sql.append(" where ");}
            sql.append(DBConsts.FIELD_NAME_TIMESTAMP).append(" <= ?");
           }
          else {logger.debug("There is no upper timestamp for table [" + tableName + "].");}

          // ���������� �� ���������� (����������� ������!)
          sql.append(" order by ").append(DBConsts.FIELD_NAME_TIMESTAMP);

          // ���������� �����
          logger.debug("Generated SQL: [" + sql.toString() + "]");
          // ���������� ������� � ��������� ������
          PreparedStatement stmt = null;
          ResultSet         rs   = null;
          // ����������� try-catch-finally ���������� ��� ����, ����� �������� ���� � ����� finally ���������� �������.
          // ����� ��� ��������� �� ������������� � �� JdbException - ��� ����, ����� ���������� ��� ������� ���� �� �,
          // ���������� �� ��� ������� ������� � ������� ����������� ������������ �������.
          try
           {
            stmt = conn.prepareStatement(sql.toString());
            // ���������� ����������� � ����������������� ������. ������ ��������������� ���������� �����������
            // � ����������� �� �� (����������) ����������.
            if (lowerTimestamp != null)
             {
              stmt.setTimestamp(1, lowerTimestamp);
              if (upperTimestamp != null) {stmt.setTimestamp(2, upperTimestamp);}
             }
            else if (upperTimestamp != null) {stmt.setTimestamp(1, upperTimestamp);}

            rs = stmt.executeQuery();
            logger.debug("Data from DB received! Processing it.");

            //*** �������� � ������������ ������� ������ �������. ����� ��������� ������ �������� rs.next() �� ���������,
            // �.�. ��� �������� ��������� � ������� �� 1 �������, � � ������������ ������ SerializableResultSet ���������
            // ����� ���������� �� 1 ������� (���������� rs.next()) � ��������� ������ (���������� ���� ������ �� �������).
            boolean iterationFlag = true;
            int counter = 1;
            // ���������� (���������) ��� ������������� �������
            Descriptor descriptor = null;
            do
             {
              SerializableResultSet srs = new SerializableResultSet(rs, tableName, DBConsts.SERIALIZATION_TABLE_FRACTION);
              // ���� � ���������� ��������� ������� �������� SRS - ���������� ��� �� ����
              if ((srs != null) && !srs.isEmpty())
               {
                String fileName = StrUtilities.getFixedLengthName(FILE_NAME_LENGTH, FILE_NAME_FILL_SYMBOL, counter);
                FSUtils.serializeObject(srs, path, fileName, EXTENSION_TABLE);
                // ������ ������ � ���������� ����������� �������
                if (descriptor == null) {descriptor = new Descriptor(tableName, DBasesLoaderCore.ObjectType.TABLE);}
                descriptor.addItem(fileName + "." + EXTENSION_TABLE);
                // �.�. ��������� �������� ������ �� ���� - ��������� �������� ������� - �����������!
                if (!result) {result = true;}
                // ���������� �������� ����������� ������
                counter++;
                // todo: �������� �������. ������� ���������� ���� �� ����. � ���������� ����� �������!
                logger.debug("Writed file [" + FSUtils.fixFPath(path, true) + fileName + "." + EXTENSION_TABLE + "].");
               }
              // ���� ��������� ����������� SRS ���� - �������� ����������!
              else {iterationFlag = false;}
             }
            while(iterationFlag);
            // ����� ���������� ���� �������� � ������� - ���������� �� ���� ���������� ��������������� �������.
            // ���������� ������������ ������ � ��� ������, ���� �� ��� ������������������, �.�. ���� ����
            // �������� �� ���� �����-���� ������.
            if (descriptor != null) {FSUtils.serializeObject(descriptor, path, DESCRIPTOR_FILE_NAME, EXTENSION_TABLE);}
            // ���� ���������� �� ���������������, �� ������ ������� � ���, ��� ������ �� ���������
            else {logger.info("No data was serialized for table [" + tableName + "].");}
           }
          // ��������������� �� ������������ � ���� - JdbException. �����? ��. ������� *** ����.
          catch (SQLException e)         {throw new JdbException(e);}
          catch (EmptyObjectException e) {throw new JdbException(e);}
          catch (IOException e)          {throw new JdbException(e);}
          // ������������ ��������
          finally
           {
            try {if (rs != null)   {rs.close();} if (stmt != null) {stmt.close();}}
            catch (SQLException e) {logger.error("Can't free resources! Message: [" + e.getMessage() + "].");}
           }
         }
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      else {throw new JdbException("Empty table name [" + tableName + "]!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // ����������� ���������� �������� ������� �� ����
    return result;
   }

   /**
   *
   * @return boolean ������������ �������� ������/���� � ����������� �� ���������� �������� �� �� ����. ����
   * ���� �����-�� ������ ���� ��������� (���� ���� ������ ����� �������), ����� ������ �������� ������, ���� �� ������
   * ��������� �� ���� - ����� ������ �������� ����.
  */
  protected static boolean unloadDBToDisk(Connection conn, String path, String dbName, ArrayList<String> tablesList,
   SimpleDBTimedModel timedModel, SimpleDBIntegrityModel integrityModel) throws JdbException
   {
    boolean result = false;
    logger.debug("DBasesLoaderCore.unloadDBToDisk().");
    // �������� ���������� � ����
    if (conn != null)
     {
      // ��������� ������ ������ (������ ���� �� ����)
      if (!StringUtils.isBlank(dbName) && (tablesList != null) && (!tablesList.isEmpty()))
       {
        // ��������� ��������. ���� ������ ���� �� ������, ������������ � ���� ������ ���������.
        if (!StringUtils.isBlank(path) && (new File(path).exists()) && (new File(path).isDirectory()))
         {
          // ����� ��������� �������� ���������� - �������� �����. ������� ���������� �� � � �����
          // ������������ ���� ���������� ������ ������
          Descriptor descriptor = null;
          for (String tableName : tablesList)
           {
            if (!StringUtils.isBlank(tableName))
             {
              // ������� ������� ��� �������� ���������� �������. ���� �������� �������� ��
              // ������� - ������������ �� � ��������� �����������
              if (new File(FSUtils.fixFPath(path, true) + tableName.toUpperCase()).mkdir())
               {
                // �������� ��������� ��� ������ �������
                Timestamp timestamp = null;
                if (timedModel != null) {timestamp = timedModel.getTimestampForTable(tableName);}
                // �������� ������ ������ ��� �������� �� ������ �������
                ArrayList<Integer> keysList = null;
                if (integrityModel != null) {keysList = integrityModel.getKeysListForTable(tableName);}
                // ����������� try-catch - ������ ��� �������� ����� ������� �� ��������� ��������� ����� ������.
                // ����� ��� ��������� ������� � ������������� ������ �� ��������������� � ��������� �������
                // � "�������������������" �������� (� ������ ������������ ����� ������� ������� ����������� ���� ��
                // � ���� - JdbException ��� �������� ��������� � ������ ������ - ��� �������� �������� � ��������
                // ��������������� ��������).
                try
                 {
                  // ��������������� �������� ������� �� ����
                  boolean unloadTableResult = DBasesLoaderCore.unloadTableToDisk(conn, FSUtils.fixFPath(path, true) + tableName,
                                               tableName, timestamp, null, keysList);

                  // ������������� ����������� � ������ � ���� ����������� �������, ������ ���� ���-�� ������� ���� ���������
                  if (unloadTableResult)
                   {
                    if (descriptor == null) {descriptor = new Descriptor(dbName, DBasesLoaderCore.ObjectType.DATABASE);}
                    descriptor.addItem(tableName);
                    // �.�. ��������� �������� (�������) ������� - ���� �� ����� - ��������� �������� �� �����������
                    if (!result) {result = true;}
                   }
                  // ���� �� ������ ��������� �� ���� - ������ ��������� ��� �������� ������� ������ �������
                  else
                   {
                    // ������� ����� ������ �������� ������� �� ���� (�������� ��������)
                    FSUtils.delTree(FSUtils.fixFPath(path, true) + tableName.toUpperCase());
                   }
                 }
                catch (JdbException e)
                 {
                  // ������� ����� ��������� �������� ������� �� ����
                  FSUtils.delTree(FSUtils.fixFPath(path, true) + tableName.toUpperCase());
                  // ����� ��������� ��.
                  logger.error("Error processing table [" + tableName + "]! Message [" + e.getMessage() + "].");
                 }
               }
              else {logger.error("Can't create catalog [" + (FSUtils.fixFPath(path, true) + tableName) + "] for " +
                                 "table [" + tableName + "]! Table skipped.");}
             }
            // ������ ��� ������� - ������� � ��� (WARN)
            else {logger.warn("Empty table name! Programmer error?");}

           } // END OF FOR

          // �� ��������� ����� ��������� ������ - ������ ����������� �� ���� (���� �� ��� ������������������)
          if (descriptor != null)
           {
            // ����������� try-catch � "�����������" �� � JdbException ���������� ��� ����, ����� ���������� ��� ���
            // ��������� �������� ���� ����� ��������� �������� �� - ������� �����, ��������� ��� �������� ��.
            // (� ��������� ������ �������� �� ������ catch() ��� ������ �� � � ������ ������ ���� �� ���� � �� �� ��������)
            try {FSUtils.serializeObject(descriptor, path, DESCRIPTOR_FILE_NAME, EXTENSION_DB);}
            catch (EmptyObjectException e) {throw new JdbException("Can't serialize db descriptor!", e);}
            catch (IOException e)          {throw new JdbException("Can't serialize db descriptor!", e);}
           }
          // ���� ���������� ������������������ �� ��� - ��� ��������, ��� ������� ������ ������ �� ���� ���������.
          // ������� �� ���� � ���.
          else {logger.info("No data was serialized for DB [" + dbName + "].");}
         }
        // ��������� ���� ��� ��������
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      // ��������� ��� �� ��� ������ ������ ������
      else {throw new JdbException("Empty db name [" + dbName + "] or tables list [" + tablesList + "]!");}
     }
    // ������ ���������� � ����
    else {throw new JdbException("Empty db connection!");}
    // ������� ����������
    return result;
   }

  /**
   * �������� �������������� ������� ������ (SerializableResultSet) � ������� ��.
   * @param conn Connection ���������� � ����.
   * @param srs SerializableResultSet ��������������� ������������� ������ ��� ������ � ��.
   * @param tableName String ��� �������, ���� ���� �������� ������ - ��� �������� ��� �������, ��������� �
   * ������������� �������, �.�. ����������� �������� ������.
  */
  private static void loadSrsToDB(Connection conn, SerializableResultSet srs, String tableName) throws JdbException, SQLException
   {
    // ��������� ���������� � ����
    if (conn != null)
     {
      // ��������� ���������� SRS
      if ((srs != null) && (!srs.isEmpty()))
       {
        // ������� (update, insert � ������ ��� �������� ������������� ������ - select)
        String        preparedInsert = srs.getPreparedInsertSql(tableName);
        String        preparedUpdate = srs.getPreparedUpdateSql(tableName);
        StringBuilder checkRecord    = new StringBuilder("select ").append(DBConsts.FIELD_NAME_KEY).append(" from ");
        // ���� ������� ��� ������� - ���������� ���, ���� �� �� ������� - ���������� ��� ������� �� SerializableResultSet
        if (!StringUtils.isBlank(tableName)) {checkRecord.append(tableName);}
        else                                 {checkRecord.append(srs.getTableName());}
        checkRecord.append(" where ").append(DBConsts.FIELD_NAME_KEY).append(" = ?");

        // todo: ����� ��� �������� �������
        //logger.debug("INSERT -> " + preparedInsert);
        //logger.debug("UPDATE -> " + preparedUpdate);
        //logger.debug("CHECK  -> " + checkRecord);

        // Statement-�������
        PreparedStatement insertStatement = conn.prepareStatement(preparedInsert);
        PreparedStatement updateStatement = conn.prepareStatement(preparedUpdate);
        PreparedStatement checkRecordStatement = conn.prepareStatement(checkRecord.toString());
        // ������� � �������� �������� ������� ��������� ���� � ������� ������
        int keyFieldIndex = srs.getKeyFieldIndex();
        // �������� ������ �� �������
        ArrayList<ArrayList<String>> data = srs.getData();
        // ������������ ������ � ������� �� � ��
        for (ArrayList<String> row : data)
         {
          // �������� ������������� ������
          checkRecordStatement.setObject(1, row.get(keyFieldIndex));
          ResultSet rs = checkRecordStatement.executeQuery();
          // ������ � ������ ������ ���������� - ��������� �������� UPDATE (������)
          if (rs.next())
           {
            // logger.debug("[" + row.get(keyFieldIndex) + "] -> UPDATE"); // <- ������ ��� �������!
            int j = 0;
            // �������� ������
            for (int i = 1; i < row.size(); i++)
             {
              if (j == keyFieldIndex) {j++;}
              updateStatement.setObject(i, row.get(j));
              j++;
             }
            // �������� ��������� ����
            updateStatement.setObject(row.size(), row.get(keyFieldIndex));
            // ���������� ������ ��������� � ����
            updateStatement.addBatch();
           }
          // ������ � ������ ������ �� ���������� - ��������� �������� INSERT (������)
          else
           {
            // logger.debug("[" + row.get(keyFieldIndex) + "] -> INSERT"); // <- ������ ��� �������!
            for (int i = 1; i <= row.size(); i++) {insertStatement.setObject(i, row.get(i - 1));}
            insertStatement.addBatch();
           }
         }
        // ������ ��������������� ������
        logger.debug("DBasesLoaderCore.loadSrsToDB(): executing updates batch.");
        updateStatement.executeBatch();
        logger.debug("DBasesLoaderCore.loadSrsToDB(): executing inserts batch.");
        insertStatement.executeBatch();
       }
      // ������� ������ SRS
      else {throw new JdbException("Empty SerializableResultSet object instance!");}
     }
    // ���������� � ���� �����
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /***/
  private static void loadTableFromDisk(Connection conn, String path, String tableName,
   boolean useTableNameCheck, DBProcessingMonitor monitor)
   throws JdbException, ClassNotFoundException, IOException, SQLException
   {
    logger.debug("DBasesLoaderCore.loadTableFromDisk().");
    // �������� ���������� � ����
    if (conn != null)
     {
      // �������� ����� ������� - ��� ������ ���� ��������
      if (!StringUtils.isBlank(tableName))
       {
        // �������� �������� ���������� (������ ������������ � ���� ���������)
        if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory())
         {
          // ������� - �� �������� ��������� ���������� �������. ���� �� ���������� - ���������� ��.
          String descriptorPath = FSUtils.fixFPath(path, true) + DESCRIPTOR_FILE_NAME + "." + EXTENSION_TABLE;
          Descriptor tableDescriptor = (Descriptor) FSUtils.deserializeObject(descriptorPath, false);

          // ����� ��������� ��������� ����������� - ��������� ��� ������� � ��� �������
          if ((ObjectType.TABLE.equals(tableDescriptor.getObjectType()))
               && (!useTableNameCheck || tableName.toUpperCase().equals(tableDescriptor.getObjectName())))
           {
            // ���������� �������� - ������������ �����, ������������� � ���
            if (!tableDescriptor.isEmpty())
             {
              ArrayList<String> tableFiles = tableDescriptor.getObjectItems();
              // ������� ���������� �����������/������������ ������
              int processedFilesCounter = 0;
              // ��������� �������� �� �������������� � ������ ������ ����� (������ ���������)
              if (monitor != null) {monitor.processMessage("[" + tableName + "] [0 / " + tableFiles.size() + "]");}

              // �������� �� ������ �������� ����������� � ��������� �� � ��
              for (String tableFile : tableFiles)
               {
                // ������ ��� �������� ������ ����� ������ ����� � ������� �������� �������� ������ ������� (�.�. �� ��
                // ������������� �� ��� �������������� ������� SRS). ��� ����, �.�. ������ ������������� (������ ����!)
                // �� ����������, �� ��� ������ �������� ��� ����������� ����� � ������ (��� ������ �������) ��������� ��
                // ����� �, �����., �� ������ ��������� "���" � ������!
                SerializableResultSet srs =
                 (SerializableResultSet)FSUtils.deserializeObject(FSUtils.fixFPath(path, true) + tableFile, false);
                // ���� ����������� SRS �� ���� � ��� ������� ��������� � ��������� - ��������� ��� (SRS) � ��
                if ((!srs.isEmpty()) && (!useTableNameCheck || tableName.toUpperCase().equals(srs.getTableName())))
                 {
                  logger.debug("Data ok in file: [" + (FSUtils.fixFPath(path, true) + tableFile) + "]. Rows: " + srs.getRowsCount());
                  DBasesLoaderCore.loadSrsToDB(conn, srs, tableName);
                  // ����� �������� �������� ������ �� ���������� ����� ��������� COMMIT - ������ ��������� � ��
                  conn.commit();
                 }
                // ����������� SRS ���� ��� ��� ������� � ��� �� ��������� � ��������� - ������������ ��!
                // �������������� ��������� - �����������.
                else
                 {
                  throw new JdbException("SRS in file [" + (FSUtils.fixFPath(path, true) + tableFile) +
                   "] is empty or invalid table name [" + srs.getTableName() + "] (must be: " + tableName + ")!");
                 }
                // �������� ������� ���-�� ������������ ������
                processedFilesCounter++;
                // ��������� �������� �� �������������� � ������ ������ �����
                if (monitor != null)
                 {monitor.processMessage("[" + tableName + "]  [" + processedFilesCounter + " / " + tableFiles.size() + "]");}
               } // END OF FOR
             }
            // ������ ���������� - ��� ������ ��������
            else {throw new JdbException("Empty table descriptor (no objects in list)!");}
           }
          // �������� ��� ������� ��� �������� ��� ������� (�� �������)
          else
           {throw new JdbException("Invalid object type [" + tableDescriptor.getObjectType() + "] (must be: " +
             ObjectType.TABLE + ") or table name [" + tableDescriptor.getObjectName() + "] (must be: " +
             tableName.toUpperCase() + ")!");}
         }
        // �������� ���� � ������ �������
        else {throw new JdbException("Path [" + path + "] is empty, not exists or not a directory!");}
       }
      // ������� ������ ��� �������
      else {throw new JdbException("Empty table name!");}
     }
    // ���������� � ���� �����
    else {throw new JdbException("Empty DBMS connection!");}
   }

  /**
   *
   * @param useIdentityInsert boolean �������� (������) ��� ��������� (����) ���������� ����������
   * SET IDENTITY_INSERT ... ON|OFF �����|����� �������� ��������� ������� � �����. ����� ����� ����� ������ ���
   * ���� MS SQL - ��������� ������ ����� ��� ������ ���� ������� ������ ���������� ������� ������.
  */
  protected static void loadDBFromDisk(Connection conn, String path, ArrayList<String> tablesList,
   DBProcessingMonitor monitor, boolean useIdentityInsert)
   throws JdbException, ClassNotFoundException, IOException, SQLException
   {
    logger.debug("DBasesLoaderCore.loadDBFromDisk().");
    // �������� ���������� � ����
    if (conn != null)
     {
      // �������� ��������, �� �������� ��������� �� (������ ������������, ���� ��������� � ���� ��������)
      if (!StringUtils.isBlank(path) && new File(path).exists() && new File(path).isDirectory() && !FSUtils.isEmptyDir(path))
       {
        // ��������� ������ ������ (������ ���� �� ����)
        if ((tablesList != null) && (!tablesList.isEmpty()))
         {
          // ������� - �� �������� ��������� ���������� ��. ���� �� ���������� - ���������� ��.
          String descriptorPath = FSUtils.fixFPath(path, true) + DESCRIPTOR_FILE_NAME + "." + EXTENSION_DB;
          // ������ ��� �������� ����������� �� �������� � ��������. �� �� ������������� �� ��� �������������� ������� SRS.
          Descriptor dbDescriptor = (Descriptor) FSUtils.deserializeObject(descriptorPath, false);

          // ����� ��������� ��������� ����������� - ��������� ��� �������. ��� �� � ������ ������ �� �����������, �.�.
          // ������ �������� ��������� � ����� �� - ���������� ���� ������� � ��� (� ��) �����. ������.
          if (ObjectType.DATABASE.equals(dbDescriptor.getObjectType()))
           {
            // ���������� �������� - ��������� �������� ������
            if (!dbDescriptor.isEmpty())
             {
              // ��������� ���������� �������� � �� - ��� ����� ������ ������ �� �������� ������ (������������ -
              // �������� ��������� �������� ���������� � ����!). ���� �������� �� - ��������� �����������, �.�.
              // ���� �������� autocommit=false �� ������� ����������, �� ��������� ����� ������� ����������.
              conn.setAutoCommit(false);

              // ������� ������������ ������
              int progressTablesCounter = 0;
              //
              double onePercent = 100/tablesList.size();
              //int progress;

              // �������� �� ���������� ������ ������ � ���� ��� ������� ���� ������ � ����������� � �������� �������
              // �� ����� - �������� ��������� ������� � ����� � ��
              for (String table : tablesList)
               {
                // ���� ��������� ��� ������� - ��������� ������� � �����
                if ((!StringUtils.isBlank(table) && dbDescriptor.containsItem(table)))
                 {
                  // ��������� �� �������������� ������� ������-��������
                  if (monitor != null) {monitor.processMessage("[" + table + "]");}

                  String tablePath = FSUtils.fixFPath(path, true) + table.toUpperCase();
                  if (new File(tablePath).exists() && new File(tablePath).isDirectory() && !FSUtils.isEmptyDir(tablePath))
                   {
                    // ����������� try-catch ��������� ��� ����, ����� ������ �������� ����� ������� �� ������� ��������
                    // ���� �� � �����. ����������� �� �� ����������� � ���� �� (JdbException) - ��� ����� ������ ����������
                    // �����.
                    try
                     {
                      // ���������� ���������� SET IDENTITY_INSERT ... � ���������� ��������� �������
                      if (useIdentityInsert)
                       {
                        // ��������� ������� � ���� � ���������������� ��������������� (������ ��� MS SQL 2005+)
                        conn.createStatement().executeUpdate("SET IDENTITY_INSERT " + table + " ON");
                        // ��������� ��� �������� �������, ���� ��������� ������ (������ ��� MS SQL 2005+)
                        conn.createStatement().executeUpdate("ALTER TABLE " + table + " DISABLE TRIGGER ALL");
                       }
                      // ��������������� �������� ������� � ����� � ��. �������� ���������� ���������� ����� ������� �
                      // ���������� � ����������� ������ �������.
                      DBasesLoaderCore.loadTableFromDisk(conn, tablePath, table, true, monitor);
                     }
                    // �������� �� ��� ����, ����� �� �������� �������� ��.
                    catch (JdbException e)           {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (ClassNotFoundException e) {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (IOException e)            {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    catch (SQLException e)           {logger.error("Error loading table [" + table + "]! Message: " + e.getMessage());}
                    // ���������� ���������� SET IDENTITY_INSERT [table] OFF
                    finally
                     {
                      // ���������� ���������� SET IDENTITY_INSERT ...
                      if (useIdentityInsert)
                       {
                        try
                         {
                          // ��������� ������� � ���� � ���������������� ��������������� (������ ��� MS SQL 2005+)
                          conn.createStatement().executeUpdate("SET IDENTITY_INSERT " + table + " OFF");
                          // �������� ��� �������� �������, ���� ��������� ������ (������ ��� MS SQL 2005+)
                          conn.createStatement().executeUpdate("ALTER TABLE " + table + " ENABLE TRIGGER ALL");
                         }
                        catch (SQLException e)
                         {logger.error("Can't execute query [SET IDENTITY_INSERT " + table + " OFF]! Reason: " + e.getMessage());}
                       }
                     }
                   }
                  // ���� �� ����������, �� �������� ��������� ��� ������� ����. ������ ������������ � ���,
                  // ��������� ���� �� �� �����������.
                  else {logger.error("Table [" + table + "] skipped! Reason: table path [" + tablePath +
                          "] not exists, not a directory or is empty!");}
                 }
                // ��� ������� �� ������ ����� ��� �� ���������� � ����������� ��. ������ ������������ � ���,
                // ��������� ���� �� �� �����������.
                else {logger.error("Table [" + table + "] skipped! Reason: table name is empty or not present in db descriptor!");}

                // ���������� ��������� ���������� ����� ��������� ��������� �������
                progressTablesCounter++;
                // ��������� �������� ��������� ��� ������-��������
                if (monitor != null) {monitor.processProgress((int) (progressTablesCounter*onePercent));}
               } // END OF FOR
              // �� ��������� ��������� ������ ��������� �������� ��������� � 100%
              if (monitor != null) {monitor.processProgress(100);}
             }
            // ������ ���������� - � ��� ��� ������ ��������
            else {throw new JdbException("Empty db descriptor (no objects in list)!");}
           }
          // �������� ��� �������
          else
           {throw new JdbException("Invalid object type [" + dbDescriptor.getObjectType() +
                                   "] (must be: " + ObjectType.DATABASE + ")!");}
         }
        // ��������� ������ ������ ��� �������� ����
        else {throw new JdbException("Empty tables list [" + tablesList + "]!");}
       }
      // �������� ���� � ������ �������
      else {throw new JdbException("Path [" + path + "] is empty, or not exists, or not a directory, or is empty directory!");}
     }
    // ���������� � ���� �����
    else {throw new JdbException("Empty DBMS connection!");}
   }

  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");

    try
     {
      DBConfig supidConfig         = new DBConfig("resources/ifxNormDocsConfig.xml");
      DBConfig supidAppMSSQLConfig = new DBConfig("resources/mssqlAppSupidConfig.xml");
      Connection supidConn         = DBUtils.getDBConn(supidConfig);
      Connection supidAppMSSQLConn = DBUtils.getDBConn(supidAppMSSQLConfig);

      // ������ ������
      ArrayList<String> tables = new ArrayList<String>(Arrays.asList(new String[] {"docTypes", "norm_docs", "norm_docs_parts",
       "changes_journal", "files"}));
      // �������� �� ����
      String basePath = "c:\\temp\\supid";
      new File(basePath).mkdirs();
      FSUtils.clearDir(basePath);
      DBasesLoaderCore.unloadDBToDisk(supidConn, basePath, "supid", tables, null, null);

      // �������� ������ � ��
      DBasesLoaderCore.loadDBFromDisk(supidAppMSSQLConn, basePath, tables, null, true);

     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    catch (JdbException e)            {logger.error(e.getMessage());}
    catch (ClassNotFoundException e)  {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
   }

 }