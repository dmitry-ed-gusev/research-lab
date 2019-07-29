package dgusev.utils;

import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static dgusev.utils.MyCommonUtils.MapSortType.ASC;

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

    private static final String PAIRS_DELIM = ";";
    private static final String KEY_VALUE_DELIM = "=";

    /**
     * Sort type ASC/DESC.
     */
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

    private MyCommonUtils() {}

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

    /**
     * Define predicate negation.
     */
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

    /**
     * Validate single property.
     */
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
     *
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

    /***/
    private static String getPairsDelimiter(String pairsDelimeter) {
        if (!StringUtils.isBlank(pairsDelimeter)) {
            return StringUtils.trimToEmpty(pairsDelimeter);
        } else {
            return PAIRS_DELIM;
        }
    }

    /***/
    private static String getKeyValueDelimeter(String keyValueDelimeter) {
        if (!StringUtils.isBlank(keyValueDelimeter)) {
            return StringUtils.trimToEmpty(keyValueDelimeter);
        } else {
            return KEY_VALUE_DELIM;
        }
    }

    /**
     * Converts string in format [name1=value1[pairsDelim]....[pairsDelim]nameN=valueN] into Properties object.
     * Deviations like: [name1=name2=name3=value1] or [name1=value1=value2] are processed correctly.
     */
    public static Properties getPropsFromString(String str, String pairsDelim, String keyValueDelim) {
        LOG.debug("MyCommonUtils.getPropsFromString() is working.");
        Properties result = null;

        // Если строка не пустая - работаем дальше
        if ((str != null) && (!str.trim().equals(""))) {

            String localPairsDelim = MyCommonUtils.getPairsDelimiter(pairsDelim);
            String localKeyValueDelim = MyCommonUtils.getKeyValueDelimeter(keyValueDelim);
            LOG.debug("USED: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

            String[] keyValueArray, keyValueSplitted;
            result = new Properties();
            // Разделяем строку на массив пар имя=значение. Пары значений разделяются символом <pairsDelim>, или, если нам не
            // передали этот символ, то символом по умолчанию - ; (PAIRS_DELIM). Нам необходимо получить значение ключа, которое
            // находится справа от самого первого слева знака pairsDelim (в строке 1=2=3;4=5;6=7 мы должны получить: key={1}, value={2=3;4=5;6=7}).
            keyValueArray = str.trim().split(localPairsDelim);

            // В цикле каждую пару имя=значение разбиваем на имя и значение
            for (String keyValuePair : keyValueArray) {

                //logger.debug("keyValuePair: " + keyValuePair); // <- вывод был необходим для отладки метода

                if ((keyValuePair != null) && (!keyValuePair.trim().equals(""))) {
                    // Значения разделяются символом localKeyValueDelim
                    keyValueSplitted = keyValuePair.split(localKeyValueDelim);

                    //if (keyValueSplitted.length >= 2)                                    // <- вывод был необходим для отладки метода
                    // logger.debug("splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- вывод был необходим для отладки метода
                    //              "value=[" + keyValueSplitted[1] + "]");                // <- вывод был необходим для отладки метода

                    // Если мы получили несколько параметров (более 2-х как минимум), то первый из них - имя значения (name), а второй
                    // - само значение (value), остальные парметры игнорируются (имя - параметр номер 0, значение - параметр номер 1).
                    if ((keyValueSplitted.length >= 2) && (!keyValueSplitted[0].trim().equals("")) && (!keyValueSplitted[1].trim().equals(""))) {

                        //logger.debug("Result splitted pair: key=[" + keyValueSplitted[0] + "] " +  // <- вывод был необходим для отладки метода
                        //             "value=[" + keyValueSplitted[1] + "]");                       // <- вывод был необходим для отладки метода

                        result.put(keyValueSplitted[0].trim(), keyValueSplitted[1].trim());
                    }
                }
            } // end of for

        } // end of if
        else {
            LOG.debug("Source string is empty!");
        }

        return result;
    }

    /**
     * Метод получает на входе набор свойств (Properties), из которого формирует строку формата
     * [name1=value1[pairsDelim]...[pairsDelim]nameN=valueN]. pairsDelim - символ-разделитель для пар значений. Если не
     * указан - используется значение по умолчанию - ; (константа PAIRS_DELIM - модуль Consts). Имя параметра от его значения
     * отделяется символом keyValueDelim - по умолчанию =. Если переданный набор свойств (Properties) пуст - метод вернет
     * значение null.
     *
     * @param props         Properties набор свойств, который преобразуется в результирующую строку.
     * @param pairsDelim    String символ-разделитель для пар имя/значение: name=value[DELIM]name=value.
     * @param keyValueDelim String символ-разделитель для имени и значения в паре имя/значение: name[KEY_VALUE_DELIM]value.
     * @return String строка, сформированная из полученного набора свойств.
     */
    public static String getStringFromProps(Properties props, String pairsDelim, String keyValueDelim) {
        LOG.debug("MyCommonUtils.getStringFromProps() is working.");

        StringBuilder result = null;

        // Если Properties не пусто - то продолжаем работать
        if ((props != null) && (!props.isEmpty())) {
            String key;
            result = new StringBuilder();

            String localPairsDelim = MyCommonUtils.getPairsDelimiter(pairsDelim);
            String localKeyValueDelim = MyCommonUtils.getKeyValueDelimeter(keyValueDelim);
            LOG.debug("USED PARAMS: pairsDelim: [" + localPairsDelim + "]; keyValuesDelim: [" + localKeyValueDelim + "].");

            // Проходим по всем парам имя/значение в наборе и формируем строку
            Enumeration e = props.keys();
            while (e.hasMoreElements()) {
                key = e.nextElement().toString();
                // Пара имя=значение не добавляется к строке, если ключ пустой
                if (!key.trim().equals("")) {
                    result.append(key).append(localKeyValueDelim).append(props.getProperty(key)).append(localPairsDelim);
                }
            }

            LOG.debug("RESULT: [" + result.toString() + "].");
        } else {
            LOG.debug("Received Properties object is empty!");
        }

        if (result == null) {
            return null;
        }

        return result.toString();
    }

    public enum DatePeriod {YEAR, MONTH, DAY}

    /***/
    public static String trans(String name) {
        StringBuilder name_trans = new StringBuilder();
        char name_char;
        int index_c;
        String rus = "АаБбВвГгДдЕеЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя";
        String[] eng = {"A", "a", "B", "b", "V", "v", "G", "g", "D", "d", "E", "e", "ZH", "zh", "Z", "z", "I", "i", "Y", "y", "K", "k", "L", "l",
                "M", "m", "N", "n", "O", "o", "P", "p", "R", "r", "S", "s", "T", "t", "U", "u", "F", "f", "KH", "kh", "TS", "ts", "CH", "ch",
                "SH", "sh", "SHCH", "shch", "Y", "y", "Y", "y", "Y", "y", "E", "e", "YU", "yu", "YA", "ya"};

        if (name != null && name.length() > 0) {
            for (int i = 0; i < name.length(); i++) {
                name_char = name.charAt(i);
                index_c = rus.indexOf(name_char);
                if (index_c > -1) {
                    name_trans.append(eng[index_c]);
                } else {
                    name_trans.append(name_char);
                }
            }
        }

        return name_trans.toString();
    }

    /**
     * Изменение на N-ое количество дней/месяцев/лет даты типа java.util.Date
     *
     * @param date       - дата
     * @param period     - количество дней/месяцев/лет на которые требуется перенести дату
     * @param typePeriod - тип периода: день/месяц/год
     * @return -  дата типа java.util.Date
     */
    public static Date dateToPeriod(Date date, int period, DatePeriod typePeriod) {

        Date returnDate = null;

        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            if (DatePeriod.YEAR.equals(typePeriod)) {
                cal.add(Calendar.YEAR, period);
            } else if (DatePeriod.MONTH.equals(typePeriod)) {
                cal.add(Calendar.MONTH, period);
            } else if (DatePeriod.DAY.equals(typePeriod)) {
                cal.add(Calendar.DATE, period);
            }

            returnDate = cal.getTime();

        } else {
            LOG.warn("Input data is NULL!");
        }
        return returnDate;
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

    /**
     * Метод формирует имя (строковое) фиксированной длины (параметр lenght) на основе указанного в параметре
     * name имени. Указанное имя дополняется лидирующими символами, указанными в парметре symbol. Параметр lenght
     * обязательно должен быть положительным и не равным 0, иначе метод вернет значение null. Еще одно ограничение на
     * значение параметра lenght: значение данного параметра должно быть больше (строго больше) длины значения параметра
     * name - в противном случае метод не выполнит никаких действий и вернет значение параметра name (имеется в виду случай
     * непустого параметра name, при пустом name метод вернет null). Параметр symbol должен содержать отображаемый символ,
     * в противном случае результирующее имя может вызывать крах различных модулей. Параметр name должен быть непустым (и
     * не состоять из одних символов пробела, табуляции и т.п.), если же он пуст, то метод верент значение null.
     */
    public static String getFixedLengthName(int lenght, char symbol, String name) {
        String result = null;
        // Проверяем параметр name
        if (!StringUtils.isBlank(name)) {
            LOG.debug("Name parameter is OK. Processing name [" + name + "].");
            // Проверяем параметр lenght (он должен быть положителен и не равен 0)
            if (lenght > 0) {
                LOG.debug("Lenght [" + lenght + "] is OK. Processing.");
                // Действия выполняем только при значении lenght > name.lenght
                if (lenght > name.length()) {
                    StringBuilder resultName = new StringBuilder();
                    for (int i = 0; i < (lenght - name.length()); i++) {
                        resultName.append(symbol);
                    }
                    resultName.append(name);
                    // Присвоение значения результату
                    result = resultName.toString();
                }
                // Значение lenght <= name.lenght
                else {
                    result = name;
                }
            }
            // Параметр lenght не подошел
            else {
                LOG.error("Wrong lenght [" + lenght + "]!");
            }
        }
        // Параметр name пуст - сообщим об ошибке!
        else {
            LOG.error("Name parameter is empty!");
        }
        // Возвращаем результат
        return result;
    }

} // end of MyCommonUtils class