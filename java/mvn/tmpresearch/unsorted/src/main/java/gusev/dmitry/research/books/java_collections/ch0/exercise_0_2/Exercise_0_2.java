package gusev.dmitry.research.books.java_collections.ch0.exercise_0_2;

import java.util.Date;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 24.09.2014)
 */

public class Exercise_0_2 {

    public static final int MAX_VALUE = 100_000;
    public static final int INFO_STEP = 1000;

    public static void main(String[] args) {
        //int a = 37, b = 5;
        //System.out.println ("-> " + (a - a / b * b - a % b));

        long start = System.nanoTime();
        System.out.println(new Date());
        int result;
        for (long i = 1; i <= MAX_VALUE ; i++) {
            for (long j = 1; j <= MAX_VALUE; j++) {
                result = (int) (i - i/j*j - i%j);
                if (result != 0) {
                    System.out.printf("!!! -> [%s] [a=%s, b=%s]", result, i, j);
                }
            }
            if (i % INFO_STEP == 0) {
                System.out.println("processed -> " + i);
            }
        } // end of FOR

        long end = System.nanoTime();
        long time = (end - start)/1_000_000_000;
        System.out.println(new Date());
        System.out.printf("It takes %s second(s).\n", time);
    }

}