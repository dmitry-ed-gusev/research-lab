/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

import java.io.File;

/**
 * Object for configuration of connecting to Veeva Server
 */
public final class VeevaEtmfSourceConfig {

	/**
	 * Url for authorization
	 */
	private String authUrl;

	/**
	 * Url for ETMF data query 
	 */
	private String queryUrl;

	/**
	 * Username to login Veeva
	 */
	private String username;
	
	/**
	 * Password to login Veeva
	 */
	private String password;
	
	/**
	 * ETMF query that use in query url
	 */
	private String emtfQuery;
	
	/**
	 * Temporary folder to store the query return result file
	 */
	private String jsonFileTmpDir;
	
	/**
	 * File name prefix and serve as temporary sub folder to store query return result 
	 */
	private String jsonFilePrefix;

	private VeevaEtmfSourceConfig() {
	}

	public String getAuthURL() {
		return authUrl;
	}

	public void setAuthURL(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJsonFilePrefix() {
		return jsonFilePrefix;
	}

	public void setJsonFilePrefix(String jsonFilePrefix) {
		this.jsonFilePrefix = jsonFilePrefix;
	}

	public String getQueryURL() {
		return queryUrl;
	}

	public void setQueryURL(String etmfServerQueryURL) {
		this.queryUrl = etmfServerQueryURL;
	}

	public String getJsonFileTmpDir() {
		return jsonFileTmpDir;
	}

	public void setJsonFileTmpDir(String jsonFileTmpDir) {
		this.jsonFileTmpDir = jsonFileTmpDir;
	}

	public String getEmtfQuery() {
		return emtfQuery;
	}

	public void setEmtfQuery(String emtfQuery) {
		this.emtfQuery = emtfQuery;
	}

	public String getTempDir() {
		return new StringBuilder(jsonFileTmpDir).append(File.separator).append(jsonFilePrefix).toString();
	}

	
	public static VeevaEtmfSourceConfig getNewInstaance() {
		return new VeevaEtmfSourceConfig();
	}

	public String toString() {
		return "\nauthUrl:" + authUrl + "\nusername:" + username + "\npassword:*****" + "\nqueryUrl:" + queryUrl
				+ "\nemtfQuery:" + emtfQuery + "\ntempDir:" + getTempDir() + "\njsonFilePrefix:" + jsonFilePrefix
				+ "\njsonFileTmpDir:" + jsonFileTmpDir;
	}
}
