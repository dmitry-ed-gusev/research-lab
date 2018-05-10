package gusevdm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Holder for Enigma connection settings.
 */

@NotThreadSafe
public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    // regex pattern for checking invalid chars in URL
    private   static final Pattern URL_INVALID_CHARS              = Pattern.compile("[\\x00-\\x1F]");

    private static final String ABSTRACT_CREDENTIALS_PROPERTY = "abstract_credentials";

    private   static final String ABSTRACT_URL_PROPERTY           = "abstract_url";
    private   static final String ABSTRACT_API_KEY_PROPERTY       = "abstract_api_key";
    private   static final String KNOX_HDFS_URI_PROPERTY          = "knox.hdfs.uri";

    private   static final String METABASE_URL_PROPERTY           = "metabase_url";
    private   static final String METABASE_USER_PROPERTY          = "metabase_user";
    private   static final String METABASE_PASSWORD_PROPERTY      = "metabase_password";   // NOSONAR: Sonar treats the field as a hard-coded password

    private   static final String RIVER_URL_PROPERTY              = "river_url";
    private   static final String RIVER_API_KEY_PROPERTY          = "river_api_key";
    static final String RIVER_TIMEOUT_SECONDS_PROPERTY  = "river.timeout.seconds";
    static final String RIVER_TIMEOUT_ATTEMPTS_PROPERTY = "river.timeout.attempts";

    static final long   DEFAULT_RIVER_TIMEOUT_SECONDS   = 5L;
    static final int    DEFAULT_RIVER_TIMEOUT_ATTEMPTS  = 10;

    private   static final List<String> REQUIRED_PROPERTIES = Arrays.asList(
            ABSTRACT_URL_PROPERTY,
            ABSTRACT_API_KEY_PROPERTY,
            METABASE_URL_PROPERTY,
            METABASE_USER_PROPERTY,
            METABASE_PASSWORD_PROPERTY,
            RIVER_URL_PROPERTY,
            RIVER_API_KEY_PROPERTY
    );
    private static Environment instance;

    private Map<String, String> credentials = null;

    // For unit tests only. Use getInstance() instead.
    Environment() {
    }

    private Environment(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public String getAbstractUrl() {
        return credentials.get(ABSTRACT_URL_PROPERTY);
    }

    public String getAbstractApiKey() {
        return credentials.get(ABSTRACT_API_KEY_PROPERTY);
    }

    public String getKnoxHdfsURI() {
        return System.getProperty(KNOX_HDFS_URI_PROPERTY);
    }

    public String getMetabaseUrl() {
        return credentials.get(METABASE_URL_PROPERTY);
    }

    public String getMetabaseUser() {
        return credentials.get(METABASE_USER_PROPERTY);
    }

    public String getMetabasePassword() {
        return credentials.get(METABASE_PASSWORD_PROPERTY);
    }

    public String getRiverUrl() {
        return credentials.get(RIVER_URL_PROPERTY);
    }

    public String getRiverApiKey() {
        return credentials.get(RIVER_API_KEY_PROPERTY);
    }

    /**
     * Get River timeout in desired time unit. If invalid value is set, the default (5 seconds) is returned.
     * @param timeUnit the {@link TimeUnit} to convert the timeout to
     * @return the converted timeout
     */
    public long getRiverTimeout(TimeUnit timeUnit) {
        LOGGER.debug("Environment.getRiverTimeout() is working.");

        String timeoutString = System.getProperty(RIVER_TIMEOUT_SECONDS_PROPERTY);
        long timeout = DEFAULT_RIVER_TIMEOUT_SECONDS;
        try {
            timeout = Long.parseLong(timeoutString);
        } catch (NumberFormatException e) {
            String message = String.format("Value of River timeout is incorrect. Default will be used: %d seconds",
                    DEFAULT_RIVER_TIMEOUT_SECONDS);
            LOGGER.warn(message);
        }
        return timeUnit.convert(timeout, TimeUnit.SECONDS);
    }

    /**
     * Get River timeout attempts. If invalid value is set, the default (10) is returned.
     * @return the number of attempts of waiting for a state of a River dataset
     */
    public int getRiverTimeoutAttempts() {
        LOGGER.debug("Environment.getRiverTimeoutAttempts() is working.");

        String attemptsString = System.getProperty(RIVER_TIMEOUT_ATTEMPTS_PROPERTY);
        int attempts = DEFAULT_RIVER_TIMEOUT_ATTEMPTS;
        try {
            attempts = Integer.parseInt(attemptsString);
        } catch (NumberFormatException e) {
            String message = String.format("Value of River timeout attempts is incorrect. Default will be used: %d",
                    DEFAULT_RIVER_TIMEOUT_ATTEMPTS);
            LOGGER.warn(message);
        }
        return attempts;
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
     * @param credentialsFile Path to yaml-file with Enigma credentials info
     * @throws IllegalArgumentException if environment is not set up correctly
     */
    @SuppressWarnings("unchecked")
    static void load(String credentialsFile) {
        LOGGER.debug("Environment.load(String credentialsFile) is working.");
        //Load credentials file
        Yaml yaml = new Yaml();
        try (FileInputStream in = new FileInputStream(credentialsFile)) {
            Map<String, Object> credentials = (Map<String, Object>) yaml.load(in);
            Map<String, String> abstractCredentials = (Map<String, String>) credentials.get(ABSTRACT_CREDENTIALS_PROPERTY);
            validateEnvironment(abstractCredentials);
            instance = new Environment(abstractCredentials); // NOSONAR: Sonar asks to synchronize the instance,
            // but this class is intentionally not thread safe.
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid credentials file", e);
        }
    }

    /**
     * Make sure that all required system properties are set and not blank. The properties are:
     * <p>
     * <ul>
     * <li>abstract.uri
     * <li>abstract.api.key
     * <li>metabase.url
     * <li>metabase.user
     * <li>metabase.password
     * <li>river.uri
     * <li>river.api.key
     * </ul>
     *
     * @throws IllegalArgumentException if at least one of the system properties is invalid
     */
    private static void validateEnvironment(Map<String, String> credentials) {
        LOGGER.debug("Environment.validateEnvironment() is working.");

        // check presence of all required properties
        for (String property : REQUIRED_PROPERTIES) {
            validateProperty(property, credentials.getOrDefault(property, null));
        }
        validateProperty(KNOX_HDFS_URI_PROPERTY, System.getProperty(KNOX_HDFS_URI_PROPERTY));
        LOGGER.debug(String.format("Presence of all mandatory properties [%s] checked. All OK.", REQUIRED_PROPERTIES));

        // check URLs (Abstract/Metabase/River) for illegal characters
        Environment.checkUrl(credentials.get(ABSTRACT_URL_PROPERTY));
        Environment.checkUrl(credentials.get(METABASE_URL_PROPERTY));
        Environment.checkUrl(credentials.get(RIVER_URL_PROPERTY));
        LOGGER.debug("All URLs checked. All OK.");
    }

    private static void validateProperty(String property, String value) {
        if (value == null) {
            String errorMessage = String.format("Required system property is missing: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }
        if (value.trim().isEmpty()) {
            String errorMessage = String.format("System property is blank: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    static void checkUrl(String url) {
        if (URL_INVALID_CHARS.matcher(url).find()) {
            throw new IllegalArgumentException(String.format("URL [%s] contains invalid characters!", url));
        }
    }

}
