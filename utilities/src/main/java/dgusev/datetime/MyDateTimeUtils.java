package dgusev.datetime;

import dgusev.utils.MyCommonUtils;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gusev.dmitry.jtils.UtilitiesDefaults.DEFAULT_ENCODING;

/** Date & Time common utilities. */

// todo: refactor methods getXXXList() -> create one method!

@CommonsLog
public final class MyDateTimeUtils {

    private static final Log LOGGER = LogFactory.getLog(MyDateTimeUtils.class);

    private MyDateTimeUtils() {}

    /** Check parameters and throw IllegalArgumentException in case of invalid parameters. */
    /*
    private static void checkParameters(Date date, int count, SimpleDateFormat dateFormat) {
        // just reject null value for date/date format and MIN_INT value (we are using Math.abs())
        if (date == null || dateFormat == null || Integer.MIN_VALUE == count) { // fail-fast
            throw new IllegalArgumentException(String.format(
                    "Invalid date [%s], date format [%s] or counter [%s]!", date, dateFormat, count));
        }
    }
    */

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getHoursList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getHoursList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // create list of days dates with specified date/time format
        for (int i = 0; i <= Math.abs(count); i++) { // iterate
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.HOUR_OF_DAY, signum); // shift calendar by days
        }

        return datesList;
    }

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getDatesList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getDatesList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // create list of days dates with specified date/time format
        for (int i = 0; i <= Math.abs(count); i++) { // iterate
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, signum); // shift calendar by days
        }

        return datesList;
    }

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getWeeksStartDatesList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getWeeksStartDatesList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // get date for first day (Monday) of specified week
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, -1);
        }

        // add other dates for first dates of previous weeks
        for (int i = 0; i <= Math.abs(count); i++) {
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.WEEK_OF_YEAR, signum); // shift calendar by weeks
        }

        return datesList;
    }

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getMonthsStartDatesList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getMonthStartDatesList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date and shift to first day of month
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // create list of days dates with specified date/time format
        for (int i = 0; i <= Math.abs(count); i++) { // iterate
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.MONTH, signum); // shift calendar by months
        }

        return datesList;
    }

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getQuartersStartDatesList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getQuartersStartDatesList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date and shift to first day of month
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        // get first month of quarter
        int month = cal.get(Calendar.MONTH);
        while (month % 3 != 0) {
            cal.add(Calendar.MONTH, -1);
            month = cal.get(Calendar.MONTH);
        }

        // create list of dates
        for (int i = 0; i <= Math.abs(count); i++) {
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.MONTH, signum * 3); // shift calendar by quarters
        }

        return datesList;
    }

    /**
     * Rejects null values for Date/SimpleDateFormat and MIN_INT value for count (method uses Math.abs() that
     * has numeric overflow for such value: Math.abs(Integer.MIN_VALUE) = Integer.MIN_VALUE (below zero)).
     */
    public static List<String> getYearsStartDatesList(Date date, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug(String.format("MyDateTimeUtils.getDatesList() is working. Date: [%s], count: [%s], format: [%s].",
                date, count, dateFormat));

        MyDateTimeUtils.checkParameters(date, count, dateFormat); // fail-fast check for input parameters

        List<String> datesList = new ArrayList<>();
        // get sign of counter
        int signum = Integer.signum(count);

        // set specified date and shift to first day of year
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);

        // create list of dates
        for (int i = 0; i <= Math.abs(count); i++) {
            datesList.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.YEAR, signum); // shift calendar by years
        }

        return datesList;
    }

    /***/
    public static List<String> getDatesListBack(Date date, TimePeriodType periodType, int count, SimpleDateFormat dateFormat) {
        LOGGER.debug("MyDateTimeUtils.getDatesListBack() is working.");

        switch (periodType) {
            //case HOUR:    return MyDateTimeUtils.getHoursList(date, count, dateFormat);
            case DAY:     return MyDateTimeUtils.getDatesList(date, count, dateFormat);
            case WEEK:    return MyDateTimeUtils.getWeeksStartDatesList(date, count, dateFormat);
            case MONTH:   return MyDateTimeUtils.getMonthsStartDatesList(date, count, dateFormat);
            case QUARTER: return MyDateTimeUtils.getQuartersStartDatesList(date, count, dateFormat);
            case YEAR:    return MyDateTimeUtils.getYearsStartDatesList(date, count, dateFormat);
            default: throw new IllegalStateException(String.format("Invalid period specified: [%s]!", periodType));
        }

    }

    /***/
    public static List<Date> getDatesList(@NonNull Date baseDate, long count, @NonNull TimePeriodType timePeriodType) {
        LOG.debug("MyDateTimeUtils.getDatesList() is working.");

        List<Date> datesList = new ArrayList<>();
        int signum = Long.signum(count); // get sign value of count

        // set specified date and shift to first day of month
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // create list of days dates with specified date/time format
        for (int i = 0; i <= Math.abs(count); i++) {
            datesList.add(cal.getTime());
            cal.add(Calendar.MONTH, signum); // shift calendar by months
        }

        return datesList;
    }

    /**
     * Generates Map (hierarchical) with TimePeriods.
     * Identification of objects (all identifiers are positive long numbers):
     *  (not implemented yet) - day     -> (len 7-8) -> DDMMYYYY, if day < 10, then DMMYYYY. Examples: 1012016 (1 Jan 2016), 12102005 (12 Oct 2006).
     *  - month   -> (len 5-6) -> MMYYYY, if month < 10, then MYYYY. Examples: 102019 (Oct 2019), 22017 (Feb 2017).
     *  - quarter -> (len 10)  -> Q00000YYYY. Examples: 2000002018 (II quarter 2018), 4000002001 (IV quarter 2001).
     *  - year    -> (len 4)   -> YYYY. Examples: 2001, 2017 (self-explanatory).
     *  These identifiers are used for map keys, identifiers for periods are autogenerated, starting from 1.
     */
    // todo: implement unit tests for generator
    public static Map<Long, TimePeriod> generatePeriods(@NonNull String... years) {
        LOG.debug(String.format("MyDateTimeUtils.generatePeriods() is working. Years: %s", Arrays.toString(years)));

        String[] quartersNames = {"I кв.", "II кв.", "III кв.", "IV кв."};
        String[] monthsNames   = {"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};

        // resulting map with periods
        Map<Long, TimePeriod> periods = new TreeMap<>();

        // temporary counters/identifiers
        int quarterCounter;
        long yearId;
        long quarterId;
        long monthId;
        long yearParentId;
        long quarterParentId;

        int idCounter = 1; // autoincremented ID counter

        // temporary objects storage
        TimePeriod period;
        String monthName;
        String title;
        Date startDate;

        // internal date format for parsing dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (String year : years) { // <- YEARS
                LOG.debug(String.format("Processing year: %s", year));
                // add year period
                yearId = Long.parseLong(year);
                startDate = dateFormat.parse(year + "-01-01");
                // add year to map (year ID - auto generated, ID of year in a map - as described in doc above)
                periods.put(yearId, new TimePeriod(idCounter, year, -1, startDate, TimePeriodType.YEAR));
                // get parent ID for quarters
                yearParentId = idCounter;
                // increment ID counter
                idCounter++;

                quarterCounter = 1;
                for (String quarter : quartersNames) { // <- QUARTERS
                    LOG.debug(String.format("\tProcessing quarter: %s", quarterCounter));
                    // create quarter period
                    quarterId = Long.parseLong(quarterCounter + "00000" + year);
                    title = quarter + " " + year;
                    startDate = dateFormat.parse(year + "-" + (1 + 3 * (quarterCounter - 1)) + "-01");
                    // add period to map (quarter ID - auto generated, ID of quarter in a map - as described in doc above)
                    periods.put(quarterId, new TimePeriod(idCounter, title, yearParentId, startDate, TimePeriodType.QUARTER));
                    quarterParentId = idCounter;
                    idCounter++;

                    for (int monthCounter = 3 * (quarterCounter - 1); monthCounter < 3 * quarterCounter; monthCounter++) { // <- MONTHS
                        LOG.debug(String.format("\t\tProcessing month: %s", monthCounter + 1));
                        monthId = Long.parseLong(monthCounter + 1 + year);
                        monthName = monthsNames[monthCounter];
                        title = monthName + " " + year;
                        startDate = dateFormat.parse(year + "-" + ((monthCounter + 1) > 9 ? (monthCounter + 1) : "0" + (monthCounter + 1)) + "-01");
                        // create period object (period ID - auto generated, ID of period in a map - as described in doc above)
                        period = new TimePeriod(idCounter, title, quarterParentId, startDate, TimePeriodType.MONTH);
                        idCounter++;
                        // add it to resulting map
                        periods.put(monthId, period);
                    } // end of FOR -> MONTHS

                    quarterCounter++; // increase quarters counter after months processing

                } // end of FOR -> QUARTERS

            } // end of FOR -> YEARS

        } catch (ParseException e) { // wrap checked exception into runtime and re-throw exception
            throw new IllegalStateException(String.format("Unexpected internal exception: %s", e));
        }

        return periods;

    } // end of generatePeriods()

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

    /** Shift date to past/future by period units, specified by DatePeriod. */
    public static Date shiftDatetimeByPeriod(@NonNull Date date, int period, @NonNull TimePeriodType timePeriodType) {
        LOG.debug("MyDateTimeUtils.shiftDatetimeByPeriod() is working.");

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (timePeriodType.getCalendarValue() == -1) {
            cal.add(Calendar.MONTH, period * 3); // quarter = 3 months
        } else {
            cal.add(timePeriodType.getCalendarValue(), period);
        }

        return cal.getTime();
    }

    /***/
    public static Map<String, List<String>> readDatesPeriodsFromCSV(@NonNull String csvFile, @NonNull Date baseDate,
                                                                    @NonNull SimpleDateFormat dateFormat) throws IOException {

        LOG.debug("MyDateTimeUtils.readDatesPeriodsFromCSV() is working.");

        // check and fail-fast behaviour
        if (StringUtils.isBlank(csvFile) || !new File(csvFile).exists() || !new File(csvFile).isFile()) {
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

        // todo: merge two cycles - iterating over records and generating dates lists (see FOR below)
        // create CSV file reader (and read the file)
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), DEFAULT_ENCODING))) {
            CSVParser csvParser = new CSVParser(fileReader, csvFormat);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            LOG.info(String.format("Got [%s] record(s) from CSV [%s].", csvRecords.size(), csvFile));

            // todo: add check - record columns count (.size()) and non-empty values for first and last (3rd) columns
            // iterate over records, create instances and fill in resulting list
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
            datesList = MyDateTimeUtils.getDatesListBack(baseDate, entry.getMiddle(), entry.getRight(), dateFormat);

            result.put(name, datesList);

        } // end of FOR -> batches

        return result;
    }

}
