package dgusev.dbpilot.utils;

import dgusev.dbpilot.config.DBConfig;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@CommonsLog
public final class DBUtilities {

    private DBUtilities() {}

    /***/
    public static String getStringResultSet(ResultSet rs, int width) {
        LOG.debug("DBUtilities.getStringResultSet() is working.");

        StringBuilder rows = new StringBuilder();
        // process result set
        if (rs != null) {
            try {
                if (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    // line for header and footer of the table
                    String horizontalLine = StringUtils.repeat("-", width * columnCount + columnCount + 1) + "\n";
                    // creating header
                    StringBuilder header = new StringBuilder(horizontalLine).append("|");
                    for (int i = 1; i <= columnCount; i++) {
                        header.append(String.format("%" + width + "s|", StringUtils.center(rs.getMetaData().getColumnName(i), width)));
                    }
                    header.append("\n").append(horizontalLine);
                    // add header to result
                    rows.append(header);
                    // creating data rows
                    int counter = 0;
                    do {
                        StringBuilder row = new StringBuilder("|");
                        for (int i = 1; i <= columnCount; i++) {
                            row.append(String.format("%" + width + "s|", StringUtils.center(rs.getString(i), width)));
                        }
                        row.append("\n");
                        // add data row to result
                        rows.append(row);
                        counter++;
                    } while (rs.next());
                    // add footer horizontal line
                    rows.append(horizontalLine).append("Total record(s): ").append(counter).append("\n");
                } else {
                    LOG.warn("ResultSet is not NULL, but is EMPTY!");
                }
            } // end of TRY
            catch (SQLException e) {
                LOG.error("SQL error occured: " + e.getMessage());
            }
        } else LOG.warn("ResultSet is NULL!");

        return rows.toString();
    }

    /** Метод, в зависимости от параметров соединения с СУБД, формирует конфигурационный JDBC URL для соединения с СУБД.  */
    public static String getJDBCUrl(DBConfig config) {
        LOG.debug("DBUtilities.getJDBCUrl() is working.");
        switch (config.getDbType()) {
            case MYSQL:        return JdbcUrlHelper.getMysqlJdbcUrl(config);
            case ODBC:         return JdbcUrlHelper.getOdbcJdbcUrl(config);
            case DBF:          return JdbcUrlHelper.getDbfJdbcUrl(config);
            case INFORMIX:     return JdbcUrlHelper.getInformixJdbcUrl(config);
            case MSSQL_JTDS:   return JdbcUrlHelper.getMssqlJtdsJdbcUrl(config);
            case MSSQL_NATIVE: return JdbcUrlHelper.getMssqlNativeJdbcUrl(config);
            default:           throw new IllegalArgumentException(String.format("Unsupported DB type: [%s]!", config.getDbType()));
        }
    }

    /**
     * Метод создает и возвращает соединение (объект Connection) с указанной в конфиге СУБД. Данный метод может работать
     * как с прямым соединением (через JDBC драйвер), так и с источником данных JNDI, выбор способа соединения зависит
     * от переданного методу конфига - если в конфиге заполнено поле dataSource, то будет выполнено соединение через
     * JNDI источник данных, если же не заполнено - метод попытается установить соединение посредством JDBC драйвера
     * указанной СУБД.
     */
    public static Connection getDBConn(DBConfig config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        LOG.debug("DBUtilities.getDBConn() is working..");

        Connection connection;

        // Теперь нам надо определиться как мы коннектимся к БД - через JDBC или через JNDI. Если в переданном нам
        // конфиге указано имя источника данных JNDI - используем этот тип соединения, если же не указано - используем тип
        // соединения - через JDBC-драйвер.
        //if (!StringUtils.isBlank(config.getDataSource())) {
        //    logger.debug("Connecting to DBMS over JNDI data source.");
        //    try {
                // Получаем источник данных
        //        DataSource dataSource = (DataSource) new InitialContext().lookup(config.getDataSource());
        //        connection = dataSource.getConnection();
        //    } catch (NamingException e) {
        //        throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));
        ///    } catch (SQLException e) {
         //       throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));
         //   }
        //}
        // Имя источника данных JNDI не указано - коннектимся через JDBC
        //else {
            LOG.debug("Connecting to DBMS over JDBC driver.");
            //try {
                // Получаем драйвер
                String dbDriver = config.getDbType().getDriver();
                // Если драйвер не пуст - работаем дальше
                if (!StringUtils.isBlank(dbDriver)) {
                    LOG.debug("Database driver OK. Processing. Driver: [" + dbDriver + "].");
                    // Загрузка класса драйвера (для драйверов типа JDBC 4 не нужна - ???)
                    // todo: необходима ли прямая загрузка драйвера?
                    Class.forName(dbDriver).newInstance();
                    LOG.debug("Driver [" + dbDriver + "] loaded!");
                    // Дополнительные параметры для соединения с СУБД
                    Properties connectionInfo = config.getConnInfo();
                    // Непосредственно подключение к СУБД
                    if ((connectionInfo != null) && (!connectionInfo.isEmpty())) {
                        LOG.debug("Using getConnection() with [CONNECTION INFO].");
                        connection = DriverManager.getConnection(DBUtilities.getJDBCUrl(config), config.getConnInfo());
                        //LOG.debug("Connection ok.");
                    } else {
                        LOG.debug("Using getConnection() without [CONNECTION INFO].");
                        connection = DriverManager.getConnection(DBUtilities.getJDBCUrl(config));
                        //LOG.debug("Connection ok.");
                    }
                }
                // Если драйвер (класс драйвера) не указан - возбуждаем ИС и сообщаем об этом
                else {
                    throw new IllegalStateException("Database driver class is empty (NULL)!");
                }
            //} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            //    throw new DBConnectionException(ExceptionUtils.getExceptionMessage(e));
            //}

            //}

        // Возвращаем объект "соединение с СУБД".
        return connection;
    }
}
