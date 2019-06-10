package gusev.dmitry.jtils.rest;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.NonNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/** Unit tests for RestClient. */
public class RestClientTest {

    // REST test server parameters
    private static final int    REST_SERVER_PORT = 3000;
    private static final String REST_SERVER_HOST = "localhost";
    private static final String REST_SERVER_FULL_HTTP_HOST =
            String.format("http://%s:%s", REST_SERVER_HOST, REST_SERVER_PORT);
    private static final String REST_SERVER_FULL_HTTPS_HOST =
            String.format("https://%s:%s", REST_SERVER_HOST, REST_SERVER_PORT);

    // HTTP request parameters
    private static final Cookie                         DEFAULT_COOKIE =
            new Cookie("cookie_name", "cookie_value");
    private static final MultivaluedMap<String, String> DEFAULT_HEADER =
            new MultivaluedMapImpl() {{
                putSingle("Content-Type", "application/json");
            }};

    // HTTP mock server parameters
    private static final Header                      DEFAULT_MOCK_HEADER =
            new Header("Content-Type", "application/json");
    private static final org.mockserver.model.Cookie DEFAULT_MOCK_COOKIE =
            new org.mockserver.model.Cookie("cookie_name", "cookie_value");

    // HTTP server JSON answers
    private static final String JSON_SUCCESS_ANSWER_STR = "{\"success\":\"true\"}";

    /** HTTP REST client implementation for tests. */
    static class HttpRestClientImpl extends RestClient {
        @Override
        protected String getPath() {
            return REST_SERVER_FULL_HTTP_HOST;
        }
    }

    /** HTTP REST client implementation for tests. */
    static class HttpsRestClientImpl extends RestClient {

        HttpsRestClientImpl(@NonNull String trustedHost) throws NoSuchAlgorithmException, KeyManagementException {
            super(trustedHost);
        }

        @Override
        protected String getPath() {
            return REST_SERVER_FULL_HTTPS_HOST;
        }
    }

    // internal rest clients - for tests
    private static HttpRestClientImpl  httpRestClient;
    private static HttpsRestClientImpl httpsRestClient;
    // mock rest server - for tests
    private static ClientAndServer     mockRestServer;

    @BeforeClass
    public static void startInitAndMockServers() throws KeyManagementException, NoSuchAlgorithmException {

        // init rest clients
        httpRestClient  = new HttpRestClientImpl();                  // http rest client instance
        httpsRestClient = new HttpsRestClientImpl(REST_SERVER_HOST); // https rest client instance

        // start mock server
        mockRestServer = startClientAndServer(REST_SERVER_PORT);
        // build mock server client expectations
        createRestServerClientExpectations();
    }

    @AfterClass
    public static void stopMockServers() {
        mockRestServer.stop();
    }

    private static void createRestServerClientExpectations() {

        // #1. Test GET request (2 times - for http and https requests)
        new MockServerClient(REST_SERVER_HOST, REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("GET")
                                .withHeader(DEFAULT_MOCK_HEADER)
                                .withCookie(DEFAULT_MOCK_COOKIE)
                                .withPath("/path/get"),
                        exactly(2)) // <- 2 times - http/https
                .respond(response() // <- response
                                .withStatusCode(200)
                                .withHeaders(DEFAULT_MOCK_HEADER)
                                .withCookie(DEFAULT_MOCK_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withDelay(new Delay(SECONDS, 1)));

        // #2. Test POST request
        new MockServerClient(REST_SERVER_HOST, REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("POST")
                                .withHeader(DEFAULT_MOCK_HEADER)
                                .withCookie(DEFAULT_MOCK_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/post"),
                        exactly(1))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_MOCK_HEADER)
                        .withCookie(DEFAULT_MOCK_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

        // #3. Test PUT request
        new MockServerClient(REST_SERVER_HOST, REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("PUT")
                                .withHeader(DEFAULT_MOCK_HEADER)
                                .withCookie(DEFAULT_MOCK_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/put"),
                        exactly(1))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_MOCK_HEADER)
                        .withCookie(DEFAULT_MOCK_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

        // #4. Test DELETE request
        new MockServerClient(REST_SERVER_HOST, REST_SERVER_PORT)
                .when(request()  // <- request
                                .withMethod("DELETE")
                                .withHeader(DEFAULT_MOCK_HEADER)
                                .withCookie(DEFAULT_MOCK_COOKIE)
                                .withBody(JSON_SUCCESS_ANSWER_STR)
                                .withPath("/path/delete"),
                        exactly(1))
                .respond(response() // <- response
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_MOCK_HEADER)
                        .withCookie(DEFAULT_MOCK_COOKIE)
                        .withBody(JSON_SUCCESS_ANSWER_STR)
                        .withDelay(new Delay(SECONDS, 1)));

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
    public void testGetPath() {
        assertEquals("Should be equals!", httpRestClient.getPath(), REST_SERVER_FULL_HTTP_HOST);
    }

    @Test
    public void testSuccessHttpGetRestRequest() {

        // execute GET request
        RestResponse response = httpRestClient.executeGet("/path/get", DEFAULT_COOKIE, DEFAULT_HEADER);

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

        // execute GET request
        RestResponse response = httpsRestClient.executeGet("/path/get", DEFAULT_COOKIE, DEFAULT_HEADER);

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

        // execute POST request
        RestResponse response = httpRestClient.executePost("/path/post", JSON_SUCCESS_ANSWER_STR,
                MediaType.APPLICATION_JSON_TYPE, DEFAULT_COOKIE, DEFAULT_HEADER);

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

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute PUT request
        RestResponse response = httpRestClient.executePut("/path/put", jsonObject,
                DEFAULT_COOKIE, DEFAULT_HEADER);

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

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON_SUCCESS_ANSWER_STR);

        // execute DELETE request
        RestResponse response = httpRestClient.executeDelete("/path/delete", jsonObject,
                DEFAULT_COOKIE, DEFAULT_HEADER);

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
