package dgusev.datetime;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Date & Time common utilities.
 */
public class MyDateTimeUtilsTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // CSV files with dates/times periods
    private static final String CSV_DATES1 = "src/test/resources/dgusev/datetime/csv_dates_periods1.csv";
    private static final String CSV_DATES2 = "src/test/resources/dgusev/datetime/csv_dates_periods2.csv";

    // --- tests for dates list

    @Test(expected = IllegalArgumentException.class)
    public void testGetDatesListNullDate() {
        MyDateTimeUtils.getDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDatesListNullDateFormat() {
        MyDateTimeUtils.getDatesList(new Date(), 9, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDatesListMinIntegerCount() {
        MyDateTimeUtils.getDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetDatesList() throws ParseException {
        List<String> expected = Arrays.asList("2000-06-12", "2000-06-11");
        List<String> actual = MyDateTimeUtils.getDatesList(DATE_FORMAT.parse("2000-6-12"), -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for weeks start dates list

    @Test(expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListNullDate() {
        MyDateTimeUtils.getWeeksStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListNullDateFormat() {
        MyDateTimeUtils.getWeeksStartDatesList(new Date(), 9, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWeeksStartDatesListMinIntegerCount() {
        MyDateTimeUtils.getWeeksStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetWeeksStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("2018-03-26", "2018-03-19", "2018-03-12", "2018-03-05");
        List<String> actual = MyDateTimeUtils.getWeeksStartDatesList(DATE_FORMAT.parse("2018-4-1"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetWeeksStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("2018-03-26", "2018-04-02", "2018-04-09");
        List<String> actual = MyDateTimeUtils.getWeeksStartDatesList(DATE_FORMAT.parse("2018-03-30"), 2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for months start dates list

    @Test(expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListNullDate() {
        MyDateTimeUtils.getMonthsStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListNullDateFormat() {
        MyDateTimeUtils.getMonthsStartDatesList(new Date(), 9, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMonthsStartDatesListMinIntegerCount() {
        MyDateTimeUtils.getMonthsStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetMonthsStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("1980-03-01");
        List<String> actual = MyDateTimeUtils.getMonthsStartDatesList(DATE_FORMAT.parse("1980-03-11"), 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetMonthsStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("1980-03-01", "1980-02-01", "1980-01-01", "1979-12-01");
        List<String> actual = MyDateTimeUtils.getMonthsStartDatesList(DATE_FORMAT.parse("1980-03-11"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for quarters start dates list

    @Test(expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListNullDate() {
        MyDateTimeUtils.getQuartersStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListNullDateFormat() {
        MyDateTimeUtils.getQuartersStartDatesList(new Date(), 9, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetQuartersStartDatesListMinIntegerCount() {
        MyDateTimeUtils.getQuartersStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetQuartersStartDatesList1() throws ParseException {
        List<String> expected = Arrays.asList("2000-10-01", "2000-07-01", "2000-04-01", "2000-01-01");
        List<String> actual = MyDateTimeUtils.getQuartersStartDatesList(DATE_FORMAT.parse("2000-12-30"), -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetQuartersStartDatesList2() throws ParseException {
        List<String> expected = Arrays.asList("1979-10-01");
        List<String> actual = MyDateTimeUtils.getQuartersStartDatesList(DATE_FORMAT.parse("1979-12-30"), 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    // --- tests for years start dates list

    @Test(expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListNullDate() {
        MyDateTimeUtils.getYearsStartDatesList(null, -10, new SimpleDateFormat(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListNullDateFormat() {
        MyDateTimeUtils.getYearsStartDatesList(new Date(), 9, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetYearsStartDatesListMinIntegerCount() {
        MyDateTimeUtils.getYearsStartDatesList(new Date(), Integer.MIN_VALUE, new SimpleDateFormat());
    }

    @Test
    public void testGetYearsStartDatesList() throws ParseException {
        List<String> expected = Arrays.asList("2010-01-01", "2009-01-01");
        List<String> actual = MyDateTimeUtils.getYearsStartDatesList(DATE_FORMAT.parse("2010-03-3"), -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDatesListBackForHourPeriod() {
        MyDateTimeUtils.getDatesListBack(new Date(), TimePeriodType.HOUR, 0, new SimpleDateFormat());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDatesListBackForMinutePeriod() {
        MyDateTimeUtils.getDatesListBack(new Date(), TimePeriodType.MINUTE, 0, new SimpleDateFormat());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDatesListBackForSecondPeriod() {
        MyDateTimeUtils.getDatesListBack(new Date(), TimePeriodType.SECOND, 0, new SimpleDateFormat());
    }

    @Test
    public void testGetDatesListBackYears() throws ParseException {
        List<String> expected = Arrays.asList("1970-01-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(DATE_FORMAT.parse("1970-07-11"),
                TimePeriodType.YEAR, 0, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackQuarters() throws ParseException {
        List<String> expected = Arrays.asList("2010-04-01", "2010-01-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2010-04-01"),
                TimePeriodType.QUARTER, -1, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackMonths() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-01", "2000-02-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2000-04-10"),
                TimePeriodType.MONTH, -2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackWeeks() throws ParseException {
        List<String> expected = Arrays.asList("2018-08-20", "2018-08-13", "2018-08-06");
        List<String> actual = MyDateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2018-08-24"),
                TimePeriodType.WEEK, -2, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackDays() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-31", "2000-03-30", "2000-03-29");
        List<String> actual = MyDateTimeUtils.getDatesListBack(DATE_FORMAT.parse("2000-04-01"),
                TimePeriodType.DAY, -3, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratePeriodsNullYearsList() {
        MyDateTimeUtils.generatePeriods(null);
    }

    @Test
    public void testGeneratePeriodsEmptyYearsList() {
        assertEquals(Collections.EMPTY_MAP, MyDateTimeUtils.generatePeriods());
    }

    @Test
    public void testGeneratePeriods() throws ParseException {
        // execute test method
        Map<Long, TimePeriod> periods = MyDateTimeUtils.generatePeriods("2019");

        System.out.println(periods);

        // prepare sample data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // year
        TimePeriod timePeriodYear = new TimePeriod(1, "2019", -1, dateFormat.parse("2019-01-01"), TimePeriodType.YEAR);

        // quarters
        TimePeriod timePeriodQ1 = new TimePeriod(2, "I кв. 2019", 1, dateFormat.parse("2019-01-01"), TimePeriodType.QUARTER);
        TimePeriod timePeriodQ2 = new TimePeriod(6, "II кв. 2019", 1, dateFormat.parse("2019-04-01"), TimePeriodType.QUARTER);
        TimePeriod timePeriodQ3 = new TimePeriod(10, "III кв. 2019", 1, dateFormat.parse("2019-07-01"), TimePeriodType.QUARTER);
        TimePeriod timePeriodQ4 = new TimePeriod(14, "IV кв. 2019", 1, dateFormat.parse("2019-10-01"), TimePeriodType.QUARTER);

        // months
        TimePeriod timePeriodM1 = new TimePeriod(3, "янв 2019", 2, dateFormat.parse("2019-01-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM2 = new TimePeriod(4, "фев 2019", 2, dateFormat.parse("2019-02-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM3 = new TimePeriod(5, "мар 2019", 2, dateFormat.parse("2019-03-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM4 = new TimePeriod(7, "апр 2019", 6, dateFormat.parse("2019-04-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM5 = new TimePeriod(8, "май 2019", 6, dateFormat.parse("2019-05-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM6 = new TimePeriod(9, "июн 2019", 6, dateFormat.parse("2019-06-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM7 = new TimePeriod(11, "июл 2019", 10, dateFormat.parse("2019-07-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM8 = new TimePeriod(12, "авг 2019", 10, dateFormat.parse("2019-08-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM9 = new TimePeriod(13, "сен 2019", 10, dateFormat.parse("2019-09-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM10 = new TimePeriod(15, "окт 2019", 14, dateFormat.parse("2019-10-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM11 = new TimePeriod(16, "ноя 2019", 14, dateFormat.parse("2019-11-01"), TimePeriodType.MONTH);
        TimePeriod timePeriodM12 = new TimePeriod(17, "дек 2019", 14, dateFormat.parse("2019-12-01"), TimePeriodType.MONTH);

        // tests
        assertEquals(17, periods.entrySet().size());

        assertEquals(timePeriodYear, periods.get(2019L));  // year

        assertEquals(timePeriodQ1, periods.get(1000002019L)); // Q1
        assertEquals(timePeriodQ2, periods.get(2000002019L)); // Q2
        assertEquals(timePeriodQ3, periods.get(3000002019L)); // Q3
        assertEquals(timePeriodQ4, periods.get(4000002019L)); // Q4

        assertEquals(timePeriodM1, periods.get(12019L)); // M1
        assertEquals(timePeriodM2, periods.get(22019L)); // M2
        assertEquals(timePeriodM3, periods.get(32019L)); // M3
        assertEquals(timePeriodM4, periods.get(42019L)); // M4
        assertEquals(timePeriodM5, periods.get(52019L)); // M5
        assertEquals(timePeriodM6, periods.get(62019L)); // M6
        assertEquals(timePeriodM7, periods.get(72019L)); // M7
        assertEquals(timePeriodM8, periods.get(82019L)); // M8
        assertEquals(timePeriodM9, periods.get(92019L)); // M9
        assertEquals(timePeriodM10, periods.get(102019L)); // M10
        assertEquals(timePeriodM11, periods.get(112019L)); // M11
        assertEquals(timePeriodM12, periods.get(122019L)); // M12

    }

    /**
     * Helper method for tests getMonthDateRange().
     */
    private static void dateRangeHelper(int startDelta, int endDelta) {

        Pair<Date, Date> monthRange = MyDateTimeUtils.getMonthDateRange(startDelta, endDelta); // preparing test data
        Calendar testCalendar = GregorianCalendar.getInstance();  // calendar instance for test

        Date currentDate = new Date(); // preparing standard data
        Calendar currentCalendar = GregorianCalendar.getInstance(); // calendar with current (standard) date/time

        // testing start date
        currentCalendar.setTime(currentDate);
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        currentCalendar.add(Calendar.MONTH, startDelta);
        testCalendar.setTime(monthRange.getLeft());
        assertEquals(String.format("Day is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(String.format("Month is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.MONTH));
        assertEquals(String.format("Year is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.YEAR));
        assertEquals(String.format("Hour is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(String.format("Minute is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MINUTE), 0);
        assertEquals(String.format("Second is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.SECOND), 0);
        assertEquals(String.format("Millisecond is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MILLISECOND), 0);

        // testing end date
        currentCalendar.setTime(currentDate);
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        currentCalendar.add(Calendar.MONTH, endDelta);
        testCalendar.setTime(monthRange.getRight());
        assertEquals(String.format("Day is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(String.format("Month is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.MONTH));
        assertEquals(String.format("Year is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.YEAR));
        assertEquals(String.format("Hour is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.HOUR_OF_DAY), 23);
        assertEquals(String.format("Minute is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MINUTE), 59);
        assertEquals(String.format("Second is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.SECOND), 59);
        assertEquals(String.format("Millisecond is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MILLISECOND), 999);
    }

    @Test
    public void testGetMonthDateRange() {
        MyDateTimeUtilsTest.dateRangeHelper(0, 0);
        MyDateTimeUtilsTest.dateRangeHelper(1, 1);
        MyDateTimeUtilsTest.dateRangeHelper(9, 9);
        MyDateTimeUtilsTest.dateRangeHelper(-1, -1);
        MyDateTimeUtilsTest.dateRangeHelper(-13, -13);
        MyDateTimeUtilsTest.dateRangeHelper(0, 2);
        MyDateTimeUtilsTest.dateRangeHelper(4, 0);
        MyDateTimeUtilsTest.dateRangeHelper(-0, -9);
        MyDateTimeUtilsTest.dateRangeHelper(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDateFormat() throws IOException {
        MyDateTimeUtils.readDatesPeriodsFromCSV("csv file", new Date(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullFile() throws IOException {
        MyDateTimeUtils.readDatesPeriodsFromCSV(null, new Date(), new SimpleDateFormat());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNotExistingFile() throws IOException {
        MyDateTimeUtils.readDatesPeriodsFromCSV("invalid file", new Date(), new SimpleDateFormat());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDate() throws IOException {
        MyDateTimeUtils.readDatesPeriodsFromCSV("invalid file", null, new SimpleDateFormat());
    }

    @Test
    public void testReadDatesPeriodsFromCSV() throws IOException, ParseException {

        // fixed base date
        Date baseDate = DATE_FORMAT.parse("2018-08-10");

        // expected result
        Map<String, List<String>> expected = new HashMap<String, List<String>>() {{
            put("name1", Arrays.asList("2018-08-10", "2018-08-11", "2018-08-12", "2018-08-13"));
            put("name2", Arrays.asList("2018-08-01", "2018-07-01", "2018-06-01", "2018-05-01"));
            put("name3", Arrays.asList("2018-01-01", "2017-01-01"));
        }};

        // get actual result
        Map<String, List<String>> actual = MyDateTimeUtils.readDatesPeriodsFromCSV(CSV_DATES1, baseDate, DATE_FORMAT);

        // test/assertion
        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testReadDatesPeriodsFromCSVWithHours() throws ParseException {

        // we use date in ISO 8601 format (https://ru.wikipedia.org/wiki/ISO_8601)
        // see also: https://stackoverflow.com/questions/19112357/java-simpledateformatyyyy-mm-ddthhmmssz-gives-timezone-as-ist

        // setup base date
        SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        FORMAT.setTimeZone(TimeZone.getTimeZone("MSK"));
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date baseDate = FORMAT.parse("2019-03-11T12:01:28+03:00");

        // expected result
        Map<String, List<String>> expected = new HashMap<String, List<String>>() {{
            put("name1", Arrays.asList("2018-08-10", "2018-08-11", "2018-08-12", "2018-08-13"));
            put("name2", Arrays.asList("2018-08-01", "2018-07-01", "2018-06-01", "2018-05-01"));
            put("name3", Arrays.asList("2018-01-01", "2017-01-01"));
        }};

        System.out.println(baseDate);
        System.out.println(FORMAT.format(baseDate));
        System.out.println(new Date());
        System.out.println(FORMAT.format(new Date()));

    }


}
