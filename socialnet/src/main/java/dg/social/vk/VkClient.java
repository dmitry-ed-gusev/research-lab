package dg.social.vk;

import dg.social.AbstractClient;
import dg.social.HttpFormType;
import dg.social.domain.VkUser;
import dg.social.utilities.CommonUtilities;
import dg.social.utilities.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dg.social.CommonDefaults.DEFAULT_ENCODING;
import static dg.social.HttpFormType.ACCESS_TOKEN_FORM;
import static dg.social.HttpFormType.APPROVE_ACCESS_RIGHTS_FORM;
import static dg.social.HttpFormType.LOGIN_FORM;
import static dg.social.utilities.HttpUtilities.HTTP_DEFAULT_HEADERS;
import static dg.social.utilities.HttpUtilities.HTTP_GET_COOKIES_HEADER;

/**
 * VK (VKontakte) social network client.
 * Implemented:
 *  - receiving access token
 *  - search for users by simple query string
 * Created by gusevdm on 12/6/2016.
 */

// todo: implement periodically check of access token
public class VkClient extends AbstractClient {

    private static final Log LOG = LogFactory.getLog(VkClient.class); // module logger

    // todo: move it to abstract class
    // http client instance (own instance of client for each instance of VkClient)
    private final CloseableHttpClient HTTP_CLIENT  = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    private final HttpContext         HTTP_CONTEXT = new BasicHttpContext();
    private final RequestConfig       HTTP_REQUEST_CONFIG;

    // VK login form credentials
    private final Map<String, String> VK_LOGIN_FORM_CREDENTIALS;
    // attempts to get access token
    private final static int VK_ACCESS_ATTEMPTS_COUNT = 4;
    //private static final String VK_USER_LOGIN_MISSED_DIGITS = "96180114"; // todo: needed by 'add missed digits' form

    // VK login form email/pass elements
    private static final String LOGIN_FORM_EMAIL_KEY = "email";
    private static final String LOGIN_FORM_PASS_KEY  = "pass";
    private static final long   TOKEN_VALIDITY_SECONDS = 60 * 60 * 24; // token validity period (default)

    private Pair<Date, String> accessToken = null; // VK access token date/time and token value

    /** Create VkClient instance, working through proxy. */
    public VkClient(VkClientConfig config, VkFormsRecognizer formsRecognizer) throws IOException {
        super(config, formsRecognizer);

        LOG.debug("VkClient constructor() working.");

        // init http request config (through builder)
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        // set proxy (if needed) for http request config
        if (this.getConfig().getProxy() != null) { // add proxyHost to get http request
            requestConfigBuilder.setProxy(this.getConfig().getProxy()).build();
        }

        // add cookies policy into http request config
        this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        // create vk login form credentials
        this.VK_LOGIN_FORM_CREDENTIALS = new HashMap<String, String>() {{
            put(LOGIN_FORM_EMAIL_KEY, config.getUsername());
            put(LOGIN_FORM_PASS_KEY,  config.getPassword());
        }};

        // try to read VK access token from file
        try {
            Pair<Date, String> token = CommonUtilities.readAccessToken(config.getTokenFileName());
            // check access token validity (by time)
            if ((System.currentTimeMillis() - token.getLeft().getTime()) / 1000 < TOKEN_VALIDITY_SECONDS) { // token is valid (by date/time)
                this.accessToken = token;
            }
        } catch (IOException | ParseException e) {
            LOG.warn(String.format("Can't read access token from file: [%s]. Reason: [%s].",
                    config.getTokenFileName(), e.getMessage()));
        }

        // if we haven't read token from file - get new token (and write it to file)
        if (this.accessToken == null) {
            this.accessToken = this.getAccessToken();
            CommonUtilities.saveAccessToken(this.accessToken, this.getConfig().getTokenFileName(), true);
        }

    }

