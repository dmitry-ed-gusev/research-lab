#!/usr/bin/env bash

# cSpell: disable

###################################################################################################
#
#   Python virtual environment (venv) initialization script for git bash (MinGW). Script does
#   the following:
#       - deactivate the current virtual environment
#       - removes the current virtual environment (if exists)
#
#   Created:  Dmitrii Gusev, 11.11.2024
#   Modified: Dmitrii Gusev, 22.11.2024
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'
export VENV_NAME='.venv-scriptor'
export IPYKERNEL_NAME='scriptor-ipykernel'
export VENV='.venv/'
clear

# -- deactivate the current virtual environment
printf "\n-- Deactivating virtual environment --\n"
# suppressed error output to /dev/null
deactivate 2> /dev/null || printf "\tNo active virtual environment!\n"
printf "\tDone.\n"

# -- remove existing virtual environment
printf "\n-- Removing existing virtual environment --\n"
rm -rf .venv || printf "\tNo virtual environment to remove!\n"
printf "\tDone.\n"

# -- update pip (if necessary)
printf "\n-- Upgrading pip and other core dependencies --\n"
python -m pip install --upgrade --no-cache-dir --verbose pip
pip install --upgrade --no-cache-dir --verbose virtualenv pipenv pytest jupyter jupyterlab notebook \
    pip setuptools build twine
printf "\tDone.\n"

# -- create new virtual environment
printf "\n-- Creating new virtual environment + activating --\n"
python -m venv .venv --prompt "${VENV_NAME}"
source .venv/Scripts/activate
printf "\tDone.\n"

# -- installing dependencies in a virtual environment
printf "\n-- Upgrade pip + Install SAM library (editable) + Install dependencies into virtual env --\n"
# - pip upgrade
python -m pip install --upgrade --no-cache-dir --verbose pip
printf "\tPIP module upgraded.\n"
sleep 2
# - install library as 'editable' for development
pip install --no-cache-dir --verbose --editable .
printf "\tSAM library installed for development (editable).\n"
sleep 2
# - install all dependencies
pip install --no-cache-dir --verbose -r requirements.txt
printf "\tRequirements installed.\n"
printf "\tDone.\n"
sleep 2

# -- install local ipykernel into the virtual environment
printf "\n-- Installing local ipykernel + check --\n"
ipython kernel install --user --name="${IPYKERNEL_NAME}"
# -- list installed ipykernels - just a debug output
jupyter kernelspec list
sleep 3
printf "\tDone.\n"

# -- show outdated dependencies in the virtual environment
printf "\n-- Outdated dependencies list --\n"
pip list --outdated
printf "\tDone.\n"
sleep 3
