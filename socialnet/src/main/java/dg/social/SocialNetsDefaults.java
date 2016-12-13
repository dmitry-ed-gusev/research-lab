package dg.social;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;

/**
 * Some common defaults.
 * Created by gusevdm on 12/12/2016.
 */

public final class SocialNetsDefaults {

    /**
     * Default encoding for content.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * Default Merck proxy server.
     */
    public static final HttpHost MERCK_PROXY = new HttpHost("webproxy.merck.com", 8080);

    /**
     * Default http headers for client.
     */
    public static final Header[] HTTP_HEADERS = {
            new BasicHeader("User-Agent", "Mozilla/5.0"),
            new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
            //new BasicHeader("Accept-Language", "en-US,en;q=0.5"),
            new BasicHeader("Accept-Language", "ru-RU,ru;q=0.5"),
            new BasicHeader("Connection", "keep-alive")
    };

    private SocialNetsDefaults() {
    } // can't instantiate utility class

}
