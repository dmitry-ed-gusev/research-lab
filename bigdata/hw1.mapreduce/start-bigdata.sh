#!/usr/bin/env bash

# ========================================================
#
#   Starting script for BigData example task (Hadoop).
#   Usage: start-bigdata.sh <text example file>
#
#   Created: Gusev Dmitrii, 30.04.2017
#   Modified: Gusev Dmitrii, 04.05.2017
#
# ========================================================

# - directories for input/output of map-reduce job
BASE_DIR="/user"
USER_DIR="${BASE_DIR}/${USER}"
FINAL_OUTPUT_DIR="${USER_DIR}/output"

# - starting the whole script
echo "Starting job on a cluster..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line arguments error: use at least one file name!"
    exit 1
fi

# - check prerequisites - (and create, if necessary) some dirs
echo "Checking directory [${BASE_DIR}]."
if hdfs dfs -test -d ${BASE_DIR} ; then
    echo "Directory [${BASE_DIR}] is already exist."
else
    hdfs dfs -mkdir ${BASE_DIR}
    echo "Directory [${BASE_DIR}] was created."
fi
echo "Checking directory [${USER_DIR}]."
if hdfs dfs -test -d ${USER_DIR} ; then
    echo "Directory [${USER_DIR}] is already exist."
else
    hdfs dfs -mkdir ${USER_DIR}
    echo "Directory [${USER_DIR}] was created."
fi

# - clean up user dir
# todo: fix error msg, if already clean
echo "Clean up user dir [${USER_DIR}] on HDFS."
hdfs dfs -rm -r -f ${USER_DIR}/*
# - copy (with overwriting) test file(s) into HDFS (to user home)
echo "Copy input file ${1} to HDFS."
hdfs dfs -copyFromLocal -f ${1} ${USER_DIR}/${1}
# - start hadoop map-reduce job
echo "Starting Hadoop Map-Reduce job."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS@ ${1} ${FINAL_OUTPUT_DIR}
# - show output (list output dir)
echo "Show listing of output dir [${FINAL_OUTPUT_DIR}]."
hdfs dfs -ls ${FINAL_OUTPUT_DIR}
