#!/bin/bash

cd /var/www/html/ippbx/tests/test_mode
setsid ./run_test_mode2.sh >/var/log/test_mode.log 2>&1 </dev/null &
