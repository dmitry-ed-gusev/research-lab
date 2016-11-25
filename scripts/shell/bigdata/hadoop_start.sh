#!/usr/bin/env bash

# -- include common environment variables
. common_env.sh

# -- starting DFS
start-dfs.sh

# -- starting YARN
start-yarn.sh

# -- starting History Server
mr-jobhistory-daemon.sh start historyserver

# -- create home dir if not exists
if hadoop fs -test -d $USER_HOME_DIR ; then
    echo "Directory  [$USER_HOME_DIR] exists"
else
    # -- maybe add option -p (without it - fail if exists)
    hadoop fs -mkdir $USER_HOME_DIR
    echo "Creating  directory [$USER_HOME_DIR]"
fi
