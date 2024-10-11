package dmitry.gusev.storm.topologies.output;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Map;

/**
 * 2nd bolt for output (consumer) topology. Receives text string and puts it to an output
 * file (append). Should be single-instanced (one executor, one task) because writes to a file
 * line by line (append).
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 08.04.2016)
 */

public class OutText2FileBolt extends BaseRichBolt {

    private static final Log log = LogFactory.getLog(OutText2FileBolt.class);

    private final StormAppProperties properties;
    private OutputCollector outputCollector;

    /***/
    public OutText2FileBolt(StormAppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        log.debug("OutText2FileBolt.prepare() working.");
        this.outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {
        log.trace("OutText2FileBolt.execute() working."); // too much output
        // get text line from input
        String textLine = input.getStringByField(StormAppDefaults.STORM_TEXT_LINE_FIELD);
        // append text line to output file
        try (FileWriter fw = new FileWriter(this.properties.getDestinationFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // todo: just a debug case!!!
            if (this.properties.isDebugExceptions() && textLine.startsWith("1")) {
                throw new UnsupportedEncodingException("-TEST CONSUMER EXCEPTION-");
            }

            out.println(textLine);
            this.outputCollector.ack(input); // explicit ack for tuple
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            this.outputCollector.fail(input); // fail input tuple in case of exception
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        log.debug("OutText2FileBolt.declareOutputFields() working.");
        // no code here - this bolt is end-point of topology
    }

}
