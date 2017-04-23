#!/usr/bin/env bash

# This script should be called as <. test.sh> - not as a child process (<./test.sh>),
# but in a context of current process

# -- set some useful variables
COLLECTION_NAME="com.citibikenyc"

# -- call to common libraries
. common_env.sh
. river_common.sh
. hive_common.sh

# -- example: creating collection in Abstract
#create_collection COLLECTION_ID $COLLECTION_NAME

# -- example: generate JSON with table meta info
export-table-test "$MSD_HIVE_TABLE" "$MSD_HIVE_TABLE_COLUMNS"