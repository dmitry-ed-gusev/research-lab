package spammer;

import jdb.config.DBConfig;
import jdb.exceptions.DBConnectionException;
import jdb.exceptions.DBModuleConfigException;
import jdb.utils.DBUtils;
import jlib.logging.InitLogger;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import spammer.config.MailerConfig;
import spammer.mailsProcessor.Mailer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ������� ����������� ������ ������� ����������. ��� ���������� ������������ jar-���� ������� ������������ ������
 * ����� � �������� MAIN-������. ����� ����� ������������ ��������� ��������� ������, ��� �������� ����� �������
 * ��������� ������ ����� (��� ����������� jar-����� � ������ -help (��. ������ �������� Defaults).
 *
 * @author Gusev Dmitry (�������)
 * @version 2.0 (DATE: 17.12.2010)
*/

// todo: �������� � ���������� ����� �������������� � ������ ����� - ������� ��� ��������� ������������ :)

public class MAIN
 {

  public static void main(String[] args)
   {
    // �������������� ������� ��� ������� ����������
    InitLogger.initLoggers(new String[] {"jdb", "org", "jlib", Defaults.LOGGER_NAME},
     Level.DEBUG, Defaults.LOGGER_FILE, Defaults.LOGGER_PATTERN, true);

    // ����� ������ ����������
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
    logger.info("Starting SPAMMER project. Checking DBMS connection. Config [" + Defaults.DBCONFIG_FILE + "].");

    // ��������� � ������ ���������� ��������� ������. ������������� ������� ����������.
    MailerConfig mailerConfig = new MailerConfig(args);
    logger.info("Mailer module config initialized OK.");

    // ���� ������� ����� �������� ������ - �������
    if (mailerConfig.isShowVersion()) {logger.info(Defaults.SYSTEM_VERSION);}
    // ���� �� ������� ����� �������� ���� - ���� �������
    else if (mailerConfig.isShowHelp())
     {
      logger.info("Showing help screen...");
      new HelpFormatter().printHelp("java -jar MassEmailsSender.jar", null,
       mailerConfig.getCmdLineOptions(), Defaults.SYSTEM_VERSION, true);
     }
    // ���� �� ������� ����� ������ ����� ��� ������ - �������� �� ������� �����.
    else
     {
      // ����� �������� �������� ���������� � ����. ���� ��� ���� - ��� ��!
      // ����� ���������� ��������� ��������� ��������� ������.
      Connection connection = null;
      try
       {
        // �������� ������� �� �����
        DBConfig dbConfig = new DBConfig(Defaults.DBCONFIG_FILE);
        // �������� ���������� � ����
        connection = DBUtils.getDBConn(dbConfig);
        // ���� ���������� �� ������� �������� - ������. ������ �� ��������.
        if (connection == null) {throw new DBConnectionException("Can't connect to DBMS! Config file [" + Defaults.DBCONFIG_FILE + "].");}
        // ���� ���������� �������� - ��������� ���
        else                    {connection.close();}
        // ���������� �������� - ����������
        logger.info("DBMS connection OK (dbConfig [" + Defaults.DBCONFIG_FILE + "]). Processing.");

        // ����� ���������� ���� �������� ��������������� ��������� ��������. ����� ���������� ��� ������������ ������
        // ������� �������������� ��� � ������ ��������� (Mailer).
        Mailer mailer = new Mailer(mailerConfig);
        mailer.startSpam();
       }
      // �������� ��
      catch (DBModuleConfigException e) {logger.error(e.getMessage());}
      catch (ConfigurationException e)  {logger.error(e.getMessage());}
      catch (IOException e)             {logger.error(e.getMessage());}
      catch (DBConnectionException e)   {logger.error(e.getMessage());}
      catch (SQLException e)            {logger.error(e.getMessage());}
      // ������������ ��������
      finally
       {try {if (connection != null) {connection.close();}} catch (SQLException e) {logger.error(e.getMessage());}}
     }
    
    /**
    // �������� - ��������� �����������
    DeliveryDTO delivery = new DeliveryDTO();
    delivery.setSubject("�������� ��������. ������ �������������� ��������.");
    delivery.setText("�������� ������. ���� �������� ��������. ����� 019, ���.");
    delivery.setInitiator("019gus");
    delivery.addFile(new DeliveryFileDTO("2-040202-020_1.pdf"));
    delivery.addFile(new DeliveryFileDTO("2-040202-020_2.pdf"));
    delivery.addFile(new DeliveryFileDTO("2-040202-020_3.pdf"));
    // ��������� � �� ������
    new DeliveriesDAO().change(delivery);
    */
   
   }

 }