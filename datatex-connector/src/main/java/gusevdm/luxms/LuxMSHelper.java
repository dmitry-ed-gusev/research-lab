package gusevdm.luxms;

import gusevdm.Environment;
import gusevdm.luxms.model.LuxDataSet;
import gusevdm.luxms.model.LuxModelInterface;
import gusevdm.luxms.model.LuxUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gusevdm.ConnectorDefaults.DEFAULT_ENCODING;
import static gusevdm.luxms.model.LuxDataSet.*;

/** Some helpers methods for LuxMS client. */
// todo: add other dataset parameters

public final class LuxMSHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSHelper.class);

    private LuxMSHelper() {}

    /***/
    // todo: move this logic into dataset itself!
    public static LuxDataSet parseDataSet(JSONObject json) {
        LOGGER.debug(String.format("LuxMSHelper.parseDataSet() is working. Parsing dataset from JSON:%n\t[%s].", json));

        if (json == null) {
            throw new IllegalStateException("Received JSON object is NULL!");
        }

        // create dataset
        LuxDataSet luxDataSet = new LuxDataSet(Long.parseLong(json.get(DS_ID).toString()),
                json.get(DS_DESCRIPTION).toString(), json.get(DS_TITLE).toString());
        // set other parameters
        luxDataSet.setVisible(Integer.parseInt(json.get(DS_IS_VISIBLE).toString()) == 1);
        luxDataSet.setArchive(Integer.parseInt(json.get(DS_IS_ARCHIVE).toString()) == 1);
        luxDataSet.setGuid(json.get(DS_GUID).toString());
        luxDataSet.setOwnerUser(json.get(DS_OWNER_USER_ID) == null ? null : json.get(DS_OWNER_USER_ID).toString());
        luxDataSet.setParentGuid(json.get(DS_PARENT_GUID) == null ? null : json.get(DS_PARENT_GUID).toString());
        luxDataSet.setPostProcessSql(json.get(DS_POST_PROCESS_SQL) == null ? null : json.get(DS_POST_PROCESS_SQL).toString());
        luxDataSet.setSchemaName(json.get(DS_SCHEMA_NAME).toString());

        return luxDataSet;
    }

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

        String csvFile = environment.getCsvImportDir() + "/lux_units.csv";
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
