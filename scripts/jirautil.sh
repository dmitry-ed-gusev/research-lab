#!/bin/bash
#
# =============================================================================
#   This script intended for invoking jirautil.py script from console and
#   hide (put defaults values to) params: jira address, user, password.
#
#   Created:  Gusev Dmitry, 22.05.2017
#   Modified:
# =============================================================================

# todo: ask password and not store it here?
./jirautil.py --address https://issues.merck.com --user gusevdm --pass Vinnypuhh14! "$@"