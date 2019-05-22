#!/bin/bash
#
# =============================================================================
#   This script install Apache Hive on Ubuntu Server machine.
#   Script is a part of scripts suite and shouldn't be called by itself.
#   Use <./mysys.sh -install-hive>
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#  
#   Created:  Gusev Dmitry, 10.04.2017
#   Modified: Gusev Dmitrii, 06.11.2017
# =============================================================================

# -- Installing Apache Hive
# - delete target Hive TAR GZ file if it exists
if [ -f ${HIVE_ARCHIVE} ] ; then
    rm ${HIVE_ARCHIVE}
fi
# - download Apache Hive from repository
wget ${HIVE_BINARY_URL}
# - extract Hive from archive
tar xvfz ${HIVE_ARCHIVE}
# - move Hadoop to /opt directory
sudo mv ${HIVE_FULL_NAME} /opt

# - add path to Hive executable to PATH variable (system wide).
# "" - in this quotes variables will be processed/evaluated (values used)
# '' - in this quotes variables will be used 'as is' (no values)
echo "export HIVE_HOME=/opt/${HIVE_FULL_NAME}" | sudo tee /etc/profile.d/hive.sh
echo 'export PATH="$HIVE_HOME/bin:$PATH"' | sudo tee -a /etc/profile.d/hive.sh
sudo chmod +x /etc/profile.d/hive.sh
