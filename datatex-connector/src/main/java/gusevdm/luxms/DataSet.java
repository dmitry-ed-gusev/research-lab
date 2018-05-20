package gusevdm.luxms;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/** Logical representation of LuxMS BI dataset. */
// todo: implement BUILDER pattern???

public class DataSet {

    // common constants for dataset JSON
    public static final String DS_ID               = "id";
    public static final String DS_GUID             = "guid";
    public static final String DS_PARENT_GUID      = "parent_guid";
    public static final String DS_DESCRIPTION      = "description";
    public static final String DS_TITLE            = "title";
    public static final String DS_IS_VISIBLE       = "is_visible";
    public static final String DS_SCHEMA_NAME      = "schema_name";
    public static final String DS_POST_PROCESS_SQL = "postprocess_sql";
    public static final String DS_OWNER_USER_ID    = "owner_user_id";
    public static final String DS_IS_ARCHIVE       = "is_archive";

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
    public DataSet(long id, String description, String title) {
        this.id = id;
        this.description = description;
        this.title = title;
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