package gusevdm.bi.luxms.model.elements;

import gusevdm.bi.luxms.model.LuxDataType;
import gusevdm.bi.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.Date;

import static gusevdm.bi.luxms.LuxDefaults.LUX_DATE_FORMAT;

/**
 * Period for LuxMS system (When?).
 * Period JSON:
 *     {
 *         "id": "337719944274378750",
 *         "title": "II quarter 2015",
 *         "parent_id": "1",
 *         "start_time" : "2015-12-01",  // <- SQL date
 *         "period_type":6
 *     }
 */

public class LuxPeriod implements LuxModelInterface {

    // CSV headers
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_TITLE       = "TITLE";
    private static final String CSV_PARENT_ID          = "PARENT_ID";
    private static final String CSV_START_TIME         = "START_TIME";
    private static final String CSV_PERIOD_TYPE        = "PERIOD_TYPE";

    // CSV file header (list of headers)
    public static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_TITLE, CSV_PARENT_ID, CSV_START_TIME, CSV_PERIOD_TYPE
    };

    // internal state
    private final long          id;
    private final String        title;
    private final long          parentId;
    private final Date          startDate;
    private final LuxPeriodType periodType;

    /***/
    public LuxPeriod(long id, String title, long parentId, Date startDate, LuxPeriodType periodType) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.startDate = startDate;
        this.periodType = periodType;
    }

    /***/
    public LuxPeriod(CSVRecord record) throws ParseException {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.title      = record.get(CSV_HEADER_TITLE);
        this.parentId   = record.get(CSV_PARENT_ID) == null ? -1 : Long.parseLong(record.get(CSV_PARENT_ID));
        this.startDate  = LUX_DATE_FORMAT.parse(record.get(CSV_START_TIME));
        this.periodType = LuxPeriodType.getTypeByName(record.get(CSV_PERIOD_TYPE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",          /*this.id > 0 ? this.id : ""*/ this.id);
        body.put("title",       this.title);
        body.put("parent_id",   this.parentId <= 0 ? null : this.parentId);
        body.put("start_time",  LUX_DATE_FORMAT.format(this.startDate));
        body.put("period_type", this.periodType.getValue());
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.PERIODS;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public String getStrId() {
        return String.valueOf(this.id);
    }

    public String getTitle() {
        return title;
    }

    public long getParentId() {
        return parentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public LuxPeriodType getPeriodType() {
        return periodType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("title", title)
                .append("parentId", parentId)
                .append("startDate", startDate)
                .append("periodType", periodType)
                .toString();
    }

}