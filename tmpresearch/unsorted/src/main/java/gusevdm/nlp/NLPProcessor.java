package gusevdm.nlp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static gusevdm.nlp.NLPUtils.PUNCTUATION_REGEX;
import static gusevdm.nlp.NLPUtils.NUMBER_ADDITION_REGEX;
import static gusevdm.nlp.NLPUtils.GARBAGE;


/***/

public class NLPProcessor {

    private static Log LOG = LogFactory.getLog(NLPProcessor.class);

    /***/
    public static void main(String[] args) {
        LOG.info("NLPProcessor is starting...");

        //System.out.println(StringUtils.removeAll("[3.33-5,,5\\5_5]", PUNCTUATION_REGEX));
        //System.exit(777);

        // read input file line-by-line
        String inputFile = "c:/temp/payments/nazn.txt";
        String encoding = "windows-1251";

        // todo: !!!
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // 1. get input line and split it into array (by any space-like separator)
                // 2. remove (filter) array elements "-"/"IN"/"OUT"
                // 3. remove (filter) numbers/dates in most of variations
                // 4. remove words with length = 1
                // 5. remove words from GARBAGE list (prepositions, etc.)
                String[] split = Arrays.stream(StringUtils.split(line))
                        .filter(s -> !s.equalsIgnoreCase("-"))
                        .filter(s -> !s.equalsIgnoreCase("in") && !s.equalsIgnoreCase("out"))
                        .filter(s -> !s.contains("НДС"))
                        .filter(s -> !StringUtils.isNumeric(StringUtils.removeAll(
                                StringUtils.trimToEmpty(s), PUNCTUATION_REGEX + "|" + NUMBER_ADDITION_REGEX)))
                        .filter(s -> s.length() > 1) // remove short words
                        .filter(s -> !NLPUtils.in(s, GARBAGE))  // remove unnecessary words
                        .toArray(String[]::new);

                System.out.println("-> " + Arrays.toString(split));
            }
        } catch (IOException e) {
            LOG.error(e);
        }

    }

}
