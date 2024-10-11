package dmitry.gusev.storm.topologies.output;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.lang.IllegalStateException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Spout for output (consumer) topology. Receives JMS message
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 08.04.2016)
 */

public class OutJMS2MLSpout extends BaseRichSpout {

    private static final Log log = LogFactory.getLog(OutJMS2MLSpout.class);

    private final StormAppProperties properties;
    private SpoutOutputCollector     outputCollector;
    // ActiveMQ related objects
    private Connection jmsConnection;
    private Session    jmsSession;
    private MessageConsumer jmsConsumer;

    // we share messages count across all instances of this spout (use thread-safe atomics)
    private static final AtomicLong messagesCounter = new AtomicLong(0);
    private static final AtomicLong ackedMessages   = new AtomicLong(0);
    private static final AtomicLong failedMessages  = new AtomicLong(0);

    /***/
    public OutJMS2MLSpout(StormAppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("OutJMS2MLSpout.declareOutputFields() working.");
        declarer.declare(new Fields(StormAppDefaults.STORM_MARKLOGIC_URI_FIELD));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        log.debug("OutJMS2MLSpout.open() working.");
        this.outputCollector = collector;
        try { // connects to JMS queue and creats messages consumer
            ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(this.properties.getMqUser(),
                    this.properties.getMqPassword(), this.properties.getMqURL());
            this.jmsConnection = jmsConnectionFactory.createConnection();
            this.jmsConnection.start();
            this.jmsSession = this.jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = this.jmsSession.createQueue(this.properties.getMqQueueName());
            this.jmsConsumer = this.jmsSession.createConsumer(destination);
        } catch (JMSException e) { // wrap checked exception into runtime exception
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void nextTuple() {
        log.trace("OutJMS2MLSpout.nextTuple() working."); // <- too much output
        try {
            // receive message from JMS queue
            Message message = this.jmsConsumer.receiveNoWait();
            if (message instanceof TextMessage) {
                TextMessage txtMessage = (TextMessage) message;
                String msgId   = txtMessage.getJMSMessageID();
                String msgText = txtMessage.getText();
                OutJMS2MLSpout.messagesCounter.incrementAndGet();

                if (log.isTraceEnabled()) { // avoid performance/memory issues (and too much output)
                    log.trace(String.format("Received TextMessage with ID = [%s] and text = [%s].", msgId, msgText));
                }
                if (log.isDebugEnabled() && this.properties.getProcessedLinesCount() > 0 &&
                        OutJMS2MLSpout.messagesCounter.get() % this.properties.getProcessedLinesCount() == 0) { // only for debug - processing progress
                    log.debug(String.format("Processed [%s] message(s).", OutJMS2MLSpout.messagesCounter.get()));
                }

                // emit data to next bolt only if we received something. As tuple ID using JMS message ID.
                if (msgId != null && msgText != null) {
                    this.outputCollector.emit(new Values(msgText), msgId);
                }
            } // end of -> if (TextMessage)

        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void close() {
        log.debug("OutJMS2MLSpout.close() working.");
        if (jmsSession != null) { // close JMS session
            try {
                jmsSession.close();
            } catch (JMSException e) {
                log.error("Can't close JMS Session!", e);
            }
        }

        if (jmsConnection != null) { // close JMS connection
            try {
                jmsConnection.close();
            } catch (JMSException e) {
                log.error("Can't close JMS Connection!", e);
            }
        }
    }

    @Override
    public void ack(Object msgId) {
        log.trace("OutJMS2MLSpout.ack() working."); // <- too much output
        OutJMS2MLSpout.ackedMessages.incrementAndGet();

        if (log.isDebugEnabled() && this.properties.getProcessedLinesCount() > 0 &&
                OutJMS2MLSpout.ackedMessages.get() % this.properties.getProcessedLinesCount() == 0) {
            log.debug(String.format("OutJMS2MLSpout: acks [%s]", OutJMS2MLSpout.ackedMessages.get()));
        }
    }

    @Override
    public void fail(Object msgId) {
        log.trace("OutJMS2MLSpout.fail() working."); // <- too much output
        OutJMS2MLSpout.failedMessages.incrementAndGet();
        log.error(String.format("OutJMS2MLSpout: fails [%s]. Failed to process message -> [%s]", OutJMS2MLSpout.failedMessages.get(), msgId));
    }

}
