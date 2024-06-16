#!/usr/bin/env bash
###################################################################################################
#
#   Init script for ansible virtual environment.
#
#   Warning: script must be used (run) from shell, not from the virtual environment (pipenv shell).
#
#   Created:  Dmitrii Gusev, 16.06.2024
#   Modified:
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'

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

# -- upgrading pip (just for the case)
printf "\n--- Upgrading PIP (if there are updates) ---\n\n"
# pip --no-cache-dir install --upgrade pip # option I: working but not in a mingw/gitbash
${CMD_PYTHON} -m pip --no-cache-dir install --upgrade pip # option II: works in mingw/gitbash
printf "\n\n ** upgrading pip - done **\n"

# for wsl ubuntu
python -m venv .venv-wsl --prompt .venv-3.10-wsl-ansible
source .venv-wsl/bin/activate
python -m pip install --upgrade pip

sudo apt update
sudo apt upgrade
sudo apt dist-upgrade

sudo apt install libffi-dev libssl-dev -y
pip install pywinrm docker
