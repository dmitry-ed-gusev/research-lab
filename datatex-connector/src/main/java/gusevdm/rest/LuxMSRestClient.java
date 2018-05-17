package gusevdm.rest;

import gusevdm.Environment;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;

import java.io.IOException;
import java.util.List;

import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_DATAPATH;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_METADATA;

public class LuxMSRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSRestClient.class);

    private static final String LUXMS_LOGIN_PATH = "/api/auth/login?username=%s&password=%s";
    private static final String LUXMS_DATASETS_PATH = "/api/db/adm.datasets";

    // identity info
    private final String path;
    private final String user;
    private final String password;

    // session cookie info
    private Cookie sessionCookie;

    public LuxMSRestClient() {
        LOGGER.debug("LuxMSRestClient constructor() is working.");
        Environment env = Environment.getInstance();
        this.path = env.getLuxMSURL();
        this.user = env.getLuxMSUser();
        this.password = env.getLuxMSPassword();
    }

    /***/
    // todo: code from social crowler
    //public CloseableHttpResponse sendHttpPost(String uri, List<NameValuePair> postParams, Header[] cookies) throws IOException {
    //    LOG.debug("AbstractClient.sendHttpPost(String) working.");
    //    return HttpUtils.sendHttpPost(this.HTTP_CLIENT, this.HTTP_CONTEXT, this.HTTP_REQUEST_CONFIG, uri, postParams, cookies);
    //}

    /**
     * Login to LuxMS BI server and set {@link LuxMSRestClient#sessionCookie}.
     *
     * @throws RestException on request execution error
     */
    public void loginByGET() {
        LOGGER.debug("LuxMSRestClient.loginByGET() is working.");
        //JSONObject body = new JSONObject();
        //body.put("user",       user);
        //body.put("password",   password);
        //body.put("rememberMe", "true");
        //RestResponse response = this.executePost("/signin", body);
        RestResponse response = this.executeGet(String.format(LUXMS_LOGIN_PATH, this.user, this.password));
        sessionCookie = response.getCookie();
        System.out.println("---> " + sessionCookie);
    }

    /***/
    public void loginByPOST() {
        LOGGER.debug("LuxMSRestClient.loginByPOST() is working.");
        this.executeSimplePost("/api/auth/login", null, null);
    }

    /***/
    public void createDataset(String datasetTitle, String datasetDesc, boolean isVisible) {
        LOGGER.debug("LuxMSRestClient.createDataset() is working.");
        JSONObject body = new JSONObject();
        body.put("title", datasetTitle);
        body.put("description", datasetDesc);
        body.put("is_visible", isVisible ? 1 : 0);

        RestResponse response = this.executePost(LUXMS_DATASETS_PATH, body);
        System.out.println("--->" + response);
    }

    /***/
    public void removeDataset() {
        LOGGER.debug("LuxMSRestClient.removeDataset() is working.");
    }

    /***/
    public void updateDataset() {
        LOGGER.debug("LuxMSRestClient.updateDataset() is working.");
    }

    /***/
    public void hideDataset(){
        LOGGER.debug("LuxMSRestClient.hideDataset() is working.");
    }

    /***/
    public void linkDatasetToGroup() {
        LOGGER.debug("LuxMSRestClient.linkDatasetToGroup() is working.");
    }

    @Override
    protected String getPath() {
        return this.path;
    }

}
