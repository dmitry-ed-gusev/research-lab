package dgusev.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Simple text grid draw component for reports.
 * Some important properties:
 *  - for multiple lines in header/data rows use new line symbol [\n] as separator.
 *  - there is horizontal padding in a cell (left and right).
 *  - cell width automatically grows for a larger values
 *  - if cell value is empty, we draw empty cell
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 29.08.13)
*/

// todo: if header is null or empty don't draw empty header in text grid, excel - ok.

@CommonsLog
public final class TextTable {

    private static final String DEFAULT_SHEET_NAME = "Данные"; // default data sheet name
    private static final int    DEBUG_INFO_STEP    = 500;      // for excel export - debug every XXX records
    // implementation specific values - don't change
    private static final String DELIMITER          = "\n"; // values delimiter in a cell
    private static final int    DEFAULT_CELL_WIDTH = 10;   // default cell width (in symbols)
    private static final int    HORIZONTAL_SPACING = 1;    // left-right padding in a cell

    // internal state - text table model
    private TextTableModel      tableModel;

    /***/
    public TextTable(TextTableModel tableModel) {
        this.tableModel = tableModel;
    }

    /**
     * Implementation details. Utility method.
     * Returns max length of values from string, string splitted by DELIMITER symbol.
    */
    private static int getMaxValueLength(String values) {
        int result = 0;
        if (!StringUtils.isBlank(values)) {
            String[] valuesArray = values.split(DELIMITER);
            for (String value : valuesArray) {
                if (value.length() > result) {
                    result = value.length();
                }
            }
        }
        return result;
    }

    /**
     * Implementation detail - creating one grid row (possible multiline) and returns it as string value.
     * This method does not perform any calculations - it just create one text row with data.
    */
    private static String getGridDataLine(String[] dataRow, Map<Integer, Integer> widthMap) {
        StringBuilder gridLine = new StringBuilder();

        // determine data row height
        int dataRowHeight = 1;
        for (String value : dataRow) {
            int matches = StringUtils.countMatches(value, "\n");
            if (matches + 1 > dataRowHeight) {
                dataRowHeight = matches + 1;
            }
        }

        // create multi lines data row
        for (int lineCounter = 1; lineCounter <= dataRowHeight; lineCounter++) { // multiple lines
            StringBuilder line = new StringBuilder();
            line.append("|");

            // data line
            //int startPoint = (!this.verticalNumberedList && this.verticalHeader == null ? 0 : -1);
            for (int valueIndex = 0; valueIndex < widthMap.size(); valueIndex++) { // cycle for one data line
                // current cell width
                int currentCellWidth = widthMap.get(valueIndex);
                // current cell value
                String currentCellValue;
                if (valueIndex < dataRow.length) {
                    String cellValue = dataRow[valueIndex];
                    if (!StringUtils.isBlank(cellValue)) { // check for null values (print empty string "" instead)
                        String[] cellValues = cellValue.split("\n");
                        currentCellValue = (cellValues.length > (lineCounter - 1) ? cellValues[lineCounter - 1] : "");
                    } else {
                        currentCellValue = "";
                    }
                } else {
                    currentCellValue = "";
                }
                line.append(String.format("%" + currentCellWidth + "s|", StringUtils.center(currentCellValue, currentCellWidth)));
            } // end of one line cycle

            line.append("\n");
            gridLine.append(line);
        } // end of one data row cycle

        return gridLine.toString();
    }

    /***/
    private static void updateWidthMap(String[] dataRow, Map<Integer, Integer> widthMap) {
        // processing current row and update width map
        for (int i = 0; i < dataRow.length; i++) {
            String currentValue = dataRow[i];
            // current cell max width
            int maxValueLength = TextTable.getMaxValueLength(currentValue);
            // width from width map
            int valueLengthFromMap;
            if (widthMap.get(i) == null) {
                widthMap.put(i, DEFAULT_CELL_WIDTH);
                valueLengthFromMap = DEFAULT_CELL_WIDTH;
            } else {
                valueLengthFromMap = widthMap.get(i);
            }
            // compare values and select max
            if (maxValueLength > valueLengthFromMap - HORIZONTAL_SPACING * 2) {
                widthMap.put(i, maxValueLength + HORIZONTAL_SPACING * 2);
            }
        }
    }

