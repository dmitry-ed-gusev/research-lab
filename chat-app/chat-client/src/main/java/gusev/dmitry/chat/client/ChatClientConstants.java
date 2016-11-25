package gusev.dmitry.chat.client;

/**
 * Interface with chat client module constants.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 03.11.12)
 */

public interface ChatClientConstants {

    // application header
    public static final String APP_HEADER          = "Java chat client, 2012 (c).";

    // config file
    public static final String APP_CONFIG_FILE     = "chatClient.properties";
    public static final String LOG4J_CONFIG_FILE   = "log4j.xml";
    // server parameters
    public static final String CHAT_SERVER_ADDRESS = "server.address";
    public static final String CHAT_SERVER_PORT    = "server.port";

    // messages area size
    public static final int MESSAGES_AREA_ROWS     = 10;
    public static final int MESSAGES_AREA_COLUMNS  = 35;

    // actions for buttons
    // todo: enum for actions
    public static final String ACTION_MSG_SEND     = "send_msg";
    public static final String ACTION_CONNECT      = "connect";
    public static final String ACTION_DISCONNECT   = "disconnect";

    // toolbar buttons icons
    public static final String ICON_CONNECTED      = "icons/connect16x16.png";
    public static final String ICON_DISCONNECTED   = "icons/disconnect16x16.png";

}