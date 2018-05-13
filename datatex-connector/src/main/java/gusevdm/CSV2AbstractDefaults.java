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

}
