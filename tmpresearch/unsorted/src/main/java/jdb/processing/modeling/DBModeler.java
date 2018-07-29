package jdb.processing.modeling;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.integrity.TableIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.FieldStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.model.structure.key.IndexedField;
import jdb.model.time.DBTimedModel;
import jdb.model.time.TableTimedModel;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.processing.sql.execution.SqlExecutor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * ������ ����� ��������� ��������� ������� ���������� ��������� ������� ��� ������ - ������
 * ���������, ������ � ��������� �������, ������ �����������, ������ ��� �������� ������. ������
 * ���������� ������� ���������� ���������������� � ��������������� ������ ������ DBSpider.
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 23.07.2010)
*/

public class DBModeler extends DBCommonProcessor
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  public DBModeler(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * ����� ���������� ������ (ResultSet), ���������� ��� ������ DatabaseMetaData.getColumns(),
   * � ������ �������� ���� FieldStructureModel - ����� ������� TableStructureModel. ������ ������������
   * ��� �������� � ������ tableName. ����� ������������� ������ �����������.
   * @param rs ResultSet �������������� ������ ������.
   * @return TreeSet<FieldStructureModel> ���������� ������.
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBModelException ������ ��� ������ � ������� ��
  */
  private TreeSet<FieldStructureModel> getStructureFieldsList(ResultSet rs) throws SQLException, DBModelException 
   {
    TreeSet<FieldStructureModel> result = null;
    // ���� ������ �� ���� � � ��� ���� ������ - ���������
    if ((rs != null) && (rs.next()))
     {
      result = new TreeSet<FieldStructureModel>();
      // ����������� ���� ������� ������� (���� ��� ����)
      do
       {
        // ������� ��������� ������ FielsStructureModel
        FieldStructureModel field = new FieldStructureModel(rs.getString(DBConsts.META_DATA_COLUMN_NAME),
         rs.getInt(DBConsts.META_DATA_COLUMN_DATA_TYPE), rs.getString(DBConsts.META_DATA_COLUMN_TYPE_NAME),
          rs.getInt(DBConsts.META_DATA_COLUMN_SIZE));
        // ����� �� ������ ���� ��������� �������� NULL
        if (rs.getInt(DBConsts.META_DATA_COLUMN_NULLABLE) == DatabaseMetaData.columnNoNulls) {field.setNullable(false);}
        else {field.setNullable(true);}
        // �������� �� ��������� ��� ������� ����
        String defaultValue = rs.getString(DBConsts.META_DATA_COLUMN_DEFAULT);
        if (defaultValue != null) field.setDefaultValue(defaultValue);
        // ��������� ���� � �������������� ������
        result.add(field);
       }
      while (rs.next());
      rs.close();
     }
    return result;
   }

  /**
   * ����� ���������� ������ (ResultSet), ���������� ��� ������ DatabaseMetaData.getIndexInfo(), � ������ ��������
   * ������� - ������ �������� ���� IndexedField. ����� ������������� ������ �����������. ����� ��� ������������
   * ������ ��������������� ����� ������������ ������ ������ ������, ���������� ��� ������
   * DatabaseMetaData.getPrimaryKeys(). ������ ������ ����� ��� ����, ����� �������� ��������������� ����,
   * �������� � ��������� ����.
   * ����� �������� ��������������� ��� public-������ ������� ������ - getDBStructureModel.
   * @param primaryKeysRS ResultSet ������ �� ������� �����, ������� ������ � ��������� ����.
   * @param indexesRS ResultSet ������ �� ������� ���� ��������������� ����� �������.
   * @return TreeSet<IndexedField> �������������� ������ ��������������� �����.
   * @throws SQLException ������ ��� ��������� �������.
   * @throws DBModelException ������ ��� ������ � ������� ��.
  */
  private TreeSet<IndexedField> getStructureIndexesList(ResultSet primaryKeysRS, ResultSet indexesRS) throws SQLException, DBModelException
   {
    TreeSet<IndexedField> result = null;
    // ���� ������ �� ������� �� ���� � � ��� ���� ������ - ���������. ������ �� ������� ����� ���������� �����
    // �� ��������� - �.�. ���� ��� �������� ������ - ��� � ���������� ����� � ���������.
    if ((indexesRS != null) && (indexesRS.next()))
     {
      // ��� ������ ���������� ������ ����� ���������� ����� - ���� ������ �� ����
      TreeSet<String> primaryKeysList = null;
      if (primaryKeysRS.next())
       {
        primaryKeysList = new TreeSet<String>();
        do
         {
          String keyColumn = primaryKeysRS.getString(DBConsts.META_DATA_COLUMN_NAME);
          if (!StringUtils.isBlank(keyColumn)) {primaryKeysList.add(keyColumn.toUpperCase());}
         }
        while (primaryKeysRS.next());
        primaryKeysRS.close();
       }

      // ������ ������������ ������ �� ������� ��������
      result = new TreeSet<IndexedField>();
      do
       {
        // �������� ��� ������� � ��� �������������� ����
        String indexName = indexesRS.getString(DBConsts.META_DATA_INDEX_NAME);
        String fieldName = indexesRS.getString(DBConsts.META_DATA_COLUMN_NAME);
        // ���� ���������� ����� �� ����� - ������� ������ � ������
        if (!StringUtils.isBlank(indexName) && !StringUtils.isBlank(fieldName))
         {
          // ������� ����� ������ � ������� ��� � ������
          IndexedField field = new IndexedField(indexName, fieldName);
          // ������� ������������ ������� � ��� ���
          //field.setType(tmpRS.getShort("TYPE"));
          field.setUnique(!indexesRS.getBoolean(DBConsts.META_DATA_NON_UNIQUE));
          // ������� - ������ �� ������ ���� � ��������� ����
          if ((primaryKeysList != null) && (primaryKeysList.contains(fieldName.toUpperCase()))) {field.setPrimaryKey(true);}
          // ��������������� ���������� �������������� ���� � ������
          result.add(field);
         }
       }
      while (indexesRS.next());
     }
    return result;
   }

  /**
   * ����� ��������� ������ �� ��������������� ������� ������������ ����������� ������� ������ � ���� � �������
   * ����������� ������ �� - ��������� ������ DBStructureModel. ���� �������� �� �� ������� ����� (�� �������� ������),
   * �� � ��������� ������ ����� ����� �����. �������� dbName ��������� ������� ��� �������� ������ �� ������ ��, ��������
   * �� ��, � ������� �� ������������. ���� �� �� ��������� � ����, � ������� �� ������������, ��������� ��.
   * @param dbName String ��� �� � ������ ����, ��� ������� ���������� ��������� ����������� ������.
   * @return DatabaseModel ��������� ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ��� ������ � ������� ��.
  */
  public DBStructureModel getDBStructureModel(String dbName)
   throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {
    // ���� ������������ ������ �������� ������ - ���������� ��!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // ��������� ������� ��������� ��. ���� ������� �� ��� ��������, �� ���������� ��, ���� �� ��� -
    // ���������� ��, ��������� � ������������ ����������. ���� �� ���, �� ��� ��� �� �� ������� - ��!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // ��� ����� ���� DBF � ODBC ������ ������������� �� ������ �� ��� �����������
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // ��� ������ ���� - ��� ��! ��������.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // ���� � ������ �� ��� � �������, �� ������� ���������� ���������
    logger.debug("getDBStructureModel(): creating structure model for DB [" + localDbName + "].");

    // ��������� ������ DBSpider - ��� ���������� ��������������� �����
    DBSpider spider = new DBSpider(this.getConfig());
    // ��������� ������������� ��, ����� ���, ��� ���-�� ������
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}

    // ������������ ������ - ������ ��������� ��
    DBStructureModel database = new DBStructureModel(localDbName);
    // ��������� ��� ����, � ������� �������� ������ ��
    database.setDbType(this.getConfig().getDbType());
    // ������ ������ ������� �� - �� ����� ��� �������� ���� ������
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);

    // ���� ������ ������ �������� ������� - ������������ ���
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      // ������ ���������� � ����
      Connection connection = null;
      try
       {
        // ���������� � ���� � ��������� ���������� � ��
        connection = DBUtils.getDBConn(this.getConfig());
        // ������ � ��������������� � ����
        DatabaseMetaData  metaData   = connection.getMetaData();

        // ��� ���� ��������� ���������� ������� ��, ���� �� ����� �������� ������ �� �� ��������, �� �� �����������
        // (��������� � ������� ����������) ��� ���� �� � ������� ����������� �� ������� �����.
        if (this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX))
         {
          if (StringUtils.isBlank(this.getConfig().getDbName()) ||
              (!StringUtils.isBlank(this.getConfig().getDbName()) &&
               !this.getConfig().getDbName().equalsIgnoreCase(localDbName)))
           {SqlExecutor.executeUpdateQuery(connection, "database " + localDbName);}
         }

        // ������� ���� ������ ��������� �� � ���������� �� � ������ ��
        for (TableModel table : tablesList)
         {
          // ���� ������� �� ���� � ��� ������� �� ����� - ������������ ��
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            logger.debug("Processing model for table [" + table.getTableName() + "].");
            // ����������� try->catch ��������� ����� ��� ��������� �� ��� �������� ����� ������ ��������� ������� ���
            // ��� �������� ����� ������� ��� ��� �������� ������� �������� �������. ���� �� ����� �� ������ ��������� �� -
            // ������� ������ ������� �� ������� � �������������� ������ ��.
            try
             {
              // �������� ����� ��� ������� ������ � �������
              String schemaName = null;
              if (!StringUtils.isBlank(table.getTableSchema())) {schemaName = table.getTableSchema();}
              // �������� ���� � �������
              ResultSet columnsRS     = metaData.getColumns(localDbName, schemaName, table.getTableName(), null);
              ResultSet indexesRS     = metaData.getIndexInfo(localDbName, schemaName, table.getTableName(), false, false);
              ResultSet primaryKeysRS = metaData.getPrimaryKeys(localDbName, schemaName, table.getTableName());
              // ������� ������ ������ ������� ��
              TableStructureModel tableModel = new TableStructureModel(table.getTableName());
              // ���������� � ����� � ���� ������� �������
              tableModel.setTableSchema(table.getTableSchema());
              tableModel.setTableType(table.getTableType());
              // ���������� � ����� ������� �������������� �������
              tableModel.setFields(this.getStructureFieldsList(columnsRS));
              // ���������� �� �������� � ��������� ������ ������� �������������� �������
              tableModel.setIndexes(this.getStructureIndexesList(primaryKeysRS, indexesRS));
              // ��������������� ��������� ������ ������� �� � ������ �� - ���� �� �������� �� ����� ��
              database.addTable(tableModel);
             }
            // �������� ��������� �� ��� ���������/���������� ������ �������
            catch (DBModelException e)
             {logger.error("Error while processing table [" + table.getTableName() + "]. Message: " + e.getMessage());}
           }
          // ���� �� ������� ���� ��� ��� ������� ����� - �������� � ���!
          else {logger.warn("NULL table in tables list or table name is empty!");}
         } // END OF FOR CYCLE

        // ����� ��� ���� ��������� ���������� ������� �� � �������� ��������� (���� �� ��������� �������� "DATABASE ...")
        if (this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX))
         {
          // ���� � ������� ���������� �� ������� �� - ������� ���������
          // todo: ����� ���� ���������� ��� "CLOSE DATABASE" - ����� ��� �������������?
          if (StringUtils.isBlank(this.getConfig().getDbName()))
           {SqlExecutor.executeUpdateQuery(connection, "close database");}
          // ���� �� � ������� ���������� ������� ��, �� ��� �� ��������� � ������������� - ������ �������� ��
          else if (!StringUtils.isBlank(this.getConfig().getDbName()) &&
                   !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
           {SqlExecutor.executeUpdateQuery(connection, "database " + this.getConfig().getDbName());}
         }
        
       } // END OF TRY
      // ������� ������� ���������� � ���� � ������� �������
      finally {if (connection != null) connection.close();}
     }
    // ���� �� ���������� ������ ������ ���� - ������� � ���
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // ���������� ���������
    return database;
   }

  /**
   * ����� ��������� ������ �� ��������������� ������� ������������ ����������� ������� ������ � ���� � �������
   * ����������� ������ �� - ��������� ������ DBStructureModel. ���� �������� �� �� ������� ����� (�� �������� ������),
   * �� � ��������� ������ ����� ����� �����. ��� �������� ������ ������������ ��, ��������� � ������������ �����������
   * � ����. ���� �� �� ��������� � ����, � ������� �� ������������, ��������� ��.
   * @return DatabaseModel ��������� ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ��� ������ � ������� ��.
  */
  public DBStructureModel getDBStructureModel() throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {return this.getDBStructureModel(null);}

  /**
   * ����� ������ ��� ������� �� ������ ��� �������� �� (��) �����������. ���� ������ ��������� �� ������� -
   * ����� ���������� �������� null. ���� �������� �� �� ������� ����� - ����� ������ ����� �� ������ (�� null, �� �
   * ������ ������� ������).
   * @param dbName String ��� �� � ������ ����, ��� ������� ���������� ��������� ����������� ������.
   * @return DBIntegrityModel ������ ������� �� ��� �������� null.
   * @throws SQLException ������ ��� ���������� sql-������� ��� ��� ��������� ��� �����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ��� ������ � ������� ��.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public DBIntegrityModel getDBIntegrityModel(String dbName)
   throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {
    // ���� ������������ ������ �������� ������ - ���������� ��!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // ��������� ������� ��������� ��. ���� ������� �� ��� ��������, �� ���������� ��, ���� �� ��� -
    // ���������� ��, ��������� � ������������ ����������. ���� �� ���, �� ��� ��� �� �� ������� - ��!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // ��� ����� ���� DBF � ODBC ������ ������������� �� ������ �� ��� �����������
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // ��� ������ ���� - ��� ��! ��������.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // ���� � ������ �� ��� � �������, �� ������� ���������� ���������
    logger.debug("getDBIntegrityModel(): creating integrity model for DB [" + localDbName + "].");

    // ��������� ������ DBSpider - ��� ���������� ��������������� �����
    DBSpider spider = new DBSpider(this.getConfig());
    // ��������� ������������� ��, ����� ���, ��� ���-�� ������
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}
    
    // ������ �� ��� �������� �� ����������� - ��������� ������ ������� ������
    DBIntegrityModel integrity  = new DBIntegrityModel(localDbName);
    // ��������� ��� ����, � ������� �������� ������ ��
    integrity.setDbType(this.getConfig().getDbType());
    // ������ ������ ������� �� - �� ����� ��� �������� ���� ������
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);
    
    // ���� ���������� ������ ������ �� �� ���� - ��������
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      Connection connection = null;
      Statement  stmt       = null;
      try
       {
        connection     = DBUtils.getDBConn(getConfig());
        stmt           = connection.createStatement();

        // ����� sql-������� ��� ������������ �� (��� ������ ����)
        String selectDBQuery = null;
        switch (this.getConfig().getDbType())
         {
          case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: selectDBQuery = "use "      + localDbName; break;
          case INFORMIX:                                  selectDBQuery = "database " + localDbName; break;
         }
        // ���� ���������� ������ �� ���� - ��������� ���
        if (!StringUtils.isBlank(selectDBQuery) &&
            (StringUtils.isBlank(this.getConfig().getDbName()) ||
             (!StringUtils.isBlank(this.getConfig().getDbName()) &&
              !this.getConfig().getDbName().equalsIgnoreCase(localDbName))))
         {SqlExecutor.executeUpdateQuery(connection, selectDBQuery);}

        // �������� �� ���� �������� �� � ������� ��� ������ ������� ������
        for (TableModel table : tablesList)
         {
          // ���� ������� �� ���� � ��� ������� �� ����� - ������������ ��
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            String fullTableName;
            if (!StringUtils.isBlank(table.getTableSchema()))
             {fullTableName = table.getTableSchema() + "." + table.getTableName();}
            else
             {fullTableName = table.getTableName();}

            logger.debug("Processing model for table [" + fullTableName + "].");
            ResultSet rs = null;
            // ����������� try->catch ��������� ����� ��� ��������� �� ��� �������� ����� ������ �������. ����
            // ��������� �� - ������� ������ ������� �� ������� � �������������� ������ ��.
            try
             {
              StringBuilder sql = new StringBuilder("select ").append(DBConsts.FIELD_NAME_KEY).append(" from ");
              sql.append(fullTableName);
              rs = stmt.executeQuery(sql.toString());
              // ���� � ������� ��� ������ (��������� ���� ��� �����) - ������� �� �������� � ������
              if (rs.next())
               {
                logger.debug("Keys list found for table [" + fullTableName + "].");
                // ������� ����� ������ ������� �����������
                TableIntegrityModel integrityTable = new TableIntegrityModel(table.getTableName());
                integrityTable.setTableSchema(table.getTableSchema());
                integrityTable.setTableType(table.getTableType());
                // � ����� ��������� ��� ����� �� ������� � ������
                do {integrityTable.addKey(rs.getInt(DBConsts.FIELD_NAME_KEY));} while (rs.next());
                // ��������������� ���������� ������� � ������
                integrity.addTable(integrityTable);
               }
              // �������� � ���, ��� ����� �� �����
              else {logger.debug("For table [" + fullTableName + "] keys list is not found!");}
             }
            // �������� ��, ����������� ��� ��������� ����� ������� - ����� ������� �� �������� ���� ����
            catch (SQLException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            catch (DBModelException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            // ����������� ����������� �������
            finally                           {if (rs != null) rs.close();}
           } 
          // ���� �� ������� ���� ��� ��� ������� ����� - �������� � ���!
          else {logger.warn("NULL table in tables list or table name is empty!");}
         } // END OF FOR CYCLE

        // ����� sql-������� ��� ������������ �� (��� ������ ����) - ������� �� � �������� ���������
        String returnDBQuery = null;
        // �������� ������ ������ ���� ��� �� � ������������ ���������� �� �����
        if (!StringUtils.isBlank(this.getConfig().getDbName()))
         {
          switch (this.getConfig().getDbType())
           {
            case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: returnDBQuery = "use "      + this.getConfig().getDbName(); break;
            case INFORMIX:                                  returnDBQuery = "database " + this.getConfig().getDbName(); break;
           }
         }
        // ���� ���������� ������ �� ���� - ��������� ��� (����������� �� �������)
        if (!StringUtils.isBlank(selectDBQuery) && !StringUtils.isBlank(this.getConfig().getDbName()) &&
            !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
         {SqlExecutor.executeUpdateQuery(connection, returnDBQuery);}

       }
      // ����������� �������
      finally {if (stmt != null) {stmt.close();} if (connection != null) {connection.close();}}
     }
    // ���� �� ���������� ������ ���� - ������� � ���!
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // ���������� ���������
    return integrity;
   }

  public DBIntegrityModel getDBIntegrityModel() throws SQLException, DBModuleConfigException, DBConnectionException, DBModelException
   {return this.getDBIntegrityModel(null);}
  
  /**
   * ����� ������� � ���������� ������ �� � ��������� ������� (����� DBTimedModel). ���� �������� �� (�� �������)
   * ����� - �.�. �� ����� ������, �� � ����������� ������ ����� ����� (��� ������).
   * @return DatabaseTimeModel ����������� ������ �� � ��������� ������� ��� �������� null.
   * @throws SQLException ������ ��� ���������� sql-������� ��� ��� ��������� ��� �����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ��� ������ � ������� ��.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public DBTimedModel getDBTimedModel(String dbName) 
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {
    // ���� ������������ ������ �������� ������ - ���������� ��!
    String configErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}

    // ��������� ������� ��������� ��. ���� ������� �� ��� ��������, �� ���������� ��, ���� �� ��� -
    // ���������� ��, ��������� � ������������ ����������. ���� �� ���, �� ��� ��� �� �� ������� - ��!
    String localDbName;
    if (!StringUtils.isBlank(dbName))
     {
      // ��� ����� ���� DBF � ODBC ������ ������������� �� ������ �� ��� �����������
      if ((DBConsts.DBType.DBF.equals(this.getConfig().getDbType()) ||
           DBConsts.DBType.ODBC.equals(this.getConfig().getDbType())) &&
           !dbName.equalsIgnoreCase(this.getConfig().getDbName()))
       {throw new SQLException("Can't change database [" + this.getConfig().getDbName() + "] -> [" +
                               dbName + "] for this DBMS type [" + this.getConfig().getDbType() + "]!");}
      // ��� ������ ���� - ��� ��! ��������.
      else {localDbName = dbName;}
     }
    else if (!StringUtils.isBlank(this.getConfig().getDbName())) {localDbName = this.getConfig().getDbName();}
    else {throw new SQLException("Database name not specified!");}
    // ���� � ������ �� ��� � �������, �� ������� ���������� ���������
    logger.debug("getDBTimedModel(): creating timed model for DB [" + localDbName + "].");

    // ��������� ������ DBSpider - ��� ���������� ��������������� �����
    DBSpider spider = new DBSpider(this.getConfig());
    // ��������� ������������� ��, ����� ���, ��� ���-�� ������
    if (!spider.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS!");}

    // ������ �� � ��������� ������� ���������� ������ - ������� ���������
    DBTimedModel dbTimedModel = new DBTimedModel(this.getConfig().getDbName());
    // ��������� ��� ����, � ������� �������� ������ ��
    dbTimedModel.setDbType(this.getConfig().getDbType());
    // ������ ������ ������� �� - �� ����� ��� �������� ���� ������
    ArrayList<TableModel> tablesList = spider.getUserTablesList(localDbName);

    // ���� ���������� ������ ������ �� �� ���� - ��������
    if ((tablesList != null) && (!tablesList.isEmpty()))
     {
      logger.debug("Received tables list is not empty [" + tablesList.size() + " table(s)]. Processing.");
      Connection        connection   = null;
      Statement         stmt         = null;
      try
       {
        connection = DBUtils.getDBConn(getConfig());
        stmt       = connection.createStatement();

        // ����� sql-������� ��� ������������ �� (��� ������ ����)
        String selectDBQuery = null;
        switch (this.getConfig().getDbType())
         {
          case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: selectDBQuery = "use "      + localDbName; break;
          case INFORMIX:                                  selectDBQuery = "database " + localDbName; break;
         }
        // ���� ���������� ������ �� ���� - ��������� ���
        if (!StringUtils.isBlank(selectDBQuery) &&
            (StringUtils.isBlank(this.getConfig().getDbName()) ||
             (!StringUtils.isBlank(this.getConfig().getDbName()) &&
              !this.getConfig().getDbName().equalsIgnoreCase(localDbName))))
         {SqlExecutor.executeUpdateQuery(connection, selectDBQuery);}

        // ������� ������ ���� ������ ������� ��
        for (TableModel table : tablesList)
         {
          // ���� ������� �� ���� � ��� ������� �� ����� - ������������ ��
          if ((table != null) && !StringUtils.isBlank(table.getTableName()))
           {
            // ��������� ������ ��� ������� (� ��������� � ��������� ����� ��� ��������� ����)
            String fullTableName;
            if (!StringUtils.isBlank(table.getTableSchema()))
             {fullTableName = table.getTableSchema() + "." + table.getTableName();}
            else
             {fullTableName = table.getTableName();}
            logger.debug("Processing model for table [" + fullTableName + "].");

            ResultSet rs = null;
            // ����������� try->catch ��������� ����� ��� ��������� �� ��� �������� ����� ������ �������. ����
            // ��������� �� - ������� ������ ������� �� ������� � �������������� ������ ��.
            try
             {
              // ����������� �� ������� ��������� (timestamp)
              StringBuilder sql = new StringBuilder("select max(").append(DBConsts.FIELD_NAME_TIMESTAMP);
              sql.append(") from ").append(fullTableName);
              rs = stmt.executeQuery(sql.toString());
              // ���� ���� ��������� - ���������� ���
              if (rs.next())
               {
                logger.debug("MAX(TIMESTAMP) found for table [" + fullTableName + "].");
                TableTimedModel timedTable = new TableTimedModel(table.getTableName(), rs.getTimestamp(1));
                timedTable.setTableSchema(table.getTableSchema());
                timedTable.setTableType(table.getTableType());
                dbTimedModel.addTable(timedTable);
               }
              // ���� ��� ������ - ������ ������� ������ �����... ����� ������� �� ����������� � �������������� ������.
              else {logger.warn("Table [" + fullTableName + "] doesn't have [" + DBConsts.FIELD_NAME_TIMESTAMP + "] field!");}
             } // END OF TRY
            // �������� �� ����� ����� ��� ����, ����� ������� ��� ���������� ������ ����� ������� �������� ���� ����
            catch (SQLException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            catch (DBModelException e)
             {logger.error("Error while processing table [" + fullTableName + "]. Message: " + e.getMessage());}
            // ��������� ������ - ��������� ������� � �������!
            finally                {if (rs != null) rs.close();}
           }
          // ���� �� ������� ���� ��� ��� ������� ����� - �������� � ���!
          else {logger.warn("NULL table in tables list or table name is empty!");} 
         } // END FOR CYCLE

        // ����� sql-������� ��� ������������ �� (��� ������ ����) - ������� �� � �������� ���������
        String returnDBQuery = null;
        // �������� ������ ������ ���� ��� �� � ������������ ���������� �� �����
        if (!StringUtils.isBlank(this.getConfig().getDbName()))
         {
          switch (this.getConfig().getDbType())
           {
            case MSSQL_JTDS: case MSSQL_NATIVE: case MYSQL: returnDBQuery = "use "      + this.getConfig().getDbName(); break;
            case INFORMIX:                                  returnDBQuery = "database " + this.getConfig().getDbName(); break;
           }
         }
        // ���� ���������� ������ �� ���� - ��������� ��� (����������� �� �������)
        if (!StringUtils.isBlank(selectDBQuery) && !StringUtils.isBlank(this.getConfig().getDbName()) &&
            !this.getConfig().getDbName().equalsIgnoreCase(localDbName))
         {SqlExecutor.executeUpdateQuery(connection, returnDBQuery);}

       }
      // � ����� ������ �������� ������� �������� ���������� � �������
      finally {if (stmt != null) stmt.close(); if (connection != null) connection.close();}
     }
    // ���� �� ���������� ������ ���� - ������� � ���!
    else {logger.warn("Tables list for this database [" + localDbName + "] is empty!");}
    // ���������� ���������
    return dbTimedModel;
   }

  public DBTimedModel getDBTimedModel()
   throws SQLException, DBConnectionException, DBModuleConfigException, DBModelException
   {return this.getDBTimedModel(null);}

  /**
   * ����� �������� - ��������� � ��������. ������������ ��� ������������ ���� ����������.
   * @param current DBStructureModel
   * @param foreign DBStructureModel
   * @return String
  */
  public String getDifferenceReport(DBStructureModel current, DBStructureModel foreign)
   {
    // �������������� �����
    StringBuilder report;
    // ���� ���������� ������ ��� ������ �� ����� - ������������ ��
    if ((current != null) && (foreign != null))
     {
      report = new StringBuilder("\nDIFFERENCE REPORT \n");
      // ������� ������� �� (������)
      report.append("DB [").append(current.getDbName()).append("] (").append(current.getDbType()).append(") current: ");
      if ((current.getTables() != null) && (!current.getTables().isEmpty()))
       {report.append("(").append(current.getTables().size()).append(") ").append(current.getCSVTablesList());}
      else {report.append("table list is empty.");}
      report.append("\n");
      // ������� ������� �� (������)
      report.append("DB [").append(foreign.getDbName()).append("] (").append(foreign.getDbType()).append(") foreign: ");
      if ((foreign.getTables() != null) && (!foreign.getTables().isEmpty()))
       {report.append("(").append(foreign.getTables().size()).append(") ").append(foreign.getCSVTablesList());}
      else {report.append("table list is empty.");}
      report.append("\n");

      // ���� �� �� ��������� ��� ������� ��������� - ����� ���������� ����� �� ������� ����� ����
      if (!current.equals(foreign))
       {
        // ������ ���� �������� ������ ������ - ����� ������������� �������. ������� �������� ��
        // �������� ������� �� � ���������� �� � ��������� ������� ��.
        report.append("\n***** COMPARE [").append(current.getDbName()).append("] (").append(current.getDbType());
        report.append(") (current) -> [").append(foreign.getDbName()).append("] (").append(foreign.getDbType());
        report.append(") (foreing)\n");
        int existsCounter = 0;
        int equalsCounter = 0;
        for (TableStructureModel table : current.getTables())
         {
          // �������� ������� � ����� �� ������ �� ������ ������ ������� ��
          TableStructureModel foreignTable = foreign.getTable(table.getTableName());
          // ���� ����� ������� ����������� �� ������� �� - � �����
          if (foreignTable == null)
           {
            report.append("--> [exists] TABLE [").append(table.getTableName()).append("] DOESN'T EXISTS IN FOREIGN DB.\n");
            existsCounter++;
           }
          // ���� �� ������� ���������� - ������� ������� (��� ������������ - � �����).
          else if (!foreignTable.equals(table))
           {
            report.append("\n--> [equals] TABLE [").append(table.getTableName()).append("] DOESN'T EQUAL IN DBs.");
            report.append(table.getDifferenceReport(foreignTable));
            equalsCounter++;
           }
         }
        // ���� � ���������� ����������� ������
        report.append("***** Not exists in foreign DB: ").append(existsCounter).append("; not equals: ");
        report.append(equalsCounter).append("\n");

        // ������ �������� �� �������� ������� �� � ���������� �� � ��������� ������� ��.
        report.append("\n***** COMPARE [").append(foreign.getDbName()).append("] (").append(foreign.getDbType());
        report.append(") (foreign) -> [").append(current.getDbName()).append("] (").append(current.getDbType());
        report.append(") (current)\n");
        // �������� ��������
        int existsCounterBack = 0;
        int equalsCounterBack = 0;
        for (TableStructureModel table : foreign.getTables())
         {
          // �������� ������� � ��� �� ������ �� ������ ������ ������� ��
          TableStructureModel currentTable = current.getTable(table.getTableName());
          // ���� ����� ������� ����������� � ������� �� - � �����
          if (currentTable == null)
           {
            report.append("--> [exists] TABLE [").append(table.getTableName()).append("] DOESN'T EXISTS IN CURRENT DB.\n");
            existsCounterBack++;
           }
          // ���� �� ������� ���������� - ������� ������� (��� ������������ - � �����).
          else if (!currentTable.equals(table))
           {
            report.append("\n--> [equals] TABLE [").append(table.getTableName()).append("] DOESN'T EQUAL IN DBs.");
            report.append(table.getDifferenceReport(currentTable));
            equalsCounterBack++;
           }
         }
        // ���� � ���������� ����������� ������
        report.append("***** Not exists in current DB: ").append(existsCounterBack).append("; not equals: ");
        report.append(equalsCounterBack).append("\n");

       }

      // ���� ��� ��������� �� ��������� ������� - ����� �� ������������
      else {report.append("DATABASES ARE EQUAL!");}
     }
    // ���� ���������� �������� ���� - ������ �� ���� � ������
    else {report = new StringBuilder("Current and/or foreign database object is NULL!");}

    return report.toString();
   }

 }