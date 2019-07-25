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
public class Db2JsonReader extends JsonFacade {

    private static final String DATE_TYPE = "DATETIME";
    private static final String NUMERIC_TYPE = "NUMERIC";
    private static final String REAL_TYPE = "REAL";
    private static final String INT_TYPE = "INT";

    private static final Pattern DECIMAL_P_S_PATTERN = Pattern.compile("DECIMAL\\((\\d+),(\\d+)\\)");



    public Db2JsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }


    private String appendSourceSelectRules(String select, String sourceDbType) {

        Matcher numberPS = DECIMAL_P_S_PATTERN.matcher(sourceDbType);
        if(numberPS.find()){
            return String.format("CASE WHEN ABS(%s) < 1. THEN '0' ELSE '' END || TRANSLATE(RTRIM(TRANSLATE(TRANSLATE(LTRIM(RTRIM(TRANSLATE(CHAR(%s),' ', '0'))), '0', ' '), ' ', '.')), '.', ' ') ",select,select);
        }

        return select;
    }



    private String appendTargetSelectRules(String select, String sourceType) {
        String nativeType = sourceType.toUpperCase(Locale.ENGLISH);
        select = DIToolUtilities.backQuote(select);
        if (nativeType.contains(DATE_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss')", select);
        } else if (select.contains("LOAD_ID")) {
            return select + "_";
        } else if (isTargetTechnicalColumn(select)) {
            return null;
        } else {
            return select;
        }
    }




    private String getHiveSelect(StringJoiner sjHive, String hiveTableName) {
        return String.format("select %s from %s", sjHive.toString(), hiveTableName);
    }


    private static String getTableNameNoAlias(String v){
        if(v.contains(" ")) {
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
            StringJoiner sjTarget = new StringJoiner(",");

            for (Object c : columns) {
                JSONObject column = (JSONObject) c;

                String sourceDbColumnName = getSourceColumnName(column);
                String sourceColumnType = getSourceColumnType(column);

                String targetDbColumnName = getTargetColumnName(column);

                if (StringUtils.isBlank(sourceDbColumnName) || StringUtils.isBlank(targetDbColumnName)) {
                    continue;
                }

                if (!pKeys.isEmpty() && pKey == null && DIToolUtilities.isInList(sourceDbColumnName, pKeys) &&
                        (sourceColumnType.contains(INT_TYPE) || sourceColumnType.contains(NUMERIC_TYPE) ||
                                sourceColumnType.contains(REAL_TYPE))) {
                    pKey = sourceDbColumnName;
                    primaryKeyTypeAllowed = true;
                }

                String sourceSelect = appendSourceSelectRules(sourceDbColumnName, sourceColumnType);
                String targetSelect = appendTargetSelectRules(targetDbColumnName, sourceColumnType);


                if (sourceSelect != null && targetSelect !=null) {
                    sjSource.add(sourceSelect);
                    sjTarget.add(targetSelect);
                }

            }

            String dbSelect = String.format("select %s from %s", sjSource.toString(), sourceDbTableName);

            String hiveSelect = getHiveSelect(sjTarget, targetDbTableName);


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
