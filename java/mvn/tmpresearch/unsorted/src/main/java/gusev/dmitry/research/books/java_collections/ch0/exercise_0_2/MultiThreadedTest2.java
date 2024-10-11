package gusev.dmitry.research.books.java_collections.ch0.exercise_0_2;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Exercise 0.2 with multithreading. Implementation with CyclicBarrier
 * (package java.util.concurrent).
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 01.10.2014)
*/

public class MultiThreadedTest2 implements Runnable {

    private static final int THREADS_COUNT = 4;
    private static final int MAX_VALUE     = 100_000;
    private static final int INFO_STEP     = 1000;

    private int a;       // start value for current thread
    private CyclicBarrier barrier; // cyclic barrier for thread

    public MultiThreadedTest2(int a, CyclicBarrier barrier) {
        if (a <= 0 || barrier == null) {
            throw new IllegalArgumentException(String.format("Negative argument [%s] or invalid CyclicBarrier [null=%s]!", a, (barrier == null)));
        }
        this.a = a;
        this.barrier = barrier;
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

        // cyclic barrier await
        try {
            this.barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            System.out.println("Barrier broken or interrupted!");
        }
    }

    /***/
    public static void main(String[] args) {
        System.out.println("MultiThreadedTest2 starting...");

        long start = System.nanoTime();

        @SuppressWarnings("ConstantConditions")
        CyclicBarrier barrier = new CyclicBarrier((MAX_VALUE >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE) + 1);

        Thread[] threads = new Thread[THREADS_COUNT];
        for (long i = 1; i <= MAX_VALUE; i++) {

            threads[(int) ((i - 1) % THREADS_COUNT)] = new Thread(new MultiThreadedTest2((int) i, barrier)); // creating portion of threads
            // if portion of threads created - starts them and wait for finish
            if (i % THREADS_COUNT == 0 || i == MAX_VALUE) {
                //System.out.println(String.format("run portion of threads -> [%s], barrier parties -> [%s]", i, barrier.getParties()));

                //System.out.println("Starting portion of threads -> " + i);
                for (Thread thread : threads) { // starting threads
                    if (thread != null) {
                        thread.start();
                    }
                }

                // cyclic barrier await - waiting for main thread
                try {
                    barrier.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    System.out.println("Barrier broken or interrupted!");
                }

                // reset threads array (otherwise we will re-execute finished threads and
                // get java.lang.IllegalThreadStateException - if threads count isn't divider
                // for MAX_VALUE)
                threads = new Thread[THREADS_COUNT];

                // reset CyclicBarrier object (if current iteration is not last run of threads group
                if (i != MAX_VALUE) {
                    int newPartiesValue = (MAX_VALUE - (int) i >= THREADS_COUNT ? THREADS_COUNT : MAX_VALUE - (int) i) + 1;
                    //System.out.println("new parties value for barrier -> " + newPartiesValue);
                    barrier = new CyclicBarrier(newPartiesValue);
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