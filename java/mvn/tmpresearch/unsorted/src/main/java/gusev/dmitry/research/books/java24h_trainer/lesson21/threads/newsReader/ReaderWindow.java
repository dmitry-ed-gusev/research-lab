package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.newsReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 26.10.12)
 */

public class ReaderWindow implements ActionListener {

    // logger
    private Log log = LogFactory.getLog(ReaderWindow.class);

    // some constants
    private final static int    TEXT_AREAS_COUNT    = 3;
    private final static int    TEXT_AREA_ROWS      = 6;
    private final static int    TEXT_AREA_COLS      = 60;
    private final static String ACTION_SWING_UTILS  = "startWithThreads";
    private final static String ACTION_SWING_WORKER = "startWithWorker";
    private final static String ACTION_CLEAR        = "clear";

    // text areas list
    private static ArrayList<JTextArea>         textAreas = new ArrayList<JTextArea>();
    // threads list
    private static ArrayList<ReaderThread>      threads   = new ArrayList<ReaderThread>();

    // constructor - creates interface
    public ReaderWindow() {
        log.debug("ReaderWindow constructor.");
        JFrame mainFrame = new JFrame("News READER.");

        JPanel panel = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        panel.setLayout(gb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // start button (swing utils with Thread)
        gbc.anchor = GridBagConstraints.WEST;
        JButton startThreadsButton = new JButton("Start with threads");
        startThreadsButton.setActionCommand(ACTION_SWING_UTILS);
        startThreadsButton.addActionListener(this);
        gb.setConstraints(startThreadsButton, gbc);
        panel.add(startThreadsButton);
        // start button (swing worker implementation)
        gbc.gridx = 1;
        JButton startSwingWorkerButton = new JButton("Start with Swing Worker");
        startSwingWorkerButton.setActionCommand(ACTION_SWING_WORKER);
        startSwingWorkerButton.addActionListener(this);
        gb.setConstraints(startSwingWorkerButton, gbc);
        panel.add(startSwingWorkerButton);
        // clear all text areas button
        gbc.gridx = 2;
        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand(ACTION_CLEAR);
        clearButton.addActionListener(this);
        gb.setConstraints(clearButton, gbc);
        panel.add(clearButton);

        // text areas = adding in a cycle
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        //textAreas = new ArrayList<JTextArea>();
        for (int i = 1; i <= TEXT_AREAS_COUNT; i++) {
            gbc.gridy = i;
            JTextArea textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLS);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textAreas.add(textArea);
            JScrollPane scrollPane = new JScrollPane(textArea);
            gb.setConstraints(scrollPane, gbc);
            panel.add(scrollPane);
        }

        // other settings for main frame
        mainFrame.setContentPane(panel);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // move main frame to screen center
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
        mainFrame.setLocation(x, y);
        // show main frame
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Log log = LogFactory.getLog(ReaderWindow.class);
        log.info("News Reader starting.");

        // Initial thread should schedule the GUI creation task by invoking javax.swing.SwingUtilities.invokeLater
        // or javax.swing.SwingUtilities.invokeAndWait. Why does not the initial thread simply create the GUI itself?
        // Because almost all code that creates or interacts with Swing components must run on the event dispatch thread.
        // (text above was get from Pracle Swing concurrency tutorial)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // creating gui
                //ReaderWindow r = new ReaderWindow();
                new ReaderWindow();
                // preparing threads
                for (int i = 1; i <= TEXT_AREAS_COUNT; i++) {
                    threads.add(new ReaderThread("ReaderThread" + i, "java24h_lesson21_news" + i + ".txt", textAreas.get(i - 1)));
                }
            }
        });
        log.info("Initial (main) thread of news reader is finished.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("Button pressed! Action [" + e.getActionCommand() + "].");

        if (ACTION_SWING_UTILS.equals(e.getActionCommand())) { // run action with SwingUtilities
            log.debug("utils");
            for(int i = 0; i <= TEXT_AREAS_COUNT - 1; i++) {
                SwingUtilities.invokeLater(threads.get(i));
            }
        } else if (ACTION_SWING_WORKER.equals(e.getActionCommand())) { // run action with SwingWorkers
            log.debug("worker");
            // we need to create SwingWorkers every time we pressed button, because (see javadoc):
            // * SwingWorker is only designed to be executed once. Executing a SwingWorker more than once will *
            // * not result in invoking the doInBackground method twice.                                       *
            for(int i = 0; i <= TEXT_AREAS_COUNT - 1; i++) {
                ReaderSwingWorker rsw = new ReaderSwingWorker("java24h_lesson21_news" + (i + 1) + ".txt", textAreas.get(i));
                rsw.execute();
            }
        } if (ACTION_CLEAR.equals(e.getActionCommand())) {
            for (JTextArea textArea : textAreas) {
                textArea.setText(null);
            }
        } else { // invalid action type
            log.error("Invalid action type [" + e.getActionCommand() + "]!");
        }

    }

}