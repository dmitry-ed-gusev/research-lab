package gusevdm.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Config for one report. */
public class ReportConfig {

    private static final String REPORT_SQL_FILE        = "sql_file";
    private static final String REPORT_YEARS           = "years";
    private static final String REPORT_LOCATION_COLUMN = "location_title_column";

    private static final List<String> REPORT_PROPERTIES = Arrays.asList(
            REPORT_SQL_FILE,
            REPORT_YEARS,
            REPORT_LOCATION_COLUMN
    );

    // report config
    private Map<String, String> config = null;


}
