#!/usr/bin/env bash

# ========================================================
#
#   Starting script for HDFS utility in Java.
#   Usage: hdfsutil.sh <path in hdfs>
#
#   Created: Gusev Dmitrii, 05.05.2017
#   Modified:
#
# ========================================================

# - starting the whole script
echo "Starting HDFS Utility ..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line arguments error: use at least one file name/path!"
    exit 1
fi

# - start concrete HDFSUtil java class on a Hadoop cluster
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_HDFS@ "$@"