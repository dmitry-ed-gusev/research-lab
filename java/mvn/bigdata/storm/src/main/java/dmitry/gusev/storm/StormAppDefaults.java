package dmitry.gusev.storm;

/**
 * Project's internal defaults.
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 06.04.2016)
 */
public interface StormAppDefaults {

    // Common defaults
    String APP_CONFIG                     = "storm-app.properties";
    String DEFAULT_ENCODING               = "UTF-8";
    String XML_TEMPLATE                   = "<?xml version=\"1.0\" encoding=\"utf-8\"?>%n<%s>%s</%s>";
    int    GENERATED_FILE_MAX_LINE_LENGTH = 200;

    // Stromg topology/spouts/bolts names
    String STORM_INPUT_TOPOLOGY_NAME      = "inputStormAppTopology";
    String STORM_OUTPUT_TOPOLOGY_NAME     = "outputStormAppTopology";
    String STORM_SPOUT_IN_FILE2TEXT       = "in-file2text-spout";
    String STORM_BOLT_IN_TEXT2ML          = "in-text2ml-bolt1";
    String STORM_BOLT_IN_ML2JMS           = "in-ml2jms-bolt2";
    String STORM_SPOUT_OUT_JMS2ML         = "out-jms2ml-spout";
    String STORM_BOLT_OUT_ML2TEXT         = "out-ml2text-bolt1";
    String STORM_BOLT_OUT_TEXT2FILE       = "out-text2file-bolt2";
    // Storm fields names for spouts/bolts
    String STORM_TEXT_LINE_FIELD          = "textLine";
    String STORM_MARKLOGIC_URI_FIELD      = "mlDocUri";

}
