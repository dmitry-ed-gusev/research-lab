package gusevdm.luxms;

import gusev.dmitry.jtils.utils.JIOUtils;
import gusevdm.Environment;
import gusevdm.luxms.model.LuxDataSet;
import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelFactory;
import gusevdm.luxms.model.LuxModelInterface;
import gusevdm.luxms.model.elements.LuxUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gusevdm.ConnectorDefaults.DEFAULT_ENCODING;

/***/
// todo: make this class a singleton???
public class LuxMSClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSClient.class);

    // some useful defaults
    private static final String DATASET_ID_FILE = "datasetid";

    // internal state
    private final String importDir;
    private final LuxMSRestClient luxMSRestClient;

    /***/
    public LuxMSClient() {
        LOGGER.debug("LuxMSClient constructor() is working.");
        Environment environment = Environment.getInstance();
        this.importDir       = environment.getCsvImportDir();
        this.luxMSRestClient = new LuxMSRestClient();
    }

    /** Load one element of LuxMS data model from specified CSV file. */
    private static List<LuxModelInterface> loadElementFromCSV(String filePath, LuxDataType elementType) throws IOException {
        LOGGER.debug("LuxMSClient.loadElementFromCSV() is working.");

        // result
        List<LuxModelInterface> elements = new ArrayList<>();

        // build CSV format (with specified file header)
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withHeader(elementType.getCsvFileHeader())
                .withSkipHeaderRecord()
                .withIgnoreSurroundingSpaces()
                .withTrim()
                .withNullString(""); // <- allows for missing values - converts to nulls

        // create CSV file reader (and red the file)
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), DEFAULT_ENCODING))) {

            //fileReader.readLine(); // read the first line - ignore header line

            // CSV parser instance
            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOGGER.info(String.format("Got records from CSV [%s]. Records count [%s].", filePath, csvRecords.size()));

            // iterate over records, create instances and fill in resulting array
            csvRecords.forEach(record -> elements.add(LuxModelFactory.getInstance(elementType, record)));
        }

        return elements;
    }

    /** Load dataset id from CSV representation of dataset. If there is no id - return [-1]. */
    private static long getDatasetIdForCSV(String datasetPath) throws IOException {
        LOGGER.debug("LuxMSClient.checkDatasetIdForCSV() is working.");

        String idFilePath = datasetPath + DATASET_ID_FILE;
        File idFile = new File(idFilePath);
        if (idFile.exists() && idFile.isFile()) { // file exists - read id from it
            return JIOUtils.readLongFromFile(idFilePath);
        }

        return -1; // no dataset id file found
    }

    /***/
    public void loadFromCSV(String datasetName) throws IOException {
        LOGGER.debug("LuxMSClient.loadFromCSV() is working.");

        // check directory with dataset (csv import dir)
        String datasetPath = this.importDir + "/" + datasetName + "/";
        File file = new File(datasetPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalStateException(
                    String.format("Dataset directory [%s] doesn't exist or isn't a directory!", datasetName));
        }

        // check for dataset id file - if present, update existing, if not - create new one
        long datasetId = LuxMSClient.getDatasetIdForCSV(datasetPath);
        LOGGER.debug(String.format("Loaded dataset ID = [%s].", datasetId));

        // todo: probably - bug in REST protocol for LuxMS RESP API. Created (by REST) dataset isn't usable!
        // method of putting entries to LuxMS - update or insert
        //boolean updateEntries = true; // update (HTTP POST), not insert (HTTP PUT)
        // create dataset and write dataset id file (if no dataset exists)
        if (datasetId <= 0) {
            LOGGER.warn("Dataset in LuxMS system doesnt exist! Creating.");
            throw new IllegalStateException("Dataset is unknown (can't find [datasetid] file)!");
            //LuxDataSet dataset = this.luxMSRestClient.
            //        createDataset(datasetName, "Automatically created dataset.", true);
            //datasetId = dataset.getId();
            // write dataset id file
            //JIOUtils.writeLongToFile(datasetId, datasetPath + DATASET_ID_FILE,false);
            // switch for method of interaction with LuxMS system
            //updateEntries = false;
        }

        // iterate over all dataset elements type and load them one-by-one
        for (LuxDataType luxDataType : LuxDataType.values()) {

            // generate path
            String dataTypePath = datasetPath + luxDataType.getCsvFileName();
            File typeFile = new File(dataTypePath);

            if (typeFile.exists() && typeFile.isFile()) { // process only if file exists

                // load LuxMS model's element
                List<LuxModelInterface> elements = LuxMSClient.loadElementFromCSV(dataTypePath, luxDataType);
                System.out.println("\n\t" + elements + "\n");

                for (LuxModelInterface element : elements) {

                    this.luxMSRestClient.addTableEntry(datasetId, element, true);

                    // todo: see mentioned bug above (REST protocol for LuxMS BI)
                    // add/update entry in LuxMS system
                    //if (updateEntries) { // HTTP PUT -> update
                        // todo: implementation
                    //} else { // HTTP POST -> insert
                        //this.luxMSRestClient.addTableEntry(datasetId, element);
                    //}
                } // end of FOR

            } else { // file doesn't exist - warn to log and skip it
                LOGGER.warn(String.format("Type file [%s] doesn't exist or not a file! Skipped.", typeFile));
            }

        } // end of FOR

    }
}
