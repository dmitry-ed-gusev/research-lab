#!/bin/bash
# ===================================================================
#   Script removes (unsets) the system/APT proxy server for current
#   user (in ~/.profile file and for APT utility (/etc/apt/apt.conf)).
#
#   This script is a part of scripts suite and shouldn't be called
#   by itself. Use ./mysys.sh -unset-proxy
#
#   WARNING! Script should not be started as user 'root' (with command
#   like: sudo ./<script_name>)! Script will ask for such privileges,
#   if necessary.
#
#   Created:  Gusev Dmitry, 19.04.2017
#   Modified:
# ===================================================================

# todo: implement this function

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after proxy setup
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
