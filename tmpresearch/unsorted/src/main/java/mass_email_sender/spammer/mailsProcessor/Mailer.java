package mass_email_sender.spammer.mailsProcessor;

import gusev.dmitry.utils.MyIOUtils;
import jlib.mail.JMail;
import jlib.mail.JMailConfig;
import mass_email_sender.spammer.Defaults;
import mass_email_sender.spammer.config.MailerConfig;
import mass_email_sender.spammer.dataModel.dao.DeliveriesDAO;
import mass_email_sender.spammer.dataModel.dao.EmailsDAO;
import mass_email_sender.spammer.dataModel.dao.MandatoryEmailsDAO;
import mass_email_sender.spammer.dataModel.dto.DeliveryDTO;
import mass_email_sender.spammer.dataModel.dto.DeliveryFileDTO;
import mass_email_sender.spammer.dataModel.dto.EmailDTO;
import mass_email_sender.spammer.dataModel.dto.RecipientTypeDTO;
import mass_email_sender.spammer.mailsList.impl.TestEmailsListBuilder;
import mass_email_sender.spammer.mailsList.impl.dbf.DbfShipownersEng;
import mass_email_sender.spammer.mailsList.impl.dbf.DbfShipownersRus;
import mass_email_sender.spammer.mailsList.interfaces.EmailsListInterface;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Модуль спам-рассылки почты. Конфигурируется экземпляром класса MailerConfig.
 * @author Gusev Dmitry (Дмитрий)
 * @version 5.1 (DATE: 04.04.2010)
*/

// todo: бага: если у рассылки указаны типы получателей, но они неверные (т.е. для них нет списков мылофф), то список
// todo: майлов для рассылки может оказаться пустым. Это обработается отправщиком - письма не будут отправлены, но потенциально -
// todo: список может оказаться пустым! (04.04.2011)

