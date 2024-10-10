package dgusev.utils;

import dgusev.io.MyCsvUtils;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Unit tests for CsvUtils module.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// todo: need more tests for overloaded methods and internal behavior

public class MyCsvUtilsTest {

    private static final String CSV_TEXT_FILE  = "src/test/resources/dgusev/utils/csvfile.txt";

    @Test
    public void testEmptyStrings() {
        Stream.of(null, "", "    ").forEach(value -> {
            List<String> list = MyCsvUtils.parseLine(value);
            assertNotNull(list);
            assertTrue(list.isEmpty());
            assertEquals(0, list.size());
        });
    }

    @Test
    public void testNoQuotes() {
        String line = "10,AU,Australia";
        List<String> result = MyCsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testNoQuoteButDoubleQuotesInColumn() throws Exception {
        String line = "10,AU,Aus\"\"tralia";
        List<String> result = MyCsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testDoubleQuotes() {
        String line = "\"10\",\"AU\",\"Australia\"";
        List<String> result = MyCsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testDoubleQuotesButDoubleQuotesInColumn() {
        String line = "\"10\",\"AU\",\"Aus\"\"tralia\"";
        List<String> result = MyCsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testDoubleQuotesButCommaInColumn() {
        String line = "\"10\",\"AU\",\"Aus,tralia\"";
        List<String> result = MyCsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus,tralia"));
    }

    @Test
    public void testCustomSeparator() {
        String line = "10|AU|Australia";
        List<String> result = MyCsvUtils.parseLine(line, '|');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testCustomSeparatorAndQuote() {
        String line = "'10'|'AU'|'Australia'";
        List<String> result = MyCsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testCustomSeparatorAndQuoteButCustomQuoteInColumn() {
        String line = "'10'|'AU'|'Aus|tralia'";
        List<String> result = MyCsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus|tralia"));
    }

    @Test
    public void testCustomSeparatorAndQuoteButDoubleQuotesInColumn() {
        String line = "'10'|'AU'|'Aus\"\"tralia'";
        List<String> result = MyCsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testQuotesStringWithSpaceSeparator() {
        String line = "    zz    xx \"- -\" 555 777 \"-\"     \"Some text   \"   ";
        List<String> result = MyCsvUtils.parseLine(line, ' ', '"');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(7));
        assertThat(result.get(0), is("zz"));
        assertThat(result.get(1), is("xx"));
        assertThat(result.get(2), is("- -"));
        assertThat(result.get(3), is("555"));
        assertThat(result.get(4), is("777"));
        assertThat(result.get(5), is("-"));
        assertThat(result.get(6), is("Some text   "));
    }

    @Test
    public void testReadCSVFile() throws IOException {
        List<String> sample = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five", "шесть"));
        assertEquals(sample, MyCsvUtils.readCSVFile(CSV_TEXT_FILE, "UTF-8"));
    }

}