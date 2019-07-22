package dgusev.dbpilot.config;

import jdb.DBConsts;
import jdb.exceptions.DBModuleConfigException;
import dgusev.auth.Password;
import jlib.exceptions.EmptyObjectException;
import jlib.exceptions.EmptyPassException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import jlib.utils.common.Utils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Данный класс расширяет функциональность простого класса конфигурации соединения с СУБД через JDBC-драйвер (класса
 * BaseJdbcConfig). В данном классе реализованы методы загрузки/выгрузки конфигурации из/в файл(а) на диске.
 * Данный класс следует использовать вместо класса ConnectionConfig.
 * Формат конфигурационного файла следующий:<br><br>
 *
 * <b>
 * &nbsp;&lt;?xml version = '1.0'?&gt;<br>
 * &nbsp;&nbsp;&lt;[ROOT_ELEMENT]&gt;<br>
 *  &nbsp;&nbsp;&nbsp;&lt;db&gt;<br>
 *   &nbsp;&nbsp;&nbsp;&nbsp;&lt;[OPTIONAL_SECTION_NAME]&gt;<br>
 *
 *    <br><i>{1}</i><br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dataSource&gt;[data_source_name]&lt;/dataSource&gt;<br>
 *
 *    <br><i>{2}</i><br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;type&gt;[mysql|dbf|informix|odbc|derby_embedd|derby_server]&lt;/type&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;host&gt;[server_host]:[server_port]&lt;/host&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;server&gt;[db_server_name]&lt;/server&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;dbname&gt;[db_name]&lt;/dbname&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;user&gt;[user]&lt;/user&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pwd&gt;[password]&lt;/pwd&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;params&gt;[name1=value1;...;nameN=valueN]&lt;/params&gt;<br>
 *    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;info&gt;[name1=value1;...;nameN=valueN]&lt;/info&gt;<br> 
 *
 *    <br>
 *   &nbsp;&nbsp;&nbsp;&nbsp;&lt;/[OPTIONAL_SECTION_NAME]&gt;<br>
 *  &nbsp;&nbsp;&nbsp;&lt;/db&gt;<br>
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
 * @see jdb.DBConsts#XML_DB_ROOT_TAG XML_DB_ROOT_TAG
*/

