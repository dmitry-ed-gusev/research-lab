package dgusev.apps.downloadManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 29.09.2014)
*/

public class DownloadManager extends JFrame implements Observer {

    private final static Log log = LogFactory.getLog(DownloadManager.class);

    private JTextField          addTextField; // field for adding new download
    private DownloadsTableModel tableModel;   // table model
    private JTable              table;        // main visual table

    private JButton  pauseButton, resumeButton, cancelButton, clearButton; // manage selected download process
    private Download selectedDownload;  // currently selected download process
    private boolean  clearing;          // table clearing flag

    /***/
    public DownloadManager() throws HeadlessException {
        this.setTitle("Download manager");
        this.setSize(640, 480);

        // process window closing event
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DownloadManager.this.actionExit();
            }
        });

        // set up 'File' menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu   = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        // set up download adding panel
        JPanel addPanel = new JPanel();
        this.addTextField = new JTextField(30);
        addPanel.add(addTextField);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionAdd();
            }
        });
        addPanel.add(addButton);

        // set up downloads table
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DownloadManager.this.tableSelectionChanged();
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // allow select just one row

        // set up Progressbar as progress column visualizer
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show text
        table.setDefaultRenderer(JProgressBar.class, renderer);
        // set row height - it should be enough for JProgressBar
        table.setRowHeight((int) renderer.getPreferredSize().getHeight());

        // set up downloads panel
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // set up buttons panel
        JPanel buttonsPanel = new JPanel();

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionPause();
            }
        });
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionResume();
            }
        });
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);

        cancelButton = new JButton("cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionCancel();
            }
        });
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadManager.this.actionClear();
            }
        });
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);

        // adding panel to content pane
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(addPanel, BorderLayout.NORTH);
        this.getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    /***/
    private void actionExit() {
        System.exit(0);
    }

    /***/
    private void actionAdd() {
        if (this.addDownload(this.addTextField.getText())) {
            addTextField.setText(""); // reset text field
        } else {
            JOptionPane.showMessageDialog(this, "Invalid download URL", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /***/
    private boolean addDownload(String strUrl) {
        boolean result;
        URL verifiedUrl = this.verifyUrl(strUrl);
        if (verifiedUrl != null) {
            // add download to table and start it
            tableModel.addDownload(new Download(verifiedUrl));
            result = true;
        } else {
            result = false;
            log.error(String.format("Invalid download URL [%s]!", strUrl));
        }
        return result;
    }

    /***/
    private URL verifyUrl(String url) {
        // allowed only addresses that start with HTTP:
        if (!url.toLowerCase().startsWith("http://")) {
            return null;
        }

        // check URL format
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }

        // check - does addres contains file name?
        if (verifiedUrl.getFile().length() < 2) {
            return null;
        }

        return verifiedUrl;
    }

    /** Called in case of changing table row selection. */
    private void tableSelectionChanged() {
        if (selectedDownload != null) {
            selectedDownload.deleteObserver(DownloadManager.this);
        }

        // if not clearing, set selected download process and register observer
        if (!clearing && table.getSelectedRow() > -1) {
            selectedDownload = tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(DownloadManager.this);
            this.updateButtons();
        }
    }

    /***/
    private void actionPause() {
        selectedDownload.pause();
        this.updateButtons();
    }

    /***/
    private void actionResume() {
        selectedDownload.resume();
        this.updateButtons();
    }

    /***/
    private void actionCancel() {
        selectedDownload.cancel();
        this.updateButtons();
    }

    /***/
    private void actionClear() {
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        this.updateButtons();
    }

    /** Updating buttons state depending on current download process state. */
    private void updateButtons() {
        if (selectedDownload != null) {
            int status = selectedDownload.getStatus();
            switch (status) {
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
            }
        } else { // no selected download process (selection is empty)
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }

    /** Update called, when Download object notifying observers about changes. */
    public void update(Observable o, Object arg) {
        // update buttons if selected download process changed
        if (selectedDownload != null && selectedDownload.equals(o)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DownloadManager.this.updateButtons();
                }
            });
        }
    }

    /***/
    public static void start() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DownloadManager manager = new DownloadManager();
                manager.setVisible(true);

                // add some downloads to processing
                /*
                for (int i = 1; i <= 10000; i++) {
                    String url = String.format("http://support.csis.pace.edu/CSISWeb/docs/techReports/techReport%s.pdf", i);
                    manager.addDownload(url);
                }
                */

            }
        });
    }

}