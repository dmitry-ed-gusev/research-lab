package gusevdm.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.Reader;

//import static gusevdm.CSV2AbstractDefaults.STATE_FAILED;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

/**
 * Basic HTTP REST Client.
 * Client assumes, that data exchange with server is done via JSON data.
 */

// https://stackoverflow.com/questions/32042944/upgrade-from-jersey-client-1-9-to-jersey-client-2-8

public abstract class RestClient {

    // module logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);
    // internal jersey client instance
    private final Client     jerseyClient = Client.create();
    // internal json parser instance
    private final JSONParser jsonParser = new JSONParser();

    // response message
    private static final String SERVER_RESPONSE_MSG = "Server response: [%s].";

    //private static final String RESPONSE_COOKIE_KEY = "session";
    //private static final String JSON_FIELD_STATE    = "state";

    /**
     * Should be implemented be each subclass.
     * @return path to recourse for each implementation
     */
    protected abstract String getPath();

    RestResponse executeGet(String resource) {
        LOGGER.debug("RestClient.executeGet(String) is working.");
        return this.executeGet(resource, null);
    }

    RestResponse executeGet(String resource, Cookie cookie) {
        LOGGER.debug("RestClient.executeGet(String, Cookie) is working.");
        LOGGER.debug(String.format("GET request parameters:%n\tresource [%s],%n\tcookie [%s].", resource, cookie));

        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie).get(ClientResponse.class);
        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(null, response);
    }

    RestResponse executePost(JSONObject entity) {
        LOGGER.debug("RestClient.executePost(JSONObject) is working.");
        return executePost(null, entity);
    }

    RestResponse executePost(String resource, JSONObject entity) {
        LOGGER.debug("RestClient.executePost(String, JSONObject) is working.");
        return executePost(resource, entity, null);
    }

    private RestResponse executePost(String resource, JSONObject entity, Cookie cookie) {
        LOGGER.debug("RestClient.executePost(String, JSONObject, Cookie) is working.");
        LOGGER.debug(
                String.format("POST request parameters:%n\tresource [%s],%n\tentity [%s],%n\tcookie [%s].",
                        resource, entity, cookie));

        String entityString = entity.toJSONString();

        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Excute simple configurable POST request. */
    /*RestResponse*/ void executeSimplePost(String resource, MediaType mediaType, Cookie cookie) {
        LOGGER.debug("RestCLient.executeSimplePost() is working.");

        String pathWithResource = this.getPath();
        if (resource != null) {
            pathWithResource = pathWithResource + resource;
        }

        LOGGER.debug(String.format("Building client. Path: [%s], media type: [%s], cookie: [%s].",
                pathWithResource, mediaType, cookie));

        ClientResponse response = this.jerseyClient.resource(pathWithResource)
                .accept("application/x-www-form-urlencoded")
                .cookie(cookie)
                .entity("username=admin&password=admin")
                //.entity("password=admin")
                .post(ClientResponse.class);

        //return this.buildResponse()

        RestResponse restResponse = this.buildResponse(null, response);
        System.out.println("===> " + restResponse.toString());
        //System.out.println("RESPONSE -> " + response);
    }

    protected RestResponse executePut(String resource, JSONObject entity) {
        LOGGER.debug("RestClient.executePut(String, JSONObject) is working.");
        return executePut(resource, entity, null);
    }

    RestResponse executePut(String resource, JSONObject entity, Cookie cookie) {
        LOGGER.debug("RestClient.executePut(String, JSONObject, Cookie) is working.");
        LOGGER.debug(
                String.format("PUT request parameters:%n\tresource [%s],%n\tentity [%s],%n\tcookie [%s].",
                        resource, entity, cookie));

        String entityString = entity.toJSONString();
        ClientResponse response = this.buildClient(resource, MediaType.APPLICATION_JSON_TYPE, cookie)
                .entity(entityString, MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class);

        LOGGER.info(String.format(SERVER_RESPONSE_MSG, response));
        return this.buildResponse(entity, response);
    }

    /** Build jersey client. */
    // todo: adding request header (see RiverRestClient).
    protected WebResource.Builder buildClient(String resource, MediaType mediaType, Cookie cookie) {
        LOGGER.debug("RestClient.buildClient() is working.");

        String pathWithResource = this.getPath();
        if (resource != null) {
            pathWithResource = pathWithResource + resource;
        }

        LOGGER.debug(String.format("Building client. Path: [%s], media type: [%s], cookie: [%s].",
                pathWithResource, mediaType, cookie));

        return this.jerseyClient.resource(pathWithResource).accept(mediaType).cookie(cookie);
    }

    /** Build and return REST response class. */
    protected RestResponse buildResponse(JSONObject request, ClientResponse response) {
        LOGGER.debug("RestClient.buildResponse() is working.");

        // get response entity (body) and status code
        String entity = response.getEntity(String.class);
        int status = response.getStatus();
        LOGGER.info(String.format("Response status: [%s].", status));
        LOGGER.debug(String.format("Response body:%n%s", entity));

        if (response.getClientResponseStatus().getFamily() != SUCCESSFUL) {
            throw new RestException(request, response, "Not OK code returned!");
        }

        // parse response body into JSON object
        JSONObject body;
        try {
            body = (JSONObject) this.jsonParser.parse(entity);
        } catch (ParseException e) {
            throw new RestException(request, response, "Cannot parse response body! " + e.toString());
        }

        //if (body != null && STATE_FAILED.equals(body.get(JSON_FIELD_STATE))) {
        //    throw new IndexingException(request, response, body);
        //}

        // get response cookies
        Cookie cookie = response.getCookies().stream()
                // todo: implement filtering cookie(s) by key or let caller to do it?
                //.filter(oneCookie -> RESPONSE_COOKIE_KEY.equals(oneCookie.getName()))
                //.filter(oneCookie -> "Set-Cookie:".equals(oneCookie.getName()))
                .findAny()
                .orElse(null);
        LOGGER.debug(String.format("Response cookie:%n[%s]%n", cookie));

        // get response headers
        MultivaluedMap<String, String> headers = response.getHeaders();
        LOGGER.debug(String.format("Response headers:%n[%s]%n", headers));

        // create RestResponse instance and return it
        return new RestResponse(status, body, cookie, headers);
    }

    /***/
    JSONArray parseJsonArray(Reader reader) throws IOException, ParseException {
        LOGGER.debug("RestClient.parseJsonArray() is working.");
        return (JSONArray) this.jsonParser.parse(reader);
    }

}
