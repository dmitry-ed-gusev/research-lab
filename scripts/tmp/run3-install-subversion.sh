#!/bin/bash
#
# ===================================================================
#   This script installs Subversion server (+client untilities).
#   Script should not be started as user 'root' 
#   (sudo ./<script_name>)!
#  
#   Created:  Gusev Dmitry, 27.10.2015
#   Modified: Gusev Dmitry, 02.11.2015
# ===================================================================

# -- Call other script for set environment for current process
source set_env.sh

# -- Install Subversion (+libraries, +apache2 module, +startup)
sudo apt-get -qy install subversion libapache2-svn libsvn1 apache2-utils
# - turn on (enable) module authz_svn for apache
sudo a2enmod authz_svn
# - restart apache2 service
sudo service apache2 restart
# - create repos directory
sudo mkdir -v $SVN_REPOS_HOME
# - copy start/stop script to init.d catalog
sudo cp -fv subversion/$SVN_STARTUP_SCRIPT /etc/init.d
# - make subversion script executable
sudo chmod -v +x /etc/init.d/$SVN_STARTUP_SCRIPT
# - add script to startup list
sudo update-rc.d $SVN_STARTUP_SCRIPT defaults
# - start subversion 
sudo /etc/init.d/$SVN_STARTUP_SCRIPT start

# -- Reboot after installation
sudo reboot now