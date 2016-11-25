package gusev.dmitry.jtils.calc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.10.12)
*/

public class Calculator {

    private Log log = LogFactory.getLog(Calculator.class);

    // calculator display
    private JTextField calcDisplay;

    public Calculator() {

        log.debug("Calculator UI creating.");

        // Calculator engine instance - for performing actions
        CalculatorEngine engine = new CalculatorEngine(this);
        // initializing main frame (JFrame)
        JFrame mainCalcFrame = new JFrame(Consts.APP_TITLE);

        // main panel-container
        JPanel mainCalcPanel = new JPanel();
        // layout manager for main panel
        BorderLayout bl = new BorderLayout();
        bl.setHgap(4); // H-gaps between panels (parts of layout)
        bl.setVgap(4); // V-gaps between panels (parts of layout)
        mainCalcPanel.setLayout(bl);

        // calc display
        calcDisplay = new JTextField(Consts.DISPLAY_SIZE);
        calcDisplay.setEditable(false);  // field not editable
        calcDisplay.setHorizontalAlignment(JTextField.RIGHT); // text aligned to right side
        mainCalcPanel.add(BorderLayout.NORTH, calcDisplay);
        // key listener for display - all keys events will be transferred to display component
        calcDisplay.addKeyListener(engine);

        // initial text for display
        calcDisplay.setText(Consts.DISPLAY_INITIAL_VALUE);

        // digital buttons panel
        JPanel buttonsPanel = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        buttonsPanel.setLayout(gb);
        // GridBagConstraints instance
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1;
        gbc.gridwidth = 1; // Cell width - in cells units. This cell is as wide as 1 other one.
        gbc.fill = GridBagConstraints.BOTH; // Fill all space in the cell
        //gbc.weightx = 2.0; // proportion of horizontal space taken by this component
        //gbc.weighty = 1.0; // proportion of vertical space taken by this component
        gbc.anchor = GridBagConstraints.CENTER; // position of the component within the cell

        gbc.insets = new Insets(1, 1, 5, 1); // spacing between cells in four dimensions
        // array of action buttons - MC, MR, MS, M+, M-, ← (Alt-27), CE, C, ±, √ (Alt-251)
        String[] specButtons = {CalculatorEngine.CalcAction.MC.getValue(), CalculatorEngine.CalcAction.MR.getValue(), CalculatorEngine.CalcAction.MS.getValue(),
                CalculatorEngine.CalcAction.MPLUS.getValue(), CalculatorEngine.CalcAction.MMINUS.getValue(), CalculatorEngine.CalcEraseAction.BACKSPACE.getValue(),
                CalculatorEngine.CalcEraseAction.ERASE_CURRENT.getValue(), CalculatorEngine.CalcEraseAction.ERASE_ALL.getValue(),
                CalculatorEngine.CalcAction.SIGN.getValue(), CalculatorEngine.CalcAction.SQRT.getValue()};
        for (int i = 0; i < specButtons.length; i++ ) {
            gbc.gridx = (i + 1)%5 == 0 ? 5 : (i + 1)%5;
            gbc.gridy = i/5 + 1;
            CalcButton specButton = new CalcButton(specButtons[i], engine);
            gb.setConstraints(specButton, gbc);
            buttonsPanel.add(specButton);
        }

        // arithmetic actions buttons
        gbc.insets = new Insets(1, 5, 1, 1);
        gbc.gridx = 4;
        CalculatorEngine.CalcAction[] actionButtons = {CalculatorEngine.CalcAction.DIV, CalculatorEngine.CalcAction.MUL, CalculatorEngine.CalcAction.SUB, CalculatorEngine.CalcAction.ADD};
        for (int i = 0; i < actionButtons.length; i++) {
            gbc.gridy = i + 3;
            CalcButton actionButton = new CalcButton(actionButtons[i].getValue(), engine);
            gb.setConstraints(actionButton, gbc);
            buttonsPanel.add(actionButton);
        }

        // Buttons %, 1/x and =
        gbc.gridx = 5;
        gbc.insets = new Insets(1, 1, 1, 1);
        // Button %
        gbc.gridy = 3;
        CalcButton percentButton = new CalcButton(CalculatorEngine.CalcAction.PERCENT.getValue(), engine);
        gb.setConstraints(percentButton, gbc);
        buttonsPanel.add(percentButton);
        // Button 1/x
        gbc.gridy = 4;
        CalcButton divByXButton = new CalcButton(CalculatorEngine.CalcAction.ONE_DIV.getValue(), engine);
        gb.setConstraints(divByXButton, gbc);
        buttonsPanel.add(divByXButton);
        // Button =
        gbc.gridy = 5;
        gbc.gridheight = 2;
        CalcButton eqButton = new CalcButton(CalculatorEngine.CalcAction.EQUALS.getValue(), engine);
        gb.setConstraints(eqButton, gbc);
        buttonsPanel.add(eqButton);

        // adding digits 1-9
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.gridheight = 1;
        for (int i = 9; i > 0; i--) {
         gbc.gridx = (i%3 == 0 ? 3 : i%3);
         gbc.gridy = (i%3 == 0 ? 4 - i/3 : 3 - i/3) + 2;
         CalcButton button = new CalcButton(String.valueOf(i), engine); // create button
         gb.setConstraints(button, gbc);
         buttonsPanel.add(button);
        }

        // buttons 0(zero) and ,(comma)
        gbc.gridx = 3;
        gbc.gridy = 6;
        CalcButton commaButton = new CalcButton(",", engine);
        gb.setConstraints(commaButton, gbc);
        buttonsPanel.add(commaButton);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        CalcButton zeroButton = new CalcButton("0", engine);  // button
        gb.setConstraints(zeroButton, gbc);
        buttonsPanel.add(zeroButton);

        // main calculator buttons
        mainCalcPanel.add(BorderLayout.CENTER, buttonsPanel);

        // set up main app frame params - set size, position, properties, listeners
        mainCalcFrame.setContentPane(mainCalcPanel);
        mainCalcFrame.pack(); // Set the size of the window big enough to accomodate all controls
        //mainCalcFrame.setSize(300, 100); // set concrete size
        mainCalcFrame.setResizable(false); // user shouldn't resize the window

        // start frame position - screen center
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - mainCalcFrame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - mainCalcFrame.getHeight()) / 2);
        mainCalcFrame.setLocation(x, y);

        // window listener - reaction on closing main window
//        mainCalcFrame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                log.info("Shutting down calculator.");
//                System.exit(0);
//            }
//        });

        // exit program when closed main frame
        mainCalcFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // set initial focus to calc display
        calcDisplay.requestFocusInWindow();
        // last command - make main frame visible
        mainCalcFrame.setVisible(true);

    } // end of constructor

    public void addDisplayText(String text) {
        log.debug("Adding display text [" + text + "].");
        if (!StringUtils.isBlank(text)) {
            this.calcDisplay.setText(this.calcDisplay.getText() + text);
        }
    }

    public void replaceDisplayText(String text) {
        log.debug("Replacing display text with value [" + text + "].");
        if (!StringUtils.isBlank(text)) {
            this.calcDisplay.setText(text);
        }
    }

    public String getDisplayText() {
        if (this.calcDisplay == null) {
            throw new IllegalStateException("Calculator display component can't be null!");
        }
        return this.calcDisplay.getText();
    }

    public static void main(String[] args) {

        Log log = LogFactory.getLog(Calculator.class);
        log.info("Starting calculator.");

        // instantiating calculator
        Calculator calc = new Calculator();

    }
}