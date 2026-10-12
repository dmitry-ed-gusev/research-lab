#!/bin/bash

###################################################################################################
#
#   Vagrant adding boxes script - setup from scratch.
#
#   Created:  Dmitrii Gusev, 22.09.2026
#   Modified: Dmitrii Gusev, 12.10.2026
#
###################################################################################################

# -- safe bash scripting / proper locale / delay between steps
set -euf -o pipefail
export LANG='en_US.UTF-8'
export _STEP_SLEEP=3

# direct download link example: https://vagrant.elab.pro/centos/8/1.0.0/virtualbox, see more info here:
# Habr article https://habr.com/ru/articles/735700/
# - vagrant add boxes from https://vagrant.elab.pro
# vagrant box add elabpro/redos-7
# - vagrant - add boxes from files
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

# - online vagrant boxes in Russia - setup env variable to the boxes source
export VAGRANT_SERVER_URL=https://vagrant.elab.pro
# - offline boxes folder (local)
export OFFLINE_BOXES_DIR="/mnt/CA60846660845AD9/vagrant_boxes"
ONLINE_BOXES_RU=(
    "archlinux/archlinux"
    "bento/ubuntu-24.04 --provider libvirt"
    "bento/ubuntu-24.04 --provider virtualbox"
    "debian/bullseye64"
    "elabpro/redos-7"
    "generic/rhel9"
    "ubuntu/22.04"
)
OFFLINE_BOXES=(
    "--name local/bento-ubuntu-24.04 --provider libvirt \
        ${OFFLINE_BOXES_DIR}/bento-ubuntu-24.04-libvirt.box"
    "--name local/bento-ubuntu-24.04 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/bento-ubuntu-24.04-virtualbox.box"
    "--name local/bento-ubuntu-26.04 --provider libvirt \
        ${OFFLINE_BOXES_DIR}/bento-ubuntu-26.04-libvirt.box"
    "--name local/bento-ubuntu-26.04 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/bento-ubuntu-26.04-virtualbox.box"
    "--name local/centos-7 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/centos-7-virtualbox.box"
    "--name local/centos-8 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/centos-8-virtualbox.box"
    "--name local/debian-10 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/debian-10-buster64-virtualbox.box"
    "--name local/debian-11 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/debian-11-bullseye64-virtualbox.box"
    "--name local/fedora-43 --provider libvirt \
        ${OFFLINE_BOXES_DIR}/Fedora-Cloud-Base-Vagrant-libvirt-43-1.6.x86_64.vagrant.libvirt.box"
    "--name local/fedora-43 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/Fedora-Cloud-Base-Vagrant-VirtualBox-43-1.6.x86_64.vagrant.virtualbox.box"
    "--name local/redos-7 --provider virtualbox \
        ${OFFLINE_BOXES_DIR}/redos-7-virtualbox.box"
)

# -- merged array of all vagrant boxes (online + offline)
BOXES=("${ONLINE_BOXES_RU[@]}" "${OFFLINE_BOXES[@]}")

# Префикс (для реальной работы замените на "vagrant ssh" или "vagrant up")
# Мы используем встроенную функцию, чтобы сымитировать поведение утилиты vagrant
# vagrant_mock() {
#     # Если в аргументах есть слово "invalid", имитируем ошибку (код 1)
#     if [[ "$*" == *"invalid"* ]]; then
#         return 1
#     fi
#     # Иначе имитируем успешное выполнение (код 0)
#     return 0
# }
# prefix="vagrant_mock"

# -- vagrant cmd prefix
prefix="vagrant box add"
# -- init counters
success_count=0
failure_count=0
# -- out the info header
printf "\n= [INFO] Executing Vagrant boxes adding.\n"

# -- temporary turn off errors control (immediate stop)
set +e
set +o pipefail

# -- starting the cmds processing cycle
for item in "${BOXES[@]}"; do

    # - start the cmd execution
    printf "\n= [INFO] Executing: %S %s\n" "${prefix}" "${item}"
    sleep "${_STEP_SLEEP}"
    # - split stings of prefix/params by the spaces into array
    read -r -a cmd_args <<< "$prefix $item"

    # - executing cmd, saving execution status (resulting code) with a PIPESTATUS
    "${cmd_args[@]}"
    status=${PIPESTATUS[0]}
    printf "\n= [INFO] Cmd %s executed. Resulting status: %s\n" "${cmd_args[*]}" "${status}"
    # - processing the cmd execution status
    if [ "${status}" -eq 0 ]; then  # status=0 - execution OK
        printf "\n= [SUCCESS] Command executed OK.\n"
        ((success_count++))
    else  # status != 0 - execution ERROR
        printf "\n= [ERROR] Execution ERROR (code: %s): %s %s\n" >&2 "${status}" "${prefix}" "${status}"
        ((failure_count++))
    fi

done # end of FOR cycle

# -- turn on (back) the strict error control mode
set -e
set -o pipefail

# Финальный отчет
printf "\n\n"
printf "\n============================================================"
printf "\n===                   Execution report                   ==="
printf "\n============================================================"
printf "\n                                                            "
printf "\n   Total cmds executed:                               %s    " "${#BOXES[@]}"
printf "\n   Success:                                           %s    " "$success_count"
printf "\n   Failures:                                          %s    " "$failure_count"
printf "\n                                                            "
printf "\n============================================================\n\n"

# -- the script return code = 1, in case of any error (good automation practice)
[ $failure_count -eq 0 ] || exit 1
