package gusevdm;

/** Container for common configurations and logic of CSV2Abstract. */

public final class CSV2AbstractDefaults {

    /** Default tool locale. */
    public static final String DEFAULT_LOCALE = "UTF-8";

    public static final char   CSV_QUOTECHAR  = '\"';
    public static final char   CSV_SEPARATOR  = ',';
    public static final char   CSV_ESCAPE     = '\n';

    // Common HTTP response/request JSON fields
    public static final String JSON_FIELD_DATAPATH = "datapath";
    public static final String JSON_FIELD_METADATA = "metadata";
    public static final String JSON_FIELD_RESULT   = "result";
    public static final String JSON_FIELD_TABLE    = "table";
    public static final String JSON_FIELD_VALUE    = "value";

    // Abstract dataset states
    public static final String STATE_AWAITING_METADATA = "awaiting_metadata";
    public static final String STATE_INDEXING          = "indexing";
    public static final String STATE_INDEXED           = "indexed";
    public static final String STATE_FAILED            = "failed";

    private CSV2AbstractDefaults() {
    }

    /** Command-line options enumeration. */
    public enum CommandLineOption {
        OPTION_HELP           ("help",            "Print help/usage for the tool."),
        OPTION_DATASET        ("dataset",         "Qualified name of Abstract dataset to be published"),
        OPTION_CSV            ("csv",             "HDFS path to CSV file"),
        OPTION_SCHEMA         ("schema",          "HDFS path to JSON schema file"),
        OPTION_REINDEX        ("reindex",         "(Re)index Abstract collection"),
        OPTION_LOG_LEVEL      ("log-level",       "Logger level (use values: DEBUG/INFO/WARN, case insensitive)"),
        CREDENTIALS           ("credentials",     "Path to enigma credentials file");

        private final String name;
        private final String description;

        CommandLineOption(String paramName, String paramDesc) {
            this.name = paramName;
            this.description = paramDesc;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /** Command-line exit statuses enumeration. */
    public enum ExitStatus {
        OK(0), GENERAL_ERROR(1), MISUSE(2);

        private final int value;

        ExitStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
