/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.ditoolcore.reportimpl;

import com.msd.bdp.ditoolcore.OutputUtils;
import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.ResultObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Class to build the Html Report.
 */
public class Report {

    private final OutputUtils outputUtils;
    private final boolean smoke;
    private boolean isPassed;
    private double expDiff = 3;


    public Report(OutputUtils outputUtils, boolean smoke) {
        this.outputUtils = outputUtils;
        this.smoke = smoke;
    }

    public void setExpDiff(double expDiff) {
        this.expDiff = expDiff;
    }

    /**
     * Returns if test was passed or failed
     *
     * @return boolen isPassed
     */
    public boolean isTestPassed() {
        return isPassed;
    }

    /**
     * Creates the HTML report from the data integrity results
     *
     * @param sourceDatabaseUrl    source database URL
     * @param sourceDatabaseUser   source database user
     * @param sourceDatabaseSchema source database schema
     * @param targetDatabaseUrl    target database URL
     * @param targetDatabaseUser   target database user
     * @param targetDatabaseSchema target database schema
     * @param JSONFile             json file
     * @param executionTime        test duration
     * @param resultObjects        the list of result objects
     * @throws IOException
     */
    public void createHtmlReport(String sourceDatabaseUrl, String sourceDatabaseUser, String sourceDatabaseSchema,
                                 String targetDatabaseUrl, String targetDatabaseUser, String targetDatabaseSchema,
                                 String JSONFile, String executionTime, List<ResultObject> resultObjects) throws IOException {

        Date date = new Date();


        InputStream is = getClass().getResource("/reportTemplate.html").openStream();
        Document doc = Jsoup.parse(IOUtils.toString(is, "UTF-8"));

        //filling the information table
        doc.select("#sourceDatabaseUrl td").get(1).append(sourceDatabaseUrl);
        doc.select("#sourceDatabaseUser td").get(1).append(sourceDatabaseUser == null ? "" : sourceDatabaseUser);
        doc.select("#sourceDatabaseSchema td").get(1).append(sourceDatabaseSchema == null ? "" : sourceDatabaseSchema);
        doc.select("#targetDatabaseUrl td").get(1).append(targetDatabaseUrl);
        doc.select("#targetDatabaseUser td").get(1).append(targetDatabaseUser == null ? "" : targetDatabaseUser);
        doc.select("#targetDatabaseSchema td").get(1).append(targetDatabaseSchema == null ? "" : targetDatabaseSchema);
        doc.select("#JSONFile td").get(1).append(JSONFile);
        doc.select("#executionDate td").get(1).append(date.toString());
        doc.select("#executionTime td").get(1).append(executionTime == null ? "" : executionTime);
        doc.select("#testType td").get(1).append(smoke ? "Row count validation" : "Row count and data validation");
        doc.select("#acceptPercentage td").get(1).append(String.format("%.4f %%", expDiff));

        Element successTable = doc.select("#success tbody").get(0);
        Element failedTable = doc.select("#fail tbody").get(0);
        Element skippedTable = doc.select("#skipped tbody").get(0);

        int totalPass = 0;
        int totalFail = 0;
        int totalSkipped = 0;


        //filling the report page
        for (ResultObject o : resultObjects) {
            double sourceSize = o.sourceTableSize;
            double targetSize = o.targetTableSize;
            double max = Math.max(sourceSize, targetSize);
            double min = Math.min(sourceSize, targetSize);

            double diff = Math.abs(((max / min) - 1) * 100);
            if(Double.isInfinite(diff)){
                diff = 100;
            }


            //table has passed. No error message was fired
            if (StringUtils.isBlank(o.errorMessage) && o.sourceTableSize == o.targetTableSize &&
                    StringUtils.isBlank(o.sourceDifferenceFileName) && StringUtils.isBlank(o.targetDifferenceFileName)) {
                totalPass++;
                successTable.append(getSuccessRecord(o));

                //table is skipped
            } else if (!StringUtils.isBlank(o.errorMessage) && o.errorMessage.contains("is not checked:")) {
                totalSkipped++;
                skippedTable.append(getSkippedRecord(o, diff));

                //table has failed
            } else {
                totalFail++;

                if (smoke && o.errorMessage == null) {
                    if (targetSize > sourceSize) {
                        String errorMessage = "Target table has more records than the source table";
                        failedTable.append(getFailedRecord(o, diff, errorMessage, true));

                    } else if (targetSize <= sourceSize) {
                        String errorMessage;
                        boolean needInv = false;
                        if (diff > expDiff) {
                            errorMessage = String.format("Target table has %.4f %% difference from the source table.", diff);
                            needInv = true;

                        } else {
                            errorMessage = String.format("Difference lower than or equal to %.4f %% is acceptable. Current difference is %.4f %%",
                                    expDiff, diff);
                        }
                        failedTable.append(getFailedRecord(o, diff, errorMessage, needInv));
                    }

                } else {

                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("<p class=\"minimize\">").
                            append(o.errorMessage == null ? "" : o.errorMessage).append("</p>").append("<p class=\"minimize\">").
                            append(getDbType(sourceDatabaseUrl) + " sql select: ").
                            append(o.sqlObject.sourceSqlString == null ? "" : o.sqlObject.sourceSqlString).
                            append("</p>").
                            append("<p class=\"minimize\">").
                            append(getDbType(targetDatabaseUrl) + " sql select: ").
                            append(o.sqlObject.targetSqlString == null ? "" : o.sqlObject.targetSqlString).
                            append("</p>");
                    failedTable.append(getFailedRecord(o, diff, errorMessage.toString(), false));
                }
            }


        }
        doc.select("#totalPass span").append(Integer.toString(totalPass));
        doc.select("#totalFail span").append(Integer.toString(totalFail));
        doc.select("#totalSkipped span").append(Integer.toString(totalSkipped));

        outputUtils.createHtmlReport(doc.toString());

        isPassed = totalFail == 0;

    }

