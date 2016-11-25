#!/bin/bash

source /var/security-module-vars.sh
find /var/www/html/ippbx/ -name "*.php" -o -name "*.js" -o -name "*.html" | grep -v '\.backup'> $INSTALL_DIR/files.list
$INSTALL_DIR/CheckSumGenerator.py -l $INSTALL_DIR/files.list -c $CONFIG_FILE -b $BACKUP_DIR
