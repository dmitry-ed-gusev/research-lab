package dg.bigdata.hw3;

import gusev.dmitry.jtils.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MapReduce application for processing web access logs.
 * Created by gusevdm on 6/19/2017.
 */

public class ProcessAccessLogsMain {

    private static final Log LOG = LogFactory.getLog(ProcessAccessLogsMain.class);

    /***/
    private static void readLogs() {
        //UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

    public static void main(String[] args) {
        LOG.info("Starting HW3: access logs processing.");

        CommonUtils.unZipIt("access_logs.zip");
    }

}
