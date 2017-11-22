package gusevdm.nlp;

import gusev.dmitry.jtils.nlp.NGramma;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/***/
public class NGrammaTest {

    @Test
    public void testNgrammaEquality() {
        String[][] ngrams = {{"word1", "word2"}, {"word1", null}, {null, "word1"},
                {null, null}, {"wordNN", "   "}, {"   ", "wordNN"}};

        Arrays.stream(ngrams).forEach(ngram -> {
            NGramma nGramma1 = new NGramma(ngram);
            NGramma nGramma2 = new NGramma(ngram);
            assertTrue("Should be equals!", nGramma1.equals(nGramma2));
            assertTrue("Should have the same hash code!",
                    nGramma1.hashCode() == nGramma2.hashCode());
        });

    }

}
