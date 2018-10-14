package mass_email_sender.spammer.config;

import mass_email_sender.spammer.Defaults;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Класс конфигурации модуля-майлера (Mailer). Позволяет инициализировать свои поля значениями из командной строки.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.12.2010)
*/
@SuppressWarnings({"AccessStaticViaInstance"})
public class MailerConfig extends BaseMailerConfig
 {
  /** Поле, расширяющее список полей суперкласса - показывать или нет хелп к приложению. */
  private boolean showHelp       = false;
  /** Поле, расширяющее список полей суперкласса - показать версию системы. */
  private boolean showVersion    = false;
  /**
   * Поле, расширяющее список полей суперкласса - набор опций командной строки. Поле необходимо для
   * того, чтобы можно было показать экран хелпа с описанием всех опций.
  */
  private Options cmdLineOptions = null;

  public boolean isShowHelp()                              {return showHelp;}
  public void    setShowHelp(boolean showHelp)             {this.showHelp = showHelp;}
  public Options getCmdLineOptions()                       {return cmdLineOptions;}
  public void    setCmdLineOptions(Options cmdLineOptions) {this.cmdLineOptions = cmdLineOptions;}
  public boolean isShowVersion()                           {return showVersion;}
  public void    setShowVersion(boolean showVersion)       {this.showVersion = showVersion;}

  /**
   * Вспомогательный метод данного класса - строит и возвращает список опций командной строки данного
   * приложения. Этот список используется для генерации экрана помощи и для разбора параметров переданной
   * командной строки.
   * @return Options построенный набор опций командной строки.
  */
  private Options buildCmdLineOptions()
   {
    // Опция без аргумента (просто вкл/выкл) - показать экран хелпа
    Option showHelp     = new Option(Defaults.CMDLINE_SHOW_HELP, "show this help screen.");
    // Опция без аргумента - вкл/выкл демо-режима приложения
    Option demoMode     = new Option(Defaults.CMDLINE_DEMO_MODE, "turning on DEMO-MODE (no emails) for emails delivery.");
    // Опция без аргумента - показать версию системы рассылок
    Option showVersion  = new Option(Defaults.CMDLINE_SHOW_VERSION, "show system version.");
    // Опция командной строки - путь к БД флот
    Option dbFleetPath  = OptionBuilder.withArgName("fleet_db_path")
                           .hasArg().withDescription("path to Db fleet (DBF format)")
                           .create(Defaults.CMDLINE_DB_FLEET_PATH);
    // Опция командной строки - путь к БД фирм
    Option dbFirmPath   = OptionBuilder.withArgName("firm_db_path")
                           .hasArg().withDescription("path to Db firm (DBF format)")
                           .create(Defaults.CMDLINE_DB_FIRM_PATH);
    // Опция командной строки - мыловский хост
    Option mailHost     = OptionBuilder.withArgName("mail_server_host")
                           .hasArg().withDescription("smtp mail server host for sending spam")
                           .create(Defaults.CMDLINE_MAIL_HOST);
    // Опция командной строки - мыловский порт
    Option mailPort     = OptionBuilder.withArgName("mail_server_port")
                           .hasArg().withDescription("smtp mail server port for sending spam")
                           .create(Defaults.CMDLINE_MAIL_PORT);
    // Опция командной строки - обратный адрес отправителя рассылки
    Option mailFrom     = OptionBuilder.withArgName("mail_from")
                           .hasArg().withDescription("return address for spam messages")
                           .create(Defaults.CMDLINE_MAIL_FROM);
    // Опция командной строки - идентификатор рассылки из БД рассылок
    Option deliveryId   = OptionBuilder.withArgName("delivery_id")
                           .hasArg().withDescription("delivery identificator for process")
                           .create(Defaults.CMDLINE_DELIVERY_ID);
    // Опция командной строки - путь к файловому репозиторию рассылок
    Option deliveriesFilesPath = OptionBuilder.withArgName("deliveries_files_path")
                                  .hasArg().withDescription("path to deliveries files repository")
                                  .create(Defaults.CMDLINE_DELIVERIES_FILES_PATH);
    // Опция командной строки - адрес(а) для тестовой отправки рассылки
    Option testMailTo   = OptionBuilder.withArgName("mail|mails_list")
                           .hasArg().withDescription("mail(s) for test delivery")
                           .create(Defaults.CMDLINE_TEST_MAIL_TO);
    // Опция командной строки - кодировка электронного письма
    Option mailEncoding = OptionBuilder.withArgName("email_encoding")
                           .hasArg().withDescription("email subject and text encoding")
                           .create(Defaults.CMDLINE_MAIL_ENCODING);


    // Непосредственно формирование списка опций
    Options cmdLineOptions = new Options();
    cmdLineOptions.addOption(showHelp);
    cmdLineOptions.addOption(demoMode);
    cmdLineOptions.addOption(showVersion);
    cmdLineOptions.addOption(dbFleetPath);
    cmdLineOptions.addOption(dbFirmPath);
    cmdLineOptions.addOption(mailHost);
    cmdLineOptions.addOption(mailPort);
    cmdLineOptions.addOption(mailFrom);
    cmdLineOptions.addOption(deliveryId);
    cmdLineOptions.addOption(deliveriesFilesPath);
    cmdLineOptions.addOption(testMailTo);
    cmdLineOptions.addOption(mailEncoding);
    // Возвращаем результат работы метода
    return cmdLineOptions;
   }

  /**
   * Конструктор. Разбирает переданную ему командную строку и инициализирует свои поля указанными значениями.
   * Инициализация происходит только если командная строка успешно разобрана.
   * @param args String[] командная строка для разбора и инициализации полей класса.
  */
  public MailerConfig(String args[])
   {
    // Логгер данного метода
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
    // Если переданная командная строка не пуста - работаем (иначе все
    // опции остаются со значениями по умолчанию - строка не разбирается)
    if ((args != null) && (args.length > 0))
     {
      // Строим нгабор опций командной строки
      this.cmdLineOptions = this.buildCmdLineOptions();
      // Разбираем (парсим) командную строку
      CommandLine cmdLine = null;
      try
       {
        Parser parser = new GnuParser();       // <- первый тип парсера (разборщика) командной строки
        //Parser parser = new PosixParser();     // <- второй тип парсера (разборщика) командной строки
        cmdLine = parser.parse(this.cmdLineOptions, args, false);
        logger.debug("Command line parsed. Processing.");
       }
      catch (ParseException e) {logger.error("Can't parse command line! Reason: [" + e.getMessage() + "]");}

      // Если разбор командной строки успешен - инициализируем поля класса
      if ((cmdLine != null) && (cmdLine.getOptions() != null) && (cmdLine.getOptions().length > 0))
       {
        // На основе разобранной командной строки инициализируем поля класса
        if (cmdLine.hasOption(Defaults.CMDLINE_DEMO_MODE))    {this.setDemoMode(true);}
        if (cmdLine.hasOption(Defaults.CMDLINE_SHOW_HELP))    {this.showHelp = true;}
        if (cmdLine.hasOption(Defaults.CMDLINE_SHOW_VERSION)) {this.showVersion = true;}

        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_DB_FLEET_PATH)))
         {this.setFleetDBPath(cmdLine.getOptionValue(Defaults.CMDLINE_DB_FLEET_PATH));}
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_DB_FIRM_PATH)))
         {this.setFirmDBPath(cmdLine.getOptionValue(Defaults.CMDLINE_DB_FIRM_PATH));}
        
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_HOST)))
         {this.setMailHost(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_HOST));}
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_PORT)))
         {
          int mailPort = 0;
          try {mailPort = Integer.valueOf(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_PORT));}
          catch(NumberFormatException e)
           {
            logger.error("Can't parse value [" + cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_PORT) + "] for SMTP mail " +
                         "server port. Message: [" + e.getMessage() + "]");
           }
          this.setMailPort(mailPort);
         }

        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_FROM)))
         {this.setMailFrom(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_FROM));}
        
        // Значение для идентификатора рассылки разбирается. Если разобрать его не удалось -
        // идентификатор рассылки будет 0.
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_DELIVERY_ID)))
         {
          int deliveryIdValue = 0;
          try {deliveryIdValue = Integer.valueOf(cmdLine.getOptionValue(Defaults.CMDLINE_DELIVERY_ID));}
          catch(NumberFormatException e)
           {
            logger.error("Can't parse value [" + cmdLine.getOptionValue(Defaults.CMDLINE_DELIVERY_ID) + "] for delivery ID. " +
                         "Message: [" + e.getMessage() + "]");
           }
          this.setDeliveryId(deliveryIdValue);
         }

        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_DELIVERIES_FILES_PATH)))
         {this.setDeliveriesFilesPath(cmdLine.getOptionValue(Defaults.CMDLINE_DELIVERIES_FILES_PATH));}
        // Тестовое мыло
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_TEST_MAIL_TO)))
         {this.setTestMailTo(cmdLine.getOptionValue(Defaults.CMDLINE_TEST_MAIL_TO));}
        // Кодировка письма
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_ENCODING)))
         {this.setMailEncoding(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_ENCODING));}

        // Закончили разбор командной строки - сообщаем в лог
        logger.info("Parameters from command line processed OK!");
       }
      // Если разбор командной строки прошел неудачно и строка после разбора пуста - сообщаем в лог. Все параметры
      // примут значения по умолчанию.
      else {logger.error("Command line after parametersparsing is empty! All parameters initialized with default values.");}
     }
    // Если полученная командная строка пуста - просто сообщим в лог и все параметры остаются со
    // значениями по умолчанию
    else {logger.info("No parameters for this MailerConfig. Processing with default values.");}
   }

  /** Конструктор. Инициализирует поля класса значениями по умолчанию. */
  public MailerConfig() {}

 }