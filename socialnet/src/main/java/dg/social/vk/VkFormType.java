package dg.social.vk;

/**
 * VK auth forms types.
 * Created by gusevdm on 12/15/2016.
 */

public enum VkFormType {

    LOGIN_FORM ("Получение доступа к ВКонтакте"),
    ADD_PHONE_DIGITS_FORM(""),
    APPROVE_ACCESS_RIGHTS_FORM(""),
    ACCESS_TOKEN_FORM(""),
    UNKNOWN_FORM("UNKNOWN FORM");

    private String formTitle;

    VkFormType(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getFormTitle() {
        return formTitle;
    }

}
