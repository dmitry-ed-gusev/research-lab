package gusev.dmitry.research.db.pilot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 10.10.12)
 */
public class DbPilotGui {

    private Log log = LogFactory.getLog(DbPilotGui.class);

    public DbPilotGui() {

        // maoin panel
        JPanel mainPanel = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        mainPanel.setLayout(gb);

        //
        GridBagConstraints gbc = new GridBagConstraints();

        // left text area
        JTextArea leftTextArea = new JTextArea("left text area");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gb.setConstraints(leftTextArea, gbc);
        mainPanel.add(leftTextArea);

        // top area
        JTextArea topTextArea = new JTextArea("top text area");
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gb.setConstraints(topTextArea, gbc);
        mainPanel.add(topTextArea);

        // bottom JTable area
        DbPilotGridModel gridModel = new DbPilotGridModel();
        JTable grid = new JTable(gridModel);
        JScrollPane scrollPane = new JScrollPane(grid);
        gbc.gridy = 1;
        gb.setConstraints(scrollPane, gbc);
        mainPanel.add(scrollPane);

        // application main frame
        JFrame mainFrame = new JFrame("DBPilot");
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

        // close window listener - exit from system when window closed
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        DbPilotGui dbPilot = new DbPilotGui();
    }

}
