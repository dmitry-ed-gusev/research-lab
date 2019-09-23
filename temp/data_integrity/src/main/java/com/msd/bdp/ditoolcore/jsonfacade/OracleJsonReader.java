/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.jsonfacade;

import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.SqlObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class extending abstract json reader and implements Oracle-specific functionality.
 * Source database: Oracle
 * Target database: Hive
 */
public class OracleJsonReader extends JsonFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleJsonReader.class);
    private static final String TIMESTAMP_TYPE = "TIMESTAMP";
    private static final String DATE_TYPE = "DATE";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String DECIMAL_TYPE = "DECIMAL";
    private static final String BLOB_TYPE = "BLOB";
    private static final String RAW_TYPE = "RAW";
    private static final String FLOAT_TYPE = "FLOAT";

    private static final Pattern NUMBER_P_S_PATTERN = Pattern.compile("NUMBER\\((\\d+),(\\d+)\\)");
    private static final Pattern NUMBER_P_PATTERN = Pattern.compile("NUMBER\\((\\d+)\\)");



    public OracleJsonReader(File jsonFileName, String sourceSchema, String targetSchema) {
        super(jsonFileName, sourceSchema, targetSchema);
    }


    private static String getDbNestedTableColumnName(String fullColumnName, String nestedTableName) {
        String columnName = StringUtils.replaceOnce(fullColumnName, nestedTableName + "__", "").
                replaceAll("__", ".");
        return columnName.startsWith(".") ?
                String.format("%s%s", nestedTableName, columnName) :
                String.format("%s.%s", nestedTableName, columnName);
    }

    private static boolean isNestedTable(JSONObject o) {
        try {
            return o.get("nestedTable").toString().contains("true");
        } catch (JSONException e) {
            LOGGER.debug("No nested table sign in the JSON", e);
            return false;
        }
    }

    private static String getNestedTableName(String fullTableName) {
        return fullTableName.split("__")[(fullTableName.split("__")).length - 1];
    }

    private static String getNameFromNestedName(String fullName) {
        String name = fullName;
        if (name.contains(",")) {
            name = name.split(",")[0];
        }

        name = getTableNameNoAlias(name);

        return name;
    }

    private static String getTableNameNoAlias(String v){
        if(v.contains(" ")) {
//            return v.substring(0, (v.length()/2));
            return v.split(" ")[0];
        }
        return v;
    }

    private static String getOraTableName(String fullName) {
        if (fullName.contains(".")) {
            return fullName.split("\\.")[(fullName.split("\\.")).length - 1];
        }
        return fullName;
    }

    private String appendSourceSelectRules(String select, String dbType) {
        String nativeType = dbType.toUpperCase(Locale.ENGLISH);
        if (dbType.contains(BLOB_TYPE) || dbType.contains(RAW_TYPE)) {
            return null;
        } else if (nativeType.contains(NUMBER_TYPE)) {
            Matcher numberPS = NUMBER_P_S_PATTERN.matcher(dbType);
            Matcher numberP = NUMBER_P_PATTERN.matcher(dbType);
            Integer precision = 0;
            Integer scale = 0;
            Pattern n = Pattern.compile("NUMBER\\((\\d+)");
            if (n.matcher(dbType).find()) {
                if (numberP.find()) {
                    precision = Integer.valueOf(numberP.group(1));
                    scale = 0;
                } else if (numberPS.find()) {
                    precision = Integer.valueOf(numberPS.group(1));
                    scale = Integer.valueOf(numberPS.group(2));
                }
                String precisionFormat = StringUtils.repeat("9", precision-scale);
                String scaleFormat = StringUtils.repeat("9", scale);
                return scale.equals(0) ? select :
                        String.format("TRIM(TO_CHAR(%s, CASE WHEN %s = TRUNC(%s) THEN '%s' ELSE 'FM%s0.%s' END))", select, select, select, precisionFormat, precisionFormat, scaleFormat);
            } else {
                return String.format("TRIM(TO_CHAR(%s))", select);
            }
        } else if (nativeType.contains(FLOAT_TYPE)) {
            return String.format("TRIM(TO_CHAR(%s))", select);
        } else if (nativeType.contains(DATE_TYPE) || nativeType.contains(TIMESTAMP_TYPE)) {
            return getModifiedDateQuery(select);
        } else if (nativeType.contains("XMLTYPE")){
            return String.format("TO_CLOB(%s)",select);
        } else {
            return select;
        }
    }

    private String appendTargetSelectRules(String select, String sourceDbType, String targetColumnType) {
        sourceDbType = sourceDbType.toUpperCase(Locale.ENGLISH);
        select = DIToolUtilities.backQuote(select);
        if (sourceDbType.contains(DATE_TYPE) || sourceDbType.contains(TIMESTAMP_TYPE)) {
            return String.format("date_format(%s, 'yyyy-MM-dd HH:mm:ss')", select);
        } else if (isTargetTechnicalColumn(select)) {
            return null;
        } else if(targetColumnType.contains(DECIMAL_TYPE)){
            return String.format("cast(%s as string)",select);
        }else {
            return select;
        }
    }


    private String getModifiedDateQuery(String value) {
        return String.format("TO_CHAR(%s,'YYYY-MM-DD HH24:MI:SS')", value);
    }

    private String getTargetSelect(StringJoiner shTarget, String targetTableName) {
        return String.format("select %s from %s", shTarget.toString(), targetTableName);
    }


    private SqlObject createSelectForNestedTable(JSONArray columns, String sourceDbFullTableName, String targetTableName, List<String> pKeys) {
        StringJoiner sjSource = new StringJoiner(",");
        StringJoiner sjHive = new StringJoiner(",");
        String nestedTableName = getNestedTableName(targetTableName);
        String tempName = getNameFromNestedName(sourceDbFullTableName);
        String sourceTableName = getOraTableName(tempName);
        String pKey = null;
        for (Object c : columns) {
            JSONObject column = (JSONObject) c;
            String targetDbColumnName = getTargetColumnName(column);
            String targetDbColumnType = getTargetColumnType(column);
            String sourceDbColumnName = getSourceColumnName(column);
            String sourceColumnType = getSourceColumnType(column);
            if (StringUtils.isBlank(sourceDbColumnName) ||
                    StringUtils.isBlank(targetDbColumnName) ||
                    isArtificalKey(sourceDbColumnName) ||
                    isArtificalKey(targetDbColumnName)) {
                continue;
            }
            String nestedColumnName = getDbNestedTableColumnName(targetDbColumnName, nestedTableName);

            String sourceSelect;


            if (!pKeys.isEmpty() &&
                    pKey == null &&
                    DIToolUtilities.isInList(sourceDbColumnName, pKeys) &&
                    sourceColumnType.contains(NUMBER_TYPE)) {
                pKey = sourceDbColumnName;

            }

            if (targetDbColumnName.contains(sourceDbColumnName + "__") && !sourceDbColumnName.equals(nestedTableName)) {//nested column
                sourceDbColumnName = sourceTableName + "." + targetDbColumnName.replaceAll("__", ".");
                sourceSelect = appendSourceSelectRules(sourceDbColumnName, sourceColumnType);
            } else if (sourceDbColumnName.equals(nestedTableName)) {
                sourceSelect = appendSourceSelectRules(nestedColumnName, sourceColumnType);
            } else {
                sourceSelect = appendSourceSelectRules(sourceTableName + "." + sourceDbColumnName, sourceColumnType);
            }

            String targetSelect = appendTargetSelectRules(targetDbColumnName, sourceColumnType, targetDbColumnType);

            if (sourceSelect != null && targetSelect != null) {
                sjSource.add(sourceSelect);
                sjHive.add(targetSelect);
            }

        }

        String dbSelect = String.format("select %s from %s", sjSource.toString(), sourceDbFullTableName);

        String hiveSelect = getTargetSelect(sjHive, targetTableName);

        return new SqlObject(sourceDbFullTableName, targetTableName,
                dbSelect,
                hiveSelect,
                pKey,
                false,
                sourceDbFullTableName.replaceAll(" ","_"));

    }


    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema) {
        List<SqlObject> sqlObjects = new ArrayList<>();
        SqlObject sObj;
        for (Object t : tables) {
            boolean primaryKeyTypeAllowed = false;
            boolean isNestedColumn = false;
            JSONObject table = (JSONObject) t;

            String sourceDbFullTableName = getSourceTableName(table);
            String shortTableName = getTableNameNoAlias(sourceDbFullTableName);
            String sourceDbTableName = getOraTableName(shortTableName);
            String targetDbTableName = getTargetTableName(table);
            JSONArray columns = (JSONArray) table.get("columns");
            List<String> pKeys = getPrimaryKey(table);
            String pKey = null;
            StringJoiner sjSource = new StringJoiner(",");
            StringJoiner sjTarget = new StringJoiner(",");

            sourceDbFullTableName = sourceSchema == null ? sourceDbFullTableName : sourceSchema + "." + sourceDbFullTableName;
            targetDbTableName = targetSchema == null ? targetDbTableName : targetSchema + "." + targetDbTableName;

            String tableNameNoAlias = getTableNameNoAlias(sourceDbFullTableName);


            if (!isNestedTable(table)) {
                for (Object c : columns) {
                    JSONObject column = (JSONObject) c;


                    String sourceDbColumnName = getSourceColumnName(column);
                    String sourceDbColumnType = getSourceColumnType(column);

                    String targetDbColumnName = getTargetColumnName(column);
                    String targetDbColumnType = getTargetColumnType(column);

                    if (StringUtils.isBlank(sourceDbColumnName) ||
                            StringUtils.isBlank(targetDbColumnName) ||
                            isArtificalKey(sourceDbColumnName) ||
                            isArtificalKey(targetDbColumnName)) {
                        continue;
                    }

                    if (!pKeys.isEmpty() && pKey == null &&
                            DIToolUtilities.isInList(sourceDbColumnName, pKeys) &&
                            sourceDbColumnType.contains(NUMBER_TYPE)) {
                        pKey = sourceDbColumnName;
                        primaryKeyTypeAllowed = true;
                    }

                    String sourceSelect;
                    String targetSelect;

                    if (targetDbColumnName.contains(sourceDbColumnName + "__")) {
                        isNestedColumn = true;
                        sourceDbColumnName = sourceDbTableName + "." + targetDbColumnName.replaceAll("__", ".");
                        sourceSelect = appendSourceSelectRules(sourceDbColumnName, sourceDbColumnType);
                    } else {
                        sourceSelect = appendSourceSelectRules(sourceDbTableName + "." + DIToolUtilities.doubleQuote(sourceDbColumnName), sourceDbColumnType);
                    }

                    targetSelect = appendTargetSelectRules(targetDbColumnName, sourceDbColumnType, targetDbColumnType);

                    if (sourceSelect != null && targetSelect != null) {
                        sjSource.add(sourceSelect);
                        sjTarget.add(targetSelect);
                    }


                }


                String fromDbTableString = isNestedColumn ? sourceDbFullTableName :
                        tableNameNoAlias;
                String dbSelect = String.format("select %s from %s", sjSource.toString(), fromDbTableString);

                String targetSelect = getTargetSelect(sjTarget, targetDbTableName);

                sObj = new SqlObject(sourceDbFullTableName, targetDbTableName,
                        dbSelect,
                        targetSelect,
                        pKey,
                        primaryKeyTypeAllowed,
                        shortTableName);
                sqlObjects.add(sObj);
            } else {
                sqlObjects.add(createSelectForNestedTable(columns, sourceDbFullTableName, targetDbTableName, pKeys));
            }

        }
        return sqlObjects;
    }

    @Override
    protected List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema, boolean useTransformation) {
        return null;
    }

}
