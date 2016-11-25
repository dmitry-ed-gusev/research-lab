package dmitry.gusev.storm.topologies;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import dmitry.gusev.storm.topologies.output.OutJMS2MLSpout;
import dmitry.gusev.storm.topologies.output.OutML2TextBolt;
import dmitry.gusev.storm.topologies.output.OutText2FileBolt;
import org.apache.commons.logging.LogFactory;

/**
 * Builder class for output (consumer) topology.
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 17.04.2016)
 */

public class OutputTopologyBuilder {

    /***/
    public static StormTopology build(StormAppProperties appProperties) {
        LogFactory.getLog(OutputTopologyBuilder.class).debug("OutputTopologyBuilder.build() working.");

        if (appProperties == null) { // FAIL FAST
            throw new IllegalArgumentException("Received null StormAppProperties object!");
        }

        // Build and start Storm output data topology (Topology #2)
        TopologyBuilder outputTopologyBuilder = new TopologyBuilder();

        // Topology #2:SPOUT - read text message from JMS queue
        outputTopologyBuilder.setSpout(StormAppDefaults.STORM_SPOUT_OUT_JMS2ML,
                new OutJMS2MLSpout(appProperties), appProperties.getStOutJMS2MLexe())
                .setNumTasks(appProperties.getStOutJMS2MLtsk() > 0 ? appProperties.getStOutJMS2MLtsk() : appProperties.getStOutJMS2MLexe());

        // Topology #2:BOLT - receive Marklogic XML document URI, read document from Marklogic, extract text
        outputTopologyBuilder.setBolt(StormAppDefaults.STORM_BOLT_OUT_ML2TEXT,
                new OutML2TextBolt(appProperties), appProperties.getStOutML2TexTexe())
                .setNumTasks(appProperties.getStOutML2TexTtsk() > 0 ? appProperties.getStOutML2TexTtsk() : appProperties.getStOutML2TexTexe())
                .shuffleGrouping(StormAppDefaults.STORM_SPOUT_OUT_JMS2ML);

        // Topology #2:BOLT - receive text line, append text line to output text file. This BOLT is single threaded (mandatory!).
        outputTopologyBuilder.setBolt(StormAppDefaults.STORM_BOLT_OUT_TEXT2FILE, new OutText2FileBolt(appProperties), 1)
                .globalGrouping(StormAppDefaults.STORM_BOLT_OUT_ML2TEXT);

        return outputTopologyBuilder.createTopology();
    }

}
