package gusev.dmitry.research.math;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
*/

public class Factorial {

    //private static Log log = LogFactory.getLog(Factorial.class);

    public static long factorial(int n) {
        if (n < 0) {
            throw new NumberFormatException("Can't evaluate factoria for negative number!");
        }
        long result;
        if (n == 0) {
            result = 1;
        } else {
            result = n*factorial(n - 1);
        }
        return result;
    }

    public static long stirlingFactorial() {
        long result = 0;
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Factorial.factorial(3));
    }

}
