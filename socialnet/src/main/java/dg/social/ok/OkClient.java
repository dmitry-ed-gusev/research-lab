package dg.social.ok;

import dg.social.AbstractClient;
import dg.social.HttpFormType;
import dg.social.utilities.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static dg.social.CommonDefaults.DEFAULT_ENCODING;
import static dg.social.HttpFormType.ACCESS_TOKEN_FORM;
import static dg.social.HttpFormType.APPROVE_ACCESS_RIGHTS_FORM;
import static dg.social.HttpFormType.LOGIN_FORM;
import static dg.social.utilities.HttpUtilities.HTTP_DEFAULT_HEADERS;
import static dg.social.utilities.HttpUtilities.HTTP_GET_COOKIES_HEADER;

/**
 * OK (Odnoklassniki) social network client.
 * Created by gusevdm on 1/3/2017.
 */

public class OkClient extends AbstractClient {

    private static final Log LOG = LogFactory.getLog(OkClient.class);

    // attempts to get access token
    private final static int OK_ACCESS_ATTEMPTS_COUNT = 4;

    private String         accessToken; // OK client access token

    /***/
    public OkClient(OkClientConfig config, OkFormsRecognizer formsRecognizer) throws IOException {
        super(config, formsRecognizer);

        LOG.debug("OkClient constructor() working.");

        System.out.println("OK access token -> " + this.getAccessToken());
    }

    /***/
    private String getAccessToken() throws IOException {
        LOG.debug("OkClient.getAccessToken() working. [PRIVATE]");

        // generate and execute ACCESS_TOKEN request
        String okTokenRequest = this.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", okTokenRequest));

        // some tech variables
        CloseableHttpResponse httpResponse;     // store the whole http response
        Header[]              httpCookies;      // store http response cookies
        HttpEntity            httpEntity;       // store http response entity
        String                httpPageContent;  // store http response page content
        HttpFormType          receivedFormType; // store received VK form type

        // Initial HTTP request: execute http get request to token request URI
        httpResponse = this.sendHttpGet(okTokenRequest);

        try {

            // process login/access/add digits forms
            String actionUrl;
            List<NameValuePair> formParamsList;
            for (int counter = 1; counter <= OK_ACCESS_ATTEMPTS_COUNT; counter++) {

                // buffer initial received entity into memory
                httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    LOG.debug("Buffering received HTTP Entity.");
                    httpEntity = new BufferedHttpEntity(httpEntity);
                }

                httpCookies = httpResponse.getHeaders(HTTP_GET_COOKIES_HEADER); // save cookies

                // get page content for parsing
                httpPageContent = HttpUtilities.getPageContent(httpEntity, DEFAULT_ENCODING);
                //httpStringResponse = HttpUtilities.httpResponseToString(httpResponse, httpPageContent);
                if (LOG.isDebugEnabled()) { // just debug output
                    LOG.debug(HttpUtilities.httpResponseToString(httpResponse, httpPageContent));
                }

                Document doc = Jsoup.parse(httpPageContent); // parse returned page into Document object
                // check received form type
                receivedFormType = this.getHttpFormType(doc);
                LOG.debug(String.format("Got OK form: [%s].", receivedFormType));

                switch (receivedFormType) { // select action, based on form type

                    case LOGIN_FORM: // OK -> simple login form
                        LOG.debug(String.format("Processing [%s].", LOGIN_FORM));

                        actionUrl = HttpUtilities.getFirstFormActionURL(doc); // gets form action URL
                        LOG.debug(String.format("Form action: [%s].", actionUrl));

                        formParamsList = HttpUtilities.getFirstFormParams(doc, null); // get from and fill it in
                        if (LOG.isDebugEnabled()) { // just a debug
                            StringBuilder pairs = new StringBuilder();
                            formParamsList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                            LOG.debug(String.format("Found name-value pairs in OK login form:%n%s", pairs.toString()));
                        }

                        // prepare and execute next http request (send form)
                        //httpResponse = HttpUtilities.sendHttpPost(HTTP_CLIENT, HTTP_CONTEXT, HTTP_REQUEST_CONFIG, actionUrl, formParamsList, httpCookies);
                        break;

                    case APPROVE_ACCESS_RIGHTS_FORM: // OK -> approve application rights form
                        LOG.debug(String.format("Processing [%s].", APPROVE_ACCESS_RIGHTS_FORM));

                        actionUrl = HttpUtilities.getFirstFormActionURL(doc); // get form action URL
                        LOG.debug(String.format("Form action: [%s].", actionUrl));

                        formParamsList = HttpUtilities.getFirstFormParams(doc, null); // get from and fill it in
                        if (LOG.isDebugEnabled()) { // just a debug
                            StringBuilder pairs = new StringBuilder();
                            formParamsList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                            LOG.debug(String.format("Found name-value pairs in OK form:%n%s", pairs.toString()));
                        }

                        // prepare and execute next http request (send form)
                        //httpResponse = HttpUtilities.sendHttpPost(HTTP_CLIENT, HTTP_CONTEXT, HTTP_REQUEST_CONFIG, actionUrl, formParamsList, httpCookies);
                        break;

                    case ACCESS_TOKEN_FORM: // OK -> access token form (final)
                        LOG.debug(String.format("Processing [%s].", ACCESS_TOKEN_FORM));

                        // parse redirect and get access token from URL
                        RedirectLocations locations = this.getContextRedirectLocations();
                        if (locations != null) { // parse last redirect locations and get access token
                            // get the last redirect URI - it's what we need
                            URI finalUri = locations.getAll().get(locations.getAll().size() - 1);
                            String accessToken = StringUtils.split(StringUtils.split(finalUri.getFragment(), "&")[0], "=")[1];
                            LOG.debug(String.format("Received ACCESS_TOKEN: [%s].", accessToken));
                            //return new ImmutablePair<>(new Date(), accessToken);
                        } else { //
                            LOG.error("Can't find last redirect locations (list is null)!");
                        }
                        break;

                    default: // default case - unknown form
                        LOG.error(String.format("Got unknown type of form: [%s].", receivedFormType));
                }

            } // end of FOR cycle

        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }

        return null; // can't get access token
    }

    /***/
    public static void main(String[] args) throws IOException {
        LOG.info("OkClient starting...");

        // reading token from file
        StringBuilder strFile;
        try (FileReader fr = new FileReader("c:/temp/zzz.txt");
             BufferedReader br = new BufferedReader(fr)) {

            strFile = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                strFile.append(line).append("\n");
            }
        }
        LOG.debug(String.format("File has been read. Content: \n%s", strFile.toString()));

        Document form = Jsoup.parse(strFile.toString());
        LOG.debug("Form parsed.");

        System.out.println("form action -> " + HttpUtilities.getFirstFormActionURL(form));
        System.out.println("form params -> " + HttpUtilities.getFirstFormParams(form, null));

        Elements elements = form.getElementsByTag("label");
        System.out.println("->\n" + elements);

        for (Element element : elements) {
            System.out.println(element.attr("for") + " -> " + element.text());
        }

        OkFormsRecognizer formsRecognizer = new OkFormsRecognizer();
        System.out.println("***> " + formsRecognizer.getHttpFormType(form));
    }

}
