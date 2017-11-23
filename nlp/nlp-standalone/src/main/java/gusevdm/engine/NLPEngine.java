package gusevdm.engine;

import gusev.dmitry.jtils.nlp.NGramma;
import gusev.dmitry.jtils.nlp.NLPUtils;
import gusev.dmitry.jtils.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gusev.dmitry.jtils.utils.MapUtils.SortType.DESC;

public class NLPEngine {

    private static final Log LOG = LogFactory.getLog(NLPEngine.class);

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
