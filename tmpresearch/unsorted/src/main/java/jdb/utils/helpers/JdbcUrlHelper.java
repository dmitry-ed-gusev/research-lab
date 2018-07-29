package jdb.utils.helpers;

import jdb.config.connection.BaseDBConfig;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * �����-�������� ��� ������ DBUtils - ��������� ����������� ������ ��� ��������� JDBC URL, �������
 * ������������ ��� ���������� � ���������� ����.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 15.07.2010)
*/

public final class JdbcUrlHelper
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(JdbcUrlHelper.class.getName());

  // ������������� ��������������� � ������������
  private JdbcUrlHelper() {}

  /**
   * ����� ��������� URL ��� ���������� � ���� Informix ����� JDBC �������.
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getInformixJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // ������� URL ��� ����������
      jdbcUrl = new StringBuilder("jdbc:informix-sqli://").append(config.getHost());
      // ���� ���� ��� �� - ������������ � ���,���� �� ��� - ������������ � ������� ��������� (������ �������).
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append("/").append(config.getDbName());}
      // ���������� ����� jdbcUrl ��� ����������
      jdbcUrl.append(":INFORMIXSERVER=").append(config.getServerName()).append(";user=").append(config.getUser());
      jdbcUrl.append(";password=").append(config.getPassword().getPassword());
      // ���� ������� ���. ��������� - ������� �� � URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * ����� ��������� URL ��� ���������� � ���� Mysql ����� JDBC �������.
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getMysqlJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // ������� URL ��� ����������
      jdbcUrl = new StringBuilder("jdbc:mysql://");
      // ���� ������ ���� - ������� ��� � URL. ���� ����� ���� � �� ������, ����� ����� ������� ����������� � �����
      // � ������� 127.0.0.1 ������ � ������ ����� ���� ������ ���� (����� :), ���� �� ���� �� ������, �� �����
      // ����������� ���� �� ��������� - 3306
      if (!StringUtils.isBlank(config.getHost())) {jdbcUrl.append(config.getHost());}

      // ������ �� ����� ���� "/" ����������� ������ ����, ��� ����������� �� ������� ��������� ����������
      jdbcUrl.append("/");
      
      // ���� ��� �� ������� - ������������ � ���, ���� �� �� ������� - ������������ � ������� � �����
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append(config.getDbName());}
      // ���� �����/������ �� ����� - ��������� �� � URL ��� �����������
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append("?user=").append(config.getUser());
        // ���� �� ���� ������ - ��� ���� ���������
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append("&password=").append(config.getPassword().getPassword());}
       }

      // todo: ����������������� ��������� ��� MYSQL!
      jdbcUrl.append("&rewriteBatchedStatements=true");
      jdbcUrl.append("&cachePrepStmts=true");
      jdbcUrl.append("&prepStmtCacheSize=2000");
      jdbcUrl.append("&prepStmtCacheSqlLimit=4096");

      // todo: ���������� ���������� ���. ���������� � URL ����������� � Mysql
      // ���� ������� ���. ��������� ����������� - ������� �� � URL
      //if ((this.dbConnectionParameters != null) && (!this.dbConnectionParameters.trim().equals("")))
      // {
      //  if (this.dbConnectionParameters.startsWith(";")) jdbcUrl.append(this.dbConnectionParameters);
      //  else jdbcUrl.append(";").append(this.dbConnectionParameters);
      // }

     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * ����� ��������� URL ��� ���������� � ODBC-���������� ����� JDBC �������.
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getOdbcJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // ���� ������ ����� ODBC-��������� ������ ��� �������� ���� � ����� � �� (� ����� *.mdb) - ����������
      // url ��� ����������� � ���� (������������� ����� �� ����� �� �����������!). �� ������ ������ ��������
      // ������ ��� *.mdb - MS Access. (���� � ����� �� ������������ ������� \ ��� / - ��� ���� � ��.)
      if (((config.getDbName().contains("\\")) || (config.getDbName().contains("/"))) &&
          (config.getDbName().toUpperCase().endsWith(".MDB")))
       {jdbcUrl = new StringBuilder("jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=").append(config.getDbName());}
      else
       {jdbcUrl = new StringBuilder("jdbc:odbc:").append(config.getDbName());}
      // ���� ������� ���. ��������� - ������� �� � URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * ����� ��������� URL ��� ���������� � DBF-���������� ����� JDBC �������.
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getDbfJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      jdbcUrl = new StringBuilder("jdbc:DBF:/").append(config.getDbName());
      // ���� ������� ���. ��������� - ������� �� � URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * ����� ��������� URL ��� ���������� � ���� MS SQL Server ����� JDBC ������� JTDS (�������� ����������������).
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getMssqlJtdsJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // ������� URL ��� ����������
      jdbcUrl = new StringBuilder("jdbc:jtds:sqlserver://").append(config.getHost());
      // ���� ��� �� ������� - ������������ � ���, ���� �� �� ������� - ������������ � ������� � �����
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append("/").append(config.getDbName());}
      // ���� �����/������ �� ����� - ��������� �� � URL ��� �����������
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append(";user=").append(config.getUser());
        // ���� �� ���� ������ - ��� ���� ���������
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append(";password=").append(config.getPassword().getPassword());}
       }
      // ���� ������� ���. ��������� - ������� �� � URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * ����� ��������� URL ��� ���������� � ���� MS SQL Server ����� "������" JDBC ������� (�� Microsoft).
   * @param config DBJdbcConfig ������������ (���������) ���������� � ����.
   * @return String �������������� URL ��� ����������.
  */
  public static String getMssqlNativeJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // ���� ��������� ������ �� �������� - ��������
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // ������� URL ��� ����������
      jdbcUrl = new StringBuilder("jdbc:sqlserver://");
      // ���� ��� ����� (�������) ������� - ���������� ���, ���� �� ����� ����� ���, ��� � ����� ������ ����������
      // ������� - �� ������� �����, ���� ������� � ��������� ������� ����������.
      if (!StringUtils.isBlank(config.getHost())) {jdbcUrl.append(config.getHost());}

      // ���� ��� �� ������� - ������������ � ���, ���� �� �� ������� - ������������ � ������� � �����
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append(";database=").append(config.getDbName());}
      
      // ���� �����/������ �� ����� - ��������� �� � URL ��� �����������
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append(";user=").append(config.getUser());
        // ���� �� ���� ������ - ��� ���� ���������
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append(";password=").append(config.getPassword().getPassword());}
       }
      // ���� ������� ���. ��������� - ������� �� � URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // ���� ������ ���� - ������� �� ����
    else {logger.error("There are config errors [" + configErrors + "].");}
    // ������������ ��������� ����������
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

 }