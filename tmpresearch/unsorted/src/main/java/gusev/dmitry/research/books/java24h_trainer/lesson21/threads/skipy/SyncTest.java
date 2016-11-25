package gusev.dmitry.research.books.java24h_trainer.lesson21.threads.skipy;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 25.10.12)
 */

public class SyncTest {

    public static void main(String[] args) {
        Object sync = new Object();
        Data data = new Data();
        Thread t = new Thread(new WaitingThread(sync, data));
        t.start();
        try {
            System.out.println("main::Sleeping");
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.err.println("main::Interrupted: " + ex.getMessage());
        }
        synchronized (sync) {
            System.out.println("main::setting value to 1");
            data.value = 1;
            System.out.println("main::notifying thread");
            sync.notify();
            System.out.println("main::Thread notified");
        }
    }

    static class Data {
        public int value = 0;
    }

    static class WaitingThread implements Runnable {

        private Object sync;
        private Data data;

        public WaitingThread(Object sync, Data data) {
            this.sync = sync;
            this.data = data;
        }

        public void run() {
            System.out.println("own:: Thread started");
            synchronized (sync) {
                if (data.value == 0) {
                    try {
                        System.out.println("own:: Waiting");
                        sync.wait();
                        System.out.println("own:: Running again");
                    } catch (InterruptedException ex) {
                        System.err.println("own:: Interrupted: " + ex.getMessage());
                    }
                }
                System.out.println("own:: data.value = " + data.value);
            }
        }

    }
}