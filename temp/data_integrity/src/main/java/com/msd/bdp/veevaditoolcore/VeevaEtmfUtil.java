/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility that use to execute ETMF RESTful query to retrieve output from Veeva
 * Server
 */
public final class VeevaEtmfUtil {

    private static final String JSON_FILE_EXTENSION = ".json";

    private VeevaEtmfUtil() {
    }

    /**
     * Veeva Page result default limit
     */
    private static int defaultLimit = 10000;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VeevaEtmfUtil.class);

    /**
     * Web Proxy Address
     */
    private static String webproxy;

    /**
     * Web Proxy Port
     */
    private static int webproxyPort;

    /**
     * Set default page limit for Veeva Server output
     *
     * @param pageLimit Veeva Server output page limit
     */
    public static void setVeevaPageLimit(int pageLimit) {
        VeevaEtmfUtil.defaultLimit = pageLimit;
    }

    /**
     * Set Network Proxy configuration
     *
     * @param webproxy     MSD/Merck Network Proxy Address
     * @param webProxyPort MSD/Merck Network Proxy Port
     */
    public static void setProxy(String webproxy, int webProxyPort) {
        VeevaEtmfUtil.webproxy = webproxy;
        VeevaEtmfUtil.webproxyPort = webProxyPort;
    }

    /**
     * Login to Veeva Server and get return sessionId
     *
     * @param authUrl  URL to login to Veeva Server
     * @param username UserName to login Veeva Server
     * @param password Passwro to login to Veeva Server
     * @return return Login Session ID
     * @throws IOException
     */
    public static String getSessionID(String authUrl, String username, String password) throws IOException {
        return getSessionID(getURLConnection(authUrl), username, password);
    }

    /**
     * Helper method to open URL with Proxy if proxy configuration exits
     *
     * @param url Veeva Server URL
     * @return URL with Proxy setting
     * @throws IOException
     */
    private static HttpsURLConnection getURLConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpsURLConnection urlConnection;
        if (webproxy == null) {
            urlConnection = (HttpsURLConnection) urlObj.openConnection();
        } else {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(webproxy, webproxyPort));
            urlConnection = (HttpsURLConnection) urlObj.openConnection(proxy);
        }
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setDoOutput(true);
        return urlConnection;
    }

    /**
     * Login to Veeva Server and get return sessionId
     *
     * @param urlConnection Proxied URL that from {@link VeevaEtmfUtil#getURLConnection(String)}
     * @param username      UserName to login Veeva Server
     * @param password      Password to login Veeva Server
     * @return Veeva Server login Session ID
     * @throws IOException If connecting to veeva server exception occured
     */
    private static String getSessionID(HttpsURLConnection urlConnection, String username, String password)
            throws IOException {
        StringBuilder credentialBuilder = new StringBuilder("username=");
        credentialBuilder.append(username);
        credentialBuilder.append("&password=");
        credentialBuilder.append(password);
        String credentialParamsUrl = credentialBuilder.toString();
        try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
            wr.writeBytes(credentialParamsUrl);
            wr.flush();
        }

        int responseCode = urlConnection.getResponseCode();
        LOGGER.info("HTTP POST URL {} Response Code : {}", urlConnection.getURL(), responseCode);
        StringBuilder response = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        LOGGER.info("JSON output from server {}, content {}", urlConnection.getURL(), response);
        String result = null;
        result = extractJSONStrByKey(response.toString(), "sessionId");
        if (result == null) {
            throw new VeevaEtmfException("Unable to extract sessionId from content " + response.toString());
        }
        return result;

    }

    // Parameter isFirstPage is use to differentiate from recursive call for
    // subsequent pages and FirstPage.

    /**
     * Download ETMF from veeva server based on query and store in folder based on parameters.
     * Serve as recursive method as to download subsequent pages if return result exceed default page limit{@link VeevaEtmfUtil#defaultLimit}.
     *
     * @param loginSessionID     Seesion ID that obtain from login
     * @param tempFolder         Temporary folder to store result downloaded from server
     * @param jsonFilePrefix     Result file name prefix
     * @param etmfServerQueryURL URL that use to download ETMF result
     * @param etmfQuery          EMTF query
     * @param isFirstPage        For recursive usage to download subsequent pages after first page
     * @throws IOException If error occured during download process
     */
    public static void downloadETMFBasedOnQuery(String loginSessionID, String tempFolder, String jsonFilePrefix,
                                                String etmfServerQueryURL, String etmfQuery, boolean isFirstPage) throws IOException {
        // Preparing temp dir for JSON Files
        if (isFirstPage) {
            prepareTmpDir(tempFolder, true);
        }

        // Setup URL
        HttpsURLConnection urlConnection = getURLConnection(etmfServerQueryURL);
        urlConnection.setRequestProperty("Authorization", loginSessionID);
        urlConnection.setConnectTimeout(0);
        urlConnection.setReadTimeout(0);

        // HTTP Post with query
        try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
            if (isFirstPage) {
                String etmfQueryWithPageLimit = setQueryReturnPageLimit(etmfQuery);
                String etmfQueryURL = String.format("q=%s", etmfQueryWithPageLimit);
                wr.writeBytes(etmfQueryURL);
            }
            wr.flush();
        }

        // Log Response Code
        int responseCode = urlConnection.getResponseCode();
        LOGGER.info("HTTP POST URL {} Response Code : {}", urlConnection.getURL(), responseCode);

        String nextPageHeaderJson = storePagesAndGetNextPage(urlConnection, tempFolder, jsonFilePrefix);
        String nextPageURL = extractJSONStrByKey(nextPageHeaderJson, "next_page");
        if (nextPageURL != null) {
            // Extract httpServer from API URL
            String httpServer = etmfServerQueryURL.substring(0, etmfServerQueryURL.indexOf('/', 8));

            // Form nextPage URL
            nextPageURL = new StringBuilder(httpServer).append(nextPageURL).toString();
            LOGGER.debug("Next Page URL {}", nextPageURL);

            // Recursive call
            downloadETMFBasedOnQuery(loginSessionID, tempFolder, jsonFilePrefix, nextPageURL, null, false);
        }
    }

    /**
     * Concatenate ETMF Query with Page default limit
     *
     * @param etmfQuery ETMF Query
     * @return etmfQuery with Page limit
     */
    private static String setQueryReturnPageLimit(String etmfQuery) {
        if (!etmfQuery.toUpperCase(Locale.ENGLISH).contains("LIMIT")) {
            return new StringBuilder(etmfQuery).append(" LIMIT ").append(defaultLimit).toString();
        }
        return etmfQuery;
    }

    /**
     * Store the download File and extract the nextPage Header back if available
     *
     * @param urlConnection  Download Pages URL Connection
     * @param tempFolder     Temporary directory to store downloaded Json Files
     * @param jsonFilePrefix Json file name prefix
     * @return Next Page Metadata
     * @throws IOException If store file into disk exception occured
     */
    private static String storePagesAndGetNextPage(HttpsURLConnection urlConnection, String tempFolder, String jsonFilePrefix)
            throws IOException {
        String tempFilePath = tempFolder + File.separator + jsonFilePrefix + JSON_FILE_EXTENSION;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(tempFilePath)))) {
            int inByte;
            StringBuilder nextPageInfo = new StringBuilder();
            boolean runTimeStartHeaderExtract = false;
            final int CURLY_BRACES_COUNT_START = 2;
            int countCurlyBraces = 0;
            while ((inByte = in.read()) != -1) {
                // Write to File
                out.write(inByte);

                // Extract header info - Start
                if ((char) inByte == '{') {
                    countCurlyBraces++;
                    // Extract header info start from 2nd '{'
                    if (countCurlyBraces == CURLY_BRACES_COUNT_START) {
                        runTimeStartHeaderExtract = true;
                    }
                }

                // Extract header info - write to buffer
                if (runTimeStartHeaderExtract) {
                    nextPageInfo.append((char) inByte);
                }

                // Extract header info JSON object.
                // Stop when detect close '}'
                if (runTimeStartHeaderExtract && (char) inByte == '}') {
                    runTimeStartHeaderExtract = false;
                }
            }
            String nextPageHeaderJson = nextPageInfo.toString();
            LOGGER.info("Next Page Info: {}", nextPageHeaderJson);

            // Move temp file to target file
            Integer offSet = extractJSONIntByKey(nextPageHeaderJson, "offset");
            String targetFilePath = new StringBuilder(tempFolder).append(File.separatorChar).append(jsonFilePrefix)
                    .append("-").append(offSet).append(JSON_FILE_EXTENSION).toString();
            FileUtils.moveFile(new File(tempFilePath), new File(targetFilePath));
            return nextPageHeaderJson;
        }
    }

    /**
     * Extract String value from Json based on key
     *
     * @param rawJson Json input string
     * @param key     Key to extract from Json
     * @return String value of key
     */
    private static String extractJSONStrByKey(String rawJson, String key) {
        JSONObject jsonObj = new JSONObject(rawJson);
        if (!jsonObj.isNull(key)) {
            return jsonObj.getString(key);
        }
        return null;
    }

    /**
     * Extract Integer value from Json based on key
     *
     * @param rawJson Json input string
     * @param key     Key to extract from Json
     * @return Integer value of key
     */
    private static Integer extractJSONIntByKey(String rawJson, String key) {
        JSONObject jsonObj = new JSONObject(rawJson);
        if (!jsonObj.isNull(key)) {
            return jsonObj.getInt(key);
        }
        return null;
    }

    /**
     * Setup temporay folder
     *
     * @param jsonFileTmpDir Name of temporary folder
     * @param clearDir       Delete existing temporary folder
     * @throws IOException
     */
    private static void prepareTmpDir(String jsonFileTmpDir, boolean clearDir)  {
		File dstFileFolder = new File(jsonFileTmpDir);
		DateFormat dateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		if (clearDir) {
			String renameWithDataTime = new StringBuilder(dstFileFolder.getName()).append(dateFormat.format(date))
					.toString();
			File renameFile = new File(dstFileFolder.getParent(), renameWithDataTime);
			boolean renamed = dstFileFolder.renameTo(renameFile);
			LOGGER.info("Rename folder from {} to {}, status:{}", jsonFileTmpDir, renameFile.getAbsolutePath(), renamed);
		}

		if (!dstFileFolder.exists()) {
			dstFileFolder.mkdirs();
		}
    }

    /**
     * Convert Downloaded Json to CSV for based on ETMF Query column ordering (For comparison operation purpose)
     *
     * @param jsonFileFolder Folder of Json files
     * @param jsonFilePrefix File name prefix of the json file. Ex. Country_xxxxx
     * @param veevaEtmfQuery Veeva ETMF Query
     * @throws IOException
     */
    public static void convertJson2Csv(final String jsonFileFolder, final String jsonFilePrefix, String veevaEtmfQuery)
            throws IOException {

        // Extract query column for csv sequence conversion purpose
        String selectStr = "select";
        int queryStartColumnsIdx = veevaEtmfQuery.toLowerCase(Locale.ENGLISH).indexOf(selectStr) + selectStr.length();
        int queryEndColumnIdx = veevaEtmfQuery.toLowerCase(Locale.ENGLISH).indexOf("from");
        String columnsOnly = veevaEtmfQuery.substring(queryStartColumnsIdx, queryEndColumnIdx);
        String[] sequenceOfColumnNames = columnsOnly.split(",");
        JSONArray columnNames = new JSONArray();
        for (String column : sequenceOfColumnNames)
            columnNames.put(column.trim());

        Files.list(Paths.get(jsonFileFolder)).filter(f -> f.getFileName().toString().startsWith(jsonFilePrefix)
                && f.getFileName().toString().endsWith(JSON_FILE_EXTENSION)).forEach(jsonFile -> {
            String jsonFilePath = jsonFile.toAbsolutePath().toString();
            LOGGER.debug("Convert JSON to CSV:" + jsonFilePath);
            JSONObject root;
            try {
                root = new JSONObject(new JSONTokener(new FileReader(jsonFilePath)));
                JSONArray data = root.getJSONArray("data");
                String csv = CDL.toString(columnNames, data);
                String csvFilePath = jsonFilePath.replace(".json", ".csv");
                FileUtils.writeStringToFile(new File(csvFilePath), csv);
            } catch (JSONException | IOException e) {
                throw new VeevaEtmfException("Unable to convert json file to csv file " + jsonFilePath, e);
            }
        });

    }

}
