#!/bin/bash
#
# =============================================================================
#   This script installs SonarQube 4.5.6. Server will start on system start.
#   Listen port 9000, context /sonar.
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified:
#
#   DRAFT VERSION
#
# =============================================================================

# ============ Install SonarQube ============
# -- set up Sonar server DB in MySql server
mysql -u $MYSQL_USER -p$MYSQL_PASS < $SOFTWARE_HOME/sonar-server-db.sql
# -- unzip (quietly) Sonar from distributive
unzip -q $SOFTWARE_HOME/$SONAR_NAME
# -- move Sonar to /opt directory
sudo mv $SONAR_NAME /opt
# -- set up Sonar config (jdbc url/user/pass)
sudo sed -i "/#sonar.jdbc.username=/ c\sonar.jdbc.username=sonar" /opt/$SONAR_NAME/conf/sonar.properties
sudo sed -i "/#sonar.jdbc.password=/ c\sonar.jdbc.password=sonar" /opt/$SONAR_NAME/conf/sonar.properties
sudo sed -i "/#sonar.jdbc.url=jdbc:mysql/ c\sonar.jdbc.url=$SONAR_JDBC_URL" /opt/$SONAR_NAME/conf/sonar.properties
sudo sed -i "/#sonar.web.port=/ c\sonar.web.port=$SONAR_PORT" /opt/$SONAR_NAME/conf/sonar.properties
sudo sed -i "/#sonar.web.context=/ c\sonar.web.context=$SONAR_CONTEXT" /opt/$SONAR_NAME/conf/sonar.properties
# -- setup Sonar for autostart on system boot (copy start script to /etc/init.d)
sudo cp /opt/$SONAR_NAME/bin/$SONAR_PLATFORM/sonar.sh /etc/init.d/sonar
# - edit Sonar WRAPPER/PIDDIR settings
sudo sed -i "/WRAPPER_CMD=/ c\WRAPPER_CMD=/opt/$SONAR_NAME/bin/$SONAR_PLATFORM/wrapper" /etc/init.d/sonar
sudo sed -i "/WRAPPER_CONF=/ c\WRAPPER_CONF=/opt/$SONAR_NAME/conf/wrapper.conf" /etc/init.d/sonar
sudo sed -i "/PIDDIR=/ c\PIDDIR=\"/var/run\"" /etc/init.d/sonar
# - set update-rc.d parameters
sudo update-rc.d -f sonar remove
sudo chmod 755 /etc/init.d/sonar
sudo update-rc.d sonar defaults