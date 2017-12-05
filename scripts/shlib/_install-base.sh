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
#   Modified: Gusev Dmitry, 06.11.2017
# =============================================================================

# -- install some useful common software packages
sudo apt -qy zip unzip
# -- install Midnight Commander
sudo apt -qy install mc
# -- install Apache2 web server
sudo apt -qy install apache2 apache2-utils
# -- install NFS support packages (server and client)
sudo apt -qy install nfs-kernel-server nfs-common
# -- install GIT
sudo apt -qy install git

# -- install pip (python package manager) and virtualenv
wget https://bootstrap.pypa.io/get-pip.py
sudo -H python get-pip.py
sudo -H pip install virtualenv
