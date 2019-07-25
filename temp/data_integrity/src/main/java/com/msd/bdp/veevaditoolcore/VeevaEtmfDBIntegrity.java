/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.msd.bdp.ditoolcore.DIToolUtilities.convertResultSetToCSV;


/**
 * Object for executing Veeva and DB comparison 
 */
public class VeevaEtmfDBIntegrity {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(VeevaEtmfDBIntegrity.class);

	/**
	 * Output Utility
	 */
	private final OutputUtils outputUtils;

	public VeevaEtmfDBIntegrity(OutputUtils outputUtils) {
		this.outputUtils = outputUtils;
	}


	/**
	 * Facade method to consist of operations to download ETMF Veeva Jsons and compare against DB
	 * <br> 1. Login to Veeva Server
	 * <br> 2. Download Json Files from Veeva Server based on configuration
	 * <br> 3. Convert Json Files to CSV files
	 * <br> 4. Extract value from Database server based on query
	 * <br> 5. Two way comparision between Database result with CSV file and write the result to output file
	 *
	 * @param sourceFileConfig Source configuration related to Query Veeva server
	 * @param targetDBConfig Target configuration that related to Database server
	 * @return Comparision output results
	 */
	public List<String> compareEtmfJsonAndDB(VeevaEtmfSourceConfig sourceFileConfig,
			VeevaEtmfTargetDBConfig targetDBConfig) throws DiCoreException {
		LOGGER.info("VeevaSource Config {} DB Config {}", sourceFileConfig, targetDBConfig);
		// Veeva Login Session ID
		String loginSessionID = null;
		try {
			loginSessionID = VeevaEtmfUtil.getSessionID(sourceFileConfig.getAuthURL(), sourceFileConfig.getUsername(),
					sourceFileConfig.getPassword());
		} catch (IOException e) {
			throw new VeevaEtmfException("Unable to login to Veeva Server", e);
		}

		List<String> combineAllNotFoundResult = new ArrayList<>();

		// Download Veeva Json Document
		try {
			VeevaEtmfUtil.downloadETMFBasedOnQuery(loginSessionID, sourceFileConfig.getTempDir(),
					sourceFileConfig.getJsonFilePrefix(), sourceFileConfig.getQueryURL(),
					sourceFileConfig.getEmtfQuery(), true);
		} catch (IOException e) {
			throw new VeevaEtmfException("Unable to download Veeva JSON document", e);
		}

		// Convert Veeva Json to CSV for comparison later
		try {
			VeevaEtmfUtil.convertJson2Csv(sourceFileConfig.getTempDir(), sourceFileConfig.getJsonFilePrefix(),
					sourceFileConfig.getEmtfQuery());
		} catch (IOException e) {
			throw new VeevaEtmfException("Unable to convert JSON to CSV", e);
		}

		// Compare CSV to DB
		try {
			combineAllNotFoundResult.addAll(compareFileToDB(sourceFileConfig, targetDBConfig));
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new VeevaEtmfException("Unable to compare CSV to DB", e);
		}

		// Compare DB to CSV
		try {
			combineAllNotFoundResult.addAll(compareDBToFile(sourceFileConfig, targetDBConfig));
		} catch (SQLException | IOException  e) {
			throw new VeevaEtmfException("Unable to compare DB to CSV", e);
		}
		return combineAllNotFoundResult;
	}

	/**
	 * Compare Veeva CSV files with DB return result that found in source files but NOT in database
	 * 
	 * @param sourceFileConfig Veeva source files configuration
	 * @param targetDBConfig Target Database Configuration 
	 * @return Result that found in Veeva source files but not in Database
	 * @throws IOException 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private List<String> compareFileToDB(VeevaEtmfSourceConfig sourceFileConfig, VeevaEtmfTargetDBConfig targetDBConfig)
			throws IOException, InterruptedException, ExecutionException {

		File[] jsonFiles = new File(sourceFileConfig.getTempDir())
				.listFiles((File file) -> file.getName().startsWith(sourceFileConfig.getJsonFilePrefix())
						&& file.getName().endsWith(".csv"));
		ExecutorService searchTasksExecutor = Executors.newFixedThreadPool(jsonFiles.length);
		List<Future<List<String>>> allResultsNotFoundInDB = new ArrayList<>();
		List<VeevaEtmfCompareFileToDB> searchingThreadList = new ArrayList<>();

		DbFacadePool targetConnPool = new DbFacadePool(targetDBConfig.getTargetDBUrl(),
				targetDBConfig.getTargetDbUser(), targetDBConfig.getTargetDbPassword(), null, null);
		List<String> combineAllNotFoundResult = new ArrayList<>();

		try {

			for (File jsonFile : jsonFiles) {
				VeevaEtmfCompareFileToDB searchingTask = new VeevaEtmfCompareFileToDB(targetConnPool, jsonFile,
						targetDBConfig);
				searchingThreadList.add(searchingTask);
				Future<List<String>> notFoundResult = searchTasksExecutor.submit(searchingTask);
				allResultsNotFoundInDB.add(notFoundResult);
			}

			for (Future<List<String>> notFoundResult : allResultsNotFoundInDB) {
				List<String> notFoundCSVs = notFoundResult.get();
				outputUtils.writeToCSV(notFoundCSVs, targetDBConfig.getOutputFileNamePrefix());
				combineAllNotFoundResult.addAll(notFoundCSVs);
			}
		} finally {
			searchTasksExecutor.shutdown();
			targetConnPool.close();
		}
		return combineAllNotFoundResult;
	}

	/**
	 * Compare DB against CSV files and return result that found in DB but not CSV files
	 * 
	 * @param sourceFileConfig
	 * @param targetDBConfig
	 * @return Result that found in DB but not CSV files
	 * @throws SQLException
	 * @throws IOException
	 */
	private List<String> compareDBToFile(VeevaEtmfSourceConfig sourceFileConfig, VeevaEtmfTargetDBConfig targetDBConfig)
			throws SQLException, IOException, DiCoreException {
		try (DbFacadePool targetConnPool = new DbFacadePool(targetDBConfig.getTargetDBUrl(),
				targetDBConfig.getTargetDbUser(), targetDBConfig.getTargetDbPassword(), null, null);
				DbFacade targetDf = targetConnPool.borrowConnectionWithCheck();
				Connection connection = targetDf.getConnection();
				Statement stm = connection.createStatement();
				ResultSet rs = stm.executeQuery(targetDBConfig.getTargetDBQuery())) {
			VeevaEtmfCompareDBToFile compareDBInCSV = new VeevaEtmfCompareDBToFile(sourceFileConfig);
			List<String> csvNotFoundInFile = new ArrayList<>();
			while (rs.next()) {
				String dbRecordInCSV = convertResultSetToCSV(rs);
				boolean found = compareDBInCSV.isResultSetCSVFoundInFiles(dbRecordInCSV);
				if (!found) {
					csvNotFoundInFile.add("NOT_FOUND_IN_CSV," + dbRecordInCSV);
				}
			}
			if (csvNotFoundInFile.isEmpty()) {
				outputUtils.writeToCSV(csvNotFoundInFile, targetDBConfig.getOutputFileNamePrefix());
			}
			return csvNotFoundInFile;
		}
	}

}
