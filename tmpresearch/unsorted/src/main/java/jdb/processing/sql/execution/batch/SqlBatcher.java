package jdb.processing.sql.execution.batch;

import dgusev.dbpilot.DBConsts;
import dgusev.dbpilot.config.DBConfig;
import jdb.config.batch.BatchConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.processing.sql.execution.batch.executors.MultiThreadsSqlBatchExecutor;
import jdb.processing.sql.execution.batch.executors.SingleThreadSqlBatchExecutor;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Класс-фасад для выполнения sql-батчей. В зависимости от параметров батчи выполняются в однопотоковом или
 * в многопотоковом режимах.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 13.05.2010)
*/

public class SqlBatcher
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(SqlBatcher.class.getName());

  
  /**
   * Метод выполняет sql-батч в однопоточном или многопоточном режимах. Выбор режима зависит от параметров,
   * указанных в классе-конфигурации модуля.
   * @param config BatchConfig класс-конфигурация модуля для выполнения sql-батча.
   * @return ArrayList[String] список возникших некритических ошибок, возникших при выполнении sql-батча.
   * @throws DBModuleConfigException ИС - ошибки конфигурирования модуля выполнения sql-батчей.
   * @throws DBConnectionException ИС - ошибки соединения с СУБД.
   * @throws SQLException ИС - критические ошибки при выполнении sql-запросов.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public static ArrayList<String> execute(BatchConfig config) throws DBModuleConfigException, DBConnectionException, SQLException
   {
    // Результат - список ошибок, возникших при выполнении sql-батча
    ArrayList<String> result;

    // Проверяем конфигурацию на ошибки, если они есть - ничего не делаем!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else                                    {logger.debug("Batch configuration is OK. Processing.");}
    // Проверяем соединение с СУБД перед выполнением вычислений и запуском цикла создания потоков
    Connection connection = null;
    Statement statement  = null;
    try
     {
      // Соединяемся с указанной СУБД. Если соединение с СУБД установить не удалось - ничего больше не
      // выполняется (так как возбуждается ИС).
      connection = DBUtils.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. All OK.");
     }
    // Обязательно освобождаем ресурсы (пытаемся)
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // После выполнения всех проверок (и успешного их прохождения) непосредственно выполняем sql-батч. Выполнение батча
    // в многопотоковом/однопотоковом режимах производится в зависимости от параметров конфига. Для многопоточной обработки
    // батча необходимо, чтобы параметр isMultiThreads=true и количество соединений с СУБД (одновременных) было больше
    // минимального (=2, см. константы в классе DBConsts). Также необходимо, чтобы количество запросов в батче коррелировалось
    // с количеством соединений с СУБД. Т.е. на одно соединение с СУБД должно приходиться более одного sql-запроса, для
    // этого введена константа "соотношение" - DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL, которая указывает то минимальное число
    // sql-запросов для одного соединения с СУБД, которое позволяет обрабатывать sql-батч в многопотоковом режиме.
    // todo: возможно, следует просто уменьшить количество соединений для обеспечения верности соотношения? при том, что
    // todo: получаемое количество соединений будет больше минимального?

    // Рассчитываем количество соединений с СУБД
    int realConnNumber;
    int connNumber = config.getDbmsConnNumber();
    if ((connNumber  >= DBConsts.MIN_DBMS_CONNECTIONS) && (connNumber <= DBConsts.MAX_DBMS_CONNECTIONS))
     {realConnNumber = connNumber;}
    else if (connNumber > DBConsts.MAX_DBMS_CONNECTIONS)
     {realConnNumber = DBConsts.MAX_DBMS_CONNECTIONS;}
    else
     {realConnNumber = DBConsts.MIN_DBMS_CONNECTIONS;}
    // По результатам расчета параметров выбираем способ запуска батча - однопоточный или многопоточный.
    if (config.isMultiThreads() && (config.getBatchSize() >= realConnNumber*DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL))
     {
      logger.debug("Sql batch multi threads processing.");
      result = MultiThreadsSqlBatchExecutor.execute(config);
     }
    else
     {
      logger.debug("Sql batch single thread processing.");
      result = SingleThreadSqlBatchExecutor.execute(config);
     }

    // Возвращаем результат выполнения батча (список возникших НЕКРИТИЧЕСКИХ ошибок)
    return result;
   }

  /**
   * Метод только для тестирования данного класса.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");

    // Соединение с БД
    DBConfig dbConfig = new DBConfig();
    dbConfig.setDbType(DBConsts.DBType.MYSQL);
    dbConfig.setHost("localhost:3306");
    dbConfig.setDbName("test");
    dbConfig.setUser("root");
    dbConfig.setPassword("mysql");

    // Создание sql-батча
    ArrayList<String> sqlBatch = new ArrayList<String>();
    for (int i = 0; i < 200000; i++)
     {
      sqlBatch.add("insert into test(string1, number1, string2, number2) " +
                   "values('string" + i + "', " + i + ",'string" + i*10 + "', " + i*10 + ")");
     }

    // Конфигурация для выполнения батча
    BatchConfig batchConfig = new BatchConfig();
    batchConfig.setDbConfig(dbConfig);
    //batchConfig.setBatch(sqlBatch);
    batchConfig.setOperationsCount(200);
    batchConfig.setMultiThreads(true);
    batchConfig.setDbmsConnNumber(20);

    logger.debug("--- Config created. Starting batch. ---");
    
    try
     {
      // Предварительная очистка таблицы назначения
      //DataChanger.cleanupTable(dbConfig, "test");
      // Непосредственно запуск батча
      SqlBatcher.execute(batchConfig);
     }
    catch (DBConnectionException e)   {logger.error("->" + e.getMessage()); e.printStackTrace();}
    catch (DBModuleConfigException e) {logger.error("->" + e.getMessage()); e.printStackTrace();}
    catch (SQLException e)            {logger.error("->" + e.getMessage()); e.printStackTrace();}

   }

 }