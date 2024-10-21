package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v1;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class TestThreads {

    public static void main(String args[]){

        MarketNews mn = new MarketNews("Market News");
        mn.start();

        Portfolio p = new Portfolio("Portfolio data");
        p.start();

        System.out.println("TestThreads is finished");

    }

}