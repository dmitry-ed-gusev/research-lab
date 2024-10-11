package gusev.dmitry.research.swing.animated;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

/**
 * Dialog window with animated picture (GIF).
 * @author Shakirov Marat, revised by Gusev Dmitry.
 * @version 2.0 22.04.2008
*/

public class AnimatedDialog extends JDialog implements /*PropertyChangeListener,*/ Serializable {

    // module logger
    private Log log = LogFactory.getLog(AnimatedDialog.class);

    // always on top?
    private boolean isAlwaysOnTop = true;

    // panel with content
    //private JPanel contentPane;

    /**
     * Constructor with reference to parent frame.
     * @param owner Frame parent frame reference. This reference shouldn't be equal null - child frame (this dialog)
     *              always should have a parent - for correct display. If this reference will be equal to null, parent
     *              frame can be invisible (overlayed by other window) but this child dialog can be visible - inconsistent
     *              state.
     * @throws java.awt.HeadlessException
     */
    public AnimatedDialog(Frame owner, String iconPath, String text, boolean isAlwaysOnTop) /*throws HeadlessException*/ {
        super(owner);
        log.debug("AnimatedDialog() constructor working.");

        // check parameters
        if (owner == null) { // check reference to parent frame - it can't be null!
            throw new IllegalArgumentException("Reference to parent frame can't be null!");
        } else if (StringUtils.isBlank(iconPath)) { // icon path should be provided
            throw new IllegalArgumentException("Path to dialog icon is empty!");
        } else if (StringUtils.isBlank(text)) { // dialog text should be provided
            throw new IllegalArgumentException("Dialog text is empty!");
        }

        this.isAlwaysOnTop = isAlwaysOnTop;
        this.setUndecorated(true); // no decoration elements
        this.getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // border

        // This dialog should be modal - otherwise parent window will be
        // accesssible during this dialog show
        this.setModal(true);

        //this.initComponents();

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout());
        JLabel lb = new JLabel();
        //lb.setIcon(new ImageIcon(getClass().getResource("/storm/swing/Dialogs/img/ship.gif")));
        //lb.setIcon(new ImageIcon(getClass().getResource("img/ship.gif")));
        //lb.setText("<html>Подождите,<br>подготавливаются данные...");
        lb.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        lb.setText(text);
        lb.setBackground(Color.WHITE);
        contentPane.add(lb);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setContentPane(contentPane);
        this.pack();
        this.setResizable(false);
    }

    /**
     * конструктор без параметров
     * @throws java.awt.HeadlessException сключения
     */
    /*
    public AnimatedTimedDialog() throws HeadlessException {
        super();
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        //setModal(true);
        initComponents();
        setContentPane(contentPane);
        pack();
        setResizable(false);
        Logger logger = Logger.getLogger("storm");
        logger.debug("TimeDialog constructor ends");
    }
    */


    /**
     * инициализация компонентов окна
     */
    /*
    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout());
        JLabel lb = new JLabel();
        //lb.setIcon(new ImageIcon(getClass().getResource("/storm/swing/Dialogs/img/ship.gif")));
        lb.setIcon(new ImageIcon(getClass().getResource("img/ship.gif")));
        lb.setText("<html>Подождите,<br>подготавливаются данные...");
        lb.setBackground(Color.WHITE);
        contentPane.add(lb);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    }
    */

    /**
     * переопределяем метод pack() для вывода окна посередине экрана
     */
    public void pack() {
        log.debug("AnimatedDialog.pack() working.");
        // if isAlwaysOnTop - we will be on top :)
        if (this.isAlwaysOnTop) {
            log.debug("Always on top is TRUE.");
            this.setAlwaysOnTop(true);  // always on top (this dialog will be on top, not parent window)
            // we need this adapter to bring our dialog to fron even if window lost focus
            this.addWindowListener(new WindowAdapter() {
                public void windowDeactivated(WindowEvent e) {
                    log.debug("WindowListener.windowDeactivated() working.");
                    toFront();
                }
            });
        }

        // parent method pack()
        super.pack();
        // center position of dialog (on screen)
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    /**
     * слушатель
     * @param evt событие
     */
    public void propertyChange(final PropertyChangeEvent evt) {
        log.debug("property changed!");
        if ((evt.getPropertyName().equalsIgnoreCase("showTime")) &&
                (evt.getNewValue().toString().equalsIgnoreCase("true"))) {
            log.debug("true");
            new Thread(new Runnable() {
                public void run() {
                    setVisible(true);
                }
            }).start();
        }
        if ((evt.getPropertyName().equalsIgnoreCase("showTime")) &&
                (evt.getNewValue().toString().equalsIgnoreCase("false"))) {
            log.debug("false");
            setVisible(false);
        }

    }

    public void on() {
        log.debug("AnimatedDialog.on().");
        // we should do this in other thread - if we do this action in current thread it will be locked
        // until we can hide this dialog window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
            }
        });
    }

    public void off() {
        log.debug("AnimatedDialog.off().");
        this.setVisible(false);
    }

    public static void main(String[] args) {
        AnimatedDialog dlg = new AnimatedDialog(null, "", "", true);
        dlg.pack();
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setVisible(true);
    }

}