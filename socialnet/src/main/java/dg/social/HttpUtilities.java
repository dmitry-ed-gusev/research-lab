package dg.social;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

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
    public static final String HTTP_GET_COOKIES_HEADER  = "Set-Cookie";

    /***/
    public static final String HTTP_SET_COOKIES_HEADER  = "Cookie";

    /***/
    public static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type";

    /***/
    public static final String HTTP_CONTENT_TYPE_FORM   = "application/x-www-form-urlencoded";

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
    public static void sendHttpPost(HttpClient httpClient, String url, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        LOG.debug("HttpUtilities.sendPost() working.");

        // prepare post request to submit a form
        HttpPost httpPost = new HttpPost(url);
        //httpPost.setHeaders(HTTP_HEADERS);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        for (Header header : cookies) {
            httpPost.setHeader("Cookie", header.getValue());
        }

        // add header
        //post.setHeader("Host", "accounts.google.com");
        //httpPost.setHeader("User-Agent", USER_AGENT);
        //httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
        //post.setHeader("Cookie", getCookies());
        //httpPost.setHeader("Connection", "keep-alive");
        //post.setHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");

        // set entity
        httpPost.setEntity(new UrlEncodedFormEntity(postParams));

        // execute query
        HttpResponse response = httpClient.execute(httpPost);

        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        System.out.println("--->%n" + HttpUtilities.getPageContent(response.getEntity(), "UTF-8"));

        /*
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());
        */
    }

}
