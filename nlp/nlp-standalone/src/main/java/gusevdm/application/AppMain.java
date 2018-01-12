package gusevdm.application;

import gusev.dmitry.jtils.utils.CommonUtils;
import gusevdm.deployer.AppDeployerGlassfish;
import gusevdm.engine.NLPEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.embeddable.GlassFishException;

import java.io.IOException;

// todo: 1. add cmd line options (on/off steps)
// todo: 2. out resulting map to file, remove text "NGramms..." from output
// todo: 3. move to config file list of unnecessary (garbage words)
// todo: 4. move to config (outer file) lenght of ngramms
// todo: 5. move to cmd line arguments in/out files names

/***/
public class AppMain {

    private static final Log LOG = LogFactory.getLog(AppMain.class);
    // application config (properties format)
    private static final String APP_CONFIG    = "panalyzer.properties";
    // temporary file to write/read cured data from source file
    private static final String TMP_FILE_NAME = "curedData.txt";

    private AppConfig config;

    /***/
    public AppMain(AppConfig config) {
        this.config = config;
    }

    /***/
    // todo: use config
    public static void analyzePayments(String inputFile, String outputFile) {
        LOG.debug("AppMain.analyzePayments() is working.");

        try {
            // todo: add config loading and getting needed values
            // delete output file if exists
            CommonUtils.deleteFileIfExist(TMP_FILE_NAME);
            // clean input data (rewrite them in output file)
            NLPEngine.cleanInputData(inputFile, TMP_FILE_NAME,
                    AppConfig.DEFAULT_ENCODING, AppConfig.DEFAULT_PROGRESS_COUNTER);

            // delete ingrams file
            CommonUtils.deleteFileIfExist(outputFile);
            // build ngrams on cured data
            NLPEngine.buildNgramsMap(TMP_FILE_NAME, outputFile,
                    AppConfig.DEFAULT_ENCODING, AppConfig.DEFAULT_PROGRESS_COUNTER);

        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /***/
    public static void main(String[] args) {
        LOG.info("AppMain is starting...");

        /*
        // input and output files
        String inputFile = "c:/temp/nazn.txt";
        String outputFile = "c:/temp/ngrams.txt";
        // process/analyze payments
        AppMain.analyzePayments(inputFile, outputFile);
        */

        try {
            // config
            AppConfig config = new AppConfig(APP_CONFIG);
            // application itself
            //AppMain application = new AppMain(config);
            // deploy web-service
            AppDeployerGlassfish.deployWebService(config.getWebServiceFile(),
                    config.getWebServiceContext(), config.getWebServicePort());

        } catch (IOException | GlassFishException e) {
            LOG.error(e);
        }

    } // end of main()

}
