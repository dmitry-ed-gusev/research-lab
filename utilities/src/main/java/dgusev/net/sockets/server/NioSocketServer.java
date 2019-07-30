package dgusev.net.sockets.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * NIO socket server for interacting with IO servers -> receiving telegrams and answers to IO servers.
 * Host is set to NULL - this means localhost.
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.09.13)
 */

// todo: check and remove all warnings!

public final class NioSocketServer implements Runnable {

    private Log log              = LogFactory.getLog(NioSocketServer.class);

    private static final int    SERVER_BUFFER_SIZE_BYTES = 4096;
    private static final String LOG_DATA_ENCODING        = "UTF-8"; //encoding for log message with received data

    // this fields should be set up externally (by injection/constructor)
    private String              host;              // host to listen on (host=null means localhost - ?), format [host:port]
    private SocketServerWorker  worker;            // worker impl that will proceed all incoming data and returns answers
    // module internal state
    private ServerSocketChannel serverChannel;                     // socket channel on which we'll accept connections
    private Selector            selector;                          // selector we'll be monitoring
    private final List          changeRequests = new LinkedList(); // A list of ChangeRequest instances
    private Map                 pendingData    = new HashMap();    // Maps a SocketChannel to a list of ByteBuffer instances
    // byte buffer into it we'll read data when it's available (size in bytes!)
    private ByteBuffer          readBuffer     = ByteBuffer.allocate(SERVER_BUFFER_SIZE_BYTES);
    // server state - running/stopped
    private boolean             isStarted      = false;

    /***/
    public NioSocketServer(String host, SocketServerWorker worker) throws IOException {
        log.debug("NioTCSocketServer construtor() working.");

        if (StringUtils.isBlank(host) || !host.contains(":") || worker == null) { // check input parameters
            throw new IOException(String.format("Empty/invalid host [%s] or empty SocketServerWorker reference!", host));
        }

        this.host   = host;    // save given host value
        this.worker = worker;  // save given reference to SocketServerWorker

        // parsing given host (host:port)
        String[] hostValues = host.split(":");
        int port;
        try {
            port = Integer.parseInt(hostValues[1]);
        } catch (NumberFormatException e) {
            throw new IOException(String.format("Invalid port value [%s]! Error message: %s", host, e.getMessage()));
        }

        this.selector = SelectorProvider.provider().openSelector();      // new socket selector
        this.serverChannel = ServerSocketChannel.open();                 // new server socket channel
        this.serverChannel.configureBlocking(false);                     // configure socket channel as non-blocking
        // bind server channel to inet socket address (host:port)
        this.serverChannel.socket().bind(new InetSocketAddress(hostValues[0], port));
        // register server socket channel, indicating an interest in accepting new connections
        this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.changeRequests) {
                    for (Object changeRequest : this.changeRequests) {
                        ChangeRequest change = (ChangeRequest) changeRequest;
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                if (key != null) {
                                    key.interestOps(change.ops);
                                } else {
                                    log.error("NioSocketServer: key (SelectionKey) is NULL!");
                                }
                        }
                    }
                    this.changeRequests.clear();
                }  // end of synchronized block

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();
                    if (key.isValid()) { // if key is valid - process it
                        // Check what event is available and deal with it
                        if (key.isAcceptable()) {
                            this.accept(key);
                        } else if (key.isReadable()) {
                            this.read(key);
                        } else if (key.isWritable()) {
                            this.write(key);
                        }
                    }
                } // end of WHILE

            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    /***/
    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socketService's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested in writing on this socketService.
                // Switch back to waiting for data.
                key.interestOps(SelectionKey.OP_READ);
            }
        } // end of synchronized block

    }

    /***/
    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data. This operation really doesn't clear buffer,
        // just set counter to zero (buffer start) and doesn't remove buffer content
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            log.error(e);
            // The remote forcibly closed the connection, cancel the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            return;
        }

        // if nothing is read - this is shut down
        if (numRead == -1) {
            // Remote entity shut the socketService down cleanly. Do the same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            return;
        }

        // Hand the data off to our worker (processing) thread
        byte[] readBytes = new byte[numRead];                                // array for REALLY received data
        System.arraycopy(this.readBuffer.array(), 0, readBytes, 0, numRead); // copy received data into new array
        log.debug(String.format("Server received -> size=[%s] data=[%s]", numRead, new String(readBytes, LOG_DATA_ENCODING)));
        // process received data and send answer back
        this.worker.processData(this, socketChannel, readBytes);
    }

    /***/
    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socketService channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        //Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);
        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    /***/
    public void send(SocketChannel socket, byte[] data) {
        synchronized (this.changeRequests) {
            // Indicate we want the interest ops set changed
            this.changeRequests.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    /**
     * Method returns nio socket server config -> hostname:port.
     * @return String nio server config.
    */
    public String getServerConfig() {
        return (String.format("NIO TCSocketServer: [%s].", this.host));
    }

    /***/
    public synchronized void serverStart() {
        if (!isStarted) {
            new Thread(this).start();
            new Thread(this.worker).start();
            log.info("NIO Server started: " + this.host);
        } else {
            log.warn("NIO Server already started! Host: " + this.host);
        }
    }

    /***/
    public void serverStop() {
        // todo: implement method!
        throw new UnsupportedOperationException("Not implemented yet!");
    }

}