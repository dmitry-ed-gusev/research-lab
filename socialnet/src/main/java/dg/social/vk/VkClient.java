package dg.social.vk;

import dg.social.HttpUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dg.social.SocialNetsDefaults.DEFAULT_ENCODING;
import static dg.social.SocialNetsDefaults.MERCK_PROXY;

/**
 * Implementation of receiving ACCESS_TOKEN (for VK API access) using Implicit Flow.
 * Created by gusevdm on 12/6/2016.
 */

// https://www.mkyong.com/java/apache-httpclient-examples/
// http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/

// todo: implement config for concrete social network, with common interface
// todo: implement concrete methods for social network, with common interface

public class VkClient {

    private static final Log LOG = LogFactory.getLog(VkClient.class); // module logger

    // http client instance (own instance of client for each instance of VkClient)
    private final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    // VK user/app credentials (user, pass, api_id)
    private static final String VK_USER_LOGIN = "+79618011494";
    private static final String VK_USER_PASS  = "vinny-bot13";
    private static final String VK_APP_ID     = "5761788";

    // client HTTP headers
    private static final String HTTP_HEADER_USER_AGENT      = "Mozilla/5.0";
    private static final String HTTP_HEADER_ACCEPT          = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String HTTP_HEADER_ACCEPT_LANGUAGE = "en-US,en;q=0.5";

    // <div> element class (div with this class holds login form for VK)
    public static final String LOGIN_FORM_DIV_CLASS         = "form_item fi_fat";
    // VK login form email/pass elements
    private static final String LOGIN_FORM_EMAIL_KEY        = "email";
    private static final String LOGIN_FORM_PASS_KEY         = "pass";
    private static final String LOGIN_FORM_INPUT_ELEMENT    = "input";
    private static final String LOGIN_FORM_INPUT_ATTR_KEY   = "name";
    private static final String LOGIN_FORM_INPUT_ATTR_VALUE = "value";

    private VkClientConfig config;    // VK client configuration
    private HttpHost       proxyHost; // proxy for working trough

    /** Create VkClient instance, working through proxy. */
    public VkClient(VkClientConfig config, HttpHost proxyHost) {
        this.config    = config;
        this.proxyHost = proxyHost;
    }

    /** Create VkClient instance, working directly (without proxy). */
    public VkClient(VkClientConfig config) {
        this(config, null);
    }

    /**
     * Returns action URL from VK login form.
     */
    private static String getVKLoginFormActionURL(Document document) {
        LOG.debug("VkClient.getVKLoginFormActionURL() working.");
        Element loginForm   = document.getElementsByClass(LOGIN_FORM_DIV_CLASS).first();
        Element formElement = loginForm.getElementsByTag("form").first();
        return formElement.attr("action");
    }

    /**
     * Return all parameters from VK login form (with email/pass added).
     */
    private static List<NameValuePair> getVKLoginFormParams(Document document) {
        LOG.debug("VkClient.getVKLoginFormParams() working.");

        Element loginForm = document.getElementsByClass(LOGIN_FORM_DIV_CLASS).first(); // get first element with specified class
        Elements inputElements = loginForm.getElementsByTag(LOGIN_FORM_INPUT_ELEMENT); // get all form elements with type <input>

        // iterate over all input elements
        List<NameValuePair> paramList = new ArrayList<>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr(LOGIN_FORM_INPUT_ATTR_KEY);
            String value = inputElement.attr(LOGIN_FORM_INPUT_ATTR_VALUE);

            if (key.equals(LOGIN_FORM_EMAIL_KEY)) { // user in login form
                value = VK_USER_LOGIN;
            }

            if (key.equals(LOGIN_FORM_PASS_KEY)) { // pass in login form
                value = VK_USER_PASS;
            }

            // add found parameter to parameters list
            paramList.add(new BasicNameValuePair(key, value));
        } // end of FOR

