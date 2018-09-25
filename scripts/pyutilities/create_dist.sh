#!/bin/bash
#
# =======================================================================================
#
#   Create disribution for [pyutilities] library.
#   See more info here: https://packaging.python.org/tutorials/packaging-projects/#
#
#   Created:  Gusev Dmitry, 25.09.2018
#   Modified:
# =======================================================================================

# clean previous versions distributions
rm -rf dist
rm -rf build
rm -rf pyutilities.egg-info

# upgrade versions of setuptools/wheel/twine
pip install --user --upgrade setuptools wheel twine

# create distribution for library in /dist catalog
python setup.py sdist bdist_wheel

# upload new library to Test PyPi
# twine upload --repository-url https://test.pypi.org/legacy/ dist/*

# upload new library dist to PyPi
twine upload dist/*
