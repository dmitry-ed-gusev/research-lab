package dgusev.io;

import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dgusev.utils.MyCommonUtils.not;
import static gusev.dmitry.jtils.UtilitiesDefaults.DEFAULT_ENCODING;

/**
 * Reading/parsing CSV files and other useful utilities.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
// https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/

@CommonsLog
public class MyCsvUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE     = '"';

    /** Parse line with default separator (comma [,]) and default quote (double quote ["]). */
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /** Parse line with  custom separator and default quote (double quote ["]). */
    public static List<String> parseLine(String cvsLine, char separator) {
        return parseLine(cvsLine, separator, DEFAULT_QUOTE);
    }

    /**
     * Parse line with custom separator and quote symbols.
     * If quote symbol is space (' ') it will be replaced by default quote symbol (double quote ["]).
     */
    public static List<String> parseLine(String cvsLine, char separator, char customQuote) {

        List<String> result = new ArrayList<>();

        if (StringUtils.isBlank(cvsLine)) { // <- fast-check (if input is empty, return!)
            return result;
        }

        if (customQuote == ' ') { // replace space as a quote to default quote
            customQuote = DEFAULT_QUOTE;
        }

        //if (separators == ' ') { // <- we are allowed to use space as a separator char
        //    separators = DEFAULT_SEPARATOR;
        //}

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separator) {

                    // filter out empty values
                    if (!StringUtils.isBlank(curVal.toString())) {
                        result.add(StringUtils.strip(curVal.toString(), String.valueOf(customQuote)));
                    }

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') { //ignore LF characters
                    //noinspection UnnecessaryContinue
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        // filter out empty values
        if (!StringUtils.isBlank(curVal.toString())) {
            result.add(StringUtils.strip(curVal.toString(), String.valueOf(customQuote)));
        }

        return result;
    }

    /***/
    public static List<String> readCSVFile(@NonNull InputStream fileStream, @NonNull String encoding) throws IOException {
        LOG.debug("MyIOUtils.readCSVFile(Stream) is working.");

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

        return MyCsvUtils.readCSVFile(new FileInputStream(fileName), encoding);
    }

    /**
     * Метод возвращает список, разделенный запятыми (CSV - Comma-Separated-Values), полученный из массива-списка.
     * Если исходный список пуст метод вернет значение NULL.
     */
    public static String getCSVFromArrayList(ArrayList<Integer> list) {
        String result = null;
        if ((list != null) && (!list.isEmpty())) {
            StringBuilder csv = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                csv.append(list.get(i));
                if (i < (list.size() - 1)) {
                    csv.append(", ");
                }
            }
            result = csv.toString();
        }
        return result;
    }

    /***/
    public static List<CSVRecord> getCSVRecordsList(@NonNull String csvFile) throws IOException {
        LOG.debug("MyCsvUtils.getCSVRecordsList() is working.");

        // build CSV format (with specified file header)
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withIgnoreSurroundingSpaces()
                .withTrim()              // trim leading/trailing spaces
                .withIgnoreEmptyLines()  // ignore empty lines
                .withCommentMarker('#'); // use # as a comment sign

        // read and process CSV file
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), DEFAULT_ENCODING))) {
            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOG.info(String.format("Got [%s] record(s) from CSV [%s].", csvRecords.size(), csvFile));
            return csvRecords;
        }
    }

}