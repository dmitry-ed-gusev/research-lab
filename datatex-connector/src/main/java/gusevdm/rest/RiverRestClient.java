package gusevdm.rest;

import com.sun.jersey.api.client.WebResource;
import gusevdm.CSV2AbstractDefaults;
import gusevdm.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_DATAPATH;

/**
 * River REST client implementation.
 */

public class RiverRestClient extends RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiverRestClient.class);

    private static final String FORMAT_CSV = "csv";
    // Headers and their values
    private static final String HEADER_AUTHORIZATION         = "Authorization";
    private static final String HEADER_PATTERN_AUTHORIZATION = "Bearer %s";
    // HTTP response/request JSON fields
    static final String JSON_FIELD_NAME          = "name";
    static final String JSON_FIELD_COLLECTION_ID = "collection_id";
    private static final String JSON_FIELD_ID            = "id";
    private static final String JSON_FIELD_STATE         = "state";
    private static final String JSON_FIELD_SOURCE        = "source";
    private static final String JSON_FIELD_SCHEMA        = "schema";
    private static final String JSON_FIELD_URL           = "url";
    private static final String JSON_FIELD_FORMAT        = "format";
    private static final String JSON_FIELD_HEADER        = "header";
    // WebHDFS operations
    private static final String OPERATION_OPEN = "?op=OPEN";
    // Resource path URL patterns
    private static final String PATH_PATTERN_CREATE_DATASET = "%s/datasets/";
    private static final String PATH_PATTERN_DATASET_STATE  = "%s/datasets/%s";

    private final Sleeper sleeper;
    private final String  csvFile;
    private final String  schemaFile;
    private final String  hdfsURI;
    private final String  path;
    private final String  apiKey;
    private final long    timeoutMilliseconds;
    private final int     timeoutAttempts;
    private final RiverRestValidator validator = new RiverRestValidator();

    public RiverRestClient(String csvFile, String schemaFile) {
        this(csvFile, schemaFile, Environment.getInstance(), Thread::sleep);
    }

    RiverRestClient(String csvFile, String schemaFile, Environment environment, Sleeper sleeper) {
        LOGGER.debug("RiverRestClient constructor() is working.");

        this.csvFile    = csvFile;
        this.schemaFile = schemaFile;
        this.sleeper    = sleeper;

        this.hdfsURI             = environment.getKnoxHdfsURI();
        this.path                = environment.getRiverUrl() + "/v2/collections/";
        this.apiKey              = environment.getRiverApiKey();
        this.timeoutMilliseconds = environment.getRiverTimeout(TimeUnit.MILLISECONDS);
        this.timeoutAttempts     = environment.getRiverTimeoutAttempts();
    }

    /**
     * Create collection and return the ID.
     *
     * @param collectionName qualified Abstract collection name
     * @return collection ID string
     * @throws RestException on request execution error
     */
    @SuppressWarnings("unchecked") // NOSONAR: working with not generic type
    public String createCollection(String collectionName) {
        LOGGER.debug("RiverRestClient.createCollection() is working.");
        LOGGER.info(String.format("Trying to create collection: [%s]", collectionName));

        JSONObject requestBody = new JSONObject();
        requestBody.put(JSON_FIELD_NAME, collectionName);
        RestResponse response = this.executePost(requestBody);

        String createdCollectionId   = (String) response.getBody().get(JSON_FIELD_ID);
        String createdCollectionName = (String) response.getBody().get(JSON_FIELD_NAME);

        validator.checkCollectionName(collectionName, createdCollectionName);

        LOGGER.info(String.format("Created collection: name [%s], id [%s].", createdCollectionName, createdCollectionId));

        return createdCollectionId;
    }

    /**
     * Create dataset and return the ID.
     *
     * @param collectionID Abstract collection ID
     * @param dataset qualified Abstract dataset name
     * @return dataset ID string
     * @throws RestException on request execution error
     */
    public String createDataset(String collectionID, String dataset) {
        LOGGER.debug("RiverRestClient.createDataset() is working.");
        LOGGER.info(String.format("Trying to create dataset [%s] in collection [%s].", dataset, collectionID));

        try {
            JSONObject   body     = this.buildCreateDatasetBody(dataset);
            RestResponse response = this.executePost(String.format(PATH_PATTERN_CREATE_DATASET, collectionID), body);

            JSONObject responseBody   = response.getBody();
            LOGGER.debug(String.format("Got response JSON: %s.", responseBody));

            String createdDatasetName = validator.getAndCheckDatasetName(dataset, responseBody);
            validator.checkCollectionID(collectionID, responseBody);
            String createdDatasetPath = validator.getAndCheckDataPath(dataset, responseBody);

            String createdDatasetId   = (String) responseBody.get(JSON_FIELD_ID);
            LOGGER.info(String.format("Created dataset: name [%s], id [%s], path [%s] in collection [%s].",
                    createdDatasetName, createdDatasetId, createdDatasetPath, collectionID));

            return createdDatasetId;

        } catch (IOException | ParseException e) {
            throw new RestException("Cannot construct request body", e);
        }
    }

    /**
     * Wait for a dataset to receive particular state.
     *
     * @param collectionID collection ID
     * @param datasetID dataset ID
     * @param states destination states
     * @throws RestException if {@code failed} state is returned, overall timeout is reached or HTTP request failed
     * @throws InterruptedException if the thread was interrupted
     */
    public void waitForState(String collectionID, String datasetID, String... states) throws InterruptedException {
        LOGGER.debug(
                String.format("RiverRestClient.waitForState() is working. Collection id: [%s], dataset id: [%s], states list: %s.",
                        collectionID, datasetID, Arrays.toString(states)));

        int attemptsLeft = this.timeoutAttempts;

        do {
            String currentState = this.readDatasetState(collectionID, datasetID);
            String message = String.format("Waiting for indexing: collection [%s], dataset [%s]. Current state: [%s].",
                    collectionID, datasetID, currentState);

            LOGGER.info(message);
            LOGGER.info(String.format("Attempts left: %s", attemptsLeft));

            if (Arrays.stream(states).anyMatch(currentState::equals)) { return; }
            sleeper.sleep(timeoutMilliseconds);

            attemptsLeft--;

        } while (attemptsLeft > 0);

        // Method did not return from the timeout loop, fail.
        throw new TimeoutException(String.format("Timeout reached while waiting for one of dataset state %s. %d attempts with %d delay were executed",
                Arrays.toString(states), this.timeoutAttempts, timeoutMilliseconds));
    }

    protected InputStream openSchema() throws IOException {
        LOGGER.debug(String.format("RiverRestClient.openSchema() is working. Schema path: [%s].", this.schemaFile));
        return new FileInputStream(this.schemaFile);
    }

    //@Override
    protected WebResource.Builder buildClient(String resource, MediaType mediaType, Cookie cookie) {
        LOGGER.debug("RiverRestClient.buildClient() working.");
        WebResource.Builder builder = super.buildClient(resource, mediaType, cookie, null);
        builder.header(HEADER_AUTHORIZATION, String.format(HEADER_PATTERN_AUTHORIZATION, apiKey));
        return builder;
    }

    @Override
    protected String getPath() {
        return this.path;
    }

    /**
     * Build JSON body of create dataset request.
     *
     * @param dataset qualified Abstract dataset name
     * @return the request body as {@link JSONObject}
     * @throws IOException    if JSON schema cannot be read from HDFS
     * @throws ParseException if JSON schema parsing failed
     */

    // NOSONAR: working with not generic type
    @SuppressWarnings("unchecked")
    private JSONObject buildCreateDatasetBody(String dataset) throws IOException, ParseException {
        LOGGER.debug(String.format("RiverRestClient.buildCreateDatasetBody() is working. Dataset name [%s].", dataset));

        // create metadata
        JSONObject metadata = new JSONObject();
        metadata.put(JSON_FIELD_DATAPATH, dataset);

        // create schema
        JSONArray schema;
        try (Reader reader = new InputStreamReader(this.openSchema(), StandardCharsets.UTF_8)) {
            schema = this.parseJsonArray(reader);
        }

        // create source
        JSONObject source = new JSONObject();
        source.put(JSON_FIELD_FORMAT, FORMAT_CSV);
        source.put(JSON_FIELD_HEADER, false);
        source.put(JSON_FIELD_URL,    this.buildCSVUrl());

        // create full body for "create dataset query"
        JSONObject body = new JSONObject();
        body.put(JSON_FIELD_NAME, dataset);
        body.put(CSV2AbstractDefaults.JSON_FIELD_METADATA, metadata);
        body.put(JSON_FIELD_SCHEMA, schema);
        body.put(JSON_FIELD_SOURCE, source);

        LOGGER.debug(String.format("JSON for 'create dataset body': %s", body));

        return body;
    }

    private String buildCSVUrl() {
        LOGGER.debug("RiverRestClient.buildCSVUrl() working.");
        String builtUrl = URI.create(hdfsURI + "/" + csvFile + OPERATION_OPEN).normalize().toString();
        LOGGER.debug(String.format("Built CSV url: [%s].", builtUrl));
        return builtUrl;
    }

    private String readDatasetState(String collectionId, String datasetId) {
        LOGGER.debug(
                String.format("RiverRestClient.readDatasetState() is working. Reading dataset [%s] in collection [%s].",
                        datasetId, collectionId));

        RestResponse response = this.executeGet(String.format(PATH_PATTERN_DATASET_STATE, collectionId, datasetId));
        JSONObject body = response.getBody();
        String readCollectionId = (String) body.get(JSON_FIELD_COLLECTION_ID);
        String readDatasetId    = (String) body.get(JSON_FIELD_ID);

        validator.checkIDsValidity(collectionId, datasetId, readCollectionId, readDatasetId);

        String readDatasetName  = (String) body.get(JSON_FIELD_NAME);
        String readDatasetState = (String) body.get(JSON_FIELD_STATE);
        LOGGER.debug(String.format("Read state for dataset (id -> [%s], name -> [%s]) in collection [%s] is [%s].",
                readDatasetId, readDatasetName, readCollectionId, readDatasetState));

        return readDatasetState;
    }

    @FunctionalInterface
    interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }
}
