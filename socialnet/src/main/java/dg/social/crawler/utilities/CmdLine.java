package dg.social.crawler.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class represents command line. It supports options with/without values: <br>
 * -option | -option value. If cmd line contents doubled (tripled etc) options with values
 * (-arg1 val1 -arg1 val2) - methods of this class will return first found value.
 * In methods parameters don't omit "-" (minus) option symbol.
 * Created by gusevdm on 28/12/2016.
*/

// todo: immutability (copy ArrayList on instance creation)
public class CmdLine {

    private static final Log LOG = LogFactory.getLog(CmdLine.class); // module logger

    private boolean           isLoggingEnabled = true;              // enable/disable class internal logging
    private ArrayList<String> cmdLine          = new ArrayList<>(); // cmd line internal storage

    /** If args is null, constructor will throw IllegalArgumentException. */
    public CmdLine(String[] args, boolean isLoggingEnabled) {

        this.isLoggingEnabled = isLoggingEnabled;

        if (isLoggingEnabled) {
            LOG.debug(String.format("CmdLine constructor() working. Cmd line: %s.", (args != null ? Arrays.toString(args) : "null")));
        }

        if (args == null) { // fail-fast
            throw new IllegalArgumentException("Empty command line string!");
        }

        this.cmdLine.addAll(Arrays.asList(args));
    }

    /** Check presence of option with "-" (minus) sign. */
    public boolean hasOption(String option) {
        return !cmdLine.isEmpty() && !StringUtils.isBlank(option) && cmdLine.contains(option);
    }

    /** Returns first found value for specified option. In option use "-" (minus) sign. */
    public String optionValue(String option) {

        if (!StringUtils.isBlank(option)) {
            int optionNameIndex = cmdLine.indexOf(option);
            int optionValueIndex = optionNameIndex + 1;
            if ((optionNameIndex >= 0) && (optionValueIndex < cmdLine.size())) {
                String optionValue = cmdLine.get(optionValueIndex);
                if (!StringUtils.isBlank(optionValue) && !optionValue.startsWith("-")) {
                    return optionValue;
                }
            }
        }

        return null;
    }

}