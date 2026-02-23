#!/usr/bin/env bash

####################################################################################################
#
#   Base shell library script: contains versions and various parameters for other scripts.
#
#   Warning! Independent file! Should not include any others scripts!
#
#   Created:  Dmitrii Gusev, 21.10.2025
#   Modified: Dmitrii Gusev, 12.11.2025
#
####################################################################################################

# -- safe bash scripting + encoding
set -euf -o pipefail
export LANG='en_US.UTF-8'
