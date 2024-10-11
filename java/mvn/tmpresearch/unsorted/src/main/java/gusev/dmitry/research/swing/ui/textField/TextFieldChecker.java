package gusev.dmitry.research.swing.ui.textField;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.GeneralPath;

/**
 * User: mgarin Date: 13.04.11 Time: 14:02
 */

public class TextFieldChecker {
    public static void main(String[] args) {
        try {
            // Устанавливаем нативный стиль компонентов
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            //
        }

        JFrame f = new JFrame();

        f.getRootPane().setOpaque(true);
        f.getRootPane().setBackground(Color.WHITE);
        f.getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        f.getContentPane().setLayout(new BorderLayout(5, 5));
        f.getContentPane().setBackground(Color.WHITE);

        f.getContentPane().add(new JLabel("Введите Ваше имя:"), BorderLayout.WEST);

        // Создаем расширенный JTextField
        JTextField field = new JTextField() {
            private boolean lostFocusOnce = false;
            private boolean incorrect = false;

            {
                // Слушатели для обновления состояния проверки
                addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        lostFocusOnce = true;
                        incorrect = getText().trim().equals("");
                        repaint();
                    }
                });
                addCaretListener(new CaretListener() {
                    public void caretUpdate(CaretEvent e) {
                        if (lostFocusOnce) {
                            incorrect = getText().trim().equals("");
                        }
                    }
                });
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Расширенная отрисовка при некорректности данных
                if (incorrect) {
                    Graphics2D g2d = (Graphics2D) g;

                    // Включаем антиалиасинг для гладкой отрисовки
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    // Получаем отступы внутри поля
                    Insets insets;
                    if (getBorder() == null) {
                        insets = new Insets(2, 2, 2, 2);
                    } else {
                        insets = getBorder().getBorderInsets(this);
                    }

                    // Создаем фигуру в виде подчеркивания текста
                    GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                    gp.moveTo(insets.left, getHeight() - insets.bottom);
                    for (int i = 0; i < getWidth() - insets.right - insets.left; i += 3) {
                        gp.lineTo(insets.left + i,
                                getHeight() - insets.bottom - ((i / 3) % 2 == 1 ? 2 : 0));
                    }

                    // Отрисовываем её красным цветом
                    g2d.setPaint(Color.RED);
                    g2d.draw(gp);
                }
            }
        };
        field.setColumns(14);
        f.getContentPane().add(field, BorderLayout.CENTER);

        f.getContentPane().add(new JButton("Ok") {
            {
                setOpaque(false);
            }
        }, BorderLayout.SOUTH);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}