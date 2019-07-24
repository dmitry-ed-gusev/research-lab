package jlib.system.exec.mysql;

import jlib.system.exec.WindowsExec;
import jlib.system.exec.WindowsExecResult;
import org.apache.log4j.Logger;

/**
 * Данный класс предназначен для работы с СУБД MySQL, установленной локально. Работа
 * подразумевает запуск/останов сервиса "mysql", инсталляцию/удаление данного сервиса и
 * т.п. операции. Предполагается работа данного модуля в ОС семейства Windows XP/Vista.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 25.02.2009)
*/

public class MysqlEngine
 {
  // todo: сделать команду инсталляции сервиса mysql
  
  /** Разрешен ли в данной ОС запуск внешних команд. */
  private boolean isOSVersionCorrect = false;

  public MysqlEngine() {this.isOSVersionCorrect = new WindowsExec().isOSVersionCorrect();}

  /**
   * Проверка - инсталлирован ли в данной системе сервис СУБД Mysql - сервис называется 'mysql'.
   * @return boolean ИСТИНА/ЛОЖЬ - результат выполнения команды.
  */
  public boolean isMysqlServiceInstalled()
   {
    boolean result = false;
    // Если версия ОС подходит, то выполняем команду
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_QUERY_SERVICE);
      if ((execResult != null) && (execResult.getOutput().indexOf(MysqlEngineConsts.SC_ERROR) < 0)) result = true;
     }
    return result;
   }

  /**
   * Проверка - запущен ли сервис mysql в данной системе.
   * @return boolean ИСТИНА/ЛОЖЬ - результат выполнения команды.
  */
  public boolean isMysqlServiceRunning()
   {
    boolean result = false;
    // Если версия ОС подходит, то выполняем команду
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_QUERY_SERVICE);
      if ((execResult != null) && (execResult.getOutput().indexOf(MysqlEngineConsts.SC_SERVICE_STATUS_RUNNING) >= 0)) result = true;
     }
    return result;
   }

  /**
   * Запуск сервиса mysql.
   * @return boolean ИСТИНА/ЛОЖЬ - результат выполнения команды.
  */
  public boolean startMysqlService()
   {
    boolean result = false;
    // Если версия ОС подходит, то выполняем команду
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_START_SERVICE);
      if ((execResult != null) && (execResult.getExitCode() == 0) && (execResult.getError() == null)) result = true;
     }
    return result;
   }

  /**
   * Останов сервиса mysql.
   * @return boolean ИСТИНА/ЛОЖЬ - результат выполнения команды.
  */
  public boolean stopMysqlService()
   {
    boolean result = false;
    // Если версия ОС подходит, то выполняем команду
    if (this.isOSVersionCorrect)
     {
      WindowsExecResult execResult = new WindowsExec().execute(MysqlEngineConsts.MYSQL_STOP_SERVICE);
      if ((execResult != null) && (execResult.getExitCode() == 0) && (execResult.getError() == null)) result = true;
     }
    return result;
   }
  
  /**
   * Метод для тестирования класса.
   * @param args String[] параметры данного метода.
  */
  public static void main(String[] args)
   {
    Logger logger = Logger.getLogger("updater");
    MysqlEngine mysql = new MysqlEngine();
    logger.info(mysql.isMysqlServiceInstalled());
    logger.info(mysql.isMysqlServiceRunning());
   }

 }