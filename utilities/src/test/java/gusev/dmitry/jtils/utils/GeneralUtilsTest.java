package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

// todo: read file from test resources -> https://stackoverflow.com/questions/3891375/how-to-read-a-text-file-resource-into-java-unit-test

/** Unit tests for GeneralUtils. */
public class GeneralUtilsTest {

    private static final String           CSV_CONFIG  = "src/test/resources/csv_config.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDateFormat() throws IOException {
        GeneralUtils.readDatesPeriodsFromCSV("csv file", new Date(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullFile() throws IOException {
        GeneralUtils.readDatesPeriodsFromCSV(null, new Date(), new SimpleDateFormat());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNotExistingFile() throws IOException {
        GeneralUtils.readDatesPeriodsFromCSV("invalid file", new Date(), new SimpleDateFormat());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadDatesPeriodsFromCSVNullDate() throws IOException {
        GeneralUtils.readDatesPeriodsFromCSV("invalid file", null, new SimpleDateFormat());
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
        Map<String, List<String>> actual = GeneralUtils.readDatesPeriodsFromCSV(CSV_CONFIG, baseDate, DATE_FORMAT);

        assertEquals("Should be equals!", expected, actual);
    }

}
