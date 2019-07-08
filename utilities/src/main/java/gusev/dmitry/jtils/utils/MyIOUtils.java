package gusev.dmitry.jtils.utils;

import gusev.dmitry.jtils.datetime.DateTimeUtils;
import gusev.dmitry.jtils.datetime.TimePeriodType;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static gusev.dmitry.jtils.UtilitiesDefaults.DEFAULT_ENCODING;
import static gusev.dmitry.jtils.utils.MyCommonUtils.not;

/** Some IO utilities, useful for me. */
@CommonsLog
@NotThreadSafe
public final class MyIOUtils {

    private static final JSONParser JSON_PARSER = new JSONParser();
    private static final String     SELECT_SQL  = "SELECT * FROM %s"; // SQL query: get data from source DB

    private MyIOUtils() {}

    /**
     * Method is trying to delete specified file.
     * @param fileName String file for deletion
     * @param failOnDelete boolean if true - throws IOException if file can't be deleted
     */
    public static void deleteFileIfExists(@NonNull String fileName, boolean failOnDelete) throws IOException {
        LOG.debug(String.format("MyIOUtils.deleteFileIfExist() is working. File [%s].", fileName));

        File file = new File(fileName);
        if (file.exists()) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Remove it -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - throw an exception or write a log message
                if (failOnDelete) {
                    throw new IOException(String.format("Cant't delete file [%s] by unknown reason!", fileName));
                } else {
                    LOG.error(String.format("Cant't delete file [%s] by unknown reason!", fileName));
                }
            } // end of IF - fail on deletion
        } else {
            LOG.debug(String.format("File [%s] doesn't exist.", fileName));
        } // end of main IF statement
    }

    /** Read simple long value from file (file can be edited with with any editor). */
    public static long readLongFromFile(@NonNull String filePath) throws IOException {
        LOG.info(String.format("MyIOUtils.readLongFromFile() is working. Read long from [%s].", filePath));
        // reading from file
        try (FileReader fr = new FileReader(filePath);
             BufferedReader br = new BufferedReader(fr)) {

            // read first line with value
            return Long.parseLong(br.readLine());
        }
    }

    /** Write simple long value to file (file can be edited with with any editor). */
    public static void writeLongToFile(long value, @NonNull String fileName, boolean overwrite) throws IOException {
        LOG.info(String.format("MyIOUtils.writeLongToFile() is working. Write long [%s] to file [%s].", value, fileName));

        // overwrite file (if specified) - and fail on deletion error
        if (overwrite) {
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write value to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.println(value);
        }
    }

    /** Reads access token and its date from specified file. If file doesn't exist throw exception. */
    public static Pair<Date, String> readDatePairFromFile(@NonNull String tokenFile, @NonNull SimpleDateFormat format)
            throws IOException, ParseException {
        LOG.debug(String.format("MyIOUtils.readDatePairFromFile() working. Read from [%s].", tokenFile));

        // todo: remove this unnecessary code??? check!
        if (StringUtils.isBlank(tokenFile)) { // fail-fast
            throw new IllegalArgumentException("File name is null!");
        }

        // reading token from file
        try (BufferedReader br = new BufferedReader(new FileReader(tokenFile))) {
            Date tokenDate = format.parse(br.readLine()); // first line of file
            String token = br.readLine();                 // second line of file
            return new ImmutablePair<>(tokenDate, token);
        }
    }

    /**
     * Writes access token and its date from specified file.
     * If file already exist - throw exception or overwrite it (if overwrite = true).
     */
    public static void writeDatePairToFile(@NonNull Pair<Date, String> token, @NonNull SimpleDateFormat format,
                                    @NonNull String fileName, boolean overwrite) throws IOException {
        LOG.debug(String.format("MyIOUtils.writeDatePairToFile() is working. " +
                "Pair: [%s], file: [%s], overwrite: [%s].", token, fileName, overwrite));

        // check input parameters - fail-fast
        if (token.getLeft() == null || StringUtils.isBlank(token.getRight()) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("Empty pair (or its part): [%s] or pair file name: [%s]!", token, fileName));
        }

        if (overwrite) { // overwrite file
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write token to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.println(format.format(token.getLeft()));
            out.println(token.getRight());
        }
    }

    /**
     * Saves string to file with specified or auto-generated file name (based on time).
     * Returns file name.
     * If received string is empty throws run-time exception.
     */
    public static void writeStringToFile(@NonNull String string, @NonNull String fileName, boolean overwrite) throws IOException {
        LOG.debug(String.format("MyIOUtils.writeStringToFile() is working. Write to [%s].", fileName));

        if (StringUtils.isBlank(string) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("String to save [%s] and/or file name [%s] is empty!", string, fileName));
        }

        if (overwrite) {
            MyIOUtils.deleteFileIfExists(fileName, true);
        }

        // write data to file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.print(string); // write data to file
        }
    }

    /***/
    // todo: implement unit tests!
    public static String readStringFromFile(@NonNull String filePath) throws IOException {
        LOG.debug(String.format("MyIOUtils.readStringFromFile() is working. Read from [%s].", filePath));

        StringBuilder strBuider = new StringBuilder();

        // try-with-resources
        try (BufferedReader strReader = new BufferedReader(new FileReader(new File(filePath)))) {
            String tmpStr;
            while ((tmpStr = strReader.readLine()) != null) {
                strBuider.append(tmpStr).append("\n");
            }
        }

        return strBuider.toString();
    }

    /***/
    public static List<String> readCSVFile(@NonNull InputStream fileStream, @NonNull String encoding) throws IOException {
        LOG.debug("MyIOUtils.readCSVFile(Stream) is working.");

        if (fileStream == null) { // fail-fast
            throw new IOException("Empty file stream!");
        }

        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                fileStream, StringUtils.isBlank(encoding) ? DEFAULT_ENCODING : encoding))) {
            String rawLine;
            while ((rawLine = reader.readLine()) != null) {
                result.addAll(Arrays.stream(StringUtils.split(rawLine, ','))
                        .map(StringUtils::trimToEmpty)
                        .filter(not(StringUtils::isBlank))
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    /***/
    // todo: add variable separator
    // todo: read file from input stream
    public static List<String> readCSVFile(@NonNull String fileName, @NonNull String encoding) throws IOException {
        LOG.debug("MyIOUtils.readCSVFile(String) is working.");

        if (StringUtils.isBlank(fileName)) { // fail-fast
            throw new IOException("Empty file name!");
        }

        return MyIOUtils.readCSVFile(new FileInputStream(fileName), encoding);
    }

    /***/
    // todo: change default comment marker? (parameter for it???)
    public static Map<String, List<String>> readDatesPeriodsFromCSV(@NonNull String csvFile,
                                                                    @NonNull Date baseDate,
                                                                    @NonNull SimpleDateFormat dateFormat) throws IOException {

        LOG.debug("MyIOUtils.readDatesPeriodsFromCSV() is working.");

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
            LOG.info(String.format("Got records from CSV [%s]. Records count [%s].", csvFile, csvRecords.size()));
            // iterate over records, create instances and fill in resulting list

            // todo: add check - record columns count (.size()) and non-empty values for first and last (3rd) columns
            csvRecords.forEach(record -> periodsList.add(
                    new ImmutableTriple<String, TimePeriodType, Integer>(
                            record.get(0), TimePeriodType.getTypeByName(record.get(1)), Integer.parseInt(record.get(2)))));
        }

        LOG.debug(String.format("Loaded from CSV:%n[%s].", periodsList)); // <- just debug output

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

    /** Dump sql ResultSet to CSV. Reworked original implementation with intermediate progress output. */
    // todo: move to some db utilities class (dbPilot project?)
    public static void dumpResultSetToCSV(String csvFile, int reportStep, ResultSet rs, String tableName) throws IOException, SQLException {
        LOG.debug(String.format("MyIOUtils.dumpResultSetToCSV() is working. CSV file [%s].", csvFile));

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
                    LOG.info(String.format("[%s] -> %s records exported.", StringUtils.trimToNull(tableName), counter));
                }

            } // end of while for the whole ResultSet

            csvPrinter.flush();
            LOG.info(String.format("[%s] -> %s records exported in total.", StringUtils.trimToNull(tableName), counter));
        }

        LOG.info(String.format("[%s] -> successfully exported.", StringUtils.trimToNull(tableName)));
    }

    /***/
    public static void dumpDBToCSV(@NonNull Connection connection, int fetchSize, int reportStep,
                                   @NonNull String[] tablesList, @NonNull String dumpDir) throws SQLException, IOException {
        LOG.debug("MyIOUtils.dumpDBToCSV() is working.");

        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(fetchSize); // mandatory parameter to speed up dumping db

            String csvFile;
            for (String table : tablesList) {  // iterate over tables to export
                LOG.info(String.format("[%s] -> export started.", StringUtils.trimToNull(table)));

                try (ResultSet rs = stmt.executeQuery(String.format(SELECT_SQL, StringUtils.trimToNull(table)))) {
                    LOG.debug("Got ResultSet, starting output to CSV.");
                    // write one CSV for one table
                    csvFile = dumpDir + "/" + StringUtils.trimToNull(table) + ".csv";
                    MyIOUtils.dumpResultSetToCSV(csvFile, reportStep, rs, table); // dump ResultSet to CSV file
                } catch (SQLException e) {
                    LOG.error(String.format("Can't export table [%s]! Skipped.", table), e);
                } // end of internal TRY statement (with result set)

            } // end of FOR

        } // end of external TRY statement (with connection and statement)

    }

    /***/
    public static JSONObject readJsonObjectFromFile(@NonNull String jsonFile) throws IOException, org.json.simple.parser.ParseException {
        LOG.debug(String.format("MyIOUtils.readJsonObjectFromFile() is working. Read from [%s].", jsonFile));
        return (JSONObject) JSON_PARSER.parse(new FileReader(jsonFile));
    }

    /***/
    public static JSONArray readJsonArrayFromFile(@NonNull String jsonFile) throws IOException, org.json.simple.parser.ParseException {
        LOG.debug(String.format("MyIOUtils.readJsonObjectFromFile() is working. Read from [%s].", jsonFile));
        return (JSONArray) JSON_PARSER.parse(new FileReader(jsonFile));
    }

}
