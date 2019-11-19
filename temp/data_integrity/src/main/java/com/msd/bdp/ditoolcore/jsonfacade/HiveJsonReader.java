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
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class extending abstract json reader and implements Teradata-specific functionality.
 * Source database: Hive
 * Target database: Teradata
 */
public class HiveJsonReader extends JsonFacade {

    private static final String DECIMAL_TYPE = "DECIMAL";
    private static final String FLOAT_TYPE = "FLOAT";
    private static final String REAL_TYPE = "REAL";
    private static final String TIMESTAMP_TYPE = "TIMESTAMP";
    private static final String DATE_TYPE = "DATE";
    private static final String NUMBER_TYPE = "NUMBER";

    private static final Pattern NUMBER_P_S_PATTERN = Pattern.compile("DECIMAL\\((\\d+),(\\d+)\\)");
    private static final Pattern NUMBER_P_PATTERN = Pattern.compile("DECIMAL\\((\\d+)\\)");


    public HiveJsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }

    private String getModifiedDateQuery(String value) {
        return String.format("TO_CHAR(%s,'YYYY-MM-DD HH24:MI:SS')", value);
    }

    private String getTargetSelect(StringJoiner shTarget, String targetTableName) {
        return String.format("select %s from %s", shTarget.toString(), targetTableName);
    }


    private String appendSourceSelects(String hiveColumnName, String targetType) {

        String updatedSelect = DIToolUtilities.backQuote(hiveColumnName);

        if (targetType.contains(TIMESTAMP_TYPE)
                || targetType.contains(DATE_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss')", updatedSelect);
        } else if (hiveColumnName.equalsIgnoreCase("HARD_DELETED_FLAG") ||
                hiveColumnName.equalsIgnoreCase("ACTIVE_FLAG")) {
            return String.format("CASE `%s` WHEN TRUE then 1 WHEN FALSE then 0 ELSE null END `%s`",
                    hiveColumnName, hiveColumnName);
        }
        return updatedSelect;
    }


    private String appendTargetSelect(String select, String nativeType) {
        if ("DELETE_DTTM".equals(select)
                || "UPD_DTTM".equals(select)
                || "INS_DTTM".equals(select)
                ) {
            return getModifiedDateQuery(select);

        } else if (nativeType.contains(REAL_TYPE)
                || nativeType.contains(FLOAT_TYPE)) {
            return String.format("cast(%s as decimal(38, 0))", select);

        }
        else if (nativeType.contains(NUMBER_TYPE) || nativeType.contains(DECIMAL_TYPE)) {
            Matcher numberPS = NUMBER_P_S_PATTERN.matcher(nativeType);
            Matcher numberP = NUMBER_P_PATTERN.matcher(nativeType);
            Integer precision;
            Integer scale;
            Pattern n = Pattern.compile("DECIMAL\\((\\d+)");
            if (n.matcher(nativeType).find()) {
                if (numberP.find()) {
                    precision = Integer.valueOf(numberP.group(1));
                    return String.format("cast(%s as decimal(%d, 0))", select, precision);
                } else if (numberPS.find()) {
                    precision = Integer.valueOf(numberPS.group(1));
                    scale = Integer.valueOf(numberPS.group(2));
                    String precisionFormat = StringUtils.repeat("9", precision-scale);
                    String scaleFormat = StringUtils.repeat("9", scale);
                    return scale.equals(0) ? select :
                            String.format("TRIM(TO_CHAR(%s, CASE WHEN %s = TRUNC(%s) THEN '%s' ELSE 'FM%s0.%s' END))", select, select, select, precisionFormat, precisionFormat, scaleFormat);
                }
            }
        }
        else if (nativeType.contains(TIMESTAMP_TYPE)
                || nativeType.contains(DATE_TYPE)) {
            return getModifiedDateQuery(select);
        }
        return select;
    }


    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema) {
        List<SqlObject> sqlObjects = new ArrayList<>();
        SqlObject sObj;
        for (Object t : tables) {
            boolean primaryKeyTypeAllowed = false;
            JSONObject table = (JSONObject) t;

            String sourceDbFullTableName = getSourceTableName(table);
            String shortTableName = sourceDbFullTableName;
            String targetDbTableName = getTargetTableName(table);

            List<String> pKeys = getPrimaryKey(table);
            String pKey = null;

            StringJoiner sjSource = new StringJoiner(",");
            StringJoiner sjTarget = new StringJoiner(",");

            JSONArray columns = (JSONArray) table.get("columns");
            for (Object c : columns) {
                JSONObject column = (JSONObject) c;
                String sourceDbColumnName = getSourceColumnName(column).toUpperCase();
                String sourceDbColumnType = getSourceColumnType(column).toUpperCase();

                String targetDbColumnName = getTargetColumnName(column).toUpperCase();
                String targetDbColumnType = getTargetColumnType(column).toUpperCase();

                if (StringUtils.isBlank(sourceDbColumnName) || StringUtils.isBlank(targetDbColumnName)) {
                    continue;
                }

                if (!pKeys.isEmpty() && pKey == null && DIToolUtilities.isInList(sourceDbColumnName, pKeys) &&
                        (sourceDbColumnType.contains(NUMBER_TYPE) || sourceDbColumnType.contains(DECIMAL_TYPE))) {
                    pKey = sourceDbColumnName;
                    primaryKeyTypeAllowed = true;
                }

                String sourceSelect = appendSourceSelects(sourceDbColumnName, targetDbColumnType);
                String targetSelect = appendTargetSelect(targetDbColumnName, targetDbColumnType);

                if (sourceSelect != null && targetSelect !=null) {
                    sjSource.add(sourceSelect);
                    sjTarget.add(targetSelect);
                }


            }

            sourceDbFullTableName = sourceSchema == null ? sourceDbFullTableName : sourceSchema + "." + sourceDbFullTableName;
            targetDbTableName = targetSchema == null ? targetDbTableName : targetSchema + "." + targetDbTableName;
            String dbSelect = String.format("select %s from %s", sjSource.toString(), sourceDbFullTableName);

            String targetSelect = getTargetSelect(sjTarget, targetDbTableName);

            sObj = new SqlObject(sourceDbFullTableName, targetDbTableName,
                    dbSelect,
                    targetSelect,
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
