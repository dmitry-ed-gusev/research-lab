package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLine;
import gusev.dmitry.jtils.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static gusev.dmitry.jtils.utils.MapUtils.SortType.DESC;

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
    static String getIPinYouId(String recordString) throws ParseException {
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

    /** Init and return config map. */
    private static Map<String, String> initConfig(String[] args) {
        LOG.info(String.format("IPinYou.initConfig() is working. Cmd line: %s.", Arrays.toString(args)));

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

        return new HashMap<String, String>() {{
            put(OPTION_SOURCE, sourceHdfsDir);
            put(OPTION_OUT_FILE, outputFile);
        }};
    }

    /***/
    public static void main(String[] args) throws IOException {
        LOG.info("IPinYou is starting...");

        // init and get config
        Map<String, String> config = IPinYou.initConfig(args);
        String sourceHdfsDir = config.get(OPTION_SOURCE);
        String outputFile    = config.get(OPTION_OUT_FILE);

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
        // process files one by one and calculate count for each ID
        String line;
        String id;
        Integer count;
        long   counter;
        for (FileStatus status : statuses) {
            LOG.info(String.format("Processing path [%s].", status.getPath()));

            // process one of files and calculate
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status.getPath())))) {
                counter = 0;
                while((line = br.readLine()) != null) {
                    try {
                        id = IPinYou.getIPinYouId(line);
                            // reduce amount of map lookups
                            count = values.get(id);
                            values.put(id, count == null ? 1 : count + 1);
                            counter++;
                            // debug progress output
                            if (counter % FILE_PROCESSING_REPORT_STEP == 0) {
                                LOG.info(String.format("Processed: %s", counter));
                            }

                    } catch (ParseException e) {
                        LOG.error(String.format("Can't parse line [%s], skipped!", line), e);
                    }
                } // end of WHILE
                LOG.info(String.format("Total processed for file [%s]: %s", status.getPath(), counter));
            } // end of TRY

            LOG.info(String.format("Map contains [%s] element(s).", values.size()));
        } // end of FOR

        LOG.info(String.format("[%s] file(s) was/were processed.", statuses.length));
        LOG.info(String.format("Result map contains [%s] element(s).", values.size()));

        // reduce result map - remove all entries with value = 1
        values = MapUtils.removeFromMapByValue(values, 1);
        LOG.info(String.format("Map reduced and now contains [%s] elements.", values.size()));

        // sort resulting map (after reducing)
        Map<String, Integer> sortedMap = MapUtils.sortMapByValue(values, DESC);
        LOG.info("Result map has been sorted.");

        // get big string from map (TOP 100)
        String result = MapUtils.getTopFromMap(sortedMap, 100);
        LOG.info("Got TOP100 from sorted map.");

        // write results to file in hdfs
        // todo: move to HdfsUtils (writeStringToHdfsFile())?
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

    } // end of main()

}
