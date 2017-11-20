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
import static gusevdm.nlp.NLPUtils.DEFAULT_GARBAGE_WORDS;
import static gusevdm.nlp.NLPUtils.not;

// todo: 1. add cmd line options (on/off steps)
// todo: 2. out resulting map to file, remove text "NGramms..." from output
// todo: 3. move to config file list of unnecessary (garbage words)
// todo: 4. move to config (outer file) lenght of ngramms
// todo: 5. move to cmd line arguments in/out files names

/***/
public class NLPProcessor {

    private static final Log    LOG              = LogFactory.getLog(NLPProcessor.class);

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
    public static void cleanInputData(String inputFile, String outputFile, String inputFileEncoding, int progressCounter) {
        LOG.debug("NLPProcessor.cleanInputData() is working.");
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
                        .filter(word -> !NLPUtils.in(word, true, DEFAULT_GARBAGE_WORDS))  // remove garbage
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
        LOG.debug(String.format("NLPProcessor.buildNgramsMap() is working. Cured data file [%s].", curedDataFile));

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

    /***/
    public static void main(String[] args) {
        LOG.info("NLPProcessor is starting...");

        // input and output files
        String inputFile     = "c:/temp/payments/nazn.txt";
        String curedDataFile = "c:/temp/payments/curedData.txt";
        String ngramsFile    = "c:/temp/payments/ngrams.txt";

        try {
            // todo: add config loading and getting needed values
            // delete output file if exists
            NLPProcessor.deleteFileIfExist(curedDataFile);
            // clean input data (rewrite them in output file)
            NLPProcessor.cleanInputData(inputFile, curedDataFile, "", 1);

            // delete ingrams file
            NLPProcessor.deleteFileIfExist(ngramsFile);
            // build ngrams on cured data
            NLPProcessor.buildNgramsMap(curedDataFile, ngramsFile, "", 1);

        } catch (IOException e) {
            LOG.error(e);
        }

    } // end of main()

}
