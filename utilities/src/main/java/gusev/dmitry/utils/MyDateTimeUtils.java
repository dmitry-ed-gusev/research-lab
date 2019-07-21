package gusev.dmitry.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** Date & Time common utilities. */
public final class MyDateTimeUtils {

    private static final Log LOGGER = LogFactory.getLog(MyDateTimeUtils.class);

    private MyDateTimeUtils() {}

    /** Check parameters and throw IllegalArgumentException in case of invalid parameters. */
    private static void checkParameters(Date date, int count, SimpleDateFormat dateFormat) {
        // just reject null value for date/date format and MIN_INT value (we are using Math.abs())
        if (date == null || dateFormat == null || Integer.MIN_VALUE == count) { // fail-fast
            throw new IllegalArgumentException(String.format(
                    "Invalid date [%s], date format [%s] or counter [%s]!", date, dateFormat, count));
        }
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
            case DAY:     return MyDateTimeUtils.getDatesList(date, count, dateFormat);
            case WEEK:    return MyDateTimeUtils.getWeeksStartDatesList(date, count, dateFormat);
            case MONTH:   return MyDateTimeUtils.getMonthsStartDatesList(date, count, dateFormat);
            case QUARTER: return MyDateTimeUtils.getQuartersStartDatesList(date, count, dateFormat);
            case YEAR:    return MyDateTimeUtils.getYearsStartDatesList(date, count, dateFormat);
            default: throw new IllegalStateException(String.format("Invalid period specified: [%s]!", periodType));
        }

    }

}
