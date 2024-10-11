package gusev.dmitry.research.books.java24h_trainer.lesson8;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 26.09.12)
*/

public class FlowLayoutSample {

    public static void main(String[] args) {
        Log log = LogFactory.getLog(FlowLayoutSample.class);
        log.info("FlowLayout example started.");

        // panel - window content
        JPanel content = new JPanel();

        // set layout manager for panel
        FlowLayout fl = new FlowLayout();
        content.setLayout(fl);

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
            content.add(entry.getKey());
            content.add(entry.getValue());
        }

        // add ok button
        content.add(okButton);

        // frame
        JFrame frame = new JFrame("Calculator with FlowLayout manager.");
        // panel to frame
        frame.setContentPane(content);
        // set size and make visible
        frame.setSize(400, 100);
        frame.setVisible(true);
    }

}