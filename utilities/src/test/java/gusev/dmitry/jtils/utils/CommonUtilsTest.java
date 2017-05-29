package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests (JUnit) for CommonUtils class methods.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 07.08.2014)
*/

public class CommonUtilsTest {

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
            Set<String> result = CommonUtils.parseStringArray(array);
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
            Set<String> actual = CommonUtils.parseStringArray(array);
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
            Set<String> actual = CommonUtils.parseStringArray(array);
            assertEquals("Size should be 3!", 3, actual.size());
            assertEquals("Should be equals!", expected, actual);
        });
    }

    /** Helper method for tests getMonthDateRange(). */
    private static void dateRangeHelper(int startDelta, int endDelta) {

        Pair<Date, Date> monthRange = CommonUtils.getMonthDateRange(startDelta, endDelta); // preparing test data
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
    public void getMonthDateRangeTest() {
        CommonUtilsTest.dateRangeHelper(0, 0);
        CommonUtilsTest.dateRangeHelper(1, 1);
        CommonUtilsTest.dateRangeHelper(9, 9);
        CommonUtilsTest.dateRangeHelper(-1, -1);
        CommonUtilsTest.dateRangeHelper(-13, -13);
        CommonUtilsTest.dateRangeHelper(0, 2);
        CommonUtilsTest.dateRangeHelper(4, 0);
        CommonUtilsTest.dateRangeHelper(-0, -9);
        CommonUtilsTest.dateRangeHelper(-1, 0);
    }

}