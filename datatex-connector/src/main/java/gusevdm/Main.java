package gusevdm;

import gusevdm.helpers.ExitStatus;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

import static com.sun.org.apache.xalan.internal.xsltc.dom.CollatorFactoryBase.DEFAULT_LOCALE;
import static gusevdm.helpers.CommandLineOption.*;

/** Command-line entry point for LuxMS REST Client. */

public class Main {

    private static final Logger LOGGER            = LoggerFactory.getLogger(Main.class);

    // some console messages
    private static final String ERROR_MSG         = "Error message: [%s].";
    private static final String HINT_MSG          = String.format("Try '--%s' option for help.", OPTION_HELP.getName());
    private static final String VERSION_MSG       = "DataTex Connector utility, version 0.2.0, 2018 (C) Larga";
    private static final String DEFAULT_CONFIG    = "environment.yml";
    // values for this module (config, suffix, parser, etc...)
    private final OptionSpec<String> config;
    private final OptionSpec<String> suffix;
    private final OptionParser       parser;
    private final Runtime            runtime;

    private Main() {
        this(new OptionParser(), Runtime.getRuntime());
    }

    /** Constructor. Init cmd line parameters. */
    Main(OptionParser parser, Runtime runtime) {
        LOGGER.debug("Main constructor() is working.");

        this.parser  = parser;
        this.runtime = runtime;
        parser.posixlyCorrect(true); // use POSIX correct syntax for cmd line parameters
        parser.accepts(OPTION_HELP.getName(), OPTION_HELP.getDescription()).forHelp(); // setup option for help

        // not required options (optional). for required options just add .required() to the end
        parser.accepts(OPTION_LOG_LEVEL.getName(), OPTION_LOG_LEVEL.getDescription())                   // log level
                .withRequiredArg().ofType(String.class);
        this.config = parser.accepts(OPTION_CONFIG_FILE.getName(), OPTION_CONFIG_FILE.getDescription()) // config file
                .withRequiredArg().ofType(String.class);
        parser.accepts(OPTION_LIST_DATASETS.getName(), OPTION_LIST_DATASETS.getDescription());          // list datasets
        parser.accepts(OPTION_LIST_TABLES.getName(), OPTION_LIST_TABLES.getDescription());              // list DB tables
        this.suffix = parser.accepts(OPTION_ENV_SUFFIX.getName(), OPTION_ENV_SUFFIX.getDescription())   // env suffix
                .withRequiredArg().ofType(String.class);
        parser.accepts(OPTION_DELETE_DATASET.getName(), OPTION_DELETE_DATASET.getDescription())         // delete dataset
                .withRequiredArg().ofType(Long.class);
        parser.accepts(OPTION_CREATE_DATASET.getName(), OPTION_CREATE_DATASET.getDescription())         // create dataset
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(",");
    }


    /**
     * Run CSV2Abstract with command-line arguments. The available options are:
     * <p>
     * <ul>
     *     <li>{@code --dataset} - qualified name of Abstract dataset to be published (mandatory)
     *     <li>{@code --csv} - HDFS path to CSV file (mandatory)
     *     <li>{@code --schema} - HDFS path to JSON schema file (mandatory)
     *     <li>{@code --reindex - (re)index Abstract collection
     *     <li>{@code --help} - print help/usage for the tool
     * </ul>
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("DataTex connector is starting...");
        new Main().run(args);
    }

    // See documentation for Main.main
    void run(String... args) {
        LOGGER.debug("Main.run(String...) is working.");
        try {
            OptionSet optionSet = this.parser.parse(args);

            if (optionSet.has(OPTION_HELP.getName())) { // show help and exit
                showHelpAndExit();
            }

            if (optionSet.has(OPTION_LOG_LEVEL.getName())) { // new log level specified
                checkAndSetLogLevel(optionSet);
            }

            // run main module
            run(optionSet);

        } catch (OptionException e) { // invalid/unexpected option(s) provided (misuse case)
            LOGGER.error(String.format(ERROR_MSG, e.getMessage()), e);
            LOGGER.info(HINT_MSG);
            runtime.exit(ExitStatus.MISUSE.getValue());
        } catch (IllegalArgumentException e) {  // NOSONAR
            LOGGER.error(String.format(ERROR_MSG, e.getMessage()), e);
            runtime.exit(ExitStatus.MISUSE.getValue());
        } catch (CSV2AbstractException e) {
            LOGGER.error("Execution time error", e);
            runtime.exit(ExitStatus.GENERAL_ERROR.getValue());
        } catch (Exception e) { // NOSONAR: we have to catch all cases and try to log them
            LOGGER.error("Unexpected exception", e);
            runtime.exit(ExitStatus.GENERAL_ERROR.getValue());
        }
    }

    /** Real run() - after all initializations. */
    void run(OptionSet optionSet) {
        LOGGER.debug("Main.run(OptionSet) is working.");

        // select config file (default or provided via cmd line option)
        String credentialsFile = optionSet.valueOf(this.config);
        if (StringUtils.isBlank(credentialsFile)) {
            credentialsFile = DEFAULT_CONFIG;
        }
        // load environment for config
        LOGGER.debug(String.format("Using config file [%s].", credentialsFile));
        Environment.load(credentialsFile, optionSet.valueOf(this.suffix));

        // execute engine (ConnectorEngine)
        ConnectorEngine engine = new ConnectorEngine(optionSet);
        engine.execute();
    }

    /** Show help screen and exit. */
    private void showHelpAndExit() throws IOException {
        LOGGER.debug("Main.showHelpAndExit() is working.");
        StringWriter stringWriter = new StringWriter();
        parser.printHelpOn(stringWriter);
        // build message for --help option
        StringBuilder msg = new StringBuilder();
        msg.append("\n\n").append(VERSION_MSG).append("\n\n").append(stringWriter.toString());
        LOGGER.info(msg.toString());
        runtime.exit(ExitStatus.OK.getValue());
    } // end of showHelpAndExit()

    /** Set requested logging level. */
    private void checkAndSetLogLevel(OptionSet optionSet) {
        LOGGER.debug("Main.checkAndSetLogLevel() is working.");

        String strLevel = (String) optionSet.valueOf(OPTION_LOG_LEVEL.getName());

        if (strLevel != null && !strLevel.trim().isEmpty()) {
            Level level = Level.toLevel(strLevel.toUpperCase(DEFAULT_LOCALE), Level.OFF);

            if (level.equals(Level.DEBUG) || level.equals(Level.INFO) || level.equals(Level.WARN)) {
                LogManager.getRootLogger().setLevel(level);
            } else {
                LOGGER.warn(String.format("Invalid value [%s] for logging level!", strLevel));
                LOGGER.info(HINT_MSG);
                runtime.exit(ExitStatus.MISUSE.getValue());
            }
        }
    } // end of checkAndSetLogLevel()

}
