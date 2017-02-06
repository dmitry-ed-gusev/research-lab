package gusev.dmitry.jtils.utils;

import org.junit.Test;

import javax.management.MalformedObjectNameException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link MoneyAmountInWords}.
 * Created by vinnypuhh on 20.01.2017.
 */

// todo: check - > than 99 копеек
// todo: move message to constant

public class MoneyAmountInWordsTest {

    // good money amounts - double
    private final Map<Double, String> amountsGoodDouble = new HashMap<Double, String>() {{
        put(1000D,     "одна тысяча рублей 00 копеек");
        put(2000D,     "две тысячи рублей 00 копеек");
        put(5000D,     "пять тысяч рублей 00 копеек");
        put(1001D,     "одна тысяча один рубль 00 копеек");
        put(1002D,     "одна тысяча два рубля 00 копеек");
        put(1005D,     "одна тысяча пять рублей 00 копеек");
        put(1001.01D,  "одна тысяча один рубль 01 копейка");
        put(1001.10D,  "одна тысяча один рубль 10 копеек");
        put(1001.02D,  "одна тысяча один рубль 02 копейки");
        put(1002.05D,  "одна тысяча два рубля 05 копеек");
        put(1005.55D,  "одна тысяча пять рублей 55 копеек");
        put(84432.51D, "восемьдесят четыре тысячи четыреста тридцать два рубля 51 копейка");
    }};

    // good money amounts - long
    private final Map<Long, String> amountsGoodLong = new HashMap<Long, String>() {{
        put(1000L, "одна тысяча рублей 00 копеек");
        put(2000L, "две тысячи рублей 00 копеек");
        put(5000L, "пять тысяч рублей 00 копеек");
        put(1001L, "одна тысяча один рубль 00 копеек");
        put(1002L, "одна тысяча два рубля 00 копеек");
        put(1005L, "одна тысяча пять рублей 00 копеек");
    }};

    // good money amounts - string
    private final Map<String, String> amountsGoodString = new HashMap<String, String> () {{
        put("1234.6", "одна тысяча двести тридцать четыре рубля 60 копеек");
        //put("", "");
        //put("", "");
        //put("", "");
        //put("", "");
        //put("", "");
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
    public void testNullStringAmount() {
        new MoneyAmountInWords(null);
    }

    @Test
    public void testEmptyStringAmount() {
        MoneyAmountInWords amount = new MoneyAmountInWords("");
        assertEquals("Amounts should be equals!", "ноль рублей 0 копеек", amount.num2str());
    }

}
