package dg.social;

import org.apache.http.HttpHost;

/**
 * Some common defaults.
 * Created by gusevdm on 12/12/2016.
 */

public final class SocialNetsDefaults {

    /** Default encoding for content. */
    public static final String   DEFAULT_ENCODING = "UTF-8";
    /** Default Merck proxy server. */
    public static final HttpHost MERCK_PROXY      = new HttpHost("webproxy.merck.com", 8080);

    private SocialNetsDefaults() {} // can't instantiate utility class

}
