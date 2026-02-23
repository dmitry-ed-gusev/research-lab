#!/usr/bin/env bash
###################################################################################################
#
#   Clone git repository by provided URL. Repo URL should be provided as cmd line parameter.
#
#   This script works under following environments:
#       - MacOS, 10.14+
#       - Windows GitBash/MinGW
#       - TBD -> linux debian
#       - TBD -> linux red hat
#
#   Warning: script must be used (run) from shell, not from the virtual environment (pipenv shell).
#
#   Created:  Dmitrii Gusev, 30.01.2022
#   Modified: Dmitrii Gusev, 21.05.2023
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'

# -- check repo URL provided

# -- starting script
clear
printf "Python Development Environment setup is starting...\n\n"

# -- setup some commands aliases, depending on the machine type
unameOut="$(uname -s)" # get machine name (short)
# - based on the machine type - setup aliases
case "${unameOut}" in
    Linux*)     MACHINE=Linux; CMD_PYTHON=python3; CMD_PIP=pip3;;
    Darwin*)    MACHINE=Mac; CMD_PYTHON=python3; CMD_PIP=pip3;;
    CYGWIN*)    MACHINE=Cygwin; CMD_PYTHON=python; CMD_PIP=pip;;
    MINGW*)     MACHINE=MinGW; CMD_PYTHON=python; CMD_PIP=pip;;
    *)          MACHINE="UNKNOWN:${unameOut}"; printf "Unknown machine: %s" "${MACHINE}"; exit 1
esac
# - just a debug output
printf "Machine type: [%s], using python: [%s], using pip: [%s].\n\n" \
    "${MACHINE}" "${CMD_PYTHON}" "${CMD_PIP}"
