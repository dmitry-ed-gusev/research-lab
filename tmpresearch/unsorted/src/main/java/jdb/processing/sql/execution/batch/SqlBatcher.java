package jdb.processing.sql.execution.batch;

import jdb.DBConsts;
import jdb.config.DBConfig;
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
 * �����-����� ��� ���������� sql-������. � ����������� �� ���������� ����� ����������� � ������������� ���
 * � �������������� �������.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 13.05.2010)
*/

public class SqlBatcher
 {
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(SqlBatcher.class.getName());

  
  /**
   * ����� ��������� sql-���� � ������������ ��� ������������� �������. ����� ������ ������� �� ����������,
   * ��������� � ������-������������ ������.
   * @param config BatchConfig �����-������������ ������ ��� ���������� sql-�����.
   * @return ArrayList[String] ������ ��������� ������������� ������, ��������� ��� ���������� sql-�����.
   * @throws DBModuleConfigException �� - ������ ���������������� ������ ���������� sql-������.
   * @throws DBConnectionException �� - ������ ���������� � ����.
   * @throws SQLException �� - ����������� ������ ��� ���������� sql-��������.
  */
  @SuppressWarnings({"JDBCResourceOpenedButNotSafelyClosed"})
  public static ArrayList<String> execute(BatchConfig config) throws DBModuleConfigException, DBConnectionException, SQLException
   {
    // ��������� - ������ ������, ��������� ��� ���������� sql-�����
    ArrayList<String> result;

    // ��������� ������������ �� ������, ���� ��� ���� - ������ �� ������!
    String configErrors = DBUtils.getConfigErrors(config);
    if (!StringUtils.isBlank(configErrors)) {throw new DBModuleConfigException(configErrors);}
    else                                    {logger.debug("Batch configuration is OK. Processing.");}
    // ��������� ���������� � ���� ����� ����������� ���������� � �������� ����� �������� �������
    Connection connection = null;
    Statement statement  = null;
    try
     {
      // ����������� � ��������� ����. ���� ���������� � ���� ���������� �� ������� - ������ ������ ��
      // ����������� (��� ��� ������������ ��).
      connection = DBUtils.getDBConn(config.getDbConfig());
      statement  = connection.createStatement();
      logger.debug("Connection to DBMS established. All OK.");
     }
    // ����������� ����������� ������� (��������)
    finally {if (statement != null) {statement.close();} if (connection != null) {connection.close();}}

    // ����� ���������� ���� �������� (� ��������� �� �����������) ��������������� ��������� sql-����. ���������� �����
    // � ��������������/������������� ������� ������������ � ����������� �� ���������� �������. ��� ������������� ���������
    // ����� ����������, ����� �������� isMultiThreads=true � ���������� ���������� � ���� (�������������) ���� ������
    // ������������ (=2, ��. ��������� � ������ DBConsts). ����� ����������, ����� ���������� �������� � ����� ���������������
    // � ����������� ���������� � ����. �.�. �� ���� ���������� � ���� ������ ����������� ����� ������ sql-�������, ���
    // ����� ������� ��������� "�����������" - DBConsts.MIN_RATIO_DBMS_CONN_TO_SQL, ������� ��������� �� ����������� �����
    // sql-�������� ��� ������ ���������� � ����, ������� ��������� ������������ sql-���� � �������������� ������.
    // todo: ��������, ������� ������ ��������� ���������� ���������� ��� ����������� �������� �����������? ��� ���, ���
    // todo: ���������� ���������� ���������� ����� ������ ������������?

    // ������������ ���������� ���������� � ����
    int realConnNumber;
    int connNumber = config.getDbmsConnNumber();
    if ((connNumber  >= DBConsts.MIN_DBMS_CONNECTIONS) && (connNumber <= DBConsts.MAX_DBMS_CONNECTIONS))
     {realConnNumber = connNumber;}
    else if (connNumber > DBConsts.MAX_DBMS_CONNECTIONS)
     {realConnNumber = DBConsts.MAX_DBMS_CONNECTIONS;}
    else
     {realConnNumber = DBConsts.MIN_DBMS_CONNECTIONS;}
    // �� ����������� ������� ���������� �������� ������ ������� ����� - ������������ ��� �������������.
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

    // ���������� ��������� ���������� ����� (������ ��������� ������������� ������)
    return result;
   }

  /**
   * ����� ������ ��� ������������ ������� ������.
   * @param args String[] ��������� ������.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");

    // ���������� � ��
    DBConfig dbConfig = new DBConfig();
    dbConfig.setDbType(DBConsts.DBType.MYSQL);
    dbConfig.setHost("localhost:3306");
    dbConfig.setDbName("test");
    dbConfig.setUser("root");
    dbConfig.setPassword("mysql");

    // �������� sql-�����
    ArrayList<String> sqlBatch = new ArrayList<String>();
    for (int i = 0; i < 200000; i++)
     {
      sqlBatch.add("insert into test(string1, number1, string2, number2) " +
                   "values('string" + i + "', " + i + ",'string" + i*10 + "', " + i*10 + ")");
     }

    // ������������ ��� ���������� �����
    BatchConfig batchConfig = new BatchConfig();
    batchConfig.setDbConfig(dbConfig);
    //batchConfig.setBatch(sqlBatch);
    batchConfig.setOperationsCount(200);
    batchConfig.setMultiThreads(true);
    batchConfig.setDbmsConnNumber(20);

    logger.debug("--- Config created. Starting batch. ---");
    
    try
     {
      // ��������������� ������� ������� ����������
      //DataChanger.cleanupTable(dbConfig, "test");
      // ��������������� ������ �����
      SqlBatcher.execute(batchConfig);
     }
    catch (DBConnectionException e)   {logger.error("->" + e.getMessage()); e.printStackTrace();}
    catch (DBModuleConfigException e) {logger.error("->" + e.getMessage()); e.printStackTrace();}
    catch (SQLException e)            {logger.error("->" + e.getMessage()); e.printStackTrace();}

   }

 }