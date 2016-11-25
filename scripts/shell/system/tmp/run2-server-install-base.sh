#!/bin/bash
#
# =============================================================================
#   This script installs the following basic software:
#    - Midnight Commander
#    - Apache2 web server
#    - NFS support software (server/client packages)
#    - VCS clients/servers (Subversion/Git/Mercurial). For Subversion installed both 
#      server/client, server will be set up/tuned and added to init.d (execute on startup)
#    - GitLab distributed VCS server (local installation, from DEB package) (port 7000)
#    - MySql (server+client, last versions)
#    - Ruby/Rails/RVM (last versions)
#    - Java 7/8 (see definitions block)
#    - Jenkins CI server (port 5000). Installation properties: 
#      * Jenkins will be launched as a daemon up on start. Startup script /etc/init.d/jenkins
#      * The 'jenkins' user will be created to start this service
#      * Log file will be placed in /var/log/jenkins/jenkins.log
#      * Config file /etc/default/jenkins will hold configuration for the Jenkins server
#      * Jenkins by default will listen on port 8181. Access this port with browser to start using Jenkins
#    - Apache Ant 1.9.6 - java build tool. Added to system wide PATH variable.
#    - Apache Maven 3.3.3 - java build tool. Added to system wide PATH variable.
#    - SonarQube 4.5.6. Server will start on system start. Listen port 9000, context /sonar.
# 
#   If you want to skip some software, you may use keys:
#    -noruby    - skip Ruby/Rails/RVM/Redmine installation
#    -nomysql   - skip MySql installation
#    -nojava    - skip Java/Jenkins installation
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#  
#   Created:  Gusev Dmitry, 22.10.2015
#   Modified: Gusev Dmitry, 21.03.2016
# =============================================================================

# --- DEBUG MODE (ON/OFF) ---
# - If DEBUG=true script will wait for a key press after every logic part installation. Set DEBUG
# - value to any other, than "true" - script will slip any question.
DEBUG=true

# -- Call other script for set up environment
source set_env.sh

# -- Check cmd line arguments and set FALSE flag for some installations
for arg in "$@"
do
	case "$arg" in
    -noruby)  	INSTALL_RUBY=NO
				;;
    -nomysql) 	INSTALL_MYSQL=NO
				;;
	-nojava)  	INSTALL_JAVA=NO
				;;
	esac
done

# ============================== INSTALL: COMMON SOFTWARE ==============================
# -- Midnight Commander
sudo apt-get -qy install mc
echo "---> MC installed"
# -- Apache2 web server
sudo apt-get -qy install apache2 
echo "---> Apache2 installed"
# -- NFS support packages (server and client)
sudo apt-get -qy install nfs-kernel-server nfs-common
echo "---> NFS support installed"
# -- Some necessary packages (c/c++ compiler and check sources libraries)
sudo apt-get -qy install make cmake vera vera++ cppcheck vagrant valgrind
echo "---> Nesessary packages installed. Next -> VCS clients/servers."
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# ============================== INSTALL: VCS (SVN/GIT/HG) ==============================
# -- VCS support: Subversion/Git/Mercurial (+libraries, +apache2 module, +startup)
sudo apt-get -qy install git mercurial subversion libapache2-svn libsvn1 apache2-utils
echo "---> VCS clients installed. Next -> Subversion server."
# -- Subversion server: change some settings (enable module authz_svn for apache and restart)
sudo a2enmod authz_svn
sudo service apache2 restart
# create repositories home directory
sudo mkdir -v $SVN_REPOS_HOME
# copy start/stop svn script to init.d, make it executable, add to startup list (init.d), start svn
sudo cp -fv $SOFTWARE_HOME/subversion/$SVN_STARTUP_SCRIPT /etc/init.d
sudo chmod -v +x /etc/init.d/$SVN_STARTUP_SCRIPT
sudo update-rc.d $SVN_STARTUP_SCRIPT defaults
sudo /etc/init.d/$SVN_STARTUP_SCRIPT start
echo "---> Subversion server installed. Next -> GITLAB server"
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- GitLab server (installation from DEB package)
sudo dpkg -i $SOFTWARE_HOME/$GITLAB_PACKAGE_NAME
# configure GitLab for first time
sudo gitlab-ctl reconfigure
# change port number for GitLab instance (using sed in inline mode - 
# change a whole string (started with 'external_url') with new string with our value)
sudo sed -i "/external_url/ c\external_url '$GITLAB_HOST:$GITLAB_PORT'" /etc/gitlab/gitlab.rb
# reconfigure GitLab instance
sudo gitlab-ctl reconfigure
# - advice for user
#echo "!!!!! Change /etc/gitlab/gitlab.rb (external_url setting) and port $GITLAB_PORT to $APACHE_SETTINGS/ports.conf as line <Listen $GITLAB_PORT> and restart Apache !!!!!" 
echo "---> GitLab server installed. Next -> MySQL -> Ruby/Rvm/Rails -> Java/Jenkins."
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# ============================== INSTALL: MYSQL ==============================
# -- Check flag and install/don't install MySql DBMS
if [ "$INSTALL_MYSQL" == "NO" ]; then
	echo "!!! Mysql installation skipped !!!"
