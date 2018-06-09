package gusevdm.luxms;

import gusevdm.Environment;
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

    private final String importDir;

    /***/
    public LuxMSClient() {
        LOGGER.debug("LuxMSClient constructor() is working.");
        Environment environment = Environment.getInstance();
        this.importDir = environment.getCsvImportDir();
    }

    /***/
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

    /***/
    public void loadFromCSV(String datasetName) throws IOException {
        LOGGER.debug("LuxMSHelper.loadFromCSV() is working.");

        // check directory with dataset
        String datasetPath = this.importDir + "/" + datasetName + "/";
        File file = new File(datasetPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalStateException(
                    String.format("Dataset directory [%s] doesn't exist or is a directory!", datasetName));
        }

        // iterate over all dataset elements type and load them one-by-one
        for (LuxDataType luxDataType : LuxDataType.values()) {

            // generate path
            String dataTypePath = datasetPath + luxDataType.getCsvFileName();
            File typeFile = new File(dataTypePath);

            if (typeFile.exists() && typeFile.isFile()) { // process only if file exists
                List<LuxModelInterface> elements = LuxMSClient.loadElementFromCSV(dataTypePath, luxDataType);
                System.out.println("\n\t" + elements + "\n");

                // todo: implement loading into LuxMS system!

            } else { // file doesn't exist - warn to log and skip it
                LOGGER.warn(String.format("Type file [%s] doesn't exist or not a file! Skipped.", typeFile));
            }

        } // end of FOR

    }
}
