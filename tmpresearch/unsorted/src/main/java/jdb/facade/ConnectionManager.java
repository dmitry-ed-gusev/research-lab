package jdb.facade;

import dgusev.dbpilot.DBConsts;
import jlib.common.Consts;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * ОПИСАНИЕ:
 * Данный класс реализует соединение с СУБД. Объект соединения (Connection)
 * является статическим. Также этот класс предоставляет для использования
 * пул статических соединений с СУБД, кол-во которых конечно; пул является
 * статическим. Класс ConnectionManager позволяет получить только ОДИН экземпляр
 * (в данном классе реализован шаблон ООП singleton - одиночка)! Поэтому
 * конструктор по умолчанию объявлен как private, других конструкторов нет.
 * Создание экземпляра и доступ к уже созданному экземпляру (получение ссылки
 * на него) осуществляется с помощью метода getInstance(). Этот метод возвращает
 * ссылку на созданный экземпляр класса, а если нет ни одного экземпляра, то
 * создает его. Для использования пула соединений необходимо передать
 * методу connect(Properties, Properties) в первом параметре пару имя=значение
 * следующего вида: LIB.USE_CONN_POOL=LIB.TRUE_STR.
 * ВНИМАНИЕ! ДАННЫЙ КЛАСС ЯВЛЯЕТСЯ ЧАСТЬЮ БОЛЬШОГО КЛАССА DBFacade
 * (ИСПОЛЬЗУЕТСЯ ЭТИМ КЛАССОМ)! ОТДЕЛЬНОЕ ИСПОЛЬЗОВАНИЕ ДАННОГО КЛАССА НЕ
 * РЕКОМЕНДУЕТСЯ!
 * @author Gusev Dmitry, dept. 019, RMRS. (c) 2006.
 * @version 1.0
 * @deprecated НЕ РЕКОМЕНДУЕТСЯ ИСПОЛЬЗОВАНИЕ КЛАССА ВВИДУ ЕГО УСТАРЕВАНИЯ!
*/

