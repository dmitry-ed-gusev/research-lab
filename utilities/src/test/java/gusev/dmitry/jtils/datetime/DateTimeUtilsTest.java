package gusev.dmitry.jtils.datetime;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** Unit tests for Date & Time common utilities. */
public class DateTimeUtilsTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // --- tests for dates list

    @Test (expected = IllegalArgumentException.class)
    public void testGetDatesListNullDate() {
        DateTimeUtils.getDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetDatesListNullDateFormat() {
        DateTimeUtils.getDatesList(new Date(), 9, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetDatesListMinIntegerCount() {
        DateTimeUtils.getDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetDatesList() throws ParseException {
        List<String> expected = Arrays.asList("2000-06-12", "2000-06-11");
        List<String> actual   = DateTimeUtils.getDatesList(DATE_FORMAT.parse("2000-6-12"), -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for weeks start dates list

    @Test (expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListNullDate() {
        DateTimeUtils.getWeeksStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListNullDateFormat() {
        DateTimeUtils.getWeeksStartDatesList(new Date(), 9, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListMinIntegerCount() {
        DateTimeUtils.getWeeksStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetWeeksStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("2018-03-26", "2018-03-19", "2018-03-12", "2018-03-05");
        List<String> actual   = DateTimeUtils.getWeeksStartDatesList(DATE_FORMAT.parse("2018-4-1"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetWeeksStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("2018-03-26", "2018-04-02", "2018-04-09");
        List<String> actual   = DateTimeUtils.getWeeksStartDatesList(DATE_FORMAT.parse("2018-03-30"), 2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for months start dates list

    @Test (expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListNullDate() {
        DateTimeUtils.getMonthsStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListNullDateFormat() {
        DateTimeUtils.getMonthsStartDatesList(new Date(), 9, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListMinIntegerCount() {
        DateTimeUtils.getMonthsStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetMonthsStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("1980-03-01");
        List<String> actual   = DateTimeUtils.getMonthsStartDatesList(DATE_FORMAT.parse("1980-03-11"), 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetMonthsStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("1980-03-01", "1980-02-01", "1980-01-01", "1979-12-01");
        List<String> actual   = DateTimeUtils.getMonthsStartDatesList(DATE_FORMAT.parse("1980-03-11"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for quarters start dates list

    @Test (expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListNullDate() {
        DateTimeUtils.getQuartersStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListNullDateFormat() {
        DateTimeUtils.getQuartersStartDatesList(new Date(), 9, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListMinIntegerCount() {
        DateTimeUtils.getQuartersStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetQuartersStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("2000-10-01", "2000-07-01", "2000-04-01", "2000-01-01");
        List<String> actual   = DateTimeUtils.getQuartersStartDatesList(DATE_FORMAT.parse("2000-12-30"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetQuartersStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("1979-10-01");
        List<String> actual   = DateTimeUtils.getQuartersStartDatesList(DATE_FORMAT.parse("1979-12-30"), 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for years start dates list

    @Test (expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListNullDate() {
        DateTimeUtils.getYearsStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListNullDateFormat() {
        DateTimeUtils.getYearsStartDatesList(new Date(), 9, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListMinIntegerCount() {
        DateTimeUtils.getYearsStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetYearsStartDatesList() throws ParseException {
        List<String> expected = Arrays.asList("2010-01-01", "2009-01-01");
        List<String> actual   = DateTimeUtils.getYearsStartDatesList(DATE_FORMAT.parse("2010-03-3"), -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test (expected = IllegalStateException.class)
    public void testGetDatesListBackForHourPeriod() {
        DateTimeUtils.getDatesListBack(new Date(), TimePeriodType.HOUR, 0, new SimpleDateFormat());
    }

    @Test (expected = IllegalStateException.class)
    public void testGetDatesListBackForMinutePeriod() {
        DateTimeUtils.getDatesListBack(new Date(), TimePeriodType.MINUTE, 0, new SimpleDateFormat());
    }

    @Test (expected = IllegalStateException.class)
    public void testGetDatesListBackForSecondPeriod() {
        DateTimeUtils.getDatesListBack(new Date(), TimePeriodType.SECOND, 0, new SimpleDateFormat());
    }

    @Test
    public void testGetDatesListBackYears() throws ParseException {
        List<String> expected = Arrays.asList("1970-01-01");
        List<String> actual   = DateTimeUtils.getDatesListBack(DATE_FORMAT.parse("1970-07-11"),
                TimePeriodType.YEAR, 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackQuarters() throws ParseException {
        List<String> expected = Arrays.asList("2010-04-01", "2010-01-01");
        List<String> actual   = DateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2010-04-01"),
                TimePeriodType.QUARTER, -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackMonths() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-01", "2000-02-01");
        List<String> actual   = DateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2000-04-10"),
                TimePeriodType.MONTH, -2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackWeeks() throws ParseException {
        List<String> expected = Arrays.asList("2018-08-20", "2018-08-13", "2018-08-06");
        List<String> actual   = DateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2018-08-24"),
                TimePeriodType.WEEK, -2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackDays() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-31", "2000-03-30", "2000-03-29");
        List<String> actual   = DateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2000-04-01"),
                TimePeriodType.DAY, -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

}
