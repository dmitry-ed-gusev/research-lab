package gusev.dmitry.jtils.calc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 21.08.2014)
*/

public final class CalcFunctions {

    private CalcFunctions() {} // no instances for utility class

    /***/
    public static String convertDecimalToNotation(BigInteger sourceNumber, int radix) {
        StringBuilder result = new StringBuilder();
        if (radix > 0) {
            BigInteger[] quotientAndRemainder = {sourceNumber, BigInteger.ZERO};
            BigInteger   biRadix              = BigInteger.valueOf(radix);       // converts radix to BigInteger (bi)
            boolean      isHexadecimal        = (radix == 16);
            BigInteger   ten                  = BigInteger.valueOf(10); // used for hexadecimal letters
            BigInteger   fiftyFive            = BigInteger.valueOf(55); // used for hexadecimal (65='A', 10HEX->'A' etc)
            do {
                quotientAndRemainder = quotientAndRemainder[0].divideAndRemainder(biRadix);
                if (isHexadecimal) { // HEXADECIMAL (it uses letters)
                    result.append(quotientAndRemainder[1].compareTo(ten) >= 0 ?
                            (char) (fiftyFive.add(quotientAndRemainder[1]).intValue()) : quotientAndRemainder[1].intValue());
                } else {
                    result.append(quotientAndRemainder[1].intValue());
                }
            } while (quotientAndRemainder[0].compareTo(BigInteger.ZERO) != 0);
            result = result.reverse();
        } else {
            result = new StringBuilder(String.valueOf(sourceNumber));
        }
        return result.toString();
    }

    /***/
    public static String convertDecimalToNotation(long sourceNumber, int radix) {
        StringBuilder result = new StringBuilder();
        if (radix > 0) {
            long base = sourceNumber;
            long remainder;
            do {
                remainder = Math.abs(base % radix);
                base /= radix;
                if (radix == 16) { // HEXADECIMAL (it uses letters)
                    if (remainder >= 10) {
                        result.append((char) (65 + remainder - 10));
                    } else {
                        result.append(remainder);
                    }
                } else {
                    result.append(remainder);
                }
            } while (base != 0);
            result = result.reverse();
        } else {
            result = new StringBuilder(String.valueOf(sourceNumber));
        }
        return result.toString();
    }

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(CalcFunctions.class);
        log.info("CalcFunctions starting.");

        System.out.println(CalcFunctions.convertDecimalToNotation(128, 2));
        System.out.println(CalcFunctions.convertDecimalToNotation(new BigInteger("128"), 2));
    }

}