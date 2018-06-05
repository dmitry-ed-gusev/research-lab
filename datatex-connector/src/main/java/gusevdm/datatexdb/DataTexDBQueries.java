package gusevdm.datatexdb;

/** Sql queries for DataTex DB. */
public final class DataTexDBQueries {

    /** Get departments SQL. */
    public static final String SQL_DEPARTMENTS =
            "select adc.uniqueid as UNIQUE_ID, adc.valuestring as DEPT_ID, ads.valuestring as DEPT_NAME" +
            "    from adstorage adc, costcenter cc, adstorage ads " +
            "    where cc.absuniqueid = adc.uniqueid and adc.namename ='AccCostCenterCode'" +
            "        and cc.absuniqueid = ads.uniqueid and ads.namename ='AccCostCenterDscr'";

    /** Equipments list. */
    public static final String SQL_EQUIPMENTS = "select " +
            "wcd.mainresourcecode as ID, \n" +
            "wcd.workcentercode as EQUIPMENT_ID,\n" +
            "ad.valuestring as DEPT_ID,\n" +
            "wc.longdescription as NAME,\n" +
            "wcd.mainresourcecode as MODEL,\n" +
            "ad.uniqueid\n" +
            "--from workcenterdetail wcd, workcenter wc left join costcenter cc on wc.costcentercode = cc.code left join adstorage ad on cc.absuniqueid = ad.uniqueid and ad.namename = 'AccCostCenterCode'\n" +
            "from workcenterdetail wcd, workcenter wc, costcenter cc, adstorage ad\n" +
            "where wcd.workcentercode = wc.code and wc.costcentercode = cc.code and cc.absuniqueid = ad.uniqueid and ad.namename = 'AccCostCenterCode'";

    private DataTexDBQueries() {} // class is completely final
}
