package dg.social.crawler.networks.fb;

/**
 * FB auth forms types with various parameters for recognizing.
 */

public enum FbFormType {

    // simple login form
    LOGIN_FORM ("ÐŸÐ¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð° Ðº Ð’ÐšÐ¾Ð½Ñ‚Ð°ÐºÑ‚Ðµ", "Ð”Ð»Ñ� Ð¿Ñ€Ð¾Ð´Ð¾Ð»Ð¶ÐµÐ½Ð¸Ñ� Ð’Ð°Ð¼ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ð¾ Ð²Ð¾Ð¹Ñ‚Ð¸ Ð’ÐšÐ¾Ð½Ñ‚Ð°ÐºÑ‚Ðµ."),
    // unknown place - add missed digits to phone number
    ADD_PHONE_DIGITS_FORM("", ""), // todo: implementation!
    // change application access rights list form
    APPROVE_ACCESS_RIGHTS_FORM("ÐŸÐ¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð° Ðº Ð’ÐšÐ¾Ð½Ñ‚Ð°ÐºÑ‚Ðµ", "Ð·Ð°Ð¿Ñ€Ð°ÑˆÐ¸Ð²Ð°ÐµÑ‚ Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿ Ðº Ð’Ð°ÑˆÐµÐ¼Ñƒ Ð°ÐºÐºÐ°ÑƒÐ½Ñ‚Ñƒ"),
    // final form - blank page with access token
    ACCESS_TOKEN_FORM("OAuth Blank", ""),
    // unknown form
    UNKNOWN_FORM("UNKNOWN FORM", "UNKNOWN INFO");

    /** Html info elements class (usually - <div/>).*/
    public static final String FB_OP_INFO_CLASS_NAME   = "op_info";

    private String formTitle;       // title for form page
    private String opInfoClassText; // <div> class name for action

    FbFormType(String formTitle, String opInfoClassText) {
        this.formTitle       = formTitle;
        this.opInfoClassText = opInfoClassText;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public String getOpInfoClassText() {
        return opInfoClassText;
    }

}
