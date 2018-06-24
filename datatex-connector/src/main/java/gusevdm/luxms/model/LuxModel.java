package gusevdm.luxms.model;

import gusevdm.luxms.model.elements.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static gusevdm.luxms.LuxDefaults.LUX_DATE_FORMAT;

/** The whole model for LuxMS data cube. */
// todo: immutability???
public class LuxModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxModel.class);

    // internal state (data model)
    private Map<Long, LuxUnit>      units      = null;
    private Map<Long, LuxMetric>    metrics    = null;
    private Map<Long, LuxPeriod>    periods    = null;
    private Map<Long, LuxLocation>  locations  = null;
    private Map<Long, LuxDataPoint> dataPoints = null;

    // additional parameters (for load from DataTex)
    private Long     datasetId;
    private String   sqlFile;
    private String[] locationsTitlesCols;
    private String[] dataValuesCols;
    private int[]    dataValuesMetricsIds;

    /***/
    public LuxModel(String... years) throws ParseException {
        LOGGER.debug(String.format("LuxModel constructor(String...) is working. Years: %s", Arrays.toString(years)));

        if (years != null && years.length > 0) { // autogenerate periods values
            this.periods = LuxModel.generatePeriods(years);
        }
    }

    public Map<Long, LuxUnit> getUnits() {
        return units;
    }

    public Map<Long, LuxMetric> getMetrics() {
        return metrics;
    }

    public Map<Long, LuxPeriod> getPeriods() {
        return periods;
    }

    public Map<Long, LuxLocation> getLocations() {
        return locations;
    }

    public Map<Long, LuxDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setUnits(Map<Long, LuxUnit> units) {
        this.units = units;
    }

    public void setMetrics(Map<Long, LuxMetric> metrics) {
        this.metrics = metrics;
    }

    public void setPeriods(Map<Long, LuxPeriod> periods) {
        this.periods = periods;
    }

    public void setLocations(Map<Long, LuxLocation> locations) {
        this.locations = locations;
    }

    public void setDataPoints(Map<Long, LuxDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getSqlFile() {
        return sqlFile;
    }

    public void setSqlFile(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    public String[] getLocationsTitlesCols() {
        return locationsTitlesCols;
    }

    public void setLocationsTitlesCols(String[] locationsTitlesCols) {
        this.locationsTitlesCols = locationsTitlesCols;
    }

    public String[] getDataValuesCols() {
        return dataValuesCols;
    }

    public void setDataValuesCols(String[] dataValuesCols) {
        this.dataValuesCols = dataValuesCols;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public int[] getDataValuesMetricsIds() {
        return dataValuesMetricsIds;
    }

    public void setDataValuesMetricsIds(String[] dataValuesMetricsIds) {
        this.dataValuesMetricsIds = Arrays.stream(dataValuesMetricsIds).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * Generates Map (hierarhical) with LuxPeriods.
     * Identification of objects (all identifiers are positive long numbers):
     *  - day     -> (len 7-8) -> DDMMYYYY, if day < 10, then DMMYYYY. Examples: 1012016 (1 Jan 2016), 12102005 (12 Oct 2006).
     *  - month   -> (len 5-6) -> MMYYYY, if month < 10, then MYYYY. Examples: 102019 (Oct 2019), 22017 (Feb 2017).
     *  - quarter -> (len 10)  -> Q00000YYYY. Examples: 2000002018 (II quarter 2018), 4000002001 (IV quarter 2001).
     *  - year    -> (len 4)   -> YYYY. Examples: 2001, 2017 (self-explanatory).
     */
    public static Map<Long, LuxPeriod> generatePeriods(String... years) throws ParseException {
        LOGGER.debug(String.format("LuxPeriod.generatePeriods() is working. Years: %s",
                Arrays.toString(years)));

        String[] quartersNames = {"I кв.", "II кв.", "III кв.", "IV кв."};
        String[] monthsNames   = {"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};

        // resulting map with periods
        Map<Long, LuxPeriod> periods = new TreeMap<>();

        // temporary counters
        int quarterCounter;
        // temporary identificators
        long yearId;
        long quarterId;
        long monthId;

        LuxPeriod period;
        String monthName;
        String title;
        Date startDate;

        for (String year : years) { // <- YEARS
            LOGGER.debug(String.format("Processing year: %s", year));
            // add year period
            yearId = Long.parseLong(year);
            startDate = LUX_DATE_FORMAT.parse(year + "-01-01");
            periods.put(yearId, new LuxPeriod(yearId, year, -1, startDate, LuxPeriodType.YEAR));

            quarterCounter = 1;
            for (String quarter : quartersNames) { // <- QUARTERS
                LOGGER.debug(String.format("\tProcessing quarter: %s", quarterCounter));
                // create quarter period
                quarterId = Long.parseLong(String.valueOf(quarterCounter) + "00000" + year);
                title = quarter + " " + year;
                startDate = LUX_DATE_FORMAT.parse(year + "-" +
                        String.valueOf(1 + 3 * (quarterCounter - 1)) + "-01");
                // add period to map
                periods.put(quarterId, new LuxPeriod(quarterId, title, yearId, startDate, LuxPeriodType.QUARTER));

                for (int monthCounter = 3 * (quarterCounter - 1); monthCounter < 3 * quarterCounter; monthCounter++) { // <- MONTHS
                    LOGGER.debug(String.format("\t\tProcessing month: %s", monthCounter + 1));
                    monthId   = Long.parseLong(monthCounter + 1 + year);
                    monthName = monthsNames[monthCounter];
                    title     = monthName + " " + year;
                    startDate = LUX_DATE_FORMAT.parse(year + "-" + (monthCounter > 9 ? monthCounter : "0" + monthCounter) + "-01");
                    // create period object
                    period    = new LuxPeriod(monthId, title, quarterId, startDate, LuxPeriodType.MONTH);
                    // add it to resulting map
                    periods.put(monthId, period);
                } // end of FOR -> MONTHS

                quarterCounter++; // increase quarters counter after months processing

            } // end of FOR -> QUARTERS

        } // end of FOR -> YEARS

        return periods;
    }

}
