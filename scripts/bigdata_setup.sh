#!/bin/bash
#
# =============================================================================
#   This script setup Ubuntu Server with Java/BigData tools. Script does:
#    - update server to the latest packages
#    - installs base necessary software packages
#    - installs Oracle JDK, Apache Ant, Apache Maven
#    - installs Apache Hadoop system
#
#   WARNING! Script should not be started as user 'root' (with command like:
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#
#   Created:  Gusev Dmitry, 12.04.2017
#   Modified: Gusev Dmitry, 13.04.2017
# =============================================================================

# - update server
mysys.sh -update -debug -no-reboot
# - install base packages
mysys.sh -install-base -debug -no-reboot
# - install JDK/Ant/Maven and reboot server
mysys.sh -install-java -debug -no-reboot
# - install Apache Hadoop
mysys.sh -install-hadoop -debug
