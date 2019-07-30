package dgusev.web.rest;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.util.List;

/**
 * REST response object.
 *
 * Warning! Class isn't immutable -> due to using MultiValuedMap!
 */

// todo: fix immutability! do we need it?

@CommonsLog
@ToString
public class RestResponse {

    @Getter private final int             status;
    @Getter private final JSONObject      bodyObject;
    @Getter private final JSONArray       bodyArray;
    @Getter private final List<NewCookie> cookies;
    @Getter private final MultivaluedMap<String, String> headers; // todo: copy map - breaks immutability!

    /** REST response object constructor with JSON object. */
    public RestResponse(int status, JSONObject bodyObject, List<NewCookie> cookies, MultivaluedMap<String, String> headers) {
        LOG.debug("RestResponse constructor(JSONObject) is working.");
        this.status     = status;
        this.bodyObject = bodyObject;
        this.bodyArray  = null;
        this.cookies    = cookies;
        this.headers    = headers; // todo: copy map - breaks immutability!
    }

    /** REST response object constructor with JSON array. */
    public RestResponse(int status, JSONArray bodyArray, List<NewCookie> cookies, MultivaluedMap<String, String> headers) {
        this.status     = status;
        this.bodyObject = null;
        this.bodyArray  = bodyArray; // todo: check immutability
        this.cookies    = cookies;
        this.headers    = headers; // todo: copy map - breaks immutability!
    }

    /***/
    public Cookie getCookie() { // method for backward compatibility

        if (this.cookies != null) {
            return this.cookies.stream().findAny().orElse(null);
        }

        return null;
    }

}
