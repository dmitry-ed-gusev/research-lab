package jdb.nextGen;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Модуль содержит необходимые утилитарные/системные функции для работы с различными типами СУБД. Класс является
 * финальным - не расширяемым. Также нельзя создать экземпляр данного класса.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 2.0 (DATE: 31.05.2011)
*/

public final class DBPilot
 {
  //
  private static Logger logger = Logger.getLogger(DBPilot.class.getName());

  // Предотвращение наследования и инстанцирования
  private DBPilot() {}

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, сущесвует ли таблица с указанным именем (tableName) в
   * БД, на которую указывает соединение (conn). Если имя таблицы и/или соединение пусто - метод возбуждает ИС
   * (SQLException). Если соединение указывает не на конкретную БД, а в целом на сервер, метод вернет ЛОЖЬ (т.е.
   * соединение должно указывать на конкретную БД на сервере). Поиск таблицы по имени осуществляется БЕЗ учета схем БД
   * (т.е. таблица с указанным именем [table] будет найдена, а таблица с указанным именем [schema].[table] найдена не будет).
   * Еще одна особенность метода - поиск таблицы осуществляется без учета регистра символов, т.е. следующие имена таблиц
   * эквивалентны: table1 и TabLE1 (имена таблиц переводятся в ВЕРХНИЙ регистр символов для сравнения).
   * @param conn Connection
   * @param tableName String
   * @return boolean
   * @throws SQLException ИС - пустое соединение, пустое имя таблицы, ошибки работы с БД.
  */
  public static boolean isTableExists(Connection conn, String tableName) throws SQLException
   {
    logger.debug("DBPilot: isTableExists().");
    boolean result = false;
    // Проверяем соединение с СУБД
    if (conn != null)
     {
      // Проверяем имя таблицы
      if (!StringUtils.isBlank(tableName))
       {
        ResultSet tablesRS   = null;
        try
         {
          DatabaseMetaData metaData   = conn.getMetaData();
          // Перебор всех таблиц указанной БД и добавление их в список.
          tablesRS = metaData.getTables(null, null, null, null);
          // Если список таблиц получен - пройдемся по нему
          while (tablesRS.next() && !result)
           {
            if (tableName.toUpperCase().equals(tablesRS.getString(DBConsts.META_DATA_TABLE_NAME).toUpperCase()))
             {result = true;}
           }
         }
        // Закроем открытые курсоры
        finally
         {
         try {if (tablesRS != null) {tablesRS.close();}}
         catch (SQLException e) {logger.error("Can't free resources! Reason [" + e.getMessage() + "].");}
         }
       }
      // Пустое имя таблицы
      else {throw new SQLException("Empty table name!");}
     }
    // Соединение пусто - ошибка
    else {throw new SQLException("Empty connection!");}
    // Возвращаем результат
    return result;
   }

  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    try
     {
      DBConfig mssqlStormTestConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlAppStormConfig.xml");
      DBConfig mysqlStormConfig     = new DBConfig("jdb_java_module/dbConfigs/mysqlStormConfig.xml");
      Connection mssqlStormTestConn = DBUtils.getDBConn(mssqlStormTestConfig);
      Connection mysqlStormConn = DBUtils.getDBConn(mysqlStormConfig);
      logger.info(DBPilot.isTableExists(mysqlStormConn, "aaa"));
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    catch (SQLException e)            {logger.error(e.getMessage());}
   }

 }