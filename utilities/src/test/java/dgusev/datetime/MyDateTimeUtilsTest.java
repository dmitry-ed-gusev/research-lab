package dgusev.datetime;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

/** Unit tests for Date & Time common utilities. */

public class MyDateTimeUtilsTest {

    // various date/time formats for testing
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT            = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SIMPLE_DATETIME_FORMAT        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATETIME_FORMAT_WITH_TIMEZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    // CSV files with dates/times periods
    private static final String CSV_DATES1 = "src/test/resources/dgusev/datetime/csv_dates_periods1.csv";
    private static final String CSV_DATES2 = "src/test/resources/dgusev/datetime/csv_dates_periods2.csv";

    @Before
    public void beforeEach() {
        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        TimeZone.setDefault(gmtZone); // set the general time zone before each test

        // set the same time zone for all SDF instances
        SIMPLE_DATE_FORMAT.setTimeZone(gmtZone);
        SIMPLE_DATETIME_FORMAT.setTimeZone(gmtZone);
        DATETIME_FORMAT_WITH_TIMEZONE.setTimeZone(gmtZone);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetDatesList_NEWNullBaseDate() {
        MyDateTimeUtils.getDatesList_NEW(null, -10L, TimePeriodType.YEAR);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetDatesList_NEWNullTimePeriod() {
        MyDateTimeUtils.getDatesList_NEW(new Date(), 2L, null);
    }

    @Test
    public void testGetDatesList_NEWSeconds() throws ParseException {
        // base date for counting
        Date baseDate = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");

        // expected result
        List<Date> expected = Arrays.asList(baseDate,
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:59:59"),
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:59:58"),
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:59:57"));

        // actual test
        assertEquals(expected, MyDateTimeUtils.getDatesList_NEW(baseDate, -3, TimePeriodType.SECOND));
    }

    @Test
    public void testGetDatesList_NEWMinutes() throws ParseException {
        // base date for counting
        Date baseDate = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");

        // expected result
        List<Date> expected = Arrays.asList(baseDate,
                SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:01:00"),
                SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:02:00"));

        // actual test
        assertEquals(expected, MyDateTimeUtils.getDatesList_NEW(baseDate, 2, TimePeriodType.MINUTE));
    }

    @Test
    public void testGetDatesList_NEWHours() throws ParseException {
        // base date for counting
        Date baseDate = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");

        // expected result
        List<Date> expected = Arrays.asList(baseDate,
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:00:00"),
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 22:00:00"),
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 21:00:00"),
                SIMPLE_DATETIME_FORMAT.parse("2018-12-31 20:00:00"));

        // actual test
        assertEquals(expected, MyDateTimeUtils.getDatesList_NEW(baseDate, -4, TimePeriodType.HOUR));
    }

    @Test
    public void testGetDatesList_NEWDays() throws ParseException {
        // base date for counting
        Date baseDate = SIMPLE_DATETIME_FORMAT.parse("2000-06-12 00:00:00");

        // expected
        List<Date> expected = Arrays.asList(baseDate, SIMPLE_DATETIME_FORMAT.parse("2000-06-11 00:00:00"));

        // actual test
        assertEquals(expected, MyDateTimeUtils.getDatesList_NEW(baseDate, -1, TimePeriodType.DAY));
    }

    @Test
    public void testGetDatesList_NEWWeeks() throws ParseException {
        Date baseDate1 = SIMPLE_DATE_FORMAT.parse("2018-4-1");
        List<Date> expected1 = Arrays.asList(
                SIMPLE_DATE_FORMAT.parse("2018-03-26"),
                SIMPLE_DATE_FORMAT.parse("2018-03-19"),
                SIMPLE_DATE_FORMAT.parse("2018-03-12"),
                SIMPLE_DATE_FORMAT.parse("2018-03-05"));

        Date baseDate2 = SIMPLE_DATE_FORMAT.parse("2018-03-30");
        List<Date> expected2 = Arrays.asList(
                SIMPLE_DATE_FORMAT.parse("2018-03-26"),
                SIMPLE_DATE_FORMAT.parse("2018-04-02"),
                SIMPLE_DATE_FORMAT.parse("2018-04-09"));

        assertEquals(expected1, MyDateTimeUtils.getDatesList_NEW(baseDate1, -3, TimePeriodType.WEEK));
        assertEquals(expected2, MyDateTimeUtils.getDatesList_NEW(baseDate2, 2, TimePeriodType.WEEK));
    }

    @Test
    public void testGetDatesList_NEWMonth() throws ParseException {
        Date baseDate1 = SIMPLE_DATE_FORMAT.parse("1980-03-11");
        List<Date> expected1 = Collections.singletonList(SIMPLE_DATE_FORMAT.parse("1980-03-01"));

        Date baseDate2 = SIMPLE_DATE_FORMAT.parse("1980-03-10");
        List<Date> expected2 = Arrays.asList(
                SIMPLE_DATE_FORMAT.parse("1980-03-01"),
                SIMPLE_DATE_FORMAT.parse("1980-02-01"),
                SIMPLE_DATE_FORMAT.parse("1980-01-01"),
                SIMPLE_DATE_FORMAT.parse("1979-12-01"));

        assertEquals(expected1, MyDateTimeUtils.getDatesList_NEW(baseDate1, 0, TimePeriodType.MONTH));
        assertEquals(expected2, MyDateTimeUtils.getDatesList_NEW(baseDate2, -3, TimePeriodType.MONTH));
    }

    @Test
    public void testGetDatesList_NEWQuarters() throws ParseException {
        Date baseDate1 = SIMPLE_DATE_FORMAT.parse("2000-12-30");
        List<Date> expected1 = Arrays.asList(
                SIMPLE_DATE_FORMAT.parse("2000-10-01"),
                SIMPLE_DATE_FORMAT.parse("2000-07-01"),
                SIMPLE_DATE_FORMAT.parse("2000-04-01"),
                SIMPLE_DATE_FORMAT.parse("2000-01-01"));

        Date baseDate2 = SIMPLE_DATE_FORMAT.parse("1979-12-30");
        List<Date> expected2 = Collections.singletonList(SIMPLE_DATE_FORMAT.parse("1979-10-01"));

        assertEquals(expected1, MyDateTimeUtils.getDatesList_NEW(baseDate1, -3, TimePeriodType.QUARTER));
        assertEquals(expected2, MyDateTimeUtils.getDatesList_NEW(baseDate2, 0, TimePeriodType.QUARTER));
    }

    @Test
    public void testGetDatesList_NEWYears() throws ParseException {
        Date baseDate = SIMPLE_DATE_FORMAT.parse("2010-03-3");

        List<Date> expected = Arrays.asList(
                SIMPLE_DATE_FORMAT.parse("2010-01-01"),
                SIMPLE_DATE_FORMAT.parse("2009-01-01"));

        assertEquals(expected, MyDateTimeUtils.getDatesList_NEW(baseDate, -1, TimePeriodType.YEAR));
    }

    @Test
    public void testGetDatesListBackYears() throws ParseException {
        List<String> expected = Arrays.asList("1970-01-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(SIMPLE_DATE_FORMAT.parse("1970-07-11"),
                TimePeriodType.YEAR, 0, SIMPLE_DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackQuarters() throws ParseException {
        List<String> expected = Arrays.asList("2010-04-01", "2010-01-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(SIMPLE_DATE_FORMAT.parse("2010-04-01"),
                TimePeriodType.QUARTER, -1, SIMPLE_DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackMonths() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-01", "2000-02-01");
        List<String> actual = MyDateTimeUtils.getDatesListBack(SIMPLE_DATE_FORMAT.parse("2000-04-10"),
                TimePeriodType.MONTH, -2, SIMPLE_DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackWeeks() throws ParseException {
        List<String> expected = Arrays.asList("2018-08-20", "2018-08-13", "2018-08-06");
        List<String> actual = MyDateTimeUtils.getDatesListBack(SIMPLE_DATE_FORMAT.parse("2018-08-24"),
                TimePeriodType.WEEK, -2, SIMPLE_DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testGetDatesListBackDays() throws ParseException {
        List<String> expected = Arrays.asList("2000-04-01", "2000-03-31", "2000-03-30", "2000-03-29");
        List<String> actual = MyDateTimeUtils.getDatesListBack(SIMPLE_DATE_FORMAT.parse("2000-04-01"),
                TimePeriodType.DAY, -3, SIMPLE_DATE_FORMAT);

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

        // execute test method itself
        Map<Long, TimePeriod> periods = MyDateTimeUtils.generatePeriods("2019");

        // test count of periods
        assertEquals(17, periods.entrySet().size());

        // tests for different periods
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
        Date baseDate = SIMPLE_DATE_FORMAT.parse("2018-08-10");

        // expected result
        Map<String, List<String>> expected = new HashMap<String, List<String>>() {{
            put("name1", Arrays.asList("2018-08-10", "2018-08-11", "2018-08-12", "2018-08-13"));
            put("name2", Arrays.asList("2018-08-01", "2018-07-01", "2018-06-01", "2018-05-01"));
            put("name3", Arrays.asList("2018-01-01", "2017-01-01"));
        }};

        // get actual result
        Map<String, List<String>> actual = MyDateTimeUtils.readDatesPeriodsFromCSV(CSV_DATES1, baseDate, SIMPLE_DATE_FORMAT);

        // test/assertion
        assertEquals("Should be equals!", expected, actual);
    }

    @Test
    public void testReadDatesPeriodsFromCSVWithHours() throws ParseException {

        // we use date in ISO 8601 format (https://ru.wikipedia.org/wiki/ISO_8601)
        // see also: https://stackoverflow.com/questions/19112357/java-simpledateformatyyyy-mm-ddthhmmssz-gives-timezone-as-ist

        // setup base date
        //FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        //TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Date baseDate = DATETIME_FORMAT_WITH_TIMEZONE.parse("2019-03-11T12:01:28+03:00");

        // get current (default) time zone
        TimeZone currentZone = TimeZone.getDefault();
        System.out.println(String.format("Date in current timezone ([%s] / [%s]) -> %s", currentZone.getID(),
                currentZone.getDisplayName(), baseDate));

        // set time zone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        currentZone = TimeZone.getDefault();
        System.out.println(String.format("Date in changed timezone ([%s] / [%s]) -> %s", currentZone.getID(),
                currentZone.getDisplayName(), baseDate));

        TimeZone formatZone = DATETIME_FORMAT_WITH_TIMEZONE.getTimeZone();
        System.out.println(String.format("Date in timezone of SimpleDateFormat ([%s] / [%s]) -> %s", formatZone.getID(),
                formatZone.getDisplayName(), DATETIME_FORMAT_WITH_TIMEZONE.format(baseDate)));

        DATETIME_FORMAT_WITH_TIMEZONE.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        formatZone = DATETIME_FORMAT_WITH_TIMEZONE.getTimeZone();
        System.out.println(String.format("Date in changed timezone of SimpleDateFormat ([%s] / [%s]) -> %s", formatZone.getID(),
                formatZone.getDisplayName(), DATETIME_FORMAT_WITH_TIMEZONE.format(baseDate)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShiftDatetimeByPeriodNullDate() {
        MyDateTimeUtils.shiftDatetimeByPeriod(null, 10, TimePeriodType.DAY);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShiftDatetimeByPeriodNullTimePeriodType() {
        MyDateTimeUtils.shiftDatetimeByPeriod(new Date(), 3, null);
    }

    @Test
    public void testShiftDatetimeByPeriodSecond() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:59:59");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.SECOND));
    }

    @Test
    public void testShiftDatetimeByPeriodMinute() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:59:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.MINUTE));
    }

    @Test
    public void testShiftDatetimeByPeriodHour() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-31 23:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.HOUR));
    }

    @Test
    public void testShiftDatetimeByPeriodDay() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-31 00:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.DAY));
    }

    @Test
    public void testShiftDatetimeByPeriodWeek() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-25 00:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.WEEK));
    }

    @Test
    public void testShiftDatetimeByPeriodMonth() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-12-01 00:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.MONTH));
    }

    @Test
    public void testShiftDatetimeByPeriodQuarter() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-10-01 00:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.QUARTER));
    }

    @Test
    public void testShiftDatetimeByPeriodYear() throws ParseException {
        Date baseDate     = SIMPLE_DATETIME_FORMAT.parse("2019-01-01 00:00:00");
        Date expectedDate = SIMPLE_DATETIME_FORMAT.parse("2018-01-01 00:00:00");

        assertEquals(expectedDate, MyDateTimeUtils.shiftDatetimeByPeriod(baseDate, -1, TimePeriodType.YEAR));
    }

}
