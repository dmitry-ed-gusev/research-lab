/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.veevaditoolcore;

import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static com.msd.bdp.ditoolcore.DIToolUtilities.convertResultSetToCSV;


/**
 * Object to compare Veeva downloaded File with Database 
 */
class VeevaEtmfCompareFileToDB implements Callable<List<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VeevaEtmfCompareFileToDB.class);

	private VeevaEtmfTargetDBConfig targetDBConfig;
	private File jsonFile;
	private DbFacadePool targetConnPool;


	public VeevaEtmfCompareFileToDB(DbFacadePool targetConnPool, File jsonFile, VeevaEtmfTargetDBConfig targetDBConfig) {
		this.jsonFile = jsonFile;
		this.targetDBConfig = targetDBConfig;
		this.targetConnPool = targetConnPool;
	}
	
	/**
	 * Callable thread method to perform comparison 
	 */
	@Override
	public List<String> call() throws Exception {
		List<String> csvLineNotFoundInDBList = new ArrayList<>();
		LOGGER.debug("Read File {}",  jsonFile);
		DbFacade dbFacade = targetConnPool.borrowConnectionWithCheck();
		try (ResultSet rs = dbFacade.getConnection()
				.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				.executeQuery(targetDBConfig.getTargetDBQuery());
				Scanner scanner = new Scanner(new FileInputStream(jsonFile))) {
			// Compare CSV > DB
			while (scanner.hasNextLine()) {
				String currentLIne = scanner.nextLine();
				LOGGER.debug("Search CSV in File : {}", currentLIne);
				boolean foundCsvLineInDB = false;
				while (rs.next()) {
					String dbRecordInCSV = convertResultSetToCSV(rs);
					if (dbRecordInCSV.equals(currentLIne)) {
						foundCsvLineInDB = true;
						LOGGER.debug("Found Line in DB {} ", currentLIne);
						break;
					}
				}
				if (!foundCsvLineInDB) {
					csvLineNotFoundInDBList.add("NOT_FOUND_FROM_DB_QUERY," + currentLIne);
				}
				rs.beforeFirst(); // Reset ResultSet to First Line
			}

		} finally {
			targetConnPool.destroyConnection(dbFacade);
		}
		return csvLineNotFoundInDBList;
	}

}
