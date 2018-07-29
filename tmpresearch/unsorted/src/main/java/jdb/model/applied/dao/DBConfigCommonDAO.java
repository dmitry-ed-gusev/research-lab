package jdb.model.applied.dao;

import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 05.05.2011)
*/

public class DBConfigCommonDAO
 {
  /** ������ ������� ������. */
  private        Logger   logger     = null;
  /** ���� ��� �������� ����� ������-����� � ����������� ����������. */
  private static String   configFile = null;
  /** ���� ��� �������� ������ ������������ ���������� � ����. */
  private static DBConfig config     = null;

  /***/
  public DBConfigCommonDAO(String loggerName, String configFile)
   {
    // �������� ������ �� ������
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // ���� ����� ������� ���������� ��� �� ��������������� - �������������
    if (DBConfigCommonDAO.config == null)
     {
      // ������������� ������� ����������, ������ ���� ������� �� ������ ��� ����� ����������
      if (!StringUtils.isBlank(configFile))
       {
        logger.debug("DBConfig is not initialized from file [" + configFile + "]. Processing.");
        // �� ������ ������ �������� ��� ������-�����
        DBConfigCommonDAO.configFile = configFile;
        // ��������������� �������� ������ �� ����� ������������
        try
         {
          DBConfigCommonDAO.config = new DBConfig(configFile);
          logger.info("DBConfig initialized from file [" + configFile + "].");
         }
        catch (DBModuleConfigException e) {logger.error(e.getMessage());}
        catch (ConfigurationException e)  {logger.error(e.getMessage());}
        catch (IOException e)             {logger.error(e.getMessage());}
       }
      // ���� ������� ������ ��� ����� - ��� �������������, � ��� - ������!
      else {logger.error("Config file name is EMPTY!");}
     }
    // ���� ����� ������� ��� ��������������� - ������� �� ����
    else {logger.debug("DBConfig already initialized!");}
   }

  /***/
  public DBConfigCommonDAO(String loggerName, DBConfig dbConfig)
   {
    // �������� ������ �� ������
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // ���� ����� ������� ���������� ��� �� ��������������� - �������������
    if (DBConfigCommonDAO.config == null)
     {
      // ������������� ������� ����������, ������ ���� ������ �� ���� � �� �������� ������
      if ((dbConfig != null) && StringUtils.isBlank(dbConfig.getConfigErrors()))
       {
        logger.debug("DBConfig is not initialized yet. Processing.");
        DBConfigCommonDAO.config = dbConfig;
        logger.info("DBConfig initialized.");
       }
      // ���� ������� ������ ��� ����� - ��� �������������, � ��� - ������!
      else {logger.error("Received DBConfig is or has errors!");}
     }
    // ���� ����� ������� ��� ��������������� - ������� �� ����
    else {logger.debug("DBConfig already initialized!");}
   }

  /***/
  public Connection getConnection() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // ���� ��� ������ �� ���� - �������� �� ���� ���������� � ����
    if (config != null)
     {
      logger.debug("DBConfig is OK. Getting connection.");
      // ��������������� �������� ���������� � ����
      Connection connection = DBUtils.getDBConn(config);
      // ���� ���������� ���������� �� ����� - ���������� ���
      if (connection != null) {return connection;}
      // ���� �� ���������� ����� - ������
      else {throw new SQLException("Connection received from DBConfig is empty! Config file [" + configFile + "].");}
     }
    // ���� �� ������ ���� - ������!
    else {throw new SQLException("DBConfig is null (maybe not initialized)!");}
   }

  /***/
  public static DBConfig getConfig() {return config;}

 }