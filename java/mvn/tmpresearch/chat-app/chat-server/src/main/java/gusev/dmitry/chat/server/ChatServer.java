package gusev.dmitry.chat.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Chat server main module - it accepts clients connections and starts clients processing threads.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 02.11.12)
 */

// todo: we need maven build for whole app: compile, create jar, copy resources (log4j.xml, *.properties)

public class ChatServer implements ChatServerConstants {

    public static void main(String[] args) {

        Log log = LogFactory.getLog(ChatServer.class);
        log.info("Starting chat server.");

        ServerSocket serverSocket = null;
        Socket       client       = null;

        //BufferedReader inbound    = null;
        //OutputStream outbound = null;

        // messages storage
        ChatKernel msgStorage = new ChatKernel();
        int clientId = 0;
        try {
            // Create a server socket
            serverSocket = new ServerSocket(SERVER_PORT);
            log.info("Sockect created. Port [" + SERVER_PORT + "]. Waiting for a client connection...");

            boolean isServerShutDown = false;
            // main server cycle
            while (!isServerShutDown) {
                log.debug("Waiting for client connection. Max clients [" + MAX_CLIENTS + "]. " +
                        "Current clients [" + msgStorage.getClientsCount() + "]");
                // Wait for a request
                client = serverSocket.accept();
                // get the output stream
                PrintWriter outbound = new PrintWriter(client.getOutputStream(), true);

                // check current clients count
                if (msgStorage.getClientsCount() < MAX_CLIENTS) { // if we haven't reached max clients count

                // creating clients connections thread - one thread for one client's connection
                //while (msgStorage.getClientsCount() < MAX_CLIENTS) {
                //    log.debug("Waiting for client connection. Max clients [" + MAX_CLIENTS + "]. " +
                //            "Current clients [" + msgStorage.getClientsCount() + "]");
                    // Wait for a request
                    //client = serverSocket.accept();
                    clientId++;
                    log.debug("Client connected. Clients count = [" + clientId + "].");
                    // get the input stream
                    BufferedReader inbound = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //PrintWriter outbound = new PrintWriter(client.getOutputStream(), true);
                    // print greeting message
                    outbound.println(GREETING_MESSAGE);
                    // adding client to storage
                    msgStorage.addClient(clientId, outbound);
                    // creating thread for connected client
                    ConnectionThread connectionThread = new ConnectionThread(inbound, clientId, msgStorage);
                    connectionThread.start();
                    log.debug("Client thread started.");
                //}
                } else { // we have reached max clients connections limit
                    log.warn("Clients connections limit [" + MAX_CLIENTS + "] was reached.");
                    outbound.println("Max clients connections limit [" + MAX_CLIENTS + "] has been reached. Try again later.");
                    client.shutdownInput();
                    client.shutdownOutput();
                    client.close();
                    log.debug("Client connction closed.");
                }
                //log.info("Going into waiting cycle. Don't accept any connection.");
                log.info("Main server cycle is finished.");

                // waits while one or more clients disconnect
                /*
                while (msgStorage.getClientsCount() >= MAX_CLIENTS) {
                    try {
                        log.debug("Server waiting thread: wait 5 seconds.");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        log.error("Waiting thread interrupted!", e);
                    }
                }
                */

            } // main server cycle (two cycles inside - accepting connection and waiting)

        } catch (IOException ioe) { // catch IOException
            log.error(ioe);
        } finally { // we should free resources (close socket)
            log.debug("Server shutdown: trying to close server socket.");
            if (serverSocket != null) { // if socket isn't null  - try to close
                log.debug("Server socket isn't null. Try to close.");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error("Can't close server socket!", e);
                }
            } else { // socket is null - we don't need to close socket
                log.debug("Server socket is null. Nothing to close.");
            }
            log.debug("Server shutdown: server socket closed.");
        }
    }

}
