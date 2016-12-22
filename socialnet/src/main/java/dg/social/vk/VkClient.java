package dg.social.vk;

import dg.social.CommonUtilities;
import dg.social.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dg.social.CommonsDefaults.DATE_TIME_FORMAT;
import static dg.social.CommonsDefaults.DEFAULT_ENCODING;
import static dg.social.HttpUtilities.*;
import static dg.social.vk.VkFormType.*;

/**
 * Implementation of receiving ACCESS_TOKEN (for VK API access) using Implicit Flow.
 * Created by gusevdm on 12/6/2016.
 */

// todo: implement periodically check of access token
public class VkClient {

    private static final Log LOG = LogFactory.getLog(VkClient.class); // module logger

    // http client instance (own instance of client for each instance of VkClient)
    private final CloseableHttpClient HTTP_CLIENT  = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
    private final HttpContext         HTTP_CONTEXT = new BasicHttpContext();
    private final RequestConfig       HTTP_REQUEST_CONFIG;
    private final Map<String, String> VK_LOGIN_FORM_CREDENTIALS;

    // attempts to get access token
    private final static int VK_ACCESS_ATTEMPTS_COUNT = 4;
    // VK user/app credentials (user, pass, api_id)
    private static final String VK_USER_LOGIN = "+79618011494";
    private static final String VK_USER_LOGIN_MISSED_DIGITS = "96180114";
    private static final String VK_USER_PASS = "vinny-bot13";
    private static final String VK_APP_ID = "5761788";
    // VK login form email/pass elements
    private static final String LOGIN_FORM_EMAIL_KEY = "email";
    private static final String LOGIN_FORM_PASS_KEY = "pass";

    private VkClientConfig     config;      // VK client configuration
    private HttpHost           proxyHost;   // proxy for working trough // todo: do we need this field?
    private Pair<Date, String> accessToken; // VK access token date/time and token value

    // todo: implement checking validity (by time) access token from file

    /** Create VkClient instance, working through proxy. */
    public VkClient(VkClientConfig config, HttpHost proxyHost) throws IOException {
        this.config    = config;
        this.proxyHost = proxyHost;
        // init http request config (through builder)
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // set proxy (if needed) for http request config
        if (this.proxyHost != null) { // add proxyHost to get http request
            requestConfigBuilder.setProxy(this.proxyHost).build();
        }
        // add cookies policy into http request config
        this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        // create vk login form credentials
        this.VK_LOGIN_FORM_CREDENTIALS = new HashMap<String, String>() {{
            put(LOGIN_FORM_EMAIL_KEY, VK_USER_LOGIN);
            put(LOGIN_FORM_PASS_KEY,  VK_USER_PASS);
        }};

        // read or get (obtain) VK access token
        try {
            Pair<Date, String> token = CommonUtilities.readAccessToken(config.getAccessTokenFileName());
            // check access token validity (by time)
            // todo: implement validation
            if (true) { // token is valid (by date/time)
                this.accessToken = token;
            } else { // token is invalid
                // todo: implement
            }
        } catch (IOException | ParseException e) {
            LOG.warn(String.format("Can't read access token from file: [%s]. Reason: [%s].",
                    config.getAccessTokenFileName(), e.getMessage()));
            // If we can't read access token from file - get (obtain) new token.
            // IOException will be thrown outside constructor.
            this.accessToken = this.getAccessToken();
            // write new obtained token (overwrite existing file)
            CommonUtilities.saveAccessToken(this.accessToken, this.config.getAccessTokenFileName(), true);
        }

    }

    /** Create VkClient instance, working directly (without proxy). */
    public VkClient(VkClientConfig config) throws IOException {
        this(config, null);
    }

