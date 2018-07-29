package spammer;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 22.12.10)
*/

public class Test
 {
  public int i;

  /**
   * Метод для тестирования.
   * @param args String[] параметры метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger(Defaults.LOGGER_NAME);
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
   }


 }