class ConnectionManager
 {
  /** Макс. возможное число соединений в пуле. */
  private static final int MAX_CONNECTIONS = 20;

  /** Непосредственно класс ведения журнала. */
  private static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
  /** Конфигурация менеджера соединений с СУБД. */
  //private ConnectionConfig config = null;

  /** Единственный экземпляр данного класса(реализация шаблона singleton). */
  private static ConnectionManager manager = null;

  /** Единственный (статический) пул соединений с СУБД. */
  private static ArrayList<Connection> connectionsPool = null;

  /**
   * Конструктор по умолчанию объявлен как private для того, чтобы быть уверенным, что никто не сможет получить еще
   * один экземпляр данного класса.
   * Конструктор инициализирует компонент-логгер для данного класса.
  */
  private ConnectionManager()
   {
    // Инициализация логгера
    logger.debug("WORKING ConnectionManager constructor.");
   }

  /**
   * Метод используется для получения только одного экземпляра класса ConnectionManager, который сохраняется в локальной
   * переменной и возвращается по необходимости.Т.к. в данном классе реализован шаблон ООП singleton (одиночка), то
   * экземпля класса создается только если он не был еще создан, в противном случае возвращается ссылка на уже созданный
   * экземпляр.
   * @return ConnectionManager ссылка на единственный экземпляр класса ConnectionManager.
  */
  public static synchronized ConnectionManager getInstance()
   {
    // Если менеджер не инициализирован - инициализируем
    if(ConnectionManager.manager == null) ConnectionManager.manager = new ConnectionManager();
    logger.debug("WORKING ConnectionManager.getInstance(). Manager initialized.");
    return manager;
   }

  /**
   * Метод возвращает максимальное количество соединений возможное в пуле.
   * @return int вместимость пула соединений.
  */
  public int getPoolMaxCapacity()
   {
    logger.debug("WORKING ConnectionManager.getPoolCapacity(). RETURNED: " + ConnectionManager.MAX_CONNECTIONS);
    return ConnectionManager.MAX_CONNECTIONS;
   }

  /**
   * Если пул соединений инициализирован, то метод возвращает количество соединений, реально находящихся в пуле.
   * Если пул не инициализирован, метод возвращает значение -1.
   * @return количество соединений, находящихся в пуле.
  */
  public int getCurrentPoolSize()
   {
    int res = -1;
    if (this.isPoolInitialized()) res = ConnectionManager.connectionsPool.size();
    logger.debug("WORKING ConnectionManager.getPoolSize(). Pool size: [" + res + "].");
    return res;
   }

  /**
   * Метод возвращает ИСТИНА, если пул соединений инициализирован и его размер
   * больше нуля (т.е. в пуле есть хотя бы одно соединение). При этом переменная
   * useConnPool не принимается во внимание (т.е. возвращается реальное состояние
   * пула соединений, несмотря на то, используется ли пул соединений данным
   * экземпляром класса или нет).
   * @return boolean инициализирован пул соединений или нет
  */
  private boolean isPoolInitialized()
   {
    boolean res = false;
    if ((ConnectionManager.connectionsPool != null) && (!ConnectionManager.connectionsPool.isEmpty())) res = true;
    logger.debug("WORKING  ConnectionManager.isPoolInitialized(). Result: " + res);
    return res;
   }

  /**
   * Приватный метод для непосредственного выполнения действий по созданию
   * соединения с СУБД или для инициализации пула соединений. Если используется
   * пул соединений, то возвращает номер созданного соединения в пуле, если же
   * нет - возвращает 0. При использовании пула новые соединения добавляются в
   * пул до его заполнения - до достижения MAX_CONNECTIONS соединений в пуле.
   * После достижения этого кол-ва соединений метод возвращает ID последнего
   * соединения в пуле.
  */
  public synchronized void openConnection(/** ConnectionConfig config */) throws Exception
   {
    /**
    logger.debug("ENTERING ConnectionManager.openConnection().");
    // Пробуем загрузить драйвер для соединения с СУБД
    Class.forName(config.getDbDriverClass()).newInstance();
    logger.debug("Database driver [" + config.getDbDriverClass() + "] loaded successfully.");
    // Если пул соединений не инициализирован - инициализация
    if (!this.isPoolInitialized())
     {
      logger.debug("Connections pool not initialized yet. Initializing...");
      ConnectionManager.connectionsPool = new ArrayList<Connection>(MAX_CONNECTIONS);
     }
    else logger.debug("Connection pool already initialized.");
    // Если в пуле кол-во соединений меньше максимума - значит мы можем добавить новое соединение в пул
    if (connectionsPool.size() < MAX_CONNECTIONS)
     {
      logger.debug("Connection pool size OK [< " + ConnectionManager.MAX_CONNECTIONS + "]. Adding new connection...");
      if (config.getDbConnectionInfo() != null)
       ConnectionManager.connectionsPool.add(DriverManager.getConnection(config.getDbJdbcUrl(),
                                             Utils.getPropsFromString(config.getDbConnectionInfo())));
      else
       ConnectionManager.connectionsPool.add(DriverManager.getConnection(config.getDbJdbcUrl()));
     }
    else logger.debug("MAX pool size[] reached! Can't add connection!");
    logger.debug("LEAVING ConnectionManager.openConnection().");
    */
   }

  /**
   * 123
   * @throws Exception
   * @return int
  */
  /*
  public int connect(Properties props) throws Exception
   {
    logger.debug("ENTERING connect_pool(Properties props).");
    // Значение по умолчанию {-1} - пул уже заполнен и новое соединение не добавлено
    int conn_number = -1;
      logger.debug("Using connection pool - continue.");
      if (this.isPoolInitialized()) // <- ПУЛ ИНИЦИАЛИЗИРОВАН
       {
        logger.debug("Connection pool already initialized. Trying to add connection.");
        // Новое соединение в пул добавляем, только если позволяет размер пула
        if (this.getPoolSize() < this.MAX_CONNECTIONS)
         {
          logger.debug("Connection pool size < MAX, adding new connection.");
          openConnection(props);
          conn_number = this.getPoolSize() - 1;
          logger.debug("Get number of last connection [#" + conn_number + "].");
         }
        else
         logger.debug("Connection pool size = MAX, connection not added.");
       }
      else // <- ПУЛ НЕ ИНИЦИАЛИЗИРОВАН
       {
        logger.debug("Connection pool is not initialized. Initializing.");
        openConnection(props);
        conn_number = this.getPoolSize() - 1;
        logger.debug("Get number of last connection [#" + conn_number + "].");
       }
    logger.debug("LEAVING connect_pool(Properties props).");
    return conn_number;
   }
  */
  /**
   * Метод возвращает ИСТИНА, если соединение номер conn_number
   * существует в пуле соединений и пул соединений используется.
   * @param conn_number int номер соединения, наличие которого
   * мы хотим проверить в пуле.
   * @return boolean результат проверки наличия соединения в пуле.
  */
  /*
  public boolean isConnExist(int conn_number)
   {
    boolean res = false;
    logger.debug("ENETRING isConnExist(int conn_number).");
    if (this.isPoolInitialized() && (conn_number >= 0) && (conn_number <= this.getPoolSize()))
     res = true;
    logger.debug("[Result: " + res + "]");
    logger.debug("LEAVING isConnExist(int conn_number).");
    return res;
   }
  */

  /**
   * Метод возвращает объект СОЕДИНЕНИЕ (Connection) номер conn_number
   * из пула соединений. Если же пул не используется (useConnPool=false),
   * не инициализирован (isPoolInitialized()=false) или соединения под
   * этим номером в пуле нет (isConnExist()=false), то данный метод
   * возвращает значение null.
   * //@param conn_number int номер соединения, которое мы хотим
   * получить из пула.
   * @return Connection объект под номером conn_number из пула
   * соединений или null, в случае неудачи.
  */
  /*
  public Connection getConn(int conn_number)
   {
    logger.debug("ENTERING getConn(int conn_number).");
    Connection conn = null;
    if (this.isConnExist(conn_number))
     {
      conn = (Connection) ConnectionManager.connectionsPool.get(conn_number);
      logger.debug("Connection pool initialized and used.");
      logger.debug("Connection #" + conn_number + " exists in the pool.");
     }
    else
     logger.debug("Conn. pool not used or not initialized or conn #" +
                  conn_number + " not exists in the pool!");
    logger.debug("LEAVING getConn(int conn_number).");
    return conn;
   }
  */
  /**
   * Закрывает соединение с СУБД. Если используется пул соединений, то
   * закрывается и удаляется из пула последнее добавленное соединение (т.е.
   * соединение, имеющее наибольший (последний) порядковый номер). После
   * закрытия последнего (единственного, оставшегося в пуле соединения), пулу
   * присваивается значение null. В случае, если пул не инициализирован ( =
   * null), или в пуле нет соединений, метод не выполняет никаких действий. Если
   * же пул соединений не используется, то метод возвращает статическое
   * соединение, используемое данным объектом для работы с СУБД.
   *
   * @throws SQLException
  */
  public void close() throws SQLException
   {
    logger.debug("ENTERING close().");
      logger.debug("Using connection pool - closing all connections in the pool.");
      // Если пул соединений инициализирован - проходим
      // по всем соединениям, закрываем их и обнуляем ссылки
      if (this.isPoolInitialized())
       {
        Connection tmp_conn = null;
        logger.debug("Connection pool initialized [size=" + ConnectionManager.connectionsPool.size() + "].");
        for (int i = 0; i < ConnectionManager.connectionsPool.size(); i++)
         {
          logger.debug("Closing connection #" + i);
          // Извлекаем соединение из пула
          tmp_conn = (Connection) ConnectionManager.connectionsPool.get(i);
          // Закрываем соединение
          tmp_conn.close();
          // Обнуляем ссылку на соединение
          tmp_conn = null;
          logger.debug("Connection #" + i + " closed successfully.");
         }
        logger.debug("Clearing connection pool.");
        ConnectionManager.connectionsPool.clear();
        logger.debug("Clearing the pool completed successfully.");
       }
      else logger.debug("Connection pool not initialized - nothing to close!");
    logger.debug("LEAVING close().");
   }

  /***/
  public void close(int conn_number) throws SQLException
   {
    logger.debug("ENTERING close(int i).");
    logger.debug("Using connection pool. Closing connection #" + conn_number + " in pool.");

    logger.debug("LEAVING close(int i).");
   }

  /**
   Метод main предназначен только для тестирования класса ConnectionManager.
   @param args набор строковых параметров.
  */
  public static void main(String[] args)
   {
    Logger logger                   = Logger.getLogger(ConnectionManager.class.getName());
    PatternLayout patternLayout     = new PatternLayout(Consts.LOGGER_LOG_FORMAT_DEFAULT);
    ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
    logger.addAppender(consoleAppender);

    ConnectionManager conn = ConnectionManager.getInstance();

    logger.info("********** STARTING main() **********");

    Properties props = new Properties();

    //props.put(DBConsts.DB_TYPE, DBConsts.DBTYPE_INFORMIX);
    props.put(DBConsts.DB_HOST,   "10.1.19.30:1526");
    props.put(DBConsts.DB_SERVER, "edu");
    props.put(DBConsts.DB_NAME,     "flt");
    props.put(DBConsts.DB_USER,   "informix");
    props.put(DBConsts.DB_PWD,    "123456");

    //props.put(DBC.DB_TYPE, DBC.DBTYPE_DBF);
    //props.put(DBC.DB_NAME, "c:/temp/");
    try
     {
      //conn.connect(props);
      //conn.print_config();
      conn.close();
     }
    catch (Exception e)
     {logger.fatal("REASON: " + e.getMessage());}


    logger.info("********** FINISHING main(). **********");
   }

 }//--> Конец класса ConnectionManager.
