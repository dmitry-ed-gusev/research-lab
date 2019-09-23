package com.msd.bdp.ditoolcore.transformation;

public enum RuleEnum {

    ORACLE_HIVE("ORACLE_HIVE"),
    DB2_HIVE("DB2_HIVE"),
    MSSQL_HIVE("MSSQL_HIVE");

    private String text;

    RuleEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static RuleEnum fromString(String text) {
        for (RuleEnum b : RuleEnum.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
