package com.msd.bdp.keytrudaDbIntegruity.keytrudaCsvCore.BibFmcSystems;

import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.ResultObject;
import com.msd.bdp.ditoolcore.SqlObject;
import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import com.msd.xlsx2csv.Xlsx2CsvConverter;
import com.msd.xlsx2csv.XlsxSourceReaderWithConfig;
import com.msd.xlsx2csv.impl.Xlsx2CsvConverterImpl;
import com.msd.xlsx2csv.impl.XlsxSourceReaderWithConfigImpl;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class CsvToDbdataIntegrity {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvToDbdataIntegrity.class);
    private final OutputUtils outputUtils;

    private String dbUrl;
    private String dbUser;
    private String dbPass;
    private String dbSchema;

    private String mappingCsvFile;
    private List<String> dataFiles;

    private String kerbKeyTab;
    private String kerbPrincipal;

    public CsvToDbdataIntegrity(OutputUtils outputUtils) {
        this.outputUtils = outputUtils;
    }

    public CsvToDbdataIntegrity setDbConnection(String targetDbUrl, String targetDbUser, String targetDbPassword, String targetSchema) {
        this.dbUrl = targetDbUrl;
        this.dbUser = targetDbUser;
        this.dbPass = targetDbPassword;
        this.dbSchema = targetSchema;
        return this;
    }

    public CsvToDbdataIntegrity setMappingCsv(String mappingCsvFile) {
        this.mappingCsvFile = mappingCsvFile;
        return this;
    }

    public CsvToDbdataIntegrity setDataCsv(List<String> dataFiles) {
        this.dataFiles = dataFiles;
        return this;
    }

    public CsvToDbdataIntegrity kerberos(String kerbKeyTab, String kerbPrincipal) {
        this.kerbKeyTab = kerbKeyTab;
        this.kerbPrincipal = kerbPrincipal;
        return this;
    }

    public List<ResultObject> compareDatabase() throws DiCoreException {

        XlsxSourceReaderWithConfig reader = new XlsxSourceReaderWithConfigImpl();
        reader.readMappingDefinition(mappingCsvFile);
        List<ResultObject> results = new ArrayList<>();
        results.add(upstream_process_data(reader));
        return results;

    }

    private ResultObject upstream_process_data(XlsxSourceReaderWithConfig reader) throws DiCoreException {
        List<String> csvData = new ArrayList<>();
        for (String dataFile : dataFiles) {
            csvData.addAll(getDataForDiscrete(dataFile, reader));
        }
        LOGGER.info("CSV: Collected data from table upstream_discreet_process_data for {} rows ", csvData.size());

        String select = "select unique_id,site,scale,unit_operation,batch_id,cycle_no,campaign,campaign_type,purpose,column_header,column_value,assigned_date,row_id,row_counter from "+dbSchema+".upstream_process_data";
        List<String> dbData = new ArrayList<>();
        try (DbFacadePool dbPool = new DbFacadePool(dbUrl, dbUser, dbPass, kerbKeyTab, kerbPrincipal);
             DbFacade db = dbPool.borrowConnectionWithCheck()) {
            dbData = db.executeQuery(select);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Unable to get data from database",e);
        }

        int differences = compareLists(csvData, dbData, "upstream_process_data");

        SqlObject sO = new SqlObject("csv", "upstream_process_data", "", select, null, false,"csv");
        if (differences != 0) {
            return new ResultObject(sO, csvData.size(), dbData.size(), "upstream_process_data" + ".csv", "",
                    "Number of differences between CSV and DB: " + differences);
        } else {
            return new ResultObject(sO, csvData.size(), dbData.size(), "", "",
                    null);
        }
    }

    private int compareLists(List<String> result1, List<String> result2, String fileName) {
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

    private List<String> getListOfSheets(String dataFile) {
        Xlsx2CsvConverter converter = new Xlsx2CsvConverterImpl();
        List<String> sheets = new ArrayList<>();
        converter.listSheets(dataFile, sheetName ->
                sheets.add(sheetName)
        );
        return sheets;
    }


    private List<String> getDataForDiscrete(String dataFile, XlsxSourceReaderWithConfig reader) {
        List<String> csvData = new ArrayList<>();
        for (String sheet : getListOfSheets(dataFile)) {
            reader.readSourceDataFromExcel(dataFile, sheet, 1l, row -> {
                StringJoiner builder = new StringJoiner("<:>");
                String destination = row.getValueAsString(0);
                String batchId = row.getValueAsString(1);
                String columnHeader = row.getValueAsString(2);
                String columnValue = row.getValueAsString(3);
                String unitOperation = row.getValueAsString(4);
                String site = row.getValueAsString(5);
                String uniqueId = row.getValueAsString(6);
                String sourceRowId = row.getValueAsString(7);

                builder.add(batchId).
                        add(columnHeader).
                        add(columnValue).
                        add(site);


                if (destination.equalsIgnoreCase("up-discrete")) {
                    csvData.add(">>" + builder.toString() + "<<");
                }

            });
        }
        return csvData;
    }

}
