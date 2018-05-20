package gusevdm.rest;

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
    private final JSONObject body;
    private final Cookie     cookie;
    private final MultivaluedMap<String, String> headers;

    /** REST response object constructor. */
    public RestResponse(int status, JSONObject body, Cookie cookie, MultivaluedMap<String, String> headers) {
        LOGGER.debug("RestResponse constructor() is working.");
        this.status = status;
        this.body   = body;
        this.cookie = cookie;
        // todo: copy map - breaks immutability!
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public JSONObject getBody() {
        return body;
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
                ", body="    + this.body +
                ", cookies=" + this.cookie +
                ", headers=" + this.headers +
                '}';
    }

}
