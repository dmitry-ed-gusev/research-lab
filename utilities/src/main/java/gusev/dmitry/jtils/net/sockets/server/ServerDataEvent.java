package gusev.dmitry.jtils.net.sockets.server;

import java.nio.channels.SocketChannel;

/**
 * Class for encapsulating received data and sending back answer.
 * Class is immutable.
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.09.13)
*/

public final class ServerDataEvent {

    private NioSocketServer server; // socket server instance
    private SocketChannel   socket; // socket channel
    private byte[]          data;   // data from processing

    /***/
    public ServerDataEvent(NioSocketServer server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data   = data;
    }

    /***/
    public byte[] getData() {
        byte[] dataCopy;
        if (this.data != null && this.data.length > 0) { // internal data isn't empty
            // copy internal data
            dataCopy = new byte[this.data.length];
            System.arraycopy(data, 0, dataCopy, 0, this.data.length);
        } else { // internal data is empty - we return empty array
            dataCopy = new byte[0];
        }
        // return a copy
        return dataCopy;
    }

    /***/
    public void sendAnswer(byte[] answer) {
        this.server.send(this.socket, answer);
    }

}