package jlib.mail;

import jlib.JLibConsts;
import jlib.logging.InitLogger;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * ������ ����� ��������� �������� ��������� �� ����������� �����. ���� � ������ ��������� �����, ��� ����� �����
 * ����������. ������ ����� ���������� ���������� javamail 1.3.3 ��� ������ � ��. ������. ��� �������� ����� ���-��
 * �������� SMTP ��� �����������. �������� �������������� �� ������(�������), ��������� � ���� to. ��� ����, ����
 * ������������ ������ �������, �� ������ � ������ ����������� ��������.
 *
 * @author Gusev Dmitry. Copyright (c) 2005, Gusev Dmitry, dept. 019.
 * @version 6.0 (09.12.2010)
*/

public class JMail
 {
  /** ����� ������� �������. */
  private Logger      logger = Logger.getLogger(getClass().getName());
  /** ������������ ������-���������. */
  private JMailConfig config = null;

  /**
   * ����������� �� ���������. ����������� �������������� ������������ ������.
   * @param config JMailConfig ������������ ������-�������. 
  */
  public JMail(JMailConfig config) {this.config = config;}

  public JMailConfig getConfig() {
   return config;
  }

  public void setConfig(JMailConfig config) {
   this.config = config;
  }

  /**
   * ����� ��� ���������������� �������� ������. ���� �� ����� �������� ��������� ������ - ������������ ��, ������� �����
   * ��������� ���������� � ��������� ������. ���� ������ files_list �������� ���� �� ���� ��� �����, �� ������ �����
   * MultiPart, ���� �� ��� �� ������ ����� �����, �� ����� ���������� ������� ������.
   * @throws MessagingException �� - ������ ��� �������� ��������� ���������.
  */
  public void send() throws MessagingException
   {
    logger.debug("WORKING JMail.send().");
    // ���� ������� ������ �� ���� - ��������
    if ((this.config != null) && (!this.config.isEmpty()))
     {
      logger.debug("JMail config ok. Processing.");
      // ���������� ����� ������ ��� ����� ����� ��������� ��������� (MultiPart � Plain)
      Properties props = System.getProperties();
      props.put("mail.smtp.host", config.getMailHost());

      // ���� ��������� ������ ���� �� ������ - ����������� � �� ����� 0
      if (config.getMailPort() > 0) {props.put("mail.smtp.port", config.getMailPort());}
      
      Session session = Session.getInstance(props, null);

      // ������� ������ ���������
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(config.getFrom())); // <- �� ����
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getTo(), false)); // <- ����/����
      msg.setSubject(config.getSubject(), config.getEncoding()); // <- ���� ������
      // ���� �������� ������ - ������ ������� ����
      msg.setSentDate(new Date());
      // ���������-������
      msg.setHeader("X-Mailer", JLibConsts.JMAIL_MAILER);

      // ��������� ������������� ���� ������������� ������ (���� ��� ����) �
      // ���������� ������ ����������� ������� ������
      boolean isFilesExists = false;
      if ((config.getFilesList() != null) && (!config.getFilesList().isEmpty()))
       {
        for (String file : config.getFilesList())
         {if (new File(file).exists()) {isFilesExists = true;}}
       }

      // ���� ���� ����� ��� �������� - ������� ������ ���� MultiPart
      if (isFilesExists)
       {
        logger.debug("There are files for this mail. Using Multipart message.");
        // �������� ������� MultiPart � ���������� � ���� ������
        Multipart mp = new MimeMultipart();
        // ������� ������� � �������� ��������� ����� - ������ ����� ������
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(config.getText(), config.getEncoding());
        mp.addBodyPart(mbp);
        // ������� � ��������� ��������� ����� (� �����) - ��������� ����� ������
        FileDataSource fds;
        for (String file : config.getFilesList())
         {
          mbp = new MimeBodyPart();
          fds = new FileDataSource(file);
          mbp.setDataHandler(new DataHandler(fds));

          // todo: ������������� � ���������� ����� ���������� �����
          try {mbp.setFileName(MimeUtility.encodeText(fds.getName()));}
          catch (UnsupportedEncodingException e) {logger.error(e.getMessage());}

          mp.addBodyPart(mbp);
         }
       // ���������� ������� Multipart � ������
       msg.setContent(mp, "text/plain; charset=\"" + config.getEncoding() + "\"");
      }
     // ���� ������ ��� �������� ��� - ������� ������
     else
      {
       logger.debug("There are no files for this mail.");
       msg.setContent(config.getText(), "text/plain; charset=\"" + config.getEncoding() + "\"");
      }
     // ��������������� �������� ������
     Transport.send(msg);
    }
   // ���� �� ������� ������ ���� - ������. �������� � ���.
   else {logger.error("Empty JMail config! Can't process!");}
  }

 /**
  * ����� main - ������ ��� ������������ ������� ������.
  * @param args String[] ��������� ��������� ������ ��� ������ main.
  */
 public static void main(String[] args)
  {
   Logger logger = Logger.getLogger(JMail.class.getName());
   InitLogger.initLogger(JMail.class.getName());

   JMailConfig config = new JMailConfig();
   config.setTo("019gus@rs-head.spb.ru");
   config.setFrom("019gus@rs-head.spb.ru");
   config.setSubject("����!");
   config.setMailHost("10.1.254.70");
   config.setText("������� ����!!!");
   config.addFile("D:\\my_docs\\�������\\Java\\j2ee\\updaterService\\production\\client\\updaterClient.zip");

   JMail mail = new JMail(config);
   try
    {
     //for (int i = 0; i < 100; i++) {mail.send();}
     //config.setTo("brusakov@rs-head.spb.ru");
     //for (int i = 0; i < 100; i++) {mail.send();}
     mail.send();
     logger.debug("Mail was sent.");
    }
   catch (MessagingException e) {logger.error(e.getMessage());}
  }
  
}