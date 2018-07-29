package spammer.mailsProcessor;

import jlib.mail.JMail;
import jlib.mail.JMailConfig;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import spammer.Defaults;
import spammer.config.MailerConfig;
import spammer.dataModel.dao.DeliveriesDAO;
import spammer.dataModel.dao.EmailsDAO;
import spammer.dataModel.dao.MandatoryEmailsDAO;
import spammer.dataModel.dto.DeliveryDTO;
import spammer.dataModel.dto.DeliveryFileDTO;
import spammer.dataModel.dto.EmailDTO;
import spammer.dataModel.dto.RecipientTypeDTO;
import spammer.mailsList.impl.TestEmailsListBuilder;
import spammer.mailsList.impl.dbf.DbfShipownersEng;
import spammer.mailsList.impl.dbf.DbfShipownersRus;
import spammer.mailsList.interfaces.EmailsListInterface;

import javax.mail.MessagingException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * ������ ����-�������� �����. ��������������� ����������� ������ MailerConfig.
 * @author Gusev Dmitry (�������)
 * @version 5.1 (DATE: 04.04.2010)
*/

// todo: ����: ���� � �������� ������� ���� �����������, �� ��� �������� (�.�. ��� ��� ��� ������� ������), �� ������
// todo: ������ ��� �������� ����� ��������� ������. ��� ������������ ����������� - ������ �� ����� ����������, �� ������������ -
// todo: ������ ����� ��������� ������! (04.04.2011)

