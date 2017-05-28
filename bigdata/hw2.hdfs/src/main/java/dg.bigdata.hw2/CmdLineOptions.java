package dg.bigdata.hw2;

/**
 * Cmd line options enumeration for HDFS utility
 * Created by gusevdm on 5/25/2017.
 */
public enum CmdLineOptions {

    CAT_FILE_BY_URL("-catFileByUrl");

    private String optionName;

    CmdLineOptions(String optionName) {
        this.optionName = optionName;
    }

}
