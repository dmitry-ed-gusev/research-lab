package gusev.dmitry.research.books.java24h_trainer.lesson8;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 28.09.12)
*/

public class BorderLayoutSample {

    public static void main(String[] args) {

        Log log = LogFactory.getLog(BorderLayoutSample.class);
        log.info("BorderLayout example started.");

        // main component container
        JPanel windowContent = new JPanel();

        // layout manager for main panel
        BorderLayout borderLayout = new BorderLayout();
        windowContent.setLayout(borderLayout);

        // north panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 2));
        northPanel.add(new JLabel("Number 1:"));
        northPanel.add(new JTextField(10));

        // center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 2));
        centerPanel.add(new JLabel("Number 2:"));
        centerPanel.add(new JTextField(10));
        centerPanel.add(new JLabel("Sum:"));
        centerPanel.add(new JTextField(10));

        // add north panel to main panel
        windowContent.add(BorderLayout.NORTH, northPanel);
        // add center panel to main panel
        windowContent.add(BorderLayout.CENTER, centerPanel);

        // creating components and assigning they with container
        //windowContent.add(BorderLayout.NORTH, new JLabel("Number 1:"));
        //windowContent.add(BorderLayout.NORTH, new JTextField(10));
        //windowContent.add(BorderLayout.CENTER, new JLabel("Number 2:"));
        //windowContent.add(BorderLayout.CENTER, new JTextField(10));
        //windowContent.add(BorderLayout.SOUTH, new JLabel("Sum:"));
        //windowContent.add(BorderLayout.SOUTH, new JTextField(10));

        // add button to south area
        windowContent.add(BorderLayout.SOUTH, new JButton("Add"));

        // top level container
        JFrame frame = new JFrame("Border layout calculator.");
        frame.setContentPane(windowContent);
        // size and visibility
        frame.setSize(400, 150);
        frame.setVisible(true);
    }

}