        return paramList;
    }

    /***/
    public String getAccessToken() throws IOException {
        LOG.debug("VkClient.getAccessToken() working.");

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.config.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        // create http get request
        HttpGet httpGet = new HttpGet(vkTokenRequest);
        //set get request headers
        httpGet.setHeader("User-Agent",      HTTP_HEADER_USER_AGENT);
        httpGet.setHeader("Accept",          HTTP_HEADER_ACCEPT);
        httpGet.setHeader("Accept-Language", HTTP_HEADER_ACCEPT_LANGUAGE);

        if (this.proxyHost != null) { // add proxyHost to get http request
            RequestConfig requestConfig = RequestConfig.custom().setProxy(this.proxyHost).build();
            httpGet.setConfig(requestConfig);
        }

        // execute ACCESS_TOKEN http request
        CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);

        try {

            if (LOG.isDebugEnabled()) { // debug output of received headers
                // print response headers
                HeaderIterator hIterator = response.headerIterator();
                while (hIterator.hasNext()) {
                    LOG.debug(String.format("Header [%s].", hIterator.next()));
                }
            }

            // prepare and print response
            String pageContent = HttpUtilities.getPageContent(response.getEntity(), DEFAULT_ENCODING);
            if (LOG.isDebugEnabled()) {
                LOG.debug("-->\n" + pageContent);
            }

            // parse returned page
            Document doc = Jsoup.parse(pageContent);

            // get action attribute from html form
            String actionUrl = VkClient.getVKLoginFormActionURL(doc);
            LOG.debug(String.format("Form action: [%s].", actionUrl));

            // Google form id
            //Element loginform = doc.getElementById("gaia_loginform");
            //Element loginform = doc.getElementsByClass(LOGIN_FORM_DIV_CLASS).first();
            //Elements inputElements = loginform.getElementsByTag("input");

            // get vk login form parameters
            List<NameValuePair> paramList = VkClient.getVKLoginFormParams(doc);
            if (LOG.isDebugEnabled()) { // just a debug
                StringBuilder pairs = new StringBuilder();
                paramList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                LOG.debug(String.format("Found name-value pairs in VK login form:%n%s", pairs.toString()));
            }

            /*
            for (Element inputElement : inputElements) {
                String key = inputElement.attr("name");
                String value = inputElement.attr("value");

                System.out.println();
                // fill in the form
                if (key.equals("Email"))
                    value = username;
                else if (key.equals("Passwd"))
                    value = password;
                paramList.add(new BasicNameValuePair(key, value));
            }
            */

        } finally {
            response.close();
            //httpclient.close();
        }

        return null;
    }

    /***/
    public boolean isTokenAlive(String token) {
        LOG.debug("VkClient.isTokenAlive() working.");
        return false;
    }

    /***/
    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkClient.class);
        log.info("VK client starting...");

        // create VK config and client (with config)
        VkClientConfig config = new VkClientConfig(VK_USER_LOGIN, VK_USER_PASS, VK_APP_ID);
        VkClient vkClient = new VkClient(config, MERCK_PROXY); // client works through proxy

        // get access token for specified application (API_ID)
        String access_token = vkClient.getAccessToken();

        // create default http client
        //CloseableHttpClient httpclient = HttpClients.createDefault();
        // create get request
        //String vkTokenRequest = String.format(ACCESS_TOKEN_REQUEST, VK_APP_ID, DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
        //log.debug(String.format("URL: [%s].", vkTokenRequest));
        
        /*// create http get request
        HttpGet httpGet = new HttpGet(vkTokenRequest);
        //set get request headers
        httpGet.setHeader("User-Agent",      HTTP_HEADER_USER_AGENT);
        httpGet.setHeader("Accept",          HTTP_HEADER_ACCEPT);
        httpGet.setHeader("Accept-Language", HTTP_HEADER_ACCEPT_LANGUAGE);

        if (USE_PROXY) {
            // create proxyHost http host
            HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT);
            // add proxyHost to get request
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxyHost).build();
            httpGet.setConfig(requestConfig);
        }*/

        // execute GET request with http client
        //CloseableHttpResponse response = httpclient.execute(httpGet);


    }


}
