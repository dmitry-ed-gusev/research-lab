package jdb.processing.sql.execution;

import jdb.config.connection.BaseDBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс содержит статические методы для выполнения одиночных sql-запросов (любых). 
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 21.06.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class SqlExecutor
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(SqlExecutor.class.getName());

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод может использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql String выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, String sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    logger.debug("executeSelectQuery SQL [" + sql + "].");
    // Если нам передано пустое соединение - ошибка!
    if (connection == null) {throw new DBConnectionException("Connection to DBMS is empty!");}
    // Если нам передан пустой запрос - ошибка!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty sql-query!");}
    // Если запрос неверного типа - не SELECT-запрос - ошибка!
    if (!DBUtils.isSelectQuery(sql)) {throw new SQLException("No SELECT-query for this method!");}
    // Если же все в порядке - выполняем запрос и возвращаем результат.
    Statement statement = connection.createStatement();
    // Для отладки выводим реальный запрос
    String realSql;
    if (useSqlFilter) {realSql = SqlFilter.removeDeprecated(sql);}
    else              {realSql = sql;}
    logger.debug("Real sql-query for execution: [" + realSql + "].");
    // Выполняем запрос
    return statement.executeQuery(realSql);
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql String выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, String sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод может использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuffer выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException 
   {
    // Проверка на null-значение необходима, т.к. если sql==null, то конструкция sql.toString() будет вызывать ИС
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeSelectQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuffer выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuffer sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод может использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuilder выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeSelectQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор ResultSet. Если результат выполнения
   * запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод использует уже установленное соединение,
   * которое НЕ закрывается после выполнения запроса. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuilder выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return ResultSet курсор, полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static ResultSet executeSelectQuery(Connection connection, StringBuilder sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeSelectQuery(connection, sql, true);}

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует уже установленное соединение, которое НЕ закрывается после выполнения запроса. При ошибках в параметрах
   * (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод может использовать фильтрацию
   * sql-запросов (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql String выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static String executeStrSelectQuery(Connection connection, String sql, boolean useSqlFilter)
   throws DBConnectionException, SQLException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    // Получаем курсор (или ИС если ничего не получилось)
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
    // Преобразуем курсор в строку и возвращаем
    return DBUtils.getStringResultSet(rs);
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует уже установленное соединение, которое НЕ закрывается после выполнения запроса. При ошибках в параметрах
   * (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод всегда использует фильтрацию
   * sql-запросов.
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql String выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static String executeStrSelectQuery(Connection connection, String sql)
   throws DBConnectionException, SQLException {return SqlExecutor.executeStrSelectQuery(connection, sql, true);}

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует уже установленное соединение, которое НЕ закрывается после выполнения запроса. При ошибках в параметрах
   * (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод может использовать фильтрацию
   * sql-запросов (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuffer выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static String executeStrSelectQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws DBConnectionException, SQLException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    // Получаем курсор (или ИС если ничего не получилось)
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
    // Преобразуем курсор в строку и возвращаем
    return DBUtils.getStringResultSet(rs);
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует уже установленное соединение, которое НЕ закрывается после выполнения запроса. При ошибках в параметрах
   * (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод всегда использует фильтрацию
   * sql-запросов.
   * @param connection Connection соединение с СУБД, через которое выполняем запрос. Если соединение пусто, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql StringBuffer выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
  */
  public static String executeStrSelectQuery(Connection connection, StringBuffer sql)
   throws DBConnectionException, SQLException {return SqlExecutor.executeStrSelectQuery(connection, sql, true);}

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует указанную в параметрах конфигурацию соединения - перед выполнением запроса соединяемся с БД.
   * При ошибках в параметрах (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод
   * может использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param config BaseDBConfig конфигурация соединения с СУБД. Если конфигурация пуста или неверна, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
   * @throws jdb.exceptions.DBModuleConfigException неверный или пустой конфиг для соединения с СУБД.
  */
  public static String executeStrSelectQuery(BaseDBConfig config, String sql, boolean useSqlFilter)
   throws SQLException, DBModuleConfigException, DBConnectionException
   {
    logger.debug("executeStrSelectQuery: executing query [" + sql + "].");
    String result = null;

    // Если нам передан конфиг с ошибками, возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Если же конфиг в порядке - выведем отладочной сообщение
    else {logger.debug("DBMS connection config is OK. Processing.");}

    // Если нам передан пустой запрос - ошибка!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty query!");}
    // Если запрос неверного типа - не SELECT-запрос - ошибка!
    if (!DBUtils.isSelectQuery(sql)) {throw new SQLException("No SELECT-query for this method!");}
    // Если же все в порядке - выполняем запрос и возвращаем результат.
    Connection connection = null;
    try
     {
      // Получаем соединение
      connection = DBUtils.getDBConn(config);
      // Получаем курсор (или ИС если ничего не получилось)
      ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql, useSqlFilter);
      // Преобразуем запрос в строку
      result = DBUtils.getStringResultSet(rs);
     }
    // Даже если запрос выполнился неудачно - соединение необходимо попытаться закрыть!
    finally {if (connection != null) {connection.close();}}
    return result;
   }

  /**
   * Метод выполняет один "SELECT..." запрос и возвращает результирующий курсор (ResultSet) в строковом виде (String).
   * Если результат выполнения запроса будет пуст, то метод вернет значение null. Для выполнения запроса метод
   * использует указанную в параметрах конфигурацию соединения - перед выполнением запроса соединяемся с БД.
   * При ошибках в параметрах (конфигурация соединения с СУБД или неверном sql-запросе) будет сгенерирована ИС. Метод
   * всегда использует фильтрацию sql-запросов.
   * @param config BaseDBConfig конфигурация соединения с СУБД. Если конфигурация пуста или неверна, то запрос
   * выполнен не будет, а будет сгенерирована ИС.
   * @param sql выполняемый SELECT-запрос. Если запрос не SELECT-типа, то будет выдана ошибка (ИС)!
   * @return String курсор (в строковом виде), полученный в результате выборки из БД или значение null.
   * @throws SQLException ошибка при создании объекта Statement или ошибочных параметрах метода.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД (соединение пусто).
   * @throws jdb.exceptions.DBModuleConfigException неверный или пустой конфиг для соединения с СУБД.
  */
  public static String executeStrSelectQuery(BaseDBConfig config, String sql)
   throws SQLException, DBModuleConfigException, DBConnectionException {return SqlExecutor.executeStrSelectQuery(config, sql, true);}
  
  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного в конфиге config соединения с СУБД. Метод может
   * использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param config BaseDBConfig конфиг для соединения с СУБД.
   * @param sql выполняемый запрос.
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(BaseDBConfig config, String sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("executeUpdate: executing query [" + sql + "].");
    int result = 0;

    // Если нам передан конфиг с ошибками, возбуждаем ИС!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    // Если же конфиг в порядке - выведем отладочной сообщение
    else {logger.debug("DBMS connection config is OK. Processing.");}
    
    // Если нам передан пустой запрос - ошибка!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty query!");}
    // Если запрос неверного типа - SELECT-запрос - ошибка!
    if (DBUtils.isSelectQuery(sql)) {throw new SQLException("SELECT-query for this method is not allowed!");}
    // Если все в порядке - выполняем запрос
    Connection connection = null;
    try
     {
      // Получаем соединение
      connection = DBUtils.getDBConn(config);
      Statement statement = connection.createStatement();
      // Непосредственно выполняем запрос
      if (useSqlFilter) {result = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
      else              {result = statement.executeUpdate(sql);}
     }
    // Даже если запрос выполнился неудачно - соединение необходимо попытаться закрыть!
    finally {if (connection != null) {connection.close();}}
    // Возвращаем результат выполнения запроса
    return result;
   }

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного в конфиге config соединения с СУБД. Метод всегда
   * использует фильтрацию sql-запросов.
   * @param config BaseDBConfig конфиг для соединения с СУБД.
   * @param sql выполняемый запрос.
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(BaseDBConfig config, String sql)
   throws SQLException, DBConnectionException, DBModuleConfigException {return SqlExecutor.executeUpdateQuery(config, sql, true);}

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного в конфиге config соединения с СУБД. Метод может
   * использовать фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param config BaseDBConfig конфиг для соединения с СУБД.
   * @param sql StrinBuilder выполняемый запрос.
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(BaseDBConfig config, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException, DBModuleConfigException
   {
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(config, sql.toString(), useSqlFilter);}
   }

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного в конфиге config соединения с СУБД. Метод всегда
   * использует фильтрацию sql-запросов (см. параметр useSqlFilter).
   * @param config BaseDBConfig конфиг для соединения с СУБД.
   * @param sql выполняемый запрос.
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(BaseDBConfig config, StringBuilder sql)
   throws DBConnectionException, DBModuleConfigException, SQLException
   {return SqlExecutor.executeUpdateQuery(config, sql, true);}

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод может использовать фильтрацию sql-запросов
   * (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД.
   * @param sql String выполняемый запрос.
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, String sql, boolean useSqlFilter) 
   throws SQLException, DBConnectionException
   {
    logger.debug("executeUpdate: executing query [" + sql + "].");
    // Если нам передано пустое соединение - ошибка!
    if (connection == null) {throw new DBConnectionException("Connection to DBMS is empty!");}
    // Если нам передан пустой запрос - ошибка!
    if (StringUtils.isBlank(sql)) {throw new SQLException("Empty sql-query!");}
    // Если запрос неверного типа - SELECT-запрос - ошибка!
    if (DBUtils.isSelectQuery(sql)) {throw new SQLException("SELECT-query for this method is not allowed!");}
    // Если все в порядке - выполняем запрос
    Statement statement = connection.createStatement();
    int result;
    if (useSqlFilter) {result = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
    else              {result = statement.executeUpdate(sql);}
    return result;
   }

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД.
   * @param sql String выполняемый запрос.
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, String sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод может использовать фильтрацию sql-запросов
   * (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД.
   * @param sql StringBuffer выполняемый запрос.
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, StringBuffer sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    // Проверка на null-значение необходима, т.к. если sql==null, то конструкция sql.toString() будет вызывать ИС
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД.
   * @param sql StringBuffer выполняемый запрос.
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, StringBuffer sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод может использовать фильтрацию sql-запросов
   * (см. параметр useSqlFilter).
   * @param connection Connection соединение с СУБД.
   * @param sql StringBuilder выполняемый запрос.
   * @param useSqlFilter boolean сипользовать или нет фильтрацию sql-запроса перед выполнением
   * (true=использовать/false=не использовать).
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, StringBuilder sql, boolean useSqlFilter)
   throws SQLException, DBConnectionException
   {
    // Проверка на null-значение необходима, т.к. если sql==null, то конструкция sql.toString() будет вызывать ИС
    if ((sql == null) || (StringUtils.isBlank(sql.toString()))) {throw new SQLException("Empty sql-query!");}
    else {return SqlExecutor.executeUpdateQuery(connection, sql.toString(), useSqlFilter);}
   }

  /**
   * Метод выполняет один не-"SELECT..." запрос для указанного соединения с СУБД. Если соединение пусто - возникает
   * ИС DBConnectionException - ошибка соединения с СУБД. Метод всегда использует фильтрацию sql-запросов.
   * @param connection Connection соединение с СУБД.
   * @param sql StringBuilder выполняемый запрос.
   * @return int результат выполнения запроса.
   * @throws java.sql.SQLException ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBConnectionException ошибки соединения с СУБД.
  */
  public static int executeUpdateQuery(Connection connection, StringBuilder sql)
   throws SQLException, DBConnectionException {return SqlExecutor.executeUpdateQuery(connection, sql, true);}
  
 }