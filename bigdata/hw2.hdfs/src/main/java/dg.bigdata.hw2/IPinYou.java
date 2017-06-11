package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of BigData course HW #2.
 * Created by gusevdm on 5/17/2017.
 */

// todo: see here -> http://stackoverflow.com/questions/14573209/read-a-text-file-from-hdfs-line-by-line-in-mapper
public class IPinYou {

    private static final Log LOG = LogFactory.getLog(IPinYou.class);

    private static final String OPTION_SOURCE   = "-source";
    private static final String OPTION_OUT_FILE = "-outFile";

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
            boolean result = path != null && path.toString().toLowerCase().endsWith(".txt");
            LOG.info(String.format("Path [%s] is accepted [%s].", path, result));
            return result;
        });

        // process files one by one and calculate
        for (FileStatus status : statuses) {
            LOG.info(String.format("Processing path [%s].", status.getPath()));
            // process one of files and calculate


        }


        // write results to
        System.exit(444);

        Map<String, Integer> values = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("c:/temp/bid.20130606.txt"))) {
            String line;
            String id;
            long counter = 0;
            while ((line = br.readLine()) != null) {
                // process the line

                try {
                    id = IPinYou.getIPinYouId(line);
                    int count = values.containsKey(id) ? values.get(id) : 0;
                    values.put(id, count + 1);
                    counter++;

                    if (counter % 100000 == 0) {
                        LOG.info(String.format("Processed: %s", counter));
                    }
                } catch (ParseException e) {
                    LOG.warn("Skipped line, can't parse ID!");
                }

                //System.out.println("===> " + line);
            } // end of while cycle

            LOG.info(String.format("Total processed: %s", counter));

        }

    }

}
