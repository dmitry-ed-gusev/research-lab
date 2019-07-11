package gusev.dmitry.jtils.utils;

import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static gusev.dmitry.jtils.utils.MyCommonUtils.MapSortType.*;

/**
 * Some useful common utils for whole application. Utils for different cases - counting, work with dbases etc.
 * Class is final and can't be instantiated, all methods are static.
 * <p>
 * Transliteration was made by Gafetdinov Denis (many thanks!).
 *
 * @author Gusev Dmitry, Gafetdinov Denis.
 * @version 4.0 (DATE: 28.05.2017)
 */

@CommonsLog
public final class MyCommonUtils {

    /** Sort type ASC/DESC. */
    public enum MapSortType {
        ASC, DESC
    }

    // this static member is used for transliteration method
    private static final Map<Character, String> CHARS_MAP = new HashMap<Character, String>() {{
        put('А', "A");
        put('Б', "B");
        put('В', "V");
        put('Г', "G");
        put('Д', "D");
        put('Е', "E");
        put('Ё', "E");
        put('Ж', "Zh");
        put('З', "Z");
        put('И', "I");
        put('Й', "I");
        put('К', "K");
        put('Л', "L");
        put('М', "M");
        put('Н', "N");
        put('О', "O");
        put('П', "P");
        put('Р', "R");
        put('С', "S");
        put('Т', "T");
        put('У', "U");
        put('Ф', "F");
        put('Х', "H");
        put('Ц', "C");
        put('Ч', "Ch");
        put('Ш', "Sh");
        put('Щ', "Sh");
        put('Ъ', "'");
        put('Ы', "Y");
        put('Ь', "'");
        put('Э', "E");
        put('Ю', "U");
        put('Я', "Ya");
        //lowercase letters pairs
        put('а', "a");
        put('б', "b");
        put('в', "v");
        put('г', "g");
        put('д', "d");
        put('е', "e");
        put('ё', "e");
        put('ж', "zh");
        put('з', "z");
        put('и', "i");
        put('й', "i");
        put('к', "k");
        put('л', "l");
        put('м', "m");
        put('н', "n");
        put('о', "o");
        put('п', "p");
        put('р', "r");
        put('с', "s");
        put('т', "t");
        put('у', "u");
        put('ф', "f");
        put('х', "h");
        put('ц', "c");
        put('ч', "ch");
        put('ш', "sh");
        put('щ', "sh");
        put('ъ', "'");
        put('ы', "y");
        put('ь', "'");
        put('э', "e");
        put('ю', "u");
        put('я', "ya");
    }};

    private MyCommonUtils() { // non-instantiability
    }

    /***/
    // todo: move to some db utilities class (dbPilot project?)
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
        //LOG.debug("MyCommonUtils.getShortAndTranslit() working."); // -> too much output

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

