#!/bin/bash
#
# =============================================================================
#   This script shows some statistics about the system:
#      * system memory usage
#      * system disk space usage
#      * CPU load
#
#   Script is a part of scripts suite and shouldn't be called by itself.
#   Use <./mysys.sh -update>
#
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified:
# =============================================================================

free -m | awk 'NR==2{printf "Memory Usage: %s/%sMB (%.2f%%)\n", $3,$2,$3*100/$2 }'
df -h | awk '$NF=="/"{printf "Disk Usage: %d/%dGB (%s)\n", $3,$2,$5}'
top -bn1 | grep load | awk '{printf "CPU Load: %.2f\n", $(NF-2)}'