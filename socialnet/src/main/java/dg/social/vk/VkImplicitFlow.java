package dg.social.vk;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.StringWriter;
import java.net.URI;

/**
 * Implementation of VK Implicit Flow for API access.
 * Created by gusevdm on 12/6/2016.
 */
public class VkImplicitFlow {

    public static final String PROXY_HOST = "webproxy.merck.com";

    public static final String APP_ID = "5761788";
    public static final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    public static final String REQUEST_SCOPE = "friends,photos";
    public static final String ACCESS_TOKEN_REQUEST =
            "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=%s&scope=%s&response_type=token&v=5.60&state=123456";

    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkImplicitFlow.class);
        log.info("VK client starting...");

        // build request URI
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("bash.im")
                //.setPath("/")
                //.setParameter("" ,"") // <- empty parameter adds [?=] symbols to uri
                .build();
        log.debug(String.format("Generated URI [%s].",uri.toString()));

        //
        HttpHost proxyHost = new HttpHost(PROXY_HOST, 8080);

        // create default http client
        CloseableHttpClient httpclient = HttpClients.createDefault();

        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxyHost).build();

        // create get request
        HttpGet httpget = new HttpGet(uri);
        httpget.setConfig(requestConfig);

        // execute GET request with http client
        CloseableHttpResponse response = httpclient.execute(httpget);

        try {

            // print response headers
            HeaderIterator hIterator = response.headerIterator();
            while (hIterator.hasNext()) {
                log.debug(String.format("Header [%s].", hIterator.next()));
            }

            System.out.println("-> " + response.toString());
            System.out.println("-> " + response.getEntity().toString());

            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer, "windows-1251");
            String theString = writer.toString();

            System.out.println("-> " + theString);

        } finally {
            response.close();
            httpclient.close();
        }
    }

}
