#!/usr/bin/env python
# coding=utf-8

"""
    Extended utility module for JIRA.

    Created: Gusev Dmitrii, 07.10.2017
    Modified:
"""

import codecs
from jira_utility_base import JiraUtilityBase
import jira_constants as jconst


class JiraUtilityExtended(JiraUtilityBase):

    def __init__(self, config):
        print "JIRAUtilityExtended.__init__() is working. Config [%s]." % config
        super(JiraUtilityExtended, self).__init__(config)

    def print_current_status_report(self, out_file=None, print_to_console=True):
        # preparing parameters
        team_name = self.config.get(jconst.CONFIG_KEY_TEAM_NAME)
        print "Specified team name: [%s]." % team_name
        team = self.config.get(jconst.CONFIG_KEY_TEAM_MEMBERS % team_name)
        print "Members of team [%s]: %s" % (team_name, team)

        # report header and body
        report = 'Current "In Progress" status'
        for user in team:
            issues = self.get_current_status_for_user(user)
            report += 'Issues "In Progress" for user [{}], count [{}].\n'.format(user, len(issues))
            if len(issues) > 0:
                report += self.get_issues_report(issues).get_string()
                report += '\n\n'
        # print report to console
        if print_to_console:
            print '\n', report
        # out report to file
        if out_file and out_file.strip():
            with codecs.open(out_file, 'w', 'utf-8') as out:
                out.write(report)
