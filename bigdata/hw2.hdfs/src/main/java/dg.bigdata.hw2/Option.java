package dg.bigdata.hw2;

import gusev.dmitry.jtils.utils.CmdLineOption;

/**
 * Cmd line options enumeration for HDFS utility
 * Created by gusevdm on 5/25/2017.
 */
public enum Option implements CmdLineOption {

    CAT_FILE_BY_URL ("-catFileByUrl",  "-catFileByUrl <file url>         Cats specified file from HDFS. File URL is mandatory."),
    CAT_FILE_BY_FS  ("-catFileByFS",   "-catFileByFS <file name>         Cats specified file from HDFS. FIle is mandatory."),
    COPY_FROM_LOCAL ("-copyFromLocal", "-copyFromLocal <local file>      Copy <local file> to HDFS (see option <-destination>)"),
    COPY_TO_LOCAL   ("-copyToLocal",   "-copyToLocal <hdfs file>         Copy <hdfs file> to local disk (see option <-destination>)"),
    COPY_DESTINATION("-destination",   "-destination <local/hdfs file>   Copy destination. Depends on copy direction - " +
            "if copy is from local it's <hdfs file>, if copy is to local - it's <local file>.");

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
