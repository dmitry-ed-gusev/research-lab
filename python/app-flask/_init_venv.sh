#!/usr/bin/env bash

# cSpell: disable

###################################################################################################
#
#   Python virtual environment (venv) initialization script for git bash (MinGW). Script does
#   the following:
#       - (off) deactivate the current virtual environment
#       - removes the current virtual environment (if exists - delete folder)
#       - upgrade global pip
#       - upgrade global dependencies: virtualenv, pipenv, pytest, jupyter, jupyterlab,
#                                       notebook, pip, setuptools build twine
#       - create new virtual environment
#       - upgrade pip and install dependencies into virtual environment (requirements.txt)
#
#   Created:  Dmitrii Gusev, 11.11.2024
#   Modified: Dmitrii Gusev, 25.11.2024
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'
export VENV_FOLDER='.venv'
export VENV_NAME='.venv-flask-app'
export REQUIREMENTS_FILE='requirements.txt'
clear

# -- deactivate the current virtual environment
# printf "\n-- Deactivating virtual environment --\n"
# suppressed error output to /dev/null
# deactivate 2> /dev/null || printf "\tNo active virtual environment!\n"
# deactivate
# printf "\tDone.\n"

# -- remove existing virtual environment
printf "\n-- Removing existing virtual environment --\n"
rm -rf ${VENV_FOLDER} || printf "\tNo virtual environment to remove!\n"
printf "\tDone.\n"

# -- update pip (if necessary)
printf "\n-- Upgrading pip and other core dependencies --\n"
python -m pip install --upgrade --no-cache-dir --verbose pip
pip install --upgrade --no-cache-dir --verbose virtualenv pipenv pytest jupyter jupyterlab notebook \
    pip setuptools build twine
printf "\tDone.\n"

# -- create new virtual environment
printf "\n-- Creating new virtual environment + activating --\n"
python -m venv "${VENV_FOLDER}" --prompt "${VENV_NAME}"
# shellcheck disable=SC1091
source "${VENV_FOLDER}"/Scripts/activate
printf "\tDone.\n"

# -- installing dependencies in a virtual environment
printf "\n-- Upgrade pip + Install dependencies into virtual env --\n"
# - pip upgrade
python -m pip install --upgrade --no-cache-dir --verbose pip
printf "\tPIP module upgraded.\n"
sleep 2
# - install all dependencies
pip install --no-cache-dir --verbose -r ${REQUIREMENTS_FILE}
printf "\tRequirements installed.\n"
printf "\tDone.\n"
sleep 2

# -- show outdated dependencies in the virtual environment
printf "\n-- Outdated dependencies list --\n"
pip list --outdated
printf "\tDone.\n"
sleep 3
