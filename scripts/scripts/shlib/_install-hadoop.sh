#!/bin/bash
#
# =============================================================================
#   This script install Apache Hadoop platform on Ubuntu Server machine.
#   Script is a part of scripts suite and shouldn't be called by itself.
#   Use <./mysys.sh -install-hadoop>
#
#   All paths in this script are (and should be) relative to base dir [scripts].
#   where base script [mysys.sh] is located.
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#  
#   Created:  Gusev Dmitry, 10.04.2017
#   Modified: Gusev Dmitry, 06.11.2017
# =============================================================================

# todo: check installation of Java before
# todo: check, if hadoop is already installed
# todo: format namenode before usage ([hdfs namenode format] <- before start hdfs)

# -- Installing Apache Hadoop
# - delete target Hadoop TAR GZ file if it exists
if [ -f ${HADOOP_ARCHIVE} ] ; then
    rm ${HADOOP_ARCHIVE}
fi
# - download Apache Hadoop from repository
wget ${HADOOP_BINARY_URL}
# - extract Hadoop from archive
tar xvfz ${HADOOP_ARCHIVE}
# - move Hadoop to /opt directory
sudo mv ${HADOOP_NAME} /opt
# - add path to Hadoop executable to PATH variable (system wide).
# "" - in this quotes variables will be processed/evaluated (values used)
# '' - in this quotes variables will be used 'as is' (no values)
echo "HADOOP_HOME=/opt/${HADOOP_NAME}" | sudo tee /etc/profile.d/hadoop.sh
echo 'PATH="$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH"' | sudo tee -a /etc/profile.d/hadoop.sh
sudo chmod +x /etc/profile.d/hadoop.sh
# - create tmp folder for Hadoop (and set access rights)
sudo mkdir /tmp-hadoop
sudo mkdir /tmp-hadoop/dfs
sudo chmod -R 777 /tmp-hadoop
# - add empty ssh key for connecting (ssh) to localhost
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
# - copy pre-set config files to Hadoop
cp shlib/hadoop-preset/* /opt/${HADOOP_NAME}/etc/hadoop/
# - set proper JAVA_HOME variable
pylib/fedit.py -f /opt/${HADOOP_NAME}/etc/hadoop/hadoop-env.sh -t starts -s "export JAVA_HOME" -d "export JAVA_HOME=$JAVA_HOME"

echo "Don't forget to execute command [hdfs namenode -format] before Hadoop usage!"
