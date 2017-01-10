package dg.social.vk;

/**
 * VK auth forms types with various parameters for recognizing.
 * Created by gusevdm on 12/15/2016.
 */

public enum VkFormType {

    // simple login form
    LOGIN_FORM ("Получение доступа к ВКонтакте", "Для продолжения Вам необходимо войти ВКонтакте."),
    // unknown place - add missed digits to phone number
    ADD_PHONE_DIGITS_FORM("", ""), // todo: implementation!
    // change application access rights list form
    APPROVE_ACCESS_RIGHTS_FORM("Получение доступа к ВКонтакте", "запрашивает доступ к Вашему аккаунту"),
    // final form - blank page with access token
    ACCESS_TOKEN_FORM("OAuth Blank", ""),
    // unknown form
    UNKNOWN_FORM("UNKNOWN FORM", "UNKNOWN INFO");

    /** Html info elements class (usually - <div/>).*/
    public static final String VK_OP_INFO_CLASS_NAME   = "op_info";

    private String formTitle;       // title for form page
    private String opInfoClassText; // <div> class name for action

    VkFormType(String formTitle, String opInfoClassText) {
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
