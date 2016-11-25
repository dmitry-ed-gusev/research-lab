package gusev.dmitry.jtils.net.sockets;

import gusev.dmitry.jtils.net.sockets.client.TCServiceClient;
import gusev.dmitry.jtils.net.sockets.server.NioSocketServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 13.10.2014)
*/

public class NioSocketsMain {

    public static void main(String[] args) throws IOException {
        Log log = LogFactory.getLog(NioSocketsMain.class);
        log.info("NioSocketsMain starting...");

        String host = "172.18.20.151";
        int    port = 8080;

        // server
        NioSocketServer server = new NioSocketServer(host + ":" + port, new ServerWorker());
        log.info("Server created...");
        //new Thread(server).start();
        server.serverStart();
        log.info("Server started...");

        // client
        //TCServiceClient client = new TCServiceClient(/*host*/ "127.0.0.1", port);
        //client.sendData();
        //log.info("Data was sent...");
    }

}