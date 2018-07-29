package jdb.model.applied.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ������ ����� ����� �������� ������������ ��� �������, ����������� DAO-���������� ����������. � �������� ���
 * ���������� ����� �������������� ��� J2EE ����������. ������ ����� ������������ ��� ������ � ���������� ������
 * (Data Source), ������� ��������� �� ������ �������� JNDI. ��� �������-��������� ������ ����������� � ������������
 * ������. ����� ������ ��� JNDI-��������� ������ � ���� ��� ������������������ ��������� (��� ����� ����������).
 * ����� ����� ��������� ����� ��������� ���������� � ���� (Connection) � ������� ����������� ��������� ������.
 *
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 22.11.2010)
*/

public class DSCommonDAO
 {
  /** ���������-������ ������� ������. */
  private        Logger     logger         = null;
  /** ���� ��� �������� ����� ��������� ������. */
  private static String     dataSourceName = null;
  /** ���� ��� �������� ������ �� JNDI-�������� ������. */
  private static DataSource dataSource     = null;

  /**
   * ����������� ����������. � ������������ ���������������� ����, �������� ������ �� �������� ������, � �����
   * ���������-������ ��� ������� ������. ����� ����� �������������� ���������� ���� ��� �������� ����� ���������
   * ������. ���� �������� ������ ��� ��������������� (������ �� ����� DataSource �� �����), ������ �� ����������,
   * ������ ������� ���������� ������ � ���.
   * @param loggerName String
   * @param dataSourceName String
  */
  @SuppressWarnings({"JNDIResourceOpenedButNotSafelyClosed"})
  public DSCommonDAO(String loggerName, String dataSourceName)
   {
    // �������� ������ �� ������ ��� ������� ������
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // ���� �������� ������ ��� �� ��������������� - �������������.
    if (dataSource == null)
     {
      // ������������� ���� ��� �������� ������ �� �������� ������, ������ ���� ������� �� ������ ��� ���������
      if (!StringUtils.isBlank(dataSourceName))
       {
        logger.debug("Data source [" + dataSourceName + "] is not initialized yet. Processing.");
        // �������� ��� ��������� ������
        DSCommonDAO.dataSourceName = dataSourceName;
        // ���������������� ������������� ��������� JNDI � ����� ������������ ������� �� �����
        Context context = null;
        try
         {
          context = new InitialContext();
          DSCommonDAO.dataSource = (DataSource) context.lookup(DSCommonDAO.dataSourceName);
          logger.info("Data source [" + DSCommonDAO.dataSourceName + "] initialized!");
         }
        // �������� ��
        catch (NamingException e)
         {logger.error("Can't get datasource [" + DSCommonDAO.dataSourceName + "]: " + e.getMessage());}
        // ��������� ��������. ����, ���� ��� � �� ���������, �� ������� ������ ��� (�� ���� �������� �������).
        finally
         {
          try {if (context != null) {context.close();}}
          catch (NamingException e) {logger.error("Can't close context! Reason: " + e.getMessage());}
         }
       }
      // ���� ��������� ��� ��������� ������ ����� - ������ �� ��������������, � ��� ����� ������
      else {logger.error("Data source name is EMPTY!");}
     }
    // ���� �� �������� ������ (������ �� ����) ��� ��������������� - ������� �� ����
    else {logger.debug("Data source already initialized!");}
   }

  /**
   * �����������. � ������������ ���������������� ����, �������� ������ �� �������� ������, � ����� ���������-������
   * ��� ������� ������. ���������� ���� ��� �������� ����� ��������� ������ �������� ������. ���� �������� ������ ���
   * ��������������� (������ �� ����� DataSource �� �����), ������ �� ����������, ������ ������� ���������� ������ � ���.
   * @param loggerName String
   * @param ds DataSource
  */
  public DSCommonDAO(String loggerName, DataSource ds)
   {
    // �������� ������ �� ������ ��� ������� ������
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // ���� �������� ������ ��� �� ��������������� - �������������.
    if (DSCommonDAO.dataSource == null)
     {
      logger.debug("Starting DataSource initialization.");
      // ������������� ������ �� �������� ������, ���� � �������� ��������� ������ �������� (�� NULL) ��������� ������
      if (ds != null)
       {
        logger.debug("Data source is not initialized yet. Processing.");
        DSCommonDAO.dataSource = ds;
        logger.info("Data source [" + DSCommonDAO.dataSourceName + "] initialized!");
       }
      // ���� ��������� ������ �� �������� ������ ����� - ������ �� ��������������, � ��� ����� ������
      else {logger.error("Data source link is NULL!");}
     }
    // ���� �� �������� ������ (������ �� ����) ��� ��������������� - ������� �� ����
    else {logger.debug("Data source already initialized!");}
   }

  /**
   * ����� ���������� ����������, ���������� � ��������� ������ ������� ����������, ������ �� ������� ��������
   * � ���� ������� ������.
   * @return Connection ���������� � ����, ���������� �� ��������� ������ ��� �������� null.
   * @throws java.sql.SQLException ��, ����������� � �������� ��������� ����������.
  */
  public Connection getConnection() throws SQLException
   {
    // ���� �������� ������ �� ���� - �������� �� ���� ����������
    if (dataSource != null)
     {
      logger.debug("Data source OK. Getting connection.");
      // �������� ���������� �� ��������� ������.
      Connection conn = DSCommonDAO.dataSource.getConnection();
      // ���� ���������� ���������� �� ����� - ��� ��, ���������� ���
      if (conn != null) {return conn;}
      // ���� �� ���������� ����� - ������
      else {throw new SQLException("Connection received from data source [" + DSCommonDAO.dataSourceName + "] is empty!");}
     }
    // ���� �� �������� ���� - ������
    else {throw new SQLException("Data source [" + DSCommonDAO.dataSourceName + "] is null (maybe not initialized)!");}
   }

  /**
   * ����� ���������� ������ �� �������� ������ (DataSource), ������ �� ������� �������� � ������ ������. ���� ��������
   * ������ �� ���������������, �� ���� ������� �� SQLException.
   * @return DataSource ������������ ������ �� �������� ������.
   * @throws SQLException ��, ����������� � �������� ��������� ������ ��  �������� ������.
  */
  public DataSource getDataSource() throws SQLException {return dataSource;}

 }