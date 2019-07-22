package dgusev.dbpilot.config;

import jdb.DBResources;
import jdb.config.common.ConfigInterface;
import dgusev.auth.Password;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Данный класс реализует простое хранение конфигурации для соединения с СУБД. В классе есть значимые поля, методы доступа
 * к ним и метод проверки корректности параметров (их наличия). Классом можно пользоваться для конфигурирования соединения
 * с СУБД, значения всех полей устанавливаются вручную.
 *
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 10.03.2011)
 */

@ToString
public class BaseDBConfig implements ConfigInterface {

    @Getter @Setter private String dataSource; // JNDI data source (ignore other params if specified)
    @Getter @Setter private DBType dbType;     // DBMS type
    @Getter @Setter private String host;
    @Getter @Setter private String serverName;
    @Getter @Setter private String dbName;
    @Getter @Setter private String user;
    @Getter @Setter private Password password;
    @Getter @Setter private String connParams; // additional connection parameters (for JDBC url)
    @Getter @Setter private Properties connInfo;   // additional connection info (added to DriverManager)

    /***/
    public String getConfigErrors() {
        String result = null;
        // Если тип СУБД пуст - проверяем наименование источника данных, если ок, то все номано,
        // если он (источник данных) тоже пуст - ошибка!
        if (StringUtils.isBlank(dataSource)) {
            if (dbType == null) {
                result = DBResources.ERR_MSG_DB_CONFIG_DATA;
            }
            // Если тип СУБД не пуст - проверяем параметры для каждого типа СУБД
            else {
                switch (dbType) {
                    // Для Informix'a, MSSQL'я и MySQL обязательно указание хоста (с портом), логина и пароля пользователя.
                    case DBType.INFORMIX:
                    case DBType.MYSQL:
                    case DBType.MSSQL_JTDS:
                    case DBType.MSSQL_NATIVE:
                        if (StringUtils.isBlank(host)) {
                            result = String.format(DBResources.ERR_MSG_DB_HOST, dbType);
                        } else if (StringUtils.isBlank(user)) {
                            result = String.format(DBResources.ERR_MSG_DB_USERNAME, dbType);
                        } else if ((password == null) || (StringUtils.isBlank(password.getPassword()))) {
                            result = String.format(DBResources.ERR_MSG_DB_PASSWORD, dbType);
                        }
                        break;
                    // Для ODBC и DBFa обязательно указание наименования БД
                    case DBType.ODBC:
                    case DBType.DBF:
                        if (StringUtils.isBlank(dbName)) {
                            result = String.format(DBResources.ERR_MSG_DB_NAME, dbType);
                        }
                        break;
                    // Если тип СУБД не подошел - значит указан неверный тип!
                    default:
                        result = String.format(DBResources.ERR_MSG_DB_TYPE, dbType);
                        break;
                }
            }
        }
        // Возвращаем результат проверки
        return result;
    }

}