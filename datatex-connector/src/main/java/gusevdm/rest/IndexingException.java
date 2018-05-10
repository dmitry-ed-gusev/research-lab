package gusevdm.rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;

class IndexingException extends RuntimeException {
    IndexingException(JSONObject request, ClientResponse response, JSONObject responseBody) {
        super(prepareMessage(request, response, responseBody));
    }

    private static String prepareMessage(JSONObject request, ClientResponse response, JSONObject responseBody) {
        StringBuilder message = new StringBuilder("Failed status is returned\nReason: ")
                .append(response.getStatus()).append(" ").append(response.getClientResponseStatus().getReasonPhrase());
        if (request != null) {
            message.append("\nRequest body: ").append(request.toString());
        }
        message.append("\nError message: ").append(responseBody.get("error_msg"))
                .append("\nCollection ID: ").append(responseBody.get("collection_id"))
                .append("\nDataset Name: ").append(responseBody.get("name"))
                .append("\nDataset ID: ").append(responseBody.get("id"));
        return message.toString();
    }
}
