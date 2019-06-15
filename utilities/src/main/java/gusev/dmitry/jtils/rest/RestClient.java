package gusev.dmitry.jtils.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import gusev.dmitry.jtils.rest.security.SSLContextUtil;
import gusev.dmitry.jtils.rest.security.SpecifiedHostnameVerifier;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * Basic HTTP/HTTPS REST Client. For HTTPS requests should be specified "trusted" host - it
 * will be accepted without any certificates.
 * Client assumes, that data exchange with server is done using JSON data.
 */

// todo: migrate to latest Jersey Client version
// todo: for Jersey update see: https://stackoverflow.com/questions/32042944/upgrade-from-jersey-client-1-9-to-jersey-client-2-8
// todo: timeout for jersey client???

@CommonsLog
public abstract class RestClient {

    //private static final Log LOGGER = LogFactory.getLog(RestClient.class);      // module logger
    private static final String SERVER_RESPONSE_MSG = "Server response: [%s]."; // server response message
    private static final String HTTP_URL_PREFIX     = "http://";
    private static final String HTTPS_URL_PREFIX    = "https://";

    // internal client state
    //private final String     path;         // server host path
    private final Client     jerseyClient; // internal jersey client instance
    private final JSONParser jsonParser;   // internal json parser instance

    /** Default constructor, no trusted hosts. */
    //public RestClient() {
    //    this.jerseyClient = Client.create();
    //    this.jsonParser = new JSONParser();
    //}

