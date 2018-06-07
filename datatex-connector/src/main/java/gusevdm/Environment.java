package gusevdm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/** Connection properties. */

@NotThreadSafe
public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    // regex pattern for checking invalid chars in URL
    private   static final Pattern URL_INVALID_CHARS    = Pattern.compile("[\\x00-\\x1F]");

    // LuxMS BI environment properties
    private static final String LUXMS_ENV_PROPERTY      = "luxms_env";
    private static final String LUXMS_URL_PROPERTY      = "lux_url";
    private static final String LUXMS_USER_PROPERTY     = "lux_user";
    private static final String LUXMS_PASS_PROPERTY     = "lux_password";
    // DataTex DB environment properties
    private static final String DATATEX_ENV_PROPERTY    = "datatex_env";
    private static final String DATATEX_HOST_PROPERTY   = "db_host";
    private static final String DATATEX_PORT_PROPERTY   = "db_port";
    private static final String DATATEX_USER_PROPERTY   = "db_user";
    private static final String DATATEX_PASS_PROPERTY   = "db_pass";
    private static final String DATATEX_SCHEMA_PROPERTY = "db_schema";
    private static final String DATATEX_SID_PROPERTY    = "db_sid";
    // General properties
    private static final String GENERAL_ENV_PROPERTY    = "general_env";
    private static final String GENERAL_CSV_EXPORT_DIR  = "csv_export_dir";
    private static final String GENERAL_CSV_IMPORT_DIR  = "csv_import_dir";

    // list of environments
    private static final List<String> ENVIRONMENTS = new ArrayList<String>() {{
        add(LUXMS_ENV_PROPERTY);
        add(DATATEX_ENV_PROPERTY);
        add(GENERAL_ENV_PROPERTY);
    }};

    // list of all required properties
    private   static final List<String> REQUIRED_PROPERTIES = Arrays.asList(
            // LuxMS required properties
            LUXMS_URL_PROPERTY,
            LUXMS_USER_PROPERTY,
            LUXMS_PASS_PROPERTY,
            // DataTex required properties
            DATATEX_HOST_PROPERTY,
            DATATEX_PORT_PROPERTY,
            DATATEX_USER_PROPERTY,
            DATATEX_PASS_PROPERTY,
            DATATEX_SCHEMA_PROPERTY,
            DATATEX_SID_PROPERTY,
            // general required properties
            GENERAL_CSV_EXPORT_DIR,
            GENERAL_CSV_IMPORT_DIR
    );

    private static Environment instance;

    private Map<String, String> credentials = null;

    // For unit tests only. Use getInstance() instead.
    Environment() {}

    private Environment(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public String getLuxMSURL() {
        return credentials.get(LUXMS_URL_PROPERTY);
    }

    public String getLuxMSUser() {
        return credentials.get(LUXMS_USER_PROPERTY);
    }

    public String getLuxMSPassword() {
        return credentials.get(LUXMS_PASS_PROPERTY);
    }

    public String getDataTexHost() {
        return credentials.get(DATATEX_HOST_PROPERTY);
    }

    public String getDataTexPort() {
        return String.valueOf(credentials.get(DATATEX_PORT_PROPERTY));
    }

    public String getDataTexUser() {
        return credentials.get(DATATEX_USER_PROPERTY);
    }

    public String getDataTexPass() {
        return credentials.get(DATATEX_PASS_PROPERTY);
    }

    public String getDataTexSchema() {
        return credentials.get(DATATEX_SCHEMA_PROPERTY);
    }

    public String getDataTexSID() {
        return credentials.get(DATATEX_SID_PROPERTY);
    }

    public String getCsvExportDir() {
        return credentials.get(GENERAL_CSV_EXPORT_DIR);
    }

    public String getCsvImportDir() {
        return credentials.get(GENERAL_CSV_IMPORT_DIR);
    }

    /**
     * Get instance of {@link Environment} class. Return the same instance on each call. Validate the
     * environment is initialized/loaded before. This method is not thread safe.
     *
     * @return Environment object
     * @throws IllegalStateException if environment is not initialized
     */
    public static Environment getInstance() {
        LOGGER.debug("Environment.getInstance() is working.");
        if (instance == null) {
            throw new IllegalStateException("Environment was not loaded");
        }
        return instance;
    }

    /**
     * Create instance of {@link Environment} class. Load credentials info from yaml file.
     * Validates credentials and environment properties. This method is not thread safe.
     *
     * @param credentialsFile Path to yaml-file with credentials info
     * @throws IllegalArgumentException if environment is not set up correctly
     */
    @SuppressWarnings("unchecked")
    static void load(String credentialsFile, String suffix) {
        LOGGER.debug("Environment.load(String credentialsFile) is working.");
        //Load credentials file
        Yaml yaml = new Yaml();
        try (FileInputStream in = new FileInputStream(credentialsFile)) {

            // load the whole file
            Map<String, Object> credentials = (Map<String, Object>) yaml.load(in);
            // calculate suffix
            String localSuffix = StringUtils.isBlank(suffix) ? "" : suffix;

            // general environment
            Map<String, String> environments = new HashMap<>();
            Map<String, String> tmpEnv; // tmp environment
            for (String environment : ENVIRONMENTS) {
                tmpEnv = (Map<String, String>) credentials.get(environment + localSuffix);
                if (tmpEnv == null || tmpEnv.isEmpty()) {
                    throw new IllegalStateException(String.format("Can't load environment [%s]!", environment + localSuffix));
                }
                // merge environment with others
                // todo: what if properties with same name in different environments?
                environments.putAll(tmpEnv);
            }

            // validate merged environment
            validateEnvironment(environments);

            // initialize Environment instance
            instance = new Environment(environments); // NOSONAR: Sonar asks to synchronize the instance,
            // but this class is intentionally not thread safe.
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid configuration file", e);
        }
    }

    /**
     * Make sure that environment is set up properly.
     * @throws IllegalArgumentException if at least one of the system properties is invalid
     */
    private static void validateEnvironment(Map<String, String> credentials) {
        LOGGER.debug("Environment.validateEnvironment() is working.");

        // check presence of all required properties
        for (String property : REQUIRED_PROPERTIES) {
            validateProperty(property, String.valueOf(credentials.getOrDefault(property, null)));
        }

        //validateProperty(KNOX_HDFS_URI_PROPERTY, System.getProperty(KNOX_HDFS_URI_PROPERTY));
        LOGGER.debug(String.format("Presence of all mandatory properties [%s] checked. All OK.", REQUIRED_PROPERTIES));

        // check URL(s) for illegal characters
        Environment.checkUrl(credentials.get(LUXMS_URL_PROPERTY));
        LOGGER.debug("URL(s) checked. All OK.");
    }

    /** Validate single property. */
    private static void validateProperty(String property, String value) {
        if (value == null) {
            String errorMessage = String.format("Required property is missing: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }
        if (value.trim().isEmpty()) {
            String errorMessage = String.format("Required property is blank: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /** Check URL for invalid chars. */
    static void checkUrl(String url) {
        if (URL_INVALID_CHARS.matcher(url).find()) {
            throw new IllegalArgumentException(String.format("URL [%s] contains invalid characters!", url));
        }
    }

}
