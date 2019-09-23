/*
 * Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */
package com.msd.bdp.jsonTesting;


import com.msd.bdp.ditoolcore.SqlObject;
import com.msd.bdp.ditoolcore.jsonfacade.HiveJsonReader;
import com.msd.bdp.ditoolcore.jsonfacade.OracleJsonReader;
import com.msd.bdp.ditoolcore.jsonfacade.SqlserverJsonReader;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
public class JsonReaderTest {

    @Test
    public void testTeradataJsonReader() throws Exception {
        // given
        SqlObject testee = new SqlObject( "test.HIVE_NAME",
                "MANTIS_STAGE.teradata_name",
                "select `ID`,`PRODUCT_ID`,date_format(`DELETE_DTTM`, 'yyyy-MM-dd HH:mm:ss'),CASE `ACTIVE_FLAG` WHEN TRUE then 1 WHEN FALSE then 0 ELSE null END `ACTIVE_FLAG`,CASE `HARD_DELETED_FLAG` WHEN TRUE then 1 WHEN FALSE then 0 ELSE null END `HARD_DELETED_FLAG`,date_format(`LOAD_DTTM`, 'yyyy-MM-dd HH:mm:ss') from test.HIVE_NAME",
                "select ID,PRODUCT_ID,TO_CHAR(DELETE_DTTM,'YYYY-MM-DD HH24:MI:SS'),ACTIVE_FLAG,HARD_DELETED_FLAG,TO_CHAR(SRC_LOAD_DTTM,'YYYY-MM-DD HH24:MI:SS') from MANTIS_STAGE.teradata_name",

                "ID",
                true,"HIVE_NAME");



        // when
        HiveJsonReader sut = new HiveJsonReader(new File("src/test/resources/unitTestTeradata.json"),"test","MANTIS_STAGE");

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

    @Test
    public void testSqlserverJsonReader() throws Exception {
        // given

        SqlObject testee = new SqlObject( "\"NUCLEUS_PROD_BRI.dbo\".PROJECT_TEMP_FIELDS PROJECT_TEMP_FIELDS",
                "test.PROJECT_TEMP_FIELDS",
                "select \"LOAD_ID\",\"TEMPLATE\",\"FIELD_NAME\",\"ENTRY_MODE\",\"ORDER_NUMBER\",\"DEFAULT_VALUE\",\"FIELD_LABEL\",\"ALLOW_USER_ENTRY\",\"VERSION\",\"DATA_TYPE\",\"DEPENDS_ON\",\"GROUP_TITLE\",\"FORMULA\" from \"NUCLEUS_PROD_BRI.dbo\".PROJECT_TEMP_FIELDS PROJECT_TEMP_FIELDS",
                "select `LOAD_ID_R`,`TEMPLATE`,`FIELD_NAME`,`ENTRY_MODE`,`ORDER_NUMBER`,`DEFAULT_VALUE`,`FIELD_LABEL`,`ALLOW_USER_ENTRY`,`VERSION`,`DATA_TYPE`,`DEPENDS_ON`,`GROUP_TITLE`,`FORMULA` from test.PROJECT_TEMP_FIELDS",
                null,
                false,"PROJECT_TEMP_FIELDS");

        // when
        SqlserverJsonReader sut = new SqlserverJsonReader(new File("src/test/resources/unitTestSqlserver.json"),"NUCLEUS_PROD_BRI.dbo","test");

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

    @Test
    public void testOracleJsonReader_nestedColumn() throws Exception {
        // given

        SqlObject testee = new SqlObject( "OPS$WPPLIMS.SQA_LOTS SQA_LOTS",
                "test.SQA_LOTS",
                "select SQA_LOTS.\"LOT_ID\",SQA_LOTS.\"PROTOCOL_NAME\",TO_CHAR(SQA_LOTS.\"REASSAY_DATE\",'YYYY-MM-DD HH24:MI:SS') from OPS$WPPLIMS.SQA_LOTS",
                "select cast(`LOT_ID` as string),`PROTOCOL_NAME`,date_format(`REASSAY_DATE`, 'yyyy-MM-dd HH:mm:ss') from test.SQA_LOTS",
                null,
                false,"SQA_LOTS");


        // when
        OracleJsonReader sut = new OracleJsonReader(new File("src/test/resources/unitTestOracle_NestedColumn.json"),"OPS$WPPLIMS","test");

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

    @Test
    public void testOracleJsonReader_nestedTable_nestedColumn() throws Exception {
        // given
        SqlObject testee = new SqlObject( "OPS$WPPLIMS.SQA_LOTS SQA_LOTS, TABLE(ATTRIBUTES) ATTRIBUTES",
                "SQA_LOTS__ATTRIBUTES",
                "select SQA_LOTS.LOT_ID,SQA_LOTS.MATERIAL_NAME,TO_CHAR(SQA_LOTS.DATE_IN,'YYYY-MM-DD HH24:MI:SS'),SQA_LOTS.ORDER_NUMBER,SQA_LOTS.REASSAY_INTERVAL from OPS$WPPLIMS.SQA_LOTS SQA_LOTS, TABLE(ATTRIBUTES) ATTRIBUTES",
                "select cast(`LOT_ID` as string),`MATERIAL_NAME`,date_format(`DATE_IN`, 'yyyy-MM-dd HH:mm:ss'),`ORDER_NUMBER`,cast(`REASSAY_INTERVAL` as string) from SQA_LOTS__ATTRIBUTES",
                null,
                false,"OPS$WPPLIMS.SQA_LOTS_SQA_LOTS,_TABLE(ATTRIBUTES)_ATTRIBUTES");



        // when
        OracleJsonReader sut = new OracleJsonReader(new File("src/test/resources/unitTestOracle_NestedTableColumn.json"),"OPS$WPPLIMS",null);

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

    @Test
    public void testOracleJsonReader() throws Exception {
        // given

        SqlObject testee = new SqlObject( "OPS$DHPLIMS.LMK_CALCULATION_TARGET LMK_CALCULATION_TARGET",
                "LMK_CALCULATION_TARGET",
                "select LMK_CALCULATION_TARGET.\"CALC_ID\",LMK_CALCULATION_TARGET.\"SEQUENCE\",LMK_CALCULATION_TARGET.\"MNEMONIC\",LMK_CALCULATION_TARGET.\"VALUATION\",LMK_CALCULATION_TARGET.\"PROMPT\",LMK_CALCULATION_TARGET.\"COMPONENT\",LMK_CALCULATION_TARGET.\"MU_ID\",TO_CLOB(LMK_CALCULATION_TARGET.\"COL_XMLTYPE\") from OPS$DHPLIMS.LMK_CALCULATION_TARGET",
                "select cast(`CALC_ID` as string),cast(`SEQUENCE` as string),`MNEMONIC`,`VALUATION`,`PROMPT`,`COMPONENT`,cast(`MU_ID` as string),`COL_XMLTYPE` from LMK_CALCULATION_TARGET",
                "CALC_ID",
                true,"LMK_CALCULATION_TARGET");


        // when
        OracleJsonReader sut = new OracleJsonReader(new File("src/test/resources/unitTestOracle.json"),"OPS$DHPLIMS",null);

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

    @Test
    public void testOracleJsonReader_old() throws Exception {
        // given

        SqlObject testee = new SqlObject( "OPS$DHPLIMS.LMK_CALCULATION_TARGET LMK_CALCULATION_TARGET",
                "LMK_CALCULATION_TARGET",
                "select LMK_CALCULATION_TARGET.\"CALC_ID\",LMK_CALCULATION_TARGET.\"SEQUENCE\",LMK_CALCULATION_TARGET.\"MNEMONIC\",LMK_CALCULATION_TARGET.\"VALUATION\",LMK_CALCULATION_TARGET.\"PROMPT\",LMK_CALCULATION_TARGET.\"COMPONENT\",LMK_CALCULATION_TARGET.\"MU_ID\",TO_CLOB(LMK_CALCULATION_TARGET.\"COL_XMLTYPE\") from OPS$DHPLIMS.LMK_CALCULATION_TARGET",
                "select cast(`CALC_ID` as string),cast(`SEQUENCE` as string),`MNEMONIC`,`VALUATION`,`PROMPT`,`COMPONENT`,cast(`MU_ID` as string),`COL_XMLTYPE` from LMK_CALCULATION_TARGET",
                "CALC_ID",
                true,"LMK_CALCULATION_TARGET");


        // when
        OracleJsonReader sut = new OracleJsonReader(new File("src/test/resources/unitTestOracle.json"),"OPS$DHPLIMS",null);

        // then
        SqlObject result = sut.getSqlObjects().get(0);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(testee,result));
    }

}
