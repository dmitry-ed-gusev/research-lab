package dgusev.utils;

import dgusev.cmd.CmdLine;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static dgusev.utils.MyCommonUtils.MapSortType.ASC;
import static dgusev.utils.MyCommonUtils.MapSortType.DESC;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests (JUnit) for MyCommonUtils class methods.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 07.08.2014)
*/

public class MyCommonUtilsTest {

    @Mock
    CmdLine cmdLine;

    @Before
    public void beforeTest() {
        initMocks(this);
    }

    @Test
    public void testPropertiesListOneFlag() {
        // todo: implement like testPropertiesListOneOption() test!
    }

    @Test
    public void testParseStringArrayEmptyArray() {
        Set<String> emptySet = new HashSet<>();

        // sample data
        List<String> samples = new ArrayList<String>() {{
            add(null);
            add("");
            add("    ");
        }};
        // tests
        samples.forEach(array -> {
            Set<String> result = MyCommonUtils.parseStringArray(array);
            assertNotNull("Shouldn't be NULL!", result);
            assertEquals("Size should be 0!", 0, result.size());
            assertEquals("Should be empty set!", emptySet, result);
        });
    }

    @Test
    public void testParseStringArrayValidValue() {
        // sample source data
        List<String> samples = new ArrayList<String>() {{
            // spaces
            add("['value']");
            add("[    'value']");
            add("['value'    ]");
            add("[   'value'    ]");
            add("   ['value']");
            add("['value']   ");
            add("   ['value']   ");
            add("  ['   value']  ");
            add("  ['value   ']  ");
            add("  ['   value   ']  ");
            add("  [    '   value   '    ]  ");
            // duplicates
            add("   [ '    value'    ,     'value    '  ] ");
            add("['value','value']");
            // empty values
            add("['value', '']");
            add("  [  '      ' ,   ' value   ', '', '  ']");
        }};

        // expected result
        Set<String> expected = new HashSet<String>() {{
            add("value");
        }};

        // tests
        samples.forEach(array -> {
            Set<String> actual = MyCommonUtils.parseStringArray(array);
            assertEquals("Size should be 1!", 1, actual.size());
            assertEquals("Should be equals!", expected, actual);
        });
    }

    @Test
    public void testParseStringArrayValidValues() {
        // sample source data
        List<String> samples = new ArrayList<String>() {{
            add("['value1', 'value2', 'd\"value3']");
            add("[ '   value1', 'value2   ', '  d\"value3', '  ']");
            add("['', '   ', ' value1', 'value2', 'd\"value3', '   ']");
        }};

        // expected result
        Set<String> expected = new HashSet<String>() {{
            add("value1");
            add("value2");
            add("d\"value3");
        }};

        // tests
        samples.forEach(array -> {
            Set<String> actual = MyCommonUtils.parseStringArray(array);
            assertEquals("Size should be 3!", 3, actual.size());
            assertEquals("Should be equals!", expected, actual);
        });
    }

    @Test
    public void testSortMapNullMap() {
        assertNull(MyCommonUtils.sortMapByValue(null, ASC));
    }

    @Test
    public void testSortMapNullMapAndSortType() {
        assertNull(MyCommonUtils.sortMapByValue(null, null));
    }

    @Test
    public void testSortMapEmptyMap() {
        Map result = MyCommonUtils.sortMapByValue(new HashMap<String, Integer>(), null);
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
        map = MyCommonUtils.sortMapByValue(map, null);
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
        MyCommonUtils.sortMapByValue(map, null);
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
        testMap = MyCommonUtils.sortMapByValue(testMap, ASC);
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
        testMap = MyCommonUtils.sortMapByValue(testMap, DESC);
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
        assertNull(MyCommonUtils.removeFromMapByValue(null, null));
    }

    @Test
    public void testRemoveFromMapEmptyMap() {
        Map<String, Integer> emptyMap = new HashMap<>();
        Map<String, Integer> resultMap = MyCommonUtils.removeFromMapByValue(emptyMap, null);

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
        Map<String, Integer> resultMap = MyCommonUtils.removeFromMapByValue(sourceMap, null);

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
        Map<String, Integer> resultMap = MyCommonUtils.removeFromMapByValue(sourceMap, null);

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
        Map<String, Integer> resultMap = MyCommonUtils.removeFromMapByValue(sourceMap, 3);

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
        Map<String, Integer> resultMap = MyCommonUtils.removeFromMapByValue(sourceMap, 2);

        // checks
        assertTrue(sourceMap == resultMap);
        assertEquals(sourceMapSize - 1, resultMap.size());
        assertFalse(resultMap.containsKey("ddd"));
        assertFalse(resultMap.containsKey(2));
    }

    @Test
    @Ignore
    // todo: ignored test!
    public void testGetFixedLenghtName() {
        assertEquals("00012", MyCommonUtils.getFixedLengthName(5, '0', "12"));
        assertEquals("12", MyCommonUtils.getFixedLengthName(1, 'x', "12"));
        assertEquals(null, MyCommonUtils.getFixedLengthName(-1, '0', "12"));
        assertEquals(null, MyCommonUtils.getFixedLengthName(10, '0', ""));
        assertEquals(null, MyCommonUtils.getFixedLengthName(10, '0', String.valueOf(-1)));
        assertEquals(null, MyCommonUtils.getFixedLengthName(10, '0', String.valueOf(0)));
        assertEquals("0000001234", MyCommonUtils.getFixedLengthName(10, '0', String.valueOf(1234)));
    }

}