/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.jsonfacade;

import com.msd.bdp.DiToolException;
import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.SqlObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class extending abstract json reader and implements Sqlserver-specific functionality.
 * Source database: MSSql
 * Target database: Hive
 */
public class SqlserverJsonReader extends JsonFacade {

    private static final String MSSQL_DATE_TYPE = "DATETIME";
    private static final String BIT_DATE_TYPE = "BIT";
    private static final String NUMERIC_TYPE = "NUMERIC";
    private static final String REAL_TYPE = "REAL";
    private static final String INT_TYPE = "INT";
    private static final Pattern NUMERIC_DECIMAL_PATTERN = Pattern.compile("(NUMBER|DECIMAL)\\((\\d+),(\\d+)\\)");


    public SqlserverJsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }


    private String appendSourceSelectRules(String select, String sourceDbType) {
        String nativeType = sourceDbType.toUpperCase(Locale.ENGLISH);
        Matcher mtch = NUMERIC_DECIMAL_PATTERN.matcher(nativeType);
        select = DIToolUtilities.doubleQuote(select);
        if (mtch.find()) {
            String precision = mtch.group(2);
            String scale = mtch.group(3);
            return String.format("LTRIM(str(%s,%s,%s))", select, precision, scale);
        } else if (nativeType.contains(REAL_TYPE)) {
            return String.format("CASE WHEN convert(varchar(4000),%s) IS NOT NULL THEN convert(varchar(4000),convert(varchar(4000),%s)) ELSE NULL END", select, select);
        } else if (nativeType.contains(MSSQL_DATE_TYPE)) {
            return getModifiedDateQuery(select);
        } else if (sourceDbType.contains(BIT_DATE_TYPE)) {
            return String.format("case WHEN %s = 1 then 'true' WHEN %s = 0 then 'false' else 'null' end", select, select);
        } else {
            return select;
        }

    }


    private String appendTargetSelectRules(String select, String sourceType) {
        String nativeType = sourceType.toUpperCase(Locale.ENGLISH);
        select = DIToolUtilities.backQuote(select);
        if (nativeType.contains(MSSQL_DATE_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss')", select);
        } else if (isTargetTechnicalColumn(select)) {
            return null;
        } else {
            return select;
        }
    }


    private String getModifiedDateQuery(String value) {
        return String.format("convert(varchar(30), %s ,120)", value);
    }


    private String getHiveSelect(StringJoiner sjHive, String hiveTableName) {
        return "select "
                + sjHive.toString()
                + " from "
                + hiveTableName;
    }


    private static String getTableNameNoAlias(String v) {
        if (v.contains(" ")) {
            return v.split(" ")[0];
        }
        return v;
    }

    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema) {

        List<SqlObject> sqlObjects = new ArrayList<>();
        SqlObject sObj;
        for (Object t : tables) {
            boolean primaryKeyTypeAllowed = false;
            JSONObject table = (JSONObject) t;

            String sourceDbTableName = getSourceTableName(table);
            String shortTableName = getTableNameNoAlias(sourceDbTableName);
            String targetDbTableName = getTargetTableName(table);
            JSONArray columns = (JSONArray) table.get("columns");
            List<String> pKeys = getPrimaryKey(table);
            String pKey = null;
            sourceDbTableName = sourceSchema == null ? sourceDbTableName : DIToolUtilities.doubleQuote(sourceSchema) + "." + sourceDbTableName;
            targetDbTableName = targetSchema == null ? targetDbTableName : targetSchema + "." + targetDbTableName;
            StringJoiner sjSource = new StringJoiner(",");
            StringJoiner sjHive = new StringJoiner(",");

            for (Object c : columns) {
                JSONObject column = (JSONObject) c;

                String sourceDbColumnName = getSourceColumnName(column);
                String sourceColumnType = getSourceColumnType(column);

                String targetDbColumnName = getTargetColumnName(column);

                if (StringUtils.isBlank(sourceDbColumnName) ||
                        StringUtils.isBlank(targetDbColumnName)) {
                    continue;
                }

                if (!pKeys.isEmpty() && pKey == null && DIToolUtilities.isInList(sourceDbColumnName, pKeys) &&
                        (sourceColumnType.contains(INT_TYPE) || sourceColumnType.contains(NUMERIC_TYPE) ||
                                sourceColumnType.contains(REAL_TYPE))) {
                    pKey = sourceDbColumnName;
                    primaryKeyTypeAllowed = true;
                }

                String sourceSelect;
                String targetSelect;


                sourceSelect = appendSourceSelectRules(sourceDbColumnName, sourceColumnType);
                targetSelect = appendTargetSelectRules(targetDbColumnName, sourceColumnType);


                if (sourceSelect != null) {
                    sjSource.add(sourceSelect);
                }
                if (targetSelect != null) {
                    sjHive.add(targetSelect);
                }

            }

            String dbSelect = "select "
                    + sjSource.toString()
                    + " from "
                    + sourceDbTableName;

            String hiveSelect = getHiveSelect(sjHive, targetDbTableName);


            sObj = new SqlObject(sourceDbTableName, targetDbTableName,
                    dbSelect,
                    hiveSelect,
                    pKey,
                    primaryKeyTypeAllowed,
                    shortTableName);
            sqlObjects.add(sObj);

        }
        return sqlObjects;
    }

    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema, boolean useTransformation) {
        throw new DiToolException("Unsupported method");
    }

}
