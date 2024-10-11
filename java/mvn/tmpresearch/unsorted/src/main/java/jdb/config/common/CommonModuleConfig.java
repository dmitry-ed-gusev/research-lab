package jdb.config.common;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.monitoring.DBProcessingMonitor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Общий класс-предок для классов конфигурирования модулей сложной обработки данных. Использование данного класса отдельно
 * представляется нецелесообразным.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 21.06.2010)
*/

public class CommonModuleConfig implements ConfigInterface
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /** Конфигурация для соединения с СУБД. */
  private DBConfig            dbConfig            = null;
  /**
   * Данный параметр используется только модулем многопотоковой обработки sql-батча. Количество соединений с СУБД,
   * одно соединение - один поток. ВНИМАНИЕ! Необходимо помнить - один поток - это одно соединение с СУБД, всегда
   * помните о максимальном количестве подключений к СУБД! В классе конфигурирования модуля сериализации/десериализации
   * этот параметр может использоваться для передачи модулю многопоточной обработки sql-батча.
  */
  private int                 dbmsConnNumber      = 0;
  /** Интерфейс процесса-монитора для отображения хода работы модуля сериализации/десериализации. */
  private DBProcessingMonitor monitor             = null;
  /**
   * Количество выполненных операций или запросов на вставку/выборку данных, по достижении которого (и кратных
   * ему значений) будет выдана отладочная информация процессу-монитору и выведено отладочное сообщение [DEBUG] в лог.
   */
  private int                 operationsCount     = 0;
  /**
   * Префиксное сообщение для сообщения монитора. Сообщение будет добавлено ПЕРЕД сообщением монитора
   * (сообщение монитора будет иметь вид: [[префикс] + [сообщение монитора]])
  */
  private String              monitorMsgPrefix    = null;
  /** Включено/выключено многопотоковое выполнение sql-батча. */
  private boolean             isMultiThreads      = false;
  /** Использование фильтрации sql-запросов. По умолчанию - включено. */
  private boolean             useSqlFilter        = true;

  public DBConfig getDbConfig() {
   return dbConfig;
  }

  public void setDbConfig(DBConfig dbConfig) {
   this.dbConfig = dbConfig;
  }

  public int getDbmsConnNumber() {
   return dbmsConnNumber;
  }

  public void setDbmsConnNumber(int dbmsConnNumber) {
   this.dbmsConnNumber = dbmsConnNumber;
  }

  public DBProcessingMonitor getMonitor() {
   return monitor;
  }

  public void setMonitor(DBProcessingMonitor monitor) {
   this.monitor = monitor;
  }

  public int getOperationsCount() {
   return operationsCount;
  }

  public void setOperationsCount(int operationsCount) {
   this.operationsCount = operationsCount;
  }

  public String getMonitorMsgPrefix() {
   return monitorMsgPrefix;
  }

  public void setMonitorMsgPrefix(String monitorMsgPrefix) {
   this.monitorMsgPrefix = monitorMsgPrefix;
  }

  public boolean isMultiThreads() {
   return isMultiThreads;
  }

  public void setMultiThreads(boolean multiThreads) {
   isMultiThreads = multiThreads;
  }

  public boolean isUseSqlFilter() {
   return useSqlFilter;
  }

  public void setUseSqlFilter(boolean useSqlFilter) {
   this.useSqlFilter = useSqlFilter;
  }

  /**
   * Метод загружает несколько своих параметров из указанного конфига. Параметры загружаются из раздела &lt;db&gt;, из
   * указанной секции sectionName. Если имя секции не указано - параметры читаются из раздела &lt;db&gt;. Данный раздел
   * не должен быть корневым - он должен находиться внутри другого раздела (корневого или ROOT-раздела). Параметр
   * loadDBConfig указывает, надо ли загружать конфигурацию для соединения с СУБД из данного раздела конфига (ИСТИНА -
   * загружаем, ЛОЖЬ - нет). Параметры для соединения с СУБД будут прочитаны из того же раздела &lt;db&gt; и той же
   * секции sectionName.
   * @param fileName String xml-файл, из которого будем загружать конфигурацию.
   * @param sectionName String имя подсекции секции &lt;db&gt; в конфиг-файле, из которой будут читаться параметры. Т.е.
   * возможно хранение в одном конфиг файле одновременно нескольких конфигураций (под разными именами подсекций (подразделов)
   * в разделе &lt;db&gt;). Если указан параметр loadDBConfig=TRUE, то из этой же подсекции будут прочитаны параметры для
   * соединения с СУБД.
   * @param loadDBConfig boolean параметр указывает, необходимо ли читать из конфига параметры для соединения с СУБД.
   * @throws java.io.IOException ИС - указано пустое имя файла, файл не существует и т.п.
   * @throws org.apache.commons.configuration.ConfigurationException ИС - ошибка при загрузке конфигурации из xml-файла.
   * @throws jdb.exceptions.DBModuleConfigException - ошибка конфигурационного файла - неверный тип СУБД, тип СУБД отсутствует
   * и т.п. ошибки (связанные с конфигурированием).
  */
  public void loadFromFile(String fileName, String sectionName, boolean loadDBConfig) throws IOException, org.apache.commons.configuration2.ex.ConfigurationException, ConfigurationException {

    logger.debug("WORKING CommonModuleConfig.loadFromFile().");
    // Если имя файла пусто - ошибка!
    if (StringUtils.isBlank(fileName))
     {throw new IOException("File name is blank!");}
    // Если файл не существует или это не файл - ошибка!
    else if ((!new File(fileName).exists()) || (!new File(fileName).isFile()))
     {throw new IOException("File [" + fileName + "] doesn't exists or not a file!");}
    // Если указан параметр loadDBConfig=true, то загружаем конфиг для соединения с СУБД. Конфиг загружается
    // из той же подсекции раздела <db> конфига.
    if (loadDBConfig)
     {
      logger.debug("MODE: loading DB connection config from xml-file.");
      this.dbConfig = new DBConfig();
      this.dbConfig.loadFromFile(fileName, sectionName, false);
     }
    else {logger.debug("MODE: skipping DB connection config loading.");}

    // Класс xml-конфигурации
    XMLConfiguration config = new XMLConfiguration(fileName);
    // Загружаем (читаем) конфиг из файла
    config.load();
    // Формируем префикс имени (если есть имя секции, мы его используем, если же нет, то читаем конфиг прямо
    // из раздела (секции) <db></db>)
    String prefix;
    if (!StringUtils.isBlank(sectionName)) {prefix = DBConsts.XML_DB_TAG + "." + sectionName + ".";}
    else                                   {prefix = DBConsts.XML_DB_TAG + ".";}
    // Читаем из файла конфига количество соединений dbmsConnNumber
    String dbmsConnNumberString = config.getString(prefix + DBConsts.XML_DBMS_CONN_NUMBER);
    try {this.dbmsConnNumber = Integer.valueOf(dbmsConnNumberString);}
    catch (NumberFormatException e) {logger.warn(e.getMessage() + ". Config file property [" + DBConsts.XML_DBMS_CONN_NUMBER + "]");}
    

    // Читаем из конфига количество операций для выдачи сообщений монитора
    String opsCounterString = config.getString(prefix + DBConsts.XML_OPERATIONS_COUNTER);
    try {this.operationsCount = Integer.valueOf(opsCounterString);}
    catch (NumberFormatException e) {logger.warn(e.getMessage() + ". Config file property [" + DBConsts.XML_OPERATIONS_COUNTER + "]");}
    // Читаем опцию вкл/выкл многопоточности
    this.isMultiThreads = Boolean.valueOf(config.getString(prefix + DBConsts.XML_MULTI_THREADS));
   }

  @Override
  public String getConfigErrors()
   {
    String result = null;
    String dbConfigErrors = null; //DBUtils.getConfigErrors(dbConfig);
    if (!StringUtils.isBlank(dbConfigErrors))      {result = dbConfigErrors;}
    return result;
   }

  @Override
  public String toString() {
   return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
           append("dbConfig", dbConfig).
           append("dbmsConnNumber", dbmsConnNumber).
           append("monitor", monitor).
           append("operationsCount", operationsCount).
           append("monitorMsgPrefix", monitorMsgPrefix).
           append("isMultiThreads", isMultiThreads).
           toString();
  }

}