@echo off
rem =========================================================================================================
rem
rem    Starting batch (win) script for IPinYou application.
rem    BigData course, Homework #2.
rem    Usage: ipinyou.bat <hdfs user> <hdfs source folder> <hdfs output file>
rem
rem    Sample cmd line:
rem     ipinyou.bat myuser webhdfs://localhost/user/myuser webhdfs://localhost/user/myuser/ipinyou_output.txt
rem
rem    Created:  Gusev Dmitrii, 25.06.2017
rem    Modified: Gusev Dmitrii, 07.07.2017
rem
rem =========================================================================================================

echo "Starting IPinYou application"
java -cp @JAR_NAME@.jar;libs\* @MAIN_CLASS_IPINYOU@ -pauseBefore -skipNulls -hdfsUser %1 -source %2 -outFile webhdfs://localhost/user/myuser/bid_result.txt

rem echo "Reading result file from HDFS"
rem todo: implement it :)