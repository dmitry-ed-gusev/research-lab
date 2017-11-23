package gusev.dmitry.jtils.utils;

import java.util.function.Predicate;

/***/
public class LambdaUtils {

    /**
     * Define predicate negation.
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return t -> !predicate.test(t);
    }

}
