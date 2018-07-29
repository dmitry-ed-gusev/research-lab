package jdb.processing.loading;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.monitoring.DBTestMonitor;
import jdb.processing.loading.core.DBLoadCore;
import jdb.processing.loading.core.DBUnloadCore;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ������ ����� ��������� ������ �������� �� �� ���� � �������� �� � �����. ����� �������� "�������" (����������
 * ������� "�����") ��� ������� ��������/��������. ��������/�������� �� ������������� ������������ ������ � �������
 * ������� ������, � �� �������� � ������� ������� DBUnloadCore/DBLoadCore.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 20.05.2010)
 *
 * @deprecated ������ ����� �� ������������� ������������, �.�. ��� ��������/�������� �� �� ���� ������������ �����
 * {@link jdb.nextGen.DBasesLoader DBasesLoader} ������ �������.
*/

public class DBLoader
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(DBLoader.class.getName());

  /**
   * ����� ��������� � ����� ��������������� �� � ������� �� (�� ������� ��������� ����� ������������ ���������� � ����).
   * ��� ��������������� �� (�� �����) ������ ��������� � ������ ������� �� (�� ������� ��������� ����� ������������
   * ����������� � ����).
   * @param config DBLoaderConfig ������������ ��� ���������� ��������������. �������� ��� ����������� ������.
   * @throws java.sql.SQLException �� ��� ���������� ������� ������� ��.
   * @throws java.io.IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws jdb.exceptions.DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws jdb.exceptions.DBConnectionException ������ ���������� � ����.
   * @throws jdb.exceptions.DBModelException ������ ������ ���� ������.
   * @return ArrayList[String] ������ ��������� ��� ������ ������ ��. � ������ ������ ������������ �� �����������
   * �� - ��, ������� �� �������� � �������� ������.
  */
  public static ArrayList<String> loadFromDisk(DBLoaderConfig config)
   throws DBConnectionException, DBModuleConfigException, IOException, DBModelException, SQLException
   {
    ArrayList<String> errorsList = null;
    // ��������� ���������, ���� ��� ��������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors)) {errorsList = DBLoadCore.load(config);}
    // ���� ������� ��������� ������� - ������ ������ ������������
    else {logger.error(configErrors);}
    return errorsList;
   }

  /**
   * ����� ������������ �������� �� ���� ��, �������� ��������� ������������ DBLoaderConfig.
   * @param config DBLoaderConfig ������������, �������� ������� �� ���� ����� ��������� ��.
   * @throws SQLException �� ��� ���������� ������� ������� ��.
   * @throws IOException ������ �����/������ ��� ������ � �������� ��������.
   * @throws DBModuleConfigException ������ ���������������� ���������� � ����.
   * @throws DBConnectionException ������ ���������� � ����.
   * @throws DBModelException ������ ������ ���� ������.
  */
  public static void unloadToDisk(DBLoaderConfig config)
   throws DBConnectionException, DBModuleConfigException, IOException, DBModelException, SQLException
   {
    // ��������� ���������, ���� ��� ��������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors)) {DBUnloadCore.unload(config);}
    // ���� ������� ��������� ������� - ������ ������ ������������
    else {logger.error(configErrors);}
   }

  /**
   * ����� ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb", Level.INFO);
    InitLogger.initLogger("jlib", Level.INFO);
    Logger logger = Logger.getLogger("jdb");

    // �������� �������
    DBProcessingMonitor monitor = new DBTestMonitor();
    
    //DBConfig mysqlConfig1 = new DBConfig();
    //mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    //mysqlConfig1.setHost("localhost:3306");
    //mysqlConfig1.setDbName("storm");
    //mysqlConfig1.setUser("root");
    //mysqlConfig1.setPassword("mysql");

    DBConfig ifxConfig = new DBConfig();
    ifxConfig.setDbType(DBConsts.DBType.INFORMIX);
    ifxConfig.setServerName("hercules");
    ifxConfig.setHost("appserver:1526");
    ifxConfig.setDbName("norm_docs");
    ifxConfig.setUser("informix");
    ifxConfig.setPassword("ifx_dba_019");

    DBConfig mssqlConfig = new DBConfig();
    mssqlConfig.setDbType(DBConsts.DBType.MSSQL_JTDS);
    mssqlConfig.setHost("APP");
    mssqlConfig.setDbName("norm_docs");
    mssqlConfig.setUser("sa");
    mssqlConfig.setPassword("adminsql245#I");

    try
     {
      DBLoaderConfig ifxLoaderConfig = new DBLoaderConfig();
      ifxLoaderConfig.setDbConfig(ifxConfig);
      ifxLoaderConfig.setPath("c:\\temp\\norm_docs");
      ifxLoaderConfig.setOperationsCount(100);
      ifxLoaderConfig.setMonitor(monitor);
      // ��������������� �������� �� ������ �� ���� (�� ����������)
      DBLoader.unloadToDisk(ifxLoaderConfig);

      DBLoaderConfig mssqlLoaderConfig = new DBLoaderConfig();
      mssqlLoaderConfig.setDbConfig(mssqlConfig);
      mssqlLoaderConfig.setPath("c:\\temp\\mssql");
      mssqlLoaderConfig.setClearTableBeforeLoad(true);
      //mssqlLoaderConfig.
      //DBLoader.loadFromDisk(mssqlLoaderConfig);
      
     }
    catch (DBConnectionException e)   {logger.error("[" + e.getClass().getName() + "] " + e.getMessage());}
    catch (DBModuleConfigException e) {logger.error("[" + e.getClass().getName() + "] " + e.getMessage());}
    catch (IOException e)             {logger.error("[" + e.getClass().getName() + "] " + e.getMessage());}
    catch (DBModelException e)        {logger.error("[" + e.getClass().getName() + "] " + e.getMessage());}
    catch (SQLException e)            {logger.error("[" + e.getClass().getName() + "] " + e.getMessage());}

   }

 }