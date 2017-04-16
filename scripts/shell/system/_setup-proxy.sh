#!/bin/bash
# ===================================================================
#   Script sets up the system proxy server for current user (in
#   ~/.profile file and for APT utility (/etc/apt/apt.conf).
#
#   This script is a part of scripts suite and shouldn't be called
#   by itself. Use ./mysys.sh -proxy <proxy-server-value>
#
#   WARNING! Script should not be started as user 'root' (with command
#   like: sudo ./<script_name>)! Script will ask for such privileges,
#   if necessary.
#
#   Created:  Gusev Dmitry, 16.04.2017
#   Modified:
# ===================================================================

# todo: switch setting up a proxy on/off by cmd line argument

# - Proxy server settings
#PROXY=http://webproxy.merck.com:8080
# export proxy variables for current session (override settings)
export {HTTP,HTTPS}_PROXY=${PROXY}
export {http,https}_proxy=${PROXY}
# - Set up system proxy for current user (put it in ~/.profile file)
# todo: check if proxy is already set there - in this case override (use fedit.py)
# todo: refactor put the proxy to ~/.profile - move it to some function
grep -Fq "export HTTP_PROXY=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export HTTP_PROXY=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export HTTPS_PROXY=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export HTTPS_PROXY=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export http_proxy=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export http_proxy=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export https_proxy=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export https_proxy=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi

# -- Setup proxy server(s) (http/https) for APT utility (if necessary)
grep -Fq "Acquire::http::Proxy" /etc/apt/apt.conf
if [ $? -ne 0 ]; then
    echo "echo 'Acquire::http::Proxy \"${PROXY}\";' >> /etc/apt/apt.conf" | sudo sh
fi
grep -Fq "Acquire::https::Proxy" /etc/apt/apt.conf
if [ $? -ne 0 ]; then
    echo "echo 'Acquire::https::Proxy \"${PROXY}\";' >> /etc/apt/apt.conf" | sudo sh
fi

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after proxy setup
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
