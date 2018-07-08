package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JIOUtilsTest {

    @Test
    public void testWriteLongToFile() {
        // todo: implementation!!!
    }

    @Test
    public void testReadLongFromFile() throws IOException {
        assertEquals("Read invalid value!", 234,
                JIOUtils.readLongFromFile("src/test/resources/long_value.txt"));
    }

}
