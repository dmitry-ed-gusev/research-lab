package gusev.dmitry.jtils.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link SortMapUtils} module.
 * Created by gusevdm on 6/13/2017.
 */
public class SortMapUtilsTest {

    @Test
    public void testSortMapNullMap() {
        assertNull(SortMapUtils.sortMapByValue(null));
    }

    @Test
    public void testSortMapEmptyMap() {
        Map result = SortMapUtils.sortMapByValue(new HashMap<String, Integer>());
        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0, result.entrySet().size());
    }

    @Test
    public void testSortByValue() {
        // init test map
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<>(1000);
        for (int i = 0; i < 1000; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }

        // sort map
        testMap = SortMapUtils.sortMapByValue(testMap);

        // tests/assertions
        Assert.assertEquals(1000, testMap.size());

        Integer previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull(entry.getValue());
            if (previous != null) {
                Assert.assertTrue(entry.getValue() >= previous);
            }
            previous = entry.getValue();
        }
    }

}
