package gusev.dmitry.research.algorithms;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Some research with lambdas.
 * Created by Dmitrii_Gusev on 7/13/2017.
 */
public final class Lambdas {

    private Lambdas() {}

    /***/
    static int addUp(Stream<Integer> numbers) {

        if (numbers == null) { // fail-fast
            throw new IllegalArgumentException("Can't sum null stream!");
        }

        return numbers.reduce(0, (accumulator, element) -> accumulator + element);
    }

    /***/
    static long countLowerCase(String string) {

        if (StringUtils.isBlank(string)) {
            return 0;
        }

        return string.chars().filter(Character::isLowerCase).count();
    }

    /***/
    static Optional<String> getLongestLowerCaseString(List<String> strings) {

        if (strings == null || strings.isEmpty()) {
            return Optional.empty();
        }

        return strings.stream().max(Comparator.comparing(Lambdas::countLowerCase));
    }

    /** Implementation of Stream.map() using reduce() and lambdas. */
    static <I, O> List<O> map(Stream<I> stream, Function<I, O> mapper) {
        return stream.reduce(new ArrayList<O>(), (acc, x) -> {
            // We are copying data from acc to new list instance. It is very inefficient,
            // but contract of Stream.reduce method requires that accumulator function does
            // not mutate its arguments.
            // Stream.collect method could be used to implement more efficient mutable reduction,
            // but this exercise asks to use reduce method.
            List<O> newAcc = new ArrayList<>(acc);
            newAcc.add(mapper.apply(x));
            return newAcc;
        }, (List<O> left, List<O> right) -> {
            // We are copying left to new list to avoid mutating it.
            List<O> newLeft = new ArrayList<>(left);
            newLeft.addAll(right);
            return newLeft;
        });
    }

    /** Implementation of Stream.filter() using reduce() and lambdas. */
    static <I> List<I> filter(Stream<I> stream, Predicate<I> predicate) {
        List<I> initial = new ArrayList<>();
        return stream.reduce(initial,
                (List<I> acc, I x) -> {
                    if (predicate.test(x)) {
                        // We are copying data from acc to new list instance. It is very inefficient,
                        // but contract of Stream.reduce method requires that accumulator function does
                        // not mutate its arguments.
                        // Stream.collect method could be used to implement more efficient mutable reduction,
                        // but this exercise asks to use reduce method explicitly.
                        List<I> newAcc = new ArrayList<>(acc);
                        newAcc.add(x);
                        return newAcc;
                    } else {
                        return acc;
                    }
                },
                Lambdas::combineLists);
    }

    private static <I> List<I> combineLists(List<I> left, List<I> right) {
        // We are copying left to new list to avoid mutating it.
        List<I> newLeft = new ArrayList<>(left);
        newLeft.addAll(right);
        return newLeft;
    }

}
