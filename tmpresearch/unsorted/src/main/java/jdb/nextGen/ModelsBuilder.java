package jdb.nextGen;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.nextGen.exceptions.JdbException;
import jdb.nextGen.models.SimpleDBIntegrityModel;
import jdb.nextGen.models.SimpleDBTimedModel;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * �����-����������� ������� ��. ����� ������������� � �� ��������������, �.�. �������� �����������.
 * @author Gusev Dmitry (����� �������)
 * @version 3.0 (DATE: 31.05.2011)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public final class ModelsBuilder
 {
  private static Logger logger = Logger.getLogger(ModelsBuilder.class.getName());

  // �������������� ������������ � ���������������
  private ModelsBuilder() {}

  /***/
  public static Timestamp getTimestampForTable(Connection conn, String tableName) throws JdbException, SQLException
   {
    Timestamp timestamp = null;
    if (conn != null)
     {
      if (!StringUtils.isBlank(tableName))
       {
        String    sql = "select max(" + DBConsts.FIELD_NAME_TIMESTAMP + ") from " + tableName;
        ResultSet rs  = null;
        try
         {
          rs = conn.createStatement().executeQuery(sql);
          if (rs.next()) {timestamp = rs.getTimestamp(1);}
         }
        finally {if (rs != null) {rs.close();}}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // ���������� ���������
    return timestamp;
   }

  /***/
  public static SimpleDBTimedModel getDBTimedModel(Connection conn, String dbName, ArrayList<String> tables)
   throws JdbException, SQLException
   {
    SimpleDBTimedModel model;
    if (conn != null)
     {
      if (!StringUtils.isBlank(dbName))
       {
        if ((tables != null) && (!tables.isEmpty()))
         {
          // ��������������� ��������� - ��� �������� ��������
          model          = new SimpleDBTimedModel(dbName);
          String    sql  = "select max(" + DBConsts.FIELD_NAME_TIMESTAMP + ") from ";
          Statement stmt = null;
          ResultSet rs   = null;
          try
           {
            stmt  = conn.createStatement();
            for (String table : tables)
             {
              if (!StringUtils.isBlank(table))
               {
                // ����������� try->catch - ��� ��������� �� (����� �� ���������� ���������).
                try
                 {
                  rs = stmt.executeQuery(sql + table);
                  // ���� ���� ��������� - ���������� ��� � ������� ������� � ������
                  if (rs.next()) {model.addTable(table, rs.getTimestamp(1));}
                  // ���� ��� ������ - ������� ����� ����������� � ������
                  else {model.addTable(table, null);}
                 }
                // �������� �� ����� ����� ��� ����, ����� ������� ��� ���������� ������ ����� ������� �������� ���� ����
                catch (SQLException e) {logger.error("Processing table: [" + table + "]. Message: " + e.getMessage());}
               }
             }
           }
          // ������������ ��������
          finally {if (rs != null) {rs.close();} if (stmt != null) {stmt.close();}}
         }
        else {throw new JdbException("Empty tables list!");}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // ���������� ���������
    return model;
   }

  /***/
  public static SimpleDBIntegrityModel getDBIntegrityModel(Connection conn, String dbName, ArrayList<String> tables)
   throws JdbException, SQLException
   {
    SimpleDBIntegrityModel model;
    if (conn != null)
     {
      if (!StringUtils.isBlank(dbName))
       {
        if ((tables != null) && (!tables.isEmpty()))
         {
          // ��������������� ��������� - ��� �������� ��������
          model          = new SimpleDBIntegrityModel(dbName);
          String    sql  = "select " + DBConsts.FIELD_NAME_KEY + " from ";
          Statement stmt = null;
          ResultSet rs   = null;
          try
           {
            stmt  = conn.createStatement();
            for (String table : tables)
             {
              if (!StringUtils.isBlank(table))
               {
                // ����������� try->catch - ��� ��������� �� (����� �� ���������� ���������).
                try
                 {
                  rs = stmt.executeQuery(sql + table);
                  // ���� ���� ��������� - ���������� ��� � ������� ������� � ������
                  if (rs.next())
                   {
                    ArrayList<Integer> keys = new ArrayList<Integer>();
                    do {keys.add(rs.getInt(1));} while(rs.next());
                    model.addTable(table, keys);
                    logger.debug("Integrity model for [" + table + "]. Keys count: " + keys.size());
                   }
                  // ���� ��� ������ - ����� ������� ����� ����������� � ������
                  else {model.addTable(table, null);}
                 }
                // �������� �� ����� ����� ��� ����, ����� ������� ��� ���������� ������ ����� ������� �������� ���� ����
                catch (SQLException e) {logger.error("Processing table: [" + table + "]. Message: " + e.getMessage());}
               }
             }
           }
          // ������������ ��������
          finally {if (rs != null) {rs.close();} if (stmt != null) {stmt.close();}}
         }
        else {throw new JdbException("Empty tables list!");}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // ���������� ���������
    return model;
   }

  /**
   * ����� ���������� �� �����, ������� ���� � ��������� ������, �� ��� � ����������. ���� ��������� ������ ���� - �����
   * ���������� NULL. ���� ���������� ������ ���� - ����� ���������� ��������� ������ ��������� (���� �� �� ����). ����
   * ����� ��� ������ - ����� ������ �������� NULL. ���� �� �� ����� ��� ������, �� ����� ������ ������ ������, �������
   * ���� � ��������� ������, �� ��� � ����������. �����, ������� ���� � ���������� ������, �� ��� � ��������� � ���������
   * �� ��������!
  */
  private static ArrayList<Integer> getIntegrityDifference(ArrayList<Integer> serverKeysList, ArrayList<Integer> clientKeysList)
   {
    ArrayList<Integer> list = null;
    if ((serverKeysList != null) && (!serverKeysList.isEmpty()))
     {
      if ((clientKeysList != null) && (!clientKeysList.isEmpty()))
       {
        // �������������
        list = new ArrayList<Integer>();
        // �������� �� ���������� ������
        for (Integer key : serverKeysList)
         {if ((key != null) && (!clientKeysList.contains(key))) {list.add(key);}}
        // ���� � ��������� ���� �� ��������� - �������� ���������
        if (list.isEmpty()) {list = null;}
       }
     }
    return list;
   }

  /**
   * ����� ���������� ������ ����������� ��, ������� ������� �� ������� ����� ��������� ������� � ����������. � ���������
   * �������� �� ������� �� ��������� (� ������ �� ���!) ������, � ������� ���� �����, ������������� � ���������������
   * ������� ������ ���������� ��. ���� ��������� ������ ����� - ����� ���������� �������� NULL. ���� ���������� ������
   * ����� - ����� ����� ���������� �������� NULL. �����! � ��������� ����� ������� ������ �� �������, ������� ���� �
   * � ��������� ������, � � ����������!
   * ��� �� ��� �������������� ������ ������� �� ��������� ������ ��.
  */
  public static SimpleDBIntegrityModel getDBIntegrityDifference(SimpleDBIntegrityModel serverModel,
   SimpleDBIntegrityModel clientModel) throws JdbException {
    SimpleDBIntegrityModel model = null;
    if ((serverModel != null) && (!serverModel.isEmpty()) && (clientModel != null) && (!clientModel.isEmpty()))
     {
      // �������� �� ������ ������ ��������� ������.
      for (String tableName : serverModel.getTablesList())
       {
        // ���� � ���������� ������ ���������� ����� �� ������� - ����������, ���� ��
        // ���, �� ������� ������ ������������
        if (clientModel.containsTable(tableName))
         {
          // �������� �������� � ������ ������
          ArrayList<Integer> keys = ModelsBuilder.getIntegrityDifference(serverModel.getKeysListForTable(tableName),
           clientModel.getKeysListForTable(tableName));
          // ���� �������� ����������, ��������� ���������
          if ((keys != null) && (!keys.isEmpty()))
           {
            // ������������� ������-����������
            if (model == null) {model = new SimpleDBIntegrityModel(serverModel.getDbName());}
            // ���������� ���������� � �������� ������ �������� ��
            model.addTable(tableName, keys);
           }
         }
       }
     }
    return model;
   }

  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[]{"jdb", "jlib", "org"});
    Logger logger = Logger.getLogger("jdb");

    // ������� ������� ����� - ��� ����������
    ArrayList<String> STORM_SYNC_TABLES_LIST = new ArrayList<String>(Arrays.asList
     (
      "KEEL_LAYING", "SHIP_LENGTH", "SHIP_DELIVERY", "SHIP_BUILD", "SHIP_DWT", "SHIP_LENGTH_MK", "SHIP_SIZE", "RULESET",
      "STRAN", "SHIP_TYPE_2_RULESET", "SHIP_TYPE", "SHIP_TYPE_2_FLEET", "SHIP_AGE", "SURVEY_ASPECT", "SURVEY_OCCASION",
      "SURVEY_TYPE", "SPECIALIZATION", "ITEMS", "ITEM_2_RULESET", "MISC_2_RULESET", "SHIP_MISC", "FLEET_2_MISC",
      "SURVEY_ACTION", "TOPIC", "TOPIC_PARENTS", "VERSION_CLIENT", "VERSION_CLIENT_CONTENT", "VERSION_MODULES"
     ));

    try
     {
      DBConfig config      = new DBConfig("jdb_java_module/dbConfigs/mysqlStormConfig.xml");
      DBConfig msSqlConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlProductionStormConfig.xml");
      Connection conn      = DBUtils.getDBConn(config);
      Connection msSqlConn = DBUtils.getDBConn(msSqlConfig);
      logger.info("started");
      logger.info("finished");
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    //catch (SQLException e)            {logger.error(e.getMessage());}
    //catch (JdbException e)            {logger.error(e.getMessage());}

   }

 }