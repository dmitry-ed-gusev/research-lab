package gusev.dmitry.chat.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Main class of chat client module.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 03.11.12)
 */

// todo: we need maven build for whole app: compile, create jar, copy resources (log4j.xml, *.properties)

public class ChatClient implements ChatClientConstants {

    public static void main(String[] args) {

        // module logger
        Log log = LogFactory.getLog(ChatClient.class);
        // Using for log4j xml configuration (external file, if file in classpath -> this line not needed)
        DOMConfigurator.configure(LOG4J_CONFIG_FILE);
        log.info("ChatClient started. Loading properties from file [" + APP_CONFIG_FILE + "].");

        // loading config from file
        FileReader reader = null;
        final Properties config = new Properties();
        // Trying to load properties and start app. If we can't load properties from
        // config file we will shut down application
        try {
            reader = new FileReader(APP_CONFIG_FILE);
            config.load(reader);
            log.debug("Properties were loaded successfully.");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // chat client window creating
                    ChatClientWindow chatWindow = new ChatClientWindow(config);
                    // show main window
                    chatWindow.showChatClientWindow();
                }
            });
        } catch (FileNotFoundException e) {
            log.error(e); // log exception stack trace
        } catch (IOException e) {
            log.error(e); // log exception stack trace
        } finally { // we should free resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

        // main app thread finished
        log.info("ChatClient.Main thread finished.");
    }

}