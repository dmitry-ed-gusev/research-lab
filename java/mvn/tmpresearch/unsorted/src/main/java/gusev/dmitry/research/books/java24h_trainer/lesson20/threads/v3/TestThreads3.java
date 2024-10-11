package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v3;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class TestThreads3 {

    public static void main(String args[]) {

        MarketNews3 mn = new MarketNews3("Market News (3)");
        mn.start();

        MarketNews3_1 mn3_1 = new MarketNews3_1("Market New (3_1)");
        mn3_1.start();
        //mn3_1.interrupt();

        Portfolio3 p = new Portfolio3("Portfolio data"/*, mn3_1*/);
        p.start();

        try {
            //System.out.println("mn join");
            mn.join();
            mn3_1.join();
            //System.out.println("p join");
            p.join();
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        }

        System.out.println("The main method of TestThreads3 is finished.");

    }

}