package jdb.facade;

import dgusev.dbpilot.DBConsts;
import jlib.common.Consts;
import org.apache.log4j.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 Данный класс реализует шаблон ООП facade - фасад, который позволяет
 упростить взаимодействие со сложной системой и закрывает от пользователя
 основную внутреннюю функциональность взаимодействия. Класс DBFacade
 реализует фасад для работы с различными типами СУБД с помощью SQL-запросов.
 На данный момент поддерживается подключение и выполнение любых SQL-запросов
 для следующих СУБД:      <br>
  - Informix              <br>
  - mySQL                 <br>
  - ODBC-источник данных  <br>
  - DBF-файл данных       <br>
 Также в данном классе реализована функциональность соединения с СУБД с помощью
 одного разделяемого соединения (единственная точка подключения к СУБД, используемая
 в программе) или используя пул соединений, т.е. возможность использования
 ограниченного конечного количества соединений с СУБД (возможно одновременное подключение
 и выполнение запросов к нескольким СУБД). <br>
 Функциональность нескольких одновременных подключений к различным или к одной СУБД
 реализована следующим образом: <br>
  методы connect() и close() вызаваются парами, при вызове метода connect() создается
  новое соединение, оно помещается в пул и возвращается методом. Если пул уже полон, то
  новое соединение не создается, а методом возвращается последнее добавленное в пул
  соединение. Метод close() закрывает последнее добавленное в пул соединение и
  освобождает место в пуле. Максимальный размер пула показывает метод . Текущее
  количество соединений (объектов Connection) в пуле возвращает метод . Если пул не
  используется или еще не инициализирован, то оба метода возвращаеют 0.

 Журнал класса: по умолчание уровень ведения журнала установлен в Level.ERROR, и для
 вывода сообщений журнала используется системная консоль.

 @author Gusev Dmitry, dept. 019. 2005(с).
 @version 1.0
 * @deprecated НЕ РЕКОМЕНДУЕТСЯ ИСПОЛЬЗОВАНИЕ КЛАССА ВВИДУ ЕГО УСТАРЕВАНИЯ!
