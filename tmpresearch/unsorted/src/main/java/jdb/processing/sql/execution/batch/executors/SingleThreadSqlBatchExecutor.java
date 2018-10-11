package jdb.processing.sql.execution.batch.executors;

import jdb.DBConsts;
import jdb.DBResources;
import jdb.config.batch.BatchConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.monitoring.DBProcessingMonitor;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Класс содержит метод для выполнения наборов sql-запросов (sql-batch). Запросы выполняются в одном потоке (метод
 * данного класса однопоточный). В наборах запросов выполняются только НЕ-SELECT запросы (запросы типа "SELECT..."
 * отфильтровываются). Данный класс можно использовать как отдельный модуль (независимый), так и через модуль более
 * высокого уровня - SqlBatcher (модуль-фасад для модулей однопотокового/многопотокового выполнения sql-батчей).
 *<br>
 * 16.11.2010 Был изменен класс конфигурирования - {@link jdb.config.batch.BatchConfig BatchConfig}. Теперь
 * результирующий выполняемый батч собирается из нескольких частей. Подробное описание алгоритма см. в
 * классе {@link jdb.config.batch.BatchConfig BatchConfig}.
 * 
 * @author Gusev Dmitry (019gus)
 * @version 7.0 (DATE: 18.11.2010)
*/

@SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
public class SingleThreadSqlBatchExecutor
 {
  /** Логгер данного класса. */
  private static Logger logger = Logger.getLogger(SingleThreadSqlBatchExecutor.class.getName());

  /**
   * Данный метод выполняет для соединения с СУБД, указанного в конфиге config, набор SQL-запросов - батч (batch). Запросы
   * в списке не должны быть "SELECT..."-запросами. Параметр stopOnError указывает - останавливать ли выполнение набора
   * запросов при возникновении ошибки или нет.
   * @param config BatchConfig конфигурация для модуля выполнения батча.
   * @return ArrayList [String] список ошибок, возникших при выполнении списка запросов.
   * @throws java.sql.SQLException критическая ошибка при создании объекта Statement.
   * @throws jdb.exceptions.DBModuleConfigException ошибки конфигурации соединения с СУБД.
   * @throws jdb.exceptions.DBConnectionException ошибки непосредственно соединения с СУБД.
  */
  public static ArrayList<String> execute(BatchConfig config) throws SQLException, DBConnectionException, DBModuleConfigException
   {
    logger.debug("executeSqlBatch: executing!");
    // Результат выполнения батча - список возникших ошибок
    ArrayList<String> result = null;

    // Проверяем конфигурацию на ошибки, если они есть - ничего не делаем!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else                                    {logger.debug("Batch configuration is OK. Processing.");}
    
    logger.debug("Processing batch. Size [" + config.getBatchSize() + "]");
    // Префиксное сообщение для сообщения монитора. Вычисляем один раз.
    String monitorPrefix = StringUtils.trimToEmpty(config.getMonitorMsgPrefix());

    // Проверяем соединение с СУБД перед выполнением вычислений и запуском цикла создания потоков
    Connection connection = null;
    Statement statement  = null;
    try
     {
      // Соединяемся с указанной СУБД. Если соединение с СУБД установить не удалось - ничего больше не
      // выполняется (так как возбуждается ИС).
      connection = DBUtils.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. Statement object created.");
      // Получаем ссылку на экземпляр класса-монитора
      DBProcessingMonitor monitor = config.getMonitor();
      
      // Флаг продолжения итераций. Если указан параметр метода stopOnError=true (останов при возникновении ошибки), то
      // при возникновении ИС данный флаг будет сброшен в значение false и выполнение батча прекратится.
      boolean continueFlag = true;

      // Из батчей-частей полученного конфига составляем полный батч для выполнения его в одном (текущем) потоке
      ArrayList<String> fullBatch = null;
      // Добавляем батч-префикс
      if ((config.getBatchPrefix() != null) && !config.getBatchPrefix().isEmpty())
       {
        fullBatch = new ArrayList<String>();
        fullBatch.addAll(config.getBatchPrefix().getBatch());
       }
      // Добавляем однократно запускаемый префиксный батч
      if ((config.getBatchRunOncePrefix() != null) && !config.getBatchRunOncePrefix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchRunOncePrefix().getBatch());
       }
      // Добавляем тело батча
      if ((config.getBatchBody() != null) && !config.getBatchBody().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchBody().getBatch());
       }
      // Добавляем однократно запускаемый постфиксный батч
      if ((config.getBatchRunOncePostfix() != null) && !config.getBatchRunOncePostfix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchRunOncePostfix().getBatch());
       }
      // Добавляем батч-постфикс
      if ((config.getBatchPostfix() != null) && !config.getBatchPostfix().isEmpty())
       {
        if (fullBatch == null) {fullBatch = new ArrayList<String>();}
        fullBatch.addAll(config.getBatchPostfix().getBatch());
       }

      // ***
      // Дополнительная проверка - не пуст ли полученный результирующий батч. В принципе, в данной проверке нет необходимости,
      // т.к. она выполняется методом .getConfigErrors() - если полученный методом в качестве параметра батч пуст, он не пройдет
      // проверку. НО! При возможном изменении указанного метода (.getConfigErrors()) данный метод будет все равно надежен, т.к.
      // внутренние проверки данного метода не допустят возникновения ИС. Поэтому - данная проверка тут присутствует.
      if ((fullBatch != null) && (!fullBatch.isEmpty()))
       {
        logger.debug("Batch created OK. Processing.");
        // Еще одна небольшая проверочка. Размер полученного нами батча и значение, возвращаемое соотв. методом класса
        // конфигурации (BatchConfig) должны совпадать. Если нет - где-то ошибка!
        if (fullBatch.size() != config.getBatchSize()) {logger.warn("Batches sizes mismatch! Maybe some errors? :)");}
        else                                           {logger.info("Batches sizes match. Processing.");}

        // Сообщение монитору об общем ходе выполнения батча (начало выполнения батча)
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, 0, fullBatch.size()));}

        // В цикле проходим по полному батчу (по всем запросам в наборе(пакете)) и выполняем их.
        // Выполняются ТОЛЬКО не-SELECT запросы.
        Iterator<String> iterator = fullBatch.iterator();
        logger.debug("Starting batch processing. SQL filtering mode [" + config.isUseSqlFilter() + "]");
        // Счетчик выполнения запросов
        int counter = 0;
        // Шаг (количество обработанных запросов), через который будет выдано сообщение монитору.
        int monitorMessageStep;
        if ((config.getOperationsCount() >= DBConsts.MIN_MONITOR_OPERATIONS_COUNT) &&
            (config.getOperationsCount() <= DBConsts.MAX_MONITOR_OPERATIONS_COUNT))
         {monitorMessageStep = config.getOperationsCount();}
        else
         {monitorMessageStep = DBConsts.MAX_MONITOR_OPERATIONS_COUNT;}

        // Переменная для хранения выполняемого в данный момент запроса
        String sql;
        while (iterator.hasNext() && continueFlag)
         {
          sql = iterator.next();
          // Нижеследующая строчка кода только для глубокой отладки
          //logger.debug("Trying to execute sql-query: [" + sql +"].");
          // Выполняем каждый запрос в блоке try...catch, чтобы можно были управлять процессом при возникновении ИС
          try
           {
            // Если используется фильтрация sql-запросов - выполняем ее!
            int executeResult;
            if (config.isUseSqlFilter()) {executeResult = statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
            else                         {executeResult = statement.executeUpdate(sql);}
            // Результат выполнения запроса используем для предупреждений (если результат отличен от нормального (1))
            if (executeResult != 1) {logger.warn("Sql query execute result <> 1 [" + executeResult + "]!");}
            // Увеличение счетчика обработанных запросов. Счетчик увеличивается только после удачного выполнения
            // запроса, т.е. если какие-то запросы выполнены не будут, то конечный результат нам это покажет.
            counter++;
            // Если мы достигли количества для отображения - выведем отладочное сообщение. Также, если есть монитор,
            // выведем сообщение для монитора (количество обработанных/общее количество)
            if (counter%monitorMessageStep == 0)
             {
              logger.debug("Processed: [" + counter + "/" + fullBatch.size() + "]");
              // Если есть монитор - сообщаем ему о ходе выполнения батча
              if (monitor != null)
               {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, counter, fullBatch.size()));}
             }
            // Нижеследующая строчка кода только для глубокой отладки
            //logger.debug("Query executed. Result: [" + executeResult + "]");
           }
          // Обработка ИС при выполнении запроса
          catch (SQLException e)
           {
            // Сообщаем в лог об ошибке
            logger.error(e.getMessage() + " SQL: [" + sql + "]");
            // Добавляем сведение об ошибке в результат
            if (result == null) {result = new ArrayList<String>();}
            result.add("Error message: " + e.getMessage() + ", ");
            // Если параметр метода stopOnError=true, то сбрасываем флаг продолжения итераций
            if (config.isStopOnError()) {continueFlag = false;}
           }
         } // END OF WHILE CYCLE
        
        logger.debug("TOTAL processed: [" + counter + "/" + fullBatch.size() + "]");
        // Если есть монитор - сообщаем ему
        if (monitor != null)
         {monitor.processMessage(String.format(DBResources.MSG_SQL_PROCESSING, monitorPrefix, counter, fullBatch.size()));}
        logger.debug("Batch processing complete.");
       }
      // Если после обработки батчей-частей полученного методом конфига получился пустой батч - это ошибка! Выполнять
      // ничего не надо. Просто сообщае в лог. Подробнее о такой провекре - см. коммент выше, помеченный ***.
      else {logger.error("Batch was created with error: result batch is empty (or null)!");}
     }
    // Обязательно освобождаем ресурсы. ИС перехватываем только в цикле выполнения батча, чтобы можно было управлять
    // процессом выполнения батча. Вне цикла ИС не перехватываются - происходит облом метода и возникновение указанных
    // в сигнатуре метода ИС.
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // Возвращение результата
    return result;
   }

 }