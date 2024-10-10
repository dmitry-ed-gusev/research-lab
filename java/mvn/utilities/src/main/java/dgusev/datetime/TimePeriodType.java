package dgusev.datetime;

import lombok.Getter;
import lombok.NonNull;

import java.util.Calendar;

/** Simple time periods types enumeration. */

public enum TimePeriodType {

    SECOND  (1, 0,   Calendar.SECOND),
    MINUTE  (2, 0,   Calendar.MINUTE),
    HOUR    (3, 0,   Calendar.HOUR_OF_DAY),
    DAY     (4, 1,   Calendar.DAY_OF_YEAR),
    WEEK    (5, 7,   Calendar.WEEK_OF_YEAR),
    MONTH   (6, 31,  Calendar.MONTH),  // the longest month is 31 days
    QUARTER (7, 93,  -1),  // quarter with the longest months
    YEAR    (8, 366, Calendar.YEAR);    // high year

    TimePeriodType(int value, int days, int calendarValue) {
        this.value         = value;
        this.days          = days;
        this.calendarValue = calendarValue;
    }

    @Getter private final int value;
    @Getter private final int days;
    @Getter private final int calendarValue;

    public static TimePeriodType getTypeByName(@NonNull String typeName) {
        return TimePeriodType.valueOf(typeName.toUpperCase());
    }

}
