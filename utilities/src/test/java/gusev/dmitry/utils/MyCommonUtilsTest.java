package gusev.dmitry.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static gusev.dmitry.utils.MyCommonUtils.MapSortType.ASC;
import static gusev.dmitry.utils.MyCommonUtils.MapSortType.DESC;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
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

    /** Helper method for tests getMonthDateRange(). */
    private static void dateRangeHelper(int startDelta, int endDelta) {

        Pair<Date, Date> monthRange = MyCommonUtils.getMonthDateRange(startDelta, endDelta); // preparing test data
        Calendar testCalendar = GregorianCalendar.getInstance();  // calendar instance for test

        Date currentDate = new Date(); // preparing standard data
        Calendar currentCalendar = GregorianCalendar.getInstance(); // calendar with current (standard) date/time

        // testing start date
        currentCalendar.setTime(currentDate);
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        currentCalendar.add(Calendar.MONTH, startDelta);
        testCalendar.setTime(monthRange.getLeft());
        assertEquals(String.format("Day is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(String.format("Month is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.MONTH));
        assertEquals(String.format("Year is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.YEAR));
        assertEquals(String.format("Hour is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(String.format("Minute is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MINUTE), 0);
        assertEquals(String.format("Second is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.SECOND), 0);
        assertEquals(String.format("Millisecond is invalid (start)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MILLISECOND), 0);

        // testing end date
        currentCalendar.setTime(currentDate);
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        currentCalendar.add(Calendar.MONTH, endDelta);
        testCalendar.setTime(monthRange.getRight());
        assertEquals(String.format("Day is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(String.format("Month is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.MONTH));
        assertEquals(String.format("Year is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.YEAR));
        assertEquals(String.format("Hour is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.HOUR_OF_DAY), 23);
        assertEquals(String.format("Minute is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MINUTE), 59);
        assertEquals(String.format("Second is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.SECOND), 59);
        assertEquals(String.format("Millisecond is invalid (end)! Delta [%s, %s].", startDelta, endDelta),
                testCalendar.get(Calendar.MILLISECOND), 999);
    }

    @Test
    public void testGetMonthDateRange() {
        MyCommonUtilsTest.dateRangeHelper(0, 0);
        MyCommonUtilsTest.dateRangeHelper(1, 1);
        MyCommonUtilsTest.dateRangeHelper(9, 9);
        MyCommonUtilsTest.dateRangeHelper(-1, -1);
        MyCommonUtilsTest.dateRangeHelper(-13, -13);
        MyCommonUtilsTest.dateRangeHelper(0, 2);
        MyCommonUtilsTest.dateRangeHelper(4, 0);
        MyCommonUtilsTest.dateRangeHelper(-0, -9);
        MyCommonUtilsTest.dateRangeHelper(-1, 0);
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
}