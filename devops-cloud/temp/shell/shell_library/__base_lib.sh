#!/usr/bin/env bash

####################################################################################################
#
#   This script joins all other library scripts in one pack. To use the scripts library it is
#   necessary to 'source' (import) this one script - and you get access to all procedures/functions.
#
#   Created:  Dmitrii Gusev, 31.05.2025
#   Modified: Dmitrii Gusev, 21.10.2025
#
####################################################################################################

# -- safe bash scripting + encoding
set -euf -o pipefail
export LANG='en_US.UTF-8'

# -- some useful constants for the scripts suite
export SHELL_CACHE="./.cache"
export SHELL_PROFILE_FILE="${HOME}/.bash_profile"
export SHELL_USR_LOCAL_PREFIX="/usr/local"
export SHELL_USR_LOCAL_BIN_PREFIX="${SHELL_USR_LOCAL_PREFIX}/bin"

# -- import all other library scripts in one point
. "$(pwd)"//__base_lib_devtools.sh
# source ./shell_library/__base_lib_python.sh
# source ./shell_library/__base_lib_system.sh
