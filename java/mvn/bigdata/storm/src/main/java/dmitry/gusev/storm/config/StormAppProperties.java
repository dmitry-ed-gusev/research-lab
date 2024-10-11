package dmitry.gusev.storm.config;

/**
 * StormAppProperties - whole application config (common properties, 
 * Marklogic client props, ActiveMQ client props, etc.).
 * @author Gusev Dm. (dgusev)
 * @version 3.0 (DATE: 05.05.2016)
 */

import com.marklogic.client.DatabaseClientFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public final class StormAppProperties implements Serializable {

    private static final Log log = LogFactory.getLog(StormAppProperties.class);

    // MarkLogic
    private final String mlHost;
    private final int    mlPort;
    private final String mlAdminUser;
    private final String mlAdminPassword;
    private final String mlReaderUser;
    private final String mlReaderPassword;
    private final String mlWriterUser;
    private final String mlWriterPassword;
    private final DatabaseClientFactory.Authentication mlAuthType;
    // ActiveMQ
    private final String mqUser;
    private final String mqPassword;
    private final String mqURL;
    private final String mqQueueName;
    // Apache Storm
    private final int stInText2MLexe;
    private final int stInText2MLtsk;
    private final int stInML2JMSexe;
    private final int stInML2JMStsk;
    private final int stOutJMS2MLexe;
    private final int stOutJMS2MLtsk;
    private final int stOutML2TexTexe;
    private final int stOutML2TexTtsk;
    // Common
    private final String sourceFile;
    private boolean      isGenerateSourceFile;
    private long         generateSourceFileLines;
    private final String destinationFile;
    private final String xmlTagName;
    private final int    linesCount;
    private final int    processedLinesCount;
    private boolean      debugExceptions;
    private int          stormMessageTimeout;

    /***/
    public StormAppProperties(String configName) throws IOException {
        log.debug("StormAppProperties constructor() working.");

        try (InputStream stream = new FileInputStream(configName)) {
            // read Properties from a file
            Properties properties = new Properties();
            properties.load(stream);
            // read Marklogic properties
            this.mlHost = properties.getProperty("marklogic.host");
            this.mlPort = Integer.parseInt(properties.getProperty("marklogic.port")); // no error processing (FAIL FAST!)
            this.mlAdminUser = properties.getProperty("marklogic.admin.user");
            this.mlAdminPassword = properties.getProperty("marklogic.admin.password");
            this.mlReaderUser = properties.getProperty("marklogic.reader.user");
            this.mlReaderPassword = properties.getProperty("marklogic.reader.password");
            this.mlWriterUser = properties.getProperty("marklogic.writer.user");
            this.mlWriterPassword = properties.getProperty("marklogic.writer.password");
            this.mlAuthType = DatabaseClientFactory.Authentication.valueOf(properties.getProperty("marklogic.authentication.type").toUpperCase());
            // read ActiveMQ properties
            this.mqUser = properties.getProperty("activemq.user");
            this.mqPassword = properties.getProperty("activemq.password");
            this.mqURL = properties.getProperty("activemq.url");
            this.mqQueueName = properties.getProperty("activemq.queue.name");
            // read Apache Storm properties
            this.stInText2MLexe = Integer.parseInt(properties.getProperty("storm.topology.input.text2ml.executors"));
            this.stInText2MLtsk = Integer.parseInt(properties.getProperty("storm.topology.input.text2ml.tasks"));
            this.stInML2JMSexe = Integer.parseInt(properties.getProperty("storm.topology.input.ml2jms.executors"));
            this.stInML2JMStsk = Integer.parseInt(properties.getProperty("storm.topology.input.ml2jms.tasks"));
            this.stOutJMS2MLexe = Integer.parseInt(properties.getProperty("storm.topology.output.jms2ml.executors"));
            this.stOutJMS2MLtsk = Integer.parseInt(properties.getProperty("storm.topology.output.jms2ml.tasks"));
            this.stOutML2TexTexe = Integer.parseInt(properties.getProperty("storm.topology.output.ml2text.executors"));
            this.stOutML2TexTtsk = Integer.parseInt(properties.getProperty("storm.topology.output.ml2text.tasks"));
            // check storm parallells config
            if (this.stInML2JMSexe <= 0 || this.stInText2MLexe <= 0 || this.stOutJMS2MLexe <= 0 || this.stOutML2TexTexe <= 0) {
                throw new IllegalStateException("Invalid (negative) Apache Storm executors value(s)!");
            }
            // Common
            this.xmlTagName = properties.getProperty("common.xml.tag");
            this.sourceFile = properties.getProperty("common.source.file");
            this.isGenerateSourceFile = Boolean.parseBoolean(properties.getProperty("common.source.file.generate"));
            this.generateSourceFileLines = Long.parseLong(properties.getProperty("common.source.file.generate.size"));
            this.destinationFile = properties.getProperty("common.destination.file");
            this.linesCount = Integer.parseInt(properties.getProperty("common.lines.count")); // no error processing (FAIL FAST!)
            this.processedLinesCount = Integer.parseInt(properties.getProperty("common.processed.lines.count")); // no error processing (FAIL FAST!)
            this.debugExceptions = Boolean.parseBoolean(properties.getProperty("common.debug.exceptions"));
            this.stormMessageTimeout = Integer.parseInt(properties.getProperty("common.storm.message.timeout")); // no error processing (FAIL FAST!)
        }

    }

    public String getMlHost() {
        return mlHost;
    }

    public int getMlPort() {
        return mlPort;
    }

    public String getMlAdminUser() {
        return mlAdminUser;
    }

    public String getMlAdminPassword() {
        return mlAdminPassword;
    }

    public String getMlReaderUser() {
        return mlReaderUser;
    }

    public String getMlReaderPassword() {
        return mlReaderPassword;
    }

    public String getMlWriterUser() {
        return mlWriterUser;
    }

    public String getMlWriterPassword() {
        return mlWriterPassword;
    }

    public DatabaseClientFactory.Authentication getMlAuthType() {
        return mlAuthType;
    }

    public String getMqUser() {
        return mqUser;
    }

    public String getMqPassword() {
        return mqPassword;
    }

    public String getMqURL() {
        return mqURL;
    }

    public String getMqQueueName() {
        return mqQueueName;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public boolean isGenerateSourceFile() {
        return isGenerateSourceFile;
    }

    public long getGenerateSourceFileLines() {
        return generateSourceFileLines;
    }

    public String getDestinationFile() {
        return destinationFile;
    }

    public String getXmlTagName() {
        return xmlTagName;
    }

    public int getLinesCount() {
        return linesCount;
    }

    public int getProcessedLinesCount() {
        return processedLinesCount;
    }

    public boolean isDebugExceptions() {
        return debugExceptions;
    }

    public int getStormMessageTimeout() {
        return stormMessageTimeout;
    }

    public int getStInText2MLexe() {
        return stInText2MLexe;
    }

    public int getStInText2MLtsk() {
        return stInText2MLtsk;
    }

    public int getStInML2JMSexe() {
        return stInML2JMSexe;
    }

    public int getStInML2JMStsk() {
        return stInML2JMStsk;
    }

    public int getStOutJMS2MLexe() {
        return stOutJMS2MLexe;
    }

    public int getStOutJMS2MLtsk() {
        return stOutJMS2MLtsk;
    }

    public int getStOutML2TexTexe() {
        return stOutML2TexTexe;
    }

    public int getStOutML2TexTtsk() {
        return stOutML2TexTtsk;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("mlHost", mlHost)
                .append("mlPort", mlPort)
                .append("mlAdminUser", mlAdminUser)
                .append("mlAdminPassword", mlAdminPassword)
                .append("mlReaderUser", mlReaderUser)
                .append("mlReaderPassword", mlReaderPassword)
                .append("mlWriterUser", mlWriterUser)
                .append("mlWriterPassword", mlWriterPassword)
                .append("mlAuthType", mlAuthType)
                .append("mqUser", mqUser)
                .append("mqPassword", mqPassword)
                .append("mqURL", mqURL)
                .append("mqQueueName", mqQueueName)
                .append("stInText2MLexe", stInText2MLexe)
                .append("stInText2MLtsk", stInText2MLtsk)
                .append("stInML2JMSexe", stInML2JMSexe)
                .append("stInML2JMStsk", stInML2JMStsk)
                .append("stOutJMS2MLexe", stOutJMS2MLexe)
                .append("stOutJMS2MLtsk", stOutJMS2MLtsk)
                .append("stOutML2TexTexe", stOutML2TexTexe)
                .append("stOutML2TexTtsk", stOutML2TexTtsk)
                .append("sourceFile", sourceFile)
                .append("isGenerateSourceFile", isGenerateSourceFile)
                .append("generateSourceFileLines", generateSourceFileLines)
                .append("destinationFile", destinationFile)
                .append("xmlTagName", xmlTagName)
                .append("linesCount", linesCount)
                .append("processedLinesCount", processedLinesCount)
                .append("debugExceptions", debugExceptions)
                .append("stormMessageTimeout", stormMessageTimeout)
                .toString();
    }

}
