package dgusev.dbpilot.config;

import dgusev.auth.Password;
import dgusev.dbpilot.DBConsts;
import dgusev.utils.MyCommonUtils;
import dgusev.io.MyIOUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Данный класс реализует простое хранение конфигурации для соединения с СУБД. В классе есть значимые поля, методы доступа
 * к ним и метод проверки корректности параметров (их наличия). Классом можно пользоваться для конфигурирования соединения
 * с СУБД, значения всех полей устанавливаются вручную.
 Формат конфигурационного файла следующий:<br><br>
 *
 * <b>
 * &nbsp;&lt;?xml version = '1.0'?&gt;<br>
 * &nbsp;&nbsp;&lt;[ROOT_ELEMENT]&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;db&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;[OPTIONAL_SECTION_NAME]&gt;<br>
 *
 * <br><i>{1}</i><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dataSource&gt;[data_source_name]&lt;/dataSource&gt;<br>
 *
 * <br><i>{2}</i><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;type&gt;[mysql|dbf|informix|odbc|derby_embedd|derby_server]&lt;/type&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;host&gt;[server_host]:[server_port]&lt;/host&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;server&gt;[db_server_name]&lt;/server&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dbname&gt;[db_name]&lt;/dbname&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;user&gt;[user]&lt;/user&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pwd&gt;[password]&lt;/pwd&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;params&gt;[name1=value1;...;nameN=valueN]&lt;/params&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;info&gt;[name1=value1;...;nameN=valueN]&lt;/info&gt;<br>
 *
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/[OPTIONAL_SECTION_NAME]&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;/db&gt;<br>
 * &nbsp;&nbsp;&lt;/[ROOT_ELEMENT]&gt;<br><br>
 * </b><br>
 *
 * <br>- [ROOT_ELEMENT] - может быть любым, по умолчанию при сохранении конфига используется значение константы
 * jdb.DBConsts.XML_DB_ROOT_TAG {@linkplain jdb.DBConsts#XML_DB_ROOT_TAG (смотри константу XML_DB_ROOT_TAG)}.
 * <b>[ROOT_ELEMENT] обязательно должен быть, если его нет (и элемент &lt;db&gt; является корневым), то файл не будет
 * прочитан.</b>
 * <br><b>Данный элемент конфига ОБЯЗАТЕЛЬНО должен присутствовать, в противном случае
 * метод загрузки из конфига выполнится удачно (без ошибок), но данные загружены не будут.</b>
 * <br>- [OPTIONAL_SECTION_NAME] - опциональная секция, она может присутствовать, может отсутствовать, позволяет
 * хранить настройки нескольких соединений в одной секции конфиг-файла. Если секция есть, то для чтения из нее параметров
 * в методе загрузки данных (или соотв. конструкторе) необходимо указывать ее имя.
 * <br>- <b><i>{1}</i></b>-<b><i>{2}</i></b> - указывается либо секция 1, либо секция 2. Секция 1 - конфигурация для соединения
 * с БД через вызов источника данных (DataSource) с дерева JNDI. Секция 2 - конфигурация для соединения с БД напрямую через
 * JDBC-драйвер. При указании в конфиге сразу обоих секций приоритет имеет секция 1 - ее параметры будут считаны, а параметры
 * секции 2 будут проигнорированы.
 * <br>- остальные параметры похожи на описанные в класса UniversalConfig.
 * <br>- возможно комбинирование нескольких секций [OPTIONAL_SECTION_NAME] - для чтения из них параметров необходимо будет
 * указывать имя секции. Также при этом можно оставить и парметры в секции [ROOT_ELEMENT] - для их чтения указание имени
 * секции не требуется.
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 21.02.2011)
 * @author Gusev Dmitry (019gus)
 * @version 4.0 (DATE: 10.03.2011)
 */

@ToString
@CommonsLog
public class DBConfig {

