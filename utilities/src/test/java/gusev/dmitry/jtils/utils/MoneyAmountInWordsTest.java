package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link MoneyAmountInWords}.
 * Created by vinnypuhh on 20.01.2017.
 */

// todo: check > than 99 копеек

public class MoneyAmountInWordsTest {

    private static final String ZERO_AMOUNT = "ноль рублей 0 копеек";

    // good money amounts - double
    private final Map<Double, String> amountsGoodDouble = new HashMap<Double, String>() {{
        put(1000D,     "одна тысяча рублей 00 копеек");
        put(2000D,     "две тысячи рублей 00 копеек");
        put(5000D,     "пять тысяч рублей 00 копеек");
        put(1001D,     "одна тысяча один рубль 00 копеек");
        put(1002D,     "одна тысяча два рубля 00 копеек");
        put(1005.D,    "одна тысяча пять рублей 00 копеек");
        put(1001.01D,  "одна тысяча один рубль 01 копейка");
        put(1001.10D,  "одна тысяча один рубль 10 копеек");
        put(1001.02D,  "одна тысяча один рубль 02 копейки");
        put(1002.05D,  "одна тысяча два рубля 05 копеек");
        put(1005.55D,  "одна тысяча пять рублей 55 копеек");
        put(84432.51D, "восемьдесят четыре тысячи четыреста тридцать два рубля 51 копейка");
        put(.54D,      "ноль рублей 54 копейки");
        put(0.D,       ZERO_AMOUNT);
        put(.0D,       ZERO_AMOUNT);
        put(00.00D,    ZERO_AMOUNT);
        put(0D,        ZERO_AMOUNT);
    }};

    // good money amounts - long
    private final Map<Long, String> amountsGoodLong = new HashMap<Long, String>() {{
        put(1000L, "одна тысяча рублей 00 копеек");
        put(2000L, "две тысячи рублей 00 копеек");
        put(5000L, "пять тысяч рублей 00 копеек");
        put(1001L, "одна тысяча один рубль 00 копеек");
        put(1002L, "одна тысяча два рубля 00 копеек");
        put(1005L, "одна тысяча пять рублей 00 копеек");
        put(101L,  "сто один рубль 00 копеек");
        put(0L,    ZERO_AMOUNT);
    }};

    // good money amounts - string
    private final Map<String, String> amountsGoodString = new HashMap<String, String> () {{
        put("  1234.6",  "одна тысяча двести тридцать четыре рубля 60 копеек");
        put("32  ",      "тридцать два рубля 00 копеек");
        put("   234.",   "двести тридцать четыре рубля 00 копеек");
        put(".45  ",     "ноль рублей 45 копеек");
        put("  .00",     ZERO_AMOUNT);
        put("   0",      ZERO_AMOUNT);
        put("00.00  ",   ZERO_AMOUNT);
        put("         ", ZERO_AMOUNT);
        put("",          ZERO_AMOUNT);
        put(null,        ZERO_AMOUNT);
        put("9.123",     "девять рублей 123 копейки");
    }};

    @Test
    public void testGoodAmountsDouble() {
        amountsGoodDouble.entrySet().forEach(entry -> {
            MoneyAmountInWords amount = new MoneyAmountInWords(entry.getKey());
            assertEquals("Amounts should be equals!", entry.getValue(), amount.num2str());
        });
    }

    @Test
    public void testGoodAmountsLong() {
        amountsGoodLong.entrySet().forEach(entry -> {
            MoneyAmountInWords amount = new MoneyAmountInWords(entry.getKey());
            assertEquals("Amounts should be equals!", entry.getValue(), amount.num2str());
        });
    }

    @Test
    public void testGoodAmountsString() {
        amountsGoodString.entrySet().forEach(entry -> {
            MoneyAmountInWords amount = new MoneyAmountInWords(entry.getKey());
            assertEquals("Amounts should be equals!", entry.getValue(), amount.num2str());
        });
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNegativeLongAmount() {
        MoneyAmountInWords amount = new MoneyAmountInWords(-10L);
        amount.num2str();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNegativeDoubleAmount() {
        MoneyAmountInWords amount = new MoneyAmountInWords(-1D);
        amount.num2str();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNegativeStringAmount() {
        MoneyAmountInWords amount = new MoneyAmountInWords("-9.00");
        amount.num2str();
    }

}
