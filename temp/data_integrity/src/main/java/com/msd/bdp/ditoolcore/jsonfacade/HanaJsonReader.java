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
import java.util.regex.Pattern;


/**
 * Class extending abstract json reader and implements Oracle-specific functionality.
 * Source database: Hana
 * Target database: Hive
 */
public class HanaJsonReader extends JsonFacade {

    private static final String TIMESTAMP_TYPE = "TIMESTAMP";
    private static final String DATE_TYPE = "DATE";
    private static final String NUMBER_TYPE = "DECIMAL";
    private static final String DOUBLE_TYPE = "DOUBLE";

    public HanaJsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }

    private String appendTargetSelectRules(String select, String sourceDbType) {
        sourceDbType = sourceDbType.toUpperCase(Locale.ENGLISH);
        select = DIToolUtilities.backQuote(select);
        if (sourceDbType.contains(DATE_TYPE) || sourceDbType.contains(TIMESTAMP_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss')", select);
        }  else if (isTargetTechnicalColumn(select)) {
            return null;
        } else {
            return select;
        }
    }

    private String appendSourceSelectRules(String select, String dbType) {
        String nativeType = dbType.toUpperCase(Locale.ENGLISH);
        select = DIToolUtilities.doubleQuote(select);
        if (nativeType.contains(NUMBER_TYPE)) {
            Pattern n = Pattern.compile("DECIMAL\\((\\d+)");
            if (n.matcher(dbType).find()) {

                return String.format("TO_NUMBER(%s)", select);

            }
        } else if (nativeType.contains(DOUBLE_TYPE)) {
            return String.format("TO_NUMBER(%s)", select);
        }
                return select;

    }


    private String getTargetSelect(StringJoiner shTarget, String targetTableName) {
        return String.format("select %s from %s", shTarget.toString(), targetTableName);
    }


    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema) {

        List<SqlObject> sqlObjects = new ArrayList<>();
        SqlObject sObj;
        for (Object t : tables) {
            boolean primaryKeyTypeAllowed = false;
            JSONObject table = (JSONObject) t;

            String sourceDbTableName = getSourceTableName(table);
            String shortTableName = sourceDbTableName.contains(" ") ? sourceDbTableName.split(" ")[0] : sourceDbTableName;
            String targetDbTableName = getTargetTableName(table);
            JSONArray columns = (JSONArray) table.get("columns");
            List<String> pKeys = getPrimaryKey(table);
            String pKey = null;
            StringJoiner sjSource = new StringJoiner(",");
            StringJoiner sjTarget = new StringJoiner(",");



            for (Object c : columns) {
                JSONObject column = (JSONObject) c;

                String sourceDbColumnName = getSourceColumnName(column);
                String sourceDbColumnType = getSourceColumnType(column);
                String targetDbColumnName = getTargetColumnName(column);
                String targetDbColumnType = getTargetColumnType(column);


                if (StringUtils.isBlank(sourceDbColumnName) || StringUtils.isBlank(targetDbColumnName)) {
                    continue;
                }

                if (!pKeys.isEmpty() && pKey == null && DIToolUtilities.isInList(sourceDbColumnName, pKeys) && sourceDbColumnType.contains(NUMBER_TYPE)) {
                    pKey = sourceDbColumnName;
                    primaryKeyTypeAllowed = true;
                }

                String sourceSelect;
                String targetSelect;

                sourceSelect =  appendSourceSelectRules(sourceDbColumnName, sourceDbColumnType);
                targetSelect = appendTargetSelectRules(targetDbColumnName, sourceDbColumnType);

                if (sourceSelect != null && targetSelect != null) {
                    sjSource.add(sourceSelect);
                    sjTarget.add(targetSelect);
                }


            }
            sourceDbTableName =  sourceSchema == null ? sourceDbTableName : sourceSchema + "." + sourceDbTableName;

            String dbSelect = "select "
                    + sjSource.toString()
                    + " from "
                    + sourceDbTableName;

            targetDbTableName = targetSchema == null ? targetDbTableName : targetSchema + "." + targetDbTableName;
            String targetSelect = getTargetSelect(sjTarget, targetDbTableName);

            sObj = new SqlObject(sourceDbTableName, targetDbTableName,
                    dbSelect,
                    targetSelect,
                    pKey,
                    primaryKeyTypeAllowed,
                    shortTableName.replaceAll(" ","_"));
            sqlObjects.add(sObj);


        }
        return sqlObjects;
    }

    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema, boolean useTransformation) {
        throw new DiToolException("Unsupported method");
    }

}
