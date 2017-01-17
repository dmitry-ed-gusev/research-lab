package dg.social.ok;

import dg.social.AbstractClient;
import dg.social.HttpFormType;
import dg.social.utilities.CommonUtilities;
import dg.social.utilities.HttpUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.RedirectLocations;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static dg.social.CommonDefaults.DEFAULT_ENCODING;
import static dg.social.HttpFormType.ACCESS_TOKEN_FORM;
import static dg.social.HttpFormType.LOGIN_FORM;
import static dg.social.utilities.HttpUtilities.HTTP_GET_COOKIES_HEADER;

/**
 * OK (Odnoklassniki) social network client.
 * Created by gusevdm on 1/3/2017.
 */

public class OkClient extends AbstractClient {

    private static final Log LOG = LogFactory.getLog(OkClient.class);

    private final Map<String, String> OK_LOGIN_FORM_CREDENTIALS;         // VK login form credentials
    private static final String LOGIN_FORM_EMAIL_KEY = "fr.email";          // VK login form email element
    private static final String LOGIN_FORM_PASS_KEY  = "fr.password";           // VK login form pass element
    // attempts to get access token
    private final static int OK_ACCESS_ATTEMPTS_COUNT = 4;

    private String         accessToken; // OK client access token

    /***/
    public OkClient(OkClientConfig config, OkFormsRecognizer formsRecognizer) throws IOException {
        super(config, formsRecognizer);

        LOG.debug("OkClient constructor() working.");

        // init vk login form credentials
        this.OK_LOGIN_FORM_CREDENTIALS = new HashMap<String, String>() {{
            put(LOGIN_FORM_EMAIL_KEY, OkClient.this.getUsername());
            put(LOGIN_FORM_PASS_KEY,  OkClient.this.getPassword());
        }};

        System.out.println("OK access token -> " + this.getAccessToken());
    }

    /***/
    private Pair<Date, String> getAccessToken() throws IOException {
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
            for (int counter = 1; counter <= OK_ACCESS_ATTEMPTS_COUNT; counter++) {

                httpEntity = httpResponse.getEntity();                                        // get http entity
                httpCookies = httpResponse.getHeaders(HTTP_GET_COOKIES_HEADER);               // save cookies
                httpPageContent = HttpUtilities.getPageContent(httpEntity, DEFAULT_ENCODING); // get html page content

                if (LOG.isDebugEnabled()) { // just debug output
                    LOG.debug(HttpUtilities.httpResponseToString(httpResponse, httpPageContent));
                }

                Document doc = Jsoup.parse(httpPageContent);  // parse returned page into Document object
                receivedFormType = this.getHttpFormType(doc); // check received form type
                LOG.debug(String.format("Got OK form: [%s].", receivedFormType));

                switch (receivedFormType) { // select action, based on form type

                    case LOGIN_FORM: // OK -> simple login form
                        LOG.debug(String.format("Processing [%s].", LOGIN_FORM));
                        httpResponse = this.submitForm(doc, "https://connect.ok.ru", OK_LOGIN_FORM_CREDENTIALS, httpCookies);
                        break;

                    case ACCESS_TOKEN_FORM: // OK -> access token form (final)
                        // todo: implement getting secret_session_key
                        LOG.debug(String.format("Processing [%s].", ACCESS_TOKEN_FORM));
                        // parse redirect and get access token from URL
                        RedirectLocations locations = this.getContextRedirectLocations();
                        if (locations != null) { // parse last redirect locations and get access token
                            // get the last redirect URI (params) - it's what we need
                            //String uriParams =
                            //finalUri
                            URI finalUri = locations.getAll().get(locations.getAll().size() - 1);
                            //finalUri.get
                            String accessToken = StringUtils.split(StringUtils.split(finalUri.getFragment(), "&")[0], "=")[1];
                            LOG.debug(String.format("Received ACCESS_TOKEN: [%s].", accessToken));
                            return new ImmutablePair<>(new Date(), accessToken);
                        } else { //
                            LOG.error("Can't find last redirect locations (list is null)!");
                        }
                        break;

                    default: // default case - unknown form
                        LOG.error(String.format("Got unknown type of form: [%s].", receivedFormType));
                        String unknownFormFile = CommonUtilities.saveStringToFile(httpPageContent); // save unknown form to file (for analysis)
                        LOG.info(String.format("Unknown form save to file [%s].", unknownFormFile));
                        return null; // can't get access token
                }

            } // end of FOR cycle

        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }

        return null; // can't get access token
    }

}
