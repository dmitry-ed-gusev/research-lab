package gusev.dmitry.jtils.mail.jmailer;

/**
 * Config class for email client.
 * This class is immutable.
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 01.01.14)
 */

public class EMailClientConfig {

    // parameters for mail host/protocol
    private final String  mailProtocol;    // mandatory, mail protocol
    private final String  mailPort;        // optional
    private final String  mailHost;        // mandatory parameter
    // parameters for mail user
    private final String  mailUser;        // mandatory parameter
    private final String  mailDomain;      // optional
    private final String  mailPassword;    // mandatory parameter
    // some parameters for email client module
    private final boolean deleteMessages;
    private final String  mailFolderName;

    /**
     * We use "builder" pattern for named parameters.
     */
    public static class Builder {
        // mandatory fields
        private final String mailProtocol;
        private final String mailHost;
        private final String mailUser;
        private final String mailPassword;
        // optional parameters - default values
        private String  mailPort       = null;
        private String  mailDomain     = null;
        private boolean deleteMessages = false;
        private String  mailFolderName = null;

        /***/
        public Builder(String mailProtocol, String mailHost, String mailUser, String mailPassword) {
            this.mailProtocol = mailProtocol;
            this.mailHost     = mailHost;
            this.mailUser     = mailUser;
            this.mailPassword = mailPassword;
        }

        public Builder mailPort(String value) {
            this.mailPort = value;
            return this;
        }

        public Builder mailDomain(String value) {
            this.mailDomain = value;
            return this;
        }

        public Builder deleteMessages(boolean value) {
            this.deleteMessages = value;
            return this;
        }

        public Builder mailFolderName(String value) {
            this.mailFolderName = value;
            return this;
        }

        public EMailClientConfig build() {
            return new EMailClientConfig(this);
        }

    } // end of internal Builder class (pattern BUILDER)

    /***/
    private EMailClientConfig(Builder builder) {
        this.mailProtocol   = builder.mailProtocol;
        this.mailPort       = builder.mailPort;
        this.mailHost       = builder.mailHost;
        this.mailUser       = builder.mailUser;
        this.mailDomain     = builder.mailDomain;
        this.mailPassword   = builder.mailPassword;
        this.deleteMessages = builder.deleteMessages;
        this.mailFolderName = builder.mailFolderName;
    }

    public String getMailProtocol() {
        return mailProtocol;
    }

    public String getMailPort() {
        return mailPort;
    }

    public String getMailHost() {
        return mailHost;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public boolean isDeleteMessages() {
        return deleteMessages;
    }

    public String getMailFolderName() {
        return mailFolderName;
    }

}