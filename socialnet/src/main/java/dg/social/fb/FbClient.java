package dg.social.fb;

import static dg.social.HttpUtilities.HTTP_DEFAULT_CONTENT_ENCODING;
import static dg.social.HttpUtilities.HTTP_DEFAULT_HEADERS;
import static dg.social.HttpUtilities.HTTP_GET_COOKIES_HEADER;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import dg.social.HttpUtilities;
import dg.social.vk.VkClient;

public class FbClient {

	private static final Log LOG = LogFactory.getLog(FbClient.class); // module
																		// logger

	private final CloseableHttpClient HTTP_CLIENT = HttpClientBuilder.create()
			.setRedirectStrategy(new LaxRedirectStrategy()).build();
	private final HttpContext HTTP_CONTEXT = new BasicHttpContext();
	private final RequestConfig HTTP_REQUEST_CONFIG;

	// VK user/app credentials (user, pass, api_id)
	private static final String USER_LOGIN = "djromen@gmail.com";
	// private static final String VK_USER_LOGIN_MISSED_DIGITS = "96180114";
	private static final String USER_PASS = "hg5Wa7yV9";
	// private static final String VK_APP_ID = "5761788";

	private FbClientConfig config; // VK client configuration
	private HttpHost proxyHost; // proxy for working trough

	/** Create VkClient instance, working through proxy. */
	public FbClient(FbClientConfig config, HttpHost proxyHost) {
		this.config = config;
		this.proxyHost = proxyHost;
		// init http request config (through builder)
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		// set proxy (if needed)
		if (this.proxyHost != null) { // add proxyHost to get http request
			requestConfigBuilder.setProxy(this.proxyHost).build();
		}
		this.HTTP_REQUEST_CONFIG = requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	}

	/** Create VkClient instance, working directly (without proxy). */
	public FbClient(FbClientConfig config) {
		this(config, null);
	}

	public String getAccessToken() throws IOException {
		LOG.debug("getAccessToken() working.");

		// generate and execute ACCESS_TOKEN request
		String fbTokenRequest = this.config.getAccessTokenRequest();
		LOG.debug(String.format("Http request for ACCESS_TOKEN: [%s].", fbTokenRequest));

		// HTTP request #1: execute http get request to token request URI
		HttpGet httpGetInitial = new HttpGet(fbTokenRequest);
		httpGetInitial.setHeaders(HTTP_DEFAULT_HEADERS);
		httpGetInitial.setConfig(HTTP_REQUEST_CONFIG);
		// execute request
		CloseableHttpResponse httpResponseInitial = HTTP_CLIENT.execute(httpGetInitial);
		// save initial cookies
		Header[] httpInitialCookies = httpResponseInitial.getHeaders(HTTP_GET_COOKIES_HEADER);

		// for (Header header : cookies) {
		// System.out.println("===> " + header.getName() + ":::" +
		// header.getValue());
		// }
		// System.exit(777);

		// try {

		if (LOG.isDebugEnabled()) { // just debug output
			LOG.debug(HttpUtilities.httpResponseToString(httpResponseInitial, true));
		}

		// get page content for parsing
		String pageContent = HttpUtilities.getPageContent(httpResponseInitial.getEntity(),
				HTTP_DEFAULT_CONTENT_ENCODING);
		// if (LOG.isDebugEnabled()) {
		// LOG.debug(String.format("Received response:%n%s", pageContent));
		// }

		// parse returned page
		Document doc = Jsoup.parse(pageContent);

		return null;
	}

	// }

