package gusevdm.luxms.model.elements;

import gusevdm.luxms.model.LuxDataType;
import gusevdm.luxms.model.LuxModelInterface;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static gusevdm.luxms.LuxDefaults.LUX_DATE_FORMAT;

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
    private final long          id;
    private final String        title;
    private final Date          startDate;
    private final LuxPeriodType periodType;

    /***/
    public LuxPeriod(long id, String title, Date startDate, LuxPeriodType periodType) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.periodType = periodType;
    }

    /***/
    public LuxPeriod(CSVRecord record) throws ParseException {
        this.id         = Long.parseLong(record.get(CSV_HEADER_ID));
        this.title      = record.get(CSV_HEADER_TITLE);
        this.startDate  = LUX_DATE_FORMAT.parse(record.get(CSV_START_TIME));
        this.periodType = LuxPeriodType.getTypeByName(record.get(CSV_PERIOD_TYPE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAsJSON() {
        JSONObject body = new JSONObject();
        body.put("id",          this.id > 0 ? this.id : "");
        body.put("title",       this.title);
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

    /** Generates Map with LuxPeriod by months. ID in a map -> [MMYYYY]. */
    public static Map<Long, LuxPeriod> generateMonthsPeriods(String... years) throws ParseException {
        String[] monthsNames = {"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};

        Map<Long, LuxPeriod> periods = new TreeMap<>();

        // tmp for map
        int monthCounter;
        LuxPeriod period;
        // tmp for one period
        long   id;
        String title;
        Date   startDate;
        for (String year : years) { // iterate over provided year
            monthCounter = 1;
            for (String month : monthsNames) { // iterate over all months names
                id        = Long.parseLong(monthCounter + year);
                title     = month + " " + year;
                startDate = LUX_DATE_FORMAT.parse(year + "-" + (monthCounter > 9 ? monthCounter : "0" + monthCounter) + "-01");
                // create period object
                period    = new LuxPeriod(id, title, startDate, LuxPeriodType.MONTH);
                // add it to resulting map
                periods.put(id, period);
                // increment months counter
                monthCounter++;
            } // end of FOR cycle for one year
        } // end of FOR cycle for all years

        return periods;
    }

    //public static void main(String[] args) throws ParseException {
    //    System.out.println(LuxPeriod.generateMonthsPeriods("2017", "2018"));
    //}

}
