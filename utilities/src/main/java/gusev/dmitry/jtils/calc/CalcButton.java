package gusev.dmitry.jtils.calc;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 03.10.12)
*/

public class CalcButton extends JButton {

    public CalcButton(String text, ActionListener actionListener) {
        super(text);
        if (actionListener == null) {
            throw new IllegalStateException("Action listener for JButton can't be null!");
        }
        // register action listener
        this.addActionListener(actionListener);
        // all our calc buttons can't gain focus
        this.setFocusable(false);
        // set action command
        this.setActionCommand(text);
    }

}