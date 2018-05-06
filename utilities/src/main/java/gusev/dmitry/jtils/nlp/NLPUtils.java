package gusev.dmitry.jtils.nlp;

import gusev.dmitry.jtils.utils.MapUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static gusev.dmitry.jtils.utils.LambdaUtils.not;
import static gusev.dmitry.jtils.utils.MapUtils.SortType.DESC;

/**
 * NLP utilities. Used for processing natural language.
 */
public final class NLPUtils {

    // module logger
    private static final Log LOG = LogFactory.getLog(NLPUtils.class);

    // some not useful words (garbage)
    public static final String[] DEFAULT_GARBAGE_WORDS = {
            "по", "за", "из", "от", "на", "не", "тч", "г", "ндс", "Сумма", "без", "облагается",
            "бн", "года", "оплата", "сч", "out", "in", "руб", "счф", "дог", "сумма", "мо", "ед",
            "тн", "вп", "том", "числе", "счет", "счету", "для", "сф", "втч",
            "январь", "февраль", "март", "апрель", "май", "июнь",
            "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
    };

    // letters with same form in english and russian
    public static final Map<Character, Character> LETTERS = new HashMap<Character, Character>() {{
        put('a', 'а');
        put('b', 'в');
        put('c', 'с');
        put('d', 'д');
        put('e', 'е');
        put('f', 'ф');
        put('g', 'д');
        put('h', 'п');
        put('i', 'и');
        put('j', 'й');
        put('k', 'к');
        put('l', 'и');
        put('m', 'м');
        put('n', 'п');
        put('o', 'о');
        put('p', 'р');
        put('q', 'д');
        put('r', 'г');
        put('s', 'с');
        put('t', 'т');
        put('u', 'и');
        put('v', 'в');
        put('w', 'ш');
        put('x', 'х');
        put('y', 'у');
        put('z', 'з');
    }};

    // special characters regex. for [] braces we have to add more groups to regex.
    public static final String SPECIAL_CHARS_REGEX = "[!?\"'`~@#$%&^*+-.,\\\\/;:<=>{|}()_№]|[\\[]|[\\]]";

    private NLPUtils() { // non-instanceability
    }

    /**
     * Function-predicate IN - is given string in a list.
     * Function can compare ignoring case or not, takes into account spaces and other special characters.
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

    /***/
    // todo: implement
    public static boolean in(String[] list, boolean ignoreCase, String str) {
        throw new NotImplementedException("not implemented");
    }

