package gusev.dmitry.chat.client;

//import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

/**
 * Chat client window. In this window implemented resizeable JTextArea for messages.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.11.12)
 */

// todo: add "clean messages text area" toobox button
// todo: add settings dialog window

public class ChatClientWindow implements ChatClientConstants {

    // this module logger
    private Log log = LogFactory.getLog(ChatClientWindow.class);
    // system properties
    private Properties config;
    // GUI controls. We need links to this controls for usage and interaction.
    private JFrame     mainFrame;    // main app frame
    private JTextArea  messagesArea; // all messages area
    private JTextField message;      // one message field
    private JLabel     statusLabel;  // label for status
    // Chat client engine - action listener and logic processor for chat client app
    private ChatClientEngine chatEngine;

    /**
     * Constructor. Creates main chat window and ChatEngine and connects classes to each other.
     * If received Properties object is null or empty constructor will throw IllegalStateException.
    */
    public ChatClientWindow(Properties config) {
        log.debug("ChatClientWindow.constructor() working.");

        // we can't work with empty properties
        if (config == null || config.isEmpty()) {
            throw new IllegalStateException("System properties are empty! Can't continue.");
        }

        this.config = config;
        // creating chat engine
        chatEngine = new ChatClientEngine(this, this.config);

        // main panel with controls
        JPanel mainPanel = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        mainPanel.setLayout(gb);
        GridBagConstraints gbc = new GridBagConstraints();

        // some settings for grid bag constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        // This field is used when the component is smaller than its display area. It determines where,
        // within the display area, to place the component. (description from javadoc)
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);
        // width and height of one cell in cell units
        gbc.gridwidth = 2;
        gbc.gridheight = 1;

        // toolbar and buttons
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        // connect button
        JButton connectButton = new JButton();
        ImageIcon connectIcon = new ImageIcon(ChatClientWindow.class.getResource(ICON_CONNECTED));
        connectButton.setIcon(connectIcon);
        connectButton.addActionListener(chatEngine);
        connectButton.setFocusable(false);
        connectButton.setActionCommand(ACTION_CONNECT);
        connectButton.setToolTipText("Connect to chat server");
        toolbar.add(connectButton);

        // disconnect button
        JButton disconnectButton = new JButton();
        ImageIcon disconnectIcon = new ImageIcon(ChatClientWindow.class.getResource(ICON_DISCONNECTED));
        disconnectButton.setIcon(disconnectIcon);
        disconnectButton.addActionListener(chatEngine);
        disconnectButton.setFocusable(false);
        disconnectButton.setActionCommand(ACTION_DISCONNECT);
        disconnectButton.setToolTipText("Disconnect from chat server");
        toolbar.add(disconnectButton);

        // add toolbar to main panel
        gb.setConstraints(toolbar, gbc);
        mainPanel.add(toolbar);

        // text area panel (messages)
        messagesArea = new JTextArea(MESSAGES_AREA_ROWS, MESSAGES_AREA_COLUMNS);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        messagesArea.setFocusable(false);
        messagesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagesArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // cell coordinates in grid
        gbc.gridx = 0;
        gbc.gridy = 1;
        // how to distribute extra horizontal/vertical space
        gbc.weightx = 1; // how to distribute extra HORIZONTAL space
        gbc.weighty = 1; // how to distribute extra VERTICAL space
        // add text area to main panel
        mainPanel.add(scrollPane, gbc);

        // text field with new message text
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.weighty = 0;
        //gbc.weightx = 2;
        //gbc.fill = GridBagConstraints.VERTICAL;
        message = new JTextField();
        message.addKeyListener(chatEngine);
        message.requestFocusInWindow(); // request initial focus in window
        gb.setConstraints(message, gbc);
        mainPanel.add(message);

        // button "Send"
        gbc.gridx = 1;
        gbc.weightx = 0;
        //gbc.fill = GridBagConstraints.NONE;
        JButton sendButton = new JButton("Send");
        gb.setConstraints(sendButton, gbc);
        sendButton.addActionListener(chatEngine); // add action listener for send button
        //sendButton.addKeyListener(this);    // add key listener for send button
        sendButton.setActionCommand(ACTION_MSG_SEND);
        sendButton.setFocusable(false);
        mainPanel.add(sendButton);

        // create the status bar panel (at the bottom)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel statusPanel = new JPanel();
        mainPanel.add(statusPanel, gbc);
        statusPanel.setLayout(new GridLayout());
        // status label
        statusLabel = new JLabel("Application loaded.");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.add(statusLabel);

        // main app frame
        mainFrame = new JFrame(APP_HEADER);
        mainFrame.setContentPane(mainPanel);

        // close main window (default operation)
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // when we will close main frame we will call disconnect() method
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatEngine.disconnect();
            }
        });

        // pack main frame - set proper size
        mainFrame.pack();

        // move main frame to screen center
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
        mainFrame.setLocation(x, y);

        // use windows look and feel (LAF)
        try {
            // todo: fixed due to change JDK 8 -> JDK 11
            //UIManager.setLookAndFeel(new WindowsLookAndFeel());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            log.error(e);
        }

        log.debug("Gui created. Event dispatch thread: " + SwingUtilities.isEventDispatchThread());
    }

    public void setStatus(String status) {
        log.debug("ChatClientWindow.setStatus() working.");
        if (!StringUtils.isBlank(status)) {
            this.statusLabel.setText(status);
        }
    }

    /**
     * Method shows main application frame.
    */
    public void showChatClientWindow() {
        // Connecting to chat server.
        chatEngine.connect();
        // show main app frame
        this.mainFrame.setVisible(true);
    }

    public String getMessage() {
        return this.message.getText();
    }

    public void putMessageOnScreen(String message) {
        log.debug("ChatClientWindow.putMessageOnScreen() working.");
        if (!StringUtils.isBlank(message)) {
            this.messagesArea.append(message + "\n");
            this.message.setText("");
        }
    }

}