package dgusev.dbpilot;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class DBPilot {

    /**
     * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, сущесвует ли таблица с указанным именем (tableName) в
     * БД, на которую указывает соединение (conn). Если имя таблицы и/или соединение пусто - метод возбуждает ИС
     * (SQLException). Если соединение указывает не на конкретную БД, а в целом на сервер, метод вернет ЛОЖЬ (т.е.
     * соединение должно указывать на конкретную БД на сервере). Поиск таблицы по имени осуществляется БЕЗ учета схем БД
     * (т.е. таблица с указанным именем [table] будет найдена, а таблица с указанным именем [schema].[table] найдена не будет).
     * Еще одна особенность метода - поиск таблицы осуществляется без учета регистра символов, т.е. следующие имена таблиц
     * эквивалентны: table1 и TabLE1 (имена таблиц переводятся в ВЕРХНИЙ регистр символов для сравнения).
     */
    public static boolean isTableExists(Connection conn, String tableName) throws SQLException {
        log.debug("DBPilot: isTableExists().");

        boolean result = false;
        // Проверяем соединение с СУБД
        if (conn != null) {
            // Проверяем имя таблицы
            if (!StringUtils.isBlank(tableName)) {
                ResultSet tablesRS = null;
                try {
                    DatabaseMetaData metaData = conn.getMetaData();
                    // Перебор всех таблиц указанной БД и добавление их в список.
                    tablesRS = metaData.getTables(null, null, null, null);
                    // Если список таблиц получен - пройдемся по нему
                    while (tablesRS.next() && !result) {
                        if (tableName.toUpperCase().equals(tablesRS.getString(DBConsts.META_DATA_TABLE_NAME).toUpperCase())) {
                            result = true;
                        }
                    }
                }
                // Закроем открытые курсоры
                finally {
                    try {
                        if (tablesRS != null) {
                            tablesRS.close();
                        }
                    } catch (SQLException e) {
                        log.error("Can't free resources! Reason [" + e.getMessage() + "].");
                    }
                }
            }
            // Пустое имя таблицы
            else {
                throw new SQLException("Empty table name!");
            }
        }
        // Соединение пусто - ошибка
        else {
            throw new SQLException("Empty connection!");
        }
        // Возвращаем результат
        return result;
    }

}
