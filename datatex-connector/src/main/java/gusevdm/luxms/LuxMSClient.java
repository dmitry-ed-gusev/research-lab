package gusevdm.luxms;

import gusevdm.Environment;
import gusevdm.luxms.model.elements.LuxUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static gusevdm.ConnectorDefaults.DEFAULT_ENCODING;

/***/
public class LuxMSClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSClient.class);

    /***/
    public static List<LuxUnit> loadFromCSV(long datasetId) {
        LOGGER.debug("LuxMSHelper.loadFromCSV() is working.");

        // resulting list of parsed data
        List<LuxUnit> units = new ArrayList<>();

        Reader fileReader = null;
        CSVParser csvParser;

        Environment environment = Environment.getInstance();

        // build CSV format
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(LuxUnit.FILE_HEADER);

        String csvFile = environment.getCsvImportDir() + "/units.csv";
        try {
            // file reader with specified encoding for CSV parser
            fileReader = new InputStreamReader(new FileInputStream(csvFile), DEFAULT_ENCODING);
            // CSV parser instance
            csvParser = new CSVParser(fileReader, csvFormat);
            LOGGER.debug("CSV Parser created.");

            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOGGER.info(String.format("Get records from CSV file [%s]. Records count [%s].", csvFile, csvRecords.size()));

            LuxUnit unit;
            for (int i = 1; i < csvRecords.size(); i++) {
                unit = new LuxUnit(csvRecords.get(i));
                LOGGER.debug(String.format("Loaded model item [%s].", unit));
                // add resulting item to list
                units.add(unit);
            } // end of FOR statement

            LOGGER.info(String.format("Processed records [%s].", csvRecords.size()));

        } catch (IOException e) {
            LOGGER.error("Can't load data from CSV!", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    LOGGER.error(String.format("Can't close Reader object for file [%s]!", csvFile));
                }
            }
        } // end of finally

        return units;
    }
}
