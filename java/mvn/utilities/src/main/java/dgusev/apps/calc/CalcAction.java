package dgusev.apps.calc;

import org.apache.commons.lang3.StringUtils;

/**
 * Calculator actions (operations) enumeration.
 * Created by vinnypuhh on 03.12.2016.
 */

public enum CalcAction {

    ADD("+"), SUB("-"), MUL("*"), DIV("/"), SIGN("±"), SQRT("√"), PERCENT("%"),
    ONE_DIV("1/x"), EQUALS("="), MC("MC"), MR("MR"), MS("MS"), MPLUS("M+"), MMINUS("M-"),
    BACKSPACE("←"), ERASE_ALL("C"), ERASE_CURRENT("CE");

    private String strValue;

    CalcAction(String strValue) {
        this.strValue = strValue;
    }

    public String getValue() {
        return this.strValue;
    }

    /**
     * If strAction is null, empty or is missing (no such action) - returns ERASE_ALL action.
     */
    // todo: write unit test!
    public static CalcAction getActionByString(String strAction) {
        if (StringUtils.isBlank(strAction)) { // str action is null/empty - return default
            return ERASE_ALL;
        }

        for (CalcAction action : CalcAction.values()) { // action isn't null - search for
            if (action.getValue().equalsIgnoreCase(strAction)) {
                return action;
            }
        }

        // action not found - return default
        return ERASE_ALL;
    }

}
