package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class represents command line. It supports options with/without values: <br>
 * -option | -option value. If cmd line contents doubled (tripled etc) options with values
 * (-arg1 val1 -arg1 val2) - methods of this class will return first occured value.
 * In methods parameters don't omit "-" (minus) option symbol.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 03.06.13)
*/

public class CommandLine {

    // module logger
    //private Log log = LogFactory.getLog(CommandLine.class);
    // cmd line internal storage (for processing)
    private ArrayList<String> cmdLine = new ArrayList<String>();

    /**
     * If args is null, constructor will throw IllegalArgumentException.
    */
    public CommandLine(String[] args) {
        if (args != null) {
            this.cmdLine.addAll(Arrays.asList(args));
        } else {
            throw new IllegalArgumentException("Empty command line string!");
        }
    }

    /**
     * Check option with "-" (minus) sign.
    */
    public boolean hasOption(String option) {
        boolean result = false;
        if (!cmdLine.isEmpty() && !StringUtils.isBlank(option) && cmdLine.contains(option)) {
            result = true;
        }
        return result;
    }

    /**
     * Returns first found value for specified option. In option use "-" (minus) sign.
    */
    public String optionValue(String option) {
        String result = null;
        if (!StringUtils.isBlank(option)) {
            int optionNameIndex = cmdLine.indexOf(option);
            int optionValueIndex = optionNameIndex + 1;
            if ((optionNameIndex >= 0) && (optionValueIndex < cmdLine.size())) {
                String optionValue = cmdLine.get(optionValueIndex);
                if (!StringUtils.isBlank(optionValue) && !optionValue.startsWith("-")) {
                    result = optionValue;
                }
            }
        }
        return result;
    }

}