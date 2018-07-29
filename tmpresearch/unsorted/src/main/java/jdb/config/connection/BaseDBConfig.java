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
 * Данный класс реализует простое хранение конфигурации для соединения с СУБД. В классе есть значимые поля, методы доступа
 * к ним и метод проверки корректности параметров (их наличия). Классом можно пользоваться для конфигурирования соединения
 * с СУБД, значения всех полей устанавливаются вручную.
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 10.03.2011)
*/

public class BaseDBConfig implements ConfigInterface
 {
  /**
   * Имя источника данных на дереве JNDI, к которому подключаемся. Если указан данный параметр, то
   * все остальные параметры будут проигнорированы.
  */
  private String     dataSource = null;
  
  /** Тип СУБД, к которой поизводится прямое подключение (через JDBC драйвер). */
  private DBType     dbType     = null;
  /** Поле для хранения хоста СУБД. */
  private String     host       = null;
  /** Поле для хранения имени сервера СУБД (не хоста, а процесса СУБД). */
  private String     serverName = null;
  /** Поле для хранения имени БД на сервере. */
  private String     dbName     = null;
  /** Поле для хранения имени пользователя для доступа к СУБД. */
  private String     user       = null;
  /** Поле для хранения пароля пользователя для доступа к СУБД. */
  private Password   password   = null;
  /**
   * Поле для хранения дополнительных параметров для подключения к СУБД. Данные параметры представляют собой пары
   * имя=значение, разделенные символом ; (точка с запятой) и добавляются(в конец) напрямую к jdbcUrl.
  */
  private String     connParams = null;
  /**
   * Поле для хранения доп. информации для соединения с СУБД. Эта информация - пары имя=значение, находящиеся в классе
   * Properties - добавляется(как параметр) в метод создания соединения с СУБД.
  */
  private Properties connInfo   = null;

  // Обычные методы доступа к полям класса (геттеры и сеттеры)
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
    // Если тип СУБД пуст - проверяем наименование источника данных, если ок, то все номано,
    // если он (источник данных) тоже пуст - ошибка!
    if (StringUtils.isBlank(dataSource))
     {
      if (dbType == null) {result = DBResources.ERR_MSG_DB_CONFIG_DATA;}
      // Если тип СУБД не пуст - проверяем параметры для каждого типа СУБД
      else
       {
        switch (dbType)
         {
          // Для Informix'a, MSSQL'я и MySQL обязательно указание хоста (с портом), логина и пароля пользователя.
          case INFORMIX: case MYSQL: case MSSQL_JTDS: case MSSQL_NATIVE:
           if (StringUtils.isBlank(host))          {result = String.format(DBResources.ERR_MSG_DB_HOST, dbType);}
           else if (StringUtils.isBlank(user))     {result = String.format(DBResources.ERR_MSG_DB_USERNAME, dbType);}
           else if ((password == null) || (StringUtils.isBlank(password.getPassword())))
            {result = String.format(DBResources.ERR_MSG_DB_PASSWORD, dbType);}
          break;
          // Для ODBC и DBFa обязательно указание наименования БД
          case ODBC: case DBF:
           if (StringUtils.isBlank(dbName)) {result = String.format(DBResources.ERR_MSG_DB_NAME, dbType);}
          break;
          // Если тип СУБД не подошел - значит указан неверный тип!
          default:
           result = String.format(DBResources.ERR_MSG_DB_TYPE, dbType);
          break;
         }
       }
     }
    // Возвращаем результат проверки
    return result;
   }

  /**
   * Класс формирует строку со всеми текущими значениями своих полей - конфигурация (состояние) данного класса.
   * @return String строка с конфиг параметрами.
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