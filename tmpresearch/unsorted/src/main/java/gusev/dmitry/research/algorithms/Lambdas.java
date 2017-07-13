package gusev.dmitry.research.algorithms;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    static Optional<String> getLongestString(List<String> strings) {
        return null;
    }

}
