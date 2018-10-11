package jdb.utils.helpers;

import jdb.config.connection.BaseDBConfig;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Класс-помощник для класса DBUtils - реализует статические методы для генерации JDBC URL, которые
 * используются для соединения с различными СУБД.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 15.07.2010)
*/

public final class JdbcUrlHelper
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(JdbcUrlHelper.class.getName());

  // Предотвращаем инстанцирование и наследование
  private JdbcUrlHelper() {}

  /**
   * Метод формирует URL для соединения с СУБД Informix через JDBC драйвер.
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getInformixJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // Создаем URL для соединения
      jdbcUrl = new StringBuilder("jdbc:informix-sqli://").append(config.getHost());
      // Если есть имя БД - подключаемся к ней,если же нет - подключаемся к серверу Информикс (только серверу).
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append("/").append(config.getDbName());}
      // Оставшаяся часть jdbcUrl для соединения
      jdbcUrl.append(":INFORMIXSERVER=").append(config.getServerName()).append(";user=").append(config.getUser());
      jdbcUrl.append(";password=").append(config.getPassword().getPassword());
      // Если указаны доп. параметры - добавим их к URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * Метод формирует URL для соединения с СУБД Mysql через JDBC драйвер.
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getMysqlJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // Создаем URL для соединения
      jdbcUrl = new StringBuilder("jdbc:mysql://");
      // Если указан ХОСТ - добавим его к URL. Хост может быть и не указан, тогда будет попытка подключения к хосту
      // с адресом 127.0.0.1 Вместе с ХОСТОМ может быть указан порт (через :), если же порт не указан, то будет
      // использован порт по умолчанию - 3306
      if (!StringUtils.isBlank(config.getHost())) {jdbcUrl.append(config.getHost());}

      // Третий по счету знак "/" обязательно должен быть, вне зависимости от наличия остальных параметров
      jdbcUrl.append("/");
      
      // Если имя БД указано - подключаемся к ней, если же не указано - подключаемся к серверу в целом
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append(config.getDbName());}
      // Если логин/пароль не пусты - добавляем их к URL для подключения
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append("?user=").append(config.getUser());
        // Если не пуст пароль - его тоже добавляем
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append("&password=").append(config.getPassword().getPassword());}
       }

      // todo: экспериментальные параметры для MYSQL!
      jdbcUrl.append("&rewriteBatchedStatements=true");
      jdbcUrl.append("&cachePrepStmts=true");
      jdbcUrl.append("&prepStmtCacheSize=2000");
      jdbcUrl.append("&prepStmtCacheSqlLimit=4096");

      // todo: реализация добавления доп. параметров к URL подключения к Mysql
      // Если указаны доп. параметры подключения - добавим их к URL
      //if ((this.dbConnectionParameters != null) && (!this.dbConnectionParameters.trim().equals("")))
      // {
      //  if (this.dbConnectionParameters.startsWith(";")) jdbcUrl.append(this.dbConnectionParameters);
      //  else jdbcUrl.append(";").append(this.dbConnectionParameters);
      // }

     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * Метод формирует URL для соединения с ODBC-источником через JDBC драйвер.
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getOdbcJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // Если вместо имени ODBC-источника данных нам передали путь к файлу с БД (к файлу *.mdb) - сформируем
      // url для подключения к нему (существование файла на диске НЕ ПРОВЕРЯЕТСЯ!). На данный момент работает
      // только для *.mdb - MS Access. (если в имени БД присутствуют символы \ или / - это путь к БД.)
      if (((config.getDbName().contains("\\")) || (config.getDbName().contains("/"))) &&
          (config.getDbName().toUpperCase().endsWith(".MDB")))
       {jdbcUrl = new StringBuilder("jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=").append(config.getDbName());}
      else
       {jdbcUrl = new StringBuilder("jdbc:odbc:").append(config.getDbName());}
      // Если указаны доп. параметры - добавим их к URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * Метод формирует URL для соединения с DBF-хранилищем через JDBC драйвер.
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getDbfJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      jdbcUrl = new StringBuilder("jdbc:DBF:/").append(config.getDbName());
      // Если указаны доп. параметры - добавим их к URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * Метод формирует URL для соединения с СУБД MS SQL Server через JDBC драйвер JTDS (свободно распространяемый).
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getMssqlJtdsJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // Создаем URL для соединения
      jdbcUrl = new StringBuilder("jdbc:jtds:sqlserver://").append(config.getHost());
      // Если имя БД указано - подключаемся к ней, если же не указано - подключаемся к серверу в целом
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append("/").append(config.getDbName());}
      // Если логин/пароль не пусты - добавляем их к URL для подключения
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append(";user=").append(config.getUser());
        // Если не пуст пароль - его тоже добавляем
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append(";password=").append(config.getPassword().getPassword());}
       }
      // Если указаны доп. параметры - добавим их к URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

  /**
   * Метод формирует URL для соединения с СУБД MS SQL Server через "родной" JDBC драйвер (от Microsoft).
   * @param config DBJdbcConfig конфигурация (параметры) соединения с СУБД.
   * @return String сформированный URL для соединения.
  */
  public static String getMssqlNativeJdbcUrl(BaseDBConfig config)
   {
    String result;
    StringBuilder jdbcUrl = null;
    // Если указанный конфиг не ошибочен - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      // Создаем URL для соединения
      jdbcUrl = new StringBuilder("jdbc:sqlserver://");
      // Если имя хоста (сервера) указано - используем его, если же имени хоста нет, его в любом случае необходимо
      // указать - не указано здесь, надо указать в коллекции свойств соединения.
      if (!StringUtils.isBlank(config.getHost())) {jdbcUrl.append(config.getHost());}

      // Если имя БД указано - подключаемся к ней, если же не указано - подключаемся к серверу в целом
      if (!StringUtils.isBlank(config.getDbName())) {jdbcUrl.append(";database=").append(config.getDbName());}
      
      // Если логин/пароль не пусты - добавляем их к URL для подключения
      if (!StringUtils.isBlank(config.getUser()))
       {
        jdbcUrl.append(";user=").append(config.getUser());
        // Если не пуст пароль - его тоже добавляем
        if ((config.getPassword() != null) && !StringUtils.isBlank(config.getPassword().getPassword()))
         {jdbcUrl.append(";password=").append(config.getPassword().getPassword());}
       }
      // Если указаны доп. параметры - добавим их к URL
      if (!StringUtils.isBlank(config.getConnParams()))
       {
        if (config.getConnParams().startsWith(";")) {jdbcUrl.append(config.getConnParams());}
        else {jdbcUrl.append(";").append(config.getConnParams());}
       }
     }
    // Если конфиг пуст - сообщим об этом
    else {logger.error("There are config errors [" + configErrors + "].");}
    // Формирование конечного результата
    if (jdbcUrl != null) {result = jdbcUrl.toString();} else {result = null;}
    logger.debug("JdbcHelper: generated URL -> [" + result + "]");
    return result;
   }

 }