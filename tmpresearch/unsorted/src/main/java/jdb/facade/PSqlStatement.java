package jdb.facade;

import jdb.exceptions.SqlStatementException;
import jlib.common.Consts;
import org.apache.log4j.*;

import java.sql.*;

/**
 Класс, реализующий функциональность объекта PreparedStatement
 (заранее подготовленный для выполнения SQL-запрос).
 * @deprecated НЕ РЕКОМЕНДУЕТСЯ ИСПОЛЬЗОВАНИЕ КЛАССА ВВИДУ ЕГО УСТАРЕВАНИЯ!
*/

class PSqlStatement
 {
  /** Определяем формат ведение журнала (на основе шаблона). */
  private static PatternLayout   patternLayout   = new PatternLayout(Consts.LOGGER_LOG_FORMAT_DEFAULT);
  /** Класс для вывода сообщений журнала на консоль (ConsoleAppender). */
  private static ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
  /** Класс для вывода сообщений журнала в файл (FileAppender). */
  private static FileAppender    fileAppender    = null;
  /** Название файла для ведения журнала класса. */
  private static String          log_file        = PSqlStatement.class.getName();
  /** Непосредственно класс ведения журнала. */
  private static Logger          logger          = Logger.getLogger(PSqlStatement.class);
  /**
   * Переменная для хранения уровня ведения журнала.
   * Описание уровней см. в классе org.apache.log4j.
   * Возможные значения:
   * - Level.DEBUG
   * - Level.INFO
   * - Level.WARN
   * - Level.ERROR
   * - Level.FATAL
   * - Level.ALL - вывод сообщений всех уровней
   * - Level.OFF - отключение вывода всех сообщений
   * Приоритет значений: DEBUG < INFO < WARN < ERROR < FATAL.
   * По умолчанию инициализируется значением Level.ALL
   * Установка и получение значений осуществляется с помощью
   * пары методов set_log_level(Level level) + get_log_level().
  */
  private static Level log_level = Level.ERROR;

  /** Поле для хранения внутреннего объекта Connection - соединение с СУБД. */
  private Connection conn          = null;
  /** Поле для хранения внутреннего объекта PreparedStatement. */
  private PreparedStatement pstmt  = null;
  /** Внутреннее поле для хранения SQL-запроса. */
  private String sql               = null;
  /***/
  private int resultSetType        = -1;
  /***/
  private int resultSetConcurrency = -1;

  /**
   * Конструктор по умолчанию. Выполняет только инициализацию логгера для
   * данного класса (добавление компонента для вывода сообщений в консоль).
   * Если логгер уже имеет компонент для вывода сообщений - ничего не делаем.
  */
  public PSqlStatement()
   {
    // Устанавливаем уровень для сообщений лога
    logger.setLevel(PSqlStatement.log_level);
    // Запрещаем наследование компонентов вывода сообщений (Appenders)
    if (logger.getAdditivity()) logger.setAdditivity(false);
    // Если у логгера еще нет ни одного компонента для вывода (Appender),
    // то добавляем этот компонент
    if (!logger.getAllAppenders().hasMoreElements())
     {
      // Компонент для вывода на консоль
      logger.addAppender(consoleAppender);
      try
       {
        // Компонент для вывода в файл <- пока не используем
        fileAppender = new FileAppender (patternLayout, log_file, false);
        logger.addAppender(fileAppender);
       }
      catch (Exception e)
       {
        logger.error("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
        //throw new Exception("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
       }
      // После добавления компонента, мы можем начать вести журнал
      logger.debug("Logger [" + Logger.class + "] initialized OK.");
      logger.debug("ConsoleAppender was added for [" + Logger.class + "].");
      logger.debug("FileAppender was added for [" + Logger.class + "].");
     }
   }

 /**
  * Инициализация объекта Statement.
  *
  * @throws SQLException
  */
 private void initStatement() throws SQLException
   {
    // Only initialize PrepareStatement if member sql is not null.
    if(sql == null) throw new SqlStatementException(/**DBConsts.DBCONN_ERR_EMPTY_SQL*/);
    pstmt = conn.prepareStatement(sql);
   }
  /**
   Закрывает PreparedStatement объект.
   @throws SQLException
  */
  public void close() throws SQLException
   {if(pstmt != null) {pstmt.close(); pstmt = null;}}

 /**
  * Устанавливает значение поля sql данного объекта и переинициализирует объект
  * PreparedStatement.
  *
  * @param sql String
  * @throws SQLException
  */
 public void setSql(String sql) throws SQLException
   {this.sql = sql; pstmt = null; initStatement();}

 public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    this.resultSetType        = resultSetType;
    this.resultSetConcurrency = resultSetConcurrency;
    this.setSql(sql);
   }

 /**
  Возвращает текущий SQL-запрос.
  @return sql-запрос.
 */
 public String getSql() {return sql;}

 /**
  * Выполняет статический SQL-запрос на выборку данных(SELECT), переданный в
  * качестве параметра.
  *
  * @param sql SQL-запрос для выполнения
  * @return Объект ResultSet с набором выбранных данных.
  * @throws SQLException
  */
 public ResultSet executeQuery(String sql) throws SQLException
  {setSql(sql); return executeQuery();}

 public ResultSet executeQuery(String sql,int resultSetType, int resultSetConcurrency)
  throws SQLException
   {this.setSql(sql,resultSetType,resultSetConcurrency); return executeQuery();}

 /**
  * Выполняет статический SQL-запрос (SELECT), установленный в качестве значения
  * поля sql данного класса.
  *
  * @return Объект ResultSet с набором выбранных данных.
  * @throws SQLException
  */
 public ResultSet executeQuery() throws SQLException
  {
   //Check to see if pstmt statement is null.
   if(pstmt == null)
    throw new SqlStatementException(/**DBConsts.DBCONN_ERR_PSQLSTMT_INIT*/);
   return pstmt.executeQuery();
  }

 /**
  * Выполняет статические SQL-запросы UPDATE, INSERT, или DELETE, переданные в
  * качестве парметра.
  *
  * @param sql SQL-запрос для выполнения.
  * @return Значение типа int - код ошибки привыполнении запроса.
  * @throws SQLException
  */
 public int execute(String sql) throws SQLException
  {setSql(sql); return execute();}

 /**
  * Выполняет статические SQL-запросы UPDATE, INSERT, или DELETE, установленные
  * в качестве значения поля sql данного класса.
  *
  * @return Значение типа int - код ошибки привыполнении запроса.
  * @throws SQLException
  */
 public int execute() throws SQLException
 {
  if(pstmt == null) throw new SqlStatementException(/**DBConsts.DBCONN_ERR_PSQLSTMT_INIT*/);
  return pstmt.executeUpdate();
 }
 /**
  Устанавливает объект Connection на действующее соединение с СУБД.
  @param conn указатель на соединение с СУБД.
 */
 public void setConnection(Connection conn) {this.conn = conn;}
 /**
  Устанавливает значение типа String в подготовленном sql-запросе(PreparedStatement).
  @throws SQLException
  @param index положение параметра в подготовленном запросе
  @param value значение параметра для подстановки
 */
 public void setString(int index, String value) throws SQLException
  {pstmt.setString(index,value);}
 /**
  Устанавливает значение типа int в подготовленном sql-запросе(PreparedStatement).
  @throws SQLException
  @param index положение параметра в подготовленном запросе
  @param value значение параметра для подстановки
 */
 public void setInt(int index, int value) throws SQLException
  {pstmt.setInt(index,value);}
 /**
  Устанавливает значение типа Double в подготовленном sql-запросе(PreparedStatement).
  @throws SQLException
  @param index положение параметра в подготовленном запросе
  @param value значение параметра для подстановки
 */
 public void setDouble(int index, double value) throws SQLException
  {pstmt.setDouble(index,value);}
 /**
  Устанавливает значение типа Date в подготовленном sql-запросе(PreparedStatement).
  @throws SQLException
  @param index положение параметра в подготовленном запросе
  @param value значение параметра для подстановки
 */
 public void setDate(int index, Date value) throws SQLException
  {pstmt.setDate(index,value);}
}//--> конец объекта PSqlStatement
