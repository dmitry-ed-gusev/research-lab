package gusevdm.rest;

import gusevdm.ConnectorDefaults;
import org.json.simple.JSONObject;

import static gusevdm.ConnectorDefaults.JSON_FIELD_DATAPATH;

/**
 * Helper class for validation in {@link RiverRestClient}
 */

class RiverRestValidator {

    String getAndCheckDataPath(String dataset, JSONObject responseBody) {
        JSONObject metadata = (JSONObject) responseBody.get(ConnectorDefaults.JSON_FIELD_METADATA);
        if (metadata == null) {
            throw new IllegalStateException(String.format("Got null metadata during creating dataset [%s]!", dataset));
        }
        String createdDatasetPath = (String) metadata.get(JSON_FIELD_DATAPATH);
        if (!dataset.equals(createdDatasetPath)) {
            throw new IllegalStateException(
                    String.format("Dataset creating error: expected dataset path [%s], actual dataset path [%s].",
                            dataset, createdDatasetPath));
        }
        return createdDatasetPath;
    }

    String getAndCheckDatasetName(String dataset, JSONObject responseBody) {
        String createdDatasetName = (String) responseBody.get(RiverRestClient.JSON_FIELD_NAME);
        if (!dataset.equals(createdDatasetName)) {
            throw new IllegalStateException(
                    String.format("Dataset creating error: requested dataset name: [%s], created dataset name [%s]!",
                            dataset, createdDatasetName));
        }
        return createdDatasetName;
    }

    void checkCollectionID(String collectionID, JSONObject responseBody) {
        String collectionIdWithDatasetCreated = (String) responseBody.get(RiverRestClient.JSON_FIELD_COLLECTION_ID);
        if (!collectionID.equals(collectionIdWithDatasetCreated)) {
            throw new IllegalStateException(
                    String.format("Dataset creating error: expected dataset collection id [%s], actual collection id [%s].",
                            collectionID, collectionIdWithDatasetCreated));
        }
    }

    void checkIDsValidity(String collectionId, String datasetId, String readCollectionId, String readDatasetId) {
        if (!collectionId.equals(readCollectionId)) {
            throw new IllegalStateException(
                    String.format("Read dataset state error: expected collection id [%s], actual collection id [%s]!",
                            collectionId, readCollectionId));
        }

        if (!datasetId.equals(readDatasetId)) {
            throw new IllegalStateException(
                    String.format("Read dataset state error: expected dataset id [%s], actual dataset id [%s]!",
                            datasetId, readDatasetId));
        }
    }


    void checkCollectionName(String collectionName, String createdCollectionName) {
        if (!collectionName.equals(createdCollectionName)) {
            throw new IllegalStateException(
                    String.format("Collection creating error: requested name: [%s], created name [%s]!",
                            collectionName, createdCollectionName));
        }
    }
}
