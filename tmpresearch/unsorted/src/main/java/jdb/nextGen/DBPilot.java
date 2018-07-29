package jdb.nextGen;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ������ �������� ����������� �����������/��������� ������� ��� ������ � ���������� ������ ����. ����� ��������
 * ��������� - �� �����������. ����� ������ ������� ��������� ������� ������.
 * @author Gusev Dmitry (����� �������)
 * @version 2.0 (DATE: 31.05.2011)
*/

public final class DBPilot
 {
  //
  private static Logger logger = Logger.getLogger(DBPilot.class.getName());

  // �������������� ������������ � ���������������
  private DBPilot() {}

  /**
   * ����� ���������� �������� ������/���� � ����������� �� ����, ��������� �� ������� � ��������� ������ (tableName) �
   * ��, �� ������� ��������� ���������� (conn). ���� ��� ������� �/��� ���������� ����� - ����� ���������� ��
   * (SQLException). ���� ���������� ��������� �� �� ���������� ��, � � ����� �� ������, ����� ������ ���� (�.�.
   * ���������� ������ ��������� �� ���������� �� �� �������). ����� ������� �� ����� �������������� ��� ����� ���� ��
   * (�.�. ������� � ��������� ������ [table] ����� �������, � ������� � ��������� ������ [schema].[table] ������� �� �����).
   * ��� ���� ����������� ������ - ����� ������� �������������� ��� ����� �������� ��������, �.�. ��������� ����� ������
   * ������������: table1 � TabLE1 (����� ������ ����������� � ������� ������� �������� ��� ���������).
   * @param conn Connection
   * @param tableName String
   * @return boolean
   * @throws SQLException �� - ������ ����������, ������ ��� �������, ������ ������ � ��.
  */
  public static boolean isTableExists(Connection conn, String tableName) throws SQLException
   {
    logger.debug("DBPilot: isTableExists().");
    boolean result = false;
    // ��������� ���������� � ����
    if (conn != null)
     {
      // ��������� ��� �������
      if (!StringUtils.isBlank(tableName))
       {
        ResultSet tablesRS   = null;
        try
         {
          DatabaseMetaData metaData   = conn.getMetaData();
          // ������� ���� ������ ��������� �� � ���������� �� � ������.
          tablesRS = metaData.getTables(null, null, null, null);
          // ���� ������ ������ ������� - ��������� �� ����
          while (tablesRS.next() && !result)
           {
            if (tableName.toUpperCase().equals(tablesRS.getString(DBConsts.META_DATA_TABLE_NAME).toUpperCase()))
             {result = true;}
           }
         }
        // ������� �������� �������
        finally
         {
         try {if (tablesRS != null) {tablesRS.close();}}
         catch (SQLException e) {logger.error("Can't free resources! Reason [" + e.getMessage() + "].");}
         }
       }
      // ������ ��� �������
      else {throw new SQLException("Empty table name!");}
     }
    // ���������� ����� - ������
    else {throw new SQLException("Empty connection!");}
    // ���������� ���������
    return result;
   }

  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    try
     {
      DBConfig mssqlStormTestConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlAppStormConfig.xml");
      DBConfig mysqlStormConfig     = new DBConfig("jdb_java_module/dbConfigs/mysqlStormConfig.xml");
      Connection mssqlStormTestConn = DBUtils.getDBConn(mssqlStormTestConfig);
      Connection mysqlStormConn = DBUtils.getDBConn(mysqlStormConfig);
      logger.info(DBPilot.isTableExists(mysqlStormConn, "aaa"));
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
   }

 }