	/*
	 * if (!doc.getElementsByClass(LOGIN_FORM_DIV_CLASS).isEmpty()) { // we've
	 * get login form - perform login
	 * LOG.debug("Received VK login form. Performing login.");
	 * 
	 * // get action attribute from html form String actionUrl =
	 * VkClient.getVKLoginFormActionURL(doc);
	 * LOG.debug(String.format("Form action: [%s].", actionUrl));
	 * 
	 * // get vk login form parameters List<NameValuePair> paramList =
	 * VkClient.getVKLoginFormParams(doc); if (LOG.isDebugEnabled()) { // just a
	 * debug StringBuilder pairs = new StringBuilder(); paramList.forEach(pair
	 * -> pairs.append(String.format("pair -> key = [%s], value = [%s]%n",
	 * pair.getName(), pair.getValue())));
	 * LOG.debug(String.format("Found name-value pairs in VK login form:%n%s",
	 * pairs.toString())); }
	 * 
	 * // save cookies //String cookies = response.getFirstHeader("Set-Cookie")
	 * == null ? "" : response.getFirstHeader("Set-Cookie").toString();
	 * //Header[] cookies = response.getHeaders("Set-Cookies");
	 * 
	 * // send http post request (submit the form) - login to VK
	 * //HttpUtilities.sendPost(HTTP_CLIENT, actionUrl, paramList, cookies); //
	 * prepare post request to submit a form
	 * 
	 * HttpPost httpPost = new HttpPost(actionUrl);
	 * httpPost.setHeaders(HTTP_DEFAULT_HEADERS);
	 * httpPost.setHeader(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_FORM); for
	 * (Header header : httpInitialCookies) {
	 * httpPost.setHeader(HTTP_SET_COOKIES_HEADER, header.getValue()); }
	 * httpPost.setConfig(HTTP_REQUEST_CONFIG); // set entity
	 * httpPost.setEntity(new UrlEncodedFormEntity(paramList));
	 * 
	 * // execute query #2
	 * 
	 * HttpResponse responsePost = HTTP_CLIENT.execute(httpPost, HTTP_CONTEXT);
	 * Header[] cookies2 = responsePost.getHeaders(HTTP_GET_COOKIES_HEADER); for
	 * (Header header : cookies2) { System.out.println("===> " +
	 * header.getName() + ":::" + header.getValue()); }
	 * 
	 * int responseCode = responsePost.getStatusLine().getStatusCode();
	 * 
	 * System.out.println("\nSending 'POST' request to URL : " + actionUrl);
	 * System.out.println("Post parameters : " + paramList);
	 * System.out.println("Response Code : " + responseCode);
	 * 
	 * System.out.println("===> " + httpPost.getURI()); String pageContent2 =
	 * HttpUtilities.getPageContent(responsePost.getEntity(), "UTF-8");
	 * System.out.println("2--->\n" + pageContent2);
	 * 
	 * // processing of "Add missing phone number digits" form
	 * 
	 * if (LOG.isDebugEnabled()) { // debug output of received headers // print
	 * response headers HeaderIterator hIterator =
	 * responsePost.headerIterator(); while (hIterator.hasNext()) {
	 * LOG.debug(String.format("Header [%s].", hIterator.next())); } } // parse
	 * returned page #2 Document doc2 = Jsoup.parse(pageContent2);
	 * 
	 * URI finalUrl = httpPost.getURI();
	 * System.out.println("1************************* = " + finalUrl);
	 * 
	 * RedirectLocations locations = (RedirectLocations)
	 * HTTP_CONTEXT.getAttribute(HttpClientContext.REDIRECT_LOCATIONS); if
	 * (locations != null) { finalUrl =
	 * locations.getAll().get(locations.getAll().size() - 1);
	 * System.out.println("2************************* = " + finalUrl); }
	 * 
	 * 
	 * // todo: the same check for form as previous one! (???) if
	 * (!doc2.getElementsByClass(LOGIN_FORM_DIV_CLASS).isEmpty()) { // we've get
	 * new form - add missed digits
	 * LOG.debug("Received VK login form (#2). Performing login.");
	 * 
	 * // get action attribute from html form String actionUrl2 =
	 * VkClient.getVKLoginFormActionURL(doc2);
	 * LOG.debug(String.format("Form action: [%s].", actionUrl2));
	 * 
	 * // get vk login form parameters List<NameValuePair> paramList2 =
	 * VkClient.getVKLoginFormParams(doc2); if (LOG.isDebugEnabled()) { // just
	 * a debug StringBuilder pairs = new StringBuilder();
	 * paramList2.forEach(pair ->
	 * pairs.append(String.format("pair -> key = [%s], value = [%s]%n",
	 * pair.getName(), pair.getValue())));
	 * LOG.debug(String.format("Found name-value pairs in VK login form:%n%s",
	 * pairs.toString())); }
	 * 
	 * // prepare and run 3rd query - add missed digits to form HttpPost
	 * httpPost2 = new HttpPost("https://vk.com" + actionUrl2);
	 * httpPost2.setHeaders(HTTP_DEFAULT_HEADERS);
	 * httpPost2.setHeader(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_FORM);
	 * for (Header header : cookies2) {
	 * httpPost2.setHeader(HTTP_SET_COOKIES_HEADER, header.getValue()); }
	 * httpPost2.setConfig(HTTP_REQUEST_CONFIG); // set entity
	 * httpPost2.setEntity(new UrlEncodedFormEntity(paramList2));
	 * 
	 * // execute query #3 HttpResponse responsePost2 =
	 * HTTP_CLIENT.execute(httpPost2); Header[] cookies3 =
	 * responsePost2.getHeaders(HTTP_GET_COOKIES_HEADER); for (Header header :
	 * cookies3) { System.out.println("===> " + header.getName() + ":::" +
	 * header.getValue()); }
	 * 
	 * int responseCode2 = responsePost2.getStatusLine().getStatusCode();
	 * 
	 * System.out.println("\nSending 'POST' request to URL : " + actionUrl2);
	 * System.out.println("Post parameters : " + paramList2);
	 * System.out.println("Response Code : " + responseCode2);
	 * 
	 * System.out.println("===> " + httpPost2.getURI()); String pageContent3 =
	 * HttpUtilities.getPageContent(responsePost2.getEntity(), "UTF-8");
	 * System.out.println("!!!--->\n" + pageContent3);
	 * 
	 * 
	 * } else { LOG.error("Received unknown VK form (#2). Can't process!"); }
	 * 
	 * } else { LOG.error("Received unknown VK form. Can't process!"); }
	 * 
	 * } finally { httpResponseInitial.close(); //httpclient.close(); }
	 */
	// return null;
	// }

	public static void main(String[] args) throws IOException {

		Log log = LogFactory.getLog(VkClient.class);
		log.info("FB Client starting.");

		FbClientConfig config = new FbClientConfig(USER_LOGIN, USER_PASS);// ;,
																			// VK_APP_ID);

		FbClient fbClient = new FbClient(config, HttpUtilities.HTTP_DEFAULT_PROXY);

		String access_token = fbClient.getAccessToken();

		// vkClient.login();

		log.info("VK Client finished.");

	}

}
