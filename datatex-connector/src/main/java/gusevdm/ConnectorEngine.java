package gusevdm;

import gusevdm.datatexdb.DataTexDBClient;
import gusevdm.luxms.LuxMSClient;
import gusevdm.luxms.model.LuxDataSet;
import gusevdm.luxms.LuxMSRestClient;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import static gusevdm.helpers.CommandLineOption.*;
import static gusevdm.luxms.model.LuxDataType.*;

/** Engine class for DataTex Connector Utility. */
public class ConnectorEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorEngine.class);

    private final OptionSet       options;
    private final LuxMSRestClient luxRest;
    private final LuxMSClient     luxmsClient;
    private final DataTexDBClient datatexClient;

    /***/
    public ConnectorEngine(OptionSet options) {
        LOGGER.debug("ConnectorEngine constructor() is working.");
        this.options       = options;
        this.luxRest       = new LuxMSRestClient();
        this.luxmsClient   = new LuxMSClient();
        this.datatexClient = new DataTexDBClient();
    }

    /***/
    @SuppressWarnings("unchecked")
    public void execute() {
        LOGGER.debug("ConnectorEngine.execute() is working.");

        // todo: !!!
        this.luxRest.getDatasetTable(METRICS, 1);
        System.exit(444);
        // todo: !!!

        if (this.options.has(OPTION_LUX_LIST_DATASETS.getName())) { // list datasets in LuxMS instance
            LOGGER.info("Listing datasets in LuxMS BI Server.");
            // list datasets
            List<LuxDataSet> datasets = this.luxRest.listDatasets();
            StringBuilder datasetsList = new StringBuilder();
            datasetsList.append("\n");
            datasets.forEach(dataset -> datasetsList.append(String.format("\t%s%n%n", dataset)));
            LOGGER.info("LuxMS BI Server datasets:\n" + datasetsList.toString());
        }

        if (this.options.has(OPTION_LUX_CREATE_DATASET.getName())) { // create dataset
            LOGGER.info("Creating dataset.");
            // get values from cmd line option
            List<String> values = (List<String>) this.options.valuesOf(OPTION_LUX_CREATE_DATASET.getName());
            // create dataset
            LuxDataSet luxDataSet = this.luxRest.createDataset(values.get(0), values.get(1), true);
            LOGGER.info(String.format("Created dataset:\n\t[%s]", luxDataSet));
        }

        if (this.options.has(OPTION_LUX_DELETE_DATASET.getName())) { // remove dataset by ID
            long datasetId = (long) this.options.valueOf(OPTION_LUX_DELETE_DATASET.getName());
            LOGGER.info(String.format("Removing dataset ID = [%s] on LuxMS BI Server.", datasetId));
            long idRemoved = this.luxRest.removeDataset(datasetId);
            LOGGER.debug(String.format("Removed dataset ID = [%s], requested ID = [%s].", idRemoved, datasetId));
        }

        if (this.options.has(OPTION_DTEX_LIST_TABLES.getName())) { // list all tables in given schema in DataTex DB
            LOGGER.debug("Listing all tables in DataTex DB in a given schema.");
            try {
                LOGGER.info(this.datatexClient.getTablesList());
            } catch (SQLException e) {
                LOGGER.error("Can't get list of tables from DataTex DB!", e);
            }

        }

    } // end of execute() method

}
