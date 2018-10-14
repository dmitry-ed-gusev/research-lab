package gusev.dmitry.chat.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Thread that will process one client connection.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 08.11.12)
 */

public class ConnectionThread extends Thread {

    private Log log = LogFactory.getLog(ConnectionThread.class); // thread logger

    private BufferedReader  clientReader; // client's input reader
    private int             threadId;     // id for this thread/client
    private ChatKernel      kernel;       // chat server kernel

    public ConnectionThread(BufferedReader clientReader, int threadId, ChatKernel kernel) {
        log.debug("Creating thread for processing client [" + threadId + "].");
        this.clientReader = clientReader;
        this.threadId     = threadId;
        this.kernel       = kernel;
    }

    public void run() {
        log.debug("Processing thread for client [" + threadId + "] started.");
        try { // interacting with client
            boolean done = false;
            while (!done) {
                String line = clientReader.readLine();
                if (line == null) { // empty lione - connection finished
                    done = true;
                } else {            // not empty line - send to kernel
                    kernel.addMessage(threadId, line);
                }
            }
        } catch (IOException e) { // exception case
            log.error("Client [" + threadId + "] error!", e);
        } finally { // we should close client's stream
            log.debug("Removing client from kernel.");
            kernel.removeClient(threadId);
            log.debug("Trying to close client [" + threadId + "] stream.");
            try {
                clientReader.close();
            } catch (IOException e) {
                log.error("Can't close client's input stream!", e);
            }
            log.debug("Stream for client [" + threadId + "] closed OK.");
        }
    }

}