    private static final String ERR_MSG_DB_CONFIG_DATA = "DB TYPE is NULL and DATA SOURCE name is NULL! Can't connect!";
    private static final String ERR_MSG_DB_HOST = "Database host is empty for db type [%1$S]!";
    private static final String ERR_MSG_DB_USERNAME = "Username is empty for db type [%1$S]!";
    private static final String ERR_MSG_DB_PASSWORD = "Password is empty for db type [%1$S]!";
    private static final String ERR_MSG_DB_NAME = "Database name is empty for db type [%1$S]!";
    private static final String ERR_MSG_DB_TYPE = "Invalid database type [%1$S]!";

    @Getter @Setter private String dataSource; // JNDI data source (ignore other params if specified)
    @Getter @Setter private DBType dbType;     // DBMS type
    @Getter @Setter private String host;
    @Getter @Setter private String serverName;
    @Getter @Setter private String dbName;
    @Getter @Setter private String user;
    @Getter @Setter private Password          password;
    @Getter @Setter private String            connParams;       // additional connection parameters (for JDBC url)
    @Getter @Setter private Properties        connInfo;         // additional connection info (added to DriverManager)
    @Getter @Setter private ArrayList<String> allowedTables;    // tables allowed for processing
    @Getter @Setter private ArrayList<String> deprecatedTables; // tablew deprecated for processing
    @Getter @Setter private boolean           isDemo = false;   // demo mode on/off

    public DBConfig() {
    }

    public DBConfig(String fileName) throws IOException, ConfigurationException {
        this.loadFromFile(fileName, null, true);
    }

    /***/
    public String getConfigErrors() {
        LOG.debug("DBConfig.getConfigErrors() is working.");

        String result = null;

        if (StringUtils.isBlank(dataSource)) { // JNDI data source isn't specified
            if (dbType == null) {
                result = ERR_MSG_DB_CONFIG_DATA;
            } else {
                switch (dbType) {
                    // Для Informix'a, MSSQL'я и MySQL обязательно указание хоста (с портом), логина и пароля пользователя.
                    case INFORMIX:
                    case MYSQL:
                    case MSSQL_JTDS:
                    case MSSQL_NATIVE:
                        if (StringUtils.isBlank(host)) {
                            result = String.format(ERR_MSG_DB_HOST, dbType);
                        } else if (StringUtils.isBlank(user)) {
                            result = String.format(ERR_MSG_DB_USERNAME, dbType);
                        } else if ((password == null) || (StringUtils.isBlank(password.getPassword()))) {
                            result = String.format(ERR_MSG_DB_PASSWORD, dbType);
                        }
                        break;
                    // Для ODBC и DBFa обязательно указание наименования БД
                    case ODBC:
                    case DBF:
                        if (StringUtils.isBlank(dbName)) {
                            result = String.format(ERR_MSG_DB_NAME, dbType);
                        }
                        break;
                    // Если тип СУБД не подошел - значит указан неверный тип!
                    default:
                        result = String.format(ERR_MSG_DB_TYPE, dbType);
                        break;
                } // end of SWITCH
            }
        }

        return result;
    }

