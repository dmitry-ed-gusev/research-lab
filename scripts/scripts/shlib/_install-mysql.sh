#!/bin/bash
#
# =============================================================================
#   This script install Mysql DBMS (server + client) Ubuntu Server machine.
#   Script is a part of scripts suite and shouldn't be called by itself.
#   Use <./mysys.sh -install-mysql>
#
#   WARNING! Script should not be started as user 'root' (with command like: 
#   sudo ./<script_name>)! Script will ask for such privileges, if necessary.
#  
#   Created:  Gusev Dmitry, 10.04.2017
#   Modified: Gusev Dmitrii, 06.11.2017
# =============================================================================

# todo: check ability to connect to mysql outside of this PC - from network.
# todo: mysql server should be configured to do so (see document :) ).

# -- Install MySql (server+client) - last version. Before installing - set root password.
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password password ${MYSQL_ROOT_PASS}"
sudo debconf-set-selections <<< "mysql-server mysql-server/root_password_again password ${MYSQL_ROOT_PASS}"
sudo apt-get -qy install mysql-server mysql-client
