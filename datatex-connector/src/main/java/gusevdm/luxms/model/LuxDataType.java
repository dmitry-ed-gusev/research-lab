package gusevdm.luxms.model;

import gusevdm.luxms.model.elements.*;
import org.apache.commons.lang3.StringUtils;

public enum LuxDataType {
    // order of elements is important
    UNITS     ("units",     "units.csv",     LuxUnit.FILE_HEADER,      "???"),
    METRICS   ("metrics",   "metrics.csv",   LuxMetric.FILE_HEADER,    "metric_id"),
    LOCATIONS ("locations", "locations.csv", LuxLocation.FILE_HEADER,  "loc_id"),
    PERIODS   ("periods",   "periods.csv",   LuxPeriod.FILE_HEADER,    "period_id"),
    DATA      ("data",      "data.csv",      LuxDataPoint.FILE_HEADER, "???");

    /***/
    private LuxDataType(String tableName, String csvFileName, String[] csvFileHeader, String idName) {
        this.tableName = tableName;
        this.csvFileName = csvFileName;
        this.csvFileHeader = csvFileHeader;
        this.idName = idName;
    }

    private final String tableName;
    private final String csvFileName;
    private final String[] csvFileHeader;
    private final String idName;

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

    /***/
    public static LuxDataType getTypeByName(String typeName) {
        if(StringUtils.isBlank(typeName)) {
            return null;
        }

        // todo: error processing!!!
        return LuxDataType.valueOf(typeName.toUpperCase());
    }

}
