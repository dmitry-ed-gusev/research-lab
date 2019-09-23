package dgusev.apps.calc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.10.12)
 */

public class CalculatorEngine extends KeyAdapter implements ActionListener {

    private static final Log LOG = LogFactory.getLog(CalculatorEngine.class);

    // link to calculator swing class
    private Calculator calculator;

    // actions
    private CalcAction[] actionsList = new CalcAction[CalculatorDefaults.ACTIONS_MAX_COUNT];
    // operands
    private BigDecimal[] operands = new BigDecimal[CalculatorDefaults.ACTIONS_MAX_COUNT * 2];

    // some flags
    private boolean floatingPoint = false; // only one point for one number
    private boolean initialState = true;  // is initial display value (0) changed?

    public CalculatorEngine(Calculator calculator) {
        LOG.debug("CalculatorEngine() constructor working.");
        this.calculator = calculator;
    }

    @Override
    // todo: implement actions
    public void actionPerformed(ActionEvent e) {
        LOG.debug("Some action: [source: " + e.getSource().getClass().getName() + "; " +
                "action command: " + e.getActionCommand() + "]");

        String command = e.getActionCommand();

        if (command.matches("[0-9]")) { // pressed digital button
            if (initialState) { // if state is initial - replacing text
                calculator.replaceDisplayText(command);
                initialState = false;
            } else { // if state isn't initial - adding text
                calculator.addDisplayText(command);
            }
        } else if (command.matches("\\.|,")) { // pressed . or , (floating point)
            if (!floatingPoint) {
                floatingPoint = true;
                initialState = false;
                calculator.addDisplayText(",");
            }
        } else {
            // perform an action, depending on its type
            switch (CalcAction.getActionByString(command)) {
                case ERASE_ALL:
                    this.resetCalcStateAction();
                    break;
                case ERASE_CURRENT:
                    LOG.debug("ERASE_CURRENT called.");
                    break;
                case BACKSPACE:
                    this.backspaceCalcAction();
                    break;

                case ADD:
                    calculator.addDisplayText("+");
                    break;
                case SUB:
                    calculator.addDisplayText("-");
                    break;
                default:
                    LOG.debug(String.format("Action = [%s].", command));
            }
        }

    }

    /**
     * We use this listener's method, because only it receives keys codes. Other
     * methods (keyTyped()/keyReleased()) don't.
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        LOG.debug("Key pressed: [key: " + keyEvent.getKeyChar() + "; code: " +
                keyEvent.getKeyCode() + "].");
        if (keyEvent.getKeyCode() == 27) { // ESC key pressed
            this.resetCalcStateAction();
        } else if (keyEvent.getKeyCode() == 8) { // BackSpace keycode
            this.backspaceCalcAction();
        } else { // other key - event will be transfered to actionPerformed() method
            ActionEvent event = new ActionEvent(keyEvent.getSource(), keyEvent.getID(), String.valueOf(keyEvent.getKeyChar()));
            this.actionPerformed(event);
        }
    }

    /***/
    private void resetCalcStateAction() {
        LOG.debug("resetCalcStateAction() working.");
        this.initialState = true;
        this.floatingPoint = false;
        this.calculator.replaceDisplayText("0");
    }

    /***/
    private void backspaceCalcAction() {
        LOG.debug("backspaceCalcAction() working.");
        if (!this.initialState) {
            String text = this.calculator.getDisplayText();
            if (text.length() == 1) { // one symbol on display
                this.initialState = true;
                this.calculator.replaceDisplayText(CalculatorDefaults.DISPLAY_INITIAL_VALUE);
            } else { // more than one symbol on display
                this.calculator.replaceDisplayText(text.substring(0, text.length() - 1));
            }
        }
    }

}