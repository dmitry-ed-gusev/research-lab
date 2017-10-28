package gusevdm.nlp;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Arrays;

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

    }

}
