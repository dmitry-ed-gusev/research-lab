package dg.social.vk;

/**
 * VK auth forms types with various parameters for recognizing.
 * Created by gusevdm on 12/15/2016.
 */

public enum VkFormType {

    LOGIN_FORM ("Получение доступа к ВКонтакте", "Для продолжения Вам необходимо войти ВКонтакте.", "form_item fi_fat"),
    ADD_PHONE_DIGITS_FORM("", "", ""),
    APPROVE_ACCESS_RIGHTS_FORM("Получение доступа к ВКонтакте", "запрашивает доступ к Вашему аккаунту", "form_item"),
    ACCESS_TOKEN_FORM("", "", ""),
    UNKNOWN_FORM("UNKNOWN FORM", "UNKNOWN INFO", "UNKNOWN CLASS");

    /***/
    public static final String VK_OP_INFO_CLASS_NAME   = "op_info";
    /** <div> element class (div with this class holds login form for VK) */
    public static final String VK_LOGIN_FORM_DIV_CLASS = "form_item fi_fat"; // todo: move to enum (see above)

    private String formTitle;       // title for form page
    private String opInfoClassText; // <div> class name for action
    private String formDivClass;    // class name for <div> element, that contains form

    VkFormType(String formTitle, String opInfoClassText, String formDivClass) {
        this.formTitle       = formTitle;
        this.opInfoClassText = opInfoClassText;
        this.formDivClass    = formDivClass;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public String getOpInfoClassText() {
        return opInfoClassText;
    }

    public String getFormDivClass() {
        return formDivClass;
    }

}
