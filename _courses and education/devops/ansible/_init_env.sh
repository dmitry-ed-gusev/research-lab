#!/usr/bin/env bash
###################################################################################################
#
#   Init script for ansible virtual environment.
#
#   Warning: script must be used (run) from shell, not from the any virtual environment!
#
#   Created:  Dmitrii Gusev, 16.06.2024
#   Modified: Dmitrii Gusev, 11.07.2024
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'

# -- some useful variables
VENV_NAME=".venv-ansible"
VENV_PROMPT=".venv-ansible"
REQUIREMENTS_FILE="requirements.txt"
MSG_NO_PYTHON="\nWARNING: no installed python/python3 in the system!\n"
MSG_NO_PIP="\nWARNING: no installed pip/pip3 in the system!\n"

clear
printf "Python Development Environment setup is starting...\n\n"

# -- PRE-CHECK I. Setup some commands aliases, depending on the machine type
# - get machine name (short)
unameOut="$(uname -s)"
# - based on the machine type - setup aliases
case "${unameOut}" in
    Linux*)     MACHINE=Linux; CMD_PYTHON=python3; CMD_PIP=pip3;;
    Darwin*)    MACHINE=Mac; CMD_PYTHON=python3; CMD_PIP=pip3;;
    CYGWIN*)    MACHINE=Cygwin; CMD_PYTHON=python; CMD_PIP=pip;;
    MINGW*)     MACHINE=MinGW; CMD_PYTHON=python; CMD_PIP=pip;;
    *)          MACHINE="UNKNOWN:${unameOut}"; printf "Unknown machine: %s" "${MACHINE}"; exit 1
esac
# - get OS name from system files
OS_NAME=$(grep "PRETTY_NAME=" $"/etc/os-release" | awk -F= '{print $2}')

# - just a debug output
printf "\tOS: [%s], OS type: [%s], using python: [%s], using pip: [%s]\n\n" \
    "${OS_NAME}" "${MACHINE}" "${CMD_PYTHON}" "${CMD_PIP}"

# -- PRE-CHECK II. Python presence on the machine.
printf "\tVersions of python3 / pip3:\n"
printf "\t\t%s\n" "$(${CMD_PYTHON} --version)" || { printf "%s" "${MSG_NO_PYTHON}" ; sleep 5 ; exit ; }
printf "\t\t%s\n" "$(${CMD_PIP} --version)" || { printf "%s" "${MSG_NO_PIP}" ; sleep 5 ; exit ; }
sleep 4

# -- Create virtual environment and activate it
printf "\n--- Creating virtual environment ---\n\n"
# - if dir with virtual environment exists - skip it, otherwise - create
if [ -d "${VENV_NAME}" ]; then
    printf "\nVirtual environment already exists: %s\n" "${VENV_NAME}"
else
    printf "\nCreating virtual environment: %s\n" "${VENV_NAME}"
    ${CMD_PYTHON} -m venv ${VENV_NAME} --prompt ${VENV_PROMPT}
fi
# - activating virtual environment
printf "\nActivating virtual environment\n"
# shellcheck source=/dev/null
# (details see here: https://www.shellcheck.net/wiki/SC1091)
source ${VENV_NAME}/bin/activate || { printf "Can't activate virtual environment!" ; exit ; }
printf "\n ** creating environment - done **\n\n"
sleep 2

# -- Install/re-install all dependencies to the virtual environment
printf "\n--- Installing dependencies in the virtual environment ---\n\n"
python -m pip --no-cache-dir install --upgrade pip
printf "\n ** upgrading pip - done **\n\n"
pip install --upgrade --force-reinstall --no-cache-dir -r ${REQUIREMENTS_FILE}
printf "\n ** installation - done **\n\n"
sleep 2

# -- Deactivating virtual environment
deactivate
printf "\n ** virtual environment deactivated **\n\n"
sleep 5
