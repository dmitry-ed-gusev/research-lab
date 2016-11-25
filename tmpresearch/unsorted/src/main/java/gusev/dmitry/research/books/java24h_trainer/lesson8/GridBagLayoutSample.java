package gusev.dmitry.research.books.java24h_trainer.lesson8;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.10.12)
*/

public class GridBagLayoutSample {

    public static void main(String[] args) {

        Log log = LogFactory.getLog(GridBagLayoutSample.class);
        log.info("GridBagLayout example started.");

        // main component container
        JPanel windowContent = new JPanel();

        // GridBagLayout layout manager
        GridBagLayout gb = new GridBagLayout();
        windowContent.setLayout(gb);

        // GridBagConstraints instance
        GridBagConstraints gbc = new GridBagConstraints();

        // constraints for first cell
        gbc.gridx = 0; // x coordinate in the grid
        gbc.gridy = 0; // y coordinate in the grid
        // Cell height - in cells units. This cell has the same height as other cells
        gbc.gridheight = 1;
        // Cell width - in cells units. This cell is as wide as 1 other one.
        gbc.gridwidth = 1;
        // Fill all space in the cell
        gbc.fill = GridBagConstraints.BOTH;
        // proportion of horizontal space taken by this component
        gbc.weightx = 1.0;
        // proportion of vertical space taken by this component
        gbc.weighty = 1.0;
        // position of the component within the cell
        gbc.anchor = GridBagConstraints.CENTER;

        // set constraints for this field
        JLabel label1 = new JLabel("Number 1:");
        gb.setConstraints(label1, gbc);
        // add component to panel
        windowContent.add(label1);

        // next control element
        gbc.gridx = 1;
        JTextField field1 = new JTextField(10);
        gb.setConstraints(field1, gbc);
        windowContent.add(field1);

        JLabel label2 = new JLabel("Number 2:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gb.setConstraints(label2, gbc);
        windowContent.add(label2);

        JTextField field2 = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gb.setConstraints(field2, gbc);
        windowContent.add(field2);

        JLabel label3 = new JLabel("Sum:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gb.setConstraints(label3, gbc);
        windowContent.add(label3);

        JTextField field3 = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gb.setConstraints(field3, gbc);
        windowContent.add(field3);

        JButton addButton = new JButton("Add");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // width = 2 cells width
        gbc.fill = GridBagConstraints.CENTER; // how to fill the cell?
        gb.setConstraints(addButton, gbc);
        windowContent.add(addButton);

        // top level container
        JFrame frame = new JFrame("Box layout calculator.");
        frame.setContentPane(windowContent);
        // size and visibility
        frame.setSize(400, 150);
        frame.setVisible(true);

    }

}