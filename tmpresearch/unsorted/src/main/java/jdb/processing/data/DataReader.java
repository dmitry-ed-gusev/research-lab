package jdb.processing.data;

import jdb.exceptions.DBConnectionException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Модуль содержит методыпростого чтения значений отдельных полей указанной таблицы. Данный модуль предназначен
 * для уменьшения объема кода сложных систем (простые операции выполняются данным модулем).
 * @author Gusev Dmitry
 * @version 2.0 (DATE: 30.11.2010)
*/

public class DataReader
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(DataReader.class.getName());

  /**
   * Чтение строкового значения поля dataFieldName по значению keyFieldValue ключевого поля keyFieldName из таблицы
   * tableName. Если по указанному ключу будет нейдено больше одной записи, то метод прочитает значение из первой
   * найденной записи. Порядок записей в выборке будет соответствовать порядку записей в таблице БД. Метод использует
   * уже созданное и открытое соединение с СУБД. Соединение после окончания работы метода НЕ закрывается. 
   * @param connection Connection соединение, с которым работает метод.
   * @param tableName String таблица, из которой читаем данные.
   * @param keyFieldName String наименование ключевого поля.
   * @param keyFieldValue int значение ключевого поля.
   * @param dataFieldName String наименование поля данных (поле, значение которого читаем).
   * @return String прочитанное знаечние или NULL (если ничо не прочитали :)).
   * @throws SQLException ошибки при выполнении sql-запроса.
   * @throws DBConnectionException ошибки при соединении с СУБД.
  */
  public static String getStringValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName) throws SQLException, DBConnectionException
   {
    String result = null;
    // Проверяем соединение
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}

    // Проверяем параметры
    //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    //if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}

    // Генерируем запрос для чтения поля данных
    String sql = "select " + dataFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // Выполняем запрос и получаем данные
    ResultSet rs = connection.createStatement().executeQuery(sql);
    if (rs.next()) {result = rs.getString(dataFieldName);}
    // Возвращаем полученный результат
    return result;
   }

  /**
   * Чтение целочисленного значения поля dataFieldName по значению keyFieldValue ключевого поля keyFieldName из таблицы
   * tableName. Если по указанному ключу будет нейдено больше одной записи, то метод прочитает значение из первой
   * найденной записи. Порядок записей в выборке будет соответствовать порядку записей в таблице БД. Метод использует
   * уже созданное и открытое соединение с СУБД. Соединение после окончания работы метода НЕ закрывается.
   * @param connection Connection соединение, с которым работает метод.
   * @param tableName String таблица, из которой читаем данные.
   * @param keyFieldName String наименование ключевого поля.
   * @param keyFieldValue int значение ключевого поля.
   * @param dataFieldName String наименование поля данных (поле, значение которого читаем).
   * @return int прочитанное знаечние или 0 (если ничо не прочитали :)).
   * @throws SQLException ошибки при выполнении sql-запроса.
   * @throws DBConnectionException ошибки при соединении с СУБД.
  */
  public static int getIntValue(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
   String dataFieldName) throws SQLException, DBConnectionException
   {
    int result = 0;
    // Проверяем соединение
    if (connection == null) {throw new DBConnectionException("Connection is empty!");}

    // Проверяем параметры
    //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
    //if (!StringUtils.isBlank(paramsCheckResult)) {throw new SQLException(paramsCheckResult);}

    // Генерируем запрос для чтения поля данных
    String sql = "select " + dataFieldName + " from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
    logger.debug("Generated query: " + sql);
    // Выполняем запрос и получаем данные
    ResultSet rs = connection.createStatement().executeQuery(sql);
    if (rs.next()) {result = rs.getInt(dataFieldName);}
    // Если ничего не найдено - мы не смогли прочесть значение, возбуждаем ИС, т.к. вернуть 0 - это означает, что мы
    // его прочли (не отделить данную ситуацию от реально прочитанного значения 0)
    else {throw new SQLException("Value not found!");}
    // Возвращаем полученный результат
    return result;
   }

 }