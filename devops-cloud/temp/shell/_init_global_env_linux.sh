#!/usr/bin/env bash

###################################################################################################
#
#   Python global environment initialization script for Linux. Script does the following things
#   (steps):
#
#   Created:  Dmitrii Gusev, 15.10.2025
#   Modified: Dmitrii Gusev, 12.11.2025
#
###################################################################################################

# -- safe bash scripting
set -euf -o pipefail
# -- default encoding for scripts and utilities
export LANG='en_US.UTF-8'

. ./shell_library/__base_lib.sh

clear; print_title "Setting up Linux Development Environment..."; sleep 2;
