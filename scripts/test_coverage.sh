#!/usr/bin/env bash
virtualenv .venv
virtualenv --relocatable .venv

source .venv/bin/activate

pip install nose2 cov-core coverage pyyaml mock
pip install jira prettytable bs4

#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

python -m nose2 -v -s pytests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pylib \
    --coverage-report xml --coverage-report html xml

#python -m nose2 -v -s tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pipeline --coverage-report xml

#pip install robotframework
#pip install robotframework-sshlibrary

#robot --dryrun --variablefile ${WORKSPACE}/tests/robot/environments/dev_env.py --variable ROOTDIR:${WORKSPACE}/tests/robot tests/robot/pipeline_tests/atomic