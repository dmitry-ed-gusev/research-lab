package dg.social;

/**
 * Common http forms types.
 * Created by gusevdm on 1/11/2017.
 */

public enum HttpFormType {

    LOGIN_FORM,                 // simple login form
    APPROVE_ACCESS_RIGHTS_FORM, // approve application access rights form
    ACCESS_TOKEN_FORM,          // form/page with new access token
    ADD_MISSED_DIGITS_FORM,     // add missed phone number digits form
    UNKNOWN_FORM                // completely unknown form

}
