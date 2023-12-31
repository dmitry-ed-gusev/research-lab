#!/bin/bash
#
# =============================================================================
#   This is a main script of system scripts suite for update/setup Linux
#   system. Developed and tested under Ubuntu Server 16.04 x64 LTS.
#
#   Tech notes:
#       * order of options processing (in script) is important. Options may
#          be specified in any order, but will be processed in correct order.
#
#   WARNING! Script should not be started as user 'root' (with command like:
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#
#   Created:  Gusev Dmitry, 26.11.2016
#   Modified: Gusev Dmitry, 23.11.2017
# =============================================================================

# todo: implement combining options/exit after each option
# todo: move reboot to this script (from child scripts)
# todo: add idempotency property to scripts (nothing happened if already installed)
#
# todo: cmd for copy scripts catalog (recursively) to remote pc by scp:
# todo: scp -P 2022 -r scripts myuser@localhost:/home/myuser/

# -- Call other script for set environment for current process
source shlib/_env.sh

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
	-no-debug) DEBUG_MODE=false
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
	# - set proxy server value
	-set-proxy)
              shift
              if test $# -gt 0; then
                PROXY=$1
                SET_PROXY=YES
                echo ${PROXY}
              else
                echo "Error: no proxy server value specified!"
                cat ${USAGE_FILE}
                exit 1
              fi
              shift
              ;;
    # - remove proxy (system and for APT)
    -unset-proxy) UNSET_PROXY=YES
              ;;
	esac
done

# #1. SIMPLE OPTION: print statistics. This option is completely independent and can't be combined with others.
if [ "$SHOW_STAT" == "YES" ]; then
    echo "System statistics:"
    SHOW_STAT=NO
    source shlib/_stat.sh
    exit 0
fi

# #2. SIMPLE OPTION: setup system/APT proxy server. This option is independent and may be combined with other
# options, but should be processed before them. Also switch off unset proxy option.
if [ "$SET_PROXY" == "YES" ]; then
    echo "Setup system/APT proxy server [${PROXY}]."
    SET_PROXY=NO
    UNSET_PROXY=NO
    source shlib/_setup-proxy.sh
fi

# #3. SIMPLE OPTION: remove (unset) system and APT utility proxy. This option is independent and may be combined
# with other options, but should be processed before them.
if [ "$UNSET_PROXY" == YES ]; then
    echo "Unset (remove) system/APT proxy server."
    UNSET_PROXY=NO
    source shlib/_unset-proxy.sh
fi

# #4. INSTALL/UPDATE OPTION: update the system. This option can be combined with others.
if [ "$UPDATE_SYSTEM" == "YES" ]; then
	echo "Updating the system..."
	UPDATE_SYSTEM=NO
    # Execute the calling script in the current script's process, and pulls in variables
    # and functions from the current script so they are usable from the calling script.
    # If you use 'exit' in calling script, it will exit the current script as well.
    source shlib/_system-update.sh
fi

# #5. INSTALL/UPDATE OPTION: install base software packages. This option can be combined with other options.
if [ "$INSTALL_BASE" == "YES" ]; then
    echo "Installing base software packages."
    INSTALL_BASE=NO
    source shlib/_install-base.sh
fi

# #6. INSTALL/UPDATE OPTION: install MySql (client and server). This option is independent and can be combined
# with other options (probably depends on system update).
if [ "$INSTALL_MYSQL" == "YES" ]; then
    echo "Installing MySql DBMS."
    INSTALL_MYSQL=NO
    source shlib/_install-mysql.sh
fi

# #7. INSTALL/UPDATE OPTION: install Oracle Java JDK. This option can be combined with other options.
if [ "$INSTALL_JAVA" == "YES" ]; then
    echo "Installing: Oracle JDK. Version: ${JAVA_VERSION}."
    echo "Installing: Apache Ant tool. Version: ${ANT_VERSION}."
    echo "Installing: Apache Maven tool. Version: ${MAVEN_VERSION}."
    INSTALL_JAVA=NO
    source shlib/_install-java.sh
fi

# #8. INSTALL/UPDATE OPTION: install Jenkins.
if [ "$INSTALL_JENKINS" == "YES" ]; then
    echo "Installing Jenkins server."
    INSTALL_JENKINS=NO
    source shlib/_install-jenkins.sh
fi

# #9. INSTALL/UPDATE OPTION: install Sonar Qube (code analyzer).
if [ "$INSTALL_SONAR" == "YES" ]; then
    echo "Installing Sonar server."
    INSTALL_SONAR=NO
    source shlib/_install-sonar.sh
fi

# #10. INSTALL/UPDATE OPTION: install Apache Hadoop. This option can be combined with other options,
# but is dependent on INSTALL_BASE and INSTALL_JAVA options (should be done first).
if [ "$INSTALL_HADOOP" == "YES" ]; then
    echo "Installing Apache Hadoop. Version: ${HADOOP_VERSION}"
    INSTALL_HADOOP=NO
    source shlib/_install-hadoop.sh
fi

# #11. INSTALL/UPDATE OPTION: install Apache Hive. This option can be combined with other options, but is
# dependent on INSTALL_BASE, INSTALL_JAVA, INSTALL_HADOOP options (should be done first).
if [ "$INSTALL_HIVE" == "YES" ]; then
    echo "Installing Apache Hive. Version: ${HIVE_VERSION}"
    INSTALL_HIVE=NO
    source shlib/_install-hive.sh
fi

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "${DEBUG_MODE}" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after updating
if [ "${REBOOT_AFTER_UPDATE}" == "YES" ]; then
    sudo reboot now
fi
