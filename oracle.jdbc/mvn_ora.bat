@echo off
rem Use current version number [12.2.0.1.0] in root POM for this project (<properties>).
rem todo: check if oracle driver is installed and (if not) - install it (during build).

echo Installing Oracle JDBC Driver, ver. 12.2.0.1.0
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1.0 -Dpackaging=jar -Dfile=ojdbc8.jar -DgeneratePom=true
