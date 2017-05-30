package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLineOption;

/**
 * Cmd line options enumeration for HDFS utility
 * Created by gusevdm on 5/25/2017.
 */
public enum Option implements CmdLineOption {

    CAT_FILE_BY_URL("-catFileByUrl", "-catFileByUrl <file url>   Cats specified file from HDFS. File URL is mandatory."),
    CAT_FILE_BY_FS("-catFileByFS",   "-catFileByFS <file name>   Cats specified file from HDFS. FIle is mandatory.");

    private String optionName;
    private String optionDesc;

    Option(String optionName, String optionDesc) {
        this.optionName = optionName;
        this.optionDesc = optionDesc;
    }

    @Override
    public String getName() {
        return this.optionName;
    }

    @Override
    public String getDescription() {
        return this.optionDesc;
    }

}
