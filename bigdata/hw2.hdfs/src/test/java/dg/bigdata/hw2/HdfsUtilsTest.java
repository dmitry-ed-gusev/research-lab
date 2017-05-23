package dg.bigdata.hw2;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Unit tests for {@link HdfsUtils} class.
 * Created by gusevdm on 5/16/2017.
 */
public class HdfsUtilsTest {

    @Test (expected = IllegalArgumentException.class)
    public void testReadFromHdfsByURLEmptyOut() throws IOException {
        HdfsUtils.readFromHdfsByURL(new Configuration(), null, "path");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadFromHdfsByURLNullPath() throws IOException {
        HdfsUtils.readFromHdfsByURL(new Configuration(), new ByteArrayOutputStream(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadFromHdfsByURLEmptyPath() throws IOException {
        HdfsUtils.readFromHdfsByURL(new Configuration(), new ByteArrayOutputStream(), "   ");
    }

}
