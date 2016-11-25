package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v1;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class MarketNews extends Thread {

    public MarketNews (String threadName) {
        super(threadName); // name your thread
    }

    public void run() {
        System.out.println("The stock market is improving!");
    }

}