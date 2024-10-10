package dgusev.net.sockets;


import dgusev.net.sockets.server.NioSocketServer;
import dgusev.net.sockets.server.SocketServerWorker;

import java.nio.channels.SocketChannel;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 13.10.2014)
 */
public class ServerWorker implements SocketServerWorker {

    @Override
    public void processData(NioSocketServer server, SocketChannel socketChannel, byte[] data) {
        System.out.println("ServerWorker processData().");
    }

    @Override
    public void run() {
        System.out.println("ServerWorker run().");
    }

}
