package jdb.config.load;

import dgusev.dbpilot.DBConsts;
import jdb.config.common.CommonModuleConfig;
import jdb.config.common.ConfigInterface;
import jdb.exceptions.DBModuleConfigException;
import jdb.model.integrity.DBIntegrityModel;
import jdb.model.time.DBTimedModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * Модуль конфигурации для работы модулей сериализации/десериализации данных. Модуль создан для того, чтобы методы
 * сериализации/десериализации не были многопараметрическими (данный конфиг удобное хранилище всех параметров).
 * @author Gusev Dmitry (019gus)
 * @version 7.0 (DATE: 11.11.2010)
 *
 * @deprecated данный класс не рекомендуется использовать, т.к. для загрузки/выгрузки БД на диск используется класс
 * {@link jdb.nextGen.DBasesLoader DBasesLoader} вместо класса {@link jdb.processing.loading.DBLoader}, который и
 * использовал данный конфиг для своей работы.
*/

public class DBLoaderConfig extends CommonModuleConfig implements ConfigInterface
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /**
   * Путь к каталогу с сериализованной БД или таблицей (что именно находится в этом параметре определяется его
   * использованием - для модуля работы с таблицами это путь к каталогу одной таблицы, для модуля работы с БД - это путь
   * к каталогу БД). Также различается содержимое данного поля в зависимости от назначения модуля (сериализация/десериализация) -
   * для модулей сериализации - в этот каталог будет производиться выгрузка данных, для модулей десериализации - в этом
   * каталоге лежат (должны лежать) сериализованные данные.
  */
  private String           path                = null;
  /** Удалять исходные файлы или нет. Используется только модулями десериализации. По умолчанию исходные файлы не удаляются. */
  private boolean          isDeleteSource      = false;
  /** Модель целостности БД. Используется при сериализации данных из БД. */
  private DBIntegrityModel dbIntegrityModel    = null;
  /** Модель БД с указанием времени. Используется при сериализации данных из БД. */
  private DBTimedModel     dbTimedModel        = null;
  /**
   * Количество записей одной таблицы для сериализации в один файл. Этот параметр влияет и на модуль десериализации
   * данных - при слишком большой его величине десериализация становится очень требовательной к памяти ПК, что приводит
   * к неработоспособности при малом ее количестве (ошибка OutOfMemory). Также величина параметра влияет на размер
   * одного файла с данными таблицы, но т.к. файл архивируется (ZIP), то размер растет не сильно. Слишком маленькое
   * значение данного параметра приведет к формированию большого количества файлов (при сериализации) и к существенным
   * потерям производительности за счет обработки большого количества файлов (и при сериализации, и при десериализации).
   * Рекомендуемое значение данного параметра лежит в диапазоне 250 - 2000 (записей). Также при выборе значения данного
   * параметра рекомендуется учитывать длину одной записи - если длина небольшая, то можно сериализовать больше строк в
   * один файл без существенного увеличения его (файла) размера, если же длина одной записи существенна - 30Кб и более,
   * рекомендуется выбирать меньшее значение (это просто общие соображения).
   * Данное значение влияет ТОЛЬКО на сериализацию БД.
   * Если при выполнении сериализации значение не указано (или <= 0), то используется значение по умолчанию - см. константу
   * SERIALIZATION_TABLE_FRACTION в модуле DBConsts.
  */
  private int              serializeFraction   = 0;
  /**
   * Параметр указывает, использовать для непосредственной загрузки данных полный или частичный скрипт (имеется в виду
   * формируемый для загрузки данных в таблицы sql-батч или sql-скрип). При использовании полного скрипта все файлы для
   * одной таблицы собираются в один большой sql-скрипт(батч) и затем этот скрипт выполняется. Недостатком данного метода
   * является требование к объему доступной памяти (при больших объемах данных может возникать ИС OutOfMemory). При
   * использовании частичного скрипта каждый файл преобразуется в скрипт и сразу же выполняется. Требования к памяти не
   * очень серьезные. Производительность у обоих методов примерно одинакова. Предпочтение к использованию - метод частичного
   * скрипта, хотя для каждой задачи выбор должен быть индивидуален.
  */
  private boolean          useFullScript       = false;
  /**
   * Наименование ключевого поля, на основании которого производится загрузка с диска данных в таблицу БД. Если данное
   * поле имеет неверное значение, то модуль загрузки данных может повредить данные в таблице, куда загружаются данные
   * из файлов на диске.
  */
  private String           keyFieldName        = DBConsts.FIELD_NAME_KEY;
  /** Очищать ли таблицу БД перед загрузкой в нее данных с диска. По умолчанию - выключено. */
  private boolean          isClearTableBeforeLoad = false;
  /**
   * Опция используется только для соединения с СУБД MSSQL. Если включена, то перед загрузкой данных в БД с диска (для
   * СУБД MS SQL Server) будет выполнена инструкция "SET IDENTITY_INSERT [TABLENAME] ON", которая позволяет вставлять
   * значения в столбцы первичного ключа с автоинкрементом. После выполнения загрузки данных в таблицу будет выполнена
   * инструкция "SET IDENTITY_INSERT [TABLENAME] OFF". Если же данная опция выключена, то указанные инструкции выполняться
   * не будут.
  */
  private boolean          useSetIdentityInsert   = true;

  /** Конструктор по умолчанию. */
  public DBLoaderConfig() {}

  /**
   * Конструктор создает экземпляр данного класса на основе уже существующего (если он не содержит ошибок).
   * @param config DBLoaderConfig класс, на основе данных которого создается данный экземпляр.
  */
  public DBLoaderConfig(DBLoaderConfig config)
   {
    String configErrors = null; // DBUtils.getConfigErrors(config);
    // Если с указанным конфигом все в порядке - работаем
    if (StringUtils.isBlank(configErrors))
     {
      this.setDbConfig(config.getDbConfig()); // оба экземпляра классов имеют ссылку на один экземпляр класса DBConfig!
      this.setMonitor(config.getMonitor());   // оба экземпляра классов имеют ссылку на один экземпляр класса DBProcessingMonitor
      this.setMonitorMsgPrefix(config.getMonitorMsgPrefix());
      this.setOperationsCount(config.getOperationsCount());
      this.setDbmsConnNumber(config.getDbmsConnNumber());
      this.setMultiThreads(config.isMultiThreads());
      this.serializeFraction = config.getSerializeFraction();
      this.dbIntegrityModel  = config.getDbIntegrityModel(); // оба экземпляра классов имеют ссылку на один экземпляр класса модели!
      this.dbTimedModel      = config.getDbTimedModel(); // оба экземпляра классов имеют ссылку на один экземпляр класса модели!
      this.isDeleteSource    = config.isDeleteSource();
      this.path              = config.getPath();
      this.useFullScript     = config.isUseFullScript();
      this.isClearTableBeforeLoad = config.isClearTableBeforeLoad();
      this.useSetIdentityInsert   = config.isUseSetIdentityInsert();
     }
    // Если указанный конфиг ошибочен - сообщаем в лог
    else {logger.error("BatchConfig() constructor: can't get data from SerializationConfig! Reason: " + configErrors);}
   }

  public String getPath() {
   return path;
  }

  public void setPath(String path) {
   this.path = path;
  }

  public boolean isDeleteSource() {
   return isDeleteSource;
  }

  public void setDeleteSource(boolean deleteSource) {
   isDeleteSource = deleteSource;
  }

  public DBIntegrityModel getDbIntegrityModel() {
   return dbIntegrityModel;
  }

  public void setDbIntegrityModel(DBIntegrityModel dbIntegrityModel) {
   this.dbIntegrityModel = dbIntegrityModel;
  }

  public DBTimedModel getDbTimedModel() {
   return dbTimedModel;
  }

  public void setDbTimedModel(DBTimedModel dbTimedModel) {
   this.dbTimedModel = dbTimedModel;
  }

  public int getSerializeFraction() {
   return serializeFraction;
  }

  public void setSerializeFraction(int serializeFraction) {
   this.serializeFraction = serializeFraction;
  }

  public boolean isUseFullScript() {
   return useFullScript;
  }

  public void setUseFullScript(boolean useFullScript) {
   this.useFullScript = useFullScript;
  }

  public String getKeyFieldName() {
   return keyFieldName;
  }

  public void setKeyFieldName(String keyFieldName) {
   this.keyFieldName = keyFieldName;
  }

  public boolean isClearTableBeforeLoad() {
   return isClearTableBeforeLoad;
  }

  public void setClearTableBeforeLoad(boolean clearTableBeforeLoad) {
   isClearTableBeforeLoad = clearTableBeforeLoad;
  }

  public boolean isUseSetIdentityInsert() {
   return useSetIdentityInsert;
  }

  public void setUseSetIdentityInsert(boolean useSetIdentityInsert) {
   this.useSetIdentityInsert = useSetIdentityInsert;
  }

  /**
   * Метод возвращает значение ИСТИНА/ЛОЖЬ в зависимости от того, "разрешена" ли указанная таблица для подключения к
   * СУБД, ссылка на которое хранится в одном из полей данного класса (доступ методами getDBConfig() и setDBConfig()).
   * Данный метод для проверки использует одноименный метод класса DBConfig.
   * @param tableName String имя проверяемой на валидность таблицы.
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, "разрешена" ли указанная таблица для данного подключения к СУБД.
   * @throws DBModuleConfigException ИС - ошибки конфигурирования соединения с СУБД, для которого проверяется валидность
   * таблицы (в данном случае - скорее всего неверно указан или не указан совсем тип СУБД (необходим для проверки таблицы
   * по системному каталогу указанного типа СУБД)).
  */
  public boolean isTableAllowed(String tableName) throws DBModuleConfigException
   {
    boolean result;
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {result = this.getDbConfig().isTableAllowed(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
    return result;
   }

  /**
   * Метод добавляет одну таблицу к списку "разрешенных" таблиц. Таблица добавляется только если указано
   * непустое имя.
   * @param tableName String имя добавляемой таблицы.
   * @throws DBModuleConfigException ошибка конфигурации данного модуля (скорее всего пусто поле DBConfig)
  */
  public void addAllowedTable(String tableName) throws DBModuleConfigException
   {
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().addAllowedTable(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * Данный метод добавляет одну таблицу к списку "запрещенных" таблиц. Таблица добавляется, только если указанное
   * имя таблицы не пустое.
   * @param tableName String имя добавляемой таблицы.
   * @throws DBModuleConfigException ошибка конфигурации данного модуля (скорее всего пусто поле DBConfig)
  */
  public void addDeprecatedTable(String tableName) throws DBModuleConfigException
   {
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().addDeprecatedTable(tableName);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * Сброс списка "разрешенных" таблиц.
   * @throws DBModuleConfigException ошибка конфигурации данного модуля (скорее всего пусто поле DBConfig)
  */
  public void resetAllowedTables() throws DBModuleConfigException
   {
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().setAllowedTables(null);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * Сброс списка "запрещенных" таблиц.
   * @throws DBModuleConfigException ошибка конфигурации данного модуля (скорее всего пусто поле DBConfig)
  */
  public void resetDeprecatedTables() throws DBModuleConfigException
   {
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().setDeprecatedTables(null);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * Сброс всех ограничений (списков "разрешенных" и "запрещенных" таблиц).
   * @throws DBModuleConfigException ошибка конфигурации данного модуля (скорее всего пусто поле DBConfig) 
  */
  public void resetConstraints() throws DBModuleConfigException
   {
    String configErrors = null; // DBUtils.getConfigErrors(this.getDbConfig());
    if (StringUtils.isBlank(configErrors)) {this.getDbConfig().setConstraints(null, null);}
    else {throw new DBModuleConfigException(configErrors);}
   }

  /**
   * Метод возвращает описание ошибок конфигурации экземпляра класса. Если ошибок конфигурации нет, то метод вернет NULL.
   * @return String описание ошибок конфигурации или NULL.
  */
  @Override
  public String getConfigErrors()
   {
    String result = null;
    String errors = super.getConfigErrors();
    if (!StringUtils.isBlank(errors))   {result = errors;}
    else if (StringUtils.isBlank(path)) {result = "Path to data catalog is empty (or null)!";}
    return result;
   }

  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            appendSuper(super.toString()).
            append("path", path).
            append("isDeleteSource", isDeleteSource).
            append("dbIntegrityModel", dbIntegrityModel).
            append("dbTimedModel", dbTimedModel).
            toString();
   }

  /**
   * Метод только для тестирования данного класса.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    Logger logger = Logger.getLogger("jdb");
    DBLoaderConfig config = new DBLoaderConfig();
    logger.debug(config.toString());
   }

 }