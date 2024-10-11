package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.skipy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 25.10.12)
 */

public class WaitWithTOTest {

    static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {
        Object sync = new Object();
        Thread t = new Thread(new WaitingThread(sync));
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log("main::Interrupted: " + ex.getMessage());
        }
        synchronized (sync) {
            log("main::Sleeping for 5 seconds");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                log("main::Interrupted: " + ex.getMessage());
            }
            log("main::Exiting synchronized block");
        }
    }

    static void log(String msg) {
        System.out.println(FORMAT.format(new Date()) + ": " + msg);
    }

    static class WaitingThread implements Runnable {

        private Object sync;

        public WaitingThread(Object sync) {
            this.sync = sync;
        }

        public void run() {
            synchronized (sync) {
                log("own:: Waiting for 2 seconds");
                try {
                    sync.wait(2000);
                } catch (InterruptedException ex) {
                    log("own:: Interrupted: " + ex.getMessage());
                }
                log("own:: Running again");
            }
        }
    }
}