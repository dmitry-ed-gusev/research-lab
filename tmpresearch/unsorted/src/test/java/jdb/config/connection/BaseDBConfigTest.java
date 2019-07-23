package jdb.config.connection;

import dgusev.dbpilot.DBConsts;
import jdb.DBResources;
import dgusev.auth.Password;
import jlib.exceptions.EmptyPassException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Тестовый модуль для базового класса конфигурирования в библиотеке jdb.
 * @author Gusev Dmitry (Гусев Дмитрий)
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

  /** Тестируем основные алгоритмы метода. */
  @Test
  public void testGetConfigErrors()
   {
    // нет инфы для соединения - возвращает ошибку
    assertEquals("No config data! Must return: [" + DBResources.ERR_MSG_DB_CONFIG_DATA + "].",
                 DBResources.ERR_MSG_DB_CONFIG_DATA, config.getConfigErrors());
    // есть имя источника данных (но нет типа СУБД) - возвращает null
    config.setDataSource("dataSourceName");
    assertNull("With data source name must return null!", config.getConfigErrors());
    // есть неверный тип СУБД - возвращает ошибку
    config.setDbType(DBConsts.DBType.UNKNOWN);
    //assertEquals("Wrong db type! Must return: [" + String.format(DBResources.ERR_MSG_DB_TYPE, DBConsts.DBType.UNKNOWN) + "].",
    //             String.format(DBResources.ERR_MSG_DB_TYPE, DBConsts.DBType.UNKNOWN), config.getConfigErrors());
   }

  /** Тест проверки параметров для СУБД Информикс. */
  @Test
  public void testGetConfigErrorsInformix()
   {
    config.setDbType(DBConsts.DBType.INFORMIX);
    // пустое имя хоста
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.INFORMIX),
                  config.getConfigErrors());
    // пустое имя пользователя
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.INFORMIX),
            config.getConfigErrors());
    // пустой пароль
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.INFORMIX),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** Тест проверки параметров для СУБД MySQL. */
  @Test
  public void testGetConfigErrorsMySql()
   {
    config.setDbType(DBConsts.DBType.MYSQL);
    // пустое имя хоста
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MYSQL),
                  config.getConfigErrors());
    // пустое имя пользователя
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MYSQL),
            config.getConfigErrors());
    // пустой пароль
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MYSQL),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** Тест проверки параметров для СУБД MS SQL (JTDS-драйвер). */
  @Test
  public void testGetConfigErrorsMSSqlJTDS()
   {
    config.setDbType(DBConsts.DBType.MSSQL_JTDS);
    // пустое имя хоста
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MSSQL_JTDS),
                  config.getConfigErrors());
    // пустое имя пользователя
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MSSQL_JTDS),
            config.getConfigErrors());
    // пустой пароль
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MSSQL_JTDS),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** Тест проверки параметров для СУБД MS SQL (NATIVE-драйвер). */
  @Test
  public void testGetConfigErrorsMSSqlNative()
   {
    config.setDbType(DBConsts.DBType.MSSQL_NATIVE);
    // пустое имя хоста
    assertEquals("No host name. Must return message.", String.format(DBResources.ERR_MSG_DB_HOST, DBConsts.DBType.MSSQL_NATIVE),
                  config.getConfigErrors());
    // пустое имя пользователя
    config.setHost("hostName");
    assertEquals("No user name. Must return message.", String.format(DBResources.ERR_MSG_DB_USERNAME, DBConsts.DBType.MSSQL_NATIVE),
            config.getConfigErrors());
    // пустой пароль
    config.setUser("username");
    assertEquals("No password for user. Must return message.", String.format(DBResources.ERR_MSG_DB_PASSWORD, DBConsts.DBType.MSSQL_NATIVE),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setPassword(password);
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** Тест проверки параметров для ODBC-источника данных. */
  @Test
  public void testGetConfigErrorsOdbc()
   {
    config.setDbType(DBConsts.DBType.ODBC);
    // пустое имя БД
    assertEquals("No db name. Must return message.", String.format(DBResources.ERR_MSG_DB_NAME, DBConsts.DBType.ODBC),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setDbName("dbName");
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

  /** Тест проверки параметров для СУБД DBF. */
  @Test
  public void testGetConfigErrorsDbf()
   {
    config.setDbType(DBConsts.DBType.DBF);
    // пустое имя БД
    assertEquals("No db name. Must return message.", String.format(DBResources.ERR_MSG_DB_NAME, DBConsts.DBType.DBF),
            config.getConfigErrors());
    // все заполнено (без ошибок)
    config.setDbName("dbName");
    assertNull("No errors. Method must return NULL.", config.getConfigErrors());
   }

 }