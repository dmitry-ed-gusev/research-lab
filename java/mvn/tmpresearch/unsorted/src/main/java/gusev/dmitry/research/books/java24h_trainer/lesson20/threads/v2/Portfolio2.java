package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v2;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class Portfolio2 implements Runnable {

    @Override
    public void run() {
        System.out.println("You have 500 shares of IBM");
    }

}
