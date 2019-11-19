/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp;


/**
 *  Data Integrity Tool Exception
 */
public final class DiToolException extends RuntimeException {

    private static final long serialVersionUID = 3040794687312446544L;

    public DiToolException(String errorMessage, Exception parentException) {
        super(errorMessage, parentException);
    }

    public DiToolException(String errorMessage) {
        super(errorMessage);
    }

}