        // use try-with-resources for auto close input streams
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {

            if (!StringUtils.isBlank(outputFolder)) {
                //create output directory if not exists
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

            ZipEntry ze = zis.getNextEntry(); // get first zip entry and start iteration
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File((StringUtils.isBlank(outputFolder) ? "" : (outputFolder + File.separator)) + fileName);

                LOG.debug(String.format("Processing -> name: %s | size: %s | compressed size: %s \n\t" +
                                "absolute name: %s",
                        fileName, ze.getSize(), ze.getCompressedSize(), newFile.getAbsoluteFile()));

                // if entry is a directory - create it and continue (skip the rest of cycle body)
                if (fileName.endsWith("/") || fileName.endsWith("\\")) {
                    if (newFile.mkdirs()) {
                        LOG.debug(String.format("Created dir: [%s].", newFile.getAbsoluteFile()));
                    } else {
                        throw new IllegalStateException(String.format("Can't create dir [%s]!", newFile.getAbsoluteFile()));
                    }
                    ze = zis.getNextEntry();
                    continue;
                }

                // todo: do we need this additional dirs creation?
                //File parent = file.getParentFile();
                //if (parent != null) {
                //    parent.mkdirs();
                //}

                // write extracted file on disk
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            } // end of WHILE cycle

            //zis.closeEntry();
            //zis.close();
            LOG.info(String.format("Archive [%s] unzipped successfully.", zipFile));

        } catch (IOException ex) {
            LOG.error(ex);
        }

    } // end of unZipIt

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

    /** Define predicate negation. */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return t -> !predicate.test(t);
    }

    /**
     * Return top of map as a string. If map is null/empty - return null.
     * If count <= 0 or >= input map size - return a whole map.
     */
    public static <K, V> String getTopFromMap(Map<K, V> map, int topCount) {
        LOG.debug("IPinYou.getTopFromMap() is working.");

        if (map == null || map.isEmpty()) { // fast checks for map (and return)
            return null;
        }

        int upperBound;
        if (topCount <= 0 || topCount >= map.size()) { // fast checks for count
            upperBound = map.size();
        } else {
            upperBound = topCount;
        }

        int counter = 0;
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry;

        // iterate over map and convert it to string
        while (iterator.hasNext() && counter < upperBound) {
            entry = iterator.next();
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            counter++;
        }

        return builder.toString();
    }

    /***/
    /*
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        //classic iterator example
//        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
//            Map.Entry<String, Integer> entry = it.next();
//            sortedMap.put(entry.getKey(), entry.getValue());
//        }

        return sortedMap;
    }
    */

    /**
     * Sort input Map by values. Map values should be comparable. Method uses generics.
     * Warning! Resulting type of map is LinkedHashMap (method may return different map type/implementation!)
     * todo: add a parameter for selecting order
     * todo: method changes the source map!!! add a key - change or not source map?
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, MapSortType type) {

        if (map == null) { // fast check and return null
            return null;
        }

        Map<K, V> result = new LinkedHashMap<>();
        if (map.isEmpty()) { // if source empty - return empty result
            return result;
        }
        if (map.size() == 1) { // if source size is 1 - don't sort
            result.putAll(map);
            return result;
        }

        if (type == null) { // fail-fast check
            throw new IllegalArgumentException("Sort type mustn't be NULL!");
        }

        // convert map to list of entries <Key, Value>
        //List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        // just move all map entries to list (modify source map, consume memory)
        List<Map.Entry<K, V>> list = new LinkedList<>();
        Iterator<Map.Entry<K, V>> sourceMapIterator = map.entrySet().iterator();
        //Map.Entry<K, V> sourceMapEntry;
        while (sourceMapIterator.hasNext()) {
            list.add(sourceMapIterator.next());
            //sourceMapEntry = sourceMapIterator.next();
            //result.put(sourceMapEntry.getKey(), sourceMapEntry.getValue());
            sourceMapIterator.remove();
        }

        // sort list of entries by values with specified comparator
        // (switch the o1 o2 position for a different order)
        list.sort((o1, o2) -> {
            if (ASC == type) {
                return (o1.getValue()).compareTo(o2.getValue()); // <- ASC
            } else {
                return (o2.getValue()).compareTo(o1.getValue()); // <- DESC
            }
        });

        // version with lambda
        //list.sort(Comparator.comparing(o -> (o.getValue())));

        // loop the sorted list and put it into a new insertion ordered LinkedHashMap.
        // not effective if map is very big (fails with out of memory)
        //for (Map.Entry<K, V> entry : list) {
        //    result.put(entry.getKey(), entry.getValue());
        //}

        // version with lambda. not effective if map is very big (fails with out of memory)
        //list.forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        // iterate over list, put each map entry from list to resulting map and remove entry from list
        // (we need it due to memory saving reasons)
        Iterator<Map.Entry<K, V>> iterator = list.iterator();
        Map.Entry<K, V> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            result.put(entry.getKey(), entry.getValue());
            iterator.remove();
        }

        return result;
    }

    /**
     * Java 8 Version. This will sort according to the value in ascending order; for descending order,
     * it is just possible to uncomment the call to Collections.reverseOrder().
     * todo: add a parameter for selecting order
     * todo: add unit tests for method
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueByLambda(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /***/
    public static <K, V> Map<K, V> removeFromMapByValue(Map<K, V> map, V value) {
        LOG.debug("MapUtils.removeFromMapByValue() is working.");

        if (map == null) { // fast check and return null
            return null;
        }

        if (map.isEmpty()) { // fast check and return original
            return map;
        }

        // iterate over map and remove unnecessary entries
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry;
        V entryValue;
        while (iterator.hasNext()) {
            entry = iterator.next();
            entryValue = entry.getValue();

            // check condition and remove entry from map
            if ((value == null && entryValue == null) ||
                    (value != null && value.equals(entryValue))) {
                iterator.remove();
            }
        } // end of WHILE

        return map;
    }

    /** Validate single property. */
    public static void validateSingleProperty(@NonNull String property, @NonNull String value) {

        if (value == null || "null".equals(value)) {
            String errorMessage = String.format("Required property is missing: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }

        if (value.trim().isEmpty()) {
            String errorMessage = String.format("Required property is blank: %s", property);
            throw new IllegalArgumentException(errorMessage);
        }

    }

    /**
     * Make sure that the whole environment is set up properly.
     * @throws IllegalArgumentException if at least one of the system properties is invalid
     */
    public static void validateEnvironment(@NonNull Map<String, String> environment, @NonNull List<String> mandatoryProperties) {
        LOG.debug("ConnectorUtilities.validateEnvironment() is working.");

        // check presence of all required properties
        for (String property : mandatoryProperties) {
            // todo: warning! -> string valueOf(null) -> "null" (string literal), not null value itself!
            MyCommonUtils.validateSingleProperty(property, String.valueOf(environment.getOrDefault(property, null)));
        }

        LOG.debug(String.format("Presence of all mandatory properties [%s] checked. All OK.", mandatoryProperties));
    }

} // end of MyCommonUtils class