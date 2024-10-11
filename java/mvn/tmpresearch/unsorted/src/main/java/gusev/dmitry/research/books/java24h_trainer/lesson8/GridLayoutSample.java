package gusev.dmitry.research.books.java24h_trainer.lesson8;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 27.09.12)
*/

public class GridLayoutSample {

    public static void main(String[] args) {

        Log log = LogFactory.getLog(GridLayoutSample.class);
        log.info("GridLayout example started.");

        // component container
        JPanel windowContent = new JPanel();

        // layout manager
        GridLayout gLayout = new GridLayout(4, 2);
        windowContent.setLayout(gLayout);

       // Create controls (in memory) and put them into map. We use here LinkedHashMap
       // implementation, because it preserve order of added entries.
       Map<JLabel, JTextField> controls = new LinkedHashMap<JLabel, JTextField>();
       controls.put(new JLabel("Number 1:"), new JTextField(10));
       controls.put(new JLabel("Number 2:"), new JTextField(10));
       controls.put(new JLabel("Sum:"), new JTextField(10));
       // ok button
       JButton okButton = new JButton("Add");

       // add controls to panel
       for (Map.Entry<JLabel, JTextField> entry : controls.entrySet()) {
           windowContent.add(entry.getKey());
           windowContent.add(entry.getValue());
       }

       // add ok button
       windowContent.add(okButton);

        // top level container
        JFrame frame = new JFrame("Grid layout calculator.");
        frame.setContentPane(windowContent);
        // size and visibility
        frame.setSize(400, 100);
        frame.setVisible(true);
    }
}