public class Mailer
 {
  /** Логгер данного модуля. */
  private static Logger logger = Logger.getLogger(Defaults.LOGGER_NAME);
  /** Конфигурация данного модуля-мейлера. */
  private MailerConfig config = null;

  public Mailer(MailerConfig config) {this.config = config;}

  //public MailerConfig getConfig()            {return config;}
  //public void setConfig(MailerConfig config) {this.config = config;}

  /**
   * Непосредственно метод, осуществляющий массовую рассылку почты. Перед применением ознакомьтесь с инструкцией.
  */
  @SuppressWarnings({"ReuseOfLocalVariable"})
  public void startSpam() throws ConfigurationException {
    logger.debug("Mailer: startSpam().");
    // Проверяем конфиг для работы майлера - если конфиг пуст или содержит ошибки - ничо не делаем! Ошибка!
    if ((this.config != null) && (StringUtils.isBlank(this.config.getConfigErrors())))
     {
      logger.info("Mailer config OK. Processing delivery.");
      // Если включен демо-режим - укажем это
      if (this.config.isDemoMode()) {logger.warn("DEMO-MODE IS ACTIVE. NO MESSAGES WILL BE SENT!");}
      
      // Получаем ссылку на экземпляр класса DeliveriesDAO - она будет использована во многих местах
      DeliveriesDAO deliveriesDAO = new DeliveriesDAO();
      // Если идентификатор рассылки положителен - работаем. Ищем данные по рассылке в табле
      DeliveryDTO delivery = deliveriesDAO.findByID(config.getDeliveryId());
      if ((delivery != null) && (!delivery.isEmpty()))
       {
        logger.debug("Delivery with ID = " + config.getDeliveryId() + " was found. Processing.");

        // Сразу же устанавливаем статус текущей рассылки - в процессе. Данные пробиваем в БД.
        Defaults.DeliveryStatus deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS;
        deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, null);

        // Создаем конфиг для почтового модуля (JMail)
        JMailConfig mailConfig = new JMailConfig();
        mailConfig.setFrom(config.getMailFrom());
        mailConfig.setMailHost(config.getMailHost());
        // Если указан порт (строго больше нуля) - также укажем его
        if (config.getMailPort() > 0) {mailConfig.setMailPort(config.getMailPort());}
        // Кодировка письма (только если указана непустая)
        if (!StringUtils.isBlank(config.getMailEncoding()))
         {
          logger.info("Using mail encoding: " + config.getMailEncoding());
          mailConfig.setEncoding(config.getMailEncoding());
         }
        // Тема
        mailConfig.setSubject(delivery.getSubject());
        // Текст
        mailConfig.setText(delivery.getText());

        // Ошибки рассылки (начальное значение NULL - нет ошибок)
        String deliveryError = null;
      
        // Добавляем аттачменты к письму (если они есть в рассылке и если доступен файловый ресурс). Если в рассылке
        // (в экземпляре класса DeliveryDTO) указаны аттачменты, а физически их нет - рассылка не стартует (будет
        // выведено сообщение в лог и сделана запись в БД рассылок)
        HashSet<DeliveryFileDTO> deliveryFiles = delivery.getFiles();
        if ((deliveryFiles != null) && (!deliveryFiles.isEmpty()))
         {
          logger.info("There are [" + deliveryFiles.size() + "] file(s) for this emails delivery. Checking.");
          // Если указанный в качестве параметра путь к файловому репозитарию путь ок - работаем
          String filesPath = this.config.getDeliveriesFilesPath();
          if ((!StringUtils.isBlank(filesPath)) && (new File(filesPath).exists()))
           {
            logger.debug("Files repository path [" + filesPath + "] is ok.");
            String path = MyIOUtils.fixFPath(filesPath, true); // <- коректировка пути к файлам с добавлением конечного слеша

            // В цикле проходим по всем файлам, указанным в классе рассылки и, если они найдены, добавляем к
            // конфигурации модуля почтовой рассылки (JMail). Если хоть один из файлов не найден - ошибка, рассылка
            // осуществлена не будет.
            for (DeliveryFileDTO deliveryFile : deliveryFiles)
             {
              // Дополнительная проверка - не пуст ли полученный файл. Если пуст - ошибка!
              if ((deliveryFile != null) && (!deliveryFile.isEmpty()))
               {
                String oneAttachmentPath = path + delivery.getId() + "/" + deliveryFile.getFileName();
                // Если файл есть (существует) - добавляем его к конфигу майлера
                if (new File(oneAttachmentPath).exists() && new File(oneAttachmentPath).isFile())
                 {mailConfig.addFile(oneAttachmentPath);}
                // Если файла нет - ошибка!
                else
                 {
                  deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
                  deliveryError  = "Attachment file [" + oneAttachmentPath + "] not found or not a file!";
                  // Сообщим в лог
                  logger.error(deliveryError);
                  // Пробьем в базу рассылок
                  deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
                 }
               }
              // Если получен пустой файл из списка - ошибка!
              else
               {
                deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
                deliveryError  = "Internal error! Empty files in delivery's attachments list!";
                // Сообщим в лог
                logger.error(deliveryError);
                // Пробьем в базу рассылок
                deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
               }
             }

           }
          // Если же указанный путь ошибочен (но файлы в рассылке указаны) - рассылка не осуществляется.
          else
           {
            deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
            deliveryError  = "There are [" + deliveryFiles.size() + "] file(s) for this delivery, " +
                             "but file repository path [" + filesPath + "] doesn't exists!";
            // Сообщим в лог
            logger.error(deliveryError);
            // Пробьем в базу рассылок
            deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
           }
         }
        // Если файлов в рассылке нет - просто сообщим в лог
        else {logger.info("This emails delivery attachments-free!");}

        // Если проверка файлов (и их добавление в конфиг майлера) завершилась удачно или для данной рассылки
        // вообще нет аттаченных файлов - продолжаем работу
        if (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS)
         {
          logger.debug("Files processing completed. Continue delivery processing.");

          // Получаем список адресов для рассылки. Если указан параметр командной строки -testMailTo, то почта
          //отправляется только на адрес(а), указанный(-ые) в данном параметре. Значение параметра считается за одну
          // запись.
          TreeMap<String, Integer> emailsList = null;
          if (StringUtils.isBlank(this.config.getTestMailTo()))
           {
            logger.info("Standard delivery. Processing emails list.");
            // Если у данной рассылки указаны получатели - обработаем их.
            if ((delivery.getRecipients() != null) && (!delivery.getRecipients().isEmpty()))
             {
              logger.info("This delivery had [" + delivery.getRecipients().size() + "] recipients types. Processing.");
              EmailsListInterface builder;
              // Конечный список майл-адресов для рассылки
              TreeMap<String, Integer> list;
              // В цикле формируется сам список мылофф для рассылки
              for (RecipientTypeDTO type : delivery.getRecipients())
               {
                if ((type != null) && (type.getRecipientType() != null))
                 {
                  switch (type.getRecipientType())
                   {
                    // Тестовый список рассылки (отдел 019)
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
                    // Русскоязычные судовладельцы
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
                    // Англоязычные судовладельцы
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

              // Если во время обработки в список мылофф для отправки было что-то добавлено, то добавляем в этот список
              // майл-адреса из таблицы mandatoryEmails - те адреса, куда должны отправляться письма при ЛЮБОЙ рассылке.
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
            // Если же получателей нет - ничего не рассылаем, ибо некуда.
            else {logger.error("This delivery had [0] recipients types. Can't process!");}
           }
          // Тестовая рассылка
          else
           {
            logger.info("Test delivery. Processing.");
            emailsList = new TreeMap<String, Integer>();
            emailsList.put(this.config.getTestMailTo(), 0);
           }

          // Если список мылоф не пуст - работаем
          if ((emailsList != null) && (!emailsList.isEmpty()))
           {
            logger.info("Emails list contains [" + emailsList.size() + "] address(es). Processing.");

            // Экземпляр класса "письмо" - для пробивания в журнал отправки
            EmailDTO email = new EmailDTO();
            email.setDeliveryId(delivery.getId());

            // Сам почтовик
            JMail mailer = new JMail(mailConfig);
            // Непосредственно цикл отправки спама
            Set<String> keys = emailsList.keySet();
            int    companyId;
            String sendResult = null;
            // Класс для пробития инфы в БД (в таблицу мылов)
            EmailsDAO emailsDAO = new EmailsDAO();

            logger.info("Starting emails delivery...");
            // Счетчик обработанных мылов
            int counter = 0;
            for (String key : keys)
             {
              companyId = emailsList.get(key);
              // Удаляем неверные разделители (если вдруг в одной строчке несколько мыл). Все неверные разделители,
              // указанные в строке будут заменены на верные.
              String strEmail = key;
              for (String aWrong : Defaults.EMAILS_DELIMITERS_WRONG)
               {strEmail = strEmail.replaceAll(aWrong, Defaults.EMAILS_DELIMITER_RIGHT);}
              // Отладочное сообщение
              logger.debug("Email before process: [" + key + "], email after process (for send): [" + strEmail + "]. ");

              // Пробиваем мыло в конфиг почтовика
              mailConfig.setTo(strEmail);
              // Пробиваем данные в экземпляр класса "письмо" - для отправки в БД (на MS SQL)
              email.setCompanyId(companyId);
              email.setEmail(strEmail);
              // Отправляем письмеццо. Если включен демо-режим, то письмо не отправляется, а просто
              // сообщается о его отправке. Запись в БД также делается.
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
                // Результат отправки письма по данному адресу (для БД)
                sendResult = e.getMessage();
               }
              // Добиваем данные в экземпляр класса "письмо" и пробиваем все в БД (результат отправки письма)
              if (!StringUtils.isBlank(sendResult))
               {
                email.setErrorText(sendResult);
                // Если есть сообщение и мы не в демо-режиме - ошибка! Пробиваем в базу (таблицу) ё-мыл.
                if (!this.config.isDemoMode()) {email.setStatus(Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED.getIntValue());}
                // Статус рассылки - с ошибками. Выставляем однократно при возникновении любой ошибки
                // во время отправки писем по списку. Если же мы в демо-режиме, то статус всей рассылки будет ОК.
                if ((!this.config.isDemoMode()) && (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS))
                 {
                  deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FINISHED_WITH_ERRORS;
                  deliveryError  = "There are delivery errors. See emails log for this delivery.";
                 }
                // Обнуляем результат
                sendResult = null;
               }
              // Пробиваем в БД инфу об отправке данных (письма) на адрес
              emailsDAO.change(email);
              // Обнуляем поля errorText и Status
              email.setErrorText(null);
              email.setStatus(Defaults.DeliveryStatus.DELIVERY_STATUS_OK.getIntValue());
              // Увеличиваем счетчик обработанных мыл
              counter++;
             } // Конец цикла отправки писем по списку

            logger.info("Emails delivery finished. [" + counter + "] email(s) processed. See log or database for results.");
            // По окончании цикла рассылки - пробиваем в БД статус завершения для рыссылки. Если ошибок нет -
            // пробиваем статус - "успешно", если же есть ошибки, то пробивается статус "неудача"
            if (deliveryStatus == Defaults.DeliveryStatus.DELIVERY_STATUS_IN_PROCESS)
             {
              deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_OK;
              deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, null);
             }
            else
             {new DeliveriesDAO().setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);}
           }
          // Если список мылоф пуст - ошибка. Сообщаем в лог. Пробиваем в БД рассылок
          else
           {
            deliveryStatus = Defaults.DeliveryStatus.DELIVERY_STATUS_FAILED;
            deliveryError  = "Emails list is empty! Can't process delivery!";
            // Сообщим в лог
            logger.error(deliveryError);
            // Пробьем в базу рассылок
            deliveriesDAO.setStatusAndError(this.config.getDeliveryId(), deliveryStatus, deliveryError);
           }
         }
        // Если же предыдущие проверки закончились неудачно - рассылка не осуществляется
        else {logger.error("Can't process emails delivery due previous errors. See log!");}
       }
      // Если идентификатор рассылки меньше нуля - сразу ошибка
      else {logger.error("Can't start spam - wrong delivery ID [" + this.config.getDeliveryId() + "].");}
     }
    // Если же конфиг пуст или содержит ошибки - ничо не делаем и сообщаем в лог
    else
     {
      // В зависимости от типа ошибочности конфига - сообщение в лог
      if (this.config == null) {logger.error("Mailer config is NULL!");}
      else                     {logger.error("Mailer config invalid! " + this.config.getConfigErrors());}
     }
   }

}