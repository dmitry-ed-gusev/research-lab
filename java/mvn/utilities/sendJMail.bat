@echo off
rem *********************************************************
rem *                                                       *
rem *   Example script for sending email from cmd line.     *
rem *                                                       *
rem *   Created by:  Gusev Dmitrii, 2013                    *
rem *   Modified by:                                        *
rem *********************************************************

echo Sending email message
java -jar @JAR_NAME@.jar -mailHost <mail server host> -mailPort <mail server port> -mailUser <user> -mailPass <password> ^
 -from <back address> -subject <email subject> -text <email text> -files <list of attached files>