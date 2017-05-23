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
echo "Starting HDFS Util on a cluster..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line arguments error: use at least one file name/path!"
    exit 1
fi

# - start concrete HDFSUtil java class on a Hadoop cluster
echo "Starting HDFSUtil Java class."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
#yarn @MAIN_CLASS@ ${1}
hadoop @MAIN_CLASS@ ${1}