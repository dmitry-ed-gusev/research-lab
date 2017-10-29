package gusevdm.nlp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static gusevdm.nlp.NLPUtils.GARBAGE_WORDS;
import static gusevdm.nlp.NLPUtils.not;

/***/

public class NLPProcessor {

    private static final Log    LOG              = LogFactory.getLog(NLPProcessor.class);
    private static final String DEFAULT_ENCODING = "windows-1251";
    private static final int    PROGRESS_COUNTER = 1_000_000;

    /***/
    public static void cleanInputData(String inputFile, String outputFile) {
        LOG.debug("NLPProcessor.cleanInputData() is working.");
        LOG.debug(String.format("Input file [%s], output file [%s].", inputFile, outputFile));

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inputFile), DEFAULT_ENCODING));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile), DEFAULT_ENCODING))) {

            String rawLine;
            String dataLine;
            int counter = 0;
            while ((rawLine = reader.readLine()) != null) {
                // 1. split input line into array (by any space-like separator) and create a stream
                // 2. remove all special characters
                // 3. remove all decimal digits
                // 4. filter out (remove) any empty and null strings
                // 5. filter out (remove) words with length = 1
                // 6. filter out (remove) words from GARBAGE list, ignoring case
                dataLine = Arrays.stream(StringUtils.split(rawLine))
                        .map(NLPUtils::cleanSpecialChars)    // special chars from words
                        .map(NLPUtils::cleanNumbers)         // clean decimal digits
                        .filter(not(StringUtils::isBlank))   // empty words
                        .filter(word -> word.length() > 1)   // short words
                        .filter(word -> !NLPUtils.in(word, true, GARBAGE_WORDS))  // remove garbage
                        .collect(Collectors.joining(" "));       // resulting string
                // print resulting data line (cured)
                //System.out.println("-> " + dataLine);
                // add data line to all data list
                //allData.addAll(dataLine);
                // write data line to output file
                writer.write(dataLine);
                writer.newLine();
                counter++;

                if (counter % PROGRESS_COUNTER == 0) {
                    LOG.info(String.format("Processed records [%s].", counter));
                }

            } // END OF WHILE

            LOG.info(String.format("Processed records [%s].", counter));

        } catch (IOException e) {
            LOG.error(e);
        }

    }

    /***/
    public static void main(String[] args) {
        LOG.info("NLPProcessor is starting...");

        // input and output files
        String inputFile  = "c:/temp/nazn.txt";
        String outputFile = "c:/temp/output.txt";

        // clean input data (rewrite them in output file)
        NLPProcessor.cleanInputData(inputFile, outputFile);

    }

}
