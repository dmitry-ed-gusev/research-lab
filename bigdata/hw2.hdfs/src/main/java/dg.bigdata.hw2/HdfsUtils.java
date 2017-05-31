package dg.bigdata.hw2;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * Useful HDFS utilities/methods.
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

    /** Lazy setter for URL handler - to handle hdfs:// protocol. */
    private static void setUrlHandler(Configuration conf) {
        LOG.debug("HdfsUtils.setUrlHandler() is working.");

        // check - if url handler not set - set it
        if (!urlHandlerSet) {
            LOG.info("URL handler isn't set. Setting...");
            URL.setURLStreamHandlerFactory(
                    new FsUrlStreamHandlerFactory(conf == null ? new Configuration() : conf));
            HdfsUtils.urlHandlerSet = true;
        } else {
            LOG.info("URL handler is already set.");
        }

    }

    /**
     * The simplest method of interacting with HDFS via URL class.
     * Depends on URL handler - see static method setUrlHandler().
     */
    public static void readFromHdfsByURL(Configuration conf, OutputStream out, String filePath)
            throws IOException {

        LOG.debug(String.format("HdfsUtils.readFromHdfsByURL() is working. File: [%s].", filePath));

        // fast checks
        if (out == null || StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException(
                    String.format("Output stream is null [%s] or file path is empty [%s]!",
                            (out == null), filePath));
        }

        HdfsUtils.setUrlHandler(conf); // check and set (if necessary) URL handler

        // read bytes from hdfs (input stream) and copy to provided out
        InputStream in = null;
        try {
            in = new URL(filePath).openStream();
            LOG.debug("Input stream opened. Starting bytes copying. Buffer: " + BUFFER_SIZE);
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
            LOG.debug("InputStream to HDFS closed.");
        }

    }

    /**
     * Interaction with HDFS by using FileSystem object directly. Here we
     * don't need URL handler to process files on HDFS.
     * Uses standard java.io.InputStream for reading data.
     */
    public static void readFromHdfsByFS(Configuration conf, OutputStream out, String filePath)
            throws IOException {

        LOG.debug(String.format("HdfsUtils.readFromHdfsByFS() is working. File: [%s].", filePath));

        // fast checks
        if (out == null || StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException(
                    String.format("Output stream is null [%s] or file path is empty [%s]!",
                            (out == null), filePath));
        }

        // create FIleSystem object to read from HDFS
        FileSystem fs = FileSystem.get(URI.create(filePath), conf == null ? new Configuration() : conf);
        InputStream in = null;
        try {
            in = fs.open(new Path(filePath));
            LOG.debug("Input stream opened. Starting bytes copying. Buffer: " + BUFFER_SIZE);
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
            LOG.debug("InputStream to HDFS closed.");
        }

    }

    /***/
    public static void copyFromLocal(Configuration conf, String sourceLocal, String destHdfs) throws IOException {
        LOG.debug(String.format("HdfsUtils.copyFromLocal() is working. Local source [%s], HDFS dest [%s].",
                sourceLocal, destHdfs));

        // fail-fast checks
        if (StringUtils.isBlank(sourceLocal) || StringUtils.isBlank(destHdfs)) {
            throw new IllegalArgumentException(String.format("Empty source [%s] or dest [%s]!", sourceLocal, destHdfs));
        }

        // create FileSystem object, representing HDFS
        FileSystem fs = FileSystem.get(URI.create(destHdfs), conf == null ? new Configuration() : conf);
        InputStream  in  = null;
        OutputStream out = null;

        try {
            // open local source file for reading
            in = new BufferedInputStream(new FileInputStream(sourceLocal));
            // open remote (HDFS) file for writing to (with progress show)
            out = fs.create(new Path(destHdfs), () -> System.out.print("."));
            // copy file from source to dest
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }

    }

    /***/
    public static void copyToLocal(Configuration conf, String sourceHdfs, String destLocal) {
        // todo: implement
        throw new IllegalStateException("Not implemented yet!");
    }

    /***/
    public static void listStatus(String... filePath) throws IOException {
        // todo: fix/implement
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

}
