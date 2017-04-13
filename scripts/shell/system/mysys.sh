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
#   Modified: Gusev Dmitry, 07.04.2017
# =============================================================================

# todo BUG! if this script called some times from one script - it repeats steps :) UNSET CONTROL VARIABLES!
# -- Call other script for set environment for current process
source _env.sh

# -- if no parameters specified - just show help and exit
if [[ $# -eq 0 ]] ; then
    cat ${USAGE_FILE}
    exit 0
fi

# -- Read cmd line arguments and set behaviour
for arg in "$@"
do
	case "$arg" in
	# - help/usage screen
	-help) cat ${USAGE_FILE}
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
	# - install MySql
	-install-mysql) INSTALL_MYSQL=YES
	          ;;
	esac
done

# -- update system (call external script)
if [ "$UPDATE_SYSTEM" == "YES" ]; then
	echo "Updating the system..."
	UPDATE_SYSTEM=NO
    # -- Execute the calling script in the current script's process, and pulls in variables and functions from the
    # -- current script so they are usable from the calling script. If you use 'exit' in calling script, it will
    # -- exit the current script as well.
    source _system-update.sh
fi

# -- print statistics
if [ "$SHOW_STAT" == "YES" ]; then
    echo "System statistics:"
    SHOW_STAT=NO
    source _stat.sh
fi

# -- install base software packages
if [ "$INSTALL_BASE" == "YES" ]; then
    echo "Installing base software packages..."
    INSTALL_BASE=NO
    source _install-base.sh
fi

# -- install Oracle Java JDK
if [ "$INSTALL_JAVA" == "YES" ]; then
    echo "Installing Oracle JDK, version $JAVA_VERSION."
    INSTALL_JAVA=NO
    source _install-java.sh
fi

# -- install Jenkins
if [ "$INSTALL_JENKINS" == "YES" ]; then
    echo "Installing Jenkins server."
    INSTALL_JENKINS=NO
    exit
fi

# -- install Sonar
if [ "$INSTALL_SONAR" == "YES" ]; then
    echo "Installing Sonar server."
    INSTALL_SONAR=NO
    exit
fi

# -- install Hadoop
if [ "$INSTALL_HADOOP" == "YES" ]; then
    echo "Installing Apache Hadoop."
    INSTALL_HADOOP=NO
    source _install-hadoop.sh
    exit
fi

# -- install Hive
if [ "$INSTALL_HIVE" == "YES" ]; then
    echo "Installing Apache Hive."
    INSTALL_HIVE=NO
    source _install-hive.sh
    exit
fi

# -- install MySql
if [ "$INSTALL_MYSQL" == "YES" ]; then
    echo "Installing MySql DBMS."
    INSTALL_MYSQL=NO
    source _install-mysql.sh
    exit
fi
