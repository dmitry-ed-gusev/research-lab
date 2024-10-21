package gusev.dmitry.research.swing.ui.dnd;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * User: mgarin Date: 18.04.11 Time: 13:05
 */

public class DndExample {
    private static final String panel1 = "panel1";

    private static Dimension phase = null;
    private static BufferedImage dragged = null;

    public static void main(String[] args) {
        try {
            // Устанавливаем нативный стиль компонентов
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            //
        }

        final JFrame f = new JFrame();

        f.getRootPane().setOpaque(true);
        f.getRootPane().setBackground(Color.WHITE);

        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().setBackground(Color.WHITE);


        // Контейнер в котором будет происходить ДнД
        final JPanel dragContainer = new JPanel();
        dragContainer.setOpaque(false);
        dragContainer.setLayout(null);
        f.getContentPane().add(dragContainer, BorderLayout.CENTER);


        // Перетаскиваемая панель
        final JPanel draggablePanel = new JPanel();
        draggablePanel.setBorder(BorderFactory
                .createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        draggablePanel.setLayout(new BorderLayout(4, 4));
        draggablePanel.add(new JLabel("Drag me somewhere!", JLabel.CENTER) {
            {
                setTransferHandler(new TransferHandler() {
                    public int getSourceActions(JComponent c) {
                        return TransferHandler.MOVE;
                    }

                    public boolean canImport(TransferSupport support) {
                        // Для "прозрачности" панели при сбросе ДнД
                        // Позволяет располагать панель даже когда курсор не над dragContainer'ом
                        return dragContainer.getTransferHandler().canImport(support);
                    }

                    public boolean importData(TransferSupport support) {
                        // Для "прозрачности" панели при сбросе ДнД
                        // Позволяет располагать панель даже когда курсор не над dragContainer'ом
                        return dragContainer.getTransferHandler().importData(support);
                    }

                    protected Transferable createTransferable(JComponent c) {
                        return new StringSelection(panel1);
                    }
                });
                addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // Для корректной вставки панели позднее
                            Point los = draggablePanel.getLocationOnScreen();
                            phase = new Dimension(e.getLocationOnScreen().x - los.x,
                                    e.getLocationOnScreen().y - los.y);

                            // Для отрисовки перетаскиваемого образа
                            dragged = new BufferedImage(draggablePanel.getWidth(),
                                    draggablePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = dragged.createGraphics();
                            g2d.setComposite(
                                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                            draggablePanel.paintAll(g2d);
                            g2d.dispose();

                            JComponent c = (JComponent) e.getSource();
                            TransferHandler handler = c.getTransferHandler();
                            handler.exportAsDrag(c, e, TransferHandler.MOVE);
                        }
                    }
                });
            }
        });
        draggablePanel.add(new JButton("Some custom button"), BorderLayout.SOUTH);
        draggablePanel.setBounds(25, 25, 200, 100);
        dragContainer.add(draggablePanel);


        // Слушатель для корректного расположния панели при её сбросе
        dragContainer.setTransferHandler(new TransferHandler() {
            public int getSourceActions(JComponent c) {
                return TransferHandler.NONE;
            }

            public boolean canImport(TransferSupport support) {
                try {
                    return support.getTransferable().getTransferData(DataFlavor.stringFlavor) !=
                            null;
                } catch (Throwable e) {
                    return false;
                }
            }

            public boolean importData(TransferHandler.TransferSupport info) {
                if (info.isDrop()) {
                    try {
                        String panelId = (String) info.getTransferable()
                                .getTransferData(DataFlavor.stringFlavor);
                        Point dropPoint = info.getDropLocation().getDropPoint();
                        if (panelId.equals(panel1)) {
                            draggablePanel.setLocation(dropPoint.x - phase.width,
                                    dropPoint.y - phase.height);
                            draggablePanel.revalidate();
                        }
                        return true;
                    } catch (Throwable e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });

        final ImageGlassPane glassPane = new ImageGlassPane();
        f.setGlassPane(glassPane);

        // Слушатель для перерисовки перетаскиваемого образа
        DragSourceAdapter dsa = new DragSourceAdapter() {
            public void dragEnter(DragSourceDragEvent dsde) {
                updateGlassPane(dsde);
            }

            public void dragMouseMoved(DragSourceDragEvent dsde) {
                updateGlassPane(dsde);
            }

            private void updateGlassPane(DragSourceDragEvent dsde) {
                glassPane.setImage(dragged);
                glassPane.setPoint(new Point(
                        dsde.getLocation().x - dragContainer.getLocationOnScreen().x -
                                phase.width,
                        dsde.getLocation().y - dragContainer.getLocationOnScreen().y -
                                phase.height));
            }

            public void dragDropEnd(DragSourceDropEvent dsde) {
                glassPane.setImage(null);
                glassPane.setPoint(null);
            }
        };
        DragSource.getDefaultDragSource().addDragSourceListener(dsa);
        DragSource.getDefaultDragSource().addDragSourceMotionListener(dsa);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        glassPane.setVisible(true);
    }

    private static class ImageGlassPane extends JComponent {
        private Point point = null;
        private BufferedImage image = null;

        public ImageGlassPane() {
            super();
            setOpaque(false);
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
            repaint();
        }

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        public void paint(Graphics g) {
            super.paint(g);

            if (point != null && image != null) {
                g.drawImage(image, point.x, point.y, null);
            }
        }
    }
}
