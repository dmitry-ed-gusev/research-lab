package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;import java.io.File;import java.io.FileWriter;import java.io.IOException;import java.io.Writer;import java.lang.Character;import java.lang.String;import java.lang.StringBuilder;import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;import java.util.Date;import java.util.GregorianCalendar;import java.util.HashMap;import java.util.List;import java.util.Map;import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some useful common utils for whole application. Utils for different cases - counting, work with dbases etc.
 * Class is final and can't be instantiated, all methods are static.
 *
 * Transliteration was made by Gafetdinov Denis.
 *
 * @author Gusev Dmitry, Gafetdinov Denis.
 * @version 3.0 (DATE: 10.10.2013)
*/

public final class CommonUtils {

    private static Log log = LogFactory.getLog(CommonUtils.class);

    // this static member is used for transliteration method
    private static final Map<Character, String> CHARS_MAP = new HashMap<>();

    static {
        // uppercase letters pairs
        CHARS_MAP.put('А', "A");  CHARS_MAP.put('Б', "B");  CHARS_MAP.put('В', "V");  CHARS_MAP.put('Г', "G");
        CHARS_MAP.put('Д', "D");  CHARS_MAP.put('Е', "E");  CHARS_MAP.put('Ё', "E");  CHARS_MAP.put('Ж', "Zh");
        CHARS_MAP.put('З', "Z");  CHARS_MAP.put('И', "I");  CHARS_MAP.put('Й', "I");  CHARS_MAP.put('К', "K");
        CHARS_MAP.put('Л', "L");  CHARS_MAP.put('М', "M");  CHARS_MAP.put('Н', "N");  CHARS_MAP.put('О', "O");
        CHARS_MAP.put('П', "P");  CHARS_MAP.put('Р', "R");  CHARS_MAP.put('С', "S");  CHARS_MAP.put('Т', "T");
        CHARS_MAP.put('У', "U");  CHARS_MAP.put('Ф', "F");  CHARS_MAP.put('Х', "H");  CHARS_MAP.put('Ц', "C");
        CHARS_MAP.put('Ч', "Ch"); CHARS_MAP.put('Ш', "Sh"); CHARS_MAP.put('Щ', "Sh"); CHARS_MAP.put('Ъ', "'");
        CHARS_MAP.put('Ы', "Y");  CHARS_MAP.put('Ь', "'");  CHARS_MAP.put('Э', "E");  CHARS_MAP.put('Ю', "U");
        CHARS_MAP.put('Я', "Ya");
        //lowercase letters pairs
        CHARS_MAP.put('а', "a");  CHARS_MAP.put('б', "b");  CHARS_MAP.put('в', "v");  CHARS_MAP.put('г', "g");
        CHARS_MAP.put('д', "d");  CHARS_MAP.put('е', "e");  CHARS_MAP.put('ё', "e");  CHARS_MAP.put('ж', "zh");
        CHARS_MAP.put('з', "z");  CHARS_MAP.put('и', "i");  CHARS_MAP.put('й', "i");  CHARS_MAP.put('к', "k");
        CHARS_MAP.put('л', "l");  CHARS_MAP.put('м', "m");  CHARS_MAP.put('н', "n");  CHARS_MAP.put('о', "o");
        CHARS_MAP.put('п', "p");  CHARS_MAP.put('р', "r");  CHARS_MAP.put('с', "s");  CHARS_MAP.put('т', "t");
        CHARS_MAP.put('у', "u");  CHARS_MAP.put('ф', "f");  CHARS_MAP.put('х', "h");  CHARS_MAP.put('ц', "c");
        CHARS_MAP.put('ч', "ch"); CHARS_MAP.put('ш', "sh"); CHARS_MAP.put('щ', "sh"); CHARS_MAP.put('ъ', "'");
        CHARS_MAP.put('ы', "y");  CHARS_MAP.put('ь', "'");  CHARS_MAP.put('э', "e");  CHARS_MAP.put('ю', "u");
        CHARS_MAP.put('я', "ya");
    }

    private CommonUtils() {} // noninstantiability

    /***/
    public static String getStringResultSet(ResultSet rs, int width) {
        log.debug("DBUtils.getStringResultSet() working.");

        StringBuilder rows = new StringBuilder();
        // process result set
        if (rs != null) {
            try {

                if (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    // line for header and footer of the table
                    String horizontalLine = StringUtils.repeat("-", width * columnCount + columnCount + 1) + "\n";
                    // creating header
                    StringBuilder header = new StringBuilder(horizontalLine).append("|");
                    for (int i = 1; i <= columnCount; i++) {
                        header.append(String.format("%" + width + "s|", StringUtils.center(rs.getMetaData().getColumnName(i), width)));
                    }
                    header.append("\n").append(horizontalLine);
                    // add header to result
                    rows.append(header);
                    // creating data rows
                    int counter = 0;
                    do {
                        StringBuilder row = new StringBuilder("|");
                        for (int i = 1; i <= columnCount; i++) {
                            row.append(String.format("%" + width + "s|", StringUtils.center(rs.getString(i), width)));
                        }
                        row.append("\n");
                        // add data row to result
                        rows.append(row);
                        counter++;
                    } while (rs.next());
                    // add footer horizontal line
                    rows.append(horizontalLine).append("Total record(s): ").append(counter).append("\n");
                } else {
                    log.warn("ResultSet is not NULL, but is EMPTY!");
                }
            } // end of TRY
            catch (SQLException e) {
                log.error("SQL error occured: " + e.getMessage());
            }
        } else log.warn("ResultSet is NULL!");

        return rows.toString();
    }

