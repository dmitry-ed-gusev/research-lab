package gusevdm.luxms.model;

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
