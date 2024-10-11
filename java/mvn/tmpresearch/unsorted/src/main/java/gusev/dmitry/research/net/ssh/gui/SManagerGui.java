package gusev.dmitry.research.net.ssh.gui;

import gusev.dmitry.research.net.ssh.SSHHost;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 23.12.2015)
 */
public class SManagerGui {

    private static final Log log = LogFactory.getLog(SManagerGui.class);

    // container
    private JPanel windowContent;
    // layout manager
    private GridLayout gLayout;

    /***/
    public SManagerGui(Set<SSHHost> servers) {
        log.debug("SManagerGui.constructor() working.");

        if (servers == null || servers.size() <= 0) { // check state
            throw new IllegalArgumentException("Servers list for GUI shouldn't be empty!");
        }

        // init gui components
        this.windowContent = new JPanel();
        // rows = servers count, columns = 2
        this.gLayout = new GridLayout(servers.size(), 2);
        this.windowContent.setLayout(this.gLayout);

        // add visual controls to frame
        Map<SSHHost, Pair<JLabel, JLabel>> controls = new LinkedHashMap<>();
        JLabel serverLabel;
        JLabel statusLabel;
        for (SSHHost server : servers) {
            serverLabel = new JLabel(String.format("%s %n(%s:%s)", server.getName(), server.getHost(), server.getPort()));
            statusLabel = new JLabel("status: ");
            controls.put(server, new ImmutablePair<>(serverLabel, statusLabel));
            this.windowContent.add(serverLabel);
            this.windowContent.add(statusLabel);
        }

    }

    /*
    // top level container
    JFrame frame = new JFrame("Grid layout calculator.");
    frame.setContentPane(windowContent);
    // size and visibility
    frame.setSize(400, 100);
    frame.setVisible(true);
    */

}