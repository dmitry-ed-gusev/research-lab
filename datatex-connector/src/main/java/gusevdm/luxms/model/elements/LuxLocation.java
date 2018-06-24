package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

import java.math.BigDecimal;

/**
 * Location for LuxMS system (Where?).
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

    // CSV headers (private, internal use)
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_TITLE       = "TITLE";
    private static final String CSV_TREE_LEVEL         = "TREE_LEVEL";
    private static final String CSV_PARENT_ID          = "PARENT_ID";
    private static final String CSV_IS_HIDDEN          = "IS_HIDDEN";
    private static final String CSV_LATITUDE           = "LATITUDE";
    private static final String CSV_LONGITUDE          = "LONGITUDE";
    private static final String CSV_SORTING            = "SORTING";

    // CSV file header (list of headers, public external use)
    public static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_TITLE, CSV_TREE_LEVEL, CSV_PARENT_ID,
            CSV_IS_HIDDEN, CSV_LATITUDE, CSV_LONGITUDE, CSV_SORTING
    };

    // internal state
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
    public LuxLocation(CSVRecord record) {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.title      = record.get(CSV_HEADER_TITLE);
        this.treeLevel  = Integer.parseInt(record.get(CSV_TREE_LEVEL));
        this.parentId   = record.get(CSV_PARENT_ID) == null ? -1 : Long.parseLong(record.get(CSV_PARENT_ID));
        this.isHidden   = (Integer.parseInt(record.get(CSV_IS_HIDDEN)) == 1);
        this.latitude   = new BigDecimal(record.get(CSV_LATITUDE));
        this.longitude  = new BigDecimal(record.get(CSV_LONGITUDE));
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
        body.put("latitude",   this.latitude);
        body.put("longitude",  this.longitude);
        body.put("srt",        this.sortOrder);
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.LOCATIONS;
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
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
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("sortOrder", sortOrder)
                .toString();
    }

}
