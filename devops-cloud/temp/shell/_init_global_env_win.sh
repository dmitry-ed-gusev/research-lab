#!/usr/bin/env bash

###################################################################################################
#
#   Python global environment initialization script for GitBash/MinGW/Linux/Mac. Script does the
#   following things (steps):
#
#
#   Created:  Dmitrii Gusev, 15.10.2025
#   Modified: Dmitrii Gusev, 17.10.2025
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail
# -- default encoding for scripts and utilities
export LANG='en_US.UTF-8'

# -- get current date and time
# _CURRENT_DATE=$(date +"%d-%m-%Y") || { printf "\nError while calculating system date!\n"; sleep 3; exit 1; }
# export _CURRENT_DATE
# _CURRENT_TIME=$(date +"%H:%M:%S") || { printf "\nError while calculating system time!\n"; sleep 3; exit 1; }
# export _CURRENT_TIME

source ./shell_library/__base_lib_system.sh

# #export _POWER_SHELL="powershell" # Power Shell 5
# export _POWER_SHELL="pwsh" # Power Shell 7


# export _POWER_SHELL_PYENV_INSTALL_CMD="Invoke-WebRequest -UseBasicParsing -Uri \"https://raw.githubusercontent.com/pyenv-win/pyenv-win/master/pyenv-win/install-pyenv-win.ps1\" -OutFile \"./install-pyenv-win.ps1\"; &\"./install-pyenv-win.ps1\""
# #export _POWER_SHELL_PYENV_UPDATE_CMD="&\"${env:PYENV_HOME}\install-pyenv-win.ps1\""
# export _POWER_SHELL_PYENV_UPDATE_CMD="&\"${PYENV_HOME}\install-pyenv-win.ps1\""

# # TODO: use array of python versions to install with pyenv - ???

# # -- Step I. Environment check + debug output
# # -- Step II.

# # -- Step III. Check pyenv presence + print version - install if not present
# pyenv --version || { "${_POWER_SHELL}" -Command "${_POWER_SHELL_PYENV_INSTALL_CMD}"; pyenv --version ; }
# sleep 3

# # -- Step IV. Update pyenv (via Power Shell)
# "${_POWER_SHELL}" -Command "${_POWER_SHELL_PYENV_UPDATE_CMD}"
# #"${_POWER_SHELL}" -Command "&\"${PYENV_HOME}\install-pyenv-win.ps1\""
# sleep 3

# # -- Step V. Install version(s) of python
# # -- Step VI. Setup/choose global python
# # -- Step VII. Install pipx with pip for installed python versions
# # -- Step VIII. Install poetry with pipx for installed python versions
# # -- Step IX. ???
# # -- Step X. ???

echo "hello..."
