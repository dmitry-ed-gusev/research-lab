package bigdata.hw1.words;

import org.apache.hadoop.io.Text;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TextArrayWritable} implementation.
 * Created by gusevdm on 11/18/2016.
 */

public class TextArrayWritableTest {

    private static final String MSG_RESULT_ARRAY = "Result should be equal to %s";

    @Test
    public void testArraysJoining() {

        // prepare data for testing
        TextArrayWritable array1 = new TextArrayWritable();
        array1.set(new Text[] {new Text("one"), new Text("two")});

        TextArrayWritable array2 = new TextArrayWritable();
        array2.set(new Text[] {new Text("three"), new Text("four")});
        // joined array
        TextArrayWritable result = array1.join(array2);

        // testing
        assertEquals("Length of result should be 4", 4, result.get().length);

        String[] checkArray = {"one", "two", "three", "four"};
        assertArrayEquals(String.format(MSG_RESULT_ARRAY, Arrays.toString(checkArray)), checkArray, result.toStrings());

    }

    @Test
    public void testJoinWithEmpty() {

        // prepare data for testing
        TextArrayWritable array1 = new TextArrayWritable();
        array1.set(new Text[] {new Text("one"), new Text("two")});

        List<TextArrayWritable> emptyArrays = new ArrayList<TextArrayWritable>() {{
            add(new TextArrayWritable());
            add(null);
        }};

        // testing
        final String[] checkArray = {"one", "two"};
        emptyArrays.forEach(array -> {
            TextArrayWritable result = array1.join(array);
            assertEquals("Length of result should be 2", 2, result.get().length);
            assertArrayEquals(String.format(MSG_RESULT_ARRAY, Arrays.toString(checkArray)), checkArray, result.toStrings());
        });

    }



}