    /***/
    private static VkFormType getVkFormType(Document doc) {
        LOG.debug("VkClient.getVkFormType() working.");

        if (doc == null) { // quick check
            LOG.warn("Received document is null!");
            return VkFormType.UNKNOWN_FORM;
        }

        // get form page <title> value
        String formTitle = doc.title();
        LOG.debug(String.format("Form title: [%s].", formTitle));

        // get text from first element with op_info class
        Element firstOpInfo = doc.body().getElementsByClass(VK_OP_INFO_CLASS_NAME).first();
        String opInfoText = (firstOpInfo == null ? "" : firstOpInfo.text());
        LOG.debug(String.format("DIV by class [%s] text: [%s].", VK_OP_INFO_CLASS_NAME, opInfoText));

        // if title match and there is div with specified class - we've found
        if (LOGIN_FORM.getFormTitle().equalsIgnoreCase(formTitle) && LOGIN_FORM.getOpInfoClassText().equalsIgnoreCase(opInfoText) &&
                !doc.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return LOGIN_FORM;
        }

        // approve rights form (adding new right for application)
        if (APPROVE_ACCESS_RIGHTS_FORM.getFormTitle().equalsIgnoreCase(formTitle) && APPROVE_ACCESS_RIGHTS_FORM.getOpInfoClassText().equalsIgnoreCase(opInfoText) &&
                !doc.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return APPROVE_ACCESS_RIGHTS_FORM;
        }

        // page with access token
        if (ACCESS_TOKEN_FORM.getFormTitle().equalsIgnoreCase(formTitle) && opInfoText.isEmpty() && doc.getElementsByTag(HTTP_FORM_TAG).isEmpty()) {
            return ACCESS_TOKEN_FORM;
        }

        return VkFormType.UNKNOWN_FORM;
    }

    /** Request and get VK access token (for using with API calls). With token method returns date/time, when token received. */
    private Pair<Date, String> getAccessToken() throws IOException {
        LOG.debug("VkClient.getAccessToken() working.");

        // generate and execute ACCESS_TOKEN request
        String vkTokenRequest = this.config.getAccessTokenRequest();
        LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", vkTokenRequest));

        // some tech variables
        CloseableHttpResponse httpResponse;     // store the whole http response
        Header[]              httpCookies;      // store http response cookies
        HttpEntity            httpEntity;       // store http response entity
        String                httpPageContent;  // store http response page content
        VkFormType            receivedFormType; // store received VK form type

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
                receivedFormType = VkClient.getVkFormType(doc);
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

        // we cannot get access token
        return null;
    }

    /***/
    public void search() {
        LOG.debug("VkClient.search() working.");
        // todo: implement
    }

    /***/
    public final static void main(String[] args) throws Exception {

        Log log = LogFactory.getLog(VkClient.class);
        log.info("VK Client starting.");

        // create VK config and client (with config)
        VkClientConfig config = new VkClientConfig(VK_USER_LOGIN, VK_USER_PASS, VK_APP_ID);
        VkClient vkClient = new VkClient(config); // client works without proxy
        //VkClient vkClient = new VkClient(config, HTTP_DEFAULT_PROXY); // client works through proxy

        /*
        // get access token for specified application (API_ID)
        Pair<Date, String> vkAccessToken = vkClient.getAccessToken();
        log.info(String.format("Got access token: [%s], date/time: [%s].", vkAccessToken.getRight(), DATE_TIME_FORMAT.format(vkAccessToken.getLeft())));

        // save vk access token to file
        CommonUtilities.saveAccessToken(vkAccessToken, "vk_token.dat", true);
        log.info(String.format("VK access_token: (date -> [%s], token -> [%s]) saved to file [%s].",
                vkAccessToken.getLeft(), vkAccessToken.getRight(), "vk_token.dat"));

        Pair<Date, String> accessToken = CommonUtilities.readAccessToken("vk_token.dat");
        log.info(String.format("VK access_token: (date -> [%s], token -> [%s]) has been read from file [%s].",
                accessToken.getLeft(), accessToken.getRight(), "vk_token.dat"));
        */

        log.info("VK Client finished.");

    }


}
