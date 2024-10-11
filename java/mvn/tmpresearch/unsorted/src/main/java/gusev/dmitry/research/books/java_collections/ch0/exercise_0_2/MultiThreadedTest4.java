package gusev.dmitry.research.books.java_collections.ch0.exercise_0_2;

import java.util.concurrent.CountDownLatch;

/**
 * Exercise 0.2 with multithreading. Implementation with CountDownLatch (package java.util.concurrent).
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 07.10.2014)
 */

public class MultiThreadedTest4 implements Runnable {

    private static final int THREADS_COUNT = 4;
    private static final int MAX_VALUE     = 100_000;
    private static final int INFO_STEP     = 1000;

    private int            a;     // start value for current thread
    private CountDownLatch latch; // latch object for thread

    public MultiThreadedTest4(int a, CountDownLatch latch) {
        if (a <= 0 || latch == null) {
            throw new IllegalArgumentException(String.format("Negative argument [%s] or invalid CountDownLatch [null=%s]!", a, (latch == null)));
        }
        this.a     = a;
        this.latch = latch;
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
        // Count down for latch. Last thread will set latch object counter to zero and main thread can continue
        this.latch.countDown();
    }

    /***/
    public static void main(String[] args) {
        System.out.println("MultiThreadedTest4 starting...");

        long start = System.nanoTime(); // get start time

        @SuppressWarnings("ConstantConditions")
        int latchCounter = MAX_VALUE >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE;

        CountDownLatch latch = new CountDownLatch(latchCounter);

        Thread[] threads = new Thread[THREADS_COUNT];
        for (long i = 1; i <= MAX_VALUE; i++) {

            threads[(int) ((i - 1) % THREADS_COUNT)] = new Thread(new MultiThreadedTest4((int) i, latch)); // creating portion of threads
            // if portion of threads created - starts them and wait for finish
            if (i % THREADS_COUNT == 0 || i == MAX_VALUE) {
                //System.out.println(String.format("run portion of threads -> [%s], semaphore permits -> [%s]", i, semaphore.availablePermits()));

                //System.out.println("Starting portion of threads -> " + i);
                for (Thread thread : threads) { // starting threads
                    if (thread != null) {
                        thread.start();
                    }
                }

                // main thread - await for latch (the last of threads will set latch counter to zero
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.out.println("Awaiting latch error: " + e.getMessage());
                }

                // reset CountDownLatch object (if current iteration is not last run of threads group) and reset threads array
                // (otherwise we will re-execute finished threads and get java.lang.IllegalThreadStateException - if
                // threads count isn't divider for MAX_VALUE)
                if (i != MAX_VALUE) {
                    // reset threads array
                    threads = new Thread[THREADS_COUNT];
                    // reset CountDownLatch object
                    latchCounter = MAX_VALUE - (int) i >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE - (int) i;
                    latch = new CountDownLatch(latchCounter);
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