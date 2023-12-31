package dgusev.web;

import static gusev.dmitry.jtils.UtilitiesDefaults.DEFAULT_ENCODING;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Some useful HTTP-related utilities.
 * Created by gusevdm on 12/9/2016.
 */

@CommonsLog
public final class MyHttpUtils {

    /** Connector USER AGENT header for HTTP requests */
    private static final String USER_AGENT                  = "Mozilla/5.0";

    /** Default http headers for http client. */
    public static final Header[] HTTP_DEFAULT_HEADERS = {
            new BasicHeader("User-Agent",      "Mozilla/5.0"),
            new BasicHeader("Accept",          "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
            //new BasicHeader("Accept-Language", "en-US,en;q=0.5"),
            new BasicHeader("Accept-Language", "ru-RU,ru;q=0.5"),
            new BasicHeader("Connection",      "keep-alive")
    };
    /***/
    public static final String HTTP_FORM_TAG              = "form";
    /***/
    public static final String HTTP_GET_COOKIES_HEADER    = "Set-Cookie";

    /***/
    private static final String HTTP_SET_COOKIES_HEADER    = "Cookie";
    /***/
    private static final String HTTP_CONTENT_TYPE_HEADER   = "Content-Type";
    /***/
    private static final String HTTP_CONTENT_TYPE_FORM     = "application/x-www-form-urlencoded"; //"x-www-form-urlencoded";
    /***/
    private static final String HTTP_FORM_ACTION_ATTR      = "action";
    /***/
    private static final String HTTP_FORM_INPUT_TAG        = "input";
    /***/
    private static final String HTTP_FORM_INPUT_KEY_ATTR   = "name";
    /***/
    private static final String HTTP_FORM_INPUT_VALUE_ATTR = "value";
    /** Regex pattern for checking invalid chars in URL. */
    private static final Pattern URL_INVALID_CHARS = Pattern.compile("[\\x00-\\x1F]");

    private MyHttpUtils() {} // utility class, can't instantiate

    /** Return content of http response as string. */
    public static String getPageContent(HttpEntity httpEntity, String encoding) throws IOException {
        log.debug("MyHttpUtils.getPageContent() working.");

        if (httpEntity == null) { // return empty string for null entity
            return "";
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(httpEntity.getContent(), writer, StringUtils.isBlank(encoding) ? DEFAULT_ENCODING : encoding);
        return writer.toString();
    }

    /**
     * Method for debug purposes - printing http response.
     * Page content passed separately, because entity in http response could be consumed only once.
     */
    public static String httpResponseToString(HttpResponse response, String pageContent) throws IOException {
        log.debug("MyHttpUtils.httpResponseToString() working.");

        if (response == null) { // check - is it null?
            return "[http request is null!]";
        }

        // processing http response
        StringBuilder result = new StringBuilder();
        result.append("\n========== HTTP Response: ==========\n");

        // get cookies and add them to result
        Header[] cookies = response.getHeaders(HTTP_GET_COOKIES_HEADER);
        for (Header cookie : cookies) {
            result.append(String.format("Cookie: [%s=%s]%n", cookie.getName(), cookie.getValue()));
        }

        result.append("\n");

        // get headers and add them
        HeaderIterator headerIterator = response.headerIterator();
        while (headerIterator.hasNext()) {
            result.append(String.format("Header: [%s]%n", headerIterator.next()));
        }

        result.append("\n");

        // get page content (http entity)
        if (!StringUtils.isBlank(pageContent)) {
            result.append(String.format("=== Page content: ===%n%s%n", pageContent));
        }

        return result.toString();
    }

    /**
     * Sends GET HTTP request to specified URI (of type URI).
     * By default http entity is buffered (wrapped into  BufferedHttpEntity).
     */
    public static CloseableHttpResponse sendHttpGet(CloseableHttpClient httpClient, HttpContext httpContext, RequestConfig requestConfig,
                                                    URI uri) throws IOException {
        log.debug("MyHttpUtils.sendHttpGet");

        if (httpClient == null || uri == null) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("HTTP Client is null [%s] or URI is empty [%s]!", httpClient == null, uri));
        }

        HttpGet httpGet = new HttpGet(uri);       // create http get request
        httpGet.setHeaders(HTTP_DEFAULT_HEADERS); // set default http headers
        // add request config
        if (requestConfig != null) {
            httpGet.setConfig(requestConfig);
        }

        CloseableHttpResponse response = httpClient.execute(httpGet, httpContext); // execute http request

        // buffer initial received entity into memory
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            log.debug("HTTP Entity isn't null. Buffering received HTTP Entity.");
            response.setEntity(new BufferedHttpEntity(httpEntity));
        } else {
            log.warn("Received HTTP Entity is NULL!");
            //response.setEntity(httpEntity); // <- // todo: do we need it??? check...
        }