    /**
     * Метод загружает данные из указанного конфигурационного xml-файла из раздела &lt;db&gt;. В файле обязательно должен
     * быть корневой (ROOT) элемент над разделом &lt;db&gt;. Корневой элемент может быть любым. Данные (теги) могут находиться
     * как в секции &lt;db&gt;, так и в любой подсекции указанной секции (имя подсекции указывется параметром sectionName).
     *
     * @param fileName     String xml-файл, из которого будем загружать конфигурацию.
     * @param sectionName  String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры. Т.е.
     *                     возможно хранение в одном конфиг файле одновременно нескольких конфигураций соединения с СУБД (под разными именами
     *                     подсекций (подразделов) в разделе &lt;db&gt;, например: db1, db2, ... , dbN). Подробнее см. javadoc модуля.
     * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
     *                     в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
     *                     файл, в котором находится пароль (файл специализированного формата).
     * @throws java.io.IOException                                     ИС - указано пустое имя файла, файл не существует и т.п.
     */
    public void loadFromFile(String fileName, String sectionName, boolean usePlainPass) throws IOException, ConfigurationException {
        LOG.debug("WORKING ExtendedDBConfig.loadFromFile().");

        // Если имя файла пусто - ошибка!
        if (StringUtils.isBlank(fileName)) {
            throw new IOException("File name is blank!");
        }

        // Если файл не существует или это не файл - ошибка! В ошибку выводим и указанное и полное имя файла.
        else if ((!new File(fileName).exists()) || (!new File(fileName).isFile())) {
            throw new IOException("Specified file [" + fileName + "] doesn't exists or not a file! " +
                    "Absolute file path [" + MyIOUtils.fixFPath(new File(fileName).getAbsolutePath(), false) + "].");
        }

        // todo: fix the code

        /*
        // Класс xml-конфигурации
        XMLConfiguration config = new XMLConfiguration(fileName);
        // Загружаем (читаем) конфиг из файла
        LOG.debug("Reading XML file...");
        config.load();
        // Формируем префикс имени (если есть имя секции, мы его используем, если же нет, то читаем конфиг прямо
        // из раздела (секции) <db></db>)
        String prefix;
        if (!StringUtils.isBlank(sectionName)) {
            prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";
        } else {
            prefix = DBConsts.XML_DB_TAG + ".";
        }

        // Читаем из файла имя источника данных
        String dataSource = config.getString(prefix + DBConsts.DB_DATA_SOURCE);
        // Если имя источника данных указано - далее файл не читаем - инициализация окончена!
        if (!StringUtils.isBlank(dataSource)) {
            LOG.debug("JNDI data source name specifyed [" + dataSource + "]! Using it.");
            this.setDataSource(dataSource);
        }
        // Если же имя источника данных не указано - читаем из файла параметры для соединения с БД через JDBC
        else {
            LOG.debug("JNDI data source name not specifyed! Processing JDBC parameters.");
            // ----- Тип СУБД (читаем из конфига)
            String strDbType = config.getString(prefix + DBConsts.DB_TYPE);
            // Если прочитанное значение не пусто - обрабатываем
            if (!StringUtils.isBlank(strDbType)) {
                // Преобразуем прочитанное значение в формат класса DBType
                try {
                    this.setDbType(DBType.valueOf(strDbType.toUpperCase()));
                }
                // Если значение не верно (нет такого в классе-перечислении) - ИС!
                catch (IllegalArgumentException e) {
                    throw new DBModuleConfigException("Invalid DBMS type! [" + e.getMessage() + "]");
                }
            }
            // Если же значение пусто - ошибка! (тип СУБД не может быть пуст)
            else {
                throw new DBModuleConfigException("DBMS type and JNDI data source name are empty !");
            }

            // ----- Хост СУБД
            this.setHost(config.getString(prefix + DBConsts.DB_HOST));
            // ----- Имя сервера СУБД
            this.setServerName(config.getString(prefix + DBConsts.DB_SERVER));
            // ----- Имя БД
            this.setDbName(config.getString(prefix + DBConsts.DB_NAME));
            // ----- Пользователь СУБД
            this.setUser(config.getString(prefix + DBConsts.DB_USER));
            // ----- Пароль пользователя
            String strPass = config.getString(prefix + DBConsts.DB_PWD);
            // Если пароль не пуст - обрабатываем его
            if (!StringUtils.isBlank(strPass)) {
                // Если установлен флаг "простой пароль", то в данном теге находится просто пароль
                if (usePlainPass) {
                    LOG.info("This database connection config use plain password!");
                    this.setPassword((strPass));
                }
                // Если же флаг "простой пароль" сброшен (false), то в данном теге находится имя файла с
                // шифрованным паролем (имя или абсолютный путь)
                else {
                    LOG.debug("Usig password file [" + strPass + "].");
                    try {
                        this.setPassword((Password) MyIOUtils.deserializeObject(strPass, false));
                    } catch (ClassNotFoundException e) {
                        LOG.error("Can't read password file! Reason: " + e.getMessage());
                    } catch (ClassCastException e) {
                        LOG.error("Can't read password file! Reason: " + e.getMessage());
                    }
                }
            }
            // Если же прочитанный пароль пуст - сообщим об этом
            else {
                LOG.warn("This config [" + fileName + "] has an empty password!");
            }
            // ----- Дополнительные параметры соединения (указываются name1=value1;name2=value2;...)
            this.setConnParams(StringUtils.trimToNull(config.getString(prefix + DBConsts.DB_CONN_PARAMS)));
            // ----- Дополнительная информация для соединения (указывается name1=value1;name2=value2;...)
            this.setConnInfo(MyCommonUtils.getPropsFromString(config.getString(prefix + DBConsts.DB_CONN_INFO)));
        }
        */
    }

