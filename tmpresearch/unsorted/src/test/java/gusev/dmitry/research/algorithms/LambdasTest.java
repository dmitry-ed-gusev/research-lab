package gusev.dmitry.research.algorithms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Lambdas class.
 * Created by Dmitrii_Gusev on 7/13/2017.
 */
public class LambdasTest {

    @Test (expected = IllegalArgumentException.class)
    public void addUpNullStreamTest() {
        Lambdas.addUp(null);
    }

    @Test
    public void addUpEmptyStreamTest() {
        assertEquals(0, Lambdas.addUp(new ArrayList<Integer>().stream()));
    }

    @Test
    public void addUpTest() {
        List<Integer> intsList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(45, Lambdas.addUp(intsList.stream()));
    }

    @Test
    public void countLowerCaseTest() {
        // boundary cases
        assertEquals(0, Lambdas.countLowerCase(null));
        assertEquals(0, Lambdas.countLowerCase(""));
        assertEquals(0, Lambdas.countLowerCase("      "));

        // normal cases
        assertEquals(7, Lambdas.countLowerCase("Free Price"));
        assertEquals(7, Lambdas.countLowerCase("   zZz Привет!"));
    }

    @Test
    public void getLongestStringTest() {
        Optional empty = Optional.empty();

        // boundary cases
        Optional<String> result1 = Lambdas.getLongestLowerCaseString(null);
        assertFalse(result1.isPresent());
        assertEquals(empty, result1);

        Optional<String> result2 = Lambdas.getLongestLowerCaseString(new ArrayList<String> ());
        assertFalse(result1.isPresent());
        assertEquals(empty, result1);

        // usual cases
        String str = "  zzz zzz zzz   ";
        List<String> list = Arrays.asList(str, "Feel free", "dj    ");
        Optional<String> result3 = Lambdas.getLongestLowerCaseString(list);
        assertTrue("Value should present!", result3.isPresent());
        assertEquals(str, result3.get());
    }

}
