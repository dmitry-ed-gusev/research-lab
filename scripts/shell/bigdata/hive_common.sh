#!/usr/bin/env bash

# set -eu

# --
#function export-table {
#    # define argument placeholder vars
#    HIVE_TABLE="${1}"
#    HIVE_COLUMNS_STR="${2}"
#
#   echo "Processing table: ${HIVE_TABLE}"
#   TABLE_LIST+="${HIVE_TABLE} "
#   TARGET_FOLDER="${TARGET_BASE}/${DB_NAME}/${HIVE_TABLE}"
#   IFS=',' read -r -a HIVE_COLUMNS <<< "${HIVE_COLUMNS_STR}"
#
#    QUERY_COLUMNS=""
#    TABLE_DEF='['
#    for COLUMN in "${HIVE_COLUMNS[@]}"
#    do
#        #HINT Split the column definitin at the last space character (' ')
#        COLUMN_NAME="${COLUMN% *}"
#        COLUMN_TYPE="${COLUMN:${#COLUMN_NAME}+1}"
#
#        QUERY_COLUMN='`'"${COLUMN_NAME}"'`'
#        case "${COLUMN_TYPE}" in
#            "tinyint" | "bigint" | "smallint" | "int")
#                ABSTRACT_TYPE="integer"
#                ;;
#            "decimal" | "float" | "double")
#                ABSTRACT_TYPE="decimal"
#                ;;
#            "date")
#                ABSTRACT_TYPE="date"
#                ;;
#            "timestamp")
#                ABSTRACT_TYPE="datetime"
#                ;;
#            "boolean")
#                ABSTRACT_TYPE="boolean"
#                ;;
#            "binary")
#                ABSTRACT_TYPE="string"
#                QUERY_COLUMN='hex(`'"${COLUMN_NAME}"'`)'
#                ;;
#            "string")
#                ABSTRACT_TYPE="string"
#                QUERY_COLUMN='regexp_replace(`'"${COLUMN_NAME}"'`,"\0$","")'
#                ;;
#            *)
#                ABSTRACT_TYPE="string"
#        esac
#
#        QUERY_COLUMNS+="${QUERY_COLUMN},"
#        #HINT replace '/' with '_'since Abstract is not able to handle it
#        ABSTRACT_NAME="${COLUMN_NAME//\//_}"
#        TABLE_DEF+='{ "name" : "'"${ABSTRACT_NAME}"'", "type": "'"${ABSTRACT_TYPE}"'", "hiveType": "'"${COLUMN_TYPE}"'" },'
#    done
#
#   #strip the last comma
#   QUERY_COLUMNS="${QUERY_COLUMNS::-1}"
#   TABLE_DEF="${TABLE_DEF::-1}]"
#
#   #store the table structure into JSON file
#   echo "${TABLE_DEF}" | hdfs dfs -put - "${TARGET_FOLDER}.json"
#   hdfs dfs -chmod 640 "${TARGET_FOLDER}.json"
#   echo -e "Table schema definition :\n${TABLE_DEF}\nsaved in ${TARGET_FOLDER}.json"
#
#   #generate Hive script to export table into CSV file
#   echo '!sh date' >> "${HQL_FILE}"
#   echo "!sh echo Exporting table ${HIVE_TABLE} to the folder '${TARGET_FOLDER}'" >> "${HQL_FILE}"
#   HIVE2CSV_SQL="INSERT OVERWRITE DIRECTORY '${TARGET_FOLDER}' ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' WITH SERDEPROPERTIES ('serialization.encoding'='UTF-8') STORED AS TEXTFILE SELECT ${QUERY_COLUMNS} FROM ${HIVE_TABLE};"
#   echo "!sh echo Using SQL query: ${HIVE2CSV_SQL}" >> "${HQL_FILE}"
#   echo "${HIVE2CSV_SQL}" >> "${HQL_FILE}"
#}

# --
function export-table-test {
    # define argument placeholder vars
    HIVE_TABLE="${1}"
    HIVE_COLUMNS_STR="${2}"

   echo "Processing table: ${HIVE_TABLE}"
   TABLE_LIST+="${HIVE_TABLE} "
   #TARGET_FOLDER="${TARGET_BASE}/${DB_NAME}/${HIVE_TABLE}"
   TARGET_FOLDER="${HIVE_TABLE}"
   IFS=',' read -r -a HIVE_COLUMNS <<< "${HIVE_COLUMNS_STR}"

    #QUERY_COLUMNS=""
    TABLE_DEF='['
    for COLUMN in "${HIVE_COLUMNS[@]}"
    do
        #HINT Split the column definition at the last space character (' ')
        COLUMN_NAME="${COLUMN% *}"
        COLUMN_TYPE="${COLUMN:${#COLUMN_NAME}+1}"

        QUERY_COLUMN='`'"${COLUMN_NAME}"'`'
        case "${COLUMN_TYPE}" in
            "tinyint" | "bigint" | "smallint" | "int")
                ABSTRACT_TYPE="integer"
                ;;
            "decimal" | "float" | "double")
                ABSTRACT_TYPE="decimal"
                ;;
            "date")
                ABSTRACT_TYPE="date"
                ;;
            "timestamp")
                ABSTRACT_TYPE="datetime"
                ;;
            "boolean")
                ABSTRACT_TYPE="boolean"
                ;;
            "binary")
                ABSTRACT_TYPE="string"
                #QUERY_COLUMN='hex(`'"${COLUMN_NAME}"'`)'
                ;;
            "string")
                ABSTRACT_TYPE="string"
                #QUERY_COLUMN='regexp_replace(`'"${COLUMN_NAME}"'`,"\0$","")'
                ;;
            *)
                ABSTRACT_TYPE="string"
        esac

        #QUERY_COLUMNS+="${QUERY_COLUMN},"
        #HINT replace '/' with '_'since Abstract is not able to handle it
        ABSTRACT_NAME="${COLUMN_NAME//\//_}"
        TABLE_DEF+='{ "name" : "'"${ABSTRACT_NAME}"'", "type": "'"${ABSTRACT_TYPE}"'", "hiveType": "'"${COLUMN_TYPE}"'" },'
    done

   #strip the last comma
   #QUERY_COLUMNS="${QUERY_COLUMNS::-1}"
   TABLE_DEF="${TABLE_DEF::-1}]"

   #store the table structure into JSON file
   #echo "${TABLE_DEF}" | hdfs dfs -put - "${TARGET_FOLDER}.json"
   #hdfs dfs -chmod 640 "${TARGET_FOLDER}.json"
   #echo -e "Table schema definition :\n${TABLE_DEF}\nsaved in ${TARGET_FOLDER}.json"

   echo "${TABLE_DEF}" >> ${TARGET_FOLDER}.json


   #generate Hive script to export table into CSV file
   #echo '!sh date' >> "${HQL_FILE}"
   #echo "!sh echo Exporting table ${HIVE_TABLE} to the folder '${TARGET_FOLDER}'" >> "${HQL_FILE}"
   #HIVE2CSV_SQL="INSERT OVERWRITE DIRECTORY '${TARGET_FOLDER}' ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' WITH SERDEPROPERTIES ('serialization.encoding'='UTF-8') STORED AS TEXTFILE SELECT ${QUERY_COLUMNS} FROM ${HIVE_TABLE};"
   #echo "!sh echo Using SQL query: ${HIVE2CSV_SQL}" >> "${HQL_FILE}"
   #echo "${HIVE2CSV_SQL}" >> "${HQL_FILE}"
}
