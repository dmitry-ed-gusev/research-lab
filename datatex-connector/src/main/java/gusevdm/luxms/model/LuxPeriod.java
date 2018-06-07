package gusevdm.luxms.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Period for LuxMS system (When?).
 * Period JSON:
 *     {
 *         "id": "337719944274378750",
 *         "title": "II quarter 2015",
 *         "start_time" : "2015-12-01",  // <- SQL date
 *         "period_type":6
 *     }
 */

public class LuxPeriod implements LuxModelInterface {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final String        id;
    private final String        title;
    private final Date          startDate;
    private final LuxPeriodType periodType;

    /***/
    public LuxPeriod(String id, String title, Date startDate, LuxPeriodType periodType) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.periodType = periodType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",          StringUtils.isBlank(this.id) ? "" : this.id);
        body.put("title",       this.title);
        body.put("start_time",  this.startDate);
        body.put("period_type", DATE_FORMAT.format(this.startDate));
        return body;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public LuxPeriodType getPeriodType() {
        return periodType;
    }
}
