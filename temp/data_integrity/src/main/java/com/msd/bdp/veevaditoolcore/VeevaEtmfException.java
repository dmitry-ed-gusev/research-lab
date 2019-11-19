/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;


/**
 * Veeva Data Integrity Tool Exception
 */
public final class VeevaEtmfException extends RuntimeException {

	private static final long serialVersionUID = 3040794687320446544L;
	
	public VeevaEtmfException(String errorMessage, Exception parentException) {
		super(errorMessage, parentException);
	}

	public VeevaEtmfException(String errorMessage) {
		super(errorMessage);
	}

}
