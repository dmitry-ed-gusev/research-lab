package gusev.dmitry.jtils.watch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple watch for time counting. Watch are intended mostly for presentations, but may be used for other purposes.
 * Some properties of watch:
 *  - main window isn't resizeable, window size set up by constants
 *
 * Lines of one digital indicator:
 *   TOP - top horizontal line in a digit,       presense flag = 1
 *   UPL - upper left vertical line in a digit,  presense flag = 2
 *   UPR - upper right vertical line in a digit, presense flag = 4
 *   MID - middle horizontal line in a digit,    presense flag = 8
 *   LOL - lower left vertical line in a digit,  presense flag = 16
 *   LOR - lower right vertical line in a digit, presense flag = 32
 *   LOW - low horizontal line in a digit,       presense flag = 64
 *
 * One digit contents (digital indicator contents these lines):
 *   (0) zero  - TOP,UPL,UPR,LOL,LOR,LOW - flags sum = 119
 *   (1) one   - UPR,LOR                 - flags sum = 36
 *   (2) two   - TOP,UPR,MID,LOL,LOW     - flags sum = 93
 *   (3) three - TOP,UPR,MID,LOR,LOW     - flags sum = 109
 *   (4) four  - UPL,MID,UPR,LOR         - flags sum = 46
 *   (5) five  - TOP,UPL,MID,LOR,LOW     - flags sum = 107
 *   (6) six   - TOP,UPL,MID,LOL,LOR,LOW - flags sum = 123
 *   (7) seven - TOP,UPR,LOR             - flags sum = 37
 *   (8) eight - all                     - flags sum = 127
 *   (9) nine  - TOP,UPL,UPR,MID,LOR,LOW - flags sum = 111
 *
 * @author Gusev Dmitry, 2014
 * @version 2.0
*/
public class PresentationWatch extends JPanel {

    // some parameters for digits (sizes, distances, etc)
    private static final double           CUT_SIZE           = 5;                            // one of params for digit (internal)
    private static final int              WINDOW_WIDTH       = 250;                          // main window width
    private static final int              WINDOW_HEIGHT      = 100;                          // main window height

