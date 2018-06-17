package gusevdm.luxms.model;

import gusevdm.luxms.model.elements.*;

import java.util.Map;

/***/
// todo: immutability???
public class LuxModel {

    // internal state
    private Map<Long, LuxUnit>      units      = null;
    private Map<Long, LuxMetric>    metrics    = null;
    private Map<Long, LuxPeriod>    periods    = null;
    private Map<Long, LuxLocation>  locations  = null;
    private Map<Long, LuxDataPoint> dataPoints = null;

    /***/
    public LuxModel() {}

    /***/
    public LuxModel(Map<Long, LuxUnit> units, Map<Long, LuxMetric> metrics, Map<Long, LuxPeriod> periods,
                    Map<Long, LuxLocation> locations, Map<Long, LuxDataPoint> dataPoints) {
        this.units      = units;
        this.metrics    = metrics;
        this.periods    = periods;
        this.locations  = locations;
        this.dataPoints = dataPoints;
    }

    public void addUnit(LuxUnit unit) {
        // todo: !!! check for null - initialize -> or just add one
        // todo: add for other elements too
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

    public Map<Long, LuxLocation> getLocations() {
        return locations;
    }

    public Map<Long, LuxDataPoint> getDataPoints() {
        return dataPoints;
    }

}
