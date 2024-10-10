package dgusev.mail.old;

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
 * Данный класс реализует отправку сообщения по электронной почте. Если к письму приложены файлы, они также будут
 * отправлены. Данный класс использует библиотеку javamail 1.3.3 для работы с эл. почтой. Для отправки файла исп-ся
 * протокол SMTP без авторизации. Отправка осуществляется по адресу(адресам), указанным в поле to. При этом, если
 * используется список адресов, то адреса в списке разделяются запятыми.
 *
 * @author Gusev Dmitry. Copyright (c) 2005, Gusev Dmitry, dept. 019.
 * @version 6.0 (09.12.2010)
 */

public class JMail {

    private final static String JMAIL_MAILER = "JMAIL-MAILER-BY-GUS";

    /**
     * Класс ведения журнала.
     */
    private Logger logger = Logger.getLogger(getClass().getName());
    /**
     * Конфигурация модуля-почтовика.
     */
    private JMailConfig config = null;

    /**
     * Конструктор по умолчанию. Конструктор инициализирует конфигурацию класса.
     *
     * @param config JMailConfig конфигурация модуля-мейлера.
     */
    public JMail(JMailConfig config) {
        this.config = config;
    }

    public JMailConfig getConfig() {
        return config;
    }

    public void setConfig(JMailConfig config) {
        this.config = config;
    }

    /**
     * Метод для непосредственной отправки письма. Если во время отправки произошла ошибка - возбуждается ИС, которая будет
     * содержать информацию о возникшей ошибке. Если массив files_list содержит хотя бы одно имя файла, то письмо будет
     * MultiPart, если же нет ни одного имени файла, то будет отправлено простое письмо.
     *
     * @throws MessagingException ИС - ошибки при отправке почтового сообщения.
     */
    public void send() throws MessagingException {
        logger.debug("WORKING JMail.send().");
        // Если текущий конфиг не пуст - работаем
        if ((this.config != null) && (!this.config.isEmpty())) {
            logger.debug("JMail config ok. Processing.");
            // Подготовка общих данных для обоих типов почтового сообщения (MultiPart и Plain)
            Properties props = System.getProperties();
            props.put("mail.smtp.host", config.getMailHost());

            // Порт добавляем только если он указан - положителен и не равен 0
            if (config.getMailPort() > 0) {
                props.put("mail.smtp.port", config.getMailPort());
            }

            Session session = Session.getInstance(props, null);

            // Создаем объект сообщения
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getFrom())); // <- от кого
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getTo(), false)); // <- куда/кому
            msg.setSubject(config.getSubject(), config.getEncoding()); // <- тема письма
            // Дата отправки письма - всегда текущая дата
            msg.setSentDate(new Date());
            // Программа-майлер
            msg.setHeader("X-Mailer", JMAIL_MAILER);

            // Проверяем существование всех прикрепленных файлов (если они есть) и
            // выставляем флажок физического наличия файлов
            boolean isFilesExists = false;
            if ((config.getFilesList() != null) && (!config.getFilesList().isEmpty())) {
                for (String file : config.getFilesList()) {
                    if (new File(file).exists()) {
                        isFilesExists = true;
                    }
                }
            }

            // Если есть файлы для отправки - создаем письмо типа MultiPart
            if (isFilesExists) {
                logger.debug("There are files for this mail. Using Multipart message.");
                // Создание объекта MultiPart и добавление к нему частей
                Multipart mp = new MimeMultipart();
                // Сначала добавим в почтовое сообщение текст - первая часть письма
                MimeBodyPart mbp = new MimeBodyPart();
                mbp.setText(config.getText(), config.getEncoding());
                mp.addBodyPart(mbp);
                // Добавим к почтовому сообщению файлы (в цикле) - остальные части письма
                FileDataSource fds;
                for (String file : config.getFilesList()) {
                    mbp = new MimeBodyPart();
                    fds = new FileDataSource(file);
                    mbp.setDataHandler(new DataHandler(fds));

                    // todo: доразобраться с кодировкой имени вложенного файла
                    try {
                        mbp.setFileName(MimeUtility.encodeText(fds.getName()));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage());
                    }

                    mp.addBodyPart(mbp);
                }
                // Добавление объекта Multipart в письмо
                msg.setContent(mp, "text/plain; charset=\"" + config.getEncoding() + "\"");
            }
            // Если файлов для отправки нет - простое письмо
            else {
                logger.debug("There are no files for this mail.");
                msg.setContent(config.getText(), "text/plain; charset=\"" + config.getEncoding() + "\"");
            }
            // Непосредственно отправка мыльца
            Transport.send(msg);
        }
        // Если же текущий конфиг пуст - ошибка. Сообщаем в лог.
        else {
            logger.error("Empty JMail config! Can't process!");
        }
    }

    /**
     * Метод main - только для тестирования данного класса.
     *
     * @param args String[] аргументы командной строки для класса main.
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(JMail.class.getName());

        JMailConfig config = new JMailConfig();
        config.setTo("019gus@rs-head.spb.ru");
        config.setFrom("019gus@rs-head.spb.ru");
        config.setSubject("тест!");
        config.setMailHost("10.1.254.70");
        config.setText("простой тест!!!");
        config.addFile("D:\\my_docs\\ПРОЕКТЫ\\Java\\j2ee\\updaterService\\production\\client\\updaterClient.zip");

        JMail mail = new JMail(config);
        try {
            //for (int i = 0; i < 100; i++) {mail.send();}
            //config.setTo("brusakov@rs-head.spb.ru");
            //for (int i = 0; i < 100; i++) {mail.send();}
            mail.send();
            logger.debug("Mail was sent.");
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

}