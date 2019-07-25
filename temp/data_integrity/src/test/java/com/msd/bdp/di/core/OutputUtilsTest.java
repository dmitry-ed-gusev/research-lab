/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.di.core;

import org.junit.Test;

/** Unit tests for OutputUtils class. */
public class OutputUtilsTest {

    @Test
    public void test_math(){
        double max = 6;
        double min = 0;
        double diff = Math.abs(((max / min) - 1) * 100);
        if(Double.isInfinite(diff)){
            diff = 100;
        }
        System.out.println(String.format("Target table has %.4f %% difference from the source table.", diff));
    }
}
