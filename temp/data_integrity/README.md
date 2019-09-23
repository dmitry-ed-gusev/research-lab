JDBC Database Comparer
===============

This tool is available at https://stash.merck.com/projects/SVT/repos/data_integrity and can be used for comparing data between:
 1. source database (Oracle or SqlServer) with Hive
 2. Hive and Teradata warehouse

# Requires:
   * JDK 8
   * For building locally maven plugin is required

# Usage:
   * Build locally or download the latest version from http://nexus.gin.merck.com:8081/content/repositories/com.msd.gin.bdp-RELEASES/com/msd/gin/bdp/DB-Comparer/
   * Go to the target folder
   * Download the DB jdbc drivers for the specific DB. For Jenkins job configuration search for drivers at http://nexus.gin.merck.com:8081/, for example search for 'tera' for teradata DB drivers.
   * Run java -jar dbComparer -help to see the options.

## Options:
-jsonFile:          Input JSON file with database tables
-dbUrl:             database URL
-dbUser:            database user
-dbPass:            database user password
-hiveUrl:           Hive database URL
-hiveDb:            Hive database name
-jdbcClass:         class name of jdbc driver
-compareResults:    flag to test full set of data ('true') or number of rows only ('false'). Optional parameter, 'true' by default.
-out:               Path to the output folder for storing csv files. Optional parameter.

## Example usage:
1. Comparing of the full set of data between Oracle and Hive:
> windows
 ```
 java -cp ojdbc14-10.1.0.5.jar;DB-Comparer-2.0.4-jar-with-dependencies.jar com.msd.bdp.dbcomparer.Main -jsonFile TWS_ORACLE_HIVE.json -dbUrl jdbc:oracle:thin:@lwsnst0031.merck.com:25881:MMDTST19 -dbUser username -dbPass password -hiveDb mmd_test_trackwise_gqts_global_consumer_latest -hiveUrl jdbc:hive2://knox-01.bdptest.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdptest.gin.merck.com@MERCK.COM -compareResults true -jdbcClass oracle.jdbc.OracleDriver
 ```
> unix-like system
 ```
 java -cp ojdbc14-10.1.0.5.jar:DB-Comparer-2.0.4-jar-with-dependencies.jar com.msd.bdp.dbcomparer.Main -jsonFile TWS_ORACLE_HIVE.json -dbUrl jdbc:oracle:thin:@lwsnst0031.merck.com:25881:MMDTST19 -dbUser username -dbPass password -hiveDb mmd_test_trackwise_gqts_global_consumer_latest -hiveUrl jdbc:hive2://knox-01.bdptest.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdptest.gin.merck.com@MERCK.COM -compareResults true -jdbcClass oracle.jdbc.OracleDriver
 ```
2. Comparing the row number between Hive and Teradata:
> windows
 ```
 java -cp DB-Comparer-1.0.0-jar-with-dependencies.jar;* com.msd.bdp.dbcomparer.Main -jsonFile tmp.json -dbUrl jdbc:teradata://TDWTST01/CHARSET=UTF16 -dbUser username -dbPass password -hiveDb mmd_test_trackwise_gqts_global_curated -hiveUrl jdbc:hive2://knox-01.bdptest.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdptest.gin.merck.com@MERCK.COM -compareResults false -jdbcClass com.teradata.jdbc.TeraDriver
 ```
> unix-like system
 ```
 java -cp DB-Comparer-1.0.0-jar-with-dependencies.jar:* com.msd.bdp.dbcomparer.Main -jsonFile tmp.json -dbUrl jdbc:teradata://TDWTST01/CHARSET=UTF16 -dbUser username -dbPass password -hiveDb mmd_test_trackwise_gqts_global_curated -hiveUrl jdbc:hive2://knox-01.bdptest.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdptest.gin.merck.com@MERCK.COM -compareResults false -jdbcClass com.teradata.jdbc.TeraDriver
 ```

