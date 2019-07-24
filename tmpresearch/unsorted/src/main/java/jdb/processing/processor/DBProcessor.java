package jdb.processing.processor;

import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.config.DBType;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.structure.DBStructureModel;
import jdb.processing.DBCommonProcessor;
import jdb.processing.spider.DBSpider;
import jdb.processing.sql.generation.SQLGenerator;
import jdb.utils.DBUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Данный класс предназначен для работы с БД и СУБД. Класс должен выполнять различные манипуляции над БД и СУБД.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 02.09.2008)
 * @deprecated методы класса не реализованы. Будет или удален или расширен в следующих версиях библиотеки.
*/

public class DBProcessor extends DBCommonProcessor
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * Конструктор по умолчанию. Инициализирует текущую конфигурацию.
   * @param config ConnectionConfig конфигурация модуля.
   * @throws DBModuleConfigException ИС возникает, если конструктору передана пустая конфигурация.
  */
  public DBProcessor(DBConfig config) throws DBModuleConfigException {super(config);}

  /**
   *
   * @throws SQLException - ошибка при подключении к СУБД или при выполнении запроса на получение метаданных.
   * @throws DBModuleConfigException ошибки конфигурирования соединения с СУБД.
   * @throws DBConnectionException ошибки соединения с СУБД.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public boolean createDB(String dbName, DBStructureModel db, DBType targetDBType, boolean usePrimaryKey)
   throws SQLException, DBModuleConfigException, DBConnectionException
   {
    boolean result = false;
    // Работаем только если указанная модель БД не пуста
    if (db != null)
     {
      // Проверяем существование указанной БД - если существует - ничего не делаем!
      DBSpider spider = new DBSpider(getConfig());
      // Имя создаваемой БД - выбираем между указанным именем и именем из модели БД
      String localDBName;
      if ((dbName != null) && (!dbName.trim().equals(""))) {localDBName = dbName;}
      else {localDBName = db.getDbName();}
      // Непосредственное создание БД осуществляется только если конечной БД не существует
      if (!spider.isDBExists(localDBName))
       {
        // Получим sql-запрос для создания указанной БД
        ArrayList<String> sql    = SQLGenerator.getCreateDBSQL(localDBName, db, targetDBType, usePrimaryKey, true, false);
        // Подготовка к выполнению sql-запроса
        Connection    connection = null;
        Statement     statement  = null;
        // Непосредственно выполнение запроса
        try
         {
          // Получем содиеис СУБД
          connection = DBUtils.getDBConn(getConfig());
          // Создаем объект statement
          statement  = connection.createStatement();
          // В цикле выполняем создание БД (выполняем созданный скрипт по одному запросу)
          if ((sql != null) && (!sql.isEmpty()))
           {
            for (String currentQuery : sql)
             {
              try
               {statement.executeUpdate(currentQuery);}
              catch (SQLException e)
               {logger.error("Can't execute sql [" + currentQuery + "]! Reason [" + e.getMessage() + "].");}
             }
           }
          else {logger.fatal("Query [CREATE DATABASE...] is empty!");}
         }
        // Обязательно освободим ресурсы
        finally {if (statement != null) statement.close(); if (connection != null) connection.close();}
       }
     }
    return result;
   }

}