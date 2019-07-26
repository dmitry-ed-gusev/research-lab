package dg.bigdata.hw2;

import dgusev.cmd.CmdLine;
import gusev.dmitry.utils.MyCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static gusev.dmitry.utils.MyCommonUtils.MapSortType.DESC;

/**
 * Implementation of BigData course HW #2.
 * Created by gusevdm on 5/17/2017.
 */

public class IPinYou {

    private static final Log LOG = LogFactory.getLog(IPinYou.class);

    private static final int    FILE_PROCESSING_REPORT_STEP = 1_000_000;
    private static final int    TOP_COUNT                   = 100;
    private static final String OPTION_PAUSE_BEFORE         = "-pauseBefore";
    private static final String OPTION_HDFS_USER            = "-hdfsUser";
    private static final String OPTION_SOURCE               = "-source";
    private static final String OPTION_OUT_FILE             = "-outFile";
    private static final String OPTION_SKIP_NULLS           = "-skipNulls";

    /**
     * Package-private access - for testing purposes.
     */
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
        String hdfsUser      = cmdLine.optionValue(OPTION_HDFS_USER);
        String pauseBefore   = String.valueOf(cmdLine.hasOption(OPTION_PAUSE_BEFORE));
        String skipNulls     = String.valueOf(cmdLine.hasOption(OPTION_SKIP_NULLS));

        // one more fail-fast consistency check
        if (StringUtils.isBlank(sourceHdfsDir) || StringUtils.isBlank(outputFile)) {
            throw new IllegalArgumentException(
                    String.format("Source HDFS folder [%s] and/or output file [%s] is empty!", sourceHdfsDir, outputFile));
        }

        LOG.info("Cmd line params are checked and are OK.");

        return new HashMap<String, String>() {{
            put(OPTION_PAUSE_BEFORE, pauseBefore);
            put(OPTION_HDFS_USER,    hdfsUser);
            put(OPTION_SOURCE,       sourceHdfsDir);
            put(OPTION_OUT_FILE,     outputFile);
            put(OPTION_SKIP_NULLS,   skipNulls);
        }};
    }

    /***/
    public static void main(String[] args) throws IOException, InterruptedException {
        long firstTimePoint = System.currentTimeMillis(); // initial time measurement

        LOG.info("IPinYou is starting...");
        // init and get config
        Map<String, String> config = IPinYou.initConfig(args);
        String  sourceHdfsDir = config.get(OPTION_SOURCE);
        String  outputFile    = config.get(OPTION_OUT_FILE);
        String  hdfsUser      = config.get(OPTION_HDFS_USER);
        boolean skipNulls     = Boolean.valueOf(config.get(OPTION_SKIP_NULLS));

        long secondTimePoint = System.currentTimeMillis(); // time measurement before key press waiting

        // if set (present) flag -pauseBefore - do a pause before execution (wait for <Enter>)
        if (Boolean.valueOf(config.get(OPTION_PAUSE_BEFORE))) {
            new Scanner(System.in).nextLine();
        }

        long thirdTimePoint = System.currentTimeMillis(); // time measurement after key press

        // Hadoop config
        Configuration hadoopConfig = new Configuration();
        // fast-fail checks: is specified HDFS dir exists and is it a dir?
        FileSystem fs = FileSystem.get(URI.create(sourceHdfsDir), hadoopConfig);
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
                while ((line = br.readLine()) != null) {
                    try {
                        id = IPinYou.getIPinYouId(line); // get id from line

                        if ((id == null && !skipNulls) || (id != null)) { // process id
                            // reduce amount of map lookups
                            count = values.get(id);
                            values.put(id, count == null ? 1 : count + 1);
                        }

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

        LOG.info(String.format("[%s] file(s) was/were processed.\n" +
                "Result map contains [%s] element(s).", statuses.length, values.size()));

        // reduce result map - remove all entries with value = 1
        values = MyCommonUtils.removeFromMapByValue(values, 1);
        LOG.info(String.format("Map reduced (removed all with value 1) and now contains [%s] elements.",
                values == null ? 0 : values.size()));
        // sort resulting map (after reducing)
        Map<String, Integer> sortedMap = MyCommonUtils.sortMapByValue(values, DESC);
        LOG.info("Result map has been sorted.");
        // get big string from map (TOP 100)
        String result = MyCommonUtils.getTopFromMap(sortedMap, TOP_COUNT);
        LOG.info("Got TOP100 from sorted map.");
        // write results to file in hdfs
        HdfsUtils.writeStringToHdfs(hadoopConfig, StringUtils.isBlank(hdfsUser) ? null : hdfsUser, result, outputFile);
        LOG.info(String.format("Data was written to output file [%s].", outputFile));

        long fourthTimeMeasurement = System.currentTimeMillis(); // last time measurement

        long totalTime = (secondTimePoint - firstTimePoint) + (fourthTimeMeasurement - thirdTimePoint);
        LOG.info(String.format("IPinYou calculation takes [%s] seconds.", totalTime / 1000));

    } // end of main()

}
