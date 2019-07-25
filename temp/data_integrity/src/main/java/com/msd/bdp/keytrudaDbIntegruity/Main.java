package com.msd.bdp.keytrudaDbIntegruity;

import com.msd.bdp.DiToolException;
import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.jsonfacade.CsvJsonReader;
import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.ResultObject;
import com.msd.bdp.ditoolcore.SqlObject;
import com.msd.bdp.ditoolcore.reportimpl.Report;
import com.msd.bdp.keytrudaDbIntegruity.keytrudaCsvCore.BibFmcSystems.CsvToDbdataIntegrity;
import com.msd.bdp.keytrudaDbIntegruity.keytrudaCsvCore.KdhSystems.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Main {

    private static OptionSet options;
    private static OptionParser parser;
    private static OptionSpec<Void> help;

    private static OptionSpec<String> mappingFileOption;
    private static OptionSpec<String> dataFileFolder;

    private static OptionSpec<String> configFileFolder;

    private static OptionSpec<String> targetUrlOption;
    private static OptionSpec<String> targetSchemaOption;

    private static OptionSpec<String> kerbKeyTab;
    private static OptionSpec<String> kerbKeyTabPrincipal;

    private static OptionSpec<File> jsonInputFileOption;

    private static OptionSpec<String> systemOption;

    private static OutputUtils outputUtils;

    private static String dbUrl;
    private static String schema;
    private static String keyTab;
    private static String keyPrincipal;
    private static File jsonFile;
    private static String targetSchema;

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws ClassNotFoundException, IOException, DiCoreException {

        setUpOptions(args);
        outputUtils = new OutputUtils(null);


        if (options.has(help)) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        dbUrl = options.valueOf(targetUrlOption);
        schema = options.valueOf(targetSchemaOption);
        keyTab = options.valueOf(kerbKeyTab);
        keyPrincipal = options.valueOf(kerbKeyTabPrincipal);
        jsonFile = options.valueOf(jsonInputFileOption);
        targetSchema = options.valueOf(targetSchemaOption);

        String system = options.valueOf(systemOption);

        if (system.equalsIgnoreCase("FMC")) {
            bibFmcSystems();
        }
        else if (system.equalsIgnoreCase("KDH")) {
            kdhSystem();
        } else throw new DiToolException("Unknown system " + system);


    }


    private static void kdhSystem() throws IOException {


        Properties dbP = new Properties();
        dbP.setProperty("dbUrl", dbUrl);
        dbP.setProperty("dbSchema", schema);
        dbP.setProperty("krbTab", keyTab);
        dbP.setProperty("drbPrincipal", keyPrincipal);

        File configRootFolder = new File(options.valueOf(configFileFolder));
        File[] fList = configRootFolder.listFiles();

        List<ResultObject> listOfResults = new ArrayList<>();
        List<SqlObject> sOs = new CsvJsonReader(jsonFile, null, targetSchema).getSqlObjects();
        for (File file : fList) {
            String tableName = getFileNameFromPath(file.getAbsolutePath());
            Properties configP = getConfig(file.getAbsolutePath());
            String fileName = configP.getProperty("EXCEL_FILE_PATH").replaceAll("\"", "").replaceAll("'", "");
            fileName = getFileNameFromPath(fileName);
            File dataFile = getDataFile(fileName);
            if (dataFile == null) {
                LOGGER.info("There is no data file \"{}\" in the folder {}", fileName, options.valueOf(dataFileFolder));
                continue;
            }
            SqlObject o = getSqlObject(sOs,tableName.toUpperCase());
            if (o != null) {
                TableImpl t = new TableImpl(configP, dbP,outputUtils, o.targetSqlString,o.targetTableName);
                listOfResults.add(t.getResultObject(dataFile.getAbsolutePath()));
            }
        }
        Report report = new Report(outputUtils, false);
        report.createHtmlReport("Data Files Files: " + options.valueOf(dataFileFolder), "", "", dbUrl, "", schema, "", "", listOfResults);

    }

    private static SqlObject getSqlObject(List<SqlObject> sqlObjects, String tableName){
        for (SqlObject o:sqlObjects) {
            if(o.targetTableName.equals(tableName)){
                return o;
            }
        }
        return null;
    }


    private static void bibFmcSystems() throws IOException, DiCoreException {
        List<String> dataFile = new ArrayList<>();
        File rootDirectory = new File(options.valueOf(dataFileFolder));
        File[] fList = rootDirectory.listFiles();
        StringJoiner files = new StringJoiner("; ");
        for (File file : fList) {
            files.add(file.getName());
            dataFile.add(file.getAbsolutePath());

        }


        CsvToDbdataIntegrity di = new CsvToDbdataIntegrity(outputUtils);
        di.kerberos(keyTab, keyPrincipal);
        di.setDataCsv(dataFile);
        di.setMappingCsv(options.valueOf(mappingFileOption));
        di.setDbConnection(dbUrl,
                "", "", schema);

        List<ResultObject> results = di.compareDatabase();

        Report report = new Report(outputUtils, false);
        report.createHtmlReport("CSV Files: " + files.toString() + dataFileFolder, "", "", dbUrl, "", schema, "", "", results);
    }

    private static void setUpOptions(String[] args) {
        synchronized (com.msd.bdp.dbcomparer.Main.class) {
            parser = new OptionParser();
            help = parser.acceptsAll(Arrays.asList("help", "h", "?"), "show help").forHelp();

            mappingFileOption = parser.accepts("mappingFile", "Path to the mapping file")
                    .withRequiredArg().ofType(String.class);
            dataFileFolder = parser.accepts("dataFolder", "Path to the fodler with data files")
                    .withRequiredArg().ofType(String.class).required();

            configFileFolder = parser.accepts("configFolder", "Path to the fodler with data files")
                    .withRequiredArg().ofType(String.class);

            targetUrlOption = parser.accepts("dbUrl", "Target database URL")
                    .withRequiredArg().ofType(String.class).required();

            targetSchemaOption = parser.accepts("dbSchema", "Target database schema")
                    .withRequiredArg().ofType(String.class);

            kerbKeyTab = parser.accepts("keyTab", "Path to the KeyTab for kerberos Authentication")
                    .withRequiredArg().ofType(String.class);
            kerbKeyTabPrincipal = parser.accepts("keyPrincipal", "Kerberos principal")
                    .withRequiredArg().ofType(String.class);

            systemOption = parser.accepts("system", "Kerberos principal")
                    .withRequiredArg().ofType(String.class);

            jsonInputFileOption = parser.accepts("jsonFile", "Input JSON file with database tables")
                    .withRequiredArg().ofType(File.class).required();

            options = parser.parse(args);

        }
    }

    private static String getFileNameFromPath(String path) {
        return FilenameUtils.getName(path);
    }


    private static File getDataFile(String fileName) {
        File rootDirectory = new File(options.valueOf(dataFileFolder));
        File[] fList = rootDirectory.listFiles();
        for (File file : fList) {
            if (FilenameUtils.getName(file.getAbsolutePath()).equalsIgnoreCase(fileName)) {
                return file;
            }

        }
        return null;
    }

    private static Properties getConfig(String configFilePath) {
        Properties prop = new Properties();
        String config;
        InputStream input = null;
        try {
            config = readFile(configFilePath);
            input = new ByteArrayInputStream(config.getBytes());

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {

                }
            }
        }
        return prop;
    }

    private static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded).replaceAll("export", "");
    }

}


