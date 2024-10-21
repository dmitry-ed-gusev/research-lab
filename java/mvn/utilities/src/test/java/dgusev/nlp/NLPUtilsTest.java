package dgusev.nlp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NLPUtilsTest {

    private static final String SPECIAL_CHARS = "!?\"'`~@#$%&^*+-.,\\/;:<=>{|}()_№[]";

    @Test
    // todo: add tests for multiple special chars
    public void testCleanSpecialChars() {
        // sources for tests
        String[] sources = {" abc123%s456def   ", "   %sabc123456def ", " abc123456def%s    "};

        for (String source : sources) {
            String expected = String.format(StringUtils.trimToEmpty(source), "");
            // test special chars one-by-one
            Arrays.stream(SPECIAL_CHARS.split(""))
                    .forEach(symbol -> assertEquals(expected, NLPUtils.cleanSpecialChars(String.format(source, symbol))));
        }
    }

    @Test
    public void testINWithEmptyArrays() {
        String[][] emptyArrays = {null, new String[0]};
        Arrays.stream(emptyArrays).forEach(emptyArray -> assertFalse(NLPUtils.in("string", false, emptyArray)));
    }

    /*
    @Test
    // todo: implement tests!
    public void testNGrams() {
        List<String> sourceList = Arrays.asList("word1", "word2", "word3", "word4");

       NLPUtils.ngrams(sourceList, 3).forEach(ngram -> System.out.println(ngram));
    }
    */

    @Test
    public void testFixRussianWordWithEmptyWords() {
        String[] emptyWords = {null, "", "     ", " "};
        Arrays.stream(emptyWords).forEach(emptyWord -> assertEquals(emptyWord, NLPUtils.fixRussianWord(emptyWord)));
    }

    @Test
    public void testFixRussianWordWithRussian() {
        String[] russianWords = {"  русСкий", "   ёжик ", "привет    ", "просто"};
        Arrays.stream(russianWords).forEach(emptyWord -> assertEquals(emptyWord, NLPUtils.fixRussianWord(emptyWord)));
    }

    @Test
    public void testFixRussianWordWithEnglish() {
        String[] russianWords = {"  zvezda", "   foto ", "hello    ", "dver"};
        Arrays.stream(russianWords).forEach(emptyWord -> assertEquals(emptyWord, NLPUtils.fixRussianWord(emptyWord)));
    }

    @Test
    public void testFixRussianWord() {
        Map<String, String> words = new HashMap<String, String>() {{
            put("  aбразиb",      "абразив");  // added some spaces
            put("русckий   ",     "русский");
            put("   dверь  ",     "дверь");
            put("eгеpь",     "егерь");
            put("fотo",      "фото");
            put("foтo",      "фото");
            put("gереВо",    "дерево");
            put("hрiвет",    "привет");
            put("kнопка",    "кнопка");
            put("Лlмон",     "лимон");
            put("mесяц",     "месяц");
            put("noлка",     "полка");
            put("pакета",    "ракета");
            put("qругой",    "другой");
            put("rеНИЙ",     "гений");
            put("поsледний", "последний");
            put("toлk",      "толк");
            put("uwак",      "ишак");
            put("vетер",     "ветер");
            put("xyй",       "хуй");
            put("звеzда",    "звезда");
        }};

        words.forEach((key, value) -> assertEquals(value, NLPUtils.fixRussianWord(key)));
    }

}
