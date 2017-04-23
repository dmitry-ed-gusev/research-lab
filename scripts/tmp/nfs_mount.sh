#!/bin/bash
# ===================================================================
#   Script mount (by NFS) folder /mnt/artifacts (mount all form
#   NFS system config).
#  
#   Created:  Gusev Dmitry, 01.10.2015
#   Modified:
# ===================================================================

mount | grep "/mnt/artifacts" > /dev/null 2>&1
if [ $? != 0 ]; then
  mount /mnt/artifacts
  exit $?
fi
exit 0
