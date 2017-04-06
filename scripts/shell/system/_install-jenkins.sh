#!/bin/bash
#
# =============================================================================
#   This script installs Jenkins server CI tool. Installation properties:
#      * installation port: 5000
#      * will be launched as a daemon up on start. Startup script /etc/init.d/jenkins
#      * The 'jenkins' user will be created to start this service
#      * Log file will be placed in /var/log/jenkins/jenkins.log
#      * Config file /etc/default/jenkins will hold configuration for the Jenkins server
#      * Jenkins by default will listen on port 8181. Access this port with browser to
#        start using Jenkins
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified:
#
#   DRAFT VERSION!!!!
#
# =============================================================================

# ============ Install Jenkins latest version from inet repository ============
# Jenkins installed in such a manner will be updateable by apt-get command from Jenkins repository (it's not really good!)
# -- Install key for Jenkins repository
# wget -q -O - $JENKINS_KEY_SERVER | sudo apt-key add -
# -- Add Jenkins repository to sources list
# sudo sh -c "echo deb $JENKINS_SOURCES_STRING > /etc/apt/sources.list.d/jenkins.list"
# -- Update with new repository
# sudo apt-get -qy update
# -- Install Jenkins
# sudo apt-get -qy install jenkins

# ============ Install Jenkins concrete version from DEB package (local installation) ============
# -- (Jenkins will be not-updateable from Jenkins repository)
sudo dpkg -i $SOFTWARE_HOME/jenkins/$JENKINS_DEB
# -- Install plugins (create Jenkins home dir if necessary, copy plugins files to home dir, set right permissions).
sudo mkdir -p $JENKINS_HOME/plugins
sudo cp -v $SOFTWARE_HOME/jenkins/*.hpi $JENKINS_HOME/plugins
sudo chown jenkins.jenkins -R /var/lib/jenkins/plugins/

# change port number for Jenkins instance (using sed in inline mode - change a whole string (started
# with 'HTTP_PORT') with new string with our port value)
sudo sed -i "/HTTP_PORT=/ c\HTTP_PORT=$JENKINS_INTERNAL_PORT" /etc/default/jenkins

# -- Restart Jenkins after plugins installation and changing port
sudo /etc/init.d/jenkins restart

# -- Setup Apache proxy for access Jenkins via Apache (turn on some mods)
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
echo "  ProxyPass / http://localhost:$JENKINS_INTERNAL_PORT/ nocanon" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "  AllowEncodedSlashes NoDecode" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
echo "</VirtualHost>" | sudo tee -a $APACHE_SETTINGS/sites-available/jenkins.conf
# - enable proxy host on Apache
sudo a2ensite jenkins
# - add port to Apache config
echo "Listen $JENKINS_APACHE_PORT" | sudo tee -a $APACHE_SETTINGS/ports.conf
# - restart Apache (after port adding)
sudo service apache2 restart