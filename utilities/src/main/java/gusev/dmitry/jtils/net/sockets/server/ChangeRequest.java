package gusev.dmitry.jtils.net.sockets.server;

import java.nio.channels.SocketChannel;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.09.13)
*/

public class ChangeRequest {

    public static final int REGISTER = 1;
    public static final int CHANGEOPS = 2;

    public SocketChannel socket;
    public int           type;
    public int           ops;

    /***/
    public ChangeRequest(SocketChannel socket, int type, int ops) {
        this.socket = socket;
        this.type = type;
        this.ops = ops;
    }

}