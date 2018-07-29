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
  /** Логгер данного класса. */
  private        Logger   logger     = null;
  /** Поле для хранения имени конфиг-файла с параметрами соединения. */
  private static String   configFile = null;
  /** Поле для хранения класса конфигурации соединения с СУБД. */
  private static DBConfig config     = null;

  /***/
  public DBConfigCommonDAO(String loggerName, String configFile)
   {
    // Получаем ссылку на логгер
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // Если класс конфига соединения еще не инициализирован - инициализация
    if (DBConfigCommonDAO.config == null)
     {
      // Инициализация конфига соединения, только есди указано не пустое имя файла параметров
      if (!StringUtils.isBlank(configFile))
       {
        logger.debug("DBConfig is not initialized from file [" + configFile + "]. Processing.");
        // На всякий случай сохраним имя конфиг-файла
        DBConfigCommonDAO.configFile = configFile;
        // Непосредственно загрузка данный из файла конфигурации
        try
         {
          DBConfigCommonDAO.config = new DBConfig(configFile);
          logger.info("DBConfig initialized from file [" + configFile + "].");
         }
        catch (DBModuleConfigException e) {logger.error(e.getMessage());}
        catch (ConfigurationException e)  {logger.error(e.getMessage());}
        catch (IOException e)             {logger.error(e.getMessage());}
       }
      // Если указано пустое имя файла - нет инициализации, в лог - ошибка!
      else {logger.error("Config file name is EMPTY!");}
     }
    // Если класс конфига уже инициализирован - сообщим об этом
    else {logger.debug("DBConfig already initialized!");}
   }

  /***/
  public DBConfigCommonDAO(String loggerName, DBConfig dbConfig)
   {
    // Получаем ссылку на логгер
    if (StringUtils.isBlank(loggerName)) {logger = Logger.getLogger(getClass().getName());}
    else                                 {logger = Logger.getLogger(loggerName);}
    // Если класс конфига соединения еще не инициализирован - инициализация
    if (DBConfigCommonDAO.config == null)
     {
      // Инициализация конфига соединения, только если конфиг не пуст и не содержит ошибок
      if ((dbConfig != null) && StringUtils.isBlank(dbConfig.getConfigErrors()))
       {
        logger.debug("DBConfig is not initialized yet. Processing.");
        DBConfigCommonDAO.config = dbConfig;
        logger.info("DBConfig initialized.");
       }
      // Если указано пустое имя файла - нет инициализации, в лог - ошибка!
      else {logger.error("Received DBConfig is or has errors!");}
     }
    // Если класс конфига уже инициализирован - сообщим об этом
    else {logger.debug("DBConfig already initialized!");}
   }

  /***/
  public Connection getConnection() throws SQLException, DBConnectionException, DBModuleConfigException
   {
    // Если наш конфиг не пуст - получаем от него соединение с СУБД
    if (config != null)
     {
      logger.debug("DBConfig is OK. Getting connection.");
      // Непосредственно получаем соединение с СУБД
      Connection connection = DBUtils.getDBConn(config);
      // Если полученное соединение не пусто - возвращаем его
      if (connection != null) {return connection;}
      // Если же соединение пусто - ошибка
      else {throw new SQLException("Connection received from DBConfig is empty! Config file [" + configFile + "].");}
     }
    // Если же конфиг пуст - ошибка!
    else {throw new SQLException("DBConfig is null (maybe not initialized)!");}
   }

  /***/
  public static DBConfig getConfig() {return config;}

 }