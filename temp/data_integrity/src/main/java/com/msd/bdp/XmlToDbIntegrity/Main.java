package com.msd.bdp.XmlToDbIntegrity;

import com.msd.bdp.ditoolcore.DiCoreException;
import com.msd.bdp.ditoolcore.dbfacade.DbFacade;
import com.msd.bdp.ditoolcore.dbfacade.DbFacadePool;
import org.apache.commons.collections4.ListUtils;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by holdberh on 7/13/2017.
 */
public class Main {
    // path to collection we iterate over
    private static final String ELEMENT_PATH = "/Record/Record";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String ITERATE_OVER_NODE = "Record";
    // path to all the fields we extract
    private static final String[] SUPPLY_CHAIN_EXPORT = {
            "../Field[@guid=\"4cb118fe-1930-4d49-8461-0c8cd5ff0a77\"]",
            // name="Supply Chain Name" alias="Supply_Chain_Name"
            "../Field[@guid=\"1948301a-ac60-42d0-b53e-136c39a79cf5\"]",
            // name="Supply Chain ID" alias="Supply_Chain_ID"
            "../Field[@guid=\"9740d355-70ef-4e3e-a1bc-559f8601f456\"]/Reference",
            // name="Site" alias="Facilities_Supply_Chains"
            "../Field[@guid=\"889de660-0834-48aa-8c3a-5dd3aeea5dba\"]/ListValues/ListValue",
            // name="Active" alias="Active"
            "../Field[@guid=\"83798cb6-9a60-4179-8ad6-3a4bb43aa432\"]/Reference",
            // name="Material" alias="Material"
            "../Field[@guid=\"4052c9c8-0b37-4e75-94b7-6aa860defe79\"]",
            // name="Merck Part Number" alias="Merck_Part_Number"
            "../Field[@guid=\"4b5fa442-a1c4-4fe8-8903-0a0b14f63d31\"]",
            // name="SAP Material Number" alias="SAP_Material_Number"
            "../Field[@guid=\"de88f236-7c49-4f8e-ac81-69e2ff434bb5\"]/Reference",
            // name="Merck Product" alias="Products_Related_Supply_Chain__Product"
            "../Field[@guid=\"48922f98-f675-4438-b4ac-ee8367ecaec3\"]/Reference",
            // name="Formulation ID" alias="Products_Related_Supply_Chain__Formulati"
            "../Field[@guid=\"f60ee270-3a97-48a8-b9bb-59f8c11df62d\"]/ListValues/ListValue",
            // name="Parent Material Category" alias="Prent_Material_Category"
            "../Field[@guid=\"2b1a8d53-8e0f-42e1-a895-1ca751136f2f\"]/Reference",
            // name="Tier Details Reference(s)" alias="Tier_Details_References"
            "Field[@guid=\"ac3b044c-b53a-4a3b-802e-8d6e2289a175\"]",
            // name="Tier Name" alias="Tier_Name"
            "Field[@guid=\"1db9b4fb-034e-40ad-942c-936d1bd247ee\"]/ListValues/ListValue",
            // name="Active" alias="Tier_Details_Status"
            "Field[@guid=\"b3b9ef96-23a5-4304-9d9b-27dde83733e9\"]/Reference",
            // name="Supplier" alias="Supplier1"
            "Field[@guid=\"73da10f0-8d90-4d86-b57e-cc519b535ad1\"]",
            // name="Supplier Address" alias="Supplier_Address"
            "Field[@guid=\"c473b871-c71b-4084-ba7e-379ee58988a8\"]/ListValues/ListValue",
            // name="Supplier Tier" alias="Supplier_Tier"
            "Field[@guid=\"10df6197-f57b-42ea-b46a-b16274bb8df0\"]/ListValues/ListValue",
            // name="Supplier Type" alias="Supplier_Type"
            "Field[@guid=\"f2846751-efcb-45df-b1ec-84988356d662\"]/ListValues/ListValue",
            // name="Supplier Category" alias="Supplier_Category"
            "Field[@guid=\"1c6aa625-3f9a-4bb9-8b65-fc1f6e042346\"]",
            // name="Supplier Catalog #" alias="Supplier_Catalog_"
            "Record/Field[@guid=\"a9922521-93c3-4de1-95f3-46089bd1bc0a\"]"
            // name="Child Supplier ID" alias="Subsidiary_Vendor_ID"
    };

