#!/usr/bin/env bash

###############################################################################
#
#   Execute unit tests for pyutilities module with generating coverage report.
#
#   Created: Dmitrii GUsev, 17.05.2019
#   Modified:
#
###############################################################################

# create virtual environment and make it relocatable
virtualenv .venv
virtualenv --relocatable .venv

# activate environment
source .venv/bin/activate

# install necessary testing dependencies
pip install nose2 pyyaml mock xlrd

#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

python -m nose2 -v -s pyutilities/tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pyutilities \
    --coverage-report xml --coverage-report html

# deactivate virtual environment (exit)
deactivate

#python -m nose2 -v -s tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pipeline --coverage-report xml
#pip install robotframework
#pip install robotframework-sshlibrary
#robot --dryrun --variablefile ${WORKSPACE}/tests/robot/environments/dev_env.py --variable ROOTDIR:${WORKSPACE}/tests/robot tests/robot/pipeline_tests/atomic