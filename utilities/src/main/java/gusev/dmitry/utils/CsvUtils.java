package gusev.dmitry.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Reading/parsing CSV files and other useful utilities.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
// https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/

public class CsvUtils {

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

}