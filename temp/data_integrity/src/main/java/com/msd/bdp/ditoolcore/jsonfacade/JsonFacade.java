/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.jsonfacade;

import com.msd.bdp.ditoolcore.SqlObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Abstract class to read the json file containing the DB structure
 * Should be extended via specific json reader implementing specific functionality.
 */
public abstract class JsonFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFacade.class);
    List<SqlObject> sqlObjects;

    public JsonFacade(File jsonFileName, String sourceSchema, String targetSchema, boolean useTransformation) {
        sqlObjects = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(new JSONTokener(new FileReader(jsonFileName)));

            JSONArray tables = (JSONArray) root.get("tables");

            sqlObjects = generateSqlObjects(tables, sourceSchema, targetSchema, useTransformation);


        } catch (FileNotFoundException e) {
            LOGGER.error("JSON file is not found", e);
        }
    }

    public JsonFacade(File jsonFileName, String sourceSchema, String targetSchema) {
        sqlObjects = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(new JSONTokener(new FileReader(jsonFileName)));

            JSONArray tables = (JSONArray) root.get("tables");

            sqlObjects = generateSqlObjects(tables, sourceSchema, targetSchema);


        } catch (FileNotFoundException e) {
            LOGGER.error("JSON file is not found", e);
        }
    }


    protected abstract List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema);

    protected abstract List<SqlObject> generateSqlObjects(JSONArray tables, String sourceSchema, String targetSchema, boolean useTransformation);


    List<String> getPrimaryKey(JSONObject o) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray primaryKeys = (JSONArray) o.get("primaryKeys");
            for (int i = 0; i < primaryKeys.length(); i++) {
                String key = primaryKeys.getString(i);
                if (!isArtificalKey(key)) {
                    list.add(key);
                }
            }

        } catch (JSONException e) {
            LOGGER.debug("No primary key definition in the table", e);
        }
        return list;
    }

    public final List<SqlObject> getSqlObjects() {
        return sqlObjects;
    }


    String getSourceTableName(JSONObject jsonObject) {
        return (String) jsonObject.get("sourceName");
    }

    String getTargetTableName(JSONObject jsonObject) {
        return (String) jsonObject.get("targetName");
    }

    String getSourceColumnName(JSONObject jsonObject) {
        return (String) jsonObject.get("sourceName");
    }

    String getTargetColumnName(JSONObject jsonObject) {
        return (String) jsonObject.get("targetName");
    }

    String getTransformation(JSONObject jsonObject) {
        return (String) jsonObject.get("transformation");
    }

    String getTargetColumnType(JSONObject jsonObject) {
        return (String) jsonObject.get("targetType");
    }

    String getSourceColumnType(JSONObject jsonObject) {
        return (String) jsonObject.get("sourceType");
    }

    boolean isArtificalKey(String v) {
        return v.equals("ARTIFICIAL_ETL_ADD_KEY");
    }

    boolean isTargetTechnicalColumn(String select) {
        return select.contains("load_id") ||
                select.contains("extract_dttm") ||
                select.contains("load_dttm");
    }


}
