#!/bin/bash
#
# =============================================================================
#   This script updates system (Debian-based). After update <autoremove> will 
#   be executed (with apt-get) - for removing unnecessary packages. Script
#   is a part of scripts suite and shouldn't be called by itself. Use
#   <./mysys.sh -update>
#
#   WARNING! If you use proxy server, you have to set up it for APT
#   utility first! Use [./mysys.sh -proxy <proxy>] command.
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#  
#   Created:  Gusev Dmitry, 26.11.2016
#   Modified: Gusev Dmitrii, 16.04.2017
# =============================================================================

# -- Update system quietly. If you remove comments from /dev/null, you won't see any info.
sudo /usr/bin/apt-get -qy update # > /dev/null
sudo /usr/bin/apt-get -qy upgrade # > /dev/null
sudo /usr/bin/apt-get -qy dist-upgrade # > dev/null

# -- Remove unnecessary packages (old kernel/headers for example, after updating)
sudo /usr/bin/apt-get -qy autoremove

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system aftre updating
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi