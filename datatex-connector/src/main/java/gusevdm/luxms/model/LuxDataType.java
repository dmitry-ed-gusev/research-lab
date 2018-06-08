package gusevdm.luxms.model;

import gusevdm.luxms.model.elements.LuxLocation;
import gusevdm.luxms.model.elements.LuxMetric;
import gusevdm.luxms.model.elements.LuxUnit;

public enum LuxDataType {
    METRICS       ("metrics",      "metrics.csv",      LuxMetric.FILE_HEADER,   "metric_id"),
    UNITS         ("units",        "units.csv",        LuxUnit.FILE_HEADER,     "???"),
    LOCATIONS     ("locations",    "locations.csv",    LuxLocation.FILE_HEADER, "loc_id"),
    PERIODS       ("periods",      "periods.csv",      null, "period_id"),
    PERIODS_TYPES ("period_types", "period_types.csv", null, "???"),
    DATA          ("data",         "data.csv",         null, "???");

    /***/
    private LuxDataType(String tableName, String csvFileName, String[] csvFileHeader, String idName) {
        this.tableName     = tableName;
        this.csvFileName   = csvFileName;
        this.csvFileHeader = csvFileHeader;
        this.idName        = idName;
    }

    private final String   tableName;
    private final String   csvFileName;
    private final String[] csvFileHeader;
    private final String   idName;

    public String getTableName() {
        return tableName;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public String[] getCsvFileHeader() {
        return csvFileHeader;
    }

    public String getIdName() {
        return idName;
    }


}
