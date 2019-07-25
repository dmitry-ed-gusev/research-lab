package com.msd.bdp.csvDbIntegrity;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;


class CsvDataParser {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CsvDataParser.class);

    private static final char DEFAULT_SEPARATOR = ',';

    static List<String> parse(List<File> files, boolean hasHeader, int[] pkPositions) throws IOException {
        return parse(files, hasHeader, pkPositions, FunctionProjectList.get(""), DEFAULT_SEPARATOR, '"', true);

    }

    static List<String> parse(List<File> files, boolean hasHeader, int[] pkPositions, Function<String[], String> function,
                              char separator, char quoteChar, boolean haveDelta) throws IOException {

        File pathToRootFile = files.get(0);

        Map<String, String> existingRecords = getRecords(pathToRootFile, hasHeader, pkPositions, function, separator, quoteChar, haveDelta);

        for (int i = 1; i <= files.size() - 1; i++) {//skipping first file as it is already parsed to existingRecords
            Map<String, String> delta = getRecords(files.get(i), hasHeader, pkPositions, function, separator, quoteChar, haveDelta);
            existingRecords.putAll(delta);
        }

        return new ArrayList<>(existingRecords.values());

    }


    private static Map<String, String> getRecords(File csvPath,
                                                  boolean hasHeader,
                                                  int[] pkPositions,
                                                  Function<String[], String> fun, char separator, char quoteChar, boolean haveDelta) throws IOException {


        Map<String, String> data = new HashMap<>();
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .withQuoteChar(quoteChar)
                .build();
        try (CSVReader reader =
                     new CSVReaderBuilder(new InputStreamReader(new FileInputStream(csvPath), StandardCharsets.UTF_8)).
                             withSkipLines(hasHeader ? 1 : 0).
                             withCSVParser(parser).
                             build()) {


            String[] line;
            int lineCount = 0;
            while ((line = reader.readNext()) != null && !String.join("", line).isEmpty()) {
                lineCount++;
                StringBuilder pks = new StringBuilder();
                String record = fun.apply(line);

                if (pkPositions != null && pkPositions.length != 0 && haveDelta) {
                    for (int i = 0; i <= line.length; i++) {

                        if (ArrayUtils.contains(pkPositions, i)) {
                            //this is pk, add to key hash map
                            pks.append(line[i]);
                        }
                    }
                } else { //line number is a fake primary key
                    pks.append(lineCount);
                }
                data.put(pks.toString(), record);
            }
            LOGGER.info("Found {} rows in the csv {}", data.size(), csvPath);

            if (data.size() != lineCount) {
                LOGGER.error("There are {} duplicated records in the CSV {}.", lineCount - data.size(), csvPath);
            }

        }

        return data;
    }

    private CsvDataParser() {
        throw new IllegalStateException("CsvDataParser class");
    }
}
