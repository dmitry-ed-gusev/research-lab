package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Some useful common utils for whole application. Utils for different cases - counting, work with dbases etc.
 * Class is final and can't be instantiated, all methods are static.
 * <p>
 * Transliteration was made by Gafetdinov Denis.
 *
 * @author Gusev Dmitry, Gafetdinov Denis.
 * @version 4.0 (DATE: 28.05.2017)
 */

public final class CommonUtils {

    private static Log LOG = LogFactory.getLog(CommonUtils.class);

    // this static member is used for transliteration method
    private static final Map<Character, String> CHARS_MAP = new HashMap<>();

    static {
        // uppercase letters pairs
        CHARS_MAP.put('А', "A");
        CHARS_MAP.put('Б', "B");
        CHARS_MAP.put('В', "V");
        CHARS_MAP.put('Г', "G");
        CHARS_MAP.put('Д', "D");
        CHARS_MAP.put('Е', "E");
        CHARS_MAP.put('Ё', "E");
        CHARS_MAP.put('Ж', "Zh");
        CHARS_MAP.put('З', "Z");
        CHARS_MAP.put('И', "I");
        CHARS_MAP.put('Й', "I");
        CHARS_MAP.put('К', "K");
        CHARS_MAP.put('Л', "L");
        CHARS_MAP.put('М', "M");
        CHARS_MAP.put('Н', "N");
        CHARS_MAP.put('О', "O");
        CHARS_MAP.put('П', "P");
        CHARS_MAP.put('Р', "R");
        CHARS_MAP.put('С', "S");
        CHARS_MAP.put('Т', "T");
        CHARS_MAP.put('У', "U");
        CHARS_MAP.put('Ф', "F");
        CHARS_MAP.put('Х', "H");
        CHARS_MAP.put('Ц', "C");
        CHARS_MAP.put('Ч', "Ch");
        CHARS_MAP.put('Ш', "Sh");
        CHARS_MAP.put('Щ', "Sh");
        CHARS_MAP.put('Ъ', "'");
        CHARS_MAP.put('Ы', "Y");
        CHARS_MAP.put('Ь', "'");
        CHARS_MAP.put('Э', "E");
        CHARS_MAP.put('Ю', "U");
        CHARS_MAP.put('Я', "Ya");
        //lowercase letters pairs
        CHARS_MAP.put('а', "a");
        CHARS_MAP.put('б', "b");
        CHARS_MAP.put('в', "v");
        CHARS_MAP.put('г', "g");
        CHARS_MAP.put('д', "d");
        CHARS_MAP.put('е', "e");
        CHARS_MAP.put('ё', "e");
        CHARS_MAP.put('ж', "zh");
        CHARS_MAP.put('з', "z");
        CHARS_MAP.put('и', "i");
        CHARS_MAP.put('й', "i");
        CHARS_MAP.put('к', "k");
        CHARS_MAP.put('л', "l");
        CHARS_MAP.put('м', "m");
        CHARS_MAP.put('н', "n");
        CHARS_MAP.put('о', "o");
        CHARS_MAP.put('п', "p");
        CHARS_MAP.put('р', "r");
        CHARS_MAP.put('с', "s");
        CHARS_MAP.put('т', "t");
        CHARS_MAP.put('у', "u");
        CHARS_MAP.put('ф', "f");
        CHARS_MAP.put('х', "h");
        CHARS_MAP.put('ц', "c");
        CHARS_MAP.put('ч', "ch");
        CHARS_MAP.put('ш', "sh");
        CHARS_MAP.put('щ', "sh");
        CHARS_MAP.put('ъ', "'");
        CHARS_MAP.put('ы', "y");
        CHARS_MAP.put('ь', "'");
        CHARS_MAP.put('э', "e");
        CHARS_MAP.put('ю', "u");
        CHARS_MAP.put('я', "ya");
    }

    private CommonUtils() {
    } // noninstantiability

