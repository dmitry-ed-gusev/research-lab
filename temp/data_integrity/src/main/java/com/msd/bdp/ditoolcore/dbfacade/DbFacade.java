/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.dbfacade;

import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.DiCoreException;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;


/**
 * Abstract class to build the DB connection
 * Should be extended via specific DB facades implementing specific functionality.
 */
public abstract class DbFacade implements Closeable {


    private static final Logger LOGGER = LoggerFactory.getLogger(DbFacade.class);
    protected final Connection connection;
    final DbType dbType;

    DbFacade(Connection connection, DbType dbType) {
        this.connection = connection;
        this.dbType = dbType;
    }


    /**
     * Created the connection facade for the particular database
     *
     * @param dbUri      database URL
     * @param userName   database user name
     * @param password   database password
     * @param kerbKeyTab kerberos key tab path(mainly used for hive connection)
     * @param kerbAlias  kerberos key tab principal (mainly used for hive connection)
     * @return returns the database facade
     * @throws SQLException
     * @throws IOException
     */
    public static DbFacade createFacade(String dbUri, String userName, String password, String kerbKeyTab,
                                        String kerbAlias) throws SQLException, IOException, DiCoreException {
        LOGGER.info("Connecting to {}", dbUri);

        DbType dbType = DIToolUtilities.getDatabaseType(dbUri);
        switch (dbType) {
            case ORACLE:
                return new OracleFacade(DriverManager.getConnection(dbUri, userName, password));
            case HIVE:
                return new HiveFacade(getKerberosConnection(dbUri, kerbKeyTab, kerbAlias));
            case SQLSERVER:
                return new MssqlFacade(DriverManager.getConnection(dbUri, userName, password));
            case TERADATA:
                return new TeradataFacade(DriverManager.getConnection(dbUri, userName, password));
            case HANA:
                return new HanaFacade(DriverManager.getConnection(dbUri, userName, password));
            case DB2:
                return new Db2Facade(DriverManager.getConnection(dbUri, userName, password));
            default:
                throw new DiCoreException("Unable to create database facade. Unknown database");
        }
    }

    /**
     * Method to make Kerberos connection to the hive database running on the cluster
     *
     * @param dbUrl
     * @param kerbKeyTab
     * @param kerbAlias
     * @return
     * @throws IOException
     */
    private static Connection getKerberosConnection(String dbUrl, String kerbKeyTab, String kerbAlias) throws IOException, SQLException {
        LOGGER.info("KERBEROS: Trying to obtain the ticket");

        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.
                loginUserFromKeytab(kerbAlias, kerbKeyTab);
        LOGGER.info("KERBEROS: Ticket is obtained");
        return DriverManager.getConnection(dbUrl, "", "");

    }

    /**
     * Get the jdbc connection.
     *
     * @return
     */
    public final Connection getConnection() {
        return connection;
    }

    /**
     * Get the database type.
     *
     * @return
     */
    public final DbType getDbType() {
        return dbType;
    }


    /**
     * Execute the query agains the database.
     *
     * @param query
     * @return The list of results.
     * @throws SQLException
     */
    public List<String> executeQuery(String query) throws SQLException {
        List<String> results = new LinkedList<>();
        LOGGER.info("{}: running the sql: {} ", dbType, query);
        int pr = 0;
        try (final Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                pr++;
                if (pr % 100000 == 0) {
                    LOGGER.info("{}: Collecting... {}", dbType, pr);
                }
                StringJoiner result = new StringJoiner("<:>");
                for (int i = 1; i <= columnsNumber; i++) {
                    String value;
                    value = (rs.getString(i) == null) ? "null" : rs.getString(i);
                    result.add(value.trim());
                }
                results.add(">>" + result.toString() + "<<");
            }
            LOGGER.info("{}: collected data for {} rows.", dbType, results.size());
            return results;
        }
    }

    /**
     * Method to get the table size
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getTableRowCount(String tableName) throws SQLException {
        int result = -1;
        String query = "select count(*) from " + tableName;
        try (final Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            rs.next();
            result = rs.getInt(1);
        } finally {
            LOGGER.info("{}: SQL: select {}. RESULT: {}", dbType, query, result);
        }

        return result;
    }

    /**
     * This method append primary key restrictions to the select
     *
     * @param query     query to be run on the database
     * @param pk        primary key
     * @param pkStart   primary key from
     * @param pkStop    primary key to
     * @param lastChunk if the chunk is the last
     * @return the list of results
     * @throws SQLException
     */
    public List<String> getChunk(String query, String pk, String pkStart, String pkStop, boolean lastChunk) throws SQLException {
        String updatedQuery;
        if (query.contains(" where ") || query.contains(" WHERE ")) {
            updatedQuery = String.format("%s and %s>=%s and %s%s%s order by %s", query, pk, pkStart, pk, lastChunk ? "<=" : "<", pkStop, pk);
        } else {
            updatedQuery = String.format("%s where %s>=%s and %s%s%s order by %s", query, pk, pkStart, pk, lastChunk ? "<=" : "<", pkStop, pk);
        }
        return executeQuery(updatedQuery);
    }

    /**
     * This method get the list of private keys
     *
     * @param query
     * @param expectedSize
     * @return the String collection of the primary keys
     * @throws SQLException
     */
    public String[] getTheListOfPK(String query, int expectedSize) throws SQLException {
        int counter = 0;
        String[] resultArray = new String[expectedSize];

        try (final Statement stm = connection.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                String result = rs.getString(1);
                resultArray[counter] = result;
                counter++;
            }
        }
        LOGGER.info("{}: SQL: {}. Collected {} PK.", dbType, query, counter);
        return resultArray;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                //SWALLOW
            }
        } else {
            throw new IllegalStateException("No connection to close.");
        }
    }

    public boolean isAlive() {
        if (connection != null) {
            try (final Statement stm = connection.createStatement()) {
                stm.execute("SELECT 1");
                return true;
            } catch (SQLException e) {
                return e.getMessage().contains("FROM keyword not found");
            }
        } else {
            return false;
        }
    }

}
