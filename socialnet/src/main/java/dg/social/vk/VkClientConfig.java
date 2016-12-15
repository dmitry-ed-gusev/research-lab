package dg.social.vk;

import dg.social.AbstractSocialNetConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Config for VK client.
 * Created by gusevdm on 12/12/2016.
 */

public class VkClientConfig extends AbstractSocialNetConfig {

    private static final Log LOG = LogFactory.getLog(VkClientConfig.class);

    // access token request result - display type (page, popup, mobile)
    private static final String DISPLAY_TYPE  = "page";
    // default redirect URI for Implicit flow
    private static final String REDIRECT_URI  = "http://oauth.vk.com/blank.html";
    // scope for requesting access token (see https://vk.com/dev/permissions)
    private static final String REQUEST_SCOPE = "friends,photos,audio";
            //"friends,photos,audio,video,pages,status,notes,messages,offline,docs,groups,notifications,stats,email";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST =
            "https://oauth.vk.com/authorize?client_id=%s&display=%s&redirect_uri=%s&scope=%s&response_type=token&v=5.60&state=123456";

    private String appApiKey; // application API_ID key

    public VkClientConfig(String username, String password, String appApiKey) {
        super(username, password);

        LOG.debug("VkClientConfig constructor() working.");
        this.appApiKey = appApiKey;
    }

    public String getAppApiKey() {
        return appApiKey;
    }

    /** Generates and return string http request for getting application ACCESS_TOKEN. */
    public String getAccessTokenRequest() {
        return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
    }

}
