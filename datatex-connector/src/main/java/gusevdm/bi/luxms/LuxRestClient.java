package gusevdm.bi.luxms;

import gusevdm.config.Environment;
import gusevdm.bi.luxms.model.LuxDataSet;
import gusevdm.bi.luxms.model.LuxDataType;
import gusevdm.bi.luxms.model.LuxModelInterface;
import gusevdm.rest.RestClient;
import gusevdm.rest.RestException;
import gusevdm.rest.RestResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

/** LuxMS BI Server REST client. */
public class LuxRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxRestClient.class);

    // paths for REST client
    private static final String LUXMS_LOGIN_PATH     = "/auth/login";
    private static final String LUXMS_DATASETS_PATH  = "/db/adm.datasets";
    private static final String LUXMS_API_ENTRY      = "/db";
    // request/response headers and cookies
    private static final String LUXMS_AUTH_HEADER    = "Set-Cookie";
    private static final String LUXMS_SESSION_HEADER = "LuxmsBI-User-Session";
    //private static final String LUXMS_COOKIE_HEADER  = "Cookie";

    // identity info for LuxMS server
    private final String path;
    private final String user;
    private final String password;
    // current session identity
    private String                         identity   = null;
    private MultivaluedMap<String, String> authHeader = null;

    /** Create instance of LuxMS RESt client, init identity info from environment. */
    public LuxRestClient() {
        LOGGER.debug("LuxRestClient constructor() is working.");
        Environment env = Environment.getInstance();
        this.path = env.getLuxMSURL();
        this.user = env.getLuxMSUser();
        this.password = env.getLuxMSPassword();
    }

    /**
     * Login to LuxMS BI server and set {@link LuxRestClient#authHeader}.
     * @throws RestException on request execution error
     */
    // todo: take a look at [social networks] module - save api key and reuse it
    // todo: review logging for this method
    private void login() {
        LOGGER.debug("LuxRestClient.login() is working.");

        if (this.identity != null && this.authHeader != null) { // check - are we already logged in?
            LOGGER.debug("Already logged in!");
            return;
        }

        LOGGER.debug("Not logged in -> logging in...");
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
               add(LUXMS_SESSION_HEADER, LuxRestClient.this.identity);
            }};
        }
    }

    /** Lists system datasets (according to provileges). */
    @SuppressWarnings("unchecked")
    public List<LuxDataSet> listDatasets() {
        LOGGER.debug("LuxRestClient.listDatasets() is working.");

        this.login();

        // execute request
        RestResponse response = this.executeGet(LUXMS_DATASETS_PATH, null, this.authHeader);
        // parse response
        List<LuxDataSet> result = new ArrayList<>();
        response.getBodyArray().forEach(json -> {
            LuxDataSet luxDataSet = new LuxDataSet((JSONObject) json);
            result.add(luxDataSet);
        });
        return result;
    }

    /** Create new dataset. LuxDataSet object with created dataset is returned (in case of success). */
    @SuppressWarnings("unchecked")
    public LuxDataSet createDataset(String datasetTitle, String datasetDesc, boolean isVisible) {
        LOGGER.debug("LuxRestClient.createDataset() is working.");

        this.login();

        // create JSON for request
        JSONObject body = new JSONObject();
        body.put("title",       datasetTitle);
        body.put("description", datasetDesc);
        body.put("is_visible",  isVisible ? 1 : 0);
        body.put("is_db_ready", 1);
        body.put("owner_user_id", 3);
        // execute request
        RestResponse response = this.executePost(LUXMS_DATASETS_PATH, body, null, this.authHeader);
        // parse response and return object
        return new LuxDataSet(response.getBodyObject());
    }

    /** Remove dataset by id, return id of removed dataset. */
    // todo: catch error if dataset doesn't exit?
    // todo: check if dataset exists before deletion?
    public long removeDataset(long id) {
        LOGGER.debug(String.format("LuxRestClient.removeDataset(long) is working. ID = [%s].", id));

        this.login();

        // execute request
        RestResponse response = this.executeDelete(LUXMS_DATASETS_PATH + "/" + String.valueOf(id),
                null, null, this.authHeader);
        // get id of deleted dataset
        return (long) response.getBodyArray().get(0);
    }

    /***/
    public void updateDataset() {
        LOGGER.debug("LuxRestClient.updateDataset() is working.");
        throw new NotImplementedException("Not implemented yet!");
    }

    /***/
    public void hideDataset(){
        LOGGER.debug("LuxRestClient.hideDataset() is working.");
        throw new NotImplementedException("Not implemented yet!");
    }

    /***/
    public void linkDatasetToGroup() {
        LOGGER.debug("LuxRestClient.linkDatasetToGroup() is working.");
        throw new NotImplementedException("Not implemented yet!");
    }

    /***/
    public String getDatasetTable(LuxDataType type, long datasetId) {
        LOGGER.debug("LuxRestClient.getDatasetTable() is working.");

        this.login();

        // execute REST request
        RestResponse response = this.executeGet(LUXMS_API_ENTRY + "/" + String.valueOf(datasetId) +
            "." + type.getTableName(), null, this.authHeader);

        StringBuilder result = new StringBuilder();
        // parsing JSON array and generating result
        JSONArray jsonArray = response.getBodyArray();
        jsonArray.forEach(json -> result.append(json).append("\n"));

        return result.toString();
    }

    /***/
    public void addTableEntry(long datasetId, LuxModelInterface model, boolean updateIfExists) {
        LOGGER.debug("LuxRestClient.addTableEntry() is working.");

        this.login();

        // build path
        String urlPath = LUXMS_API_ENTRY + "/" + datasetId + "." + model.getDataType().getTableName();
        // try to add (insert) item
        try {

            RestResponse response = this.executePost(urlPath, model.getAsJSON(), null, this.authHeader);
            System.out.println("===> " + response);

        } catch (RestException e) { // catch REST exception and process it
            int status = e.getResponse().getStatus();
            if (status == 409 && updateIfExists) { // try to UPDATE
                LOGGER.debug("Got status [409 Conflict]. Trying to update instead of insert.");
                this.updateTableEntry(datasetId, model);
            } else { // if status <> 409 or don't update if exists -> re-throw exception
                throw e;
            }
        }
    }

    /***/
    public void updateTableEntry(long datasetId, LuxModelInterface model) {
        LOGGER.debug("LuxRestClient.updateTableEntry() is working");

        this.login();

        // build path
        String urlPath = LUXMS_API_ENTRY + "/" + datasetId + "." + model.getDataType().getTableName() +
                "/" + model.getStrId();
        // update item via REST

        try {

            RestResponse response = this.executePut(urlPath, model.getAsJSON(), null, this.authHeader);
            System.out.println("===> " + response);

        } catch (RestException e) { // catch REST exception and process it
            int status = e.getResponse().getStatus();
            if (status == 404) { // try to UPDATE
                LOGGER.error("Got status [404 Not found]!");
            } else { // if status <> 404
                throw e;
            }
        }

    }

    /***/
    public void removeTableEntry(long datasetId, LuxModelInterface model) {
        throw new NotImplementedException("Not implemented yet!");
    }

    @Override
    protected String getPath() {
        return this.path;
    }

}
