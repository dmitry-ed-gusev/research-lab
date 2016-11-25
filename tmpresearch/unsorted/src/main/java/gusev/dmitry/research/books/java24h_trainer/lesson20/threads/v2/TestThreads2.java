package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v2;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class TestThreads2 {

    public static void main(String args[]) {

        MarketNews2 mn2 = new MarketNews2();
        Thread mn = new Thread(mn2, "Market News");
        mn.start();

        Runnable port2 = new Portfolio2();
        Thread p = new Thread(port2, "Portfolio Data");
        p.start();

        System.out.println("TestThreads2 is finished");
    }

}