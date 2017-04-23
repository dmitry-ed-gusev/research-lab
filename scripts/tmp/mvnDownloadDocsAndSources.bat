@echo off
rem sources could be downloaded with this command #1
call mvn dependency:resolve -Dclassifier=sources
rem sources could be downloaded and by this command #2
rem mvn dependency:sources
call mvn dependency:resolve -Dclassifier=javadoc