package gusev.dmitry.jtils.mail.console;

import gusev.dmitry.utils.CmdLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.MessagingException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Console utility for sending email messages.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 14.04.2014)
*/

public class SendJMailConsole {

    private static final String ATTACHED_FILES_DELIMITER = ",";

    /** Enumeration - command line options for sendjmail utility. */
    public enum MailCmdLineOptions {

        MAIL_HOST("-mailHost"), MAIL_PORT("-mailPort"), MAIL_USER("-mailUser"), MAIL_PASS("-mailPass"), // system settings
        MAIL_FROM_ADDRESS("-from"), MAIL_SUBJECT("-subject"), MAIL_TEXT("-text"), MAIL_FILES("-files"), // mail message parameters
        MAIL_RECIPIENTS("-recipients");

        private MailCmdLineOptions(String optionName) { // private constructor
            this.optionName = optionName;
        }

        private String optionName; // internal data for every option - name

        public String getOptionName() {
            return optionName;
        }
    }

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(SendJMailConsole.class);
        log.debug("Starting email sending...");
        // parse command line (create object)
        CmdLine cmdLine = new CmdLine(args);
        // email message object
        EmailMessage message = new EmailMessage();
        message.setSubject(cmdLine.optionValue(MailCmdLineOptions.MAIL_SUBJECT.getOptionName()));
        message.setText(cmdLine.optionValue(MailCmdLineOptions.MAIL_TEXT.getOptionName()));
        // if there are attached files - process it
        String files = cmdLine.optionValue(MailCmdLineOptions.MAIL_FILES.getOptionName());
        log.debug("Option -files value -> [" + files + "].");
        if (!StringUtils.isBlank(files)) { // option isn't empty
            log.debug("There are attached files. Processing.");
            Map<String, File> attachedFiles = new HashMap<>();
            String[] filesArray = files.split(ATTACHED_FILES_DELIMITER);
            if (filesArray.length > 0) { // processing attached files option value
                for (String oneFile : filesArray) {
                    log.debug("Processing file -> " + oneFile);
                    File oneAttachedFile = new File(StringUtils.trimToEmpty(oneFile));
                    if (oneAttachedFile.exists() && oneAttachedFile.isFile()) {
                        attachedFiles.put(StringUtils.trimToEmpty(oneFile), oneAttachedFile);
                        log.debug("File [" + oneFile + "] was added to attachements list.");
                    }
                }
            }
            if (!attachedFiles.isEmpty()) { // there are existing files
                message.setFiles(attachedFiles);
            }
        } else { // attached files option value is empty
            log.info("There are no attached files.");
        }

        // Send mail utility class preparing
        try {
            // send mail class instance
            SendMail sendMail = new SendMail(
                    cmdLine.optionValue(MailCmdLineOptions.MAIL_HOST.getOptionName()),                   // host
                    Integer.parseInt(cmdLine.optionValue(MailCmdLineOptions.MAIL_PORT.getOptionName())), // port
                    cmdLine.optionValue(MailCmdLineOptions.MAIL_USER.getOptionName()),                   // user
                    cmdLine.optionValue(MailCmdLineOptions.MAIL_PASS.getOptionName()),                   // pass
                    cmdLine.optionValue(MailCmdLineOptions.MAIL_FROM_ADDRESS.getOptionName())            // from email
            );
            // message to send
            sendMail.setMessage(message);
            // recipients
            String recipients = cmdLine.optionValue(MailCmdLineOptions.MAIL_RECIPIENTS.getOptionName());
            sendMail.setRecipients(recipients);
            // sending email
            sendMail.sendMessage();
            log.info(String.format("Email to [%s] was sent.", recipients));
        } catch (NumberFormatException /* port parse */ | MessagingException /* sending message */ e) {
            log.error("Can't create SendMail instance!", e);
        }
    }

}