#!/usr/bin/env bash

####################################################################################################
#
#   Base toolset shell library script: various useful utility procedures and functions. Intended
#   to be used together with others library scripts as a 'helper' with basic building blocks.
#
#   Warning! Independent file! Should not include any others scripts!
#
#   Created:  Dmitrii Gusev, 31.05.2025
#   Modified: Dmitrii Gusev, 12.11.2025
#
####################################################################################################

# -- safe bash scripting + encoding
set -euf -o pipefail
export LANG='en_US.UTF-8'

# -- default messages for the functions
export _MSG_NO_TITLE_PROVIDED="ERROR: no title provided!"
export _MSG_NO_TEXT_PROVIDED="ERROR: no text provided!"

# -- date and time (for logging purpose)
_CURRENT_DATE=$(date +"%d-%m-%Y") || { printf "\nError while calculating system date!\n"; sleep 3; exit 1; }
export _CURRENT_DATE
_CURRENT_TIME=$(date +"%H:%M:%S") || { printf "\nError while calculating system time!\n"; sleep 3; exit 1; }
export _CURRENT_TIME

print_title() { # ver. 3.1.0, 08.06.2025
    #
    # Prints title for script with date/time, no side actions. Provided message may contain
    # escape characters like \n.
    #
    # $1 - title to print, mandatory parameter

    # check title - fail fast if not provided
    [[ -z ${1-} ]] && { printf "\n%s\n" "${_MSG_NO_TITLE_PROVIDED}"; sleep 5; exit 1; }

    # - print two new lines, next - print the provided title with date and time
    printf "\n\n=== %s %s - %b ===\n" "${_CURRENT_DATE}" "${_CURRENT_TIME}" "${1}"
}

print_info() { # ver. 1.1.0, 08.06.2025
    #
    # Prints INFO message with the provided text, no side actions. Provided message may contain
    # escape characters like \n.
    #
    # $1 - text to print, mandatory parameter

    # check text - fail fast if not provided
    [[ -z ${1-} ]] && { printf "\n%s\n" "${_MSG_NO_TEXT_PROVIDED}"; sleep 5; exit 1; }

    # - print two new lines, next - print the provided title with date and time
    printf "\n=== [+] INFO - %b\n" "${1}"

}

print_msg() { # ver. 1.0.0, 03.06.2025
    #
    # Prints the simple message with the provided text
    #
    # $1 - text to print, mandatory parameter

    # check text - fail fast if not provided
    [[ -z ${1-} ]] && { printf "\n%s\n" "${_MSG_NO_TEXT_PROVIDED}"; sleep 5; exit 1; }

    # - print two new lines, next - print the provided title with date and time
    printf "\n=== [+]        %s\n" "${1}"

}
