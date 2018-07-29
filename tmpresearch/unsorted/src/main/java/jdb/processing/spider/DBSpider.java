package jdb.processing.spider;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.TableModel;
import jdb.model.TypeMapping;
import jdb.processing.DBCommonProcessor;
import jdb.processing.sql.execution.SqlExecutor;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ������ ������ ��������� ������ ������� ��������� ����� ����. ����� ������� ��������� �� �������� �������� ���������
 * ������� ������ �� - ��. ������ � ������ jlib.db.model. ����� ����� ��������� ������� � ���� Informix ����������
 * ��������, ������� �������� �� ������ ���� �������� ������������� (�������������� � �� ������ ���� �������� �
 * ������ ������ ��).
 * @author Gusev Dmitry (019gus)
 * @version 10.0 (DATE: 27.07.2010)
*/

public class DBSpider extends DBCommonProcessor
 {
  /** ���������-������ ������� ������. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * ����������� �� ���������. �������������� ������� ������������. ����� ����������� �������������� �����������
   * ���� ������ (� ���������, ������ ��������� ������ � ���������� �������� ���� Informix).
   * @param config DBConfig ������������ ������.
   * @throws DBModuleConfigException �� ���������, ���� ������������ �������� ������ ������������.
  */
  public DBSpider(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   * ����� �������� �� ���� ���������� � ��������� ����� ������ DBMS -> JAVA � ��������� ������ ���� ���������,
   * ������� ����������. ���� ��������� �� ������� (��� ����) ��� �������� �����-���� �������� - �����
   * ���������� �������� null.
   * @return ArrayList<TypeMapping> ������ ��������� ��������� ����� ������ DBMS -> JAVA ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<TypeMapping> getTypesMappings() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getTypesMapping(): getting types mapping [DBMS->JAVA] for current DBMS.");

    // ���� ������������ ������ �������� - ���������� ��!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // �������������� ������ ���������
    ArrayList<TypeMapping> mappings = null;
    Connection       connection = null;
    ResultSet        typesRS    = null;
    DatabaseMetaData metaData;
    try
     {
      // ���������� � ���� � ��������� ���������� � ��
      connection = DBUtils.getDBConn(getConfig());
      metaData   = connection.getMetaData();
      typesRS    = metaData.getTypeInfo();
      // ������� ��� ��������� ����� ������ ���� � ����� ������ JAVA
      if (typesRS.next())
       {
        mappings = new ArrayList<TypeMapping>();
        do
         {
          TypeMapping typeMapping = new TypeMapping(typesRS.getString(DBConsts.META_DATA_COLUMN_TYPE_NAME),
                                                    typesRS.getInt(DBConsts.META_DATA_COLUMN_DATA_TYPE),
                                                    typesRS.getInt(DBConsts.META_DATA_COLUMN_PRECISION));
          mappings.add(typeMapping);
         }
        while (typesRS.next());
       }
     }
    // ������� ���������� � ���� � ������� ������
    finally {if (typesRS != null) typesRS.close(); if (connection != null) connection.close();}
    return mappings;
   }

  /**
   * ����� ���������� ������(ArrayList[TableModel]) ������ ������ �� (� ������� ������������) ��� ��������� � ��������
   * ��������� ��. ���� �� ������� - ����� ���������� null. ��� ����� ���� DBF � ODBC ����� �� ����������� �� ����
   * ���������� - ����� ��������� �� SQLException().
   * @param dbName String ��, ��� ������� ���������� ������� ������ ������. ���� ������� - ������������ ��� ���, ���� ��
   * �������, �� ������������ ��� �� �� ������� ����������. ���� �� � � ������� ���������� �� ������� ��� - ���������
   * ������ - ��������� ��� ����� �� �������� ������ ������.
   * @param tableTypes TableType[] ������ ����� ������, ������� ���������� ��������. �������� ��� ������ - ��
   * ������-������������ DBConsts.TableType.
   * @return ArrayList[TableModel] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<TableModel> getUserTablesList(String dbName, DBConsts.TableType[] tableTypes)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ������������ ������ �������� - ���������� ��!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

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
    logger.debug("getUserTablesList(): creating list of tables for DB [" + localDbName + "].");

    // ��������� ������������� ��, ����� ���, ��� ���-�� ������
    if (!this.isDBExists(localDbName)) {throw new SQLException("Database [" + localDbName + "] doesn't exists on current DBMS!");}
    else {logger.debug("Database [" + localDbName + "] exists on current DBMS! Processing.");}

    // ������������ � ����� ���������� ������
    String[] localTableTypes;
    if ((tableTypes != null) && (tableTypes.length > 0))
     {
      localTableTypes = new String[tableTypes.length];
      for (int i = 0; i < tableTypes.length; i++) {localTableTypes[i] = tableTypes[i].strValue();}
     }
    else {localTableTypes = null;}

    // �������������� ������ ������
    ArrayList<TableModel> list       = null;
    Connection            connection = null;
    ResultSet             tablesRS   = null;
    DatabaseMetaData      metaData;
    
    try
     {
      // ���������� � ���� � ��������� ���������� � ��
      connection = DBUtils.getDBConn(getConfig());

      // ��� ���� ��������� ���������� ������� ��, ���� �� ����� �������� ������ ������ �� ��������, �� �� �����������
      // (��������� � ������� ����������) ��� ���� �� � ������� ����������� �� ������� �����.
      if (this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX))
       {
        if (StringUtils.isBlank(this.getConfig().getDbName()) ||
            (!StringUtils.isBlank(this.getConfig().getDbName()) && 
             !this.getConfig().getDbName().equalsIgnoreCase(localDbName)))
         {SqlExecutor.executeUpdateQuery(connection, "database " + localDbName);}
       }
      
      // ��������������� ��������� ���������� � �������� ��������� ��
      metaData   = connection.getMetaData();
      tablesRS = metaData.getTables(localDbName, null, null, localTableTypes);

      // todo: ������!!! ������ ��� �������!!!
      //logger.info("\n\n" + DBUtils.getStringResultSet(tablesRS));
      //System.exit(0);

      // ���� ������ ������ ������� - ��������� �� ����
      if ((tablesRS != null) && tablesRS.next())
       {
        logger.debug("Tables list received and not empty. Processing.");
        // ������ ����� �� ������ ������
        do
         {
          String tableName   = tablesRS.getString(DBConsts.META_DATA_TABLE_NAME);
          String tableSchema = tablesRS.getString(DBConsts.META_DATA_TABLE_SCHEM);
          String tableType   = tablesRS.getString(DBConsts.META_DATA_TABLE_TYPE);

          // ��������� ��������� ���� ���������� ��� ������� �� �����
          if (!StringUtils.isBlank(tableName))
           {
            // ������ - ������ �� ������� � ��������� ������� ����� ����
            boolean isTableSystem = false;

            // ��������� ������� �� �������������� � ���������� �������� ���� ���������
            if (DBConsts.DBType.INFORMIX.equals(this.getConfig().getDbType()))
             {if (DBConsts.SYSCATALOG_INFORMIX.contains(tableName.toUpperCase())) {isTableSystem = true;}}
            // ��������� ������� �� �������������� � ���������� �������� ���� MS SQL
            else if (DBConsts.DBType.MSSQL_JTDS.equals(this.getConfig().getDbType()) ||
                     DBConsts.DBType.MSSQL_NATIVE.equals(this.getConfig().getDbType()))
             {
              // ��� ������-������� ��������� �������������� � ���������� �������� ������ ���� ������� ����� ��� �������
              if (!StringUtils.isBlank(tableSchema))
               {
                String fullTableName = tableSchema.toUpperCase() + "." + tableName.toUpperCase();
                // ��������� �������������� ������� � ������ ��������� � ��������� ����� ������� - ��������� ����� ��� ���.
                if (DBConsts.SYSCATALOG_MSSQL.contains(fullTableName) || "SYS".equals(tableSchema.toUpperCase()) ||
                    "INFORMATION_SCHEMA".equals(tableSchema.toUpperCase()))
                 {isTableSystem = true;}
               }
             }

            // ���� ������� �� ��������� - ��� ��, ��������� �� � ������
            if (!isTableSystem)
             {
              logger.debug("Table [" + tableName + "] not system. Trying to add to tables list.");
              // ���� ���������� �� �� ������ �������� � ��������� �������
              try
               {
                TableModel tableModel = new TableModel(tableName);
                tableModel.setTableSchema(tableSchema);
                tableModel.setTableType(tableType);
                // ������ ���������������� ������ � ��� ������, ���� ������� ���� ���� �������
                if (list == null) {list = new ArrayList<TableModel>();}
                list.add(tableModel);
               }
              catch (DBModelException e) {logger.error(e.getMessage());}
             }
            // ���� �� ������� ��������� ��������� - ������ ������� �� ����
            else {logger.debug("Table [" + tableName + "] is system table in DBMS [" + this.getConfig().getDbType().strValue() + "].");}
           }
          // ���� ���������� ��� ������� ����� - ������! :)
          else {logger.warn("Empty table name!");}
         }
        while (tablesRS.next());
       }
      // ���� �� ������ ������ ���� - ������� �� ���� � ���
      else {logger.warn("Received tables list is empty or NULL!");}

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

     }
    // ������� ���������� � ���� � ������� �������
    finally {if (tablesRS != null) {tablesRS.close();} if (connection != null) {connection.close();}}
    // ���������� ���������
    return list;
   }

  /**
   * ����� ���������� ������(ArrayList[TableModel]) ������ ������ �� (� ������� ������������) ��� ��������� � ��������
   * ��������� ��. ���� �� ������� - ����� ���������� null. ��� ����� ���� DBF � ODBC ����� �� ����������� �� ����
   * ���������� - ����� ��������� �� SQLException(). ����� ���������� ������� ���� �����.
   * @param dbName String ��, ��� ������� ���������� ������� ������ ������. ���� ������� - ������������ ��� ���, ���� ��
   * �������, �� ������������ ��� �� �� ������� ����������. ���� �� � � ������� ���������� �� ������� ��� - ���������
   * ������ - ��������� ��� ����� �� �������� ������ ������.
   * @return ArrayList[TableModel] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<TableModel> getUserTablesList(String dbName)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.getUserTablesList(dbName, null);}

  /**
   * ����� ���������� ������(ArrayList[TableModel]) ������ ������ ��. ���� �� ������� - ����� ���������� null. �����
   * ���������� ������� ���� �����. ��� �� ��� ������ ������ ������� �� ������� ���������� � ����, ���� ��� ��� ��� -
   * ��������� ������.
   * @return ArrayList[TableModel] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<TableModel> getUserTablesList() 
   throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.getUserTablesList(null);}

  /**
   * ����� ���������� ������� ������ (ArrayList[String]) ������ ��������� �� (� ������� ������������) ��� ��������� � ��������
   * ��������� ��. ���� �� ������� - ����� ���������� null. ��� ����� ���� DBF � ODBC ����� �� ����������� �� ����
   * ���������� - ����� ��������� �� SQLException().
   * @param dbName String ��, ��� ������� ���������� ������� ������ ������. ���� ������� - ������������ ��� ���, ���� ��
   * �������, �� ������������ ��� �� �� ������� ����������. ���� �� � � ������� ���������� �� ������� ��� - ���������
   * ������ - ��������� ��� ����� �� �������� ������ ������.
   * @param tableTypes TableType[] ������ ����� ������, ������� ���������� ��������. �������� ��� ������ - ��
   * ������-������������ DBConsts.TableType.
   * @param useSchemaPrefix boolean ������������ (������) ��� ��� (����) ��� ����� ��� ������� ��� ����� ������� � ������.
   * @return ArrayList[String] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName, DBConsts.TableType[] tableTypes, boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {
    // ��������� ������ ������
    ArrayList<String> list = null;
    // "�����������" ������ ������
    ArrayList<TableModel> extendedList = this.getUserTablesList(dbName, tableTypes);
    // ���� ���������� "�����������" ������ ������ �� ���� - ��������
    if ((extendedList != null) && (!extendedList.isEmpty()))
     {
      list = new ArrayList<String>();
      // �������� �� ������������ ������ � ��������� ������� ������
      for (TableModel table : extendedList)
       {
        // ���� ���������� ��� ����� ��� ������� � ����� ������� ��� ��� ����� �� ����� - ���������
        if (useSchemaPrefix && !StringUtils.isBlank(table.getTableSchema()))
         {list.add(table.getTableSchema() + "." + table.getTableName());}
        // ���� �� ��� ����� �� ���������� ��� ��� ����� - ������ ��������� ��� ������� � ������
        else
         {list.add(table.getTableName());}
       }
     }
    // ���� �� ����������� ������ ���� - ������ ������� � ���
    else {}
    return list;
   }

  /**
   * ����� ���������� ������� ������ (ArrayList[String]) ������ ��������� �� (� ������� ������������) ��� ��������� �
   * �������� ��������� ��. ���� �� ������� - ����� ���������� null. ��� ����� ���� DBF � ODBC ����� �� ����������� �� ����
   * ���������� - ����� ��������� �� SQLException(). ����� ���������� ������� ���� �����.
   * @param dbName String ��, ��� ������� ���������� ������� ������ ������. ���� ������� - ������������ ��� ���, ���� ��
   * �������, �� ������������ ��� �� �� ������� ����������. ���� �� � � ������� ���������� �� ������� ��� - ���������
   * ������ - ��������� ��� ����� �� �������� ������ ������.
   * @param useSchemaPrefix boolean ������������ (������) ��� ��� (����) ��� ����� ��� ������� ��� ����� ������� � ������.
   * @return ArrayList[String] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName, boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(dbName, null, useSchemaPrefix);}

  /**
   * ����� ���������� ������� ������ (ArrayList[String]) ������ ��������� �� (� ������� ������������) ��� ��������� � ��������
   * ��������� ��. ���� �� ������� - ����� ���������� null. ��� ����� ���� DBF � ODBC ����� �� ����������� �� ����
   * ���������� - ����� ��������� �� SQLException().����� ���������� ������� ���� �����.
   * �� ��������� ��� ����� ������� ��� ������� ����� ������� � ������ �� ������������.
   * @param dbName String ��, ��� ������� ���������� ������� ������ ������. ���� ������� - ������������ ��� ���, ���� ��
   * �������, �� ������������ ��� �� �� ������� ����������. ���� �� � � ������� ���������� �� ������� ��� - ���������
   * ������ - ��������� ��� ����� �� �������� ������ ������.
   * @return ArrayList[String] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getUserTablesPlainList(String dbName)
     throws DBConnectionException, DBModuleConfigException, SQLException
     {return this.getUserTablesPlainList(dbName, null, false);}

  /**
   * ����� ���������� ������� ������ (ArrayList[String]) ������ ��������� �� (� ������� ������������). ���� �� ������� -
   * ����� ���������� null. ����� ���������� ������� ���� �����. ��� �� ��� ������ ������ ������� �� ������� ���������� �
   * ����, ���� ��� ��� ��� - ��������� ������.
   * @param useSchemaPrefix boolean ������������ (������) ��� ��� (����) ��� ����� ��� ������� ��� ����� ������� � ������.
   * @return ArrayList[String] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getUserTablesPlainList(boolean useSchemaPrefix)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(null, null, useSchemaPrefix);}

  /**
   * ����� ���������� ������� ������ (ArrayList[String]) ������ ��������� ��. ���� �� ������� - ����� ���������� null.
   * ����� ���������� ������� ���� �����. ��� �� ��� ������ ������ ������� �� ������� ���������� � ����, ���� ��� ��� ��� -
   * ��������� ������. �� ��������� ��� ����� ������� ��� ������� ����� ������� � ������ �� ������������.
   * @return ArrayList[String] ������ ������ ������ �� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getUserTablesPlainList()
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return this.getUserTablesPlainList(null, null, false);}

  /**
   * ���������� ������ ��� ������ ������� ����, � �������� �� ����������. ������������ ��� ������ ��������� �
   * ������ � ������� �������� - ��� ����������� ������������ �������� � ��������� ������� ��-�� ������� �
   * ��������� - ��������/��������� �����. ���� ������ ��� ������ �������� �� �������, �� ����� ���������� �������� null.
   * @return ArrayList[String] ������ ��� ������ �������� ������� ��� �������� null.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getDBSList() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getDBSList(): creating list of DBs for current server.");

    // ���� ������������ ������ �������� - ���������� ��!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // �������������� ������ ��� ������ �������� �������
    ArrayList<String> list = null;

    // todo: ��������� ��������� ������ �� ��� ���������� ����
    
    // �������� �������� ������ ��� ������ �������� ������� ������ ���� ��� Informix, MySQL ��� MSSQL. ��� Dbf � ODBC
    // ������ ��� �������� ��� ������ - �.�. �� ����� ���� ��� ��������.
    if (
        this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX)     ||
        this.getConfig().getDbType().equals(DBConsts.DBType.MYSQL)        ||
        this.getConfig().getDbType().equals(DBConsts.DBType.MSSQL_JTDS)   ||
        this.getConfig().getDbType().equals(DBConsts.DBType.MSSQL_NATIVE) 
       )
     {
      logger.debug("DBType is INFORMIX or MYSQL. Processing DBs list.");
      Connection        connection = null;
      ResultSet         basesRS    = null;
      DatabaseMetaData  metaData;
      try
       {
        // ���������� � ���� � ��������� ���������� � ��
        connection = DBUtils.getDBConn(getConfig());
        metaData   = connection.getMetaData();
        // ������� ���� ��� ������ ���������� �������
        basesRS = metaData.getCatalogs();
        if (basesRS.next())
         {
          logger.debug("Databases list of current server is not empty! Processing.");
          // � ����� �������� �� ����� ������� � ��������� ������ ��� ������
          do
           {
            String baseName = basesRS.getString(DBConsts.META_DATA_TABLE_CAT);
            if (!StringUtils.isBlank(baseName))
             {
              // ���� ������ ��� �� ��������������� - �������������
              if (list == null) {list = new ArrayList<String>();}
              // ��������� �������� ��� �� � ������
              list.add(baseName.toUpperCase());
             }
           }
          while (basesRS.next());
         }
        else {logger.warn("Cannot create list of bases for current DBMS server [meta data is empty]! ");}
       }
      // ������� ���������� � ���� � ������� �������
      finally {if (basesRS != null) {basesRS.close();} if (connection != null) {connection.close();}}
     }
    // ���� ��� ���� Derby ��� Dbf - ���������� ������ ������ 
    else {logger.warn("DBMS type is Derby or Dbf - list of databases is empty!");}

    return list;
   }

  /**
   * ����� ��������� ������������� ��������� �� ��� ����������, ���������� � ������� ������� ������� ������.
   * @param dbName String ��� ����������� �� ������������� ��.
   * @return boolean ������/���� - ���������� ��� ��� ��������� �� � ������� ����.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����. 
  */
  public boolean isDBExists(String dbName) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("isDBExists: checking existence [" + dbName + "] database.");
    boolean result = false;
    // ��������, ������ ���� ���������� �������� �� ����
    if (!StringUtils.isBlank(dbName))
     {
      logger.debug("DB name is not empty. Processing.");
      // ���� ���� - Informix ��� MySQL - �������� ������ ��� ������ ���������� ������� (���� �� �������� � �
      // ��� ���� �����), � ���� ��� ���� - Derby ��� Dbf - ��������� ������������� ���������������� �������� �� �����.
      if (
          this.getConfig().getDbType().equals(DBConsts.DBType.INFORMIX)     ||
          this.getConfig().getDbType().equals(DBConsts.DBType.MYSQL)        ||
          this.getConfig().getDbType().equals(DBConsts.DBType.MSSQL_JTDS)   ||
          this.getConfig().getDbType().equals(DBConsts.DBType.MSSQL_NATIVE)
         )
       {
        logger.debug("DBType is INFORMIX or MYSQL.");
        ArrayList<String> dbsList = this.getDBSList();
        // ���� ������ �� ���� - ���������, ���������� �� ����� ��
        if ((dbsList != null) && (!dbsList.isEmpty()) && (dbsList.contains(dbName.toUpperCase()))) {result = true;}
        else {logger.warn("Databases list is empty or not contains DB [" + dbName + "].");}
       }
      // �������� ������������� �� ��� Derby � Dbf - �������� ������������� �������� �� �����. ������� �� �����
      // ����� ���� � ������ - ��� ������, ��� � �� ������ ��� ������.
      else
       {
        logger.debug("DBMS type is Derby or Dbf.");
        if (new File(dbName).exists() && new File(dbName).isDirectory()) {result = true;}
        else {logger.warn("Catalog [" + dbName + "] doesn't exists.");}
       }
     }
    else {logger.warn("DB name is EMPTY! Can't check existence.");}
    return result;
   }

  /**
   * ����� ���������� ������� ������(ArrayList<String>) ������ ������ ��. ���� �� ������� - ����� ���������� null.
   * � ����������� �� ��������� ignoreConstraints � �������������� ������ ������ �������/�� ������� �������, ��
   * �������-�����������: ��������� ������� ���� ��������� (������ ��� ����������� � ����), "�����������",
   * "�����������". ��� �������� ��������� ������ - ��� ����������� ������������, ��� �������� ���� - ��� �����������
   * ���������.
   * @param ignoreConstraints ������������ �� �����������(������ "�����������"/"�����������" ������, ���. �������
   * ����������) �������� ������ Spider ��� ���������� ������ ������ ������� ��. ���� ���������� ����������� (��������
   * ignoreConstraints = true), �� � ������ ������ �������� ��� ������� ������� ��.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @return ArrayList<String> ������ ������ ������ �� ��� �������� null.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public ArrayList<String> getTablesList(boolean ignoreConstraints) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("getTablesList(): creating list of tables for DB[" + this.getConfig().getDbName() + "].");
    // ���� ������������ ������ ����� - ������!
    if (!StringUtils.isBlank(DBUtils.getConfigErrors(this.getConfig())))
     {throw new DBModuleConfigException("DBMS connect config is empty!");}
    // ���� � ������������ �� ������� ��� �� - ������!
    if (StringUtils.isBlank(this.getConfig().getDbName())) {throw new DBModuleConfigException("Name of DB is empty!");}
    // �������������� ������ ������
    ArrayList<String> list       = null;
    Connection        connection = null;
    ResultSet         tablesRS   = null;
    DatabaseMetaData  metaData;
    try
     {
      // ���������� � ���� � ��������� ���������� � ��
      connection = DBUtils.getDBConn(getConfig());
      metaData   = connection.getMetaData();
      // ������� ���� ������ ��������� �� � ���������� �� � ������. ��� ���� ����� ������ ������ ����������
      // ������� ��-�������...
      tablesRS = metaData.getTables(null, null, "", null);
      // ���� ������ ������ ������� - ��������� �� ����
      if (tablesRS.next())
       {
        logger.debug("Tables list received. Processing.");
        // ������ ����� �� ������ ������
        do
         {
          String tableName = tablesRS.getString(DBConsts.META_DATA_TABLE_NAME);
          // ��������� ��������� ���� ���������� ��� ������� �� �����
          if (!StringUtils.isBlank(tableName))
           {
            // ���� �� ���������� ����������� (��������� ������� ����, ������ "�����������" � "�����������" ������),
            // �� �� ������ �� ���������� (���������, ��� ������� ������� ��� ��������)
            if (!ignoreConstraints && !getConfig().isTableAllowed(tableName))
             {logger.debug("This table [" + tableName + "] is in DBMS system catalog, deprecated or not allowed - skipping.");}
            // ���� ���������� ����������� ��� ������� ��� ������� ��� ���������� ������������ - ��������
            else
             {
              logger.debug("Table name OK. Processing table [" + tableName + "].");
              // ������ ���������������� ������ � ��� ������, ���� ������� ���� ���� �������
              if (list == null) {list = new ArrayList<String>();}
              // ��� ������� ����������� � ������ ������ � ������� �������� ��������
              list.add(tableName.toUpperCase());
             }
           }
          else {logger.warn("Empty table name!");}
         }
        while (tablesRS.next());
       }
      // ���� �� ������ ������ ���� - ������� �� ���� � ���
      else {logger.warn("Received tables list is empty!");}
     }
    // ������� ���������� � ���� � ������� �������
    finally {if (tablesRS != null) {tablesRS.close();} if (connection != null) {connection.close();}}
    // ���������� ���������
    return list;
   }

  /**
   * ����� ���������� �������� ������/����, � ����������� �� ����, ���������� �� � ������� �� ������� tableName.
   * �������� ignoreConstraints ���������, ����������� �� ��� ���� ������ ����������� ��� ������� ������ - ������
   * "�����������" � "�����������" ������ (true -> ����������� �� �����������, false -> ����������� �����������).
   * ���� ����������� �����������, �� ��� ������������� ������� � �� � ��� �� ����������� � ������ "�����������"
   * ����� ������ �������� ����, ����� ��� � ��� ������������� ������� � �� � ��� �� ���������� � ������ "�����������".
   * ���� ���������� ��� ������� ����� ("" ��� null), �� ����� ���������� �������� ����.
   * @param tableName String ��� ������� �������.
   * @param ignoreConstraints boolean ������������(true) ��� ���������(false) ����������� ������� ������.
   * @return boolean ������/���� � ����������� �� ������������� ������� � ������� ��.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public boolean isTableExists(String tableName, boolean ignoreConstraints)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("isTablesExists(): checking existence of table [" + tableName + "].");

    // ���� ������������ ������ �������� - ���������� ��!
    String dbConfigErrors = DBUtils.getConfigErrors(this.getConfig());
    if (!StringUtils.isBlank(dbConfigErrors)) {throw new DBModuleConfigException(dbConfigErrors);}

    // ������������ ���������
    boolean result = false;
    // �������� ������ ���� ��� ������� �� �����
    if (!StringUtils.isBlank(tableName))
     {
      // �������� ������ ������ ������� ��
      ArrayList<String> tablesList = this.getTablesList(ignoreConstraints);
      // ���� ���������� ������ ������ �� ���� - �������� �� ����
      if ((tablesList != null) && (!tablesList.isEmpty()))
       {
        // �������� �� ������ ������ � ���� ����
        for (String table : tablesList) {if (table.equals(tableName.toUpperCase())) {result = true;}}
       }
      // ���� �� ���������� ������ ������ ���� - ������� � ���
      else {logger.warn("Tables list is empty!");}
     }
    else {logger.warn("Empty table name!");}
    return result;
   }

  /**
   * ����� ���������� �������� ������/����, � ����������� �� ����, ���������� �� � ������� �� ������� tableName.
   * ������ ����� �� ��������� ����������� �������� ������ DBSpider - ������ "�����������" � "�����������" ������,
   * ��������� ������� ����������. ���� ���������� ��� ������� ����� ("" ��� null), �� ����� ���������� �������� ����.
   * @param tableName String ��� ������� �������.
   * @return boolean ������/���� � ����������� �� ������������� ������� � ������� ��.
   * @throws SQLException - ������ ��� ����������� � ���� ��� ��� ���������� ������� �� ��������� ����������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
  */
  public boolean isAbsTableExists(String tableName) throws SQLException, DBConnectionException, DBModuleConfigException
   {return this.isTableExists(tableName, true);}

  /***/
  public void test()
   {
    try
     {
      Connection connection = DBUtils.getDBConn(getConfig());
      DatabaseMetaData metaData   = connection.getMetaData();
      // ���� �� ��������
      ResultSet rs = metaData.getIndexInfo(null, null, "check_2_action", false, false);
      System.out.println("indexes: \n" + DBUtils.getStringResultSet(rs));
      ResultSet rs2 = metaData.getBestRowIdentifier(null, null, "check_2_action", 0, true);
      System.out.println("row identifiers: \n" + DBUtils.getStringResultSet(rs2));
      ResultSet rs3 = metaData.getExportedKeys(null, null, "check_2_action");
      System.out.println("exported key: \n" + DBUtils.getStringResultSet(rs3));
      ResultSet rs4 = metaData.getImportedKeys(null, null, "check_2_action");
      System.out.println("imported key: \n" + DBUtils.getStringResultSet(rs4));
      ResultSet rs5 = metaData.getPrimaryKeys(null, null, "check_2_action");
      System.out.println("primary key: \n" + DBUtils.getStringResultSet(rs5));
      ResultSet rs6 = metaData.getVersionColumns(null, null, "check_2_action");
      System.out.println("version columns: \n" + DBUtils.getStringResultSet(rs6));
     }
    catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}

   }

  /**
   * ����� ������������ ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger(DBSpider.class.getName());

//    ConnectionConfig mysqlConfig = new ConnectionConfig();
//    mysqlConfig.setDbConnectionType("direct");
//    mysqlConfig.setDbType("mysql");
//    mysqlConfig.setDbHost("appserver:3306");
//    mysqlConfig.setDbName("storm_test");
//    mysqlConfig.setDbUser("root");
//    mysqlConfig.setDbPassword("mysql");

//    ConnectionConfig mysqlClientConfig = new ConnectionConfig();
//    mysqlClientConfig.setDbConnectionType("direct");
//    mysqlClientConfig.setDbType("mysql");
//    mysqlClientConfig.setDbHost("appserver:3306");
//    mysqlClientConfig.setDbName("storm_client");
//    mysqlClientConfig.setDbUser("root");
//    mysqlClientConfig.setDbPassword("mysql");

//    ConnectionConfig ifxConfig = new ConnectionConfig();
//    ifxConfig.setDbConnectionType("direct");
//    ifxConfig.setDbType("informix");
//    ifxConfig.setDbHost("appserver:1526");
//    ifxConfig.setDbServerName("hercules");
//    ifxConfig.setDbName("storm_test");
//    ifxConfig.setDbUser("informix");
//    ifxConfig.setDbPassword("ifx_dba_019");
    
//    ConnectionConfig dbfConfig = new ConnectionConfig();
//    dbfConfig.setDbConnectionType("direct");
//    dbfConfig.setDbType("dbf");
//    dbfConfig.setDbName("q:/new/fleet");

    try
     {
      DBConfig derbyConfig = new DBConfig();
      derbyConfig.loadFromFile("derbyConfig.xml");

      DBConfig ifxConfig = new DBConfig();
      ifxConfig.loadFromFile("ifxNormDocsConfig.xml");
      
      DBSpider spider = new DBSpider(ifxConfig);
      //logger.debug("DBs list -> " + spider.getDBSList());
      //logger.debug("Tables list -> " + spider.getTablesList());

     }
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    //catch (SQLException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    //catch (DBConnectionException e) {logger.error(e.getMessage());}

   }

 }