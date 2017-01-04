package dg.social.ok;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * OK (Odnoklassniki) social network client.
 * Created by gusevdm on 1/3/2017.
 */

public class OkClient {

    private static final Log LOG = LogFactory.getLog(OkClient.class);

    // http client instance (own instance of client for each instance of VkClient)
    private final CloseableHttpClient HTTP_CLIENT  = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    private final HttpContext         HTTP_CONTEXT = new BasicHttpContext();
    private final RequestConfig       HTTP_REQUEST_CONFIG;

    private OkClientConfig config;

    /***/
    public OkClient(OkClientConfig config) {
        LOG.debug("OkClient constructor() working.");

        if (config == null) { // fail-safe
            throw new IllegalArgumentException("Can't create VkClient instance with NULL config!");
        }

        this.config = config;

        // init http request config (through builder)
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        // set proxy (if needed) for http request config
        if (this.config.getProxy() != null) { // add proxyHost to get http request
            requestConfigBuilder.setProxy(this.config.getProxy()).build();
        }

        // add cookies policy into http request config
        this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
    }

}
