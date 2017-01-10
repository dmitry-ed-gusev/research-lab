package dg.social;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

import java.util.Properties;

/**
 * Base config class for social networks client's configs.
 * Created by gusevdm on 1/3/2017.
 */

public abstract class AbstractClientConfig {

    private static final Log LOG = LogFactory.getLog(AbstractClientConfig.class); // module logger

    private static final String CONFIG_KEY_PROXY_HOST  = "proxy.host";
    private static final String CONFIG_KEY_PROXY_PORT  = "proxy.port";
    private static final String CONFIG_KEY_USERNAME    = "username";
    private static final String CONFIG_KEY_PASSWORD    = "password";
    private static final String CONFIG_KEY_APP_API_KEY = "app.api.key";
    private static final String CONFIG_KEY_TOKEN_FILE  = "access.token.file";

    // default token file name (if not specified) - different name for each instance
    private final String DEFAULT_TOKEN_FILE_NAME = String.valueOf(System.currentTimeMillis()) + "_token.dat";

    // internal config state
    private String   username;      // username for vk client
    private String   password;      // password for vk client
    private String   appApiKey;     // application API_ID key
    private String   tokenFileName; // file name for storing access token
    private HttpHost proxy;         // proxy server for interacting with VK server

    /***/
    public AbstractClientConfig(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        LOG.debug("AbstractClientConfig constructor(String, String, String, String, String, int) working.");
        this.init(username, password, appApiKey, tokenFileName, proxyHost, proxyPort);
    }

    /***/
    public AbstractClientConfig(String configPrefix, Properties properties) {
        LOG.debug("AbstractClientConfig constructor(String, Properties) working.");

        String tmpPrefix = StringUtils.isBlank(configPrefix) ? "" : configPrefix + ".";

        this.init(
                properties.getProperty(tmpPrefix + CONFIG_KEY_USERNAME, ""),    // username
                properties.getProperty(tmpPrefix + CONFIG_KEY_PASSWORD, ""),    // password
                properties.getProperty(tmpPrefix + CONFIG_KEY_APP_API_KEY, ""), // application api key
                properties.getProperty(tmpPrefix + CONFIG_KEY_TOKEN_FILE, ""),  // temporary token file
                properties.getProperty(tmpPrefix + CONFIG_KEY_PROXY_HOST, ""),  // proxy host
                Integer.parseInt(properties.getProperty(tmpPrefix + CONFIG_KEY_PROXY_PORT, "-1")) // proxy port
        );

    }

    /***/
    private void init(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        LOG.debug("AbstractClientConfig.init() working. [PRIVATE]");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(appApiKey)) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Empty one or more mandatory parameter(s): username [%s], password [%s], app api key [%s]!", username, password, appApiKey));
        }

        // init internal state
        this.username      = username;
        this.password      = password;
        this.appApiKey     = appApiKey;
        this.tokenFileName = (StringUtils.isBlank(tokenFileName) ? DEFAULT_TOKEN_FILE_NAME : tokenFileName);

        // set a proxy for http requests
        if (!StringUtils.isBlank(proxyHost)) {
            this.proxy = new HttpHost(proxyHost, (proxyPort > 0 ? proxyPort : -1));
        }

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAppApiKey() {
        return appApiKey;
    }

    public String getTokenFileName() {
        return tokenFileName;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    /** Returns string uri for token request. */
    public abstract String getAccessTokenRequest();

    /** Returns base string uri for API request. */
    public abstract String getBaseApiRequest();

    /** Returns API version string. */
    public abstract String getApiVersion();

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("username", username)
                .append("password", password)
                .append("appApiKey", appApiKey)
                .append("tokenFileName", tokenFileName)
                .append("proxy", proxy)
                .append("token request", this.getAccessTokenRequest())
                .append("api request", this.getBaseApiRequest())
                .toString();
    }

}
