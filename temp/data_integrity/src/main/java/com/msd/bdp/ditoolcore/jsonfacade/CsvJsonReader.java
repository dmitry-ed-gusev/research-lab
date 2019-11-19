/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.jsonfacade;

import com.msd.bdp.ditoolcore.SqlObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


/**
 * Class extending abstract json reader and implements Oracle-specific functionality.
 * Source database: Hana
 * Target database: Hive
 */
public class CsvJsonReader extends JsonFacade {

    private static final String TIMESTAMP_TYPE = "TIMESTAMP";
    private static final String DATE_TYPE = "DATE";

    public CsvJsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }


    public CsvJsonReader(File jsonFileName, String sourceSchema, String targetSchema, boolean useTransformation) {
        super(jsonFileName, sourceSchema, targetSchema, useTransformation);
    }

    private String appendTargetSelectRules(String select, String sourceDbType) {

        if (sourceDbType.contains(DATE_TYPE) || sourceDbType.contains(TIMESTAMP_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss.SSS')", select);
        } else if (isTargetTechnicalColumn(select)) {
            return null;
        } else {
            return select;
        }

    }


    private String getTargetSelect(StringJoiner shTarget, String targetTableName) {
        return String.format("select %s from %s", shTarget.toString(), targetTableName);
    }


    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema, boolean usertransformation) {
        List<SqlObject> sqlObjects = new ArrayList<>();
        SqlObject sObj;
        for (Object t : tables) {

            JSONObject table = (JSONObject) t;

            String sourceDbTableName = getSourceTableName(table);
            String targetDbTableName = getTargetTableName(table);
            JSONArray columns = (JSONArray) table.get("columns");
            List<String> pKeys = getPrimaryKey(table);

            StringJoiner sjTarget = new StringJoiner(",");

            List<Integer> pkPositions = new ArrayList<>();

            for (int i = 0; i < columns.length(); i++) {

                JSONObject column = columns.getJSONObject(i);

                String sourceDbColumnName = getSourceColumnName(column);
                String sourceDbColumnType = getSourceColumnType(column);
                String targetDbColumnType = getTargetColumnType(column);
                String targetDbColumnName = getTargetColumnName(column);
                String targetTransformation = getTransformation(column);

                if (ArrayUtils.contains(pKeys.toArray(), sourceDbColumnName.toUpperCase())) {
                    pkPositions.add(i);
                }

                if (StringUtils.isBlank(sourceDbColumnName) || StringUtils.isBlank(targetDbColumnName)) {
                    continue;
                }


                String targetSelect;
                if (usertransformation) {
                    targetSelect = targetTransformation;
                } else {
                    targetSelect = appendTargetSelectRules(targetDbColumnName, sourceDbColumnType);
                }


                if ( targetSelect != null) {
                    sjTarget.add(targetSelect);
                }
            }


            String targetSelect = getTargetSelect(sjTarget, targetSchema == null ?
                    targetDbTableName :
                    String.format("%s.%s", targetSchema, targetDbTableName));
            int[] pkArray = ArrayUtils.toPrimitive(pkPositions.toArray(new Integer[pkPositions.size()]));

            sObj = new SqlObject(sourceDbTableName, targetDbTableName,
                    null,
                    targetSelect,
                    null,
                    false,
                    sourceDbTableName,
                    pkArray);
            sqlObjects.add(sObj);


        }
        return sqlObjects;
    }

    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema) {
        return new ArrayList<>();
    }


}
