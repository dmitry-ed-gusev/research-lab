package gusev.dmitry.jtils.mail.jmailer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One email message class.
 * Class is unmodifiable.
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 31.12.13)
*/

public class EMailMessage {

    private final Log log = LogFactory.getLog(EMailMessage.class);

    // regex patterns for remove from message text
    private static final String  HTML_TAG_PATTERN       = "<.*?>";
    private static final String  HTML_TAG_NBSP          = "&nbsp;";
    private static final String  HTML_TAG_COMMENT       = "(?s)<!--.*?-->";
    // regex pattern for split (remove empty lines)
    private static final String  EMPTY_LINES_PATTERN    = "\\s*\\n\\s*";
    // regex pattern for extract email address (from fields [from], [replyTo])
    private static final String  ADDRESS_PATTERN_STRING = "<(.*?)>";
    private static final Pattern ADDRESS_PATTERN        = Pattern.compile(ADDRESS_PATTERN_STRING);
    //
    private static final String  TO_FIELD_PATTERN       = ".*<|>.*";

    private String     from;         // email sender
    private Date       sentDate;     // email sent date
    private String     to;           // list of email addresses (recipients)
    private String     replyTo;      //
    private String     subject;      // email subject
    private String     text;         // original email text (with tags)
    private String     filteredText; // filtered email text (removed HTML)
    private List<File> attachments;  // list of email attachments

    /***/
    public EMailMessage(String from, Date sentDate, String to, String replyTo, String subject,
                        String text, List<File> attachments) {

        this.from         = this.filterAddress(from);    // [from] email address
        this.sentDate     = sentDate;                    // [sentDate]

        // [to] field - emails addresses list
        if (!StringUtils.isBlank(to)) {
            this.to = to.replaceAll(TO_FIELD_PATTERN, "").trim();
        } else {
            this.to = to;
        }

        this.replyTo      = this.filterAddress(replyTo); // [replyTo] email address
        this.subject      = subject;
        this.text         = text;
        this.filteredText = this.filterText(text);

        // attachments saving (list of File objects)
        if (attachments != null && !attachments.isEmpty()) {
            this.attachments = Collections.unmodifiableList(attachments);
        } else {
            this.attachments = null;
        }
    }

    /***/
    private String filterAddress(String address) {
        String filteredAddress = null;
        // extract email address data
        if (!StringUtils.isBlank(address)) {
            if (address.contains("<") || address.contains(">")) { // extract [from] email address
                Matcher matcher = ADDRESS_PATTERN.matcher(address);
                if (matcher.find()) { // match found
                    filteredAddress = matcher.group(1);
                } else { // match doesn't found
                    log.error("Address doesn't match pattern -> [" + ADDRESS_PATTERN_STRING + "].");
                }
            } else { // get [from] address "as is"
                filteredAddress = address;
            }
        }
        // return result
        return filteredAddress;
    }

    /***/
    private String filterText(String text) {
        String filteredText = null;

        if (!StringUtils.isBlank(text)) {
            // remove from message: html tags, [&nbsp;] symbol, html comments
            String   tmpMsg = text.replaceAll(HTML_TAG_PATTERN, "").replaceAll(HTML_TAG_NBSP, "\n").replaceAll(HTML_TAG_COMMENT, "");
            // remove all empty lines from message
            String[] tmpArr = tmpMsg.split(EMPTY_LINES_PATTERN);
            StringBuilder tmpBuilder = new StringBuilder();
            for (String tmpStr : tmpArr) {
                tmpBuilder.append(tmpStr).append("\n");
            }
            filteredText = tmpBuilder.toString();
        }

        return filteredText;
    }

    public String getFrom() {
        return from;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public String getTo() {
        return to;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getFilteredText() {
        return filteredText;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("sentDate", sentDate)
                .append("to", to)
                .append("replyTo", replyTo)
                .append("subject", subject)
                .append("text", text)
                .append("filteredText", filteredText)
                .append("attachments", attachments)
                .toString();
    }

}