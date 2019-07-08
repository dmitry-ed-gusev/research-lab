package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class MyIOUtilsTest {

    @Test
    public void testWriteLongToFile() {
        // todo: implementation!!!
    }

    @Test
    public void testReadLongFromFile() throws IOException {
        assertEquals("Read invalid value!", 234,
                MyIOUtils.readLongFromFile("src/test/resources/long_value.txt"));
    }

    @Test
    public void testReadCSVFile() throws IOException {
        List<String> sample = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five", "шесть"));
        assertEquals(sample, MyIOUtils.readCSVFile("src/test/resources/csvfile.txt", "UTF-8"));
    }

    private static final String           CSV_CONFIG  = "src/test/resources/csv_config.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDateFormat() throws IOException {
        MyIOUtils.readDatesPeriodsFromCSV("csv file", new Date(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullFile() throws IOException {
        MyIOUtils.readDatesPeriodsFromCSV(null, new Date(), new SimpleDateFormat());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNotExistingFile() throws IOException {
        MyIOUtils.readDatesPeriodsFromCSV("invalid file", new Date(), new SimpleDateFormat());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDate() throws IOException {
        MyIOUtils.readDatesPeriodsFromCSV("invalid file", null, new SimpleDateFormat());
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
        Map<String, List<String>> actual = MyIOUtils.readDatesPeriodsFromCSV(CSV_CONFIG, baseDate, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

}
