@echo off

rem ###############################################################################
rem #
rem #   Execute unit tests for pyutilities module with generating coverage report.
rem #   Unit tests for Windows OS.
rem #
rem #   Created:  Dmitrii Gusev, 17.05.2019
rem #   Modified: Dmitrii Gusev, 03.06.2019
rem #
rem ###############################################################################


rem create virtual environment and make it relocatable
virtualenv .venv
virtualenv --relocatable .venv

# activate environment
source .venv/bin/activate

# install necessary testing dependencies
pip install nose2 pyyaml mock xlrd

# setup PYTHONPATH variable (if necessary)
#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

# run unit tests with coverage
python -m nose2 -v -s pyutilities/tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pyutilities \
    --coverage-report xml --coverage-report html

# deactivate virtual environment (exit)
deactivate

rem pip install nose2 cov-core coverage pyyaml mock --proxy webproxy.merck.com:8080
rem pip install nose2 cov-core coverage pyyaml mock
rem set PYTHONPATH=./target/dependency;./pymocks
rem python -m nose2 -v --plugin nose2.plugins.junitxml -X --with-coverage --coverage-report html xml
