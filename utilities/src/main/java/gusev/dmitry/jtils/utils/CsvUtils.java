package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reading/parsing CSV files and other useful utilities.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
// https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/

public class CsvUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static void main(String[] args) throws Exception {

        String csvFile = "/Users/mkyong/csv/country2.csv";

        Scanner scanner = new Scanner(new File(csvFile));
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            System.out.println("Country [id= " + line.get(0) + ", code= " + line.get(1) + " , name=" + line.get(2) + "]");
        }
        scanner.close();

    }

    /***/
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /***/
    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    /***/
    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        if (StringUtils.isBlank(cvsLine)) { // <- fast-check (if input is empty, return!)
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        //if (separators == ' ') {
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

                } else if (ch == separators) {

                    // filter out empty values
                    if (!StringUtils.isBlank(curVal.toString())) {
                        result.add(StringUtils.strip(curVal.toString(), String.valueOf(customQuote)));
                    }

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
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
            //result.add(curVal.toString());
            result.add(StringUtils.strip(curVal.toString(), String.valueOf(customQuote)));
        }

        return result;
    }

}