    private static final String[] SUPPLY_CHAIN_NUMBER = {
            "../Field[@guid=\"7100cb31-37f0-41e5-bd04-334c7e732e0e\"]",
            // name="Merck Product" alias="ProductService_Name"
            "../Field[@guid=\"d4e7e16d-3e25-4a33-a934-d7b5a384eee4\"]",
            // name="MK Number" alias="MK_Number"
            "Field[@guid=\"1948301a-ac60-42d0-b53e-136c39a79cf5\"]",
            // name="Supply Chain ID" alias="Supply_Chain_ID"
            "Field[@guid=\"4cb118fe-1930-4d49-8461-0c8cd5ff0a77\"]",
            // name="Supply Chain Name" alias="Supply_Chain_Name"
    };


    public static void main(String[] args)
            throws IOException, XMLStreamException, SQLException, JaxenException, DiCoreException {
        // extract data from XML
        annex16ExportTest();
        annex16ENumberTest();

        LOGGER.info("Test completed");
    }

    private static void annex16ExportTest() throws IOException, SQLException, XMLStreamException, JaxenException, DiCoreException {


        List<String> resultfromDb;
        try (DbFacadePool targetConnPool = new DbFacadePool("jdbc:hive2://knox-01.bdpdev.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdpdev.gin.merck.com@MERCK.COM", "", "", "bdppass.headless.keytab", "bdppass@MERCK.COM")) {
            DbFacade f = targetConnPool.borrowConnection();
            resultfromDb = f.executeQuery(getAnnex16Export());

        }

        FileInputStream in = new FileInputStream(new File("Annex16Export.xml"));
        List<String[]> rows = XPathXmlReader.getRows(in, ITERATE_OVER_NODE, ELEMENT_PATH,SUPPLY_CHAIN_EXPORT);
        in.close();

        List<String> resultFromXml = transforListArray(rows);
        int differences = compareLists(resultFromXml, resultfromDb);

        if (differences != 0) {
            LOGGER.error("Prdelo. Annex 16 Export differences found " + differences);
        }
    }

    private static void annex16ENumberTest() throws IOException, SQLException, XMLStreamException, JaxenException, DiCoreException {


        List<String> resultfromDb;
        try (DbFacadePool targetConnPool = new DbFacadePool("jdbc:hive2://knox-01.bdpdev.gin.merck.com:8443/default;httpPath=gateway/default/hive;transportMode=http;ssl=true;auth=kerberos;principal=HTTP/knox-01.bdpdev.gin.merck.com@MERCK.COM", "", "", "bdppass.headless.keytab", "bdppass@MERCK.COM")) {
            DbFacade f = targetConnPool.borrowConnection();
            resultfromDb = f.executeQuery(getAnnex16Number());

        }

        FileInputStream in = new FileInputStream(new File("Annex16Export_MKNumber.xml"));
        List<String[]> rows = XPathXmlReader.getRows(in, ITERATE_OVER_NODE,ELEMENT_PATH, SUPPLY_CHAIN_NUMBER);
        in.close();

        List<String> resultFromXml = transforListArray(rows);
        int differences = compareLists(resultFromXml, resultfromDb);

        if (differences != 0) {
            LOGGER.error("Prdelo. Annex 16 Number differences found " + differences);
        }
    }

    private static String getAnnex16Export() {
        return "select " +
                "supply_chain_name,supply_chain_id,site,active,material,merck_part_number," +
                "sap_material_number,merck_product,formulation_id,parent_material_category," +
                "tier_details_references,tier_name," +
                "tier_details_status,supplier,supplier_address," +
                "supplier_tier,supplier_type,supplier_category," +
                "supplier_catalog_num,child_supplier_id" +
                " from annex16_export";
    }

    private static String getAnnex16Number() {
        return "select " +
                "merck_product,supply_chain_id,supply_chain_name,mk_number" +
                " from annex16_mknumber";
    }

    private static int compareLists(List<String> result1, List<String> result2) {
        Collections.sort(result1);
        Collections.sort(result2);
        List<String> difference = ListUtils.subtract(result1, result2);
        if (!difference.isEmpty()) {
            return difference.size();
        } else {
            return 0;
        }
    }

    private static List<String> transforListArray(List<String[]> array) {
        List<String> result = new ArrayList<>();

        for (String[] v : array) {
            StringJoiner row = new StringJoiner("<:>");
            for (String s : v) {
                row.add(s.trim());
            }
            result.add(">>" + row.toString() + "<<");
        }
        return result;
    }

}
