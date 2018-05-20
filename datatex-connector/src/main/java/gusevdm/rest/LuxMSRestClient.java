package gusevdm.rest;

import gusevdm.Environment;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;

import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_DATAPATH;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_METADATA;

public class LuxMSRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSRestClient.class);

    // paths for REST client
    private static final String LUXMS_LOGIN_PATH    = "/auth/login";
    private static final String LUXMS_DATASETS_PATH = "/db/adm.datasets";
    // useful constants
    private static final String AUTH_HEADER_NAME    = "Set-Cookie";

    // identity info
    private final String path;
    private final String user;
    private final String password;
    // session identity
    private String identity;

    /** Create instance of LuxMS RESt client, init identity info from environment. */
    public LuxMSRestClient() {
        LOGGER.debug("LuxMSRestClient constructor() is working.");
        Environment env = Environment.getInstance();
        this.path = env.getLuxMSURL();
        this.user = env.getLuxMSUser();
        this.password = env.getLuxMSPassword();
    }

    /**
     * Login to LuxMS BI server and set {@link LuxMSRestClient#identity}.
     * @throws RestException on request execution error
     */
    public void login() {
        LOGGER.debug("LuxMSRestClient.login() is working.");

        String authEntity = String.format("username=%s&password=%s", this.user, this.password);
        RestResponse response = this.executePost(LUXMS_LOGIN_PATH, authEntity,
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, null, null);

        System.out.println("---> " + response);
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
