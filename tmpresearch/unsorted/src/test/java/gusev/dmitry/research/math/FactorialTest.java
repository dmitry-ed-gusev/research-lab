package gusev.dmitry.research.math;

import org.junit.Test;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
 */
public class FactorialTest {

    @Test(expected = NumberFormatException.class)
    public void factorialExceptionTest() {
        Factorial.factorial(-1);
    }

}