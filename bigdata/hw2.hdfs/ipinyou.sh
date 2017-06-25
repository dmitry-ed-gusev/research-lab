#!/usr/bin/env bash

# ========================================================
#
#   Starting script for IPinYou application.
#   BigData course, Homework #2.
#   Usage: ipinyou.sh <local path to files>
#
#   Created:  Gusev Dmitrii, 01.06.2017
#   Modified: Gusev Dmitrii, 24.06.2017
#
# ========================================================

set -e

# - some defaults
RESULT_FILE_NAME="ipinyou_output.data"
# Save node space. If YES will process one bz2 at a time: extract txt, copy to hdfs, delete.
# If NO will extract all txt files from bz2, then copy them to hdfs (and don't delete source txt)
SAVE_SPACE=NO
# If SAVE_SPACE=NO, this option allow to skip unzip bz2 archives
SKIP_UNZIP=NO
# If SAVE_SPACE=NO, this option allow to skip copy txt files to hdfs
SKIP_COPY=NO
# If NO, do IPinYou calculation, if YES - skip calculation
SKIP_IPINYOU=NO

# - cmd line options
OPTION_SOURCE_LOCAL="-sourceLocal"
OPTION_DEST_HDFS="-destHdfs"
OPTION_SAVE_SPACE="-saveSpace"
OPTION_SKIP_UNZIP="-skipUnzip"
OPTION_SKIP_COPY="-skipCopy"
OPTION_SKIP_IPINYOU="-skipIPin"

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
    # - save space option
    ${OPTION_SAVE_SPACE}) SAVE_SPACE=YES
              ;;
    ${OPTION_SKIP_IPINYOU}) SKIP_IPINYOU=YES
              ;;
	esac
done
echo "Cmd line parsed, got all parameters."

if [ "${SAVE_SPACE}" == "YES" ]; then
    echo "Using SAVE SPACE OPTION."
    for file in ${SOURCE_LOCAL}/*.bz2
    do
        echo "Unzip file ${file}"
        bunzip2 -k ${file}
        # <${file%.*}> - get file name without extension
        # <basename ${file%.*}> - get filename without extension from path
        echo "Copy [${file%.*}] to HDFS [${DEST_HDFS}/$(basename ${file%.*})]."
        export HADOOP_CLASSPATH=@JAR_NAME@.jar
        yarn @MAIN_CLASS_HDFS@ -copyFromLocal ${file%.*} -destination ${DEST_HDFS}/$(basename ${file%.*})
        echo "Delete source TXT file ${file%.*}."
        rm ${file%.*}
    done

else

    echo "Do not using SAVE SPACE OPTION."

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

fi

if [ "${SKIP_IPINYOU}" == "NO" ]; then
    # - execute IPinYou application and calculate result
    echo "Starting IPinYou calculation."
    echo "Dest HDFS [${DEST_HDFS}]. Output file [${RESULT_FILE_NAME}]."
    export HADOOP_CLASSPATH=@JAR_NAME@.jar
    yarn @MAIN_CLASS_IPINYOU@ -source ${DEST_HDFS} -outFile ${DEST_HDFS}/${RESULT_FILE_NAME}

    # - copy result file from HDFS (to local)
    #echo "Copy result file ${DEST_HDFS}/${RESULT_FILE_NAME} from HDFS."
    #export HADOOP_CLASSPATH=@JAR_NAME@.jar
    #yarn @MAIN_CLASS_HDFS@ -copyToLocal ${DEST_HDFS}/${RESULT_FILE_NAME} -destination ${RESULT_FILE_NAME}

    # - cat result of calculation (from HDFS)
    #echo "CAT result file ${DEST_HDFS}/${RESULT_FILE_NAME} from HDFS."
    #export HADOOP_CLASSPATH=@JAR_NAME@.jar
    #yarn @MAIN_CLASS_HDFS@ -catFileByFS ${DEST_HDFS}/${RESULT_FILE_NAME}
fi
