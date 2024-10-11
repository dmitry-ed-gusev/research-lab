package gusev.dmitry.research.books.java24h_trainer.lesson21.threads;

import java.util.concurrent.Callable;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 25.10.12)
 */

class PortfolioCallable implements Callable<Integer> {

    public Integer call() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(700); // Sleep for 700 milliseconds
            System.out.println("You have " + (500 + i) + " shares of IBM");
        }
        // Just return some number as a result
        return 10;
    }

}