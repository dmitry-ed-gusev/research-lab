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
#   Modified: Gusev Dmitry, 24.04.2017
# =============================================================================

# todo: change apt-get -> apt

# -- some useful common software packages
sudo apt -qy zip unzip
# -- Midnight Commander
sudo apt-get -qy install mc
# -- Apache2 web server
sudo apt-get -qy install apache2 apache2-utils
# -- NFS support packages (server and client)
sudo apt-get -qy install nfs-kernel-server nfs-common

# todo: move installation of this packages to Sonar installation script
# -- Some necessary packages (c/c++ compiler and check sources libraries)
sudo apt-get -qy install make cmake vera vera++ cppcheck vagrant valgrind

# -- Install GIT
sudo apt-get -qy install git

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after updating
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
