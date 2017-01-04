package dg.social.vk;

import dg.social.AbstractClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

import java.util.Properties;

/**
 * Config for VK client.
 * Created by gusevdm on 12/12/2016.
 */
// todo: many parameters, maybe its better to implement Builder pattern?
public class VkClientConfig extends AbstractClientConfig {

    private static final Log LOG = LogFactory.getLog(VkClientConfig.class);

    // default name for token file
    private static final String DEFAULT_TOKEN_FILE_NAME   = "vk_token.dat";

    // keys for config parameters (for Properties object)
    private static final String VK_CONFIG_KEY_PROXY_HOST  = "vk.proxy.host";
    private static final String VK_CONFIG_KEY_PROXY_PORT  = "vk.proxy.port";
    private static final String VK_CONFIG_KEY_USERNAME    = "vk.username";
    private static final String VK_CONFIG_KEY_PASSWORD    = "vk.password";
    private static final String VK_CONFIG_KEY_APP_API_KEY = "vk.app.api.key";
    private static final String VK_CONFIG_KEY_TOKEN_FILE  = "vk.access.token.file";

    // access token request result - display type (page, popup, mobile)
    private static final String DISPLAY_TYPE  = "page";
    // default redirect URI for Implicit flow
    private static final String REDIRECT_URI  = "http://oauth.vk.com/blank.html";
    // scope for requesting access token (see https://vk.com/dev/permissions)
    private static final String REQUEST_SCOPE = "friends,groups,photos,audio";
            //"friends,photos,audio,video,pages,status,notes,messages,offline,docs,groups,notifications,stats,email";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST =
            "https://oauth.vk.com/authorize?client_id=%s&display=%s&redirect_uri=%s&scope=%s&response_type=token&v=5.60&state=123456";

    /** VK API URL. Here you should set method name (use String.format()). */
    public static final String VK_API_REQUEST_URI    = "https://api.vk.com/method/%s";
    /** VK API version. */
    public static final String VK_API_VERSION        = "5.60"; // last API version, 27.12.2016

    private String   username;      // username for vk client
    private String   password;      // password for vk client
    private String   appApiKey;     // application API_ID key
    private String   tokenFileName; // file name for storing access token
    private HttpHost proxy;         // proxy server for interacting with VK server

    /** Init instance with string values. */
    public VkClientConfig(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        LOG.debug("VkClientConfig constructor(String, String, String, String) working.");
        // call to internal initializing method
        this.init(username, password, appApiKey, tokenFileName, proxyHost, proxyPort);
    }

    /** Init instance with values from properties object. */
    public VkClientConfig(Properties properties) {
        LOG.debug("VkClientConfig constructor(Properties) working.");

        if (properties == null) { // fail-fast
            throw new IllegalArgumentException("Can't read NULL Properties object!");
        }

        // call to internal initializing method
        this.init(properties.getProperty(VK_CONFIG_KEY_USERNAME, ""), properties.getProperty(VK_CONFIG_KEY_PASSWORD, ""),
                properties.getProperty(VK_CONFIG_KEY_APP_API_KEY, ""), properties.getProperty(VK_CONFIG_KEY_TOKEN_FILE, ""),
                properties.getProperty(VK_CONFIG_KEY_PROXY_HOST, ""), Integer.parseInt(properties.getProperty(VK_CONFIG_KEY_PROXY_PORT, "-1")));
    }

    /***/
    private void init(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        LOG.debug("VkClientConfig.init() working. [PRIVATE]");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(appApiKey)) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Empty mandatory parameter: username [%s], password [%s], app api key [%s]!", username, password, appApiKey));
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
        return this.appApiKey;
    }

    public String getTokenFileName() {
        return tokenFileName;
    }

    @Override
    public HttpHost getProxy() {
        return proxy;
    }

    /** Generates and return string http request for getting application ACCESS_TOKEN. */
    public String getAccessTokenRequest() {
        return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
    }

}
