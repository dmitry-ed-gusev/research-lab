package dg.bigdata.hw2;

import dgusev.cmd.CmdLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.Arrays;

/**
 * Main class for HDFS utilities - processing cmd line args,
 * generating configs, invoke needed methods.
 * Created by vinnypuhh on 16.05.17.
 */

// todo: add showing help text/desc

public final class HdfsMain {

    private static final Log LOG = LogFactory.getLog(HdfsMain.class);

    /***/
    public static void main(String[] args) {
        LOG.info(String.format("HdfsMain is starting. Cmd line %s.", Arrays.toString(args)));

        CmdLine cmdLine = new CmdLine(args); // create cmd line object

        if (cmdLine.hasOption(HdfsOption.CAT_FILE_BY_URL)) { // cat by URL
            LOG.debug("CAT file by URL. Processing.");
            String catFile = cmdLine.optionValue(HdfsOption.CAT_FILE_BY_URL);
            if (!StringUtils.isBlank(catFile)) {
                LOG.info(String.format("CAT file [%s] by URL.", catFile));
                try {
                    HdfsUtils.readFromHdfsByURL(new Configuration(), System.out, catFile);
                } catch (IOException e) {
                    LOG.error(e);
                }
            } else {
                LOG.error("File for CAT is empty/null!");
            }
        } else if (cmdLine.hasOption(HdfsOption.CAT_FILE_BY_FS)) { // cat by FS
            LOG.debug("CAT file by FS. Processing.");
            String catFile = cmdLine.optionValue(HdfsOption.CAT_FILE_BY_FS);
            if (!StringUtils.isBlank(catFile)) {
                LOG.info(String.format("CAT file [%s] by FS.", catFile));
                try {
                    HdfsUtils.readFromHdfsByFS(new Configuration(), System.out, catFile);
                } catch (IOException e) {
                    LOG.error(e);
                }
            } else {
                LOG.error("File for CAT is empty/null!");
            }
        } else if (cmdLine.hasOption(HdfsOption.COPY_FROM_LOCAL)) { // copy from local file to hdfs
            LOG.debug("COPY file FROM LOCAL. Processing.");
            String sourceFile = cmdLine.optionValue(HdfsOption.COPY_FROM_LOCAL);
            String destFile   = cmdLine.optionValue(HdfsOption.COPY_DESTINATION);
            if (!StringUtils.isBlank(sourceFile) && !StringUtils.isBlank(destFile)) {
                LOG.info(String.format("Copy [%s] to [%s].", sourceFile, destFile));
                try {
                    HdfsUtils.copyFromLocal(new Configuration(), System.out, sourceFile, destFile);
                } catch (InterruptedException | IOException e) {
                    LOG.error(e);
                }
            } else {
                LOG.error(String.format("Source [%s] or destination [%s] is empty!", sourceFile, destFile));
            }
        } else if (cmdLine.hasOption(HdfsOption.COPY_TO_LOCAL)) { // copy from hdfs file to local
            LOG.debug("COPY file TO LOCAL. Processing.");
            String sourceFile = cmdLine.optionValue(HdfsOption.COPY_TO_LOCAL);
            String destFile   = cmdLine.optionValue(HdfsOption.COPY_DESTINATION);
            if (!StringUtils.isBlank(sourceFile) && !StringUtils.isBlank(destFile)) {
                LOG.info(String.format("Copy [%s] to [%s].", sourceFile, destFile));
                try {
                    HdfsUtils.copyToLocal(new Configuration(), sourceFile, destFile);
                } catch (IOException e) {
                    LOG.error(e);
                }
            } else {
                LOG.error(String.format("Source [%s] or destination [%s] is empty!", sourceFile, destFile));
            }
        } else { // invalid params (no known options present)
            LOG.error("Invalid command line!");
        }

    } // end of MAIN

}
