package gusevdm.nlp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gusevdm.nlp.NLPUtils.GARBAGE_WORDS;
import static gusevdm.nlp.NLPUtils.not;

/***/

public class NLPProcessor {

    private static Log LOG = LogFactory.getLog(NLPProcessor.class);

    /***/
    public static void main(String[] args) {
        LOG.info("NLPProcessor is starting...");

        // read input file line-by-line
        String inputFile = "c:/temp/nazn.txt";
        String encoding = "windows-1251";

        // todo: !!!
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding))) {

            String rawLine;
            while ((rawLine = reader.readLine()) != null) {

                // 1. split input line into array (by any space-like separator) and create a stream
                // 2. remove all special characters
                // 3. filter out (remove) any empty and null strings
                // 4. filter out (remove) any numbers/dates
                // 5. filter out (remove) words with length = 1
                // 6. filter out (remove) words from GARBAGE list, ignoring case
                List<String> dataLine = Arrays.stream(StringUtils.split(rawLine))
                        .map(NLPUtils::cleanSpecialChars)    // special chars from words
                        .filter(not(StringUtils::isBlank))   // empty words
                        .filter(not(StringUtils::isNumeric)) // numbers
                        .filter(word -> word.length() > 1)   // short words
                        .filter(word -> !NLPUtils.in(word, true, GARBAGE_WORDS))  // remove garbage
                        .collect(Collectors.toList());       // resulting list

                // print resulting data line (cured)
                System.out.println("-> " + dataLine);
            }
        } catch (IOException e) {
            LOG.error(e);
        }

    }

}
