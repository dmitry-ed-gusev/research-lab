package gusev.dmitry.jtils.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests (JUnit) for CommonUtils class methods.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 07.08.2014)
*/

public class CommonUtilsTest {

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