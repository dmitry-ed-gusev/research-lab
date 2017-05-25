package dg.bigdata.hw2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of BigData course HW #2.
 * Created by gusevdm on 5/17/2017.
 */

// todo: see here -> http://stackoverflow.com/questions/14573209/read-a-text-file-from-hdfs-line-by-line-in-mapper
public class IPinYou {

    private static final Log LOG = LogFactory.getLog(IPinYou.class);

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