    private static final Dimension        WINDOW_SIZE        = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT); // main window size
    private static final Dimension        BUTTON_SIZE        = new Dimension(25, 25);        // tool buttun size
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("mmss"); // date format for time
    private static final Color            DISPLAY_COLOR      = Color.BLACK;                  // display color
    private static final Color            DIGITS_COLOR       = Color.GREEN;                  // digits color


    /***/
    private static enum DIGIT_LINES { // digital indicator lines
        TOP(1), UPL(2), UPR(4), MID(8), LOL(16), LOR(32), LOW(64);

        private DIGIT_LINES(int flagValue) {
            this.flagValue = flagValue;
        }

        private int flagValue;
    }

    /***/
    private static enum DIGITS { // digits
        ZERO(119), ONE(36), TWO(93), THREE(109), FOUR(46), FIVE(107), SIX(123), SEVEN(37), EIGHT(127), NINE(111);

        private DIGITS(int value) { // enum constructor
            this.value = value;
        }

        private int                         value;            //
        private static Map<String, Integer> digitsMap = null; //

        /***/
        public static int getFlagsForDigit(String digit) {
            if (DIGITS.digitsMap == null) { // lazy initialization for digits map
                DIGITS.digitsMap = new HashMap<>();
                for (int i = 0; i < DIGITS.values().length; i++) {
                    DIGITS.digitsMap.put(String.valueOf(i), DIGITS.values()[i].value);
                }
            } // end of lazy initialization
            return DIGITS.digitsMap.get(digit);
        }

    }

    private int[] currentTimeFlags = new int[4]; // 4-elements array with flags for every digit (for current time)
    private long  count            = 0;          // time counter
    private Point initialClick     = null;       // first (initial) mouse button click coords (for moving "undecorated app window")

    /** Command button for Watch - it inherits from classic JButton. */
    private static class WatchButton extends JButton {
        public WatchButton(String iconName, ActionListener listener) {
            super();
            this.setIcon(new ImageIcon(getClass().getResource("icons/" + iconName)));
            this.setFocusable(false);
            this.setPreferredSize(BUTTON_SIZE);
            this.addActionListener(listener);
        }
    }

    /***/
    private static JPanel getMainAppPanel(final JFrame mainFrame) {

        // create watch panel (panel with digits)
        final PresentationWatch watch = new PresentationWatch();
        watch.setBackground(DISPLAY_COLOR);
        watch.setPreferredSize(WINDOW_SIZE);

        // Mouse listener for "mouse button click event" - getting component at cursor
        watch.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                watch.initialClick = e.getPoint();
                watch.getComponentAt(watch.initialClick);
            }
        });

        // Mouse motion listener for "mouse dragged event" - move app window at new position
        watch.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // get current location of Window
                int thisX = mainFrame.getLocation().x;
                int thisY = mainFrame.getLocation().y;
                // Determine how much the mouse moved since the initial click
                int xMoved = (thisX + e.getX()) - (thisX + watch.initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + watch.initialClick.y);
                // Move window to this position
                mainFrame.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // timer initialization with action listener
        final Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String currentTime = SIMPLE_DATE_FORMAT.format(new Date(watch.count++ * 1000));
                // get flags for current time (for every digit)
                watch.currentTimeFlags[0] = DIGITS.getFlagsForDigit(currentTime.substring(0, 1));
                watch.currentTimeFlags[1] = DIGITS.getFlagsForDigit(currentTime.substring(1, 2));
                watch.currentTimeFlags[2] = DIGITS.getFlagsForDigit(currentTime.substring(2, 3));
                watch.currentTimeFlags[3] = DIGITS.getFlagsForDigit(currentTime.substring(3));
                watch.repaint(); // call to repaint()->internally calls paintComponent()
            }
        });

        // preparing buttons for toolbox panel
        WatchButton startButton = new WatchButton("play.png", new ActionListener() { // start button
            public void actionPerformed(ActionEvent e) {
                timer.start();
            }
        });
        WatchButton stopButton = new WatchButton("pause.png", new ActionListener() { // pause button
            public void actionPerformed(ActionEvent e) {
                timer.stop();
            }
        });
        WatchButton resetButton = new WatchButton("reset.png", new ActionListener() { // reset button
            public void actionPerformed(ActionEvent e) {
                if (!timer.isRunning()) { // reset all digits to ZERO
                    watch.currentTimeFlags[0] = 119;
                    watch.currentTimeFlags[1] = 119;
                    watch.currentTimeFlags[2] = 119;
                    watch.currentTimeFlags[3] = 119;
                    watch.count = 0;
                    watch.repaint();
                }
            }
        });
        WatchButton exitButton = new WatchButton("delete.png", new ActionListener() { // exit app button
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
            }
        });

        // creating toolbox panel and adding tool buttons
        JPanel toolboxPanel = new JPanel();
        toolboxPanel.setLayout(new BoxLayout(toolboxPanel, BoxLayout.Y_AXIS));
        toolboxPanel.add(startButton);
        toolboxPanel.add(stopButton);
        toolboxPanel.add(resetButton);
        toolboxPanel.add(exitButton);

        // initial digits flags (timer initial state)
        watch.currentTimeFlags[0] = 119;
        watch.currentTimeFlags[1] = 119;
        watch.currentTimeFlags[2] = 119;
        watch.currentTimeFlags[3] = 119;

        // create main app panl and add display and toolbox panels to main panel
        JPanel mainAppPanel = new JPanel(new BorderLayout());
        mainAppPanel.add(watch, BorderLayout.CENTER);
        mainAppPanel.add(toolboxPanel, BorderLayout.EAST);

        return mainAppPanel;

    } // end of main panel constructor

    @Override // todo: code below needs some refactoring
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // turn ON antialiasing

        double
                //width = getWidth(),
                //height = getHeight(),
                vertMargin = .08 * WINDOW_HEIGHT,
                glyphWidth = .125 * WINDOW_WIDTH,
                horzPad = glyphWidth / 2,
                glyphHeight = .375 * WINDOW_HEIGHT;

        double
                x = glyphWidth,
                x1 = x + glyphWidth,
                y1 = vertMargin,
                y3 = y1 + CUT_SIZE + glyphHeight,
                y5 = y3 + CUT_SIZE + glyphHeight;

        g2.setColor(DIGITS_COLOR);         // set digits color
        g2.setStroke(new BasicStroke(5)); // size (line width) for colon (:) between minutes and seconds
        // draw colon (:) between minutes and seconds
        g2.draw(new Line2D.Double(x + 3*glyphWidth + CUT_SIZE/2, y1 + glyphHeight/2, x + 3*glyphWidth + CUT_SIZE/2, y1 + glyphHeight/2));
        g2.draw(new Line2D.Double(x + 3*glyphWidth + CUT_SIZE/2, y1 + glyphHeight*7/4, x + 3*glyphWidth + CUT_SIZE/2, y1 + glyphHeight*7/4));

        g2.setStroke(new BasicStroke(4)); // line width for digits
        int flags; // current processing digit flags
        // iterates over current time flags array and draw every line for every digit
        for (int j = 0; j < 4; j++) {
            flags = currentTimeFlags[j];
            if (j == 2) { // add distance from colon for seconds digits
                x += glyphWidth - horzPad;
                x1 += glyphWidth - horzPad;
            }
            // draw digit lines depending on flags for this digit
            if ((flags & DIGIT_LINES.TOP.flagValue) == DIGIT_LINES.TOP.flagValue)
                g2.draw(new Line2D.Double(x + CUT_SIZE, y1, x + glyphWidth, y1));   // 1
            if ((flags & DIGIT_LINES.UPL.flagValue) == DIGIT_LINES.UPL.flagValue)
                g2.draw(new Line2D.Double(x, y1 + CUT_SIZE, x, y1 + glyphHeight));  // 2_L
            if ((flags & DIGIT_LINES.UPR.flagValue) == DIGIT_LINES.UPR.flagValue)
                g2.draw(new Line2D.Double(x1 + CUT_SIZE, y1 + CUT_SIZE, x1 + CUT_SIZE, y1 + glyphHeight));        // 2_R
            if ((flags & DIGIT_LINES.MID.flagValue) == DIGIT_LINES.MID.flagValue)
                g2.draw(new Line2D.Double(x + CUT_SIZE, y3, x + glyphWidth, y3));   // 3
            if ((flags & DIGIT_LINES.LOL.flagValue) == DIGIT_LINES.LOL.flagValue)
                g2.draw(new Line2D.Double(x, y3 + CUT_SIZE, x, y3 + glyphHeight));  // 4_L
            if ((flags & DIGIT_LINES.LOR.flagValue) == DIGIT_LINES.LOR.flagValue)
                g2.draw(new Line2D.Double(x1 + CUT_SIZE, y3 + CUT_SIZE, x1 + CUT_SIZE, y3 + glyphHeight));        // 4_R
            if ((flags & DIGIT_LINES.LOW.flagValue) == DIGIT_LINES.LOW.flagValue)
                g2.draw(new Line2D.Double(x + CUT_SIZE, y5, x + glyphWidth, y5));   // 5
            x += horzPad + glyphWidth;
            x1 += horzPad + glyphWidth;
        }
    }

    /***/
    public static void startWatch() {
        JFrame mainAppFrame = new JFrame("Stopwatch"); // new app frame - main window
        mainAppFrame.getContentPane().add(PresentationWatch.getMainAppPanel(mainAppFrame));
        mainAppFrame.setAlwaysOnTop(true);        // always on top
        mainAppFrame.setUndecorated(true);        // no decorations - classic window title etc
        mainAppFrame.pack();                      // pack GUI
        mainAppFrame.setLocationRelativeTo(null); // put new app window at the screen center
        mainAppFrame.setVisible(true);            // set main app frame visible (start app)
    }

    /** just for test */
    public static void main(String[] args) {
        PresentationWatch.startWatch();
    }

}