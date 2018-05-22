package gusevdm.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

/**
 * REST response object.
 * Warning! Class isn't immutable -> due to using MultiValuedMap!
 */

// todo: fix immutability!
// todo: move to utilities module
public class RestResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponse.class);

    private final int        status;
    private final JSONObject bodyObject;
    private final JSONArray  bodyArray;
    private final Cookie     cookie;
    private final MultivaluedMap<String, String> headers;

    /** REST response object constructor with JSON object. */
    public RestResponse(int status, JSONObject bodyObject, Cookie cookie, MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestResponse constructor(JSONObject) is working.");
        this.status     = status;
        this.bodyObject = bodyObject;
        this.bodyArray  = null;
        this.cookie     = cookie;
        this.headers    = headers; // todo: copy map - breaks immutability!
    }

    /** REST response object constructor with JSON array. */
    public RestResponse(int status, JSONArray bodyArray, Cookie cookie, MultivaluedMap<String, String> headers) {
        this.status     = status;
        this.bodyObject = null;
        this.bodyArray  = bodyArray; // todo: check immutability
        this.cookie     = cookie;
        this.headers    = headers; // todo: copy map - breaks immutability!
    }

    public int getStatus() {
        return status;
    }

    public JSONObject getBodyObject() {
        return bodyObject;
    }

    public JSONArray getBodyArray() {
        return bodyArray;
    }

    public Cookie getCookie() {
        return cookie;
    }

    // todo: copy map - breaks immutability!
    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "RestResponse {" +
                "status="    + this.status +
                ", bodyObject="    + this.bodyObject +
                ", bodyArray=" + this.bodyArray +
                ", cookies=" + this.cookie +
                ", headers=" + this.headers +
                '}';
    }

}
