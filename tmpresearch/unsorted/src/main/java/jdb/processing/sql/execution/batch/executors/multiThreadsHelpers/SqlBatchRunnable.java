package jdb.processing.sql.execution.batch.executors.multiThreadsHelpers;

import jdb.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.filter.sql.SqlFilter;
import jdb.utils.DBUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Данный класс реализует модуль обработки данных одного потока для многопотокового выполнения sql-батча. Модуль получает
 * большое количество параметров и ссылки на три части батча - префикс, тело, постфикс. Префикс и постфикс могут быть пустыми,
 * тело батча пустым быть не может - в противном случае в лог будет записана ошибка и никаких действий выполнено не будет.
 * Данный класс не рекомендуется к самостоятельному (отдельному) использованию, его следует считать частью большого модуля
 * многопотокового выполнения sql-батча - модуля
 * {@link jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor MultiThreadsSqlBatchExecutor}.
 *
 * @author Gusev Dmitry (Дмитрий)
 * @version 4.0 (DATE: 19.11.2010)
*/

// todo: цикл выполнения одного потока не обрабатывает параметр stopOnError, а всегда выполняется, даже при ошибках. ???

public class SqlBatchRunnable implements Runnable
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(getClass().getName());

  // Номер потока, в котором будет выполняться данный класс (его метод run())
  private int                   threadNumber        = 0;
  // Признак - является ли данный поток последним создаваемым в группе (если ИСТИНА, то этот поток обрабатывает
  // неделимый остаток sql-батча)
  private boolean               isLastThread        = false;
  // Размер части батча (количество запросов), выпаолняемой одним потоком
  private int                   partSize            = 0;
  // Неделимый остаток батча (количество запросов в остатке)
  private int                   remainder           = 0;
  // Конфигурация для соединения с СУБД
  private DBConfig              dbConfig            = null;

  // Ссылка на батч-префикс, который должен выполниться перед основным батчем
  private ArrayList<String>     prefixBatch         = null;
  // Ссылка на непосредственно выполняемый sql-батч. Предполагается, что данному классу передается уже обработанный
  // батч (на котором выполнен препроцессинг). Перед выполнением проверяется только пуст батч или нет. Если в батче будут
  // select-запросы, они выполнены не будут.
  private ArrayList<String>     sqlBatch            = null;
  // Ссылка на батч-постфикс, который должен выполниться после основного батча
  private ArrayList<String>     postfixBatch        = null;

  // Использовать ли фильтрацию sql-запросов перед выполнением. По умолчанию - включено.
  private boolean               useSqlFilter        = true;

  // Ссылка на экземпляр класса - список всех ошибок всех потоков, ссылка должна быть не пустой (не-NULL).
  private final ArrayList<String>     allErrors;
  // Ссылка на экземпляр класса - хранитель счетчика всех обработанных запросов во всех потоках (суммарно). Счетчик
  // обработанных запросов обновляется по мере выполнения запросов. Ссылка должна быть не пустой (не-NULL).
  private final TotalProcessedQueries totalQueries;


  /**
   * Конструктор. Инициализирует все необходимые поля класса.
   * @param threadNumber int номер потока. Должен быть положительным.
   * @param isLastThread boolean является ли данный поток последним создаваемым в списке (ИСТИНА/ЛОЖЬ).
   * @param partSize int размер части sql-батча для выполнения. Значение должно быть положительным.
   * @param remainder int остаток sql-батча для выполнения (если количество запросов в батче не делится нацело на
   * количество потоков). Остаток всегда выполняется в последнем создаваемом в группе потоке.
   * @param dbConfig DBConfig конфигурация для соединения с СУБД.
   * @param prefixBatch ArrayList[String] ссылка на префиксный sql-батч.
   * @param sqlBatch ArrayList[String] ссылка на непосредственно выполняемый sql-батч.
   * @param postfixBatch ArrayList[String] ссылка на постфиксный sql-батч.
   * @param allErrors ArrayList[String] ссылка на экземпляр класса, который хранит все сообщения об оишбках во всех
   * потоках.
   * @param totalQueries TotalProcessedQueries ссылка на экземпляр класса, который хранит количество выполненных запросов
   * во всех выполняемых потоках.
   * @param useSqlFilter boolean использовать или нет фильтрацию sql-запросов.
  */
  public SqlBatchRunnable(int threadNumber, boolean isLastThread, int partSize, int remainder, DBConfig dbConfig,
   ArrayList<String> prefixBatch, ArrayList<String> sqlBatch, ArrayList<String> postfixBatch, ArrayList<String> allErrors,
   TotalProcessedQueries totalQueries, boolean useSqlFilter)
   {
    this.threadNumber = threadNumber;
    this.isLastThread = isLastThread;
    this.partSize     = partSize;
    this.remainder    = remainder;
    this.dbConfig     = dbConfig;
    // Выполняемые батчи
    this.prefixBatch  = prefixBatch;
    this.sqlBatch     = sqlBatch;
    this.postfixBatch = postfixBatch;
    this.allErrors    = allErrors;
    this.totalQueries = totalQueries;
    this.useSqlFilter = useSqlFilter;
   }

  /**
   * Метод возвращает строку с описанием ошибок параметров данного экземпляра класса. Если ошибок нет - метод
   * возвращает значение NULL.
   * @return String описание ошибок в параметрах или значение NULL. 
  */
  private String getErrors()
   {
    // Возвращаемый результат работы данного метода
    String result = null;
    // Для начала необходимо проверить входные параметры данного класса, если параметры окажутся некорректными -
    // работа выполняться не будет. Также при ошибке необходимо сообщать в лог о ее типе/содержании.
    String dbConfigErrors = DBUtils.getConfigErrors(dbConfig);
    if (!StringUtils.isBlank(dbConfigErrors))
     {result = "Can't process work! DB configuration had errors [" + dbConfigErrors + "]!";}
    // Проверяем - не пуст ли основной батч. Препроцессинг не производится! Батчи префиксный и
    // постфиксный могут быть пустыми.
    else if ((sqlBatch == null) || (sqlBatch.isEmpty()))
     {result = "Can't process work! SQL batch is empty or NULL!";}
    // Проверяем - не пуста ли ссылка на экземпляр класса - "список ошибок всех потоков"
    else if (allErrors == null)
     {result = "Can't process work! Link to [all errors list] is NULL!";}
    // Проверяем - не пуста ли ссылка на экземпляр класса - "хранитель счетчика всех обработанных запросов"
    else if (totalQueries == null)
     {result = "Can't process work! Link to [total processed queries] is NULL";}
    // Номер потока должен быть положительным
    else if (threadNumber <= 0)
     {result = "Can't process work! Thread number is negative or 0!";}
    // Размер части батча для выполнения должен быть положительным
    else if (partSize <= 0)
     {result = "Can't process work! Batch part size is negative or 0!";}
    // Непосредственно возвращение результата
    return result;
   }

  @Override
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed", "ConstantConditions"})
  public void run()
   {
    // Список некритических ошибок, возникших в текущем потоке при выполнении. Инициализируем переменную сразу.
    ArrayList<String> currentThreadErrors = new ArrayList<String>();

    logger.debug("Thread #" + threadNumber + " starting. Checking parameters.");
    
    // Получаем описание ошибок конфигурационных параметров данного класса
    String classConfigErrors = this.getErrors();
    // Непосредственно выполнение работы потока, если описание ошибок параметров данного класса пусто
    if (StringUtils.isBlank(classConfigErrors))
     {
      logger.debug("All parameters OK. Processing work.");

      // Начальная и конечная позиции (номера) в списке sql-запросов для данного потока. Если при разделении
      // списка sql-запросов на потоки остался остаток (обычно он меньше количества потоков), то этот остаток
      // достается последнему потоку (всегда).
      int start  = partSize*(threadNumber - 1);
      int finish;
      // Если есть остаток и данный поток создается последним - пересчитываем конечную позицию
      if ((remainder > 0) && (isLastThread)) {finish = (partSize*threadNumber) - 1 + remainder;}
      // Если остатка нет или данный поток создается не последним - конечная позиция стандартна
      else                                   {finish = (partSize*threadNumber) - 1;}

      // После вычисления начальной и конечной позиции в списке sql-запросов для выполнения в данном потоке необходимо
      // проверить, что оба эти значения не выходят за рамки размера sql-батча. Также проверяем, что занчения start и
      // finish соответствуют друг другу: start < finish
      int sqlBatchSize = sqlBatch.size();
      if ((start < sqlBatchSize) && (finish < sqlBatchSize) && (start < finish))
       {
        // После окончательных проверок начинаем выполнять sql-батч
        logger.debug("START [" + start + "] and FINISH [" + finish + "] values are correct. Processing.");
        Connection connection = null;
        Statement statement   = null;
        try
         {
          // Соединяемся с СУБД
          connection = DBUtils.getDBConn(dbConfig);
          statement  = connection.createStatement();

          // Счетчик выполненных sql-запросов
          int sqlCounter = 0;
          // Последнее запомненное значение счетчика выполненных sql-запросов
          int lastValueSqlCounter = 0;

          // Выполняем полученный методом префикс-батч, если он не пуст.
          if ((this.prefixBatch != null) && (!this.prefixBatch.isEmpty())) 
           {
            for (String sql : this.prefixBatch)
             {
              // Конструкция try...catch применена для того, чтобы неудачное выполнение одного sql-запроса не
              // привело к завершению всего потока выполнения.
              try
               {
                // Если установлен параметр включения фильтрации - выполняем фильтрацию перед выполнением каждого запроса
                if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
                else              {statement.executeUpdate(sql);}
                // Увеличиваем счетчик обработанных запросов
                sqlCounter++;
                // Если достигнут необходимый для вывода сообщения (обновления общего счетчика выполненных запросов) шаг
                // итераций - вывод сообщений и добавление  количества обработанных запросов к общему счетчику.
                if (sqlCounter % DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
                 {
                  int step = sqlCounter - lastValueSqlCounter;
                  //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                  // Увеличиваем общий счетчик выполненных во всех потоках запросов. Общий экземпляр класса, хранящий
                  // счетчик, блокируется на время доступа из каждого потока (оператор synchronized)
                  synchronized (totalQueries) {totalQueries.addTotal(step);}
                  lastValueSqlCounter = sqlCounter;
                 }
               }
              // Перехватываем возможную ИС при выполнении запроса (чтобы не оборвать весь цикл). Сообщение о возникшей
              // ИС добавляем в список ошибок данного потока
              catch (SQLException e)
               {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sql + "]. Message: " + e.getMessage());}

              // Для того, чтобы данный поток не сожрал все ресурсы, необходим спец. метод, который позволит системе
              // прерывать данный поток. Это возможно на каждой итерации цикла. При этом данный метод (yield()) означает
              // только место возможного прерывания, но не прерывает поток. Прерывание (засыпание) потока выполняется
              // другим методом - sleep(ms).
              Thread.yield();
             }
           }

          // В цикле выполняем часть sql-запросов в данном потоке
          for (int count = start; count <= finish; count++)
           {
            // Конструкция try...catch применена для того, чтобы неудачное выполнение одного sql-запроса не
            // привело к завершению всего потока выполнения.
            try
             {
              // Если установлен параметр включения фильтрации - выполняем фильтрацию перед выполнением каждого запроса
              if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sqlBatch.get(count)));}
              else              {statement.executeUpdate(sqlBatch.get(count));}
              // Увеличиваем счетчик обработанных запросов
              sqlCounter++;
              // Если достигнут необходимый для вывода сообщения (обновления общего счетчика выполненных запросов) шаг
              // итераций - вывод сообщений и добавление  количества обработанных запросов к общему счетчику.
              if (sqlCounter %DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
               {
                int step = sqlCounter - lastValueSqlCounter;
                //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                // Увеличиваем общий счетчик выполненных во всех потоках запросов. Общий экземпляр класса, хранящий
                // счетчик, блокируется на время доступа из каждого потока (оператор synchronized)
                synchronized (totalQueries) {totalQueries.addTotal(step);}
                lastValueSqlCounter = sqlCounter;
               }
             }
            // Перехватываем возможную ИС при выполнении запроса (чтобы не оборвать весь цикл). Сообщение о возникшей
            // ИС добавляем в список ошибок данного потока
            catch (SQLException e)
             {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sqlBatch.get(count) + "]. Message: " + e.getMessage());}

            // Для того, чтобы данный поток не сожрал все ресурсы, необходим спец. метод, который позволит системе
            // прерывать данный поток. Это возможно на каждой итерации цикла. При этом данный метод (yield()) означает
            // только место возможного прерывания, но не прерывает поток. Прерывание (засыпание) потока выполняется
            // другим методом - sleep(ms).
            Thread.yield();
           }

          // Выполняем полученный методом постфикс-батч, если он не пуст.
          if ((this.postfixBatch != null) && (!this.postfixBatch.isEmpty()))
           {
            for (String sql : this.postfixBatch)
             {
              // Конструкция try...catch применена для того, чтобы неудачное выполнение одного sql-запроса не
              // привело к завершению всего потока выполнения.
              try
               {
                // Если установлен параметр включения фильтрации - выполняем фильтрацию перед выполнением каждого запроса
                if (useSqlFilter) {statement.executeUpdate(SqlFilter.removeDeprecated(sql));}
                else              {statement.executeUpdate(sql);}
                // Увеличиваем счетчик обработанных запросов
                sqlCounter++;
                // Если достигнут необходимый для вывода сообщения (обновления общего счетчика выполненных запросов) шаг
                // итераций - вывод сообщений и добавление  количества обработанных запросов к общему счетчику.
                if (sqlCounter % DBConsts.THREAD_COUNTER_UPDATE_STEP == 0)
                 {
                  int step = sqlCounter - lastValueSqlCounter;
                  //logger.debug("Thread #" + threadNumber + ". Processed: " + sqlCounter + "/" + (finish-start));
                  // Увеличиваем общий счетчик выполненных во всех потоках запросов. Общий экземпляр класса, хранящий
                  // счетчик, блокируется на время доступа из каждого потока (оператор synchronized)
                  synchronized (totalQueries) {totalQueries.addTotal(step);}
                  lastValueSqlCounter = sqlCounter;
                 }
               }
              // Перехватываем возможную ИС при выполнении запроса (чтобы не оборвать весь цикл). Сообщение о возникшей
              // ИС добавляем в список ошибок данного потока
              catch (SQLException e)
               {currentThreadErrors.add("Thread #" + threadNumber + ". SQL [" + sql + "]. Message: " + e.getMessage());}

              // Для того, чтобы данный поток не сожрал все ресурсы, необходим спец. метод, который позволит системе
              // прерывать данный поток. Это возможно на каждой итерации цикла. При этом данный метод (yield()) означает
              // только место возможного прерывания, но не прерывает поток. Прерывание (засыпание) потока выполняется
              // другим методом - sleep(ms).
              Thread.yield();
             }
           }

          // После завершения выполнения цикла добавим к общему счетчику оставшиеся итерации
          int step = sqlCounter - lastValueSqlCounter;
          synchronized (totalQueries) {totalQueries.addTotal(step);}
         }

        // Перехватываем ИС, которые могут возникнуть при выполнении всего кода метода run(). Сообщения о возникших
        // ИС добавляем в список ошибок данного потока
        catch (DBConnectionException e)   {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
        catch (DBModuleConfigException e) {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
        catch (SQLException e)            {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}

        // Обязательно закрываем за собой соединение с СУБД (пытаемся). Если возникают ошибки - заносим их в список.
        finally
         {
          try                    {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}
          catch (SQLException e) {currentThreadErrors.add("Thread #" + threadNumber + ". Message: " + e.getMessage());}
          
          // Если в потоке возникли ошибки и они были занесены в список ошибок потока - добавим их в общий список ошибок
          if ((currentThreadErrors != null) && (!currentThreadErrors.isEmpty()))
           {
            logger.debug("THREAD #" + threadNumber + " HAD ERRORS. SEE LOG.");
            // Добавляем все ИС текущего потока в общий список ИС. При этом блокируем объект с общим списком ИС.
            synchronized (allErrors) {allErrors.addAll(currentThreadErrors);}
           }
          // Если же в потоке не было ошибок - просто сообщим об этом в лог
          else {logger.debug("THREAD #" + threadNumber + " HAD NO ERRORS. ALL OK.");}

          // Отладочное сообщение об окончании работы потока
          logger.debug("THREAD " + threadNumber + " FINISHED!");
         }
       }
      // Если в значениях есть ошибки - работа не выполняется, выводится в лог сообщение о несоответствии параметров
      else {logger.error("START[" + start + "] and/or FINISH[" + finish + "] value(s) incorrect [BATCH SIZE = " + sqlBatchSize + "]!");}
     }
    // Выводим в лог описание ошибок параметров данного класса.
    else {logger.error("Thread config had error [" +  classConfigErrors + "]!");}
   }

 }