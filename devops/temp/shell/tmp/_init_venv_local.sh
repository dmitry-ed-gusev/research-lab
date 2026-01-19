#!/usr/bin/env bash

###################################################################################################
#
#   Python virtual environment (venv) initialization script for git bash (MinGW). Script does
#   the following:
#       - (off) deactivates the current virtual environment
#       - removes the current virtual environment (if exists - delete folder)
#       - upgrades global pip
#       - upgrades global dependencies: virtualenv, pipenv, pytest, jupyter, jupyterlab,
#                                       notebook, pip, setuptools, build, twine
#       - creates new virtual environment
#       - upgrades pip and install dependencies into created virtual environment (requirements.txt)
#
#   Created:  Dmitrii Gusev, 11.11.2024
#   Modified: Dmitrii Gusev, 12.01.2025
#
###################################################################################################

# -- load bash library
source _bash_lib.sh

print_title "Python Virtual Env initializing..." "clear"

venv_setup ".venv-azim"

print_title "Python Virtual Env initialized."
