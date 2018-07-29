package jlib.system.exec.mysql;

/**
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 25.02.2009)
*/

public class MysqlEngineConsts
 {
  /***/
  public static final String MYSQL_SERVICE_NAME  = "mysql";
  /***/
  public static final String MYSQL_START_SERVICE = "net start " + MYSQL_SERVICE_NAME;
  /***/
  public static final String MYSQL_STOP_SERVICE  = "net stop " + MYSQL_SERVICE_NAME;
  /***/
  public static final String MYSQL_QUERY_SERVICE = "sc query " + MYSQL_SERVICE_NAME;

  /***/
  public static final String SC_ERROR                  = "FAILED 1060";
  /***/
  public static final String SC_SERVICE_STATUS_RUNNING = "RUNNING";
  /***/
  public static final String SC_SERVICE_STATUS_STOPPED = "STOPPED";

  /**
   * ¬рем€ в миллисекундах, которое должно пройти между стартом службы Mysql и первым обращением к Ѕƒ. Ќа это врем€
   * поток обновлени€ должен быть приостановлен. Ќа машинах разной производительности врем€ варьируетс€ - среднее
   * дл€ всех значение - 5-10 секунд (5000-10000 миллисекунд).  
  */
  public static final int    MYSQL_WAIT_BEFORE_START   = 5000; // <- 5 секунд
  /**
   * ¬рем€ в миллисекундах, которое должно пройти после останова службы Mysql дл€ того, чтобы убедитьс€, что
   * служба действительно остановлена. Ќа это врем€ поток обновлени€ должен быть приостановлен. Ќа машинах
   * разной производительности врем€ варьируетс€ - среднее дл€ всех значение - 5-10 секунд (5000-10000 миллисекунд).
  */
  public static final int    MYSQL_WAIT_BEFORE_STOP    = 5000; // <- 5 секунд
 }