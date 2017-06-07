#!/usr/bin/env bash

# ========================================================
#
#   Starting script for IPinYou application.
#   BigData course, Homework #2.
#   Usage: ipinyou.sh <local path to files>
#
#   Created:  Gusev Dmitrii, 02.06.2017
#   Modified: Gusev Dmitrii, 06.06.2017
#
# ========================================================

# todo: implement skip unzip file option
# todo: implement skip copy to hdfs option

# - some defaults
RESULT_FILE_NAME="ipinyou_output.txt"
SKIP_UNZIP=NO
SKIP_COPY=NO
# - cmd line options
OPTION_SOURCE_LOCAL="-sourceLocal"
OPTION_DEST_HDFS="-destHdfs"
OPTION_SKIP_UNZIP="-skipUnzip"
OPTION_SKIP_COPY="-skipCopy"

# - starting the whole script
echo "Starting IPinYou Utility ..."
# - check cmd line arguments count (exit if none)
if [[ $# -eq 0 ]] ; then
    echo "CMD Line error: specify both [${OPTION_SOURCE_LOCAL} <local dir>] and [${OPTION_DEST_HDFS} <HDFS dir>]!"
    exit 1
fi

# - parse cmd line arguments and get values
for arg in "$@"
do
	case "$arg" in
	# - folder with source (raw) BZ2 files
	${OPTION_SOURCE_LOCAL})
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
	${OPTION_DEST_HDFS})
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
	# - skip unzipping files
	${OPTION_SKIP_UNZIP}) SKIP_UNZIP=YES
	          ;;
	# - skip copy txt files to HDFS
    ${OPTION_SKIP_COPY}) SKIP_COPY=YES
              ;;
	esac
done
echo "Cmd line parsed, got all parameters."

# - extract txt files from bz2 archives
if [ "${SKIP_UNZIP}" == "NO" ]; then
    echo "Start unzipping files."
    for file in ${SOURCE_LOCAL}/*.bz2
    do
        echo "Processing ${file} file."
        bunzip2 -k ${file}
    done
fi

# - copy txt files to HDFS destination
# todo: move this logic to hdfs utility
if [ "${SKIP_COPY}" == "NO" ]; then
    echo "Start copy files to HDFS."
    for file in ${SOURCE_LOCAL}/*.txt
    do
        echo "Copy [${file}] to HDFS [${DEST_HDFS}/$(basename ${file})]."
        export HADOOP_CLASSPATH=@JAR_NAME@.jar
        yarn @MAIN_CLASS_HDFS@ -copyFromLocal ${file} -destination ${DEST_HDFS}/$(basename ${file})
    done
fi

# - execute IPinYou application and calculate result
echo "Start IPinYou calculation."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_IPINYOU@ -source ${DEST_HDFS} -outFile ${RESULT_FILE_NAME}
# todo: !!!
exit 777

# - cat result of calculation (from HDFS)
echo "CAT result file ${RESULT_FILE_NAME} from HDFS."
export HADOOP_CLASSPATH=@JAR_NAME@.jar
yarn @MAIN_CLASS_HDFS@ -catFileByFS ${RESULT_FILE_NAME}