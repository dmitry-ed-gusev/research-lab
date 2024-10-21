package dmitry.gusev.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import dmitry.gusev.storm.config.StormAppProperties;
import dmitry.gusev.storm.topologies.InputTopologyBuilder;
import dmitry.gusev.storm.topologies.OutputTopologyBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Starter class for Storm test application.
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 02.04.2016)
 */

public final class StormAppStarter {

    private static final Log log = LogFactory.getLog(StormAppStarter.class);

    private StormAppStarter() {} // utility class, no public constructor

    /** StormApp application entry point. */
    public static void main(String[] args) {
        log.info("StormApp application starting...");

        try {
            StormAppProperties appProperties = new StormAppProperties(StormAppDefaults.APP_CONFIG); // read config from file

            if (!Helper.removeFile(appProperties.getDestinationFile())) { // remove output file
                log.error(String.format("Can't clean up output file [%s]!", appProperties.getDestinationFile()));
                return;
            }

            if (appProperties.isGenerateSourceFile()) { // generate source file, if necessary
                Helper.generateTextFile(appProperties.getSourceFile(), appProperties.getGenerateSourceFileLines(),
                        StormAppDefaults.GENERATED_FILE_MAX_LINE_LENGTH, true);
            }

            // storm cluster config (used by both input and output topologies)
            Config stormConfig = new Config();
            stormConfig.setDebug(false);
            if (appProperties.getStormMessageTimeout() > 0) { // set message timeout or use default
                stormConfig.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, appProperties.getStormMessageTimeout());
            }

            // create Storm cluster with two topologies (input/output)
            StormTopology inputTopology = InputTopologyBuilder.build(appProperties);
            StormTopology outputTopology = OutputTopologyBuilder.build(appProperties);
            LocalCluster stormCluster = new LocalCluster();
            stormCluster.submitTopology(StormAppDefaults.STORM_INPUT_TOPOLOGY_NAME, stormConfig, inputTopology);
            stormCluster.submitTopology(StormAppDefaults.STORM_OUTPUT_TOPOLOGY_NAME, stormConfig, outputTopology);

            log.info("StormAppStarter.main() finished (main thread).");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    } // end of main() method

}
