package gusevdm.luxms;

import gusevdm.luxms.model.LuxDataSet;
import org.apache.commons.lang3.NotImplementedException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static gusevdm.luxms.model.LuxDataSet.*;

/** Some helpers methods for LuxMS client. */
// todo: add other dataset parameters

public final class LuxMSHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxMSHelper.class);

    private LuxMSHelper() {}

    /***/
    // todo: move this logic into dataset itself!
    public static LuxDataSet parseDataSet(JSONObject json) {
        LOGGER.debug(String.format("LuxMSHelper.parseDataSet() is working. Parsing dataset from JSON:%n\t[%s].", json));

        if (json == null) {
            throw new IllegalStateException("Received JSON object is NULL!");
        }

        // create dataset
        LuxDataSet luxDataSet = new LuxDataSet(Long.parseLong(json.get(DS_ID).toString()),
                json.get(DS_DESCRIPTION).toString(), json.get(DS_TITLE).toString());
        // set other parameters
        luxDataSet.setVisible(Integer.parseInt(json.get(DS_IS_VISIBLE).toString()) == 1);
        luxDataSet.setArchive(Integer.parseInt(json.get(DS_IS_ARCHIVE).toString()) == 1);
        luxDataSet.setGuid(json.get(DS_GUID).toString());
        luxDataSet.setOwnerUser(json.get(DS_OWNER_USER_ID) == null ? null : json.get(DS_OWNER_USER_ID).toString());
        luxDataSet.setParentGuid(json.get(DS_PARENT_GUID) == null ? null : json.get(DS_PARENT_GUID).toString());
        luxDataSet.setPostProcessSql(json.get(DS_POST_PROCESS_SQL) == null ? null : json.get(DS_POST_PROCESS_SQL).toString());
        luxDataSet.setSchemaName(json.get(DS_SCHEMA_NAME).toString());

        return luxDataSet;
    }

}
