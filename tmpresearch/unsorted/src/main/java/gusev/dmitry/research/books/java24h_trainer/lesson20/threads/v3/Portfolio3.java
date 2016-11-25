package gusev.dmitry.research.books.java24h_trainer.lesson20.threads.v3;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 23.10.12)
 */

public class Portfolio3 extends Thread {

    //private MarketNews3_1 mn31;

    public Portfolio3(String str/*, MarketNews3_1 mn31*/) {
        super(str);
        //this.mn31 = mn31;
    }

    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                sleep(100); // Sleep for 700 milliseconds
                System.out.println("You have " + (500 + i) + " shares of IBM");

                // notify other threads
                /*synchronized (this) {
                    if (i == 5 && this.mn31 != null){
                        System.out.println("------- Time to notify MarketNews3_1!");
                        //this.notifyAll();
                        mn31.notify();
                    }
                }
*/
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + e.toString());
        }
    }

}