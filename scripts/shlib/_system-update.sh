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
#   Modified: Gusev Dmitrii, 06.11.2017
# =============================================================================

# -- Update system quietly. If you remove comments from /dev/null, you won't see any info.
sudo /usr/bin/apt -qy update # > /dev/null
sudo /usr/bin/apt -qy upgrade # > /dev/null
sudo /usr/bin/apt -qy dist-upgrade # > dev/null
# -- Remove unnecessary packages (old kernel/headers for example, after updating)
sudo /usr/bin/apt -qy autoremove
