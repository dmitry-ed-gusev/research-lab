package gusevdm.nlp;

import java.util.List;
import java.util.function.Predicate;

public final class NLPUtils {

    // some not useful words
    public static String[] GARBAGE = {
            "по", "за", "За", "от", "на", "не", "т.ч.", "г.", "НДС", "Сумма",
            "Без", "без", "облагается", "б/н", "года", "Оплата", "оплата", "сч."
    };

    // punctuation symbols. for [] braces we have to add more groups to regex.
    public static String PUNCTUATION_REGEX = "[!?\"'@#$%&^*+-.,\\\\/;:<=>{|}()_]|[\\[]|[\\]]";
    //
    public static String NUMBER_ADDITION_REGEX = "(г)|(руб)|(В)";

    private NLPUtils() {} // non-instanceability

    /** Define predicate negation. */
    public static<T> Predicate<T> not(Predicate<T> p) {
        return t -> !p.test(t);
    }

    /***/
    public static boolean in(String str, String[] list) {
        if (list == null || list.length <= 0) { // fast check
            return false;
        }

        // search str in a given list
        for (String s : list) {
            if (s == null) { // both are null
                if (str == null) {
                    return true;
                }
            } else if (s.equals(str)) {
                return true;
            }
        } // end of FOR

        return false; // not found, if come here
    }

    /***/
    public static List<String[]> ngrams() {
        return null;
    }

}
