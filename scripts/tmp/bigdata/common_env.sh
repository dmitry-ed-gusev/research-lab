#!/usr/bin/env bash

# -- some common variables
USER_HOME_DIR="/user/$USER"

# -- Hive variables
HIVE_DB="csv_db"
MSD_HIVE_TABLE="csv_table"
MSD_HIVE_TABLE_COLUMNS="data_id int,data_string string"
HIVE_PARTITION="load_dttm"
CSV_FILE="/home/$USER/scripts/hive.csv"