    /**
     * Remove special characters {@link #SPECIAL_CHARS_REGEX} and spaces/line endings from provided string.
     * If provided string is only whitespaces, empty string or null - return null.
     */
    public static String cleanSpecialChars(String string) {

        if (StringUtils.isBlank(string)) { // fast check
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

    /**
     * Remove decimal digits 0-9 from provided string.
     * If provided string is only whitespaces, empty string or null - return null.
     */
    public static String cleanNumbers(String string) {

        if (StringUtils.isBlank(string)) { // fast check
            return null;
        }

        // remove digits
        String result = StringUtils.removeAll(StringUtils.trimToEmpty(string), "[0-9]");

        if (StringUtils.isBlank(result)) { // check string after cleaning
            return null;
        } else {
            return result;
        }

    }

    /**
     * If input list is null or empty - return empty list.
     * If n < 2 or n >= input list size - return the whole input list as ngram.
     */
    public static List<NGramma> ngrams(List<String> sourceList, int n) {

        if (sourceList == null || sourceList.isEmpty()) { // fast check #1
            return Collections.emptyList();
        }

        if (n < 2 || n >= sourceList.size()) { // fast check #2
            List<NGramma> result = new ArrayList<>();
            result.add(new NGramma(sourceList.toArray(new String[sourceList.size()])));
            return result;
        }

        List<NGramma> ngrams = new ArrayList<>(); // init resulting list
        for (int i = 0; i < sourceList.size() - n + 1; i++) { // iterate through source list and create ngrams
            ngrams.add(new NGramma(sourceList.subList(i, i + n).toArray(new String[n])));
        }
        return ngrams;
    }

    /**
     * Fix russian words with same-written english characters. Output word will be lower-cased,
     * leading and trailing spaces will be removed.
     * If input word is empty (null, empty string, only spaces), method will return it without changes.
     * If input word doesn't contain english letters - return it as is (no changes).
     * If input word doesn't contain russian letters (only different) - return as is (no changes).
     */
    public static String fixRussianWord(String inWord) {
        if (StringUtils.isBlank(inWord)) { // fast-check
            return inWord;
        }

        String internalInWord = StringUtils.trimToEmpty(inWord.toLowerCase());
        // no english letters in a word, only english letters in a word
        if (!internalInWord.matches(".*[a-z].*") ||                     // <- no english letters
                !internalInWord.toLowerCase().matches(".*[^a-z].*")) {  // <- only english letters
            return inWord;
        }

        // process word letter-by-letter and change english letters with russian
        char[] inWordCharArray = internalInWord.toCharArray();
        char[] outWordCharArray = new char[inWordCharArray.length];
        for (int counter = 0; counter < inWordCharArray.length; counter++) {
            Character replacement = LETTERS.get(inWordCharArray[counter]);
            if (replacement != null) {
                outWordCharArray[counter] = replacement;
            } else {
                outWordCharArray[counter] = inWordCharArray[counter];
            }
        }

        String outWord = new String(outWordCharArray);
        LOG.debug(String.format("[%s] -> [%s]", inWord, outWord)); // <- too much output

        return outWord;
    }

    /**
     * If input line is empty - return it as is, no changes.
     * 1. split input line into array (by any space-like separator) and create a stream
     * 2. remove all special characters
     * 3. remove all decimal digits
     * 4. filter out (remove) any empty and null strings
     * 5. cast all words to lower case
     * 6. filter out (remove) words with length = 1
     * 7. filter out (remove) words from GARBAGE list, ignoring case
     */
    public static String cleanString(String inString) {

        if (StringUtils.isBlank(inString)) {
            return inString;
        }

        // clean line and return it
        return Arrays.stream(StringUtils.split(inString))
                .map(NLPUtils::cleanSpecialChars)    // special chars from words
                .map(NLPUtils::cleanNumbers)         // clean decimal digits
                .filter(not(StringUtils::isBlank))   // empty words
                .map(String::toLowerCase)            // all words to lower case
                .filter(word -> word.length() > 1)   // short words
                .filter(word -> !NLPUtils.in(word, true, DEFAULT_GARBAGE_WORDS))  // remove garbage
                .sorted()
                .collect(Collectors.joining(" "));       // resulting string
    }

    /***/
    public static void cleanInputData(String inputFile, String outputFile, String inputFileEncoding, int progressCounter) {
        LOG.debug("AppMain.cleanInputData() is working.");
        LOG.debug(String.format("Input file [%s], output file [%s].", inputFile, outputFile));

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inputFile), inputFileEncoding));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile), inputFileEncoding))) {

            String rawLine;
            String dataLine;
            int counter = 0;
            while ((rawLine = reader.readLine()) != null) {
                dataLine = NLPUtils.cleanString(rawLine);
                // write data line to output file
                writer.write(dataLine);
                writer.newLine();
                counter++;

                if (counter % progressCounter == 0) {
                    LOG.info(String.format("Processed records [%s].", counter));
                }

            } // END OF WHILE

            LOG.info(String.format("Total records [%s].", counter));

        } catch (IOException e) {
            LOG.error(e);
        }

    }

    /***/
    public static void buildNgramsMap(String curedDataFile, String ngramsFile, String encoding, int progressCounter) {
        LOG.debug(String.format("AppMain.buildNgramsMap() is working. Cured data file [%s].", curedDataFile));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(curedDataFile), encoding))) {

            String rawLine;
            int counter = 0;
            Integer count;
            List<NGramma> oneLineNGrams;
            Map<NGramma, Integer> ngramsMap = new HashMap<>();

            while ((rawLine = reader.readLine()) != null) {

                // create ngrams from one source line
                oneLineNGrams = NLPUtils.ngrams(Arrays.asList(StringUtils.split(rawLine)), 2);

                //System.out.println("-> " + oneLineNGrams);

                for (NGramma nGramma : oneLineNGrams) {
                    count = ngramsMap.get(nGramma);
                    ngramsMap.put(nGramma, count == null ? 1 : count + 1);
                }

                counter++;
                if (counter % (progressCounter / 10) == 0) {
                    LOG.info(String.format("Processed records [%s].", counter));
                }

            } // end of while()
            LOG.info(String.format("Total records [%s].", counter));

            // sort resulting map by values
            ngramsMap = MapUtils.sortMapByValue(ngramsMap, DESC);
            // out result
            //System.out.println(ngramsMap.toString());
            int outCounter = 0;
            int limit = 10000;
            String outLine;

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(ngramsFile), encoding))) {

                for (Map.Entry<NGramma, Integer> entry : ngramsMap.entrySet()) {
                    // generate output string
                    outLine = String.format("%s -> %s", Arrays.toString(entry.getKey().getContent()), entry.getValue());
                    // out to console
                    System.out.println(outLine);
                    // out to file
                    writer.write(outLine);
                    writer.newLine();

                    outCounter++;

                    if (outCounter >= limit) {
                        break;
                    }

                } // end of FOR
            } catch (IOException e) {
                LOG.error(e);
            }

        } catch (IOException e) {
            LOG.error(e);
        }

    }

}
