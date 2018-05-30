package gusevdm.luxms;

import com.sun.jersey.api.client.ClientResponse;
import gusevdm.Environment;
import gusevdm.rest.RestClient;
import gusevdm.rest.RestException;
import gusevdm.rest.RestResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class LuxMSRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSRestClient.class);

    // paths for REST client
    private static final String LUXMS_LOGIN_PATH     = "/auth/login";
    private static final String LUXMS_DATASETS_PATH  = "/db/adm.datasets";
    // request/response headers and cookies
    private static final String LUXMS_AUTH_HEADER    = "Set-Cookie";
    private static final String LUXMS_SESSION_HEADER = "LuxmsBI-User-Session";
    //private static final String LUXMS_COOKIE_HEADER  = "Cookie";

    // identity info
    private final String path;
    private final String user;
    private final String password;
    // session identity
    private String identity = null;
    private MultivaluedMap<String, String> authHeader = null;

    /** Create instance of LuxMS RESt client, init identity info from environment. */
    public LuxMSRestClient() {
        LOGGER.debug("LuxMSRestClient constructor() is working.");
        Environment env = Environment.getInstance();
        this.path = env.getLuxMSURL();
        this.user = env.getLuxMSUser();
        this.password = env.getLuxMSPassword();
    }

    /**
     * Login to LuxMS BI server and set {@link LuxMSRestClient#authHeader}.
     * @throws RestException on request execution error
     */
    // todo: take a look at [social networks] module - save api key and reuse it
    public void login() {
        LOGGER.debug("LuxMSRestClient.login() is working.");

        // prepare data and execute request
        String authEntity = String.format("username=%s&password=%s", this.user, this.password);
        RestResponse response = this.executePost(LUXMS_LOGIN_PATH, authEntity,
                MediaType.APPLICATION_FORM_URLENCODED_TYPE, null, null);

        // get auth info from response
        response.getHeaders().forEach((key, values) -> {  // BiConsumer
            LOGGER.debug(String.format("Header: %s = %s", key, values));
            if (LUXMS_AUTH_HEADER.equals(key)) {  // found auth header
                values.forEach(value -> {  // Consumer
                    if (value.contains(LUXMS_SESSION_HEADER)) { // value contains session header
                        LOGGER.debug(String.format("*Found session header [%s].", LUXMS_SESSION_HEADER));
                        // get value of auth identity value
                        this.identity = value.substring(value.indexOf(LUXMS_SESSION_HEADER) + LUXMS_SESSION_HEADER.length() + 1,
                                value.indexOf(";"));
                        LOGGER.debug(String.format("*Found session auth identity [%s].", this.identity));
                    }
                }); // values -> forEach
            } // if found auth header
        }); // map entries -> forEach

        if (this.identity == null) { // if we can't find identity - we can't work with LuxMS BI server
            throw new IllegalStateException("Can't find session identity value!");
        } else {
            LOGGER.info("Authorization successful. ");
            this.authHeader = new MultivaluedHashMap<String, String>() {{
               add(LUXMS_SESSION_HEADER, LuxMSRestClient.this.identity);
            }};
        }
    }

    /** Lists system datasets (according to provileges). */
    public List<DataSet> listDatasets() {
        LOGGER.debug("LuxMSRestClient.listDatasets() is working.");
        // execute request
        RestResponse response = this.executeGet(LUXMS_DATASETS_PATH, null, this.authHeader);
        // parse response
        List<DataSet> result = new ArrayList<>();
        response.getBodyArray().forEach(json -> {
            DataSet dataSet = LuxMSHelper.parseDataSet((JSONObject) json);
            result.add(dataSet);
        });
        return result;
    }

    /** Create new dataset. DataSet object with created dataset is returned (in case of success). */
    @SuppressWarnings("unchecked")
    public DataSet createDataset(String datasetTitle, String datasetDesc, boolean isVisible) {
        LOGGER.debug("LuxMSRestClient.createDataset() is working.");

        // create JSON for request
        JSONObject body = new JSONObject();
        body.put("title", datasetTitle);
        body.put("description", datasetDesc);
        body.put("is_visible", isVisible ? 1 : 0);
        // execute request
        RestResponse response = this.executePost(LUXMS_DATASETS_PATH, body, null, this.authHeader);
        // parse response and return object
        return LuxMSHelper.parseDataSet(response.getBodyObject());
    }

    /** Remove dataset by id, return id of removed dataset. */
    // todo: catch error if dataset does't exit?
    // todo: check if dataset exists before deletion?
    public long removeDataset(long id) {
        LOGGER.debug("LuxMSRestClient.removeDataset() is working.");
        // execute request
        RestResponse response = this.executeDelete(LUXMS_DATASETS_PATH + "/" + String.valueOf(id),
                null, null, this.authHeader);
        // get id of deleted dataset
        return (long) response.getBodyArray().get(0);
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