    /**
     * Метод создает и возвращает объект XMLConfiguration для того, чтобы можно было сохранять содержимое данного
     * класса в файл или объединять с другими конфигурациями (и также сохранять в файл).
     *
     * @param rootName    String имя корневого (root) элемента для создаваемого файла конфигурации.
     * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, в которую будут записаны параметры.
     * @return XMLConfiguration созданный объект на основании состояния данного экземпляра класса.
     */
    private XMLConfiguration getXMLConfig(String rootName, String sectionName) {
        LOG.debug("getXMLConfig(): generating XMLConfiguration object.");
        // Создаем экземпляр класса XML-конфигурации
        XMLConfiguration config = new XMLConfiguration();
        // Корневой элемент (выбираем в зависимости от параметра rootName). Если корневой элемнт указан - берем
        // его, если же не указан - корневой элемнт по умолчанию используем.
        if (!StringUtils.isBlank(rootName)) {
            config.setRootElementName(rootName);
        } else {
            config.setRootElementName(DBConsts.XML_DB_ROOT_TAG);
        }
        // Формируем префикс
        String prefix;
        if (!StringUtils.isBlank(sectionName)) {
            prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";
        } else {
            prefix = DBConsts.XML_DB_TAG + ".";
        }

        // Добавляем данные. Если в конфиге указано имя источника данных (поле dataSource), то сохраняем только его.
        if (!StringUtils.isBlank(this.getDataSource())) {
            LOG.debug("Adding JNDI data source name to XML config object.");
            config.addProperty(prefix + DBConsts.DB_DATA_SOURCE, this.getDataSource());
        }
        // Если же поле "имя источника данных" пусто, то сохраняем параметры для соединения через JDBC-драйвер.
        else {
            LOG.debug("Adding JDBC parameters to XML config object.");
            // Непосредственно добавляем параметры
            config.addProperty(prefix + DBConsts.DB_TYPE, this.getDbType());
            config.addProperty(prefix + DBConsts.DB_HOST, this.getHost());
            config.addProperty(prefix + DBConsts.DB_SERVER, this.getServerName());
            config.addProperty(prefix + DBConsts.DB_NAME, this.getDbName());
            config.addProperty(prefix + DBConsts.DB_USER, this.getUser());
            // При сохранении пароля он всегда сохраняется в файл (если пароль не пуст)
            if (this.getPassword() != null) {
                LOG.debug("Password is not empty. Processing.");
                try {

                    // todo: fix!!!
                    //String passFilePath = MyIOUtils.serializeObject(this.getPassword(), SystemUtils.getUserDir().getAbsolutePath());
                    String passFilePath = null;

                    String passFileName = passFilePath.substring(passFilePath.lastIndexOf("/") + 1);
                    config.addProperty(prefix + DBConsts.DB_PWD, passFileName);
                } catch (/*IOException e*/ Exception e) {
                    LOG.error("Can't save password file! Reason: " + e.getMessage());
                }
            }
            // Если пароль пуст - сообщим об этом
            else {
                LOG.warn("Empty password! Can't process it.");
            }
            // Дополнительный параметры соединения
            config.addProperty(prefix + DBConsts.DB_CONN_PARAMS, this.getConnParams());
            config.addProperty(prefix + DBConsts.DB_CONN_INFO, MyCommonUtils.getStringFromProps(this.getConnInfo(), null, null));
        }
        // Возвращаем результат
        return config;
    }