public class Mailer
 {
  /** ������ ������� ������. */
  private static Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
  /** ������������ ������� ������-�������. */
  private MailerConfig  config = null;

  public Mailer(MailerConfig config) {this.config = config;}

  //public MailerConfig getConfig()            {return config;}
  //public void setConfig(MailerConfig config) {this.config = config;}

  /**
   * ��������������� �����, �������������� �������� �������� �����. ����� ����������� ������������ � �����������.
  */
  @SuppressWarnings({"ReuseOfLocalVariable"})
  public void startSpam()
   {
    logger.debug("Mailer: startSpam().");
    // ��������� ������ ��� ������ ������� - ���� ������ ���� ��� �������� ������ - ���� �� ������! ������!
    if ((this.config != null) && (StringUtils.isBlank(this.config.getConfigErrors())))
     {
      logger.info("Mailer config OK. Processing delivery.");
      // ���� ������� ����-����� - ������ ���
      if (this.config.isDemoMode()) {logger.warn("DEMO-MODE IS ACTIVE. NO MESSAGES WILL BE SENT!");}
      
      // �������� ������ �� ��������� ������ DeliveriesDAO - ��� ����� ������������ �� ������ ������
      DeliveriesDAO deliveriesDAO = new DeliveriesDAO();
      // ���� ������������� �������� ����������� - ��������. ���� ������ �� �������� � �����
      DeliveryDTO delivery = deliveriesDAO.findByID(config.getDeliveryId());
      if ((delivery != null) && (!delivery.isEmpty()))
       {
        logger.debug("Delivery with ID = " + config.getDeliveryId() + " was found. Processing.");

        // ����� �� ������������� ������ ������� �������� - � ��������. ������ ��������� � ��.
        Defaults.DeliveryStatus deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS;
        deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, null);

        // ������� ������ ��� ��������� ������ (JMail)
        JMailConfig mailConfig = new JMailConfig();
        mailConfig.setFrom(config.getMailFrom());
        mailConfig.setMailHost(config.getMailHost());
        // ���� ������ ���� (������ ������ ����) - ����� ������ ���
        if (config.getMailPort() > 0) {mailConfig.setMailPort(config.getMailPort());}
        // ��������� ������ (������ ���� ������� ��������)
        if (!StringUtils.isBlank(config.getMailEncoding()))
         {
          logger.info("Using mail encoding: " + config.getMailEncoding());
          mailConfig.setEncoding(config.getMailEncoding());
         }
        // ����
        mailConfig.setSubject(delivery.getSubject());
        // �����
        mailConfig.setText(delivery.getText());

        // ������ �������� (��������� �������� NULL - ��� ������)
        String deliveryError = null;
      
        // ��������� ���������� � ������ (���� ��� ���� � �������� � ���� �������� �������� ������). ���� � ��������
        // (� ���������� ������ DeliveryDTO) ������� ����������, � ��������� �� ��� - �������� �� �������� (�����
        // �������� ��������� � ��� � ������� ������ � �� ��������)
        HashSet<DeliveryFileDTO> deliveryFiles = delivery.getFiles();
        if ((deliveryFiles != null) && (!deliveryFiles.isEmpty()))
         {
          logger.info("There are [" + deliveryFiles.size() + "] file(s) for this emails delivery. Checking.");
          // ���� ��������� � �������� ��������� ���� � ��������� ����������� ���� �� - ��������
          String filesPath = this.config.getDeliveriesFilesPath();
          if ((!StringUtils.isBlank(filesPath)) && (new File(filesPath).exists()))
           {
            logger.debug("Files repository path [" + filesPath + "] is ok.");
            String path = FSUtils.fixFPath(filesPath, true); // <- ������������ ���� � ������ � ����������� ��������� �����

            // � ����� �������� �� ���� ������, ��������� � ������ �������� �, ���� ��� �������, ��������� �
            // ������������ ������ �������� �������� (JMail). ���� ���� ���� �� ������ �� ������ - ������, ��������
            // ������������ �� �����.
            for (DeliveryFileDTO deliveryFile : deliveryFiles)
             {
              // �������������� �������� - �� ���� �� ���������� ����. ���� ���� - ������!
              if ((deliveryFile != null) && (!deliveryFile.isEmpty()))
               {
                String oneAttachmentPath = path + delivery.getId() + "/" + deliveryFile.getFileName();
                // ���� ���� ���� (����������) - ��������� ��� � ������� �������
                if (new File(oneAttachmentPath).exists() && new File(oneAttachmentPath).isFile())
                 {mailConfig.addFile(oneAttachmentPath);}
                // ���� ����� ��� - ������!
                else
                 {
                  deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
                  deliveryError  = "Attachment file [" + oneAttachmentPath + "] not found or not a file!";
                  // ������� � ���
                  logger.error(deliveryError);
                  // ������� � ���� ��������
                  deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
                 }
               }
              // ���� ������� ������ ���� �� ������ - ������!
              else
               {
                deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
                deliveryError  = "Internal error! Empty files in delivery's attachments list!";
                // ������� � ���
                logger.error(deliveryError);
                // ������� � ���� ��������
                deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
               }
             }

           }
          // ���� �� ��������� ���� �������� (�� ����� � �������� �������) - �������� �� ��������������.
          else
           {
            deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
            deliveryError  = "There are [" + deliveryFiles.size() + "] file(s) for this delivery, " +
                             "but file repository path [" + filesPath + "] doesn't exists!";
            // ������� � ���
            logger.error(deliveryError);
            // ������� � ���� ��������
            deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
           }
         }
        // ���� ������ � �������� ��� - ������ ������� � ���
        else {logger.info("This emails delivery attachments-free!");}

        // ���� �������� ������ (� �� ���������� � ������ �������) ����������� ������ ��� ��� ������ ��������
        // ������ ��� ���������� ������ - ���������� ������
        if (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS)
         {
          logger.debug("Files processing completed. Continue delivery processing.");

          // �������� ������ ������� ��� ��������. ���� ������ �������� ��������� ������ -testMailTo, �� �����
          //������������ ������ �� �����(�), ���������(-��) � ������ ���������. �������� ��������� ��������� �� ����
          // ������.
          TreeMap<String, Integer> emailsList = null;
          if (StringUtils.isBlank(this.config.getTestMailTo()))
           {
            logger.info("Standard delivery. Processing emails list.");
            // ���� � ������ �������� ������� ���������� - ���������� ��.
            if ((delivery.getRecipients() != null) && (!delivery.getRecipients().isEmpty()))
             {
              logger.info("This delivery had [" + delivery.getRecipients().size() + "] recipients types. Processing.");
              EmailsListInterface builder;
              // �������� ������ ����-������� ��� ��������
              TreeMap<String, Integer> list;
              // � ����� ����������� ��� ������ ������ ��� ��������
              for (RecipientTypeDTO type : delivery.getRecipients())
               {
                if ((type != null) && (type.getRecipientType() != null))
                 {
                  switch (type.getRecipientType())
                   {
                    // �������� ������ �������� (����� 019)
                    case RECIPIENT_TYPE_TEST:
                     logger.debug("Recipient type: " + Defaults.RecipientType.RECIPIENT_TYPE_TEST);
                     builder = new TestEmailsListBuilder();
                     list = builder.getEmailsList();
                     if ((list != null) && (!list.isEmpty()))
                      {
                       if (emailsList == null) {emailsList = new TreeMap<String, Integer>();}
                       emailsList.putAll(list);
                      }
                     break;
                    // ������������� �������������
                    case RECIPIENT_TYPE_SHIPOWNERS_RUS:
                     logger.debug("Recipient type: " + Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_RUS);
                     builder = new DbfShipownersRus(this.config.getFleetDBPath(), this.config.getFirmDBPath());
                     list = builder.getEmailsList();
                     if ((list != null) && (!list.isEmpty()))
                      {
                       if (emailsList == null) {emailsList = new TreeMap<String, Integer>();}
                       emailsList.putAll(list);
                      }
                     break;
                    // ������������ �������������
                    case RECIPIENT_TYPE_SHIPOWNERS_ENG:
                     logger.debug("Recipient type: " + Defaults.RecipientType.RECIPIENT_TYPE_SHIPOWNERS_ENG);
                     builder = new DbfShipownersEng(this.config.getFleetDBPath(), this.config.getFirmDBPath());
                     list = builder.getEmailsList();
                     if ((list != null) && (!list.isEmpty()))
                      {
                       if (emailsList == null) {emailsList = new TreeMap<String, Integer>();}
                       emailsList.putAll(list);
                      }
                     break;
                   }
                 }
               } // END OF FOR

              // ���� �� ����� ��������� � ������ ������ ��� �������� ���� ���-�� ���������, �� ��������� � ���� ������
              // ����-������ �� ������� mandatoryEmails - �� ������, ���� ������ ������������ ������ ��� ����� ��������.
              if ((emailsList != null) && (!emailsList.isEmpty()))
               {
                TreeMap<String, Integer> mandatoryList = new MandatoryEmailsDAO().getEmailsList();
                if ((mandatoryList != null) && (!mandatoryList.isEmpty()))
                 {
                  logger.debug("Mandatory emails list is not empty [" + mandatoryList.size() + "]! Processing.");
                  emailsList.putAll(mandatoryList);
                 }
               }
              else {logger.error("Empty emails list! Can't process!");}
             }
            // ���� �� ����������� ��� - ������ �� ���������, ��� ������.
            else {logger.error("This delivery had [0] recipients types. Can't process!");}
           }
          // �������� ��������
          else
           {
            logger.info("Test delivery. Processing.");
            emailsList = new TreeMap<String, Integer>();
            emailsList.put(this.config.getTestMailTo(), 0);
           }

          // ���� ������ ����� �� ���� - ��������
          if ((emailsList != null) && (!emailsList.isEmpty()))
           {
            logger.info("Emails list contains [" + emailsList.size() + "] address(es). Processing.");

            // ��������� ������ "������" - ��� ���������� � ������ ��������
            EmailDTO email = new EmailDTO();
            email.setDeliveryId(delivery.getId());

            // ��� ��������
            JMail mailer = new JMail(mailConfig);
            // ��������������� ���� �������� �����
            Set<String> keys = emailsList.keySet();
            int    companyId;
            String sendResult = null;
            // ����� ��� �������� ���� � �� (� ������� �����)
            EmailsDAO emailsDAO = new EmailsDAO();

            logger.info("Starting emails delivery...");
            // ������� ������������ �����
            int counter = 0;
            for (String key : keys)
             {
              companyId = emailsList.get(key);
              // ������� �������� ����������� (���� ����� � ����� ������� ��������� ���). ��� �������� �����������,
              // ��������� � ������ ����� �������� �� ������.
              String strEmail = key;
              for (String aWrong : Defaults.EMAILS_DELIMITERS_WRONG)
               {strEmail = strEmail.replaceAll(aWrong, Defaults.EMAILS_DELIMITER_RIGHT);}
              // ���������� ���������
              logger.debug("Email before process: [" + key + "], email after process (for send): [" + strEmail + "]. ");

              // ��������� ���� � ������ ���������
              mailConfig.setTo(strEmail);
              // ��������� ������ � ��������� ������ "������" - ��� �������� � �� (�� MS SQL)
              email.setCompanyId(companyId);
              email.setEmail(strEmail);
              // ���������� ���������. ���� ������� ����-�����, �� ������ �� ������������, � ������
              // ���������� � ��� ��������. ������ � �� ����� ��������.
              try
               {
                if (!this.config.isDemoMode()) {mailer.send();}
                else
                 {
                  logger.info("[DEMO MODE]: sending email to [" + strEmail + "]");
                  sendResult = "DEMO EMAIL DELIVERY OK";
                 }
               }
              catch (MessagingException e)
               {
                logger.error("Sending for this [" + strEmail + "] address failed. Message [" + e.getMessage() + "]");
                // ��������� �������� ������ �� ������� ������ (��� ��)
                sendResult = e.getMessage();
               }
              // �������� ������ � ��������� ������ "������" � ��������� ��� � �� (��������� �������� ������)
              if (!StringUtils.isBlank(sendResult))
               {
                email.setErrorText(sendResult);
                // ���� ���� ��������� � �� �� � ����-������ - ������! ��������� � ���� (�������) �-���.
                if (!this.config.isDemoMode()) {email.setStatus(Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED.getIntValue());}
                // ������ �������� - � ��������. ���������� ���������� ��� ������������� ����� ������
                // �� ����� �������� ����� �� ������. ���� �� �� � ����-������, �� ������ ���� �������� ����� ��.
                if ((!this.config.isDemoMode()) && (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS))
                 {
                  deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FINISHED_WITH_ERRORS;
                  deliveryError  = "There are delivery errors. See emails log for this delivery.";
                 }
                // �������� ���������
                sendResult = null;
               }
              // ��������� � �� ���� �� �������� ������ (������) �� �����
              emailsDAO.change(email);
              // �������� ���� errorText � Status
              email.setErrorText(null);
              email.setStatus(Defaults.DeliveryStatus.DELIVERY_STATUS_OK.getIntValue());
              // ����������� ������� ������������ ���
              counter++;
             } // ����� ����� �������� ����� �� ������

            logger.info("Emails delivery finished. [" + counter + "] email(s) processed. See log or database for results.");
            // �� ��������� ����� �������� - ��������� � �� ������ ���������� ��� ��������. ���� ������ ��� -
            // ��������� ������ - "�������", ���� �� ���� ������, �� ����������� ������ "�������"
            if (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS)
             {
              deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_OK;
              deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, null);
             }
            else
             {new DeliveriesDAO().setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);}
           }
          // ���� ������ ����� ���� - ������. �������� � ���. ��������� � �� ��������
          else
           {
            deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
            deliveryError  = "Emails list is empty! Can't process delivery!";
            // ������� � ���
            logger.error(deliveryError);
            // ������� � ���� ��������
            deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
           }
         }
        // ���� �� ���������� �������� ����������� �������� - �������� �� ��������������
        else {logger.error("Can't process emails delivery due previous errors. See log!");}
       }
      // ���� ������������� �������� ������ ���� - ����� ������
      else {logger.error("Can't start spam - wrong delivery ID [" + this.config.getDeliveryId() + "].");}
     }
    // ���� �� ������ ���� ��� �������� ������ - ���� �� ������ � �������� � ���
    else
     {
      // � ����������� �� ���� ����������� ������� - ��������� � ���
      if (this.config == null) {logger.error("Mailer config is NULL!");}
      else                     {logger.error("Mailer config invalid! " + this.config.getConfigErrors());}
     }
   }

  /**
   * ����� ��� ������������.
   * @param args String[] ��������� ������.
  */
  /**
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[] {"jdb", "org", "jlib", Defaults.LOGGER_NAME},
     Level.INFO, Defaults.LOGGER_FILE, Defaults.LOGGER_PATTERN, true);
    Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);

    MailerConfig config = new MailerConfig();
    config.setDeliveryId(6);
    config.setDeliveriesFilesPath("c:\\temp\\SpammerFiles");
    Mailer mailer = new Mailer(config);
    mailer.startSpam();
   }
  */

 }