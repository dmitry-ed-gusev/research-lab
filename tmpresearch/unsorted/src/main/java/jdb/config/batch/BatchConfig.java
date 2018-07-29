package jdb.config.batch;

import jdb.config.common.CommonModuleConfig;
import jdb.config.common.ConfigInterface;
import jdb.config.load.DBLoaderConfig;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Конфигурация для модуля(модулей) выполнения пакетов sql-запросов (sql-батчей). Пакет (батч) состоит из нескольких
 * частей - однократно запускаемая префиксная часть (run-once prefix), однократно запускаемая постфиксная часть (run-once
 * postfix), часть-префикс, часть-постфикс и тело батча. Ниже подробнее рассказано об этих частях. Очень рекомендую почитать.
 * <ul>
 * <li>однократно запускаемая префиксная часть - часть батча (тоже батч - набор sql-запросов), которая гарантированно должна
 * выполняться однократно перед запуском основного батча. Выделена в отдельную часть для многопотокового выполнения батча -
 * при таком его выполнении эта часть батча будет выполнена ОДИН раз перед выполнением всех потоков. На потоки эта часть батча
 * рабиваться не будет в любом случае - она всегда выполняется в одном потоке.
 * <li>однократно запускаемая постфиксная часть - часть батча (тоже батч - набор sql-запросов), которая гарантированно должна
 * выполняться однократно после запуска основного батча. Выделена в отдельную часть для многопотокового выполнения батча -
 * при таком его выполнении эта часть батча будет выполнена ОДИН раз после выполнения всех потоков. На потоки эта часть батча
 * рабиваться не будет в любом случае - она всегда выполняется в одном потоке.
 * <li>батч-префикс - выполняется перед основным телом батча. При многопотоковом выполнении батча префикс добавляется
 * каждому потоку ПЕРЕД основными запросами, выполняемыми потоком.
 * <li>батч-постфикс - выполняется после основного тела батча. При многопотоковом выполнении батча постфикс добавляется
 * каждому потоку ПОСЛЕ основных запросов, выполняемых потоком.
 * <li>тело батча - основной набор запросов батча. При многопотоковым выполнении батча тело поровну делится на количество
 * потоков (есть еще и другие условия - см. модуль выполнения батча).
 * </ul>
 * <br><b>ВАЖНО! Алгоритм построения полного батча (для выполнения) из частей.</b><br>
 * <ul>
 * <li>ОДНОПОТОКОВОЕ ВЫПОЛНЕНИЕ: [БАТЧ-ПРЕФИКС] + [ОДНОКРАТНО ЗАПУСКАЕМЫЙ ПРЕФИКСНЫЙ БАТЧ] + [ТЕЛО БАТЧА] +
 * [ОДНОКРАТНО ЗАПУСКАЕМЫЙ ПОСТФИКСНЫЙ БАТЧ] + [БАТЧ-ПОСТФИКС].
 * <li>МНОГОПОТОКОВОЕ ВЫПОЛНЕНИЕ: сначала в одном потоке выполняется связка (перед созданием и запуском нескольких потоков)
 * [БАТЧ-ПРЕФИКС] + [ОДНОКРАТНО ЗАПУСКАЕМЫЙ ПРЕФИКСНЫЙ БАТЧ] + [БАТЧ-ПОСТФИКС], затем в каждом потоке выполняется
 * следующая связка [БАТЧ-ПРЕФИКС] + [ЧАСТЬ ТЕЛА БАТЧА] + [БАТЧ-ПОСТФИКС], после завершения выполнения всех потоков будет
 * выполнена последняя связка [БАТЧ-ПРЕФИКС] + [ОДНОКРАТНО ЗАПУСКАЕМЫЙ ПОСТФИКСНЫЙ БАТЧ] + [БАТЧ-ПОСТФИКС]. 
 * </ul>
 * Об этом алгоритме следует помнить при формировании частей батча. При неправильно сформированных частях батча возможно
 * повреждение данных. Будь бдителен! :)
 * Особенности выполнения батча см. в описании следующих классов:
 * {@link jdb.processing.sql.execution.batch.executors.SingleThreadSqlBatchExecutor},
 * {@link jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor},
 * {@link jdb.processing.sql.execution.batch.SqlBatcher}.
 *  
 * @author Gusev Dmitry (019gus)
 * @version 9.0 (DATE: 18.11.2010)
*/

