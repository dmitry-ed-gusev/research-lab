package dgusev.dbpilot.config;

import lombok.Getter;

/** DBs types enumeration. */
public enum DBType
{
    UNKNOWN      ("UNKNOWN",     -1),
    INFORMIX     ("INFORMIX",     0),
    MYSQL        ("MYSQL",        1),
    ODBC         ("ODBC",         2),
    DBF          ("DBF",          3),
    MSSQL_JTDS   ("MSSQL_JTDS",   4),
    MSSQL_NATIVE ("MSSQL_NATIVE", 5);

    // Поля класса-перечисления
    @Getter private final String strValue;
    @Getter private final int    intValue;

    // Конструктор класса-перечисления
    DBType(String strValue, int intValue) {
        this.strValue = strValue;
        this.intValue = intValue;
    }

}