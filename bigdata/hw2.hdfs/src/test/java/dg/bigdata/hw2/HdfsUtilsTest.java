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
    public void testReadFromHdfsEmptyOut() throws IOException {
        HdfsUtils.readFromHdfs(new Configuration(), null, "path");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadFromHdfsNullPath() throws IOException {
        HdfsUtils.readFromHdfs(new Configuration(), new ByteArrayOutputStream(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReadFromHdfsEmptyPath() throws IOException {
        HdfsUtils.readFromHdfs(new Configuration(), new ByteArrayOutputStream(), "   ");
    }

}
