#!/usr/bin/env bash

# ========================================================
#
#   Starting script for BigData example task (Hadoop).
#   Usage: start-bigdata.sh <text example file>
#
#   Created: Gusev Dmitrii, 30.04.2017
#
# ========================================================

# directories for input/output of map-reduce job
BASE_DIR="/user"
USER_DIR="${BASE_DIR}/${USER}"
FINAL_OUTPUT_DIR="$USER_DIR/output"
#INTERMEDIATE_OUTPUT="$USER_DIR/intermediate_output"
#REDUCER_OUTPUT="part-r-00000"

# starting mapreduce job
echo "Starting MapReduce job on a cluster..."
# check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line arguments error: use at least one file name!"
    exit 1
fi

# check prerequisites - (and create, if necessary) some dirs
echo "Checking directory [${BASE_DIR}]."
if hdfs dfs -test -d ${BASE_DIR} ; then
    hdfs dfs -mkdir ${BASE_DIR}
    echo "Directory [${BASE_DIR}] was created."
fi
echo "Checking directory [${USER_DIR}]."
if hdfs dfs -test -d ${USER_DIR} ; then
    hdfs dfs -mkdir ${USER_DIR}
    echo "Directory [${USER_DIR}] was created."
fi

# - create output dir if not exists
#if hadoop fs -test -d $OUTPUT ; then
#    echo "Directory  [$OUTPUT] exists. Won't create it."
#else
#    # -- maybe add option -p (without it - fail if exists)?
#    hadoop fs -mkdir $OUTPUT
#    echo "Directory [$OUTPUT] created."
#fi

# - if output dir exists - empty and delete it
#if hdfs dfs -test -d $OUTPUT ; then
#    hdfs dfs -rm -f "$OUTPUT/*"
#    hdfs dfs -rmdir "$OUTPUT"
#fi

# clean up user dir
# todo: fix error msg, if already clean
hdfs dfs -rm -r -f ${USER_DIR}/*
# copy (with overwriting) test file(s) into HDFS (to user home)
hdfs dfs -copyFromLocal -f ${1} ${1}
# start hadoop map-reduce job
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS@ ${1} ${FINAL_OUTPUT}
# if successful, show output (list output dir)
#hdfs dfs -cat $OUTPUT/$REDUCER_OUTPUT
hdfs dfs -ls ${FINAL_OUTPUT}
