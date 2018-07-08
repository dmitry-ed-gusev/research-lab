package gusevdm.helpers;

/** Command-line exit statuses enumeration. */
public enum ExitStatus {
    OK(0), GENERAL_ERROR(1), MISUSE(2);

    private final int value;

    ExitStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}