package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

/**
 * One Metric for LuxMS system (What?).
 * Metric JSON:
 *     {
 *         "id": 11,
 *         "title": "Performance",
 *         "tree_level": 1,   // <- for root tree_level=0
 *         "parent_id": null, // <- for root parent_id=null
 *         "is_hidden": 0,
 *         "unit_id": 2,
 *         "srt": 10          // <- sorting in UI
 *     }
 */

public class LuxMetric implements LuxModelInterface {

    // CSV headers
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_TITLE       = "TITLE";
    private static final String CSV_TREE_LEVEL         = "TREE_LEVEL";
    private static final String CSV_PARENT_ID          = "PARENT_ID";
    private static final String CSV_IS_HIDDEN          = "IS_HIDDEN";
    private static final String CSV_UNIT_ID            = "UNIT_ID";
    private static final String CSV_SORTING            = "SORTING";

    // CSV file header (list of headers)
    public static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_TITLE, CSV_TREE_LEVEL, CSV_PARENT_ID,
            CSV_IS_HIDDEN, CSV_UNIT_ID, CSV_SORTING
    };

    // internal state
    private final long    id;
    private final String  title;
    private final int     treeLevel;
    private final long    parentId;
    private final boolean isHidden;
    private final long    unitId;
    private final int     sortOrder;

    /***/
    public LuxMetric(long id, String title, int treeLevel, long parentId, boolean isHidden, long unitId, int sortOrder) {
        this.id = id;
        this.title = title;
        this.treeLevel = treeLevel;
        this.parentId = parentId;
        this.isHidden = isHidden;
        this.unitId = unitId;
        this.sortOrder = sortOrder;
    }

    /***/
    public LuxMetric(CSVRecord record) {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.title      = record.get(CSV_HEADER_TITLE);
        this.treeLevel  = Integer.parseInt(record.get(CSV_TREE_LEVEL));
        this.parentId   = record.get(CSV_PARENT_ID) == null ? -1 : Long.parseLong(record.get(CSV_PARENT_ID));
        this.isHidden   = (Integer.parseInt(record.get(CSV_IS_HIDDEN)) == 1);
        this.unitId     = Long.parseLong(record.get(CSV_UNIT_ID));
        this.sortOrder  = Integer.parseInt(record.get(CSV_SORTING));
    }

    /***/
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",         /*this.id > 0 ? this.id : ""*/ this.id);
        body.put("title",      this.title);
        body.put("tree_level", this.treeLevel);
        body.put("parent_id",  this.parentId <= 0 ? null : this.parentId);
        body.put("is_hidden",  this.isHidden ? 1 : 0);
        body.put("unit_id",    this.unitId);
        body.put("srt",        this.sortOrder);
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.METRICS;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getStrId() {
        return String.valueOf(this.id);
    }

    public String getTitle() {
        return title;
    }

    public int getTreeLevel() {
        return treeLevel;
    }

    public long getParentId() {
        return parentId;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public long getUnitId() {
        return unitId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("title", title)
                .append("treeLevel", treeLevel)
                .append("parentId", parentId)
                .append("isHidden", isHidden)
                .append("unitId", unitId)
                .append("sortOrder", sortOrder)
                .toString();
    }

}
