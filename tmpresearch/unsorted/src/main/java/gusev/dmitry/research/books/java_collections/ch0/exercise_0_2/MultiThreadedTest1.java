package gusev.dmitry.research.books.java_collections.ch0.exercise_0_2;

/**
 * Exercise 0.2 with multithreading - classic variant (without classes from
 * package java.util.concurrent).
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 24.09.2014)
*/

public class MultiThreadedTest1 implements Runnable {

    public static final int THREADS_COUNT     = 4;
    public static final int MAX_VALUE         = 100_000;
    public static final int INFO_STEP         = 1000;
    //public static final int THREAD_SLEEP_STEP = 1000;

    private int a;

    public MultiThreadedTest1(int a) {
        if (a <= 0) {
            throw new IllegalArgumentException("Negative argument!");
        }
        this.a = a;
    }

    @Override
    public void run() {
        //System.out.printf("Thread [%s] started. a=%s\n", this, a);
        // calculating cycle
        int result;
        for (long i = 1; i <= MAX_VALUE; i++) {
            result = (int) (this.a - this.a/i*i - this.a%i);
            if (result != 0) { // check result
                System.out.printf("!!!!!!!!!!!! [%s] [a=%s, b=%s]", result, this.a, i);
            }

            /*
            if (i%THREAD_SLEEP_STEP == 0) { // let other threads/processes to work
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            */

        }
        //System.out.printf("[a=%s] finished -> %s\n", a, this);

    }

    /***/
    public static void main(String[] args) {
        System.out.println("MultiThreadedTest1 starting...");

        long start = System.nanoTime();
        Thread[] threads = new Thread[THREADS_COUNT];
        for (long i = 1; i <= MAX_VALUE; i++) {
            threads[(int) ((i - 1)%THREADS_COUNT)] = new Thread(new MultiThreadedTest1((int) i)); // creating portion of threads
            // if portion of threads created - starts them and wait for finish
            if (i%THREADS_COUNT == 0 || i == MAX_VALUE) {
                //System.out.println("Starting portion of threads -> " + i);
                for (Thread thread : threads) { // starting threads
                    if (thread != null) {
                        thread.start();
                    }
                }
                //System.out.println("Waiting for portion of threads " + i);
                try { // wait for threads finish
                    for (Thread thread : threads) {
                        if (thread != null) {
                            thread.join();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("ERROR - interrupted!");
                }
                // reset threads array (otherwise we will re-execute finished threads and
                // get java.lang.IllegalThreadStateException - if threads count isn't divider
                // for MAX_VALUE)
                threads = new Thread[THREADS_COUNT];
            }
            if (i % INFO_STEP == 0) {
                System.out.println("processed -> " + i);
            }
        } // end of FOR cycle
        long end = System.nanoTime();
        long time = (end - start)/1_000_000_000; // nanoseconds -> seconds
        System.out.printf("It takes %s second(s).\n", time);
    }

}