    /**
     * Method returns date range for specified date and delta (in days). Method is null-safe, if input date is null,
     * method returns pair with two current date/time values.
     * @param date Date date for date range (start point)
     * @param deltaDays int half-range delta
     * @return Pair[Date, Date] immutable pair with date range bounds.
    */
    public static Pair<Date, Date> getDateRange(Date date, int deltaDays) {
        Pair<Date, Date> result;
        if (date != null) { // input date isn't null - processing
            // calendar instance for date manipulating
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date startDate;
            Date endDate;
            // calculate bounds of date range
            if (deltaDays > 0) {
                calendar.add(Calendar.DATE, deltaDays); // move date forward
                endDate = calendar.getTime();
                calendar.add(Calendar.DATE, -2 * deltaDays); // move date backward
                startDate = calendar.getTime();
            } else if (deltaDays < 0) {
                calendar.add(Calendar.DATE, deltaDays); // move date backward
                startDate = calendar.getTime();
                calendar.add(Calendar.DATE, -2 * deltaDays); // move date forward
                endDate = calendar.getTime();
            } else {
                startDate = date;
                endDate   = startDate;
            }
            result = new ImmutablePair<>(startDate, endDate);
        } else { // input date is null - we return pair with today date/time
            result = new ImmutablePair<>(new Date(), new Date());
        }
        // resulting pair
        return result;
    }

    /***/
    public static void saveStringToFile(String data, String fileName) {
        log.debug("CommonUtils.saveStringToFile() working. ");

        if (!StringUtils.isBlank(data) && !StringUtils.isBlank(fileName)) { // data and file name OK - write it!
            if (!new File(fileName).isDirectory()) {

                // try-with-resources - Java7 feature
                try (Writer         fwriter = new FileWriter(fileName, false);
                     BufferedWriter bwriter = new BufferedWriter(fwriter)) {
                    bwriter.write(data);
                } catch (IOException e) {
                    log.error(e);
                }
            } else { // input fileName points to a directory
                log.error("Can't write data to [" + fileName + "] file! It's a directory!");
            }
        } else { // empty data or file name
            log.error("Can't save data to file [" + fileName + "]! Wrong file or empty data!");
        }

    }

    /**
     * Format string to specified length - cut long string or fit short string with spaces (to the rigth) to fit
     * length. If string is empty/null or length <= 0, then method returns empty (not null!) string => "".
     * @param str String to be formatted
     * @param length int target length
     * @return String resulting formatted string
    */
    public static String formatStringToLength(String str, int length) {
        String result;

        if (!StringUtils.isBlank(str) && length > 0) {
            // check length
            if (str.length() <= length) {
                result = StringUtils.rightPad(str, length);  // fit answer with spaces (rigth)
            } else {
                log.debug("String is to long [" + str + "]! Cutting.");
                result = str.substring(0, length); // cut right part of message
            }
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Method replace russian symbols with latin symbols - it made transliteration.
     * @param str String cyrillic string
     * @return String latin string
    */
    public static Pair<String, String> getShortAndTranslit(String str) {
        //log.debug("CommonUtils.getShortAndTranslit() working."); // -> too much output

        Pair<String, String> result;
        StringBuilder shortRusName = new StringBuilder();
        StringBuilder shortEngName = new StringBuilder();

        if (!StringUtils.isBlank(str)) { // input string is OK - processing
            // Family Name Patronymic -> Family N. P. (short full name)
            Matcher matcher = Pattern.compile("\\b(\\p{InCyrillic}+)\\b").matcher(str);
            int counter = 0;
            while (matcher.find()) {
                counter++;
                if (counter == 1) {
                    shortRusName.append(matcher.group().replaceFirst(matcher.group().substring(0, 1),
                            matcher.group().substring(0, 1).toUpperCase())).append(" ");
                } else if (counter > 1) {
                    shortRusName.append(matcher.group().substring(0, 1).toUpperCase()).append(". ");
                }
            }

            // make a transliteration for short russian name
            for (int i = 0; i < shortRusName.length(); i++) {
                Character ch = shortRusName.charAt(i);
                String charFromMap = CHARS_MAP.get(ch);
                if (charFromMap == null) {
                    shortEngName.append(ch);
                } else {
                    shortEngName.append(charFromMap);
                }
            }

            // creating result
            result = new ImmutablePair<>(shortRusName.toString().trim(), shortEngName.toString().trim());
        } else { // input string is empty - we will return empty pair
            result = new ImmutablePair<>("", "");
        }

        return result;
    }

    /***/
    public static int[] convertListToArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    /***/
    public static Pair<Date, Date> getMonthDateRange(int startMonthDelta, int endMonthDelta) {
        Date currentDate = new Date();
        // generating start date of current month
        Calendar startCalendar = GregorianCalendar.getInstance();
        startCalendar.setTime(currentDate);
        startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        if (startMonthDelta != 0) { // add delta to start date month number, delta can be greater/less than zero
            startCalendar.add(Calendar.MONTH, startMonthDelta);
        }
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        // generating end date of current month
        Calendar endCalendar = GregorianCalendar.getInstance();
        endCalendar.setTime(currentDate);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (endMonthDelta != 0) { // add delta to end date month number, delta can be greater/less than zero
            endCalendar.add(Calendar.MONTH, endMonthDelta);
        }
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);
        // make a pair and return it
        return new ImmutablePair<>(startCalendar.getTime(), endCalendar.getTime());
    }

}