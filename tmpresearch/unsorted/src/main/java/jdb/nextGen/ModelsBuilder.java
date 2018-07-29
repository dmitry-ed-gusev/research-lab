package jdb.nextGen;

import jdb.DBConsts;
import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.nextGen.exceptions.JdbException;
import jdb.nextGen.models.SimpleDBIntegrityModel;
import jdb.nextGen.models.SimpleDBTimedModel;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс-построитель моделей БД. Класс нерасширяемый и не инстанцируемый, т.к. является утилитарным.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 3.0 (DATE: 31.05.2011)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public final class ModelsBuilder
 {
  private static Logger logger = Logger.getLogger(ModelsBuilder.class.getName());

  // Предотвращение наследования и инстанцирования
  private ModelsBuilder() {}

  /***/
  public static Timestamp getTimestampForTable(Connection conn, String tableName) throws JdbException, SQLException
   {
    Timestamp timestamp = null;
    if (conn != null)
     {
      if (!StringUtils.isBlank(tableName))
       {
        String    sql = "select max(" + DBConsts.FIELD_NAME_TIMESTAMP + ") from " + tableName;
        ResultSet rs  = null;
        try
         {
          rs = conn.createStatement().executeQuery(sql);
          if (rs.next()) {timestamp = rs.getTimestamp(1);}
         }
        finally {if (rs != null) {rs.close();}}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // Возвращаем результат
    return timestamp;
   }

  /***/
  public static SimpleDBTimedModel getDBTimedModel(Connection conn, String dbName, ArrayList<String> tables)
   throws JdbException, SQLException
   {
    SimpleDBTimedModel model;
    if (conn != null)
     {
      if (!StringUtils.isBlank(dbName))
       {
        if ((tables != null) && (!tables.isEmpty()))
         {
          // Непосредственно обработка - все проверки пройдены
          model          = new SimpleDBTimedModel(dbName);
          String    sql  = "select max(" + DBConsts.FIELD_NAME_TIMESTAMP + ") from ";
          Statement stmt = null;
          ResultSet rs   = null;
          try
           {
            stmt  = conn.createStatement();
            for (String table : tables)
             {
              if (!StringUtils.isBlank(table))
               {
                // Конструкция try->catch - для перехвата ИС (чтобы не прервалась обработка).
                try
                 {
                  rs = stmt.executeQuery(sql + table);
                  // Если есть результат - обработаем его и добавим таблицу в список
                  if (rs.next()) {model.addTable(table, rs.getTimestamp(1));}
                  // Если нет данных - таблица также добавляется в список
                  else {model.addTable(table, null);}
                 }
                // Перехват ИС нужен здесь для того, чтобы неудача при построении модели одной таблицы обрушила весь цикл
                catch (SQLException e) {logger.error("Processing table: [" + table + "]. Message: " + e.getMessage());}
               }
             }
           }
          // Освобождение ресурсов
          finally {if (rs != null) {rs.close();} if (stmt != null) {stmt.close();}}
         }
        else {throw new JdbException("Empty tables list!");}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // Возвращаем результат
    return model;
   }

  /***/
  public static SimpleDBIntegrityModel getDBIntegrityModel(Connection conn, String dbName, ArrayList<String> tables)
   throws JdbException, SQLException
   {
    SimpleDBIntegrityModel model;
    if (conn != null)
     {
      if (!StringUtils.isBlank(dbName))
       {
        if ((tables != null) && (!tables.isEmpty()))
         {
          // Непосредственно обработка - все проверки пройдены
          model          = new SimpleDBIntegrityModel(dbName);
          String    sql  = "select " + DBConsts.FIELD_NAME_KEY + " from ";
          Statement stmt = null;
          ResultSet rs   = null;
          try
           {
            stmt  = conn.createStatement();
            for (String table : tables)
             {
              if (!StringUtils.isBlank(table))
               {
                // Конструкция try->catch - для перехвата ИС (чтобы не прервалась обработка).
                try
                 {
                  rs = stmt.executeQuery(sql + table);
                  // Если есть результат - обработаем его и добавим таблицу в список
                  if (rs.next())
                   {
                    ArrayList<Integer> keys = new ArrayList<Integer>();
                    do {keys.add(rs.getInt(1));} while(rs.next());
                    model.addTable(table, keys);
                    logger.debug("Integrity model for [" + table + "]. Keys count: " + keys.size());
                   }
                  // Если нет данных - такая таблица также добавляется в список
                  else {model.addTable(table, null);}
                 }
                // Перехват ИС нужен здесь для того, чтобы неудача при построении модели одной таблицы обрушила весь цикл
                catch (SQLException e) {logger.error("Processing table: [" + table + "]. Message: " + e.getMessage());}
               }
             }
           }
          // Освобождение ресурсов
          finally {if (rs != null) {rs.close();} if (stmt != null) {stmt.close();}}
         }
        else {throw new JdbException("Empty tables list!");}
       }
      else {throw new JdbException("Empty db name!");}
     }
    else {throw new JdbException("Empty db connection!");}
    // Возвращаем результат
    return model;
   }

  /**
   * Метод возвращает те ключи, которые есть в серверном списке, но нет в клиентском. Если серверный список пуст - метод
   * возвращает NULL. Если клиентский список пуст - метод возвращает серверный список полностью (если он не пуст). Если
   * пусты оба списка - метод вернет значение NULL. Если же не пусты оба списка, то метод вернет списко ключей, которые
   * ЕСТЬ в серверном списке, но НЕТ в клиентском. Ключи, которые есть в клиентском списке, но нет в серверном в результат
   * НЕ ПОПАДАЮТ!
  */
  private static ArrayList<Integer> getIntegrityDifference(ArrayList<Integer> serverKeysList, ArrayList<Integer> clientKeysList)
   {
    ArrayList<Integer> list = null;
    if ((serverKeysList != null) && (!serverKeysList.isEmpty()))
     {
      if ((clientKeysList != null) && (!clientKeysList.isEmpty()))
       {
        // Инициализация
        list = new ArrayList<Integer>();
        // Проходим по серверному списку
        for (Integer key : serverKeysList)
         {if ((key != null) && (!clientKeysList.contains(key))) {list.add(key);}}
        // Если в результат ничо не добавлено - обНУЛЛим результат
        if (list.isEmpty()) {list = null;}
       }
     }
    return list;
   }

  /**
   * Метод возвращает модель целостности БД, которая состоит из разницы между серверной моделью и клиентской. В результат
   * попадают те таблицы из СЕРВЕРНОЙ (и только из нее!) модели, в которых есть ключи, отсутствующие в соответствующих
   * моделях таблиц клиентской БД. Если серверная модель пуста - метод возвращает значение NULL. Если клиентская модель
   * пуста - метод также возвращает значение NULL. Важно! В результат могут попасть только те таблицы, которые есть и
   * в серверной модели, и в клиентской!
   * Имя БД для результирующей модели берется из серверной модели БД.
  */
  public static SimpleDBIntegrityModel getDBIntegrityDifference(SimpleDBIntegrityModel serverModel,
   SimpleDBIntegrityModel clientModel) throws JdbException {
    SimpleDBIntegrityModel model = null;
    if ((serverModel != null) && (!serverModel.isEmpty()) && (clientModel != null) && (!clientModel.isEmpty()))
     {
      // Проходим по списку таблиц серверной модели.
      for (String tableName : serverModel.getTablesList())
       {
        // Если в клиентской модели содержится такая же таблица - сравниваем, если же
        // нет, то таблица просто пропускается
        if (clientModel.containsTable(tableName))
         {
          // Получаем разность в ключах таблиц
          ArrayList<Integer> keys = ModelsBuilder.getIntegrityDifference(serverModel.getKeysListForTable(tableName),
           clientModel.getKeysListForTable(tableName));
          // Если разность существует, формируем результат
          if ((keys != null) && (!keys.isEmpty()))
           {
            // Инициализация модели-результата
            if (model == null) {model = new SimpleDBIntegrityModel(serverModel.getDbName());}
            // Добавление результата в конечную модель разности БД
            model.addTable(tableName, keys);
           }
         }
       }
     }
    return model;
   }

  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[]{"jdb", "jlib", "org"});
    Logger logger = Logger.getLogger("jdb");

    // Таблицы системы Шторм - для обновления
    ArrayList<String> STORM_SYNC_TABLES_LIST = new ArrayList<String>(Arrays.asList
     (
      "KEEL_LAYING", "SHIP_LENGTH", "SHIP_DELIVERY", "SHIP_BUILD", "SHIP_DWT", "SHIP_LENGTH_MK", "SHIP_SIZE", "RULESET",
      "STRAN", "SHIP_TYPE_2_RULESET", "SHIP_TYPE", "SHIP_TYPE_2_FLEET", "SHIP_AGE", "SURVEY_ASPECT", "SURVEY_OCCASION",
      "SURVEY_TYPE", "SPECIALIZATION", "ITEMS", "ITEM_2_RULESET", "MISC_2_RULESET", "SHIP_MISC", "FLEET_2_MISC",
      "SURVEY_ACTION", "TOPIC", "TOPIC_PARENTS", "VERSION_CLIENT", "VERSION_CLIENT_CONTENT", "VERSION_MODULES"
     ));

    try
     {
      DBConfig config      = new DBConfig("jdb_java_module/dbConfigs/mysqlStormConfig.xml");
      DBConfig msSqlConfig = new DBConfig("jdb_java_module/dbConfigs/mssqlProductionStormConfig.xml");
      Connection conn      = DBUtils.getDBConn(config);
      Connection msSqlConn = DBUtils.getDBConn(msSqlConfig);
      logger.info("started");
      logger.info("finished");
     }
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    catch (DBConnectionException e)   {logger.error(e.getMessage());}
    catch (ConfigurationException e)  {logger.error(e.getMessage());}
    catch (IOException e)             {logger.error(e.getMessage());}
    //catch (SQLException e)            {logger.error(e.getMessage());}
    //catch (JdbException e)            {logger.error(e.getMessage());}

   }

 }