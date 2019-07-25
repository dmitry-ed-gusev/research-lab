/*
 * Copyright © 2018 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */

package com.msd.bdp.csvDbIntegrity;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/***/
public class CsvReaderTest {

    @Test
    public void csvMergeTest() throws IOException {


        List<File> files = new ArrayList<>();
        files.add(new File("src/test/resources/files/load1/dev-ams-blue.csv"));
        files.add(new File("src/test/resources/files/load2/dev-ams-blue2.csv"));
        int[] pk = new int[]{0};
        List<String> data = CsvDataParser.parse(files, false, pk, FunctionProjectList.get(""), ',','"', true);
        assertEquals(6, data.size());


    }
}