    /**
     * Create text grid and return it as a text (string).
     * @return String Generated text grid component.
    */
    public String getTextGrid() {
        //log.debug("TextTable.getTextGrid() working."); // -> too much output
        StringBuilder grid = new StringBuilder();

        if (!this.tableModel.isEmpty()) { // TextTableModel is not empty - there is some data
            // get data from table model
            String[]   header = this.tableModel.getHeader(); // table header row
            String[][] data   = this.tableModel.getData();   // table data rows

            // columns length map
            Map<Integer, Integer> columnsWidthMap = new TreeMap<>();
            // process data for columns width - header
            if (header != null && header.length > 0) {
                TextTable.updateWidthMap(header, columnsWidthMap);
            }
            // process data for columns width - data
            if (data != null) {
                for (String[] dataRow : data) {
                    if (dataRow != null) {
                        TextTable.updateWidthMap(dataRow, columnsWidthMap);
                    }
                }
            }

            // generate horizontal line
            int hLineLength = 1; // we count first "|" symbol
            for (int i = 0; i < columnsWidthMap.size(); i++) {
                hLineLength += columnsWidthMap.get(i) + 1;
            }
            String hHeaderLine = StringUtils.repeat("=", hLineLength) + "\n"; // line for top header (under it)
            String hLine       = StringUtils.repeat("-", hLineLength) + "\n"; // regular line
            grid.append(hLine);

            // generate table header
            grid.append(TextTable.getGridDataLine(header, columnsWidthMap)).append(hHeaderLine);
            // generate grid line by line
            for (String[] dataRow : data) {
                String gridLine;
                if (dataRow != null) {
                    gridLine= TextTable.getGridDataLine(dataRow, columnsWidthMap);
                } else {
                    gridLine= TextTable.getGridDataLine(new String[0], columnsWidthMap);
                }
                // generate one grid line and append it to whole grid
                grid.append(gridLine).append(hLine);
            }
        } else { // TextTableModel is empty - no data
            grid.append("-");
        }
        // return result
        return grid.toString();
    }

    /***/
    public void exportToExcelFile(String fileName, String dataSheetName) throws IOException {
        log.debug("TextTable.getExcelFile() working.");
        if (!StringUtils.isBlank(fileName)) { // file name is OK
            log.debug(String.format("File name [%s] is OK.", fileName));

            // table model data
            String[]   header = this.tableModel.getHeader(); // table header row
            String[][] data   = this.tableModel.getData();   // table data rows
            // creating excel file model
            Workbook wb = new HSSFWorkbook();        // new Excel 97-2007 file (xls)
            Sheet sheet = wb.createSheet(StringUtils.isBlank(dataSheetName) ? DEFAULT_SHEET_NAME : dataSheetName); // new sheet
            // cells style
            CellStyle cs = wb.createCellStyle();
            cs.setWrapText(true);                              // word wrap with \n symbols
            cs.setAlignment(HorizontalAlignment.CENTER);       // horizontal align
            cs.setVerticalAlignment(VerticalAlignment.CENTER); // vertical align

            int rowsCounter = 0; // rows counter for entire work book
            if (header.length > 0) { // put header data to excel (if there is data)
                int headerCellsCounter = 0;
                Row headerRow = sheet.createRow((short) rowsCounter);     // new row (header row), rows are zero-based
                for (String headerValue : header) {
                    Cell newCell = headerRow.createCell(headerCellsCounter);
                    newCell.setCellValue(headerValue);
                    newCell.setCellStyle(cs);
                    sheet.autoSizeColumn((short) headerCellsCounter); // autosize every column to fit the text
                    headerCellsCounter++;
                }
                rowsCounter++; // increment rows counter - one row for header
            }

            if (data.length > 0) { // put regular data to excel (if there is data)
                for (String[] dataRow : data) {
                    int cellsCounter = 0;
                    Row row = sheet.createRow((short) rowsCounter); // we create row in any case
                    if (dataRow != null) { // add data from data row
                        for (String cellValue : dataRow) {
                            Cell newDataCell = row.createCell(cellsCounter);
                            newDataCell.setCellValue(cellValue);
                            newDataCell.setCellStyle(cs);
                            sheet.autoSizeColumn((short) cellsCounter); // autosize every column to fit the text
                            cellsCounter++;
                        } // end of FOR -> cells in a row processing
                    }
                    rowsCounter++;
                    if (rowsCounter > 0 && rowsCounter % DEBUG_INFO_STEP == 0) {
                        log.debug("Excel export: processed -> " + rowsCounter);
                    }

                } // end of FOR -> rows processing
                log.debug(String.format("Excel export: processed -> %s. Processing finished.", rowsCounter));
            }

            // write excel model to file on disk
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
            log.debug(String.format("File [%s] created ok.", fileName));

        } else { // file name isn't OK!
            throw new IOException(String.format("File [%s]: name is empty!", fileName));
        }
    }

    /***/
    public static void main(String[] args) throws IOException {
        log.info("Text grid main method starts.");

        String[]   header = new String[] {"Header1\n*****Subheader1*****", "Header2", "Header3"};
        String[][] data   = new String[][] {new String[] {"1-1", "1-2", "1-3"}, new String[] {"2-1", "2-2", "2-3", "2-4"},
                new String[] {null}, null};

        //TextTableModel model = new TextTableModel(new String[0], new String[0][0]);
        TextTableModel model = new TextTableModel(header, data);
        TextTable      table = new TextTable(model);
        System.out.println("->\n" + table.getTextGrid());
        table.exportToExcelFile("c://temp//report.xls", "ZZZ");
    }

}