package dg.social.ok;

import dg.social.utilities.HttpUtilities;
import dg.social.vk.VkFormType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import static dg.social.CommonDefaults.DEFAULT_ENCODING;
import static dg.social.utilities.HttpUtilities.HTTP_DEFAULT_HEADERS;
import static dg.social.utilities.HttpUtilities.HTTP_GET_COOKIES_HEADER;

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

    private OkClientConfig config;      // OK client configuration
    private String         accessToken; // OK client access token

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

    /***/
    private String getAccessToken() throws IOException {
        LOG.debug("OkClient.getAccessToken() working. [PRIVATE]");

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.config.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        System.exit(777);

        // some tech variables
        CloseableHttpResponse httpResponse;     // store the whole http response
        Header[]              httpCookies;      // store http response cookies
        HttpEntity            httpEntity;       // store http response entity
        String                httpPageContent;  // store http response page content
        VkFormType            receivedFormType; // store received VK form type

        // Initial HTTP request: execute http get request to token request URI
        HttpGet httpGetInitial = new HttpGet(vkTokenRequest);
        httpGetInitial.setHeaders(HTTP_DEFAULT_HEADERS);
        httpGetInitial.setConfig(HTTP_REQUEST_CONFIG);
        httpResponse = HTTP_CLIENT.execute(httpGetInitial); // execute request

        try {

            // buffer initial received entity into memory
            httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                LOG.debug("Buffering received HTTP Entity.");
                httpEntity = new BufferedHttpEntity(httpEntity);
            }

            httpCookies = httpResponse.getHeaders(HTTP_GET_COOKIES_HEADER); // save cookies

            // get page content for parsing
            httpPageContent = HttpUtilities.getPageContent(httpEntity, DEFAULT_ENCODING);
            //httpStringResponse = HttpUtilities.httpResponseToString(httpResponse, httpPageContent);
            if (LOG.isDebugEnabled()) { // just debug output
                LOG.debug(HttpUtilities.httpResponseToString(httpResponse, httpPageContent));
            }

        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }


        return null;
    }

}
