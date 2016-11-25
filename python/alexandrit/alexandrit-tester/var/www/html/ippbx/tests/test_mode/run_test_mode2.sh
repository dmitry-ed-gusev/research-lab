#!/bin/bash

sleep 2
echo "Begin test mode..."
echo "********************************************************************************"
/var/www/html/ippbx/tests/test_mode/set_test_mode.sh begin
echo "Run tests..."
echo "********************************************************************************"
python /usr/local/alexandrit-tester/run.py
echo "End test mode..."
echo "********************************************************************************"
/var/www/html/ippbx/tests/test_mode/set_test_mode.sh end

