#!/bin/bash
#
# =============================================================================
#   This script updates system (Debian-based). After update <autoremove> will 
#   be executed (with apt-get) - for removing unnecessary packages.
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such priveleges, if necessary.
#  
#   Created:  Gusev Dmitry, 27.10.2015
#   Modified: Gusev Dmitry, 11.03.2016
# =============================================================================

# -- Update system quietly . If you uncomments /dev/null, you don't see any info.
sudo /usr/bin/apt-get -qy update # > /dev/null
sudo /usr/bin/apt-get -qy upgrade # > /dev/null
sudo /usr/bin/apt-get -qy dist-upgrade # > dev/null

# -- Remove unnecessary packages (old kernel/headers for example, after updating)
sudo /usr/bin/apt-get -qy autoremove

# -- Reboot system aftre updating
sudo reboot now