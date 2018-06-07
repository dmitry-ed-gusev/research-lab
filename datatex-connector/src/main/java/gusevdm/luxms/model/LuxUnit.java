package gusevdm.luxms.model;

import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONObject;

/**
 * Unit of measurement (for Metric).
 * Unit JSON:
 *     {
 *         "id": 1,
 *         "title": "m/hour",
 *         "value_prefix": "$",
 *         "value_suffix": "%",
 *         "tiny_title": "øò.",
 *         "axis_title": "some-title"
 *     }
 */

public class LuxUnit implements LuxModelInterface {

    // CSV headers
    public static final String CSV_HEADER_ID          = "ID";
    public static final String CSV_HEADER_TITLE       = "TITLE";
    public static final String CSV_HEADER_SHORT_TITLE = "SHORT_TITLE";
    public static final String CSV_HEADER_AXIS_TITLE  = "AXIS_TITLE";
    public static final String CSV_HEADER_PREFIX      = "PREFIX";
    public static final String CSV_HEADER_SUFFIX      = "SUFFIX";
    // CSV file header (list of headers)
    private static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_TITLE, CSV_HEADER_SHORT_TITLE,
            CSV_HEADER_AXIS_TITLE, CSV_HEADER_PREFIX, CSV_HEADER_SUFFIX
    };

    // internal state
    private final long   id;
    private final String title;
    private final String shortTitle;
    private final String axisTitle;
    private final String prefix;
    private final String suffix;

    /***/
    public LuxUnit(long id, String title, String shortTitle, String axisTitle, String prefix, String suffix) {
        this.id = id;
        this.title = title;
        this.shortTitle = shortTitle;
        this.prefix = prefix;
        this.suffix = suffix;
        this.axisTitle = axisTitle;
    }

    /***/
    //public LuxUnit(CSVRecord record) {
    //
    //}

    /***/
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",           this.id > 0 ? this.id : "");
        body.put("title",        this.title);
        body.put("value_prefix", this.prefix);
        body.put("value_suffix", this.suffix);
        body.put("tiny_title",   this.shortTitle);
        body.put("axis_title",   this.axisTitle);
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.UNITS;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getAxisTitle() {
        return axisTitle;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

}
