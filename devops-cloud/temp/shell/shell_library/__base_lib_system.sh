#!/usr/bin/env bash

#############################################################################################################
#
#   Base shell library script: system procedures.
#   Intended to be used together with others library scripts.
#
#   Created:  Dmitrii Gusev, 13.04.2025
#   Modified: Dmitrii Gusev, 12.11.2025
#
#############################################################################################################

# -- safe bash scripting + encoding
set -euf -o pipefail
export LANG='en_US.UTF-8'

# -- import helper scripts in one point
source ./___base_toolset.sh
source ./___base_versions.sh

print_system_stat() { # ver. 1.1.0, 31.05.2025
    #
    # Show system statistics in human-readable format.
    #

    free -m | awk 'NR==2{printf "Memory Usage: %s/%sMB (%.2f%%)\n", $3,$2,$3*100/$2 }' # memory
    df -h | awk '$NF=="/"{printf "Disk Usage: %d/%dGB (%s)\n", $3,$2,$5}' # disk space
    top -bn1 | grep load | awk '{printf "CPU Load: %.2f\n", $(NF-2)}' # CPU load

}

set_executable() { # ver. 1.1.0, 31.05.2025
    #
    # Set executable bit for all found python (*.py) and shell (*.sh) scripts from the current directory
    # recursively down by the files tree.
    #

    # - set +x bit for all python scripts
    find . -name '*.py' -type f -print0 | xargs -0 chmod +x
    # - set +x bit for all shell scripts
    find . -name '*.sh' -type f -print0 | xargs -0 chmod +x

}
