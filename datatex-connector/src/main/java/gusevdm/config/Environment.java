package gusevdm.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/** Environment/configuration for the whole system. */

@NotThreadSafe
public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    // regex pattern for checking invalid chars in URL
    private static final Pattern URL_INVALID_CHARS    = Pattern.compile("[\\x00-\\x1F]");

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
    private static final String GENERAL_SQL_DIR         = "reports_dir";

    // Reports properties
    private static final String REPORTS_ENV_PROPERTY    = "reports";

    // list of environments
    private static final List<String> ENVIRONMENTS = new ArrayList<String>() {{
        add(LUXMS_ENV_PROPERTY);
        add(DATATEX_ENV_PROPERTY);
        add(GENERAL_ENV_PROPERTY);
        add(REPORTS_ENV_PROPERTY);
    }};

    // list of all required properties
    private static final List<String> REQUIRED_PROPERTIES = Arrays.asList(
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
            GENERAL_CSV_IMPORT_DIR,
            GENERAL_SQL_DIR
    );

    // instance of current class (singleton)
    private static Environment instance;
    // storage for properties
    private Map<String, String> config = null;
    // reports list
    private List<String> reports = null;

    private Environment(Map<String, String> config, List<String> reports) {
        this.config  = config;
        this.reports = reports;
    }

    public String getLuxMSURL() {
        return config.get(LUXMS_URL_PROPERTY);
    }

    public String getLuxMSUser() {
        return config.get(LUXMS_USER_PROPERTY);
    }

    public String getLuxMSPassword() {
        return config.get(LUXMS_PASS_PROPERTY);
    }

    public String getDataTexHost() {
        return config.get(DATATEX_HOST_PROPERTY);
    }

    public String getDataTexPort() {
        return String.valueOf(config.get(DATATEX_PORT_PROPERTY));
    }

    public String getDataTexUser() {
        return config.get(DATATEX_USER_PROPERTY);
    }

    public String getDataTexPass() {
        return config.get(DATATEX_PASS_PROPERTY);
    }

    public String getDataTexSchema() {
        return config.get(DATATEX_SCHEMA_PROPERTY);
    }

    public String getDataTexSID() {
        return config.get(DATATEX_SID_PROPERTY);
    }

    public String getCsvExportDir() {
        return config.get(GENERAL_CSV_EXPORT_DIR);
    }

    public String getCsvImportDir() {
        return config.get(GENERAL_CSV_IMPORT_DIR);
    }

    public String getReportsDir() {
        return config.get(GENERAL_SQL_DIR);
    }

    public List<String> getReportsList() {
        return this.reports;
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
     * Create instance of {@link Environment} class. Load config info from yaml file.
     * Validates config and environment properties. This method is not thread safe.
     *
     * @param credentialsFile Path to yaml-file with config info
     * @throws IllegalArgumentException if environment is not set up correctly
     */
    @SuppressWarnings("unchecked")
    public static void load(String credentialsFile, String suffix) {
        LOGGER.debug("Environment.load(String credentialsFile) is working.");
        //Load config file
        Yaml yaml = new Yaml();
        try (FileInputStream in = new FileInputStream(credentialsFile)) {

            // load the whole YAML file
            Map<String, Object> credentials = (Map<String, Object>) yaml.load(in);
            // calculate suffix for environments
            String localSuffix = StringUtils.isBlank(suffix) ? "" : suffix;

            Map<String, String> environments = new HashMap<>(); // general environment properties
            List<String> reportsList = null;          // reports list
            Map<String, String> tmpEnv;                         // tmp environment

            for (String environment : ENVIRONMENTS) {

                if (REPORTS_ENV_PROPERTY.equals(environment)) { // don't add suffix to [reports] environment
                    // get reports list
                    reportsList = (List<String>) credentials.get(environment);
                } else { // add suffix to all other environments and process them
                    tmpEnv = (Map<String, String>) credentials.get(environment + localSuffix);

                    if (tmpEnv == null || tmpEnv.isEmpty()) { // we can't load environment -> fast-fail
                        throw new IllegalStateException(String.format("Can't load environment [%s]!", environment + localSuffix));
                    }
                    // merge environment with others
                    // todo: what if properties with same name in different environments?
                    environments.putAll(tmpEnv);
                }

            } // end of FOR -> ENVIRONMENTS

            // validate merged environment
            validateEnvironment(environments);

            // initialize Environment instance
            instance = new Environment(environments, reportsList); // NOSONAR: Sonar asks to synchronize the instance,
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
            // todo: warning! -> string valueOf(null) -> "null" (string literal), not null value itself!
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

        if (value == null || "null".equals(value)) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("config", config)
                .toString();
    }

}
