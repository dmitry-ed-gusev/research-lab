package gusevdm.luxms.model.elements;

/** Periods types for LuxMS system. */
public enum LuxPeriodType {

    SECONDS  (1),
    MINUTES  (2),
    HOURS    (3),
    DAYS     (4),
    WEEKS    (5),
    MONTHS   (6),
    QUARTERS (7),
    YEARS    (8);

    private LuxPeriodType(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

}
