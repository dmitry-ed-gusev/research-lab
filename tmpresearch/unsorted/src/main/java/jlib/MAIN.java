package jlib;

import jlib.auth.Password;
import jlib.exceptions.EmptyObjectException;
import jlib.exceptions.EmptyPassException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
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
  /** ���������-������ ������� ������. */
  private static Logger logger = Logger.getLogger(MAIN.class.getName());

  /** ����������� ��� ��� ����� ������. */
  private static final String DEFAULT_PASS_FILE_NAME          = "password";
  /** ����������� ���������� ��� ����� ������. */
  private static final String DEFAULT_PASS_FILE_EXTENSION     = "data"; 

  /** ����� ��������� ������: ������� ���� � �������. ������ - �������� ������ �����. */
  private static final String OPTION_CREATE_PASS_FILE = "createPassFile";
  /** ����� ��������� ������: ��������� ������ �� �����. */
  private static final String OPTION_READ_PASS_FILE   = "readPassFile";
  /** ����� ��������� ������: �������, ��� ������ ���������� ���� ������ (��� ������ ��� ��������). */
  private static final String OPTION_PASS_FILE_DIR    = "passFileDir";
  /** ����� ��������� ������: ��� ��� ����� ������ (��� ������ ��� ��������). */
  private static final String OPTION_PASS_FILE_NAME   = "passFileName";
  /** ����� ��������� ������: �������� ����� ������. */
  private static final String OPTION_HELP             = "help";

  /** ���������� ������ ����� (��������� ��� ��������� ����� � ������� ��������� ������). */
  private static Options options = null;

  /**
   * ����� ���������� ��������� ������ ��� ����������� ���������.
   * @param args String[] ����� ���������� ��������� ������.
   * @return CommandLine ��������������� ��������� ������ �� ����� �����������.
  */
  @SuppressWarnings({"AccessStaticViaInstance"})
  private static CommandLine buildCmdLine(String[] args)
   {
    // �������� ����� ������
    Option createFile   = OptionBuilder.withArgName("password")
                                       .hasArg()
                                       .withDescription("create the password file with specified password" )
                                       .create(OPTION_CREATE_PASS_FILE); // <- ����� � �����������
    // ���� � �������� � ������ ������
    Option passFileDir  = OptionBuilder.withArgName("path")
                                       .hasArg()
                                       .withDescription("catalog with password file")
                                       .create(OPTION_PASS_FILE_DIR);
    // ��� ����� ������
    Option passFileName = OptionBuilder.withArgName("filename")
                                       .hasArg()
                                       .withDescription("password file name")
                                       .create(OPTION_PASS_FILE_NAME);
    // ������ ����� ������
    Option readFile     = new Option(OPTION_READ_PASS_FILE, "read the specified password file");
    // ����� ������ ������ ������
    Option help         = new Option(OPTION_HELP, "print this help message"); // <- ���������� �����
    
    options = new Options();

    options.addOption(createFile);
    options.addOption(readFile);
    options.addOption(passFileDir);
    options.addOption(passFileName);
    options.addOption(help);

    // ������ ��������� ������
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
    InitLogger.initLogger("jlib");

    logger.debug("-> " + SystemUtils.getUserDir().getAbsolutePath());

    // ������� ����������� ��������� ������
    CommandLine cmdLine = MAIN.buildCmdLine(args);

    // --- ��������� ��������� ������ ---
    // ������ ������ ������
    if (cmdLine.hasOption(OPTION_HELP))
     {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar jlib", options, true);
     }
    
    // �������� ����� ������
    else if (cmdLine.hasOption(OPTION_CREATE_PASS_FILE))
     {
      // ������ ��������� ������ - ���� ��� ���, �� ������ � �� ������
      String password = cmdLine.getOptionValue(OPTION_CREATE_PASS_FILE);
      if (!StringUtils.isBlank(password))
       {
        String passFileDir;
        // ������ �������, � ������� ����� ������� ���� ������. ���� ������� ������ (�� ����� ��������) - ����� ���.
        if ((cmdLine.hasOption(OPTION_PASS_FILE_DIR)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR))))
         {passFileDir = cmdLine.getOptionValue(OPTION_PASS_FILE_DIR);}
        // ���� �� ������� �� ������ - ���� ��������� � ������� ��������
        else {passFileDir = SystemUtils.getUserDir().getAbsolutePath();}
        
        String passFileName; // <- ��� ����� ������
        String passFileExt;  // <- ���������� ����� ������
        // ������ ��� ����� ������. ���� ��� ������� (�� ����� ��������) - ����� ���.
        if ((cmdLine.hasOption(OPTION_PASS_FILE_NAME)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME))))
         {
          String str = cmdLine.getOptionValue(OPTION_PASS_FILE_NAME);
          // ��� ����� - ��, ��� ��������� ����� �� ������ ������� ����� "."
          passFileName = str.substring(0, str.lastIndexOf("."));
          // ���������� - ��, ��� ��������� ������ �� ������ ������� ����� "."
          passFileExt = str.substring(str.lastIndexOf(".") + 1);
          logger.debug("full: " + str + "| name: " + passFileName + "| ext: " + passFileExt);
         }
        // ���� �� ��� �� ������� - ��� ����� ����������� ������������� (�� ���������)
        else {passFileName = DEFAULT_PASS_FILE_NAME; passFileExt = DEFAULT_PASS_FILE_EXTENSION;}

        // ��������������� ������ ����� ������ �� ����
        try
         {
          // ������� ��������� ������ "������"
          Password pass = new Password(password);
          // ���������� ��������� ��������� �� ���� (�����������)
          FSUtils.serializeObject(pass, passFileDir, passFileName, passFileExt);
         }
        catch (EmptyPassException e) {logger.error(e.getMessage());}
        catch (EmptyObjectException e) {logger.error(e.getMessage());}
        catch (IOException e) {logger.error(e.getMessage());}
       }
      else {logger.warn("Password not specified!");}
     }

    // ������ ����� ������
    else if (cmdLine.hasOption(OPTION_READ_PASS_FILE))
     {
      String passFileDir;
      String passFileName;
      // ������ �������, � ������� ����� ������ ���� ������. ���� ������� ������ (�� ����� ��������) - ����� ���.
      if ((cmdLine.hasOption(OPTION_PASS_FILE_DIR)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR))))
       {passFileDir = FSUtils.fixFPath(cmdLine.getOptionValue(OPTION_PASS_FILE_DIR), true);}
      // ���� �� ������� �� ������ - ���� ������ � ������� ��������
      else {passFileDir = FSUtils.fixFPath(SystemUtils.getUserDir().getAbsolutePath(), true);}
      // ������ ��� ����� ������. ���� ��� ������� (�� ����� ��������) - ����� ���.
      if ((cmdLine.hasOption(OPTION_PASS_FILE_NAME)) && (!StringUtils.isBlank(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME))))
       {passFileName = FSUtils.fixFPath(cmdLine.getOptionValue(OPTION_PASS_FILE_NAME));}
      // ���� ��� ����� ������ �� ������� - ���� ���� ������ � ������ �� ���������
      else {passFileName = DEFAULT_PASS_FILE_NAME + "." + DEFAULT_PASS_FILE_EXTENSION;}
      // ��������������� ������ ���� ������
      try
       {
        Password password = (Password) FSUtils.deserializeObject(passFileDir + passFileName, false);
        System.out.println("PASSWORD (from file [" + passFileDir + passFileName + "]): " + password.getPassword());
       }
      catch (ClassNotFoundException e) {logger.error(e.getMessage());}
      catch (IOException e) {logger.error(e.getMessage());}
     }

    // ���� �� ������� �� ���� �� ��������� - ����� ���������� ����� ������
    else
     {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar jlib", options, true);
     }

   } // END OF MAIN METHOD
  
 }