package dg.social.ok;

import dg.social.AbstractClientConfig;

import java.util.Properties;

/**
 * Config for OK (Odnoklassniki) client.
 * Created by gusevdm on 1/3/2017.
 */

public class OkClientConfig extends AbstractClientConfig {

    // keys for config parameters (for Properties object)
    private static final String DEFAULT_CONFIG_PREFIX = "ok";
    // auth window style: w - std window for full site version, m - mobile auth, a - simplified mobile without header
    private static final String DISPLAY_TYPE  = "w";
    // default redirect URI
    private static final String REDIRECT_URI  = "https://api.ok.ru/blank.html";
    // scope for requesting access token (see https://apiok.ru/ext/oauth/permissions)
    private static final String REQUEST_SCOPE = "VALUABLE_ACCESS";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST =
            "https://connect.ok.ru/oauth/authorize?client_id=%s&scope=%s&response_type=token&redirect_uri=%s&layout=%s&state=123456";

    /** Init instance with string values. */
    public OkClientConfig(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        super(username, password, appApiKey, tokenFileName, proxyHost, proxyPort);
    }

    /** Init instance with values from properties object. */
    public OkClientConfig(Properties properties) {
        super(DEFAULT_CONFIG_PREFIX, properties);
    }

    @Override
    public String getAccessTokenRequest() {
        return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), REQUEST_SCOPE, REDIRECT_URI, DISPLAY_TYPE);
    }

    @Override
    public String getBaseApiRequest() {
        return null;
    }

    @Override
    public String getApiVersion() {
        return null;
    }

}
