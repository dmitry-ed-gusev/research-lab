package gusevdm.luxms;

import gusev.dmitry.jtils.utils.JIOUtils;
import gusevdm.config.Environment;
import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModel;
import gusevdm.luxms.model.LuxModelFactory;
import gusevdm.luxms.model.LuxModelInterface;
import gusevdm.luxms.model.elements.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.*;

import static gusevdm.ConnectorDefaults.DEFAULT_ENCODING;

/***/
// todo: make this class a singleton???
public class LuxClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxClient.class);

    // some useful defaults
    private static final String DATASET_ID_FILE = "datasetid";

    // internal state
    private final String importDir;
    private final LuxRestClient luxRestClient;

    /***/
    public LuxClient() {
        LOGGER.debug("LuxClient constructor() is working.");
        Environment environment = Environment.getInstance();
        this.importDir       = environment.getCsvImportDir();
        this.luxRestClient = new LuxRestClient();
    }

    /** Load one element of LuxMS data model from specified CSV file. */
    // todo: move to model or to factory
    private static List<LuxModelInterface> loadElementFromCSV(String filePath, LuxDataType elementType) throws IOException {
        LOGGER.debug("LuxClient.loadElementFromCSV() is working.");

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
        LOGGER.debug("LuxClient.checkDatasetIdForCSV() is working.");

        String idFilePath = datasetPath + DATASET_ID_FILE;
        File idFile = new File(idFilePath);
        if (idFile.exists() && idFile.isFile()) { // file exists - read id from it
            return JIOUtils.readLongFromFile(idFilePath);
        }

        return -1; // no dataset id file found
    }

    /***/
    // todo: move to model or to factory (load model from CSV -> to factory, load model to system - here (below)
    public void loadFromCSV(String datasetName) throws IOException {
        LOGGER.debug("LuxClient.loadFromCSV() is working.");

        // check directory with dataset (csv import dir)
        String datasetPath = this.importDir + "/" + datasetName + "/";
        File file = new File(datasetPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalStateException(
                    String.format("Dataset directory [%s] doesn't exist or isn't a directory!", datasetName));
        }

        // check for dataset id file - if present, update existing, if not - create new one
        long datasetId = LuxClient.getDatasetIdForCSV(datasetPath);
        LOGGER.debug(String.format("Loaded dataset ID = [%s].", datasetId));

        // todo: probably - bug in REST protocol for LuxMS RESP API. Created (by REST) dataset isn't usable!
        // method of putting entries to LuxMS - update or insert
        //boolean updateEntries = true; // update (HTTP POST), not insert (HTTP PUT)
        // create dataset and write dataset id file (if no dataset exists)
        if (datasetId <= 0) {
            LOGGER.warn("Dataset in LuxMS system doesnt exist! Creating.");
            throw new IllegalStateException("Dataset is unknown (can't find [datasetid] file)!");
            //LuxDataSet dataset = this.luxRestClient.
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
                List<LuxModelInterface> elements = LuxClient.loadElementFromCSV(dataTypePath, luxDataType);
                System.out.println("\n\t" + elements + "\n");

                for (LuxModelInterface element : elements) {

                    this.luxRestClient.addTableEntry(datasetId, element, true);

                    // todo: see mentioned bug above (REST protocol for LuxMS BI)
                    // add/update entry in LuxMS system
                    //if (updateEntries) { // HTTP PUT -> update
                        // todo: implementation
                    //} else { // HTTP POST -> insert
                        //this.luxRestClient.addTableEntry(datasetId, element);
                    //}
                } // end of FOR

            } else { // file doesn't exist - warn to log and skip it
                LOGGER.warn(String.format("Type file [%s] doesn't exist or not a file! Skipped.", typeFile));
            }

        } // end of FOR

    }

    /***/
    public LuxModel loadModelFromXml(String xmlFilePath) throws ParserConfigurationException, IOException, SAXException, ParseException {
        LOGGER.debug(String.format("LuxClient.loadModelFromXml() is working. Model file: [%s].", xmlFilePath));

        // LuxMS data model
        LuxModel luxModel = new LuxModel();

        File xmlFile = new File(xmlFilePath);
        // XML factory/builder/document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        dBuilder  = dbFactory.newDocumentBuilder();
        Document               doc       = dBuilder.parse(xmlFile);

        // normalize document - optional, but recommended
        // read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        String rootName = doc.getDocumentElement().getNodeName();
        LOGGER.debug(String.format("Found root element: %s", rootName));

        // get sql file name
        String sqlFileName = doc.getElementsByTagName("sqlFile").item(0).getTextContent();
        LOGGER.debug(String.format("Sql file name: [%s].", sqlFileName));
        luxModel.setSqlFile(sqlFileName);
        // get dataset ID for current model
        Long datasetId = Long.parseLong(doc.getElementsByTagName("datasetId").item(0).getTextContent());
        LOGGER.debug(String.format("Dataset ID: [%s].", datasetId));
        luxModel.setDatasetId(datasetId);

        // get list of units
        Map<Long, LuxUnit> units = new HashMap<>();
        NodeList nodesList = doc.getElementsByTagName("unit");
        for (int temp = 0; temp < nodesList.getLength(); temp++) {
            Node node = nodesList.item(temp);
            LOGGER.debug(String.format("Current Element: %s", node.getNodeName()));
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                long   id         = Long.parseLong(eElement.getAttribute("id"));
                String title      = eElement.getElementsByTagName("title").item(0).getTextContent();
                String shortTitle = eElement.getElementsByTagName("shortTitle").item(0).getTextContent();
                String axisTitle  = eElement.getElementsByTagName("axisTitle").item(0).getTextContent();
                String prefix     = eElement.getElementsByTagName("prefix").item(0).getTextContent();
                String suffix     = eElement.getElementsByTagName("suffix").item(0).getTextContent();
                LuxUnit unit = new LuxUnit(id, title, shortTitle, axisTitle, prefix, suffix);
                LOGGER.debug(String.format("Loaded unit: %s", unit));
                units.put(id, unit);
            }
        }
        luxModel.setUnits(units);

        // get list of metrics
        Map<Long, LuxMetric> metrics = new HashMap<>();
        nodesList = doc.getElementsByTagName("metric");
        for (int temp = 0; temp < nodesList.getLength(); temp++) {
            Node node = nodesList.item(temp);
            LOGGER.debug(String.format("Current Element: %s", node.getNodeName()));
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                long   id        = Long.parseLong(eElement.getAttribute("id"));
                String title     = eElement.getElementsByTagName("title").item(0).getTextContent();
                int    treeLevel = Integer.parseInt(eElement.getElementsByTagName("treeLevel").item(0).getTextContent());
                long   parentId  = Long.parseLong(eElement.getElementsByTagName("parentId").item(0).getTextContent());
                long   unitId    = Long.parseLong(eElement.getElementsByTagName("unitId").item(0).getTextContent());
                int    sortOrder = Integer.parseInt(eElement.getElementsByTagName("sortOrder").item(0).getTextContent());
                LuxMetric metric = new LuxMetric(id, title, treeLevel, parentId, false, unitId, sortOrder);
                LOGGER.debug(String.format("Loaded metric: %s", metric));
                metrics.put(id, metric);
            }
        }
        luxModel.setMetrics(metrics);

        // load periods config
        String[] years = doc.getElementsByTagName("years").item(0).getTextContent().trim().split("\\s*,\\s*");
        LOGGER.debug(String.format("Loaded years: %s", Arrays.toString(years)));
        Map<Long, LuxPeriod> periods = LuxModel.generatePeriods(years);
        luxModel.setPeriods(periods);

        // load locations config
        String[] locationsTitlesCols = doc.getElementsByTagName("titlesColumns").item(0).getTextContent().trim().split("\\s*,\\s*");
        LOGGER.debug(String.format("Loaded columns for locations titles: %s", Arrays.toString(locationsTitlesCols)));
        luxModel.setLocationsTitlesCols(locationsTitlesCols);

        // load data points config (columns)
        String[] dataPointsValCols = doc.getElementsByTagName("valuesColumns").item(0).getTextContent().trim().split("\\s*,\\s*");
        LOGGER.debug(String.format("Loaded columns for data points values: %s", Arrays.toString(dataPointsValCols)));
        luxModel.setDataValuesCols(dataPointsValCols);
        // load data points config (metrics ids)
        String[] dataPointsMetricsIds = doc.getElementsByTagName("dataMetricsIds").item(0).getTextContent().trim().split("\\s*,\\s*");
        LOGGER.debug(String.format("Loaded metrics ids for data points values: %s", Arrays.toString(dataPointsMetricsIds)));
        luxModel.setDataValuesMetricsIds(dataPointsMetricsIds);

        return luxModel;
    }

    /***/
    public void loadFromModel(LuxModel model) throws IOException {
        LOGGER.debug("LuxClient.loadFromModel() is working.");

        // units
        Map<Long, LuxUnit> units = model.getUnits();
        for (LuxUnit unit : units.values()) {
            this.luxRestClient.addTableEntry(model.getDatasetId(), unit, true);
        }
        // metrics
        Map<Long, LuxMetric> metrics = model.getMetrics();
        for (LuxMetric metric : metrics.values()) {
            this.luxRestClient.addTableEntry(model.getDatasetId(), metric, true);
        }
        // locations
        Map<Long, LuxLocation> locations = model.getLocations();
        for (LuxLocation location : locations.values()) {
            this.luxRestClient.addTableEntry(model.getDatasetId(), location, true);
        }
        // periods
        Map<Long, LuxPeriod> periods = model.getPeriods();
        for (LuxPeriod period : periods.values()) {
            this.luxRestClient.addTableEntry(model.getDatasetId(), period, true);
        }
        // data
        Map<Long, LuxDataPoint> dataPoints = model.getDataPoints();
        for (LuxDataPoint dataPoint : dataPoints.values()) {
            this.luxRestClient.addTableEntry(model.getDatasetId(), dataPoint, true);
        }

    }

}
