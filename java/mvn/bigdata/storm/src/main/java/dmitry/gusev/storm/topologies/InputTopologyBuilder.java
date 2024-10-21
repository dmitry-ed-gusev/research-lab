package dmitry.gusev.storm.topologies;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import dmitry.gusev.storm.StormAppDefaults;
import dmitry.gusev.storm.config.StormAppProperties;
import dmitry.gusev.storm.topologies.input.InFile2TextLineSpout;
import dmitry.gusev.storm.topologies.input.InML2JMSBolt;
import dmitry.gusev.storm.topologies.input.InText2MLBolt;
import org.apache.commons.logging.LogFactory;

/**
 * Builder class for input (producer) topology.
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 17.04.2016)
 */
public class InputTopologyBuilder {

    /***/
    public static StormTopology build(StormAppProperties appProperties) {
        LogFactory.getLog(InputTopologyBuilder.class).debug("InputTopologyBuilder.build() working.");

        if (appProperties == null) { // FAIL FAST
            throw new IllegalArgumentException("Received null StormAppProperties object!");
        }
        // Build and start Storm input data topology (Topology #1)
        TopologyBuilder inputTopologyBuilder = new TopologyBuilder();

        // Topology #1:SPOUT - read data (line by line) from a file. This SPOUT is single-threaded (mandatory!).
        inputTopologyBuilder.setSpout(StormAppDefaults.STORM_SPOUT_IN_FILE2TEXT, new InFile2TextLineSpout(appProperties), 1);

        // Topology #1:BOLT - receive text string, wrap string into XML and put into MarkLogic
        inputTopologyBuilder.setBolt(StormAppDefaults.STORM_BOLT_IN_TEXT2ML,
                new InText2MLBolt(appProperties), appProperties.getStInText2MLexe())
                .setNumTasks(appProperties.getStInText2MLtsk() > 0 ? appProperties.getStInText2MLtsk() : appProperties.getStInText2MLexe())
                .shuffleGrouping(StormAppDefaults.STORM_SPOUT_IN_FILE2TEXT);

        // Topology #1:BOLT - receive ML document URI, create JMS message and send it
        inputTopologyBuilder.setBolt(StormAppDefaults.STORM_BOLT_IN_ML2JMS,
                new InML2JMSBolt(appProperties), appProperties.getStInML2JMSexe())
                .setNumTasks(appProperties.getStInML2JMStsk() > 0 ? appProperties.getStInML2JMStsk() : appProperties.getStInML2JMSexe())
                .shuffleGrouping(StormAppDefaults.STORM_BOLT_IN_TEXT2ML);

        return inputTopologyBuilder.createTopology();
    }

}
