package gusevdm.luxms;

public enum LuxMSDataType {
    METRICS       ("metrics",      "metric_id"),
    UNITS         ("units",        "???"),
    LOCATIONS     ("locations",    "loc_id"),
    PERIODS       ("periods",      "period_id"),
    PERIODS_TYPES ("period_types", "???"),
    DATA          ("data",         "???");

    /***/
    private LuxMSDataType(String tableName, String keyName) {
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