public class ExtendedDBConfig extends BaseDBConfig
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  public String   getStringPassword()
   {
    String pass;
    if (this.getPassword() != null) {pass = this.getPassword().getPassword();}
    else                            {pass = null;}
    return pass;
   }
  
  public void setPassword(String password)
   {
    try {this.setPassword(new Password(password));}
    catch (EmptyPassException e) {logger.error(e.getMessage());}
   }

  /** Конструктор по умолчанию - создает пустой экземпляр данного класса и больше ничего не делает. */
  public ExtendedDBConfig() {}

  /**
   * Конструктор загружает данные из указанного конфигурационного xml-файла.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public ExtendedDBConfig(String fileName, String sectionName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, usePlainPass);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. В конструкторе из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws DBModuleConfigException ИС - ошибка при загрузке конфигурации из xml-файла.
  */
  public ExtendedDBConfig(String fileName, String sectionName)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, sectionName);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. Параметры данным методом читаются из корня
   * элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public ExtendedDBConfig(String fileName, boolean usePlainPass) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, usePlainPass);}

  /**
   * Конструктор загружает данные из конфигурационного xml-файла. В конструкторе из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS.
   * Параметры данным конструктором читаются из элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public ExtendedDBConfig(String fileName) throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName);}

  /**
   * Метод загружает данные из указанного конфигурационного xml-файла из раздела &lt;db&gt;. В файле обязательно должен
   * быть корневой (ROOT) элемент над разделом &lt;db&gt;. Корневой элемент может быть любым. Данные (теги) могут находиться
   * как в секции &lt;db&gt;, так и в любой подсекции указанной секции (имя подсекции указывется параметром sectionName).
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры. Т.е.
   * возможно хранение в одном конфиг файле одновременно нескольких конфигураций соединения с СУБД (под разными именами
   * подсекций (подразделов) в разделе &lt;db&gt;, например: db1, db2, ... , dbN). Подробнее см. javadoc модуля.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public void loadFromFile(String fileName, String sectionName, boolean usePlainPass)
   throws IOException, ConfigurationException, DBModuleConfigException
   {
    logger.debug("WORKING ExtendedDBConfig.loadFromFile().");
    // Если имя файла пусто - ошибка!
    if (StringUtils.isBlank(fileName))
     {throw new IOException("File name is blank!");}
    // Если файл не существует или это не файл - ошибка! В ошибку выводим и указанное и полное имя файла.
    else if ((!new File(fileName).exists()) || (!new File(fileName).isFile()))
     {throw new IOException("Specified file [" + fileName + "] doesn't exists or not a file! " +
                            "Absolute file path [" + FSUtils.fixFPath(new File(fileName).getAbsolutePath()) + "].");}

    // Класс xml-конфигурации
    XMLConfiguration config = new XMLConfiguration(fileName);
    // Загружаем (читаем) конфиг из файла
    logger.debug("Reading XML file...");
    config.load();
    // Формируем префикс имени (если есть имя секции, мы его используем, если же нет, то читаем конфиг прямо
    // из раздела (секции) <db></db>)
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}

    // Читаем из файла имя источника данных
    String dataSource = config.getString(prefix + DBConsts.DB_DATA_SOURCE);
    // Если имя источника данных указано - далее файл не читаем - инициализация окончена!
    if (!StringUtils.isBlank(dataSource))
     {
      logger.debug("JNDI data source name specifyed [" + dataSource + "]! Using it.");
      this.setDataSource(dataSource);
     }
    // Если же имя источника данных не указано - читаем из файла параметры для соединения с БД через JDBC
    else
     {
      logger.debug("JNDI data source name not specifyed! Processing JDBC parameters.");
      // ----- Тип СУБД (читаем из конфига)
      String strDbType = config.getString(prefix + DBConsts.DB_TYPE);
      // Если прочитанное значение не пусто - обрабатываем
      if (!StringUtils.isBlank(strDbType))
       {
        // Преобразуем прочитанное значение в формат класса DBType
        try {this.setDbType(DBConsts.DBType.valueOf(strDbType.toUpperCase()));}
        // Если значение не верно (нет такого в классе-перечислении) - ИС!
        catch (IllegalArgumentException e) {throw new DBModuleConfigException("Invalid DBMS type! [" + e.getMessage() + "]");}
       }
      // Если же значение пусто - ошибка! (тип СУБД не может быть пуст)
      else {throw new DBModuleConfigException("DBMS type and JNDI data source name are empty !");}
      // ----- Хост СУБД
      this.setHost(config.getString(prefix + DBConsts.DB_HOST));
      // ----- Имя сервера СУБД
      this.setServerName(config.getString(prefix + DBConsts.DB_SERVER));
      // ----- Имя БД
      this.setDbName(config.getString(prefix + DBConsts.DB_NAME));
      // ----- Пользователь СУБД
      this.setUser(config.getString(prefix + DBConsts.DB_USER));
      // ----- Пароль пользователя
      String strPass  = config.getString(prefix + DBConsts.DB_PWD);
      // Если пароль не пуст - обрабатываем его
      if (!StringUtils.isBlank(strPass))
       {
        // Если установлен флаг "простой пароль", то в данном теге находится просто пароль
        if (usePlainPass)
         {logger.info("This database connection config use plain password!"); this.setPassword((strPass));}
        // Если же флаг "простой пароль" сброшен (false), то в данном теге находится имя файла с
        // шифрованным паролем (имя или абсолютный путь)
        else
         {
          logger.debug("Usig password file [" + strPass + "].");
          try {this.setPassword((Password) FSUtils.deserializeObject(strPass, false));}
          catch (ClassNotFoundException e) {logger.error("Can't read password file! Reason: " + e.getMessage());}
          catch (ClassCastException e)     {logger.error("Can't read password file! Reason: " + e.getMessage());}
         }
       }
      // Если же прочитанный пароль пуст - сообщим об этом
      else {logger.warn("This config [" + fileName + "] has an empty password!");}
      // ----- Дополнительные параметры соединения (указываются name1=value1;name2=value2;...)
      this.setConnParams(StringUtils.trimToNull(config.getString(prefix + DBConsts.DB_CONN_PARAMS)));
      // ----- Дополнительная информация для соединения (указывается name1=value1;name2=value2;...)
      this.setConnInfo(Utils.getPropsFromString(config.getString(prefix + DBConsts.DB_CONN_INFO)));
     }
    
   }

  /**
   * Метод загружает данные из конфигурационного xml-файла. В данном методе по умолчанию из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS. 
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public void loadFromFile(String fileName, String sectionName)
   throws IOException, DBModuleConfigException, ConfigurationException
   {this.loadFromFile(fileName, sectionName, DBConsts.XML_USE_PLAIN_PASS);}

  /**
   * Метод загружает данные из конфигурационного xml-файла. Параметры данным методом читаются из корня элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param usePlainPass boolean использовать ли открытый пароль к БД или нет. Если используется открытый пароль, то
   * в соотв. теге конфига будет указан пароль, если же открытый пароль не используем, то в соотв. теге будет указан
   * файл, в котором находится пароль (файл специализированного формата).
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public void loadFromFile(String fileName, boolean usePlainPass)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, null, usePlainPass);}

  /**
   * Метод загружает данные из конфигурационного xml-файла. В данном методе по умолчанию из тега с паролем читается имя
   * файла пароля или открытый пароль - данная функциональность зависит от значения константы DBConfigConsts.USE_PLAIN_PASS.
   * Параметры данным методом читаются из элемента &lt;db&gt;.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public void loadFromFile(String fileName)
   throws DBModuleConfigException, IOException, ConfigurationException
   {this.loadFromFile(fileName, DBConsts.XML_USE_PLAIN_PASS);}

  /**
   * Метод создает и возвращает объект XMLConfiguration для того, чтобы можно было сохранять содержимое данного
   * класса в файл или объединять с другими конфигурациями (и также сохранять в файл).
   * @param rootName String имя корневого (root) элемента для создаваемого файла конфигурации.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, в которую будут записаны параметры.
   * @return XMLConfiguration созданный объект на основании состояния данного экземпляра класса.
  */
  private XMLConfiguration getXMLConfig(String rootName, String sectionName)
   {
    logger.debug("getXMLConfig(): generating XMLConfiguration object.");
    // Создаем экземпляр класса XML-конфигурации
    XMLConfiguration config = new XMLConfiguration();
    // Корневой элемент (выбираем в зависимости от параметра rootName). Если корневой элемнт указан - берем
    // его, если же не указан - корневой элемнт по умолчанию используем.
    if (!StringUtils.isBlank(rootName)) {config.setRootElementName(rootName);}
    else                                {config.setRootElementName(DBConsts.XML_DB_ROOT_TAG);}
    // Формируем префикс
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}
    
    // Добавляем данные. Если в конфиге указано имя источника данных (поле dataSource), то сохраняем только его.
    if (!StringUtils.isBlank(this.getDataSource()))
     {
      logger.debug("Adding JNDI data source name to XML config object.");
      config.addProperty(prefix + DBConsts.DB_DATA_SOURCE, this.getDataSource());
     }
    // Если же поле "имя источника данных" пусто, то сохраняем параметры для соединения через JDBC-драйвер.
    else
     {
      logger.debug("Adding JDBC parameters to XML config object.");
      // Непосредственно добавляем параметры
      config.addProperty(prefix + DBConsts.DB_TYPE,        this.getDbType());
      config.addProperty(prefix + DBConsts.DB_HOST,        this.getHost());
      config.addProperty(prefix + DBConsts.DB_SERVER,      this.getServerName());
      config.addProperty(prefix + DBConsts.DB_NAME,        this.getDbName());
      config.addProperty(prefix + DBConsts.DB_USER,        this.getUser());
      // При сохранении пароля он всегда сохраняется в файл (если пароль не пуст)
      if (this.getPassword() != null)
       {
        logger.debug("Password is not empty. Processing.");
        try
         {
          String passFilePath = FSUtils.serializeObject(this.getPassword(), SystemUtils.getUserDir().getAbsolutePath());
          String passFileName = passFilePath.substring(passFilePath.lastIndexOf("/") + 1);
          config.addProperty(prefix + DBConsts.DB_PWD, passFileName);
         }
        catch (EmptyObjectException e) {logger.error("Can't save password file! Reason: " + e.getMessage());}
        catch (IOException e)          {logger.error("Can't save password file! Reason: " + e.getMessage());}
       }
      // Если пароль пуст - сообщим об этом
      else {logger.warn("Empty password! Can't process it.");}
      // Дополнительный параметры соединения
      config.addProperty(prefix + DBConsts.DB_CONN_PARAMS, this.getConnParams());
      config.addProperty(prefix + DBConsts.DB_CONN_INFO,   Utils.getStringFromProps(this.getConnInfo()));
     }
    // Возвращаем результат
    return config;
   }
  
  /**
   * Метод сохраняет текущую конфигурацию для соединения (состояние экземпляра класса) с СУБД в файл с указанным
   * именем fileName.
   * @param fileName String имя файла, в который будут сохранены текущие параметры соединения (состояние класса).
   * @param rootName String имя корневого (root) элемента для создаваемого файла конфигурации.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, в которую будут записаны параметры.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при сохранении конфигурации в файл.
  */
  public void saveToFile(String fileName, String rootName, String sectionName) throws ConfigurationException
   {
    logger.debug("saveToFile(): trying to save to [" + fileName + "].");
    // Непосредственно сохранение файла
    this.getXMLConfig(rootName, sectionName).save(fileName);
   }

  /**
   * Метод сохраняет текущую конфигурацию для соединения (состояние экземпляра класса) с СУБД в файл с указанным
   * именем fileName.
   * @param fileName String имя файла, в который будут сохранены текущие параметры соединения (состояние класса).
   * @param rootName String имя корневого (root) элемента для создаваемого файла конфигурации.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при сохранении конфигурации в файл.
  */
  public void saveToFile(String fileName, String rootName) throws ConfigurationException
   {this.saveToFile(fileName, rootName, null);}

  /**
   * Метод сохраняет текущую конфигурацию для соединения (состояние экземпляра класса) с СУБД в файл с указанным
   * именем fileName.
   * @param fileName String имя файла, в который будут сохранены текущие параметры соединения (состояние класса).
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при сохранении конфигурации в файл.
  */
  public void saveToFile(String fileName) throws ConfigurationException {this.saveToFile(fileName, null);}

  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb", "jlib", "org.apache.commons"});
    Logger logger = Logger.getLogger("jdb");

    try
     {
      ExtendedDBConfig config = new ExtendedDBConfig();
      config.loadFromFile("stormConn.xml", "fff");
      logger.info("->" + config);
      
      /*
      JdbcConfig config2 = new JdbcConfig();
      config2.loadFromFile("asdf", "informix", false);
      config2.setDbName("123");
      config2.saveToFile("asdf", "kkk", "new_section");
      logger.info(config2.toString());
      */
      
      /*
      config.loadFromFile("dbConfig.xml", false);
      config.password = null;
      Properties props = new Properties();
      props.setProperty("asdf", "fdsa");
      props.setProperty("ert", "tre");
      config.connInfo = props;
      logger.info(config.toString());
      config.saveToFile("asdf");
      */
     }
    catch (IOException e) {logger.error(e.getMessage());}
    catch (ConfigurationException e) {logger.error(e.getMessage());}
    catch (DBModuleConfigException e) {logger.error(e.getMessage());}
    
    //catch (EmptyDBMSTypeException e) {logger.error(e.getMessage());}
    //catch (InvalidDBMSTypeException e) {logger.error(e.getMessage());}
    //catch (EmptyPassException e) {logger.error(e.getMessage());}
   }

 }