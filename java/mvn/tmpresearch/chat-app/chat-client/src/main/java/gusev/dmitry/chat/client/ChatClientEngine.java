package gusev.dmitry.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 04.11.12)
 */

public class ChatClientEngine extends KeyAdapter implements ActionListener, ChatClientConstants {

    // this class logger
    private Log log = LogFactory.getLog(ChatClientEngine.class);

    // configuration
    private Properties       config; // todo: local variable?
    private String           serverAddress;
    private int              serverPort;

    // link to chat gui window
    private ChatClientWindow chatWindow;
    // socket for connection
    private Socket           socket             = null;
    // stream for writing messages to chat server
    private PrintStream      outToServerWriter  = null;
    // reader for reading serevr messages (in background)
    private BufferedReader   inFromServerReader = null;
    // are we connected to chat server?
    //private boolean          isConnected        = false;

    /**
     * Constructor.
     * If received Properties object is null or empty, or Properties object contains invalid
     * values for chat server address or port - constructor will throw IllegalStateException.
    */
    public ChatClientEngine(ChatClientWindow chatWindow, Properties config) {
        if (config == null || config.isEmpty()) { // check params existence
            throw new IllegalStateException("System properties are empty! Can't continue.");
        } else { // parameters validation
            this.config        = config;
            this.serverAddress = this.config.getProperty(CHAT_SERVER_ADDRESS, null);
            try {
                this.serverPort = Integer.parseInt(this.config.getProperty(CHAT_SERVER_PORT, "0"));
            } catch (NumberFormatException e) {
                log.error("Can't parse Chat server port [" + this.config.getProperty(CHAT_SERVER_PORT) + "]! " +
                        "Message: " + e.getMessage());
                this.serverPort = 0;
            }
            if (StringUtils.isBlank(this.serverAddress) || this.serverPort <= 0) {
                throw new IllegalStateException("Invalid server address [" + this.config.getProperty(CHAT_SERVER_ADDRESS) + "] " +
                        "or port [" + this.config.getProperty(CHAT_SERVER_PORT) + "]!");
            }
        }
        this.chatWindow = chatWindow;
    }

    /**
     * This method will try connect to server. Afterthis action method will set status for gui window.
     */
    public void connect() {

        log.debug("ChatClientEngine.connect() working.");
        //Socket socket = null;

        // we will connect only if we not already connected
        if (this.socket == null || this.socket.isClosed()) {
            log.debug("We aren't connected now. Connecting.");
            try {
                log.debug("Trying to connect: [" + this.serverAddress + ":" + this.serverPort + "].");
                // connecting
                this.socket = new Socket(this.serverAddress, this.serverPort);
                log.debug("Connected OK.");
                // creating output/input streams
                this.outToServerWriter  = new PrintStream(socket.getOutputStream());
                this.inFromServerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                log.debug("Streams opened.");
                // creating SwingWorker - it will read server messages in background
                ChatServerReader reader = new ChatServerReader(inFromServerReader, this.chatWindow, this);
                reader.execute();
                log.debug("ChatServerReader class instance created.");
                // set status for gui window
                this.chatWindow.setStatus("Connected to [" + this.serverAddress + ":" + this.serverPort + "].");
                // set up isConnected flag
                //this.isConnected = true;
            } catch (UnknownHostException e) {
                this.processConnectionError(e);
            } catch (IOException e) {
                log.error(e.getMessage());
                this.processConnectionError(e);
            }
        } else { // we already connected - warn in log
            log.warn("We already connected! Can't connect again!");
        }
    }

    /**
     * Processing for socket connection error.
    */
    private void processConnectionError(Exception e) {
        // log connection error
        log.error(e);
        // reset connection flag
        //this.isConnected = false;
        // set gui window status message
        this.chatWindow.setStatus("Can't connect to [" + this.serverAddress + ":" + this.serverPort + "].");
        // trying to close socket
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException ex) {
                log.error("Can't close socket!", ex);
            }
        }
    }

    /**
     * Method closes socket connection (if it's opened).
    */
    public void disconnect() {
        log.debug("ChatClientEngine.disconnect() working.");
        //this.isConnected = false;
        if (this.socket != null) {
            try {
                this.socket.close();
                this.chatWindow.setStatus("Disconnected.");
            } catch (IOException e) {
                log.error(e);
                this.chatWindow.setStatus("Can't close connection!");
            }
        }
    }

    // todo: should we use SwingUtilities.invokeLater() for running tasks from this method?
    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        log.debug("Action performed. Action command [" + actionEvent.getActionCommand() + "].");
        // select action type
        if (ACTION_MSG_SEND.equals(actionEvent.getActionCommand())) { // sending message to server
            this.sendMessage();
        } else if (ACTION_CONNECT.equals(actionEvent.getActionCommand())) { // connecting to server
            this.connect();
        } else if (ACTION_DISCONNECT.equals(actionEvent.getActionCommand())) { // disconnecting from server
            this.disconnect();
        }
    }

    // todo: should we use SwingUtilities.invokeLater() for running tasks from this method?
    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // "ENTER" key pressed
        if (keyEvent.getKeyCode() == 10) {
            log.debug("Enter pressed. Code [" + keyEvent.getKeyCode() + "]");
            this.sendMessage();
        }
    }

    /**
     * This method sends message to server and puts it to gui chat window.
     */
    public void sendMessage() {
        log.debug("ChatClientEngine.sendMessage() working.");
        String message = this.chatWindow.getMessage();
        if (!StringUtils.isBlank(message)) {
            log.debug("Message [" + message + "] ok.");
            // we can't send message if we aren't connected to server
            if (this.socket != null && this.socket.isConnected()) {
                log.debug("Connection OK. Trying to send message.");
                // put message into gui chat window
                this.chatWindow.putMessageOnScreen(message);
                // put message into socket output stream
                this.outToServerWriter.println(message);
            } else { //
                log.warn("Can't send message because we aren't connected!");
            }
        } else {
            log.debug("Can't send empty message!");
        }
    }

}
