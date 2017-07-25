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

public class CsvUtilsTest {

    private static final String STR = "ip1 - - [24/Apr/2011:04:06:01 -0400] \"GET /~strabal/grease/photo9/927-3.jpg HTTP/1.1\" 200 40028 \"-\" \"Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)\"";

    @Test
    public void testEmptyString() {
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
    public void test_custom_separator() {

        String line = "10|AU|Australia";
        List<String> result = CsvUtils.parseLine(line, '|');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));

    }

    @Test
    public void test_custom_separator_and_quote() {

        String line = "'10'|'AU'|'Australia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));

    }

    @Test
    public void test_custom_separator_and_quote_but_custom_quote_in_column() {

        String line = "'10'|'AU'|'Aus|tralia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus|tralia"));

    }

    @Test
    public void test_custom_separator_and_quote_but_double_quotes_in_column() {

        String line = "'10'|'AU'|'Aus\"\"tralia'";
        List<String> result = CsvUtils.parseLine(line, '|', '\'');

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));

    }

    @Test
    public void test() {
        String line = "    zz    xx \"- -\" 555 777 \"-\"     \"Some text   \"   ";
        List<String> result = CsvUtils.parseLine(line, ' ', '"');

        System.out.println(result.size());
        System.out.println(result);
    }
}