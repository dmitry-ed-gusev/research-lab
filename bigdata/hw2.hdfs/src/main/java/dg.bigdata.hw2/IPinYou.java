package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLine;
import gusev.dmitry.jtils.utils.SortMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static gusev.dmitry.jtils.utils.SortMapUtils.SortType.*;

/**
 * Implementation of BigData course HW #2.
 * Created by gusevdm on 5/17/2017.
 */

public class IPinYou {

    private static final Log LOG = LogFactory.getLog(IPinYou.class);

    private static final int    FILE_PROCESSING_REPORT_STEP = 1_000_000;
    private static final String ENCODING                    = "UTF-8";
    private static final int    BUFFER_SIZE                 = 4096;
    private static final String OPTION_SOURCE               = "-source";
    private static final String OPTION_OUT_FILE             = "-outFile";

    /***/
    public static String getIPinYouId(String recordString) throws ParseException {
        //LOG.debug("IPinYou.getIPinYouId() is working."); // <- too much output

        if (StringUtils.isBlank(recordString)) { // fast-fail
            throw new IllegalArgumentException("Record string is empty or null!");
        }

        String[] recordParts = StringUtils.split(recordString);
        if (recordParts.length < 3) { // check - is there IPinYouIDd
            throw new ParseException("There is no IPinYouId (3rd field)!", 3);
        }

        return "null".equals(recordParts[2]) ? null : recordParts[2];
    }

    /**
     * Return top of map as a string. If map is null/empty - return null.
     * If count <= 0 or >= input map size - return a whole map.
     */
    private static <K, V> String getTopFromMap(Map<K, V> map, int topCount) {
        LOG.debug("IPinYou.getTopFromMap() is working.");

        if (map == null || map.isEmpty()) { // fast checks for map (and return)
            return null;
        }

        int upperBound;
        if (topCount <= 0 || topCount >= map.size()) { // fast checks for count
            upperBound = map.size();
        } else {
            upperBound = topCount;
        }

        int counter = 0;
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry;

        // iterate over map and convert it to string
        while (iterator.hasNext() && counter < upperBound) {
            entry = iterator.next();
            builder.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            counter++;
        }

        return builder.toString();
    }

    /***/
    public static void main(String[] args) throws IOException {
        LOG.info(String.format("IPinYou is starting... Cmd line: %s.", Arrays.toString(args)));

        // fast-fail args count check
        if (args.length < 4) { // -source/<value>/-out/<value> - mandatory
            throw new IllegalArgumentException(
                    String.format("Should be at least 4 cmd line arguments! Cmd line %s.",
                            Arrays.toString(args)));
        }
        LOG.info(String.format("Cmd line length [%s] is OK. Continue.", args.length));

        // get and parse cmd line
        CmdLine cmdLine = new CmdLine(args);
        String sourceHdfsDir = cmdLine.optionValue(OPTION_SOURCE);
        String outputFile    = cmdLine.optionValue(OPTION_OUT_FILE);
        // one more fail-fast consistency check
        if (StringUtils.isBlank(sourceHdfsDir) || StringUtils.isBlank(outputFile)) {
            throw new IllegalArgumentException(
                    String.format("Source HDFS folder [%s] and/or output file [%s] is empty!", sourceHdfsDir, outputFile));
        }
        LOG.info("Cmd line params are checked and are OK.");

        // fast-fail checks: is specified HDFS dir exists and is it a dir?
        FileSystem fs = FileSystem.get(URI.create(sourceHdfsDir), new Configuration());
        Path sourcePath = new Path(sourceHdfsDir);
        FileStatus fstatus = fs.getFileStatus(sourcePath);
        LOG.info("FileSystem/FileStatus objects created OK.");
        if (!fs.exists(sourcePath)) {
            throw new IllegalStateException(String.format("HDFS dir [%s] doesn't exist!", sourceHdfsDir));
        } else if (!fstatus.isDirectory()) {
            throw new IllegalStateException(String.format("HDFS path [%s] isn't a directory!", sourceHdfsDir));
        }
        LOG.info(String.format("HDFS path [%s] exists and is a directory.", sourceHdfsDir));

        // list all text files in a dir (using filter)
        FileStatus[] statuses = fs.listStatus(sourcePath,
                (path) -> {
            boolean result = path != null && path.getName().toLowerCase().startsWith("bid.") &&
                    path.toString().toLowerCase().endsWith(".txt");
            LOG.info(String.format("Path [%s] is accepted [%s].", path, result));
            return result;
        });
        LOG.info(String.format("Total found [%s] file(s).", statuses.length));

        // resulting map with calculation results
        Map<String, Integer> values = new HashMap<>();
        // process files one by one and calculate
        String line;
        String id;
        long   counter;
        for (FileStatus status : statuses) {
            LOG.info(String.format("Processing path [%s].", status.getPath()));

            // process one of files and calculate
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status.getPath())))) {
                counter = 0;
                while((line = br.readLine()) != null) {
                    try {
                        //System.out.println("ID -> " + IPinYou.getIPinYouId(line));
                        id = IPinYou.getIPinYouId(line);
                            int count = values.containsKey(id) ? values.get(id) : 0;
                            values.put(id, count + 1);
                            counter++;

                            if (counter % FILE_PROCESSING_REPORT_STEP == 0) {
                                LOG.info(String.format("Processed: %s", counter));
                            }

                    } catch (ParseException e) {
                        LOG.error(String.format("Can't parse line [%s], skipped!", line), e);
                    }
                } // end of WHILE
                LOG.info(String.format("Total processed for file [%s]: %s", status.getPath(), counter));
            }

            // sort resulting map after current file
            values = SortMapUtils.sortMapByValue(values, DESC);
            LOG.info(String.format("Map sorted after processing [%s].", status.getPath()));
            LOG.info(String.format("Map contains [%s] element(s).", values.size()));
        } // end of FOR

        LOG.info(String.format("[%s] file(s) was/were processed.", statuses.length));
        LOG.info(String.format("Result map contains [%s] element(s).", values.size()));

        // sort resulting map
        //Map<String, Integer> sortedMap = SortMapUtils.sortMapByValue(values, DESC);
        //LOG.info("Result map has been sorted.");

        // get big string from map (TOP 100)
        //String result = IPinYou.getTopFromMap(sortedMap, 100);
        String result = IPinYou.getTopFromMap(values, 100);
        LOG.info("Got TOP100 from sorted map.");

        // write results to file in hdfs
        InputStream  in  = null;
        OutputStream out = null;
        try {
            in  = new BufferedInputStream(new ByteArrayInputStream(result.getBytes(ENCODING)));
            out = fs.create(new Path(outputFile));
            // copy file from source to dest
            IOUtils.copyBytes(in, out, BUFFER_SIZE, false);
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }

        LOG.info(String.format("Data was written to output file [%s].", outputFile));
    }

}
