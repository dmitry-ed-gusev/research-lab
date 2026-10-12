#!/usr/bin/env bash

###################################################################################################
#
#   Vagrant usage script - various commands etc.
#
#   Created:  Dmitrii Gusev, 31.08.2026
#   Modified: Dmitrii Gusev, 31.08.2026
#
###################################################################################################

# -- safe bash scripting / proper locale / delay between steps
set -euf -o pipefail
export LANG='en_US.UTF-8'
export _STEP_SLEEP=3

# --
printf ""
source ./__vagrant_add_boxes.sh

# - vagrant boxes in Russia
# https://vagrant.elab.pro
# download link example: https://vagrant.elab.pro/centos/8/1.0.0/virtualbox
# https://habr.com/ru/articles/735700/

# # - vagrant add boxes from https://vagrant.elab.pro
# vagrant box add elabpro/redos-7
# # - vagrant - add boxes from files
# vagrant box add --name --provider virtualbox/libvirt --box-version 1.0.0 <box-file>

# -- useful defaults
# export _BOXES_DIR="/mnt/CA60846660845AD9/vagrant_boxes"
# export _FEDORA_VBOX_NAME="fedora-vg-vbox"
# export _FEDORA_VBOX="${_BOXES_DIR}/Fedora-Cloud-Base-Vagrant-VirtualBox-43-1.6.x86_64.vagrant.virtualbox.box"
# export _FEDORA_LVIRT_NAME="fedora-vg-libvirt"
# export _FEDORA_LVIRT="${_BOXES_DIR}/Fedora-Cloud-Base-Vagrant-libvirt-43-1.6.x86_64.vagrant.libvirt.box"

# # - add boxes to vagrant (previously downloaded)
# printf "\n= INFO: Adding vagrant boxes\n"
# vagrant box add "${_FEDORA_LVIRT_NAME}" "${_FEDORA_LVIRT}" || { printf "\n= WARN: Error or box exists!\n"; }
# vagrant box add "${_FEDORA_VBOX_NAME}" "${_FEDORA_VBOX}" || { printf "\n= WARN: Error or box exists!\n"; }
# printf "\n\n= INFO: Vagrant boxes list\n"
# vagrant box list
# sleep "${_STEP_SLEEP}"

# -- after OS kernel upgrade VBox needs to rebuild it's modules with new kernel-dev modules. If we need
# -- to install new dev modules, this command helps with the modules names (for installation).
printf "\n= INFO: Status of the Virtual Box modules\n"
sudo /sbin/rcvboxdrv status
sleep "${_STEP_SLEEP}"

# -- after building modules, we need to run
printf "\n= INFO: setting up the VBox modules\n"
sudo /sbin/rcvboxdrv setup
sleep "${_STEP_SLEEP}"

# -- show vagrant global status/environments
printf "\n= INFO: Vagrant global status\n"
vagrant global-status
sleep "${_STEP_SLEEP}"

# --
# vagrant init "${_FEDORA_VBOX_NAME}"

# -- end of the script
printf "\n"
