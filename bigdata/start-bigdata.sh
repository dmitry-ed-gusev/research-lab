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
OUTPUT="/user/$USER/output"
REDUCER_OUTPUT="part-r-00000"

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
if hadoop fs -test -d $OUTPUT ; then
    hadoop fs -rm -f "$OUTPUT/*"
    hadoop fs -rmdir "$OUTPUT"
fi

# - copy (with overwriting) test file into HDFS (to user home dir)
hadoop fs -copyFromLocal -f ${1} ${1}

# - start hadoop map-reduce job
export HADOOP_CLASSPATH=@JAR_NAME@.jar
hadoop @MAIN_CLASS@ ${1} $OUTPUT

# - if successful, show output
hadoop fs -cat $OUTPUT/$REDUCER_OUTPUT
