package jlib;

import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 10.03.2009)
*/

public class Test
 {
  
  public static void main(String[] args)
   {
    InitLogger.initLogger("jlib");
    Logger logger = Logger.getLogger("jlib");
   }

 }