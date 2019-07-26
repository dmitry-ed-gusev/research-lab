package jlib;

import dgusev.auth.Password;
import dgusev.io.MyIOUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 04.03.2009)
 */
public class MAIN
 {
  /** Компонент-логгер данного класса. */
  private static Logger logger = Logger.getLogger(MAIN.class.getName());

  /** Стандартное имя для файла пароля. */
  private static final String DEFAULT_PASS_FILE_NAME          = "password";
  /** Стандартное расширение для файла пароля. */
  private static final String DEFAULT_PASS_FILE_EXTENSION     = "data"; 

  /** Опция командной строки: создать файл с паролем. Пароль - аргумент данной опции. */
  private static final String OPTION_CREATE_PASS_FILE = "createPassFile";
  /** Опция командной строки: прочитать пароль из файла. */
  private static final String OPTION_READ_PASS_FILE   = "readPassFile";
  /** Опция командной строки: каталог, где должен находиться файл пароля (для чтения или создания). */
  private static final String OPTION_PASS_FILE_DIR    = "passFileDir";
  /** Опция командной строки: имя для файла пароля (для чтения или создания). */
  private static final String OPTION_PASS_FILE_NAME   = "passFileName";
  /** Опция командной строки: показать экран помощи. */
  private static final String OPTION_HELP             = "help";

  /** Глобальный список опций (необходим для генерации хелпа и разбора командной строки). */
  private static Options options = null;

  /**
   * Метод генерирует командную строку для последующей обработки.
   * @param args String[] набор параметров командной строки.
   * @return CommandLine сгенерированная командная строка со всеми параметрами.
  */
  @SuppressWarnings({"AccessStaticViaInstance"})
  private static CommandLine buildCmdLine(String[] args)
   {
    // Создание файла пароля
    Option createFile   = OptionBuilder.withArgName("password")
                                       .hasArg()
                                       .withDescription("create the password file with specified password" )
                                       .create(OPTION_CREATE_PASS_FILE); // <- опция с аргументами
    // Путь к каталогу с файлом пароля
    Option passFileDir  = OptionBuilder.withArgName("path")
                                       .hasArg()
                                       .withDescription("catalog with password file")
                                       .create(OPTION_PASS_FILE_DIR);
    // Имя файла пароля
    Option passFileName = OptionBuilder.withArgName("filename")
                                       .hasArg()
                                       .withDescription("password file name")
                                       .create(OPTION_PASS_FILE_NAME);
    // Чтение файла пароля
    Option readFile     = new Option(OPTION_READ_PASS_FILE, "read the specified password file");
    // Опция показа экрана помощи
    Option help         = new Option(OPTION_HELP, "print this help message"); // <- Логическая опция
    
    options = new Options();

    options.addOption(createFile);
    options.addOption(readFile);
    options.addOption(passFileDir);
    options.addOption(passFileName);
    options.addOption(help);

    // Разбор командной строки
    CommandLineParser gnuParser = new GnuParser();
    CommandLine cmdLine = null;
    try {cmdLine = gnuParser.parse(options, args);}
    catch (ParseException e)
     {
      //System.out.println("hh");
      logger.error(e.getMessage());
     }
    return cmdLine;
   }

  @SuppressWarnings({"AccessStaticViaInstance"})
  public static void main(String[] args)
   {
    logger.debug("-> " + SystemUtils.getUserDir().getAbsolutePath());

    // Получим разобранную командную строку
    CommandLine cmdLine = MAIN.buildCmdLine(args);

    // --- Обработка командной строки ---
    // Печать экрана помощи
    if (cmdLine.hasOption(OPTION_HELP))
     {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar jlib", options, true);
     }
    
    // Создание файла пароля
    else if (cmdLine.hasOption(OPTION_CREATE_PASS_FILE))
     {
      // Читаем указанный пароль - если его нет, то ничего и не делаем
      String password = cmdLine.getOptionValue(OPTION_CREATE_PASS_FILE);
      if (!StringUtils.isBlank(password))
       {
        String passFileDir;
        // Читаем каталог, в котором нужно создать файл пароля. Если каталог указан (не пусто значение) - берем его.
        if ((cmdLine.hasOption(OPTION_PASS_FILE_DIR)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR))))
         {passFileDir = cmdLine.getOptionValue(OPTION_PASS_FILE_DIR);}
        // Если же каталог не указан - файл создается в текущем каталоге
        else {passFileDir = SystemUtils.getUserDir().getAbsolutePath();}
        
        String passFileName; // <- имя файла пароля
        String passFileExt;  // <- расширение файла пароля
        // Читаем имя файла пароля. Если имя указано (не пусто значение) - берем его.
        if ((cmdLine.hasOption(OPTION_PASS_FILE_NAME)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME))))
         {
          String str = cmdLine.getOptionValue(OPTION_PASS_FILE_NAME);
          // имя файла - то, что находится слева от самого правого знака "."
          passFileName = str.substring(0, str.lastIndexOf("."));
          // Расширение - то, что находится справа от самого правого знака "."
          passFileExt = str.substring(str.lastIndexOf(".") + 1);
          logger.debug("full: " + str + "| name: " + passFileName + "| ext: " + passFileExt);
         }
        // Если же имя не указано - имя файла формируется автоматически (по умолчанию)
        else {passFileName = DEFAULT_PASS_FILE_NAME; passFileExt = DEFAULT_PASS_FILE_EXTENSION;}

        // Непосредственно запись файла пароля на диск
        try
         {
          // Создаем экземпляр класса "пароль"
          Password pass = new Password(password);
          // Записываем созданный экземпляр на диск (сериализуем)
          MyIOUtils.serializeObject(pass, passFileDir, passFileName, passFileExt, false);
         }
        catch (IOException e) {logger.error(e.getMessage());}
       }
      else {logger.warn("Password not specified!");}
     }

    // Чтение файла пароля
    else if (cmdLine.hasOption(OPTION_READ_PASS_FILE))
     {
      String passFileDir;
      String passFileName;
      // Читаем каталог, в котором нужно искать файл пароля. Если каталог указан (не пусто значение) - берем его.
      if ((cmdLine.hasOption(OPTION_PASS_FILE_DIR)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR))))
       {passFileDir = MyIOUtils.fixFPath(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR), true);}
      // Если же каталог не указан - файл ищется в текущем каталоге
      else {passFileDir = MyIOUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), true);}
      // Читаем имя файла пароля. Если имя указано (не пусто значение) - берем его.
      if ((cmdLine.hasOption(OPTION_PASS_FILE_NAME)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME))))
       {passFileName = MyIOUtils.fixFPath(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME), false);}
      // Если имя файла пароля не указано - ищем файл пароля с именем по умолчанию
      else {passFileName = DEFAULT_PASS_FILE_NAME + "." + DEFAULT_PASS_FILE_EXTENSION;}
      // Непосредственно читаем файл пароля
      try
       {
        Password password = (Password) MyIOUtils.deserializeObject(passFileDir + passFileName, false, false);
        System.out.println("PASSWORD (from file [" + passFileDir + passFileName + "]): " + password.getPassword());
       }
      catch (ClassNotFoundException e) {logger.error(e.getMessage());}
      catch (IOException e) {logger.error(e.getMessage());}
     }

    // Если не подошел ни один из вариантов - также показываем экран помощи
    else
     {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar jlib", options, true);
     }

   } // END OF MAIN METHOD
  
 }