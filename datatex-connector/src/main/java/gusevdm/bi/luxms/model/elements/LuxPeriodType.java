package gusevdm.bi.luxms.model.elements;

import org.apache.commons.lang3.StringUtils;

/** Periods types for LuxMS system. */
public enum LuxPeriodType {

    SECOND  (1),
    MINUTE  (2),
    HOUR    (3),
    DAY     (4),
    WEEK    (5),
    MONTH   (6),
    QUARTER (7),
    YEAR    (8);

    private LuxPeriodType(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static LuxPeriodType getTypeByName(String typeName) {
        if (StringUtils.isBlank(typeName)) {
            return null;
        }

        // todo: error processing!!!
        return LuxPeriodType.valueOf(typeName.toUpperCase());
    }

}
