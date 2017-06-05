#!/usr/bin/env bash

# ========================================================
#
#   Starting script for IPinYou application.
#   BigData course, Homework #2.
#   Usage: ipinyou.sh <local path to files>
#
#   Created:  Gusev Dmitrii, 02.06.2017
#   Modified: Gusev Dmitrii, 05.06.2017
#
# ========================================================

# todo: implement skip unzip file option
# todo: implement skip copy to hdfs option

# - some task defaults
RESULT_FILE_NAME="ipinyou_output.txt"
SOURCE_LOCAL_OPTION="-sourceLocal"
DEST_HDFS_OPTION="-destHdfs"

# - starting the whole script
echo "Starting IPinYou Utility ..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line error: specify both [${SOURCE_LOCAL_OPTION} <local dir>] and [${DEST_HDFS_OPTION} <HDFS dir>]!"
    exit 1
fi

# - parse cmd line arguments and get values
for arg in "$@"
do
	case "$arg" in
	# - folder with source (raw) BZ2 files
	"${SOURCE_LOCAL_OPTION}")
	          shift
              if test $# -gt 0; then
                SOURCE_LOCAL=$1
                echo "Using local source folder: ${SOURCE_LOCAL}"
              else
                echo "Error: no local source folder value specified!"
                exit 1
              fi
              shift
	          ;;
	# - destination - HDFS folder
	-destHdfs)
	          shift
              if test $# -gt 0; then
                DEST_HDFS=$1
                echo "Using HDFS destination folder: ${DEST_HDFS}"
              else
                echo "Error: no HDFS destination folder value specified!"
                exit 1
              fi
              shift
	          ;;
	esac
done
echo "Cmd line parsed, got all parameters."

# - extract txt files from bz2 archives
echo "Start unzipping files."
for file in ${SOURCE_LOCAL}/*.bz2
do
    echo "Processing ${file} file."
    bunzip2 -k ${file}
done

# - copy txt files to HDFS destination
echo "Start copy files to HDFS."
# todo: ${file} contains full local path, we need only file name
for file in ${SOURCE_LOCAL}/*.txt
do
    export HADOOP_CLASSPATH=@JAR_NAME@.jar
    echo "Copy [${file}] to HDFS [${DEST_HDFS}/${file}]."
    yarn @MAIN_CLASS_HDFS@ -copyFromLocal ${file} -destination ${DEST_HDFS}/${file}
done
exit 777

# - execute IPinYou application and calculate result
echo "Start IPinYou calculation."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_IPINYOU@ ${DEST_HDFS} ${RESULT_FILE_NAME}

# - cat result of calculation (from HDFS)
echo "CAT result file ${RESULT_FILE_NAME} from HDFS."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_HDFS@ "-catFileByFS ${RESULT_FILE_NAME}"