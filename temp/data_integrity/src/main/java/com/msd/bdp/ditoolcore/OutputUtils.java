/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.msd.bdp.ditoolcore.DIToolUtilities.FAIL_CODE;

public class OutputUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputUtils.class);
    private File outputFolder;

    /**
     * Method created the folder based on the specified path and write output to this folder
     *
     * @param path path for the folder
     */
    public OutputUtils(String path) {
        String folderPath = StringUtils.isBlank(path) ? "./files" : path;

        outputFolder = new File(folderPath);
        try {
            if (outputFolder.exists()) {
                FileUtils.deleteDirectory(outputFolder);
            }

            if (!outputFolder.mkdirs()) {
                LOGGER.error("The folder '{}' could not be created.", outputFolder);
                System.exit(FAIL_CODE);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to delete the folder", e);
            System.exit(FAIL_CODE);
        }
    }


    public synchronized void writeToCSV(List<String> listOfDifferences, String fileName) {
        LOGGER.info("Writing the difference to the file: {}", fileName);
        File outputFile = new File(outputFolder ,  fileName + ".csv");
        try {
            Files.write((String.join("\n", listOfDifferences) + "\n").getBytes(), outputFile);
        } catch (IOException e) {
            LOGGER.error("Unable to write to CSV file", e);
        }
    }

    public void createHtmlReport(String content) {
        LOGGER.info("Creating the report page: {}", "log.html");
        File outputFile = new File(outputFolder , "log.html");
        try {
            Files.write(content, outputFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Unable to create the report page", e);
        }
    }

}
