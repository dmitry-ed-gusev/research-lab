#!/bin/bash
#
# =============================================================================
#   This script installs the following basic software:
#    - Midnight Commander
#    - Apache2 web server
#    - NFS support software (server/client packages)
#    - Git client
#    - some useful utility packages
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#  
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified:
# =============================================================================

# -- Midnight Commander
sudo apt-get -qy install mc

# -- [graphviz] - library for graphics (Dependency Graph Viewer plugin in Jenkins),
# -- [daemon]   - package for set up startup/manage script for Jenkins (/etc/init.d/jenkins)
sudo apt-get -qy install graphviz daemon

# -- Apache2 web server
sudo apt-get -qy install apache2 

# -- NFS support packages (server and client)
sudo apt-get -qy install nfs-kernel-server nfs-common

# -- Some necessary packages (c/c++ compiler and check sources libraries)
sudo apt-get -qy install make cmake vera vera++ cppcheck vagrant valgrind

# -- VCS support: Subversion/Git/Mercurial (+libraries, +apache2 module, +startup)
sudo apt-get -qy install git

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system aftre updating
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
