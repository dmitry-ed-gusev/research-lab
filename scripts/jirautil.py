#!/usr/bin/env python
# coding=utf-8

"""
    Utility to work with JIRA - put labels, generate reports, etc.
    Contains just a starter for utility.

    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 09.10.2017
"""

from pylib.jira_utility_extended import JiraUtilityExtended
from pylib.jira_utility_init import init_jira_utility_config
import pylib.jira_constants as jconst

print 'JIRA Utility: starting...'
# init configuration - parse cmd line and load from config file
config = init_jira_utility_config()
# init JIRA object and execute specified option
jira = JiraUtilityExtended(config)
print "JIRA Utility: config and JIRA object are initialized."
# jira.execute_option(config.get(jconst.CONFIG_KEY_OPTION))

#
jql = 'project = BMA and issuetype = task and ' \
      'assignee in (andreevi, barzilov, kudriash, iushin, gorkoven, ermolaeo, sokolose, gusevdm) and ' \
      '(labels not in ("unlinked") or labels is empty) order by key desc'
#
issues = jira.execute_jql(jql)
#print JiraUtilityExtended.get_issues_report(issues)

#
counter = 0
found_issues = []
# issue link type
implements = "Implementation"
# issue link outward direction type
implements_type = "implements / must come before"

for issue in issues:
    print "\n=====================================\nProcessing issue [%s]." % issue.key
    #
    if issue.fields.issuelinks:
        is_link_found = False
        for link in issue.fields.issuelinks:
            # outward links
            if hasattr(link, "outwardIssue"):
                outwardIssue = link.outwardIssue
                print("\tOutward: " + outwardIssue.key)
                print "type ->", link.type
                print "out direction type ->", link.type.outward
                print "in direction type ->", link.type.inward
                # type -> outward/inward

                # print "1 -> [%s|%s] -> %s" % (link.type, implements, str(link.type) == implements)
                # print "2 -> [%s|%s] -> %s" % (link.type.outward, implements_type, str(link.type.outward) == implements_type)

                # check issue type
                if str(link.type) == implements and str(link.type.outward) == implements_type:
                    print "Found link! Link: %s" % link.outwardIssue
                    is_link_found = True
                    break

            # inward links
            # if hasattr(link, "inwardIssue"):
            #     inwardIssue = link.inwardIssue
            #     print("\tInward: " + inwardIssue.key)

        # if outward link "Implements" not found -> add issue to issues list
        if not is_link_found:
            found_issues.append(issue)
    else:
        print "Issue [%s] doesn't have links." % issue.key
        # found_issues.append(issue)

    counter += 1
    if counter % 50 == 0:
        print "Processed [%s] issues." % counter

print "===>", found_issues
print JiraUtilityExtended.get_issues_report(found_issues)