    private String getDbType(String url) {
        try {
            return String.valueOf(DIToolUtilities.getDatabaseType(url));
        } catch (Exception e) {
            //if csv or whatever not database
            return "Unknown";
        }

    }

    private static String getSuccessRecord(ResultObject o) {
        StringBuilder raw = new StringBuilder();
        raw.append("<tr>").
                append("<td> <div class=\"cell\"><span class=\"label pass\">Pass</span></div></td>").
                append("<td>").append(o.sqlObject.sourceTableName).append("</td>").
                append("<td>").append(o.sqlObject.targetTableName).append("</td>").
                append("<td>").append(o.sourceTableSize).append("</td>").
                append("<td>").append(o.targetTableSize).append("</td>").
                append("<td>").append("0.0000").append("</td>").
                append("<td></td>").
                append("<td></td>").
                append("<td></td>").
                append("</tr>");
        return raw.toString();
    }

    private static String getSkippedRecord(ResultObject o, double diff) {
        StringBuilder raw = new StringBuilder();
        raw.append("<tr>").
                append("<td> <div class=\"cell\"><span class=\"label skipped\">Skipped</span></div></td>").
                append("<td>").append(o.sqlObject.sourceTableName).append("</td>").
                append("<td>").append(o.sqlObject.targetTableName).append("</td>").
                append("<td>").append(o.sourceTableSize).append("</td>").
                append("<td>").append(o.targetTableSize).append("</td>").
                append("<td>").append(String.format("%.4f", diff)).append("</td>").
                append("<td></td>").
                append("<td></td>").
                append("<td>").append(o.errorMessage).append("</td>").
                append("</tr>");
        return raw.toString();

    }

    private String getFailedRecord(ResultObject o, double diff, String errorMessage, boolean needInvestigation) {
        StringBuilder raw = new StringBuilder();
        raw.append("<tr>").
                append("<td> " +
                        "<div class=\"cell\">").
                append("<span class=\"label fail\">Fail</span>");

        if (needInvestigation) {
            raw.append("<span class=\"label exclamation\">!</span>");
        }
        raw.append("</div>" +
                "</td>").
                append("<td>").append(o.sqlObject.sourceTableName).append("</td>").
                append("<td>").append(o.sqlObject.targetTableName).append("</td>").
                append("<td>").append(o.sourceTableSize).append("</td>").
                append("<td>").append(o.targetTableSize).append("</td>").
                append("<td>").append(String.format("%.4f", diff)).append("</td>").
                append("<td>").append(o.sourceDifferenceFileName == null && smoke ? "" : o.sourceDifferenceFileName).append("</td>").
                append("<td>").append(o.targetDifferenceFileName == null && smoke ? "" : o.targetDifferenceFileName).append("</td>").
                append("<td>").append(errorMessage).append("</td>").
                append("</tr>");
        return raw.toString();

    }

}
