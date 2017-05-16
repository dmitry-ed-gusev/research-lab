package dg.bigdata.hw2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for HdfsUtils class.
 * Created by vinnypuhh on 07.05.17.
 */

public class HdfsUtilsIT {

    // some Hadoop mini-cluster defaults
    private static final int CLUSTER_PORT = 8020;
    //
    private MiniDFSCluster cluster; // use an in-process HDFS cluster for testing
    private Configuration  conf;    // Hadoop configuration
    private FileSystem     fs;      // Hadoop file system object

    @Before
    // todo: make it @BeforeClass? (before all tests)
    public void setUp() throws IOException {

        // create Hadoop configuration
        this.conf = new HdfsConfiguration();
        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }

        // init internal mini-cluster
        this.cluster = new MiniDFSCluster.Builder(conf)
                .nameNodePort(CLUSTER_PORT)
                .build();

        // get file system from mini-cluster
        this.fs = this.cluster.getFileSystem();

        // todo: add more fake files to file system for tests
        // create a file with content
        OutputStream out = this.fs.create(new Path("/dir/file"));
        out.write("content".getBytes("UTF-8"));
        out.close();
    }

    @After
    // todo: make it @AfterClass? (after all tests)
    public void tearDown() throws IOException {
        if (this.fs != null) {
            this.fs.close();
        }
        if (this.cluster != null) {
            this.cluster.shutdown();
        }
    }

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HdfsUtils.readFromHdfs(this.conf, out, "hdfs:///dir/file");

        System.out.println("===> " + out.toString("UTF-8"));
    }

    /*
    @Test(expected = FileNotFoundException.class)
    public void throwsFileNotFoundForNonExistentFile() throws IOException {
        fs.getFileStatus(new Path("no-such-file"));
    }

    @Test
    public void fileStatusForFile() throws IOException {
        Path file = new Path("/dir/file");
        FileStatus stat = fs.getFileStatus(file);
        assertThat(stat.getPath().toUri().getPath(), is("/dir/file"));
        assertThat(stat.isDirectory(), is(false));
        assertThat(stat.getLen(), is(7L));
        assertThat(stat.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        assertThat(stat.getReplication(), is((short) 1));
        assertThat(stat.getBlockSize(), is(128 * 1024 * 1024L));
        assertThat(stat.getOwner(), is(System.getProperty("user.name")));
        assertThat(stat.getGroup(), is("supergroup"));
        assertThat(stat.getPermission().toString(), is("rw-r--r--"));
    }

    @Test
    public void fileStatusForDirectory() throws IOException {
        Path dir = new Path("/dir");
        FileStatus stat = fs.getFileStatus(dir);
        assertThat(stat.getPath().toUri().getPath(), is("/dir"));
        assertThat(stat.isDirectory(), is(true));
        assertThat (stat.getLen(), is(0L));
        assertThat(stat.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        assertThat(stat.getReplication(), is((short) 0));
        assertThat(stat.getBlockSize(), is(0L));
        assertThat(stat.getOwner(), is(System.getProperty("user.name")));
        assertThat(stat.getGroup(), is("supergroup"));
        assertThat(stat.getPermission().toString(), is("rwxr-xr-x"));
    }
*/

}
