package dg.social.fb;

import dg.social.crawler.AbstractClientConfig;

import java.util.Properties;

/**
 * Config for Facebook client.
 */
public class FbClientConfig extends AbstractClientConfig {

	private static final String ACCESS_TOKEN_REQUEST = "https://www.facebook.com/%s/dialog/oauth?response_type=token&display=popup&client_id=%s&redirect_uri=%s&scope=%s";
    private static final String FB_API_VERSION = "v2.8"; 
	private static final String CLIENT_ID = "145634995501895";
	private static final String REDIRECT_URL = "https%3A%2F%2Fdevelopers.facebook.com%2Ftools%2Fexplorer%2Fcallback";
	private static final String SCOPE="email%2Cuser_hometown%2Cuser_religion_politics%2Cpublish_actions%2Cuser_likes%2Cuser_status%2Cuser_about_me%2Cuser_location%2Cuser_tagged_places%2Cuser_birthday%2Cuser_photos%2Cuser_videos%2Cuser_education_history%2Cuser_posts%2Cuser_website%2Cuser_friends%2Cuser_relationship_details%2Cuser_work_history%2Cuser_games_activity%2Cuser_relationships%2Cads_management%2Cpages_messaging%2Cread_page_mailboxes%2Cads_read%2Cpages_messaging_payments%2Crsvp_event%2Cbusiness_management%2Cpages_messaging_phone_number%2Cuser_events%2Cmanage_pages%2Cpages_messaging_subscriptions%2Cuser_managed_groups%2Cpages_manage_cta%2Cpages_show_list%2Cpages_manage_instant_articles%2Cpublish_pages%2Cuser_actions.books%2Cuser_actions.music%2Cuser_actions.video%2Cuser_actions.fitness%2Cuser_actions.news%2Cread_audience_network_insights%2Cread_custom_friendlists%2Cread_insights";

	
	
    // config prefix for VK parameters (for Properties)
    private static final String DEFAULT_CONFIG_PREFIX   = "fb";
    // access token request result - display type (page, popup, mobile)
   
    /*private static final String DISPLAY_TYPE            = "page";
    // default redirect URI for Implicit flow
    private static final String REDIRECT_URI            = "http://oauth.vk.com/blank.html";
    // scope for requesting access token (see https://vk.com/dev/permissions)
    private static final String REQUEST_SCOPE           = "friends,groups,photos,audio";
            //"friends,photos,audio,video,pages,status,notes,messages,offline,docs,groups,notifications,stats,email";
 */
    
    // VK API URL. Here you should set method name (use String.format())
  //  private static final String VK_API_REQUEST_URI      = "https://api.vk.com/method/%s";
    // VK API version
  

    /** Init instance with string values. */
    public FbClientConfig(String username, String password, String appApiKey, String tokenFileName, String proxyHost, int proxyPort) {
        super(username, password, appApiKey, tokenFileName, proxyHost, proxyPort);
    }

    /** Init instance with values from properties object. */
    public FbClientConfig(Properties properties) {
        super(DEFAULT_CONFIG_PREFIX, properties);
    }

    @Override
    
	/**
	 * Generates and return string http request for getting application
	 * ACCESS_TOKEN.
	 */
	public String getAccessTokenRequest() {
		return String.format(ACCESS_TOKEN_REQUEST, FB_API_VERSION, CLIENT_ID, REDIRECT_URL, SCOPE);

	}

	@Override
	public String getBaseApiRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApiVersion() {
		// TODO Auto-generated method stub
		return null;
	}
    
  //  public String getAccessTokenRequest() {
 //       return String.format(ACCESS_TOKEN_REQUEST, this.getAppApiKey(), DISPLAY_TYPE, REDIRECT_URI, REQUEST_SCOPE);
 //   }

  /*  @Override
    public String getBaseApiRequest() {
        return VK_API_REQUEST_URI;
    }

    @Override
    public String getApiVersion() {
        return VK_API_VERSION;
    }
*/
}
