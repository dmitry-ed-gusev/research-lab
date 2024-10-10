package dgusev.mail.console;

import java.io.File;
import java.util.Map;

/**
 * Simple email message for sending.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 20.02.14)
*/

public class EmailMessage {

    private String            subject;
    private String            text;
    private Map<String, File> files;          // attached files (for message)

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public void setFiles(Map<String, File> files) {
        this.files = files;
    }

}