#!/usr/bin/env bash

# -- stopping DFS
stop-dfs.sh

# -- stopping YARN
stop-yarn.sh

# -- stopping History Server
mr-jobhistory-daemon.sh stop historyserver