public class BatchConfig extends CommonModuleConfig implements ConfigInterface
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  /** Префикс-батч для основного тела батча. */
  private SimpleBatch batchPrefix           = null;
  /** Однократно запускаемый префикс-батч. */
  private SimpleBatch batchRunOncePrefix = null;
  /** Непосредственно тело sql-батча, содержащее sql запросы. Запросы должны быть не-SELECT типа. */
  private SimpleBatch batchBody             = null;
  /** Однократно запускаемый постфикс-батч. */
  private SimpleBatch batchRunOncePostfix   = null;
  /** Постфикс-батч для основного тела батча. */
  private SimpleBatch batchPostfix          = null;

  /** Останавливать или нет выполнение списка запросов при ошибке. */
  private boolean     stopOnError           = false;

  /** Конструктор по умолчанию. Поля класса инициализируются значениями по умолчанию */
  public BatchConfig() {}

  /**
   * Данный конструктор создает экземпляр класса и инициализирует поля значениями из переданного параметра - экземпляра
   * класса DbLoaderConfig, если он не пуст. Если переданный параметр пуст или ошибочен, то поля класса инициализируются
   * значениями по умолчанию.
   * @param config DBLoaderConfig параметр, который используется в качестве источника значений для полей данного
   * класса.
  */
  public BatchConfig(DBLoaderConfig config)
   {
    String configErrors = DBUtils.getConfigErrors(config);
    // Если с указанным конфигом все в порядке - работаем
    if (StringUtils.isBlank(configErrors))
     {
      this.setDbConfig(config.getDbConfig()); // оба экземпляра классов имеют ссылку на один экземпляр класса DBConfig!
      this.setMonitor(config.getMonitor());   // оба экземпляра классов имеют ссылку на один экземпляр класса DBProcessingMonitor
      this.setMonitorMsgPrefix(config.getMonitorMsgPrefix());
      this.setOperationsCount(config.getOperationsCount());
      this.setDbmsConnNumber(config.getDbmsConnNumber());
      this.setMultiThreads(config.isMultiThreads());
     }
    // Если указанный конфиг ошибочен - сообщаем в лог
    else {logger.error("BatchConfig() constructor: can't get data from DBLoaderConfig! Reason: " + configErrors);}
   }

  public SimpleBatch getBatchRunOncePrefix() {
   return batchRunOncePrefix;
  }

  public SimpleBatch getBatchRunOncePostfix() {
   return batchRunOncePostfix;
  }

  public SimpleBatch getBatchPrefix() {
   return batchPrefix;
  }

  public SimpleBatch getBatchPostfix() {
   return batchPostfix;
  }

  public SimpleBatch getBatchBody() {
   return batchBody;
  }

  /**
   * Установка значения тела sql-батча. При установке тела батча присходит его обработка (препроцессинг) - удаление
   * пустых и select-запросов. Если после обработки батч окажется пустым, присвоения значения не происходит и выводится
   * соотв. запись в лог. Если тело батча данного экземпляра конфига уже содержало какие-либо запросы (уже было
   * инициализировано), то эти запросы будут затерты устанавливаемым батчем.
   * @param batch ArrayList[String] устанавливаемое значение тела батча.
  */
  public void setBatchBody(ArrayList<String> batch)
   {this.batchBody = new SimpleBatch(batch, this.isUseSqlFilter());}

  /**
   * Установка значения однократно запускаемого префиксного sql-батча. При установке однократно запускаемого префиксного
   * sql-батча присходит его обработка (препроцессинг) - удаление пустых и select-запросов. Если после обработки батч
   * окажется пустым, присвоения значения не происходит и выводится соотв. запись в лог. Если однократно запускаемый
   * префиксный батч данного экземпляра конфига уже содержал какие-либо запросы (уже был инициализирован), то эти запросы
   * будут затерты устанавливаемым батчем. Запросы, содержащиеся в однократно запускаемом префиксном батче, будут
   * гарантированно выполнены только один раз - даже при многопотоковом выполнении батча они будут выполнены однократно
   * перед генерацией и запуском потоков.
   * @param batch ArrayList[String] устанавливаемое значение однократно запускаемого префиксного батча.
  */
  public void setBatchRunOncePrefix(ArrayList<String> batch)
   {this.batchRunOncePrefix = new SimpleBatch(batch, this.isUseSqlFilter());}

  /**
   * Установка значения однократно запускаемого постфиксного sql-батча. При установке однократно запускаемого постфиксного
   * sql-батча присходит его обработка (препроцессинг) - удаление пустых и select-запросов. Если после обработки батч
   * окажется пустым, присвоения значения не происходит и выводится соотв. запись в лог. Если однократно запускаемый
   * постфиксный батч данного экземпляра конфига уже содержал какие-либо запросы (уже был инициализирован), то эти запросы
   * будут затерты устанавливаемым батчем. Запросы, содержащиеся в однократно запускаемом постфиксном батче, будут
   * гарантированно выполнены только один раз - даже при многопотоковом выполнении батча они будут выполнены однократно
   * после генерации и выполнения всех потоков.
   * @param batch ArrayList[String] устанавливаемое значение однократно запускаемого постфиксного батча.
  */
  public void setBatchRunOncePostfix(ArrayList<String> batch)
   {this.batchRunOncePostfix = new SimpleBatch(batch, this.isUseSqlFilter());}

  /**
   * Установка префикса sql-батча. Префикс выполняется перед основным телом sql-батча. При установке префикса батча
   * присходит его обработка (препроцессинг) - удаление пустых и select-запросов. Если после обработки префикс-батч
   * окажется пустым, присвоения значения не происходит и выводится соотв. запись в лог. Если префикс-батч данного
   * экземпляра конфига уже содержал какие-либо запросы (уже был инициализирован), то эти запросы будут затерты
   * устанавливаемым батчем. Запросы, содержащиеся в батче-префиксе, выполняются перед основным телом батча. При этом,
   * следует иметь в виду, что при многопотоковом выполнении всего батча префикс будет выполнен каждым потоком (полностью),
   * перед выполнением своей части тела sql-батча.
   * @param batch ArrayList[String] устанавливаемое значение префикса sql-батча.
  */
  public void setBatchPrefix(ArrayList<String> batch)
   {this.batchPrefix = new SimpleBatch(batch, this.isUseSqlFilter());}

  /**
   * Установка постфикса sql-батча. Постфикс выполняется после основного тела батча. При установке постфикса батча
   * присходит его обработка (препроцессинг) - удаление пустых и select-запросов. Если после обработки постфикс-батч
   * окажется пустым, присвоения значения не происходит и выводится соотв. запись в лог. Если постфикс-батч данного
   * экземпляра конфига уже содержал какие-либо запросы (уже был инициализирован), то эти запросы будут затерты
   * устанавливаемым батчем. Запросы, содержащиеся в батче-постфиксе, выполняются после основного тела батча. При этом,
   * следуеи иметь в виду, что при многопотоковом выполнении всего батча постфикс будет полностью выполнен каждым потоком,
   * после выполнения своей части тела sql-батча.
   * @param batch ArrayList[String] устанавливаемое значение постфикса sql-батча.
  */
  public void setBatchPostfix(ArrayList<String> batch)
   {this.batchPostfix = new SimpleBatch(batch, this.isUseSqlFilter());}

  /**
   * Метод добавляет один sql-запрос к телу батча данного конфига (добавдяется запрос в конец батча). Если тело батча еще
   * не инициализировано - происходит инициализация. Перед добавлением запрос проверяется на валидность - он не должен быть
   * пуст, не должен быть select-запросом. Также, в зависимости от настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в тело батча запрос.
  */
  public void addSqlToBatchBody(String sql)
   {
    if (this.batchBody == null) {this.batchBody = new SimpleBatch(sql, this.isUseSqlFilter());}
    else                        {this.batchBody.addSqlToBatch(sql, this.isUseSqlFilter());}
   }

  /**
   * Метод добавляет один sql-запрос к однократно заускаемому префиксному батчу данного конфига (добавдяется запрос в конец
   * батча). Если однократно запускаемый префиксный батч еще не инициализирован - происходит инициализация. Перед добавлением
   * запрос проверяется на валидность - он не должен быть пуст, не должен быть select-запросом. Также, в зависимости от
   * настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в однократно запускаемый префиксный батч запрос.
  */
  public void addSqlToBatchRunOncePrefix(String sql)
   {
    if (this.batchRunOncePrefix == null) {this.batchRunOncePrefix = new SimpleBatch(sql, this.isUseSqlFilter());}
    else                                 {this.batchRunOncePrefix.addSqlToBatch(sql, this.isUseSqlFilter());}
   }

  /**
   * Метод добавляет один sql-запрос к однократно заускаемому постфиксному батчу данного конфига (добавдяется запрос в конец
   * батча). Если однократно запускаемый постфиксный батч еще не инициализирован - происходит инициализация. Перед добавлением
   * запрос проверяется на валидность - он не должен быть пуст, не должен быть select-запросом. Также, в зависимости от
   * настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в однократно запускаемый постфиксный батч запрос.
  */
  public void addSqlToBatchRunOncePostfix(String sql)
   {
    if (this.batchRunOncePostfix == null) {this.batchRunOncePostfix = new SimpleBatch(sql, this.isUseSqlFilter());}
    else                                  {this.batchRunOncePostfix.addSqlToBatch(sql, this.isUseSqlFilter());}
   }

  /**
   * Метод добавляет один sql-запрос к префиксу батча данного конфига (добавдяется запрос в конец батча). Если префикс
   * батча еще не инициализирован - происходит инициализация. Перед добавлением запрос проверяется на валидность - он не
   * должен быть пуст, не должен быть select-запросом. Также, в зависимости от настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в префикс батча запрос.
  */
  public void addSqlToBatchPrefix(String sql)
   {
    if (this.batchPrefix == null) {this.batchPrefix = new SimpleBatch(sql, this.isUseSqlFilter());}
    else                          {this.batchPrefix.addSqlToBatch(sql, this.isUseSqlFilter());}
   }

  /**
   * Метод добавляет один sql-запрос к постфиксу батча данного конфига (добавдяется запрос в конец батча). Если постфикс
   * батча еще не инициализирован - происходит инициализация. Перед добавлением запрос проверяется на валидность - он не
   * должен быть пуст, не должен быть select-запросом. Также, в зависимости от настроек, выполняется фильтрация запроса.
   * @param sql String добавляемый в постфикс батча запрос.
  */
  public void addSqlToBatchPostfix(String sql)
   {
    if (this.batchPostfix == null) {this.batchPostfix = new SimpleBatch(sql, this.isUseSqlFilter());}
    else                           {this.batchPostfix.addSqlToBatch(sql, this.isUseSqlFilter());}
   }

  public boolean isStopOnError() {
   return stopOnError;
  }

  public void setStopOnError(boolean stopOnError) {
   this.stopOnError = stopOnError;
  }

  /**
   * Полный размер батча - сумма размеров всех частей батча.
   * @return int полный суммарный размер батча.
  */
  public int getBatchSize()
   {
    int result = 0;
    if ((batchRunOncePrefix  != null) && (!batchRunOncePrefix.isEmpty()))  {result += batchRunOncePrefix.getBatchSize();}
    if ((batchRunOncePostfix != null) && (!batchRunOncePostfix.isEmpty())) {result += batchRunOncePostfix.getBatchSize();}
    if ((batchPrefix         != null) && (!batchPrefix.isEmpty()))         {result += batchPrefix.getBatchSize();}
    if ((batchPostfix        != null) && (!batchPostfix.isEmpty()))        {result += batchPostfix.getBatchSize();}
    if ((batchBody           != null) && (!batchBody.isEmpty()))           {result += batchBody.getBatchSize();}
    return result;
   }

  /**
   * Полный размер однократно запускаемого (run-once) префиксного батча.
   * @return int полный размер префиксного run-once батча.
  */
  public int getBatchRunOncePrefixSize()
   {
    int result = 0;
    if ((batchRunOncePrefix != null) && (!batchRunOncePrefix.isEmpty())) {result += batchRunOncePrefix.getBatchSize();}
    return result;
   }

  /**
   * Полный размер однократно запускаемого (run-once) постфиксного батча.
   * @return int полный размер постфиксного run-once батча.
  */
  public int getBatchRunOncePostfixSize()
   {
    int result = 0;
    if ((batchRunOncePostfix != null) && (!batchRunOncePostfix.isEmpty())) {result += batchRunOncePostfix.getBatchSize();}
    return result;
   }

  /**
   * Полный размер батча-префикса.
   * @return int полный размер батча-префикса.
  */
  public int getBatchPrefixSize()
   {
    int result = 0;
    if ((batchPrefix != null) && (!batchPrefix.isEmpty())) {result = batchPrefix.getBatchSize();}
    return result;
   }

  /**
   * Полный размер батча-постфикса.
   * @return int полный размер батча-постфикса.
  */
  public int getBatchPostfixSize()
   {
    int result = 0;
    if ((batchPostfix != null) && (!batchPostfix.isEmpty())) {result = batchPostfix.getBatchSize();}
    return result;
   }

  /**
   * Полный размер тела батча.
   * @return int полный размер тела батча.
  */
  public int getBatchBodySize()
   {
    int result = 0;
    if ((batchBody != null) && (!batchBody.isEmpty())) {result = batchBody.getBatchSize();}
    return result;
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
    // Если уже есть ошибки конфига - сообщим о них
    if (!StringUtils.isBlank(errors))  {result = errors;}
    // Если пусты все части полного батча - это тоже ошибка
    else if (this.getBatchSize() <= 0) {result = "All parts of SQL batch is empty!";}
    return result;
   }

  /***/
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
            append("batchRunOncePrefix", batchRunOncePrefix).
            append("batchRunOncePostfix", batchRunOncePostfix).
            append("batchPrefix", batchPrefix).
            append("batchPostfix", batchPostfix).
            append("batchBody", batchBody).
            append("stopOnError", stopOnError).
            toString();
   }

  /**
   * Метод только для тестирования данного класса.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    BatchConfig config = new BatchConfig();
    logger.debug(config.toString());
   }

 }