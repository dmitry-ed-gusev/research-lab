package gusev.dmitry.research.java24h.lesson12;

import javax.swing.*;
import java.awt.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
 */
public class PingPongTable extends JPanel implements PingPongConsts {

    JLabel label;
    public Point point = new Point(0, 0);

    private int ComputerRacketX = 15;
    private int kidRacketY      = KID_RACKET_Y_START;

    Dimension preferredSize = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);

    // This method sets the size of the frame. It’s called by JVM
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    // Constructor instantiates the engine
    PingPongTable() {
        PingPongEngine gameEngine = new PingPongEngine(this);
        // Listen to mouse clicks to show its coordinates
        addMouseListener(gameEngine);
        // Listen to mouse movements to move the rackets
        addMouseMotionListener(gameEngine);
    }

    // Add a panel with a JLabel to the frame
    void addPaneltoFrame(Container container) {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(this);
        label = new JLabel("Click to see coordinates");
        container.add(label);
    }

    // Repaint the window. This method is called by the JVM when it needs to refresh the
    // screen or when a method repaint() is called from PingPongEngine
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        // paint the table green
        g.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
        // paint the right racket
        g.setColor(Color.yellow);
        g.fillRect(KID_RACKET_X_START, kidRacketY,5,30);
        // paint the left racket
        g.setColor(Color.blue);
        g.fillRect(ComputerRacketX,100,5,30);
        //paint the ball
        g.setColor(Color.red);
        g.fillOval(25, 110, 10, 10);
        // Draw the white lines on the table
        g.setColor(Color.white);
        g.drawRect(10,10,300,200);
        g.drawLine(160,10,160,210);
        // Display the dot as a small 2x2 pixels rectangle
        if (point != null) {
            label.setText("Coordinates (x,y): " + point.x + ", " + point.y);
            g.fillRect(point.x, point.y, 2, 2);
        }
    }

    // Set the current position of the kid’s racket
    public void setKidRacketY(int xCoordinate){
        this.kidRacketY = xCoordinate;
    }

    // Return the current position of the kid’s racket
    public int getKidRacketY(int xCoordinate){
        return kidRacketY;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Ping Pong Green Table");
        // Ensure that the window can be closed
        // by pressing a little cross in the corner
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        PingPongTable table = new PingPongTable();
        table.addPaneltoFrame(f.getContentPane());
        // Set the size and make the frame visible
        f.pack();
        f.setVisible(true);
    }

}
