package dg.bigdata.hw2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * Some useful HDFS utilities.
 * Created by vinnypuhh on 30.04.17.
 */
public class HdfsUtils {

    private static final Log LOG = LogFactory.getLog(HdfsUtils.class);

    private static final int BUFFER_SIZE = 4096;

    static {
        // set handler for hdfs:// protocol - it allows to
        // interact with HDFS via URL class
        //LOG.info("STATIC: set URL handler for HDFS protocol.");
        //URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    private static boolean urlHandlerSet = false;

    /**
     * The simplest method of interacting with HDFS via URL class.
     * Depends on URL handler - see static block.
     */
    public static void readFromHdfs(Configuration conf, OutputStream out, String filePath) throws IOException {
        LOG.debug("HdfsUtils.simpleHdfsCat() is working.");

        // fast checks
        if (out == null || StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException(
                    String.format("Output stream is null [%s] or file path is empty [%s]!",
                            (out == null), filePath));
        }

        // check - if url handler not set - set it
        if (!urlHandlerSet) {
            LOG.info("URL handler isn't set. Setting...");
            URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory(conf == null ? new Configuration() : conf));
            HdfsUtils.urlHandlerSet = true;
        }

        // read bytes from hdfs (input stream) and copy to provided out
        InputStream  in = null;
        try {
            in = new URL(filePath).openStream();
            LOG.debug("Input stream opened. Starting bytes copying. Buffer: " + BUFFER_SIZE);
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
        }

    }

    /**
     * Interaction with HDFS by using FileSystem directly.
     * Uses standard java.io.InputStream for reading data.
     */
    public static void hdfsCat(String filePath, Configuration conf) throws IOException {
        //Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(filePath), conf == null ? new Configuration() : conf);
        InputStream in = null;
        try {
            in = fs.open(new Path(filePath));
            System.out.println("========================================================");
            IOUtils.copyBytes(in, System.out, BUFFER_SIZE, false);
            System.out.println("\n========================================================");
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
            IOUtils.copyBytes(in, System.out, BUFFER_SIZE, false);
            in.seek(0); // go back to the start of the file
            IOUtils.copyBytes(in, System.out, BUFFER_SIZE, false);
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
        IOUtils.copyBytes(in, out, BUFFER_SIZE, true);
    }

    /***/
    public static void listStatus(String... filePath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(filePath[0]), conf);
        Path[] paths = new Path[filePath.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = new Path(filePath[i]);
        }
        FileStatus[] status = fs.listStatus(paths);
        Path[] listedPaths = FileUtil.stat2Paths(status);
        for (Path p : listedPaths) {
            System.out.println(p);
        }
    }

    /***/
    public static void main(String[] args) throws Exception {
        //new Configuration()
        //HdfsUtils.hdfsCat(args[0]);
    }

}
