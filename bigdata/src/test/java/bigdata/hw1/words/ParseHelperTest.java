package bigdata.hw1.words;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.io.Text;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for utility class ParseHelper.
 * Created by gusevdm on 11/16/2016.
 */

public class ParseHelperTest {

    private static final String MSG_MAX_LENGTH  = "Max length should be [%s]";
    private static final String MSG_WORDS_COUNT = "Words count should be [%s]";
    private static final String MSG_WORDS_ARRAY = "Words array should be [%s]";

    @Test
    public void testParseEmptyRows() {

        // initial data
        List<String> rows = new ArrayList<String>() {{
            add(null);
            add("");
            add("              ");
        }};

        // testing for each initial data row
        rows.forEach(row -> {
            Pair<Integer, List<Text>> resultPair = ParseHelper.parseDataRow(row);
            // testing
            assertTrue(String.format(MSG_MAX_LENGTH, Integer.MIN_VALUE), Integer.MIN_VALUE == resultPair.getLeft());
            assertEquals(String.format(MSG_WORDS_COUNT, 0), 0, resultPair.getRight().size());
        });

    }

    @Test
    public void testParseComplexRow() {

        // source data row
        String row = "one two, three; four!!!! seven.,?! twenty,! 123456";

        // parse source data
        Pair<Integer, List<Text>> resultPair = ParseHelper.parseDataRow(row);

        // testing
        Integer    length = resultPair.getLeft();
        List<Text> words  = resultPair.getRight();
        assertTrue(String.format(MSG_MAX_LENGTH, 6), 6 == length);
        assertEquals(String.format(MSG_WORDS_COUNT, 2), 2, words.size());
        Text[] array = {new Text("twenty"), new Text("123456")};
        assertArrayEquals(String.format(MSG_WORDS_ARRAY, Arrays.toString(array)), array, words.toArray(new Text[0]));
    }

}
