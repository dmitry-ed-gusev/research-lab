package com.msd.bdp.csvDbIntegrity;

import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.jsonfacade.CsvJsonReader;
import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.ResultObject;
import com.msd.bdp.ditoolcore.SqlObject;
import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import com.msd.bdp.ditoolcore.reportimpl.Report;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    /**
     * Argument for the json file
     */
    private static final String PATH_TO_THE_CSV_FOLDER = "csvRoot";

    /**
     * Argument for the json file
     */
    private static final String JSON_SCHEMA_FILE = "jsonFile";

    /**
     * Argument for the target database URL
     */
    private static final String TARGET_DB_URL = "targetUrl";

    /**
     * Argument for the target database schema
     */
    private static final String TARGET_DB_SCHEMA = "targetSchema";

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

    private static final String HAS_HEADER = "hasHeader";

    private static final String DELIMETER = "delimeter";

    private static final String QUOTE_CHAR = "quoteChar";

    private static final String PROJECT = "project";

    private static final String DELTA = "delta";


    private static final Logger LOGGER = LoggerFactory.getLogger(com.msd.bdp.csvDbIntegrity.Main.class);
    private static OptionSet options;
    private static OptionParser parser;
    private static OptionSpec<Void> help;
    private static OptionSpec<File> jsonInputFileOption;
    private static OptionSpec<File> pathToCsvFolderOption;
    private static OptionSpec<String> targetUrlOption;
    private static OptionSpec<String> targetSchemaOption;

    private static OptionSpec<String> outputFolderOption;

    private static OptionSpec<String> kerbKeyTab;
    private static OptionSpec<String> kerbKeyTabPrincipal;
    private static OptionSpec<Boolean> hasHeaderOption;
    private static OptionSpec<Boolean> deltaOption;
    private static OptionSpec<String> separatorOption;
    private static OptionSpec<String> quoteCharOption;
    private static OptionSpec<String> projectOption;
    private static OutputUtils outputUtils;


    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, DiCoreException {
        setUpOptions(args);

        if (options.has(help)) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
        File jsonFile = options.valueOf(jsonInputFileOption);
        File pathToCsvRoot = options.valueOf(pathToCsvFolderOption);
        String targetUrl = options.valueOf(targetUrlOption);
        String targetSchema = options.valueOf(targetSchemaOption);
        char separator = options.valueOf(separatorOption).charAt(0);
        char quoteChar = options.valueOf(quoteCharOption).charAt(0);


        outputUtils = new OutputUtils(options.valueOf(outputFolderOption));

        List<SqlObject> sOs = new CsvJsonReader(jsonFile, null, targetSchema, true).getSqlObjects();
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        List<ResultObject> result = new ArrayList<>();
        for (SqlObject sO : sOs) {
            List<File> files = getListOfFiles(pathToCsvRoot, sO.targetTableName);

            if(files.isEmpty()){
                break;
            }


            List<String> csvData = CsvDataParser.parse(files,options.valueOf(hasHeaderOption),
                    sO.pkPositions,
                    FunctionProjectList.get(options.valueOf(projectOption)),
                    separator, quoteChar, options.valueOf(deltaOption));


            List<String> resultfromDb;

            try (DbFacadePool targetConnPool = new DbFacadePool(targetUrl, "", "", options.valueOf(kerbKeyTab), options.valueOf(kerbKeyTabPrincipal));
                 DbFacade f = targetConnPool.borrowConnection()) {
                resultfromDb = f.executeQuery(sO.targetSqlString);

            }
            LOGGER.info("Found {} rows in the hive database {} table {}", resultfromDb.size(), targetSchema, sO.targetTableName);


            int csvToHive = compareLists(csvData, resultfromDb, "csvToHive-" + sO.targetTableName);
            int hiveToCsv = compareLists(resultfromDb, csvData, "hiveToCsv-" + sO.targetTableName);
            if (csvToHive != 0 || hiveToCsv != 0) {
                result.add(new ResultObject(sO, csvData.size(), resultfromDb.size(),
                        csvToHive == 0 ? " " : "csvToHive-" + sO.targetTableName,
                        hiveToCsv == 0 ? "" : "hiveToCsv-" + sO.targetTableName,
                        null));
            } else {
                result.add(new ResultObject(sO, csvData.size(), resultfromDb.size(), null, null, null));
            }
        }


        Report report = new Report(outputUtils, false);
        report.createHtmlReport(pathToCsvRoot.getName(), "", "",
                targetUrl, "", targetSchema,
                jsonFile.getName(), "-", result);
    }


    private static int compareLists(List<String> result1, List<String> result2, String fileName) {
        Collections.sort(result1);
        Collections.sort(result2);
        List<String> difference = ListUtils.subtract(result1, result2);
        if (!difference.isEmpty()) {
            outputUtils.writeToCSV(difference, fileName);
            return difference.size();
        } else {
            return 0;
        }
    }

    private static void setUpOptions(String[] args) {
        synchronized (com.msd.bdp.dbcomparer.Main.class) {
            parser = new OptionParser();
            help = parser.acceptsAll(Arrays.asList("help", "h", "?"), "show help").forHelp();
            jsonInputFileOption = parser.accepts(JSON_SCHEMA_FILE, "Input JSON file with database tables")
                    .withRequiredArg().ofType(File.class).required();
            pathToCsvFolderOption = parser.accepts(PATH_TO_THE_CSV_FOLDER, "Path to the CSV folder or file")
                    .withRequiredArg().ofType(File.class).required();

            targetUrlOption = parser.accepts(TARGET_DB_URL, "Target database URL")
                    .withRequiredArg().ofType(String.class).required();

            targetSchemaOption = parser.accepts(TARGET_DB_SCHEMA, "Target database schema")
                    .withRequiredArg().ofType(String.class);


            outputFolderOption = parser.accepts(OUTPUT_FOLDER_PATH, "Path to the output folder for storing csv files.")
                    .withRequiredArg().ofType(String.class);

            kerbKeyTab = parser.accepts(KERB_KEYTAB, "Path to the KeyTab for kerberos Authentication")
                    .withRequiredArg().ofType(String.class);
            kerbKeyTabPrincipal = parser.accepts(KERB_KEYTAB_PRINCIPAL, "Kerberos principal")
                    .withRequiredArg().ofType(String.class);
            hasHeaderOption = parser.accepts(HAS_HEADER, "CSV has header?")
                    .withRequiredArg().ofType(Boolean.class).defaultsTo(false);
            deltaOption = parser.accepts(DELTA, "Have delta ingest")
                    .withRequiredArg().ofType(Boolean.class).defaultsTo(false);
            separatorOption = parser.accepts(DELIMETER, "Separator").withRequiredArg().ofType(String.class).defaultsTo(",");

            quoteCharOption = parser.accepts(QUOTE_CHAR, "Quote char").withRequiredArg().ofType(String.class).defaultsTo("\"");

            projectOption = parser.accepts(PROJECT, "Project").withRequiredArg().ofType(String.class).defaultsTo("MMD");


            options = parser.parse(args);
        }
    }

    private static  List<File> getListOfFiles(File pathToCsvRoot, String tableName) {
        List<File> files = new ArrayList<>();
        if (pathToCsvRoot.isDirectory()) {

            File[] tables = pathToCsvRoot.listFiles();
            assert tables != null;
            for (File table : tables) {
                    if(table.getName().equalsIgnoreCase(tableName)){
                        File[] loads = table.listFiles();
                        Arrays.sort(loads);
                        for (File load:loads){
                            File[] csv = load.listFiles();
                            if(csv!=null) {
                                files.addAll(Arrays.asList(csv));
                            }
                        }

                    }
            }
        }
        return files;
    }


}
