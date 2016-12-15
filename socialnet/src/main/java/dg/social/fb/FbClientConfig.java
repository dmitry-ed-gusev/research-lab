package dg.social.fb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dg.social.AbstractSocialNetConfig;

public class FbClientConfig extends AbstractSocialNetConfig {

	private static final Log LOGGER = LogFactory.getLog(FbClientConfig.class);

	private static final String ACCESS_TOKEN_REQUEST = "https://www.facebook.com/v2.8/dialog/oauth?response_type=token&display=popup&client_id=%s&redirect_uri=%s&scope=%s";
	private static final String CLIENT_ID = "145634995501895";
	private static final String REDIRECT_URL = "https%3A%2F%2Fdevelopers.facebook.com%2Ftools%2Fexplorer%2Fcallback";
	private static final String SCOPE="email%2Cuser_hometown%2Cuser_religion_politics%2Cpublish_actions%2Cuser_likes%2Cuser_status%2Cuser_about_me%2Cuser_location%2Cuser_tagged_places%2Cuser_birthday%2Cuser_photos%2Cuser_videos%2Cuser_education_history%2Cuser_posts%2Cuser_website%2Cuser_friends%2Cuser_relationship_details%2Cuser_work_history%2Cuser_games_activity%2Cuser_relationships%2Cads_management%2Cpages_messaging%2Cread_page_mailboxes%2Cads_read%2Cpages_messaging_payments%2Crsvp_event%2Cbusiness_management%2Cpages_messaging_phone_number%2Cuser_events%2Cmanage_pages%2Cpages_messaging_subscriptions%2Cuser_managed_groups%2Cpages_manage_cta%2Cpages_show_list%2Cpages_manage_instant_articles%2Cpublish_pages%2Cuser_actions.books%2Cuser_actions.music%2Cuser_actions.video%2Cuser_actions.fitness%2Cuser_actions.news%2Cread_audience_network_insights%2Cread_custom_friendlists%2Cread_insights";

	//private String appApiKey; // application API_ID key

	public FbClientConfig(String username, String password){//, String appApiKey) {
		super(username, password);

	//	LOGGER.info("FbClientConfig constructor() working.");
	//	this.appApiKey = appApiKey;
	}

	//public String getAppApiKey() {
	//	return appApiKey;
	//}

	/**
	 * Generates and return string http request for getting application
	 * ACCESS_TOKEN.
	 */
	public String getAccessTokenRequest() {
		return String.format(ACCESS_TOKEN_REQUEST, CLIENT_ID, REDIRECT_URL, SCOPE);

	}

}
