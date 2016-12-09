package dg.social.vk;

import dg.social.HttpUtilities;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VK Implicit Flow for API access.
 * Created by gusevdm on 12/6/2016.
 */

// https://www.mkyong.com/java/apache-httpclient-examples/
// http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
public class VkImplicitFlow {

    // proxy server parameters
    public static final String  PROXY_HOST = "webproxy.merck.com";
    public static final int     PROXY_PORT = 8080;
    public static final boolean USE_PROXY  = true;

    // VK application ID (APP_ID)
    public static final String APP_ID        = "5761788";
    // display type (page, popup, mobile)
    public static final String DISPLAY_TYPE  = "page";
    // default redirect URI for Implicit flow
    public static final String REDIRECT_URI  = "http://oauth.vk.com/blank.html";
    // scope for requesting access token (see https://vk.com/dev/permissions)
    public static final String REQUEST_SCOPE = "friends,photos";
    // request for access token
    public static final String ACCESS_TOKEN_REQUEST =
            "https://oauth.vk.com/authorize?client_id=%s&display=%s&redirect_uri=%s&scope=%s&response_type=token&v=5.60&state=123456";

    // client HTTP headers
    private static final String HTTP_HEADER_USER_AGENT      = "Mozilla/5.0";
    private static final String HTTP_HEADER_ACCEPT          = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String HTTP_HEADER_ACCEPT_LANGUAGE = "en-US,en;q=0.5";

    // <div> element class (div with this class holds login form for VK)
    public static final String LOGIN_FORM_DIV_CLASS         = "form_item fi_fat";

    /***/
    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkImplicitFlow.class);
        log.info("VK client starting...");

        // create default http client
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // create get request
        String vkTokenRequest = String.format(ACCESS_TOKEN_REQUEST, APP_ID, DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
        log.debug(String.format("URL: [%s].", vkTokenRequest));
        
        // create http get request
        HttpGet httpGet = new HttpGet(vkTokenRequest);
        //set get request headers
        httpGet.setHeader("User-Agent",      HTTP_HEADER_USER_AGENT);
        httpGet.setHeader("Accept",          HTTP_HEADER_ACCEPT);
        httpGet.setHeader("Accept-Language", HTTP_HEADER_ACCEPT_LANGUAGE);

        if (USE_PROXY) {
            // create proxy http host
            HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT);
            // add proxy to get request
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxyHost).build();
            httpGet.setConfig(requestConfig);
        }

        // execute GET request with http client
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {

            // print response headers
            HeaderIterator hIterator = response.headerIterator();
            while (hIterator.hasNext()) {
                log.debug(String.format("Header [%s].", hIterator.next()));
            }

            //System.out.println("-> " + response.toString());
            //System.out.println("-> " + response.getEntity().toString());

            // prepare and print response
            String pageContent = HttpUtilities.getPageContent(response.getEntity(), "UTF-8");
            System.out.println("-->\n" + pageContent);

            //
            Document doc = Jsoup.parse(pageContent);
            // Google form id
            //Element loginform = doc.getElementById("gaia_loginform");

            Element loginform = doc.getElementsByClass("form_item fi_fat").first();

            Elements inputElements = loginform.getElementsByTag("input");

            List<NameValuePair> paramList = new ArrayList<NameValuePair>();

            for (Element inputElement : inputElements) {
                String key = inputElement.attr("name");
                String value = inputElement.attr("value");

                System.out.println(String.format("---> key = [%s], value = [%s].", key, value));
                // fill in the form
                /*
                if (key.equals("Email"))
                    value = username;
                else if (key.equals("Passwd"))
                    value = password;
                */

                paramList.add(new BasicNameValuePair(key, value));

            }

        } finally {
            response.close();
            httpclient.close();
        }
    }

}
