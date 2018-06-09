package gusevdm.rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Exception caused by REST/HTTP communication errors with remote server. */

public class RestException extends RuntimeException {

    public RestException(String message) {
        super(message);
    }

    RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(JSONObject request, ClientResponse response, String message) {
        this(request, response, null, message);
    }

    private RestException(JSONObject request, ClientResponse response, JSONObject responseBody, String message) {
        super(prepareMessage(request, response, responseBody, message));
    }

    private static String prepareMessage(JSONObject request, ClientResponse response, JSONObject responseBody, String message) {
        StringBuilder fullMessage = new StringBuilder(message)
                .append("\nReason: ").append(response.getStatus()).append(response.getClientResponseStatus().getReasonPhrase())
                .append("\nHeaders: ").append(multivalueMapToString(response.getHeaders()));
        if (request != null) {
            fullMessage.append("\nRequest body: ").append(request.toJSONString());
        }
        if (responseBody != null) {
            fullMessage.append("\nResponse body: ").append(responseBody.toJSONString());
        }
        return fullMessage.toString();
    }

    private static String multivalueMapToString(MultivaluedMap<String, String> map){
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            s.append(entry.getKey()).append(" : ").append(Arrays.toString(entry.getValue().toArray())).append("\n");
        }
        return s.toString();
    }

}
