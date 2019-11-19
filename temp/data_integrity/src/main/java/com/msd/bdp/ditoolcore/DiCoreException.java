/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore;


/**
 *  Data Integrity Tool Core Exception
 */
public final class DiCoreException extends Exception {

    private static final long serialVersionUID = 3040794687312446544L;

    public DiCoreException(String errorMessage, Exception parentException) {
        super(errorMessage, parentException);
    }

    public DiCoreException(String errorMessage) {
        super(errorMessage);
    }

}
