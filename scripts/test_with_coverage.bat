@echo off

rem ###############################################################################
rem #
rem #   Execute unit tests for [scripts] module with generating coverage report.
rem #   Unit tests for Windows OS.
rem #
rem #   Created:  Dmitrii Gusev, 04.06.2019
rem #   Modified: Dmitrii Gusev, 18.06.2019
rem #
rem ###############################################################################


rem -- install dependencies
pip install -r requirements.txt

rem -- create virtual environment
virtualenv .venv

rem -- make virtual environment relocatable (todo: ??? do we need relocatable env ???)
rem virtualenv --relocatable .venv

rem -- activate environment
call .venv\Scripts\activate.bat

rem -- install necessary testing dependencies
call pip install nose2 pyyaml mock xlrd jira pyutilities

rem -- setup PYTHONPATH variable (if necessary)
rem #PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
rem #PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
rem #export PYTHONPATH

rem -- run unit tests with coverage
call python -m nose2 -v -s scripts/pytests --plugin nose2.plugins.junitxml -X --with-coverage --coverage scripts ^
    --coverage-report xml --coverage-report html

rem -- deactivate virtual environment (exit)
call deactivate.bat
