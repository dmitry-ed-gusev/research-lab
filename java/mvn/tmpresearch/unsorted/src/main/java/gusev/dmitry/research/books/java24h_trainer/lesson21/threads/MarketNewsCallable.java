package gusev.dmitry.research.books.java24h_trainer.lesson21.threads;

import java.util.concurrent.Callable;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 25.10.12)
 */

class MarketNewsCallable implements Callable<Integer> {

    public Integer call() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000); // sleep for 1 second
            System.out.println("The market is improving " + i);
        }
        // Just return some number as a result
        return 12345;
    }

}