package gusevdm.luxms.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Logical representation of LuxMS BI dataset. */
public class LuxDataSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuxDataSet.class);

    // common constants for dataset JSON
    private static final String DS_ID               = "id";
    private static final String DS_GUID             = "guid";
    private static final String DS_PARENT_GUID      = "parent_guid";
    private static final String DS_DESCRIPTION      = "description";
    private static final String DS_TITLE            = "title";
    private static final String DS_IS_VISIBLE       = "is_visible";
    private static final String DS_SCHEMA_NAME      = "schema_name";
    private static final String DS_POST_PROCESS_SQL = "postprocess_sql";
    private static final String DS_OWNER_USER_ID    = "owner_user_id";
    private static final String DS_IS_ARCHIVE       = "is_archive";

    // internal object state
    private long    id;
    private String  guid;
    private String  parentGuid;
    private String  description;
    private String  title;
    private boolean isVisible;
    private String  schemaName;
    private String  postProcessSql;
    private String  ownerUser;
    private boolean isArchive;

    /***/
    public LuxDataSet(long id, String description, String title) {
        LOGGER.debug("LuxDataSet constructor() is working.");
        this.id          = id;
        this.description = description;
        this.title       = title;
    }

    /***/
    public LuxDataSet(JSONObject json) {
        LOGGER.debug(String.format("LuxDataSet constructor(JSON) is working. " +
                "Creating dataset from JSON:%n\t[%s].", json));

        if (json == null) { // fail-fast check
            throw new IllegalStateException("Received JSON object is NULL!");
        }

        // create dataset
        this.setId(Long.parseLong(json.get(DS_ID).toString()));
        this.setDescription(json.get(DS_DESCRIPTION).toString());
        this.setTitle(json.get(DS_TITLE).toString());
        this.setVisible(Integer.parseInt(json.get(DS_IS_VISIBLE).toString()) == 1);
        this.setArchive(Integer.parseInt(json.get(DS_IS_ARCHIVE).toString()) == 1);
        this.setGuid(json.get(DS_GUID).toString());
        this.setOwnerUser(json.get(DS_OWNER_USER_ID) == null ? null : json.get(DS_OWNER_USER_ID).toString());
        this.setParentGuid(json.get(DS_PARENT_GUID) == null ? null : json.get(DS_PARENT_GUID).toString());
        this.setPostProcessSql(json.get(DS_POST_PROCESS_SQL) == null ? null : json.get(DS_POST_PROCESS_SQL).toString());
        this.setSchemaName(json.get(DS_SCHEMA_NAME).toString());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getParentGuid() {
        return parentGuid;
    }

    public void setParentGuid(String parentGuid) {
        this.parentGuid = parentGuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getPostProcessSql() {
        return postProcessSql;
    }

    public void setPostProcessSql(String postProcessSql) {
        this.postProcessSql = postProcessSql;
    }

    public String getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(String ownerUser) {
        this.ownerUser = ownerUser;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public void setArchive(boolean archive) {
        isArchive = archive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("guid", guid)
                .append("parentGuid", parentGuid)
                .append("description", description)
                .append("title", title)
                .append("isVisible", isVisible)
                .append("schemaName", schemaName)
                .append("postProcessSql", postProcessSql)
                .append("ownerUser", ownerUser)
                .append("isArchive", isArchive)
                .toString();
    }

}

/*
// dataset JSON object
{
        //"parent_guid":null,
        "period_spans":{

        },
        //"postprocess_sql":null,
        //"description":"My New Own Dataset for temp purposes...",
        //"title":"my_dataset",
        "is_db_ready":0,
        "sync_cfg":{

        },
        "src_image_type":null,
        //"id":8,
        "sqlite_snapshot_id":null,
        "images":{

        },
        "ui_cfg":{

        },
        //"is_visible":1,
        "depends_on":[

        ],
        //"owner_user_id":null,
        "logged_since":null,
        //"schema_name":"ds_8",
        "server_id":0,
        "sqlite_serial":null,
        "version":"3.0",
        "head_dataset_id":null,
        "srt":2147483647,
        "serial":"2018-05-20T23:58:02.359467+03:00",
        //"is_archive":0,
        //"guid":"086cff47-9e61-41d9-bcaa-5a3c83492adf",
        "config":{

        }
} */