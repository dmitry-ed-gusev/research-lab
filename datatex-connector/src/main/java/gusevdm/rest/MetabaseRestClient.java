package gusevdm.rest;

import com.sun.jersey.api.client.ClientResponse;
import gusevdm.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import java.util.regex.Pattern;

import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_DATAPATH;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_METADATA;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_RESULT;


/**
 * Metabase REST client.
 */

public class MetabaseRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetabaseRestClient.class);

    private static final String JSON_FIELD_PUBLISHED               = "published";
    private static final String JSON_FIELD_COLUMNS                 = "columns";
    private static final String JSON_FIELD_IS_DISPLAYED_HORIZONTAL = "is_displayed_horizontal";
    private static final String JSON_FIELD_INDEX_TABLE             = "index_table";
    private static final String JSON_FIELD_INDEX_HORIZONTAL        = "index_horizontal";
    private static final String JSON_FIELD_IS_DISPLAYED_TABLE      = "is_displayed_table";
    private static final String JSON_FIELD_SERIALID                = "serialid";
    private static final String JSON_NAME_COLUMN                   = "name";

    private static final String PATH_DATAPATH                      = "/apibase/datapath";
    private static final String PATH_PATTERN_GET_DATAPATH          = "/apibase/datapath?datapath=%s";
    private static final String PATH_PATTERN_GET_CHILDREN_DATAPATH = "/apibase/datapath/children?datapath=%s";
    //private final String path;
    //private final String user;
    //private final String password;

    private Cookie sessionCookie;

    public MetabaseRestClient() {
        this(Environment.getInstance());
    }

    MetabaseRestClient(Environment environment) {
        LOGGER.debug("MetabaseRestClient constructor() is working.");
        //this.path = environment.getMetabaseUrl();
        //this.user = environment.getMetabaseUser();
        //this.password = environment.getMetabasePassword();
    }

    /**
     * Login to Metabase Server and set {@link MetabaseRestClient#sessionCookie}.
     *
     * @throws RestException on request execution error
     */
    public void login() {
        LOGGER.debug("MetabaseRestClient.login() is working.");
        JSONObject body = new JSONObject();
        //body.put("user",       user);
        //body.put("password",   password);
        body.put("rememberMe", "true");
        RestResponse response = this.executePost("/signin", body);
        sessionCookie = response.getCookie();
    }

    /**
     * Check if dataset is accessible and published.
     *
     * @param dataset qualified Abstract dataset name
     * @return {@code true} if dataset status can be queried and it is set to "published"
     */
    public boolean isAccessibleAndPublished(String dataset) {
        LOGGER.debug(
                String.format("MetabaseRestClient.isAccessibleAndPublished() is working. Dataset [%s], sessionCookie [%s].",
                        dataset, sessionCookie));
        try {
            RestResponse response = this.executeGet(String.format(PATH_PATTERN_GET_DATAPATH, dataset), sessionCookie, null);
            JSONObject result = (JSONObject) response.getBodyObject().get(JSON_FIELD_RESULT);

            String datapath     = (String) result.get(JSON_FIELD_DATAPATH);
            if (!dataset.equals(datapath)) {
                throw new IllegalStateException(
                        String.format("Error checking accessibility: expected datapath: [%s], actual: [%s]!",
                                dataset, datapath));
            }

            boolean isPublished = (boolean) result.get(JSON_FIELD_PUBLISHED);
            LOGGER.debug(String.format("Dataset: [%s] publishing state: [%s].", dataset, isPublished));

            return isPublished;

        } catch (RestException e) {
            LOGGER.warn(String.format("Dataset [%s] cannot be found or Metabase error has happened", dataset), e);
            return false;
        }

    }

    /**
     * Explicitly set metadata and indexing and publish collection on Metabase.
     *
     * @param collection qualified Abstract collection name
     * @throws RestException on request execution error
     */
    public void publishCollection(String collection, boolean forcePublish) {
        LOGGER.debug(
                String.format("MetabaseRestClient.publishCollection() is working. Collection [%s], sessionCookie [%s].",
                        collection, sessionCookie));
        String[] parts = collection.split(Pattern.quote("."));
        if (parts.length > 1) {
            StringBuilder datapathBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                datapathBuilder.append(parts[i]);
                String datapath = datapathBuilder.toString();
                publishCollectionIfNeeded(datapath, false);
                datapathBuilder.append(".");
            }
        }
        publishCollectionIfNeeded(collection, forcePublish);
    }

    private void publishCollectionIfNeeded(String datapath, boolean forcePublish) {
        RestResponse response = executeGet(String.format(PATH_PATTERN_GET_DATAPATH, datapath), sessionCookie, null);
        JSONObject result = (JSONObject) response.getBodyObject().get(JSON_FIELD_RESULT);

        boolean isPublished = (boolean) result.getOrDefault(JSON_FIELD_PUBLISHED, false);
        if (!isPublished || forcePublish) {
            result.put(JSON_FIELD_PUBLISHED, true);

            JSONObject body = new JSONObject();
            body.put(JSON_FIELD_DATAPATH, datapath);
            body.put(JSON_FIELD_METADATA, result);

            LOGGER.info(String.format("Publishing collection [%s]...", datapath));
            executePut(PATH_DATAPATH, body, sessionCookie, null);
            LOGGER.info(String.format("Collection [%s] has been published.", datapath));
        } else {
            LOGGER.info(String.format("Collection [%s] is already published and republish isn't requested.", datapath));
        }
    }

    /**
     * Publish dataset on Metabase if it os not published or if {@code forcePublish} is {@code true}
     *
     * @param dataset dataset to publish
     * @param forcePublish whether dataset should be published again if it's already published
     */
    public void publishDataset(String dataset, boolean forcePublish) {
        LOGGER.debug(
                String.format("MetabaseRestClient.publishDataset() is working. Dataset [%s], sessionCookie [%s].",
                        dataset, sessionCookie));

        RestResponse response = executeGet(String.format(PATH_PATTERN_GET_DATAPATH, dataset), sessionCookie, null);
        JSONObject result = (JSONObject) response.getBodyObject().get(JSON_FIELD_RESULT);
        boolean isPublished = (boolean) result.getOrDefault(JSON_FIELD_PUBLISHED, false);

        if (!isPublished || forcePublish) {
            JSONObject metadata = correctTableMetadata(result);

            JSONObject body = new JSONObject();
            body.put(JSON_FIELD_DATAPATH, dataset);
            body.put(JSON_FIELD_METADATA, metadata);

            LOGGER.info(String.format("Publishing dataset [%s]...", dataset));
            executePut(PATH_DATAPATH, body, sessionCookie, null);
            LOGGER.info(String.format("Dataset [%s] has been published.", dataset));
        } else {
            LOGGER.info(String.format("Dataset [%s] is already published and republish isn't requested.", dataset));
        }
    }

    /**
     * For some reason, Metabase does not check all boolean attributes to true.
     * We should handle it by updating the attributes.
     *
     * @param metadata metadata obtained from Metabase response
     * @return Modified JSONObject
     */
    private static JSONObject correctTableMetadata(JSONObject metadata) {
        LOGGER.debug("MetabaseRestClient.correctTableMetadata() is working.");
        LOGGER.debug(String.format("Input JSON:%n%s", metadata));

        metadata.put(JSON_FIELD_PUBLISHED, true);
        JSONArray columns = (JSONArray) metadata.get(JSON_FIELD_COLUMNS);
        for (Object columnObject : columns) {
            JSONObject column = (JSONObject) columnObject;

            if(!JSON_FIELD_SERIALID.equals(column.get(JSON_NAME_COLUMN))) {
                column.put(JSON_FIELD_IS_DISPLAYED_TABLE, true);
                column.put(JSON_FIELD_INDEX_HORIZONTAL, true);
                column.put(JSON_FIELD_INDEX_TABLE, true);
                column.put(JSON_FIELD_IS_DISPLAYED_HORIZONTAL, true);
            }

        }

        LOGGER.debug(String.format("Output JSON:%n%s", metadata));
        return metadata;
    }

    @Override
    protected String getPath() {
        //return path;
        return null;
    }

    @Override
    protected RestResponse buildResponse(JSONObject request, ClientResponse response) {
        RestResponse res = super.buildResponse(request, response);
        if (sessionCookie != null) {
            sessionCookie = res.getCookie();
        }
        return res;
    }

    Cookie getSessionCookie() {
        return sessionCookie;
    }

    void setSessionCookie(Cookie cookie) {
        this.sessionCookie = cookie;
    }
}
