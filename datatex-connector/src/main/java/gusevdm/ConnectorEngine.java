package gusevdm;

import gusevdm.config.Environment;
import gusevdm.datatexdb.DataTexDBClient;
import gusevdm.luxms.LuxClient;
import gusevdm.luxms.model.LuxDataSet;
import gusevdm.luxms.LuxRestClient;
import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModel;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static gusevdm.helpers.CommandLineOption.*;
import static gusevdm.luxms.LuxDefaults.REPORT_XMS_FILE;

/** Engine class for DataTex Connector Utility. */
// todo: move usage of LuxMS Rest client to LuxMS Engine
public class ConnectorEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorEngine.class);

    private final OptionSet       options;
    private final Environment     environment;
    private final LuxRestClient   luxRest;
    private final LuxClient       luxmsClient;
    private final DataTexDBClient datatexClient;

    /***/
    public ConnectorEngine(OptionSet options) {
        LOGGER.debug("ConnectorEngine constructor() is working.");
        this.options       = options;
        this.environment   = Environment.getInstance();
        this.luxRest       = new LuxRestClient();
        this.luxmsClient   = new LuxClient();
        this.datatexClient = new DataTexDBClient();
    }

    /***/
    @SuppressWarnings("unchecked")
    public void execute() throws IOException, SQLException, ParseException, ParserConfigurationException, SAXException {
        LOGGER.debug("ConnectorEngine.execute() is working.");

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

        if (this.options.has(OPTION_LUX_SHOW_TABLE.getName())) { // show one table from specified dataset
            LOGGER.debug("Showing table from LuxMS BI Server dataset.");
            // get values from cmd line option
            List<String> values = (List<String>) this.options.valuesOf(OPTION_LUX_SHOW_TABLE.getName());
            // parse values
            Long datasetId = Long.parseLong(values.get(0));
            LuxDataType dataType = LuxDataType.getTypeByName(values.get(1));
            // get table from LuxMS dataset
            String datasetTable = this.luxRest.getDatasetTable(dataType, datasetId);

            LOGGER.info(String.format("Content of table [%s] from dataset [%s]:\n%s",
                    dataType, datasetId, datasetTable));
        }

        if (this.options.has(OPTION_LUX_IMPORT_DATASET.getName())) { // import dataset from CSV
            String datasetName = String.valueOf(this.options.valueOf(OPTION_LUX_IMPORT_DATASET.getName()));
            LOGGER.debug(String.format("Importing dataset [%s] from CSV files.", datasetName));

            this.luxmsClient.loadFromCSV(datasetName);
        }

        if (this.options.has(OPTION_LOAD_DATA_TO_BI.getName())) { // load data from DataTex to LuxMS BI
            LOGGER.debug("Loading data from DataTex DB into LuxMS BI system.");

            List<String> reports = this.environment.getReportsList();
            String reportXmlFile;
            LuxModel luxModel;
            for (String report : reports) {
                reportXmlFile = this.environment.getReportsDir() + "/" + report + "/" + REPORT_XMS_FILE;
                // load LuxMS model from XML for current report
                luxModel = this.luxmsClient.loadModelFromXml(reportXmlFile);
                // populate model with data from DataTex DBMS
                luxModel = this.datatexClient.loadLuxModelData(luxModel);
                // load LuxMS model with data into LuxMS BI system
                this.luxmsClient.loadFromModel(luxModel);
            }

        }
    } // end of execute() method

}
