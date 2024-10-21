package dmitry.gusev.storm.topologies.output;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.xpath.*;
import java.util.Map;

/**
 * 1st bolt for output (consumer) topology. Receives tuple with Marklogic doc URI, reads doc (XML) from ML,
 * extraxts (with XPath) text string and emit new tuple to the next bolt with this string.
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 08.04.2016)
 */

public class OutML2TextBolt extends BaseRichBolt {

    private static final Log log = LogFactory.getLog(OutML2TextBolt.class);

    private final StormAppProperties properties;
    private OutputCollector outputCollector;
    // Marklogic related entities
    transient private DatabaseClient mlClient;
    transient private XMLDocumentManager mlXmlDocumentManager;
    // XPath expression
    transient private XPathExpression xpathExpression;

    /***/
    public OutML2TextBolt(StormAppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        log.debug("OutML2TextBolt.prepare() working.");
        this.outputCollector = collector;
        // Marklogic objects
        this.mlClient = DatabaseClientFactory.newClient(this.properties.getMlHost(), this.properties.getMlPort(),
                this.properties.getMlWriterUser(), this.properties.getMlWriterPassword(), this.properties.getMlAuthType());  // MarkLogic client
        this.mlXmlDocumentManager = this.mlClient.newXMLDocumentManager();      // create a manager for XML documents

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            this.xpathExpression = xpath.compile(String.format("/%s", this.properties.getXmlTagName()));
        } catch (XPathExpressionException e) { // wrap checked exception into runtime
            throw new IllegalStateException(e);
        }
    }

    @Override
    // todo: shall we catch MarkLogic runtime exceptions for explicit tuple failing?
    public void execute(Tuple input) {
        log.trace(String.format("OutML2TextBolt.execute() working. Input size [%s].", input.getValues().size())); // <- too much output

        // get document URI from Bolt input
        String docURI = input.getStringByField(StormAppDefaults.STORM_MARKLOGIC_URI_FIELD);
        try {
            Document document = this.mlXmlDocumentManager.readAs(docURI, Document.class);                     // reads from MarkLogic
            String textLine = String.valueOf(this.xpathExpression.evaluate(document, XPathConstants.STRING)); // extract text from XML
            // emit tuple with explicit anchor and acking
            this.outputCollector.emit(input, new Values(textLine));
            this.outputCollector.ack(input);

            if (log.isTraceEnabled()) { // avoid performance/memory issues (and too much output)
                log.debug(String.format("read text line -> [%s]", textLine)); // <- just a debug!
            }
        } catch (XPathExpressionException e) {
            log.error(e.getMessage(), e);
            this.outputCollector.fail(input); // fail input tuple in case of exception
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("OutML2TextBolt.declareOutputFields() working.");
        declarer.declare(new Fields(StormAppDefaults.STORM_TEXT_LINE_FIELD));
    }

    @Override
    public void cleanup() {
        log.debug("OutML2TextBolt.cleanup() working.");
        if (mlClient != null) {
            mlClient.release();
        }
    }

}
