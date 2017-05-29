package gusev.dmitry.jtils.utils;

/**
 * Command line option, used by CmdLine parser.
 * Created by vinnypuhh on 28.05.2017.
 */

// todo: add getDescription() method
// todo: add getHelp(String app_desc) method:
/*
    public static String getHelpText() {
        StringBuilder result = new StringBuilder();

        result.append(APP_DESCRIPTION); // add description

        for (CmdLineOption option : CmdLineOption.values()) { // add options
            result.append(option.getOptionDesc()).append("\n");
        }

        return result.toString();
    }
     */
// todo: add abstract class implementation

public interface CmdLineOption {

    /** Return option name. */
    String getName();
}
