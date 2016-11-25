package gusev.dmitry.jtils.net.sockets.server;

import java.nio.channels.SocketChannel;

/**
 * NIO socvet server worker interface. Worker used for processing received data and send aswer back.
 * @author Gusev Dmitry (gusevd)
 * @version 2.0 (DATE: 14.10.13)
*/

public interface SocketServerWorker extends Runnable {

    /**
     * Method for data processing. It uses link to socket server for send back some data (message).
     * @param server NioSocketServer reference to socket server - it used for send answer on received data
     * @param socketChannel SocketChannel reference to socket channel - it used for send answer on received data
     * @param data byte[] received data - array of bytes, we can use any encoding for it (to make string) or use "as is"
    */
    public void processData(NioSocketServer server, SocketChannel socketChannel, byte[] data);

}