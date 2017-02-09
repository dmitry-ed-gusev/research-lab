package dg.social.crawler.utilities;

/**
 * Command line options enumeration. Every option contains name and description.
 * Created by gusevdm on 12/28/2016.
 */

public enum CmdLineOption {

    CONFIG_FILE  ("-config",      "-config <config file name>   Config file for SCrawler, file name is mandatory"),
    LOGGER_LEVEL ("-logLevel",    "-logLevel <level>            Set internal logger level, values TRACE/DEBUG/INFO/ERROR/WARN/FATAL"),
    SEARCH_STRING("-search",      "-search <search string>      Search string for simple search. If contains multiple words, use \" for value"),
    OUTPUT_FILE  ("-output",      "-output <file name>          Specify output file name for search results"),
    OUTPUT_FORCE ("-forceOutput", "-forceOutput                 Force overwrite output file, if it exists"),
    HELP         ("-help",        "-help                        Show help and current usage info");

    // application description
    private static final String APP_DESCRIPTION = "\nSocial networks crawler. Dmitrii Gusev, 2017.\n\nUsage/options:\n";

    private String optionName;
    private String optionDesc;

    CmdLineOption(String optionName, String optionDesc) {
        this.optionName = optionName;
        this.optionDesc = optionDesc;
    }

    /** Return help text for option -help or for invalid usage. */
    public static String getHelpText() {
        StringBuilder result = new StringBuilder();

        result.append(APP_DESCRIPTION); // add description

        for (CmdLineOption option : CmdLineOption.values()) { // add options
            result.append(option.getOptionDesc()).append("\n");
        }

        return result.toString();
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionDesc() {
        return optionDesc;
    }

}
