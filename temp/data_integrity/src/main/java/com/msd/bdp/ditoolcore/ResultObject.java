/*
 * Copyright © 2015 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore;

/**
 * Object that contains result information (sql object that was used, table sizes, file nammes
 * with differences and error message if it was fired) after the table was compared.
 */
public final class ResultObject {

    /**
     * Object contains source sql select, target sql select, source table name, target table name, primary key information
     */
    public final SqlObject sqlObject;

    /**
     * Source table size
     */
    public final int sourceTableSize;

    /**
     * Target table size
     */
    public final int targetTableSize;

    /**
     * Name of the output file with differences from source to target database
     */
    public  String sourceDifferenceFileName;

    /**
     * Name of the ouptu file with difference from target to source database
     */
    public  String targetDifferenceFileName;

    /**
     * Error message if it was fired while table compare was executed.
     */
    public  String errorMessage;

    public ResultObject(SqlObject sqlObject, int sourceTableSize, int targetTableSize, String errorMessage) {
        this.sqlObject = sqlObject;
        this.targetTableSize = targetTableSize;
        this.sourceTableSize = sourceTableSize;
        this.sourceDifferenceFileName = null;
        this.targetDifferenceFileName = null;
        this.errorMessage = errorMessage;

    }

    public ResultObject(SqlObject sqlObject, int sourceTableSize, int targetTableSize, String sourceDifferenceFileName,
                        String targetDifferenceFileName, String errorMessage) {
        this.sqlObject = sqlObject;
        this.targetTableSize = targetTableSize;
        this.sourceTableSize = sourceTableSize;
        this.sourceDifferenceFileName = sourceDifferenceFileName;
        this.targetDifferenceFileName = targetDifferenceFileName;
        this.errorMessage = errorMessage;

    }


    public void setSourceDifferenceFileName(String v){
        this.sourceDifferenceFileName = v;
    }

    public void setTargetDifferenceFileName(String v){
        this.targetDifferenceFileName = v;
    }

    public void setErrorMessage(String v){
        this.errorMessage = v;
    }
}
