package dgusev.io;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class MyIOUtilsTest {

    // other test text files
    private static final String           LONG_TEXT_FILE = "src/test/resources/gusev/dmitry/utils/long_value.txt";
    private static final String           TEXT_FILE1     = "src/test/resources/gusev/dmitry/utils/text_file1.txt";
    private static final String           TEXT_FILE2     = "src/test/resources/gusev/dmitry/utils/text_file2.txt";
    private static final String           TEXT_FILE3     = "src/test/resources/gusev/dmitry/utils/text_file3.txt";

    private static final SimpleDateFormat DATE_FORMAT    = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testWriteLongToFile() {
        // todo: implementation!!!
    }

    @Test
    public void testReadLongFromFile() throws IOException {
        assertEquals("Read invalid value!", 234, MyIOUtils.readLongFromFile(LONG_TEXT_FILE));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadStringFromFileNullFile() throws IOException {
        MyIOUtils.readStringFromFile(null);
    }

    @Test (expected = FileNotFoundException.class)
    public void testReadStringFromFileEmptyFile() throws IOException {
        MyIOUtils.readStringFromFile("   ");
    }

    @Test (expected = FileNotFoundException.class)
    public void testReadStringFromFileNonExistingFile() throws IOException {
        MyIOUtils.readStringFromFile(" non_existing_file.txt   ");
    }

    @Test
    public void testReadStringFromFile1() throws IOException {
        String expected = "This is a string from file1...\n";
        String result = MyIOUtils.readStringFromFile(TEXT_FILE1);

        assertEquals(expected, result);
    }

    @Test
    public void testReadStringFromFile2() throws IOException {
        String expected = "\n\n\nString from file2\n";
        String result   = MyIOUtils.readStringFromFile(TEXT_FILE2);

        assertEquals(expected, result);
    }

    @Test
    public void testReadStringFromFile3() throws IOException {
        String expected = "\n\nsssss\n\n\nString from file3\n\n";
        String result   = MyIOUtils.readStringFromFile(TEXT_FILE3);

        assertEquals(expected, result);
    }

    @Test
    public void testWriteStringToFile() {
        // todo: implementation
    }

}