    /***/
    public static String getStringResultSet(ResultSet rs, int width) {
        LOG.debug("DBUtils.getStringResultSet() working.");

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
                    LOG.warn("ResultSet is not NULL, but is EMPTY!");
                }
            } // end of TRY
            catch (SQLException e) {
                LOG.error("SQL error occured: " + e.getMessage());
            }
        } else LOG.warn("ResultSet is NULL!");

        return rows.toString();
    }

    /**
     * Method returns date range for specified date and delta (in days). Method is null-safe, if input date is null,
     * method returns pair with two current date/time values.
     *
     * @param date      Date date for date range (start point)
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
                endDate = startDate;
            }
            result = new ImmutablePair<>(startDate, endDate);
        } else { // input date is null - we return pair with today date/time
            result = new ImmutablePair<>(new Date(), new Date());
        }
        // resulting pair
        return result;
    }

    /**
     * Format string to specified length - cut long string or fit short string with spaces (to the rigth) to fit
     * length. If string is empty/null or length <= 0, then method returns empty (not null!) string => "".
     *
     * @param str    String to be formatted
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
                LOG.debug("String is to long [" + str + "]! Cutting.");
                result = str.substring(0, length); // cut right part of message
            }
        } else {
            result = "";
        }

        return result;
    }

    /**
     * Method replace russian symbols with latin symbols - it made transliteration.
     *
     * @param str String cyrillic string
     * @return String latin string
     */
    public static Pair<String, String> getShortAndTranslit(String str) {
        //LOG.debug("CommonUtils.getShortAndTranslit() working."); // -> too much output

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
        for (int i = 0; i < ret.length; i++) {
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

    /**
     * Writes access token and its date from specified file.
     * If file already exist - throw exception or overwrite it (if overwrite = true).
     */
    public static void saveDatePair(Pair<Date, String> token, SimpleDateFormat format,
                                    String tokenFile, boolean overwrite) throws IOException {
        LOG.debug(String.format("CommonUtilities.saveAccessToken() working. " +
                "Pair: [%s], file: [%s], overwrite: [%s].", token, tokenFile, overwrite));

        if (token == null || token.getLeft() == null ||
                StringUtils.isBlank(token.getRight()) ||
                StringUtils.isBlank(tokenFile)) { // check input parameters
            throw new IllegalArgumentException(
                    String.format("Empty pair (or its part): [%s] or pair file name: [%s]!", token, tokenFile));
        }

        // check for file existence (delete if needed)
        File file = new File(tokenFile);
        if (file.exists() && overwrite) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", tokenFile, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                LOG.error(String.format("Cant't delete file [%s]!", tokenFile));
                return;
            }
        }

        // write token to file
        try (FileWriter fw = new FileWriter(tokenFile);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // write access token and current date/time to file
            out.println(format.format(token.getLeft()));
            out.println(token.getRight());
        }

    }

    /**
     * Reads access token and its date from specified file.
     * If file doesn't exist throw exception.
     */
    public static Pair<Date, String> readDatePair(String tokenFile, SimpleDateFormat format) throws IOException, ParseException {
        LOG.debug("CommonUtilities.readAccessToken() working.");

        if (StringUtils.isBlank(tokenFile)) { // fail-fast
            throw new IllegalArgumentException("File name is null!");
        }

        // reading token from file
        try (FileReader fr = new FileReader(tokenFile);
             BufferedReader br = new BufferedReader(fr)) {

            Date tokenDate = format.parse(br.readLine()); // first line of file
            String token = br.readLine();                         // second line of file

            return new ImmutablePair<>(tokenDate, token);
        }
    }

    /**
     * Saves string to file with specified or auto-generated file name (based on time).
     * Returns file name.
     * If received string is empty throws run-time exception.
     */
    // todo: thread safety! this code isn't thread safe!
    // todo: add file name prefix - to determine source (social network client) for current file
    public static void saveStringToFile(String string, String fileName, boolean overwrite) throws IOException {
        LOG.debug("CommonUtilities.saveStringToFile() is working.");

        if (StringUtils.isBlank(string) || StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException(
                    String.format("String to save [%s] and/or file name [%s] is empty!", string, fileName));
        }

        // check for file existence (delete if needed)
        File file = new File(fileName);
        if (file.exists() && overwrite) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                throw new IllegalStateException(String.format("Cant't delete file [%s]!", fileName));
            }
        }

        // write data to file
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(string); // write data to file
        }

    }

    /**
     * Unzip ZIP archive and output its content to outputFolder.
     * If there are files (in output folder) - they will be overwritten.
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) {

        if (StringUtils.isBlank(zipFile)) { // fail-fast
            throw new IllegalArgumentException(String.format("Empty ZIP file name [%s]!", zipFile));
        }

        byte[] buffer = new byte[1024]; // unzip process buffer

        try {
            if (!StringUtils.isBlank(outputFolder)) {
                //create output directory is not exists
                File folder = new File(outputFolder);
                if (!folder.exists()) {
                    LOG.info(String.format("Destination output path [%s] doesn't exists! Creating...", outputFolder));
                    if (folder.mkdirs()) {
                        LOG.info(String.format("Destination output path [%s] created successfully!", outputFolder));
                    } else {
                        throw new IllegalStateException(String.format("Can't create zip output folder [%s]!", outputFolder));
                    }
                }
            } // end of check/create output folder

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)); //get the zip file content
            ZipEntry ze = zis.getNextEntry();                               //get the zipped file list entry
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File((StringUtils.isBlank(outputFolder) ? "" : (outputFolder + File.separator)) + fileName);
                LOG.debug(String.format("Unzipping file: absolute path [%s] / short name [%s].",
                        newFile.getAbsoluteFile(), newFile.getName()));

                // todo: create all non exists folders else we hit FileNotFoundException for compressed folder
                // todo: new File(newFile.getParent()).mkdirs();

                // write extracted file on disk
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            } // end of WHILE cycle

            zis.closeEntry();
            zis.close();
            LOG.info(String.format("Archive [%s] unzipped successfully.", zipFile));

        } catch (IOException ex) {
            LOG.error(ex);
        }

    }

    /**
     * Parses string arrays: ['value1', 'd"value2'] and returns set of strings.
     * If input string is empty or null, will return empty set.
     */
    // todo: add parameter -> strip spaces and where?
    public static Set<String> parseStringArray(String array) {

        Set<String> result = new HashSet<>();

        if (StringUtils.isBlank(array)) { // fast-check
            return result;
        }

        // get value and remove [] symbols (at start and at the end)
        String values = StringUtils.strip(StringUtils.trimToEmpty(array), "[]");
        String tmpValue;
        for (String value : StringUtils.split(values, ",")) { // add values to set
            tmpValue = StringUtils.trimToEmpty(StringUtils.strip(StringUtils.trimToEmpty(value), "'"));
            if (!StringUtils.isBlank(tmpValue)) {
                result.add(tmpValue);
            }
        }

        return result;
    }

}