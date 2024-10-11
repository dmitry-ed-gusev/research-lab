package gusev.dmitry.chat.server;

/**
 * Common constants for chat server.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 10.11.12)
 */

public interface ChatServerConstants {

    // greeting message
    public static final String  GREETING_MESSAGE = "You are connected to Hercules chat server!";
    // server port (for clients)
    public static final int     SERVER_PORT      = 3000;
    // max concurrent client
    public static final int     MAX_CLIENTS      = 3;

}