package jdb.processing.sql.generation;

import jdb.DBConsts.DBType;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.model.structure.TableStructureModel;
import jdb.processing.DBEngineer;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * ������ ����� ��������� ������ ��������� sql-�������� ��� ��������� ����� ����.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 26.01.2009)
*/

public class SQLGenerator
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(SQLGenerator.class.getName());

  /**
   * ����� ������� SQL-������ ��� �������� ������� �� ���������� ���������� ������ TableStructureModel. ����
   * ��������� ��������� ����, �� ����� ���������� �������� null.
   * @param table TableStructureModel ������ �������, �� ������� ����� ������������ ������.
   * @param targetDBType DBType ��� ����, ��� ������� ��������� ������ ������. 
   * @param usePrimaryKey boolean ������ �������� ��������� - ������������ (true) ��� ��� (false) ����� ���
   * �������� ���������� �����. ���� ������� ����� true - ����� "��������� ����" ����� ������� � sql-���������
   * [CREATE TABLE...], ���� �� ������� ����� false, �� ������ "���������� �����" ����� ������ ���������� ������ ��
   * ��� �� �����, ������� ������ � ��������� ����.
   * @param addSemi boolean ��������� ��� ��� ����������� � ����� ������� sql-������� (����������� -> ;)
   * @return ArrayList[String] ������ ��������� ��������.
  */
  public static ArrayList<String> getCreateTableSQL(TableStructureModel table, DBType targetDBType,
                                                    boolean usePrimaryKey, boolean addSemi)
   {
    ArrayList<String> sql = null;
    if (table != null)
     {
      // ��������� sql-������� ��� �������� ������ �������
      String tableFieldsQuery = SQLUtils.getCreateTableFieldsSQL(table, targetDBType, usePrimaryKey, addSemi);
      // ���� ������ ��� �������� ������� �� ���� - �������� - ��������� ��� � ������ �
      // ���������� ������� ��� �������� (���� ���� ������ ��� ������� - ��� ������ ��������� �������).
      if ((tableFieldsQuery != null) && (!tableFieldsQuery.trim().equals("")))
       {
        sql = new ArrayList<String>();
        sql.add(tableFieldsQuery);
        // ��������� sql-������� ��� �������� ������ �������� ������ �������
        ArrayList<String> indexesQueries = SQLUtils.getCreateTableIndexesSQL(table, targetDBType, usePrimaryKey, addSemi);
        // ������� ����� �������� �������� � ��������� ���� �� ������ �� ����!
        if ((indexesQueries != null) && (!indexesQueries.isEmpty()))
         {for (String query : indexesQueries) {sql.add(query);}}
        else {logger.warn("Indexes query for table [" + table.getTableName() + "] is empty!");}
       }
      // ���� ������ ��� �������� ������� ���� - ��������� � ���
      else {logger.warn("Query [CREATE TABLE...] is empty for table [" + table.getTableName() + "]!");} 
     }
    return sql;
   }

  /**
   * ����� ������� sql-������ ��� �������� �� �� ������ DBStructureModel. ���� ���������� � �������� ���������
   * ������ ����� - ����� ���������� ��������� null.
   * @param dbName String ��� ��, ��� ������� ���������� ������ �� ��������. ������ �������� ������������, ������
   * ���� ���������� ��������� ������� ��� ����������������� �������� �� (CREATE DATABASE...). ���� ��������
   * ���� ����������� ������� ������ ��� ��� ����������� �� - �������� �� ����� � ���������� ������ �� - db. ����
   * �������� ���� - ������������ ��� �� �� ��������� ������.
   * @param db DBStructureModel ������-������� ��� ��������� sql-������� �������� ��.
   * @param targetDBType DBType ��� ����, ��� ������� ��������� ������ ������. ���� ������ �������� �� ������
   * (����� null), �� ��� ����, ��� ������� ��������� ������ ������� �� ��������� ������ �� (DBStructureModel). 
   * @param usePrimaryKey boolean ������ �������� ��������� - ������������ (true) ��� ��� (false) ����� ���
   * �������� ���������� �����. ���� ������� ����� true - ����� "��������� ����" ����� ������� � sql-���������
   * [CREATE TABLE...], ���� �� ������� ����� false, �� ������ "���������� �����" ����� ������ ���������� ������ ��
   * ��� �� �����, ������� ������ � ��������� ����.
   * @param createDB boolean ��������� (true) ��� ��� (false) ������ ��� �������� ����� �� (CREATE DATABASE...).
   * @param addSemi boolean ��������� ��� ��� ����������� � ����� ������� sql-������� (����������� -> ;)
   * @return String ��������������� sql-������ ��� �������� ��. 
  */
  public static ArrayList<String> getCreateDBSQL(String dbName, DBStructureModel db, DBType targetDBType,
                                                 boolean usePrimaryKey, boolean createDB, boolean addSemi)
   {
    ArrayList<String> sql = null;
    // ���� �� �� ����� - ��������
    if (db != null)
     {
      // �������� ������������ ��� �������� �� ���
      String localDBName;
      // ���� ������� ������ ��� �� - ���������� ��� ��� ��������, ���� �� ��� - ���������� ��� �� ������ ��
      if ((dbName != null) && (!dbName.trim().equals(""))) {localDBName = dbName;} else {localDBName = db.getDbName();}

      // �������������� ���������
      sql = new ArrayList<String>();
      
      // ���� ������ �����. ������ - ��������� ������ �� �������� ������ ��
      if (createDB) {sql.add("CREATE DATABASE " + localDBName + ";\n");}

      // ��� ���������� ������� ���������� ������������� �� �� ���������� (��������� ������� ������������� ������ ��).
      if (targetDBType != null)
       {
        switch (targetDBType)
         {
          case MYSQL:        sql.add("USE " + localDBName + ";\n"); break;
          case INFORMIX:     sql.add("DATABASE " + localDBName + ";\n"); break;
          case ODBC:         logger.warn("CAN'T ADD [USE DATABASE] COMMAND! UNKNOWN SYNTAX!"); break;
          case DBF:          logger.warn("CAN'T ADD [USE DATABASE] COMMAND! UNKNOWN SYNTAX!"); break;
          default:           logger.warn("UNKNOWN DBTYPE!"); break;
         }
       }
      // ���� ��� ���� �� ������ - �������� null, �� ������� �� �����������.
      else {logger.warn("Target DBMS type is NULL!");}

      // ���� ������ ������ �� ���� - �������� �� ���� � ���������� ������� ��� ������ �������
      TreeSet<TableStructureModel> tables = db.getTables();
      if ((tables != null) && (!tables.isEmpty()))
       {
        // �������� ��� ���� ����������
        DBType targetDB;
        if (targetDBType != null) {targetDB = targetDBType;} else {targetDB = db.getDbType();}
        for (TableStructureModel table : tables)
         {
          // �������� ������ ��������������� �������� ��� �������� ������� �������
          ArrayList<String> tableQueries = SQLGenerator.getCreateTableSQL(table, targetDB, usePrimaryKey, addSemi);
          // ���� ������ �� ���� - ��������� ��� � ����������
          if ((tableQueries != null) && (!tableQueries.isEmpty()))
           {for (String query : tableQueries) {sql.add(query);}}
         }
       }
     }
    return sql;
   }

  /**
   * ����� ���������� ������ sql-������ ��� ����������� ������� current � ���������� �� � ������� - �
   * ������� foreign.
   * @param current TableStructureModel ������� �������, ������� ����� �������� � ���������� ����.
   * @param currentDBType DBType ��� ����, � ������� ��������� ������� ������� current.
   * @param foreign TableStructureModel ������� �������, ��� ���������� ������� current � ������ ���.
   * @return String ��������������� sql-������.
  */
  public static String getAlterTableSQL(TableStructureModel current, DBType currentDBType, TableStructureModel foreign)
   {
    // �������������� ����� ��������
    StringBuilder sql = null;
    // �������� SQL-������ ��� ������ ����� �� ������� current
    String dropFieldsSQL   = SQLUtils.getDropFieldsSQL(current, foreign);
    // �������� SQL-������ ��� ���������� ����� � ������� current
    String addFieldsSQL    = SQLUtils.getAddFieldsSQL(current, currentDBType, foreign);
    // �������� SQL-������ ��� ��������� ����� � ������� current
    String changeFieldsSQL = SQLUtils.getChangeFieldsSQL(current, currentDBType, foreign);
    // ���� ������� �� ����� - ��������� �� � �������������� ����� ��������
    if (dropFieldsSQL   != null) {sql = new StringBuilder(); sql.append(dropFieldsSQL);}
    if (addFieldsSQL    != null) {if (sql == null) sql = new StringBuilder(); sql.append(addFieldsSQL);}
    if (changeFieldsSQL != null) {if (sql == null) sql = new StringBuilder(); sql.append(changeFieldsSQL);}
    // ��������� ���������
    String result;
    if (sql == null) {result = null;} else {result = sql.toString();}
    return result;
   }

  /**
   * ����� ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    
    try
     {
      DBConfig ifxConfig = new DBConfig();
      ifxConfig.setDbType(DBType.INFORMIX);
      ifxConfig.setHost("appserver:1526");
      ifxConfig.setServerName("hercules");
      ifxConfig.setDbName("storm");
      ifxConfig.setUser("informix");
      ifxConfig.setPassword("ifx_dba_019");

      // ����� ������ ������ �� ����������
      DBEngineer ifxEngineer = new DBEngineer(ifxConfig);
      DBStructureModel ifxModel = ifxEngineer.getDBStructureModel();
      //ArrayList<String> createSQL = SQLGenerator.getCreateDBSQL(null, ifxModel, DBType.DERBY_EMBEDD, false, false, false);
      //logger.info("->\n" + createSQL);

      // �������� ������� ����������� �� � �����
      DBConfig derbyConfig = new DBConfig();
      derbyConfig.loadFromFile("derbyConfig.xml");
      //DBEngineer derbyEngineer = new DBEngineer(derbyConfig);
      //SingleThreadSqlBatchExecutor.execute(derbyConfig, createSQL, true);
     }

    catch (SQLException e) {logger.error(e.getMessage());}
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e) {logger.error(e.getMessage());}
    catch (DBModelException e) {logger.error(e.getMessage());}
   }

 }