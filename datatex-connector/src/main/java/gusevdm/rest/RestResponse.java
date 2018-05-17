package gusevdm.rest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;

/** REST response object.  */

public class RestResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponse.class);

    private final int status;
    private final JSONObject body;
    private final Cookie cookie;

    public RestResponse(int status, JSONObject body, Cookie cookie) {
        LOGGER.debug("RestResponse constructor() is working.");
        this.status = status;
        this.body   = body;
        this.cookie = cookie;
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

    @Override
    public String toString() {
        return "RestResponse{" +
                "status=" + status +
                ", body=" + body +
                ", cookie(s)=" + cookie +
                '}';
    }

}