    /** Request and get VK access token (for using with API calls). With token method returns date/time, when token received. */
    private Pair<Date, String> getAccessToken() throws IOException {
        LOG.debug("VkClient.getAccessToken() working. [PRIVATE]");

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.getConfig().getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        // some tech variables
        CloseableHttpResponse httpResponse;     // store the whole http response
        Header[]              httpCookies;      // store http response cookies
        HttpEntity            httpEntity;       // store http response entity
        String                httpPageContent;  // store http response page content
        HttpFormType          receivedFormType; // store received VK form type

        // Initial HTTP request: execute http get request to token request URI
        HttpGet httpGetInitial = new HttpGet(vkTokenRequest);
        httpGetInitial.setHeaders(HTTP_DEFAULT_HEADERS);
        httpGetInitial.setConfig(HTTP_REQUEST_CONFIG);
        httpResponse = HTTP_CLIENT.execute(httpGetInitial); // execute request

        try {

            // process login/access/add digits forms
            String actionUrl;
            List<NameValuePair> formParamsList;
            for (int counter = 1; counter <= VK_ACCESS_ATTEMPTS_COUNT; counter++) {

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
                LOG.debug(String.format("Got VK form: [%s].", receivedFormType));

                switch (receivedFormType) { // select action, based on form type

                    case LOGIN_FORM: // VK Login form
                        LOG.debug(String.format("Processing [%s].", LOGIN_FORM));

                        actionUrl = HttpUtilities.getFirstFormActionURL(doc); // gets form action URL
                        LOG.debug(String.format("Form action: [%s].", actionUrl));

                        formParamsList = HttpUtilities.getFirstFormParams(doc, VK_LOGIN_FORM_CREDENTIALS); // get from and fill it in
                        if (LOG.isDebugEnabled()) { // just a debug
                            StringBuilder pairs = new StringBuilder();
                            formParamsList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                            LOG.debug(String.format("Found name-value pairs in VK login form:%n%s", pairs.toString()));
                        }

                        // prepare and execute next http request (send form)
                        httpResponse = HttpUtilities.sendHttpPost(HTTP_CLIENT, HTTP_CONTEXT, HTTP_REQUEST_CONFIG, actionUrl, formParamsList, httpCookies);
                        break;

                    case APPROVE_ACCESS_RIGHTS_FORM: // VK approve application rights
                        LOG.debug(String.format("Processing [%s].", APPROVE_ACCESS_RIGHTS_FORM));

                        actionUrl = HttpUtilities.getFirstFormActionURL(doc); // get form action URL
                        LOG.debug(String.format("Form action: [%s].", actionUrl));

                        formParamsList = HttpUtilities.getFirstFormParams(doc, null); // get from and fill it in
                        if (LOG.isDebugEnabled()) { // just a debug
                            StringBuilder pairs = new StringBuilder();
                            formParamsList.forEach(pair -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n", pair.getName(), pair.getValue())));
                            LOG.debug(String.format("Found name-value pairs in VK login form:%n%s", pairs.toString()));
                        }

                        // prepare and execute next http request (send form)
                        httpResponse = HttpUtilities.sendHttpPost(HTTP_CLIENT, HTTP_CONTEXT, HTTP_REQUEST_CONFIG, actionUrl, formParamsList, httpCookies);
                        break;

                    case ACCESS_TOKEN_FORM: // VK
                        LOG.debug(String.format("Processing [%s].", ACCESS_TOKEN_FORM));

                        // parse redirect and get access token from URL
                        RedirectLocations locations = (RedirectLocations) HTTP_CONTEXT.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
                        if (locations != null) { // parse last redirect locations and get access token
                            // get the last redirect URI - it's what we need
                            URI finalUri = locations.getAll().get(locations.getAll().size() - 1);
                            String accessToken = StringUtils.split(StringUtils.split(finalUri.getFragment(), "&")[0], "=")[1];
                            LOG.debug(String.format("Received ACCESS_TOKEN: [%s].", accessToken));
                            return new ImmutablePair<>(new Date(), accessToken);
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

    /**
     * Search for VK users. Uses VK API method [users.search].
     * @param userString String search string, can't be empty
     * @param fieldsList String list (comma separated) of fields for response
     * @param count int results count, if negative or equals to zero or greater, than 1000 - will be returned 1000 results
     */
    public String usersSearch(String userString, String fieldsList, int count) throws IOException, org.json.simple.parser.ParseException, URISyntaxException {
        LOG.debug(String.format("VkClient.usersSearch() working. Search string: [%s].", userString));

        if (StringUtils.isBlank(userString)) { // fail-fast
            throw new IllegalArgumentException("Cant' search users with empty search string!");
        }

        // generating query URI
        URI uri = new URI(new URIBuilder(String.format(this.getConfig().getBaseApiRequest(), "users.search"))
                .addParameter("q", userString)
                .addParameter("count", String.valueOf(count > 0 && count <= 1000 ? count : 1000))
                .addParameter("fields", (StringUtils.isBlank(fieldsList) ? "" : fieldsList))
                .addParameter("access_token", this.accessToken.getRight())
                .addParameter("v", this.getConfig().getApiVersion())
                .toString());
        LOG.debug(String.format("Generated URI: [%s].", uri));

        // execute http GET query
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeaders(HTTP_DEFAULT_HEADERS);
        httpGet.setConfig(HTTP_REQUEST_CONFIG);
        CloseableHttpResponse httpResponse = HTTP_CLIENT.execute(httpGet); // execute request

        // buffer initial received entity into memory
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            LOG.debug("Buffering received HTTP Entity.");
            httpEntity = new BufferedHttpEntity(httpEntity);
        }

        // get page content for parsing
        String httpPageContent = HttpUtilities.getPageContent(httpEntity, DEFAULT_ENCODING);
        if (LOG.isDebugEnabled()) { // just debug output
            LOG.debug(HttpUtilities.httpResponseToString(httpResponse, httpPageContent));
        }

        // return received JSON
        return httpPageContent;
    }

    /***/
    public List<VkUser> usersSearch(VkUser user) {
        // todo: implement search using user template parameter
        return null;
    }

}
