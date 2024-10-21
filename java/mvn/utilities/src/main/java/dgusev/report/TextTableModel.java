package dgusev.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

/**
 * Data model for TextTable component.
 * Some properties of this model:
 *  - null-header or header with length = 0 will not be added (ignored)
 *  - null-data line in data will be added as is (nulls)
 *
 * Important! Class is immutable!
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.09.13)
*/

public final class TextTableModel {

    private String[]   header = null; // data table header
    private String[][] data   = null; // data table rows

    /** Constructor. */
    public TextTableModel(String[] header, String[][] data) {
        // adding header
        if (header != null && header.length > 0) {
            this.header = Arrays.copyOf(header, header.length);
        }
        // adding data
        if (data != null && data.length > 0) {
            this.data = new String[data.length][];
            for (int i = 0; i < data.length; i++) {
                this.data[i] = (data[i] == null ? null : Arrays.copyOf(data[i], data[i].length));
            }
        }
    }

    /***/
    public String[] getHeader() {
        return (this.header == null ? new String[0] : Arrays.copyOf(this.header, this.header.length));
    }

    /***/
    public String[][] getData() {
        String[][] result;
        if (this.data != null) { // if we have some data - we will return its copy
            result = new String[this.data.length][];
            for (int i = 0; i < this.data.length; i++) {
                result[i] = (this.data[i] == null ? null : Arrays.copyOf(this.data[i], this.data[i].length));
            }
        } else { // there is no data
            result = new String[0][0];
        }
        return result;
    }

    /**
     * Result is a full data from table model. Row #0 - header, remain rows - data.
    */
    public String[][] getFullData() {
        String[][] result;

        if (this.data != null) { // if there is any data
            int startRowIndex = 0;
            // adding header (if it exists)
            if (this.header != null) {
                result = new String[this.data.length + 1][];
                result[startRowIndex] = Arrays.copyOf(this.header, this.header.length);
                startRowIndex++;
            } else {
                result = new String[this.data.length][];
            }
            // adding rest of data
            for (int i = 0; i < this.data.length; i++) {
                result[i + startRowIndex] = (this.data[i] == null ? null : Arrays.copyOf(this.data[i], this.data[i].length));
            }
        } else if (this.header != null) { // no data, but there is header
            result = new String[1][];
            result[0] = Arrays.copyOf(this.header, this.header.length);
        } else {
            result = new String[0][0];
        }

        return result;
    }

    /***/
    public boolean isEmpty() {
        return (this.header == null && this.data == null);
    }

    /** Just for draft tests. */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(TextTableModel.class);
        PropertyConfigurator.configure("log4j.properties");
        log.debug("TextTableModel main method starting.");

        String[] header = new String[] {"Column1", "Column2"};
        String[][] data = new String[][] {{"data1", "data2"}, {"", "ffff"}, {"zzz1", "zzz2"}};

        log.debug("-> " + Arrays.toString(header));

        TextTableModel model = new TextTableModel(header, data);
        log.debug("t-> " + Arrays.toString(model.getHeader()));
        log.debug("t-> " + Arrays.deepToString(model.getFullData()));

        header[0] = "ZZZZZZ";
        log.debug("-> " + Arrays.toString(header));
        log.debug("t-> " + Arrays.toString(model.getHeader()));
        log.debug("t-> " + Arrays.deepToString(model.getFullData()));

    }

}