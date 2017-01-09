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

    // default redirect URI
    private static final String REDIRECT_URI  = "https://api.ok.ru/blank.html";
    // scope for requesting access token (see https://apiok.ru/ext/oauth/permissions)
    private static final String REQUEST_SCOPE = "VALUABLE_ACCESS";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST =
            "https://connect.ok.ru/oauth/authorize?client_id={clientId}&scope={scope}&response_type={{response_type}}&redirect_uri={redirectUri}&layout={layout}&state={state}";

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
        return appApiKey;
    }

    @Override
    public HttpHost getProxy() {
        return null;
    }

}
