package edu.dmgusev.finders.prime;

/**
 * For a given number, determines whether or not it is prime.
 */
import edu.duke.RangeResource;

public class PrimeFinderApplication {
    public boolean isPrime(int num) {
        int div = 2;
        if (num == 2) {
            return true;
        }
        while (true) {
            if (num % div == 0) {
                return false;
            }
            if (div > Math.sqrt(num)) {
                break;
            }
            div = div + 1;
        }
        return true;
    }

    public void testPrimes() {
        RangeResource rr = new RangeResource(2, 200);
        for (int value : rr.sequence()) {
            if (isPrime(value)) {
                System.out.println(value + "\t is prime");
            }
        }
    }

}
