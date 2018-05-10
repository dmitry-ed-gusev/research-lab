package gusevdm.integration;

/**
 * Utility class with Abstract answers (JSON) for common requests.
 * Created by gusevdm on 1/30/2017.
 */

public final class AbstractAnswers {

    public static final String ANS_ABSTRACT_CREATE_COLLECTION =
            "{\n" +
            "  \"created_at\":         \"2015-03-24T18:14:30.964962\",\n" +
            "  \"created_by_user_id\": \"cd6cca2c-a43a-4b31-a331-f352b3f0390f\",\n" +
            "  \"modified_at\":        \"2015-03-24T18:14:30.964962\",\n" +
            "  \"id\":                 \"%s\",\n" +
            "  \"name\":               \"%s\"\n" +
            "}";

    public static final String ANS_ABSTRACT_CREATE_DATASET =
            "{\n" +
            "    \"id\":                 \"%s\",\n" +
            "    \"collection_id\":      \"%s\",\n" +
            "    \"delta_dataset_id\":     null,\n" +
            "    \"created_at\":         \"2015-02-20T17:33:57.183741\",\n" +
            "    \"created_by_user_id\": \"cd6cca2c-a43a-4b31-a331-f352b3f0390f\",\n" +
            "    \"modified_at\":        \"2015-02-20T17:34:00.604023\",\n" +
            "    \"archived_at\":          null,\n" +
            "    \"name\":               \"%s\",\n" +
            "    \"state\":              \"awaiting_upload\",\n" +
            "    \"error_msg\":            null,\n" +
            "    \"metadata\": {\n" +
            "        \"modified_at\": \"2015-03-08T16:53:07.000000\",\n" +
            "        \"datapath\":    \"%s\"\n" +
            "    },\n" +
            "    \"schema\": [\n" +
            "        {\"name\":\"col_1\",\"type\":\"varchar\"},\n" +
            "        {\"name\":\"col_2\",\"type\":\"date\"},\n" +
            "        {\"name\":\"col_3\",\"type\":\"varchar\"},\n" +
            "        {\"name\":\"col_4\",\"type\":\"numeric\"}\n" +
            "    ],\n" +
            "    \"source\": {\n" +
            "        \"format\": \"csv\",\n" +
            "        \"header\": false\n" +
            "        \"upload_url\": \"http://some-s3-bucket.s3.amazon.com/datasets/22f80758-206a-4043-8bac-5b7b0e12db24?Signature=XXXX&Expires=XXXX&AWSAccessKeyId=XXXX\",\n" +
            "        \"url\": null,\n" +
            "    }\n" +
            "}";

    public static final String ANS_ABSTRACT_DATASET_STATUS =
            "{\n" +
            "  \"id\":                 \"%s\",\n" +
            "  \"collection_id\":      \"%s\",\n" +
            "  \"delta_dataset_id\":   null,\n" +
            "  \"created_at\":         \"2015-02-20T17:33:57.183741\",\n" +
            "  \"created_by_user_id\": \"cd6cca2c-a43a-4b31-a331-f352b3f0390f\",\n" +
            "  \"modified_at\":        \"2015-02-20T17:34:00.604023\",\n" +
            "  \"archived_at\":        null,\n" +
            "  \"name\":               \"Citibike Trips for April\",\n" +
            "  \"state\":              \"%s\",\n" +
            "  \"error_msg\":          null,\n" +
            "  \"metadata\": {\n" +
            "    \"modified_at\": \"2015-03-08T16:53:07.000000\",\n" +
            "    \"datapath\": \"com.citibikenyc.monthly.april\"\n" +
            "  },\n" +
            "  \"schema\": [\n" +
            "    {\n" +
            "      \"name\": \"col_1\",\n" +
            "      \"type\": \"varchar\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"col_2\",\n" +
            "      \"type\": \"date\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"col_3\",\n" +
            "      \"type\": \"varchar\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"col_4\",\n" +
            "      \"type\": \"numeric\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"source\": {\n" +
            "    \"url\": \"http://some-s3-bucket.s3.amazon.com/datasets/22f80758-206a-4043-8bac-5b7b0e12db24\",\n" +
            "    \"format\": \"csv\",\n" +
            "    \"header\": false\n" +
            "  }\n" +
            "}";

    public static final String ANS_METABASE_LOGIN =
            "{\n" +
            "  \"success\": \"true\"\n" +
            "}\n";

    public static final String ANS_METABASE_DATAPATH =
            "{\n"+
            "  \"success\": true,\n"+
            "  \"result\": {\n"+
            "    \"datapath\":                    \"%s\",\n"+
            "    \"type\":                        \"parent\",\n"+
            "    \"published\":                   true,\n"+
            "    \"ancestor_datapaths\":          [],\n"+
            "    \"ancestor_datapaths_length\":   0,\n"+
            "    \"immediate_ancestor_datapath\": null,\n"+
            "    \"node\":                        \"test-pipeline\",\n"+
            "    \"display_name\":                \"test-pipeline\",\n"+
            "    \"short_desc\":                  \"A short description for test-pipeline\",\n"+
            "    \"long_desc\":                   \"A long description for test-pipeline. Please remember the importance of keeping accurate and explicit metadata for test-pipeline.\"\n"+
            "  }\n"+
            "}";

    public static final String JSON_SCHEMA =
            "[\n" +
            "  { \"name\" : \"boolean_col\",   \"type\": \"boolean\" },\n" +
            "  { \"name\" : \"tinyint_col\",   \"type\": \"integer\" },\n" +
            "  { \"name\" : \"smallint_col\",  \"type\": \"integer\" },\n" +
            "  { \"name\" : \"int_col\",       \"type\": \"integer\" },\n" +
            "  { \"name\" : \"bigint_col\",    \"type\": \"integer\" },\n" +
            "  { \"name\" : \"float_col\",     \"type\": \"decimal\" },\n" +
            "  { \"name\" : \"double_col\",    \"type\": \"decimal\" },\n" +
            "  { \"name\" : \"decimal_col\",   \"type\": \"decimal\" },\n" +
            "  { \"name\" : \"string_col\",    \"type\": \"string\" },\n" +
            "  { \"name\" : \"char_col\",      \"type\": \"string\" },\n" +
            "  { \"name\" : \"varchar_col\",   \"type\": \"string\" },\n" +
            "  { \"name\" : \"date_col\",      \"type\": \"date\" },\n" +
            "  { \"name\" : \"timestamp_col\", \"type\": \"datetime\" }\n" +
            "]\n";

    private AbstractAnswers() {
    }

}
