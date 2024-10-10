package dgusev.report;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

/**
 * Final utility helper class for TextTable reporting component.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 03.09.13)
 */

public final class TextTableHelper {

    private TextTableHelper() {}

    /**
     * Method merges data rows into one row with separators: [\n] - symbol. Helper method for building
     * TextTable components.
     * @param rows String[]... varargs method.
    */
    public static String[] mergeRows(String[]... rows) {
        String[] result;
        if (rows != null) {
            // get max length for all rows
            int maxLength = 0;
            for (String[] row : rows) {
                if (row.length > maxLength) {
                    maxLength = row.length;
                }
            }

            // creating result and filling it with data
            result = new String[maxLength];
            for (int i = 0; i < maxLength; i++) {
                StringBuilder oneCell = new StringBuilder();

                for (String[] row : rows) {
                    if (i < row.length) {
                        String rowValue = row[i];
                        if (!StringUtils.isBlank(rowValue)) {
                            if (!StringUtils.isBlank(oneCell.toString())) {
                                oneCell.append("\n");
                            }
                            oneCell.append(rowValue);
                        }
                    }

                } // end of for -> one cell value
                result[i] = oneCell.toString();
            }
        } else { // rows are empty (null)
            result = new String[0];
        }
        return result;
    }

    /**
     * Main method just for test.
     */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(TextTableHelper.class);
        PropertyConfigurator.configure("log4j.properties");
        log.info("RollingReportHelper mail method starts.");

        String[] str1 = new String[]{"zz", "fff", "ffrrr", "ttt", "rrrr"};
        String[] str2 = new String[]{"gg", "kk", "666"};
        String[] str3 = new String[]{"", "45"};
        String[] str4 = new String[]{"1", "2", "3", "4", "5", "6", "7", ""};

        System.out.println(Arrays.asList(TextTableHelper.mergeRows(str1, str3)));
    }
}