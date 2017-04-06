package dg.social.crawler.vk;

import dg.social.crawler.AbstractClientConfig;

import java.util.Properties;

/**
 * Config for VK (VKontakte) client.
 * Created by gusevdm on 12/12/2016.
 */
public class VkClientConfig extends AbstractClientConfig {

    // config prefix for VK parameters (for Properties)
    private static final String DEFAULT_CONFIG_PREFIX   = "vk";
    // access token request result - display type (page, popup, mobile)
    private static final String DISPLAY_TYPE            = "page";
    // default redirect URI for Implicit flow
    private static final String REDIRECT_URI            = "http://oauth.vk.com/blank.html";
    // scope for requesting access token (see https://vk.com/dev/permissions)
    private static final String REQUEST_SCOPE           = "friends,groups,photos,audio,video";
            //"friends,photos,audio,video,pages,status,notes,messages,offline,docs,groups,notifications,stats,email";
    // request for access token
    private static final String ACCESS_TOKEN_REQUEST    =
            "https://oauth.vk.com/authorize?client_id=%s&display=%s&redirect_uri=%s&scope=%s&response_type=token&v=5.60&state=123456";
    // VK API URL. Here you should set method name (use String.format())
    private static final String VK_API_REQUEST_URI      = "https://api.vk.com/method/%s";
    // VK API version
    private static final String VK_API_VERSION          = "5.60"; // last API version, 27.12.2016

    /** Init instance with string values. */
    public VkClientConfig(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        super(username, password, appApiKey, tokenFileName, proxyHost, proxyPort);
    }

    /** Init instance with values from properties object. */
    public VkClientConfig(Properties properties) {
        super(DEFAULT_CONFIG_PREFIX, properties);
    }

    @Override
    public String getAccessTokenRequest() {
        return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
    }

    @Override
    public String getBaseApiRequest() {
        return VK_API_REQUEST_URI;
    }

    @Override
    public String getApiVersion() {
        return VK_API_VERSION;
    }

}
