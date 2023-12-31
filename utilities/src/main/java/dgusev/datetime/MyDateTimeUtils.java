package dgusev.datetime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import dgusev.io.MyCsvUtils;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;

/** Date & Time common utilities. */

@CommonsLog
public final class MyDateTimeUtils {

    private MyDateTimeUtils() {}

    /***/
    public static List<Date> getDatesList(@NonNull Date baseDate, long count, @NonNull TimePeriodType timePeriodType) {
        log.debug("MyDateTimeUtils.getDatesList() is working.");

        List<Date> datesList = new ArrayList<>();
        int signum = Long.signum(count); // get sign value of count

        // set specified date and shift to first day of month
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);

        // units type for adding
        int unitType = timePeriodType.getCalendarValue();

        if (TimePeriodType.WEEK.equals(timePeriodType)) { // special setup for weeks
            // get date for first day (Monday) of specified week
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DATE, -1);
            }
        } else if (TimePeriodType.MONTH.equals(timePeriodType)) { // special setup for months
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else if (TimePeriodType.QUARTER.equals(timePeriodType)) { // special setup for quarters
            cal.set(Calendar.DAY_OF_MONTH, 1);
            // get first month of quarter
            int month = cal.get(Calendar.MONTH);
            while (month % 3 != 0) {
                cal.add(Calendar.MONTH, -1);
                month = cal.get(Calendar.MONTH);
            }
            // correct units/units type value
            signum = signum * 3;
            unitType = Calendar.MONTH;
        } else if (TimePeriodType.YEAR.equals(timePeriodType)) { // special setup for years
            cal.set(Calendar.DAY_OF_YEAR, 1);
        }

        // create list of days dates with specified date/time format
        for (int i = 0; i <= Math.abs(count); i++) {
            datesList.add(cal.getTime());
            cal.add(unitType, signum); // shift calendar
        }

        return datesList;
    }

    /**
     * Generates Map (hierarchical) with TimePeriods. Can be used for OLAP DBs/DWH.
     * Identification of objects (all identifiers are positive long numbers):
     *  (not implemented yet) - day     -> (len 7-8) -> DDMMYYYY, if day < 10, then DMMYYYY. Examples: 1012016 (1 Jan 2016), 12102005 (12 Oct 2006).
     *  - month   -> (len 5-6) -> MMYYYY, if month < 10, then MYYYY. Examples: 102019 (Oct 2019), 22017 (Feb 2017).
     *  - quarter -> (len 10)  -> Q00000YYYY. Examples: 2000002018 (II quarter 2018), 4000002001 (IV quarter 2001).
     *  - year    -> (len 4)   -> YYYY. Examples: 2001, 2017 (self-explanatory).
     *  These identifiers are used for map keys, identifiers for periods are autogenerated, starting from 1.
     */
    public static Map<Long, TimePeriod> generatePeriods(@NonNull String... years) {
        log.debug(String.format("MyDateTimeUtils.generatePeriods() is working. Years: %s", Arrays.toString(years)));

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
                log.debug(String.format("Processing year: %s", year));
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
                    log.debug(String.format("\tProcessing quarter: %s", quarterCounter));
                    // create quarter period
                    quarterId = Long.parseLong(quarterCounter + "00000" + year);
                    title = quarter + " " + year;
                    startDate = dateFormat.parse(year + "-" + (1 + 3 * (quarterCounter - 1)) + "-01");
                    // add period to map (quarter ID - auto generated, ID of quarter in a map - as described in doc above)
                    periods.put(quarterId, new TimePeriod(idCounter, title, yearParentId, startDate, TimePeriodType.QUARTER));
                    quarterParentId = idCounter;
                    idCounter++;

                    for (int monthCounter = 3 * (quarterCounter - 1); monthCounter < 3 * quarterCounter; monthCounter++) { // <- MONTHS
                        log.debug(String.format("\t\tProcessing month: %s", monthCounter + 1));
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

    }

    /** Shift date to past/future by period units, specified by DatePeriod. */
    public static Date shiftDatetimeByPeriod(@NonNull Date date, int period, @NonNull TimePeriodType timePeriodType) {
        log.debug("MyDateTimeUtils.shiftDatetimeByPeriod() is working.");

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (timePeriodType.getCalendarValue() == -1) { // special thing for quarters
            cal.add(Calendar.MONTH, period * 3); // quarter = 3 months
        } else {
            cal.add(timePeriodType.getCalendarValue(), period);
        }

        return cal.getTime();
    }

    /**
     * Reads date periods from CSV file. File should have 3 columns:
     *  column #1 -> name of period (string)
     *  column #2 -> period type (TimePeriodType)
     *  column #3 -> units count for period (integer)
     * Commented lines should start with # (they will be ignored).
     */
    public static Map<String, List<Date>> readDatesPeriodsFromCSV(@NonNull String csvFile, @NonNull Date baseDate) throws IOException {
        log.debug("MyDateTimeUtils.readDatesPeriodsFromCSV() is working.");
        // resulting map -> <name, dates list>
        Map<String, List<Date>> result = new HashMap<>();
        // process csv records list and build resulting map
        MyCsvUtils.getCSVRecordsList(csvFile).forEach(record -> {
                // 0 -> name, 1 -> period type, 2 -> units count
                result.put(record.get(0), MyDateTimeUtils.getDatesList(baseDate, Integer.parseInt(record.get(2)), TimePeriodType.getTypeByName(record.get(1))));
            });
        return result;
    }

    /***/
    // todo: add unit tests!!!
    public static List<Triple<String, List<Date>, TimePeriodType>> readDatesPeriodsFromCSVWithTypes(@NonNull String csvFile, @NonNull Date baseDate) throws IOException {
        log.debug("MyDateTimeUtils.readDatesPeriodsFromCSVWithTypes() is working.");
        // resulting list of triples -> <name, dates list, period type>
        List<Triple<String, List<Date>, TimePeriodType>> result = new ArrayList<>();
        // process csv records list and build resulting list of triples
        MyCsvUtils.getCSVRecordsList(csvFile).forEach(record -> {
            // 0 -> name, 1 -> period type, 2 -> units count
            result.add(new ImmutableTriple<>(record.get(0),
                    MyDateTimeUtils.getDatesList(baseDate, Integer.parseInt(record.get(2)), TimePeriodType.getTypeByName(record.get(1))),
                    TimePeriodType.getTypeByName(record.get(1))));
        });
        return result;
    }

}
