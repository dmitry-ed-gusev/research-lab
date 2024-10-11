package jdb.processing.sql.execution.batch.executors;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.utils.DBUtilities;
import jdb.DBResources;
import jdb.config.batch.BatchConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.monitoring.DBProcessingMonitor;
import jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.SqlBatchRunnable;
import jdb.processing.sql.execution.batch.executors.multiThreadsHelpers.TotalProcessedQueries;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Класс содержит статический метод для многопотокового выполнения sql-батчей. Запросы выполняются во многих потоках
 * (метод данного класса многопоточный). В наборах запросов выполняются только НЕ-SELECT запросы (запросы типа "SELECT..."
 * отфильтровываются).Данный класс можно использовать как отдельный модуль (независимый), так и через модуль более
 * высокого уровня - {@link jdb.processing.sql.execution.batch.SqlBatcher SqlBatcher} (модуль-фасад для модулей
 * однопотокового/многопотокового выполнения sql-батчей). 
 * @author Gusev Dmitry (019gus)
 * @version 6.0 (DATE: 19.11.2010)
*/

// todo: многопотоковое выполнение не обрабатывает параметр stopOnError, а всегда выполняется, даже при ошибках. ???

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class MultiThreadsSqlBatchExecutor
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(MultiThreadsSqlBatchExecutor.class.getName());

  /** Наименование группы потоков, в которой находятся все потоки выполнения sql-батча */
  private static final String THREADS_GROUP_NAME     = "sqlBatchThreads";

  /**
   * Данный метод выполняет для соединения с СУБД, указанного в конфиге config, набор SQL-запросов - батч (batch). Запросы
   * в списке не должны быть "SELECT..."-запросами.
   * @param config BatchConfig
   * @return ArrayList[String] типизированный (String) список ошибок, возникших при выполнении списка запросов.
   * @throws java.sql.SQLException критическая ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки непосредственно соединения с СУБД.
  */
  @SuppressWarnings({"MethodWithMultipleReturnPoints"})
  public static ArrayList<String> execute(BatchConfig config) throws SQLException, DBModuleConfigException, DBConnectionException, IllegalAccessException, InstantiationException, ClassNotFoundException {
    logger.debug("execute: executing!");
    // Результат выполнения батча - список возникших ошибок. Переменная объявлена как final - т.к. к
    // данной переменной осуществляется доступ из всех создаваемых потоков.
    final ArrayList<String> result;
    // Объектная переменная для хранения и доступа из потоков к счетчику обработанных запросов. Инициализируем
    // переменную сразу. Объявление final - к данной переменной осуществляется доступ из всех создаваемых потоков.
    final TotalProcessedQueries total  = new TotalProcessedQueries();

    // Проверяем конфигурацию на ошибки, если они есть - ничего не делаем (возбуждаем ИС)!
    //String configErrors = DBUtils.getConfigErrors(config);
    //if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    //else                                    {logger.debug("Batch configuration is OK. Testing connection to DBMS.");}

    // Проверяем соединение с СУБД перед выполнением вычислений и запуском цикла создания потоков
    Connection connection = null;
    Statement  statement  = null;
    try
     {
      // Соединяемся с указанной СУБД. Если соединение с СУБД установить не удалось - ничего больше не
      // выполняется (так как возбуждается ИС).
      connection = DBUtilities.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. All OK.");
     }
    // Обязательно освобождаем ресурсы (пытаемся)
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // В зависимости от наличия тела батча выполнение батча будет происходить по-разному. Если тела батча пусто - никакой
    // многопоточности не будет - все префиксы-постфиксы будут выполнены в одном потоке. Сейчас мы это и проверим.
    if ((config.getBatchBody() == null) || (config.getBatchBody().isEmpty()))
     {
      logger.debug("No batch body. Processing prefixes and postfixes.");
      // Т.к. нет тела батча, которое выполняется многопоточно, то оставшиеся части батча будут выполнены
      // в одном потоке. Для этого будет использован метод SingleThreadSqlBatchExecutor.
      result = SingleThreadSqlBatchExecutor.execute(config);
     }
    // Если же в теле батча есть хоть один запрос (тело не пусто), то заморачиваемся с потоками
    else
     {
      logger.debug("Batch body is not empty. Processing multi-threads.");
      // Сначала инициализируем переменную результата
      result = new ArrayList<String>();

      // После всех проверок (закончившихся успешно) формируем из частей батча необходимые нам для исполнения батчи.
      // Для начала формируем однократно выполняемый префиксный батч.
      ArrayList<String> batchPrefix = null;
      if ((config.getBatchRunOncePrefix() != null) & (!config.getBatchRunOncePrefix().isEmpty()))
       {
        batchPrefix = new ArrayList<String>();
        // Добавляем обычный префикс-батч
        if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
         {batchPrefix.addAll(config.getBatchPrefix().getBatch());}
        // Добавляем однократно запускаемый префикс-батч
        batchPrefix.addAll(config.getBatchRunOncePrefix().getBatch());
        // Добавляем обычный постфикс-батч
        if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
         {batchPrefix.addAll(config.getBatchPostfix().getBatch());}
       }
      // Если нету однократно запускаемого батча-префикса - сообщим в лог
      else {logger.debug("Run-once prefix batch is empty!");}
      // Теперь формируем однократно выполняемый постфиксный батч
      ArrayList<String> batchPostfix = null;
      if ((config.getBatchRunOncePostfix() != null) & (!config.getBatchRunOncePostfix().isEmpty()))
       {
        batchPostfix = new ArrayList<String>();
        // Добавляем обычный префикс-батч
        if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
         {batchPostfix.addAll(config.getBatchPrefix().getBatch());}
        // Добавляем однократно запускаемый постфикс-батч
        batchPostfix.addAll(config.getBatchRunOncePostfix().getBatch());
        // Добавляем обычный постфикс-батч
        if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
         {batchPostfix.addAll(config.getBatchPostfix().getBatch());}
       }
      // Если нету однократно запускаемого батча-постфикса - сообщим в лог
      else {logger.debug("Run-once postfix batch is empty!");}

      // --------------------------------------------------------------------------------------------------------------
      // В данной секции мы рассчитываем количество потоков, размер части тела батча для одного потока, остаток батча
      // - параметры, которые необходимы для правильного формирования пула потоков.
      //
      // Количество потоков (может задаваться произвольно :), но в разумных пределах). Если указаный параметр "количество соединений"
      // положителен - берем его значение, если же он отрицателен, то берем значение по умолчанию. Количество потоков в дальнейшем
      // может быть (и будет) скорректировано в зависимости от соотношения размера батча и самого количества потоков. Также количество
      // потоков (и соотв. соединений с СУБД) не должно превышать значение DBConsts.MAX_DBMS_CONNECTIONS.
      int threadsCount;
      int connNumber = config.getDbmsConnNumber();
      if ((connNumber  >= DBConsts.MIN_DBMS_CONNECTIONS) && (connNumber <= DBConsts.MAX_DBMS_CONNECTIONS))
       {threadsCount = connNumber;}
      else if (connNumber > DBConsts.MAX_DBMS_CONNECTIONS)
       {threadsCount = DBConsts.MAX_DBMS_CONNECTIONS;}
      else
       {threadsCount = DBConsts.MIN_DBMS_CONNECTIONS;}
      // Рассчитанный размер части тела батча, выполняемой в одном потоке
      final int partSize;
      // Остаток батча, выполняемый в последнем потоке
      final int remainder;
      // Теперь мы разбиваем список запросов на части, в зависимости от количества потоков. Если размер списка запросов
      // меньше количества потоков, то параметры будут пересчитаны по-другому (также будет изменено количество потоков).
      if (config.getBatchSize() >= threadsCount*DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL)
       {
        logger.debug("Batch size > threadsCount*RATIO [" + threadsCount + "*" + DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL + "]. Processing.");
        partSize  = config.getBatchSize()/threadsCount;
        remainder = config.getBatchSize()%threadsCount; // <- получаем остаток от целочисленного деления
       }
      // Пересчет параметров для случая, когда размер списка запросов меньше количества потоков. Для данного случая
      // изменяется и количество потоков - поток будет всего один!
      else
       {
        logger.debug("Batch size < threadsCount. Processing.");
        partSize     = config.getBatchSize();
        remainder    = 0;
        threadsCount = 1;
       }
      // После выполнения всех расчетов - выведем отладочное сообщение о параметрах метода
      logger.debug("PARAMETERS: [partSize=" + partSize + "], [remainder=" + remainder + "], [threadsCount=" + threadsCount + "]");
      // --------------------------------------------------------------------------------------------------------------

      // Рассчитываем общее количество запросов, которое необходимо будет выполнить данному методу (включая то, что
      // для потоков префикс и постфикс будут повторены по нескольку и более раз).
      int sqlCount = 0;
      // Размер префикса
      if ((batchPrefix != null)  && (!batchPrefix.isEmpty()))  {sqlCount += batchPrefix.size();}
      // Размер постфикса
      if ((batchPostfix != null) && (!batchPostfix.isEmpty())) {sqlCount += batchPostfix.size();}
      // Размер тела
      sqlCount += config.getBatchBodySize();
      // Размер повторяемого для каждого потока префикса
      if ((config.getBatchPrefix() != null) && (!config.getBatchPrefix().isEmpty()))
       {sqlCount += config.getBatchPrefixSize()*threadsCount;}
      // Размер повторяемого для каждого потока постфикса
      if ((config.getBatchPostfix() != null) && (!config.getBatchPostfix().isEmpty()))
       {sqlCount += config.getBatchPostfixSize()*threadsCount;}

      logger.debug("Starting batch. Size [" + sqlCount + "]. SQL filtering mode [" + config.isUseSqlFilter() + "]");
      // Префиксное сообщение для сообщения монитора. Вычисляем один раз.
      String monitorPrefix = "";
      // Получаем ссылку на класс-монитор
      DBProcessingMonitor monitor = config.getMonitor();
      // Если есть монитор - обрабатываем префикс. Также выдаем сообщение монитору об общем ходе выполнения батча (начало)
      if (monitor != null)
       {
        if (!StringUtils.isBlank(config.getMonitorMsgPrefix())) {monitorPrefix = config.getMonitorMsgPrefix();}
        monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, 0, sqlCount));
       }

      // Теперь займемся непосредственно потоками.
      // Группа потоков для группирования всех потоков, созданных для выполнения запросов
      ThreadGroup threadGroup = new ThreadGroup(THREADS_GROUP_NAME);
      // В цикле создаем необходимое количество потоков, которые и выполняют sql-батч
      logger.debug("Starting threads generation cycle.");
      for (int i = 1; i <= threadsCount; i++)
       {
        // Признак - является ли данный поток последним, создаваемым в группе. Применение переменной вызвано
        // соображениями наглядности/удобочитаемости кода
        boolean isLastThread = (i == threadsCount);
        // Создаем экземпляр класса, реализующий метод run() одного потока
        SqlBatchRunnable sqlBatchRunnable = new SqlBatchRunnable(i, isLastThread, partSize, remainder, config.getDbConfig(),
         /*префикс*/config.getBatchPrefix().getBatch(), /*тело*/config.getBatchBody().getBatch(),
         /*постфикс*/config.getBatchPostfix().getBatch(), result, total, config.isUseSqlFilter());
        // В созданной группе потоков создаем еще один поток
        Thread sqlBatchThread = new Thread(threadGroup, sqlBatchRunnable);
        // Запускаем созданный поток
        sqlBatchThread.start();
       }

      // Ждем остановки всех потоков в группе (выведем об этом отладочное сообщение)
      logger.debug("WAITING FOR ALL THREADS STOP IN GROUP [" + THREADS_GROUP_NAME + "]...");
      // Счетчик итераций цикла ожидания окончания всех потоков
      int counter = 0;
      // Переменная для запоминания последнего значения счетчика обработанных запросов
      int lastCounter = -1;
      // Вычисляем шаг (количество обработанных запросов), через который будет выдаваться сообщение монитору.
      int monitorMessageStep;
      if ((config.getOperationsCount() >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
          (config.getOperationsCount() <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
       {monitorMessageStep = config.getOperationsCount();}
      // Если установленное значение количества операций выходит за указанные границы - принимаем max значение
      else {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}
      // Цикл ожидания завершения всех потоков и вывода информации о прогрессе выполнения
      do
       {
        if (counter%DBConsts.WAIT_CYCLE_STEPS_COUNT == 0)
         {
          int totalProcessed = total.getTotal();
          if ((lastCounter != totalProcessed) && ((totalProcessed - lastCounter) > monitorMessageStep))
           {
            logger.debug("TOTAL processed : [" + totalProcessed + "/" + config.getBatchSize() + "]");
            // Если есть монитор данного процесса - выдадим ему сообщение
            if (monitor != null)
             {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, totalProcessed, sqlCount));}
            lastCounter = totalProcessed;
           }
         }
        counter++;
       }
      // Цикл выполняется, пока не завершатся все потоки.
      // todo: при "зависании" одного из потоков возможно возникновения "вечного" цикла
      while(threadGroup.activeCount() > 0);

      // Вот в этом месте все потоки завершены. Выводим отладочное сообщение.
      logger.debug("ALL THREADS IN GROUP [" + THREADS_GROUP_NAME + "] FINISHED SUCCESSFULLY.");
      // После окончания выполнения всех потоков еще раз соберем инфу с монитора. Если добавились еще записи -
      // выведем сообщение монитора (последнее).
      int totalProcessed = total.getTotal();
      if (lastCounter != totalProcessed)
       {
        logger.debug("TOTAL processed : [" + totalProcessed + "/" + config.getBatchSize() + "]");
        // Если есть монитор данного процесса - выдадим ему сообщение
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, totalProcessed, sqlCount));}
       }
     }
    
    // Если в результирующий список ошибок были добавлены ошибки - ок, если же ошибок не было добавлено - метод
    // должен вернуть значение null.
    if (!result.isEmpty()) {logger.warn("There are errors in threads. See log."); return result;}
    else                   {logger.debug("There are no errors in threads. All OK."); return null;}
    
   }

 }