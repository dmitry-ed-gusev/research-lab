@echo off

echo Sending email message
java -jar @JAR_NAME@.jar -mailHost dc-edgemail -mailPort 25 -mailUser mesdev -mailPass HKs524Bl0t ^
 -from mesdev@kzgroup.ru -subject -text -files