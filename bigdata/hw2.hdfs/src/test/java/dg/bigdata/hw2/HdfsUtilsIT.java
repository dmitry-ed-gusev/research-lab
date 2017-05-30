package dg.bigdata.hw2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for HdfsUtils class.
 * Created by vinnypuhh on 07.05.17.
 */

public class HdfsUtilsIT {

    // common defaults
    private static final String ENCODING                  = "UTF-8";
    // some Hadoop mini-cluster defaults
    private static final String CLUSTER_DIR_PROPERTY      = "test.build.data";
    private static final String CLUSTER_DIR               = "/tmp";
    private static final String CLUSTER_NAMENODE_HOST     = "localhost";
    private static final int    CLUSTER_NAMENODE_PORT     = 8020;
    private static final int    CLUSTER_NAMENODE_WEB_PORT = 8030;

    // fake files content/paths
    private static final String FILE_1_CONTENT = "Some letters in a file    \n\n\n\n    next line";
    private static final String FILE_1_PATH    = "/mydir/files/file1.zzz";
    private static final String FILE_2_CONTENT = "123\n456\n789";
    private static final String FILE_2_PATH    = "/file2.xxx";

    // instances of cluster/config/filesystem
    private MiniDFSCluster cluster; // use an in-process HDFS cluster for testing
    private Configuration  conf;    // Hadoop configuration
    private FileSystem     fs;      // Hadoop file system object

    /***/
    private static void createFakeFile(String content, String encoding, FileSystem fs, String path) throws IOException {
       try (OutputStream out = fs.create(new Path(path))) {
           out.write(content.getBytes(encoding));
       }
    }

    @Before
    // todo: make it @BeforeClass? (before all tests)
    public void setUp() throws IOException {
        System.setProperty(CLUSTER_DIR_PROPERTY, CLUSTER_DIR); // temporary dir for mini-cluster on local PC

        this.conf = new Configuration(); // create Hadoop configuration
        // init internal mini-cluster
        this.cluster = new MiniDFSCluster.Builder(conf)
                .nameNodePort(CLUSTER_NAMENODE_PORT)
                .nameNodeHttpPort(CLUSTER_NAMENODE_WEB_PORT)
                .build();
        this.fs = this.cluster.getFileSystem(); // get file system from mini-cluster

        // create some fake file in our mini-cluster
        HdfsUtilsIT.createFakeFile(FILE_1_CONTENT, ENCODING, this.fs, FILE_1_PATH);
        HdfsUtilsIT.createFakeFile(FILE_2_CONTENT, ENCODING, this.fs, FILE_2_PATH);
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
    public void testReadHDFSByURL() throws IOException {

        // output byte stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // build a path and read a file from HDFS
        String path = "hdfs://" + CLUSTER_NAMENODE_HOST + ":" + CLUSTER_NAMENODE_PORT;

        // tests
        HdfsUtils.readFromHdfsByURL(this.conf, out, path + FILE_1_PATH);
        assertEquals("Read by URL: should be equals (file1)!", FILE_1_CONTENT, out.toString(ENCODING));

        out.reset();
        HdfsUtils.readFromHdfsByURL(this.conf, out, path + FILE_2_PATH);
        assertEquals("Read by URL: should be equals (file2)!", FILE_2_CONTENT, out.toString(ENCODING));
    }

    @Test
    public void testReadHDFSByFS() throws IOException {

        // output byte stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // build a path and read a file from HDFS
        String path = "hdfs://" + CLUSTER_NAMENODE_HOST + ":" + CLUSTER_NAMENODE_PORT;

        // tests
        HdfsUtils.readFromHdfsByFS(this.conf, out, path + FILE_1_PATH);
        assertEquals("Read by FS: should be equals (file1)!", FILE_1_CONTENT, out.toString(ENCODING));

        out.reset();
        HdfsUtils.readFromHdfsByFS(this.conf, out, path + FILE_2_PATH);
        assertEquals("Read by FS: should be equals (file2)!", FILE_2_CONTENT, out.toString(ENCODING));
    }

    @Test
    public void testCompareRead_URLvsFS() throws IOException {
        // output byte stream
        ByteArrayOutputStream outURL = new ByteArrayOutputStream();
        ByteArrayOutputStream outFS = new ByteArrayOutputStream();

        // build a path and read a file from HDFS
        String path = "hdfs://" + CLUSTER_NAMENODE_HOST + ":" + CLUSTER_NAMENODE_PORT;

        HdfsUtils.readFromHdfsByURL(this.conf, outURL, path + FILE_1_PATH);
        HdfsUtils.readFromHdfsByFS(this.conf, outFS, path + FILE_1_PATH);

        assertTrue("FS vs URL #1: should be equals!", outURL.toString(ENCODING).equals(outFS.toString(ENCODING)));
        assertTrue("FS vs URL #2: should be equals!", outFS.toString(ENCODING).equals(outURL.toString(ENCODING)));
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
