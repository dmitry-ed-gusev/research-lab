package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

import java.math.BigDecimal;

/**
 * One Visual Control Point (data point) for LuxMS system.
 * Data point JSON:
 *     {
 *         "id": 54893
 *         "metric_id": 1,
 *         "loc_id": 123,
 *         "period_id": "337727177922248702",
 *         "value": 123.23
 *     }
 */

public class LuxDataPoint implements LuxModelInterface {

    // CSV headers
    private static final String CSV_HEADER_ID          = "ID";
    private static final String CSV_HEADER_METRIC_ID   = "METRIC_ID";
    private static final String CSV_HEADER_LOCATION_ID = "LOCATION_ID";
    private static final String CSV_HEADER_PERIOD_ID   = "PERIOD_ID";
    private static final String CSV_HEADER_VALUE       = "VALUE";

    // CSV file header
    public static final String[] FILE_HEADER = {
            CSV_HEADER_ID, CSV_HEADER_METRIC_ID, CSV_HEADER_LOCATION_ID,
            CSV_HEADER_PERIOD_ID, CSV_HEADER_VALUE
    };

    // internal state
    private final long       id;
    private final long       metricId;
    private final long       locationId;
    private final long       periodId;
    private final BigDecimal value;

    /***/
    public LuxDataPoint(long id, long metricId, long locationId, long periodId, BigDecimal value) {
        this.id = id;
        this.metricId = metricId;
        this.locationId = locationId;
        this.periodId = periodId;
        this.value = value;
    }

    /***/
    public LuxDataPoint(CSVRecord record) {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.metricId   = Long.parseLong(record.get(CSV_HEADER_METRIC_ID));
        this.locationId = Long.parseLong(record.get(CSV_HEADER_LOCATION_ID));
        this.periodId   = Long.parseLong(record.get(CSV_HEADER_PERIOD_ID));
        this.value      = new BigDecimal(record.get(CSV_HEADER_VALUE));
    }

    /***/
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",        /*this.id > 0 ? this.id : ""*/ this.id);
        body.put("metric_id", this.metricId);
        body.put("loc_id",    this.locationId);
        body.put("period_id", this.periodId);
        body.put("val",       this.value);
        return body;
    }

    @Override
    public LuxDataType getDataType() {
        return LuxDataType.DATA;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getStrId() {
        return String.valueOf(this.id);
    }

    public long getMetricId() {
        return metricId;
    }

    public long getLocationId() {
        return locationId;
    }

    public long getPeriodId() {
        return periodId;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("metricId", metricId)
                .append("locationId", locationId)
                .append("periodId", periodId)
                .append("value", value)
                .toString();
    }

}
