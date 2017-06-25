@echo off
rem ========================================================
rem
rem    Starting batch (win) script for IPinYou application.
rem    BigData course, Homework #2.
rem    Usage: ipinyou.bat  <local path to files>
rem
rem    Created:  Gusev Dmitrii, 25.06.2017
rem    Modified:
rem
rem ========================================================

echo "Starting IPinYou application"
java -cp @JAR_NAME@.jar @MAIN_CLASS_IPINYOU@ -source %1 -outFile %2