package dg.bigdata.hw2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedExceptionAction;

/**
 * Useful HDFS utilities/methods.
 * Created by vinnypuhh on 30.04.17.
 */

// todo: implement copy multiple files (by dir/by mask/etc)

public class HdfsUtils {

    private static final Log LOG = LogFactory.getLog(HdfsUtils.class);

    private static final String ENCODING    = "UTF-8";
    private static final int    BUFFER_SIZE = 4096;

    static {
        // set handler for hdfs:// protocol - it allows to
        // interact with HDFS via URL class
        //LOG.info("STATIC: set URL handler for HDFS protocol.");
        //URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    private static boolean urlHandlerSet = false;

    /**
     * Lazy setter for URL handler - to handle hdfs:// protocol.
     */
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

    /** Part of implementation - long file copy progress tracker. */
    private static class ProgressTracker implements Progressable {

        private static final int CALLS_STEPS = 200;

        private long               callsCounter = 0;
        private final OutputStream progressOut;

        /***/
        public ProgressTracker(OutputStream progressOut) {
            this.progressOut = progressOut;
        }

        public long getCallsCounter() {
            return this.callsCounter;
        }

        @Override
        public void progress() {

            if (this.progressOut != null) { // if we have output stream to output progress
                this.callsCounter++;

                if (this.callsCounter % CALLS_STEPS == 0) { // step reached
                    try {
                        this.progressOut.write(String.format("progress() calls count: [%s]\n", this.callsCounter).getBytes(ENCODING));
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                }
            }
        }
    }

    /**
     * Method is part of internal implementation and shouldn't be used outside. <- ???
     * It has a package-private access for test purposes.
     * Method has side effect - it closes passed InputStream.
     */
    static void writeToHdfs(Configuration conf, OutputStream progressOut, InputStream inputStream, String destHdfs) throws IOException {
        LOG.debug(String.format("HdfsUtils.writeStringToHdfsFile() is working. HDFS dest [%s].", destHdfs));

        // fail-fast check
        if (inputStream == null || StringUtils.isBlank(destHdfs)) {
            throw new IllegalArgumentException(String.format("Empty HDFS dest [%s] and/or input stream [%s]!", destHdfs, inputStream == null));
        }

        // create FileSystem object, representing HDFS
        FileSystem fs = FileSystem.get(URI.create(destHdfs), conf == null ? new Configuration() : conf);

        OutputStream out = null;
        LOG.debug("FileSystem object created. Processing next.");

        if (progressOut != null) {
            progressOut.write("\nCopy started\n".getBytes(ENCODING));
        }

        ProgressTracker tracker = null;
        try {
            // open remote (HDFS) file for writing to (with progress show)
            Path path = new Path(destHdfs);
            if (progressOut != null) {
                tracker = new ProgressTracker(progressOut);
                out = fs.create(path, tracker);
            } else {
                out = fs.create(path);
            }
            // copy file from source to dest
            IOUtils.copyBytes(inputStream, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(out);
            IOUtils.closeStream(inputStream);
        }

        if (progressOut != null) {
            progressOut.write(String.format("total progress() calls count: [%s]\n", tracker.getCallsCounter()).getBytes(ENCODING));
            progressOut.write("\nCopy finished.\n".getBytes(ENCODING));
        }

    }

    /**
     * @param conf        {@link Configuration} cluster configuration
     * @param progressOut {@link OutputStream} stream to output progress of reading
     * @param sourceLocal {@link String} local source file
     * @param destHdfs    {@link String} destination file on HDFS
     */
    // todo: check - sourcefile exists?
    // todo: check - dest file exists?
    public static void copyFromLocal(Configuration conf, OutputStream progressOut, String username,
                                     String sourceLocal, String destHdfs) throws IOException, InterruptedException {

        LOG.debug(String.format("HdfsUtils.copyFromLocal() is working. Local source [%s], HDFS dest [%s].%s",
                sourceLocal, destHdfs, !StringUtils.isBlank(username) ? String.format(" Operates as user [%s].", username) : ""));

        // fail-fast checks
        if (StringUtils.isBlank(sourceLocal) || StringUtils.isBlank(destHdfs)) {
            throw new IllegalArgumentException(String.format("Empty source [%s] or dest [%s]!", sourceLocal, destHdfs));
        }

        // open local source file for reading
        final InputStream inputStream = new BufferedInputStream(new FileInputStream(sourceLocal));
        // write source input stream to HDFS
        if (!StringUtils.isBlank(username)) { // operates as specified user
            LOG.debug(String.format("Username isn't empty: [%s]. Perform privileged action.", username));
            // create user group
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
            // perform privileged action (in separate thread)
            ugi.doAs((PrivilegedExceptionAction<Void>) () -> {
                HdfsUtils.writeToHdfs(conf, progressOut, inputStream, destHdfs);
                return null;
            });
        } else { // operates as caller user (JVM user)
            HdfsUtils.writeToHdfs(conf, progressOut, inputStream, destHdfs);
        }
    }

    /***/
    public static void copyFromLocal(Configuration conf, OutputStream progressOut, String sourceLocal, String destHdfs)
            throws IOException, InterruptedException {
        HdfsUtils.copyFromLocal(conf, progressOut, null, sourceLocal, destHdfs);
    }

    /***/
    public static void writeStringToHdfs(Configuration conf, String username, String strToWrite, String destHdfs)
            throws IOException, InterruptedException {

        LOG.debug(String.format("HdfsUtils.writeStringToHdfs() is working. String length [%s], HDFS dest [%s].",
                StringUtils.isBlank(strToWrite) ? -1 : strToWrite.length(), destHdfs));

        // fail-fast checks
        if (StringUtils.isBlank(strToWrite) || StringUtils.isBlank(destHdfs)) {
            throw new IllegalArgumentException(String.format("Empty string to write [%s] or dest [%s]!",
                    strToWrite.length(), destHdfs));
        }

        // open local source file for reading
        final InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(strToWrite.getBytes(ENCODING)));
        // write source input stream to HDFS
        if (!StringUtils.isBlank(username)) { // operates as specified user
            LOG.debug(String.format("Username isn't empty: [%s]. Perform privileged action.", username));
            // create user group
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
            // perform privileged action (in separate thread)
            ugi.doAs((PrivilegedExceptionAction<Void>) () -> {
                HdfsUtils.writeToHdfs(conf, null, inputStream, destHdfs);
                return null;
            });
        } else { // operates as caller user (JVM user)
            HdfsUtils.writeToHdfs(conf, null, inputStream, destHdfs);
        }
    }

    /***/
    public static void writeStringToHdfs(Configuration conf, String strToWrite, String destHdfs)
            throws IOException, InterruptedException {
        HdfsUtils.writeStringToHdfs(conf, null, strToWrite, destHdfs);
    }

    /** Read file from HDFS to local storage. */
    // todo: check - source file exists,
    public static void copyToLocal(Configuration conf, String sourceHdfs, String destLocal) throws IOException {
        LOG.debug(String.format("HdfsUtils.copyToLocal() is working. HDFS source [%s], local dest [%s].",
                sourceHdfs, destLocal));

        // fail-fast checks
        if (StringUtils.isBlank(sourceHdfs) || StringUtils.isBlank(destLocal)) {
            throw new IllegalArgumentException(String.format("Empty source [%s] or dest [%s]!", sourceHdfs, destLocal));
        }

        // create FileSystem object, representing HDFS
        FileSystem fs = FileSystem.get(URI.create(sourceHdfs), conf == null ? new Configuration() : conf);
        InputStream in = null;
        OutputStream out = null;
        try {
            // open
            in = fs.open(new Path(sourceHdfs));
            out = new BufferedOutputStream(new FileOutputStream(destLocal));
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }

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
