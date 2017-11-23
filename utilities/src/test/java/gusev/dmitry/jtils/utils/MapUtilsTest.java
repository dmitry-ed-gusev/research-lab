package gusev.dmitry.jtils.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MapUtils} module.
 * Created by gusevdm on 6/13/2017.
 */
public class MapUtilsTest {

    @Test
    public void testSortMapNullMap() {
        assertNull(MapUtils.sortMapByValue(null, MapUtils.SortType.ASC));
    }

    @Test
    public void testSortMapNullMapAndSortType() {
        assertNull(MapUtils.sortMapByValue(null, null));
    }

    @Test
    public void testSortMapEmptyMap() {
        Map result = MapUtils.sortMapByValue(new HashMap<String, Integer>(), null);
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
        map = MapUtils.sortMapByValue(map, null);
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
        MapUtils.sortMapByValue(map, null);
    }

    @Test
    public void testSortMapByValue() {
        // todo: if value is big (>100_000) -> often fails!!!
        final int size = 10_000;

        // init test map
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<>(size);
        for (int i = 0; i < size; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }
        //System.out.println(testMap);

        // sort map and check (ASC)
        testMap = MapUtils.sortMapByValue(testMap, MapUtils.SortType.ASC);
        //System.out.println(testMap);

        // test size
        // todo: fails to often (floating error!!!)
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
        testMap = MapUtils.sortMapByValue(testMap, MapUtils.SortType.DESC);
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

    @Test
    public void testRemoveFromMapNullMap() {
        assertNull(MapUtils.removeFromMapByValue(null, null));
    }

    @Test
    public void testRemoveFromMapEmptyMap() {
        Map<String, Integer> emptyMap = new HashMap<>();
        Map<String, Integer> resultMap = MapUtils.removeFromMapByValue(emptyMap, null);

        assertTrue(resultMap.isEmpty());
        assertTrue(emptyMap == resultMap);
    }

    @Test
    public void testRemoveFromMapRemoveNull1() {
        Map<String, Integer> sourceMap = new HashMap<String, Integer>() {{
            put("1", null);
            put("2", null);
        }};
        // do remove
        Map<String, Integer> resultMap = MapUtils.removeFromMapByValue(sourceMap, null);

        // check
        assertTrue(resultMap.isEmpty());
        assertTrue(resultMap.size() == 0);
        assertTrue(sourceMap == resultMap);
    }

    @Test
    public void testRemoveFromMapRemoveNull2() {
        Map<String, Integer> sourceMap = new HashMap<String, Integer>() {{
            put("1", 10);
            put("2", 20);
        }};
        // do remove
        Map<String, Integer> resultMap = MapUtils.removeFromMapByValue(sourceMap, null);

        // check
        assertTrue(!resultMap.isEmpty());
        assertTrue(resultMap.size() == 2);
        assertTrue(sourceMap == resultMap);
    }

    @Test
    public void testRemoveFromMapValue1() {
        Map<String, Integer> sourceMap = new HashMap<String, Integer>() {{
            put("1", null);
            put("2", null);
            put("zzz", 1);
            put("ddd", 2);
            put("xxx", 3);
        }};
        int sourceMapSize = sourceMap.size();

        // do remove
        Map<String, Integer> resultMap = MapUtils.removeFromMapByValue(sourceMap, 3);

        // checks
        assertTrue(sourceMap == resultMap);
        assertEquals(sourceMapSize - 1, resultMap.size());
        assertFalse(resultMap.containsKey("xxx"));
        assertFalse(resultMap.containsKey(3));
    }

    @Test
    public void testRemoveFromMapValue2() {
        Map<String, Integer> sourceMap = new HashMap<String, Integer>() {{
            put("zzz", 1);
            put("ddd", 2);
            put("xxx", 3);
        }};
        int sourceMapSize = sourceMap.size();

        // do remove
        Map<String, Integer> resultMap = MapUtils.removeFromMapByValue(sourceMap, 2);

        // checks
        assertTrue(sourceMap == resultMap);
        assertEquals(sourceMapSize - 1, resultMap.size());
        assertFalse(resultMap.containsKey("ddd"));
        assertFalse(resultMap.containsKey(2));
    }
}
