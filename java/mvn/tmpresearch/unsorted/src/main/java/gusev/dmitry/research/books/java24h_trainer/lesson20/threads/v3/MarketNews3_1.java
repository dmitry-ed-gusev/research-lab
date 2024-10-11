package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v3;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 24.10.12)
 */

public class MarketNews3_1 extends Thread {

    public MarketNews3_1(String str) {
        super(str);
    }

    public void run() {
        //System.out.println("I'll wait for 12 seconds!");

        synchronized (this) {
            try {
                wait(120);
                System.out.println("Ok! Wake up!");
            } catch (InterruptedException e) {
                System.out.println("I'm interrupted! Continue! Message: " + e.getMessage());
                //e.printStackTrace();
            }
        }

        for (int i = 0; i < 10; i++) {
            try {
                sleep(2000); // sleep for 2 seconds
            } catch (InterruptedException e) {
                System.out.println("Thread name: " + Thread.currentThread().getName() + "; Error: " + e.toString());
            }
            System.out.println("The market is falling down: -" + i);
        }
    }

}
