package bigdata.hw1.words.option1;

import bigdata.hw1.words.option1.TextArrayWritable;
import bigdata.hw1.words.option1.WordsMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Mapper class.
 * Created by gusevdm on 11/16/2016.
 */

public class WordsMapperTest {

    private static final String MSG_MAP_SIZE    = "Map size should be %s";
    private static final String MSG_MAX_LENGTH  = "Max length should be %s";
    private static final String MSG_WORDS_COUNT = "Words count should be %s";
    private static final String MSG_WORDS_ARRAY = "Words array should be %s";

    private static final String[] EMPTY_ARRAY = {};

    @Test
    public void testParseEmptyRows() {

        // initial data
        List<String> rows = new ArrayList<String>() {{
            add(null);
            add("");
            add("              ");
        }};

        // testing
        rows.forEach(row -> {

            // parse data
            MapWritable resultMap = WordsMapper.parseRow(row);

            // testing the result map size
            assertEquals(String.format(MSG_MAP_SIZE, 1), 1, resultMap.size());

            // parse result map - get all needed elements
            Map.Entry<Writable, Writable> entry      = resultMap.entrySet().iterator().next();
            IntWritable                   length     = (IntWritable) entry.getKey();
            TextArrayWritable wordsArray = (TextArrayWritable) entry.getValue();

            // testing the other parameters
            assertEquals(String.format(MSG_MAX_LENGTH, Integer.MIN_VALUE), Integer.MIN_VALUE, length.get());
            assertEquals(String.format(MSG_WORDS_COUNT, 0), 0, wordsArray.get().length);
            assertArrayEquals(String.format(MSG_WORDS_ARRAY, Arrays.toString(EMPTY_ARRAY)), EMPTY_ARRAY, wordsArray.toStrings());

        });

    }

    @Test
    public void testParseComplexRow() {

        String row = "one two, three; four!!!! seven.,?! twenty,! 123456";

        // parse data
        MapWritable                   resultMap  = WordsMapper.parseRow(row);

        // testing the result map size
        assertEquals(String.format(MSG_MAP_SIZE, 1), 1, resultMap.size());

        // parse result map - get all needed elements
        Map.Entry<Writable, Writable> entry      = resultMap.entrySet().iterator().next();
        IntWritable                   length     = (IntWritable) entry.getKey();
        TextArrayWritable             wordsArray = (TextArrayWritable) entry.getValue();

        // testing the other parameters
        assertEquals(String.format(MSG_MAX_LENGTH, 6), 6, length.get());
        assertEquals(String.format(MSG_WORDS_COUNT, 2), 2, wordsArray.get().length);
        String[] array = {"twenty", "123456"};
        assertArrayEquals(String.format(MSG_WORDS_ARRAY, Arrays.toString(array)), array, wordsArray.toStrings());
    }

}
