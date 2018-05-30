package gusevdm;

import gusevdm.datatexdb.DataTexDBClient;
import gusevdm.helpers.ExitStatus;
import gusevdm.luxms.DataSet;
import gusevdm.luxms.LuxMSRestClient;
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
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.dom.CollatorFactoryBase.DEFAULT_LOCALE;
import static gusevdm.helpers.CommandLineOption.*;

/** Command-line entry point for LuxMS REST Client. */

public class Main {

    private static final Logger LOGGER            = LoggerFactory.getLogger(Main.class);

    private static final String ERROR_MSG         = "Error message: [%s].";
    private static final String HINT_MSG          =
            String.format("Try '--%s' option for more information.", OPTION_HELP.getName());
    private static final String DEFAULT_CONFIG    = "environment.yml";

    //private final OptionSpec<String> dataset;
    //private final OptionSpec<String> csv;
    //private final OptionSpec<String> schema;
    private final OptionSpec<String> config;
    private final OptionSpec<String> suffix;
    private final OptionParser       parser;
    private final Runtime            runtime;
    //private       CSV2Abstract       csv2Abstract = null;

    private Main() {
        this(new OptionParser(), Runtime.getRuntime());
    }

    /** Constructor. Init cmd line parameters. */
    Main(OptionParser parser, Runtime runtime) {
        LOGGER.debug("Main constructor() is working.");

        this.parser  = parser;
        this.runtime = runtime;
        // use POSIX correct syntax for cmd line parameters
        parser.posixlyCorrect(true);
        // setup option for help
        parser.accepts(OPTION_HELP.getName(), OPTION_HELP.getDescription()).forHelp();

        /*
        this.dataset = parser.accepts(OPTION_DATASET.getName(), OPTION_DATASET.getDescription())
                .withRequiredArg()
                .ofType(String.class)
                .required();
        this.csv = parser.accepts(OPTION_CSV.getName(), OPTION_CSV.getDescription())
                .withRequiredArg()
                .ofType(String.class)
                .required();
        this.schema = parser.accepts(OPTION_SCHEMA.getName(), OPTION_SCHEMA.getDescription())
                .withRequiredArg()
                .ofType(String.class)
                .required();
        */

        // not required options (optional)
        parser.accepts(OPTION_LOG_LEVEL.getName(), OPTION_LOG_LEVEL.getDescription())                   // log level
                .withRequiredArg().ofType(String.class);
        this.config = parser.accepts(OPTION_CONFIG_FILE.getName(), OPTION_CONFIG_FILE.getDescription()) // config file
                .withRequiredArg().ofType(String.class);
        parser.accepts(OPTION_LIST_DATASETS.getName(), OPTION_LIST_DATASETS.getDescription());          // list datasets
        parser.accepts(OPTION_LIST_TABLES.getName(), OPTION_LIST_TABLES.getDescription());              // list DB tables
        this.suffix = parser.accepts(OPTION_ENV_SUFFIX.getName(), OPTION_ENV_SUFFIX.getDescription())   // env suffix
                .withRequiredArg().ofType(String.class);

        // switches
        //parser.accepts(OPTION_REINDEX.getName(), OPTION_REINDEX.getDescription());
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
        LOGGER.debug(String.format("Using config file [%s].", credentialsFile));
        Environment.load(credentialsFile, optionSet.valueOf(this.suffix));

        // LuxMS REST client instance
        LuxMSRestClient luxRest = new LuxMSRestClient();

        if (optionSet.has(OPTION_LIST_DATASETS.getName())) { // list datasets in LuxMS instance
            LOGGER.debug("Listing datasets in LuxMS BI.");
            luxRest.login(); // login to server and save api key
            // list datasets
            List<DataSet> datasets = luxRest.listDatasets();
            StringBuilder datasetsList = new StringBuilder();
            datasets.forEach(dataset -> datasetsList.append(String.format("%s%n", dataset)));
            LOGGER.info(datasetsList.toString());
        }

        // create dataset
        //DataSet dataSet = luxRest.createDataset("my_dataset_zzz", "My New (!) Own set", true);
        //LOGGER.debug(String.format("Created dataset [%s].", dataSet));
        // remove dataset
        //long idRemoved = luxRest.removeDataset(17);
        //LOGGER.debug(String.format("Removed dataset #%s.", idRemoved));

        // DataTex DB Client instance
        DataTexDBClient dbClient = new DataTexDBClient();

        if (optionSet.has(OPTION_LIST_TABLES.getName())) { // list all tables in given schema in DataTex DB
            LOGGER.debug("Listing all tables in DataTex DB in a given schema.");

            LOGGER.info(dbClient.getTablesList());
        }

    }

    //void setCsv2Abstract(CSV2Abstract csv2Abstract) {
    //    this.csv2Abstract = csv2Abstract;
    //}

    /** Show help screen and exit. */
    private void showHelpAndExit() throws IOException {
        LOGGER.debug("Main.showHelpAndExit() is working.");
        StringWriter stringWriter = new StringWriter();
        parser.printHelpOn(stringWriter);
        LOGGER.info(stringWriter.toString());
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
