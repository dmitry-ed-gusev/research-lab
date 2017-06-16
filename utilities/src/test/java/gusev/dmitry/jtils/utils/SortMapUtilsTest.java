package gusev.dmitry.jtils.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link SortMapUtils} module.
 * Created by gusevdm on 6/13/2017.
 */
public class SortMapUtilsTest {

    @Test
    public void testSortMapNullMap() {
        assertNull(SortMapUtils.sortMapByValue(null, SortMapUtils.SortType.ASC));
    }

    @Test
    public void testSortMapNullMapAndSortType() {
        assertNull(SortMapUtils.sortMapByValue(null, null));
    }

    @Test
    public void testSortMapEmptyMap() {
        Map result = SortMapUtils.sortMapByValue(new HashMap<String, Integer>(), null);
        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(0, result.entrySet().size());
    }

    @Test
    public void testSortMapOneElementMap() {
        final String key = "string";
        Map<String, Integer> map = new HashMap<String, Integer>() {{
           put(key, 1);
        }};
        // sort
        map = SortMapUtils.sortMapByValue(map, null);
        // test/assert
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key));
        assertEquals(new Integer(1), map.get(key));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSortMapNullType() {
        Map<String, Integer> map = new HashMap<String, Integer>() {{
            put("string1", 1);
            put("string2", 2);
        }};
        // sort and get exception
        SortMapUtils.sortMapByValue(map, null);
    }

    @Test
    public void testSortMapByValue() {
        final int size = 10000;

        // init test map
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<>(size);
        for (int i = 0; i < size; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }
        //System.out.println(testMap);

        // sort map and check (ASC)
        testMap = SortMapUtils.sortMapByValue(testMap, SortMapUtils.SortType.ASC);
        //System.out.println(testMap);

        // test size
        Assert.assertEquals(size, testMap.size());
        // test contents
        Integer previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull(entry.getValue());
            if (previous != null) {
                Assert.assertTrue(entry.getValue() >= previous);
            }
            previous = entry.getValue();
        }

        // sort map and check (DESC)
        testMap = SortMapUtils.sortMapByValue(testMap, SortMapUtils.SortType.DESC);
        // test size
        Assert.assertEquals(size, testMap.size());
        // test contents
        previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull(entry.getValue());
            if (previous != null) {
                Assert.assertTrue(entry.getValue() <= previous);
            }
            previous = entry.getValue();
        }

    }

}
