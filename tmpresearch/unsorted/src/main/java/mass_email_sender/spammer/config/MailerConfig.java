package spammer.config;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import spammer.Defaults;

/**
 * ����� ������������ ������-������� (Mailer). ��������� ���������������� ���� ���� ���������� �� ��������� ������.
 * @author Gusev Dmitry (019gus)
 * @version 3.0 (DATE: 14.12.2010)
*/
@SuppressWarnings({"AccessStaticViaInstance"})
public class MailerConfig extends BaseMailerConfig
 {
  /** ����, ����������� ������ ����� ����������� - ���������� ��� ��� ���� � ����������. */
  private boolean showHelp       = false;
  /** ����, ����������� ������ ����� ����������� - �������� ������ �������. */
  private boolean showVersion    = false;
  /**
   * ����, ����������� ������ ����� ����������� - ����� ����� ��������� ������. ���� ���������� ���
   * ����, ����� ����� ���� �������� ����� ����� � ��������� ���� �����.
  */
  private Options cmdLineOptions = null;

  public boolean isShowHelp()                              {return showHelp;}
  public void    setShowHelp(boolean showHelp)             {this.showHelp = showHelp;}
  public Options getCmdLineOptions()                       {return cmdLineOptions;}
  public void    setCmdLineOptions(Options cmdLineOptions) {this.cmdLineOptions = cmdLineOptions;}
  public boolean isShowVersion()                           {return showVersion;}
  public void    setShowVersion(boolean showVersion)       {this.showVersion = showVersion;}

  /**
   * ��������������� ����� ������� ������ - ������ � ���������� ������ ����� ��������� ������ �������
   * ����������. ���� ������ ������������ ��� ��������� ������ ������ � ��� ������� ���������� ����������
   * ��������� ������.
   * @return Options ����������� ����� ����� ��������� ������.
  */
  private Options buildCmdLineOptions()
   {
    // ����� ��� ��������� (������ ���/����) - �������� ����� �����
    Option showHelp     = new Option(Defaults.CMDLINE_SHOW_HELP, "show this help screen.");
    // ����� ��� ��������� - ���/���� ����-������ ����������
    Option demoMode     = new Option(Defaults.CMDLINE_DEMO_MODE, "turning on DEMO-MODE (no emails) for emails delivery.");
    // ����� ��� ��������� - �������� ������ ������� ��������
    Option showVersion  = new Option(Defaults.CMDLINE_SHOW_VERSION, "show system version.");
    // ����� ��������� ������ - ���� � �� ����
    Option dbFleetPath  = OptionBuilder.withArgName("fleet_db_path")
                           .hasArg().withDescription("path to Db fleet (DBF format)")
                           .create(Defaults.CMDLINE_DB_FLEET_PATH);
    // ����� ��������� ������ - ���� � �� ����
    Option dbFirmPath   = OptionBuilder.withArgName("firm_db_path")
                           .hasArg().withDescription("path to Db firm (DBF format)")
                           .create(Defaults.CMDLINE_DB_FIRM_PATH);
    // ����� ��������� ������ - ��������� ����
    Option mailHost     = OptionBuilder.withArgName("mail_server_host")
                           .hasArg().withDescription("smtp mail server host for sending spam")
                           .create(Defaults.CMDLINE_MAIL_HOST);
    // ����� ��������� ������ - ��������� ����
    Option mailPort     = OptionBuilder.withArgName("mail_server_port")
                           .hasArg().withDescription("smtp mail server port for sending spam")
                           .create(Defaults.CMDLINE_MAIL_PORT);
    // ����� ��������� ������ - �������� ����� ����������� ��������
    Option mailFrom     = OptionBuilder.withArgName("mail_from")
                           .hasArg().withDescription("return address for spam messages")
                           .create(Defaults.CMDLINE_MAIL_FROM);
    // ����� ��������� ������ - ������������� �������� �� �� ��������
    Option deliveryId   = OptionBuilder.withArgName("delivery_id")
                           .hasArg().withDescription("delivery identificator for process")
                           .create(Defaults.CMDLINE_DELIVERY_ID);
    // ����� ��������� ������ - ���� � ��������� ����������� ��������
    Option deliveriesFilesPath = OptionBuilder.withArgName("deliveries_files_path")
                                  .hasArg().withDescription("path to deliveries files repository")
                                  .create(Defaults.CMDLINE_DELIVERIES_FILES_PATH);
    // ����� ��������� ������ - �����(�) ��� �������� �������� ��������
    Option testMailTo   = OptionBuilder.withArgName("mail|mails_list")
                           .hasArg().withDescription("mail(s) for test delivery")
                           .create(Defaults.CMDLINE_TEST_MAIL_TO);
    // ����� ��������� ������ - ��������� ������������ ������
    Option mailEncoding = OptionBuilder.withArgName("email_encoding")
                           .hasArg().withDescription("email subject and text encoding")
                           .create(Defaults.CMDLINE_MAIL_ENCODING);


    // ��������������� ������������ ������ �����
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
    // ���������� ��������� ������ ������
    return cmdLineOptions;
   }

  /**
   * �����������. ��������� ���������� ��� ��������� ������ � �������������� ���� ���� ���������� ����������.
   * ������������� ���������� ������ ���� ��������� ������ ������� ���������.
   * @param args String[] ��������� ������ ��� ������� � ������������� ����� ������.
  */
  public MailerConfig(String args[])
   {
    // ������ ������� ������
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
    // ���� ���������� ��������� ������ �� ����� - �������� (����� ���
    // ����� �������� �� ���������� �� ��������� - ������ �� �����������)
    if ((args != null) && (args.length > 0))
     {
      // ������ ������ ����� ��������� ������
      this.cmdLineOptions = this.buildCmdLineOptions();
      // ��������� (������) ��������� ������
      CommandLine cmdLine = null;
      try
       {
        Parser parser = new GnuParser();       // <- ������ ��� ������� (����������) ��������� ������
        //Parser parser = new PosixParser();     // <- ������ ��� ������� (����������) ��������� ������
        cmdLine = parser.parse(this.cmdLineOptions, args, false);
        logger.debug("Command line parsed. Processing.");
       }
      catch (ParseException e) {logger.error("Can't parse command line! Reason: [" + e.getMessage() + "]");}

      // ���� ������ ��������� ������ ������� - �������������� ���� ������
      if ((cmdLine != null) && (cmdLine.getOptions() != null) && (cmdLine.getOptions().length > 0))
       {
        // �� ������ ����������� ��������� ������ �������������� ���� ������
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
        
        // �������� ��� �������������� �������� �����������. ���� ��������� ��� �� ������� -
        // ������������� �������� ����� 0.
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
        // �������� ����
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_TEST_MAIL_TO)))
         {this.setTestMailTo(cmdLine.getOptionValue(Defaults.CMDLINE_TEST_MAIL_TO));}
        // ��������� ������
        if (!StringUtils.isBlank(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_ENCODING)))
         {this.setMailEncoding(cmdLine.getOptionValue(Defaults.CMDLINE_MAIL_ENCODING));}

        // ��������� ������ ��������� ������ - �������� � ���
        logger.info("Parameters from command line processed OK!");
       }
      // ���� ������ ��������� ������ ������ �������� � ������ ����� ������� ����� - �������� � ���. ��� ���������
      // ������ �������� �� ���������.
      else {logger.error("Command line after parametersparsing is empty! All parameters initialized with default values.");}
     }
    // ���� ���������� ��������� ������ ����� - ������ ������� � ��� � ��� ��������� �������� ��
    // ���������� �� ���������
    else {logger.info("No parameters for this MailerConfig. Processing with default values.");}
   }

  /** �����������. �������������� ���� ������ ���������� �� ���������. */
  public MailerConfig() {}

 }