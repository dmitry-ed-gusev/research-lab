package jdb.facade;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс, реализующий функциональность объекта Statement.
 * @deprecated НЕ РЕКОМЕНДУЕТСЯ ИСПОЛЬЗОВАНИЕ КЛАССА ВВИДУ ЕГО УСТАРЕВАНИЯ!
*/

class SqlStatement
 {
  /** Название логгера для данного класса. */
  private static String          logger_name     = "SqlStatement";
  /** Непосредственно класс ведения журнала. */
  private static Logger          logger          = null;

  /** Поле для хранения внутреннего объекта Connection - соединение с СУБД. */
  private Connection conn          = null;
  /** Поле для хранения внутреннего объекта Statement. */
  private Statement stmt           = null;
  /** Внутреннее поле для хранения SQL-запроса. */
  private String sql               = null;
  /***/
  private int resultSetType        = -1;
  /***/
  private int resultSetConcurrency = -1;

  /***/
  //private SqlStatement() {}

  /**
   * Конструктор по умолчанию. Выполняет только инициализацию логгера для
   * данного класса (добавление компонента для вывода сообщений в консоль). Если
   * логгер уже имеет компонент для вывода сообщений - ничего не делаем.
   *
   * @param parent_logger_name String
  */
  public SqlStatement(String parent_logger_name)
   {
    if ((parent_logger_name != null) && (!parent_logger_name.trim().equals("")))
     if (SqlStatement.logger == null)
      {
       SqlStatement.logger = Logger.getLogger(parent_logger_name + "." +
                               SqlStatement.logger_name);
       logger.debug("Logger initialization completed OK.");
      }
     else
      logger.debug("Logger already initialized!");
   }

  /**
   * Инициализация объекта Statement.
   *
   * @throws SQLException
  */
  private void initStatement () throws SQLException
   {
    logger.debug("ENTERING initStatement().");
    // Если нет sql-запроса(поле sql пусто) - сразу на выход
    if (this.sql == null)
     {
      //logger.error(DBConsts.DBCONN_ERR_EMPTY_SQL);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_EMPTY_SQL);
     }
    // Если один из входных параметров пуст - создаем экземпляр класса
    // Statement с параметрами по умолчанию
    if ((this.resultSetType == -1) || (this.resultSetConcurrency == -1))
     {
      logger.debug("ResultSetType or ResultSetConcurrency is empty.");
      logger.debug("Creating easy statement (without parameters).");
      this.stmt = conn.createStatement();
     }
    else
     {
      logger.debug("ResultSetType or ResultSetConcurrency is not empty.");
      logger.debug("Creating statement with parameters.");
      // Создаем экземпляр класса Statement с переданными параметрами
      this.stmt = conn.createStatement(this.resultSetType, this.resultSetConcurrency);
     }
    logger.debug("LEAVING initStatement().");
   }

  /**
   * Метод закрывает объект SqlStatement и присваивает ему значение null.
   * @throws SQLException
  */
  public void close() throws SQLException
   {
    logger.debug("ENTERING close().");
    if(this.stmt != null)
     {
      logger.debug("Statement object not null. Closing statement.");
      this.stmt.close(); this.stmt = null;
      logger.debug("OK. Statement object closed.");
     }
    else
     logger.debug("Statement object is null, nothing to close.");
    logger.debug("LEAVING close().");
   }

  /**
   * Метод устанавливает значение поля sql данного объекта. После
   * этого объект SqlStatement реинициализируется.
   * @throws SQLException
   * @param  sql строка с SQL-запросом для выполнения.
  */
  public void setSql(String sql) throws SQLException
   {
    logger.debug("ENTERING setSql(String sql).");
    logger.debug("[SQL:" + sql + "]");
    this.sql = sql;
    logger.debug("Initializing SqlStatement field. Calling initStatement().");
    stmt = null;
    this.initStatement();
    logger.debug("LEAVING setSql(String sql).");
   }

  /**
   *
   * @throws SQLException
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
  */
  public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING setSql(String sql, int resultSetType, int resultSetConcurrency).");
    logger.debug("[resultSetType:" + resultSetType + "]");
    logger.debug("[resultSetConcurrency:" + resultSetConcurrency + "]");
    this.resultSetType        = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("LEAVING setSql(String sql, int resultSetType, int resultSetConcurrency).");
   }

  /**
   * Метод для доступа к значению поля sql данного объекта.
   * @return значение поля sql.
  */
  public String getSql()
   {
    logger.debug("ENTERING getSql().");
    logger.debug("[returning sql:" + this.sql + "]");
    logger.debug("LEAVING getSql().");
    return this.sql;
   }

  /**
   * Выполняет SQL-запрос, переданный в качестве параметра и возвращает
   * объект ResultSet с результирующим набором записей. Данный метод
   * позволяет выполнять только SELECT запросы, т.е. запросы, возвращающие
   * результирующий набор записей (ResultSet).
   * @throws SQLException
   * @param  sql строка с SQL-запросом для выполнения.
   * @return ResultSet набор записей, полученный в результате
   * выполнения sql-запроса.
  */
  public ResultSet executeQuery(String sql) throws SQLException
   {
    ResultSet rs;
    logger.debug("ENTERING executeSelectQuery(String sql).");
    logger.debug("[SQL:" + sql + "].");
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("Calling executeSelectQuery().");
    rs = this.executeQuery();
    logger.debug("LEAVING executeSelectQuery(String sql).");
    return rs;
   }

  /**
   * 123
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
   * @throws SQLException
   * @return ResultSet
  */
  public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(String sql, int resultSetType, int resultSetConcurrency).");
    logger.debug("[SQL:" + sql + "]");
    logger.debug("[resultSetType:" + resultSetType + "]");
    logger.debug("[resultSetConcurrency:" + resultSetConcurrency + "]");
    logger.debug("Calling setSql(String sql, int resultSetType, int resultSetConcurrency).");
    this.setSql(sql,resultSetType,resultSetConcurrency);
    logger.debug("Calling executeSelectQuery().");
    ResultSet rs = this.executeQuery();
    logger.debug("LEAVING executeSelectQuery(String sql, int resultSetType, int resultSetConcurrency).");
    return rs;
   }

  /**
   * Выполняет SQL-запрос, который хранится в поле sql даного объекта и
   * возвращает объект ResultSet с набором записей. Данный метод позволяет
   * выполнять только SELECT запросы, т.е. запросы, возвращающие данные. Если
   * объект не инициализирован, то возбуждается ИС.
   *
   * @throws SQLException
   * @return ResultSet набор записей, полученный в результате выполнения
   *   sql-запроса.
  */
  public ResultSet executeQuery() throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery().");
    // Проверка инициализации поля, хранящего объект SqlStatement
    if(this.stmt == null)
     {
      //logger.debug(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
     }
    else
     logger.debug("stmt field is OK. Continue.");
    logger.debug("Calling executeSelectQuery(String sql).");
    ResultSet rs = this.stmt.executeQuery(this.sql);
    logger.debug("LEAVING executeSelectQuery().");
    return rs;
   }

  /**
   * Выполняет SQL-запрос, переданный в качестве параметра и возвращает
   * код, сигнализирующий о статусе выполнения запроса. Данный метод позволяет
   * выполнять только UPDATE, INSERT или DELETE запросы.
   * @throws SQLException
   * @param  sql строка с запросом для выполнения.
   * @return код состояния, полученный от СУБД после выполнения запроса.
  */
  public int execute(String sql) throws SQLException
   {
    logger.debug("ENTERING execute(String sql).");
    logger.debug("[SQL:" + sql + "]");
    logger.debug("Calling setSql(String sql).");
    this.setSql(sql);
    logger.debug("Calling execute().");
    int res = this.execute();
    logger.debug("LEAVING execute(String sql).");
    return res;
   }

  /**
   * Выполняет SQL-запрос, который хранится в поле sql данного объекта и возвращает
   * код, сигнализирующий о статусе выполнения запроса. Данный метод позволяет
   * выполнять только UPDATE, INSERT или DELETE запросы. Если объект не
   * инициализирован, то возбуждается ИС.
   * @throws SQLException
   * @return код состояния, полученный от СУБД после выполнения запроса.
  */
  public int execute() throws SQLException
   {
    logger.debug("ENTERING execute().");
    if(this.stmt == null)
     {
      //logger.error(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
      //throw new SqlStatementException(DBConsts.DBCONN_ERR_SQLSTMT_INIT);
     }
    else
     logger.debug("stmt field is OK. Countinue.");
    logger.debug("Executing executeUpdate(String sql) method.");
    int count = this.stmt.executeUpdate(sql);
    logger.debug("LEAVING execute().");
    return count;
   }

  /**
   * Устанавливает значение поля Connection на соединение с СУБД.
   * @param conn объект Connection, содержащий рабочее соединение с СУБД.
  */
  public void setConnection(Connection conn)
   {
    logger.debug("ENTERING setConnection(Connection conn).");
    this.conn = conn;
    logger.debug("LEAVING setConnection(Connection conn).");
   }

 }//--> Конец объекта SqlStatement.
