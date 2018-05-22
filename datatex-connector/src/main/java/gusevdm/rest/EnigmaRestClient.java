package gusevdm.rest;

import gusevdm.Environment;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Optional;

import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_METADATA;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_RESULT;
import static gusevdm.CSV2AbstractDefaults.JSON_FIELD_VALUE;

/**
 * REST client for Enigma Abstract API.
 */

public class EnigmaRestClient extends RestClient {

    private static final Logger LOGGER = Logger.getLogger(EnigmaRestClient.class);

    private static final String ERROR_MESSAGE_PATTERN     = "Cannot extract Total Rows from response body: %s";
    private static final int    METADATA_TOTAL_ROWS_INDEX = 0;

    private static final String PATH_PATTERN_META     = "%s/v2/meta/%s";
    private static final String PATH_PATTERN_RESOURCE = "/%s";
    private final String path;

    public EnigmaRestClient() {
        this(Environment.getInstance());
    }

    EnigmaRestClient(Environment environment) {
        LOGGER.debug("EnigmaRestClient constructor() is working.");
        this.path = String.format(PATH_PATTERN_META, environment.getAbstractUrl(), environment.getAbstractApiKey());
    }

    /**
     * Get record count in particular dataset.
     *
     * @param datapath dataset name in dot separated notation
     * @return the number of records
     * @throws RestException on HTTP error, JSON parse exception, or missing metadata
     */
    long getRecordCount(String datapath) {
        LOGGER.debug(String.format("EnigmaRestClient.getRecordCount() is working. Data path: [%s].", datapath));

        String resource = String.format(PATH_PATTERN_RESOURCE, datapath);
        RestResponse response = executeGet(resource);

        JSONObject body = response.getBodyObject();
        try {
            String totalRowsString = Optional.ofNullable((JSONObject) body.get(JSON_FIELD_RESULT))
                    .map(result -> (JSONArray) result.get(JSON_FIELD_METADATA))
                    .filter(metadata -> !metadata.isEmpty())
                    .map(metadata -> (JSONObject) metadata.get(METADATA_TOTAL_ROWS_INDEX))
                    .map(metadataEntry -> (String) metadataEntry.get(JSON_FIELD_VALUE))
                    .orElseThrow(() -> new RestException(String.format(ERROR_MESSAGE_PATTERN, body.toJSONString())));
            return Long.parseLong(totalRowsString);
        } catch (NumberFormatException | ClassCastException e) {
            throw new RestException(String.format(ERROR_MESSAGE_PATTERN, body.toJSONString()), e);
        }
    }

    @Override
    protected String getPath() {
        return path;
    }
}
