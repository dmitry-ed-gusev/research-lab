package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLineOption;

/**
 * Cmd line options enumeration for HDFS utility
 * Created by gusevdm on 5/25/2017.
 */
public enum CmdLineOptions implements CmdLineOption {

    CAT_FILE_BY_URL("-catFileByUrl");

    private String optionName;

    CmdLineOptions(String optionName) {
        this.optionName = optionName;
    }

    @Override
    public String getName() {
        return this.optionName;
    }

}
