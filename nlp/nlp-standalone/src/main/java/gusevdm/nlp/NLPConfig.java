package gusevdm.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/***/
// todo: no errors processing/preventing!
// todo: use defensive style!

public class NLPConfig {

    // some defaults
    private static final String DEFAULT_ENCODING         = "windows-1251";
    private static final int    DEFAULT_PROGRESS_COUNTER = 1_000_000;
    private static final int    DEFAULT_NGRAMMS_SIZE     = 2;
    // keys for config file
    private static final String KEY_INPUT_FILE          = "input.file";
    private static final String KEY_INPUT_FILE_ENCODING = "input.file.encoding";
    private static final String KEY_CLEANED_FILE        = "cleaned.file";
    private static final String KEY_OUTPUT_FILE         = "output.file";
    private static final String KEY_GARBAGE_DICT        = "garbage.dictionary";
    private static final String KEY_PROGRESS_COUNTER    = "progress.output.counter";
    private static final String KEY_NGRAMMS_SIZE        = "ngramms.size";

    private String     configFile;
    private Properties properties;
    private String[]   garbageList = null;

    /***/
    public NLPConfig(String configFile) throws IOException {
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

}
