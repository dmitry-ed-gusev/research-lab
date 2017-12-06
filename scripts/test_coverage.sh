virtualenv .venv
virtualenv --relocatable .venv

source .venv/bin/activate

pip install nose2 cov-core coverage pyyaml mock
pip install jira prettytable bs4

#PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
#PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
#export PYTHONPATH

python -m nose2 -v --plugin nose2.plugins.junitxml -X --with-coverage --coverage-report xml -vvv

#pip install robotframework
#pip install robotframework-sshlibrary

#robot --dryrun --variablefile ${WORKSPACE}/tests/robot/environments/dev_env.py --variable ROOTDIR:${WORKSPACE}/tests/robot tests/robot/pipeline_tests/atomic