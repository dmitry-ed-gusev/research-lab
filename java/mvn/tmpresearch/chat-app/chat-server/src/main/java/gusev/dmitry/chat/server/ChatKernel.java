package gusev.dmitry.chat.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Chat server kernel. It keeps clients list and resends messages from each client to other
 * connected clients.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 08.11.12)
 */

public class ChatKernel {

    private Log log = LogFactory.getLog(ChatKernel.class);

    // we're using synchronized collection implementation
    //private Map<Integer, ArrayList<String>> clientsMessages = new Hashtable<Integer, ArrayList<String>>();
    // todo: we can use not synchronized HashMap because all methods are synchronized
    //private Map<Integer, PrintWriter>       clientsWriters  = new Hashtable<Integer, PrintWriter>();
    private Map<Integer, PrintWriter>       clientsWriters  = new HashMap<Integer, PrintWriter>();

    /***/
    public synchronized void addClient(int clientId, PrintWriter clientWriter) {
        log.debug("MessagesStorage.addClient() working.");
        // check input parameters
        if (clientId > 0 && clientWriter != null) {
            // check client in list
            if (clientsWriters.get(clientId) == null) { // client doesn't exist in list
                clientsWriters.put(clientId, clientWriter);
                // if client doesn't exist in clientsWriters map we won't check client with this id in
                // messages map - we simply add (or replace) list for this client messages.
                //clientsMessages.put(clientId, new ArrayList<String>());
            } else { // client already exists in list
                log.warn("Client with id = [" + clientId + "]  already exists!");
            }
        } else { // invalid parameters
            log.warn("Can't add client with id = [" + clientId + "]. PrintWriter is null = [" + (clientWriter == null) + "] ");
        }

    }

    /***/
    public synchronized void addMessage(int clientId, String message) {
        log.debug("MessagesStorage.addMessage() working.");
        // check params
        if (clientId > 0 && !StringUtils.isBlank(message)) { // params OK
            if (clientsWriters.get(clientId) != null) {

                //ArrayList<String> clientMessages = clientsMessages.get(clientId);
                // initialize if necessary
                //if (clientMessages == null || clientMessages.isEmpty()) {
                //    clientMessages = new ArrayList<String>();
                //}
                // add message to this client's messages list
                //clientMessages.add(message);
                //log.debug("Message [" + message + "] for client [" + clientId + "] was added.");

                // send message to all other clients
                for (Map.Entry<Integer, PrintWriter> clientWriter : clientsWriters.entrySet()) {
                    if (clientWriter.getKey() != clientId && clientWriter.getValue() != null) {
                        clientWriter.getValue().println(message);
                    }
                }
                log.debug("Message [" + message + "] was sent to all other clients.");
            } else { // no such client (with such id)
                log.warn("There is not anyone client with id = [" + clientId + "]!");
            }
        } else { // invaluid params
            log.warn("Can't add message with client id = [w]! Message [].");
        }
    }

    /***/
    public synchronized void removeClient(int clientId) {
        log.debug("MessagesStorage.removeClient() working. Removing client [" + clientId + "]. Clients count = [" +
                clientsWriters.size() + "]");
        PrintWriter clientWriter = clientsWriters.get(clientId);
        if (clientId > 0 &&  clientWriter != null) {
            clientWriter.close();
            clientsWriters.remove(clientId);
            log.debug("Client [" + clientId + "] removed. Current clients count = [" + clientsWriters.size() + "].");
        } else { // invalid clientId or no such client in map
            log.error("Invalid client id = [" + clientId + "] or no such client!");
        }
    }

    /***/
    public synchronized int getClientsCount() {
        return clientsWriters.size();
    }

}