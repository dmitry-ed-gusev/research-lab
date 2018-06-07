package gusevdm.luxms.model;

public enum LuxDataType {
    METRICS       ("metrics",      "metric_id"),
    UNITS         ("units",        "???"),
    LOCATIONS     ("locations",    "loc_id"),
    PERIODS       ("periods",      "period_id"),
    PERIODS_TYPES ("period_types", "???"),
    DATA          ("data",         "???");

    /***/
    private LuxDataType(String tableName, String keyName) {
        this.tableName = tableName;
        this.keyName = keyName;
    }

    private final String tableName;
    private final String keyName;

    public String getTableName() {
        return tableName;
    }

    public String getKeyName() {
        return keyName;
    }
}
