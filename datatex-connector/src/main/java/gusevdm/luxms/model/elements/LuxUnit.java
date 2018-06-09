package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_TITLE       = "TITLE";
    private static final String CSV_HEADER_SHORT_TITLE = "SHORT_TITLE";
    private static final String CSV_HEADER_AXIS_TITLE  = "AXIS_TITLE";
    private static final String CSV_HEADER_PREFIX      = "PREFIX";
    private static final String CSV_HEADER_SUFFIX      = "SUFFIX";

    // CSV file header (list of headers)
    public static final String[] FILE_HEADER = {
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
        this.id         = id;
        this.title      = title;
        this.shortTitle = shortTitle;
        this.prefix     = prefix;
        this.suffix     = suffix;
        this.axisTitle  = axisTitle;
    }

    /***/
    public LuxUnit(CSVRecord record) {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.title      = record.get(CSV_HEADER_TITLE);
        this.shortTitle = record.get(CSV_HEADER_SHORT_TITLE);
        this.axisTitle  = record.get(CSV_HEADER_AXIS_TITLE);
        this.prefix     = record.get(CSV_HEADER_PREFIX);
        this.suffix     = record.get(CSV_HEADER_SUFFIX);
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("title", title)
                .append("shortTitle", shortTitle)
                .append("axisTitle", axisTitle)
                .append("prefix", prefix)
                .append("suffix", suffix)
                .toString();
    }

}
