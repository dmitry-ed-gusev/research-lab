#!/usr/bin/env bash

# ========================================================
#
#   Starting script for IPinYou application.
#   BigData course, Homework #2.
#   Usage: ipinyou.sh <local path to files>
#
#   Created: Gusev Dmitrii, 02.06.2017
#   Modified:
#
# ========================================================

# todo: option -sourceBZ2 <> - folder with bz2 files
# todo: option -sourceTXT <> - folder with txt files
# todo: option -destHdfs <> - destination folder in HDFS

# - starting the whole script
echo "Starting IPinYou Utility ..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line arguments error: Specify file path!"
    exit 1
fi

# - start concrete HDFSUtil java class on a Hadoop cluster
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_IPINYOU@ "$@"