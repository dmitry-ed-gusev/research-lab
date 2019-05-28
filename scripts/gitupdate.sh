#!/usr/bin/env bash

###############################################################################
#
#
#   Created:  Dmitrii Gusev, 22.05.2019
#   Modified:
#
###############################################################################


# update MSD-based projects
python3 -m scripts.atlassian.gitupdate

# update my own projects
# python scripts/atlassian/gitupdate.py --pass $1 --javadoc --sources
