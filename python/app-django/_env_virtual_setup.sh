#!/usr/bin/env bash
###############################################################################
#
#   Development environment setup script. Script can be used to re-create
#   development environment fro 'scratch'.
#
#   Warning: script must be used (run) from shell, not from the virtual
#            environment (pipenv shell).
#
#   See additional interesting resources:
#     - https://stackoverflow.com/questions/3466166/how-to-check-if-running-in-cygwin-mac-or-linux
#     - https://remarkablemark.org/blog/2020/10/31/bash-check-mac/
#     - https://pypi.org/project/mysqlclient/
#
#   Created:  Dmitrii Gusev, 21.07.2022
#   Modified: Dmitrii Gusev, 22.08.2022
#
###############################################################################

# -- verbose output mode
VERBOSE="--verbose"

# -- set up encoding/language
export LANG='en_US.UTF-8'
export SHELL_PROFILE="${HOME}/.bash_profile"
# export SHELL_PROFILE="${HOME}/.zshrc"  # user profile for the zsh

# -- build directories
BUILD_DIR='build/'
DIST_DIR='dist/'

clear
printf "Development Virtual Environment setup is starting...\n\n"

# -- upgrade pip
printf "\nUpgrading pip.\n"
pip --no-cache-dir install --upgrade pip

# -- upgrading pipenv (just for the case)
printf "\nUpgrading pipenv.\n"
pip --no-cache-dir install --upgrade pipenv

# -- remove existing virtual environment, clear caches
printf "\nDeleting virtual environment and clearing caches.\n"
pipenv --rm ${VERBOSE}
pipenv --clear ${VERBOSE}

# -- clean build and distribution folders
printf "\nClearing temporary directories.\n"
printf "\nDeleting [%s]...\n" ${BUILD_DIR}
rm -r ${BUILD_DIR}
printf "\nDeleting [%s]...\n" ${BUILD_DIR}
rm -r ${DIST_DIR}

# -- removing Pipfile.lock (re-generate it)
printf "\nRemoving Pipfile.lock\n"
rm Pipfile.lock

# -- as we need mysql client - we need to install additional libraries
# todo: see additional resources above and implement section for linux
if [[ $OSTYPE == 'darwin'* ]]; then
    printf "\nWe're running on MacOS: checking for additional mysql client libraries...\n"
    printf "Using shell profile: [%s]\n" ${SHELL_PROFILE}

    # check if mysql-client libraries are installed - if not - install them
    if ! brew list mysql-client; then
        printf "\nLibraries for mysql-client are not installed - processing...\n"
        brew install mysql-client "${VERBOSE}"
        echo 'export PATH="/usr/local/opt/mysql-client/bin:$PATH"' >> "${SHELL_PROFILE}"
        export PATH="/usr/local/opt/mysql-client/bin:$PATH"
        # pip install mysqlclient  # <- python mysqlclient will be installed via pipenv
        # todo: somehow need to reboot the shell itself - otherwise it won't work...
    else
        printf "\nmysql-client libraries should be already installed!\n"
    fi

fi

# -- install all dependencies, incl. development
printf "\nInstalling dependencies, updating all + outdated.\n"
pipenv install --dev ${VERBOSE}

# - check for vulnerabilities and show dependencies graph
printf "\nChecking virtual environment for vulnerabilities.\n"
pipenv check
pipenv graph

# - outdated packages report
printf "\n\nOutdated packages list (pip list):\n"
pipenv run pip list --outdated
