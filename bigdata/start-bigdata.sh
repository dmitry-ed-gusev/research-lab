#!/usr/bin/env bash

# ========================================================
#
#   Starting script for BigData example task (Hadoop).
#   Usage: start-bigdata.sh <text example file>
#
#   Created: Gusev Dmitrii, 15.11.2016
#
# ========================================================

# - output dir for map-reduce job
USER_DIR="/user/$USER"
OUTPUT="$USER_DIR/intermediate_output"

#REDUCER_OUTPUT="part-r-00000"

# // todo: add arguments check
# - check cmd line arguments count

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

# - clean up user dir
# // todo: fix error msg, if already clean
hdfs dfs -rm -r $USER_DIR/*

# - copy (with overwriting) test file into HDFS (to user home dir)
hdfs dfs -copyFromLocal -f ${1} ${1}

# - start hadoop map-reduce job
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS@ ${1} $OUTPUT

# - if successful, show output
#hdfs dfs -cat $OUTPUT/$REDUCER_OUTPUT
hdfs dfs -ls $OUTPUT
