/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.dbcomparer;

import com.msd.bdp.DiToolException;
import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.jsonfacade.*;
import com.msd.bdp.ditoolcore.*;
import com.msd.bdp.ditoolcore.reportimpl.Report;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main {
    private static final int COMPLETED_CODE = 0;
    private static final int FAIL_CODE = 1;
    /**
     * Argument for the json file
     */
    private static final String JSON_SCHEMA_FILE = "jsonFile";
    /**
     * Argument for the JDBC class used for the source connection
     */
    private static final String SOURCE_JDBC_CLASS = "sourceJdbcClass";
    /**
     * Argument for the source database URL
     */
    private static final String SOURCE_DB_URL = "sourceUrl";
    /**
     * Argument for the source database user name
     */
    private static final String SOURCE_DB_USER = "sourceUser";
    /**
     * Argument for the source database password
     */
    private static final String SOURCE_DB_PASS = "sourcePass";
    /**
     * Argument for the source database schema
     */
    private static final String SOURCE_DB_SCHEMA = "sourceSchema";
    /**
     * Argument for the JDBC class used for the source connection
     */
    private static final String TARGET_JDBC_CLASS = "targetJdbcClass";
    /**
     * Argument for the target database URL
     */
    private static final String TARGET_DB_URL = "targetUrl";
    /**
     * Argument for the target database user name
     */
    private static final String TARGET_DB_USER = "targetUser";
    /**
     * Argument for the target database user password
     */
    private static final String TARGET_DB_PASS = "targetPass";
    /**
     * Argument for the target database schema
     */
    private static final String TARGET_DB_SCHEMA = "targetSchema";
    /**
     * Argument to specify if we want to compare results or not
     */
    private static final String COMPARE_RESULTS = "compareResults";
    /**
     * Output folder
     */
    private static final String OUTPUT_FOLDER_PATH = "out";
    /**
     * Argument to specify path to the kerberos keytab
     */
    private static final String KERB_KEYTAB = "kerbKeyTab";
    /**
     * Argument fot the kerberos principal
     */
    private static final String KERB_KEYTAB_PRINCIPAL = "keyTabPrincipal";
    /**
     * Argument fot setting the size of the connection pool
     */
    private static final String CONNECTION_POOL = "pool";
    /**
     * Argument  defines excluded tables
     */
    private static final String EXCLUDE_TABLES = "excludeTable";
    /**
     * Argument  defines  tables, that will be tested
     */
    private static final String TEST_TABLES = "testTables";

    /**
     * Argument for appending source select with the string
     */
    private static final String APPEND_STRING_SOURCE = "appendSource";

    /**
     * Argument for appending target select with the string
     */
    private static final String APPEND_STRING_TARGET = "appendTarget";

    /**
     * Argument for arbitrary limit for expected percentage of difference
     */
    private static final String ARBITRATY_LIMIT = "arbLimit";


    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static OptionSet options;
    private static OptionParser parser;
    private static OptionSpec<Void> help;
    private static OptionSpec<File> jsonInputFileOption;
    private static OptionSpec<String> sourceJdbcClassOption;
    private static OptionSpec<String> sourceUrlOption;
    private static OptionSpec<String> sourceUserOption;
    private static OptionSpec<String> sourcePassOption;
    private static OptionSpec<String> sourceSchemaOption;
    private static OptionSpec<String> targetJdbcClassOption;
    private static OptionSpec<String> targetUrlOption;
    private static OptionSpec<String> targetUserOption;
    private static OptionSpec<String> targetPassOption;
    private static OptionSpec<String> targetSchemaOption;
    private static OptionSpec<String> outputFolderOption;
    private static OptionSpec<Boolean> compareResultsOption;
    private static OptionSpec<String> kerbKeyTab;
    private static OptionSpec<String> kerbKeyTabPrincipal;
    private static OptionSpec<String> excludeTablesOption;
    private static OptionSpec<String> testTablesOption;
    private static OptionSpec<String> appendSourceStringOption;
    private static OptionSpec<String> appendTargetStringOption;
    private static OptionSpec<Double> arbitraryLimitOption;

    private static OptionSpec<Boolean> takeLatestSnapshotOption;

    private static OptionSpec<Integer> poolOption;


    public static void main(String args[]) throws Exception {
        setUpOptions(args);

        if (options.has(help)) {
            parser.printHelpOn(System.out);
            System.exit(COMPLETED_CODE);
        }

        List<String> excludeTables = options.valueOf(excludeTablesOption) == null ?
                null :
                Arrays.asList(options.valueOf(excludeTablesOption).split(","));
        List<String> testTables = options.valueOf(testTablesOption) == null ?
                null :
                Arrays.asList(options.valueOf(testTablesOption).split(","));


        File jsonFile = options.valueOf(jsonInputFileOption);
        String sourceUrl = options.valueOf(sourceUrlOption);
        String sourceUser = options.valueOf(sourceUserOption);
        String sourceSchema = options.valueOf(sourceSchemaOption);
        String targetUrl = options.valueOf(targetUrlOption);
        String targetUser = options.valueOf(targetUserOption);
        String targetSchema = options.valueOf(targetSchemaOption);
        Double expLimit = options.valueOf(arbitraryLimitOption);

        Boolean testSmoke = !options.valueOf(compareResultsOption);

        Boolean takeLatestSnapshot = options.valueOf(takeLatestSnapshotOption);

        OutputUtils outputUtils = new OutputUtils(options.valueOf(outputFolderOption));
        DbIntegrity dI = new DbIntegrity(outputUtils, options.valueOf(poolOption))
                .sourceConnection(sourceUrl,
                        sourceUser, options.valueOf(sourcePassOption))
                .targetConnection(targetUrl,
                        targetUser, options.valueOf(targetPassOption))
                .kerberos(options.valueOf(kerbKeyTab), options.valueOf(kerbKeyTabPrincipal));
        Report r = new Report(outputUtils, testSmoke);
        r.setExpDiff(expLimit);
        List<ResultObject> result;
        List<SqlObject> sOs;

        try {
            //initializing the jdbc classes
            Class.forName(options.valueOf(sourceJdbcClassOption));
            Class.forName(options.valueOf(targetJdbcClassOption));

            //this configuration is needed to allow hive to connect through kerberos
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

            //based on the source database, generation the sql objects
            switch (DIToolUtilities.getDatabaseType(options.valueOf(sourceUrlOption))) {
                case ORACLE:
                    sOs = new OracleJsonReader(jsonFile, sourceSchema, targetSchema).getSqlObjects();
                    break;
                case SQLSERVER:
                    sOs = new SqlserverJsonReader(jsonFile, sourceSchema, targetSchema).getSqlObjects();
                    break;
                case HIVE:
                    sOs = new HiveJsonReader(jsonFile, sourceSchema, targetSchema).getSqlObjects();
                    break;
                case HANA:
                    sOs = new HanaJsonReader(jsonFile, sourceSchema, targetSchema).getSqlObjects();
                    break;
                case DB2:
                    sOs = new Db2JsonReader(jsonFile, sourceSchema, targetSchema).getSqlObjects();
                    break;
                default:
                    throw new DiToolException("Unable to create the sql objects. Json reader is not implemented fot " +
                            "such database");
            }

            List<SqlObject> updateSqlObjects = new ArrayList<>();
            //if some tables need to be excluded from the scope
            if (excludeTables != null && !excludeTables.isEmpty()) {
                for (SqlObject o : sOs) {
                    if (!DIToolUtilities.isInList(o.sourceTableName, excludeTables) &&
                            !DIToolUtilities.isInList(o.targetTableName, excludeTables) &&
                            !DIToolUtilities.isInList(o.shortSourceTableName, excludeTables)) {
                        updateSqlObjects.add(o);
                    }
                }
                //if we want to test only some tables from the scope
            } else if (testTables != null && !testTables.isEmpty()) {
                for (SqlObject o : sOs) {
                    if (DIToolUtilities.isInList(o.sourceTableName, testTables) ||
                            DIToolUtilities.isInList(o.targetTableName, testTables) ||
                            DIToolUtilities.isInList(o.shortSourceTableName, testTables)) {
                        updateSqlObjects.add(o);
                    }
                }
            } else {
                updateSqlObjects = sOs;
            }

            //if we want to append string to the sql select
            if (!StringUtils.isBlank(options.valueOf(appendSourceStringOption)) || !StringUtils.isBlank(options.valueOf(appendTargetStringOption)) || takeLatestSnapshot) {
                updateSqlObjects = appendSelectString(updateSqlObjects, options.valueOf(appendSourceStringOption), options.valueOf(appendTargetStringOption), takeLatestSnapshot);
            }


            long startTime = System.currentTimeMillis();
            //running the compare test suite, that include smoke test (compare the tables sizes) and data integrity test
            if (!testSmoke) {
                result = dI.compareDatabase(updateSqlObjects);
            } else {
                //running only the smoke test
                result = dI.compareTableSize(updateSqlObjects);
            }

            long stopTime = System.currentTimeMillis();

            //creating the html report
            r.createHtmlReport(sourceUrl, sourceUser, sourceSchema,
                    targetUrl, targetUser, targetSchema, jsonFile.getName(),
                    Long.toString(TimeUnit.MILLISECONDS.toSeconds(stopTime - startTime)) + " seconds",
                    result);

            if (!r.isTestPassed()) {
                LOGGER.info("Data integrity test has failed. Please check the results");
                System.exit(FAIL_CODE);
            }
            LOGGER.info("Test completed");
            System.exit(COMPLETED_CODE);


        } catch (Exception e) {
            LOGGER.error("Failed to run the test", e);
            System.exit(FAIL_CODE);
        }

    }

    private static void setUpOptions(String args[]) {
        synchronized (Main.class) {
            parser = new OptionParser();
            help = parser.acceptsAll(Arrays.asList("help", "h", "?"), "show help").forHelp();
            jsonInputFileOption = parser.accepts(JSON_SCHEMA_FILE, "Input JSON file with database tables")
                    .withRequiredArg().ofType(File.class).required();

            sourceJdbcClassOption = parser.accepts(SOURCE_JDBC_CLASS, "JDBC class to initialize the Source JDBC Driver")
                    .withRequiredArg().ofType(String.class).required();
            sourceUrlOption = parser.accepts(SOURCE_DB_URL, "Source database URL.")
                    .withRequiredArg().ofType(String.class).required();
            sourceUserOption = parser.accepts(SOURCE_DB_USER, "Source database user")
                    .withRequiredArg().ofType(String.class);
            sourcePassOption = parser.accepts(SOURCE_DB_PASS, "Source database user password")
                    .withRequiredArg().ofType(String.class);
            sourceSchemaOption = parser.accepts(SOURCE_DB_SCHEMA, "Source database schema")
                    .withRequiredArg().ofType(String.class);


            targetJdbcClassOption = parser.accepts(TARGET_JDBC_CLASS, "JDBC class to initialize the Target JDBC Driver")
                    .withRequiredArg().ofType(String.class).required();
            targetUrlOption = parser.accepts(TARGET_DB_URL, "Target database URL")
                    .withRequiredArg().ofType(String.class).required();
            targetUserOption = parser.accepts(TARGET_DB_USER, "Target database user")
                    .withRequiredArg().ofType(String.class);
            targetPassOption = parser.accepts(TARGET_DB_PASS, "Target database user password")
                    .withRequiredArg().ofType(String.class);
            targetSchemaOption = parser.accepts(TARGET_DB_SCHEMA, "Target database schema")
                    .withRequiredArg().ofType(String.class);


            outputFolderOption = parser.accepts(OUTPUT_FOLDER_PATH, "Path to the output folder for storing csv files.")
                    .withRequiredArg().ofType(String.class);
            compareResultsOption = parser.accepts(COMPARE_RESULTS, "Compare results between source Database" +
                    " and Hive. If set to false compare only the number of rows ")
                    .withRequiredArg().ofType(Boolean.class).defaultsTo(false);
            kerbKeyTab = parser.accepts(KERB_KEYTAB, "Path to the KeyTab for kerberos Authentication")
                    .withRequiredArg().ofType(String.class);
            kerbKeyTabPrincipal = parser.accepts(KERB_KEYTAB_PRINCIPAL, "Kerberos principal")
                    .withRequiredArg().ofType(String.class);

            excludeTablesOption = parser.accepts(EXCLUDE_TABLES, "List to exclude the tables from the input. " +
                    "Use \",\" as a separator")
                    .withRequiredArg().ofType(String.class);
            testTablesOption = parser.accepts(TEST_TABLES, "List tables that will be  tested  from the input." +
                    " Use \",\" as a separator")
                    .withRequiredArg().ofType(String.class);

            poolOption = parser.accepts(CONNECTION_POOL, "Connection pool - Number of separated threads in which " +
                    "the tool will rin the integrity. Can have influence on the performance")
                    .withRequiredArg().ofType(Integer.class).defaultsTo(1);

            appendSourceStringOption = parser.accepts(APPEND_STRING_SOURCE, "String to append the source select")
                    .withRequiredArg().ofType(String.class);
            appendTargetStringOption = parser.accepts(APPEND_STRING_TARGET, "String to append the target select")
                    .withRequiredArg().ofType(String.class);

            arbitraryLimitOption = parser.accepts(ARBITRATY_LIMIT, "Acceptable percentage of difference")
                    .withRequiredArg().ofType(Double.class).defaultsTo(3d);

            takeLatestSnapshotOption = parser.accepts("useLatestSnap", "Takes the latest snapshot")
                    .withRequiredArg().ofType(Boolean.class).defaultsTo(false);

            options = parser.parse(args);
        }
    }

    /**
     * append source and target selects with the defined string
     *
     * @param sOs
     * @param appendSourceString
     * @param appendTargetString
     * @return
     */
    private static List<SqlObject> appendSelectString(List<SqlObject> sOs, String appendSourceString, String appendTargetString, boolean useLatest) {


        List<SqlObject> updatedOs = new ArrayList<>();
        for (SqlObject o : sOs) {
            String selectTemp = appendSourceString;
            if (useLatest) {
                selectTemp = String.format("where load_dttm in (select max(load_dttm) from %s ) %s", o.sourceTableName,
                        StringUtils.isBlank(appendSourceString)? "": " and " +
                        appendSourceString.replaceAll("(?i)where", "")).trim();
            }
            updatedOs.add(new SqlObject(o.sourceTableName,
                    o.targetTableName,
                    StringUtils.isBlank(selectTemp) ? o.sourceSqlString : o.sourceSqlString + " " + selectTemp,
                    StringUtils.isBlank(appendTargetString) ? o.targetSqlString : o.targetSqlString + " " + appendTargetString,
                    o.primaryKey,
                    o.isChunksAllowed,
                    o.shortSourceTableName,
                    selectTemp,
                    appendTargetString));
        }
        return updatedOs;
    }


}


