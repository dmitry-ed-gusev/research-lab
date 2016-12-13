package dg.social.vk;

import dg.social.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static dg.social.SocialNetsDefaults.DEFAULT_ENCODING;
import static dg.social.SocialNetsDefaults.HTTP_HEADERS;
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
    //private final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    private final CloseableHttpClient HTTP_CLIENT = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();

    // VK user/app credentials (user, pass, api_id)
    private static final String VK_USER_LOGIN               = "+79618011494";
    private static final String VK_USER_LOGIN_MISSED_DIGITS = "96180114";
    private static final String VK_USER_PASS                = "vinny-bot13";
    private static final String VK_APP_ID                   = "5761788";

    // <div> element class (div with this class holds login form for VK)
    private static final String LOGIN_FORM_DIV_CLASS         = "form_item fi_fat";
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

            if (key.equals("code")) { // put missed digits here
                value = VK_USER_LOGIN_MISSED_DIGITS;
            }

            // add found parameter to parameters list
            if (!StringUtils.isBlank(key)) {
                paramList.add(new BasicNameValuePair(key, value));
            }
        } // end of FOR

        return paramList;
    }

    /***/
    public String getAccessToken() throws IOException {
        LOG.debug("VkClient.getAccessToken() working.");

        // make sure cookies is turn on
        //CookieHandler.setDefault(new CookieManager());

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.config.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        // create http get request with default headers
        HttpGet httpGet = new HttpGet(vkTokenRequest);
        httpGet.setHeaders(HTTP_HEADERS);

        // builder for http request config
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if (this.proxyHost != null) { // add proxyHost to get http request
            requestConfigBuilder.setProxy(this.proxyHost).build();
        }

        RequestConfig requestConfig = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        httpGet.setConfig(requestConfig);

        //HttpClientContext context = HttpClientContext.create();
        CloseableHttpResponse responseGet = HTTP_CLIENT.execute(httpGet);

        Header[] cookies = responseGet.getHeaders("Set-Cookie");

        for (Header header : cookies) {
            System.out.println("===> " + header.getName() + ":::" + header.getValue());
        }

        //System.exit(777);

        try {

            if (LOG.isDebugEnabled()) { // debug output of received headers
                // print response headers
                HeaderIterator hIterator = responseGet.headerIterator();
                while (hIterator.hasNext()) {
                    LOG.debug(String.format("Header [%s].", hIterator.next()));
                }
            }

            // prepare and print response
            String pageContent = HttpUtilities.getPageContent(responseGet.getEntity(), DEFAULT_ENCODING);
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Received response:%n%s", pageContent));
            }

            // parse returned page
            Document doc = Jsoup.parse(pageContent);

            if (!doc.getElementsByClass(LOGIN_FORM_DIV_CLASS).isEmpty()) { // we've get login form - perform login
                LOG.debug("Received VK login form. Performing login.");

                // get action attribute from html form
                String actionUrl = VkClient.getVKLoginFormActionURL(doc);
                LOG.debug(String.format("Form action: [%s].", actionUrl));

                // get vk login form parameters
                List<NameValuePair> paramList = VkClient.getVKLoginFormParams(doc);
                if (LOG.isDebugEnabled()) { // just a debug
                    StringBuilder pairs = new StringBuilder();
                    paramList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                    LOG.debug(String.format("Found name-value pairs in VK login form:%n%s", pairs.toString()));
                }

                // save cookies
                //String cookies = response.getFirstHeader("Set-Cookie") == null ? "" : response.getFirstHeader("Set-Cookie").toString();
                //Header[] cookies = response.getHeaders("Set-Cookies");

                // send http post request (submit the form) - login to VK
                //HttpUtilities.sendPost(HTTP_CLIENT, actionUrl, paramList, cookies);
                // prepare post request to submit a form

                HttpPost httpPost = new HttpPost(actionUrl);
                httpPost.setHeaders(HTTP_HEADERS);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                for (Header header : cookies) {
                    httpPost.setHeader("Cookie", header.getValue());
                }
                httpPost.setConfig(requestConfig);
                // set entity
                httpPost.setEntity(new UrlEncodedFormEntity(paramList));

                // execute query #2
                HttpContext context = new BasicHttpContext();
                HttpResponse responsePost = HTTP_CLIENT.execute(httpPost, context);
                Header[] cookies2 = responsePost.getHeaders("Set-Cookie");
                for (Header header : cookies2) {
                    System.out.println("===> " + header.getName() + ":::" + header.getValue());
                }

                int responseCode = responsePost.getStatusLine().getStatusCode();

                System.out.println("\nSending 'POST' request to URL : " + actionUrl);
                System.out.println("Post parameters : " + paramList);
                System.out.println("Response Code : " + responseCode);

                System.out.println("===> " + httpPost.getURI());
                String pageContent2 = HttpUtilities.getPageContent(responsePost.getEntity(), "UTF-8");
                System.out.println("2--->\n" + pageContent2);

                // processing of "Add missing phone number digits" form

                if (LOG.isDebugEnabled()) { // debug output of received headers
                    // print response headers
                    HeaderIterator hIterator = responsePost.headerIterator();
                    while (hIterator.hasNext()) {
                        LOG.debug(String.format("Header [%s].", hIterator.next()));
                    }
                }
                // parse returned page #2
                Document doc2 = Jsoup.parse(pageContent2);

                URI finalUrl = httpPost.getURI();
                System.out.println("1************************* = " + finalUrl);

                RedirectLocations locations = (RedirectLocations) context.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
                if (locations != null) {
                    finalUrl = locations.getAll().get(locations.getAll().size() - 1);
                    System.out.println("2************************* = " + finalUrl);
                }


                // todo: the same check for form as previous one! (???)
                if (!doc2.getElementsByClass(LOGIN_FORM_DIV_CLASS).isEmpty()) { // we've get new form - add missed digits
                    LOG.debug("Received VK login form (#2). Performing login.");

                    // get action attribute from html form
                    String actionUrl2 = VkClient.getVKLoginFormActionURL(doc2);
                    LOG.debug(String.format("Form action: [%s].", actionUrl2));

                    // get vk login form parameters
                    List<NameValuePair> paramList2 = VkClient.getVKLoginFormParams(doc2);
                    if (LOG.isDebugEnabled()) { // just a debug
                        StringBuilder pairs = new StringBuilder();
                        paramList2.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                        LOG.debug(String.format("Found name-value pairs in VK login form:%n%s", pairs.toString()));
                    }

                    // prepare and run 3rd query - add missed digits to form
                    HttpPost httpPost2 = new HttpPost("https://vk.com" + actionUrl2);
                    httpPost2.setHeaders(HTTP_HEADERS);
                    httpPost2.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    for (Header header : cookies2) {
                        httpPost2.setHeader("Cookie", header.getValue());
                    }
                    httpPost2.setConfig(requestConfig);
                    // set entity
                    httpPost2.setEntity(new UrlEncodedFormEntity(paramList2));

                    // execute query #3
                    HttpResponse responsePost2 = HTTP_CLIENT.execute(httpPost2);
                    Header[] cookies3 = responsePost2.getHeaders("Set-Cookie");
                    for (Header header : cookies3) {
                        System.out.println("===> " + header.getName() + ":::" + header.getValue());
                    }

                    int responseCode2 = responsePost2.getStatusLine().getStatusCode();

                    System.out.println("\nSending 'POST' request to URL : " + actionUrl2);
                    System.out.println("Post parameters : " + paramList2);
                    System.out.println("Response Code : " + responseCode2);

                    System.out.println("===> " + httpPost2.getURI());
                    String pageContent3 = HttpUtilities.getPageContent(responsePost2.getEntity(), "UTF-8");
                    System.out.println("!!!--->\n" + pageContent3);


                } else {
                    LOG.error("Received unknown VK form (#2). Can't process!");
                }

            } else {
                LOG.error("Received unknown VK form. Can't process!");
            }

        } finally {
            responseGet.close();
            //httpclient.close();
        }

        return null;
    }

    /***/
    public void login() throws IOException {
        HttpGet httpGet = new HttpGet("https://vk.com/login");
        httpGet.setHeaders(HTTP_HEADERS);

        httpGet.setHeaders(HTTP_HEADERS);

        if (this.proxyHost != null) { // add proxyHost to get http request
            RequestConfig requestConfig = RequestConfig.custom().setProxy(this.proxyHost).build();
            httpGet.setConfig(requestConfig);
        }

        // execute ACCESS_TOKEN http request
        CloseableHttpResponse response = HTTP_CLIENT.execute(httpGet);

        try {

            // prepare and print response
            String pageContent = HttpUtilities.getPageContent(response.getEntity(), "windows-1251");
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Received response:%n%s", pageContent));
            }

        } finally {
            response.close();
        }


    }

    /***/
    public boolean isTokenAlive(String token) {
        LOG.debug("VkClient.isTokenAlive() working.");
        return false;
    }

    /***/
    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkClient.class);
        log.info("VK Client starting.");

        // create VK config and client (with config)
        VkClientConfig config = new VkClientConfig(VK_USER_LOGIN, VK_USER_PASS, VK_APP_ID);
        //VkClient vkClient = new VkClient(config); // client works without proxy
        VkClient vkClient = new VkClient(config, MERCK_PROXY); // client works through proxy

        // get access token for specified application (API_ID)
        String access_token = vkClient.getAccessToken();

        //vkClient.login();

        log.info("VK Client finished.");

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
