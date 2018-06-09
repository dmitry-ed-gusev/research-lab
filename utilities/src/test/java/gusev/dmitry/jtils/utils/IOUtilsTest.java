package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class IOUtilsTest {

    @Test
    public void testWriteLongToFile() {
        // todo: implementation!!!
    }

    @Test
    public void testReadLongFromFile() throws IOException {
        assertEquals("Read invalid value!", 234,
                IOUtils.readLongFromFile("src/test/resources/long_value.txt"));
    }

}
