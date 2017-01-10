package gusev.dmitry.jtils.mail.console;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Mail send module for MesUtil application. Module uses SMTP authorization by default.
 * @author Gusev Dmitry (gusevd)
 * @version 2.0 (DATE: 20.02.2014)
*/
// todo: working trough proxy???
public final class SendMail {

    private Log log = LogFactory.getLog(SendMail.class);

    private static final String EMAIL_DEFAULT_ENCODING = "UTF-8";
    private static final String EMAIL_CONTENT_TYPE     = "text/plain; charset=\"%s\"";

    private String            host;              // smtp host
    private String            port;              // smtp port
    private String            user;              // user
    private String            pass;              // pass

    private String            email;             // back (from/reply to) address
    private EmailMessage      message    = null; // message for sending (can be changed - variable)
    private String            recipients = null; // recipients for sending messages

    private Session session    = null; // internal state - mail session

    /***/
    public SendMail(String host, int port, String user, String pass, String email) {

        // fail-fast behaviour
        if (StringUtils.isBlank(host) || StringUtils.isBlank(user) || StringUtils.isBlank(pass) || StringUtils.isBlank(email)) {
            throw new IllegalArgumentException(String.format("Invalid SendMail config: host=[%s], " +
                    "user=[%s], pass=[%s], email=[%s].", host, user, pass, email));
        }

        // mail session properties
        Properties sessionProperties = new Properties();
        sessionProperties.put("mail.smtp.host", host);
        if (port > 0) {
            sessionProperties.put("mail.smtp.port", port);
        }
        // mail session with authenticator
        this.session = Session.getInstance(sessionProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SendMail.this.user, SendMail.this.pass);
            }
        });
        this.email = email;
    }

    public EmailMessage getMessage() {
        return message;
    }

    public void setMessage(EmailMessage message) {
        this.message = message;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    /***/
    public void sendMessage() throws MessagingException {
        log.debug("SendMail.sendMessage() working.");
        if (this.message != null && this.recipients != null) { // message and recipients isn't empty - we will try to send
            log.debug("Message is OK (not empty), sending.");
            Message message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(this.email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));
            message.setSubject(this.message.getSubject());
            message.setSentDate(new Date()); // sent date - now
            // processing message creating
            Map<String, File> files = this.message.getFiles();
            if (files == null || files.isEmpty()) { // there are no files to send - we will send simple message
                log.debug("There are no files for sending - we will send a simple email message.");
                message.setContent(this.message.getText(), String.format(EMAIL_CONTENT_TYPE, EMAIL_DEFAULT_ENCODING));
            } else { // there are files - we will atach them to message
                log.debug(String.format("There are [%s] file(s) for sending - we will send a multipart message.", files.size()));
                // MultiPart message
                Multipart multipart = new MimeMultipart();
                // text part
                MimeBodyPart msgTextPart = new MimeBodyPart();
                msgTextPart.setText(this.message.getText(), EMAIL_DEFAULT_ENCODING);
                multipart.addBodyPart(msgTextPart); // message part of multipart message
                // mail part with attached files
                FileDataSource fds;
                MimeBodyPart filePart;
                for (File file : files.values()) {
                    if (file.exists()) { // we will add file only if exsts
                        filePart = new MimeBodyPart();
                        fds      = new FileDataSource(file);
                        filePart.setDataHandler(new DataHandler(fds));
                        // todo: check name encoding of attached file
                        try {
                            filePart.setFileName(MimeUtility.encodeText(fds.getName()));
                        }
                        catch (UnsupportedEncodingException e) {
                            log.error(e.getMessage());
                        }
                        multipart.addBodyPart(filePart);
                    } else { // attached file doesn't exist
                        log.warn(String.format("File [%s] doesn't exist - can't attach!", file.getName()));
                    }
                } // end of FOR cycle
                // add content to message
                message.setContent(multipart, String.format(EMAIL_CONTENT_TYPE, EMAIL_DEFAULT_ENCODING));
            }
            Transport.send(message);
            log.info("Email to [" + recipients + "] has been sent OK.");
        } else { // internal message or recipients list is empty - we can't send it
            // todo: maybe - throws exception?
            log.error("Internal message object or recipients list is empty - can't send message!");
        }

    }

    /** Just for test. */
    public static void main(String[] args) throws MessagingException, IOException {
        Log log = LogFactory.getLog(SendMail.class);
        log.info("SendMail starting.");

        // one message
        Map<String, File> files = new HashMap<>();
        files.put("file1", new File("c:/temp/111.jpg"));
        files.put("file2", new File("c:/temp/112.jpg"));
        files.put("file3", new File("c:/temp/123.jpg"));
        EmailMessage message = new EmailMessage();
        message.setSubject("test subject");
        message.setText("sample text");
        //message.setFiles(files);

        // send mail class
        SendMail sendMail   = new SendMail("dc-edgemail", 25, "mesdev", "HKs524Bl0t", "mesdev@kzgroup.ru");
        sendMail.message    = message;
        sendMail.recipients = "Dmitriy.GusevD@petrostal.kzgroup.ru";
        sendMail.sendMessage();
        log.info("Message was sent.");
    }

}