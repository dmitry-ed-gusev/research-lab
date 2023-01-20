package dgusev.web.rest;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.extern.apachecommons.CommonsLog;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

// parameterized unit test example (junit 4) -> https://www.mkyong.com/unittest/junit-4-tutorial-6-parameterized-test/
// junit 5 parameterized test -> https://stackoverflow.com/questions/14082004/create-multiple-parameter-sets-in-one-parameterized-class-junit
// having some tests run only once -> https://stackoverflow.com/questions/32776335/when-using-junits-parameterized-can-i-have-some-tests-still-run-only-once

/** Unit tests for RestClient. Used JUnit 4. */
@RunWith(Parameterized.class) // <- parameterized tests class (junit 4)
@CommonsLog
public class RestClientTest {

    // mock REST server parameters (headers, answers, etc.)
    private static final int                         MOCK_REST_SERVER_PORT            = 3030;
    private static final String                      MOCK_REST_SERVER_HOST            = "localhost";
    private static final String                      MOCK_REST_SERVER_FULL_HTTP_HOST  =
            String.format("http://%s:%s", MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT);
    private static final String                      MOCK_REST_SERVER_FULL_HTTPS_HOST =
            String.format("https://%s:%s", MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT);
    private static final Header                      MOCK_REST_HEADER                 =
            new Header("Content-Type", "application/json");
    private static final org.mockserver.model.Cookie MOCK_REST_COOKIE                 =
            new org.mockserver.model.Cookie("cookie_name", "cookie_value");

    // HTTP REST request parameters
    private static final Cookie                         REST_REQUEST_COOKIE =
            new Cookie("cookie_name", "cookie_value");
    private static final MultivaluedMap<String, String> REST_REQUEST_HEADER =
            new MultivaluedMapImpl() {{
                putSingle("Content-Type", "application/json");
            }};

    // HTTP server JSON answers
    private static final String JSON_SUCCESS_ANSWER_STR = "{\"success\":\"true\"}";

    @Rule // <- rule for expected exception to be thrown
    public ExpectedException expectedException = ExpectedException.none();

    // type of parameter in parameterized test (method-provider)
    enum Type {URL_EMPTY, URL_INVALID, PATH_GET, PATH_POST, PATH_PUT, PATH_DELETE}

    // public access is required by JUnit 4 Parameterized Runner
    @Parameter (value = 0) // <- parameter for parameterized test case (index in array = 0)
    public Type urlType;
    @Parameter(value = 1) // <- parameter for parameterized test case (index in array = 1)
    public String url;

