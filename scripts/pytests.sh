#!/bin/bash
#
# =======================================================================================
#
#   Shell script for building [autoscripts] module with python scripts.
#   Script executes unit tests.
#
#   Created:  Gusev Dmitry, 14.01.2019
#   Modified:
# =======================================================================================

virtualenv .venv
virtualenv --relocatable .venv

source .venv/bin/activate

pip install nose2 cov-core coverage pyyaml

PYTHONPATH=${WORKSPACE}/target/dependency:$PYTHONPATH
PYTHONPATH=${WORKSPACE}/pymocks:$PYTHONPATH
export PYTHONPATH

python -m nose2 -v -s tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pipeline --coverage-report xml
