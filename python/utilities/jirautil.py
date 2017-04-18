#!/usr/bin/env python
# coding=utf-8

"""
 Work with JIRA - put labels, generate reports, etc.
 Created: Gusev Dmitrii, 04.04.2017
"""

from lib.jiralib import JIRAUtility

print "Working with JIRA is starting..."

# initial values
# todo: replace hardcoded values with cmd line parameters
address  = 'https://issues.merck.com'
username = 'gusevdm'
password = 'Vinnypuhh14!'
sprint_name = 'ADA Sprint 33'

# Create object and connect to JIRA
jira = JIRAUtility(address, username, password)
jira.connect()

# get all sprint issues
# issues = jira.get_all_sprint_issues(sprint_name)
# print "Found [{}] issues.".format(len(issues))
# generate report and print it
# print JIRAUtility.get_issues_report(issues)

# add team label to all current sprint issues and print report
#jira.add_label_to_sprint_issues(sprint_name)
#print JIRAUtility.get_issues_report(jira.get_all_sprint_issues(sprint_name))

#issue = jira.get_issue('BMA-1216')
#component = jira.get_component('mantis-capability')
#print 'component ->', component
#components = issue.fields.components
#print 'components ->', components
# if component in components:
#     print 'ok'
# else:
#     print 'not present'


#jira.print_raw_issue(issue)
#component = jira.get_component('mantis-capability')
#print 'Found: ', component
#print issue.raw
#for component in issue.fields.components:
#    print component.id, '->', component.name, '->', component.self
