package dg.bigdata.hw2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * HDFS utility.
 * Created by vinnypuhh on 30.04.17.
 */
public class HdfsUtils {

    static {
        // set handler for hdfs:// protocol - it allows to
        // interact with HDFS via URL class
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    /**
     * The simplest method of interacting with HDFS via URL class.
     * Depends on URL handler - see static block.
     */
    public static void SimpleHdfsCat(String filePath) throws IOException {
        InputStream in = null;
        try {
            in = new URL(filePath).openStream();
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }
    }

    /**
     * Interaction with HDFS by using FileSystem directly.
     * Uses standard java.io.InputStream for reading data.
     */
    public static void HdfsCat(String filePath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(filePath), conf);
        InputStream in = null;
        try {
            in = fs.open(new Path(filePath));
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }

    }

    /**
     * Interaction with HDFS by using FIleSystem directly.
     * Uses HDFS specific FSDataInputStream for reading data.
     */
    public static void HdfsCatTwice(String filePath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(filePath), conf);
        FSDataInputStream in = null;
        try {
            in = fs.open(new Path(filePath));
            IOUtils.copyBytes(in, System.out, 4096, false);
            in.seek(0); // go back to the start of the file
            IOUtils.copyBytes(in, System.out, 4096, false);
        } finally {
            IOUtils.closeStream(in);
        }
    }

    /***/
    public static void fileCopyWithProgress(String source, String dest) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(source));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dest), conf);
        OutputStream out = fs.create(new Path(dest), new Progressable() {
            public void progress() {
                System.out.print(".");
            }
        });
        IOUtils.copyBytes(in, out, 4096, true);
    }

    /***/
    public static void main(String[] args) throws Exception {

    }

}
