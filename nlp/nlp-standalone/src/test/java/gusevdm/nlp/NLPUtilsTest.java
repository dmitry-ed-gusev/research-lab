package gusevdm.nlp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NLPUtilsTest {

    private static final String SPECIAL_CHARS = "!?\"'`~@#$%&^*+-.,\\/;:<=>{|}()_№[]";

    @Test
    // todo: add tests for multiple special chars
    public void testCleanSpecialChars() {
        // sources for tests
        String[] sources = {" abc123%s456def   ", "   %sabc123456def ", " abc123456def%s    "};

        for (String source: sources) {
            String expected = String.format(StringUtils.trimToEmpty(source), "");
            // test special chars one-by-one
            Arrays.stream(SPECIAL_CHARS.split(""))
                    .forEach(symbol -> assertEquals(expected, NLPUtils.cleanSpecialChars(String.format(source, symbol))));
        }
    }

    @Test
    // todo: implement tests!
    public void testIN() {
    }

    @Test
    // todo: implement tests!
    public void testNGrams() {
        List<String> sourceList = Arrays.asList("word1", "word2", "word3", "word4");

       NLPUtils.ngrams(sourceList, 3).forEach(ngram -> System.out.println(ngram));
    }

}
