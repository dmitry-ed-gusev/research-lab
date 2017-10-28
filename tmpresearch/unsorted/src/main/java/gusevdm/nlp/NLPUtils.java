package gusevdm.nlp;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;

public final class NLPUtils {

    // some not useful words (garbage)
    public static final String[] GARBAGE_WORDS = {
            "по", "за", "из", "от", "на", "не", "тч", "г", "ндс", "Сумма", "без", "облагается",
            "бн", "года", "оплата", "сч", "out", "in", "руб", "счф", "дог",
            "январь", "февраль", "март", "апрель", "май", "июнь",
            "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
    };

    // special characters regex. for [] braces we have to add more groups to regex.
    public static final String SPECIAL_CHARS_REGEX = "[!?\"'`~@#$%&^*+-.,\\\\/;:<=>{|}()_№]|[\\[]|[\\]]";

    private NLPUtils() {} // non-instanceability

    /** Define predicate negation. */
    public static<T> Predicate<T> not(Predicate<T> p) {
        return t -> !p.test(t);
    }

    /**
     * Function-predicate IN - is given string in a list.
     */
    public static boolean in(String str, boolean ignoreCase, String[] list) {

        if (list == null || list.length <= 0) { // fast check
            return false;
        }

        // search str in a given list
        for (String s : list) {
            if (s == null) { // both are null
                if (str == null) {
                    return true;
                }
            } else if ((ignoreCase && s.equalsIgnoreCase(str)) || s.equals(str)) {
                return true;
            }
        } // end of FOR

        return false; // not found, if come here
    }

    /**
     * Remove special characters {@link #SPECIAL_CHARS_REGEX} and spaces/line endings from provided string.
     * If provided string is only whitespaces, empty string or null - return null.
     */
    public static String cleanSpecialChars(String string) {

        if(StringUtils.isBlank(string)) { // fast check
            return null;
        }
        // remove special chars by regex
        String result = StringUtils.removeAll(StringUtils.trimToEmpty(string), SPECIAL_CHARS_REGEX);

        if (StringUtils.isBlank(result)) { // check string after cleaning
            return null;
        } else {
            return result;
        }

    }

    /***/
    public static List<String[]> ngrams() {
        return null;
    }

}
