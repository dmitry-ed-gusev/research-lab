#!/bin/bash
#
# ===================================================================
#   This script installs Jenkins CI server (last version). 
#   Installation properties:
#    * Jenkins will be launched as a daemon up on start. Startup 
#      script /etc/init.d/jenkins will be created.
#    * The 'jenkins' user is created to run this service.
#    * Log file will be placed in /var/log/jenkins/jenkins.log. 
#    * Config file /etc/default/jenkins will capture configuration 
#      parameters for the launch like e.g JENKINS_HOME
#    * Jenkins listen on port 8080. Access this port with browser 
#      to start configuration/using Jenkins.
#
#   Script should not be started as 'root' (sudo ./<script_name>)!
#   Before running this script you MUST run scripts run1_xxx.sh and
#   run2_xxx.sh (mandatory: JDK6+, some packages).
#  
#   Created:  Gusev Dmitry, 11.11.2015
#   Modified: Gusev Dmitry, 07.12.2015
# ===================================================================

# -- Call other script for set environment for current process
source set_env.sh

# -- Set Jenkins package name for installation. Jenkins version 1.596LTS is the last version of Jenkins
# -- with JDK6 support (for jenkins master/slave).
JENKINS_DEB=jenkins_1.596.3_all.deb

# -- Install necessary Jenkins dependencies: 
# --   [graphviz] - library for graphics (Dependency Graph Viewer in Jenkins)
# --   [daemon]   - package for set up startup/manage script for Jenkins (/etc/init.d/jenkins)
sudo apt-get -qy install graphviz daemon

# ============ Install Jenkins latest version from repository. ============
# -- (Jenkins installed in such a manner will be updateable by apt-get command from Jenkins repository)
# -- Install key for Jenkins repository
# wget -q -O - $JENKINS_KEY_SERVER | sudo apt-key add -
# -- Add Jenkins repository to sources list
# sudo sh -c "echo deb $JENKINS_SOURCES_STRING > /etc/apt/sources.list.d/jenkins.list"
# -- Update with new repository
# sudo apt-get -qy update
# -- Install Jenkins
# sudo apt-get -qy install jenkins

# ============ Install Jenkins concrete version from DEB package. ============
# -- (Jenkins will be not-updateable from Jenkins repository)
sudo dpkg -i jenkins/$JENKINS_DEB
# -- Install plugins (create Jenkins home dir if necessary, copy plugins files to home dir, set right permissions). 
sudo mkdir -p $JENKINS_HOME/plugins
sudo cp -v jenkins/*.hpi $JENKINS_HOME/plugins
sudo chown jenkins.jenkins -R /var/lib/jenkins/plugins/
# -- Restart Jenkins after plugins installation
sudo /etc/init.d/jenkins restart

# -- Setup Apache proxy for access Jenkins via Apache
# - enable proxy/proxy http mods
sudo a2enmod proxy
sudo a2enmod proxy_http
# - add virtual host on Apache2 (create jenkins.conf file for host)
echo "<VirtualHost *:$JENKINS_APACHE_PORT>" | sudo tee $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  ProxyRequests Off" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  <Proxy *>" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "    Order deny,allow" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "    Allow from all" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  </Proxy>" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  ProxyPreserveHost on" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  ProxyPass / http://localhost:8080/ nocanon" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  AllowEncodedSlashes NoDecode" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "</VirtualHost>" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
# - enable proxy host on Apache
sudo a2ensite jenkins
# - advice for user
echo "!!!!! Add port $JENKINS_APACHE_PORT to $APACHE_SETTINGS/ports.conf as line <Listen $JENKINS_APACHE_PORT> and restart Apache !!!!!" 