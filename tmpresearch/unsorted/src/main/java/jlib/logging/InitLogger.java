package jlib.logging;

import jlib.JLibConsts;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;
import org.apache.log4j.helpers.NullEnumeration;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * ������ ����� ��������� ������������� ���������� �������������� - ������� - �� ���������� log4j. ��� �������
 * ��������������� ������� ������������� ������������ ����� initLogger(String loggerName) - �.�. ������ �����
 * ���������� ��� ����� ����� ����������� ������ ���������������� ������ (�� ������ ���� ���� �� ��������).
 * ������������� ����� ��������� ������ ���������� �������� � �� ���������� �� IOException - ��� ���������
 * ���������. ���� ��� ������������� ������� �� ������� ��� ��� - ������ ����� ����� ������������ ������������
 * �������� ������ (InitLogger).
 * @author Gusev Dmitry
 * @version 2.0 (18.04.2011)
*/

public class InitLogger
 {
  
  /**
   * ������������� ����������-������� loggerName � ���������� �����������. ��� ������������� ����������� ���
   * ��������� - ���������� (� ������ isConsoleAppender=true) � �������� (������ ���� ������� �������� ��� �����).
   * �������� �������� ����� ��� DailyRollingFileAppender - ��������� ��������� ���� ����. � �������� ���� � �����
   * ���� ����������� ���������: ����������/������������� ���� � �����, ��� �����. �������� forced ��������� - ���� ��
   * ������������� ����������� ��������� ������� ������� (�������� ������) ��� ��� (�������� ����). ���� � �������
   * ������� ��� ���� ���������, � �������� forced==false, �� ������� �������� ��������� �� �����, ���� �� ���������
   * ����, � forced==true, �� ��������� ����� ������� � ��������� ������ � ������ �����������.
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param isConsoleAppender boolean ��������� (true) ��� ��� ���������� �������� � ������� �������.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
   * @param pattern String ������ ��� ������� �������. ���� �� ������ - ������������ ������ �� ��������� - LOG_FORMAT_DEFAULT.
   * @param forced boolean ��������� �� ��, ���������� �� ������������� ��������� ������� �������, ���� ��� ���
   * ���������� � ���������.
  */
  public static void initLogger(String loggerName, Level level, boolean isConsoleAppender, String fileName,
                                String pattern, boolean forced)
   {
    PatternLayout             patternLayout;
    DailyRollingFileAppender  fileAppender;
    String                    logName;

    // ���� ��� ����������������� ������� ����� - � �������� ����� ���������� ������������ �������� ������
    if (StringUtils.isBlank(loggerName)) {logName = InitLogger.class.getName();}
    else                                 {logName = loggerName;}

    // �������� ������ �� ������ � ��������� ������
    Logger logger = Logger.getLogger(logName);

    // todo: **** ��������� ����������� �������� � ��������� �� ����������� � �������� ��������. ****
    // todo: **** ��������, ������ ������ ������� ��������� � ������������ ����������. ****
    //logger.setAdditivity(false); // <- ������ 08.09.2009.... ����� ��� �����????? :)

    // ���� ������ �������� forced=true, �� ��������� ��� ���������.
    if (forced) {logger.removeAllAppenders();}

    // ������� ������ ���� ���������� ������� �������, ����� � ��������������. �������������� ���������
    // ����� ������ ���� ������������ ��� ������� ������� ��������� -> logger.getAdditivity() == true
    Enumeration e = logger.getAllAppenders();
    // ��������� ������� ������-������������
    NullEnumeration nulle = NullEnumeration.getInstance();

    // ���� ���������� ������ ���������� ����� ������� ������-������������ - ��������� ���� ���������
    if (e.equals(nulle))
     {
      // ������ ������� �������
      if (!StringUtils.isBlank(pattern)) {patternLayout = new PatternLayout(pattern);}
      else                               {patternLayout = new PatternLayout(JLibConsts.LOGGER_PATTERN_DEFAULT);}

      // ��������� ������ ������� �������
      if (level != null) {logger.setLevel(level);} else {logger.setLevel(JLibConsts.LOG_LEVEL_DEFAULT);}

      // ���� ������� ����� �������� ����������� ��������� - ������� � ��������� ���
      if (isConsoleAppender)
       {
        // ���������� �������� - ��������
        ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
        // ���������� � ������� ����������� ���������
        logger.addAppender(consoleAppender);
        logger.info("INITIALIZING LOGGER [" + logger.getName() + "]. Console appender added.");
      }

      // �������� �������� (����� � ����) - �������� ���������(���� ������� ��� �����!)
      if (!StringUtils.isBlank(fileName))
       {
        String logFilePath = null;
        try
         {
          logFilePath = FSUtils.fixFPath(fileName);
          // ���� � ���� � ����� ���� ���� ������ / - ���� ���� �������� �������, ������� ���� ��������� ��
          // �������������. ���� �� ������ ������� ��� - ���� �� �������� ����� �������� (��� ������ ��� �����).
          int delimPos = logFilePath.lastIndexOf("/");
          if (delimPos > -1)
           {
            logger.debug("Delimiter symbol [/] exists. Processing log catalog.");
            // ������ ������� ������� (������������� ��� ���������� ����) ��� ����� �������.
            // (������� - ���, ��� ��������� ����� �� ������ ������� ������� /)
            String logCatalogPath = logFilePath.substring(0, delimPos);
            //logger.info("** " + logFilePath + " ** " + logCatalogPath);
            // ���� ���������� ���� � �������� ���� (�� null, � ������ ������), �� � ������������ ������!
            if (logCatalogPath.length() > 0)
             {
              logger.debug("Checking log catalog [" + logCatalogPath + "].");
              // �������� � (���� �����) �������� �������� ��� ������� �����
              File logCatalog = new File(logCatalogPath);
              if (!logCatalog.exists())
               {
                logger.debug("Log catalog [" + logCatalog.getAbsolutePath() + "] doesn't exists. Creating.");
                // ������� �������������� �������. ���� �������� ����������� �������� - ������ (� ���).
                if (!logCatalog.mkdirs()) {logger.error("Can't create log catalog [" + logCatalog.getAbsolutePath() + "]!");}
               }
              else
               {
                logger.debug("Log catalog [" + logCatalog.getAbsolutePath() + "] exists. Processing.");
                if (!logCatalog.isDirectory())
                 {
                  logger.debug("Log catalog exists but not a folder. Trying to create a folder.");
                  boolean result = logCatalog.mkdirs(); // �������� ������� ������ �������
                  // ���� �������� �������� �� ������� - ����� ���� � ���� � ������� ��������
                  if (!result)
                   {
                    logger.debug("Catalog creating failed. Writing log to current folder.");
                    logFilePath = logFilePath.substring(delimPos + 1);
                   }
                 }
                else {logger.debug("Log catalog exists and is folder. All OK!");}
               }
             } // ����� ������ ��������� ��������� ��������
            // ���� ����������� ������� ���� ���� - ������� �� ����
            else {logger.debug("Catalog path is empty!");}
           } // ����� ������ ��������� �������� �������
          // ���� ������� ����������� ���� - ��� ������ ��� �����
          else {logger.debug("Delimiter symbol [/] doesn't exists.");}
          // ��������������� �������� ��������� ��������� ��� �������� ����������
          fileAppender = new DailyRollingFileAppender(patternLayout, logFilePath, JLibConsts.LOGGER_FAPPENDER_DATE_PATTERN);
          // ���������� ��������� ���������
          logger.addAppender(fileAppender);
          logger.info("INITIALIZING LOGGER [" + logger.getName() + "]. File appender [" + fileName + "] added.");
         }
        catch (IOException ioe)
         {logger.error("Can't create file appender [" + logFilePath + "]! Reason: [" + ioe.getMessage() + "].");}
       }// ����� ��������� ������ � �������� ������ ����� (��� ���������)
      // ���� �� ��� ����� ����� - �������� �������� �� �����
      //else {logger.debug("No file appender needed.");} // <- ����������� ��������� ����� � �� ���� ��� ������ ���� �������!
      
     }
    // ���� ���������� ������ ���������� �� ���� - ������ ��� ���������������
    else {logger.warn("LOGGER [" + logger.getName() + "] ALREADY INITIALIZED. NOTHING TO DO!");}
   }
  
  /**
   * ������������� ����������-������� loggerName � ���������� �����������. � ������ ������ ���������� ��������
   * ������ ����������� (���� � ������� ��� ������ ����������, �������������� ��� �����, ��� �������� forced=true).
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
   * @param pattern String ������ ��� ������� �������. ���� �� ������ - ������������ ������ �� ��������� - LOG_FORMAT_DEFAULT.
   * @param forced boolean ��������� �� ��, ���������� �� ������������� ��������� ������� �������, ���� ��� ���
   * ���������� � ���������.
  */
  public static void initLogger(String loggerName, Level level, String fileName, String pattern, boolean forced)
  {InitLogger.initLogger(loggerName, level, true, fileName, pattern, forced);}

  /**
   * ������������� ����������-������� loggerName � ���������� �����������.
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
   * @param pattern String ������ ��� ������� �������. ���� �� ������ - ������������ ������ �� ��������� - LOG_FORMAT_DEFAULT.
  */
  public static void initLogger(String loggerName, Level level, String fileName, String pattern)
   {InitLogger.initLogger(loggerName, level, fileName, pattern, false);}

  /**
   * ������������� ����������-������� loggerName � ���������� �����������. � �������� ������� ������� ����
   * ������������ ������ �� ���������.
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
  */
  public static void initLogger(String loggerName, Level level, String fileName)
   {InitLogger.initLogger(loggerName, level, fileName, null);}

  /**
   * ������������� ����������-������� loggerName � ���������� �����������. � �������� ������� ������� ���� ������������
   * ������ �� ���������. ����� �� ����������� �������� �������� (��� ������ � ����).
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
  */
  public static void initLogger(String loggerName, Level level) {InitLogger.initLogger(loggerName, level, null);}

  /**
   * ������������� ����������-������� loggerName � ���������� �����������. � �������� ������� ������� ���� ������������
   * ������ �� ���������. ����� �� ����������� �������� �������� (��� ������ � ����). ������� ������� ������� - �����
   * ������������ �������� �� ���������.
   * @param loggerName String ������������ ����������������� �������. ���� ��� ������������� ������� �� ������� ���
   * ��� - ������ ����� ����� ������������ ������������ �������� ������ (InitLogger).
  */
  public static void initLogger(String loggerName) {InitLogger.initLogger(loggerName, null);}

  /**
   * ������ ����� �������������� ���������� �� ��������� ������ ��������, ����� ������� �������� � �������� ���������.
   * @param loggersList String[] ������ ���� �������� ��� �������������.
  */
  public static void initLoggers(String[] loggersList)
   {
    if ((loggersList != null) && (loggersList.length > 0))
     {for (String loggerName : loggersList) {if (!StringUtils.isBlank(loggerName)) {InitLogger.initLogger(loggerName);}}}
   }

  /**
   * ������ ����� �������������� ���������� ���������� ������ ��������, ����� ������� �������� � �������� ���������.
   * @param loggersList String[] ������ ���� �������� ��� �������������.
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param isConsoleAppender boolean ��������� (true) ��� ��� ���������� �������� � ������� �������.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
   * @param pattern String ������ ��� ������� �������. ���� �� ������ - ������������ ������ �� ��������� - LOG_FORMAT_DEFAULT.
   * @param forced boolean ��������� �� ��, ���������� �� ������������� ��������� ������� �������, ���� ��� ���
   * ���������� � ���������.
  */
  public static void initLoggers(String[] loggersList, Level level, boolean isConsoleAppender, String fileName,
   String pattern, boolean forced)
   {
    if ((loggersList != null) && (loggersList.length > 0))
     {
      for (String loggerName : loggersList)
       {if (!StringUtils.isBlank(loggerName)) {InitLogger.initLogger(loggerName, level, isConsoleAppender, fileName, pattern, forced);}}
     }
   }

  /**
   * ������ ����� �������������� ���������� ���������� ������ ��������, ����� ������� �������� � �������� ���������.
   * @param loggersList String[] ������ ���� �������� ��� �������������.
   * @param level Level ������� ������� �������. ���� �� ������ - ������������ ������� �� ��������� - LOG_LEVEL_DEFAULT.
   * @param fileName String ��� �����-�������. ���� ������� - ����������� �������� ��������, ���� �� ��� - �� �����������.
   * @param pattern String ������ ��� ������� �������. ���� �� ������ - ������������ ������ �� ��������� - LOG_FORMAT_DEFAULT.
   * @param forced boolean ��������� �� ��, ���������� �� ������������� ��������� ������� �������, ���� ��� ���
   * ���������� � ���������.
  */
  public static void initLoggers(String[] loggersList, Level level, String fileName, String pattern, boolean forced)
   {
    if ((loggersList != null) && (loggersList.length > 0))
     {
      for (String loggerName : loggersList)
       {if (!StringUtils.isBlank(loggerName)) {InitLogger.initLogger(loggerName, level, fileName, pattern, forced);}}
     }
   }

  /**
   * ������ ����� ������������ ������ ��� ������������ ������ InitLogger.
   * @param args String[] ��������� ������ main.
  */
  public static void main(String[] args)
   {
    String loggerName = "fff";
    InitLogger.initLogger(loggerName, Level.INFO, "/ggg/fff.log/");
    Logger logger = Logger.getLogger(loggerName);
    logger.info("Hello!");
   }

 }