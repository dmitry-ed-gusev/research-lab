package gusev.dmitry.research.swing.ui.image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: mgarin Date: 15.04.11 Time: 19:42
 */

public class ImageResizeComponent extends JComponent {
    public static Color selectionColor = Color.BLUE;
    public static Color selectionButtonColor = new Color(0, 55, 142);
    public static Color selectionBordedColor = Color.WHITE;
    public static Color selectionSelBordedColor = new Color(0, 145, 0);

    public static final int NW_RESIZE = 0;
    public static final int NE_RESIZE = 2;
    public static final int SE_RESIZE = 4;
    public static final int SW_RESIZE = 6;

    private static final int sizerLength = 6;

    private ImageIcon imageIcon = null;
    private Point p1 = null;
    private Point p2 = null;
    private int resizeType = -1;
    private Point dragStartPoint1 = null;
    private Point dragStartPoint2 = null;
    private Point dragStartMousePoint = null;
    private ControlState controlState = ControlState.none;

    public ImageResizeComponent(ImageIcon imageIcon) {
        super();

        this.imageIcon = imageIcon;
        this.p1 = new Point(sizerLength, sizerLength);
        this.p2 = new Point(sizerLength + imageIcon.getIconWidth(),
                sizerLength + imageIcon.getIconHeight());

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int sizerUnderPoint = getSizerUnderPoint(e.getPoint());
                if (sizerUnderPoint != -1) {
                    // Инициируем ресайз
                    controlState = ControlState.resizing;
                    resizeType = sizerUnderPoint;
                    repaint();
                } else if (isImageUnderPoint(e.getPoint())) {
                    // Инициируем передвижение изображения
                    controlState = ControlState.dragging;
                    dragStartPoint1 = new Point(p1);
                    dragStartPoint2 = new Point(p2);
                    dragStartMousePoint = e.getPoint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                if (controlState == ControlState.resizing) {
                    // Ресайзим изображение
                    if (resizeType == NW_RESIZE) {
                        p1.x = e.getX();
                        p1.y = e.getY();
                    } else if (resizeType == NE_RESIZE) {
                        p2.x = e.getX();
                        p1.y = e.getY();
                    } else if (resizeType == SW_RESIZE) {
                        p1.x = e.getX();
                        p2.y = e.getY();
                    } else if (resizeType == SE_RESIZE) {
                        p2.x = e.getX();
                        p2.y = e.getY();
                    }
                    repaint();
                } else if (controlState == ControlState.dragging) {
                    // Передвигаем изображение
                    p1 = new Point(dragStartPoint1.x + e.getX() - dragStartMousePoint.x,
                            dragStartPoint1.y + e.getY() - dragStartMousePoint.y);
                    p2 = new Point(dragStartPoint2.x + e.getX() - dragStartMousePoint.x,
                            dragStartPoint2.y + e.getY() - dragStartMousePoint.y);
                    repaint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                // Завершаем любые действия
                if (controlState == ControlState.resizing) {
                    controlState = ControlState.none;
                    resizeType = -1;
                    repaint();
                } else if (controlState == ControlState.dragging) {
                    controlState = ControlState.none;
                    dragStartPoint1 = null;
                    dragStartPoint2 = null;
                    dragStartMousePoint = null;
                    repaint();
                }
            }

            public void mouseMoved(MouseEvent e) {
                // Устанавливаем соответствующий координате курсор
                int sizer = getSizerUnderPoint(e.getPoint());
                if (sizer != -1) {
                    if (sizer == NW_RESIZE) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    } else if (sizer == NE_RESIZE) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    } else if (sizer == SW_RESIZE) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    } else if (sizer == SE_RESIZE) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    }
                } else if (isImageUnderPoint(e.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private int getSizerUnderPoint(Point p) {
        // Возвращаем тип ресайзера в точке
        if (hitsPoint(p1.x, p1.y, p)) {
            return NW_RESIZE;
        } else if (hitsPoint(p2.x, p1.y, p)) {
            return NE_RESIZE;
        } else if (hitsPoint(p1.x, p2.y, p)) {
            return SW_RESIZE;
        } else if (hitsPoint(p2.x, p2.y, p)) {
            return SE_RESIZE;
        }
        return -1;
    }

    private boolean hitsPoint(int x, int y, Point p) {
        return x - sizerLength / 2 <= p.x && p.x <= x + sizerLength / 2 &&
                y - sizerLength / 2 <= p.y && p.y <= y + sizerLength / 2;
    }

    private boolean isImageUnderPoint(Point p) {
        // Возвращаем true если изображение находится под точкой
        return new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y)).contains(p);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imageIcon != null) {
            Graphics2D g2d = (Graphics2D) g;

            // Самое лучшее качество рендеринга
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            // Позволяет избежать искажения изображения при различных непропорциональных ресайзах
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Изображение
            g2d.drawImage(imageIcon.getImage(), p1.x, p1.y, p2.x - p1.x, p2.y - p1.y, null);

            // Выделение
            g2d.setPaint(selectionColor);
            g2d.drawRect(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                    Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));

            // Ресайзеры
            g2d.setPaint(selectionButtonColor);
            g2d.fillRect(p1.x - 4, p1.y - 4, sizerLength, sizerLength);
            g2d.fillRect(p2.x - 3, p1.y - 4, sizerLength, sizerLength);
            g2d.fillRect(p1.x - 4, p2.y - 3, sizerLength, sizerLength);
            g2d.fillRect(p2.x - 3, p2.y - 3, sizerLength, sizerLength);
            g2d.setPaint(selectionBordedColor);
            g2d.drawRect(p1.x - 4, p1.y - 4, sizerLength, sizerLength);
            g2d.drawRect(p2.x - 3, p1.y - 4, sizerLength, sizerLength);
            g2d.drawRect(p1.x - 4, p2.y - 3, sizerLength, sizerLength);
            g2d.drawRect(p2.x - 3, p2.y - 3, sizerLength, sizerLength);
            if (controlState == ControlState.resizing) {
                g2d.setPaint(selectionSelBordedColor);
                if (resizeType == NW_RESIZE) {
                    g2d.drawRect(p1.x - 5, p1.y - 5, 8, 8);
                }
                if (resizeType == NE_RESIZE) {
                    g2d.drawRect(p2.x - 4, p1.y - 5, 8, 8);
                }
                if (resizeType == SW_RESIZE) {
                    g2d.drawRect(p1.x - 5, p2.y - 4, 8, 8);
                }
                if (resizeType == SE_RESIZE) {
                    g2d.drawRect(p2.x - 4, p2.y - 4, 8, 8);
                }
            }
        }
    }

    private enum ControlState {
        // Типы действий
        none,
        resizing,
        dragging
    }

    public Dimension getPreferredSize() {
        // Исходный предпочитаемый размер компонента
        return imageIcon != null ? new Dimension(imageIcon.getIconWidth() + sizerLength * 2,
                imageIcon.getIconHeight() + sizerLength * 2) : super.getPreferredSize();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();

        f.getRootPane().setOpaque(true);
        f.getRootPane().setBackground(Color.WHITE);
        f.getRootPane().setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().setBackground(Color.WHITE);

        f.getContentPane().add(new ImageResizeComponent(
                new ImageIcon(ImageResizeComponent.class.getResource("icons/image.png"))),
                BorderLayout.CENTER);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
