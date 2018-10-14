package jdb.monitoring;

import org.apache.log4j.Logger;

/**
 * Эталонная реализация интерфейса процесса-монитора (DBProcessingMonitor) для модулей обработки данных. Данная
 * реализация реализует вывод в журнал в отладочном режиме сообщений монитора процессов. Для журналирования используется
 * модуль log4j. Для вывода сообщений в журнал используется уровень ведения журнала INFO.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 09.03.2010)
*/

public class DBTestMonitor implements DBProcessingMonitor
 {
  /** Компонент-логгер данного класса. */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public void processProgress(int progress)
   {logger.info("DBTESTMONITOR -> processProgress -> " + progress);}

  @Override
  public void processMessage(String message)
   {logger.info("DBTESTMONITOR -> processMessage -> " + message);}

  @Override
  public void processDebugInfo(String debugInfo)
  {logger.info("DBTESTMONITOR -> processDebugInfo -> " + debugInfo);}
  
 }