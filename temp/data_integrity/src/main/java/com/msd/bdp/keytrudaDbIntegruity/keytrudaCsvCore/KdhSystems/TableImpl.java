package com.msd.bdp.keytrudaDbIntegruity.keytrudaCsvCore.KdhSystems;

import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.ResultObject;
import com.msd.bdp.ditoolcore.SqlObject;
import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import com.msd.xlsx2csv.ExcelRow;
import com.msd.xlsx2csv.impl.Xlsx2CsvConverterImpl;
import com.msd.xlsx2csv.utils.ColumnRowTransposer;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class TableImpl {

    private static final String EXCEL_FILE_SHEET_NAME = "EXCEL_FILE_SHEET_NAME";
    private static final String EXCEL_FILE_ROWS = "EXCEL_FILE_ROWS";
    private static final String EXCEL_FILE_COLUMNS = "EXCEL_FILE_COLUMNS";
    private static final String EXCEL_FILE_FIRST_HEADER_COLUMN = "EXCEL_FILE_FIRST_HEADER_COLUMN";

    private final String dbUrl;
    private final String kerbKeyTab;
    private final String kerbPrincipal;
    private final String select;
    private final OutputUtils ou;
    private final String tableName;

    private final Properties configP;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TableImpl.class);


    public TableImpl(Properties configP, Properties dbP, OutputUtils oU, String select, String tableName) {
        this.dbUrl = dbP.getProperty("dbUrl");
        this.kerbKeyTab = dbP.getProperty("krbTab");
        this.kerbPrincipal = dbP.getProperty("drbPrincipal");
        this.tableName = tableName;
        this.select = select;
        this.ou = oU;

        this.configP = configP;
    }


    public ResultObject getResultObject(String dataFileName) {
        SqlObject sO = new SqlObject(dataFileName, tableName, "", select, null, false, dataFileName);
        try {
            List<String> excelRows = readExcelFile(dataFileName);
            LOGGER.info("Found {} rows in the excel {}", excelRows.size(), FilenameUtils.getName(dataFileName));

            List<String> dbRows = getDbRows();
            LOGGER.info("Found {} rows in the table {}", dbRows.size(), tableName);

            int sourceFromHiveDiff = compareLists(excelRows, dbRows, "SvsH" + tableName);
            int hiveFromSourceDiff = compareLists(dbRows, excelRows, "HvsS" + tableName);

            if (sourceFromHiveDiff != 0 && hiveFromSourceDiff != 0) {
                return new ResultObject(sO, excelRows.size(), dbRows.size(),
                        "(" + sourceFromHiveDiff + ")" + "SvsH" + tableName + ".csv",
                        "(" + hiveFromSourceDiff + ")" + "HvsS" + tableName + ".csv",
                        "Number of differences between SOURCE and DB: " + sourceFromHiveDiff + " between DB and SOURCE: " + hiveFromSourceDiff);
            } else {
                LOGGER.info("There is no differences between table {} and file", tableName, FilenameUtils.getName(dataFileName));
                return new ResultObject(sO, excelRows.size(), dbRows.size(), "", "",
                        null);
            }

        } catch (Exception e) {
            LOGGER.warn("There was an exception while running comparison between table {} and file {}", tableName, FilenameUtils.getName(dataFileName));
            LOGGER.error(e.getMessage());
            return new ResultObject(sO, -1, -1, "", "",
                    e.getMessage());
        }

    }


    private List<String> readExcelFile(String dataFileName) {
        List<String> rows = new ArrayList<>();
        String firstHeaderColumn = getProperty(configP, EXCEL_FILE_FIRST_HEADER_COLUMN);
        String sheetName = getProperty(configP, EXCEL_FILE_SHEET_NAME);
        Xlsx2CsvConverterImpl inst = new Xlsx2CsvConverterImpl();
        int columnIndex = (firstHeaderColumn != null && firstHeaderColumn.length() > 0) ?
                sheetName.equals("Small Scale Operational Vars") ? 2 : 1
                : 0;


        Consumer<ExcelRow> basicRowProcessor = row -> {
            StringJoiner s = new StringJoiner("<:>");

            for (int i = 0; i < row.getColumnCount() - columnIndex; ++i) {
                s.add((row.getValueAsString(i) == null || row.getValueAsString(i).equals("")) ? "null" : row.getValueAsString(i).trim());
            }
            rows.add(">>" + s.toString() + "<<");

        };


        Consumer<ExcelRow> rowProcessor = (firstHeaderColumn != null && firstHeaderColumn.length() > 0) ?
                new ColumnRowTransposer(basicRowProcessor, firstHeaderColumn) : basicRowProcessor;

        dataFileName = dataFileName.replaceAll("\\\\", "/");

        inst.openExcel(dataFileName,
                sheetName,
                getProperty(configP, EXCEL_FILE_COLUMNS),
                getProperty(configP, EXCEL_FILE_ROWS),
                rowProcessor);
        return rows;

    }

    private String getProperty(Properties p, String key) {
        return p.getProperty(key).replaceAll("\"", "").replaceAll("'", "");
    }

    private List<String> getDbRows() throws IOException, SQLException, DiCoreException {
        List<String> dbData;

        try (DbFacadePool dbPool = new DbFacadePool(dbUrl, "", "", kerbKeyTab, kerbPrincipal);
             DbFacade db = dbPool.borrowConnectionWithCheck()) {
            dbData = db.executeQuery(select);
        }
        return dbData;
    }

    private int compareLists(List<String> result1, List<String> result2, String fileName) {
        Collections.sort(result1);
        Collections.sort(result2);
        List<String> difference = ListUtils.subtract(result1, result2);
        if (!difference.isEmpty()) {
            ou.writeToCSV(difference, fileName);
            return difference.size();
        } else {
            return 0;
        }
    }


}
