package dmitry.gusev.storm.topologies.input;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.lang.IllegalStateException;
import java.util.Map;

/**
 * 2nd bolt for input (producer) topology. Receives tuple with MarkLogic doc URI and puts it
 * as a text message to JMS queue (ActiveMQ).
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 07.04.2016)
 */

public class InML2JMSBolt extends BaseRichBolt {

    private static final Log log = LogFactory.getLog(InML2JMSBolt.class);

    private final StormAppProperties properties;
    private OutputCollector outputCollector;
    // ActiveMQ related entities
    transient private Connection jmsConnection;
    transient private Session jmsSession;
    transient private MessageProducer producer;

    /***/
    public InML2JMSBolt(StormAppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector outputCollector) {
        log.debug("InML2JMSBolt.prepare() working.");
        this.outputCollector = outputCollector;
        try { // connect to JMS
            ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(this.properties.getMqUser(),
                    this.properties.getMqPassword(), this.properties.getMqURL());
            this.jmsConnection = jmsConnectionFactory.createConnection();
            this.jmsConnection.start();
            this.jmsSession = this.jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = this.jmsSession.createQueue(this.properties.getMqQueueName());
            this.producer = jmsSession.createProducer(destination);
        } catch (JMSException e) { // wrap checked exception into runtime exception
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void execute(Tuple input) {
        log.trace(String.format("InML2JMSBolt.execute() working. Input size [%s].", input.getValues().size())); // <- too much output
        try {
            String docUri = input.getStringByField(StormAppDefaults.STORM_MARKLOGIC_URI_FIELD);
            // create and send jms text message
            TextMessage msg = this.jmsSession.createTextMessage(docUri);
            this.producer.send(msg);
            this.outputCollector.ack(input); // explicit ack for tuple

            if (log.isTraceEnabled()) { // avoid performance/memory issues (and too much output)
                log.trace(String.format("Sent TextMessage with text [%s].", docUri));
            }
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
            this.outputCollector.fail(input); // fail input tuple in case of exception
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("InML2JMSBolt.declareOutputFields() working.");
        // no code here - this bolt is end-point of topology
    }

    @Override
    public void cleanup() {
        log.debug("InML2JMSBolt.cleanup() working.");

        if (this.jmsSession != null) { // close JMS session
            try {
                this.jmsSession.close();
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (this.jmsConnection != null) { // close JMS connection
            try {
                this.jmsConnection.close();
            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

}
