package jdb.processing.tester;

import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.processing.spider.DBSpider;
import jlib.logging.InitLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Класс для проверки различных параметров как сервера СУБД так и самой БД.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 01.09.2008)
*/

public class DBTester
 {
  /** Компонент-логгер данного класса. */
  private Logger           logger = Logger.getLogger(this.getClass().getName());
  /** Текущая конфигурация данного модуля. */
  private DBConfig config = null;

  /**
   * Конструктор по умолчанию. Инициализирует текущую конфигурацию.
   * @param config ConnectionConfig конфигурация модуля.
  */
  /**
  public DBTester(DBConfig config) throws EmptyConnectionConfigException
   {
    logger.debug("WORKING DBTester constructor().");
    // Инициализация конфигурации модуля
    if (config == null) throw new EmptyConnectionConfigException("Empty connection config!");
    else this.config = config;
   }
  */
  
  /**
   * Проверка соединения с БД. Возвращает описание ошибки - если не удалось соединиться с БД, если же удалось -
   * метод возвращает значение null.
   * @return String описание ошибки или значение null (если все в порядке).
   * @deprecated не рекомендуется использовать данный класс.
  */
  public String testDBMSConnection()
   {
    String     result = null;
    Connection conn   = null;

    /**
    try
     {
      // Локальная конфигурация для подключения к СУБД
      ConnectionConfig localConfig = new ConnectionConfig(this.config);
      // Обнуление имени БД необходимо для подключения к серверу в целом, а не к конкретной БД
      localConfig.setDbName(null);
      // Подключаемся
      JdbcConnector connector = JdbcConnector.getInstance(localConfig);
      conn = connector.getConnection();
     }
    // Перехват всех возможных ИС и формирование строки с описанием ошибки соединения
    catch (SQLException e)
     {
      result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "code: " + e.getErrorCode() +
                             " sqlState: " + e.getSQLState());
     }
    catch (EmptyConnectionTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (ClassNotFoundException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (EmptyDBMSTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (EmptyConnectionConfigException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (InvalidDBMSTypeException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (InstantiationException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (IllegalAccessException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}

    // Если удалось соединиться нужно закрыть за собой соединение
    if (conn != null)
     try {conn.close();} catch (SQLException e) {logger.error("Can't close connection! [" + e.getMessage() + "]");}
    */

    return result;
   }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, существует ли на текущем сревере баз данных
   * база с именем dbName.
   * @param dbName String имя искомой БД.
   * @return boolean ИСТИНА/ЛОЖЬ - существует ли указанная БД.
  */
  public boolean isDBExists(String dbName)
   {
    boolean result = false;
    logger.debug("Checking existence DB [" + dbName + "] on current DBMS server.");
    try
     {
      // Если переданное имя БД пусто - ничего вообще не делаем
      if ((dbName != null) && (!dbName.trim().equals("")))
       {
        // Проверяем существование БД только если есть коннект к СУБД
        String connectResult = this.testDBMSConnection();
        if (connectResult == null)
         {
          logger.debug("Connect to DBMS is OK! Searching DB.");
          DBSpider spider = new DBSpider(this.config);
          ArrayList<String> databases = spider.getDBSList();
          // если получен непустой список баз данных данного сервера - ищем по нему искомую БД
          if ((databases != null) && (!databases.isEmpty()))
           if (databases.contains(dbName.toUpperCase()))
            result = true;
         } // конец оператора проверки коннекта к СУБД
        else logger.error(String.format(DBTesterConsts.MSG_ERROR_CONNECT, "", connectResult, ""));
       } // конец оператора проверки имени БД
      else logger.error("DB name is empty! Nothing to search!");
     }
    catch (SQLException e)
     {logger.error(String.format(DBTesterConsts.MSG_ERROR, e.getClass().getName(), e.getMessage()));}
     catch (DBConnectionException e) {
     logger.error(e.getMessage());
    } catch (DBModuleConfigException e) {
     logger.error(e.getMessage());
    }
    return result;
   }

  /**
   * Метод предназначен только для тестирования данного класса.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jlib");
    //Logger logger = Logger.getLogger(DBTester.class.getName());

    // Убираем отладочный вывод universalConnector
    InitLogger.initLogger("jlib.db.universalConnector", Level.INFO);
    // Убираем отладочный вывод universalConfig
    InitLogger.initLogger("jlib.db.universalConfig", Level.INFO);

    /**
    ConnectionConfig mysqlServerConfig = new ConnectionConfig();
    mysqlServerConfig.setDbConnectionType("direct");
    mysqlServerConfig.setDbType("mysql");
    mysqlServerConfig.setDbHost("localhost:3306");
    mysqlServerConfig.setDbName("storm");
    mysqlServerConfig.setDbUser("root");
    mysqlServerConfig.setDbPassword("mysql");

    ConnectionConfig mysqlClientConfig = new ConnectionConfig();
    mysqlClientConfig.setDbConnectionType("direct");
    mysqlClientConfig.setDbType("mysql");
    mysqlClientConfig.setDbHost("localhost:3306");
    mysqlClientConfig.setDbName("storm_client");
    mysqlClientConfig.setDbUser("root");
    mysqlClientConfig.setDbPassword("mysql");

    ConnectionConfig ifxConfig = new ConnectionConfig();
    ifxConfig.setDbConnectionType("direct");
    ifxConfig.setDbType("informix");
    ifxConfig.setDbHost("appserver:1526");
    ifxConfig.setDbServerName("hercules");
    ifxConfig.setDbName("storm_test");
    ifxConfig.setDbUser("informix");
    ifxConfig.setDbPassword("ifx_dba_019");

    ConnectionConfig dbfConfig = new ConnectionConfig();
    dbfConfig.setDbConnectionType("direct");
    dbfConfig.setDbType("dbf");
    dbfConfig.setDbName("q:/new/fleet");
    */
   
   }

 }