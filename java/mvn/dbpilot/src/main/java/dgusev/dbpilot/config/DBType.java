package dgusev.dbpilot.config;

import lombok.Getter;

/** DBs types enumeration. */
public enum DBType
{
    UNKNOWN      ("UNKNOWN",     -1, null),
    INFORMIX     ("INFORMIX",     0, "com.informix.jdbc.IfxDriver"),
    MYSQL        ("MYSQL",        1, "com.mysql.jdbc.Driver"),
    ODBC         ("ODBC",         2, "sun.jdbc.odbc.JdbcOdbcDriver"),
    DBF          ("DBF",          3, "com.hxtt.sql.dbf.DBFDriver"),
    MSSQL_JTDS   ("MSSQL_JTDS",   4, "net.sourceforge.jtds.jdbc.Driver"),
    MSSQL_NATIVE ("MSSQL_NATIVE", 5, "com.microsoft.sqlserver.jdbc.SQLServerDriver");

    @Getter private final String strValue;
    @Getter private final int    intValue;
    @Getter private final String driver;

    DBType(String strValue, int intValue, String driver) {
        this.strValue = strValue;
        this.intValue = intValue;
        this.driver   = driver;
    }

}