    /** Constructor with trusted hostname. */
    public RestClient(@NonNull String path, boolean trustHost) throws NoSuchAlgorithmException, KeyManagementException {

        LOG.debug("RestClient constructor() is working.");



        // init internal JSON parser instance
        this.jsonParser = new JSONParser();

        if (trustHost) { // if we should trust the host - provide hostname verifier/insecure SSL context
            LOG.debug("");
            // extract host name
            String trustedHost = "";

            // create jersey client config
            ClientConfig config = new DefaultClientConfig();

            // set config properties - our own hosts verifier and ssl context
            config.getProperties()
                    .put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                            new HTTPSProperties(new SpecifiedHostnameVerifier(trustedHost),
                                    SSLContextUtil.getInsecureSSLContext()));

            // init jersey client and json parser
            this.jerseyClient = Client.create(config);
        } else {
            LOG.debug("");
            this.jerseyClient = Client.create();
        }

    }

    /**
     * Should be implemented be each subclass.
     * @return path to recourse for each implementation
     */
    protected abstract String getPath();

    /***/
    public RestResponse executeGet(String resource) {
        LOG.debug("RestClient.executeGet(String) is working.");
        return this.executeGet(resource, null, null);
    }

    /***/
    public RestResponse executeGet(String resource, Cookie cookie) {
        LOG.debug("RestClient.executeGet(String, Cookie) is working.");
        return this.executeGet(resource, cookie, null);
    }

    /***/
    public RestResponse executeGet(String resource, Cookie cookie, MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.executeGet(String, Cookie) is working.");
        LOG.debug(String.format("GET request parameters:%n\tresource [%s],%n\tcookie [%s].", resource, cookie));

        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .get(ClientResponse.class);
        LOG.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(null, response);
    }

    public RestResponse executePost(JSONObject entity) {
        LOG.debug("RestClient.executePost(JSONObject) is working.");
        return executePost(null, entity);
    }

    public RestResponse executePost(String resource, JSONObject entity) {
        LOG.debug("RestClient.executePost(String, JSONObject) is working.");
        return executePost(resource, entity, null, null);
    }

    /** Execute POST request with JSON entity. */
    public RestResponse executePost(String resource, JSONObject entity, Cookie cookie,
                                     MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.executePost(String, JSONObject, Cookie, Headers) is working.");
        LOG.debug(
                String.format("POST request parameters:%n\tresource [%s],%n\tentity [%s]," +
                                "%n\tcookie [%s],%n\theaders [%s]",
                        resource, entity, cookie, headers));

        String entityString = entity.toJSONString();

        // executing POST request with JSON entity
        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        LOG.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Execute simple configurable POST request. */
    public RestResponse executePost(String resource, String entity, MediaType mediaType, Cookie cookie,
                                   MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.executeSimplePost(String, String, MediaType, Cookie, Headers) is working.");
        LOG.debug(
                String.format("POST request parameters:%n\tresource [%s],%n\tentity [%s]," +
                                "%n\tmedia type [%s],%n\tcookie [%s],%n\theaders [%s]",
                        resource, entity, mediaType, cookie, headers));

        // execute POST request with string entity
        ClientResponse response = this.buildClient(resource, mediaType, cookie, headers)
                .entity(entity, mediaType)
                .post(ClientResponse.class);

        LOG.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(null, response);
    }

    public RestResponse executePut(String resource, JSONObject entity) {
        LOG.debug("RestClient.executePut(String, JSONObject) is working.");
        return executePut(resource, entity, null, null);
    }

    public RestResponse executePut(String resource, JSONObject entity, Cookie cookie,
                            MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.executePut(String, JSONObject, Cookie) is working.");
        LOG.debug(
                String.format("PUT request parameters:%n\tresource [%s],%n\tentity [%s],%n\tcookie [%s].",
                        resource, entity, cookie));

        String entityString = entity.toJSONString();
        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class);

        LOG.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Execute DELETE HTTP method. */
    public RestResponse executeDelete(String resource, JSONObject entity, Cookie cookie,
                               MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.executeDelete(String, JSONObject, Cookie) is working.");
        LOG.debug(
                String.format("DELETE request parameters:%n\tresource [%s],%n\tentity [%s],%n\tcookie [%s].",
                        resource, entity, cookie));

        // start client building
        WebResource.Builder builder = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers);
        // add JSON entity, if any
        if (entity != null) {
            String entityString = entity.toJSONString();
            builder.entity(entityString, MediaType.APPLICATION_JSON_TYPE);
        }

        // execute request
        ClientResponse response = builder.delete(ClientResponse.class);
        LOG.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Build jersey client. */
    WebResource.Builder buildClient(String resource, MediaType mediaType, Cookie cookie,
                                              MultivaluedMap<String, String> headers) {
        LOG.debug("RestClient.buildClient() is working.");

        // build full path
        String pathWithResource = this.getPath();
        if (resource != null) {
            pathWithResource = pathWithResource + resource;
        }

        LOG.debug(String.format("Building client. Path: [%s], media type: [%s], cookie: [%s].",
                pathWithResource, mediaType, cookie));

        // build jersey client
        WebResource.Builder builder = this.jerseyClient.resource(pathWithResource).accept(mediaType).cookie(cookie);

        // add headers
        if (headers != null && !headers.isEmpty()) {  // process all headers from Map
            headers.forEach((key, values) -> {  // BiConsumer
                values.forEach(value -> {  // Consumer
                    LOG.debug(String.format("Adding header: %s = %s", key, value));
                    builder.header(key, value);
                });
            });
        }

        return builder;
    }

    /**
     * Build and return REST response class.
     * Response is returned without any processing - "as is".
     */
    // todo: implement unit tests (with mocks)
    private RestResponse buildResponse(JSONObject request, ClientResponse response) {
        LOG.debug("RestClient.buildResponse() is working.");

        // get response entity (body) and status code
        String entity = response.getEntity(String.class);
        int status = response.getStatus();
        LOG.info(String.format("Response status: [%s].", status));
        LOG.debug(String.format("Response body:%n%s", entity));

        // server returned error code
        if (response.getClientResponseStatus().getFamily() != SUCCESSFUL) {
            throw new RestException(request, response, "Not OK code returned!");
        }

        // get response cookies
        Cookie cookie = response.getCookies().stream()
                .findAny()
                .orElse(null);
        LOG.debug(String.format("Response cookie:%n[%s]%n", cookie));

        // get response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        LOG.debug(String.format("Response headers:%n[%s]%n", headers));

        // depending on response body type (JSONObject/JSONArray) return RestResponse instance
        try {
            Object body = this.jsonParser.parse(entity);
            if (body instanceof JSONObject) { // returned JSON object
                return new RestResponse(status, (JSONObject) body, cookie, headers);
            } else if (body instanceof JSONArray) { // returned JSON Array
                return new RestResponse(status, (JSONArray) body, cookie, headers);
            } else { // unknown object returned
                throw new IllegalStateException(String.format("Returned unknown object type [%s]!", body));
            }
        } catch (ParseException e) {
            throw new RestException(request, response, "Cannot parse response body! " + e.toString());
        }

    }

    /***/
    static String processUrl(@NonNull String url) {
        LOG.debug("RestClient.processUrl() is working.");

        if (StringUtils.isBlank(url)) { // fail-fast check for blank server url
            throw new IllegalArgumentException("Provided empty url!");
        }

        String processedUrl = StringUtils.trimToEmpty(url).toLowerCase();

        // fail-fast - server url should start with http:// or https://
        if (!processedUrl.startsWith(HTTP_URL_PREFIX) && !processedUrl.startsWith(HTTPS_URL_PREFIX)) {
            throw new IllegalArgumentException(String.format("Provided url [%s] should start with %s or %s!",
                    url, HTTP_URL_PREFIX, HTTPS_URL_PREFIX));
        }

        return processedUrl;
    }

    /***/
    static String extractHost(@NonNull String url) {
        LOG.debug("RestClient.extractHost() is working.");

        // pre-process url
        String processedUrl = RestClient.processUrl(url);

        // calculate prefix index for url
        int prefixIndex;
        // todo: replace with just .length() function for constants?
        if (processedUrl.startsWith(HTTP_URL_PREFIX)) {
            prefixIndex = processedUrl.indexOf(HTTP_URL_PREFIX) + HTTP_URL_PREFIX.length();
        } else {
            prefixIndex = processedUrl.indexOf(HTTPS_URL_PREFIX) + HTTPS_URL_PREFIX.length();
        }
        LOG.debug(String.format("For url [%s] prefix index [%s].", processedUrl, prefixIndex));

        // calculate postfix index for url
        int postfixIndex;
        if (processedUrl.substring(prefixIndex).contains(":")) {        // contains (colon) : -> postfix starts with :
            postfixIndex = processedUrl.indexOf(":", prefixIndex);
        } else if (processedUrl.substring(prefixIndex).contains("/")) { // contains (slash) / -> postfix starts with /
            postfixIndex = processedUrl.indexOf("/", prefixIndex);
        } else if (processedUrl.substring(prefixIndex).contains("?")) { // contains ? -> postfix starts with ?
            postfixIndex = processedUrl.indexOf("?", prefixIndex);
        } else { // doesn't contain both - / and ? -> no postfix, hostname lasts till end of url string
            postfixIndex = processedUrl.length();
        }
        LOG.debug(String.format("For url [%s] postfix index [%s].", processedUrl, postfixIndex));

        // extract hostname from url
        String result = processedUrl.substring(prefixIndex, postfixIndex);

        if (StringUtils.isBlank(result)) { // extracted url is empty -> invalid state
            throw new IllegalStateException("Extracted empty url!");
        }

        LOG.debug(String.format("For url [%s] extracted host [%s].", processedUrl, result));

        return result;

    }

}
