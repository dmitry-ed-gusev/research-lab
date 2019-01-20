package gusev.dmitry.jtils.net.monitoring;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * System module for ping network nodes - check their accessibility.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 19.08.13)
*/

public class NetPinger {

    private Log log = LogFactory.getLog(NetPinger.class);

    // when class loaded by classloader we determine OS type and select command for ping
    private static final boolean IS_WINDOWS_OS    = System.getProperty("os.name").startsWith("Windows");
    private static final String  PING_COMMAND     = (IS_WINDOWS_OS ? "ping -n 1 %s" : "ping -c 1 %s");

    // nodes list for ping
    private Map<String, String> nodesList;

    /***/
    public NetPinger(Map<String, String> nodesList) {
        if (nodesList == null || nodesList.size() <= 0) {
            throw new IllegalArgumentException("Empty nodes list!");
        }
        this.nodesList = nodesList;
    }

    /**
     * Ping node and return string result of ping.
     * @param nodeName String Name of node. May be empty, but not recommended.
     * @param nodeAddress String Network name or IP address of node. Mandatory parameter, shouldn't be empty.
    */
    private static String pingNode(String nodeName, String nodeAddress) throws IOException, InterruptedException {
        Log log = LogFactory.getLog(NetPinger.class);
        log.debug("PingNodes.pingNode() working. OS type [" + (IS_WINDOWS_OS ? "WIN" : "NIX") + "]. " +
                "Node [" + nodeName + ": " + nodeAddress + "].");

        String result;
        if (!StringUtils.isBlank(nodeAddress)) {
            // execute another OS process
            Process myProcess = Runtime.getRuntime().exec(String.format(PING_COMMAND, nodeAddress));
            myProcess.waitFor();
            // check exit value
            result = String.format("Ping node [%s: %s] result: %s", nodeName, nodeAddress, (myProcess.exitValue() == 0 ? "OK" : "FAIL"));
        } else { // empty parameter(s)
            throw new IOException("Empty node address!");
        }

        return result;
    }

    /***/
    public void ping() {
        log.debug("PingNodes.ping() working.");
        // ping all nodes by list
        for (Map.Entry<String, String> node : this.nodesList.entrySet()) {
            try {
                log.info(NetPinger.pingNode(node.getKey(), node.getValue()));
            } catch (IOException | InterruptedException e) {
                log.error(e);
            }
        }
    }

    /** Method just for test. */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(NetPinger.class);
        log.info("Start ping.");

        Map<String, String> nodes = new HashMap<>();
        nodes.put("DEV server", "dc-mes-node1");
        nodes.put("ORA server #1", "dc-mes-node2");
        nodes.put("ORA server #2", "dc-mes-node3");
        nodes.put("Weighers NORTH", "ves");
        nodes.put("Weighers SOUTH", "vesu");

        NetPinger pinger = new NetPinger(nodes);
        pinger.ping();

    }


}