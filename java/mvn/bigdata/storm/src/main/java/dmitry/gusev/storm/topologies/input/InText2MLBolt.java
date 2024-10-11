package dmitry.gusev.storm.topologies.input;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 1st bolt for input (producer) topology. Receives tuple with text line, wraps up line into XML
 * document and writes XML document to Marklogic. After writing receives from Marklogic document
 * URI. Emits received URI to next tuple in a topology.
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 07.04.2016)
 */

public class InText2MLBolt extends BaseRichBolt {

    private static final Log log = LogFactory.getLog(InText2MLBolt.class);

    private final StormAppProperties properties;
    private OutputCollector outputCollector;
    // Marklogic related entities
    transient private DatabaseClient mlClient;
    transient private XMLDocumentManager  docMgr;
    transient private DocumentUriTemplate uriTemplate;
    transient private InputStreamHandle   handle;

    /***/
    public InText2MLBolt(StormAppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        log.debug("InText2MLBolt.prepare() working.");
        this.outputCollector = collector;
        // Marklogic objects
        this.mlClient = DatabaseClientFactory.newClient(this.properties.getMlHost(), this.properties.getMlPort(), this.properties.getMlWriterUser(),
                this.properties.getMlWriterPassword(), this.properties.getMlAuthType());  // MarkLogic client
        this.docMgr = this.mlClient.newXMLDocumentManager();          // create a manager for XML documents
        this.uriTemplate = this.docMgr.newDocumentUriTemplate("xml"); // URI template that says: "use an XML extension to generate URIs"
        this.handle = new InputStreamHandle();                        // create a handle on the content
    }

    @Override
    public void execute(Tuple input) {
        log.trace(String.format("InText2MLBolt.execute() working. Input size [%s].", input.getValues().size())); // <- too much output

        String fileLine = input.getStringByField(StormAppDefaults.STORM_TEXT_LINE_FIELD);
        // create string representation of XML document
        String strXmlDoc = String.format(StormAppDefaults.XML_TEMPLATE, this.properties.getXmlTagName(),
                fileLine.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;"), this.properties.getXmlTagName());

        try { // process data and put it into MarkLogic and emit tuple to the next BOLT

            // todo: just a debug case!!!
            if (this.properties.isDebugExceptions() && fileLine.startsWith("=")) {
                throw new UnsupportedEncodingException("-TEST PRODUCER EXCEPTION-");
            }

            InputStream docStream = new ByteArrayInputStream(strXmlDoc.getBytes(StormAppDefaults.DEFAULT_ENCODING));
            this.handle.set(docStream);
            // write the document content, returning a document descriptor.
            DocumentDescriptor documentDescriptor = this.docMgr.create(this.uriTemplate, this.handle);

            if (log.isTraceEnabled()) { // avoid performance/memory issues (and too much output)
                log.trace(String.format("String -> [%s], Marklogic doc URI -> [%s]", fileLine, documentDescriptor.getUri()));
            }

            // emit data to the next bolt (with explicit anchor and acking)
            this.outputCollector.emit(input, new Values(documentDescriptor.getUri()));
            this.outputCollector.ack(input);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            this.outputCollector.fail(input); // fail in case of exception
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("InText2MLBolt.declareOutputFields() working.");
        declarer.declare(new Fields(StormAppDefaults.STORM_MARKLOGIC_URI_FIELD));
    }

    @Override
    public void cleanup() {
        log.debug("InText2MLBolt.cleanup() working.");
        if (this.mlClient != null) { // free ML resources
            this.mlClient.release();
        }
    }

}
