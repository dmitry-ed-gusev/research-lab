/*
 * Copyright © 2018 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */

package com.msd.bdp.ditoolcore;

import com.msd.bdp.ditoolcore.dbfacade.DbType;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/***/
public final class DIToolUtilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(DIToolUtilities.class);

    private static final String JDBC_PREFIX = "jdbc:";
    private static final String ORACLE_PREFIX = "oracle";
    private static final String SQLSERVER_PREFIX = "sqlserver";
    private static final String TERADATA_PREFIX = "teradata";
    private static final String HIVE_PREFIX = "hive2";
    private static final String HANNA_PREFIX = "sap";
    private static final String AS400_PREFIX = "as400";
    //private static final int    COMPLETED_CODE   = 0;
    public static final int FAIL_CODE = -3;

    private DIToolUtilities() {
    }

    /**
     * Convert first row of ResultSet object to CSV.
     *
     * @param rs ResultSet
     * @return String
     * @throws SQLException
     */
    public static String convertResultSetToCSV(ResultSet rs) throws SQLException {
        LOGGER.debug("DIToolUtilities.convertResultSetToCSV() is working.");

        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();

        StringBuilder csvString = new StringBuilder();
        for (int i = 1; i <= colCount; i++) {
            csvString.append(rs.getString(i));
            if (i != colCount) {
                csvString.append(",");
            }
        }

        return csvString.toString();
    }

    /***/
    public static List<String> getListsDifference(List<String> list1, List<String> list2) {
        return ListUtils.subtract(list1, list2);
    }

    public static String backQuote(String value) {
        return String.format("`%s`", value);
    }

    public static DbType getDatabaseType(String dbUrl) {
        String[] dbType = getDBType(dbUrl);
        switch (dbType[0].toLowerCase()) {
            case ORACLE_PREFIX:
                return DbType.ORACLE;
            case TERADATA_PREFIX:
                return DbType.TERADATA;
            case HIVE_PREFIX:
                return DbType.HIVE;
            case HANNA_PREFIX:
                return DbType.HANA;
            case SQLSERVER_PREFIX:
                return DbType.SQLSERVER;
            case AS400_PREFIX:
                return DbType.DB2;
            default:
                if (dbType.length > 1 && SQLSERVER_PREFIX.equals(dbType[1])) {
                    return DbType.SQLSERVER;
                }
                throw new IllegalArgumentException("Unsupported database!");
        }
    }

    private static String[] getDBType(String databaseUri) {
        if (databaseUri != null && databaseUri.startsWith(JDBC_PREFIX)) {
            String[] parts = databaseUri.split(":");
            if (parts.length == 2) {
                return new String[]{parts[1]};
            } else if (parts.length > 2) {
                return new String[]{parts[1], parts[2]};
            }
        }

        throw new IllegalArgumentException("The URI [" + databaseUri + "] is not a JDBC database");
    }

    public static String singleQuote(String value) {
        return String.format("'%s'", value);
    }

    public static String doubleQuote(String value) {
        return String.format("\"%s\"", value);
    }

    public static boolean isInList(String value, List<String> list) {
        return list.stream().anyMatch(item -> item.equalsIgnoreCase(value));
    }

}
