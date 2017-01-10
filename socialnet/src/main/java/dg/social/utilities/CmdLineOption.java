package dg.social.utilities;

/**
 * Command line options enumeration.
 * Created by gusevdm on 12/28/2016.
 */

public enum CmdLineOption {

    CONFIG_FILE("-config");

    private String optionName;

    CmdLineOption(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionName() {
        return optionName;
    }

}
