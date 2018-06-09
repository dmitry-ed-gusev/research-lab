package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

import java.text.ParseException;
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

    // CSV headers
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_TITLE       = "TITLE";
    private static final String CSV_START_TIME         = "START_TIME";
    private static final String CSV_PERIOD_TYPE        = "PERIOD_TYPE";

    // CSV file header (list of headers)
    public static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_TITLE, CSV_START_TIME, CSV_PERIOD_TYPE
    };

    // internal state
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

    /***/
    public LuxPeriod(CSVRecord record) throws ParseException {
        this.id         = record.get(CSV_HEADER_ID);
        this.title      = record.get(CSV_HEADER_TITLE);
        this.startDate  = DATE_FORMAT.parse(record.get(CSV_START_TIME));
        this.periodType = LuxPeriodType.getTypeByName(record.get(CSV_PERIOD_TYPE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",          StringUtils.isBlank(this.id) ? "" : this.id);
        body.put("title",       this.title);
        body.put("start_time",  DATE_FORMAT.format(this.startDate));
        body.put("period_type", this.periodType.getValue());
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.PERIODS;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("title", title)
                .append("startDate", startDate)
                .append("periodType", periodType)
                .toString();
    }
}
