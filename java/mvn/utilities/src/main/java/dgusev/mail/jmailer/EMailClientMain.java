package dgusev.mail.jmailer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 01.01.14)
 */

public class EMailClientMain {

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(EMailClientMain.class);
        log.info("EMail client starting...");

        // email config
        EMailClientConfig emailConfig = new EMailClientConfig.Builder("imaps", "imap.gmail.com",
                "mes.ps.kz", "mes_hae_kz").mailFolderName("inbox").build();
        // email client instance
        EMailClient       emailClient = new EMailClient(emailConfig);
        try {
            // get messages list from mailbox
            List<EMailMessage> messages = emailClient.getMessagesList();
            log.debug(messages.isEmpty() ? "[no messages]" : messages);
        } catch (MessagingException | IOException e) {
            log.error(e);
        }

    }

}