package gusev.dmitry.research.io;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Some useful I/O utilities and functions.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 29.12.2015)
*/

public final class IOUtils {

    private static final Log log = LogFactory.getLog(IOUtils.class);

    private IOUtils() {} // can't instantiate this class

    /**
     * Rename all found files in path to uppercase. Action isn't recursive.
     */
    // todo: implement!
    public void batchUppercaseFiles(String path) {
        log.debug("IOUtils.batchUppercaseFiles() working");

        if (StringUtils.isBlank(path)) { // check value of input parameter
            throw new IllegalArgumentException("Empty path!");
        }


    }

}