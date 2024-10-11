package ru.dmitriigusev.kata.arrays;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/***/
public final class ArraysKata {

    private ArraysKata() {}

    /** Determine, if each symbol in the string appears only once. */
    public static boolean areAllStringSymbolsUnique(String str) {

        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Provided empty / null string!");
        }

        if (str.length() == 1) { // string with just one symbol should return true immediately
            return true;
        }

        Map<Character, Integer> symbolsMap = new HashMap<>();
        char[] stringChars = str.toCharArray();

        int counter;
        for (int i = 0; i < str.length(); i++) {
            counter = symbolsMap.getOrDefault(stringChars[i], 0);
            if (counter > 0) {
                return false;
            } else {
                symbolsMap.put(stringChars[i], counter + 1);
            }

        } // end of FOR cycle

        return true;
    }

    /**
     * Determine, if each symbol in the string appears only once.
     * This implementation should not use additional data structures.
     */
    public static boolean areAllStringSymbolsUniqueLessMemory(String str) {

        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("Provided empty / null string!");
        }

        if (str.length() == 1) { // string with just one symbol should return true immediately
            return true;
        }

        char[] stringChars = str.toCharArray();
        int position;
        for (int i = 0; i < str.length(); i++) {
            position = str.indexOf(stringChars[i]); // first occurrence of the character
            if (str.indexOf(stringChars[i], position + 1) > 0) { // second search - if char exists, it isn't unique
                return false;
            }
        } // end of FOR cycle

        return true;
    }

}
