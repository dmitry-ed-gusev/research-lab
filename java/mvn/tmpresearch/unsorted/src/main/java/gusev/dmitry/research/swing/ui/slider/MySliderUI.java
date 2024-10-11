package gusev.dmitry.research.swing.ui.slider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * User: mgarin Date: 29.03.11 Time: 20:34
 */

public class MySliderUI extends BasicSliderUI {
    public static final ImageIcon BG_LEFT_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/bg_left.png"));
    public static final ImageIcon BG_MID_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/bg_mid.png"));
    public static final ImageIcon BG_RIGHT_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/bg_right.png"));
    public static final ImageIcon BG_FILL_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/bg_fill.png"));

    public static final ImageIcon VER_BG_LEFT_ICON = createTurnedIcon(BG_LEFT_ICON);
    public static final ImageIcon VER_BG_MID_ICON = createTurnedIcon(BG_MID_ICON);
    public static final ImageIcon VER_BG_RIGHT_ICON = createTurnedIcon(BG_RIGHT_ICON);
    public static final ImageIcon VER_BG_FILL_ICON = createTurnedIcon(BG_FILL_ICON);

    public static final ImageIcon GRIPPER_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/gripper.png"));
    public static final ImageIcon GRIPPER_PRESSED_ICON =
            new ImageIcon(MySliderUI.class.getResource("icons/gripper_pressed.png"));

    public MySliderUI(final JSlider b) {
        super(b);

        // Для корректной перерисовки слайдера
        b.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                b.repaint();
            }
        });
        b.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                b.repaint();
            }

            public void mouseReleased(MouseEvent e) {
                b.repaint();
            }
        });
    }

    // Возвращаем новый размер гриппера
    protected Dimension getThumbSize() {
        return new Dimension(GRIPPER_ICON.getIconWidth(), GRIPPER_ICON.getIconHeight());
    }

    // Отрисовываем сам гриппер в необходимом месте
    public void paintThumb(Graphics g) {
        int positionX = thumbRect.x + thumbRect.width / 2;
        int positionY = thumbRect.y + thumbRect.height / 2;
        g.drawImage(isDragging() ? GRIPPER_PRESSED_ICON.getImage() : GRIPPER_ICON.getImage(),
                positionX - GRIPPER_ICON.getIconWidth() / 2,
                positionY - GRIPPER_ICON.getIconHeight() / 2, null);
    }

    // Отрисовываем «путь» слайдера
    public void paintTrack(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            // Сам путь
            g.drawImage(BG_LEFT_ICON.getImage(), trackRect.x,
                    trackRect.y + trackRect.height / 2 - BG_LEFT_ICON.getIconHeight() / 2, null);
            g.drawImage(BG_MID_ICON.getImage(), trackRect.x + BG_LEFT_ICON.getIconWidth(),
                    trackRect.y + trackRect.height / 2 - BG_MID_ICON.getIconHeight() / 2,
                    trackRect.width - BG_LEFT_ICON.getIconWidth() - BG_RIGHT_ICON.getIconWidth(),
                    BG_MID_ICON.getIconHeight(), null);
            g.drawImage(BG_RIGHT_ICON.getImage(),
                    trackRect.x + trackRect.width - BG_RIGHT_ICON.getIconWidth(),
                    trackRect.y + trackRect.height / 2 - BG_RIGHT_ICON.getIconHeight() / 2, null);

            // Заполнение участка пути слева от гриппера
            g.drawImage(BG_FILL_ICON.getImage(), trackRect.x + 1,
                    trackRect.y + trackRect.height / 2 - BG_FILL_ICON.getIconHeight() / 2,
                    thumbRect.x + thumbRect.width / 2 - trackRect.x - BG_LEFT_ICON.getIconWidth(),
                    BG_FILL_ICON.getIconHeight(), null);
        } else {
            // Сам путь
            g.drawImage(VER_BG_LEFT_ICON.getImage(),
                    trackRect.x + trackRect.width / 2 - VER_BG_LEFT_ICON.getIconWidth() / 2,
                    trackRect.y, null);
            g.drawImage(VER_BG_MID_ICON.getImage(),
                    trackRect.x + trackRect.width / 2 - VER_BG_LEFT_ICON.getIconWidth() / 2,
                    trackRect.y + VER_BG_LEFT_ICON.getIconHeight(),
                    VER_BG_MID_ICON.getIconWidth(),
                    trackRect.height - VER_BG_LEFT_ICON.getIconHeight() -
                            VER_BG_RIGHT_ICON.getIconHeight(), null);
            g.drawImage(VER_BG_RIGHT_ICON.getImage(),
                    trackRect.x + trackRect.width / 2 - VER_BG_LEFT_ICON.getIconWidth() / 2,
                    trackRect.y + trackRect.height - VER_BG_RIGHT_ICON.getIconHeight(), null);

            // Заполнение участка пути сверху от гриппера
            g.drawImage(VER_BG_FILL_ICON.getImage(),
                    trackRect.x + trackRect.width / 2 - VER_BG_LEFT_ICON.getIconWidth() / 2 + 1,
                    trackRect.y + 1, VER_BG_FILL_ICON.getIconWidth(),
                    thumbRect.y + thumbRect.height / 2 - trackRect.y -
                            VER_BG_LEFT_ICON.getIconHeight(), null);
        }
    }

    private static ImageIcon createTurnedIcon(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconHeight(), icon.getIconWidth(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.translate(icon.getIconHeight(), 0);
        g2d.rotate(Math.PI / 2);
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();
        return new ImageIcon(bi);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Пример MySliderUI");

        JSlider slider = new JSlider();
        slider.setUI(new MySliderUI(slider));
        slider.setOpaque(true);
        slider.setBackground(Color.WHITE);
        slider.setSnapToTicks(true);
        slider.setMinimum(0);
        slider.setMaximum(10);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(slider);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
