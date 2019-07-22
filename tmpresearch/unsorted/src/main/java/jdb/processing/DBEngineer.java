package jdb.processing;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModelException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.structure.DBStructureModel;
import jdb.model.time.DBTimedModel;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.integrity.DBIntegrityChecker;
import jdb.processing.modeling.DBModeler;
import jdb.processing.spider.DBSpider;
import jdb.processing.tester.DBTesterConsts;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 17.03.2009)
 *
 * @deprecated данный класс не рекомендуется к использованию. Большая часть его методов не используется. Класс будет
 * удален в последующих релизах библиотеки. Некоторые употребительные методы данного класса будут находиться в классе
 * {@link jdb.processing.spider.DBSpider DBSpider}.
*/

public class DBEngineer
 {
  /** Компонент-логгер данного класса. */
  private Logger             logger           = Logger.getLogger(getClass().getName());
  /** Текущий конфиг БД-процессора. */
  private DBConfig           config           = null;
  
  /***/
  private DBIntegrityChecker integrityChecker = null;
  /***/
  private DBModeler          modeler          = null;
  /***/
  //private DBProcessor        processor        = null;
  /***/
  private DBSpider           spider           = null;

  /**
   * Конструктор.
   * @param config JdbcConfig конфиг для соединения класса с СУБД.
   * @throws jdb.exceptions.DBModuleConfigException ошибка - указан пустой(null) кофиг.
  */
  public DBEngineer(DBConfig config) throws DBModuleConfigException
   {
    String configErrors = DBUtils.getConfigErrors(config);
    if (StringUtils.isBlank(configErrors)) {this.config = config;}
    else {throw new DBModuleConfigException(configErrors);}
   }

  public DBConfig getConfig() {return config;}

  /***/
  public ArrayList<String> makeIntegrity(DBIntegrityModel db, DBProcessingMonitor monitor)
   throws DBConnectionException, SQLException, DBModuleConfigException, DBModelException
   {
    // Если еще не инициализировано поле DBIntegrityChecker - инициализируем его
    if (integrityChecker == null) {integrityChecker = new DBIntegrityChecker(config);}
    return integrityChecker.makeIntegrity(db, monitor);
   }
  
  /***/
  public ArrayList<String> makeIntegrity(DBIntegrityModel db)
   throws DBConnectionException, SQLException, DBModuleConfigException, DBModelException
   {
    // Если еще не инициализировано поле DBIntegrityChecker - инициализируем его
    if (integrityChecker == null) {integrityChecker = new DBIntegrityChecker(config);}
    return integrityChecker.makeIntegrity(db);
   }

  /***/
  public DBStructureModel getDBStructureModel()
   throws DBConnectionException, SQLException, DBModuleConfigException, DBModelException
   {
    // Если еще не инициализировано поле DBModeler - инициализируем его
    if (modeler == null) {modeler = new DBModeler(config);}
    return modeler.getDBStructureModel();
   }

  /***/
  public DBIntegrityModel getDBIntegrityModel()
   throws DBConnectionException, SQLException, DBModuleConfigException, DBModelException
   {
    // Если еще не инициализировано поле DBModeler - инициализируем его
    if (modeler == null) {modeler = new DBModeler(config);}
    return modeler.getDBIntegrityModel();
   }

  /***/
  public DBTimedModel getDBTimedModel()
   throws DBConnectionException, SQLException, DBModuleConfigException, DBModelException
   {
    // Если еще не инициализировано поле DBModeler - инициализируем его
    if (modeler == null) {modeler = new DBModeler(config);}
    return modeler.getDBTimedModel();
   }

  /**
  public boolean createDB(String dbName, DBStructureModel db, DBType targetDBType, boolean usePrimaryKey)
   throws DBConnectionException, SQLException, DBModuleConfigException
   {
    // Если еще не инициализировано поле DBProcessor - инициализируем его
    if (processor == null) {processor = new DBProcessor(config);}
    return processor.createDB(dbName, db, targetDBType, usePrimaryKey);
   }
  */
  
  /**
   * Проверка соединения с БД. Возвращает описание ошибки - если не удалось соединиться с БД, если же удалось -
   * метод возвращает значение null.
   * @return String описание ошибки или значение null (если все в порядке).
  */
  public String testDBMSConnection()
   {
    String     result     = null;
    Connection connection = null;
    try
     {
      connection = DBUtils.getDBConn(this.config);
      // Если полученный объект Connection пуст - соединение мы не установили (почему - ХЗ?)
      if (connection == null) {throw new SQLException("Can't establish a connection! Unknown reason!");}
     }
    // Перехват ИС
    catch (SQLException e)
     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "code: " + e.getErrorCode() +
                             " sqlState: " + e.getSQLState());}
    catch (DBConnectionException e) {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}
    catch (DBModuleConfigException e)     {result = String.format(DBTesterConsts.MSG_ERROR_CONNECT, e.getClass().getName(), e.getMessage(), "");}

    // Если удалось соединиться нужно закрыть за собой соединение
    if (connection != null)
     {try {connection.close();} catch (SQLException e) {logger.error("Can't close connection! [" + e.getMessage() + "]");}}

    return result;
   }

  /***/
  public boolean isTableExists(String tableName, boolean ignoreConstraints)
   throws DBConnectionException, SQLException, DBModuleConfigException
   {
    // Если еще не инициализировано поле DBSpider - инициализируем его
    if (spider == null) {spider = new DBSpider(config);}
    return spider.isTableExists(tableName, ignoreConstraints);
   }

  /***/
  public boolean isAbsTableExists(String tableName)
   throws DBConnectionException, SQLException, DBModuleConfigException
   {
    // Если еще не инициализировано поле DBSpider - инициализируем его
    if (spider == null) {spider = new DBSpider(config);}
    return spider.isAbsTableExists(tableName);
   }

  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger(DBEngineer.class.getName());

    DBConfig mysqlConfig1 = new DBConfig();
    mysqlConfig1.setDbType(DBConsts.DBType.MYSQL);
    mysqlConfig1.setHost("localhost:3306");
    mysqlConfig1.setDbName("storm");
    mysqlConfig1.setUser("root");
    mysqlConfig1.setPassword("mysql");
    try
     {
      DBEngineer engineer = new DBEngineer(mysqlConfig1);
      //logger.info(engineer.isAbsTableExists("items"));
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    //catch (DBConnectionException e) {logger.error(e.getMessage());}
    //catch (SQLException e) {logger.error(e.getMessage());}

   }

 }