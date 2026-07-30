#!/usr/bin/env bash

###################################################################################################
#
#   Ansible usage script - various commands etc.
#
#   Created:  Dmitrii Gusev, 02.07.2026
#   Modified: Dmitrii Gusev, 02.07.2026
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail

# -- general setup - some variables
export LANG='en_US.UTF-8'

# ansible simple command - ping
ansible atomserver -i inventory/myservers.ini -m ping -k
