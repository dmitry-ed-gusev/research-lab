package dg.social.crawler;

import java.text.SimpleDateFormat;

/**
 * Some common defaults for SCrawler.
 * Created by gusevdm on 12/12/2016.
 */

public final class SCrawlerDefaults {

    private SCrawlerDefaults() {} // mustn't instantiate utility class!

    /** Default encoding for content. */
    public static final String           DEFAULT_ENCODING    = "UTF-8";
    /** Default date/time format. */
    public static final SimpleDateFormat DATE_TIME_FORMAT    = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    /** Crawler config Spring bean ID */
    public static final String           CRAWLER_CONFIG_BEAN = "crawler.config";

    /** Common HTML forms types (for social networks). */
    public enum HttpFormType {
        LOGIN_FORM,                 // simple login form
        APPROVE_ACCESS_RIGHTS_FORM, // approve application access rights form
        ACCESS_TOKEN_FORM,          // form/page with new access token
        ADD_MISSED_DIGITS_FORM,     // add missed phone number digits form
        UNKNOWN_FORM                // completely unknown form
    }

    /** Social networks types. */
    public enum SocialNetwork {
        VK, TELESCOPE
    }

}
