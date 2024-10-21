package gusev.dmitry.jtils;

/**
 * CMD line options for utilities application.
 * @author Gusev Dmitry (Чебурашка)
 * @version 1.0 (DATE: 21.12.2014)
*/

public enum Options {
    CALC     ("-calc",      "Calculator application."),
    WATCH    ("-watch",     "Presentation Watch (timer) application."),
    DLMANAGER("-dlmanager", "Download Manager application.");

    /***/
    Options(String optionName, String description) {
        this.optionName  = optionName;
        this.description = description;
    }

    private String optionName;
    private String description;

    public String getOptionName() {
        return optionName;
    }

    public String getDescription() {
        return description;
    }

    /***/
    public static int getMaxOptionLength() {
        int result = 0;
        for (Options option : Options.values()) {
            if (option.getOptionName().length() > result) {
                result = option.getOptionName().length();
            }
        }
        return result;
    }

}