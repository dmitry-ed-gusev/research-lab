package gusevdm.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/***/
// todo: no errors processing/preventing!
// todo: use defensive style!

public class AppConfig {

    // some defaults
    public static final String  DEFAULT_ENCODING            = "windows-1251";
    public static final int     DEFAULT_PROGRESS_COUNTER    = 1_000_000;
    private static final int    DEFAULT_NGRAMMS_SIZE        = 2;
    private static final String DEFAULT_WEB_SEVICE_FILE     = "panalyzer-web.war";
    private static final int    DEFAULT_WEB_SERVICE_PORT    = 8080;
    private static final String DEFAULT_WEB_SERVICE_CONTEXT = "panalyzer";

    // keys for config file
    private static final String KEY_INPUT_FILE          = "input.file";
    private static final String KEY_INPUT_FILE_ENCODING = "input.file.encoding";
    private static final String KEY_CLEANED_FILE        = "cleaned.file";
    private static final String KEY_OUTPUT_FILE         = "output.file";
    private static final String KEY_GARBAGE_DICT        = "garbage.dictionary";
    private static final String KEY_PROGRESS_COUNTER    = "progress.output.counter";
    private static final String KEY_NGRAMMS_SIZE        = "ngramms.size";
    // keys for web-sevice
    private static final String KEY_WEB_SERVICE_FILE    = "pan.war.file";
    private static final String KEY_WEB_SERVICE_PORT    = "pan.service.port";
    private static final String KEY_WEB_SERVICE_CONTEXT = "pan.service.context.path";

    private String     configFile;
    private Properties properties;
    private String[]   garbageWordsList = null;

    /***/
    public AppConfig(String configFile) throws IOException {
        this.configFile = configFile;

        this.properties = new Properties();
        try (InputStream input = new FileInputStream(this.configFile)) {
            // load a properties file
            this.properties.load(input);
        } // end of try
    }

    public String getInputFile() {
        return this.properties.getProperty(KEY_INPUT_FILE, "");
    }

    public String getInputFileEncoding() {
        return this.properties.getProperty(KEY_INPUT_FILE_ENCODING, DEFAULT_ENCODING);
    }
    
    public String getCleanedDataFile() {
        return this.properties.getProperty(KEY_CLEANED_FILE, "");
    }

    public String getOutputFile() {
        return this.properties.getProperty(KEY_OUTPUT_FILE, "");
    }

    public String[] getGrabageDict() {
        // todo: implement loading garbage dictionary
        return null;
    }

    public int getProgressCounter() {
        String strCounter = this.properties.getProperty(KEY_PROGRESS_COUNTER, String.valueOf(DEFAULT_PROGRESS_COUNTER));
        return Integer.parseInt(strCounter);
    }

    public int getNGrammsSize() {
        String strSize = this.properties.getProperty(KEY_NGRAMMS_SIZE, String.valueOf(DEFAULT_NGRAMMS_SIZE));
        return Integer.parseInt(strSize);
    }

    public String getWebServiceFile() {
        return this.properties.getProperty(KEY_WEB_SERVICE_FILE, DEFAULT_WEB_SEVICE_FILE);
    }

    public int getWebServicePort() {
        String strPort = this.properties.getProperty(KEY_WEB_SERVICE_PORT, String.valueOf(DEFAULT_WEB_SERVICE_PORT));
        return Integer.parseInt(strPort);
    }

    public String getWebServiceContext() {
        return this.properties.getProperty(KEY_WEB_SERVICE_CONTEXT, DEFAULT_WEB_SERVICE_CONTEXT);
    }

}
