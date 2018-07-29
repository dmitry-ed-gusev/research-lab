package jdb.config.connection;

import jdb.DBConsts;
import jdb.DBResources;
import jlib.auth.Password;
import jlib.exceptions.EmptyPassException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * �������� ������ ��� �������� ������ ���������������� � ���������� jdb.
 * @author Gusev Dmitry (����� �������)
 * @version 1.0 (DATE: 14.03.11)
*/

public class BaseDBConfigTest
 {
  private BaseDBConfig config;
  private Password     password;

  @Before
  public void setUp() throws EmptyPassException
   {
    config = new BaseDBConfig();
    password = new Password("password");
   }

  /** ��������� �������� ��������� ������. */
  @Test
  public void testGetConfigErrors()
   {
    // ��� ���� ��� ���������� - ���������� ������
    assertEquals("No config data! Must return: [" + DBResources.ERR_MSG_DB_CONFIG_DATA + "].",
                 DBResources.ERR_MSG_DB_CONFIG_DATA, config.getConfigErrors());
    // ���� ��� ��������� ������ (�� ��� ���� ����) - ���������� null
    config.setDataSource("dataSourceName");
    assertNull("With data source name must return null!", config.getConfigErrors());
    // ���� �������� ��� ���� - ���������� ������
    config.setDbType(DBConsts.DBType.UNKNOWN);
    assertEquals("Wrong db type! Must return: [" + String.format(DBResources.ERR_MSG_DB_TYPE, DBConsts.DBType.UNKNOWN) + "].",
                 String.format(DBResources.ERR_MSG_DB_TYPE, DBConsts.DBType.UNKNOWN), config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ���� ���������. */
  @Test
  public void testGetConfigErrorsInformix()
   {
    config.setDbType(DBConsts.DBType.INFORMIX);
    // ������ ��� �����
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.INFORMIX),
                  config.getConfigErrors());
    // ������ ��� ������������
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.INFORMIX),
            config.getConfigErrors());
    // ������ ������
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.INFORMIX),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ���� MySQL. */
  @Test
  public void testGetConfigErrorsMySql()
   {
    config.setDbType(DBConsts.DBType.MYSQL);
    // ������ ��� �����
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MYSQL),
                  config.getConfigErrors());
    // ������ ��� ������������
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MYSQL),
            config.getConfigErrors());
    // ������ ������
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MYSQL),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ���� MS SQL (JTDS-�������). */
  @Test
  public void testGetConfigErrorsMSSqlJTDS()
   {
    config.setDbType(DBConsts.DBType.MSSQL_JTDS);
    // ������ ��� �����
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MSSQL_JTDS),
                  config.getConfigErrors());
    // ������ ��� ������������
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MSSQL_JTDS),
            config.getConfigErrors());
    // ������ ������
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MSSQL_JTDS),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ���� MS SQL (NATIVE-�������). */
  @Test
  public void testGetConfigErrorsMSSqlNative()
   {
    config.setDbType(DBConsts.DBType.MSSQL_NATIVE);
    // ������ ��� �����
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MSSQL_NATIVE),
                  config.getConfigErrors());
    // ������ ��� ������������
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MSSQL_NATIVE),
            config.getConfigErrors());
    // ������ ������
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MSSQL_NATIVE),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ODBC-��������� ������. */
  @Test
  public void testGetConfigErrorsOdbc()
   {
    config.setDbType(DBConsts.DBType.ODBC);
    // ������ ��� ��
    assertEquals("No db name. Must return message.", String.format(DBResources.ERR_MSG_DB_NAME, DBConsts.DBType.ODBC),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setDbName("dbName");
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** ���� �������� ���������� ��� ���� DBF. */
  @Test
  public void testGetConfigErrorsDbf()
   {
    config.setDbType(DBConsts.DBType.DBF);
    // ������ ��� ��
    assertEquals("No db name. Must return message.", String.format(DBResources.ERR_MSG_DB_NAME, DBConsts.DBType.DBF),
            config.getConfigErrors());
    // ��� ��������� (��� ������)
    config.setDbName("dbName");
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

 }