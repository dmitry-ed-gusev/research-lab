package dg.social.vk;

/**
 * VK auth forms types.
 * Created by gusevdm on 12/15/2016.
 */

public enum VkFormType {

    LOGIN_FORM ("Получение доступа к ВКонтакте", "Для продолжения Вам необходимо войти <b>ВКонтакте</b>."),
    ADD_PHONE_DIGITS_FORM("", ""),
    APPROVE_ACCESS_RIGHTS_FORM("Получение доступа к ВКонтакте", "запрашивает доступ к Вашему аккаунту"),
    ACCESS_TOKEN_FORM("", ""),
    UNKNOWN_FORM("UNKNOWN FORM", "UNKNOWN INFO");

    private String formTitle;
    private String opInfo;

    VkFormType(String formTitle, String opInfo) {
        this.formTitle = formTitle;
        this.opInfo    = opInfo;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public String getOpInfo() {
        return opInfo;
    }

}
