package dgusev.dbpilot.config;

import lombok.Getter;

/** DB tables types. */
public enum DBTableType {
    ALL              ("ALL"),
    TABLE            ("TABLE"),
    VIEW             ("VIEW"),
    SYSTEM_TABLE     ("SYSTEM TABLE"),
    GLOBAL_TEMPORARY ("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY  ("LOCAL TEMPORARY"),
    ALIAS            ("ALIAS"),
    SYNONYM          ("SYNONYM"),
    UNKNOWN          ("UNKNOWN");        // <- данное значение есть в Информиксе

    @Getter private String strValue;

    DBTableType(String strValue) {
        this.strValue = strValue;
    }

}
