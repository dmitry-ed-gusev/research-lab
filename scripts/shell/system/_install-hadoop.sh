#!/bin/bash
#
# =============================================================================
#   This script install Apache Hadoop platform on Ubuntu Server machine.
#   Script is a part of scripts suite and shouldn't be called by itself.
#   Use <./mysys.sh -install-hadoop>
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#  
#   Created:  Gusev Dmitry, 10.04.2017
#   Modified:
# =============================================================================

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after updating
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
