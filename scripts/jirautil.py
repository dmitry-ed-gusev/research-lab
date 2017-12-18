#!/usr/bin/env python
# coding=utf-8

"""
    Utility to work with JIRA - put labels, generate reports, etc.
    Contains just a starter for utility.

    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 09.10.2017
"""

from pylib.jira_utility_extended import JiraUtilityExtended
from pylib._jira_init import init_jira_utility_config
import pylib._jira_constants as jconst

print 'JIRA Utility: starting...'
# init configuration - parse cmd line and load from config file
config = init_jira_utility_config()
# init JIRA object and execute specified option
jira = JiraUtilityExtended(config)
print "JIRA Utility: config and JIRA object are initialized."
# jira.execute_option(config.get(jconst.CONFIG_KEY_OPTION))

#

#
issues = jira.execute_jql(jql)
#print JiraUtilityExtended.get_issues_report(issues)

print "===>", found_issues
# generate report and put it in file
report = JiraUtilityExtended.get_issues_report(found_issues)
print report
jira.write_report_to_file(report.get_string())
