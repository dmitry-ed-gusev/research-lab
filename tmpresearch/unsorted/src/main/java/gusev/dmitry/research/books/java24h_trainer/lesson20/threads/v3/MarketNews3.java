package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v3;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class MarketNews3 extends Thread {

    public MarketNews3 (String str) {
        super(str);
    }

    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                sleep (1000); // sleep for 1 second
                System.out.println("The market is improving: +" + i);
            }
        } catch(InterruptedException e) {
            System.out.println("Thread name: " + Thread.currentThread().getName() + "; Error: " + e.toString());
        }
    }

}