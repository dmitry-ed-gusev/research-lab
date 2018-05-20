package gusevdm;

import gusevdm.helpers.ExitStatus;
import gusevdm.luxms.DataSet;
import gusevdm.luxms.LuxMSRestClient;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

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
            String.format("Try '%s --%s' for more information.", Main.class.getName(), OPTION_HELP.getName());

    //private final OptionSpec<String> dataset;
    //private final OptionSpec<String> csv;
    //private final OptionSpec<String> schema;
    private final OptionSpec<String> environment;
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

        this.environment = parser.accepts(ENVIRONMENT.getName(), ENVIRONMENT.getDescription())
                .withRequiredArg()
                .ofType(String.class)
                .required();

        // not required options
        parser.accepts(OPTION_LOG_LEVEL.getName(), OPTION_LOG_LEVEL.getDescription()).withRequiredArg().ofType(String.class);

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
        LOGGER.info("CSV2Abstract tool starting.");
        new Main().run(args);
    }

    // See documentation for Main.main
    void run(String... args) {
        LOGGER.debug("Main.run(String...) is working.");
        try {
            OptionSet optionSet = this.parser.parse(args);

            if (optionSet.has(OPTION_HELP.getName())) {
                showHelpAndExit();
            }

            if (optionSet.has(OPTION_LOG_LEVEL.getName())) {
                checkAndSetLogLevel(optionSet);
            }

            //validateDatasetName(optionSet);
            run(optionSet);

        } catch (OptionException e) {    // NOSONAR Sonar expects the exception to be either logged or rethrown
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
        /*
        if (this.csv2Abstract != null) {
            LOGGER.debug("Default Csv2Abstract module overridden. Using new module.");
            this.csv2Abstract.run();
        } else {
            LOGGER.debug("Using default Csv2Abstarct implementation.");
            String credentialsFile = optionSet.valueOf(environment);
            Environment.load(credentialsFile);
            CSV2Abstract csv2AbstractTool = new CSV2Abstract(optionSet.valueOf(dataset), optionSet.valueOf(csv),
                    optionSet.valueOf(schema), optionSet.has(OPTION_REINDEX.getName()));
            csv2AbstractTool.run();
        }
        */

        String credentialsFile = optionSet.valueOf(environment);
        Environment.load(credentialsFile);

        // client instance
        LuxMSRestClient luxRest = new LuxMSRestClient();
        // login
        luxRest.login();

        // list datasets
        List<DataSet> datasets = luxRest.listDatasets();
        datasets.forEach(dataset -> LOGGER.debug(String.format("Dataset -> %s", dataset)));

        // create dataset
        //DataSet dataSet = luxRest.createDataset("my_dataset_zzz", "My New (!) Own set", true);
        //LOGGER.debug(String.format("Created dataset [%s].", dataSet));

        // remove dataset
        luxRest.removeDataset(9);
    }

    //void setCsv2Abstract(CSV2Abstract csv2Abstract) {
    //    this.csv2Abstract = csv2Abstract;
    //}

    private void showHelpAndExit() throws IOException {
        StringWriter stringWriter = new StringWriter();
        parser.printHelpOn(stringWriter);
        LOGGER.info(stringWriter.toString());
        runtime.exit(ExitStatus.OK.getValue());
    }

    private void checkAndSetLogLevel(OptionSet optionSet) {
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
    }

    /*
    // Validate dataset name - check for illegal characters.
    private void validateDatasetName(OptionSet optionSet) {
        LOGGER.debug("Main.validateDatasetName() is working.");
        String datasetName = optionSet.valueOf(this.dataset);
        for (char c: datasetName.toCharArray()) {
            if (!(isLowerCaseLatinLetter(c) || Character.isDigit(c) || c == '.' || c == '-')) {
                String errorMessage = String.format("Illegal character [%s] in dataset name: %s.%n" +
                        "Latin lower case letters, digits, periods and hyphens are allowed.", c, datasetName);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    private static boolean isLowerCaseLatinLetter(char c) {
        return c >= 'a' && c <= 'z';
    }
    */
}
