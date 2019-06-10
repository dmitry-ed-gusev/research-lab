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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public abstract class RestClient {

    private static final Log LOGGER = LogFactory.getLog(RestClient.class);      // module logger
    private static final String SERVER_RESPONSE_MSG = "Server response: [%s]."; // server response message


    // internal jersey client instance todo: timeout for jersey client???
    private final Client           jerseyClient;
    // internal json parser instance
    private final JSONParser       jsonParser;

    /** Default constructor, no trusted hosts. */
    public RestClient() {

        this.jerseyClient = Client.create();
        this.jsonParser = new JSONParser();
    }

    /** Constructor with trusted hostname. */
    public RestClient(@NonNull String trustedHost) throws NoSuchAlgorithmException, KeyManagementException {

        // create jersey client config
        ClientConfig config = new DefaultClientConfig();

        // set config properties - our own hosts verifier and ssl context
        config.getProperties()
                .put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                        new HTTPSProperties(new SpecifiedHostnameVerifier(trustedHost),
                                SSLContextUtil.getInsecureSSLContext()));

        // init jersey client and json parser
        this.jerseyClient = Client.create(config);
        this.jsonParser = new JSONParser();
    }

    /**
     * Should be implemented be each subclass.
     * @return path to recourse for each implementation
     */
    protected abstract String getPath();

    /***/
    public RestResponse executeGet(String resource) {
        LOGGER.debug("RestClient.executeGet(String) is working.");
        return this.executeGet(resource, null, null);
    }

    /***/
    public RestResponse executeGet(String resource, Cookie cookie) {
        LOGGER.debug("RestClient.executeGet(String, Cookie) is working.");
        return this.executeGet(resource, cookie, null);
    }

    /***/
    public RestResponse executeGet(String resource, Cookie cookie, MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.executeGet(String, Cookie) is working.");
        LOGGER.debug(String.format("GET request parameters:%n\tresource [%s],%n\tcookie [%s].", resource, cookie));

        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .get(ClientResponse.class);
        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(null, response);
    }

    public RestResponse executePost(JSONObject entity) {
        LOGGER.debug("RestClient.executePost(JSONObject) is working.");
        return executePost(null, entity);
    }

    public RestResponse executePost(String resource, JSONObject entity) {
        LOGGER.debug("RestClient.executePost(String, JSONObject) is working.");
        return executePost(resource, entity, null, null);
    }

    /** Execute POST request with JSON entity. */
    public RestResponse executePost(String resource, JSONObject entity, Cookie cookie,
                                     MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.executePost(String, JSONObject, Cookie, Headers) is working.");
        LOGGER.debug(
                String.format("POST request parameters:%n\tresource [%s],%n\tentity [%s]," +
                                "%n\tcookie [%s],%n\theaders [%s]",
                        resource, entity, cookie, headers));

        String entityString = entity.toJSONString();

        // executing POST request with JSON entity
        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Execute simple configurable POST request. */
    public RestResponse executePost(String resource, String entity, MediaType mediaType, Cookie cookie,
                                   MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.executeSimplePost(String, String, MediaType, Cookie, Headers) is working.");
        LOGGER.debug(
                String.format("POST request parameters:%n\tresource [%s],%n\tentity [%s]," +
                                "%n\tmedia type [%s],%n\tcookie [%s],%n\theaders [%s]",
                        resource, entity, mediaType, cookie, headers));

        // execute POST request with string entity
        ClientResponse response = this.buildClient(resource, mediaType, cookie, headers)
                .entity(entity, mediaType)
                .post(ClientResponse.class);

        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(null, response);
    }

    public RestResponse executePut(String resource, JSONObject entity) {
        LOGGER.debug("RestClient.executePut(String, JSONObject) is working.");
        return executePut(resource, entity, null, null);
    }

    public RestResponse executePut(String resource, JSONObject entity, Cookie cookie,
                            MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.executePut(String, JSONObject, Cookie) is working.");
        LOGGER.debug(
                String.format("PUT request parameters:%n\tresource [%s],%n\tentity [%s],%n\tcookie [%s].",
                        resource, entity, cookie));

        String entityString = entity.toJSONString();
        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie, headers)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class);

        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Execute DELETE HTTP method. */
    public RestResponse executeDelete(String resource, JSONObject entity, Cookie cookie,
                               MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.executeDelete(String, JSONObject, Cookie) is working.");
        LOGGER.debug(
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
        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Build jersey client. */
    WebResource.Builder buildClient(String resource, MediaType mediaType, Cookie cookie,
                                              MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestClient.buildClient() is working.");

        // build full path
        String pathWithResource = this.getPath();
        if (resource != null) {
            pathWithResource = pathWithResource + resource;
        }

        LOGGER.debug(String.format("Building client. Path: [%s], media type: [%s], cookie: [%s].",
                pathWithResource, mediaType, cookie));

        // build jersey client
        WebResource.Builder builder = this.jerseyClient.resource(pathWithResource).accept(mediaType).cookie(cookie);

        // add headers
        if (headers != null && !headers.isEmpty()) {  // process all headers from Map
            headers.forEach((key, values) -> {  // BiConsumer
                values.forEach(value -> {  // Consumer
                    LOGGER.debug(String.format("Adding header: %s = %s", key, value));
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
    protected RestResponse buildResponse(JSONObject request, ClientResponse response) {
        LOGGER.debug("RestClient.buildResponse() is working.");

        // get response entity (body) and status code
        String entity = response.getEntity(String.class);
        int status = response.getStatus();
        LOGGER.info(String.format("Response status: [%s].", status));
        LOGGER.debug(String.format("Response body:%n%s", entity));

        // server returned error code
        if (response.getClientResponseStatus().getFamily() != SUCCESSFUL) {
            throw new RestException(request, response, "Not OK code returned!");
        }

        // get response cookies
        Cookie cookie = response.getCookies().stream()
                .findAny()
                .orElse(null);
        LOGGER.debug(String.format("Response cookie:%n[%s]%n", cookie));

        // get response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        LOGGER.debug(String.format("Response headers:%n[%s]%n", headers));

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

}
