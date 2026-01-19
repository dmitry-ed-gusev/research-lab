#!/usr/bin/env bash

####################################################################################################
#
#   Script for setup development environment on the machine. The script installs:
#       - (+) (linux) go language (https://go.dev/)
#       - (-) (linux) asdf tool
#       - (-) (linux) pyenv tool
#       - (-) (win)   pyenv tool
#       - (-) (linux) nvm tool
#       - (-) (win)   nvm tool
#       - (-) python (?)
#       - (-) java (?)
#       - (-) maven (?)
#       - (-) gradle (?)
#
#   Created:  Dmitrii Gusev, 31.05.2025
#   Modified: Dmitrii Gusev, 12.11.2025
#
####################################################################################################

# -- safe bash scripting + encoding
set -euf -o pipefail
export LANG='en_US.UTF-8'

# -- import helper scripts in one point
. ./shell_library/___base_toolset.sh
source ___base_versions.sh

# -- important constants - [Go language]
GO_VERSION="1.24.3"
GO_DOWNLOAD_LINK="https://go.dev/dl/go${GO_VERSION}.linux-amd64.tar.gz"
GO_LOCAL_FILE_NAME="go_${GO_VERSION}.tar.gz"

# -- important constants - [asdf tool] - various tools versions management
ASDF_INSTALL_ADDRESS=""

# -- important constants - [pyenv tool] - python versions management
PYENV_HOME="~/.pyenv"
PYENV_INSTALL_ADDRESS=""

# -- important constants - [sdkman tool] - various SDKs versions management
SDKMAN_HOME="~/.sdkman"
SDKMAN_INSTALL_CMD='curl -s "https://get.sdkman.io" | bash'

# -- important constants - [nvm tool] - Node Versions Management tool
# TBD

# - Greeting info message
clear; print_title "Starting Setup for Developer"; sleep 2;

download_all_distros() { # ver. 1.0.0, 14.06.2025

    true;

}

install_go() { # ver. 2.0.0, 08.06.2025
    #
    # Install Go language (version is specified by the environment variable)
    #

    print_title "Installing Go (for the current user), ver. ${GO_VERSION}."
    # - download go into the cache folder with overriding existing file
    print_info "Downloading Go archive."
    wget --output-document "${SHELL_CACHE}/${GO_LOCAL_FILE_NAME}" --verbose \
        "${GO_DOWNLOAD_LINK}"
    # - remove existing go installation
    print_info "Removing directory with the previous Go installation."
    sudo rm -rf "${SHELL_USR_LOCAL_PREFIX}/go" --verbose
    # - unpack go into the target folder
    print_info "Unpacking the newly downloaded Go archive."
    sudo tar -C "${SHELL_USR_LOCAL_PREFIX}" -xzf "${SHELL_CACHE}/${GO_LOCAL_FILE_NAME}" --verbose
    # - adding go executable to PATH variable at the shell startup
    print_info "Adding Go to PATH variable in file ${SHELL_PROFILE_FILE}."
    if ! grep -q "${SHELL_USR_LOCAL_PREFIX}/go/bin" "${SHELL_PROFILE_FILE}"; then
        print_msg "Go was not added yet to the PATH variable - performing."
        # - adding Go to PATH in ~/.bash_profile (block add)
        {
            printf "\n# Adding Go to the PATH\n"
            # shellcheck disable=SC2016
            printf "export PATH=\$PATH:%s/go/bin\n" "${SHELL_USR_LOCAL_PREFIX}"
        } >> "${SHELL_PROFILE_FILE}"
    else
        print_msg "Go already added to the PATH variable!"
    fi
    # - printing installed Go version and exit message
    printf "\n%s\n" "$(${SHELL_USR_LOCAL_PREFIX}/go/bin/go version)"
    print_info "Go installation is done.\n"
}

install_nvm_win () { # ver. 1.0.0, 08.06.2025

    true;

}

install_nvm_linux() { # ver. 1.0.0, 08.06.2025

    true;

}

install_asdf() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    true;

}

install_pyenv_linux() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    true;

}

install_pyenv_win() { # ver. 1.0.0, 09.06.2025
    #
    #
    #

    true;

}

install_sdkman() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    print_title "Installing SDKMan Tool (for the current user)."
    # - remove previous installation of SDKMan
    print_info ""

    # -
    print_info "Downloading SDKMan script and executing it."

    }

setup_python() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    true;

}

setup_java_with_sdkman() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    true;

}

setup_maven_with_sdkman() { # ver. 1.0.0, 08.06.2025
    #
    #
    #

    true;

}

setup_gradle_with_sdkman() { # ver. 1.0.0, 08.06.2025

    true;

}

# ==================================================================================================

# install_go

