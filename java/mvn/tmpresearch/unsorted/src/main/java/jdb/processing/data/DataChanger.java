package jdb.processing.data;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import dgusev.dbpilot.utils.DBUtilities;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.processing.sql.execution.SqlExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс реализует несколько методов для изменения значений одиночных полей в таблицах баз данных. Данные методы
 * позволяют сократить конечный объем кода для сложных систем - установить значение вкл/выкл можно с использованием
 * данного кода.
 *
 * @author Gusev Dmitry (019gus)
 * @version 5.0 (DATE: 30.09.2010)
 *
 * @deprecated
 */

public class DataChanger {
    /**
     * Компонент-логгер данного класса.
     */
    private static Logger logger = Logger.getLogger(DataChanger.class.getName());

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setIntValue_(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем параметры
        //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
        //if (!StringUtils.isBlank(paramsCheckResult)) {
        //    throw new SQLException(paramsCheckResult);
        //}
        // Генерируем запрос для изменения данных
        String sql = "update " + tableName + " set " + dataFieldName + " = " + dataFieldValue + " where " +
                keyFieldName + " = " + keyFieldValue;
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения
        return SqlExecutor.executeUpdateQuery(connection, sql);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setIntValue_(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем параметры
//        String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
//        if (!StringUtils.isBlank(paramsCheckResult)) {
//            throw new SQLException(paramsCheckResult);
//        }
        // Генерируем запрос для изменения данных
        String sql = "update " + tableName + " set " + dataFieldName + " = " + dataFieldValue + " where " + keyFieldName;
        // Если значение ключевого поля не пусто - подставляем его в запрос как есть
        if (!StringUtils.isBlank(keyFieldValue)) {
            sql = sql + " = '" + keyFieldValue + "'";
        }
        // Если же значение ключевого поля пусто - его надо подставить в запрос по-другому
        else {
            sql += " is null";
        }
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения
        return SqlExecutor.executeUpdateQuery(connection, sql);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, DBModuleConfigException, сообщения которых описывают возникшую ошибку.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setIntValue_(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение
            connection = DBUtilities.getDBConn(config);
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setIntValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, DBModuleConfigException, сообщения которых описывают возникшую ошибку.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setIntValue_(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение
            connection = DBUtilities.getDBConn(config);
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setIntValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует указанный источник данных (DataSource) для соединения с СУБД. При ошибках в работе метода -
     * возникают ИС SQLException и DBConnectionException, сообщения которых описывают возникшую ошибку.
     *
     * @param dataSource     DataSource используемый источник данных для получения соединения с БД.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setIntValue_(DataSource dataSource, String tableName, String keyFieldName, int keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение (если источник данных не пуст)
            if (dataSource != null) {
                connection = dataSource.getConnection();
            } else {
                throw new SQLException("Data source is NULL!");
            }
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setIntValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует указанный источник данных (DataSource) для соединения с СУБД. При ошибках в работе метода -
     * возникают ИС SQLException и DBConnectionException, сообщения которых описывают возникшую ошибку.
     *
     * @param dataSource     DataSource используемый источник данных для получения соединения с БД.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue int новое целочисленное значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setIntValue_(DataSource dataSource, String tableName, String keyFieldName, String keyFieldValue,
                                  String dataFieldName, int dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение (если источник данных не пуст)
            if (dataSource != null) {
                connection = dataSource.getConnection();
            } else {
                throw new SQLException("Data source is NULL!");
            }
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setIntValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается. Метод может использовать фильтрацию sql-запросов.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @param useSqlFilter   boolean использовать или нет фильтрацию sql-запроса.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setStringValue_(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
                                     String dataFieldName, String dataFieldValue, boolean useSqlFilter) throws SQLException, DBConnectionException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем параметры
        //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
        //if (!StringUtils.isBlank(paramsCheckResult)) {
        //    throw new SQLException(paramsCheckResult);
        //}
        // Генерируем запрос для изменения данных.
        String sql = "update " + tableName + " set " + dataFieldName + " = ";
        // Если значение поля данных не пусто - подставляем его в запрос как есть
        if (!StringUtils.isBlank(dataFieldValue)) {
            // Если используем фильтр sql-запросов, то в строковом значении заменяем кавычки на "разрешенные" (двойные -> ").
            // "Запрещенные" символы будут убраны на этапе фильтрации всего sql-запроса.
            if (useSqlFilter) {
                sql = sql + "'" + SqlFilter.changeQuotes(dataFieldValue) + "'";
            } else {
                sql = sql + "'" + dataFieldValue + "'";
            }
        }
        // Если же значение ключевого поля пусто - его надо подставить в запрос по-другому
        else {
            sql += " null";
        }
        // Окончательная генерация sql-запроса
        sql = sql + " where " + keyFieldName + " = " + keyFieldValue;
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения. Запрос выполняется БЕЗ фильтрации.
        return SqlExecutor.executeUpdateQuery(connection, sql, useSqlFilter);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается. Метод всегда использует фильтрацию sql-запросов.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setStringValue_(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
                                     String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException {
        return DataChanger.setStringValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается. Метод может использовать фильтрацию sql-запросов.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @param useSqlFilter   boolean использовать или нет фильтрацию sql-запроса.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setStringValue_(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
                                     String dataFieldName, String dataFieldValue, boolean useSqlFilter) throws SQLException, DBConnectionException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем параметры
        //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName, dataFieldName);
        //if (!StringUtils.isBlank(paramsCheckResult)) {
        //    throw new SQLException(paramsCheckResult);
        //}

        // Генерируем запрос для изменения данных.
        String sql = "update " + tableName + " set " + dataFieldName + " = ";
        // Если значение поля данных не пусто - подставляем его в запрос как есть
        if (!StringUtils.isBlank(dataFieldValue)) {
            // Если используем фильтр sql-запросов, то в строковом значении заменяем кавычки на "разрешенные" (двойные -> ").
            // "Запрещенные" символы будут убраны на этапе фильтрации всего sql-запроса.
            if (useSqlFilter) {
                sql = sql + "'" + SqlFilter.changeQuotes(dataFieldValue) + "'";
            } else {
                sql = sql + "'" + dataFieldValue + "'";
            }
        }
        // Если же значение ключевого поля пусто - его надо подставить в запрос по-другому
        else {
            sql += " null";
        }
        // Окончательная генерация sql-запроса
        sql = sql + " where " + keyFieldName;
        // Если значение ключевого поля не пусто - подставляем его в запрос как есть
        if (!StringUtils.isBlank(keyFieldValue)) {
            sql = sql + " = '" + keyFieldValue + "'";
        }
        // Если же значение ключевого поля пусто - его надо подставить в запрос по-другому
        else {
            sql += " is null";
        }
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения
        return SqlExecutor.executeUpdateQuery(connection, sql, useSqlFilter);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается. Метод всегда использует фильтрацию sql-запросов.
     *
     * @param connection     Connection используемое соединение с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int setStringValue_(Connection connection, String tableName, String keyFieldName, String keyFieldValue,
                                     String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException {
        return DataChanger.setStringValue_(connection, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Метод может использовать
     * фильтрацию sql-запросов.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @param useSqlFilter   boolean использовать или нет фильтрацию sql-запросов.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setStringValue_(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
                                     String dataFieldName, String dataFieldValue, boolean useSqlFilter)
            throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение
            connection = DBUtilities.getDBConn(config);
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setStringValue_(connection, tableName, keyFieldName, keyFieldValue,
                    dataFieldName, dataFieldValue, useSqlFilter);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - целочисленный параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Метод всегда использует
     * фильтрацию sql-запросов.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  int целочисленное значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setStringValue_(DBConfig config, String tableName, String keyFieldName, int keyFieldValue,
                                     String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        return DataChanger.setStringValue_(config, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Метод может использовать
     * фильтрацию sql-запросов.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @param useSqlFilter   boolean использовать или нет фильтрацию sql-запросов.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setStringValue(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
                                     String dataFieldName, String dataFieldValue, boolean useSqlFilter)
            throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение
            connection = DBUtilities.getDBConn(config);
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.setStringValue_(connection, tableName, keyFieldName, keyFieldValue,
                    dataFieldName, dataFieldValue, useSqlFilter);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение строкового поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение строкового поля для установки - dataFieldValue.
     * Если значение поля для установки null, то соответствующее поле в записи таблицы будет также установлено в null.
     * Метод использует указанную конфигурацию соединения с СУБД - config. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Метод всегда использует
     * фильтрацию sql-запросов.
     *
     * @param config         DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName      String имя таблицы, в которой изменяются данные.
     * @param keyFieldName   String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue  String строковое значение ключевого поля таблицы.
     * @param dataFieldName  String наименование поля, данные которого должны быть изменены.
     * @param dataFieldValue String новое строковое значение для изменяемого поля.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int setStringValue(DBConfig config, String tableName, String keyFieldName, String keyFieldValue,
                                     String dataFieldName, String dataFieldValue) throws SQLException, DBConnectionException, DBModuleConfigException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        return DataChanger.setStringValue(config, tableName, keyFieldName, keyFieldValue, dataFieldName, dataFieldValue, true);
    }

    /**
     * Метод устанавливает статус записи в таблице удалена/активна по указанному ключевому полю. Метод базируется на
     * других методах данного класса (setIntValue()). Если не указано имя ключевого поля или имя поля со статусом
     * удаления, то для этих полей берутся значения по умолчанию (см. класс DBConsts данной библиотеки). Значения
     * статуса удалено/активно - см. константы DBConsts.DELETED_RECORD_STATUS и DBConsts.ACTIVE_RECORD_STATUS, указанное
     * значение корректируется до значения одной из этих констант (если значение != 0, то это статус DBConsts.DELETED_RECORD_STATUS,
     * если же = 0, то это статус DBConsts.ACTIVE_RECORD_STATUS). ИС при работе данного метода - ошибки соединения с СУБД или
     * выполнения запросов. Для соединения с СУБД метод использует уже установленное соединение, которое после выполнения
     * изменения данных НЕ закрывается.
     *
     * @param connection        Connection соединение с СУБД для работы метода.
     * @param tableName         String имя таблицы, в которой меняем статус записи.
     * @param keyFieldName      String наименование ключевого поля таблицы.
     * @param keyFieldValue     int значение ключа записи.
     * @param deletedFieldName  String наименование поля, хранящего статус удаления/активности записи.
     * @param deletedFieldValue int новое значение статуса удаления/активности записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setDeleted(Connection connection, String tableName, String keyFieldName, int keyFieldValue,
                                 String deletedFieldName, int deletedFieldValue) throws SQLException, DBConnectionException {
        logger.debug("DataChanger: setDeleted().");
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Выбираем имя ключевого поля. Если имя поля не указано - выбираем имя по умолчанию.
        String keyField;
        if (StringUtils.isBlank(keyFieldName)) {
            keyField = DBConsts.FIELD_NAME_KEY;
        } else {
            keyField = keyFieldName;
        }
        // Выбираем имя поля со статусом удаления. Если имя поля не указано - выбираем имя по умолчанию.
        String deletedField;
        if (StringUtils.isBlank(deletedFieldName)) {
            deletedField = DBConsts.FIELD_NAME_DELETED;
        } else {
            deletedField = deletedFieldName;
        }
        // Выбираем устанавливаемый статус удаления.
        int deletedValue;
        if (deletedFieldValue != 0) {
            deletedValue = DBConsts.RECORD_STATUS_DELETED;
        } else {
            deletedValue = DBConsts.RECORD_STATUS_ACTIVE;
        }
        return DataChanger.setIntValue_(connection, tableName, keyField, keyFieldValue, deletedField, deletedValue);
    }

    /**
     * Метод устанавливает статус записи в таблице удалена/активна по указанному ключевому полю. Метод базируется на
     * других методах данного класса (setIntValue()). Если не указано имя ключевого поля или имя поля со статусом
     * удаления, то для этих полей берутся значения по умолчанию (см. класс DBConsts данной библиотеки). Значения
     * статуса удалено/активно - см. константы DBConsts.DELETED_RECORD_STATUS и DBConsts.ACTIVE_RECORD_STATUS, указанное
     * значение корректируется до значения одной из этих констант (если значение != 0, то это статус DBConsts.DELETED_RECORD_STATUS,
     * если же = 0, то это статус DBConsts.ACTIVE_RECORD_STATUS). ИС при работе данного метода - ошибки соединения с СУБД или
     * выполнения запросов. Для соединения с СУБД метод использует источник данных (DataSource).
     *
     * @param dataSource        DataSource источник данных для получения соединения с сервером БД.
     * @param tableName         String имя таблицы, в которой меняем статус записи.
     * @param keyFieldName      String наименование ключевого поля таблицы.
     * @param keyFieldValue     int значение ключа записи.
     * @param deletedFieldName  String наименование поля, хранящего статус удаления/активности записи.
     * @param deletedFieldValue int новое значение статуса удаления/активности записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setDeleted_(DataSource dataSource, String tableName, String keyFieldName, int keyFieldValue,
                                 String deletedFieldName, int deletedFieldValue) throws SQLException, DBConnectionException {
        logger.debug("DataChanger: setDeleted().");
        // Проверяем источник данных
        if (dataSource == null) {
            throw new DBConnectionException("DataSource is NULL!");
        }
        // Выбираем имя ключевого поля. Если имя поля не указано - выбираем имя по умолчанию.
        String keyField;
        if (StringUtils.isBlank(keyFieldName)) {
            keyField = DBConsts.FIELD_NAME_KEY;
        } else {
            keyField = keyFieldName;
        }
        // Выбираем имя поля со статусом удаления. Если имя поля не указано - выбираем имя по умолчанию.
        String deletedField;
        if (StringUtils.isBlank(deletedFieldName)) {
            deletedField = DBConsts.FIELD_NAME_DELETED;
        } else {
            deletedField = deletedFieldName;
        }
        // Выбираем устанавливаемый статус удаления.
        int deletedValue;
        if (deletedFieldValue != 0) {
            deletedValue = DBConsts.RECORD_STATUS_DELETED;
        } else {
            deletedValue = DBConsts.RECORD_STATUS_ACTIVE;
        }
        return DataChanger.setIntValue_(dataSource, tableName, keyField, keyFieldValue, deletedField, deletedValue);
    }

    /**
     * Метод устанавливает статус записи в таблице удалена/активна по указанному ключевому полю. Метод базируется на
     * других методах данного класса (setIntValue(), setDeleted()). Имя ключевого поля и имя поля со статусом
     * удаления берутся по умолчанию (см. класс DBConsts данной библиотеки). Значения статуса удалено/активно - см.
     * константы DBConsts.DELETED_RECORD_STATUS и DBConsts.ACTIVE_RECORD_STATUS, указанное значение корректируется до
     * значения одной из этих констант (если значение != 0, то это статус DBConsts.DELETED_RECORD_STATUS, если же = 0,
     * то это статус DBConsts.ACTIVE_RECORD_STATUS). ИС при работе данного метода - ошибки соединения с СУБД или
     * выполнения запросов. Для соединения с СУБД метод использует уже установленное соединение, которое после выполнения
     * изменения данных НЕ закрывается.
     *
     * @param connection        Connection соединение с СУБД для работы метода.
     * @param tableName         String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue     int значение ключа записи.
     * @param deletedFieldValue int новое значение статуса удаления/активности записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setDeleted(Connection connection, String tableName, int keyFieldValue, int deletedFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted(connection, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue,
                DBConsts.FIELD_NAME_DELETED, deletedFieldValue);
    }

    /**
     * Метод устанавливает статус записи в таблице удалена/активна по указанному ключевому полю. Метод базируется на
     * других методах данного класса (setIntValue(), setDeleted()). Имя ключевого поля и имя поля со статусом
     * удаления берутся по умолчанию (см. класс DBConsts данной библиотеки). Значения статуса удалено/активно - см.
     * константы DBConsts.DELETED_RECORD_STATUS и DBConsts.ACTIVE_RECORD_STATUS, указанное значение корректируется до
     * значения одной из этих констант (если значение != 0, то это статус DBConsts.DELETED_RECORD_STATUS, если же = 0,
     * то это статус DBConsts.ACTIVE_RECORD_STATUS). ИС при работе данного метода - ошибки соединения с СУБД или
     * выполнения запросов. Для соединения с СУБД метод использует источник данных (DataSource).
     *
     * @param dataSource        DataSource источник данных для получения соединения с сервером БД.
     * @param tableName         String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue     int значение ключа записи.
     * @param deletedFieldValue int новое значение статуса удаления/активности записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setDeleted(DataSource dataSource, String tableName, int keyFieldValue, int deletedFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted_(dataSource, tableName, DBConsts.FIELD_NAME_KEY, keyFieldValue,
                DBConsts.FIELD_NAME_DELETED, deletedFieldValue);
    }

    /**
     * Метод устанавливает статус записи "удалена". Значения для имени ключевого поля, имени поля со статусом удаления записи,
     * значения поля статуса удаления записи берутся по умолчанию (см. описание других методов).
     *
     * @param connection    Connection соединение с СУБД для работы метода.
     * @param tableName     String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue int значение ключа записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setRecordDeleted(Connection connection, String tableName, int keyFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted(connection, tableName, keyFieldValue, DBConsts.RECORD_STATUS_DELETED);
    }

    /**
     * Метод устанавливает статус записи "активна (неудалена)". Значения для имени ключевого поля, имени поля со статусом
     * удаления записи, значения поля статуса удаления записи берутся по умолчанию (см. описание других методов).
     *
     * @param connection    Connection соединение с СУБД для работы метода.
     * @param tableName     String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue int значение ключа записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setRecordUndeleted(Connection connection, String tableName, int keyFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted(connection, tableName, keyFieldValue, DBConsts.RECORD_STATUS_ACTIVE);
    }

    /**
     * Метод устанавливает статус записи "удалена". Значения для имени ключевого поля, имени поля со статусом удаления записи,
     * значения поля статуса удаления записи берутся по умолчанию (см. описание других методов).
     *
     * @param dataSource    DataSource источник данных для получения соединения с сервером БД.
     * @param tableName     String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue int значение ключа записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setRecordDeleted(DataSource dataSource, String tableName, int keyFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted(dataSource, tableName, keyFieldValue, DBConsts.RECORD_STATUS_DELETED);
    }

    /**
     * Метод устанавливает статус записи "активна (неудалена)". Значения для имени ключевого поля, имени поля со статусом
     * удаления записи, значения поля статуса удаления записи берутся по умолчанию (см. описание других методов).
     *
     * @param dataSource    DataSource источник данных для получения соединения с сервером БД.
     * @param tableName     String имя таблицы, в которой меняем статус записи.
     * @param keyFieldValue int значение ключа записи.
     * @return int код выполнения запроса на изменение данных (в большинстве случаев = 0 при успешном выполнении запроса).
     * @throws SQLException          ошибки при выполении запроса.
     * @throws DBConnectionException оишбки работы с соединением с СУБД.
     */
    public static int setRecordUndeleted(DataSource dataSource, String tableName, int keyFieldValue)
            throws SQLException, DBConnectionException {
        return DataChanger.setDeleted(dataSource, tableName, keyFieldValue, DBConsts.RECORD_STATUS_ACTIVE);
    }

    /**
     * Метод выполняет очистку указанной таблицы tableName для указанной конфигурации соединения с СУБД. Если имя
     * таблицы пусто - возникает ИС.
     *
     * @param config    DBConfig используемая конфигурация соединения с БД для изменения данных.
     * @param tableName String имя очищаемой таблицы.
     * @return int результат выполнения операциии (выполнения запроса DELETE...).
     * @throws SQLException                           ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException   ошибки при работе с соединением (например оно пусто).
     * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения (например конфигурация пуста).
     */
    public static int cleanupTable(DBConfig config, String tableName)
            throws DBConnectionException, SQLException, DBModuleConfigException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // Поле для хранения результата
        int result = 0;
        // Проверяем имя таблицы - если оно пусто - ошибка!
        if (StringUtils.isBlank(tableName)) {
            throw new SQLException("Error occured during cleaning up table: table name is empty!");
        }
        logger.info("Cleaning table [" + tableName.toUpperCase() + "].");
        // Запрос для полной очистки таблицы
        String sql = "delete from " + tableName;
        SqlExecutor.executeUpdateQuery(config, sql);
        // Возвращаем результат
        return result;
    }

    /**
     * Метод удаляет запись с идентификатором id из таблицы tableName, ключевое поле которой называется keyFieldName.
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается.
     *
     * @param connection    Connection используемое соединение с БД для изменения данных.
     * @param tableName     String имя таблицы, в которой изменяются данные.
     * @param keyFieldName  String наименование ключевого поля таблицы. Поле должно быть целочисленным.
     * @param keyFieldValue int целочисленное значение ключевого поля таблицы.
     * @return int результат выполнения операциии (выполнения запроса DELETE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int deleteRecord_(Connection connection, String tableName, String keyFieldName, int keyFieldValue)
            throws DBConnectionException, SQLException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем параметры
        //String paramsCheckResult = DataProcessingHelper.checkParams(tableName, keyFieldName);
        //if (!StringUtils.isBlank(paramsCheckResult)) {
        //    throw new SQLException(paramsCheckResult);
        //}
        // Генерируем запрос для изменения данных
        String sql = "delete from " + tableName + " where " + keyFieldName + " = " + keyFieldValue;
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения
        return SqlExecutor.executeUpdateQuery(connection, sql);
    }

    /**
     * Метод удаляет запись с идентификатором id из таблицы tableName, ключевое поле которой называется ID (см. значение
     * по умолчанию в модуле DBConsts).
     * Метод использует уже установленное соединение с СУБД - connection. При ошибках в работе метода - возникают ИС
     * SQLException, DBConnectionException, сообщения которых описывают возникшую ошибку. Используемое методом
     * соединение после работы метода НЕ закрывается.
     *
     * @param connection    Connection используемое соединение с БД для изменения данных.
     * @param tableName     String имя таблицы, в которой изменяются данные.
     * @param keyFieldValue int целочисленное значение ключевого поля таблицы.
     * @return int результат выполнения операциии (выполнения запроса DELETE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int deleteRecord_(Connection connection, String tableName, int keyFieldValue)
            throws DBConnectionException, SQLException {
        // Проверяем соединение
        if (connection == null) {
            throw new DBConnectionException("Connection is empty!");
        }
        // Проверяем имя таблицы - оно должно быть не пустым
        if (StringUtils.isBlank(tableName)) {
            throw new SQLException("Can't delete record: table name is empty.");
        }
        // Генерируем запрос для изменения данных
        String sql = "delete from " + tableName + " where " + DBConsts.FIELD_NAME_KEY + " = " + keyFieldValue;
        logger.debug("Generated query: " + sql);
        // Выполняем запрос и возвращаем результат выполнения
        return SqlExecutor.executeUpdateQuery(connection, sql);
    }

    /**
     * Метод устанавливает в таблице tableName с ключевым полем keyFieldName значение целочисленного поля dataFiledName.
     * Значение ключа - строковый параметр keyFieldValue, значение целочисленного поля для установки - dataFieldValue.
     * Метод использует указанный источник данных (DataSource) для соединения с СУБД. При ошибках в работе метода -
     * возникают ИС SQLException и DBConnectionException, сообщения которых описывают возникшую ошибку.
     *
     * @param dataSource    DataSource используемый источник данных для получения соединения с БД.
     * @param tableName     String имя таблицы, в которой изменяются данные.
     * @param keyFieldValue String строковое значение ключевого поля таблицы.
     * @return int результат выполнения операциии (выполнения запроса UPDATE...).
     * @throws SQLException                         ИС, возникающая в процессе изменения данных.
     * @throws jdb.exceptions.DBConnectionException ошибки при работе с соединением (например оно пусто).
     */
    public static int deleteRecord_(DataSource dataSource, String tableName, int keyFieldValue)
            throws DBConnectionException, SQLException {
        // Поле для хранения результата
        int result = 0;
        Connection connection = null;
        try {
            // Устанавливаем соединение (если источник данных не пуст)
            if (dataSource != null) {
                connection = dataSource.getConnection();
            } else {
                throw new SQLException("Data source is NULL!");
            }
            // Вызываем метод для установки значения (использующий установленное соединение)
            result = DataChanger.deleteRecord_(connection, tableName, keyFieldValue);
        }
        // Закрываем соединение в любом случае (пытаемся)
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        // Возвращаем результат
        return result;
    }

}