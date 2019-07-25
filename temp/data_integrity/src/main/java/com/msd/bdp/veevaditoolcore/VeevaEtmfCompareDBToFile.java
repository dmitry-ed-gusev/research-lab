/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object to compare Database with downloaded file from Veeva server
 */
class VeevaEtmfCompareDBToFile {

    private Map<File, Scanner> fileScanners = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(VeevaEtmfCompareDBToFile.class);

    public VeevaEtmfCompareDBToFile(VeevaEtmfSourceConfig sourceFileConfig) throws FileNotFoundException {
        setupFilesScanner(sourceFileConfig);
    }

    private void setupFilesScanner(VeevaEtmfSourceConfig sourceFileConfig) throws FileNotFoundException {
        File[] csvFiles = new File(sourceFileConfig.getTempDir())
                .listFiles((File file) -> file.getName().startsWith(sourceFileConfig.getJsonFilePrefix())
                        && file.getName().endsWith(".csv"));
        for (File csvFile : csvFiles) {
            fileScanners.put(csvFile, new Scanner(csvFile));
        }
    }

    /**
     * Check if DB CSV record found in any Veeva Source File
     * 
     * @param dbCSVRecord
     * @return
     */
    public boolean isResultSetCSVFoundInFiles(final String dbCSVRecord) {
        Set<Entry<File, Scanner>> scannerSet = fileScanners.entrySet();
        boolean foundMatchInCSVFile = false;
        for (Entry<File, Scanner> scannerEntry : scannerSet) {
            Scanner scanner = scannerEntry.getValue();
            String fileName = scannerEntry.getKey().getName();
            scanner.reset();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals(dbCSVRecord)) {
                    foundMatchInCSVFile = true;
                    LOGGER.debug("{} Found, File: {}", dbCSVRecord, fileName);
                    break;
                }
            }
            if (foundMatchInCSVFile) {
                break;
            }

        }
        return foundMatchInCSVFile;
    }
}
