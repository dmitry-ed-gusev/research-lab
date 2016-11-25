#!/usr/bin/env bash

. common_env.sh

# -- create Hive database
hive -e "CREATE DATABASE IF NOT EXISTS $HIVE_DB;"

# -- create Hive table
hive -e "CREATE TABLE IF NOT EXISTS $HIVE_DB.$MSD_HIVE_TABLE($HIVE_TABLE_COLUMNS) \
PARTITIONED BY ($HIVE_PARTITION String) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' STORED AS TEXTFILE;"

# -- load data from CSV file
hive -e "LOAD DATA LOCAL INPATH \"$CSV_FILE\" OVERWRITE INTO TABLE $HIVE_DB.$MSD_HIVE_TABLE PARTITION ($HIVE_PARTITION='10-18-2016');"