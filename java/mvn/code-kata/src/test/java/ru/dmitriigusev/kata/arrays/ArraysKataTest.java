package ru.dmitriigusev.kata.arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/***/

class ArraysKataTest {

    @Test
    @DisplayName("testAllStringSymbolsUniqueThrowException(): test for IllegalArgumentException")
    void testAllStringSymbolsUniqueThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArraysKata.areAllStringSymbolsUnique(null);
        });
    }

    @ParameterizedTest(name = "testAllStringSymbolsUnique(): test for string \"{0}\", expected result -> {1}")
    @CsvSource({
            "x, true",
            "aaa, false",
            "ab, true",
            "'string one', false",
            "'zxcvbnm asdfghjklqwertyuio', true"
    })
    void testAllStringSymbolsUnique(String stringUnderTest, boolean expectedResult) {
        assertEquals(expectedResult, ArraysKata.areAllStringSymbolsUnique(stringUnderTest),
                () -> stringUnderTest + " -> all symbols are unique: " + expectedResult);
    }

    @Test
    @DisplayName("testAllStringSymbolsUniqueLessMemoryThrowException(): test for IllegalArgumentException")
    void testAllStringSymbolsUniqueLessMemoryThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArraysKata.areAllStringSymbolsUniqueLessMemory(null);
        });
    }

    @ParameterizedTest(name = "testAllStringSymbolsLessMemoryUnique(): test for string \"{0}\", expected result -> {1}")
    @CsvSource({
            "x, true",
            "aaa, false",
            "ab, true",
            "'string one', false",
            "'zxcvbnm asdfghjklqwertyuio', true"
    })
    void testAllStringSymbolsUniqueLessMemory(String stringUnderTest, boolean expectedResult) {
        assertEquals(expectedResult, ArraysKata.areAllStringSymbolsUniqueLessMemory(stringUnderTest),
                () -> stringUnderTest + " -> all symbols are unique: " + expectedResult);
    }

}
