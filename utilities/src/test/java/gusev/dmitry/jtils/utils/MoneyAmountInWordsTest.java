package gusev.dmitry.jtils.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link MoneyAmountInWords}.
 * Created by vinnypuhh on 20.01.2017.
 */

public class MoneyAmountInWordsTest {

    // good money
    private final Map<Double, String> amountsGood = new HashMap<Double, String>() {{
        put(1000D,   "одна тысяча рублей 00 копеек");
        put(2000D,   "две тысячи рублей 00 копеек");
        put(5000D,   "пять тысяч рублей 00 копеек");
        put(1001D,   "одна тысяча один рубль 00 копеек");
        put(1002D,   "одна тысяча два рубля 00 копеек");
        put(1005D,   "одна тысяча пять рублей 00 копеек");
        put(1001.01, "одна тысяча один рубль 01 копейка");
        put(1001.02, "одна тысяча один рубль 02 копейки");
        put(1002.05, "одна тысяча два рубля 05 копеек");
        //put(1000L, "одна тысяча рублей 00 копеек");
        //put(1000L, "одна тысяча рублей 00 копеек");
    }};

    @Test
    public void testGoodAmounts() {
        amountsGood.entrySet().forEach(entry -> {
            MoneyAmountInWords amount = new MoneyAmountInWords(entry.getKey());
            assertEquals("Amounts should be equals!", entry.getValue(), amount.num2str());
        });
    }

    @Test
    public void testBadAmounts() {

    }

}
