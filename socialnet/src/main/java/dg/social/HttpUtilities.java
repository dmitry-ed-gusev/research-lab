package dg.social;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dg.social.vk.VkFormType.VK_LOGIN_FORM_DIV_CLASS;

/**
 * Some useful HTTP-related utilities.
 * Created by gusevdm on 12/9/2016.
 */

// todo: move defaults to other (separate) class?

public final class HttpUtilities {

    private static final Log LOG = LogFactory.getLog(HttpUtilities.class); // module logger

    /** Default encoding for content. */
    public static final String   HTTP_DEFAULT_CONTENT_ENCODING = "UTF-8";

    /** Default http proxy server (Merck). */
    public static final HttpHost HTTP_DEFAULT_PROXY = new HttpHost("webproxy.merck.com", 8080);

    /** Default http headers for http client. */
    public static final Header[] HTTP_DEFAULT_HEADERS = {
            new BasicHeader("User-Agent",      "Mozilla/5.0"),
            new BasicHeader("Accept",          "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
            //new BasicHeader("Accept-Language", "en-US,en;q=0.5"),
            new BasicHeader("Accept-Language", "ru-RU,ru;q=0.5"),
            new BasicHeader("Connection",      "keep-alive")
    };

    /***/
    public static final String HTTP_GET_COOKIES_HEADER    = "Set-Cookie";
    /***/
    public static final String HTTP_SET_COOKIES_HEADER    = "Cookie";
    /***/
    public static final String HTTP_CONTENT_TYPE_HEADER   = "Content-Type";
    /***/
    public static final String HTTP_CONTENT_TYPE_FORM     = "application/x-www-form-urlencoded";
    /***/
    public static final String HTTP_FORM_TAG              = "form";
    /***/
    public static final String HTTP_FORM_ACTION_ATTR      = "action";
    /***/
    public static final String HTTP_FORM_INPUT_TAG        = "input";
    /***/
    public static final String HTTP_FORM_INPUT_KEY_ATTR   = "name";
    /***/
    public static final String HTTP_FORM_INPUT_VALUE_ATTR = "value";

    private HttpUtilities() {} // utility class, can't instantiate

    /** Return content of http response as string. */
    public static String getPageContent(HttpEntity httpEntity, String encoding) throws IOException {
        LOG.debug("HttpUtilities.getPageContent() working.");

        if (httpEntity == null) { // return empty string for null entity
            return "";
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(httpEntity.getContent(), writer, StringUtils.isBlank(encoding) ? HTTP_DEFAULT_CONTENT_ENCODING : encoding);
        return writer.toString();
    }

    /**
     * Method for debug purposes - printing http response.
     * Page content passed separately, because entity in http response could be consumed only once.
     */
    public static String httpResponseToString(HttpResponse response, String pageContent) throws IOException {
        LOG.debug("HttpUtilities.httpResponseToString() working.");

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

    /** Sends POST HTTP request to URL with list of parameters. */
    public static CloseableHttpResponse sendHttpPost(CloseableHttpClient httpClient, HttpContext httpContext, RequestConfig requestConfig, String url, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        LOG.debug("HttpUtilities.sendPost() working.");

        if (httpClient == null) { // fail-fast
            throw new IllegalArgumentException("Can't execute http request via null http client!");
        }

        if (StringUtils.isBlank(url)) { // fail-fast
            throw new IllegalArgumentException("Can't send http post request to empty url!");
        }

        HttpPost httpPost = new HttpPost(url);     // prepare post request to submit a form
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

        // execute request and return response
        return httpClient.execute(httpPost, httpContext);
    }

    /** Returns action URL from first found html form. */
    public static String getFirstFormActionURL(Document document) {
        LOG.debug("HttpUtilities.getFirstFormActionURL() working.");

        if (document == null) { // fail-fast
            throw new IllegalArgumentException("Can't extract action URL from null html document!");
        }

        return document.getElementsByTag(HTTP_FORM_TAG).first().attr(HTTP_FORM_ACTION_ATTR);
    }

    /** Return all parameters from VK login form (with email/pass added). */
    public static List<NameValuePair> getFirstFormParams(Document document, Map<String, String> formItems) {
        LOG.debug("HttpUtilities.getFirstFormParams() working.");

        if (document == null) { // fail-fast
            throw new IllegalArgumentException("Can't extract form parameters from null html document!");
        }

        // get all <input> elements from first found html <form>
        Elements formInputElements = document.getElementsByTag(HTTP_FORM_TAG).first().getElementsByTag(HTTP_FORM_INPUT_TAG);

        // iterate over all found input elements
        List<NameValuePair> paramList = new ArrayList<>();
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

            // add found parameter to parameters list
            if (!StringUtils.isBlank(inputKey)) {
                paramList.add(new BasicNameValuePair(inputKey, inputValue));
            }
        } // end of FOR

        return paramList;
    }

}
