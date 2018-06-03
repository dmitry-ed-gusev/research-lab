package gusevdm.helpers;

/** Command-line options enumeration. */
public enum CommandLineOption {
    OPTION_HELP           ("help",            "Print help/usage for the tool"),
    //OPTION_DATASET        ("dataset",         "Qualified name of Abstract dataset to be published"),
    //OPTION_CSV            ("csv",             "HDFS path to CSV file"),
    //OPTION_SCHEMA         ("schema",          "HDFS path to JSON schema file"),
    //OPTION_REINDEX        ("reindex",         "(Re)index Abstract collection"),
    OPTION_LOG_LEVEL      ("log-level",       "Logger level (use values: DEBUG/INFO/WARN, case insensitive)"),
    OPTION_LIST_DATASETS  ("listds",          "List all datasets in LuxMS BI server"),
    OPTION_LIST_TABLES    ("listtables",      "List all tables in DataTex DBMS in a given schema"),
    OPTION_ENV_SUFFIX     ("env",             "Environment suffix for configuration"),
    OPTION_CONFIG_FILE    ("config",          "Path to alternate config file");

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
