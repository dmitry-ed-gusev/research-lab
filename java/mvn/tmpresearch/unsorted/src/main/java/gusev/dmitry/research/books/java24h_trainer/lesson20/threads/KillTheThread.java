package gusev.dmitry.research.books.java24h_trainer.lesson20.threads;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 24.10.12)
 */

class KillTheThread {

    public static void main(String args[]) {
        Portfolio4 p = new Portfolio4("Portfolio data");
        p.start();
        System.out.println("Thread started!");
        // Some other code goes here, and now it’s time to kill the thread
        p.stopMe();
    }
}

class Portfolio4 extends Thread {

    private volatile Thread stopMe = Thread.currentThread();

    public Portfolio4(String str) {
        super(str);
    }

    public void stopMe() {
        System.out.println("Set flag to some value for stop the thread.");
        stopMe = null;
    }

    public void run() {
        System.out.println("Starting thread logic.");
        int i = 0;
        while (stopMe == Thread.currentThread()) {
            try {
                //Do some portfolio processing here
                sleep(700); // Sleep for 700 milliseconds
                System.out.println("You have " + (500 + i) + " shares of IBM");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + e.toString());
            }
            i++;
        }
    }

}