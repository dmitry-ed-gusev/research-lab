package dg.social.vk;

import dg.social.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static dg.social.HttpUtilities.*;
import static dg.social.vk.VkFormType.LOGIN_FORM;

/**
 * Implementation of receiving ACCESS_TOKEN (for VK API access) using Implicit Flow.
 * Created by gusevdm on 12/6/2016.
 */

// https://www.mkyong.com/java/apache-httpclient-examples/
// http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/

// todo: implement config for concrete social network, with common interface
// todo: implement concrete methods for social network, with common interface

// todo: processing of form "Allow rights for application"

public class VkClient {

    private static final Log LOG = LogFactory.getLog(VkClient.class); // module logger

    // http client instance (own instance of client for each instance of VkClient)
    //private final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    private final CloseableHttpClient HTTP_CLIENT = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    private final HttpContext HTTP_CONTEXT = new BasicHttpContext();
    private final RequestConfig HTTP_REQUEST_CONFIG;

    // VK user/app credentials (user, pass, api_id)
    private static final String VK_USER_LOGIN = "+79618011494";
    private static final String VK_USER_LOGIN_MISSED_DIGITS = "96180114";
    private static final String VK_USER_PASS = "vinny-bot13";
    private static final String VK_APP_ID = "5761788";

    // <div> element class (div with this class holds login form for VK)
    private static final String LOGIN_FORM_DIV_CLASS = "form_item fi_fat";
    // VK login form email/pass elements
    private static final String LOGIN_FORM_EMAIL_KEY = "email";
    private static final String LOGIN_FORM_PASS_KEY = "pass";
    private static final String LOGIN_FORM_INPUT_ELEMENT = "input";
    private static final String LOGIN_FORM_INPUT_ATTR_KEY = "name";
    private static final String LOGIN_FORM_INPUT_ATTR_VALUE = "value";

    private VkClientConfig config;    // VK client configuration
    private HttpHost proxyHost; // proxy for working trough

    /**
     * Create VkClient instance, working through proxy.
     */
    public VkClient(VkClientConfig config, HttpHost proxyHost) {
        this.config = config;
        this.proxyHost = proxyHost;
        // init http request config (through builder)
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // set proxy (if needed)
        if (this.proxyHost != null) { // add proxyHost to get http request
            requestConfigBuilder.setProxy(this.proxyHost).build();
        }
        // add cookies policy into
        this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
    }

    /**
     * Create VkClient instance, working directly (without proxy).
     */
    public VkClient(VkClientConfig config) {
        this(config, null);
    }

