package jdb.utils;

import dgusev.dbpilot.config.DBConfig;
import jdb.config.load.DBLoaderConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.DBModel;
import jdb.utils.helpers.JdbcUrlHelper;
import jlib.exceptions.utils.ExceptionUtils;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Данный класс содержит различные (нужные и ненужные) утилитки для работы с СУБД.
 * @author Gusev Dmitry (019gus)
 * @version 7.1 (DATE: 26.04.2011)
*/

@SuppressWarnings({"CallToDriverManagerGetConnection", "JDBCResourceOpenedButNotSafelyClosed", "JNDIResourceOpenedButNotSafelyClosed"})
public final class DBUtils
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(DBUtils.class.getName());

  // Предотвращаем инстанцирование и наследование
  private DBUtils() {}

  /**
   * Метод формирует и возвращает строковое представление переданного в качестве параметра курсора ResultSet.
   * Если курсор null или не содеожит ни одной записи - метод возвращает значение null.
   * @param rs ResultSet курсор для визуализации (для генерации строкового представления).
   * @return String строковое представление переданного курсора.
  */
  public static String getStringResultSet(ResultSet rs)
   {
    logger.debug("WORKING DBUtils.getStringResultSet().");
    StringBuilder result = null;
    // Если курсор пуст - возвращаем null
    if (rs != null)
     {
      logger.debug("ResultSet is not empty. Processing.");
      try
       {
        // Если в курсоре есть хоть одна запись - работаем
        if (rs.next())
         {
          result = new StringBuilder();
          int columnCount = rs.getMetaData().getColumnCount();
          // Счетчик строк курсора
          int counter = 0;
          // Проходим по всем строкам крусора и переносим их в результат
          do
           {
            // Формируем строку результата проходя по всем полям строки курсора
            for (int i = 1; i <= columnCount; i++) {result.append(rs.getString(i)).append("|");}
            result.append("\n");
            counter++;
           }
          while(rs.next());
          result.append("-------\n");
          result.append("RECORDS: ").append(counter).append("\n");
         }
        else
         {logger.warn("ResultSet is not NULL, but is EMPTY!");}
       } // end of TRY
      catch (SQLException e) {logger.error("SQL error occured: " + e.getMessage());}
     }
    else logger.warn("ResultSet is NULL!");

    String str;
    if (result != null) {str = result.toString();} else {str = null;}
    return str;
   }

  /**
   * Метод, в зависимости от параметров соединения с СУБД, формирует конфигурационный URL для соединения с СУБД - jdbcUrl.
   * Если будет указан пустой конфиг - метод возвращает значение null. Если не поддерживается указанный тип СУБД -
   * метод также вернет значение null.
   * @param config конфиг для формирования JDBC URL.
   * @return String универсальная строка-конфигуратор для соединения с СУБД (URL).
  */
  public static String getDBUrl(DBConfig config)
   {
    // Результат выполнения метода
    String result = null;
    // Если указанный конфиг для соединения не содержит ошибок и не пуст - работаем
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors))
     {
      logger.debug("Received connection config is OK. Processing.");
      // Выбор типа СУБД
      switch (config.getDbType())
       {
        case MYSQL:        result = JdbcUrlHelper.getMysqlJdbcUrl(config);       break;
        case ODBC:         result = JdbcUrlHelper.getOdbcJdbcUrl(config);        break;
        case DBF:          result = JdbcUrlHelper.getDbfJdbcUrl(config);         break;
        case INFORMIX:     result = JdbcUrlHelper.getInformixJdbcUrl(config);    break;
        case MSSQL_JTDS:   result = JdbcUrlHelper.getMssqlJtdsJdbcUrl(config);   break;
        case MSSQL_NATIVE: result = JdbcUrlHelper.getMssqlNativeJdbcUrl(config); break;
        default:           logger.error("Unsupported DB type: [" + config.getDbType() + "]!"); result = null; break;
       }
     }
    // Если же указанный конфиг пуст - выводим ошибку в лог
    else {logger.error("Received connection config had errors [" + configErrors + "]!");}
    // Отладочный вывод
    logger.debug("getDBUrl: generated URL -> [" + result + "].");
    return result;
   }

  /**
   * Метод создает и возвращает соединение (объект Connection) с указанной в конфиге СУБД. Данный метод может работать
   * как с прямым соединением (через JDBC драйвер), так и с источником данных JNDI, выбор способа соединения зависит
   * от переданного методу конфига - если в конфиге заполнено поле dataSource, то будет выполнено соединение через
   * JNDI источник данных, если же не заполнено - метод попытается установить соединение посредством JDBC драйвера
   * указанной СУБД.
   * @param config DBConfig конфиг для соединения с СУБД.
   * @return Connection созданное с СУБД соединение.
   * @throws DBModuleConfigException ошибки в конфигурации соединения с СУБД (в классе конфига).
   * @throws DBConnectionException ошибки при соединении с СУБД.
  */
  public static Connection getDBConn(DBConfig config) throws DBModuleConfigException, DBConnectionException
   {
    logger.debug("DBUtils.getDBConn(): connecting to DBMS.");
    Connection connection;
    // Если нам передан конфиг с ошибками - возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Теперь нам надо определиться как мы коннектимся к БД - через JDBC или через JNDI. Если в переданном нам
    // конфиге указано имя источника данных JNDI - используем этот тип соединения, если же не указано - используем тип
    // соединения - через JDBC-драйвер.
    if (!StringUtils.isBlank(config.getDataSource()))
     {
      logger.debug("Connecting to DBMS over JNDI data source.");
      try
       {
        // Получаем источник данных
        DataSource dataSource = (DataSource) new InitialContext().lookup(config.getDataSource());
        connection = dataSource.getConnection();
       }
      catch (NamingException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (SQLException e)    {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
     }
    // Имя источника данных JNDI не указано - коннектимся через JDBC
    else
     {
      logger.debug("Connecting to DBMS over JDBC driver.");
      try
       {
        // Получаем драйвер
        String dbDriver = config.getDbType().getDriver();
        // Если драйвер не пуст - работаем дальше
        if (!StringUtils.isBlank(dbDriver))
         {
          logger.debug("Database driver OK. Processing. Driver: [" + dbDriver + "].");
          // Загрузка класса драйвера (для драйверов типа JDBC 4 не нужна - ???)
          // todo: необходима ли прямая загрузка драйвера?
          Class.forName(dbDriver).newInstance();
          logger.debug("Driver [" + dbDriver + "] loaded!");
          // Дополнительные параметры для соединения с СУБД
          Properties connectionInfo = config.getConnInfo();
          // Непосредственно подключение к СУБД
          if ((connectionInfo != null) && (!connectionInfo.isEmpty()))
           {
            logger.debug("Using getConnection() with [CONNECTION INFO].");
            connection = DriverManager.getConnection(DBUtils.getDBUrl(config), config.getConnInfo());
            //logger.debug("Connection ok.");
           }
          else
           {
            logger.debug("Using getConnection() without [CONNECTION INFO].");
            connection = DriverManager.getConnection(DBUtils.getDBUrl(config));
            //logger.debug("Connection ok.");
           }
         }
        // Если драйвер (класс драйвера) не указан - возбуждаем ИС и сообщаем об этом
        else {throw new DBConnectionException("Database driver class is empty (NULL)!");}
       }
      catch (ClassNotFoundException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (IllegalAccessException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (InstantiationException e) {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
      catch (SQLException e)           {throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));}
     }
    // Возвращаем объект "соединение с СУБД".
    return connection;
   }

  /**
   * Метод создает и возвращает соединение, сконфигурированное с помощью JNDI источника данных dataSourceName. Если
   * имя источника данных пусто - возникнет ИС.
   * @param dataSourceName String имя JNDI источника данных.
   * @return Connection созданное с СУБД соединение.
   * @throws DBModuleConfigException ошибки в конфигурации соединения с СУБД (пустое имя источника данных).
   * @throws DBConnectionException ошибки при соединении с СУБД.
  */
  public static Connection getDBConn(String dataSourceName) throws DBConnectionException, DBModuleConfigException, IOException, ConfigurationException {
    if (!StringUtils.isBlank(dataSourceName))
     {
      DBConfig config = new DBConfig();
      config.setDataSource(dataSourceName);
      return DBUtils.getDBConn(config);
     }
    // Имя источника данных пусто
    else {throw new DBModuleConfigException("Empty data source name!");}
   }

  /**
   * Проверка валидности (правильности, правомерности, возможности) соединения с СУБД с помощью указанного конфига.
   * Указанный конфиг проверяется - не пуст ли он, затем производится соединение с СУБД, если соединение завершилось
   * удачно - мы получили соединение - метод возвращает значение ИСТИНА, полученное соединение закрывается. При
   * возникновении каких-либо ошибок, метод возвращает значение ЛОЖЬ, при этом все возможные ИС перехватываются,
   * сообщения о возникших ошибках записываются в лог.
   * @param config DBConfig проверяемая конфигурация для соединения с СУБД.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, возможно ли соединение с СУБД с помощью указанного конфига.
  */
  public static boolean isConnectionValid(DBConfig config)
   {
    boolean result = false;
    Connection connection = null;
    try
     {
      logger.debug("Trying get connection to DBMS.");
      // Пытаемся получить соединение
      connection = DBUtils.getDBConn(config);
      // Если удалось получить соединение - значит все ОК
      if (connection != null) {result = true;}
     }
    // Перехватываем и обрабатываем все возможные ИС
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    // Если соединение открыть удалось - обязательно закрываем его (пытаемся)
    finally
     {
      try {if (connection != null) {connection.close();}}
      catch (SQLException e) {logger.error("Can't close connection (reason: " + e.getMessage() + ")!");}
     }
    // Отладочный вывод
    logger.debug("DBConfig validation result: " + result);
    return result;
   }

  /**
   * Проверка валидности (правильности, правомерности, возможности) соединения с СУБД с помощью указанного источника
   * данных. Указанный источник данных проверяется - не пуст ли он, затем производится получение соединения с СУБД с
   * помощью данного источника данных. Если соединение удачно получено, то метод возвращает значение ИСТИНА, полученное
   * соединение закрывается. При возникновении каких-либо ошибок, метод возвращает значение ЛОЖЬ, при этом все возможные
   * ИС перехватываются, сообщения о возникших ошибках записываются в лог.
   * @param dataSource DataSource проверяемый на валидность источник данных для соединения с СУБД.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, возможно ли соединение с СУБД с помощью указанного источника
   * данных.
  */
  public static boolean isConnectionValid(DataSource dataSource)
   {
    boolean result = false;
    Connection connection = null;
    try
     {
      logger.debug("Trying get connection to DBMS.");
      // Пытаемся получить соединение
      if (dataSource != null)
       {
        connection = dataSource.getConnection();
        // Если удалось получить соединение - значит все ОК
        if (connection != null) {result = true;}
       }
      // Если источник данных пуст - сообщим в лог
      else {logger.warn("Data source is NULL!");}
     }
    // Перехват всех ИС
    catch (SQLException e) {logger.error(e.getMessage());}
    // Если соединение открыть удалось - обязательно закрываем его (пытаемся)
    finally
     {
      try {if (connection != null) {connection.close();}}
      catch (SQLException e) {logger.error("Can't close connection (reason: " + e.getMessage() + ")!");}
     }
    // Отладочный вывод
    logger.debug("DataSource validation result: " + result);
    return result;
   }

  /**
   * Метод возвращает сообщение, характеризующее ошибки класса конфигурации, реализующего интерфейс ConfigInterface.
   * Если ошибок нет (конфигурация в порядке), то метод возвращает значение NULL.
   * @param config ConfigInterface конфигурация для проверки.
   * @return String описание ошибок конфига или NULL.
  */
  public static String getConfigErrors(DBConfig config)
   {
    // Если конфиг в порядке, то метод должен вернуть NULL
    String result;
    if (config == null) {result = "Configuration is NULL!";}
    else                {result = config.getConfigErrors();}
    return result;
   }

   // todo: remove method!!!
  public static String getConfigErrors(DBLoaderConfig config)
  {
   // Если конфиг в порядке, то метод должен вернуть NULL
   String result;
   if (config == null) {result = "Configuration is NULL!";}
   else                {result = config.getConfigErrors();}
   return result;
  }

  /**
   * Данный метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, является ли указанный SQL-запрос запросом
   * типа "SELECT...".
   * @param sql String запрос для анализа.
   * @return boolean ИСТИНА - это "SELECT..."-запрос, ЛОЖЬ - в противном случае
   * @throws java.sql.SQLException ИС возникает, если указанный запрос пуст.
  */
  public static boolean isSelectQuery(String sql) throws SQLException
   {
    // Если анализируемый запрос пуст - мы не можем ничего сказать! Ошибка!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Query for analize is empty!");}
    // Теперь анализируем гарантированно не пустой запрос (и возвращаем результат)
    return (StringUtils.strip(sql).toUpperCase().startsWith("SELECT"));
   }

  /**
   * Метод анализирует переданную в качестве параметра модель БД и возвращает результат - пуста или нет указанная
   * модель (ИСТИНА - модель пуста, ЛОЖЬ - модель не пуста).
   * @param model DBModel тестируемая модель БД.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет данный конкретный экземпляр класса-модели БД.
  */
  public static boolean isDBModelEmpty(DBModel model)
   {
    boolean result = true;
    if ((model != null) && (!model.isEmpty())) {result = false;}
    return result;
   }

}