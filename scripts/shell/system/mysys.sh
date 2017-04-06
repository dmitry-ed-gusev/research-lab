#!/bin/bash
#
# =============================================================================
#   This is a main script of system scripts suite for update/setup Linux
#   system. Developed and tested under Ubuntu Server 16.04 x64 LTS.
#
#   WARNING! Script should not be started as user 'root' (with command like:
#   sudo ./<script_name>)! Script will ask for such priveleges, if necessary.
#
#   Created:  Gusev Dmitry, 26.11.2016
#   Modified: Gusev Dmitry, 27.11.2016
# =============================================================================

# -- Call other script for set environment for current process
source _env.sh

# -- Read cmd line arguments and set behaviour
for arg in "$@"
do
	case "$arg" in
	# - help/usage screen
	-help) cat usage.txt
	          ;;
	# - system update
	-update)  UPDATE_SYSTEM=YES
	          ;;
	# - no reboot after system update
	-no-reboot) REBOOT_AFTER_UPDATE=NO
	          ;;
	# - set on debug mode
	-debug) DEBUG_MODE=true
	          ;;
	# - print system statistics
	-stat) SHOW_STAT=YES
	          ;;
	# - install base software
	-install-base) INSTALL_BASE=YES
	          ;;
	# - install Java (Oracle JDK)
	-install-java) INSTALL_JAVA=YES
	          ;;
	# - install Jenkins server
	-install-jenkins) INSTALL_JENKINS=YES
	          ;;
	# - install Sonar server
	-install-sonar) INSTALL_SONAR=YES
	          ;;
	# - install Hadoop
	-install-hadoop) INSTALL_HADOOP=YES
	          ;;
	# - install Hive
	-install-hive) INSTALL_HIVE=YES
	          ;;
	esac
done

# -- update system (call external script)
if [ "$UPDATE_SYSTEM" == "YES" ]; then
	echo "Updating the system..."
    # -- Execute the calling script in the current script's process, and pulls in variables and functions from the
    # -- current script so they are usable from the calling script. If you use 'exit' in calling script, it will
    # -- exit the current script as well.
    source _system-update.sh
fi

# -- print statistics
if [ "$SHOW_STAT" == "YES" ]; then
    echo "System statistics:"
    source _stat.sh
fi

# -- install base software packages
if [ "$INSTALL_BASE" == "YES" ]; then
    echo "Installing base software packages..."
    source _install-base.sh
fi

# -- install Oracle Java JDK
if [ "$INSTALL_JAVA" == "YES" ]; then
    echo "Installing Oracle JDK, version $JAVA_VERSION."
    source _install-java.sh
fi

# -- install Jenkins
if [ "$INSTALL_JENKINS" == "YES" ]; then
    echo "Installing Jenkins server."
    exit
fi

# -- install Sonar
if [ "$INSTALL_SONAR" == "YES" ]; then
    echo "Installing Sonar server."
    exit
fi

# -- install Hadoop
if [ "$INSTALL_HADOOP" == "YES" ]; then
    echo "Installing Apache Hadoop."
    exit
fi

# -- install Hive
if [ "$INSTALL_HIVE" == "YES" ]; then
    echo "Installing Hive."
    exit
fi