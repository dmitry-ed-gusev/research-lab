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
 * Данный класс реализует инициализацию компонента журналирования - логгера - из библиотеки log4j. Для отладки
 * вспомогательных классов рекомендуется использовать метод initLogger(String loggerName) - т.к. логгер всего
 * приложения все равно будет перекрывать логгер вспомогательного класса (он должен быть выше по иерархии).
 * Рекомендуемый метод добавляет только консольный аппендер и не генерирует ИС IOException - нет файлового
 * аппендера. Если при инициализации логгера не указать его имя - вместо имени будет использовано наименование
 * текущего класса (InitLogger).
 * @author Gusev Dmitry
 * @version 2.0 (18.04.2011)
*/

public class InitLogger
 {
  
  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами. При инициализации добавляются два
   * аппендера - консольный (в случае isConsoleAppender=true) и файловый (только если указано непустое имя файла).
   * Файловый аппендер имеет тип DailyRollingFileAppender - ежедневно сменяемый файл лога. В качестве пути к файлу
   * логу допускается указывать: абсолютный/относительный пути к файлу, имя файла. Параметр forced указывает - надо ли
   * принудительно пересоздать аппендеры данного логгера (значение ИСТИНА) или нет (значение ЛОЖЬ). Если у данного
   * логгера уже есть аппендеры, а параметр forced==false, то никаких действий выполнено не будет, если же аппендеры
   * есть, а forced==true, то аппендеры будут удалены и добавлены заново с новыми параметрами.
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param isConsoleAppender boolean добавлять (true) или нет консольный аппендер к данному логгеру.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
   * @param pattern String шаблон для записей журнала. Если не указан - используется шаблон по умолчанию - LOG_FORMAT_DEFAULT.
   * @param forced boolean указывает на то, необходимо ли пересоздавать аппендеры данного логгера, если они уже
   * существуют и добавлены.
  */
  public static void initLogger(String loggerName, Level level, boolean isConsoleAppender, String fileName,
                                String pattern, boolean forced)
   {
    PatternLayout             patternLayout;
    DailyRollingFileAppender  fileAppender;
    String                    logName;

    // Если имя инициализируемого логгера пусто - в качестве имени используем наименование текущего класса
    if (StringUtils.isBlank(loggerName)) {logName = InitLogger.class.getName();}
    else                                 {logName = loggerName;}

    // Получаем ссылку на логгер с указанным именем
    Logger logger = Logger.getLogger(logName);

    // todo: **** Запрещаем наследовать свойства и аппендеры от вышестоящих в иерархии логгеров. ****
    // todo: **** Возможно, данная строка вызовет изменения в логгировании приложения. ****
    //logger.setAdditivity(false); // <- убрана 08.09.2009.... может она нужна????? :)

    // Если указан параметр forced=true, то сбрасывам все аппендеры.
    if (forced) {logger.removeAllAppenders();}

    // Получим список всех аппендеров данного логгера, своих и унаследованных. Унаследованные аппендеры
    // будут только если наследование для данного логгера разрешено -> logger.getAdditivity() == true
    Enumeration e = logger.getAllAppenders();
    // Экземпляр пустого списка-перечисления
    NullEnumeration nulle = NullEnumeration.getInstance();

    // Если полученный список аппендеров равен пустому списку-перечислению - добавляем свои аппендеры
    if (e.equals(nulle))
     {
      // Шаблон ведения журнала
      if (!StringUtils.isBlank(pattern)) {patternLayout = new PatternLayout(pattern);}
      else                               {patternLayout = new PatternLayout(JLibConsts.LOGGER_PATTERN_DEFAULT);}

      // Установка уровня ведения журнала
      if (level != null) {logger.setLevel(level);} else {logger.setLevel(JLibConsts.LOG_LEVEL_DEFAULT);}

      // Если указана опция создания консольного аппендера - создаем и добавляем его
      if (isConsoleAppender)
       {
        // Консольный аппендер - создание
        ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
        // Добавление к логгеру консольного аппендера
        logger.addAppender(consoleAppender);
        logger.info("INITIALIZING LOGGER [" + logger.getName() + "]. Console appender added.");
      }

      // Файловый аппендер (вывод в файл) - создание аппендера(если указано имя файла!)
      if (!StringUtils.isBlank(fileName))
       {
        String logFilePath = null;
        try
         {
          logFilePath = FSUtils.fixFPath(fileName);
          // Если в пути к файлу лога есть символ / - этот путь содержит каталог, который надо проверить на
          // существование. Если же такого символа нет - путь не содержит имени каталога (это просто имя файла).
          int delimPos = logFilePath.lastIndexOf("/");
          if (delimPos > -1)
           {
            logger.debug("Delimiter symbol [/] exists. Processing log catalog.");
            // Теперь получим каталог (относительный или абсолютный путь) для файла журнала.
            // (каталог - все, что находится слева от самого правого символа /)
            String logCatalogPath = logFilePath.substring(0, delimPos);
            //logger.info("** " + logFilePath + " ** " + logCatalogPath);
            // Если полученный путь к каталогу пуст (не null, а пустая строка), то и обрабатывать нечего!
            if (logCatalogPath.length() > 0)
             {
              logger.debug("Checking log catalog [" + logCatalogPath + "].");
              // Проверка и (если нужно) создание каталога для ведения логов
              File logCatalog = new File(logCatalogPath);
              if (!logCatalog.exists())
               {
                logger.debug("Log catalog [" + logCatalog.getAbsolutePath() + "] doesn't exists. Creating.");
                // Создаем несуществующий каталог. если создание завершилось неудачей - ошибка (в лог).
                if (!logCatalog.mkdirs()) {logger.error("Can't create log catalog [" + logCatalog.getAbsolutePath() + "]!");}
               }
              else
               {
                logger.debug("Log catalog [" + logCatalog.getAbsolutePath() + "] exists. Processing.");
                if (!logCatalog.isDirectory())
                 {
                  logger.debug("Log catalog exists but not a folder. Trying to create a folder.");
                  boolean result = logCatalog.mkdirs(); // пытаемся создать нужный каталог
                  // Если создание каталога не удалось - пишем логи в файл в текущем каталоге
                  if (!result)
                   {
                    logger.debug("Catalog creating failed. Writing log to current folder.");
                    logFilePath = logFilePath.substring(delimPos + 1);
                   }
                 }
                else {logger.debug("Log catalog exists and is folder. All OK!");}
               }
             } // конец секции обработки НЕПУСТОГО каталога
            // Если вычисленный каталог лога пуст - сообщим об этом
            else {logger.debug("Catalog path is empty!");}
           } // конец секции обработки каталога логгера
          // Если символа разделителя нету - это просто имя файла
          else {logger.debug("Delimiter symbol [/] doesn't exists.");}
          // Непосредственно создание файлового аппендера для логгеров приложения
          fileAppender = new DailyRollingFileAppender(patternLayout, logFilePath, JLibConsts.LOGGER_FAPPENDER_DATE_PATTERN);
          // Добавление файлового аппендера
          logger.addAppender(fileAppender);
          logger.info("INITIALIZING LOGGER [" + logger.getName() + "]. File appender [" + fileName + "] added.");
         }
        catch (IOException ioe)
         {logger.error("Can't create file appender [" + logFilePath + "]! Reason: [" + ioe.getMessage() + "].");}
       }// конец обработки секции с непустам именем файла (для аппендера)
      // Если же имя файла пусто - файловый аппендер не нужен
      //else {logger.debug("No file appender needed.");} // <- консольного аппендера может и не быть для вывода этой строчки!
      
     }
    // Если полученный список аппендеров не пуст - логгер уже инициализирован
    else {logger.warn("LOGGER [" + logger.getName() + "] ALREADY INITIALIZED. NOTHING TO DO!");}
   }
  
  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами. В данном методе консольный аппендер
   * всегда добавляется (если у логгера нет других аппендеров, унаследованных или своих, или параметр forced=true).
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
   * @param pattern String шаблон для записей журнала. Если не указан - используется шаблон по умолчанию - LOG_FORMAT_DEFAULT.
   * @param forced boolean указывает на то, необходимо ли пересоздавать аппендеры данного логгера, если они уже
   * существуют и добавлены.
  */
  public static void initLogger(String loggerName, Level level, String fileName, String pattern, boolean forced)
  {InitLogger.initLogger(loggerName, level, true, fileName, pattern, forced);}

  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами.
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
   * @param pattern String шаблон для записей журнала. Если не указан - используется шаблон по умолчанию - LOG_FORMAT_DEFAULT.
  */
  public static void initLogger(String loggerName, Level level, String fileName, String pattern)
   {InitLogger.initLogger(loggerName, level, fileName, pattern, false);}

  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами. В качестве шаблона ведения лога
   * используется шаблон по умолчанию.
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
  */
  public static void initLogger(String loggerName, Level level, String fileName)
   {InitLogger.initLogger(loggerName, level, fileName, null);}

  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами. В качестве шаблона ведения лога используется
   * шаблон по умолчанию. Также не добавляется файловый аппендер (нет записи в файл).
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
  */
  public static void initLogger(String loggerName, Level level) {InitLogger.initLogger(loggerName, level, null);}

  /**
   * Инициализация компонента-логгера loggerName с указанными параметрами. В качестве шаблона ведения лога используется
   * шаблон по умолчанию. Также не добавляется файловый аппендер (нет записи в файл). Уровень ведения журнала - также
   * используется значение по умолчанию.
   * @param loggerName String наименование инициализируемого логгера. Если при инициализации логгера не указать его
   * имя - вместо имени будет использовано наименование текущего класса (InitLogger).
  */
  public static void initLogger(String loggerName) {InitLogger.initLogger(loggerName, null);}

  /**
   * Данный метод инициализирует значениями по умолчанию список логгеров, имена которых переданы в качестве параметра.
   * @param loggersList String[] список имен логгеров для инициализации.
  */
  public static void initLoggers(String[] loggersList)
   {
    if ((loggersList != null) && (loggersList.length > 0))
     {for (String loggerName : loggersList) {if (!StringUtils.isBlank(loggerName)) {InitLogger.initLogger(loggerName);}}}
   }

  /**
   * Данный метод инициализирует указанными значениями список логгеров, имена которых переданы в качестве параметра.
   * @param loggersList String[] список имен логгеров для инициализации.
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param isConsoleAppender boolean добавлять (true) или нет консольный аппендер к данному логгеру.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
   * @param pattern String шаблон для записей журнала. Если не указан - используется шаблон по умолчанию - LOG_FORMAT_DEFAULT.
   * @param forced boolean указывает на то, необходимо ли пересоздавать аппендеры данного логгера, если они уже
   * существуют и добавлены.
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
   * Данный метод инициализирует указанными значениями список логгеров, имена которых переданы в качестве параметра.
   * @param loggersList String[] список имен логгеров для инициализации.
   * @param level Level уровень ведения журнала. Если не указан - используется уровень по умолчанию - LOG_LEVEL_DEFAULT.
   * @param fileName String имя файла-журнала. Если указано - добавляется файловый аппендер, если же нет - не добавляется.
   * @param pattern String шаблон для записей журнала. Если не указан - используется шаблон по умолчанию - LOG_FORMAT_DEFAULT.
   * @param forced boolean указывает на то, необходимо ли пересоздавать аппендеры данного логгера, если они уже
   * существуют и добавлены.
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
   * Данный метод предназначен только для тестирования класса InitLogger.
   * @param args String[] параметры метода main.
  */
  public static void main(String[] args)
   {
    String loggerName = "fff";
    InitLogger.initLogger(loggerName, Level.INFO, "/ggg/fff.log/");
    Logger logger = Logger.getLogger(loggerName);
    logger.info("Hello!");
   }

 }