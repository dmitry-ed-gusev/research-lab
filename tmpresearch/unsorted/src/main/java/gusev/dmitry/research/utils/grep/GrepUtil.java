package gusev.dmitry.research.utils.grep;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 15.10.12)
 */

public class GrepUtil {

    private static final String KEY_SEARCH     = "";
    private static final String KEY_INPUT_FILE = "";

    public static void main(String[] args) {

        Log log = LogFactory.getLog(GrepUtil.class);
        log.debug("GREP starting.");

        if (args != null && args.length > 0) {

        } else {
            log.info("Usage: java grep -s <search condition> [-f <input text file>]");
        }
    }

}
