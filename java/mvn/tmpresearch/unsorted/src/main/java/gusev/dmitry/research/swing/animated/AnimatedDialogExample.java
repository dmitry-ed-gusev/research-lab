package gusev.dmitry.research.swing.animated;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 16.10.12)
 */

public class AnimatedDialogExample implements ActionListener {

    private Log    log = LogFactory.getLog(AnimatedDialogExample.class);

    // swing controls elements
    private JFrame     frame;
    private JTextField field;

    // animated dialog reference
    AnimatedDialog ad;

    public AnimatedDialogExample() {
        log.debug("AnimatedDialogExample() constructor.");
        // main app frame
        frame = new JFrame("Animated dialig test.");

        // creating animated dialog
        ad = new AnimatedDialog(this.frame, "img/ship.gif", "<html><b>Please wait...</b>", true);

        // initializing GUI controls
        field  = new JTextField(20);
        JButton button = new JButton("Run!");
        button.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(field);
        panel.add(button);

        frame.setContentPane(panel);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                log.debug("Shutting down...");
                System.exit(0);
            }
        });
        frame.setResizable(false);
        // start frame position - screen center
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("Button pressed!");
        //ad.on();
        ad.propertyChange(new PropertyChangeEvent("showTime", "showTime", true, true));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.debug("....");
                for (int i = 1; i <= 10; i++) {
                    log.debug("Sleep 1 second (" + i + ").");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage());
                            }
                }
            }
        });

        //ad.on();

        ad.off();
//        if (!StringUtils.isBlank(this.field.getText())) {
//            int seconds = 0;
//            try {
//                seconds = Integer.parseInt(this.field.getText());
//            } catch (NumberFormatException ex) {
//                log.error("Can't parse integer [" + this.field.getText() + "]! Reason: " + ex.getMessage());
//
//            }
//            if (seconds > 0) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        showDialog(5);
//                        ad.off();
//                    }
//                });
//            }
//        }
    }

    private void showDialog(int seconds) {
        //ad.on();
        for (int i = 1; i <= seconds; i++) {
            log.debug("Sleep 1 second (" + i + ").");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        //ad.off();
    }

    public static void main(String[] args) {
        Log log = LogFactory.getLog(AnimatedDialogExample.class);
        log.debug("AnimatedDialogExample.main() working.");

        AnimatedDialogExample ade = new AnimatedDialogExample();

        //log.debug("created AnimatedDialog object...");
        //ad.on();
        //ad.propertyChange(new PropertyChangeEvent("showTime", "showTime", true, true));

        //log.debug("doing some things....");

        //int sum = 0;
        // sleep for some seconds
        /*
        for (int i = 1; i <= 30; i++) {
            log.debug("Sleep 1 second (" + i + ").");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }

        log.debug("finished doing some things... ");
        */
        //ad.off();
        //ad.propertyChange(new PropertyChangeEvent("showTime", "showTime", false, false));

    }

}