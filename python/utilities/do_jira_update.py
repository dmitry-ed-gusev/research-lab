#!/usr/bin/env python
# coding=utf-8

"""
 Work with JIRA - put labels, generate reports, etc.
 Created: Gusev Dmitrii, 04.04.2017
"""

from jirautils import JIRAUtility

print "Working with JIRA is starting..."

# initial values
username = 'gusevdm'
password = 'Vinnypuhh14!'
sprint_name = 'ADA Sprint 33'

# connect to JIRA
jira = JIRAUtility(config_path='configs')
jira.connect(username, password)

# get all sprint issues
# issues = jira.get_all_sprint_issues(sprint_name)
# print "Found [{}] issues.".format(len(issues))
# generate report and print it
# print JIRAUtility.get_issues_report(issues)

# add team label to all current sprint issues and print report
jira.add_label_to_sprint_issues(sprint_name)
print JIRAUtility.get_issues_report(jira.get_all_sprint_issues(sprint_name))
