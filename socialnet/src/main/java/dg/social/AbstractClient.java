package dg.social;

import dg.social.utilities.HttpUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Base abstract class for social networks clients.
 * Created by gusevdm on 1/10/2017.
 */

public class AbstractClient {

    private static final Log LOG = LogFactory.getLog(AbstractClient.class);

    // http client instance (own instance of http client for each instance of social network client)
    private final CloseableHttpClient HTTP_CLIENT  = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    private final HttpContext         HTTP_CONTEXT = new BasicHttpContext();
    private final RequestConfig       HTTP_REQUEST_CONFIG;

    private AbstractClientConfig config;
    private HttpFormRecognizer   formRecognizer;

    /***/
    public AbstractClient(AbstractClientConfig config, HttpFormRecognizer formRecognizer) {
        LOG.debug("AbstractClient constructor() working.");

        if (config == null || formRecognizer == null) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Empty mandatory parameter: config [%s], recognizer [%s]!", config, formRecognizer));
        }

        this.config         = config;
        this.formRecognizer = formRecognizer;

        // init http request config (through builder)
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // set proxy (if needed) for http request config
        if (this.config.getProxy() != null) { // add proxyHost to http request config
            requestConfigBuilder.setProxy(this.config.getProxy()).build();
        }
        // add cookies policy into http request config
        this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();

    }

    /***/
    public String getUsername() {
        return this.config.getUsername();
    }

    /***/
    public String getPassword() {
        return this.config.getPassword();
    }

    /***/
    public String getAccessTokenRequest() {
        return this.config.getAccessTokenRequest();
    }

    /***/
    public String getTokenFileName() {
        return this.config.getTokenFileName();
    }

    /***/
    public String getApiVersion() {
        return this.config.getApiVersion();
    }

    /***/
    public String getBaseApiRequest() {
        return this.config.getBaseApiRequest();
    }

    /***/
    public HttpFormType getHttpFormType(Document document) {
        return this.formRecognizer.getHttpFormType(document);
    }

    /***/
    public CloseableHttpResponse sendHttpGet(String uri) throws IOException {
        LOG.debug("AbstractClient.sendHttpGet(String) working.");
        return HttpUtilities.sendHttpGet(this.HTTP_CLIENT, this.HTTP_CONTEXT, this.HTTP_REQUEST_CONFIG, uri);
    }

    /***/
    public CloseableHttpResponse sendHttpGet(URI uri) throws IOException {
        LOG.debug("AbstractClient.sendHttpGet(URI) working.");
        return HttpUtilities.sendHttpGet(this.HTTP_CLIENT, this.HTTP_CONTEXT, this.HTTP_REQUEST_CONFIG, uri);
    }

    /***/
    public CloseableHttpResponse sendHttpPost(String uri, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        LOG.debug("AbstractClient.sendHttpPost(String) working.");
        return HttpUtilities.sendHttpPost(this.HTTP_CLIENT, this.HTTP_CONTEXT, this.HTTP_REQUEST_CONFIG, uri, postParams, cookies);
    }

    /***/
    public CloseableHttpResponse sendHttpPost(URI uri, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        LOG.debug("AbstractClient.sendHttpPost(URI) working.");
        return HttpUtilities.sendHttpPost(this.HTTP_CLIENT, this.HTTP_CONTEXT, this.HTTP_REQUEST_CONFIG, uri, postParams, cookies);
    }

    /***/
    public RedirectLocations getContextRedirectLocations() {
        LOG.debug("AbstractClient.getContextRedirectLocations() working.");
        return (RedirectLocations) this.HTTP_CONTEXT.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
    }


}
