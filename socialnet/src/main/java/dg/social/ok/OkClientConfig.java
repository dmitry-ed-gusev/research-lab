package dg.social.ok;

import dg.social.AbstractClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

/**
 * Config for OK (Odnoklassniki) client.
 * Created by gusevdm on 1/3/2017.
 */

// todo: move common behavior (for OK/VK) to abstract parent class

// https://apiok.ru/ext/oauth/client

public class OkClientConfig extends AbstractClientConfig {

    private static final Log LOG = LogFactory.getLog(OkClientConfig.class);

    // keys for config parameters (for Properties object)
    private static final String OK_CONFIG_KEY_PROXY_HOST  = "ok.proxy.host";
    private static final String OK_CONFIG_KEY_PROXY_PORT  = "ok.proxy.port";
    private static final String OK_CONFIG_KEY_USERNAME    = "ok.username";
    private static final String OK_CONFIG_KEY_PASSWORD    = "ok.password";
    private static final String OK_CONFIG_KEY_APP_API_KEY = "ok.app.api.key";
    private static final String OK_CONFIG_KEY_TOKEN_FILE  = "ok.access.token.file";

    // auth window style: w - std window for full site version, m - mobile auth, a - simplified mobile without header
    private static final String DISPLAY_TYPE  = "w";
    // default redirect URI
    private static final String REDIRECT_URI  = "https://api.ok.ru/blank.html";
    // scope for requesting access token (see https://apiok.ru/ext/oauth/permissions)
    private static final String REQUEST_SCOPE = "VALUABLE_ACCESS";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST =
            "https://connect.ok.ru/oauth/authorize?client_id=%s&scope=%s&response_type=token&redirect_uri=%s&layout=%s&state=123456";

    private String   appApiKey; // application id key
    private HttpHost proxy;     // proxy server for interacting with OK server

    /** Init instance with string values. */
    public OkClientConfig(String appApiKey, String proxyHost, int proxyPort) {
        LOG.debug("OkClientConfig constructor() working.");

        if (StringUtils.isBlank(appApiKey)) { // fail-fast
            throw new IllegalArgumentException(String.format("Empty mandatory parameter: app api key [%s]!", appApiKey));
        }

        // init internal state
        this.appApiKey = appApiKey;
        // set proxy
        if (!StringUtils.isBlank(proxyHost)) {
            this.proxy = new HttpHost(proxyHost, (proxyPort > 0 ? proxyPort : -1));
        }

    }

    public String getAppApiKey() {
        return this.appApiKey;
    }

    @Override
    public HttpHost getProxy() {
        return this.proxy;
    }

    /** Generates and return string http request for getting application ACCESS_TOKEN. */
    public String getAccessTokenRequest() {
        return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), REQUEST_SCOPE, REDIRECT_URI, DISPLAY_TYPE);
    }

}
