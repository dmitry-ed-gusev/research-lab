package dg.social.ok;

/**
 * OK auth forms types with params for recognizing.
 * Created by gusevdm on 1/10/2017.
 */

public enum OkFormType {

    LOGIN_FORM(""),
    APPROVE_ACCESS_RIGHTS_FORM(""),
    ACCESS_TOKEN_FORM(""),
    UNKNOWN_FORM("");

    private String zzz;

    OkFormType(String zzz) {
        this.zzz = zzz;
    }

}
