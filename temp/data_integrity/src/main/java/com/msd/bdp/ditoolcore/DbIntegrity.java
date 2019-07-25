/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore;

import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class wraps the main data integrity tool features.
 */
public class DbIntegrity {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbIntegrity.class);
    private final OutputUtils outputUtils;
    private static final int MAX_CHUNK_SIZE = 50000;

    private String sourceDbUrl;
    private String sourceDbUser;
    private String sourceDbPassword;

    private String targetDbUrl;
    private String targetDbUser;
    private String targetDbPassword;

    private int connectionPool;
    private String kerbKeyTab;
    private String kerbPrincipal;


    public DbIntegrity(OutputUtils outputUtils, int connectionPool) {
        this.outputUtils = outputUtils;
        this.connectionPool = connectionPool;
    }

    /**
     * Sets the source connection properties for the DbIntegrity
     *
     * @param sourceDbUrl      source user URL
     * @param sourceDbUser     source database user
     * @param sourceDbPassword source database user password
     * @return returns the DBIntegrity object with source connection details
     */
    public DbIntegrity sourceConnection(String sourceDbUrl, String sourceDbUser, String sourceDbPassword) {
        this.sourceDbUrl = sourceDbUrl;
        this.sourceDbUser = sourceDbUser;
        this.sourceDbPassword = sourceDbPassword;
        return this;
    }

    /**
     * Sets the target connection properties for the DbIntegrity
     *
     * @param targetDbUrl      target database URL
     * @param targetDbUser     target database user
     * @param targetDbPassword target database user password
     * @return returns the DBIntegrity object with target connection details
     */
    public DbIntegrity targetConnection(String targetDbUrl, String targetDbUser, String targetDbPassword) {
        this.targetDbUrl = targetDbUrl;
        this.targetDbUser = targetDbUser;
        this.targetDbPassword = targetDbPassword;
        return this;
    }

    /**
     * Sets the kerberos credentials
     *
     * @param kerbKeyTab    kerberos key tab
     * @param kerbPrincipal kerberos key tab principal
     * @return
     */
    public DbIntegrity kerberos(String kerbKeyTab, String kerbPrincipal) {
        this.kerbKeyTab = kerbKeyTab;
        this.kerbPrincipal = kerbPrincipal;
        return this;
    }


    public List<ResultObject> compareDatabase(final List<SqlObject> sqlObjects) throws IOException, ExecutionException, InterruptedException {

        List<ResultObject> tR = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(connectionPool);
        try (DbFacadePool sourceConnPool = new DbFacadePool(sourceDbUrl, sourceDbUser, sourceDbPassword, kerbKeyTab, kerbPrincipal);
             DbFacadePool targetConnPool = new DbFacadePool(targetDbUrl, targetDbUser, targetDbPassword, kerbKeyTab, kerbPrincipal)) {

            final List<Future<ResultObject>> fTR = new ArrayList<>();

            for (final SqlObject sO : sqlObjects) {

                fTR.add(executor.submit(() -> {
                            ResultObject rO;
                            try {
                                //getting the size of the tables. Creating the result object
                                rO = getTableSize(sO, sourceConnPool, targetConnPool);

                                int biggestTableSize = Math.max(rO.sourceTableSize, rO.targetTableSize);
                                if (biggestTableSize > 0) {//we need to test such table
                                    if (rO.sqlObject.isChunksAllowed) { //we can test such tables using chunks
                                        String[] listOfIDs = (rO.sourceTableSize >= rO.targetTableSize)
                                                ? getTheListOfPk(rO.sqlObject, sourceConnPool,rO.sqlObject.sourceTableName, rO.sourceTableSize)
                                                : getTheListOfPk(rO.sqlObject, targetConnPool,rO.sqlObject.targetTableName, rO.targetTableSize);

                                        rO = compareTableByChunks(rO, sourceConnPool, targetConnPool, listOfIDs);

                                    } else if (biggestTableSize <= 500000) {//if table less then 500000 and no PK we can run full check
                                        rO = compareTable(rO, sourceConnPool, targetConnPool);

                                    } else {//table>500000 and no PK we should skip such table
                                        rO.setErrorMessage("Table " + sO.sourceTableName +
                                                " is not checked: table does not have primary key or the number of rows more then 500 000");
                                    }

                                } else {//Table is empty
                                    return rO;
                                }
                                return rO;
                            } catch (Exception e) {
                                LOGGER.error("There was an SQLException while executing table in this Thread", e);
                                return new ResultObject(sO,
                                        -1,
                                        -1,
                                        null,
                                        null,
                                        e.getMessage());
                            }
                        }
                ));
            }
            for (Future<ResultObject> f : fTR) {
                tR.add(f.get());
            }
        } finally {
            executor.shutdown();
        }
        return tR;
    }


    /**
     * This test method get the size of the tables on the source and target databases.
     *
     * @param sqlObjects sql objects, that contain names of the tables
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public List<ResultObject> compareTableSize(final List<SqlObject> sqlObjects) throws InterruptedException, IOException, ExecutionException {
        List<ResultObject> tR = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(connectionPool);

        try (DbFacadePool sourceConnPool = new DbFacadePool(sourceDbUrl, sourceDbUser, sourceDbPassword, kerbKeyTab, kerbPrincipal);
             DbFacadePool targetConnPool = new DbFacadePool(targetDbUrl, targetDbUser, targetDbPassword, kerbKeyTab, kerbPrincipal)) {
            final List<Future<ResultObject>> fTR = new ArrayList<>();
            for (final SqlObject sO : sqlObjects) {
                fTR.add((executor.submit(() ->
                        getTableSize(sO, sourceConnPool, targetConnPool)
                )));
            }
            for (Future<ResultObject> f : fTR) {
                tR.add(f.get());
            }
        }
        return tR;
    }


    private static ResultObject getTableSize(SqlObject sO, DbFacadePool sourceConnPool, DbFacadePool targetConnPool) throws InterruptedException, SQLException, IOException, DiCoreException {
        ExecutorService resultExecutor = Executors.newFixedThreadPool(2);

        DbFacade sourceDf = sourceConnPool.borrowConnectionWithCheck();
        DbFacade targetDf = targetConnPool.borrowConnectionWithCheck();
        try {
            String sourceCondition;
            if(sO.appendSourceSelectString!=null){
                sourceCondition = String.format("%s %s", sO.sourceTableName, sO.appendSourceSelectString);
            } else {
                sourceCondition = sO.sourceTableName;
            }

            Future<Integer> sSize = resultExecutor.submit(() ->
                    sourceDf.getTableRowCount(sourceCondition));

            String targetCondition;
            if(sO.appendTargetSelectString!=null){
                targetCondition = String.format("%s %s", sO.targetTableName, sO.appendTargetSelectString);
            } else {
                targetCondition = sO.targetTableName;
            }

            Future<Integer> tSize = resultExecutor.submit(() ->
                    targetDf.getTableRowCount(targetCondition));
            int sourceSize = sSize.get();
            int targetSize = tSize.get();
            return new ResultObject(sO, sourceSize, targetSize, null);
        } catch (ExecutionException e) {
            return new ResultObject(sO, -1, -1, null, null, e.getMessage());
        } finally {
            sourceConnPool.returnConnection(sourceDf);
            targetConnPool.returnConnection(targetDf);
            resultExecutor.shutdown();
        }


    }

    private String[] getTheListOfPk(SqlObject sO, DbFacadePool connPool, String tableName, int size) throws SQLException, IOException, DiCoreException {
        String query = String.format("select %s from %s  order by %s",
                sO.primaryKey, tableName, sO.primaryKey);
        DbFacade dF = connPool.borrowConnectionWithCheck();
        try {
            return dF.getTheListOfPK(query, size);
        } finally {
            connPool.returnConnection(dF);
        }
    }

    private ResultObject compareTableByChunks(ResultObject rO, DbFacadePool sourceConnPool, DbFacadePool targetConnPool, String[] listOfIDs)
            throws InterruptedException, SQLException, IOException, ExecutionException, DiCoreException {

        int biggestTableSize = Math.max(rO.sourceTableSize, rO.targetTableSize);
        SqlObject sO = rO.sqlObject;
        ExecutorService resultExecutor = Executors.newFixedThreadPool(2);
        String sfName = null;
        String tfName = null;
        int sDiffNumber = 0;
        int tDiffNumber = 0;
        for (int i = 0; i <= listOfIDs.length; i += MAX_CHUNK_SIZE) {

            DbFacade sourceDf = sourceConnPool.borrowConnectionWithCheck();
            DbFacade targetDf = targetConnPool.borrowConnectionWithCheck();

            final String pkStart = listOfIDs[i];
            final String pkStop = ((i + MAX_CHUNK_SIZE) < biggestTableSize)
                    ? listOfIDs[i + MAX_CHUNK_SIZE]
                    : listOfIDs[biggestTableSize - 1];
            boolean lastChunk = i + MAX_CHUNK_SIZE > listOfIDs.length;

            //executing selects in parallel
            List<String> sList;
            List<String> tList;
            try {
                Future<List<String>> fSlist = resultExecutor.submit(() ->
                        sourceDf.getChunk(sO.sourceSqlString,
                                sO.primaryKey, pkStart, pkStop, lastChunk));
                Future<List<String>> fTlist = resultExecutor.submit(() ->
                        targetDf.getChunk(sO.targetSqlString,
                                sO.primaryKey, pkStart, pkStop, lastChunk));
                sList = fSlist.get();
                tList = fTlist.get();

            } finally {
                sourceConnPool.returnConnection(sourceDf);
                targetConnPool.returnConnection(targetDf);
            }

            LOGGER.info("Starting to compare chunk in {} in {} against {}", sO.sourceTableName, sourceDf.getDbType(), targetDf.getDbType());
            sfName = sourceDf.getDbType() + "vs" + targetDf.getDbType() + "_" + sO.shortSourceTableName;
            tfName = targetDf.getDbType() + "vs" + sourceDf.getDbType() + "_" + sO.shortSourceTableName;

            // todo: extract code below to method...
            // difference left-to-right
            List<String> tmpList = DIToolUtilities.getListsDifference(sList, tList);
            //int sDiffNumber;
            if (tmpList != null && !tmpList.isEmpty()) {
                outputUtils.writeToCSV(tmpList, sfName);
                sDiffNumber = tmpList.size();
            } else {
                sDiffNumber = 0;
            }

            // difference right-to-left
            tmpList = DIToolUtilities.getListsDifference(tList, sList);
            //int tDiffNumber;
            if (tmpList != null && !tmpList.isEmpty()) {
                outputUtils.writeToCSV(tmpList, tfName);
                tDiffNumber = tmpList.size();
            } else {
                tDiffNumber = 0;
            }

            // todo: remove this code
            //sDiffNumber += compareLists(sList, tList, sfName);
            //tDiffNumber += compareLists(tList, sList, tfName);

        }
        resultExecutor.shutdown();
        if (sDiffNumber != 0) {
            rO.setSourceDifferenceFileName(sDiffNumber + " " + sfName);
        }
        if (tDiffNumber != 0) {
            rO.setTargetDifferenceFileName(tDiffNumber + " " + tfName);
        }
        return rO;
    }


    private ResultObject compareTable(ResultObject rO, DbFacadePool sourceConnPool, DbFacadePool targetConnPool)
            throws InterruptedException, IOException, ExecutionException, SQLException, DiCoreException {

        SqlObject sO = rO.sqlObject;
        ExecutorService resultExecutor = Executors.newFixedThreadPool(2);
        List<String> sList;
        List<String> tList;
        DbFacade sourceDf = sourceConnPool.borrowConnectionWithCheck();
        DbFacade targetDf = targetConnPool.borrowConnectionWithCheck();
        try {

            Future<List<String>> fSlist = resultExecutor.submit(() ->
                    sourceDf.executeQuery(sO.sourceSqlString));
            Future<List<String>> fTlist = resultExecutor.submit(() ->
                    targetDf.executeQuery(sO.targetSqlString));
            sList = fSlist.get();
            tList = fTlist.get();

        } finally {
            sourceConnPool.returnConnection(sourceDf);
            targetConnPool.returnConnection(targetDf);
            resultExecutor.shutdown();
        }

        LOGGER.info("Starting to compare {} in {} against {}", sO.sourceTableName, sourceDf.getDbType(), targetDf.getDbType());
        String sfName = sourceDf.getDbType() + "vs" + targetDf.getDbType() + "_" + sO.shortSourceTableName;
        String tfName = targetDf.getDbType() + "vs" + sourceDf.getDbType() + "_" + sO.shortSourceTableName;

        // todo: extract code below to method...
        // difference left-to-right
        List<String> tmpList = DIToolUtilities.getListsDifference(sList, tList);
        int sDiffNumber;
        if (tmpList != null && !tmpList.isEmpty()) {
            outputUtils.writeToCSV(tmpList, sfName);
            sDiffNumber = tmpList.size();
        } else {
            sDiffNumber = 0;
        }

        // difference right-to-left
        tmpList = DIToolUtilities.getListsDifference(tList, sList);
        int tDiffNumber;
        if (tmpList != null && !tmpList.isEmpty()) {
            outputUtils.writeToCSV(tmpList, tfName);
            tDiffNumber = tmpList.size();
        } else {
            tDiffNumber = 0;
        }

        if (sDiffNumber != 0) {
            rO.setSourceDifferenceFileName(sDiffNumber + " " + sfName);
        }
        if (tDiffNumber != 0) {
            rO.setTargetDifferenceFileName(tDiffNumber + " " + tfName);
        }
        return rO;
    }


    /**
     * Returns the number of differences found
     *
     * @param result1  first result cto be comparced
     * @param result2  second result to be ccompared
     * @param fileName name of the output file
     * @return the number of differences
     */
    /*
    private synchronized int compareLists(List<String> result1, List<String> result2, String fileName) {
        Collections.sort(result1);
        Collections.sort(result2);
        List<String> difference = ListUtils.subtract(result1, result2);
        if (!difference.isEmpty()) {
            outputUtils.writeToCSV(difference, fileName);
c            return 0;
        }
    }c
    */

}

