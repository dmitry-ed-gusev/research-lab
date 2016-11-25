#!/bin/bash
#
# ===================================================================
#   This script installs the following basic software:
#    - NFS support software (server/client packages)
#    - Midnight Commander
#    - Apache2
#    - MySql (server+client, last versions)
#    - Ruby/Rails/RVM (last versions)
#    - Java 7/8 (see definitions block)
#   If you want to skip one?some of the software, you may use keys:
#    -noruby  - skip Ruby/Rails/RVM installation
#    -nomysql - skip MySql installation
#    -nojava  - skip Java installation 
#   Script should not be started as user 'root' (sudo ./<script_name>)!
#  
#   Created:  Gusev Dmitry, 22.10.2015
#   Modified: Gusev Dmitry, 23.12.2015
# ===================================================================

# -- Call other script for set environment for current process
source set_env.sh

# -- Check cmd line arguments and set FALSE flag for some installations
for arg in "$@"
do
	case "$arg" in
    -noruby)  INSTALL_RUBY=NO
			  ;;
    -nomysql) INSTALL_MYSQL=NO
              ;;
	-nojava)  INSTALL_JAVA=NO
			  ;;
	esac
done

# -- Install NFS support packager (server and client)
sudo apt-get -qy install nfs-kernel-server nfs-common
echo "---> NFS support installed"

# -- Install Midnight Commander
sudo apt-get -qy install mc
echo "---> MC installed"

# -- Install Apache2 web server
sudo apt-get -qy install apache2 
echo "---> Apache2 installed"

# -- Check flag and install/don't install MySql DBMS
if [ "$INSTALL_MYSQL" == "NO" ]; then
	echo "!!! Mysql installation skipped !!!"
else
	# -- Install MySql (server+client) - last version. Before installing - set root password.
	sudo debconf-set-selections <<< "mysql-server mysql-server/root_password password $MYSQL_PASS"
	sudo debconf-set-selections <<< "mysql-server mysql-server/root_password_again password $MYSQL_PASS"
	sudo apt-get -qy install mysql-server mysql-client
	echo "---> MySql installed"
fi

# -- Check flag and install/don't install Ruby/Rails/RVM
if [ "$INSTALL_RUBY" == "NO" ]; then
	echo "!!! Ruby/Rails/RVM installation skipped !!!"
else
	# -- Install Ruby/Rails/RVM
	sudo apt-get -qy remove ruby
	# - install(import) nesessary key
	gpg --keyserver $RVM_KEY_SERVER --recv-keys $RVM_KEY
	# - installation
	\curl -sSL $RVM_SERVER | sudo bash -s stable --rails
	# - execute the script in the first (current) script process, and pulls in variables and functions from the 
	# - other script so they are usable from the calling script
	source /usr/local/rvm/scripts/rvm
	# - add users to rvm group (current user ($USER) and www-data <- apache user)
	# - this is necessary (a must!) for Redmine installing
	sudo usermod -aG rvm $USER
	sudo usermod -aG rvm www-data
	echo "---> Ruby/Rails/RVM installed"
fi

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
	echo "---> Java $JAVA_VERSION installed"
fi

# -- Reboot after installation
sudo reboot now
