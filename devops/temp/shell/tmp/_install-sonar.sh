#!/bin/bash
#
# =============================================================================
#   This script installs SonarQube 5.X.X Server will start on system start.
#   Installation properties:
#       * sonar process will listen port 9000
#       * sonar context on server -> [/sonar], so Sonar will be accessible by URL
#         http://<host name>:9000/sonar
#       * initial user/password: admin/admin
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified: Gusev Dmitrii, 25.11.2017
#
# =============================================================================

# todo: before proceeding - check mysql installed???
# todo: https://github.com/saurabhjuneja/sonar-example/tree/master/scripts/database/mysql

# - install some necessary packages (for c/c++ -> c/c++ compiler and check sources libraries)
sudo apt-get -qy install make cmake vera vera++ cppcheck vagrant valgrind

# - delete target Sonar ZIP archive file if it exists
if [ -f ${SONAR_ARCHIVE} ] ; then
    rm ${SONAR_ARCHIVE}
fi
# - download Sonar from server
wget ${SONAR_BINARY_URL}
# - unzip (quietly) Sonar from archive
unzip -q ${SONAR_ARCHIVE}
# - set executable bit for Sonar starting script
sudo chmod +x ${SONAR_NAME}/bin/${SONAR_PLATFORM}/sonar.sh
# - move Sonar to /opt directory
sudo mv ${SONAR_NAME} /opt
# - set up Sonar server DB in MySql server (execute script)
pwd
mysql -u root -p${MYSQL_ROOT_PASS} < shlib/create_sonar_db.sql

# - set up Sonar config (jdbc url/user/pass) -> /opt/<sonar_home>/conf/sonar.properties
sudo sed -i "/#sonar.jdbc.username=/ c\sonar.jdbc.username=sonar" /opt/${SONAR_NAME}/conf/sonar.properties
sudo sed -i "/#sonar.jdbc.password=/ c\sonar.jdbc.password=sonar" /opt/${SONAR_NAME}/conf/sonar.properties
sudo sed -i "/#sonar.jdbc.url=jdbc:mysql/ c\sonar.jdbc.url=${SONAR_JDBC_URL}" /opt/${SONAR_NAME}/conf/sonar.properties
sudo sed -i "/#sonar.web.port=/ c\sonar.web.port=${SONAR_PORT}" /opt/${SONAR_NAME}/conf/sonar.properties
sudo sed -i "/#sonar.web.context=/ c\sonar.web.context=${SONAR_CONTEXT}" /opt/${SONAR_NAME}/conf/sonar.properties
# - setup Sonar for autostart on system boot (copy start script to /etc/init.d)
sudo cp /opt/${SONAR_NAME}/bin/${SONAR_PLATFORM}/sonar.sh /etc/init.d/sonar
# - edit Sonar WRAPPER/PIDDIR settings
sudo sed -i "/WRAPPER_CMD=/ c\WRAPPER_CMD=/opt/${SONAR_NAME}/bin/${SONAR_PLATFORM}/wrapper" /etc/init.d/sonar
sudo sed -i "/WRAPPER_CONF=/ c\WRAPPER_CONF=/opt/${SONAR_NAME}/conf/wrapper.conf" /etc/init.d/sonar
sudo sed -i "/PIDDIR=/ c\PIDDIR=\"/var/run\"" /etc/init.d/sonar
# - set update-rc.d parameters
sudo update-rc.d -f sonar remove
sudo chmod 755 /etc/init.d/sonar
sudo update-rc.d sonar defaults

# - optional -> start Sonar server after installation
# sudo /etc/init.d/sonar start
