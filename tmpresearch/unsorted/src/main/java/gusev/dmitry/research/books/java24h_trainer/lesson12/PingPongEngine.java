package gusev.dmitry.research.java24h.lesson12;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
 */

public class PingPongEngine implements MouseListener, MouseMotionListener, PingPongConsts {

    PingPongTable table;

    public int kidRacketY = KID_RACKET_Y_START;
    // Constructor. Stores a reference to the UI object
    public PingPongEngine(PingPongTable greenTable) {
        table = greenTable;
    }

    // Methods required by the MouseListener interface
    public void mouseClicked(MouseEvent e) {
        // Get X and Y coordinates of the mouse pointer and set it to the “white point” on the table
        // Bad practice! Fix it in the Try It section
        table.point.x = e.getX();
        table.point.y = e.getY();
        //The method repaint internally calls the table’s
        // method paintComponent() that refreshes the window
        table.repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    // Methods required by the MouseMotionListener interface
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int mouseY = e.getY();
        // If a mouse is above the kid’s racket
        // and the racket did not go over the table top
        // move it up, otherwise move it down
        if (mouseY < kidRacketY && kidRacketY > TABLE_TOP){
            kidRacketY -= RACKET_INCREMENT;
        }else if (kidRacketY < TABLE_BOTTOM) {
            kidRacketY += RACKET_INCREMENT;
        }

        // Set the new position of the racket table class
        table.setKidRacketY(kidRacketY);
        table.repaint();
    }

}