    /**
     * Returns action URL from VK login form.
     */
    private static String getVKLoginFormActionURL(Document document) {
        LOG.debug("VkClient.getVKLoginFormActionURL() working.");
        Element loginForm = document.getElementsByClass(LOGIN_FORM_DIV_CLASS).first();
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
    private static VkFormType getVkFormType(Document doc) {
        LOG.debug("VkClient.getVkFormType() working.");

        if (doc == null) { // quick check
            return VkFormType.UNKNOWN_FORM;
        }

        String formTitle = doc.title(); // get form page title
        System.out.println("title -> " + formTitle);

        System.out.println("XXX -> " + doc.getElementsByTag("title").isEmpty());
        System.out.println("YYY -> " + doc.getElementsByClass(LOGIN_FORM_DIV_CLASS).first());
        // if title match and there is div with specified class - we've found
        if (LOGIN_FORM.getFormTitle().equalsIgnoreCase(formTitle) && !doc.getElementsByClass(LOGIN_FORM_DIV_CLASS).isEmpty()) {
            return LOGIN_FORM;
        }

        return VkFormType.UNKNOWN_FORM;
    }

    /***/
    // todo: take a look at exceptions processing
    public String getAccessToken() throws IOException {
        LOG.debug("VkClient.getAccessToken() working.");

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.config.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        // HTTP request #1: execute http get request to token request URI
        HttpGet httpGetInitial = new HttpGet(vkTokenRequest);
        httpGetInitial.setHeaders(HTTP_DEFAULT_HEADERS);
        httpGetInitial.setConfig(HTTP_REQUEST_CONFIG);
        // execute request
        CloseableHttpResponse httpResponseInitial = HTTP_CLIENT.execute(httpGetInitial);
        // buffer received entity into memory
        HttpEntity entity = httpResponseInitial.getEntity();
        if (entity != null) {
            //entity = new BufferedHttpEntity(entity);
            httpResponseInitial.setEntity(new BufferedHttpEntity(entity));
        }
        // save initial cookies
        Header[] httpInitialCookies = httpResponseInitial.getHeaders(HTTP_GET_COOKIES_HEADER);

        try {


            if (LOG.isDebugEnabled()) { // just debug output
                //LOG.debug(HttpUtilities.httpResponseToString(httpResponseInitial, true));
            }

            // get page content for parsing
            String pageContent = HttpUtilities.getPageContent(httpResponseInitial.getEntity(), HTTP_DEFAULT_CONTENT_ENCODING);
            //if (LOG.isDebugEnabled()) {
            //    LOG.debug(String.format("Received response:%n%s", pageContent));
            //}

            System.out.println("SSSSSSSSSSSSSSS -> " + pageContent);

            if (LOG.isDebugEnabled()) { // just debug output
                LOG.debug("67676767676767 -> " + HttpUtilities.httpResponseToString(httpResponseInitial, true));
            }

            // parse returned page
            Document doc = Jsoup.parse(pageContent);
            System.out.println("form type -> " + VkClient.getVkFormType(doc));
            System.out.println("?????? -> " + VkClient.getVKLoginFormActionURL(doc));

            System.exit(777);

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
                httpPost.setHeaders(HTTP_DEFAULT_HEADERS);
                httpPost.setHeader(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_FORM);
                for (Header header : httpInitialCookies) {
                    httpPost.setHeader(HTTP_SET_COOKIES_HEADER, header.getValue());
                }
                httpPost.setConfig(HTTP_REQUEST_CONFIG);
                // set entity
                httpPost.setEntity(new UrlEncodedFormEntity(paramList));

                // execute query #2

                HttpResponse responsePost = HTTP_CLIENT.execute(httpPost, HTTP_CONTEXT);
                Header[] cookies2 = responsePost.getHeaders(HTTP_GET_COOKIES_HEADER);
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

                RedirectLocations locations = (RedirectLocations) HTTP_CONTEXT.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
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
                    httpPost2.setHeaders(HTTP_DEFAULT_HEADERS);
                    httpPost2.setHeader(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_FORM);
                    for (Header header : cookies2) {
                        httpPost2.setHeader(HTTP_SET_COOKIES_HEADER, header.getValue());
                    }
                    httpPost2.setConfig(HTTP_REQUEST_CONFIG);
                    // set entity
                    httpPost2.setEntity(new UrlEncodedFormEntity(paramList2));

                    // execute query #3
                    HttpResponse responsePost2 = HTTP_CLIENT.execute(httpPost2);
                    Header[] cookies3 = responsePost2.getHeaders(HTTP_GET_COOKIES_HEADER);
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
            httpResponseInitial.close();
            //httpclient.close();
        }

        return null;
    }

    /***/
    public boolean isTokenAlive(String token) {
        LOG.debug("VkClient.isTokenAlive() working.");

        // todo: implementation

        return false;
    }

    /***/
    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkClient.class);
        log.info("VK Client starting.");

        // create VK config and client (with config)
        VkClientConfig config = new VkClientConfig(VK_USER_LOGIN, VK_USER_PASS, VK_APP_ID);
        //VkClient vkClient = new VkClient(config); // client works without proxy
        VkClient vkClient = new VkClient(config, HTTP_DEFAULT_PROXY); // client works through proxy

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
