package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Command line option, used by CmdLine parser.
 * Created by vinnypuhh on 28.05.2017.
 */

public interface CmdLineOption {

    /** Return option name. */
    String getName();

    /** Return option description */
    String getDescription();


    /** Return formatted help text: description with options list with some help. */
    static String getHelpText(String description, CmdLineOption[] options) {

        StringBuilder result = new StringBuilder();

        if (!StringUtils.isBlank(description)) { // add description
            result.append(description).append("\n");
        }

        if (options != null) {
            result.append("Usage (options description):\n");
            for (CmdLineOption option : options) { // add options
                result.append(option.getDescription()).append("\n");
            }
        }

        return result.toString();
    }

}