*/
public class DBFacade
 {
  // --- ХРАНЕНИЕ ПАРАМЕТРОВ ПОДКЛЮЧЕНИЯ К СУБД ---
  /** Поле для хранения типа СУБД. */
  private String db_type  = null;
  /** Поле для хранения хоста СУБД. */
  private String host     = null;
  /** Поле для хранения имени сервера СУБД (не хоста, а процесса СУБД). */
  private String server   = null;
  /** Поле для хранения имени БД на сервере. */
  private String db       = null;
  /** Поле для хранения имени пользователя для доступа к СУБД. */
  private String user     = null;
  /** Поле для хранения пароля пользователя для доступа к СУБД. */
  private String pwd      = null;
  /** Поле для хранения дополнительных параметров для подключения к СУБД. */
  private String params   = null;
  /** Поле для хранения доп. информации для соединения с СУБД. */
  private String connInfo = null;
  /**  */
  private Properties connProperties = null;

  /** Формат для журнала сообщений данного класса. */
  private static String          logger_format          = Consts.LOGGER_LOG_FORMAT_DEFAULT;
  /** Определяем формат ведение журнала (на основе шаблона). */
  private static PatternLayout   logger_patternLayout   = null;
  /** Класс для вывода сообщений журнала на консоль (ConsoleAppender). */
  private static ConsoleAppender logger_consoleAppender = null;
  /** Класс для вывода сообщений журнала в файл (FileAppender). */
  private static FileAppender    logger_fileAppender    = null;
  /** Название файла для ведения журнала класса. */
  private static String          logger_file            = null;
  /***/
  private static String          logger_parent_name     = null;
  /** Название компонента логгера (ведение журнала). */
  private static String          logger_name            = "DBFacade";
  /** Непосредственно класс ведения журнала. */
  private static Logger          logger                 = null;
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
  private static Level logger_level = Level.ERROR;

  /** Защищенный singleton-объект шаблона DBFacade. */
  private static DBFacade dbf = null;
  /** Поле для хранения экземпляра класса PreparedStatement. */
  private PSqlStatement p_ss   = null;
  /** Поле для хранения экземпляра класса Statement. */
  private SqlStatement  ss     = null;
  /** Поле для хранения экземпляра класса ConnectionManager. */
  private ConnectionManager cm     = null;
  /**
   Логическое поле, указывающее используется ли объектом DBFacade для
   выполнения SQL-запросов класс Statement (usePrepared = false) или
   класс PreparedStatement (usePrepared = true).
  */
  private static boolean usePrepared = false;
  /***/
  private static boolean test_mode   = false;

  /**  */
  public void setDb_type(String db_type)   {this.db_type = db_type;}
  /**  */
  public void setHost(String host)         {this.host = host;}
  /**  */
  public void setServer(String server)     {this.server = server;}
  /**  */
  public void setDb(String db)             {this.db = db;}
  /**  */
  public void setUser(String user)         {this.user = user;}
  /**  */
  public void setPwd(String pwd)           {this.pwd = pwd;}
  /**  */
  public void setParams(String params)     {this.params = params;}
  /**  */
  public void setConnInfo(String connInfo) {this.connInfo = connInfo;}

  /**
   * Инициализация логгера для данного класса (добавление компонента
   * для вывода сообщений в консоль). Если логгер уже имеет компонент
   * для вывода сообщений - ничего не делаем.
  */
  private static void initLogger(Properties props) //throws Exception
   {
    // Если логгер еще не инициализирован - инициализация
    if (DBFacade.logger == null)
     {
      // Если переданы параметры инициализации логгера - обработка
      if ((props != null) && (props.size() > 0))
       {
        // Формат сообщений для журнала
        DBFacade.logger_format = props.getProperty(Consts.LOGGER_LOG_FORMAT, DBFacade.logger_format);
        // Название файла для записей журнала
        DBFacade.logger_file   = props.getProperty(Consts.LOGGER_FILE_NAME, null);
        // Уровень ведения журнала
        DBFacade.logger_level  = Level.toLevel(props.getProperty(Consts.LOGGER_LOG_LEVEL, ""),
                                              DBFacade.logger_level);
       }
      // Шаблон ведения журнала
      DBFacade.logger_patternLayout   = new PatternLayout(DBFacade.logger_format);
      // Компонент для вывода сообщений журнала на консоль
      DBFacade.logger_consoleAppender = new ConsoleAppender(DBFacade.logger_patternLayout);
      // Непосредственно сам логгер
      DBFacade.logger = Logger.getLogger(DBFacade.logger_name);
      // Запрещаем наследование компонентов вывода сообщений (Appenders)
      DBFacade.logger.setAdditivity(false);
      // Устанавливаем уровень для сообщений лога
      DBFacade.logger.setLevel(DBFacade.logger_level);
      // Добавляем компонент для вывода сообщений на консоль
      DBFacade.logger.addAppender(logger_consoleAppender);
      // Если нам передали имя файла для журнала - добавим компонент для вывода в файл
      if ((DBFacade.logger_file != null) && (!DBFacade.logger_file.trim().equals("")))
       {
        try
         {
          DBFacade.logger_fileAppender = new FileAppender (DBFacade.logger_patternLayout,
                                                             DBFacade.logger_file, false);
          DBFacade.logger.addAppender(DBFacade.logger_fileAppender);
          logger.debug("FileAppender was added for [" + Logger.class + "].");
         }
        catch (Exception e)
         {
          logger.error("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
          //throw new Exception("Can't add fileAppender to Logger [ERROR: " + e.getMessage() + "]!");
         }
       }
      logger.debug("ConsoleAppender was added for [" + Logger.class + "].");
      // После инициализации логгера - сообщаем об этом
      logger.debug("Logger [" + Logger.class + "] initialized OK.");
     }
    else
     logger.debug("Logger already initialized!");
   }

  /**
   * Предоставляет единственную точку доступа к экземпляру класса DBFacade. Если
   * нет еще ни одного экземпляра класса DBFacade он создается и метод возвращает
   * ссылку на него, если же уже есть экземпляр - просто возвращается ссылка. При
   * использовании данного метода создается одиночное статическое соединение с СУБД
   * (пул создать нельзя). Для выполнения sql-запросов используется класс
   * Statement.
   * @return ссылка на экземпляр класса DBFacade (если его не было - создали).
   * @throws Exception
  */
  public static synchronized DBFacade getInstance() throws Exception {return DBFacade.getInstance(null);}

  /**
   * Предоставляет единственную точку доступа к экземпляру класса DBFacade. Если
   * нет еще ни одного экземпляра класса DBFacade он создается и метод возвращает
   * ссылку на него, если же уже есть экземпляр - просто возвращается ссылка.
   * Первый входной параметр указывает на исползование/не использование пула соединений.
   * Второй входной параметр указывает на тип объекта для выполнения sql-запросов -
   * Statement или Prepared Statement (преподготовленный запрос).
   * @return экземпляр класса DBFacade (если его не было - создали).
   * @throws Exception
  */
  public static synchronized DBFacade getInstance(Properties props) throws Exception
   {
    // Раз нам передали параметры - там могут быть и пар-ры для логгера
    DBFacade.initLogger(props);
    logger.debug("ENTERING getInstance(Properties props).");
    if ((props != null) && (props.size() > 0))
     {
      logger.debug("Input parameters (Properties) are not empty.");
      // Получим значение для поля <usePrepared>
      //DBFacade.usePrepared = Boolean.getBoolean(props.getProperty(DBConsts.DBCONN_USE_PREPARED, "false"));
      logger.debug("Received usePrepared value [" + DBFacade.usePrepared + "].");
     }
    else
     logger.debug("Input parameters are empty.");
    // Теперь попытаемся инициализировать экземпляр класса DBFacade
    logger.debug("Trying to initialize DBFacade instance.");
    if(DBFacade.dbf == null)
     {
      logger.debug("DBFacade is not initialized. Initializing...");
      DBFacade.dbf = new DBFacade();
     }
    else
     logger.debug("DBFacade already initialized.");
    logger.debug("LEAVING getInstance(Properties props).");
    return DBFacade.dbf;
   }

  /**
   * Конструктор объявлен как private для того, чтобы нельза было получить
   * дополнительные экземпляры данного класса - создание и получение
   * экземпляров возможно только с помощью методов getInstance() - это
   * ключевая особенность шаблона ООП singleton (одиночка).
   * @throws Exception
  */
  private DBFacade() throws Exception {cm = ConnectionManager.getInstance();}

  /**
   * Метод позволяет получить информацию - используется ли для данного экземпляра
   * класса DBFacade класс PreparedStatement (значение true) или же класс
   * Statement (значение false).
   * @return boolean значение параметра, определяющего использование одного или
   * другого класса для выполнения sql-запросов.
  */
  public boolean get_use_prepared_status()
   {
    logger.debug("ENTERING get_use_prepared_status().");
    logger.debug("Returning usePrepared value [" + DBFacade.usePrepared + "].");
    logger.debug("LEAVING get_use_prepared_status().");
    return DBFacade.usePrepared;
   }

  /**
   * Метод позволяет установить значение параметра, который определяет использование
   * одного из классов для выполнения sql-запросов (Statement или Prepared Statement).
   * использовать данный метод имеет смысл только ПЕРЕД методом DBFacade.connect(), т.к.
   * только перед непосредственным установлением соединения с СУБД можно указать тип класса,
   * используемого для выполнения sql-запросов. Т.о. использовать для выполнения
   * sql-запросов класса PreparedStatement можно двумя способами:
   * <br> <b>1.</b> выполнить метод DBFacade.getInstance(<boolean>, true)
   * <br> <b>2.</b> выполнить метод DBFacade.getInstance(), а затем выполнить метод
   * DBFacade.set_use_prepared_status(true).
   * <br>Причем, если после вызова метода из п.1 вызвать метод
   * DBFacade.set_use_prepared_status(false), то для выполнения sql-запросов будет
   * использован класс Statement.
   * @param usePrepared boolean 123
  */
  public void set_use_prepared_status(boolean usePrepared)
   {
    logger.debug("ENTERING set_use_prepared_status(boolean usePrepared).");
    logger.debug("Received usePrepared value [" + usePrepared + "]. Setting value.");
    DBFacade.usePrepared = usePrepared;
    logger.debug("LEAVING set_use_prepared_status(boolean usePrepared).");
   }

  //Initialization to open a connection and pass it to the SqlStatement object.
  //private void init(Properties props) throws Exception
  // {
  //  Connection conn = cm.connect(props);
  //  p_ss = new PSqlStatement();
  //  p_ss.setConnection(conn);
  //  ss = new SqlStatement();
  //  ss.setConnection(conn);
  // }

  /**
   * Данный метод выполняет соединение с СУБД. Входные параметры определяют
   * параметры соединения.
   *
   * @param props Properties Основные параметры соединения с СУБД (тип, хост
   *   ...).
   * @throws Exception
   * @return int
  */
  public int connect(Properties props) throws Exception
   {
    logger.debug("ENTERING connect(Properties props).");
    // В этой переменной храним номер созданного соединения в пуле
    int conn_number = -1;
    // В этой переменной храним созданное соединение (объект Connection)
    Connection conn = null;
      logger.debug("Using connection pool. Trying to add connection to the pool.");
      // Добавление соединения в пул, получаем его номер в пуле
      //conn_number = cm.connect(props);
      logger.debug("[returned number of connection in pool: " + conn_number + "]");
      // Если нам вернулась -1 - пул заполнен и новое соединение не добавлено
      if (conn_number == -1) // <- ПУЛ ЗАПОЛНЕН, СОЕДИНЕНИЕ НЕ ДОБАВЛЕНО
       {
        logger.error("Connection pool size = MAX. New connection not added!");
        // ИС - пул заполнен!
        throw new Exception("Connection pool size = MAX. New connection not added!");
       }
      else // <- СОЕДИНЕНИЕ ДОБАВЛЕНО
       {
        logger.debug("Connection pool size < MAX. New connection added!");
        // Получаем этот объект Connection из пула
        //conn = cm.getConn(conn_number);
       }

    logger.debug("Calling setStmtConn(Connection conn) method.");
    // Вызываем метод, для инициализации класса Statement и установки соединения по умолчанию
    if (conn != null)
     {
      logger.debug("Connection is not null - STATEMENT object initialized!");
      this.setStmtConn(conn);
     }
    else
     logger.debug("Connection is null! STATEMENT object is not initialized!");
    logger.debug("LEAVING connect(Properties props).");
    return conn_number;
   }

 /**
  *
  * @param conn Connection
  */
 private void setStmtConn(Connection conn)
   {
    logger.debug("ENTERING setStmtConn(Connection conn).");
    // Установка соединения производится только если соединение инициализировано
    if (conn != null)
     {
      logger.debug("Connection received successfully.");
      if (DBFacade.usePrepared) // <- USING PREPARED SQL STATEMENT
       {
        logger.debug("Using prepared SQL-statements.");
        // Если класс PreparedStatement не инициализирован - инициализация
        if(this.p_ss == null)
         {
          logger.debug("Prepared SQL-statement not initialized. Initializing...");
          this.p_ss = new PSqlStatement();
         }
        else
         logger.debug("Prepared SQL-statement already initialized.");
        // Непосредственно установка соединения для объекта Prepared Statement
        logger.debug("Setting connection for prepared SQL-statement.");
        p_ss.setConnection(conn);
       }
      else // <- NOT USING PREPARED SQL STATEMENT
       {
        logger.debug("Not using prepared SQL-statements.");
        // Если класс Statement не инициализирован - инициализация
        if(this.ss == null)
         {
          logger.debug("SQL-statement not initialized. Initializing...");
          this.ss = new SqlStatement(DBFacade.logger_name);
         }
        else
         logger.debug("SQL-statement already initialized.");
        // Непосредственно установка соединения для объекта Statement
        logger.debug("Setting connection for SQL-statement.");
        this.ss.setConnection(conn);
       }
     }
    else // <- СОЕДИНЕНИЕ НЕ БЫЛО ИНИЦИАЛИЗИРОВАНО
     logger.debug("Connection not initialized (input Connection parameter is NULL)! ");
    logger.debug("LEAVING setStmtConn(Connection conn).");
   }

  /**
   * Метод для выполнения sql-запроса, возвращающего данные (select).
   * Функциональность зависит от параметра usePrepared
   * @param sql String запрос, передаваемый методу на выполнение. Запрос
   * может быть только типа SELECT ...
   * @return ResultSet результат выполнения запроса на выборку данных - курсор.
   * @throws SQLException
  */
  public ResultSet executeQuery(String sql) throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(String sql).");
    ResultSet rs = this.executeQuery(-1, sql, -1, -1);
    logger.debug("LEAVING executeSelectQuery(String sql).");
    return rs;
   }

  /***/
  public ResultSet executeQuery(int conn_number, String sql) throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(int conn_number, String sql).");
    ResultSet rs = this.executeQuery(conn_number, sql, -1, -1);
    logger.debug("LEAVING executeSelectQuery(int conn_number, String sql).");
    return rs;
   }

  /***/
  public ResultSet executeQuery(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    return this.executeQuery(-1, sql, resultSetType, resultSetConcurrency);
   }

  /**
   * Данный метод является базовым для выполнения sql-запроса, переданного в качестве
   * параметра. Метод позволяет выполнять только SELECT-запросы, т.е. только
   * запросы, возвращающие данные - курсор (ResultSet).
   * @param conn_number int
   * @param sql String
   * @param resultSetType int
   * @param resultSetConcurrency int
   * @throws SQLException
   * @return ResultSet
  */
  public ResultSet executeQuery(int conn_number, String sql,
                                int resultSetType, int resultSetConcurrency)
   throws SQLException
   {
    logger.debug("ENTERING executeSelectQuery(int conn_number, String sql, " +
                 "int resultSetType, int resultSetConcurrency)");
    logger.debug("[SQL:" + sql + "]");
    // Храним результат выборки - курсор
    ResultSet rs;
    // Проверим, что переданный нам запрос не пуст и это select-запрос.
    // Если проверка завершилась неудачно, то установим флаг в значение
    // ЛОЖЬ и дальнейшие операции выполнены не будут - метод вернет
    // пустой курсор (null).
    if ((sql != null) && (!sql.trim().equals("")) &&
        (sql.trim().toUpperCase().startsWith("SELECT")))
     {
      logger.debug("SQL query is OK (not empty and 'select...' like).");
     }
    else
     {
      logger.error("SQL is invalid (empty or not 'select...' like)!");
      logger.error("[SQL:" + sql + "]");
      // Если запрос неверного типа - выдается ИС (исключительная ситуация)
      throw new SQLException("SQL is invalid (empty or not 'select...' like)!" +
                             "\n[SQL:" + sql + "]");
     }
    // Добавить проверку инициализации соединения и/или пула!

      logger.debug("Using connection pool. Continue.");
    /*
      if (this.cm.isConnExist(conn_number))
       {
        logger.debug("Connection #" + conn_number + " exists in the pool.");
        // Установим для объекта Statement соединение по умолчанию
        this.setStmtConn(this.cm.getConn(conn_number));
       }
      else
       {
        logger.debug("Connection #" + conn_number + " not exists in the pool.");
        logger.debug("Using last connection in the pool.");
       }
     */
    
    // Непосредственное выполнение SQL-запроса
    if (DBFacade.usePrepared) // <- USED PREPARED STATEMENT
     {
      logger.debug("Using prepared statements. Continue - running query.");
      // Если заданы оба параметра, обозначающие тип возвращаемого курсора,
      // то вызываем метод executeSelectQuery() с этими параметрами.
      if ((resultSetType > 0) && (resultSetConcurrency > 0))
       {
        logger.debug("For ResultSet class types used - running query.");
        rs = this.p_ss.executeQuery(sql, resultSetType, resultSetConcurrency);
       }
      else
       {
        logger.debug("For ResultSet class types not used - running query.");
        rs = this.p_ss.executeQuery(sql);
       }
     }
    else // <- NOT USED PREPARED STATEMENT
     {
      logger.debug("Not using prepared statement. Continue - running query.");
      // Если заданы оба параметра, обозначающие тип возвращаемого курсора,
      // то вызываем метод executeSelectQuery() с этими параметрами.
      if ((resultSetType > 0) && (resultSetConcurrency > 0))
       {
        logger.debug("For ResultSet class types used - running query.");
        rs = this.ss.executeQuery(sql, resultSetType, resultSetConcurrency);
       }
      else
       {
        logger.debug("For ResultSet class types not used - running query.");
        rs = this.ss.executeQuery(sql);
       }
     }

    logger.debug("LEAVING executeSelectQuery(int conn_number, String sql, " +
                 "int resultSetType, int resultSetConcurrency)");
    return rs;
   }

  //Method to execute SELECT SQL statements
  public ResultSet executeQuery() throws SQLException
   {
    if (DBFacade.usePrepared) return this.p_ss.executeQuery();
    else return this.ss.executeQuery();
   }

  //Method to execute all SQL statements except SELECT
  public int execute(String sql) throws SQLException
   {
    logger.debug("ENTERING execute(String sql).");
    logger.debug("[SQL:" + sql + "]");
    // Проверим на правильность переданный нам sql-запрос
    if ((sql != null) && (!sql.trim().equals("")) &&
        (!sql.trim().toUpperCase().startsWith("SELECT")))
     {
      logger.debug("SQL query is OK (not empty and not 'select...' like).");
     }
    else
     {
      logger.error("SQL query is invalid (empty or 'select...' like)!");
      logger.error("[SQL:" + sql + "]");
      throw new SQLException("SQL query is invalid (empty or 'select...' like)!" +
                             "\n[SQL:" + sql + "]");
     }

    int res = 0;
    if (usePrepared) res = p_ss.execute(sql);
    else res = ss.execute(sql);
    logger.debug("LEAVING execute(String sql).");
    return res;
   }

  //Method to execute all SQL statements except SELECT
  public int execute() throws SQLException
   {
    if (usePrepared) return p_ss.execute();
    else return ss.execute();
   }

  //Sets the SQL string in the PSqlStatement object
  public void setSql(String sql) throws SQLException
   {if (usePrepared) p_ss.setSql(sql); else ss.setSql(sql);}

  public void setSql(String sql, int resultSetType, int resultSetConcurrency)
   throws SQLException
    {
     if (DBFacade.usePrepared) p_ss.setSql(sql);
     else ss.setSql(sql, resultSetType,resultSetConcurrency);
    }

  //Clears the SqlStatement object
  //public void reset(Properties props) throws Exception
  // {
    //Set the reference to the ss to null;
    //p_ss = null;
  //  ss = null;
    //Reinitialize object
  //  init(props);
  // }

  /**
   Закрывает соединение с СУБД.
   @throws SQLException
  */
  public void close() throws SQLException
   {
    if (usePrepared) {if(p_ss != null) p_ss.close();}
    else {if(ss != null) ss.close();}
    if(cm != null) cm.close();
   }

  /**
   * Set a String value in a PreparedStatement
   *
   * @param index int
   * @param value String
   * @throws SQLException
  */
  public void setString(int index, String value) throws SQLException
   {
    logger.debug("ENTERING setString(int index, String value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setString(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setString(int index, String value).");
   }

  /**
   * Set an int value in a PreparedStatement
   *
   * @param index int
   * @param value int
   * @throws SQLException
  */
  public void setInt(int index, int value) throws SQLException
   {
    logger.debug("ENTERING setInt(int index, int value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setInt(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setInt(int index, int value).");
   }

  /**
   * Set a double value in a PreparedStatement
   *
   * @param index int
   * @param value double
   * @throws SQLException
  */
  public void setDouble(int index, double value) throws SQLException
   {
    logger.debug("ENTERING setDouble(int index, double value).");
    logger.debug("[index:" + index + ";value:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setDouble(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setDouble(int index, double value).");
   }

  /**
   * Set a Date value in a PreparedStatement
   *
   * @param index int
   * @param value Date
   * @throws SQLException
  */
  public void setDate(int index, Date value) throws SQLException
   {
    logger.debug("ENTERING setDate(int index, Date value).");
    logger.debug("[index:" + index + "; Date:" + value + "]");
    if (DBFacade.usePrepared)
     {
      logger.debug("Using prepared statements. Continue.");
      this.p_ss.setDate(index, value);
     }
    else
     logger.error("Prepared statements not used by this instance!");
    logger.debug("LEAVING setDate(int index, Date value).");
   }

  /**
   * Метод показывает конфигурационные поля данного класса, а также конфигурацию
   * соединения с СУБД (вызывает метод print_config() класса ConnectionManager).
  */
  public void print_config()
   {
    System.out.println("----- DEBUG INFO (CONFIG) [" + this.getClass().getName() + "] -----");
    System.out.println("usePrepared = " + DBFacade.usePrepared);
    System.out.println("*** DBMS connection properties ***");
    //this.cm.print_config();
    System.out.println("----- DEBUG INFO (CONFIG) [" + this.getClass().getName() + "] -----");
   }

 /**
  * Метод main предназначен для тестирования данного класса.
  * @param args String[] список параметров командной строки для передачи
  * методу main.
  */
 public static void main(String[] args)
    {
     try
      {
       Properties props = new Properties();
       DBFacade db = DBFacade.getInstance();
       //props.put(DBConsts.DB_TYPE, DBConsts.DBTYPE_INFORMIX);
       props.put(DBConsts.DB_HOST,    "10.1.19.30:1526");
       props.put(DBConsts.DB_SERVER,  "edu");
       //props.put(DBConsts.DB_NAME,      "flt");
       props.put(DBConsts.DB_USER,    "informix");
       props.put(DBConsts.DB_PWD,     "123456");

       props = null;
       db.connect(props);

       //db.print_config();
       //ResultSet rs = db.executeSelectQuery("SELECT namer FROM flt");

       //props = new Properties();
       //props.setProperty(DBConsts.DB_TYPE, DBConsts.DBTYPE_DBF);
       //props.put(DBC.DB_CONN_INFO, DBC.DBCONN_INFO_DBF_DBASE4);
       //props.setProperty(DBConsts.DB_NAME, "e:/temp");
       //int dbf_conn = db.connect(props);
       //db.print_config();
       //ResultSet rs2 = db.executeSelectQuery("SELECT name FROM insp");

       //ResultSet rs  = db.executeSelectQuery(ifx_conn, "SELECT namer FROM flt",
       //                                ResultSet.TYPE_SCROLL_INSENSITIVE,
       //                                ResultSet.CONCUR_READ_ONLY);
       //ResultSet rs2 = db.executeSelectQuery(dbf_conn, "SELECT name FROM insp");

       //int Counter = 0;
       //while (rs.next()) Counter++;
       //System.out.println("count = " + Counter);

       //rs.beforeFirst();

       //while (rs.next() && rs2.next())
       // {System.out.println(rs.getString("namer") + ":" + rs2.getString("name"));}

       //  while (rs2.next())
       //  {System.out.println(rs2.getString("name"));}
       //db.close();
     /*
     Properties props = new Properties();
     props.put(DBC.DB_TYPE, DBC.DBTYPE_ODBC);
     props.put(DBC.DB_NAME, "c:\\temp\\documents.mdb");
     props.put(LOGC.LOGGER_LOG_LEVEL, "DEBUG");
     //props.put(LOGC.LOGGER_FILE_NAME, "c:\\temp\\1");
     DBFacade db = DBFacade.getInstance(props);
     db.connect(props);
     String sql = "SELECT Departments.DepartmentNumber, Departments.DepartmentName, DocumentType.DocumentTypeID, " +
            "Mailing.MailingDate, DocumentType.DocumentType_Ru, Documents.Number_New, VolumeType.VolumeType_Ru, " +
            "Edoc.VolumeNumber FROM VolumeType INNER JOIN ((Departments INNER JOIN Keepers ON Departments.DepartmentID = " +
            "Keepers.DepartmentID) INNER JOIN (EdocType INNER JOIN ((DocumentType INNER JOIN (Documents INNER JOIN Edoc ON " +
            "Documents.DocumentID = Edoc.DocumentID) ON DocumentType.DocumentTypeID = Documents.DocumentTypeID) INNER JOIN " +
            "Mailing ON Edoc.EdocID = Mailing.EdocID) ON EdocType.EdocTypeID = Edoc.EdocTypeID) ON (Documents.DocumentID = " +
            "Keepers.DocumentID) AND (Keepers.KeepersID = Mailing.KeepersID)) ON VolumeType.VolumeTypeID = Edoc.VolumeTypeID " +
            "WHERE (((Departments.DepartmentNumber)=19) AND ((DocumentType.DocumentTypeID)=1 Or (DocumentType.DocumentTypeID)=2 " +
            "Or (DocumentType.DocumentTypeID)=3) AND ((Edoc.EdocTypeID)=1) AND ((Mailing.ConfirmationDate) Is Null))" +
            " UNION SELECT Departments.DepartmentNumber, Departments.DepartmentName, DocumentType.DocumentTypeID, " +
            "Mailing.MailingDate, DocumentType.DocumentType_Ru, AdditionalDocument.AdditionalDocumentNumber_New, " +
            "VolumeType.VolumeType_Ru, Edoc.VolumeNumber FROM VolumeType INNER JOIN ((Departments INNER JOIN Keepers ON " +
            "Departments.DepartmentID = Keepers.DepartmentID) INNER JOIN (EdocType INNER JOIN ((DocumentType INNER JOIN " +
            "(Documents INNER JOIN (AdditionalDocument INNER JOIN Edoc ON AdditionalDocument.AdditionalDocumentID = Edoc.DocumentID) " +
            "ON Documents.DocumentID = AdditionalDocument.DocumentID) ON DocumentType.DocumentTypeID = Documents.DocumentTypeID) " +
            "INNER JOIN Mailing ON Edoc.EdocID = Mailing.EdocID) ON EdocType.EdocTypeID = Edoc.EdocTypeID) ON (Documents.DocumentID " +
            "= Keepers.DocumentID) AND (Keepers.KeepersID = Mailing.KeepersID)) ON VolumeType.VolumeTypeID = Edoc.VolumeTypeID WHERE " +
            "(((Departments.DepartmentNumber)=19) AND ((DocumentType.DocumentTypeID)=1 Or (DocumentType.DocumentTypeID)=2 Or " +
            "(DocumentType.DocumentTypeID)=3) AND ((Edoc.EdocTypeID)=2) AND ((Mailing.ConfirmationDate) Is Null)) ORDER BY " +
            "DocumentType.DocumentTypeID, Mailing.MailingDate, Documents.Number_New, Edoc.VolumeNumber";
     ResultSet rs = db.executeSelectQuery(sql);
      ResultSetMetaData rsmeta = rs.getMetaData();
      while (rs.next())
       {
        for (int i = 1; i <= rsmeta.getColumnCount(); i++)
         System.out.print(rs.getString(i) + " ");
        System.out.println();
       }
      db.close();
      */
      }
     catch (Exception e)
      {System.out.println("error:" + e.getMessage()); /*e.printStackTrace();*/}
    }
 }//--> Конец объекта DBFacade.
