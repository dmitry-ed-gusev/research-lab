package gusev.dmitry.jtils.utils;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for CsvUtils module.
 * Created by Dmitrii_Gusev on 7/24/2017.
 */

// todo: need more tests for overloaded methods and internal behavior

public class CsvUtilsTest {

    @Test
    public void testEmptyStrings() {
        Stream.of(null, "", "    ").forEach(value -> {
            List<String> list = CsvUtils.parseLine(value);
            assertNotNull(list);
            assertTrue(list.isEmpty());
            assertTrue(list.size() == 0);
        });
    }

    @Test
    public void testNoQuotes() {
        String line = "10,AU,Australia";
        List<String> result = CsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testNoQuoteButDoubleQuotesInColumn() throws Exception {
        String line = "10,AU,Aus\"\"tralia";
        List<String> result = CsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testDoubleQuotes() {
        String line = "\"10\",\"AU\",\"Australia\"";
        List<String> result = CsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testDoubleQuotesButDoubleQuotesInColumn() {
        String line = "\"10\",\"AU\",\"Aus\"\"tralia\"";
        List<String> result = CsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testDoubleQuotesButCommaInColumn() {
        String line = "\"10\",\"AU\",\"Aus,tralia\"";
        List<String> result = CsvUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus,tralia"));
    }

    @Test
    public void testCustomSeparator() {
        String line = "10|AU|Australia";
        List<String> result = CsvUtils.parseLine(line, '|');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testCustomSeparatorAndQuote() {
        String line = "'10'|'AU'|'Australia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));
    }

    @Test
    public void testCustomSeparatorAndQuoteButCustomQuoteInColumn() {
        String line = "'10'|'AU'|'Aus|tralia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus|tralia"));
    }

    @Test
    public void testCustomSeparatorAndQuoteButDoubleQuotesInColumn() {
        String line = "'10'|'AU'|'Aus\"\"tralia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));
    }

    @Test
    public void testQuotesStringWithSpaceSeparator() {
        String line = "    zz    xx \"- -\" 555 777 \"-\"     \"Some text   \"   ";
        List<String> result = CsvUtils.parseLine(line, ' ', '"');

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

}