        return response;
    }

    /**
     * Sends GET HTTP request to specified URI (of type String).
     * By default http entity is buffered (wrapped into  BufferedHttpEntity).
     */
    public static CloseableHttpResponse sendHttpGet(CloseableHttpClient httpClient, HttpContext httpContext, RequestConfig requestConfig,
                                                    String uri) throws IOException {
        return MyHttpUtils.sendHttpGet(httpClient, httpContext, requestConfig, URI.create(uri));
    }

    /**
     * Sends POST HTTP request to URI (of type URI) with list of parameters.
     * By default http entity is buffered (wrapped into  BufferedHttpEntity).
     */
    public static CloseableHttpResponse sendHttpPost(CloseableHttpClient httpClient, HttpContext httpContext, RequestConfig requestConfig,
                                                     URI uri, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        log.debug("MyHttpUtils.sendHttpPost() working.");

        if (httpClient == null || uri == null) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("HTTP Client is null [%s] or URI is empty [%s]!", httpClient == null, uri));
        }

        HttpPost httpPost = new HttpPost(uri);     // prepare post request to submit a form
        httpPost.setHeaders(HTTP_DEFAULT_HEADERS); // set default http headers
        httpPost.setHeader(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_FORM); // set content type
        // add cookies
        if (cookies != null && cookies.length > 0) {
            for (Header cookie : cookies) {
                httpPost.setHeader(HTTP_SET_COOKIES_HEADER, cookie.getValue());
            }
        }

        // add request config
        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }

        httpPost.setEntity(new UrlEncodedFormEntity(postParams)); // set http request entity

        CloseableHttpResponse response = httpClient.execute(httpPost, httpContext); // execute http request

        // buffer initial received entity into memory
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            log.debug("HTTP Entity isn't null. Buffering received HTTP Entity.");
            response.setEntity(new BufferedHttpEntity(httpEntity));
        } else {
            log.warn("Received HTTP Entity is NULL!");
            //response.setEntity(httpEntity); // <- // todo: do we need it??? check...
        }

        return response;
    }

    /**
     * Sends POST HTTP request to URI (of type String) with list of parameters.
     * By default http entity is buffered (wrapped into  BufferedHttpEntity).
     */
    public static CloseableHttpResponse sendHttpPost(CloseableHttpClient httpClient, HttpContext httpContext, RequestConfig requestConfig,
                                                     String uri, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        return MyHttpUtils.sendHttpPost(httpClient, httpContext, requestConfig, URI.create(uri), postParams, cookies);
    }

    /**
     * Returns action URL from first found html form in received Document.
     * If fixPrefix isn't null/empty and calculated URL starts with "/" - prefix will be added.
     */
    public static String getFirstFormActionURL(Document document, String fixPrefix) {
        log.debug("MyHttpUtils.getFirstFormActionURL() working.");

        if (document == null) { // fail-fast
            throw new IllegalArgumentException("Can't extract action URL from null html document!");
        }

        // extract form action URL
        String actionUrl = document.getElementsByTag(HTTP_FORM_TAG).first().attr(HTTP_FORM_ACTION_ATTR);
        log.debug(String.format("Found form action URL: [%s].", actionUrl));

        // fix extracted URL (if necessary)
        if (actionUrl.startsWith("/") && !StringUtils.isBlank(fixPrefix)) {
            actionUrl = fixPrefix + actionUrl;
            log.debug(String.format("Fixed extracted action URL: [%s].", actionUrl));
        }

        return actionUrl;
    }

    /** Returns action URL from first found html form in received Document. */
    public static String getFirstFormActionURL(Document document) {
        return MyHttpUtils.getFirstFormActionURL(document, null);
    }

    /** Return all parameters from VK login form (with email/pass added). */
    public static List<NameValuePair> getFirstFormParams(Document document, Map<String, String> formItems) {
        log.debug("MyHttpUtils.getFirstFormParams() working.");

        if (document == null) { // fail-fast
            throw new IllegalArgumentException("Can't extract form parameters from null html document!");
        }

        // get all <input> elements from first found html <form>
        Elements formInputElements = document.getElementsByTag(HTTP_FORM_TAG).first().getElementsByTag(HTTP_FORM_INPUT_TAG);

        // iterate over all found input elements
        List<NameValuePair> paramsList = new ArrayList<>();
        for (Element formInputElement : formInputElements) {
            String inputKey = formInputElement.attr(HTTP_FORM_INPUT_KEY_ATTR);
            String inputValue = formInputElement.attr(HTTP_FORM_INPUT_VALUE_ATTR);

            // put needed data into form parameters (fill in the form)
            if (formItems != null && !formItems.isEmpty()) {
                for (Map.Entry<String, String> formItem : formItems.entrySet()) {
                    if (inputKey.equals(formItem.getKey())) {
                        inputValue = formItem.getValue();
                    }
                }
            }

            // todo !!!!
            // add found parameter to parameters list
            //if (!StringUtils.isBlank(inputKey)) {
                paramsList.add(new BasicNameValuePair(inputKey, inputValue));
            //}
        } // end of FOR

        if (log.isDebugEnabled()) { // just a debug
            StringBuilder pairs = new StringBuilder();
            paramsList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
            log.debug(String.format("Found name-value pairs in html form:%n%s", pairs.toString()));
        }

        return paramsList;
    }

    /** Check URL for invalid chars. */
    public static void checkUrl(String url) {
        if (URL_INVALID_CHARS.matcher(url).find()) {
            throw new IllegalArgumentException(String.format("URL [%s] contains invalid characters!", url));
        }
    }

    /** Sending simple HTTP GET request. */
    public static Pair<Integer, String> sendGet(String url) throws IOException {
        log.debug(String.format("GeneralUtils.sendGet() is working. URL: [%s].", url));

        // build URL and open connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");                      // optional, default is GET
        con.setRequestProperty("User-Agent", USER_AGENT); // add request header

        // get response code
        int responseCode = con.getResponseCode();
        // get response text
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // build and return resulting tuple
        return new ImmutablePair<>(responseCode, response.toString());
    }

    // HTTP POST request
    // todo: method for future usage...
    private static void sendPost(String url) throws Exception {
        log.debug(String.format("GeneralUtils.sendPost() is working. URL: [%s].", url));

        //String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }


}
