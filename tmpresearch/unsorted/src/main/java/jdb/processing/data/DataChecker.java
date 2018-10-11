package jdb.processing.data;

import jdb.DBConsts;
import jdb.exceptions.DBConnectionException;
import jdb.processing.data.helpers.DataProcessingHelper;
import jdb.processing.sql.execution.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс реализует методы для различных проверок данных в БД. Методы данного класса не изменяют данные в таблицах БД.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 16.02.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class DataChecker
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(DataChecker.class.getName());

  /**
   * Метод проверяет в таблице tableName с ключевым полем keyFieldName существование записи с идентификатором (значением
   * ключевого поля) keyFieldValue. Метод использует уже установленное соединение с СУБД - connection. При ошибках в
   * работе метода - возникают ИС SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку.
   * Используемое методом соединение после работы метода НЕ закрывается.
   * @param connection Connection используемое соединение с БД для проверки данных.
   * @param tableName String имя таблицы, в которой проверяется существование записи.
   * @param keyFieldName String наименование ключевого поля таблицы. Поле должно быть целочисленным.
   * @param keyFieldValue int целочисленной значение ключеового поля таблицы.
   * @return boolean значение ИСТИНА/ЛОЖЬ в зависимости от того, существует запись или нет.
   * @throws SQLException ИС, возникающая в процессе проверки данных.
   * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
  */
  public static boolean isRecordExists(Connection connection, String tableName, String keyFieldName,
   int keyFieldValue) throws SQLException, DBConnectionException
   {
    // Проверяем соединение
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}
    // Результат проверки существования записи
    boolean result = false;
    // Проверка параметров
    String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName);
    if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}
    // Генерируем запрос для проверки данных
    String sql = "select " + keyFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    // Выполняем запрос и если результат не пуст - возвращаем значение ИСТИНА
    ResultSet rs = SqlExecutor.executeSelectQuery(connection, sql);
    if (rs.next()) {result = true;}
    // Отладочный вывод (результат выполнения запроса и сам запрос)
    logger.debug("In table [" + tableName + "] record with [" + keyFieldName + " = " + keyFieldValue +
                 "] exists: [" + result + "]. SQL [" + sql + "].");
    return result;
   }

  /**
   * Метод проверяет в таблице tableName с ключевым полем DBFieldsConsts.KEY_FIELD_NAME (значение по умолчанию = ID)
   * существование записи с идентификатором (значением ключевого поля) keyFieldValue. Метод использует уже установленное
   * соединение с СУБД - connection. При ошибках в работе метода - возникают ИС SQLException, DBConnectionException,
   * сообщения которых описывают возникшую ошибку. Используемое методом соединение после работы метода НЕ закрывается.
   * @param connection Connection используемое соединение с БД для проверки данных.
   * @param tableName String имя таблицы, в которой проверяется существование записи.
   * @param keyFieldValue int целочисленной значение ключеового поля таблицы.
   * @return boolean значение ИСТИНА/ЛОЖЬ в зависимости от того, существует запись или нет.
   * @throws SQLException ИС, возникающая в процессе проверки данных.
   * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
  */
  public static boolean isRecordExists(Connection connection, String tableName, int keyFieldValue)
   throws DBConnectionException, SQLException
    {return DataChecker.isRecordExists(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue);}

  /**
   * Метод проверяет в таблице tableName с ключевым полем keyFieldName существование записи с идентификатором (значением
   * ключевого поля) keyFieldValue. Метод использует источник данных (DataSource) для получения соединения с СУБД. При
   * ошибках в работе метода - возникают ИС SQLException, DBConnectionException, сообщения которых описывают возникшую
   * ошибку.
   * @param dataSource DataSource источник данных для получения соединения с СУБД.
   * @param tableName String имя таблицы, в которой проверяется существование записи.
   * @param keyFieldName String наименование ключевого поля таблицы. Поле должно быть целочисленным.
   * @param keyFieldValue int целочисленной значение ключеового поля таблицы.
   * @return boolean значение ИСТИНА/ЛОЖЬ в зависимости от того, существует запись или нет.
   * @throws SQLException ИС, возникающая в процессе проверки данных.
   * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
  */
  public static boolean isRecordExists(DataSource dataSource, String tableName, String keyFieldName,
   int keyFieldValue) throws SQLException, DBConnectionException
   {
    boolean    result     = false;
    Connection connection = null;
    try
     {
      // Устанавливаем соединение (если источник данных не пуст)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // Для получения результата вызываем другой метод
      result = DataChecker.isRecordExists(connection, tableName, keyFieldName, keyFieldValue);
     }
    // Закрываем соединение в любом случае (пытаемся)
    finally {if (connection != null) {connection.close();}}
    return result;
   }

  /**
   * Метод проверяет в таблице tableName с ключевым полем DBFieldsConsts.KEY_FIELD_NAME (значение по умолчанию = ID)
   * существование записи с идентификатором (значением ключевого поля) keyFieldValue. Метод использует источник данных
   * (DataSource) для получения соединения с СУБД. При ошибках в работе метода - возникают ИС SQLException,
   * DBConnectionException, сообщения которых описывают возникшую ошибку.
   * @param dataSource DataSource источник данных для получения соединения с СУБД.
   * @param tableName String имя таблицы, в которой проверяется существование записи.
   * @param keyFieldValue int целочисленной значение ключеового поля таблицы.
   * @return boolean значение ИСТИНА/ЛОЖЬ в зависимости от того, существует запись или нет.
   * @throws SQLException ИС, возникающая в процессе проверки данных.
   * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
  */
  public static boolean isRecordExists(DataSource dataSource, String tableName, int keyFieldValue)
   throws SQLException, DBConnectionException
   {
    boolean    result     = false;
    Connection connection = null;
    try
     {
      // Устанавливаем соединение (если источник данных не пуст)
      if (dataSource != null) {connection = dataSource.getConnection();}
      else                    {throw new SQLException("Data source is NULL!");}
      // Для получения результата вызываем другой метод
      result = DataChecker.isRecordExists(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue);
     }
    // Закрываем соединение в любом случае (пытаемся)
    finally {if (connection != null) {connection.close();}}
    return result;
   }

 }