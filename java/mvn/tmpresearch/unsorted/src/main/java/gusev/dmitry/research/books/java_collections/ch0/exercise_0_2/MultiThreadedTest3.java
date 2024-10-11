package gusev.dmitry.research.books.java_collections.ch0.exercise_0_2;

import java.util.concurrent.Semaphore;

/**
 * Exercise 0.2 with multithreading. Implementation with Semaphor
 * (package java.util.concurrent).
 *
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 07.10.2014)
 */

public class MultiThreadedTest3 implements Runnable {

    private static final int THREADS_COUNT = 4;
    private static final int MAX_VALUE     = 100_000;
    private static final int INFO_STEP     = 1000;

    private int       a;         // start value for current thread
    private Semaphore semaphore; // semaphore object for thread

    public MultiThreadedTest3(int a, Semaphore semaphore) {
        if (a <= 0 || semaphore == null) {
            throw new IllegalArgumentException(String.format("Negative argument [%s] or invalid Semaphore [null=%s]!", a, (semaphore == null)));
        }
        this.a         = a;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        int result;
        // calculating cycle
        for (long i = 1; i <= MAX_VALUE; i++) {
            result = (int) (this.a - this.a / i * i - this.a % i);
            if (result != 0) { // check result
                System.out.printf("!!!!!!!!!!!! [%s] [a=%s, b=%s]", result, this.a, i);
            }
        } // end of FOR cycle
        // release semaphore lock (increase permits count by 1). Last thread will set permits count to 1 (one) and
        // main thread can acquire permit and continue execution
        this.semaphore.release();
        //System.out.println("----> " + this.semaphore.availablePermits());
    }

    /***/
    public static void main(String[] args) {
        System.out.println("MultiThreadedTest3 starting...");

        long start = System.nanoTime(); // get start time

        int semaphorePermits = -(MAX_VALUE >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE) + 1;
        //System.out.println("initial permits value -> " + semaphorePermits);
        Semaphore semaphore = new Semaphore(semaphorePermits);

        Thread[] threads = new Thread[THREADS_COUNT];
        for (long i = 1; i <= MAX_VALUE; i++) {

            threads[(int) ((i - 1) % THREADS_COUNT)] = new Thread(new MultiThreadedTest3((int) i, semaphore)); // creating portion of threads
            // if portion of threads created - starts them and wait for finish
            if (i % THREADS_COUNT == 0 || i == MAX_VALUE) {
                //System.out.println(String.format("run portion of threads -> [%s], semaphore permits -> [%s]", i, semaphore.availablePermits()));

                //System.out.println("Starting portion of threads -> " + i);
                for (Thread thread : threads) { // starting threads
                    if (thread != null) {
                        thread.start();
                    }
                }

                // try to acquire permit (will wait until the last thread will increase permits count and set it ot 1 (one))
                try {
                    //System.out.println("before -------------> " + semaphore.availablePermits());
                    semaphore.acquire();
                    //System.out.println("after -------------> " + semaphore.availablePermits());
                } catch (InterruptedException e) {
                    System.out.println("Acquiring semaphor permit error: " + e.getMessage());
                }

                // reset Semaphor object (if current iteration is not last run of threads group) and reset threads array
                // (otherwise we will re-execute finished threads and get java.lang.IllegalThreadStateException - if
                // threads count isn't divider for MAX_VALUE)
                if (i != MAX_VALUE) {
                    // reset threads array
                    threads = new Thread[THREADS_COUNT];
                    // reset Semaphor object
                    semaphorePermits = -(MAX_VALUE - (int) i >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE - (int) i) + 1;
                    //System.out.println("new permits value for semaphor -> " + semaphorePermits);
                    semaphore = new Semaphore(semaphorePermits);
                }

            } // end of 'run threads group' block (if)

            if (i % INFO_STEP == 0) { // debug information about processing
                System.out.println("processed -> " + i);
            }
        } // end of FOR cycle
        long end = System.nanoTime();
        long time = (end - start) / 1_000_000_000; // nanoseconds -> seconds
        System.out.printf("It takes %s second(s).\n", time);
    }

}