    @Parameters(name = "{index}: test -> {0}, url -> {1}") // <- test data provider - provides test values for test method
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {Type.URL_EMPTY,   "http://"},
                {Type.URL_EMPTY,   " http:///  "},
                {Type.URL_EMPTY,   " https://?"},
                {Type.URL_EMPTY,   "https://:   "},
                {Type.URL_EMPTY,   "   http://   :   "},
                {Type.URL_EMPTY,   "http://:8080    "},
                {Type.URL_INVALID, null},
                {Type.URL_INVALID, ""},
                {Type.URL_INVALID, "          "},
                {Type.URL_INVALID, "  \n"},
                {Type.URL_INVALID, "\t   "},
                {Type.URL_INVALID, " localhost"},
                {Type.PATH_GET,    "/path/get"},
                {Type.PATH_POST,   "/path/post"},
                {Type.PATH_PUT,    "/path/put"},
                {Type.PATH_DELETE, "/path/delete"}
        });
    } // end of data() method

    // internal rest clients - for tests
    private static RestClient      httpRestClient;
    private static RestClient      httpsRestClient;

    // mock rest server - for tests
    private static ClientAndServer mockRestServer;

    @BeforeClass
    public static void startInitAndMockServers() throws KeyManagementException, NoSuchAlgorithmException {

        // REST over HTTP client instance (don't trust specified host)
        RestClientTest.httpRestClient  = new RestClient(MOCK_REST_SERVER_FULL_HTTP_HOST);  // http rest client instance
        // REST over HTTPS client instance (trust specified host)
        RestClientTest.httpsRestClient = new RestClient(MOCK_REST_SERVER_FULL_HTTPS_HOST, true);

        // start mock server
        RestClientTest.mockRestServer = startClientAndServer(MOCK_REST_SERVER_PORT);
        // build mock server client expectations
        RestClientTest.createRestServerClientExpectations();
    }

    @AfterClass
    public static void stopMockServers() {
        RestClientTest.mockRestServer.stop();
    }

    @Before
    public void beforeEach() {
        LOG.debug("RestClientTest.beforeEach() is working.");
    }

    @After
    public void afterEach() {
        LOG.debug("RestClientTest.afterEach() is working.");
    }

    private static void createRestServerClientExpectations() {

        // #1. Test GET request (2 times - for http and https requests)
        new MockServerClient(MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("GET")
                                .withHeader(MOCK_REST_HEADER)
                                .withCookie(MOCK_REST_COOKIE)
                                .withPath("/path/get"),
                        exactly(2)) // <- 2 times - http/https
                .respond(response() // <- response
                                .withStatusCode(200)
                                .withHeaders(MOCK_REST_HEADER)
                                .withCookie(MOCK_REST_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withDelay(new Delay(SECONDS, 1)));

        // #2. Test POST request
        new MockServerClient(MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("POST")
                                .withHeader(MOCK_REST_HEADER)
                                .withCookie(MOCK_REST_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/post"),
                        exactly(2))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(MOCK_REST_HEADER)
                        .withCookie(MOCK_REST_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

        // #3. Test PUT request
        new MockServerClient(MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("PUT")
                                .withHeader(MOCK_REST_HEADER)
                                .withCookie(MOCK_REST_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/put"),
                        exactly(2))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(MOCK_REST_HEADER)
                        .withCookie(MOCK_REST_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

        // #4. Test DELETE request
        new MockServerClient(MOCK_REST_SERVER_HOST, MOCK_REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("DELETE")
                                .withHeader(MOCK_REST_HEADER)
                                .withCookie(MOCK_REST_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/delete"),
                        exactly(2))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(MOCK_REST_HEADER)
                        .withCookie(MOCK_REST_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

    }

    @Test
    public void testRestClientConstructorInvalidUrl() throws KeyManagementException, NoSuchAlgorithmException {
        Assume.assumeTrue(urlType == Type.URL_INVALID);
        this.expectedException.expect(IllegalArgumentException.class);
        new RestClient(url, true);
    }

    @Test
    public void testRestClientConstructorEmptyUrl() throws KeyManagementException, NoSuchAlgorithmException {
        Assume.assumeTrue(urlType == Type.URL_EMPTY);
        this.expectedException.expect(IllegalStateException.class);
        new RestClient(url, true);
    }

    @Test
    public void testProcessUrlInvalidUrl() {
        // test only for invalid urls
        Assume.assumeTrue(urlType == Type.URL_INVALID);
        // expect this exception
        this.expectedException.expect(IllegalArgumentException.class);
        // tests itself
        RestClient.processUrl(url);
    }

    @Test
    public void testProcessUrlRightUrl() {
        // sample data (values) + expected results (keys)
        Map<String, String> urlsMap = new HashMap<String, String>() {{
            put("http://host1",      "http://host1");
            put("https://host2.com", "    https://host2.com          ");
            put("http://host3.com:8080",  "   hTTp://hOST3.COm:8080      ");
        }};

        // tests on sample map (each key and processed value should match)
        urlsMap.forEach((key, value) -> assertEquals(key, RestClient.processUrl(value)));
    }

    @Test
    public void testExtractHostInvalidUrl() {
        // test only for invalid urls
        Assume.assumeTrue(urlType == Type.URL_INVALID);
        // expect this exception
        this.expectedException.expect(IllegalArgumentException.class);
        // tests itself
        RestClient.extractHost(url);
    }

    @Test
    public void testExtractHostResultInEmptyUrl() {
        // test only for empty urls
        Assume.assumeTrue(urlType == Type.URL_EMPTY);
        this.expectedException.expect(IllegalStateException.class);
        RestClient.extractHost(url);
    }

    @Test
    public void testExtractHostRightUrl() {
        Map<String, String> urlsMap = new HashMap<String, String>() {{
            put("hostname",           "   hTTp://hostname    ");
            put("hostname.com",       "     hTTps://hostname.com/path1/path2     ");
            put("domain.hostname.ru", "   hTTp://domain.hostname.ru?name=value    ");
            put("my.own.host",        " https://my.own.host/path1/path2?name=value    ");
            put("myhost.com",         "    http://myhost.com:8080/path1/path2?name=value    ");
        }};

        // tests on sample map
        urlsMap.forEach((key, value) -> assertEquals(key, RestClient.extractHost(value)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildClientWithIllegalCharacterInPath() {
        httpRestClient.buildClient("/path1\n/path2", new MediaType(), new Cookie("name", "value"), null);
    }

    @Test
    public void testBuildClientProperPath() {
        httpRestClient.buildClient("/path1/path2", new MediaType(), new Cookie("name", "value"), null);
    }

    @Test
    public void testSuccessHttpGetRestRequest() {
        Assume.assumeTrue(urlType == Type.PATH_GET);

        // execute GET request
        RestResponse response = httpRestClient.executeGet(url, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpsGetRestRequest() {
        Assume.assumeTrue(urlType == Type.PATH_GET);

        // execute GET request
        RestResponse response = httpsRestClient.executeGet(url, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpPostRestRequest() {
        Assume.assumeTrue(urlType == Type.PATH_POST);

        // execute POST request
        RestResponse response = httpRestClient.executePost(url, JSON_SUCCESS_ANSWER_STR,
                MediaType.APPLICATION_JSON_TYPE, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpsPostRestRequest() {
        Assume.assumeTrue(urlType == Type.PATH_POST);

        // execute POST request
        RestResponse response = httpsRestClient.executePost(url, JSON_SUCCESS_ANSWER_STR,
                MediaType.APPLICATION_JSON_TYPE, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpPutRestRequest() throws ParseException {
        Assume.assumeTrue(urlType == Type.PATH_PUT);

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute PUT request
        RestResponse response = httpRestClient.executePut(url, jsonObject, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpsPutRestRequest() throws ParseException {
        Assume.assumeTrue(urlType == Type.PATH_PUT);

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute PUT request
        RestResponse response = httpsRestClient.executePut(url, jsonObject, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpDeleteRestRequest() throws ParseException {
        Assume.assumeTrue(urlType == Type.PATH_DELETE);

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute DELETE request
        RestResponse response = httpRestClient.executeDelete(url, jsonObject, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

    @Test
    public void testSuccessHttpsDeleteRestRequest() throws ParseException {
        Assume.assumeTrue(urlType == Type.PATH_DELETE);

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute DELETE request
        RestResponse response = httpsRestClient.executeDelete(url, jsonObject, REST_REQUEST_COOKIE, REST_REQUEST_HEADER);

        // general tests/assertions
        assertEquals(200, response.getStatus());
        assertEquals(JSON_SUCCESS_ANSWER_STR, response.getBodyObject().toString());
        // test for response cookies
        Cookie responseCookie = response.getCookie();
        assertEquals("cookie_name", responseCookie.getName());
        assertEquals("cookie_value", responseCookie.getValue());
        // test for response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.get("Content-Type").get(0));
    }

}
