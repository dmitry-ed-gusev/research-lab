package gusevdm.luxms.model;

import org.json.simple.JSONObject;

import java.math.BigDecimal;

/**
 * Location for LuxMS system.
 * Location JSON:
 *     {
 *         "id": 11,
 *         "title": "Equipment #1",
 *         "tree_level": 1,
 *         "parent_id": null,
 *         "is_hidden": 0,
 *         "latitude": 37.61556,
 *         "longitude": 55.75222,
 *         "srt": 10
 *     }
 */

public class LuxLocation implements LuxModelInterface {

    private final long       id;
    private final String     title;
    private final int        treeLevel;
    private final long       parentId;
    private final boolean    isHidden;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final int        sortOrder;

    /***/
    public LuxLocation(long id, String title, int treeLevel, long parentId, boolean isHidden, BigDecimal latitude, BigDecimal longitude, int sortOrder) {
        this.id = id;
        this.title = title;
        this.treeLevel = treeLevel;
        this.parentId = parentId;
        this.isHidden = isHidden;
        this.latitude = latitude;
        this.longitude = longitude;
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
        body.put("latitude",   this.latitude);
        body.put("longitude",  this.longitude);
        body.put("srt",        this.sortOrder);
        return body;
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
