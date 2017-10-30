package gusevdm.nlp;

import gusev.dmitry.jtils.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gusev.dmitry.jtils.utils.MapUtils.SortType.DESC;
import static gusevdm.nlp.NLPUtils.GARBAGE_WORDS;
import static gusevdm.nlp.NLPUtils.not;

// todo: 1. add cmd line options (on/off steps)
// todo: 2. out resulting map to file, remove text "NGramms..." from output
// todo: 3. move to config file list of unnecessary (garbage words)
// todo: 4. move to config (outer file) lenght of ngramms
// todo: 5. move to cmd line arguments in/out files names

/***/
public class NLPProcessor {

    private static final Log    LOG              = LogFactory.getLog(NLPProcessor.class);
    private static final String DEFAULT_ENCODING = "windows-1251";
    private static final int    PROGRESS_COUNTER = 1_000_000;

    /***/
    // todo: move it to common utilities
    public static void deleteFileIfExist(String fileName) throws IOException {
        LOG.debug(String.format("NLPProcessor.deleteFileIfExist() is working. File [%s].", fileName));

        File file = new File(fileName);
        if (file.exists()) {
            boolean isDeleteOK = file.delete();
            LOG.info(String.format("File [%s] exists. Removing -> [%s].", fileName, isDeleteOK ? "OK" : "Fail"));
            if (!isDeleteOK) { // if can't delete - we won't process.
                //LOG.error(String.format("Cant't delete file [%s] by unknown reason!", fileName));
                throw new IOException(String.format("Cant't delete file [%s] by unknown reason!", fileName));
            }
        }

    }

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
                // 5. cast all words to lower case
                // 6. filter out (remove) words with length = 1
                // 7. filter out (remove) words from GARBAGE list, ignoring case
                dataLine = Arrays.stream(StringUtils.split(rawLine))
                        .map(NLPUtils::cleanSpecialChars)    // special chars from words
                        .map(NLPUtils::cleanNumbers)         // clean decimal digits
                        .filter(not(StringUtils::isBlank))   // empty words
                        .map(String::toLowerCase)            // all words to lower case
                        .filter(word -> word.length() > 1)   // short words
                        .filter(word -> !NLPUtils.in(word, true, GARBAGE_WORDS))  // remove garbage
                        .sorted()
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

            LOG.info(String.format("Total records [%s].", counter));

        } catch (IOException e) {
            LOG.error(e);
        }

    }

    /***/
    public static void buildNgramsMap(String curedDataFile) {
        LOG.debug(String.format("NLPProcessor.buildNgramsMap() is working. Cured data file [%s].", curedDataFile));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(curedDataFile), DEFAULT_ENCODING))) {

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
                if (counter % (PROGRESS_COUNTER / 10) == 0) {
                    LOG.info(String.format("Processed records [%s].", counter));
                }

            } // end of while()
            LOG.info(String.format("Total records [%s].", counter));

            // sort resulting map by values
            ngramsMap = MapUtils.sortMapByValue(ngramsMap, DESC);
            // out result
            //System.out.println(ngramsMap.toString());
            int outCounter = 0;
            int limit = 1000;
            for (Map.Entry<NGramma, Integer> entry: ngramsMap.entrySet()) {
                System.out.println(String.format("%s -> %s", entry.getKey(), entry.getValue()));
                outCounter++;

                if (outCounter >= limit) {
                    break;
                }

            }

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

        try {
            // delete output file if exists
            NLPProcessor.deleteFileIfExist(outputFile);

            // clean input data (rewrite them in output file)
            NLPProcessor.cleanInputData(inputFile, outputFile);

            // build ngrams on cured data
            NLPProcessor.buildNgramsMap(outputFile);

        } catch (/*IOException*/Throwable e) {
            LOG.error(e);
        }

    } // end of main()

}
