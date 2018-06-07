package gusevdm.luxms.model;

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
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",         this.id > 0 ? this.id : "");
        body.put("title",      this.title);
        body.put("tree_level", this.treeLevel);
        body.put("parent_id",  this.parentId);
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
}
