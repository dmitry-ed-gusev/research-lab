package dgusev.utils;

import org.apache.commons.lang3.StringUtils;

/** Time periods types. */
public enum TimePeriodType {

    SECOND  (1, 0),
    MINUTE  (2, 0),
    HOUR    (3, 0),
    DAY     (4, 1),
    WEEK    (5, 7),
    MONTH   (6, 31),  // the longest month is 31 days
    QUARTER (7, 93),  // quarter with the longest months
    YEAR    (8, 366); // high year

    TimePeriodType(int value, int days) {
        this.value = value;
        this.days  = days;
    }

    private final int value;
    private final int days;

    public int getValue() {
        return value;
    }

    public int getDays() {
        return days;
    }

    public static TimePeriodType getTypeByName(String typeName) {
        if (StringUtils.isBlank(typeName)) {
            return null;
        }

        // todo: error processing!!!
        return TimePeriodType.valueOf(typeName.toUpperCase());
    }

}