else
	# -- Install MySql (server+client) - last version. Before installing - set root password.
	sudo debconf-set-selections <<< "mysql-server mysql-server/root_password password $MYSQL_PASS"
	sudo debconf-set-selections <<< "mysql-server mysql-server/root_password_again password $MYSQL_PASS"
	sudo apt-get -qy install mysql-server mysql-client
	echo "---> MySql installed. Next -> Ruby/Rvm/Rails -> Java/Jenkins."
fi
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi
	
# ============================== INSTALL: Ruby/Rails/RVM ==============================
# -- Check flag and install/don't install Ruby/Rails/RVM
if [ "$INSTALL_RUBY" == "NO" ]; then
	echo "!!! Ruby/Rails/RVM installation skipped !!!"
else
	# -- Install Ruby/Rails/RVM
	sudo apt-get -qy remove ruby
	# - install(import) nesessary key
	sudo gpg --keyserver $RVM_KEY_SERVER --recv-keys $RVM_KEY
	# - installation
	\curl -sSL $RVM_SERVER | sudo bash -s stable --rails
	# - execute the script in the first (current) script process, and pulls in variables and functions from the 
	# - other script so they are usable from the calling script
	source /usr/local/rvm/scripts/rvm
	# - add users to rvm group (current user ($USER) and www-data <- apache user)
	# - this is necessary (a must!) for Redmine installing
	sudo usermod -aG rvm $USER
	sudo usermod -aG rvm www-data
	echo "---> Ruby/Rails/RVM installed. Next -> Java/Jenkins."
fi
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# ============================== INSTALL: JAVA/JENKINS/ANT/MAVEN ==============================
# -- Check flag and install/don't install Java
if [ "$INSTALL_JAVA" == "NO" ]; then
	echo "!!! Java installation skipped !!!"
else
	# -- Install Java 
	# - add alternate repository for Oracle Java
	sudo add-apt-repository -y $JAVA_ALT_REPO
	sudo apt-get -qy update
	# - auto accept Oracle license
	sudo echo oracle-java$JAVA_VERSION-installer shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
	sudo apt-get -qy install oracle-java$JAVA_VERSION-installer
	echo "---> Java $JAVA_VERSION installed. Next -> Jenkins."
	
	# Install Jenkins dependencies:  [graphviz] - library for graphics (Dependency Graph Viewer plugin in Jenkins),
	# [daemon] - package for set up startup/manage script for Jenkins (/etc/init.d/jenkins)
	sudo apt-get -qy install graphviz daemon
	
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
	
	# ============ Install Apache ANT ============
	# -- extract ANT from archive
	tar xvfz $SOFTWARE_HOME/$ANT_ARCHIVE
	# -- set executable bit for <ant> - starting script
	sudo chmod +x $ANT_NAME/bin/ant
	# -- move ANT to /opt directory
	sudo mv $ANT_NAME /opt
	# -- add path to ANT executable to PATH variable (system wide). 
	# "" - in this quotes variables will be processed (values used)
	# '' - in this quotes variables will be used 'as is' (no values)
	echo "ANT_HOME=/opt/$ANT_NAME" | sudo tee /etc/profile.d/ant.sh
	echo 'PATH="$ANT_HOME/bin:$PATH"' | sudo tee -a /etc/profile.d/ant.sh
	sudo chmod +x /etc/profile.d/ant.sh

	# ============ Install Apache MAVEN ============
	# -- extract MAVEN from archive
	tar xvfz $SOFTWARE_HOME/$MAVEN_ARCHIVE
	# -- set executable bit for <mvn> - starting script
	sudo chmod +x $MAVEN_NAME/bin/mvn
	# -- move MAVEN to /opt directory
	sudo mv $MAVEN_NAME /opt
	# -- add path to MAVEN executable to PATH variable (system wide). 
	# "" - in this quotes variables will be processed (values used)
	# '' - in this quotes variables will be used 'as is' (no values)
	echo "M2_HOME=/opt/$MAVEN_NAME" | sudo tee /etc/profile.d/maven.sh
	echo 'PATH="$M2_HOME/bin:$PATH"' | sudo tee -a /etc/profile.d/maven.sh
	sudo chmod +x /etc/profile.d/maven.sh
	
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
fi
# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot after installation
sudo reboot now
