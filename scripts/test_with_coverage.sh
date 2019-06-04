#!/usr/bin/env bash

###############################################################################
#
#   Execute unit tests for pyutilities module with generating coverage report.
#   Unit tests for linux/unix OS.
#
#   Created:  Dmitrii Gusev, 17.05.2019
#   Modified: Dmitrii Gusev, 04.06.2019
#
###############################################################################


# create virtual environment and make it relocatable
virtualenv .venv
virtualenv --relocatable .venv

# activate environment
source .venv/bin/activate

# install necessary testing dependencies
pip install nose2 pyyaml mock xlrd cov-core coverage jira prettytable bs4 pyutilities

# setup PYTHONPATH variable (if necessary)
#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

# run unit tests with coverage
python -m nose2 -v -s scripts/pytests --plugin nose2.plugins.junitxml -X --with-coverage --coverage scripts \
    --coverage-report xml --coverage-report html

# deactivate virtual environment (exit)
deactivate
