package gusev.dmitry.research.books.java24h_trainer.lesson8;

import javax.swing.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 26.09.12)
*/

public class GuiHelloWorld extends JFrame {

    public GuiHelloWorld() {
        this.setSize(200, 300);
        this.setTitle("First title :)");
        this.setVisible(true);
    }

    public static void main(String[] args) {
        GuiHelloWorld hello = new GuiHelloWorld();
    }
}