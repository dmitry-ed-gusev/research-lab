package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class represents command line. It supports options with/without values: <br>
 * -option | -option value. If cmd line contents doubled (tripled etc) options with values
 * (-arg1 val1 -arg1 val2) - methods of this class will return first found value.
 * In methods parameters don't omit "-" (minus) option symbol.
 * Created by gusevdm on 28/12/2016.
*/

 // todo: use CmdLineOption enum?

public class CmdLine {

    private static final Log LOG = LogFactory.getLog(CmdLine.class); // module logger

    private static final String CMD_LINE_OPTION_PREFIX = "-";

    private List<String> cmdLine = new LinkedList<>(); // cmd line internal storage

    /** If args are null, constructor will throw IllegalArgumentException. */
    public CmdLine(String[] args) {
        LOG.debug(String.format("CmdLine constructor() working. Cmd line: %s.",
                (args != null ? Arrays.toString(args) : "null")));

        if (args == null) { // fail-fast
            throw new IllegalArgumentException("Empty command line string!");
        }

        for (String arg : args) { // filter cmd line arguments
            if (StringUtils.isBlank(arg) || StringUtils.isBlank(arg.replaceAll(CMD_LINE_OPTION_PREFIX, ""))) {
                throw new IllegalArgumentException(String.format("Invalid cmd line argument: [%s]!", arg));
            } else {
                this.cmdLine.add(StringUtils.trimToEmpty(arg));
            }
        }

    }

    /** Check presence of option with "-" (minus) sign. */
    public boolean hasOption(String option) {
        LOG.debug(String.format("CmdLine.hasOption() is working. Option to check: [%s].", option));
        boolean result = !this.cmdLine.isEmpty() &&
                !StringUtils.isBlank(option) && this.cmdLine.contains(option);
        LOG.debug(String.format("Option [%s] check result [%s].", option, result));
        return result;
    }

    /** Returns first found value for specified option. In option use "-" (minus) sign. */
    public String optionValue(String option) {
        LOG.debug(String.format("CmdLine.optionValue() is working. Option to check value: [%s].", option));
        String result = null;
        if (!this.cmdLine.isEmpty() && !StringUtils.isBlank(option)) {
            int optionNameIndex  = this.cmdLine.indexOf(option);
            int optionValueIndex = optionNameIndex + 1;
            if ((optionNameIndex >= 0) && (optionValueIndex < this.cmdLine.size())) {
                String optionValue = this.cmdLine.get(optionValueIndex);
                if (!StringUtils.isBlank(optionValue) && !optionValue.startsWith("-")) {
                    result = optionValue;
                }
            }
        }

        LOG.debug(String.format("For option [%s] found value [%s].", option, result));
        return result;
    }

}