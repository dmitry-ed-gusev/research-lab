package gusevdm.luxms;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static gusevdm.luxms.DataSet.*;

/** Some helpers methods for LuxMS client. */
// todo: add other dataset parameters

public final class LuxMSHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSHelper.class);

    private LuxMSHelper() {}

    /***/
    public static DataSet parseDataSet(JSONObject json) {
        LOGGER.debug(String.format("LuxMSHelper.parseDataSet() is working. Parsing dataset from JSON:%n\t[%s].", json));

        if (json == null) {
            throw new IllegalStateException("Received JSON object is NULL!");
        }

        // create dataset
        DataSet dataSet = new DataSet(Long.parseLong(json.get(DS_ID).toString()),
                json.get(DS_DESCRIPTION).toString(), json.get(DS_TITLE).toString());
        // set other parameters
        dataSet.setVisible(Integer.parseInt(json.get(DS_IS_VISIBLE).toString()) == 1);
        dataSet.setArchive(Integer.parseInt(json.get(DS_IS_ARCHIVE).toString()) == 1);
        dataSet.setGuid(json.get(DS_GUID).toString());
        dataSet.setOwnerUser(json.get(DS_OWNER_USER_ID) == null ? null : json.get(DS_OWNER_USER_ID).toString());
        dataSet.setParentGuid(json.get(DS_PARENT_GUID) == null ? null : json.get(DS_PARENT_GUID).toString());
        dataSet.setPostProcessSql(json.get(DS_POST_PROCESS_SQL) == null ? null : json.get(DS_POST_PROCESS_SQL).toString());
        dataSet.setSchemaName(json.get(DS_SCHEMA_NAME).toString());

        return dataSet;
    }

}
