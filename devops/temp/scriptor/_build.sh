#!/usr/bin/env bash

# cSpell: disable

###################################################################################################
#
#   Build script for the SAM :: Scriptor Automation Module.
#
#   Created:  Dmitrii Gusev, 22.11.2024
#   Modified:
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

# TODO: build the module, run the tests, etc...

# TODO: implement tests and test execution with coverage

# install necessary requirements
pip3 install -r requirements.txt

# create virtual environment and make it relocatable
virtualenv .venv
virtualenv --relocatable .venv

# activate environment
source .venv/bin/activate

# install necessary testing dependencies
pip3 install nose2 pyyaml mock xlrd

# setup PYTHONPATH variable (if necessary)
#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

# run unit tests with coverage
python3 -m nose2 -v -s pyutilities/tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pyutilities \
    --coverage-report xml --coverage-report html

# deactivate virtual environment (exit)
deactivate
