package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * Main class for HDFS utilities - processing cmd line args,
 * generating configs, invoke needed methods.
 * Created by vinnypuhh on 16.05.17.
 */

// todo: implement class
// todo: add showing help text/desc

public final class HdfsMain {

    private static final Log LOG = LogFactory.getLog(HdfsMain.class);

    /***/
    public static void main(String[] args) {
        LOG.info("HdfsMain is starting...");

        CmdLine cmdLine = new CmdLine(args);

        String catFile = cmdLine.optionValue(Option.CAT_FILE_BY_URL);
        if (!StringUtils.isBlank(catFile)) {
            LOG.info(String.format("CAT file [%s] by URL.", catFile));
            try {
                HdfsUtils.readFromHdfsByURL(new Configuration(), System.out, catFile);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    } // end of MAIN

}
