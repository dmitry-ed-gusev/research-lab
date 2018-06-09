package gusevdm.luxms.model;

import gusevdm.luxms.model.elements.*;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;

/** Factory class for LuxMS model elements. */
public final class LuxModelFactory {

    // todo: logger???

    private LuxModelFactory() {}

    /***/
    public static LuxModelInterface getInstance(LuxDataType dataType, CSVRecord record) {

        switch (dataType) {
            case UNITS:     return new LuxUnit(record);
            case METRICS:   return new LuxMetric(record);
            case PERIODS:
                try {
                    return new LuxPeriod(record);
                } catch (ParseException e) {
                    throw new IllegalStateException("Can't create LuxPeriod instance from CSV record!");
                }
            case LOCATIONS: return new LuxLocation(record);
            case DATA:      return new LuxDataPoint(record);
        }

        // if we reached this statement - type is unknown...
        throw new IllegalArgumentException(String.format("Unknown element type [%s]!", dataType));
    }

}
