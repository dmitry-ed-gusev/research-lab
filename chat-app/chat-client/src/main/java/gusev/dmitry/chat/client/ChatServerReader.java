package gusev.dmitry.chat.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * This class extends SwingWorker class. It reads chat server output in background and puts
 * received data to chat gui window.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 04.11.12)
 */

public class ChatServerReader extends SwingWorker<Void, String> {

    // module logger
    private Log log = LogFactory.getLog(ChatServerReader.class);

    private BufferedReader   reader;
    private ChatClientWindow window;
    private ChatClientEngine engine;

    public ChatServerReader(BufferedReader reader, ChatClientWindow window, ChatClientEngine engine) {
        //log.debug("ChatServerReader() constructor working.");
        this.reader = reader;
        this.window = window;
        this.engine = engine;
    }

    /**
     * Method reads server output in background worker thread.
    */
    @Override
    protected Void doInBackground() throws Exception {
        log.debug("SwingWorker.doInBackground(). Event dispatch thread: " + SwingUtilities.isEventDispatchThread());
        String line;
        boolean isInterrupted = false;
        while(!isInterrupted) {
            // Trying read line from input stream. If we failed (exception) we will assign a null
            // value to line variable (it's a sign that we should interrupt this method).
            try {
                line = reader.readLine();
            } catch (IOException e) {
                log.error(e);
                line = null;
            }
            // checking line - send it to screen or just interrupt our method
            if (line != null) {
                log.debug("received from server -> " + line);
                // Sending data to method, that will run in event dispath thread (method process() - see below).
                // (this call simply calls method process() behind the scene)
                this.publish(line);
            } else {
                log.warn("ChatServerReader.doInBackground() - method interrupted!");
                isInterrupted = true;
                // disconnects from server
                this.engine.disconnect();
            }
        }
        // return null because we should return Void type
        return null;
    }

    @Override
    @SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter", "QuestionableName"})
    protected void process(List<String> strings) {
        //super.process(strings);
        log.debug("SwingWorker.process(). Event dispatch thread: " + SwingUtilities.isEventDispatchThread());
        log.debug("Received process data: " + strings);
        for (String string : strings) {
            this.window.putMessageOnScreen(string);
        }
    }

}