    /**
     * Save current configuration to XML file.
     */
    public void saveToFile(String fileName, String rootName, String sectionName) throws ConfigurationException {
        LOG.debug("saveToFile(): trying to save to [" + fileName + "].");
        // Непосредственно сохранение файла
        // todo: use -> https://commons.apache.org/proper/commons-configuration/userguide/quick_start.html
        //this.getXMLConfig(rootName, sectionName).save(fileName);
    }

    public void addDeprecatedTable(String tableName) {
        if (!StringUtils.isBlank(tableName)) {
            if (this.deprecatedTables == null) {
                this.deprecatedTables = new ArrayList<>();
            }
            this.deprecatedTables.add(tableName.toUpperCase());
        } else {
            LOG.warn("addDeprecatedTable(): can't add empty deprecated name!");
        }
    }

    public void addDeprecatedTables(String[] tablesNames) {
        if ((tablesNames != null) && (tablesNames.length > 0)) {
            if (this.deprecatedTables == null) {
                this.deprecatedTables = new ArrayList<>();
            }
            this.deprecatedTables.addAll(Arrays.asList(tablesNames));
        } else {
            LOG.warn("addDeprecatedTables(): can't add empty deprecated list!");
        }
    }

    public void addAllowedTable(String tableName) {
        if (!StringUtils.isBlank(tableName)) {
            if (this.allowedTables == null) {
                this.allowedTables = new ArrayList<>();
            }
            this.allowedTables.add(tableName.toUpperCase());
        } else {
            LOG.warn("addAllowedTable(): can't add empty allowed name!");
        }
    }

    public void addAllowedTables(String[] tablesNames) {
        if ((tablesNames != null) && (tablesNames.length > 0)) {
            if (this.allowedTables == null) {
                this.allowedTables = new ArrayList<>();
            }
            this.allowedTables.addAll(Arrays.asList(tablesNames));
        } else {
            LOG.warn("addAllowedTables(): can't add empty allowed list!");
        }
    }

    public void setConstraints(ArrayList<String> allowedTables, ArrayList<String> deprecatedTables) {
        if ((allowedTables != null) && (!allowedTables.isEmpty())) {
            this.allowedTables = allowedTables;
        }
        if ((deprecatedTables != null) && (!deprecatedTables.isEmpty())) {
            this.deprecatedTables = deprecatedTables;
        }
    }

    /**
     * Проверка валидности обработки таблицы с именем tableName. Если имя таблицы пусто - метод возвращает значение false.
     * При принятии решения о валидности обработки участвуют списки разрешенных и запрещенных таблиц, а также список
     * таблиц системного каталога для данного типа СУБД. Если же до использования данного метода поле класса dbType не
     * имеет значение - будет возбуждена ИС - DBModuleConfigException.
     *
     * @param tableName String имя таблицы, валидность обработки которой мы проверяем.
     * @return boolean результат проверки валидности обработки таблицы.
     */
    public boolean isTableAllowed(String tableName) {
        boolean result = false;

        if (!StringUtils.isBlank(tableName)) {
            // Проверяем тип СУБД - если он пуст, то возбуждаем ИС
            if (this.getDbType() == null) {
                throw new IllegalStateException("Empty DBMS type!");
            } else {
                if
                    // Если список "запрещенных" пуст или таблицы в нем нет - ОК
                (((deprecatedTables == null) || (!deprecatedTables.contains(tableName.toUpperCase()))) &&
                        // Если список "разрешенных" пуст или таблица в нем есть - ОК
                        ((allowedTables == null) || (allowedTables.contains(tableName.toUpperCase()))))
                // Результат положителен
                {
                    LOG.debug("isTableAllowed(): table [" + tableName + "] is allowed.");
                    result = true;
                }
            }
        } else {
            LOG.warn("isTableAllowed: empty table name!");
        }

        return result;
    }

}