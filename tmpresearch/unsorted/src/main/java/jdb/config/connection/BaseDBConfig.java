package jdb.config.connection;

import jdb.DBConsts.DBType;
import jdb.DBResources;
import jdb.config.common.ConfigInterface;
import jlib.auth.Password;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Properties;

/**
 * ������ ����� ��������� ������� �������� ������������ ��� ���������� � ����. � ������ ���� �������� ����, ������ �������
 * � ��� � ����� �������� ������������ ���������� (�� �������). ������� ����� ������������ ��� ���������������� ����������
 * � ����, �������� ���� ����� ��������������� �������.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 10.03.2011)
*/

public class BaseDBConfig implements ConfigInterface
 {
  /**
   * ��� ��������� ������ �� ������ JNDI, � �������� ������������. ���� ������ ������ ��������, ��
   * ��� ��������� ��������� ����� ���������������.
  */
  private String     dataSource = null;
  
  /** ��� ����, � ������� ����������� ������ ����������� (����� JDBC �������). */
  private DBType     dbType     = null;
  /** ���� ��� �������� ����� ����. */
  private String     host       = null;
  /** ���� ��� �������� ����� ������� ���� (�� �����, � �������� ����). */
  private String     serverName = null;
  /** ���� ��� �������� ����� �� �� �������. */
  private String     dbName     = null;
  /** ���� ��� �������� ����� ������������ ��� ������� � ����. */
  private String     user       = null;
  /** ���� ��� �������� ������ ������������ ��� ������� � ����. */
  private Password   password   = null;
  /**
   * ���� ��� �������� �������������� ���������� ��� ����������� � ����. ������ ��������� ������������ ����� ����
   * ���=��������, ����������� �������� ; (����� � �������) � �����������(� �����) �������� � jdbcUrl.
  */
  private String     connParams = null;
  /**
   * ���� ��� �������� ���. ���������� ��� ���������� � ����. ��� ���������� - ���� ���=��������, ����������� � ������
   * Properties - �����������(��� ��������) � ����� �������� ���������� � ����.
  */
  private Properties connInfo   = null;

  // ������� ������ ������� � ����� ������ (������� � �������)
  public DBType getDbType() {return dbType;}
  public void setDbType(DBType dbType) {this.dbType = dbType;}

  public String getHost() {return host;}
  public void setHost(String host) {this.host = host;}

  public String getServerName() {return serverName;}
  public void setServerName(String serverName) {this.serverName = serverName;}

  public String getDbName() {return dbName;}
  public void setDbName(String dbName) {this.dbName = dbName;}

  public String getUser() {return user;}
  public void setUser(String user) {this.user = user;}

  public String getConnParams() {return connParams;}
  public void setConnParams(String connParams) {this.connParams = connParams;}

  public Properties getConnInfo() {return connInfo;}
  public void setConnInfo(Properties connInfo) {this.connInfo = connInfo;}

  public Password getPassword() {return password;}
  public void setPassword(Password password) {this.password = password;}

  public String getDataSource() {return dataSource;}
  public void setDataSource(String dataSource) {this.dataSource = dataSource;}


  public String getConfigErrors()
   {
    String result = null;
    // ���� ��� ���� ���� - ��������� ������������ ��������� ������, ���� ��, �� ��� ������,
    // ���� �� (�������� ������) ���� ���� - ������!
    if (StringUtils.isBlank(dataSource))
     {
      if (dbType == null) {result = DBResources.ERR_MSG_DB_CONFIG_DATA;}
      // ���� ��� ���� �� ���� - ��������� ��������� ��� ������� ���� ����
      else
       {
        switch (dbType)
         {
          // ��� Informix'a, MSSQL'� � MySQL ����������� �������� ����� (� ������), ������ � ������ ������������.
          case INFORMIX: case MYSQL: case MSSQL_JTDS: case MSSQL_NATIVE:
           if (StringUtils.isBlank(host))          {result = String.format(DBResources.ERR_MSG_DB_HOST, dbType);}
           else if (StringUtils.isBlank(user))     {result = String.format(DBResources.ERR_MSG_DB_USERNAME, dbType);}
           else if ((password == null) || (StringUtils.isBlank(password.getPassword())))
            {result = String.format(DBResources.ERR_MSG_DB_PASSWORD, dbType);}
          break;
          // ��� ODBC � DBFa ����������� �������� ������������ ��
          case ODBC: case DBF:
           if (StringUtils.isBlank(dbName)) {result = String.format(DBResources.ERR_MSG_DB_NAME, dbType);}
          break;
          // ���� ��� ���� �� ������� - ������ ������ �������� ���!
          default:
           result = String.format(DBResources.ERR_MSG_DB_TYPE, dbType);
          break;
         }
       }
     }
    // ���������� ��������� ��������
    return result;
   }

  /**
   * ����� ��������� ������ �� ����� �������� ���������� ����� ����� - ������������ (���������) ������� ������.
   * @return String ������ � ������ �����������.
  */
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("dataSource", dataSource).
            append("dbType", dbType).
            append("host", host).
            append("serverName", serverName).
            append("dbName", dbName).
            append("user", user).
            append("password", password).
            append("connParams", connParams).
            append("connInfo", connInfo).
            toString();
   }

 }