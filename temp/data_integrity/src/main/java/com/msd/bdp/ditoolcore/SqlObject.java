/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore;


/**
 * The object that contains the table specific information as:
 * table name,  sql queries to be executed on source and target databases,
 * primaryKey and flag if primaryKey type is allowed.
 */
public class SqlObject {
    /**
     * Source table name
     */
    public final String sourceTableName;

    /**
     * Target table name
     */
    public final String targetTableName;

    /**
     * SQL select that will be executed on the source database
     */
    public final String sourceSqlString;

    /**
     * SQL select that will be executed on the target database
     */
    public final String targetSqlString;

    /**
     * primary key of the table
     */
    public final String primaryKey;

    /**
     * Indicator if testing by chunks is allowed
     */
    public final boolean isChunksAllowed;

    /**
     * Short table name
     */
    public final String shortSourceTableName;

    public final String appendSourceSelectString;

    public final String appendTargetSelectString;

    public int[] pkPositions;

    public SqlObject(String sourceTableName, String targetTableName,
                     String sourceSqlString, String targetSqlString,
                     String primaryKey,
                     boolean isChunksAllowed,String shortSourceTableName) {
        this.targetTableName = targetTableName;
        this.sourceSqlString = sourceSqlString;
        this.targetSqlString = targetSqlString;
        this.sourceTableName = sourceTableName;
        this.primaryKey = primaryKey;
        this.isChunksAllowed = isChunksAllowed;
        this.shortSourceTableName = shortSourceTableName;
        this.appendSourceSelectString = null;
        this.appendTargetSelectString = null;
        this.pkPositions = null;
    }

    public SqlObject(String sourceTableName, String targetTableName,
                     String sourceSqlString, String targetSqlString,
                     String primaryKey,
                     boolean isChunksAllowed,String shortSourceTableName, int...pkPositions) {
        this.targetTableName = targetTableName;
        this.sourceSqlString = sourceSqlString;
        this.targetSqlString = targetSqlString;
        this.sourceTableName = sourceTableName;
        this.primaryKey = primaryKey;
        this.isChunksAllowed = isChunksAllowed;
        this.shortSourceTableName = shortSourceTableName;
        this.appendSourceSelectString = null;
        this.appendTargetSelectString = null;
        this.pkPositions = pkPositions;
    }

    public SqlObject(String sourceTableName, String targetTableName,
                     String sourceSqlString, String targetSqlString,
                     String primaryKey,
                     boolean isChunksAllowed,String shortSourceTableName, String appendSourceSelectString, String appendTargetSelectString ) {
        this.targetTableName = targetTableName;
        this.sourceSqlString = sourceSqlString;
        this.targetSqlString = targetSqlString;
        this.sourceTableName = sourceTableName;
        this.primaryKey = primaryKey;
        this.isChunksAllowed = isChunksAllowed;
        this.shortSourceTableName = shortSourceTableName;
        this.appendSourceSelectString = appendSourceSelectString;
        this.appendTargetSelectString = appendTargetSelectString;
        this.pkPositions = null;
    }


}
