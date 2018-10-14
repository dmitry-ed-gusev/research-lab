package gusev.dmitry.jtils.utils;

import gusev.dmitry.jtils.datetime.DateTimeUtils;
import gusev.dmitry.jtils.datetime.TimePeriodType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gusev.dmitry.jtils.UtilitiesDefaults.DEFAULT_ENCODING;

/**
 * Common utilities for Connector application.
 * Methods here should be as generic as possible.
 */
public final class GeneralUtils {

    private static final Log LOGGER = LogFactory.getLog(GeneralUtils.class);

    /** Connector USER AGENT header for HTTP requests */
    private static final String USER_AGENT                  = "Mozilla/5.0";

    private GeneralUtils() {}

    /**
     * Read simple long value from file (file can be edited with with any editor).
     */
    public static long readLongFromFile(String filePath) throws IOException {
        LOGGER.info(String.format("GeneralUtils: reading long from file [%s].", filePath));
        // reading from file
        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr)) {

            // read first line with value
            return Long.parseLong(br.readLine());
        }
    }

    /** Sending simple HTTP GET request. */
    public static Pair<Integer, String> sendGet(String url) throws IOException {
        LOGGER.debug(String.format("GeneralUtils.sendGet() is working. URL: [%s].", url));

        // build URL and open connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");                      // optional, default is GET
        con.setRequestProperty("User-Agent", USER_AGENT); // add request header

        // get response code
        int responseCode = con.getResponseCode();
        // get response text
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // build and return resulting tuple
        return new ImmutablePair<>(responseCode, response.toString());
    }

    // HTTP POST request
    // todo: method for future usage...
    private static void sendPost(String url) throws Exception {
        LOGGER.debug(String.format("GeneralUtils.sendPost() is working. URL: [%s].", url));

        //String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }

    /** Dump sql ResultSet to CSV. Reworked original implementation with intermediate progress output. */
    public static void dumpResultSetToCSV(String csvFile, int reportStep, ResultSet rs, String tableName) throws IOException, SQLException {
        LOGGER.debug(String.format("GeneralUtils.dumpResultSetToCSV() is working. CSV file [%s].", csvFile));

        // with commons-csv. write CSV from ResultSet with header from ResultSet
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFile));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(rs))) {

            // init
            long counter     = 0;
            int  columnCount = rs.getMetaData().getColumnCount();
            // iterate over result set and dump it to CSV
            while (rs.next()) {

                for (int i = 1; i <= columnCount; i++) { // write one record to CSV file
                    csvPrinter.print(rs.getObject(i));
                }
                csvPrinter.println();

                counter++; // records counter
                if (counter % reportStep == 0) { // log progress
                    LOGGER.info(String.format("[%s] -> %s records exported.", StringUtils.trimToNull(tableName), counter));
                }

            } // end of while for the whole ResultSet

            csvPrinter.flush();
            LOGGER.info(String.format("[%s] -> %s records exported in total.", StringUtils.trimToNull(tableName), counter));
        }

        LOGGER.info(String.format("[%s] -> successfully exported.", StringUtils.trimToNull(tableName)));
    }

    /***/
    // todo: change default comment marker? (parameter for it???)
    public static Map<String, List<String>> readDatesPeriodsFromCSV(
            String csvFile, Date baseDate, SimpleDateFormat dateFormat)
            throws IOException {

        LOGGER.debug("GeneralUtils.readDatesPeriodsFromCSV() is working.");

        // check and fail-fast behaviour
        if (dateFormat == null || baseDate == null || StringUtils.isBlank(csvFile) ||
                !new File(csvFile).exists() || !new File(csvFile).isFile()) {
            throw new IllegalArgumentException(
                    String.format("Empty date format [%s], date [%s] or invalid CSV file [%s]!",
                            (dateFormat == null ? null : dateFormat.toPattern()), baseDate, csvFile));
        }

        // resulting map -> <name, dates list>
        Map<String, List<String>> result = new HashMap<>();

        // list of names with periods -> <name, time period, counter>
        List<Triple<String, TimePeriodType, Integer>> periodsList = new ArrayList<>();

        // build CSV format (with specified file header)
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withIgnoreSurroundingSpaces()
                .withTrim()              // trim leading/trailing spaces
                .withIgnoreEmptyLines()  // ignore empty lines
                .withCommentMarker('#'); // use # as a comment sign

        // todo: merge two cycles - iterating over resords and generating dates lists (see FOR below)
        // create CSV file reader (and read the file)
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), DEFAULT_ENCODING))) {
            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOGGER.info(String.format("Got records from CSV [%s]. Records count [%s].", csvFile, csvRecords.size()));
            // iterate over records, create instances and fill in resulting list

            // todo: add check - record columns count (.size()) and non-empty values for first and last (3rd) columns
            csvRecords.forEach(record -> periodsList.add(
                    new ImmutableTriple<String, TimePeriodType, Integer>(
                            record.get(0), TimePeriodType.getTypeByName(record.get(1)), Integer.parseInt(record.get(2)))));
        }

        LOGGER.debug(String.format("Loaded from CSV:%n[%s].", periodsList)); // <- just debug output

        // iterate over periods and get dates list for each name
        String name;            // tmp name
        List<String> datesList; // generated dates list for each name

        // iterate over batches list and do GET requests
        for (Triple<String, TimePeriodType, Integer> entry : periodsList) {

            // get name (left value)
            name = entry.getLeft();
            // get list of dates (with middle and right values)
            datesList = DateTimeUtils.getDatesListBack(baseDate, entry.getMiddle(), entry.getRight(), dateFormat);

            result.put(name, datesList);

        } // end of FOR -> batches

        return result;
    }

    /***/
    // todo: implement unit tests!
    public static String readStringFromFile(String filePath) throws IOException {
        LOGGER.debug(String.format("GeneralUtils.readStringFromFile() is working. File: [%s].", filePath));
        StringBuilder strBuider = new StringBuilder();
        // try-with-resources
        try (BufferedReader strReader = new BufferedReader(new FileReader(new File(filePath)))) {
            String tmpStr;
            while ((tmpStr = strReader.readLine()) != null) {
                strBuider.append(tmpStr).append("\n");
            }
        }
        // return loaded SQL query
        return strBuider.toString();
    }

}
