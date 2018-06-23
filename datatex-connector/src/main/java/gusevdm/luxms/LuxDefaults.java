package gusevdm.luxms;

import java.text.SimpleDateFormat;

/** Defaults for LuxMS BI System. */
public final class LuxDefaults {

    // lux model -> period default date format
    public static final SimpleDateFormat LUX_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    // report files (sql and xml)
    public static final String REPORT_SQL_FILE = "report.sql";
    public static final String REPORT_XMS_FILE = "report.xml";

    private LuxDefaults() {}
}
