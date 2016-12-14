package dg.social;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static dg.social.SocialNetsDefaults.HTTP_HEADERS;
import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Some useful HTTP-related utilities.
 * Created by gusevdm on 12/9/2016.
 */

public final class HttpUtilities {

    private static final Log LOG = LogFactory.getLog(HttpUtilities.class); // module logger

    private HttpUtilities() {} // utility class, can't instantiate

    /** Return content of http response as string. */
    public static String getPageContent(HttpEntity httpEntity, String encoding) throws IOException {
        LOG.debug("HttpUtilities.getPageContent() working.");

        if (httpEntity == null) { // return empty string for null entity
            return "";
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(httpEntity.getContent(), writer, StringUtils.isBlank(encoding) ? "UTF-8" : encoding);
        return writer.toString();
    }

    /** Sends POST HTTP request to URL with list of parameters. */
    public static void sendHttpPost(HttpClient httpClient, String url, List<NameValuePair> postParams, Header[] cookies) throws IOException {
        LOG.debug("HttpUtilities.sendPost() working.");

        // prepare post request to submit a form
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(HTTP_HEADERS);
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
