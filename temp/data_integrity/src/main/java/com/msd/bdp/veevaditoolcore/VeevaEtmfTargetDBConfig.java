/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

/**
 * Object for target DB(Oracle, Teradata) checking configuration
 */
public final class VeevaEtmfTargetDBConfig {

	/**
	 * Target DB JDBC URL
	 */
	private String targetDBUrl;

	/**
	 * Target DB User
	 */
	private String targetDbUser;

	/**
	 * Target DB Password
	 */
	private String targetDbPassword;

	/**
	 * Target DB Query to query
	 */
	private String targetDBQuery;

	/**
	 * Target DB Schema
	 */
	private String targetSchema;

	/**
	 * Comparison Output File Name prefix
	 */
	private String outputFileNamePrefix;

	/**
	 * Comparison Output File Name prefix
	 */
	private String outputFolder;

	private VeevaEtmfTargetDBConfig() {
	}
	
	public String getTargetDBUrl() {
		return targetDBUrl;
	}

	public void setTargetDBUrl(String targetDBUrl) {
		this.targetDBUrl = targetDBUrl;
	}

	public String getTargetDbUser() {
		return targetDbUser;
	}

	public void setTargetDbUser(String targetDbUser) {
		this.targetDbUser = targetDbUser;
	}

	public String getTargetDbPassword() {
		return targetDbPassword;
	}

	public void setTargetDbPassword(String targetDbPassword) {
		this.targetDbPassword = targetDbPassword;
	}

	public String getTargetSchema() {
		return targetSchema;
	}

	public void setTargetSchema(String targetSchema) {
		this.targetSchema = targetSchema;
	}

	public String getTargetDBQuery() {
		return targetDBQuery;
	}

	public void setTargetDBQuery(String targetDBQuery) {
		this.targetDBQuery = targetDBQuery;
	}

	public String getOutputFileNamePrefix() {
		return outputFileNamePrefix;
	}

	public void setOutputFileNamePrefix(String outputFileNamePrefix) {
		this.outputFileNamePrefix = outputFileNamePrefix;
	}

	public void setOutputFileDir(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

    /**
     * Factory method that create Configuration Object
     *
     * @return
     */
	public static VeevaEtmfTargetDBConfig getNewInstance() {
		return new VeevaEtmfTargetDBConfig();
	}
	
	public String toString() {
		return "\ntargetDBUrl:" + targetDBUrl + "\ntargetDbUser:" + targetDbUser + "\ntargetDbPassword:*****"
				+ "\ntargetSchema" + targetSchema + "\ntargetDBQuery:" + targetDBQuery + "\noutputFolder:"
				+ outputFolder + "\noutputFileNamePrefix:" + outputFileNamePrefix;

	}
}
