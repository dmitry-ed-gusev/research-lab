package gusevdm.helpers;

/** Command-line options enumeration. */
public enum CommandLineOption {

    // general options/keys
    OPTION_HELP               ("help",            "Print help/usage for the tool"),
    OPTION_LOG_LEVEL          ("log-level",       "Logger level (use values: DEBUG/INFO/WARN, case insensitive)"),
    OPTION_ENV_SUFFIX         ("env",             "Environment suffix for configuration"),
    OPTION_CONFIG_FILE        ("config",          "Path to alternate config file"),

    // options for LuxMS system
    OPTION_LUX_LIST_DATASETS  ("listds",          "List all datasets in LuxMS BI Server"),
    OPTION_LUX_CREATE_DATASET ("createds",        "Create dataset (by params) in LuxMS BI Server"),
    OPTION_LUX_DELETE_DATASET ("deleteds",        "Delete dataset (by ID) in LuxMS BI Server"),
    OPTION_LUX_SHOW_TABLE     ("showdstable",     "Show (print) contents of given table for given dataset (<dsId,table>)"),
    OPTION_LUX_IMPORT_DATASET ("importds",        "Import dataset to LuxMS Server DB"),

    // options for DataTex system
    OPTION_DTEX_LIST_TABLES   ("listtables",      "List all tables in DataTex DBMS in a given schema"),
    OPTION_LOAD_DATA_TO_BI    ("loadtobi",        "Load data from DataTex DBMS to LuxMS BI system");

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
