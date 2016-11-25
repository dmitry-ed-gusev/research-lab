package gusev.dmitry.jtils.mail.jmailer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Class for receiving email messages.
 * This class is immutable - if it is initialized, it's config can't be changed!
 *
 * Settings for KZ email service (receiving messages):
 *  - dc-mail:995 (pops -> SSL)
 *  - dc-mail:993 (imaps)
 *  - dc-mail:143 (imap)
 *  - dc-mail:110 (pop3)
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 27.12.13)
*/

public class EMailClient {

    private Log log = LogFactory.getLog(EMailClient.class);

    // attachment file read buffer (bytes)
    private static final int FILE_BUFFER = 4096;

    private static final String MAIL_PROTOCOL     = "pop3";
    //private static final String MAIL_PORT     = "110";
    private static final String MAIL_INBOX_FOLDER = "INBOX";
    private static final String MAIL_USER         = "mesdev";
    private static final String MAIL_DOMAIN       = "kzgroup.ru";
    private static final String MAIL_PASS         = "HKs524Bl0t";
    private static final String MAIL_HOST         = "dc-mail";
    private static final String TMP_FOLDER        = "c:/temp/";

    // config for this email module
    private EMailClientConfig emailConfig;

    /***/
    public EMailClient(EMailClientConfig emailConfig) {
        // todo: field can be null -> we have to fix it!
        if (emailConfig != null) {
            this.emailConfig = emailConfig;
        }
    }

    /***/
    public List<EMailMessage> getMessagesList() throws MessagingException, IOException {
        log.debug("EMailClient - receiving messages.");

        // list of received email messages (result of method)
        ArrayList<EMailMessage> messages = new ArrayList<>();

        Properties props = new Properties();
        // mail protocol and port properties
        props.setProperty("mail.store.protocol", /*MAIL_PROTOCOL*/this.emailConfig.getMailProtocol());

        // mail authenticator
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(/*MAIL_USER, MAIL_PASS*/
                        EMailClient.this.emailConfig.getMailUser(), EMailClient.this.emailConfig.getMailPassword());
            }
        };

        // creating mail session
        Session session = Session.getDefaultInstance(props, authenticator);
        // get messages store instance
        Store store = session.getStore();
        // connect to messages store (for user name maybe we have to use domain: @kzgroup.ru)
        store.connect(/*MAIL_HOST, MAIL_USER, MAIL_PASS*/this.emailConfig.getMailHost(),
                this.emailConfig.getMailUser(), this.emailConfig.getMailPassword());

        // getting folders list (for debug only)
        if (log.isDebugEnabled()) {
            Folder[] folders = store.getDefaultFolder().list();
            log.debug("EMail messages store folders list:");
            for(Folder folder : folders) {
                log.debug("folder -> " + folder.getName());
            }
        }

        Folder folder = store.getFolder(/*MAIL_INBOX_FOLDER*/this.emailConfig.getMailFolderName()); // get emails [INBOX] folder
        folder.open(this.emailConfig.isDeleteMessages() ? Folder.READ_WRITE : Folder.READ_ONLY);     // open emails [INBOX] folder
        // unread messages count
        log.info("All/Unread messages -> [" + folder.getMessageCount() + "/" + folder.getUnreadMessageCount() + "]");

        //Folder folder = store.getFolder("INBOX"); // get messages folder [INBOX] object link
        //folder.open(Folder.READ_WRITE);           // open messages folder (READ_WRITE - we can change, READ_ONLY - just read)
        Message[] msgsList = folder.getMessages();

        // if there are messages in mailbox - process them
        if (msgsList.length > 0) {
            for (int i = 0; i < msgsList.length; i++) {

                log.debug("Processing message [" + (i + 1) + "].");
                //EMailMessage message = new EMailMessage();

                // processing some technical data
                String from     = InternetAddress.toString(msgsList[i].getFrom());
                Date   sentDate = msgsList[i].getSentDate();
                String to       = InternetAddress.toString(msgsList[i].getRecipients(Message.RecipientType.TO));
                String replyTo  = InternetAddress.toString(msgsList[i].getReplyTo());
                String subject  = msgsList[i].getSubject();

                // processing message content
                Object objRef = msgsList[i].getContent();
                String text = null;
                //
                List<File> attachments = new ArrayList<File>();
                if (!(objRef instanceof Multipart)) { // current message isn't multipart
                    log.debug("Email message is [NOT MULTIPART].");
                    text = String.valueOf(msgsList[i].getContent());
                } else {                              // current message is multipart
                    log.debug("Email message is [MULTIPART].");

                    // get multipart message content
                    Multipart multipart = (Multipart) msgsList[i].getContent();

                    // processing parts of multipart message
                    for (int x = 0; x < multipart.getCount(); x++) {
                        BodyPart bodyPart = multipart.getBodyPart(x);   // get message part
                        String disposition = bodyPart.getDisposition(); // part disposition

                        if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) { // mail attachments part(s)
                            // saving one email attachment
                            File file = new File(TMP_FOLDER + bodyPart.getFileName());

                            try (FileOutputStream fos = new FileOutputStream(file)) { // try-with-resources (Java 7+)
                                InputStream is = bodyPart.getInputStream();
                                byte[] buf = new byte[FILE_BUFFER];
                                // reading attachment file content
                                int bytesRead;
                                while((bytesRead = is.read(buf))!=-1) {
                                    fos.write(buf, 0, bytesRead);
                                }
                                // we will add attachment file to list only if it successfully read
                                attachments.add(file);
                            } catch (IOException e) { // catch IO exceptions
                                log.error("Can't save attachment [" + file + "]!", e);
                            }
                        } else { // mail message text part
                            text = String.valueOf(bodyPart.getContent());
                        }
                    }
                }

                // create new email message object
                EMailMessage eMailMessage = new EMailMessage(from, sentDate, to, replyTo, subject, text, attachments);
                //log.debug("RECIEVED MESSAGE: " + eMailMessage); // <- just for debug

                // add received message to emails list
                messages.add(eMailMessage);
                // mark processed message as "deleted" (if option is turned "ON")
                if (this.emailConfig.isDeleteMessages()) {
                    msgsList[i].setFlag(Flags.Flag.DELETED, true);
                }

            } // end of for (processing messages in mailbox)

            folder.close(true);
            store.close();
        } else { // there are no messages
            log.warn("Folder [" + /*MAIL_INBOX_FOLDER*/this.emailConfig.getMailFolderName() + "] is empty.");
        }

        log.debug("EMailClient - mailbox [" + /*MAIL_USER*/this.emailConfig.getMailUser() + "@" +
                /*MAIL_DOMAIN*/this.emailConfig.getMailDomain() + "] processing finished.");
        return messages;
    }

}