package gusevdm;

import gusevdm.rest.MetabaseRestClient;
import gusevdm.rest.RiverRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gusevdm.CSV2AbstractDefaults.STATE_AWAITING_METADATA;
import static gusevdm.CSV2AbstractDefaults.STATE_INDEXED;
import static gusevdm.CSV2AbstractDefaults.STATE_INDEXING;

/**
 * Export CSV files from HDFS folder to Abstract software.
 */

class CSV2Abstract {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSV2Abstract.class);

    private final String             dataset;
    private final boolean            reindex;
    private final MetabaseRestClient metabaseRestClient;
    private final RiverRestClient riverRestClient;

    CSV2Abstract(String dataset, String csvFile, String schemaFile, boolean reindex) {
        this(dataset, reindex, new MetabaseRestClient(), new RiverRestClient(csvFile, schemaFile));
    }

    CSV2Abstract(String dataset, boolean reindex, MetabaseRestClient metabaseRestClient, RiverRestClient riverRestClient) {
        LOGGER.debug("CSV2Abstract constructor() is working.");
        this.dataset = dataset;
        this.reindex = reindex;
        this.metabaseRestClient = metabaseRestClient;
        this.riverRestClient    = riverRestClient;
    }

    /**
     * Export CSV to Abstract.
     *
     * @throws CSV2AbstractException on any error during the process
     */
    void run() {
        LOGGER.info("Starting export to Abstract.");
        try {
            String collectionName = this.getCollectionByDataset();

            LOGGER.info(String.format("Create/get collection [%s]", collectionName));
            String collectionID   = this.riverRestClient.createCollection(collectionName);
            LOGGER.info(String.format("Create/get dataset [%s] in collection [%s]", dataset, collectionName));
            String datasetID      = this.riverRestClient.createDataset(collectionID, dataset);

            LOGGER.info(String.format("Waiting for '%s' state. Collection id: [%s], dataset id [%s]",
                    STATE_INDEXED, collectionID, datasetID));
            this.riverRestClient.waitForState(collectionID, datasetID, STATE_AWAITING_METADATA, STATE_INDEXING, STATE_INDEXED);

            this.metabaseRestClient.login();
            boolean forcePublish = reindex || !this.metabaseRestClient.isAccessibleAndPublished(dataset);
            if (forcePublish) {
                LOGGER.info("Force republishing of the dataset as it's not published or schema has changed");
            }
            LOGGER.info(String.format("Publishing collection [%s]", collectionName));
            this.metabaseRestClient.publishCollection(collectionName, forcePublish);
            LOGGER.info(String.format("Publishing dataset [%s]", dataset));
            this.metabaseRestClient.publishDataset(dataset, forcePublish);

            LOGGER.info(String.format("Waiting for '%s' state. Collection id: [%s], dataset id [%s]",
                    STATE_INDEXED, collectionID, datasetID));
            this.riverRestClient.waitForState(collectionID, datasetID, STATE_INDEXED);
            LOGGER.info("Completed export to Abstract");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CSV2AbstractException("waitForState was interrupted", e);
        }
    }

    private String getCollectionByDataset() {
        LOGGER.debug("CSV2Abstract.getCollectionByDataset() is working.");

        int lastIndexOfPeriod = this.dataset.lastIndexOf('.');

        if (lastIndexOfPeriod == -1) {
            throw new IllegalArgumentException(String.format("Qualified Abstract dataset name expected, got: [%s]!", dataset));
        }

        String collectionName = dataset.substring(0, lastIndexOfPeriod);
        LOGGER.debug(String.format("Calculated collection name: [%s].", collectionName));

        return collectionName;
    }
}
