package dgusev.datetime;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** Simple time periods types enumeration. */

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

    @Getter private final int value;
    @Getter private final int days;

    public static TimePeriodType getTypeByName(String typeName) {
        if (StringUtils.isBlank(typeName)) {
            return null;
        }

        // todo: implement error processing!!!
        return TimePeriodType.valueOf(typeName.toUpperCase());
    }

}
