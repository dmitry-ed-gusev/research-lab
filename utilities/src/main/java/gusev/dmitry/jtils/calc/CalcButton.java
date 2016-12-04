package gusev.dmitry.jtils.calc;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Calculator button.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 03.10.12)
*/

public class CalcButton extends JButton {

    /** Default constructor. */
    public CalcButton(String text, ActionListener actionListener) {
        super(text);
        if (actionListener == null) {
            throw new IllegalStateException("Action listener for CalcButton can't be null!");
        }

        this.addActionListener(actionListener); // register action listener
        this.setFocusable(false);               // no one calc button can't gain focus
        this.setActionCommand(text);            // set action command

    }

}