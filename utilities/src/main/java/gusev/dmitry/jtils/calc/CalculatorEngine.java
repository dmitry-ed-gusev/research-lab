package gusev.dmitry.jtils.calc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.10.12)
*/

@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
public class CalculatorEngine implements ActionListener, KeyListener {

    // enumeration class - all calculator actions (except erase actions)
    public enum CalcAction {
        ADD ("+"), SUB ("-"), MUL ("*"), DIV ("/"), SIGN("±"), SQRT("√"), PERCENT("%"),
        ONE_DIV("1/x"), EQUALS("="), MC("MC"), MR("MR"), MS("MS"), MPLUS("M+"), MMINUS("M-");

        private String strValue;

        private CalcAction(String strValue) {
            this.strValue = strValue;
        }

        public String getValue() {
            return this.strValue;
        }

        /***/
        public static String getActionsForRegEx() {
            StringBuilder builder = new StringBuilder();
            for (CalcAction action : CalcAction.values()) {
                // escape characters for short values: +-*/ etc
                if (action.getValue().length() == 1) {
                    builder.append("\\");
                }
                // in memory actions we should escape characters + and -
                if (action.equals(MPLUS)) {
                    builder.append("M\\+");
                } else if (action.equals(MMINUS)) {
                    builder.append("M\\-");
                } else {
                    builder.append(action.getValue());
                }
                // adding character | - logical OR for regular expressions
                if (action.ordinal() < CalcAction.values().length) {
                    builder.append("|");
                }
            } // end of for
            return builder.toString();
        }

        /***/


    } // end of actions enum

    // calculator erase actions
    public enum CalcEraseAction {
        BACKSPACE("←"), ERASE_ALL("C"), ERASE_CURRENT("CE");

        private String strValue;

        private CalcEraseAction(String strValue) {
            this.strValue = strValue;
        }

        public String getValue() {
            return strValue;
        }
    }

    // module logger
    private Log log = LogFactory.getLog(CalculatorEngine.class);

    // link to calculator swing class
    private Calculator calculator;

    // actions
    private CalcAction[] actionsList = new CalcAction[Consts.ACTIONS_MAX_COUNT];
    // operands
    private BigDecimal[] operands    = new BigDecimal[Consts.ACTIONS_MAX_COUNT*2];

    // some flags
    private boolean floatingPoint = false; // only one point for one number
    private boolean initialState  = true;  // is initial display value (0) changed?

    public CalculatorEngine(Calculator calculator) {
        log.debug("CalculatorEngine() constructor working.");
        this.calculator = calculator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("Some action: [source: " + e.getSource().getClass().getName() + "; " +
                "action command: " + e.getActionCommand() + "]");

        String command = e.getActionCommand();

        // pressed digital button
        if (command.matches("[0-9]")) {
            if (initialState) { // if state is initial - replacing text
                calculator.replaceDisplayText(command);
                initialState = false;
            } else { // if state isn't initial - adding text
                calculator.addDisplayText(command);
            }
        } else if (command.matches("\\.|,")) { // pressed . or , (floating point)
          if (!floatingPoint) {
              floatingPoint = true;
              initialState  = false;
              calculator.addDisplayText(",");
          }
        } else if (command.matches(CalcAction.getActionsForRegEx())) { // action requested
            log.debug("Action [" + command + "] requested!");

            // selecting command
            /*
            switch (CalcAction.valueOf(command)) {

                case ADD:
                    //BigDecimal operand = new BigDecimal(this.calculator.getDisplayText());
                    break;
            }
            */

        } else if (command.equalsIgnoreCase(CalcEraseAction.ERASE_ALL.getValue())) { // erase all
            log.debug("ERASE ALL action requested.");
            this.resetCalcStateAction();
        } else if (command.equalsIgnoreCase(CalcEraseAction.ERASE_CURRENT.getValue())) {// erase current operand
            log.debug("ERASE CURRENT action requested.");
        } else if (command.equalsIgnoreCase(CalcEraseAction.BACKSPACE.getValue())) { // erase last symbol (backspace)
            log.debug("BACKSPACE action requested.");
            this.backspaceCalcAction();
        }
        //JOptionPane.showConfirmDialog(null, "Something happened...",
        //        "Just a test", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    /** We use this listener because only it receives keys codes. */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        log.debug("Key pressed: [key: " + keyEvent.getKeyChar() + "; code: " +
                keyEvent.getKeyCode() + "].");
        if(keyEvent.getKeyCode() == 27) { // ESC key pressed
            this.resetCalcStateAction();
        } else if (keyEvent.getKeyCode() == 8) { // BackSpace keycode
            this.backspaceCalcAction();
        } else { // other key - event will be transfered to actionPerformed() method
            ActionEvent event = new ActionEvent(keyEvent.getSource(), keyEvent.getID(), String.valueOf(keyEvent.getKeyChar()));
            this.actionPerformed(event);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        //log.debug("Key released: key [" + keyEvent.getKeyChar() + "].");
    }

    private void resetCalcStateAction() {
        this.initialState = true;
        this.floatingPoint = false;
        this.calculator.replaceDisplayText("0");
    }

    private void backspaceCalcAction() {
        if (!this.initialState) {
            String text = this.calculator.getDisplayText();
            if (text.length() == 1) { // one symbol on display
                this.initialState = true;
                this.calculator.replaceDisplayText(Consts.DISPLAY_INITIAL_VALUE);
            } else { // more than one symbol on display
                this.calculator.replaceDisplayText(text.substring(0, text.length() - 1));
            }
        }
    }

}